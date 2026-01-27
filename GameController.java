import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

class GameController implements ActionListener {
    private GameModel model;
    private GameView view;
    private LifeEvent currentEvent;

    public GameController() {
        showTitleScreen();
    }

    private void showTitleScreen() {
        new TitleView(
                config -> startNewGame(config),
                () -> loadGameFromTitle(),
                SaveManager.hasSave());
    }

    private void startNewGame(GameConfig config) {
        model = new GameModel(config);
        view = new GameView(this, config);
        nextTurn();
    }

    private void loadGameFromTitle() {
        SaveData data = SaveManager.load();
        if (data == null) {
            JOptionPane.showMessageDialog(null, "セーブデータが見つかりませんでした。");
            showTitleScreen();
            return;
        }
        model = new GameModel(data);
        view = new GameView(this, model.getConfig());
        nextTurn();
    }

    private void nextTurn() {
        view.updateDisplay(model.getPlayer());
        currentEvent = model.nextEvent();
        view.showEvent(currentEvent, this, model::getEffectiveEffect);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("SAVE".equals(command)) {
            boolean ok = SaveManager.save(model.toSaveData());
            view.showSystemMessage(ok ? "セーブしました。" : "セーブに失敗しました。");
            return;
        }

        if ("LOAD".equals(command)) {
            SaveData data = SaveManager.load();
            if (data == null) {
                view.showSystemMessage("ロードに失敗しました。");
                return;
            }
            model = new GameModel(data);
            view.updateMeta(model.getPlayer(), model.getDifficulty());
            nextTurn();
            view.showSystemMessage("ロードしました。");
            return;
        }

        if ("HISTORY".equals(command)) {
            view.showHistory(model.getPlayer(), model.getStats(), model.getAchievements());
            return;
        }

        int index;
        try {
            index = Integer.parseInt(command);
        } catch (NumberFormatException ex) {
            return;
        }
        if (index < 0 || index >= currentEvent.getChoices().size()) {
            return;
        }

        Choice selected = currentEvent.getChoices().get(index);
        ChoiceEffect effect = model.getEffectiveEffect(selected);

        int ageBefore = model.getPlayer().getAge();
        int healthBefore = model.getPlayer().getHealth();
        int stressBefore = model.getPlayer().getStress();
        long moneyBefore = model.getPlayer().getMoney();
        String statusBefore = model.getPlayer().getStatus();

        DeathResult result = model.applyChoice(selected, currentEvent.getTitle());

        int ageAfter = model.getPlayer().getAge();
        int healthAfter = model.getPlayer().getHealth();
        int stressAfter = model.getPlayer().getStress();
        long moneyAfter = model.getPlayer().getMoney();
        String statusAfter = model.getPlayer().getStatus();

        view.updateDisplay(model.getPlayer());
        view.showChoiceResult(new ChoiceOutcome(effect, ageBefore, ageAfter, healthBefore, healthAfter, stressBefore,
                stressAfter, moneyBefore, moneyAfter, statusBefore, statusAfter));

        if (result.type == DeathResult.Type.ALIVE) {
            nextTurn();
        } else {
            FinalResult finalResult = model.buildFinalResult(result, currentEvent.getTitle());
            view.showGameOver(finalResult, model.getPlayer(), () -> {
                showTitleScreen();
            });
        }
    }
}
