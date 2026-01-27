import java.io.Serializable;

class Npc implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String type;
    private int relation;
    private final String traits;

    public Npc(String name, String type, int relation) {
        this.name = name;
        this.type = type;
        this.relation = relation;
        this.traits = TraitGenerator.randomTraits();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getRelation() {
        return relation;
    }

    public void addRelation(int delta) {
        relation += delta;
    }

    public String getTraitsText() {
        return traits;
    }
}
