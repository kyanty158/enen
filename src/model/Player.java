package model;

/**
 * プレイヤーの状態を管理するクラス
 */
public class Player {
    public static final int MAX_AGE_LIMIT = 8000;
    public static final int HUMAN_LIFESPAN = 100;

    private int age;
    private int health;
    private int stress;
    private long money;
    private boolean isAlive;

    public Player() {
        this.age = 15;
        this.health = 80;
        this.stress = 20;
        this.money = 0;
        this.isAlive = true;
    }

    public void incrementAge(int years) {
        this.age += years;
    }

    public void changeHealth(int amount) {
        this.health += amount;
        if (this.health > 100)
            this.health = 100;
    }

    public void changeStress(int amount) {
        this.stress += amount;
        if (this.stress < 0)
            this.stress = 0;
    }

    public void changeMoney(long amount) {
        this.money += amount;
        if (this.money < 0)
            this.money = 0;
    }

    public void checkVitality() {
        if (this.health <= 0 || this.stress >= 100) {
            this.isAlive = false;
        }
    }

    // Getter methods
    public int getAge() {
        return age;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getHealth() {
        return health;
    }

    public int getStress() {
        return stress;
    }

    public long getMoney() {
        return money;
    }
}
