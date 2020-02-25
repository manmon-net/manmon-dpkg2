package net.manmon.pkg.loader;

import java.util.HashSet;

public class GenericRelations {
    private HashSet<GenericRelation> relations = new HashSet<>();
    private HashSet<GenericOrRelation> orRelations = new HashSet<>();

    public HashSet<GenericRelation> getRelations() {
        return relations;
    }

    public void setRelations(HashSet<GenericRelation> relations) {
        this.relations = relations;
    }

    public HashSet<GenericOrRelation> getOrRelations() {
        return orRelations;
    }

    public void setOrRelations(HashSet<GenericOrRelation> orRelations) {
        this.orRelations = orRelations;
    }

    @Override
    public String toString() {
        return "relations="+relations+" orRelations="+orRelations;
    }
}
