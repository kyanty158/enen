package controller;

import model.Choice;
import model.DeathResult;
import model.GameModel;
import model.LifeEvent;
import view.GameView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ゲームのコントローラー
 * ModelとViewを繋ぐ役割を担う
 */
public class GameController implements ActionListener {
    private final GameModel model;
    private final GameView view;
    private LifeEvent currentEvent;

    public GameController() {
        model = new GameModel();
        view = new GameView(this);
        nextTurn();
    }

    /**
     * 次のターンに進む
     */
    private void nextTurn() {
        view.updateDisplay(model.getPlayer());
        currentEvent = model.nextEvent();
        view.showEvent(currentEvent, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = Integer.parseInt(e.getActionCommand());

        // 範囲外チェック（念のため）
        if (index < 0 || index >= currentEvent.getChoices().size())
            return;

        Choice selected = currentEvent.getChoices().get(index);
        DeathResult result = model.applyChoice(selected);

        if (result.type == DeathResult.Type.ALIVE) {
            nextTurn();
        } else {
            view.updateDisplay(model.getPlayer());
            view.showGameOver(result.title, result.message, result.age);
        }
    }
}
