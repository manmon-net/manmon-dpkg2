package net.manmon.pkg.loader;

import java.util.HashMap;
import java.util.HashSet;

public class CacheOrRelationInfo {
    private HashMap<Long, HashSet<Long>> orEqRequires = new HashMap<>();
    private HashMap<Long,HashSet<Long>> orRequires = new HashMap<>();
    private HashMap<Long,HashSet<Long>> orEqWithVerRequires = new HashMap<>();
    private HashMap<Long, HashSet<Long>> pkgOrRequires = new HashMap<>();
    private HashMap<String, Long> orRequireStrings = new HashMap<>();

}
