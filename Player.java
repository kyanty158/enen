import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Player {
    public static final int MAX_LIFESPAN = 100;

    private String name;
    private int age;
    private int health;
    private int stress;
    private long money;
    private boolean isAlive;
    private String status; // 現在の身分（学生、社会人、ニートなど）
    private List<String> history;
    private Set<String> seenEvents;

    public Player() {
        this("プレイヤー", 0, 100, 0, 0, "幼児");
    }

    public Player(String name, int age, int health, int stress, long money, String status) {
        this.name = name;
        this.age = age;
        this.health = health;
        this.stress = stress;
        this.money = money;
        this.isAlive = true;
        this.status = status; // 初期ステータス
        this.history = new ArrayList<>();
        this.seenEvents = new HashSet<>();
    }

    public void addHistory(String eventTitle, String choiceText) {
        String log = String.format("%d歳 [%s]: %s\n   ↳ %s", age, status, eventTitle, choiceText);
        history.add(log);
        rememberEvent(eventTitle);
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = new ArrayList<>(history);
        rebuildSeenEvents();
    }

    public boolean hasSeenEvent(String title) {
        return title != null && seenEvents.contains(title);
    }

    public void rememberEvent(String title) {
        if (title != null) {
            seenEvents.add(title);
        }
    }

    private void rebuildSeenEvents() {
        seenEvents.clear();
        for (String log : history) {
            String title = extractEventTitle(log);
            if (title != null && !title.isBlank()) {
                seenEvents.add(title);
            }
        }
    }

    private String extractEventTitle(String log) {
        if (log == null) {
            return null;
        }
        int marker = log.indexOf("]: ");
        if (marker < 0) {
            return null;
        }
        int start = marker + 3;
        int end = log.indexOf("\n", start);
        if (end < 0) {
            end = log.length();
        }
        return log.substring(start, end);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public void incrementAge(int years) {
        this.age += years;
    }

    public void setAge(int age) {
        this.age = age;
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

    public void setAlive(boolean alive) {
        this.isAlive = alive;
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
