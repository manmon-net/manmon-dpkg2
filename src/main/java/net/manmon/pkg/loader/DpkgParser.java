package net.manmon.pkg.loader;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class DpkgParser {

    protected static HashSet<GenericPackage> loadPackagesFile(String filename) throws Exception {
        File f = new File(filename);
        BufferedReader br;
        if (filename.endsWith(".gz")) {
            GZIPInputStream gzIn = new GZIPInputStream(new FileInputStream(f));
            br = new BufferedReader(new InputStreamReader(gzIn));
        } else {
            br = new BufferedReader(new FileReader(filename));
        }

        int resultNumber = -1;
        String line = br.readLine();
        boolean newTag = true;
        String currentTag = null;
        HashMap<Integer, HashMap<String, String>> result = new HashMap<>();
        String value = "";
        String patternString = "([a-z,A-Z]+)(: *)?([a-z,A-Z,0-9]+)";
        patternString="(((\\d+)\\.)?(\\d+)\\.)?()$";
        patternString="(^[a-z,A-Z,0-9\\-_]+):( +)(.+$)";

        boolean inConffiles = false;
        Pattern pattern = Pattern.compile(patternString);
        while (line != null) {
            Matcher matcher = pattern.matcher(line.trim());
            if (inConffiles) {
                if (!line.trim().startsWith("/")) {
                    inConffiles = false;
                }
            }
            if (!inConffiles) {
                if (line.startsWith("Conffiles:")) {
                    inConffiles = true;
                } else {
                    if (matcher.find()) {
                        if (currentTag != null && resultNumber >= 0) {
                            if (!result.containsKey(resultNumber)) {
                                result.put(resultNumber, new HashMap<>());
                            }
                            result.get(resultNumber).put(currentTag, value.trim());
                        }
                        currentTag = matcher.group(1);
                        value = matcher.group(3);
                        if (currentTag.equals("Package")) {
                            resultNumber++;
                        }
                        newTag = true;
                    } else {
                        if (!line.trim().equals(".")) {
                            if (newTag || (line.length() >= 1 && line.startsWith(" "))) {
                                value = value + "\n";
                            } else {
                                value = value + " ";
                            }
                            value = value + line.trim();
                        } else {
                            //if (!value.trim().equals("") && !currentTag.equals("SHA256")) {
                            //System.out.println(value);
                            //}
                            value = value + "\n";
                            newTag = false;
                        }

                    }
                }
            }
            line = br.readLine();
        }
        if (currentTag != null && resultNumber >= 0) {
            if (!result.containsKey(resultNumber)) {
                result.put(resultNumber, new HashMap<>());
            }
            result.get(resultNumber).put(currentTag,value.trim());
        }

        HashSet<GenericPackage> pkgs = new HashSet<>();
        for (Integer i : result.keySet()) {
            HashMap<String, String> values = result.get(i);
            GenericPackage pkg = new GenericPackage();
            pkg.setPkgType("DPKG");
            pkg.setArch(values.get("Architecture"));
            pkg.setName(values.get("Package"));
            pkg.setVersion(values.get("Version"));
            if (values.containsKey("SHA256")) {
                pkg.setSha256hash(values.get("SHA256"));
            }
            if (values.containsKey("Depends")) {
                GenericRelations relations = parseRelations(values.get("Depends"), false);
                pkg.setRequiresRelations(relations.getRelations());
                pkg.setDependsOrRelations(relations.getOrRelations());
            }
            if (values.containsKey("Pre-Depends")) {
                GenericRelations relations = parseRelations(values.get("Pre-Depends"), true);
                pkg.getRequiresRelations().addAll(relations.getRelations());
                pkg.getDependsOrRelations().addAll(relations.getOrRelations());
            }
            if (values.containsKey("Provides")) {
                pkg.setProvidesRelations(parseRelations(values.get("Provides"), false).getRelations());
            }
            if (values.containsKey("Conflicts")) {
                pkg.setConflictsRelations(parseRelations(values.get("Conflicts"), false).getRelations());
            }
            if (values.containsKey("Breaks")) {
                pkg.setBreaksRelations(parseRelations(values.get("Breaks"), false).getRelations());
            }
            if (values.containsKey("Replaces")) {
                pkg.setReplacesRelations(parseRelations(values.get("Replaces"), false).getRelations());
            }
            if (values.containsKey("Suggests")) {
                pkg.setSuggestsRelations(parseRelations(values.get("Suggests"), false).getRelations());
            }
            if (values.containsKey("Recommends")) {
                pkg.setRecommendsRelations(parseRelations(values.get("Recommends"), false).getRelations());
            }
            if (values.containsKey("Enhances")) {
                pkg.setEnhancesRelations(parseRelations(values.get("Enhances"), false).getRelations());
            }
            pkgs.add(pkg);
        }
        return pkgs;
    }

    private static GenericRelation getRelation(String s, boolean pre) {
        if (!s.trim().startsWith("/")) {
            System.out.println(s);
            GenericRelation relation = new GenericRelation();
            relation.setPre(pre);
            String[] pieces = s.split(" ");
            relation.setName(pieces[0]);
            if (pieces.length > 1) {
                String tmpRelation = (pieces[1].replaceFirst("^\\(", ""));
                if (tmpRelation.equals("<<")) {
                    relation.setRelation("LT");
                } else if (tmpRelation.equals("<=")) {
                    relation.setRelation("LE");
                } else if (tmpRelation.equals("=")) {
                    relation.setRelation("EQ");
                } else if (tmpRelation.equals(">=")) {
                    relation.setRelation("GE");
                } else if (tmpRelation.equals(">>")) {
                    relation.setRelation("GT");
                }
                relation.setVersion(pieces[2].replaceFirst("\\)$", ""));
            }
            return relation;
        } else {
            return null;
        }
    }

    private static GenericRelations parseRelations(String relationsString, boolean pre) {
        GenericRelations relations = new GenericRelations();
        for (String s : relationsString.split(", ")) {
            if (s.matches(".* \\| .*")) {
                GenericOrRelation orRelation = new GenericOrRelation();
                for (String subRelation : s.split(" \\| ")) {
                    orRelation.getOrRelations().add(getRelation(subRelation, pre));
                }
                relations.getOrRelations().add(orRelation);
            } else {
                relations.getRelations().add(getRelation(s, pre));
            }
        }
        return relations;
    }

}
