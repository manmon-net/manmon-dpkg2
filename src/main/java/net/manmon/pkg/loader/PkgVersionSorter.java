package net.manmon.pkg.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

public class PkgVersionSorter {
    private static final Logger logger = LoggerFactory.getLogger(PkgVersionSorter.class);
    private static Long verIdGap = 1000000000000L;
    private Connection conn;

    public PkgVersionSorter(Connection conn) {
        this.conn = conn;
    }

    public static void main(String[] args) {
        List<PkgVersion> vers = new ArrayList<>();
        vers.add(new PkgVersion(1L,"0git20070620-6", null));
        vers.add(new PkgVersion(1L,"0", null));
        vers.add(new PkgVersion(1L,"0y.dfsg-1", null));
        vers.add(new PkgVersion(1L,"0.01", null));
        vers.add(new PkgVersion(1L,"0.002", null));

        PkgVersionComparatorManmon pkgvercmp = new PkgVersionComparatorManmon();
        Collections.sort(vers, (a, b) -> pkgvercmp.compare(a.getVersion(), b.getVersion()));
        for (PkgVersion v : vers) {
            System.out.println(v.getVersion());
        }

        PkgVersionComparator x = new PkgVersionComparator();
        TreeMap<String, String> a = new TreeMap<>(x);
        a.put("0","a");
        a.put("00", "b");
        a.put("10","a");
        a.put("02","a");
        a.put("03","a");
        a.put("40","a");
        for (String s : a.keySet()) {
            System.out.println(s+":"+a.get(s));
        }
        System.out.println(00+":"+a.get("00"));


    }

