package net.manmon.pkg.resolver;

public class DbRelation {
    private Long id;
    private Boolean pre;
    private String flags;
    private Long nameId;
    private Long pkgVerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
