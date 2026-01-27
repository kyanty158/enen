import java.util.List;

class FinalResult {
    public final String endingTitle;
    public final String endingMessage;
    public final String honorTitle;
    public final List<Achievement> achievements;
    public final GameStats stats;
    public final Difficulty difficulty;

    public FinalResult(String endingTitle, String endingMessage, String honorTitle,
                       List<Achievement> achievements, GameStats stats, Difficulty difficulty) {
        this.endingTitle = endingTitle;
        this.endingMessage = endingMessage;
        this.honorTitle = honorTitle;
        this.achievements = achievements;
        this.stats = stats;
        this.difficulty = difficulty;
    }
}
