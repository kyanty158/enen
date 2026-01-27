class ChoiceEffect {
    public final int healthDelta;
    public final int stressDelta;
    public final long moneyDelta;
    public final int ageDelta;
    public final String nextStatus;

    public ChoiceEffect(int healthDelta, int stressDelta, long moneyDelta, int ageDelta, String nextStatus) {
        this.healthDelta = healthDelta;
        this.stressDelta = stressDelta;
        this.moneyDelta = moneyDelta;
        this.ageDelta = ageDelta;
        this.nextStatus = nextStatus;
    }

    public ChoiceEffect withAgeDelta(int newAgeDelta) {
        return new ChoiceEffect(healthDelta, stressDelta, moneyDelta, newAgeDelta, nextStatus);
    }
}
