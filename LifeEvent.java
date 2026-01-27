import java.util.ArrayList;
import java.util.List;

class LifeEvent {
    private String title;
    private List<Choice> choices;

    public LifeEvent(String title) {
        this.title = title;
        this.choices = new ArrayList<>();
    }

    // ステータス変更ありの選択肢追加
    public void addChoice(String text, int h, int s, long m, int ageDelta, String nextStatus) {
        choices.add(new Choice(text, h, s, m, ageDelta, nextStatus));
    }

    // 通常の選択肢追加
    public void addChoice(String text, int h, int s, long m, int ageDelta) {
        choices.add(new Choice(text, h, s, m, ageDelta, null));
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return title;
    }

    public List<Choice> getChoices() {
        return choices;
    }
}
