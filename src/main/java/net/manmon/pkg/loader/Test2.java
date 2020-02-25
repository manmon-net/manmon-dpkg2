package net.manmon.pkg.loader;

import java.util.HashSet;

public class Test2 {
    public static void main(String[] args) throws Exception {
        HashSet<GenericPackage> pkgs = DpkgParser.loadPackagesFile("/var/lib/dpkg/status");
        for (GenericPackage pkg : pkgs) {
            System.out.println(pkg.getName());
        }
    }
}
