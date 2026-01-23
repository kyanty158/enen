import controller.GameController;

import javax.swing.*;

/**
 * アプリケーションのエントリーポイント
 */
public class Main {
    public static void main(String[] args) {
        // SwingアプリはEDT(Event Dispatch Thread)で起動するのが定石です
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}
