import java.io.Serializable;

class StatPoint implements Serializable {
    private static final long serialVersionUID = 1L;
    public final int age;
    public final int health;
    public final int stress;

    public StatPoint(int age, int health, int stress) {
        this.age = age;
        this.health = health;
        this.stress = stress;
    }
}
