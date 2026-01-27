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

        SaveManager.recordEnding(result.endingTitle);
        SaveManager.recordTitle(result.honorTitle);
        SaveManager.updateBest(player.getAge(), player.getMoney());

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

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        badgePanel.setBackground(BG_COLOR);
        badgePanel.add(createBadge("称号: " + result.honorTitle, new Color(52, 152, 219)));
        badgePanel.add(createBadge("難易度: " + result.difficulty.getLabel(), new Color(155, 89, 182)));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(nameLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(ageLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(badgePanel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(msgLabel);

        // --- SUMMARY PANEL ---
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(BG_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 10, 10));
        statsGrid.setBackground(BG_COLOR);

        GameStats stats = result.stats;
        statsGrid.add(createStatCard("選択回数", stats != null ? String.valueOf(stats.getTotalChoices()) : "-"));
        statsGrid.add(createStatCard("最大体力", stats != null ? String.valueOf(stats.getMaxHealth()) : "-"));
        statsGrid.add(createStatCard("最大ストレス", stats != null ? String.valueOf(stats.getMaxStress()) : "-"));
        statsGrid.add(createStatCard("最大所持金", stats != null ? String.format("%,d円", stats.getMaxMoney()) : "-"));
        statsGrid.add(createStatCard("最小所持金", stats != null ? String.format("%,d円", stats.getMinMoney()) : "-"));
        statsGrid.add(createStatCard("累計お金変動", stats != null ? String.format("%,d円", stats.getTotalMoneyChange()) : "-"));

        JPanel achievePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        achievePanel.setBackground(BG_COLOR);
        if (result.achievements == null || result.achievements.isEmpty()) {
            achievePanel.add(createChip("実績なし"));
        } else {
            for (Achievement ach : result.achievements) {
                achievePanel.add(createChip(ach.getLabel() + " - " + ach.getDescription()));
            }
        }

        JPanel summaryInner = new JPanel();
        summaryInner.setLayout(new BoxLayout(summaryInner, BoxLayout.Y_AXIS));
        summaryInner.setBackground(BG_COLOR);
        JLabel statsLabel = new JLabel("【統計】");
        statsLabel.setForeground(Color.GRAY);
        statsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel achieveLabel = new JLabel("【実績】");
        achieveLabel.setForeground(Color.GRAY);
        achieveLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        achievePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryInner.add(statsLabel);
        summaryInner.add(Box.createVerticalStrut(6));
        summaryInner.add(statsGrid);
        summaryInner.add(Box.createVerticalStrut(8));
        summaryInner.add(createBestPanel());
        summaryInner.add(Box.createVerticalStrut(10));
        summaryInner.add(achieveLabel);
        summaryInner.add(Box.createVerticalStrut(6));
        summaryInner.add(achievePanel);

        summaryPanel.add(summaryInner, BorderLayout.CENTER);

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

    private JLabel createBadge(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return label;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(35, 38, 45));
        card.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JLabel t = new JLabel(title);
        t.setForeground(Color.GRAY);
        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("SansSerif", Font.BOLD, 16));
        card.add(t);
        card.add(Box.createVerticalStrut(4));
        card.add(v);
        return card;
    }

    private JLabel createChip(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(new Color(52, 73, 94));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return label;
    }

    private JPanel createBestPanel() {
        SaveManager.BestRecord best = SaveManager.loadBest();
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 8));
        panel.setBackground(BG_COLOR);
        panel.add(createStatCard("最長寿", best.maxAge + "歳"));
        panel.add(createStatCard("最高資産", String.format("%,d円", best.maxMoney)));
        return panel;
    }
}
