import javax.swing.*;
import java.awt.*;

class ResultView extends JFrame {
    private final Color BG_COLOR = new Color(25, 28, 35);
    private final Color TEXT_COLOR = new Color(220, 223, 228);
    private final Color ACCENT_COLOR = new Color(231, 76, 60); // 赤 (GameOver用)
    private final Color LEGEND_COLOR = new Color(241, 196, 15); // 金 (大往生用)

    public ResultView(String title, String message, int age, Player player, Runnable onRestart) {
        setTitle("結果発表");
        setSize(550, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        Color titleColor = ("伝説のエンド".equals(title) || "大往生".equals(title)) ? LEGEND_COLOR : ACCENT_COLOR;

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ageLabel = new JLabel("享年 " + age + " 歳");
        ageLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        ageLabel.setForeground(Color.WHITE);
        ageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel(
                "<html><div style='text-align: center; width: 400px;'>" + message + "</div></html>");
        msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        msgLabel.setForeground(Color.LIGHT_GRAY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(ageLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(msgLabel);

        // --- HISTORY LOG (CENTER) ---
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(BG_COLOR);
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JLabel logTitle = new JLabel("--- 人生の軌跡 ---");
        logTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        logTitle.setForeground(Color.GRAY);
        logTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(35, 38, 45));
        logArea.setForeground(TEXT_COLOR);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logArea.setMargin(new Insets(10, 10, 10, 10));

        StringBuilder sb = new StringBuilder();
        for (String log : player.getHistory()) {
            sb.append(log).append("\n\n");
        }
        logArea.setText(sb.toString());
        logArea.setCaretPosition(0); // 先頭を表示

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        historyPanel.add(logTitle, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER (BUTTONS) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton restartButton = new JButton("タイトルへ戻る");
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        restartButton.setForeground(Color.BLACK);
        restartButton.setBackground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setPreferredSize(new Dimension(200, 50));
        restartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        restartButton.addActionListener(e -> {
            dispose();
            onRestart.run();
        });

        buttonPanel.add(restartButton);

        add(headerPanel, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
