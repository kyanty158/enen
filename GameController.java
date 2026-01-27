import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameController implements ActionListener {
    private GameModel model;
    private GameView view;
    private LifeEvent currentEvent;

    public GameController() {
        // タイトル画面を表示
        showTitleScreen();
    }

    private void showTitleScreen() {
        new TitleView(() -> {
            // ゲーム開始時の処理
            model = new GameModel();
            view = new GameView(this);
            nextTurn();
        });
    }

    private void nextTurn() {
        view.updateDisplay(model.getPlayer());
        currentEvent = model.nextEvent();
        view.showEvent(currentEvent, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = Integer.parseInt(e.getActionCommand());
        if (index < 0 || index >= currentEvent.getChoices().size())
            return;

        Choice selected = currentEvent.getChoices().get(index);
        DeathResult result = model.applyChoice(selected, currentEvent.getTitle());

        if (result.type == DeathResult.Type.ALIVE) {
            nextTurn();
        } else {
            view.updateDisplay(model.getPlayer());
            // Restart用のコールバックを渡す
            view.showGameOver(result.title, result.message, result.age, model.getPlayer(), () -> {
                showTitleScreen();
            });
        }
    }
}
