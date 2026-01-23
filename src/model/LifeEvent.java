package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 人生イベントを表すクラス
 */
public class LifeEvent {
    private String title;
    private List<Choice> choices;

    public LifeEvent(String title) {
        this.title = title;
        this.choices = new ArrayList<>();
    }

    public void addChoice(String text, int h, int s, long m, int ageDelta) {
        choices.add(new Choice(text, h, s, m, ageDelta));
    }

    public String getTitle() {
        return title;
    }

    /**
     * Viewでの表示用に詳細なテキストを返す
     */
    public String getText() {
        return title;
    }

    public List<Choice> getChoices() {
        return choices;
    }
}
