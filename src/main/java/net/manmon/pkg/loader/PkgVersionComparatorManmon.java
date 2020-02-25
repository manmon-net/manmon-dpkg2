package net.manmon.pkg.loader;

import java.util.Comparator;

public class PkgVersionComparatorManmon implements Comparator<String> {
    private PkgVersionComparator vercmp = new PkgVersionComparator();
    @Override
    public int compare(String s1, String s2) {
        int rel = vercmp.compare(s1,s2);
        if (rel == 0 && s1 != null && s2 != null) {
            //System.out.println("VER: "+s1+" "+s2);
            return s1.compareTo(s2);
        } else {
            return rel;
        }
    }
}