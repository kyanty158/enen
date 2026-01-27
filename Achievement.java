enum Achievement {
    LONG_LIFE("長寿", "80歳到達"),
    CENTENARIAN("百寿", "100歳到達"),
    MILLIONAIRE("大富豪", "所持金1000万円以上"),
    DEBT_HELL("借金王", "借金1000万円以上"),
    IRON_HEALTH("健康優良", "30歳以降で体力100"),
    STRESS_MASTER("ストレス職人", "ストレス80以上で生存"),
    CEO("起業家", "社長になった"),
    NEET("ニート道", "ニートになった"),
    SURVIVOR("生還者", "体力20以下を経験"),
    WORKAHOLIC("勤勉", "選択回数50回"),
    SPEED_RUN("スピードラン", "20歳未満で死亡"),
    LATE_BLOOMER("遅咲き", "60歳以降で社長"),
    STRESS_FREE("ストレスフリー", "ストレス20以下で40歳到達"),
    HEART_OF_GOLD("善人", "所持金マイナスで大往生"),
    BOUNCY("打たれ強い", "ストレス50以上で20回以上生存"),
    HEALTH_FANATIC("健康マニア", "健康系イベントを10回選択"),
    SOCIAL_BUTTERFLY("社交家", "恋愛/合コン/同窓会系を10回選択"),
    SURVIVED_DEBT("借金サバイバー", "借金イベントから生還"),
    RESTARTER("やり直し名人", "セーブ/ロードを使う"),
    INVESTOR("投資家", "投資系選択を5回以上"),
    FAMILY_FIRST("家族思い", "家族系選択を8回以上");

    private final String label;
    private final String description;

    Achievement(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
