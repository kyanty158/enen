enum Difficulty {
    EASY("イージー", 1.1, 0.8, 1.1, 100, 0, 100000),
    NORMAL("ノーマル", 1.0, 1.0, 1.0, 100, 0, 0),
    HARD("ハード", 0.9, 1.2, 0.9, 90, 10, -100000);

    private final String label;
    private final double healthMultiplier;
    private final double stressMultiplier;
    private final double moneyMultiplier;
    private final int startHealth;
    private final int startStress;
    private final long startMoney;

    Difficulty(String label, double healthMultiplier, double stressMultiplier, double moneyMultiplier,
               int startHealth, int startStress, long startMoney) {
        this.label = label;
        this.healthMultiplier = healthMultiplier;
        this.stressMultiplier = stressMultiplier;
        this.moneyMultiplier = moneyMultiplier;
        this.startHealth = startHealth;
        this.startStress = startStress;
        this.startMoney = startMoney;
    }

    public ChoiceEffect apply(Choice choice) {
        int health = (int) Math.round(choice.healthDelta * healthMultiplier);
        int stress = (int) Math.round(choice.stressDelta * stressMultiplier);
        long money = Math.round(choice.moneyDelta * moneyMultiplier);
        return new ChoiceEffect(health, stress, money, choice.ageDelta, choice.nextStatus);
    }

    public String getLabel() {
        return label;
    }

    public int getStartHealth() {
        return startHealth;
    }

    public int getStartStress() {
        return startStress;
    }

    public long getStartMoney() {
        return startMoney;
    }

    @Override
    public String toString() {
        return label;
    }
}
