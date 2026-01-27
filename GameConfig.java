import java.io.Serializable;

class GameConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final Difficulty difficulty;
    private final String startStatus;
    private final int startAge;

    public GameConfig(String playerName, Difficulty difficulty, String startStatus, int startAge) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.startStatus = startStatus;
        this.startAge = startAge;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getStartStatus() {
        return startStatus;
    }

    public int getStartAge() {
        return startAge;
    }
}
