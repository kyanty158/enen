class GameModel {
    private final Player player;
    private final GameLogic logic;

    public GameModel() {
        this.player = new Player();
        this.logic = new GameLogic();
    }

    public Player getPlayer() {
        return player;
    }

    public LifeEvent nextEvent() {
        return logic.getEventForAge(player);
    }

    public DeathResult applyChoice(Choice choice, String eventTitle) {
        // 0. ステータス更新（もしあれば）
        if (choice.nextStatus != null) {
            player.setStatus(choice.nextStatus);
        }

        // 1. 履歴に追加
        player.addHistory(eventTitle, choice.text);

        // 2. ステータス反映
        player.changeHealth(choice.healthDelta);
        player.changeStress(choice.stressDelta);
        player.changeMoney(choice.moneyDelta);

        // 3. 生存チェック
        player.checkVitality();

        if (!player.isAlive()) {
            String reason = (player.getHealth() <= 0) ? "病死・衰弱死" : "ストレス死";
            return new DeathResult(
                    DeathResult.Type.DEAD,
                    reason,
                    "志半ばで力尽きました...",
                    player.getAge());
        }

        // 4. 年齢を加算
        player.incrementAge(choice.ageDelta);

        return new DeathResult(DeathResult.Type.ALIVE, "", "", player.getAge());
    }
}
