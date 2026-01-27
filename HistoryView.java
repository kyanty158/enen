import javax.swing.*;
import java.awt.*;
import java.util.Set;

class HistoryView extends JFrame {
    private final Color BG_COLOR = new Color(25, 28, 35);
    private final Color TEXT_COLOR = new Color(220, 223, 228);

    public HistoryView(Player player, GameStats stats, Set<Achievement> achievements) {
        setTitle("履歴ログ");
        setSize(600, 780);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_COLOR);
        tabs.setForeground(Color.WHITE);

        tabs.add("履歴", createHistoryPanel(player));
        tabs.add("年表", createTimelinePanel(player));
        tabs.add("成長曲線", createGrowthPanel(player));
        tabs.add("名簿", createRosterPanel(player));
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

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel statsLabel = new JLabel("【統計】");
        statsLabel.setForeground(Color.GRAY);
        content.add(statsLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(createStatsBox(stats));
        content.add(Box.createVerticalStrut(12));

        JLabel achieveLabel = new JLabel("【実績】");
        achieveLabel.setForeground(Color.GRAY);
        content.add(achieveLabel);
        content.add(Box.createVerticalStrut(6));

        if (achievements == null || achievements.isEmpty()) {
            content.add(createAchievementCard("実績なし", "まだ実績はありません。"));
        } else {
            for (Achievement ach : achievements) {
                content.add(createAchievementCard(ach.getLabel(), ach.getDescription()));
                content.add(Box.createVerticalStrut(6));
            }
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTimelinePanel(Player player) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filterPanel.setBackground(BG_COLOR);
        JLabel filterLabel = new JLabel("フィルタ:");
        filterLabel.setForeground(Color.LIGHT_GRAY);
        String[] options = new String[] { "すべて", "学生期", "社会人期", "老後", "恋愛系", "金運系" };
        JComboBox<String> filterBox = new JComboBox<>(options);
        filterBox.setSelectedIndex(0);
        filterPanel.add(filterLabel);
        filterPanel.add(filterBox);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_COLOR);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        java.util.List<TimelineEntry> entries = parseTimelineEntries(player);
        renderTimeline(listPanel, entries, "すべて");

        filterBox.addActionListener(e -> {
            String selected = (String) filterBox.getSelectedItem();
            renderTimeline(listPanel, entries, selected == null ? "すべて" : selected);
            listPanel.revalidate();
            listPanel.repaint();
        });

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGrowthPanel(Player player) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        java.util.List<StatPoint> points = player.getStatPoints();
        GrowthChart chart = new GrowthChart(points);
        chart.setBackground(new Color(30, 34, 44));
        chart.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        legend.setBackground(BG_COLOR);
        legend.add(createLegendDot("体力", new Color(46, 204, 113)));
        legend.add(createLegendDot("ストレス", new Color(231, 76, 60)));

        panel.add(legend, BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRosterPanel(Player player) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        java.util.List<Npc> npcs = player.getNpcs();
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(BG_COLOR);
        list.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        if (npcs.isEmpty()) {
            list.add(createNpcCard("未登録", "まだ出会いがありません。", 0));
        } else {
            for (Npc npc : npcs) {
                list.add(createNpcCard(npc.getName(), npc.getType() + " / " + npc.getTraitsText(), npc.getRelation()));
                list.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_COLOR);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createNpcCard(String name, String type, int relation) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(36, 42, 55));
        card.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel typeLabel = new JLabel(type);
        typeLabel.setForeground(Color.LIGHT_GRAY);
        typeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JProgressBar relationBar = new JProgressBar(0, 100);
        relationBar.setValue(Math.max(0, Math.min(100, relation)));
        relationBar.setStringPainted(false);
        relationBar.setForeground(new Color(52, 152, 219));
        relationBar.setBackground(new Color(60, 64, 72));

        JPanel text = new JPanel();
        text.setBackground(new Color(36, 42, 55));
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(nameLabel);
        text.add(Box.createVerticalStrut(4));
        text.add(typeLabel);
        text.add(Box.createVerticalStrut(6));
        text.add(relationBar);

        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JLabel createLegendDot(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        return label;
    }

    private java.util.List<TimelineEntry> parseTimelineEntries(Player player) {
        java.util.List<TimelineEntry> entries = new java.util.ArrayList<>();
        for (String log : player.getHistory()) {
            TimelineEntry entry = parseEntry(log);
            if (entry != null) {
                entries.add(entry);
            }
        }
        return entries;
    }

    private TimelineEntry parseEntry(String log) {
        if (log == null) {
            return null;
        }
        int ageEnd = log.indexOf("歳");
        int statusStart = log.indexOf("[");
        int statusEnd = log.indexOf("]");
        int marker = log.indexOf("]: ");
        if (ageEnd < 0 || statusStart < 0 || statusEnd < 0 || marker < 0) {
            return null;
        }
        int age = 0;
        try {
            age = Integer.parseInt(log.substring(0, ageEnd));
        } catch (NumberFormatException e) {
            age = 0;
        }
        int titleStart = marker + 3;
        int titleEnd = log.indexOf("\n", titleStart);
        String title = (titleEnd >= 0) ? log.substring(titleStart, titleEnd) : log.substring(titleStart);

        String choice = "";
        int choiceMarker = log.indexOf("↳");
        if (choiceMarker >= 0) {
            choice = log.substring(choiceMarker + 1).trim();
        }
        IconType iconType = detectIconType(choice, title);
        int intensity = detectIntensity(choice);
        return new TimelineEntry(age, title, choice, iconType, intensity);
    }

    private JPanel createTimelineCard(String ageText, String title, String choice, int age, IconType iconType, int intensity) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(36, 42, 55));
        card.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel ageBadge = new JPanel();
        ageBadge.setBackground(ageColor(age));
        ageBadge.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        JLabel ageLabel = new JLabel(ageText);
        ageLabel.setForeground(Color.WHITE);
        ageLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        ageBadge.add(ageLabel);

        JPanel textBox = new JPanel();
        textBox.setBackground(new Color(36, 42, 55));
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        String icon = iconSymbol(iconType);
        String choiceText = choice.isEmpty() ? "選択: -" : "選択: " + choice;
        JLabel choiceLabel = new JLabel(icon + " " + choiceText);
        choiceLabel.setForeground(Color.LIGHT_GRAY);
        choiceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        textBox.add(titleLabel);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(choiceLabel);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(createIntensityBar(intensity));

        card.add(ageBadge, BorderLayout.WEST);
        card.add(textBox, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDecadeHeader(int decade) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(28, 33, 43));
        header.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JLabel label = new JLabel(decade + "代");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.add(label, BorderLayout.WEST);
        return header;
    }

