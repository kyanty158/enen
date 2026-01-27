import java.util.Random;

class TraitGenerator {
    private static final String[] TRAITS = {
            "優しい", "ドライ", "情熱", "慎重", "明るい", "嫉妬深い", "クール"
    };
    private static final Random RANDOM = new Random();

    public static String randomTraits() {
        String a = TRAITS[RANDOM.nextInt(TRAITS.length)];
        String b = TRAITS[RANDOM.nextInt(TRAITS.length)];
        if (a.equals(b)) {
            b = "穏やか";
        }
        return a + "・" + b;
    }
}
