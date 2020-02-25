package net.manmon.pkg;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static Short ARCH_NOARCH = 1;
    private static Short ARCH_X86_64 = 2;

    public static void main(String[] args) throws Exception {
        new Main().load();
    }

    private ConcurrentHashMap<Long, ConcurrentHashMap<Short, ConcurrentHashMap<Long, TreeMap<Long,Long>>>> pkgUpstreamIdArchIdNameIdVerIdsAndPkgId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, HashSet<Long>> installedPkgSets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, PkgInfo> pkgInfos = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, String> names = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, String> versions = new ConcurrentHashMap<>();

    public void load() throws Exception {
        long start = new Date().getTime();
        Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1/manmon-pkg", "manmon-pkg", "");

        CopyManager copyManager = new CopyManager((BaseConnection) conn);
        ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
        long start1 = new Date().getTime();
        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream(pis);
        copyManager.copyOut("COPY pkg_upstream TO STDOUT WITH DELIMITER '#'", bos1);

        //BufferedReader br1 = new BufferedReader(new InputStreamReader(pis));
        BufferedReader br1 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bos1.toByteArray())));
        HashMap<Long, HashSet<Long>> pkgUptreams = new HashMap<>();
        System.out.println(new Date().getTime()-start1);

        String line1 = br1.readLine();
        while (line1 != null) {

            String[] s = line1.split("#");
            Long pkgId = Long.valueOf(s[0]);
            Long upstreamId = Long.valueOf(s[1]);
            line1 = br1.readLine();
            if (!pkgUptreams.containsKey(pkgId)) {
                pkgUptreams.put(pkgId, new HashSet<>());
            }
            pkgUptreams.get(pkgId).add(upstreamId);
        }
        System.out.println(new Date().getTime()-start1);

        long start2 = new Date().getTime();

        PreparedStatement pstmt = conn.prepareStatement("select pkg.id,pkg.arch,pkg.nameid_id,rpmvers.verid,local from pkg join rpmvers on pkg.rpm_ver_id=rpmvers.id");
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Long id = rs.getLong(1);
            String archString = rs.getString(2);
            Short arch = ARCH_NOARCH;
            if (archString.equals("x86_64")) {
                arch = ARCH_X86_64;
            }
            Long nameId = rs.getLong(3);
            Long verId = rs.getLong(4);
            Boolean local = rs.getBoolean(5);
            if (!local) {
                for (Long upstreamId : pkgUptreams.get(id)) {
                    if (!pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.containsKey(upstreamId)) {
                        pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.put(upstreamId, new ConcurrentHashMap<Short, ConcurrentHashMap<Long, TreeMap<Long, Long>>>());
                    }
                    if (!pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.get(upstreamId).containsKey(arch)) {
                        pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.get(upstreamId).put(arch, new ConcurrentHashMap<Long, TreeMap<Long, Long>>());
                    }
                    if (!pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.get(upstreamId).get(arch).containsKey(nameId)) {
                        pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.get(upstreamId).get(arch).put(nameId, new TreeMap<Long, Long>());
                    }
                    pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.get(upstreamId).get(arch).get(nameId).put(verId, id);
                }
            }
            PkgInfo pkgInfo = new PkgInfo();
            pkgInfo.setArch(arch);
            pkgInfo.setVerId(verId);
            pkgInfo.setNameId(nameId);
            pkgInfos.put(id, pkgInfo);
        }



        long end2 = new Date().getTime();
        System.out.println(end2-start2);

        pstmt = conn.prepareStatement("select pkg_id,pkg_set_id from pkg_set_installed");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            Long pkgId = rs.getLong(1);
            Long pkgSetId = rs.getLong(2);
            if (!installedPkgSets.containsKey(pkgSetId)) {
                installedPkgSets.put(pkgSetId, new HashSet<Long>());
            }
            installedPkgSets.get(pkgSetId).add(pkgId);
        }

        pstmt = conn.prepareStatement("SELECT id,name FROM pkg_name");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            Long id = rs.getLong(1);
            String name = rs.getString(2);
            names.put(id,name);
        }

        pstmt = conn.prepareStatement("SELECT verid,fullver FROM rpmvers");
        rs = pstmt.executeQuery();
        while (rs.next()) {
            String name = rs.getString(2);
            Long id = rs.getLong(1);
            versions.put(id,name);
        }


        HashSet<Long> clientUpstreamIds = new HashSet<>();
        clientUpstreamIds.add(31L);
        clientUpstreamIds.add(32L);

        TreeMap<String, String> result = new TreeMap<>();
        for (Long pkgId : installedPkgSets.get(431927L)) {
            PkgInfo pkgInfo = pkgInfos.get(pkgId);
            Long biggestFoundVerId = 0L;
            for (Long upstreamId : clientUpstreamIds) {
                if (pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.containsKey(upstreamId) && pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.get(upstreamId).containsKey(pkgInfo.getArch()) && pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.get(upstreamId).get(pkgInfo.getArch()).containsKey(pkgInfo.getNameId())) {
                    Long verId = pkgUpstreamIdArchIdNameIdVerIdsAndPkgId.get(upstreamId).get(pkgInfo.getArch()).get(pkgInfo.getNameId()).lastKey();
                    if (verId > biggestFoundVerId) {
                        biggestFoundVerId = verId;
                    }
                }
            }
            if (biggestFoundVerId > pkgInfo.getVerId()) {
                result.put(names.get(pkgInfo.getNameId()), versions.get(pkgInfo.getVerId()) + " " + versions.get(biggestFoundVerId) + " " + pkgId);
                //System.out.println(names.get(pkgInfo.getNameId())+" "+pkgInfo.getVerId()+" "+verId);
            }

//            for (String s : result.keySet()) {
//                System.out.println(s+" "+result.get(s));
//            }

        }


       long mem =  Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("MEM:"+(mem/1024/1024));
        System.out.println("TOTAL:"+(Runtime.getRuntime().totalMemory()/1024/1024));

        long end3 = new Date().getTime();
        //System.out.println(end3-end2);
    }
}
