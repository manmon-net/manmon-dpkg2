package net.manmon.pkg.loader;

public class GenericRelationWithId extends GenericRelation {
    private Long id;

    public GenericRelationWithId() {

    }

    public GenericRelationWithId(Long id, GenericRelation relation) {
        super();
        setId(id);
        setName(relation.getName());
        setPre(relation.getPre());
        setRelation(relation.getRelation());
        setVersion(relation.getVersion());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Relation id="+id+" name="+getName()+" relation="+getRelation()+" version="+getVersion()+" pre="+getPre();
    }
}
