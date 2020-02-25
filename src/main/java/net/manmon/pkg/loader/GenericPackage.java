package net.manmon.pkg.loader;

import java.util.HashSet;

public class GenericPackage {
    private Long id;
    private String name;
    private String arch;
    private String sha256hash = "NONE";
    private String version;
    private Long dpkgInstalledSize;
    private Long installedSize = 0L;
    private Long pkgSize = 0L;
    private Long archiveSize = 0L;
    private String pkgType = "RPM";
    private HashSet<GenericRelation> requiresRelations = new HashSet<>();
    private HashSet<GenericOrRelation> dependsOrRelations = new HashSet<>();
    private HashSet<GenericRelation> providesRelations = new HashSet<>();
    private HashSet<GenericRelation> conflictsRelations = new HashSet<>();
    private HashSet<GenericRelation> replacesRelations = new HashSet<>();
    private HashSet<GenericRelation> breaksRelations = new HashSet<>();
    private HashSet<GenericRelation> suggestsRelations = new HashSet<>();
    private HashSet<GenericRelation> recommendsRelations = new HashSet<>();
    private HashSet<GenericRelation> enhancesRelations = new HashSet<>();
    private Boolean local = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getSha256hash() {
        return sha256hash;
    }

    public void setSha256hash(String sha256hash) {
        this.sha256hash = sha256hash;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getDpkgInstalledSize() {
        return dpkgInstalledSize;
    }

    public void setDpkgInstalledSize(Long dpkgInstalledSize) {
        this.dpkgInstalledSize = dpkgInstalledSize;
    }

    public Long getInstalledSize() {
        return installedSize;
    }

    public void setInstalledSize(Long installedSize) {
        this.installedSize = installedSize;
    }

    public Long getPkgSize() {
        return pkgSize;
    }

    public void setPkgSize(Long pkgSize) {
        this.pkgSize = pkgSize;
    }

    public Long getArchiveSize() {
        return archiveSize;
    }

    public void setArchiveSize(Long archiveSize) {
        this.archiveSize = archiveSize;
    }

    public String getPkgType() {
        return pkgType;
    }

    public void setPkgType(String pkgType) {
        this.pkgType = pkgType;
    }

    public HashSet<GenericRelation> getRequiresRelations() {
        return requiresRelations;
    }

    public void setRequiresRelations(HashSet<GenericRelation> requiresRelations) {
        this.requiresRelations = requiresRelations;
    }

    public HashSet<GenericOrRelation> getDependsOrRelations() {
        return dependsOrRelations;
    }

    public void setDependsOrRelations(HashSet<GenericOrRelation> dependsOrRelations) {
        this.dependsOrRelations = dependsOrRelations;
    }

    public HashSet<GenericRelation> getProvidesRelations() {
        return providesRelations;
    }

    public void setProvidesRelations(HashSet<GenericRelation> providesRelations) {
        this.providesRelations = providesRelations;
    }

    public HashSet<GenericRelation> getConflictsRelations() {
        return conflictsRelations;
    }

    public void setConflictsRelations(HashSet<GenericRelation> conflictsRelations) {
        this.conflictsRelations = conflictsRelations;
    }

    public HashSet<GenericRelation> getReplacesRelations() {
        return replacesRelations;
    }

    public void setReplacesRelations(HashSet<GenericRelation> replacesRelations) {
        this.replacesRelations = replacesRelations;
    }

    public HashSet<GenericRelation> getBreaksRelations() {
        return breaksRelations;
    }

    public void setBreaksRelations(HashSet<GenericRelation> breaksRelations) {
        this.breaksRelations = breaksRelations;
    }

    public HashSet<GenericRelation> getSuggestsRelations() {
        return suggestsRelations;
    }

    public void setSuggestsRelations(HashSet<GenericRelation> suggestsRelations) {
        this.suggestsRelations = suggestsRelations;
    }

    public HashSet<GenericRelation> getRecommendsRelations() {
        return recommendsRelations;
    }

    public void setRecommendsRelations(HashSet<GenericRelation> recommendsRelations) {
        this.recommendsRelations = recommendsRelations;
    }

    public HashSet<GenericRelation> getEnhancesRelations() {
        return enhancesRelations;
    }

    public void setEnhancesRelations(HashSet<GenericRelation> enhancesRelations) {
        this.enhancesRelations = enhancesRelations;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }
}
