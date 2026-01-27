import java.io.Serializable;
import java.util.List;
import java.util.Set;

class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    public String playerName;
    public int age;
    public int health;
    public int stress;
    public long money;
    public String status;
    public boolean isAlive;
    public List<String> history;
    public Difficulty difficulty;
    public GameStats stats;
    public Set<Achievement> achievements;
    public GameConfig config;
    public long savedAt;
}
