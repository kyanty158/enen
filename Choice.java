class Choice {
    public String text;
    public int healthDelta;
    public int stressDelta;
    public long moneyDelta;
    public int ageDelta;
    public String nextStatus; // この選択肢を選んだ後のステータス変更（nullなら変更なし）

    public Choice(String text, int h, int s, long m, int ageDelta, String nextStatus) {
        this.text = text;
        this.healthDelta = h;
        this.stressDelta = s;
        this.moneyDelta = m;
        this.ageDelta = ageDelta;
        this.nextStatus = nextStatus;
    }

    // 既存のコンストラクタ互換用（ステータス変更なし）
    public Choice(String text, int h, int s, long m, int ageDelta) {
        this(text, h, s, m, ageDelta, null);
    }

    public String getText() {
        return text;
    }
}
