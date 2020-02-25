package net.manmon.pkg.resolver;

public class DbOrPkgRelation {
    private Long orRequireId;
    private Long pkgId;

    public Long getOrRequireId() {
        return orRequireId;
    }

    public void setOrRequireId(Long orRequireId) {
        this.orRequireId = orRequireId;
    }

    public Long getPkgId() {
        return pkgId;
    }

    public void setPkgId(Long pkgId) {
        this.pkgId = pkgId;
    }
}
