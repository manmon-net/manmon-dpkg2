package net.manmon.pkg.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PkgVersionComparator implements Comparator<String> {
    protected static final Logger logger = LoggerFactory.getLogger(PkgVersionComparator.class);

    private Pattern startsWithAlpha = Pattern.compile("^([a-zA-Z]+).*");
    private Pattern startsWithNumeric = Pattern.compile("^([0-9]+).*");
    private Pattern startsWithEpoch = Pattern.compile("^([0-9]+):.*");

    private String removeLeadingAlpha(String s) {
        return s.replaceFirst("^[a-zA-Z]*","");
    }

    private String removeLeadingNumeric(String s) {
        return s.replaceFirst("^[0-9]*","");
    }

    public static void main(String[] args) {
        String s1="0.0~git20150905.0.bb797dc-1~1";
        String s2="0.0~git20150905.0.bb797dc-1~";
        s1="0.0.1.2-8+b1";
        s2="0.0.1.2-8";
        System.out.println(new PkgVersionComparator().compare(s1,s2));
    }


    @Override
    public int compare(String ver1, String ver2) {
        if (ver1 == null && ver2 == null) {
            return 0;
        }
        if (ver1 == null) {
            return 1;
        }
        if (ver2 == null) {
            return -1;
        }
        Matcher m1 = startsWithEpoch.matcher(ver1);
        Matcher m2 = startsWithEpoch.matcher(ver2);
        Long epoch1 = 0L;
        Long epoch2 = 0L;
        if (m1.find()) {
            epoch1 = Long.valueOf(m1.group(1));
        }
        if (m2.find()) {
            epoch2 = Long.valueOf(m2.group(1));
        }
        if (!epoch1.equals(epoch2)) {
            return epoch1.compareTo(epoch2);
        } else {
            return realCompare(ver1.replaceFirst("^[0-9]+:",""), ver2.replaceFirst("^[0-9]+:",""), 0);
        }
    }

    private int realCompare(String ver1, String ver2, int matchCount) {
        boolean matching = true;
        while (matching) {
            matching = false;
            Matcher m1 = startsWithNumeric.matcher(ver1);
            Matcher m2 = startsWithNumeric.matcher(ver2);
            if (m1.find()) {
                if (m2.find()) {
                    matching = true;
                    int cmp = Long.valueOf(m1.group(1)).compareTo(Long.valueOf(m2.group(1)));
                    if (cmp != 0) {
                        return cmp;
                    }
                } else {
                    return -1;
                }
            } else if (m2.find()) {
                return 1;
            }
            ver1 = removeLeadingNumeric(ver1);
            ver2 = removeLeadingNumeric(ver2);
            if (ver1.length() == 0 && ver2.length() == 0) {
                return 0;
            } else if (ver1.length()==0) {
                return -1;
            } else if (ver2.length()==0) {
                return 1;
            }
            m1 = startsWithAlpha.matcher(ver1);
            m2 = startsWithAlpha.matcher(ver2);
            if (m1.find()) {
                if (m2.find()) {
                    matching = true;
                    int cmp = m1.group(1).compareTo(m2.group(1));
                    if (cmp != 0) {
                        return cmp;
                    }
                } else {
                    return -1;
                }
            } else if (m2.find()) {
                return 1;
            }
            ver1 = removeLeadingAlpha(ver1);
            ver2 = removeLeadingAlpha(ver2);
            if (ver1.length() == 0 && ver2.length() == 0) {
                return 0;
            } else  if (ver1.length()==0) {
                return -1;
            } else if (ver2.length()==0) {
                return 1;
            }
        }
        if (ver1.startsWith(".") && ver2.startsWith(".")) {
            ver1=ver1.replaceFirst("^\\.","");
            ver2=ver2.replaceFirst("^\\.","");
        } else if (ver1.startsWith("~") && ver2.startsWith("~")) {
            ver1=ver1.replaceFirst("^~","");
            ver2=ver2.replaceFirst("^~","");
        } else if (ver1.startsWith("+") && ver2.startsWith("+")) {
            ver1=ver1.replaceFirst("^\\+","");
            ver2=ver2.replaceFirst("^\\+","");
        } else if (ver1.startsWith("-") && ver2.startsWith("-")) {
            ver1=ver1.replaceFirst("^\\-","");
            ver2=ver2.replaceFirst("^\\-","");
        } else {
            return getCharOrder(ver1,ver2);
        }
        if (matchCount<30) {
            if (ver1.length() > 0 && ver2.length() > 0) {
                return realCompare(ver1, ver2, ++matchCount);
            } else {
                return ver1.compareTo(ver2);
            }
        } else {
            System.err.println("Match count exceeded - no match");
            return 0;
        }
    }

    private int getCharOrder(String ver1, String ver2) {
        //System.out.println("CHORDER: "+ver1+" "+ver2);
        if (ver1.length() == 0) {
            if (ver2.length() == 0) {
                return 0;
            } else {
                return -1;
            }
        } else if (ver2.length() == 0) {
            return 1;
        }
        if (ver1.startsWith(".")) {
            if (ver2.startsWith("+")) {
                return -1;
            } else if (ver2.startsWith("-")) {
                return -1;
            } else if (ver2.startsWith("~")) {
                return -1;
            }
        } else if (ver1.startsWith("+")) {
            if (ver2.startsWith(".")) {
                return 1;
            } else if (ver2.startsWith("-")) {
                return -1;
            } else if (ver2.startsWith("~")) {
                return -1;
            }
        } else if (ver1.startsWith("-")) {
            if (ver2.startsWith(".")) {
                return 1;
            } else if (ver2.startsWith("+")) {
                return 1;
            } else if (ver2.startsWith("~")) {
                return -1;
            }
        } else if (ver1.startsWith("~")) {
            if (ver2.startsWith(".")) {
                return 1;
            } else if (ver2.startsWith("-")) {
                return 1;
            } else if (ver2.startsWith("+")) {
                return 1;
            }
        }

        return 0;
    }

}