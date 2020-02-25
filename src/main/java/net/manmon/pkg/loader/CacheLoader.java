package net.manmon.pkg.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheLoader {
    private static final Logger logger = LoggerFactory.getLogger(CacheLoader.class);
    private Connection conn;

    private HashMap<Long,HashSet<Long>> tmpOrEqRequires = new HashMap<>();
    private HashMap<Long,HashSet<Long>> tmpOrRequires = new HashMap<>();
    private HashMap<Long,HashSet<Long>> tmpOrEqWithVerRequires = new HashMap<>();
    private HashMap<Long,HashSet<Long>> tmpPkgOrRequires = new HashMap<>();
    private HashMap<String, Long> tmpOrRequireStrings = new HashMap<>();

    public CacheLoader(Connection conn) {
        this.conn = conn;
    }

    public void loadCachedData() throws Exception {
        if (!PkgData.cacheLoaded.get()) {
            synchronized (PkgData.loadLock) {
                try {
                    ConcurrentHashMap<String, Long> pkgCache = new ConcurrentHashMap<>();

                    ConcurrentHashMap<Long, GenericRelation> relations = new ConcurrentHashMap<>();
                    ConcurrentHashMap<String, Long> relationsByStr = new ConcurrentHashMap<>();
                    ConcurrentHashMap<String, Long> eqRelations = new ConcurrentHashMap<>();
                    ConcurrentHashMap<String, GenericRelationWithId> eqWithVerRelations = new ConcurrentHashMap<>();
                    ConcurrentHashMap<String, Long> nameIds = new ConcurrentHashMap<>();

                    long start = new Date().getTime();
                    logger.debug("Loading data to cache");
                    PreparedStatement pstmt = conn.prepareStatement("SELECT id,name FROM pkg_name");
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        Long id = rs.getLong(1);
                        String name = rs.getString(2);
                        nameIds.put(name, id);
                    }
                    pstmt = conn.prepareStatement("SELECT p.id,p.pre,n.name,p.flags,r.ver FROM pkg_relation p JOIN pkg_ver r ON p.pkg_ver_id=r.id JOIN pkg_name n ON p.name_id=n.id");
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        Long id = rs.getLong(1);
                        GenericRelation relation = new GenericRelation();
                        relation.setPre(rs.getBoolean(2));
                        relation.setName(rs.getString(3));
                        relation.setRelation(rs.getString(4));
                        relation.setVersion(rs.getString(5));
                        relationsByStr.put(getRelationString(relation), id);
                    }

                    pstmt = conn.prepareStatement("SELECT relation_id,relation,pre,ver FROM pkg_eq_relation_with_ver");
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        GenericRelation relation = new GenericRelation();
                        relation.setName(rs.getString(2));
                        relation.setPre(rs.getBoolean(3));
                        relation.setVersion(rs.getString(4));
                        GenericRelationWithId relationWithId = new GenericRelationWithId(rs.getLong(1), relation);
                        eqWithVerRelations.put(getRelationString(relation), relationWithId);
                    }
                    pstmt = conn.prepareStatement("SELECT relation_id,relation FROM pkg_eq_relation");
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        eqRelations.put(rs.getString(2), rs.getLong(1));
                    }
                    logger.debug("Loading packages to cache");
                    pstmt = conn.prepareStatement("SELECT pkg.id,pkg_type,name,arch,version,sha256sum,installed_size,archive_size,pkg_size,local FROM pkg");
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        GenericPackage pkg = new GenericPackage();
                        pkg.setId(rs.getLong(1));
                        pkg.setPkgType(rs.getString(2));
                        pkg.setName(rs.getString(3));
                        pkg.setArch(rs.getString(4));
                        pkg.setVersion(rs.getString(5));
                        pkg.setSha256hash(rs.getString(6));
                        pkg.setInstalledSize(rs.getLong(7));
                        pkg.setArchiveSize(rs.getLong(8));
                        pkg.setPkgSize(rs.getLong(9));
                        boolean local = rs.getBoolean(10);
                        pkgCache.put(PkgLoader.pkgToString(pkg, local), pkg.getId());
                    }

                    HashMap<String,Long> versionsToPkgVerids = new HashMap<>();
                    HashMap<Long,Long> pkgVeridsToVerIds = new HashMap<>();
                    HashMap<Long, String> verIdsToVers = new HashMap<>();
                    pstmt = conn.prepareStatement("SELECT id,ver,verid FROM pkg_ver");
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        Long id = rs.getLong(1);
                        String ver = rs.getString(2);
                        Long verid = rs.getLong(3);

                        versionsToPkgVerids.put(ver,id);
                        if (verid != null && !verid.equals(0L)) {
                            pkgVeridsToVerIds.put(id,verid);
                        }
                        verIdsToVers.put(id, ver);
                    }

                    HashMap<Long, HashSet<Long>> pkgEqRequires = new HashMap<>();
                    HashMap<Long, HashSet<Long>> pkgEqProvides = new HashMap<>();
                    HashMap<Long, HashSet<Long>> pkgEqConflicts = new HashMap<>();
                    HashMap<Long, HashSet<Long>> pkgEqReplaces = new HashMap<>();
                    HashMap<Long, HashSet<Long>> pkgEqBreaks = new HashMap<>();
                    //TODO make
                    HashMap<Long, HashSet<Long>> pkgOrRelations = new HashMap<>();
                    pkgEqRequires.putAll(getPkgRelations("pkg_eq_requires"));
                    pkgEqProvides.putAll(getPkgRelations("pkg_eq_provides"));
                    pkgEqConflicts.putAll(getPkgRelations("pkg_eq_conflicts"));
                    pkgEqReplaces.putAll(getPkgRelations("pkg_eq_replaces"));
                    pkgEqBreaks.putAll(getPkgRelations("pkg_eq_breaks"));

                    HashMap<Long, HashSet<Long>> pkgEqWithVerRequires = new HashMap<>();
                    HashMap<Long, HashSet<Long>> pkgEqWithVerProvides = new HashMap<>();
                    HashMap<Long, HashSet<Long>> pkgEqWithVerConflicts = new HashMap<>();
                    HashMap<Long, HashSet<Long>> pkgEqWithVerReplaces = new HashMap<>();
                    HashMap<Long, HashSet<Long>> pkgEqWithVerBreaks = new HashMap<>();
                    pkgEqWithVerRequires.putAll(getPkgRelations("pkg_eq_requires_with_ver"));
                    pkgEqWithVerProvides.putAll(getPkgRelations("pkg_eq_provides_with_ver"));
                    pkgEqWithVerConflicts.putAll(getPkgRelations("pkg_eq_conflicts_with_ver"));
                    pkgEqWithVerReplaces.putAll(getPkgRelations("pkg_eq_replaces_with_ver"));
                    pkgEqWithVerBreaks.putAll(getPkgRelations("pkg_eq_breaks_with_ver"));

                    loadDependsOrRelations();

                    //TODO make
                    HashMap<Long, HashSet<Long>> pkgOrWithVerRelations = new HashMap<>();

                    logger.debug("Loaded");
                    long end = new Date().getTime();
                    if ((end - start) < 1000L) {
                        Thread.sleep(1000L - (end - start));
                    }
                    PkgData.nameIds.clear();
                    PkgData.nameIds.putAll(nameIds);
                    PkgData.relations.clear();
                    PkgData.relations.putAll(relations);
                    PkgData.relationsByStr.clear();
                    PkgData.relationsByStr.putAll(relationsByStr);
                    PkgData.eqRelationsByStr.clear();
                    PkgData.eqRelationsByStr.putAll(eqRelations);
                    PkgData.eqWithVerRelationsByStr.clear();
                    PkgData.eqWithVerRelationsByStr.putAll(eqWithVerRelations);
                    PkgData.pkgCache.clear();
                    PkgData.pkgCache.putAll(pkgCache);

                    PkgData.pkgEqRequires.clear();
                    PkgData.pkgEqRequires.putAll(pkgEqRequires);
                    PkgData.pkgEqProvides.clear();
                    PkgData.pkgEqProvides.putAll(pkgEqProvides);
                    PkgData.pkgEqConflicts.clear();
                    PkgData.pkgEqConflicts.putAll(pkgEqConflicts);
                    PkgData.pkgEqReplaces.clear();
                    PkgData.pkgEqReplaces.putAll(pkgEqReplaces);
                    PkgData.pkgEqBreaks.clear();
                    PkgData.pkgEqBreaks.putAll(pkgEqBreaks);

                    PkgData.pkgEqRequiresWithVer.clear();
                    PkgData.pkgEqRequiresWithVer.putAll(pkgEqWithVerRequires);
                    PkgData.pkgEqProvidesWithVer.clear();
                    PkgData.pkgEqProvidesWithVer.putAll(pkgEqWithVerProvides);
                    PkgData.pkgEqConflictsWithVer.clear();
                    PkgData.pkgEqConflictsWithVer.putAll(pkgEqWithVerConflicts);
                    PkgData.pkgEqReplacesWithVer.clear();
                    PkgData.pkgEqReplacesWithVer.putAll(pkgEqWithVerReplaces);
                    PkgData.pkgEqBreaksWithVer.clear();
                    PkgData.pkgEqBreaksWithVer.putAll(pkgEqWithVerBreaks);

                    PkgData.orEqRequires.clear();
                    PkgData.orEqRequires.putAll(tmpOrEqRequires);
                    PkgData.orEqRequiresWithVer.clear();
                    PkgData.orEqRequiresWithVer.putAll(tmpOrEqWithVerRequires);
                    PkgData.orRequires.clear();
                    PkgData.orRequires.putAll(tmpOrRequires);
                    PkgData.orRequireStrings.clear();
                    PkgData.orRequireStrings.putAll(tmpOrRequireStrings);
                    PkgData.pkgOrRelations.clear();
                    PkgData.pkgOrRelations.putAll(tmpPkgOrRequires);

                    PkgData.versionsToPkgVerids.clear();
                    PkgData.versionsToPkgVerids.putAll(versionsToPkgVerids);
                    PkgData.pkgVeridsToVerIds.clear();
                    PkgData.pkgVeridsToVerIds.putAll(pkgVeridsToVerIds);
                    PkgData.verIdsToVers.clear();
                    PkgData.verIdsToVers.putAll(verIdsToVers);
                } finally {
                    PkgData.cacheLoaded.set(true);
                }
            }
        }
    }

    private void loadDependsOrRelations() throws Exception {
        tmpOrEqRequires.clear();
        tmpOrRequires.clear();
        tmpOrEqWithVerRequires.clear();
        tmpPkgOrRequires.clear();
        tmpOrRequireStrings.clear();

        HashSet<Long> orDependsIds = new HashSet<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT id FROM pkg_or_require");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            orDependsIds.add(rs.getLong(1));
        }
        tmpOrEqRequires = new HashMap<>();
        pstmt = conn.prepareStatement("SELECT or_relation_id,relation_id FROM pkg_or_eq_requires");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            Long orRelationId = rs.getLong(1);
            Long relationId = rs.getLong(2);
            if (!tmpOrEqRequires.containsKey(orRelationId)) {
                tmpOrEqRequires.put(orRelationId, new HashSet<>());
            }
            tmpOrEqRequires.get(orRelationId).add(relationId);
        }

        tmpOrRequires = new HashMap<>();
        pstmt = conn.prepareStatement("SELECT or_require_id,or_relation_id FROM pkg_or_requires");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            Long orRequireId = rs.getLong(1);
            Long orRelationId = rs.getLong(2);
            if (!tmpOrRequires.containsKey(orRequireId)) {
                tmpOrRequires.put(orRequireId, new HashSet<>());
            }
            tmpOrRequires.get(orRequireId).add(orRelationId);
        }

        tmpOrEqWithVerRequires = new HashMap<>();
        pstmt = conn.prepareStatement("SELECT or_relation_id,relation_id FROM pkg_or_eq_requires_with_ver");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            Long orRelationId = rs.getLong(1);
            Long relationId = rs.getLong(2);
            if (!tmpOrEqWithVerRequires.containsKey(orRelationId)) {
                tmpOrEqWithVerRequires.put(orRelationId, new HashSet<>());
            }
            tmpOrEqWithVerRequires.get(orRelationId).add(relationId);
        }

        tmpPkgOrRequires = new HashMap<>();
        pstmt = conn.prepareStatement("SELECT or_require_id,pkg_id FROM pkg_or_require_pkgs");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            Long orRequireId = rs.getLong(1);
            Long pkgId = rs.getLong(2);
            if (!tmpPkgOrRequires.containsKey(pkgId)) {
                tmpPkgOrRequires.put(pkgId, new HashSet<>());
            }
            tmpPkgOrRequires.get(pkgId).add(orRequireId);
        }

        tmpOrRequireStrings = new HashMap<>();
        for (Long id : orDependsIds) {
            StringBuffer sb = new StringBuffer("MATCH");
            if (tmpOrEqWithVerRequires.containsKey(id)) {
                sb.append(" OrEqRelationsWithVer=" + idsToString(tmpOrEqWithVerRequires.get(id)));
            }
            if (tmpOrEqRequires.containsKey(id)) {
                sb.append(" OrEqRelations=" + idsToString(tmpOrEqRequires.get(id)));
            }
            if (tmpOrRequires.containsKey(id)) {
                sb.append(" OrRelations=" + idsToString(tmpOrRequires.get(id)));
            }
            String matchString = sb.toString();
            tmpOrRequireStrings.put(matchString, id);
        }

    }

    private String idsToString(HashSet<Long> idsSet) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        List<Long> ids = new ArrayList<>(idsSet);
        Collections.sort(ids);
        for (Long id : ids) {
            if (first) {
                sb.append(id);
                first = false;
            } else {
                sb.append(","+id);
            }
        }
        return sb.toString();
    }

    private HashMap<Long, HashSet<Long>> getPkgRelations(String tableName) throws Exception {
        HashMap<Long,HashSet<Long>> idsMap = new HashMap<>();
        PreparedStatement pstmt = conn.prepareStatement("SELECT pkg_id,relation_id FROM "+tableName);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Long pkgId = rs.getLong(1);
            if (!idsMap.containsKey(pkgId)) {
                idsMap.put(pkgId, new HashSet<>());
            }
            Long relationId = rs.getLong(2);
            idsMap.get(pkgId).add(relationId);
        }
        return idsMap;
    }

    private String getRelationString(GenericRelation relation) throws Exception {
        return relation.getName()+","+ relation.getRelation()+","+relation.getPre()+","+relation.getVersion();
    }
}