    private Color ageColor(int age) {
        if (age <= 12) {
            return new Color(46, 204, 113);
        }
        if (age <= 18) {
            return new Color(52, 152, 219);
        }
        if (age <= 29) {
            return new Color(155, 89, 182);
        }
        if (age <= 59) {
            return new Color(241, 196, 15);
        }
        return new Color(231, 76, 60);
    }

    private IconType detectIconType(String choice, String title) {
        String target = (choice == null ? "" : choice) + " " + (title == null ? "" : title);
        if (containsAny(target, "恋", "合コン", "告白", "誘", "同窓会")) {
            return IconType.LOVE;
        }
        if (containsAny(target, "稼", "投資", "貯金", "給料", "ボーナス", "宝くじ")) {
            return IconType.MONEY;
        }
        if (containsAny(target, "病", "徹夜", "夜逃げ", "無視", "放置", "不安")) {
            return IconType.DANGER;
        }
        if (containsAny(target, "寝", "休", "癒", "ジム", "散歩", "通う")) {
            return IconType.HEALTH;
        }
        return IconType.NEUTRAL;
    }

    private int detectIntensity(String choice) {
        if (choice == null) {
            return 0;
        }
        if (containsAny(choice, "全力", "徹夜", "夜逃げ", "土下座", "限界", "一気")) {
            return 3;
        }
        if (containsAny(choice, "頑張", "本気", "挑戦", "奮発", "栄転")) {
            return 2;
        }
        return 1;
    }

    private String iconSymbol(IconType type) {
        switch (type) {
            case LOVE:
                return "❤";
            case MONEY:
                return "💰";
            case HEALTH:
                return "↑";
            case DANGER:
                return "↓";
            default:
                return "・";
        }
    }

