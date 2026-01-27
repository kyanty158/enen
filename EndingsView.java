import javax.swing.*;
import java.awt.*;
import java.util.List;

class EndingsView extends JFrame {
    private final Color BG_COLOR = new Color(20, 24, 32);
    private final Color PANEL_COLOR = new Color(28, 33, 43);
    private final Color CARD_COLOR = new Color(36, 42, 55);
    private final Color TEXT_COLOR = new Color(230, 232, 236);
    private final Color MUTED_COLOR = new Color(150, 155, 170);

    public EndingsView() {
        setTitle("図鑑");
        setSize(520, 640);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(18, 18, 10, 18));

        JLabel title = new JLabel("図鑑");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(TEXT_COLOR);

        JLabel sub = new JLabel("エンド / 称号 コレクション");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED_COLOR);

        JPanel titleBox = new JPanel();
        titleBox.setBackground(BG_COLOR);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(sub);

        header.add(titleBox, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_COLOR);
        tabs.setForeground(Color.WHITE);
        tabs.add("エンド", createCatalogPanel(getAllEndings(), SaveManager.loadEndings(),
                "まだエンドがありません。", SaveManager.getEndingsLocation(), CatalogType.ENDING));
        tabs.add("称号", createCatalogPanel(getAllTitles(), SaveManager.loadTitles(),
                "まだ称号がありません。", SaveManager.getTitlesLocation(), CatalogType.TITLE));

        add(tabs, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createCatalogPanel(List<String> allItems, List<String> unlockedItems, String emptyText,
                                      String location, CatalogType type) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(BG_COLOR);

        java.util.Set<String> unlocked = new java.util.HashSet<>();
        if (unlockedItems != null) {
            unlocked.addAll(unlockedItems);
        }

        int unlockedCount = 0;
        if (allItems == null || allItems.isEmpty()) {
            JPanel empty = createCardPanel("未解放", emptyText, "条件を満たすと解放されます。",
                    new Color(52, 73, 94), new IconSpec(new Color(80, 85, 100), IconShape.CIRCLE));
            grid.add(empty);
        } else {
            for (String item : allItems) {
                boolean isUnlocked = unlocked.contains(item);
                if (isUnlocked) {
                    unlockedCount++;
                }
                String title = isUnlocked ? item : "？？？";
                String status = isUnlocked ? "獲得済み" : "未解放";
                String desc = isUnlocked
                        ? (type == CatalogType.ENDING ? getEndingDescription(item) : getTitleDescription(item))
                        : "条件を満たすと解放されます。";
                Color badge = isUnlocked ? new Color(46, 204, 113) : new Color(52, 73, 94);
                IconSpec spec = getIconSpec(item, type);
                if (!isUnlocked) {
                    spec = new IconSpec(new Color(80, 85, 100), spec.shape);
                }
                JPanel card = createCardPanel(title, status, desc, badge, spec);
                grid.add(card);
                grid.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_COLOR);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_COLOR);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JLabel path = new JLabel("保存先: " + location);
        path.setForeground(MUTED_COLOR);
        path.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.add(path, BorderLayout.WEST);

        root.add(createProgressBar(unlockedCount, allItems == null ? 0 : allItems.size()), BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private JPanel createCardPanel(String title, String status, String description, Color badgeColor, IconSpec iconSpec) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        IconBadge icon = new IconBadge(iconSpec);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel statusLabel = new JLabel(status);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(badgeColor);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JLabel descLabel = new JLabel("<html><span style='color:#b0b6c8; font-size:11px;'>" + escapeHtml(description) + "</span></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel left = new JPanel(new BorderLayout(10, 0));
        left.setBackground(CARD_COLOR);
        JPanel textBox = new JPanel();
        textBox.setBackground(CARD_COLOR);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.add(titleLabel);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(descLabel);
        left.add(icon, BorderLayout.WEST);
        left.add(textBox, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setBackground(CARD_COLOR);
        right.add(statusLabel);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JComponent createProgressBar(int unlocked, int total) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String text = total == 0 ? "0 / 0" : unlocked + " / " + total;
        JLabel label = new JLabel("コンプ率: " + text);
        label.setForeground(MUTED_COLOR);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JProgressBar bar = new JProgressBar(0, Math.max(1, total));
        bar.setValue(unlocked);
        bar.setStringPainted(true);
        bar.setString(text);
        bar.setForeground(new Color(52, 152, 219));
        bar.setBackground(new Color(40, 45, 55));

        panel.add(label, BorderLayout.WEST);
        panel.add(bar, BorderLayout.SOUTH);
        return panel;
    }

    private List<String> getAllEndings() {
        return java.util.List.of(
                "伝説のエンド",
                "黄金の大往生",
                "大往生",
                "借金地獄エンド",
                "起業成功エンド",
                "ストレス死",
                "病死・衰弱死",
                "セレブエンド",
                "無職エンド",
                "社畜エンド"
        );
    }

    private List<String> getAllTitles() {
        java.util.List<String> titles = new java.util.ArrayList<>();
        for (Achievement ach : Achievement.values()) {
            titles.add(ach.getLabel());
        }
        titles.add("伝説級");
        titles.add("億万長者");
        titles.add("借金王");
        titles.add("起業家");
        titles.add("長寿の達人");
        titles.add("無職マスター");
        titles.add("社交界の華");
        titles.add("健康番長");
        titles.add("逆境の生還者");
        return titles;
    }

    private String getEndingDescription(String title) {
        if ("伝説のエンド".equals(title)) return "巨万の富と長寿を達成。";
        if ("黄金の大往生".equals(title)) return "豊かな老後を迎えた。";
        if ("大往生".equals(title)) return "100歳まで生き抜く。";
        if ("借金地獄エンド".equals(title)) return "借金が限界を超えた。";
        if ("起業成功エンド".equals(title)) return "社長として成功。";
        if ("ストレス死".equals(title)) return "ストレスが限界に達した。";
        if ("病死・衰弱死".equals(title)) return "体力が尽きた。";
        if ("セレブエンド".equals(title)) return "資産が1億円を超えた。";
        if ("無職エンド".equals(title)) return "無職のまま中年期を迎えた。";
        if ("社畜エンド".equals(title)) return "過労気味で終焉。";
        return "条件達成で解放。";
    }

    private String getTitleDescription(String title) {
        for (Achievement ach : Achievement.values()) {
            if (ach.getLabel().equals(title)) {
                return ach.getDescription();
            }
        }
        if ("伝説級".equals(title)) return "百寿と大富豪を両立。";
        if ("億万長者".equals(title)) return "所持金が十分に増える。";
        if ("借金王".equals(title)) return "借金が大きく膨らむ。";
        if ("起業家".equals(title)) return "社長ルート達成。";
        if ("長寿の達人".equals(title)) return "80歳以上に到達。";
        if ("無職マスター".equals(title)) return "ニート・無職ルート。";
        if ("社交界の華".equals(title)) return "社交系の実績を獲得。";
        if ("健康番長".equals(title)) return "健康系の実績を獲得。";
        if ("逆境の生還者".equals(title)) return "借金イベントを乗り越える。";
        return "条件達成で解放。";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private IconSpec getIconSpec(String title, CatalogType type) {
        if (type == CatalogType.ENDING) {
            if ("伝説のエンド".equals(title)) return new IconSpec(new Color(241, 196, 15), IconShape.DIAMOND);
            if ("黄金の大往生".equals(title)) return new IconSpec(new Color(241, 196, 15), IconShape.CIRCLE);
            if ("大往生".equals(title)) return new IconSpec(new Color(46, 204, 113), IconShape.CIRCLE);
            if ("借金地獄エンド".equals(title)) return new IconSpec(new Color(231, 76, 60), IconShape.TRIANGLE);
            if ("起業成功エンド".equals(title)) return new IconSpec(new Color(52, 152, 219), IconShape.DIAMOND);
            if ("ストレス死".equals(title)) return new IconSpec(new Color(231, 76, 60), IconShape.SQUARE);
            if ("病死・衰弱死".equals(title)) return new IconSpec(new Color(149, 165, 166), IconShape.SQUARE);
            if ("セレブエンド".equals(title)) return new IconSpec(new Color(155, 89, 182), IconShape.DIAMOND);
            if ("無職エンド".equals(title)) return new IconSpec(new Color(127, 140, 141), IconShape.CIRCLE);
            if ("社畜エンド".equals(title)) return new IconSpec(new Color(192, 57, 43), IconShape.SQUARE);
            return new IconSpec(new Color(120, 130, 150), IconShape.CIRCLE);
        }
        if ("伝説級".equals(title)) return new IconSpec(new Color(241, 196, 15), IconShape.DIAMOND);
        if ("億万長者".equals(title)) return new IconSpec(new Color(241, 196, 15), IconShape.CIRCLE);
        if ("借金王".equals(title)) return new IconSpec(new Color(231, 76, 60), IconShape.TRIANGLE);
        if ("起業家".equals(title)) return new IconSpec(new Color(52, 152, 219), IconShape.DIAMOND);
        if ("長寿の達人".equals(title)) return new IconSpec(new Color(46, 204, 113), IconShape.CIRCLE);
        if ("無職マスター".equals(title)) return new IconSpec(new Color(127, 140, 141), IconShape.CIRCLE);
        if ("社交界の華".equals(title)) return new IconSpec(new Color(155, 89, 182), IconShape.DIAMOND);
        if ("健康番長".equals(title)) return new IconSpec(new Color(46, 204, 113), IconShape.CIRCLE);
        if ("逆境の生還者".equals(title)) return new IconSpec(new Color(231, 76, 60), IconShape.SQUARE);
        return new IconSpec(new Color(120, 130, 150), IconShape.CIRCLE);
    }

    private enum IconShape {
        CIRCLE, SQUARE, DIAMOND, TRIANGLE
    }

    private static class IconSpec {
        final Color color;
        final IconShape shape;

        IconSpec(Color color, IconShape shape) {
            this.color = color;
            this.shape = shape;
        }
    }

    private static class IconBadge extends JComponent {
        private final IconSpec spec;

        IconBadge(IconSpec spec) {
            this.spec = spec;
            setPreferredSize(new Dimension(28, 28));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(spec.color);
            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 4;
            int x = (w - size) / 2;
            int y = (h - size) / 2;
            switch (spec.shape) {
                case SQUARE:
                    g2.fillRect(x, y, size, size);
                    break;
                case DIAMOND:
                    Polygon diamond = new Polygon();
                    diamond.addPoint(w / 2, y);
                    diamond.addPoint(x + size, h / 2);
                    diamond.addPoint(w / 2, y + size);
                    diamond.addPoint(x, h / 2);
                    g2.fillPolygon(diamond);
                    break;
                case TRIANGLE:
                    Polygon tri = new Polygon();
                    tri.addPoint(w / 2, y);
                    tri.addPoint(x + size, y + size);
                    tri.addPoint(x, y + size);
                    g2.fillPolygon(tri);
                    break;
                default:
                    g2.fillOval(x, y, size, size);
                    break;
            }
        }
    }

    private enum CatalogType {
        ENDING, TITLE
    }
}