    public void sort() throws Exception {
        conn.setAutoCommit(false);
        PkgVersionComparatorManmon pkgvercmp = new PkgVersionComparatorManmon();

        List<PkgVersion> existingPkgVers = new ArrayList<>();
        List<PkgVersion> newPkgVers = new ArrayList<>();
        boolean zeroFound = false;
        for (String version : PkgData.versionsToPkgVerids.keySet()) {
            Long id = PkgData.versionsToPkgVerids.get(version);
            PkgVersion pkgVer = new PkgVersion();
            pkgVer.setId(id);
            pkgVer.setVersion(version);
            if (pkgVer.getVersion().equals("0")) {
                zeroFound = true;
            }
            if (!PkgData.pkgVeridsToVerIds.containsKey(id)) {
                newPkgVers.add(pkgVer);
            } else {
                existingPkgVers.add(pkgVer);
            }

        }
        if (!zeroFound) {
            PkgVersion zeroVer = new PkgVersion();
            zeroVer.setId(0L);
            zeroVer.setVersion("0");
            newPkgVers.add(zeroVer);
        }
        Collections.sort(existingPkgVers, (a, b) -> pkgvercmp.compare(a.getVersion(), b.getVersion()));
        Collections.sort(newPkgVers, (a, b) -> pkgvercmp.compare(a.getVersion(), b.getVersion()));

        PkgVersionComparator vercmp = new PkgVersionComparator();
        HashMap<Long, Long> versionIdsToVerids = new HashMap<>();

        if (existingPkgVers.size() == 0) {
            logger.info("No verids - setting all");
            Long verId = verIdGap;
            PkgVersion lastPkgVer = null;
            for (PkgVersion pkgVer : newPkgVers) {
                Long id = pkgVer.getId();
                if (lastPkgVer != null && vercmp.compare(lastPkgVer.getVersion(),pkgVer.getVersion())==0) {
                    versionIdsToVerids.put(id,versionIdsToVerids.get(lastPkgVer.getId()));
                } else {
                    String ver = pkgVer.getVersion();
                    versionIdsToVerids.put(id,verId);
                    verId = verId + verIdGap;
                }
                lastPkgVer = pkgVer;
            }
        } else {
            PkgVersion lastPkgVer = null;

            boolean notEnoughGap = false;
            TreeMap<String, Long> tmpMap = new TreeMap<>(vercmp);
            for (PkgVersion pkgVer : newPkgVers) {
                tmpMap.put(pkgVer.getVersion(), pkgVer.getId());
            }

            TreeMap<String, PkgVersion> dbVersMap = new TreeMap<>(vercmp);
            for (String version : PkgData.versionsToPkgVerids.keySet()) {
                Long id = PkgData.versionsToPkgVerids.get(version);
                if (PkgData.pkgVeridsToVerIds.containsKey(id)) {
                    PkgVersion pkgVersion = new PkgVersion();
                    pkgVersion.setId(id);
                    pkgVersion.setVersion(version);
                    pkgVersion.setVerId(PkgData.pkgVeridsToVerIds.get(id));
                    dbVersMap.put(version, pkgVersion);
                }
            }
            PkgVersion ver1 = new PkgVersion(16699L, "0", 1000000000000L);
            dbVersMap.put("0", ver1);

            HashSet<String> addedVers = new HashSet<>();
            for (String ver : tmpMap.keySet()) {
                if (!notEnoughGap && !addedVers.contains(ver)) {
                    if (dbVersMap.get(ver) != null) {
                        versionIdsToVerids.put(tmpMap.get(ver), dbVersMap.get(ver).getVerId());
                    } else {
                        Long id = tmpMap.get(ver);
                        String lowerVer = dbVersMap.lowerKey(ver);
                        String higherVer = dbVersMap.higherKey(ver);

                        Long lowerVerId;
                        Long lastId = null;
                        String lastVer = "";

                        if (lowerVer == null) {
                            lastVer = dbVersMap.firstKey();
                            lastId = dbVersMap.get(lastVer).getVerId();
                            lowerVerId = dbVersMap.get(dbVersMap.firstKey()).getVerId() / 2L;
                            lowerVer = dbVersMap.get(dbVersMap.firstKey()).getVersion();
                        } else {
                            System.out.println("D:" + lowerVer + " " + dbVersMap.get(lowerVer));
                            lowerVerId = dbVersMap.get(lowerVer).getVerId();
                        }
                        Long higherVerId;
                        if (higherVer == null) {
                            higherVerId = dbVersMap.get(dbVersMap.lastKey()).getVerId() + verIdGap;
                        } else {
                            higherVerId = dbVersMap.get(higherVer).getVerId();
                        }

                        TreeMap<String, Long> subMap = new TreeMap<>(pkgvercmp);
                        subMap.putAll(tmpMap.subMap(lowerVer, false, higherVer, false));

                        Long diff = (higherVerId - lowerVerId - 10) / (subMap.size() + 1);
                        System.out.println(lowerVer + " " + lowerVerId + " " + higherVer + " " + higherVerId + " " + subMap.size());

                        if (diff > 10) {
                            Long nextId = lowerVerId + diff;
                            for (String subVer : subMap.keySet()) {
                                addedVers.add(subVer);
                                Long verId = subMap.get(subVer);

                                if (lastId == null && vercmp.compare(lowerVer, subVer) == 0) {
                                    versionIdsToVerids.put(verId, lowerVerId);
                                    lastId = lowerVerId;
                                } else if (lastId != null && vercmp.compare(lastVer, subVer) == 0) {
                                    versionIdsToVerids.put(verId, lastId);
                                } else {
                                    versionIdsToVerids.put(verId, nextId);
                                    lastId = nextId;
                                    nextId = nextId + diff;
                                }

                                lastVer = subVer;
                            }
                        } else {
                            logger.error("NOT ENOUGH GAP");
                            notEnoughGap = true;
                        }
                    }
                }
            }
            if (notEnoughGap) {
                logger.info("Not enough gap - updating all");
                Long verId = 0L;

                List<PkgVersion> allVersions = new ArrayList<>();
                allVersions.addAll(existingPkgVers);
                allVersions.addAll(newPkgVers);
                Collections.sort(allVersions, (a, b) -> pkgvercmp.compare(a.getVersion(), b.getVersion()));

                versionIdsToVerids.clear();
                lastPkgVer = null;
                for (PkgVersion pkgVer : allVersions) {
                    if (lastPkgVer != null && vercmp.compare(lastPkgVer.getVersion(),pkgVer.getVersion())==0) {
                        versionIdsToVerids.put(pkgVer.getId(), lastPkgVer.getVerId());
                        pkgVer.setVerId(lastPkgVer.getVerId());
                    } else {
                        verId = verId + verIdGap;
                        versionIdsToVerids.put(pkgVer.getId(), verId);
                        pkgVer.setVerId(verId);
                    }
                    lastPkgVer = pkgVer;
                }
            }
        }
        PreparedStatement pstmt = conn.prepareStatement("UPDATE pkg_ver SET verid=? WHERE id=?");
        int count = 0;
        for (Long id : versionIdsToVerids.keySet()) {
            Long verId = versionIdsToVerids.get(id);
            pstmt.setLong(1, verId);
            if (id == null) {
                logger.error("NULL id FROM ver "+verId);
            } else {
                pstmt.setLong(2, id);
                pstmt.addBatch();
                count++;
                if (count > 1000) {
                    pstmt.executeBatch();
                    count = 0;
                }
            }
        }
        if (count>0) {
            pstmt.executeBatch();
        }
        PkgData.pkgVeridsToVerIds.putAll(versionIdsToVerids);
        conn.commit();
    }
}
