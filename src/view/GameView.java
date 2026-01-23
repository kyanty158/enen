package view;

import model.Choice;
import model.LifeEvent;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * ゲームのビュー（画面表示）を管理するクラス
 */
public class GameView extends JFrame {
    private final JLabel ageLabel = new JLabel();
    private final JLabel statsLabel = new JLabel();
    private final JTextArea eventArea = new JTextArea();
    private final JPanel buttonPanel = new JPanel();

    public GameView(ActionListener listener) {
        setTitle("Infinite Life Tunnel");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 上部パネル（年齢とステータス）
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        ageLabel.setFont(new Font("Meiryo", Font.BOLD, 24));
        ageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statsLabel.setFont(new Font("Meiryo", Font.PLAIN, 14));
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(ageLabel);
        topPanel.add(statsLabel);
        add(topPanel, BorderLayout.NORTH);

        // 中央パネル（イベントテキスト）
        eventArea.setEditable(false);
        eventArea.setLineWrap(true);
        eventArea.setWrapStyleWord(true);
        eventArea.setFont(new Font("Meiryo", Font.PLAIN, 16));
        eventArea.setMargin(new Insets(20, 20, 20, 20));
        add(new JScrollPane(eventArea), BorderLayout.CENTER);

        // 下部パネル（選択肢ボタン）
        buttonPanel.setLayout(new GridLayout(0, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * プレイヤー情報を画面に反映
     */
    public void updateDisplay(Player player) {
        ageLabel.setText("年齢: " + player.getAge() + "歳");
        statsLabel.setText(String.format("HP: %d | ストレス: %d | 所持金: %,d円",
                player.getHealth(), player.getStress(), player.getMoney()));
    }

    /**
     * イベントと選択肢を表示
     */
    public void showEvent(LifeEvent event, ActionListener listener) {
        eventArea.setText(event.getText());
        buttonPanel.removeAll();

        List<Choice> choices = event.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            JButton b = new JButton(choices.get(i).getText());
            b.setFont(new Font("Meiryo", Font.PLAIN, 14));
            b.setActionCommand(String.valueOf(i));
            b.addActionListener(listener);
            buttonPanel.add(b);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    /**
     * ゲームオーバー画面を表示
     */
    public void showGameOver(String title, String message, int age) {
        JOptionPane.showMessageDialog(
                this,
                message + "\n享年: " + age + "歳",
                title,
                JOptionPane.PLAIN_MESSAGE);
        System.exit(0);
    }
}
