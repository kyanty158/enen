import java.io.Serializable;

class Economy implements Serializable {
    private static final long serialVersionUID = 1L;

    private int year;
    private double inflation;
    private int boom; // -1 recess, 0 stable, 1 boom

    public Economy() {
        this.year = 0;
        this.inflation = 1.0;
        this.boom = 0;
    }

    public void advanceYears(int years) {
        for (int i = 0; i < Math.max(1, years); i++) {
            year++;
            double dice = Math.random();
            if (dice < 0.15) {
                boom = -1;
            } else if (dice < 0.35) {
                boom = 1;
            } else {
                boom = 0;
            }
            double infl = 0.98 + Math.random() * 0.06; // 0.98 - 1.04
            inflation *= infl;
            inflation = Math.max(0.8, Math.min(1.3, inflation));
        }
    }

    public long applyMoneyDelta(long delta) {
        double multiplier = 1.0;
        if (boom > 0 && delta > 0) {
            multiplier = 1.15;
        } else if (boom < 0 && delta > 0) {
            multiplier = 0.85;
        }
        if (boom < 0 && delta < 0) {
            multiplier = 1.2;
        }
        double adjusted = delta * multiplier * inflation;
        return Math.round(adjusted);
    }

    public String getLabel() {
        if (boom > 0) return "好景気";
        if (boom < 0) return "不況";
        return "安定";
    }

    public String getInflationLabel() {
        return String.format("物価×%.2f", inflation);
    }

    public int getYear() {
        return year;
    }
}
