import java.util.ArrayList;
import java.util.List;

class Player {
    public static final int MAX_LIFESPAN = 100;

    private int age;
    private int health;
    private int stress;
    private long money;
    private boolean isAlive;
    private String status; // 現在の身分（学生、社会人、ニートなど）
    private List<String> history;

    public Player() {
        this.age = 0;
        this.health = 100;
        this.stress = 0;
        this.money = 0;
        this.isAlive = true;
        this.status = "幼児"; // 初期ステータス
        this.history = new ArrayList<>();
    }

    public void addHistory(String eventTitle, String choiceText) {
        String log = String.format("%d歳 [%s]: %s\n   ↳ %s", age, status, eventTitle, choiceText);
        history.add(log);
    }

    public List<String> getHistory() {
        return history;
    }

    public void incrementAge(int years) {
        this.age += years;
    }

    public void setStatus(String status) {
        if (status != null) {
            this.status = status;
        }
    }

    public String getStatus() {
        return status;
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
    }

    public void checkVitality() {
        // 老化ダメージ: 60歳以降
        if (age > 60) {
            int agingDamage = (age - 60) / 4 + 1;
            this.health -= agingDamage;
        }

        if (this.health <= 0 || this.stress >= 100) {
            this.isAlive = false;
        }
    }

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
