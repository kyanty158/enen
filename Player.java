import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private Map<String, Integer> pickedTags;
    private String lastEventTitle;
    private List<StatPoint> statPoints;
    private List<Npc> npcs;
    private Location location;
    private Chapter chapter;

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
        this.pickedTags = new HashMap<>();
        this.lastEventTitle = null;
        this.statPoints = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.location = Location.HOME;
        this.chapter = Chapter.fromAge(age);
        recordStatPoint();
    }

    public void addHistory(String eventTitle, String choiceText) {
        String log = String.format("%d歳 [%s]: %s\n   ↳ %s", age, status, eventTitle, choiceText);
        history.add(log);
        rememberEvent(eventTitle);
        setLastEventTitle(eventTitle);
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = new ArrayList<>(history);
        rebuildSeenEvents();
    }

    public void addTag(String tag) {
        if (tag != null) {
            pickedTags.put(tag, pickedTags.getOrDefault(tag, 0) + 1);
        }
    }

    public int countTag(String tag) {
        return pickedTags.getOrDefault(tag, 0);
    }

    public boolean hasTag(String tag) {
        return pickedTags.containsKey(tag);
    }

    public Map<String, Integer> getTagCounts() {
        return new HashMap<>(pickedTags);
    }

    public void setTagCounts(Map<String, Integer> counts) {
        pickedTags.clear();
        if (counts != null) {
            pickedTags.putAll(counts);
        }
    }

    public String getLastEventTitle() {
        return lastEventTitle;
    }

    public void setLastEventTitle(String title) {
        this.lastEventTitle = title;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (location != null) {
            this.location = location;
        }
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        if (chapter != null) {
            this.chapter = chapter;
        }
    }

    public void recordStatPoint() {
        statPoints.add(new StatPoint(age, health, stress));
    }

    public List<StatPoint> getStatPoints() {
        return new ArrayList<>(statPoints);
    }

    public void setStatPoints(List<StatPoint> points) {
        statPoints = new ArrayList<>();
        if (points != null) {
            statPoints.addAll(points);
        }
    }

    public List<Npc> getNpcs() {
        return new ArrayList<>(npcs);
    }

    public void setNpcs(List<Npc> list) {
        npcs = new ArrayList<>();
        if (list != null) {
            npcs.addAll(list);
        }
    }

    public void bumpNpc(String type, int delta, String context) {
        if (type == null) {
            return;
        }
        for (Npc npc : npcs) {
            if (type.equals(npc.getType())) {
                npc.addRelation(npcAdjustedDelta(npc, delta, context));
                return;
            }
        }
        Npc created = new Npc(NameGenerator.randomName(), type, delta);
        created.addRelation(npcAdjustedDelta(created, 0, context));
        npcs.add(created);
    }

    private int npcAdjustedDelta(Npc npc, int delta, String context) {
        int adjusted = delta;
        String traits = npc.getTraitsText();
        if (traits.contains("優しい")) adjusted += 2;
        if (traits.contains("ドライ")) adjusted -= 1;
        if (traits.contains("情熱")) adjusted += 1;
        if (traits.contains("嫉妬深い") && context != null && context.contains("合コン")) adjusted -= 3;
        return adjusted;
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
