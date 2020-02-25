package net.manmon.pkg.loader;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PkgData {
    public static final Boolean loadLock = false;
    public static final AtomicBoolean cacheLoaded = new AtomicBoolean(false);
    public static final AtomicBoolean readLock = new AtomicBoolean(false);

    public static final ConcurrentHashMap<String, Long> versionsToPkgVerids = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, Long> pkgVeridsToVerIds = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, String> verIdsToVers = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<Long, GenericRelation> relations = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Long> relationsByStr = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, String> eqRelations = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Long> eqRelationsByStr = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, GenericRelationWithId> eqWithVerRelationsByStr = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, GenericRelation> eqWithVerRelations = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgRequires = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgReplaces = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgConflicts = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgProvides = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgBreaks = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgOrRelations = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqRequires = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqProvides = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqConflicts = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqReplaces = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqBreaks = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqRequiresWithVer = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqProvidesWithVer = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqConflictsWithVer = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqReplacesWithVer = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> pkgEqBreaksWithVer = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<Long, GenericPackage> pkgs = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Long> pkgCache = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Long> nameIds = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, String> nameIdsToNames = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<Long, HashSet<Long>> orEqRequires = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> orRequires = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, Long> orRequireStrings = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashSet<Long>> orEqRequiresWithVer = new ConcurrentHashMap<>();

}
