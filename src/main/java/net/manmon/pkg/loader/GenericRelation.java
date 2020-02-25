package net.manmon.pkg.loader;

public class GenericRelation {
    private String name;
    private String relation;
    private String version;
    private Boolean pre = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getPre() {
        return pre;
    }

    public void setPre(Boolean pre) {
        this.pre = pre;
    }

    @Override
    public String toString() {
        return "Relation name="+name+" relation="+relation+" version="+version+" pre="+pre;
    }
}
