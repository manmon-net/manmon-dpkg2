package net.manmon.pkg.resolver;

public class RelationVerIdAndPkgId {
    private Relation relation;
    private Long verId;
    private Long newNameId;
    private Long pkgId;

    public Long getNewNameId() {
        return newNameId;
    }

    public void setNewNameId(Long newNameId) {
        this.newNameId = newNameId;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Long getVerId() {
        return verId;
    }

    public void setVerId(Long verId) {
        this.verId = verId;
    }

    public Long getPkgId() {
        return pkgId;
    }

    public void setPkgId(Long pkgId) {
        this.pkgId = pkgId;
    }

    public String toString() {
        return "RelationVerIdAndPkgId newNameId="+newNameId+" relation="+relation+" verId="+verId+" pkgId="+pkgId;
    }
}
