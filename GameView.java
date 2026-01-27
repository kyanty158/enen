import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

class GameView extends JFrame {
    private final JLabel ageLabel = new JLabel();
    private final JLabel moneyLabel = new JLabel();
    private final JLabel statusLabel = new JLabel(); // ステータス表示追加
    private final JProgressBar healthBar = new JProgressBar(0, 100);
    private final JProgressBar stressBar = new JProgressBar(0, 100);
    private final JTextArea eventArea = new JTextArea();
    private final JPanel buttonPanel = new JPanel();

    private final Color BG_COLOR = new Color(40, 44, 52);
    private final Color TEXT_COLOR = new Color(220, 223, 228);
    private final Color ACCENT_COLOR = new Color(97, 175, 239);

    public GameView(ActionListener listener) {
        setTitle("人生100年サバイバル (分岐強化版)");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel infoPanel = new JPanel(new GridLayout(2, 2)); // グリッド変更
        infoPanel.setBackground(BG_COLOR);

        ageLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        ageLabel.setForeground(ACCENT_COLOR);

        moneyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        moneyLabel.setForeground(Color.YELLOW);
        moneyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(Color.LIGHT_GRAY);

        infoPanel.add(ageLabel);
        infoPanel.add(moneyLabel);
        infoPanel.add(statusLabel); // ステータス追加
        infoPanel.add(new JLabel(""));

        topPanel.add(infoPanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(createBarPanel("体力", healthBar, new Color(46, 204, 113)));
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(createBarPanel("ストレス", stressBar, new Color(231, 76, 60)));

        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        eventArea.setEditable(false);
        eventArea.setLineWrap(true);
        eventArea.setWrapStyleWord(true);
        eventArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        eventArea.setBackground(new Color(60, 64, 72));
        eventArea.setForeground(TEXT_COLOR);
        eventArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        centerPanel.add(eventArea, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM PANEL ---
        buttonPanel.setLayout(new GridLayout(0, 1, 10, 10));
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createBarPanel(String labelText, JProgressBar bar, Color color) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(BG_COLOR);
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(60, 20));
        bar.setStringPainted(true);
        bar.setForeground(color);
        bar.setBackground(Color.DARK_GRAY);
        p.add(label, BorderLayout.WEST);
        p.add(bar, BorderLayout.CENTER);
        return p;
    }

    public void updateDisplay(Player player) {
        ageLabel.setText(player.getAge() + "歳");
        moneyLabel.setText(String.format("%,d円", player.getMoney()));
        statusLabel.setText("身分: " + player.getStatus());

        healthBar.setValue(player.getHealth());
        healthBar.setString(player.getHealth() + "/100");
        stressBar.setValue(player.getStress());
        stressBar.setString(player.getStress() + "/100");
    }

    public void showEvent(LifeEvent event, ActionListener listener) {
        eventArea.setText(event.getText());
        buttonPanel.removeAll();
        List<Choice> choices = event.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            JButton b = new JButton(choices.get(i).getText());
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.setFocusPainted(false);
            b.setBackground(new Color(240, 240, 240));
            b.setForeground(Color.BLACK);
            b.setPreferredSize(new Dimension(100, 45));
            b.setActionCommand(String.valueOf(i));
            b.addActionListener(listener);
            buttonPanel.add(b);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void showGameOver(String title, String message, int age, Player player, Runnable onRestart) {
        new ResultView(title, message, age, player, onRestart);
        dispose(); // ゲーム画面を閉じる
    }
}
