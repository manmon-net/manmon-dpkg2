package net.manmon.pkg.resolver;

import net.manmon.pkg.loader.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Resolver {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static ConcurrentHashMap<Long, ConcurrentHashMap<Long,TreeMap<Long, Long>>> upstreamNameIdVerIdPkgIdMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, ConcurrentHashMap<Long, TreeMap<Long, Long>>> pkgSetIdAndNameIdAndVerIdPkgIdMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Long, ConcurrentHashMap<Long, TreeMap<Long, RelationVerIdAndPkgId>>> upstreamPkgReplacesByNameId = new ConcurrentHashMap<>();

    private static AtomicLong id = new AtomicLong(1L);

    private static Long generateNewId() {
        return id.getAndIncrement();
    }


    public static void main(String[] args) {
        pkgSetIdAndNameIdAndVerIdPkgIdMap.put(153L, new ConcurrentHashMap<>());
        pkgSetIdAndNameIdAndVerIdPkgIdMap.get(153L).put(123L, new TreeMap<>());
        pkgSetIdAndNameIdAndVerIdPkgIdMap.get(153L).get(123L).put(100L, 200L);
        pkgSetIdAndNameIdAndVerIdPkgIdMap.get(153L).get(123L).put(200L, 400L);

        upstreamNameIdVerIdPkgIdMap.put(153L, new ConcurrentHashMap<>());
        upstreamNameIdVerIdPkgIdMap.get(153L).put(123L, new TreeMap<>());
        upstreamNameIdVerIdPkgIdMap.get(153L).get(123L).put(500L, 600L);

        upstreamPkgReplacesByNameId.put(153L, new ConcurrentHashMap<>());
        upstreamPkgReplacesByNameId.get(153L).put(123L, new TreeMap<>());

        //upstreamPkgReplacesByNameId.get(153L).get(123L).put(1200L, new HashMap<>());
        //upstreamPkgReplacesByNameId.get(153L).get(123L).get(1200L).put(Relation.LESS_OR_EQUAL, 250L);
        RelationVerIdAndPkgId relation = new RelationVerIdAndPkgId();
        relation.setNewNameId(126L);
        relation.setPkgId(1200L);
        relation.setVerId(1100L);
        relation.setRelation(Relation.LESS_OR_EQUAL);

        upstreamPkgReplacesByNameId.get(153L).get(123L).put(1100L, relation);

        pkgSetIdAndNameIdAndVerIdPkgIdMap.get(153L).put(124L, new TreeMap<>());
        pkgSetIdAndNameIdAndVerIdPkgIdMap.get(153L).put(124L, new TreeMap<>());
        pkgSetIdAndNameIdAndVerIdPkgIdMap.get(153L).get(124L).put(300L, 200L);
        upstreamNameIdVerIdPkgIdMap.put(153L, new ConcurrentHashMap<>());
        upstreamNameIdVerIdPkgIdMap.get(153L).put(124L, new TreeMap<>());
        upstreamNameIdVerIdPkgIdMap.get(153L).get(124L).put(500L, 600L);

        pkgSetIdAndNameIdAndVerIdPkgIdMap.get(153L).put(128L, new TreeMap<>());
        pkgSetIdAndNameIdAndVerIdPkgIdMap.get(153L).get(128L).put(800L, 900L);
        //upstreamNameIdVerIdPkgIdMap.get(153L).put(123L, new TreeMap<>());
        //upstreamNameIdVerIdPkgIdMap.get(153L).get(123L).put(500L, 70L);


        new Resolver().resolve();
    }


    private void loadDbRelationToMap(String tableName, ConcurrentHashMap<Long,HashSet<Long>> destinationMap) {
        List<PkgEqRelation> pkgEqRelationsList = jdbcTemplate.query("select * from "+tableName, new BeanPropertyRowMapper<>(PkgEqRelation.class));
        ConcurrentHashMap<Long, HashSet<Long>> idMap = new ConcurrentHashMap<>();
        for (PkgEqRelation eqRelation : pkgEqRelationsList) {
            if (!idMap.containsKey(eqRelation.getPkgId())) {
                idMap.put(eqRelation.getPkgId(), new HashSet<>());
            }

            idMap.get(eqRelation.getPkgId()).add(eqRelation.getRelationId());
        }
        for (Long id : destinationMap.keySet()) {
            if (!idMap.containsKey(id)) {
                destinationMap.remove(id);
            }
        }
        destinationMap.putAll(idMap);
    }

    private void loadDbEqRelations() {
        List<DbEqRelation> pkgEqRelations = jdbcTemplate.query("select * from pkg_eq_relation", new BeanPropertyRowMapper<>(DbEqRelation.class));
        ConcurrentHashMap<Long, String> eqRelationsMap = new ConcurrentHashMap<>();
        for (DbEqRelation eqRelation : pkgEqRelations) {
            eqRelationsMap.put(eqRelation.getRelationId(), eqRelation.getRelation());
        }
        for (Long relationId : PkgData.eqRelations.keySet()) {
            if (!eqRelationsMap.containsKey(relationId)) {
                PkgData.eqRelations.remove(relationId);
            }
        }
        PkgData.eqRelations.putAll(eqRelationsMap);
    }

    private void loadDbEqWithVerRelations() {
        List<GenericRelationWithId> pkgEqWithVerRelations = jdbcTemplate.query("select relation_id as id,relation,pre,ver as version from pkg_eq_relation_with_ver", new BeanPropertyRowMapper<>(GenericRelationWithId.class));
        ConcurrentHashMap<Long, GenericRelation> eqWithVerRelationsMap = new ConcurrentHashMap<>();
        for (GenericRelationWithId relWithId : pkgEqWithVerRelations) {
            GenericRelation rel = new GenericRelation();
            rel.setRelation(relWithId.getRelation());
            rel.setName(relWithId.getName());
            rel.setPre(relWithId.getPre());
            rel.setVersion(relWithId.getVersion());
            eqWithVerRelationsMap.put(relWithId.getId(), rel);
        }
        for (Long relationId : PkgData.eqWithVerRelations.keySet()) {
            if (!eqWithVerRelationsMap.containsKey(relationId)) {
                PkgData.eqWithVerRelations.remove(relationId);
            }
        }
        PkgData.eqWithVerRelations.putAll(eqWithVerRelationsMap);
    }


    private void loadDbRelations() {
        List<DbRelation> pkgRelations = jdbcTemplate.query("select * from pkg_relation", new BeanPropertyRowMapper<>(DbRelation.class));
        ConcurrentHashMap<Long, GenericRelation> relationsMap = new ConcurrentHashMap<>();
        for (DbRelation relation : pkgRelations) {
            GenericRelation rel = new GenericRelation();
            rel.setRelation(relation.getFlags());
            String name = PkgData.nameIdsToNames.get(relation.getNameId());
            rel.setName(name);
            rel.setPre(relation.getPre());
            String version = PkgData.verIdsToVers.get(relation.getPkgVerId());
            rel.setVersion(version);
            relationsMap.put(relation.getId(), rel);
        }
        for (Long relationId : PkgData.relations.keySet()) {
            if (!relationsMap.containsKey(relationId)) {
                PkgData.relations.remove(relationId);
            }
        }
        PkgData.relations.putAll(relationsMap);
    }

    private void loadDbOrRequires(String tableName, ConcurrentHashMap<Long, HashSet<Long>> destinationMap) {
        List<DbOrEqRelation> orEqRequiresList = jdbcTemplate.query("select * From "+tableName, new BeanPropertyRowMapper<>(DbOrEqRelation.class));
        ConcurrentHashMap<Long, HashSet<Long>> orEqRequiresMap = new ConcurrentHashMap<>();
        for (DbOrEqRelation relation : orEqRequiresList) {
            if (!orEqRequiresMap.containsKey(relation.getOrRelationId())) {
                orEqRequiresMap.put(relation.getOrRelationId(), new HashSet<>());
            }
            orEqRequiresMap.get(relation.getOrRelationId()).add(relation.getRelationId());
        }
        for (Long id : destinationMap.keySet()) {
            if (!orEqRequiresMap.containsKey(id)) {
                destinationMap.remove(id);
            }
        }
        destinationMap.putAll(orEqRequiresMap);
    }

    private void loadDbPkgOrRequires() {
        List<DbOrPkgRelation> orPkgRelationList = jdbcTemplate.query("SELECT * FROM pkg_or_require_pkgs", new BeanPropertyRowMapper<>(DbOrPkgRelation.class));
        ConcurrentHashMap<Long, HashSet<Long>> orPkgRelationsMap = new ConcurrentHashMap<>();
        for (DbOrPkgRelation relation : orPkgRelationList) {
            if (!orPkgRelationsMap.containsKey(relation.getPkgId())) {
                orPkgRelationsMap.put(relation.getPkgId(), new HashSet<>());
            }
            orPkgRelationsMap.get(relation.getPkgId()).add(relation.getOrRequireId());
        }

        for (Long id : PkgData.pkgOrRelations.keySet()) {
            if (!orPkgRelationsMap.containsKey(id)) {
                PkgData.pkgOrRelations.remove(id);
            }
        }
        PkgData.pkgOrRelations.putAll(orPkgRelationsMap);
    }

    private void loadPkgVersions() {
        List<PkgVer> pkgVerList = jdbcTemplate.query("SELECT id,ver,verid FROM pkg_ver", new BeanPropertyRowMapper<>(PkgVer.class));
        ConcurrentHashMap<Long, Long> pkgVeridToVerid = new ConcurrentHashMap<>();
        ConcurrentHashMap<Long, Long> pkgVerIdToRpmVerIdMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Long, String> pkgVerIdToVerMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Long> verToVerIdMap = new ConcurrentHashMap<>();
        for (PkgVer pkgVer : pkgVerList) {
            pkgVerIdToVerMap.put(pkgVer.getId(), pkgVer.getVer());
            verToVerIdMap.put(pkgVer.getVer(), pkgVer.getId());
            if (pkgVer.getVerid() != null) {
                pkgVeridToVerid.put(pkgVer.getId(), pkgVer.getVerid());
            }
        }
        for (String ver : PkgData.versionsToPkgVerids.keySet()) {
            if (!verToVerIdMap.containsKey(ver)) {
                PkgData.versionsToPkgVerids.remove(ver);
            }
        }
        PkgData.versionsToPkgVerids.putAll(verToVerIdMap);

        for (Long verid : PkgData.pkgVeridsToVerIds.keySet()) {
            if (!pkgVeridToVerid.containsKey(verid)) {
                PkgData.pkgVeridsToVerIds.remove(verid);
            }
        }
        PkgData.pkgVeridsToVerIds.putAll(pkgVeridToVerid);

        for (Long verId : PkgData.verIdsToVers.keySet()) {
            if (!pkgVerIdToVerMap.containsKey(verId)) {
                PkgData.verIdsToVers.remove(verId);
            }
        }
        PkgData.verIdsToVers.putAll(pkgVerIdToVerMap);
    }

    private void loadNames() {
        List<IdAndName> idAndNameList = jdbcTemplate.query("SELECT * FROM pkg_name", new BeanPropertyRowMapper<>(IdAndName.class));
        ConcurrentHashMap<Long, String> nameIdsToNames = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Long> nameIds = new ConcurrentHashMap<>();
        for (IdAndName idAndName : idAndNameList) {
            nameIdsToNames.put(idAndName.getId(), idAndName.getName());
            nameIds.put(idAndName.getName(), idAndName.getId());
        }
        for (Long id : PkgData.nameIdsToNames.keySet()) {
            if (!nameIds.containsKey(id)) {
                PkgData.nameIdsToNames.remove(id);
            }
        }
        PkgData.nameIdsToNames.putAll(nameIdsToNames);

        for (String name : PkgData.nameIds.keySet()) {
            if (!nameIds.containsKey(name)) {
                PkgData.nameIds.remove(name);
            }
        }
        PkgData.nameIds.putAll(nameIds);
    }

    @GetMapping("/resolve")
    public boolean resolve() {
        HashSet<Long> upstreamIds = new HashSet<>();
        upstreamIds.add(153L);
        long start = new Date().getTime();
        loadPkgVersions();
        loadNames();

        loadDbRelationToMap("pkg_eq_breaks", PkgData.pkgEqBreaks);
        loadDbRelationToMap("pkg_eq_breaks_with_ver", PkgData.pkgEqBreaksWithVer);
        loadDbRelationToMap("pkg_breaks", PkgData.pkgBreaks);

        loadDbRelationToMap("pkg_eq_replaces", PkgData.pkgEqReplaces);
        loadDbRelationToMap("pkg_eq_replaces_with_ver", PkgData.pkgEqReplacesWithVer);
        loadDbRelationToMap("pkg_replaces", PkgData.pkgReplaces);

        loadDbRelationToMap("pkg_eq_conflicts", PkgData.pkgEqConflicts);
        loadDbRelationToMap("pkg_eq_conflicts_with_ver", PkgData.pkgEqConflictsWithVer);
        loadDbRelationToMap("pkg_conflicts", PkgData.pkgConflicts);

        loadDbRelationToMap("pkg_eq_provides", PkgData.pkgEqProvides);
        loadDbRelationToMap("pkg_eq_provides_with_ver", PkgData.pkgEqProvidesWithVer);
        loadDbRelationToMap("pkg_provides", PkgData.pkgProvides);

        loadDbRelationToMap("pkg_eq_requires", PkgData.pkgEqRequires);
        loadDbRelationToMap("pkg_eq_requires_with_ver", PkgData.pkgEqRequiresWithVer);
        loadDbRelationToMap("pkg_requires", PkgData.pkgRequires);

        loadDbEqRelations();
        loadDbEqWithVerRelations();

        loadDbRelations();
        loadDbOrRequires("pkg_or_eq_requires", PkgData.orEqRequires);
        loadDbOrRequires("pkg_or_eq_requires_with_ver", PkgData.orEqRequiresWithVer);
        loadDbPkgOrRequires();

        long end = new Date().getTime();
        System.out.println(end-start);
        /*
        List<PkgEqRelation> dbConflicts = jdbcTemplate.query("select pc.pkg_id, pr.flags as relation, pr.name_id, pr.pkg_ver_id as version_id from pkg_conflicts pc join pkg_relation pr on pc.relation_id=pr.id", new BeanPropertyRowMapper<>(PkgEqRelation.class));
        System.out.println("CONFLICTS: "+dbConflicts.size());
        List<DbPkg> dbPkgProvides = jdbcTemplate.query("select id,ver_id from pkg", new BeanPropertyRowMapper<>(DbPkg.class));
        System.out.println("PROVIDES: "+dbPkgProvides.size());
        for (DbPkg provides : dbPkgProvides) {
            if (!pkgProvides.containsKey(provides.getId())) {
                pkgProvides.put(provides.getId(), new TreeSet<>());
            }
            pkgProvides.get(provides.getId()).add(provides.getVerId());
        }

         */
        Long updatePkgSetId = generateNewId();
        ConcurrentHashMap<Long, ConcurrentHashMap<Long, RelationVerIdAndPkgId>> upstreamReplacedPackagesByNameId = new ConcurrentHashMap<>();

        for (Long installedPkgSetId : pkgSetIdAndNameIdAndVerIdPkgIdMap.keySet()) {
            for (Long upstreamId : upstreamIds) {
                for (Long installedNameId : pkgSetIdAndNameIdAndVerIdPkgIdMap.get(installedPkgSetId).keySet()) {
                //TreeMap<Long, Long> installedPkgVerIdsAndPkgIds = pkgSetIdAndNameIdAndVerIdPkgIdMap.get(installedPkgSetId).get(installedNameId);
                    if (upstreamNameIdVerIdPkgIdMap.containsKey(upstreamId) && upstreamPkgReplacesByNameId.containsKey(upstreamId) && upstreamPkgReplacesByNameId.get(upstreamId).containsKey(installedNameId)) {
                        Long installedVerId = pkgSetIdAndNameIdAndVerIdPkgIdMap.get(installedPkgSetId).get(installedNameId).lastKey();
                        //Long nameId = null;
                        for (Long replaceVerId : upstreamPkgReplacesByNameId.get(upstreamId).get(installedNameId).keySet()) {
                            RelationVerIdAndPkgId relation = upstreamPkgReplacesByNameId.get(upstreamId).get(installedNameId).get(replaceVerId);
                            if (replacePackageExists(installedVerId, relation.getRelation(), replaceVerId)) {
                                if (!upstreamReplacedPackagesByNameId.containsKey(upstreamId)) {
                                    upstreamReplacedPackagesByNameId.put(upstreamId, new ConcurrentHashMap<>());
                                }
                                upstreamReplacedPackagesByNameId.get(upstreamId).put(installedNameId, relation);
                            }
                        }
                        /*
                        if (upstreamNameIdVerIdPkgIdMap.get(upstreamId).containsKey(nameId)) {
                            Long newVerId = upstreamNameIdVerIdPkgIdMap.get(upstreamId).get(nameId).lastKey();
                            Long newPkgId = upstreamNameIdVerIdPkgIdMap.get(upstreamId).get(nameId).get(newVerId);

                            if (upstreamPkgReplacesByNameId.get(upstreamId).containsKey(nameId) && replacePackageExists(upstreamPkgReplacesByNameId.get(upstreamId).get(nameId), newVerId)) {
                                if (newVerId > installedVerId) {
                                    if (!newPkgSetsPkgIdAndVerIdMap.containsKey(updatePkgSetId)) {
                                        newPkgSetsPkgIdAndVerIdMap.put(updatePkgSetId, new ConcurrentHashMap<>());
                                    }
                                    if (!newPkgSetsPkgIdAndVerIdMap.get(updatePkgSetId).containsKey(nameId)) {
                                        VerIdAndPkgId verIdAndPkgId = new VerIdAndPkgId();
                                        verIdAndPkgId.setVerId(newVerId);
                                        verIdAndPkgId.setPkgId(newPkgId);
                                        newPkgSetsPkgIdAndVerIdMap.get(updatePkgSetId).put(nameId, verIdAndPkgId);
                                    } else {
                                        Long existingVerId = newPkgSetsPkgIdAndVerIdMap.get(updatePkgSetId).get(nameId).getVerId();
                                        if (newVerId > existingVerId) {
                                            VerIdAndPkgId verIdAndPkgId = new VerIdAndPkgId();
                                            verIdAndPkgId.setPkgId(newPkgId);
                                            verIdAndPkgId.setVerId(newVerId);
                                            newPkgSetsPkgIdAndVerIdMap.get(updatePkgSetId).put(nameId, verIdAndPkgId);
                                        }
                                    }
                                }
                            }
                        }*/
                    }
                }
            }
        }

        ConcurrentHashMap<Long, ConcurrentHashMap<Long, Long>> updatedPackagesByNameId = new ConcurrentHashMap<>();
        for (Long installedPkgSetId : pkgSetIdAndNameIdAndVerIdPkgIdMap.keySet()) {
            for (Long upstreamId : upstreamIds) {
                for (Long installedNameId : pkgSetIdAndNameIdAndVerIdPkgIdMap.get(installedPkgSetId).keySet()) {
                    if (!upstreamReplacedPackagesByNameId.containsKey(upstreamId) || !upstreamReplacedPackagesByNameId.get(upstreamId).containsKey(installedNameId)) {
                        if (upstreamNameIdVerIdPkgIdMap.containsKey(upstreamId) && upstreamNameIdVerIdPkgIdMap.get(upstreamId).containsKey(installedNameId)) {
                            Long newVerId = upstreamNameIdVerIdPkgIdMap.get(upstreamId).get(installedNameId).lastKey();
                            Long installedVerId = pkgSetIdAndNameIdAndVerIdPkgIdMap.get(installedPkgSetId).get(installedNameId).lastKey();
                            if (newVerId > installedVerId) {
                                if (!updatedPackagesByNameId.containsKey(upstreamId)) {
                                    updatedPackagesByNameId.put(upstreamId, new ConcurrentHashMap<>());
                                }
                                updatedPackagesByNameId.get(upstreamId).put(installedNameId, upstreamNameIdVerIdPkgIdMap.get(upstreamId).get(installedNameId).get(newVerId));
                            }
                        }
                    }
                }
            }
        }

        for (Long installedPkgSetId : pkgSetIdAndNameIdAndVerIdPkgIdMap.keySet()) {
            for (Long upstreamId : upstreamIds) {

                HashMap<Long, Long> installedAndNewPackagesMap = new HashMap<>();
                for (Long nameId : upstreamReplacedPackagesByNameId.get(upstreamId).keySet()) {
                    RelationVerIdAndPkgId relation = upstreamReplacedPackagesByNameId.get(upstreamId).get(nameId);
                    installedAndNewPackagesMap.put(relation.getNewNameId(), relation.getPkgId());
                }

                installedAndNewPackagesMap.putAll(updatedPackagesByNameId.get(upstreamId));
                for (Long installedNameId : pkgSetIdAndNameIdAndVerIdPkgIdMap.get(installedPkgSetId).keySet()) {
                    if (!installedAndNewPackagesMap.containsKey(installedNameId) && !upstreamReplacedPackagesByNameId.get(upstreamId).containsKey(installedNameId)) {
                        Long verId = pkgSetIdAndNameIdAndVerIdPkgIdMap.get(installedPkgSetId).get(installedNameId).lastKey();
                        installedAndNewPackagesMap.put(installedNameId, pkgSetIdAndNameIdAndVerIdPkgIdMap.get(installedPkgSetId).get(installedNameId).get(verId));
                    }
                }
                System.out.println("U:"+upstreamId+" " + installedAndNewPackagesMap);
            }
        }

        System.out.println("R:"+upstreamReplacedPackagesByNameId);
        System.out.println("T:"+updatedPackagesByNameId);
        return true;
    }

    private static boolean replacePackageExists(Long installedVerId, Relation relation, Long replaceVerId) {
        if (relation == null) {
            return true;
        } else if (relation.equals(Relation.EQUAL)) {
            if (installedVerId.equals(replaceVerId)) {
                return true;
            }
        } else if (relation.equals(Relation.GREATER)) {
            if (installedVerId.longValue() > replaceVerId.longValue()) {
                return true;
            }
        } else if (relation.equals(Relation.GREATER_OR_EQUAL)) {
            if (installedVerId.longValue() >= replaceVerId.longValue()) {
                return true;
            }
        } else if (relation.equals(Relation.LESS)) {
            if (installedVerId.longValue() < replaceVerId.longValue()) {
                return true;
            }
        } else if (relation.equals(Relation.LESS_OR_EQUAL)) {
            if (installedVerId.longValue() <= replaceVerId.longValue()) {
                return true;
            }
        }
        return false;
    }
}
