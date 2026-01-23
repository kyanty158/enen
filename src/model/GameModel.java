package model;

/**
 * ゲーム全体のモデルを管理するクラス
 * Controllerから呼ばれるメソッドを実装
 */
public class GameModel {
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
        return logic.getEventForAge(player.getAge());
    }

    /**
     * 選択肢を適用してゲーム状態を更新する
     */
    public DeathResult applyChoice(Choice choice) {
        // ステータス反映
        player.changeHealth(choice.healthDelta);
        player.changeStress(choice.stressDelta);
        player.changeMoney(choice.moneyDelta);

        // 死亡判定
        player.checkVitality();

        if (!player.isAlive()) {
            return new DeathResult(
                    DeathResult.Type.DEAD,
                    "過労死・ストレス死",
                    "あなたの人生はここで幕を閉じました。",
                    player.getAge());
        }

        // 年齢加算
        player.incrementAge(choice.ageDelta);

        // 寿命判定
        if (player.getAge() >= Player.MAX_AGE_LIMIT) {
            return new DeathResult(
                    DeathResult.Type.DEAD,
                    "伝説のエンド",
                    "あなたは歴史の観測者として昇華されました。",
                    player.getAge());
        }

        // 生存
        return new DeathResult(DeathResult.Type.ALIVE, "", "", player.getAge());
    }
}
