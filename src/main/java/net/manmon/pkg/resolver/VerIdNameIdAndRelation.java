package net.manmon.pkg.resolver;

public class VerIdNameIdAndRelation {
    private Relation relation;
    private Long newNameId;

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Long getNewNameId() {
        return newNameId;
    }

    public void setNewNameId(Long newNameId) {
        this.newNameId = newNameId;
    }

    public String toString() {
        return "VerIdNameIdAndRelation relation="+relation+" newNameId="+newNameId;
    }
}