    private JComponent createIntensityBar(int intensity) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        bar.setBackground(new Color(36, 42, 55));
        int count = Math.max(0, Math.min(3, intensity));
        for (int i = 0; i < 3; i++) {
            JLabel dot = new JLabel("●");
            dot.setFont(new Font("SansSerif", Font.BOLD, 8));
            if (i < count) {
                dot.setForeground(new Color(241, 196, 15));
            } else {
                dot.setForeground(new Color(70, 75, 90));
            }
            bar.add(dot);
        }
        return bar;
    }

    private boolean containsAny(String text, String... keys) {
        for (String k : keys) {
            if (text.contains(k)) {
                return true;
            }
        }
        return false;
    }

    private void renderTimeline(JPanel listPanel, java.util.List<TimelineEntry> entries, String filter) {
        listPanel.removeAll();
        java.util.List<TimelineEntry> filtered = new java.util.ArrayList<>();
        for (TimelineEntry entry : entries) {
            if (matchesFilter(entry, filter)) {
                filtered.add(entry);
            }
        }
        if (filtered.isEmpty()) {
            JPanel empty = createTimelineCard("未記録", "該当する年表はありません。", "", 0, IconType.NEUTRAL, 0);
            listPanel.add(empty);
            return;
        }

        int currentDecade = -1;
        for (TimelineEntry entry : filtered) {
            int decade = (entry.age / 10) * 10;
            if (decade != currentDecade) {
                currentDecade = decade;
                listPanel.add(createDecadeHeader(decade));
                listPanel.add(Box.createVerticalStrut(6));
            }
            listPanel.add(createTimelineCard(entry.age + "歳", entry.title, entry.choice, entry.age, entry.iconType, entry.intensity));
            listPanel.add(Box.createVerticalStrut(8));
        }
    }

    private boolean matchesFilter(TimelineEntry entry, String filter) {
        if ("すべて".equals(filter)) {
            return true;
        }
        if ("学生期".equals(filter)) {
            return entry.age <= 22;
        }
        if ("社会人期".equals(filter)) {
            return entry.age >= 23 && entry.age <= 59;
        }
        if ("老後".equals(filter)) {
            return entry.age >= 60;
        }
        if ("恋愛系".equals(filter)) {
            return entry.iconType == IconType.LOVE;
        }
        if ("金運系".equals(filter)) {
            return entry.iconType == IconType.MONEY;
        }
        return true;
    }

    private enum IconType {
        NEUTRAL, LOVE, MONEY, HEALTH, DANGER
    }

    private static class TimelineEntry {
        final int age;
        final String title;
        final String choice;
        final IconType iconType;
        final int intensity;

        TimelineEntry(int age, String title, String choice, IconType iconType, int intensity) {
            this.age = age;
            this.title = title;
            this.choice = choice;
            this.iconType = iconType;
            this.intensity = intensity;
        }
    }

    private JPanel createStatsBox(GameStats stats) {
        JPanel box = new JPanel(new GridLayout(0, 2, 10, 8));
        box.setBackground(new Color(35, 38, 45));
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        box.add(createStatCell("選択回数", stats != null ? String.valueOf(stats.getTotalChoices()) : "-"));
        box.add(createStatCell("最大体力", stats != null ? String.valueOf(stats.getMaxHealth()) : "-"));
        box.add(createStatCell("最大ストレス", stats != null ? String.valueOf(stats.getMaxStress()) : "-"));
        box.add(createStatCell("最大所持金", stats != null ? String.format("%,d円", stats.getMaxMoney()) : "-"));
        box.add(createStatCell("最小所持金", stats != null ? String.format("%,d円", stats.getMinMoney()) : "-"));
        box.add(createStatCell("累計お金変動", stats != null ? String.format("%,d円", stats.getTotalMoneyChange()) : "-"));
        return box;
    }

    private JPanel createStatCell(String title, String value) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBackground(new Color(35, 38, 45));
        JLabel t = new JLabel(title);
        t.setForeground(Color.GRAY);
        t.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("SansSerif", Font.BOLD, 14));
        cell.add(t);
        cell.add(Box.createVerticalStrut(3));
        cell.add(v);
        return cell;
    }

    private JPanel createAchievementCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(36, 42, 55));
        card.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setBackground(new Color(36, 42, 55));
        textBox.add(titleLabel);
        textBox.add(Box.createVerticalStrut(3));
        textBox.add(descLabel);

        card.add(textBox, BorderLayout.CENTER);
        return card;
    }

    private static class GrowthChart extends JPanel {
        private final java.util.List<StatPoint> points;

        GrowthChart(java.util.List<StatPoint> points) {
            this.points = points;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(50, 55, 70));
            g2.drawRect(30, 20, w - 50, h - 40);

            if (points == null || points.size() < 2) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString("データがありません", 40, 40);
                return;
            }

            int minAge = points.get(0).age;
            int maxAge = points.get(points.size() - 1).age;
            if (maxAge == minAge) {
                maxAge = minAge + 1;
            }

            drawLine(g2, points, minAge, maxAge, w, h, true, new Color(46, 204, 113));
            drawLine(g2, points, minAge, maxAge, w, h, false, new Color(231, 76, 60));
        }

        private void drawLine(Graphics2D g2, java.util.List<StatPoint> points, int minAge, int maxAge,
                              int w, int h, boolean health, Color color) {
            g2.setColor(color);
            int prevX = -1;
            int prevY = -1;
            for (StatPoint p : points) {
                int x = 30 + (int) ((p.age - minAge) * 1.0 / (maxAge - minAge) * (w - 50));
                int val = health ? p.health : p.stress;
                int y = 20 + (int) ((100 - val) / 100.0 * (h - 40));
                if (prevX != -1) {
                    g2.drawLine(prevX, prevY, x, y);
                }
                prevX = x;
                prevY = y;
            }
        }
    }
}
