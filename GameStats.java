import java.io.Serializable;

class GameStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalChoices;
    private int maxHealth;
    private int minHealth;
    private int maxStress;
    private int minStress;
    private long maxMoney;
    private long minMoney;
    private int maxAge;
    private long totalMoneyChange;
    private int totalHealthChange;
    private int totalStressChange;
    private int totalAgeChange;

    public GameStats(Player player) {
        this.totalChoices = 0;
        this.maxHealth = player.getHealth();
        this.minHealth = player.getHealth();
        this.maxStress = player.getStress();
        this.minStress = player.getStress();
        this.maxMoney = player.getMoney();
        this.minMoney = player.getMoney();
        this.maxAge = player.getAge();
    }

    public void record(ChoiceEffect effect, Player player) {
        totalChoices++;
        totalHealthChange += effect.healthDelta;
        totalStressChange += effect.stressDelta;
        totalMoneyChange += effect.moneyDelta;
        totalAgeChange += effect.ageDelta;

        maxHealth = Math.max(maxHealth, player.getHealth());
        minHealth = Math.min(minHealth, player.getHealth());
        maxStress = Math.max(maxStress, player.getStress());
        minStress = Math.min(minStress, player.getStress());
        maxMoney = Math.max(maxMoney, player.getMoney());
        minMoney = Math.min(minMoney, player.getMoney());
        maxAge = Math.max(maxAge, player.getAge());
    }

    public int getTotalChoices() {
        return totalChoices;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMinHealth() {
        return minHealth;
    }

    public int getMaxStress() {
        return maxStress;
    }

    public int getMinStress() {
        return minStress;
    }

    public long getMaxMoney() {
        return maxMoney;
    }

    public long getMinMoney() {
        return minMoney;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public long getTotalMoneyChange() {
        return totalMoneyChange;
    }

    public int getTotalHealthChange() {
        return totalHealthChange;
    }

    public int getTotalStressChange() {
        return totalStressChange;
    }

    public int getTotalAgeChange() {
        return totalAgeChange;
    }
}
