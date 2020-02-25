package net.manmon.pkg.resolver;

import net.manmon.pkg.loader.GenericPackage;
import net.manmon.pkg.loader.GenericRelation;
import net.manmon.pkg.loader.PkgData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class Resolver2 {
    public void foo() {
        HashSet<Long> installedPackageIds = new HashSet<>();
        HashMap<String, TreeSet<Long>> replaces = new HashMap<>();
        HashMap<String, HashSet<GenericRelation>> replacesRelations = new HashMap<>();
        HashMap<String, HashSet<GenericRelation>> replacesRelationsWithVer = new HashMap<>();

        for (Long pkgId : PkgData.pkgs.keySet()) {
            if (!installedPackageIds.contains(pkgId)) {
                for (Long eqRelationId : PkgData.pkgEqReplaces.get(pkgId)) {
                    String rel = PkgData.eqRelations.get(eqRelationId);
                    if (!replaces.containsKey(rel)) {
                        replaces.put(rel, new TreeSet<>());
                    }
                    replaces.get(rel).add(pkgId);
                }
                for (Long eqWithVerRelationId : PkgData.pkgEqReplacesWithVer.get(pkgId)) {
                    GenericRelation rel = PkgData.eqWithVerRelations.get(eqWithVerRelationId);
                    replacesRelationsWithVer.get(rel.getName()).add(rel);
                }
                for (Long relationId : PkgData.pkgReplaces.get(pkgId)) {
                    GenericRelation rel = PkgData.relations.get(relationId);
                    if (!replacesRelations.containsKey(rel.getName())) {
                        replacesRelations.put(rel.getName(), new HashSet<>());
                    }
                    replacesRelations.get(rel.getName()).add(rel);
                }
            }
        }




        for (Long pkgId : installedPackageIds) {
            for (Long eqRelationId : PkgData.pkgEqReplaces.get(pkgId)) {
                String rel = PkgData.eqRelations.get(eqRelationId);
                if (!replaces.containsKey(rel)) {
                    replaces.put(rel, new TreeSet<>());
                }
                replaces.get(rel).add(pkgId);
            }
            for (Long eqWithVerRelationId : PkgData.pkgEqReplacesWithVer.get(pkgId)) {
                GenericRelation rel = PkgData.eqWithVerRelations.get(eqWithVerRelationId);
            }
            for (Long relationId : PkgData.pkgReplaces.get(pkgId)) {
                GenericRelation rel = PkgData.relations.get(relationId);
                if (!replacesRelations.containsKey(rel.getName())) {
                    replacesRelations.put(rel.getName(), new HashSet<>());
                }
                replacesRelations.get(rel.getName()).add(rel);
            }
        }
        HashSet<Long> upstreamIds = new HashSet<>();
        ConcurrentHashMap<Long, HashMap<Long, TreeMap<Long, Long>>> upstreamPkgNameIdsWithVerIdAndPkgId = new ConcurrentHashMap<>();
        ConcurrentHashMap<Long, Long> pkgNameMaxVerIds = new ConcurrentHashMap<>();

        ConcurrentHashMap<Long, Long> replacedNameIds = new ConcurrentHashMap<>();
        for (Long upstreamId : upstreamIds) {
            if (upstreamPkgNameIdsWithVerIdAndPkgId.containsKey(upstreamId)) {
                for (Long pkgNameId : upstreamPkgNameIdsWithVerIdAndPkgId.get(upstreamId).keySet()) {
                    String pkgName = PkgData.nameIdsToNames.get(pkgNameId);
                    if (replaces.containsKey(pkgName)) {
                        Long newPkgId = replaces.get(pkgName).last();
                        GenericPackage pkg = PkgData.pkgs.get(newPkgId);
                        Long newPkgNameId = PkgData.nameIds.get(pkg.getName());
                        replacedNameIds.put(pkgNameId, newPkgNameId);
                        upstreamPkgNameIdsWithVerIdAndPkgId.get(upstreamId).remove(pkgNameId);
                        upstreamPkgNameIdsWithVerIdAndPkgId.get(upstreamId).put(newPkgNameId, new TreeMap<>());
                        Long newVerId = PkgData.versionsToPkgVerids.get(pkg.getVersion());
                        upstreamPkgNameIdsWithVerIdAndPkgId.get(upstreamId).get(newPkgNameId).put(newVerId, newPkgId);
                    } else if (replacesRelations.containsKey(pkgName)) {
                        for (GenericRelation rel : replacesRelations.get(pkgName)) {

                        }
                    }
                }
            }
        }

        for (Long upstreamId : upstreamIds) {
            if (upstreamPkgNameIdsWithVerIdAndPkgId.containsKey(upstreamId)) {
                for (Long pkgNameId : upstreamPkgNameIdsWithVerIdAndPkgId.get(upstreamId).keySet()) {
                    String pkgName = PkgData.nameIdsToNames.get(pkgNameId);
                    if (replaces.containsKey(pkgName)) {
                        Long newPkgId = replaces.get(pkgName).last();
                        GenericPackage pkg = PkgData.pkgs.get(newPkgId);
                        Long newPkgNameId = PkgData.nameIds.get(pkg.getName());
                    } else {
                        Long pkgUpstreamMaxVerId = upstreamPkgNameIdsWithVerIdAndPkgId.get(upstreamId).get(pkgNameId).lastKey();
                        if (pkgNameMaxVerIds.containsKey(pkgNameId)) {
                            if (pkgUpstreamMaxVerId > pkgNameMaxVerIds.get(pkgNameId)) {
                                pkgNameMaxVerIds.put(pkgNameId, pkgUpstreamMaxVerId);
                            }
                        } else {
                            pkgNameMaxVerIds.put(pkgNameId, pkgUpstreamMaxVerId);
                        }
                    }
                }
            }
        }

        for (Long pkgId : installedPackageIds) {
            GenericPackage pkg = PkgData.pkgs.get(pkgId);
            if (replaces.containsKey(pkg.getName())) {

            }
        }
    }
}
