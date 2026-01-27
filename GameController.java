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
        view.updateEconomy(model.getEconomy());
        currentEvent = model.nextEvent();
        view.showEvent(currentEvent, this, model::getEffectiveEffect);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("SAVE".equals(command)) {
            int slot = chooseSlot("セーブするスロットを選んでください");
            if (slot <= 0) {
                view.showSystemMessage("セーブをキャンセルしました。");
                return;
            }
            boolean ok = SaveManager.save(model.toSaveData(), slot);
            model.recordSaveLoad();
            view.showSystemMessage(ok ? "セーブしました。(スロット" + slot + ")" : "セーブに失敗しました。");
            return;
        }

        if ("LOAD".equals(command)) {
            int slot = chooseSlot("ロードするスロットを選んでください");
            if (slot <= 0) {
                view.showSystemMessage("ロードをキャンセルしました。");
                return;
            }
            SaveData data = SaveManager.load(slot);
            if (data == null) {
                view.showSystemMessage("ロードに失敗しました。");
                return;
            }
            model = new GameModel(data);
            model.recordSaveLoad();
            view.updateMeta(model.getPlayer(), model.getDifficulty());
            view.updateEconomy(model.getEconomy());
            nextTurn();
            view.showSystemMessage("ロードしました。(スロット" + slot + ")");
            return;
        }

        if ("HISTORY".equals(command)) {
            view.showHistory(model.getPlayer(), model.getStats(), model.getAchievements());
            return;
        }

        if ("STORY".equals(command)) {
            new StorybookView(model.getPlayer(), model.getEconomy());
            return;
        }

        if (command != null && command.startsWith("LOC_")) {
            Location loc = Location.valueOf(command.substring(4));
            model.getPlayer().setLocation(loc);
            view.updateDisplay(model.getPlayer());
            view.showSystemMessage("移動: " + loc.getLabel());
            return;
        }

        if ("LUCKY".equals(command)) {
            ChoiceEffect effect = randomLuckyEffect();

            int ageBefore = model.getPlayer().getAge();
            int healthBefore = model.getPlayer().getHealth();
            int stressBefore = model.getPlayer().getStress();
            long moneyBefore = model.getPlayer().getMoney();
            String statusBefore = model.getPlayer().getStatus();

            DeathResult result = model.applyCustomEffect("【運試し】運命のくじ引き", "くじを引く", effect);

            int ageAfter = model.getPlayer().getAge();
            int healthAfter = model.getPlayer().getHealth();
            int stressAfter = model.getPlayer().getStress();
            long moneyAfter = model.getPlayer().getMoney();
            String statusAfter = model.getPlayer().getStatus();

            view.updateDisplay(model.getPlayer());
            view.showChoiceResult(new ChoiceOutcome(effect,
                    ageBefore, ageAfter,
                    healthBefore, healthAfter,
                    stressBefore, stressAfter,
                    moneyBefore, moneyAfter,
                    statusBefore, statusAfter));

            if (result.type == DeathResult.Type.ALIVE) {
                nextTurn();
            } else {
                FinalResult finalResult = model.buildFinalResult(result, "運試し");
                view.showGameOver(finalResult, model.getPlayer(), () -> {
                    showTitleScreen();
                });
            }
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

        String tendencyBefore = computeTendency(model.getPlayer());
        String relationBefore = computeRelation(model.getPlayer());

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
        view.updateEconomy(model.getEconomy());
        view.showChoiceResult(new ChoiceOutcome(effect, ageBefore, ageAfter, healthBefore, healthAfter, stressBefore,
                stressAfter, moneyBefore, moneyAfter, statusBefore, statusAfter));
        String tendencyAfter = computeTendency(model.getPlayer());
        String relationAfter = computeRelation(model.getPlayer());
        view.showSystemMessage("傾向: " + tendencyBefore + "→" + tendencyAfter + " / 人間関係: " + relationBefore + "→"
                + relationAfter);

        if (result.type == DeathResult.Type.ALIVE) {
            nextTurn();
        } else {
            FinalResult finalResult = model.buildFinalResult(result, currentEvent.getTitle());
            view.showGameOver(finalResult, model.getPlayer(), () -> {
                showTitleScreen();
            });
        }
    }

    private ChoiceEffect randomLuckyEffect() {
        double dice = Math.random();
        if (dice < 0.2) {
            return new ChoiceEffect(10, -5, 50000, 1, null);
        }
        if (dice < 0.4) {
            return new ChoiceEffect(-10, 10, 0, 1, null);
        }
        if (dice < 0.6) {
            return new ChoiceEffect(0, -10, -30000, 1, null);
        }
        if (dice < 0.8) {
            return new ChoiceEffect(5, 5, 10000, 1, null);
        }
        return new ChoiceEffect(-5, -5, 100000, 1, null);
    }

    private int chooseSlot(String message) {
        Object[] options = { "1", "2", "3", "キャンセル" };
        int choice = JOptionPane.showOptionDialog(null, message, "スロット選択",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) {
            return -1;
        }
        return choice + 1;
    }

    private String computeTendency(Player player) {
        int health = player.countTag("health");
        int social = player.countTag("social");
        int invest = player.countTag("invest");
        int family = player.countTag("family");
        int max = Math.max(Math.max(health, social), Math.max(invest, family));
        if (max == 0) return "未定";
        if (max == health) return "健康派";
        if (max == social) return "社交派";
        if (max == invest) return "堅実派";
        return "家族派";
    }

    private String computeRelation(Player player) {
        int social = player.countTag("social");
        if (social >= 12) return "高";
        if (social >= 5) return "中";
        return "低";
    }
}
