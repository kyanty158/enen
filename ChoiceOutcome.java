class ChoiceOutcome {
    public final ChoiceEffect effect;
    public final int ageBefore;
    public final int ageAfter;
    public final int healthBefore;
    public final int healthAfter;
    public final int stressBefore;
    public final int stressAfter;
    public final long moneyBefore;
    public final long moneyAfter;
    public final String statusBefore;
    public final String statusAfter;

    public ChoiceOutcome(ChoiceEffect effect,
                         int ageBefore, int ageAfter,
                         int healthBefore, int healthAfter,
                         int stressBefore, int stressAfter,
                         long moneyBefore, long moneyAfter,
                         String statusBefore, String statusAfter) {
        this.effect = effect;
        this.ageBefore = ageBefore;
        this.ageAfter = ageAfter;
        this.healthBefore = healthBefore;
        this.healthAfter = healthAfter;
        this.stressBefore = stressBefore;
        this.stressAfter = stressAfter;
        this.moneyBefore = moneyBefore;
        this.moneyAfter = moneyAfter;
        this.statusBefore = statusBefore;
        this.statusAfter = statusAfter;
    }
}
