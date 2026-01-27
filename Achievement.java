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
    WORKAHOLIC("勤勉", "選択回数50回");

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
