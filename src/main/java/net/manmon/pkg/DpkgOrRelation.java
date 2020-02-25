package net.manmon.pkg;

import java.util.HashSet;

public class DpkgOrRelation {
    private HashSet<DpkgRelation> orRelations = new HashSet<>();

    public HashSet<DpkgRelation> getOrRelations() {
        return orRelations;
    }

    public void setOrRelations(HashSet<DpkgRelation> orRelations) {
        this.orRelations = orRelations;
    }
}
