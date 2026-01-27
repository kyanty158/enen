import java.util.Random;

class NameGenerator {
    private static final String[] FIRST = {
            "葵", "楓", "凛", "陽菜", "結衣", "碧", "凪", "奏", "葵", "結月",
            "蓮", "湊", "颯太", "悠斗", "大輔", "陸", "悠真", "海斗", "悠", "翔"
    };
    private static final String[] LAST = {
            "佐藤", "鈴木", "高橋", "田中", "伊藤", "渡辺", "山本", "中村", "小林", "加藤"
    };
    private static final Random RANDOM = new Random();

    public static String randomName() {
        String first = FIRST[RANDOM.nextInt(FIRST.length)];
        String last = LAST[RANDOM.nextInt(LAST.length)];
        return last + " " + first;
    }
}
