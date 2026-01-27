import javax.swing.*;
import java.awt.*;

class ResultView extends JFrame {
    private final Color BG_COLOR = new Color(25, 28, 35);
    private final Color TEXT_COLOR = new Color(220, 223, 228);
    private final Color ACCENT_COLOR = new Color(231, 76, 60); // 赤 (GameOver用)
    private final Color LEGEND_COLOR = new Color(241, 196, 15); // 金 (大往生用)

    public ResultView(FinalResult result, Player player, Runnable onRestart) {
        setTitle("結果発表");
        setSize(580, 820);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        Color titleColor = ("伝説のエンド".equals(result.endingTitle) || "大往生".equals(result.endingTitle)
                || "黄金の大往生".equals(result.endingTitle)) ? LEGEND_COLOR : ACCENT_COLOR;

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 10, 20));

        JLabel titleLabel = new JLabel(result.endingTitle);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 34));
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("プレイヤー: " + player.getName());
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ageLabel = new JLabel("享年 " + player.getAge() + " 歳");
        ageLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        ageLabel.setForeground(Color.WHITE);
        ageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel(
                "<html><div style='text-align: center; width: 420px;'>" + result.endingMessage
                        + "</div></html>");
        msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        msgLabel.setForeground(Color.LIGHT_GRAY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(nameLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(ageLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(msgLabel);

        // --- SUMMARY PANEL ---
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(BG_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setBackground(new Color(35, 38, 45));
        summaryArea.setForeground(TEXT_COLOR);
        summaryArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        summaryArea.setMargin(new Insets(10, 10, 10, 10));

        StringBuilder summary = new StringBuilder();
        summary.append("【称号】").append(result.honorTitle).append("\n");
        summary.append("【難易度】").append(result.difficulty.getLabel()).append("\n");
        summary.append("\n【実績】\n");
        if (result.achievements == null || result.achievements.isEmpty()) {
            summary.append("なし\n");
        } else {
            for (Achievement ach : result.achievements) {
                summary.append("・").append(ach.getLabel())
                        .append(" (" + ach.getDescription() + ")\n");
            }
        }
        summary.append("\n【統計】\n");
        GameStats stats = result.stats;
        if (stats != null) {
            summary.append(String.format("選択回数: %d\n", stats.getTotalChoices()));
            summary.append(String.format("最大体力: %d / 最小体力: %d\n", stats.getMaxHealth(), stats.getMinHealth()));
            summary.append(String.format("最大ストレス: %d / 最小ストレス: %d\n", stats.getMaxStress(), stats.getMinStress()));
            summary.append(String.format("最大所持金: %,d円\n", stats.getMaxMoney()));
            summary.append(String.format("最小所持金: %,d円\n", stats.getMinMoney()));
            summary.append(String.format("累計お金変動: %,d円\n", stats.getTotalMoneyChange()));
        }
        summaryArea.setText(summary.toString());
        summaryArea.setCaretPosition(0);

        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        summaryScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        summaryScroll.getVerticalScrollBar().setUnitIncrement(16);
        summaryScroll.setPreferredSize(new Dimension(520, 220));

        summaryPanel.add(summaryScroll, BorderLayout.CENTER);

        // --- HISTORY LOG (CENTER) ---
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(BG_COLOR);
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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
        logArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        historyPanel.add(logTitle, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER (BUTTONS) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

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

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(historyPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
