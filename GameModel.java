import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class GameModel {
    private final Player player;
    private final GameLogic logic;
    private final Difficulty difficulty;
    private final GameStats stats;
    private final Set<Achievement> achievements;
    private final GameConfig config;

    public GameModel(GameConfig config) {
        this.config = config;
        this.difficulty = (config.getDifficulty() != null) ? config.getDifficulty() : Difficulty.NORMAL;
        String name = (config.getPlayerName() == null || config.getPlayerName().isBlank()) ? "プレイヤー" : config.getPlayerName();
        String status = (config.getStartStatus() == null || config.getStartStatus().isBlank()) ? "幼児" : config.getStartStatus();
        this.player = new Player(name, config.getStartAge(), difficulty.getStartHealth(), difficulty.getStartStress(),
                difficulty.getStartMoney(), status);
        this.logic = new GameLogic();
        this.stats = new GameStats(player);
        this.achievements = new LinkedHashSet<>();
        updateAchievements();
    }

    public GameModel(SaveData data) {
        Difficulty loadedDifficulty = (data != null && data.difficulty != null) ? data.difficulty : Difficulty.NORMAL;
        String name = (data != null && data.playerName != null && !data.playerName.isBlank()) ? data.playerName
                : "プレイヤー";
        String status = (data != null && data.status != null && !data.status.isBlank()) ? data.status : "幼児";
        int age = (data != null) ? data.age : 0;
        int health = (data != null) ? data.health : 100;
        int stress = (data != null) ? data.stress : 0;
        long money = (data != null) ? data.money : 0;

        this.difficulty = loadedDifficulty;
        this.player = new Player(name, age, health, stress, money, status);
        if (data != null) {
            player.setAlive(data.isAlive);
            if (data.history != null) {
                player.setHistory(data.history);
            }
            if (data.tagCounts != null) {
                player.setTagCounts(data.tagCounts);
            }
        }

        if (data != null && data.config != null) {
            this.config = data.config;
        } else {
            this.config = new GameConfig(name, loadedDifficulty, status, age);
        }

        this.logic = new GameLogic();
        this.stats = (data != null && data.stats != null) ? data.stats : new GameStats(player);
        this.achievements = (data != null && data.achievements != null)
                ? new LinkedHashSet<>(data.achievements)
                : new LinkedHashSet<>();
        updateAchievements();
    }

    public Player getPlayer() {
        return player;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public GameStats getStats() {
        return stats;
    }

    public Set<Achievement> getAchievements() {
        return achievements;
    }

    public GameConfig getConfig() {
        return config;
    }

    public ChoiceEffect getEffectiveEffect(Choice choice) {
        return difficulty.apply(choice);
    }

    public SaveData toSaveData() {
        SaveData data = new SaveData();
        data.playerName = player.getName();
        data.age = player.getAge();
        data.health = player.getHealth();
        data.stress = player.getStress();
        data.money = player.getMoney();
        data.status = player.getStatus();
        data.isAlive = player.isAlive();
        data.history = new ArrayList<>(player.getHistory());
        data.difficulty = difficulty;
        data.stats = stats;
        data.achievements = new LinkedHashSet<>(achievements);
        data.config = config;
        data.tagCounts = player.getTagCounts();
        data.savedAt = System.currentTimeMillis();
        return data;
    }

    public LifeEvent nextEvent() {
        return logic.getEventForAge(player);
    }

    public DeathResult applyChoice(Choice choice, String eventTitle) {
        ChoiceEffect effect = difficulty.apply(choice);

        if (effect.nextStatus != null) {
            player.setStatus(effect.nextStatus);
        }

        player.addHistory(eventTitle, choice.text);
        recordTags(eventTitle, choice.text);
        player.changeHealth(effect.healthDelta);
        player.changeStress(effect.stressDelta);
        player.changeMoney(effect.moneyDelta);
        player.checkVitality();

        if (!player.isAlive()) {
            ChoiceEffect appliedEffect = effect.withAgeDelta(0);
            stats.record(appliedEffect, player);
            updateAchievements();
            String reason = (player.getHealth() <= 0) ? "病死・衰弱死" : "ストレス死";
            return new DeathResult(
                    DeathResult.Type.DEAD,
                    reason,
                    "志半ばで力尽きました...",
                    player.getAge());
        }

        player.incrementAge(effect.ageDelta);

        stats.record(effect, player);
        recordStateTags();
        updateAchievements();

        return new DeathResult(DeathResult.Type.ALIVE, "", "", player.getAge());
    }

    public FinalResult buildFinalResult(DeathResult base, String lastEventTitle) {
        String endingTitle = base.title;
        String endingMessage = base.message;

        boolean reachedHundred = player.getAge() >= 100
                || (lastEventTitle != null && lastEventTitle.contains("大往生"));

        if (reachedHundred) {
            if (player.getMoney() >= 50000000) {
                endingTitle = "伝説のエンド";
                endingMessage = "100年の人生で巨万の富を築き、伝説として語り継がれました。";
            } else if (player.getMoney() >= 10000000) {
                endingTitle = "黄金の大往生";
                endingMessage = "豊かな老後を迎え、穏やかに生涯を閉じました。";
            } else {
                endingTitle = "大往生";
                endingMessage = "長い人生を全うし、静かに幕を下ろしました。";
            }
        } else if (player.getMoney() <= -10000000) {
            endingTitle = "借金地獄エンド";
            endingMessage = "返しきれない借金に追われ、人生は崩壊しました。";
        } else if ("社長".equals(player.getStatus()) && player.getMoney() >= 5000000) {
            endingTitle = "起業成功エンド";
            endingMessage = "苦難の末に事業を軌道に乗せ、成功を掴みました。";
        } else if (player.getStress() >= 100) {
            endingTitle = "ストレス死";
            endingMessage = "積み重なったストレスに心身が耐えられませんでした。";
        } else if (player.getHealth() <= 0) {
            endingTitle = "病死・衰弱死";
            endingMessage = "体力が尽き、人生の幕を閉じました。";
        }

        String honorTitle = computeHonorTitle();
        List<Achievement> ordered = new ArrayList<>(achievements);
        ordered.sort((a, b) -> Integer.compare(a.ordinal(), b.ordinal()));
        return new FinalResult(endingTitle, endingMessage, honorTitle, ordered, stats, difficulty);
    }

    public void recordSaveLoad() {
        player.addTag("save_or_load");
        updateAchievements();
    }

    private void updateAchievements() {
        if (player.getAge() >= 80) {
            achievements.add(Achievement.LONG_LIFE);
        }
        if (player.getAge() >= 100) {
            achievements.add(Achievement.CENTENARIAN);
        }
        if (player.getMoney() >= 10000000) {
            achievements.add(Achievement.MILLIONAIRE);
        }
        if (player.getMoney() <= -10000000) {
            achievements.add(Achievement.DEBT_HELL);
        }
        if (player.getAge() >= 30 && player.getHealth() >= 100) {
            achievements.add(Achievement.IRON_HEALTH);
        }
        if (player.getStress() >= 80 && player.isAlive()) {
            achievements.add(Achievement.STRESS_MASTER);
        }
        if ("社長".equals(player.getStatus())) {
            achievements.add(Achievement.CEO);
        }
        if ("ニート".equals(player.getStatus())) {
            achievements.add(Achievement.NEET);
        }
        if (stats.getMinHealth() <= 20) {
            achievements.add(Achievement.SURVIVOR);
        }
        if (stats.getTotalChoices() >= 50) {
            achievements.add(Achievement.WORKAHOLIC);
        }
        if (player.getAge() < 20 && !player.isAlive()) {
            achievements.add(Achievement.SPEED_RUN);
        }
        if (player.getAge() >= 60 && "社長".equals(player.getStatus())) {
            achievements.add(Achievement.LATE_BLOOMER);
        }
        if (player.getAge() >= 40 && player.getStress() <= 20) {
            achievements.add(Achievement.STRESS_FREE);
        }
        if (player.getMoney() < 0 && player.getAge() >= 100) {
            achievements.add(Achievement.HEART_OF_GOLD);
        }
        if (player.countTag("stress_survive") >= 20) {
            achievements.add(Achievement.BOUNCY);
        }
        if (player.countTag("health") >= 10) {
            achievements.add(Achievement.HEALTH_FANATIC);
        }
        if (player.countTag("social") >= 10) {
            achievements.add(Achievement.SOCIAL_BUTTERFLY);
        }
        if (player.countTag("debt_event") >= 1 && player.isAlive()) {
            achievements.add(Achievement.SURVIVED_DEBT);
        }
        if (player.countTag("save_or_load") >= 1) {
            achievements.add(Achievement.RESTARTER);
        }
        if (player.countTag("invest") >= 5) {
            achievements.add(Achievement.INVESTOR);
        }
        if (player.countTag("family") >= 8) {
            achievements.add(Achievement.FAMILY_FIRST);
        }
    }

    private String computeHonorTitle() {
        if (achievements.contains(Achievement.CENTENARIAN) && achievements.contains(Achievement.MILLIONAIRE)) {
            return "伝説級";
        }
        if (achievements.contains(Achievement.MILLIONAIRE)) {
            return "億万長者";
        }
        if (achievements.contains(Achievement.DEBT_HELL)) {
            return "借金王";
        }
        if (achievements.contains(Achievement.CEO)) {
            return "起業家";
        }
        if (achievements.contains(Achievement.LONG_LIFE)) {
            return "長寿の達人";
        }
        if (achievements.contains(Achievement.NEET)) {
            return "無職マスター";
        }
        if (achievements.contains(Achievement.SOCIAL_BUTTERFLY)) {
            return "社交界の華";
        }
        if (achievements.contains(Achievement.HEALTH_FANATIC)) {
            return "健康番長";
        }
        if (achievements.contains(Achievement.SURVIVED_DEBT)) {
            return "逆境の生還者";
        }
        return "平凡な人生";
    }

    private void recordTags(String eventTitle, String choiceText) {
        String text = (eventTitle == null ? "" : eventTitle) + " " + (choiceText == null ? "" : choiceText);
        if (containsAny(text, "健康", "病院", "人間ドック", "ジム", "散歩", "体操", "寝る", "睡眠")) {
            player.addTag("health");
        }
        if (containsAny(text, "恋愛", "合コン", "同窓会", "文化祭", "後夜祭")) {
            player.addTag("social");
        }
        if (containsAny(text, "投資", "株", "資産", "ボーナス")) {
            player.addTag("invest");
        }
        if (containsAny(text, "家族", "結婚", "子供", "孫", "介護", "家庭")) {
            player.addTag("family");
        }
        if (containsAny(text, "借金", "督促", "闇金", "夜逃げ")) {
            player.addTag("debt_event");
        }
        if (containsAny(text, "セーブ", "ロード")) {
            player.addTag("save_or_load");
        }
    }

    private void recordStateTags() {
        if (player.getStress() >= 50 && player.isAlive()) {
            player.addTag("stress_survive");
        }
    }

    private boolean containsAny(String text, String... keys) {
        for (String k : keys) {
            if (text.contains(k)) {
                return true;
            }
        }
        return false;
    }
}
