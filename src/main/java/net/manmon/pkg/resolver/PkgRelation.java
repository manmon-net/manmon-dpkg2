package net.manmon.pkg.resolver;

public class PkgRelation {
    private Boolean pre;
    private String flags;
    private Long nameId;
    private Long pkgVerId;

    public PkgRelation() {

    }

    public PkgRelation(DbRelation dbRelation) {
        setFlags(dbRelation.getFlags());
        setNameId(dbRelation.getNameId());
        setPkgVerId(dbRelation.getPkgVerId());
        setPre(dbRelation.getPre());
    }

    public Boolean getPre() {
        return pre;
    }

    public void setPre(Boolean pre) {
        this.pre = pre;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public Long getNameId() {
        return nameId;
    }

    public void setNameId(Long nameId) {
        this.nameId = nameId;
    }

    public Long getPkgVerId() {
        return pkgVerId;
    }

    public void setPkgVerId(Long pkgVerId) {
        this.pkgVerId = pkgVerId;
    }
}
