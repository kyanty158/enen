import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class SaveManager {
    private static final String SAVE_FILE_NAME = "life100_save.dat";
    private static final String ENDINGS_FILE_NAME = "life100_endings.txt";
    private static final String TITLES_FILE_NAME = "life100_titles.txt";
    private static final String BEST_FILE_NAME = "life100_best.txt";
    private static final String STORY_FILE_NAME = "life100_story.txt";
    private static final int SLOT_COUNT = 3;

    private static Path getSavePath() {
        return Paths.get(System.getProperty("user.home"), SAVE_FILE_NAME);
    }

    private static Path getSavePath(int slot) {
        int safe = Math.min(Math.max(slot, 1), SLOT_COUNT);
        String name = "life100_save_slot" + safe + ".dat";
        return Paths.get(System.getProperty("user.home"), name);
    }

    private static Path getEndingsPath() {
        return Paths.get(System.getProperty("user.home"), ENDINGS_FILE_NAME);
    }

    private static Path getTitlesPath() {
        return Paths.get(System.getProperty("user.home"), TITLES_FILE_NAME);
    }

    private static Path getBestPath() {
        return Paths.get(System.getProperty("user.home"), BEST_FILE_NAME);
    }

    private static Path getStoryPath() {
        return Paths.get(System.getProperty("user.home"), STORY_FILE_NAME);
    }

    public static boolean hasSave() {
        for (int i = 1; i <= SLOT_COUNT; i++) {
            if (Files.exists(getSavePath(i))) {
                return true;
            }
        }
        return Files.exists(getSavePath());
    }

    public static boolean save(SaveData data) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(getSavePath()))) {
            out.writeObject(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean save(SaveData data, int slot) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(getSavePath(slot)))) {
            out.writeObject(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static SaveData load() {
        if (!hasSave()) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(getSavePath()))) {
            Object obj = in.readObject();
            if (obj instanceof SaveData) {
                return (SaveData) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
        return null;
    }

    public static SaveData load(int slot) {
        Path path = getSavePath(slot);
        if (!Files.exists(path)) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            Object obj = in.readObject();
            if (obj instanceof SaveData) {
                return (SaveData) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
        return null;
    }

    public static String getSaveLocation() {
        return getSavePath().toString();
    }

    public static String getSaveLocation(int slot) {
        return getSavePath(slot).toString();
    }

    public static void recordEnding(String endingTitle) {
        if (endingTitle == null || endingTitle.isBlank()) {
            return;
        }
        try {
            Path path = getEndingsPath();
            List<String> existing = Files.exists(path) ? Files.readAllLines(path) : new java.util.ArrayList<>();
            if (!existing.contains(endingTitle)) {
                existing.add(endingTitle);
                Files.write(path, existing);
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public static java.util.List<String> loadEndings() {
        try {
            Path path = getEndingsPath();
            if (Files.exists(path)) {
                return Files.readAllLines(path);
            }
        } catch (IOException e) {
            return java.util.Collections.emptyList();
        }
        return java.util.Collections.emptyList();
    }

    public static String getEndingsLocation() {
        return getEndingsPath().toString();
    }

    public static void recordTitle(String title) {
        if (title == null || title.isBlank()) {
            return;
        }
        try {
            Path path = getTitlesPath();
            List<String> existing = Files.exists(path) ? Files.readAllLines(path) : new java.util.ArrayList<>();
            if (!existing.contains(title)) {
                existing.add(title);
                Files.write(path, existing);
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public static java.util.List<String> loadTitles() {
        try {
            Path path = getTitlesPath();
            if (Files.exists(path)) {
                return Files.readAllLines(path);
            }
        } catch (IOException e) {
            return java.util.Collections.emptyList();
        }
        return java.util.Collections.emptyList();
    }

    public static String getTitlesLocation() {
        return getTitlesPath().toString();
    }

    public static void updateBest(int age, long money) {
        BestRecord best = loadBest();
        boolean updated = false;
        if (best.maxAge < age) {
            best.maxAge = age;
            updated = true;
        }
        if (best.maxMoney < money) {
            best.maxMoney = money;
            updated = true;
        }
        if (updated) {
            try {
                Files.writeString(getBestPath(), best.maxAge + "," + best.maxMoney);
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static BestRecord loadBest() {
        BestRecord record = new BestRecord();
        Path path = getBestPath();
        if (!Files.exists(path)) {
            return record;
        }
        try {
            String text = Files.readString(path).trim();
            String[] parts = text.split(",");
            if (parts.length >= 2) {
                record.maxAge = Integer.parseInt(parts[0]);
                record.maxMoney = Long.parseLong(parts[1]);
            }
        } catch (IOException | NumberFormatException e) {
            return record;
        }
        return record;
    }

    public static boolean saveStory(String text) {
        try {
            String header = "\n==== " + java.time.LocalDateTime.now() + " ====" + "\n";
            Files.writeString(getStoryPath(), header + text + "\n",
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getStoryLocation() {
        return getStoryPath().toString();
    }

    static class BestRecord {
        int maxAge = 0;
        long maxMoney = 0;
    }
}
