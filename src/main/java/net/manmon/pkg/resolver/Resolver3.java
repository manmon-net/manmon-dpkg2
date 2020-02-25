package net.manmon.pkg.resolver;

import net.manmon.pkg.loader.GenericPackage;
import net.manmon.pkg.loader.GenericRelation;
import net.manmon.pkg.loader.PkgData;
import net.manmon.pkg.loader.PkgVersionComparator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Resolver3 {

    private HashMap<String, HashMap<String, TreeSet<Long>>> replaces = new HashMap<>();
    private HashMap<String, HashMap<String, HashSet<GenericRelation>>> replacesRelations = new HashMap<>();
    private HashMap<String, HashMap<String, HashSet<GenericRelation>>>  replacesRelationsWithVer = new HashMap<>();

    private HashMap<String, HashMap<String, TreeSet<Long>>> conflicts = new HashMap<>();
    private HashMap<String, HashMap<String, HashSet<GenericRelation>>> conflictsRelations = new HashMap<>();
    private HashMap<String, HashMap<String, HashSet<GenericRelation>>>  conflictsRelationsWithVer = new HashMap<>();

    private HashMap<String, HashMap<String, TreeSet<Long>>> breaks = new HashMap<>();
    private HashMap<String, HashMap<String, HashSet<GenericRelation>>> breaksRelations = new HashMap<>();
    private HashMap<String, HashMap<String, HashSet<GenericRelation>>>  breaksRelationsWithVer = new HashMap<>();

    private HashMap<String, TreeSet<Long>> requires = new HashMap<>();
    private HashMap<String, HashSet<GenericRelation>> requiresRelations = new HashMap<>();
    private HashMap<String, HashSet<GenericRelation>>  requiresRelationsWithVer = new HashMap<>();

    private HashMap<String, TreeSet<Long>> orRequires = new HashMap<>();
    private HashMap<String, HashSet<GenericRelation>> orRequiresRelations = new HashMap<>();
    private HashMap<String, HashSet<GenericRelation>>  orRequiresRelationsWithVer = new HashMap<>();

    private HashMap<String, TreeSet<Long>> provides = new HashMap<>();
    private HashMap<String, HashSet<GenericRelation>> providesRelations = new HashMap<>();
    private HashMap<String, HashSet<GenericRelation>>  providesRelationsWithVer = new HashMap<>();

    private void loadReplacesRelationsForPkg(Long pkgId, String arch) {
        for (Long eqRelationId : PkgData.pkgEqReplaces.get(pkgId)) {
            String rel = PkgData.eqRelations.get(eqRelationId);
            if (!replaces.containsKey(arch)) {
                replaces.put(arch, new HashMap<>());
            }
            if (!replaces.get(arch).containsKey(rel)) {
                replaces.get(arch).put(rel, new TreeSet<>());
            }
            replaces.get(rel).get(arch).add(pkgId);
        }
        for (Long eqWithVerRelationId : PkgData.pkgEqReplacesWithVer.get(pkgId)) {
            GenericRelation rel = PkgData.eqWithVerRelations.get(eqWithVerRelationId);
            if (!requiresRelationsWithVer.containsKey(arch)) {
                requiresRelationsWithVer.put(arch, new HashSet<>());
            }
            if (!replacesRelationsWithVer.get(arch).containsKey(rel.getName())) {
                replacesRelationsWithVer.get(arch).put(rel.getName(), new HashSet<>());
            }
            replacesRelationsWithVer.get(arch).get(rel.getName()).add(rel);
        }
        for (Long relationId : PkgData.pkgReplaces.get(pkgId)) {
            GenericRelation rel = PkgData.relations.get(relationId);
            if (!replacesRelations.containsKey(arch)) {
                replacesRelations.put(arch, new HashMap<>());
            }
            if (!replacesRelations.get(arch).containsKey(rel.getName())) {
                replacesRelations.get(arch).put(rel.getName(), new HashSet<>());
            }
            replacesRelations.get(arch).get(rel.getName()).add(rel);
        }
    }

    private void loadConflictsRelationsForPkg(Long pkgId, String arch) {
        for (Long eqRelationId : PkgData.pkgEqConflicts.get(pkgId)) {
            String rel = PkgData.eqRelations.get(eqRelationId);
            if (!conflicts.containsKey(arch)) {
                conflicts.put(arch, new HashMap<>());
            }
            if (!conflicts.get(arch).containsKey(rel)) {
                conflicts.get(arch).put(rel, new TreeSet<>());
            }
            conflicts.get(arch).get(rel).add(pkgId);
        }
        for (Long eqWithVerRelationId : PkgData.pkgEqConflictsWithVer.get(pkgId)) {
            GenericRelation rel = PkgData.eqWithVerRelations.get(eqWithVerRelationId);
            if (!conflictsRelationsWithVer.containsKey(arch)) {
                conflictsRelationsWithVer.put(arch, new HashMap<>());
            }
            if (!conflictsRelationsWithVer.get(arch).containsKey(rel.getName())) {
                conflictsRelationsWithVer.get(arch).put(rel.getName(), new HashSet<>());
            }
            conflictsRelationsWithVer.get(arch).get(rel.getName()).add(rel);
        }
        for (Long relationId : PkgData.pkgConflicts.get(pkgId)) {
            GenericRelation rel = PkgData.relations.get(relationId);
            if (!conflictsRelations.containsKey(arch)) {
                conflictsRelations.put(arch, new HashMap<>());
            }
            if (!conflictsRelations.get(arch).containsKey(rel.getName())) {
                conflictsRelations.get(arch).put(rel.getName(), new HashSet<>());
            }
            conflictsRelations.get(arch).get(rel.getName()).add(rel);
        }
    }

    private void loadBreaksRelationsForPkg(Long pkgId, String arch) {
        for (Long eqRelationId : PkgData.pkgEqBreaks.get(pkgId)) {
            String rel = PkgData.eqRelations.get(eqRelationId);
            if (!breaks.containsKey(arch)) {
                breaks.put(arch, new HashMap<>());
            }
            if (!breaks.get(arch).containsKey(rel)) {
                breaks.get(arch).put(rel, new TreeSet<>());
            }
            breaks.get(arch).get(rel).add(pkgId);
        }
        for (Long eqWithVerRelationId : PkgData.pkgEqBreaksWithVer.get(pkgId)) {
            GenericRelation rel = PkgData.eqWithVerRelations.get(eqWithVerRelationId);
            if (!breaksRelationsWithVer.containsKey(arch)) {
                breaksRelationsWithVer.put(arch, new HashMap<>());
            }
            if (!breaksRelationsWithVer.get(arch).containsKey(rel.getName())) {
                breaksRelationsWithVer.get(arch).put(rel.getName(), new HashSet<>());
            }
            breaksRelationsWithVer.get(arch).get(rel.getName()).add(rel);
        }
        for (Long relationId : PkgData.pkgBreaks.get(pkgId)) {
            GenericRelation rel = PkgData.relations.get(relationId);
            if (!breaksRelations.containsKey(arch)) {
                breaksRelations.put(arch, new HashMap<>());
            }
            if (!breaksRelations.get(arch).containsKey(rel.getName())) {
                breaksRelations.get(arch).put(rel.getName(), new HashSet<>());
            }
            breaksRelations.get(arch).get(rel.getName()).add(rel);
        }
    }

    private void loadRequiresRelationsForPkg(Long pkgId) {
        for (Long eqRelationId : PkgData.pkgEqRequires.get(pkgId)) {
            String rel = PkgData.eqRelations.get(eqRelationId);
            if (!requires.containsKey(rel)) {
                requires.put(rel, new TreeSet<>());
            }
            requires.get(rel).add(pkgId);
        }
        for (Long eqWithVerRelationId : PkgData.pkgEqRequiresWithVer.get(pkgId)) {
            GenericRelation rel = PkgData.eqWithVerRelations.get(eqWithVerRelationId);
            if (!requiresRelationsWithVer.containsKey(rel.getName())) {
                requiresRelationsWithVer.put(rel.getName(), new HashSet<>());
            }
            requiresRelationsWithVer.get(rel.getName()).add(rel);
        }
        for (Long relationId : PkgData.pkgRequires.get(pkgId)) {
            GenericRelation rel = PkgData.relations.get(relationId);
            if (!requiresRelations.containsKey(rel.getName())) {
                requiresRelations.put(rel.getName(), new HashSet<>());
            }
            requiresRelations.get(rel.getName()).add(rel);
        }
        for (Long orRelationId : PkgData.pkgOrRelations.get(pkgId)) {

        }

    }

    private void loadProvidesRelationsForPkg(Long pkgId) {
        for (Long eqRelationId : PkgData.pkgEqProvides.get(pkgId)) {
            String rel = PkgData.eqRelations.get(eqRelationId);
            if (!provides.containsKey(rel)) {
                provides.put(rel, new TreeSet<>());
            }
            provides.get(rel).add(pkgId);
        }
        for (Long eqWithVerRelationId : PkgData.pkgEqProvidesWithVer.get(pkgId)) {
            GenericRelation rel = PkgData.eqWithVerRelations.get(eqWithVerRelationId);
            if (!providesRelationsWithVer.containsKey(rel.getName())) {
                providesRelationsWithVer.put(rel.getName(), new HashSet<>());
            }
            providesRelationsWithVer.get(rel.getName()).add(rel);
        }
        for (Long relationId : PkgData.pkgProvides.get(pkgId)) {
            GenericRelation rel = PkgData.relations.get(relationId);
            if (!providesRelations.containsKey(rel.getName())) {
                providesRelations.put(rel.getName(), new HashSet<>());
            }
            providesRelations.get(rel.getName()).add(rel);
        }
    }

    private Long getBiggestVerPkgIdForPkgWithArch(Long nameId, String arch) {
        TreeMap<Long, Long> pkgVerIdsToPkgIdsMap = new TreeMap<>(Collections.reverseOrder());
        for (Long pkgId : PkgData.pkgs.keySet()) {
            GenericPackage pkg = PkgData.pkgs.get(pkgId);
            Long pkgNameId = PkgData.nameIds.get(pkg.getName());
            Long pkgVerId = PkgData.pkgVeridsToVerIds.get(pkgId);
            if (nameId.equals(pkgNameId) && arch.equals(pkg.getArch())) {
                pkgVerIdsToPkgIdsMap.put(pkgVerId, pkgId);
            }
        }
        if (pkgVerIdsToPkgIdsMap.size()>0) {
            return pkgVerIdsToPkgIdsMap.get(pkgVerIdsToPkgIdsMap.firstKey());
        } else {
            return null;
        }
    }

    public void foo() {
        HashMap<Long, Long> installedPackageIds = new HashMap<>();
        HashSet<Long> availablePackageIds = new HashSet<>();


        HashMap<String, HashMap<Long, TreeMap<Long, Long>>> pkgsToUpdateWithArch = new HashMap<>();
        HashMap<String, HashMap<Long, TreeMap<Long, Long>>> installedPkgsWithArch = new HashMap<>();
        for (Long availablePkgId : availablePackageIds) {
            if (!installedPackageIds.containsKey(availablePkgId)) {
                GenericPackage pkg = PkgData.pkgs.get(availablePkgId);
                String arch = pkg.getArch();
                //String pkgName = pkg.getName();
                Long pkgNameId = PkgData.nameIds.get(pkg.getName());
                loadReplacesRelationsForPkg(availablePkgId, arch);
                loadConflictsRelationsForPkg(availablePkgId, arch);
                loadBreaksRelationsForPkg(availablePkgId, arch);
                //loadRequiresRelationsForPkg(pkgId);
                //loadProvidesRelationsForPkg(pkgId);

                if (!pkgsToUpdateWithArch.containsKey(arch)) {
                    pkgsToUpdateWithArch.put(arch, new HashMap<>());
                }
                if (!pkgsToUpdateWithArch.get(arch).containsKey(pkgNameId)) {
                    pkgsToUpdateWithArch.get(arch).put(pkgNameId, new TreeMap<>(Collections.reverseOrder()));
                }
                Long availableVerId = PkgData.pkgVeridsToVerIds.get(availablePkgId);
                pkgsToUpdateWithArch.get(arch).get(pkgNameId).put(availableVerId, availablePkgId);
/*
                String availableArch = pkg.getArch();

                Long availableNameId = PkgData.nameIds.get(pkgName);
                Long installedPkgId = installedPackageIds.get(availableNameId);
                Long installedVerId = getVerIdForPkgWithArch(availableNameId, pkg.getArch());

                if (availableVerId > installedVerId) {
                    packageIdsToUpdatePackageIds.put(installedPkgId, availablePkgId);
                }*/
            } else {
                GenericPackage pkg = PkgData.pkgs.get(availablePkgId);
                String arch = pkg.getArch();
                Long pkgNameId = PkgData.nameIds.get(pkg.getName());

                if (!installedPkgsWithArch.containsKey(arch)) {
                    installedPkgsWithArch.put(arch, new HashMap<>());
                }
                if (!installedPkgsWithArch.get(arch).containsKey(pkgNameId)) {
                    installedPkgsWithArch.get(arch).put(pkgNameId, new TreeMap<>());
                }
                Long installedVerId = PkgData.pkgVeridsToVerIds.get(availablePkgId);
                installedPkgsWithArch.get(arch).get(pkgNameId).put(installedVerId, availablePkgId);
            }
        }

        PkgVersionComparator pkgVersionComparator = new PkgVersionComparator();

        HashSet<Long> conflictingPackageIds = new HashSet<>();

        for (String arch : pkgsToUpdateWithArch.keySet()) {
            for (Long pkgNameId : pkgsToUpdateWithArch.get(arch).keySet()) {
                String pkgName = PkgData.nameIdsToNames.get(pkgNameId);

                if (pkgsToUpdateWithArch.get(arch).get(pkgNameId).size()>0) {
                    Long verId = pkgsToUpdateWithArch.get(arch).get(pkgNameId).firstKey();
                    Long pkgId = pkgsToUpdateWithArch.get(arch).get(pkgNameId).get(verId);
                    String pkgVer = PkgData.verIdsToVers.get(verId);
                    if (conflicts.containsKey(arch) && conflicts.get(arch).containsKey(pkgName)) {
                        conflictingPackageIds.add(pkgId);
                    }
                    if (conflictsRelations.containsKey(arch)) {
                        if (conflictsRelations.get(arch).containsKey(pkgName)) {
                            for (GenericRelation rel : conflictsRelations.get(arch).get(pkgName)) {
                                if (rel.getVersion().equals(pkgVer)) {
                                    conflictingPackageIds.add(pkgId);
                                }
                            }
                        }
                    }
                    if (conflictsRelationsWithVer.containsKey(arch)) {
                        if (conflictsRelationsWithVer.get(arch).containsKey(pkgName)) {
                            for (GenericRelation rel : conflictsRelationsWithVer.get(arch).get(pkgName)) {
                                String matchVer = rel.getVersion();
                                switch (rel.getRelation()) {
                                    case "EQ":
                                        if (pkgVersionComparator.compare(matchVer, pkgVer) == 0) {
                                            conflictingPackageIds.add(pkgId);
                                        }
                                        break;
                                    case "LT":
                                        if (pkgVersionComparator.compare(matchVer, pkgVer) == -1) {
                                            conflictingPackageIds.add(pkgId);
                                        }
                                        break;
                                    case "LE":
                                        if (pkgVersionComparator.compare(matchVer, pkgVer) == 0 || pkgVersionComparator.compare(matchVer, pkgVer) == -1) {
                                            conflictingPackageIds.add(pkgId);
                                        }
                                        break;
                                    case "GT":
                                        if (pkgVersionComparator.compare(matchVer, pkgVer) == 1) {
                                            conflictingPackageIds.add(pkgId);
                                        }
                                        break;
                                    case "GE":
                                        if (pkgVersionComparator.compare(matchVer, pkgVer) == 0 || pkgVersionComparator.compare(matchVer, pkgVer) == 1) {
                                            conflictingPackageIds.add(pkgId);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }

        for (String arch : installedPkgsWithArch.keySet()) {
            for (Long pkgNameId : installedPkgsWithArch.get(arch).keySet()) {
                if (pkgsToUpdateWithArch.containsKey(arch) && pkgsToUpdateWithArch.get(arch).containsKey(pkgNameId)) {

                }
            }
        }


    }

    public static void main(String[] args) {
        new Resolver3().foo();
    }
}
