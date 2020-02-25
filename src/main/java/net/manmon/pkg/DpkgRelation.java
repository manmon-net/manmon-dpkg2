package net.manmon.pkg;

public class DpkgRelation {
    private Boolean orRelation = false;
    private String relation;
    private String relationVersion;
    private String relationPackageName;

    public Boolean getOrRelation() {
        return orRelation;
    }

    public void setOrRelation(Boolean orRelation) {
        this.orRelation = orRelation;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRelationVersion() {
        return relationVersion;
    }

    public void setRelationVersion(String relationVersion) {
        this.relationVersion = relationVersion;
    }

    public String getRelationPackageName() {
        return relationPackageName;
    }

    public void setRelationPackageName(String relationPackageName) {
        this.relationPackageName = relationPackageName;
    }

    @Override
    public String toString(){
        if (relation != null) {
            if (relationVersion != null) {
                return "DPKG relation requiredRelation="+relationPackageName+" relation="+relation+" version="+relationVersion;
            } else {
                return "DPKG relation requiredRelation="+relationPackageName+" relation="+relation;
            }
        } else {
            return "DPKG relation requiredRelation="+relationPackageName;
        }

    }
}
