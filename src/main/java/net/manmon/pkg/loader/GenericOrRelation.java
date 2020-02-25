package net.manmon.pkg.loader;

import java.util.HashSet;

public class GenericOrRelation {
    private HashSet<GenericRelation> orRelations = new HashSet<>();

    public HashSet<GenericRelation> getOrRelations() {
        return orRelations;
    }

    public void setOrRelations(HashSet<GenericRelation> orRelations) {
        this.orRelations = orRelations;
    }

    @Override
    public String toString() {
        return "OR relations="+orRelations;
    }
}
