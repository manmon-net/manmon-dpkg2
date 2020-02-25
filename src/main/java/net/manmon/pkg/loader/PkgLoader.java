package net.manmon.pkg.loader;

import net.manmon.pkg.entities.Upstream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PkgLoader {
    private Boolean loadRelations = true;
    private Boolean queryDatabase = true;

    private Long upstreamId;

    private static final Logger logger = LoggerFactory.getLogger(PkgLoader.class);
    private Connection conn;
    private String dpkgUpstreamDir = System.getProperty("user.home") + "/manmon-pkg/dpkg-repodata/";
    private String upstreamName = "";
    private HashMap<String,String> newPackages = new HashMap<>();
    private HashMap<String,Long> newVerIds = new HashMap<>();
    private static AtomicLong idGen = new AtomicLong(10000L);
    private List<PkgVer> newVers = new ArrayList<>();

    //private ConcurrentHashMap<String, Long> nameIds = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> newNameIds = new ConcurrentHashMap<>();
    private HashMap<String,Long> newPackagesToIds = new HashMap<>();

    private CopyManager copyManager;
    private JdbcTemplate jdbcTemplate;

    private HashMap<Long, GenericRelation> addedRelations = new HashMap<>();
    //private HashMap<String, Long> relationsByStr = new HashMap<>();

    private HashMap<String, Long> addedNameIds = new HashMap<>();
    private HashMap<String, Long> addedVerIds = new HashMap<>();
    private HashMap<Long, String> addedVerIdToVers = new HashMap<>();
    private ConcurrentHashMap<String, Long> newRelationsByStr = new ConcurrentHashMap<>();

    private HashMap<Long, HashSet<Long>> addedPkgRequires = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgReplaces = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgConflicts = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgProvides = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgBreaks = new HashMap<>();
    private HashMap<Long, HashMap<Long, HashSet<Long>>> addedPkgOrDepends = new HashMap<>();

    //private HashMap<String, Long> pkgEqRelations = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqRequires = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqProvides = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqConflicts = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqReplaces = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqBreaks = new HashMap<>();


    private HashMap<Long, HashSet<Long>> addedPkgEqRequiresWithVer = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqProvidesWithVer = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqConflictsWithVer = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqReplacesWithVer = new HashMap<>();
    private HashMap<Long, HashSet<Long>> addedPkgEqBreaksWithVer = new HashMap<>();

    private ConcurrentHashMap<String, GenericRelationWithId> addedEqWithVerRelations = new ConcurrentHashMap<>();
    private HashMap<String,Long> addedRelationsStringsMap = new HashMap<>();
    private ConcurrentHashMap<String, Long> addedEqRelations = new ConcurrentHashMap<>();
    private HashMap<Long, GenericPackage> addedPackages = new HashMap<>();
    //private HashMap<String, GenericRelationWithId> pkgEqWithVerRelations = new HashMap<>();

    private HashMap<Long, HashSet<Long>> orRelationPkgEqRelationsWithVer = new HashMap<>();
    private HashMap<Long, HashSet<Long>> orRelationPkgEqRelations = new HashMap<>();

    private PreparedStatement verIdPstmt;
    private PreparedStatement pstmt;
    private PreparedStatement nameIdPstmt;

    private HashMap<String, Long> verIds = new HashMap<>();

    private HashMap<String, Long> pkgCache = new HashMap<>();

    private void addDataToCache() throws Exception {
        for (Long pkgId : addedPackages.keySet()) {
            GenericPackage pkg = addedPackages.get(pkgId);
            PkgData.pkgCache.put(pkgToString(pkg, pkg.getLocal()), pkg.getId());
        }
        for (Long pkgId : addedPackages.keySet()) {
            GenericPackage pkg = addedPackages.get(pkgId);
            PkgData.pkgCache.put(pkgToString(pkg, pkg.getLocal()), pkg.getId());
        }

        PkgData.pkgs.putAll(addedPackages);
        PkgData.pkgEqRequiresWithVer.putAll(addedPkgEqReplacesWithVer);
        PkgData.pkgEqProvidesWithVer.putAll(addedPkgEqProvidesWithVer);
        PkgData.pkgEqConflictsWithVer.putAll(addedPkgEqConflictsWithVer);
        PkgData.pkgEqReplacesWithVer.putAll(addedPkgEqReplacesWithVer);
        PkgData.pkgEqBreaksWithVer.putAll(addedPkgEqBreaksWithVer);
        PkgData.pkgEqRequires.putAll(addedPkgEqReplaces);
        PkgData.pkgEqProvides.putAll(addedPkgEqProvides);
        PkgData.pkgEqConflicts.putAll(addedPkgEqConflicts);
        PkgData.pkgEqReplaces.putAll(addedPkgEqReplaces);
        PkgData.pkgEqBreaks.putAll(addedPkgEqBreaks);
        PkgData.pkgRequires.putAll(addedPkgRequires);
        PkgData.pkgProvides.putAll(addedPkgProvides);
        PkgData.pkgReplaces.putAll(addedPkgReplaces);
        PkgData.pkgConflicts.putAll(addedPkgConflicts);
        PkgData.pkgBreaks.putAll(addedPkgBreaks);

        PkgData.versionsToPkgVerids.putAll(addedVerIds);
        PkgData.verIdsToVers.putAll(addedVerIdToVers);
        PkgData.nameIds.putAll(addedNameIds);
        for (String name : addedNameIds.keySet()) {
            PkgData.nameIdsToNames.put(addedNameIds.get(name), name);
        }
        PkgData.relationsByStr.putAll(newRelationsByStr);
        PkgData.eqWithVerRelationsByStr.putAll(addedEqWithVerRelations);
        for (String key : addedEqWithVerRelations.keySet()) {
            GenericRelationWithId relWithId = addedEqWithVerRelations.get(key);
            GenericRelation rel = new GenericRelation();
            rel.setVersion(relWithId.getVersion());
            rel.setPre(relWithId.getPre());
            rel.setName(relWithId.getName());
            rel.setRelation(relWithId.getRelation());
            PkgData.eqWithVerRelations.put(relWithId.getId(), rel);
        }
        PkgData.eqRelationsByStr.putAll(addedEqRelations);
        for (String s : addedEqRelations.keySet()) {
            PkgData.eqRelations.put(addedEqRelations.get(s), s);
        }
        PkgData.relations.putAll(addedRelations);

    }

    public PkgLoader(JdbcTemplate jdbcTemplate) throws Exception {
        this.jdbcTemplate = jdbcTemplate;
        conn = jdbcTemplate.getDataSource().getConnection();
        pstmt = conn.prepareStatement("SELECT pkg.id FROM pkg WHERE pkg_type='RPM' AND name = ? AND arch = ? AND version = ? AND sha256sum = ? AND installed_size = ? AND archive_size = ? AND pkg_size = ? AND pkg_type = ? AND local = ?");
        nameIdPstmt = conn.prepareStatement("SELECT id FROM pkg_name WHERE name = ?");
        verIdPstmt = conn.prepareStatement("SELECT id FROM pkg_ver WHERE ver=?");

    }

    public void loadPkgUpstreams(boolean updatePkgVers, String type) throws Exception {
        synchronized (PkgData.loadLock) {
            List<Upstream> upstreams;
            if (type != null && (type.equals("APT") || type.equals("YUM"))) {
                upstreams = jdbcTemplate.query("SELECT * FROM upstream WHERE disabled=false AND type=?", new Object[]{type}, new BeanPropertyRowMapper<>(Upstream.class));
            } else {
                upstreams = jdbcTemplate.query("SELECT * FROM upstream WHERE disabled=false", new BeanPropertyRowMapper<>(Upstream.class));
            }

            boolean first = true;
            for (Upstream upstream : upstreams) {
                upstreamId = upstream.getId();
                Boolean disabledChannel = jdbcTemplate.queryForObject("SELECT disabled FROM channel WHERE id=?", new Object[]{upstream.getChannelId()}, Boolean.class);
                if (!disabledChannel) {
                    String channelNameWithArch = jdbcTemplate.queryForObject("select name||'.'||arch from channel WHERE id=?", new Object[]{upstream.getChannelId()}, String.class);
                    upstreamName = channelNameWithArch + "-" + upstream.getName() + "." + upstream.getArch();
                    logger.debug("Downloading upstream " + upstreamName + " info");
                    Boolean upToDate;
                    if (upstream.getType().equals("APT")) {
                        upToDate = downloader(upstream.getUrl(), dpkgUpstreamDir + upstreamId, upstream.getFilename(), upstream.getLoadedChecksum());
                    } else {
                        upToDate = downloader(upstream.getUrl(), dpkgUpstreamDir + upstreamId, upstream.getFilename(), upstream.getLoadedChecksum());
                    }
                    if (!upToDate) {
                        if (first) {
                            idGen.set(jdbcTemplate.queryForObject("SELECT NEXTVAL('hibernate_sequence')", Long.class));
                            first = false;
                        }
                        String checksum = getChecksumForFile(dpkgUpstreamDir + upstreamId + "/" + upstream.getFilename(), "sha256");
                        logger.debug("Parsing upstream " + upstreamName + " info");
                        HashSet<GenericPackage> pkgs;
                        if (upstream.getType().equals("APT")) {
                            pkgs = DpkgParser.loadPackagesFile(dpkgUpstreamDir + upstreamId + "/" + upstream.getFilename());
                        } else if (upstream.getType().equals("YUM")) {
                            pkgs = new YumRepoImporter().metadataToGenericPkgList(upstream);
                        } else {
                            throw new Exception("Invalid upstream type");
                        }

                        logger.debug("Upstream info loaded");
                        HashSet<Long> upstreamPkgIds = new HashSet<>();
                        for (GenericPackage pkg : pkgs) {
                            Long id = load(pkg, false, upstreamId);
                            upstreamPkgIds.add(id);
                        }
                        persistData(upstreamPkgIds, checksum);
                    }
                }
            }
        }
    }

    private void persistData(HashSet<Long> upstreamPkgIds, String checksum) throws Exception {
        conn.setAutoCommit(false);
        PGConnection pgConnection = conn.unwrap(PGConnection.class);
        copyManager = pgConnection.getCopyAPI();

        if (newPackages.size() > 0) {
            if (newNameIds.size() > 0) {
                logger.debug("Persisting upstream " + upstreamName + " name ids - total " + newNameIds.size());
                copyNewNameIds();
                newNameIds.clear();
            }
            if (newVers.size() > 0) {
                logger.debug("Persisting upstream " + upstreamName + " version ids - total " + newVers.size());
                copyNewVerIds();
                newVers.clear();
            }

            logger.debug("Persisting upstream " + upstreamName + " packages - total " + newPackages.size());
            copyNewPackages(pgConnection.getCopyAPI());

            //if (pkgsToNotLocalList.size()>0) {
            //    logger.debug("Moving local to not local package - total "+pkgsToNotLocalList.size());
            //    copyLocalPackagesToNotLocal();
            //}

            if (newVers.size() > 0) {
                logger.debug("Persisting upstream " + upstreamName + " version ids - total " + newVers.size());
                copyNewVerIds();
                newVers.clear();
            }
            if (newNameIds.size() > 0) {
                logger.debug("Persisting upstream " + upstreamName + " package name ids - total " + newNameIds.size());
                copyNewNameIds();
                newNameIds.clear();
            }

            persistRelations();
            persistEqRelations();
            addDataToCache();
        }

        if (upstreamPkgIds.size()>0) {
            HashSet<Long> dbUpstreamPkgIds = new HashSet<>();
            dbUpstreamPkgIds.addAll(jdbcTemplate.queryForList("SELECT pkg_id FROM pkg_upstream WHERE upstream_id=?", new Object[]{upstreamId}, Long.class));
            HashSet<Long> upstreamAddedPkgIds = new HashSet<>();
            for (Long id : upstreamPkgIds) {
                if (!dbUpstreamPkgIds.contains(id)) {
                    upstreamAddedPkgIds.add(id);
                }
            }
            logger.debug("Persisting upstream " + upstreamName + " member pkg_ids - total " +upstreamAddedPkgIds.size());
            addPkgsToUpstream(upstreamAddedPkgIds);
        }

        new PkgVersionSorter(conn).sort();

        jdbcTemplate.queryForObject("SELECT SETVAL('hibernate_sequence',?)", new Object[]{idGen.incrementAndGet()}, Long.class);
        jdbcTemplate.update("UPDATE upstream SET loaded_checksum=? WHERE id=?", new Object[]{checksum, upstreamId});

        conn.commit();
        PkgData.readLock.set(true);
        try {
            Thread.sleep(1000L);
            for (Long relationId : PkgData.relations.keySet()) {

            }
        } finally {
            PkgData.readLock.set(false);
        }
        logger.debug("Committed");
    }

    private void addPkgsToUpstream(HashSet<Long> addedPkgIds) throws Exception {
        StringWriter psw = new StringWriter();
        String header ="pkg_id#upstream_id";
        psw.write(header);

        for (Long pkgId : addedPkgIds) {
            psw.write('\n');
            psw.write(pkgId+"#"+upstreamId);
        }
        copyManager.copyIn("COPY pkg_upstream (pkg_id,upstream_id) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw.toString()));
    }



    private void persistEqRelations() throws Exception {
        logger.debug("Persisting unique plain relations - total " + addedEqRelations.size());
        addPkgEqRelations();
        logger.debug("Persisting plain requires relations - total " + getPkgRelationsCount(addedPkgEqRequires));
        addPkgRelations("eq_requires", addedPkgEqRequires);
        logger.debug("Persisting plain provides relations - total " + getPkgRelationsCount(addedPkgEqProvides));
        addPkgRelations("eq_provides", addedPkgEqProvides);
        logger.debug("Persisting plain conflicts relations - total " + getPkgRelationsCount(addedPkgEqConflicts));
        addPkgRelations("eq_conflicts", addedPkgEqConflicts);
        logger.debug("Persisting plain replaces relations - total " + getPkgRelationsCount(addedPkgEqReplaces));
        addPkgRelations("eq_replaces", addedPkgEqReplaces);
        logger.debug("Persisting plain breaks relations - total " + getPkgRelationsCount(addedPkgEqBreaks));
        addPkgRelations("eq_breaks", addedPkgEqBreaks);

        logger.debug("Persisting EQ unique relations - total " + addedEqRelations.size());
        addPkgEqWithVersionRelations();
        logger.debug("Persisting EQ requires relations - total " + getPkgRelationsCount(addedPkgEqRequiresWithVer));
        addPkgRelations("eq_requires_with_ver", addedPkgEqRequiresWithVer);
        logger.debug("Persisting EQ provides relations - total " + getPkgRelationsCount(addedPkgEqProvidesWithVer));
        addPkgRelations("eq_provides_with_ver", addedPkgEqProvidesWithVer);
        logger.debug("Persisting EQ conflicts relations - total " + getPkgRelationsCount(addedPkgEqConflictsWithVer));
        addPkgRelations("eq_conflicts_with_ver", addedPkgEqConflictsWithVer);
        logger.debug("Persisting EQ replaces relations - total " + getPkgRelationsCount(addedPkgEqReplacesWithVer));
        addPkgRelations("eq_replaces_with_ver", addedPkgEqReplacesWithVer);

        if (addedPkgOrDepends.size()>0) {
            logger.debug("Persisting upstream " + upstreamName + " or depends - total " + getOrDependsSize());
            addPkgOrDepends();
        }

    }

    private void addPkgEqWithVersionRelations() throws Exception {
        StringWriter psw = new StringWriter();
        String header ="relation_id#relation#pre#ver";
        psw.write(header);

        for (String key : addedEqWithVerRelations.keySet()) {
            GenericRelationWithId eqRel = addedEqWithVerRelations.get(key);
            psw.write('\n');
            psw.write(eqRel.getId()+"#"+replaceDelimiter(eqRel.getName())+"#"+eqRel.getPre()+"#"+replaceDelimiter(eqRel.getVersion()));
        }
        copyManager.copyIn("COPY pkg_eq_relation_with_ver(relation_id,relation,pre,ver) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw.toString()));
    }

    private void addPkgEqRelations() throws Exception {
        StringWriter psw = new StringWriter();
        String header ="relation_id#relation";
        psw.write(header);

        for (String relation : addedEqRelations.keySet()) {
            Long id = addedEqRelations.get(relation);
            psw.write('\n');
            psw.write(id+"#"+replaceDelimiter(relation));
        }

        copyManager.copyIn("COPY pkg_eq_relation(relation_id,relation) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw.toString()));
    }


    private String replaceDelimiter(String str) {
        if (str == null) {
            return "";
        }
        return "\""+str.replaceAll("#", "\\\\#")+"\"";
    }


    private void persistRelations() throws Exception {
        if (addedRelations.size()>0) {
            logger.debug("Persisting upstream "+upstreamName+" relations - total "+addedRelations.size());
            addPkgRelations();
        }

        if (addedPkgRequires.size() > 0) {
            logger.debug("Persisting upstream " + upstreamName + " requires - total " + getPkgRelationsCount(addedPkgRequires));
            addPkgRelations("requires", addedPkgRequires);
        }
        if (addedPkgProvides.size() > 0) {
            logger.debug("Persisting upstream " + upstreamName + " provides - total " + getPkgRelationsCount(addedPkgProvides));
            addPkgRelations("provides", addedPkgProvides);
        }
        if (addedPkgConflicts.size() > 0) {
            logger.debug("Persisting upstream " + upstreamName + " conflicts - total " + getPkgRelationsCount(addedPkgConflicts));
            addPkgRelations("conflicts", addedPkgConflicts);
        }
        if (addedPkgReplaces.size() > 0) {
            logger.debug("Persisting upstream " + upstreamName + " replaces - total " + getPkgRelationsCount(addedPkgReplaces));
            addPkgRelations("replaces", addedPkgReplaces);
        }
        if (addedPkgBreaks.size() > 0) {
            logger.debug("Persisting upstream " + upstreamName + " breaks - total " + getPkgRelationsCount(addedPkgBreaks));
            addPkgRelations("breaks", addedPkgBreaks);
        }

    }

    private void copyNewPackages(CopyManager copyManager) throws Exception {
        StringWriter psw = new StringWriter();
        String header = "id#pkg_type#name#arch#ver_id#version#sha256sum#name_id#installed_size#archive_size#pkg_size#local";
        psw.write(header);

        for (String key : newPackages.keySet()) {
            psw.write('\n');
            psw.write(newPackages.get(key));
        }
        copyManager.copyIn("COPY pkg(id,pkg_type,name,arch,ver_id,version,sha256sum,name_id,installed_size,archive_size,pkg_size,local) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw.toString()));
    }

    private void copyNewNameIds() throws Exception {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO pkg_name(id,name) VALUES(?,?)");
        int count = 0;
        for (String name : newNameIds.keySet()) {
            addedNameIds.put(name, newNameIds.get(name));
            pstmt.setLong(1, newNameIds.get(name));
            pstmt.setString(2, name);
            pstmt.addBatch();
            count++;
            if (count>1000) {
                count=0;
                pstmt.executeBatch();
            }
        }
        if(count>0) {
            pstmt.executeBatch();
        }
    }

    private void copyNewVerIds() throws Exception {
        StringWriter sw = new StringWriter();
        sw.write("id#ver");
        for (PkgVer ver : newVers) {
            addedVerIds.put(ver.getVer(), ver.getId());
            addedVerIdToVers.put(ver.getId(), ver.getVer());
            sw.write('\n');
            sw.write(ver.getId() + "#" + ver.getVer());
        }
        copyManager.copyIn("COPY pkg_ver(id,ver) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(sw.toString()));
    }

    protected static String pkgToString(GenericPackage pkg, boolean local) {
        return "PKG "+pkg.getName()+" "+pkg.getArch()+" "+pkg.getVersion()+" "+pkg.getSha256hash()+" "+pkg.getInstalledSize()+" "+pkg.getArchiveSize()+" "+pkg.getPkgSize()+" "+pkg.getPkgType()+" "+local;
    }

    protected Long getPkg(GenericPackage pkg, boolean local) throws Exception {
        String pkgStr = pkgToString(pkg, local);
        if (PkgData.pkgCache.containsKey(pkgStr)) {
            return PkgData.pkgCache.get(pkgStr);
        }
        if (pkgCache.containsKey(pkgStr)) {
            return pkgCache.get(pkgStr);
        }
        pstmt.setString(1, pkg.getName());
        pstmt.setString(2, pkg.getArch());
        pstmt.setString(3, pkg.getVersion());
        pstmt.setString(4, pkg.getSha256hash());
        pstmt.setLong(5, pkg.getInstalledSize());
        pstmt.setLong(6, pkg.getArchiveSize());
        pstmt.setLong(7, pkg.getPkgSize());
        pstmt.setString(8, pkg.getPkgType());
        pstmt.setBoolean(9, local);

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getLong(1);
        } else {
            return null;
        }
    }

    private Long getVerId(String version) throws Exception {
        if (PkgData.versionsToPkgVerids.containsKey(version)) {
            return PkgData.versionsToPkgVerids.get(version);
        } else if (newVerIds.containsKey(version)) {
            return newVerIds.get(version);
        } else if (addedVerIds.containsKey(version)) {
            return addedVerIds.get(version);
        } else {
            verIdPstmt.setString(1, version);
            ResultSet rs = verIdPstmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong(1);
                verIds.put(version, id);
                return id;
            } else {
                Long id = getNextId();
                newVerIds.put(version, id);
                newVers.add(new PkgVer(id,version,null));

                return id;
            }
        }
    }



    private long getNameId(String name) throws Exception {
        if (PkgData.nameIds.containsKey(name)) {
            return PkgData.nameIds.get(name);
        } else if (newNameIds.containsKey(name)) {
            return newNameIds.get(name);
        } else if (addedNameIds.containsKey(name)) {
            return addedNameIds.get(name);
        } else {
            Long id = getNextId();
            newNameIds.put(name, id);
            return id;
        }
    }

    private long getNextId() {
        return idGen.incrementAndGet();
    }


    private Long savePkg(GenericPackage pkg, boolean local) throws Exception {
        //FIX for SUSE package version ending with .
        if (pkg.getVersion() != null && pkg.getVersion().endsWith(".")) {
            pkg.setVersion(pkg.getVersion().replaceFirst("\\.$",""));
        }

        String key = pkg.getPkgType()+"#"+pkg.getName()+"#"+pkg.getArch()+"#"+pkg.getVersion()+"#"+pkg.getSha256hash()+"#"+pkg.getInstalledSize()+"#"+pkg.getArchiveSize()+"#"+pkg.getPkgSize()+"#"+local;
        if (!newPackages.containsKey(key)) {
            Long id = getNextId();
            pkg.setId(id);
            Long verId = getVerId(pkg.getVersion());
            Long nameId = getNameId(pkg.getName());
            String dataRow = id+"#"+pkg.getPkgType()+"#"+pkg.getName()+"#"+pkg.getArch()+"#"+verId+"#"+pkg.getVersion()+"#"+pkg.getSha256hash()+"#"+nameId+"#"+pkg.getInstalledSize()+"#"+pkg.getArchiveSize()+"#"+pkg.getPkgSize()+"#"+local;
            addedPackages.put(id, pkg);
            newPackagesToIds.put(key,id);
            newPackages.put(key,dataRow);
            if (loadRelations) {
                loadPkgRelations(pkg);
            }
            return id;
        } else {
            return newPackagesToIds.get(key);
        }
    }

    private String getRelationString(GenericRelation relation, boolean getIds) throws Exception {
        if (getIds) {
            getVerId(relation.getVersion());
            getNameId(relation.getName());
        }
        return relation.getName()+","+ relation.getRelation()+","+relation.getPre()+","+relation.getVersion();

    }

    private Long getRelationId(GenericRelation relation) throws Exception {
        String s = getRelationString(relation, true);
        if (PkgData.relationsByStr.containsKey(s)) {
            return PkgData.relationsByStr.get(s);
        } else {
            if (newRelationsByStr.containsKey(s)) {
                return newRelationsByStr.get(s);
            }
            if (queryDatabase) {
                PreparedStatement pstmt = conn.prepareStatement("SELECT p.id FROM pkg_relation p JOIN pkg_ver r ON p.pkg_ver_id=r.id WHERE p.pre = ? AND p.name_id = ? AND p.flags = ? AND r.ver = ?");
                pstmt.setBoolean(1, relation.getPre());
                pstmt.setLong(2, getNameId(relation.getName()));
                pstmt.setString(3, relation.getRelation());
                pstmt.setString(4, relation.getVersion());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    PkgData.relationsByStr.put(s, id);
                    return id;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private void loadPkgRelations(GenericPackage pkg) throws Exception {
        addedPkgRequires.put(pkg.getId(), loadSpecificRelations(pkg.getRequiresRelations(),"requires", pkg.getId()));
        addedPkgProvides.put(pkg.getId(), loadSpecificRelations(pkg.getProvidesRelations(), "provides", pkg.getId()));
        addedPkgReplaces.put(pkg.getId(), loadSpecificRelations(pkg.getReplacesRelations(),"replaces", pkg.getId()));
        addedPkgConflicts.put(pkg.getId(), loadSpecificRelations(pkg.getConflictsRelations(),"conflicts", pkg.getId()));
        addedPkgBreaks.put(pkg.getId(), loadSpecificRelations(pkg.getBreaksRelations(), "breaks", pkg.getId()));
        loadDependsOrRelations(pkg.getId(), pkg.getDependsOrRelations());

    }

    private Long getOrDependsSize() {
        long count=0L;
        HashSet<Long> addedOrRelationIds = new HashSet<>();
        for (Long pkgId : addedPkgOrDepends.keySet()) {
            for (Long orRelationId : addedPkgOrDepends.get(pkgId).keySet()) {
                if (!addedOrRelationIds.contains(orRelationId)) {
                    addedOrRelationIds.add(orRelationId);
                    count++;
                }
            }
        }
        return count;
    }

    private void addPkgOrDepends() throws Exception {
        StringWriter psw = new StringWriter();
        String header = "(or_require_id#or_relation_id";
        psw.write(header);

        StringWriter psw1 = new StringWriter();
        psw1.write("id");


        StringWriter psw4 = new StringWriter();
        psw4.write("or_require_id¤pkg_id");

        HashSet<Long> addedOrRelationIds = new HashSet<>();
        for (Long pkgId : addedPkgOrDepends.keySet()) {
            for (Long orRelationId : addedPkgOrDepends.get(pkgId).keySet()) {
                psw4.write('\n');
                psw4.write(orRelationId+"#"+pkgId);
                if (!addedOrRelationIds.contains(orRelationId)) {
                    psw1.write('\n');
                    psw1.write(orRelationId+"");
                    addedOrRelationIds.add(orRelationId);
                }
                for (Long relationId : addedPkgOrDepends.get(pkgId).get(orRelationId)) {
                    psw.write('\n');
                    psw.write(orRelationId+"#"+relationId);
                }
            }
        }

        StringWriter psw2 = new StringWriter();
        psw2.write("or_relation_id#relation_id");
        for (Long orRelationId : orRelationPkgEqRelations.keySet()) {
            for (Long relationId : orRelationPkgEqRelations.get(orRelationId)) {
                psw2.write('\n');
                psw2.write(orRelationId+"#"+relationId);
            }
        }

        StringWriter psw3 = new StringWriter();
        psw3.write("or_relation_id#relation_id");
        for (Long orRelationId : orRelationPkgEqRelationsWithVer.keySet()) {
            for (Long relationId : orRelationPkgEqRelationsWithVer.get(orRelationId)) {
                psw3.write('\n');
                psw3.write(orRelationId+"#"+relationId);
            }
        }



        copyManager.copyIn("COPY pkg_or_require(id) FROM STDIN WITH DELIMITER '#' CSV HEADER", new StringReader(psw1.toString()));
        copyManager.copyIn("COPY pkg_or_require_pkgs(or_require_id,pkg_id) FROM STDIN WITH DELIMITER '#' CSV HEADER", new StringReader(psw4.toString()));
        copyManager.copyIn("COPY pkg_or_requires(or_require_id, or_relation_id) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw.toString()));
        copyManager.copyIn("COPY pkg_or_eq_requires(or_relation_id, relation_id) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw2.toString()));
        copyManager.copyIn("COPY pkg_or_eq_requires_with_ver(or_relation_id, relation_id) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw3.toString()));
    }
    private HashMap<String,Long> dependsOrRelationsStringsToIds = new HashMap<>();
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

    private void loadDependsOrRelations(Long pkgId, HashSet<GenericOrRelation> orRelationsParameter) throws Exception {
        HashMap<Long, HashSet<Long>> orRelations = new HashMap<>();
        for (GenericOrRelation orRelation : orRelationsParameter) {
            Long id = getNextId();
            orRelations.put(id, new HashSet<>());
            for (GenericRelation relation : orRelation.getOrRelations()) {
                if (relation.getRelation() != null) {
                    if (relation.getRelation().equals("EQ")) {
                        if (!orRelationPkgEqRelationsWithVer.containsKey(id)) {
                            orRelationPkgEqRelationsWithVer.put(id, new HashSet<>());
                        }
                        orRelationPkgEqRelationsWithVer.get(id).add(getPkgEqRelationWithVersion(relation));
                    } else {
                        Long relationId = getRelationId(relation);
                        if (relationId == null) {
                            //relationId = saveRelation(relation);
                        }
                        //orRelations.get(id).add(relationId);
                    }
                } else {
                    if (!orRelationPkgEqRelations.containsKey(id)) {
                        orRelationPkgEqRelations.put(id, new HashSet<>());
                    }
                    orRelationPkgEqRelations.get(id).add(getPkgEqRelationId(relation.getName()));
                }
            }

            StringBuffer sb = new StringBuffer("MATCH");
            if (orRelationPkgEqRelationsWithVer.containsKey(id)) {
                sb.append(" OrEqRelationsWithVer="+idsToString(orRelationPkgEqRelationsWithVer.get(id)));
            }
            if (orRelationPkgEqRelations.containsKey(id)) {
                sb.append(" OrEqRelations="+idsToString(orRelationPkgEqRelations.get(id)));
            }
            if (orRelations.containsKey(id)) {
                sb.append(" OrRelations="+idsToString(orRelations.get(id)));
            }
            String matchString = sb.toString();

            if (dependsOrRelationsStringsToIds.containsKey(matchString)) {
                Long matchingOrRelationId = dependsOrRelationsStringsToIds.get(matchString);
//System.out.println("MATCH "+matchingOrRelationId+" "+new Date());
                orRelationPkgEqRelationsWithVer.remove(id);
                orRelationPkgEqRelations.remove(id);
                orRelations.remove(id);
                orRelations.put(matchingOrRelationId, new HashSet<>());
            } else {
                dependsOrRelationsStringsToIds.put(matchString, id);
            }

        }
        addedPkgOrDepends.put(pkgId, orRelations);
    }


    private Long getPkgEqRelationWithVersion(GenericRelation relation) throws Exception {
        String key =  getRelationString(relation, false);
        if (!PkgData.eqWithVerRelationsByStr.containsKey(key)) {
            if (!addedEqWithVerRelations.containsKey(key)) {
                if (queryDatabase) {
                    PreparedStatement pstmt = conn.prepareStatement("SELECT relation_id FROM pkg_eq_relation_with_ver WHERE relation = ? AND pre = ? AND ver = ?");
                    pstmt.setString(1, relation.getName());
                    pstmt.setBoolean(2, relation.getPre());
                    pstmt.setString(3, relation.getVersion());
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        Long id = rs.getLong(1);
                        PkgData.eqWithVerRelationsByStr.put(key, new GenericRelationWithId(id, relation));
                        return id;
                    } else {
                        Long id = getNextId();
                        addedEqWithVerRelations.put(key, new GenericRelationWithId(id, relation));
                        return id;
                    }
                } else {
                    Long id = getNextId();
                    addedEqWithVerRelations.put(key, new GenericRelationWithId(id, relation));
                    return id;
                }
            } else {
                return addedEqWithVerRelations.get(key).getId();
            }
        } else {
            return PkgData.eqWithVerRelationsByStr.get(key).getId();
        }
    }
    private HashSet<Long> loadSpecificRelations(HashSet<GenericRelation> genericRelations, String entryType, Long pkgId) throws Exception {
        HashSet<Long> relationIds = new HashSet<>();
        for (GenericRelation relation : genericRelations) {
            if (relation.getRelation() != null && !relation.getRelation().equals("EQ") && relation.getVersion() != null) {
                Long relationId = getRelationId(relation);
                if (relationId == null) {
                    String relStr = getRelationString(relation, true);
                    if (!addedRelationsStringsMap.containsKey(relStr)) {
                        relationId = getNextId();
                        addedRelationsStringsMap.put(relStr, relationId);
                        addedRelations.put(relationId, relation);
                    } else {
                        relationId = addedRelationsStringsMap.get(relStr);
                    }
                    //relationId = saveRelation(relation);
                }
                relationIds.add(relationId);
            } else if (relation.getRelation() != null && relation.getRelation().equals("EQ")) {
                if (entryType.equals("requires")) {
                    if (!addedPkgEqRequiresWithVer.containsKey(pkgId)) {
                        addedPkgEqRequiresWithVer.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqRequiresWithVer.get(pkgId).add(getPkgEqRelationWithVersion(relation));
                } else if (entryType.equals("provides")) {
                    if (!addedPkgEqProvidesWithVer.containsKey(pkgId)) {
                        addedPkgEqProvidesWithVer.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqProvidesWithVer.get(pkgId).add(getPkgEqRelationWithVersion(relation));
                } else if (entryType.equals("replaces")) {
                    if (!addedPkgEqReplacesWithVer.containsKey(pkgId)) {
                        addedPkgEqReplacesWithVer.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqReplacesWithVer.get(pkgId).add(getPkgEqRelationWithVersion(relation));
                } else if (entryType.equals("conflicts")) {
                    if (!addedPkgEqConflictsWithVer.containsKey(pkgId)) {
                        addedPkgEqConflictsWithVer.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqConflictsWithVer.get(pkgId).add(getPkgEqRelationWithVersion(relation));
                } else if (entryType.equals("breaks")) {
                    if (!addedPkgEqBreaksWithVer.containsKey(pkgId)) {
                        addedPkgEqBreaksWithVer.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqBreaksWithVer.get(pkgId).add(getPkgEqRelationWithVersion(relation));
                }
            } else {
                if (entryType.equals("requires")) {
                    if (!addedPkgEqRequires.containsKey(pkgId)) {
                        addedPkgEqRequires.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqRequires.get(pkgId).add(getPkgEqRelationId(relation.getName()));
                } else if (entryType.equals("provides")) {
                    if (!addedPkgEqProvides.containsKey(pkgId)) {
                        addedPkgEqProvides.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqProvides.get(pkgId).add(getPkgEqRelationId(relation.getName()));
                } else if (entryType.equals("conflicts")) {
                    if (!addedPkgEqConflicts.containsKey(pkgId)) {
                        addedPkgEqConflicts.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqConflicts.get(pkgId).add(getPkgEqRelationId(relation.getName()));
                } else if (entryType.equals("replaces")) {
                    if (!addedPkgEqReplaces.containsKey(pkgId)) {
                        addedPkgEqReplaces.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqReplaces.get(pkgId).add(getPkgEqRelationId(relation.getName()));
                } else if (entryType.equals("breaks")) {
                    if (!addedPkgEqBreaks.containsKey(pkgId)) {
                        addedPkgEqBreaks.put(pkgId, new HashSet<>());
                    }
                    addedPkgEqBreaks.get(pkgId).add(getPkgEqRelationId(relation.getName()));
                }
            }
        }
        return relationIds;
    }

    private Long getPkgEqRelationId(String key) throws Exception {
        if (!PkgData.eqRelationsByStr.containsKey(key)) {
            if (!addedEqRelations.containsKey(key)) {
                if (queryDatabase) {
                    PreparedStatement pstmt = conn.prepareStatement("SELECT relation_id FROM pkg_eq_relation WHERE relation = ?");
                    pstmt.setString(1, key);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        Long id = rs.getLong(1);
                        addedEqRelations.put(key, id);
                        return id;
                    } else {
                        Long id = getNextId();
                        addedEqRelations.put(key, id);
                        return id;
                    }
                } else {
                    Long id = getNextId();
                    addedEqRelations.put(key, id);
                    return id;
                }
            } else {
                return addedEqRelations.get(key);
            }
        } else {
            return PkgData.eqRelationsByStr.get(key);
        }
    }

    private Long load(GenericPackage pkg, boolean local, Long upstreamId) throws Exception {
        //FIX for SUSE package version ending with .
        if (pkg.getVersion() != null && pkg.getVersion().endsWith(".")) {
            pkg.setVersion(pkg.getVersion().replaceFirst("\\.$",""));
        }
        Long pkgId = getPkg(pkg, local);
        if (pkgId == null) {
            return savePkg(pkg, local);
        /*
            if (!local) {
                if (isThereSamePackageAsLocal(pkg)) {
                    pkgId = getPkg(pkg,true);
                    Long newPkgId = savePkg(pkg, false);

                    //TODO update pkgids
                } else {
                    savePkg(pkg, local);
                }
            }*/
        } else {
            return pkgId;
        }

    }

    private static String getChecksumForFile(String fname, String type) throws Exception {
        String checksumCommand = "sha256sum";
        if (type.equals("sha512")) {
            checksumCommand = "sha512sum";
        } else if (!type.equals("sha256")) {
            throw new Exception("Invalid checksum type "+type);
        }
        Process p = Runtime.getRuntime().exec("/usr/bin/"+checksumCommand+" "+fname);
        p.waitFor();
        if (p.exitValue() != 0) {
            throw new Exception("Error at checksumming "+fname);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
        String line = br.readLine();
        return line.split("\\s+")[0];
    }

    private boolean downloader(String uriPath, String localDirectoryPath, String fileName, String lastChecksum) throws Exception {
        if (!uriPath.endsWith("/")) {
            uriPath = uriPath+"/";
        }
        if (!localDirectoryPath.endsWith("/")) {
            localDirectoryPath = localDirectoryPath +"/";
        }

        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        File localDirectory = new File(localDirectoryPath);
        if (!localDirectory.exists()) {
            localDirectory.mkdirs();
        }

        boolean download = true;
        String lastModified = null;
        String fullRepoUrl = uriPath + fileName;
        if (fileName.equals("repomd.xml")) {
            fullRepoUrl = uriPath + "repodata/repomd.xml";
        }
        File localFilename = new File(localDirectoryPath + fileName);
        if (localFilename.exists()) {
            HttpHead head = new HttpHead(fullRepoUrl);
            HttpResponse httpResponse = httpclient.execute(head, null).get();
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception("Error downloading upstream information " + httpResponse.getStatusLine().getStatusCode());
            }

            lastModified = httpResponse.getLastHeader("last-modified").getValue();
            Date remoteRepoMdLastModifiedDate = DateUtils.parseDate(lastModified);
            Date localRepoMdLastModifiedDate = new Date(localFilename.lastModified());
            if (remoteRepoMdLastModifiedDate.equals(localRepoMdLastModifiedDate)) {
                if (lastChecksum != null && getChecksumForFile(localDirectoryPath+fileName, "sha256").equals(lastChecksum)) {
                    download = false;
                }
            } else {
                localFilename.delete();
            }
        }
        if (download) {
            File localFile = new File(localDirectoryPath+fileName);
            if (localFile.exists()) {
                localFile.delete();
            }
            logger.debug("Downloading "+fullRepoUrl+" to "+localDirectoryPath+fileName);
            HttpResponse httpResponse = httpclient.execute(new HttpGet(fullRepoUrl), null).get();
            Files.copy(httpResponse.getEntity().getContent(), Paths.get(localDirectoryPath+fileName));
            if (lastModified != null) {
                localFilename.setLastModified(DateUtils.parseDate(lastModified).getTime());
            }
            return false;
        } else {
            logger.debug("Local file "+localDirectoryPath+fileName+" is up to date");
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        long start = new Date().getTime();
        DpkgParser.loadPackagesFile("/home/tomi/Downloads/Packages.gz");
        long end = new Date().getTime();
        System.out.println(end-start);
    }


    private void addPkgRelations() throws Exception {
        StringWriter psw = new StringWriter();
        String header ="id#flags#name_id#pre#pkg_ver_id";
        psw.write(header);
        for (Long relationId : addedRelations.keySet()) {
            psw.write('\n');
            GenericRelation r = addedRelations.get(relationId);
            psw.write(relationId+"#"+r.getRelation()+"#"+getNameId(r.getName())+"#"+r.getPre()+"#"+getVerId(r.getVersion()));
        }

        copyManager.copyIn("COPY pkg_relation(id,flags,name_id,pre,pkg_ver_id) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw.toString()));
    }


    private void addPkgRelations(String relation, HashMap<Long,HashSet<Long>> relations) throws Exception {
        StringWriter psw = new StringWriter();
        String header ="pkg_id#relation_id";
        psw.write(header);

        for (Long pkgId : relations.keySet()) {
            for (Long reqId : relations.get(pkgId)) {
                psw.write('\n');
                psw.write(pkgId+"#"+reqId);
            }
        }

        copyManager.copyIn("COPY pkg_"+relation+"(pkg_id,relation_id) FROM STDIN WITH delimiter '#' CSV HEADER", new StringReader(psw.toString()));
    }

    public Long getPkgRelationsCount(HashMap<Long,HashSet<Long>> relations) {
        Long relationsCount = 0L;
        for (Long pkgId : relations.keySet()) {
            relationsCount += relations.get(pkgId).size();
        }
        return relationsCount;
    }

}
