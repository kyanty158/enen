import javax.swing.*;
import java.awt.*;
import java.util.Set;

class HistoryView extends JFrame {
    private final Color BG_COLOR = new Color(25, 28, 35);
    private final Color TEXT_COLOR = new Color(220, 223, 228);

    public HistoryView(Player player, GameStats stats, Set<Achievement> achievements) {
        setTitle("履歴ログ");
        setSize(520, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_COLOR);
        tabs.setForeground(Color.WHITE);

        tabs.add("履歴", createHistoryPanel(player));
        tabs.add("統計/実績", createStatsPanel(stats, achievements));

        add(tabs, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHistoryPanel(Player player) {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(BG_COLOR);
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

        historyPanel.add(scrollPane, BorderLayout.CENTER);
        return historyPanel;
    }

    private JPanel createStatsPanel(GameStats stats, Set<Achievement> achievements) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(new Color(35, 38, 45));
        area.setForeground(TEXT_COLOR);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setMargin(new Insets(10, 10, 10, 10));

        StringBuilder sb = new StringBuilder();
        sb.append("【統計】\n");
        if (stats != null) {
            sb.append(String.format("選択回数: %d\n", stats.getTotalChoices()));
            sb.append(String.format("最大体力: %d / 最小体力: %d\n", stats.getMaxHealth(), stats.getMinHealth()));
            sb.append(String.format("最大ストレス: %d / 最小ストレス: %d\n", stats.getMaxStress(), stats.getMinStress()));
            sb.append(String.format("最大所持金: %,d円\n", stats.getMaxMoney()));
            sb.append(String.format("最小所持金: %,d円\n", stats.getMinMoney()));
            sb.append(String.format("累計お金変動: %,d円\n", stats.getTotalMoneyChange()));
        }
        sb.append("\n【実績】\n");
        if (achievements == null || achievements.isEmpty()) {
            sb.append("まだ実績はありません。\n");
        } else {
            for (Achievement ach : achievements) {
                sb.append(String.format("・%s (%s)\n", ach.getLabel(), ach.getDescription()));
            }
        }

        area.setText(sb.toString());
        area.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}
