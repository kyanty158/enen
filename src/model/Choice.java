package model;

/**
 * プレイヤーの選択肢を表すクラス
 */
public class Choice {
    public String text;
    public int healthDelta;
    public int stressDelta;
    public long moneyDelta;
    public int ageDelta;

    public Choice(String text, int h, int s, long m, int ageDelta) {
        this.text = text;
        this.healthDelta = h;
        this.stressDelta = s;
        this.moneyDelta = m;
        this.ageDelta = ageDelta;
    }

    /**
     * ボタン表示用にテキストを整形
     */
    public String getText() {
        return text + " (+" + ageDelta + "年)";
    }
}
