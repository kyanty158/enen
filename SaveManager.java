import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class SaveManager {
    private static final String SAVE_FILE_NAME = "life100_save.dat";

    private static Path getSavePath() {
        return Paths.get(System.getProperty("user.home"), SAVE_FILE_NAME);
    }

    public static boolean hasSave() {
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

    public static String getSaveLocation() {
        return getSavePath().toString();
    }
}
