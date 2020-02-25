package net.manmon.pkg.loader;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        TreeMap<Long, Long> map = new TreeMap<>();
        long start = new Date().getTime();
        for (long l=0;l<100000;l++) {
            map.put(l,l);
        }
        long end = new Date().getTime();
        System.out.println(end-start);

        HashMap<String,Long> map2 = new HashMap<>();
        for (long l=0;l<100000;l++) {
            map2.put(l+"",l);
        }
        TreeMap<String, Long> map3 = new TreeMap<>();
        map3.putAll(map2);
        long end2 = new Date().getTime();
        System.out.println(end2-end);

        List<String> list = new ArrayList<>();
        for (long l=0;l<100000;l++) {
           list.add(l+"");
        }
        Collections.sort(list, new PkgVersionComparator());
        long end3 = new Date().getTime();
        System.out.println(end3-end2);
    }
}
