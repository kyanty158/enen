import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

class GameView extends JFrame {
    private final JLabel ageLabel = new JLabel();
    private final JLabel moneyLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel difficultyLabel = new JLabel();
    private final JLabel tendencyLabel = new JLabel();
    private final JLabel relationLabel = new JLabel();
    private final JLabel moodLabel = new JLabel();
    private final JLabel economyLabel = new JLabel();
    private final JComboBox<String> locationBox = new JComboBox<>(new String[] { "家", "学校", "職場", "街" });
    private final JProgressBar healthBar = new JProgressBar(0, 100);
    private final JProgressBar stressBar = new JProgressBar(0, 100);
    private final JTextArea eventArea = new JTextArea();
    private final JTextArea resultArea = new JTextArea();
    private final JPanel buttonPanel = new JPanel();
    private final JPanel actionPanel = new JPanel();
    private Border defaultEventBorder;

    private final Color BG_COLOR = new Color(40, 44, 52);
    private final Color TEXT_COLOR = new Color(220, 223, 228);
    private final Color ACCENT_COLOR = new Color(97, 175, 239);
    private final Color POSITIVE_COLOR = new Color(46, 204, 113);
    private final Color NEGATIVE_COLOR = new Color(231, 76, 60);

    private Difficulty difficulty;

    public GameView(ActionListener listener, GameConfig config) {
        setTitle("人生100年サバイバル (分岐強化版)");
        setSize(520, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        this.difficulty = config.getDifficulty();

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 15, 10, 15));

        JPanel infoPanel = new JPanel(new GridLayout(6, 2));
        infoPanel.setBackground(BG_COLOR);

        ageLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        ageLabel.setForeground(ACCENT_COLOR);

        moneyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        moneyLabel.setForeground(Color.YELLOW);
        moneyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(Color.LIGHT_GRAY);

        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameLabel.setForeground(Color.LIGHT_GRAY);

        difficultyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        difficultyLabel.setForeground(Color.LIGHT_GRAY);

        tendencyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tendencyLabel.setForeground(Color.LIGHT_GRAY);

        relationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        relationLabel.setForeground(Color.LIGHT_GRAY);

        moodLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        moodLabel.setForeground(Color.LIGHT_GRAY);

        economyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        economyLabel.setForeground(Color.LIGHT_GRAY);

        infoPanel.add(ageLabel);
        infoPanel.add(moneyLabel);
        infoPanel.add(statusLabel);
        infoPanel.add(new JLabel(""));
        infoPanel.add(nameLabel);
        infoPanel.add(difficultyLabel);
        infoPanel.add(tendencyLabel);
        infoPanel.add(relationLabel);
        infoPanel.add(new JLabel(""));
        infoPanel.add(moodLabel);
        infoPanel.add(new JLabel(""));
        infoPanel.add(economyLabel);

        topPanel.add(infoPanel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(createBarPanel("体力", healthBar, POSITIVE_COLOR));
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(createBarPanel("ストレス", stressBar, NEGATIVE_COLOR));

        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultArea.setBackground(new Color(50, 54, 62));
        resultArea.setForeground(TEXT_COLOR);
        resultArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 90)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        resultArea.setText("結果表示: 選択肢を選ぶと変化が表示されます。");

        eventArea.setEditable(false);
        eventArea.setLineWrap(true);
        eventArea.setWrapStyleWord(true);
        eventArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        eventArea.setBackground(new Color(60, 64, 72));
        eventArea.setForeground(TEXT_COLOR);
        defaultEventBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15));
        eventArea.setBorder(defaultEventBorder);

        centerPanel.add(resultArea, BorderLayout.NORTH);
        centerPanel.add(eventArea, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM PANEL ---
        actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setBackground(BG_COLOR);
        initActionButtons(listener);

        buttonPanel.setLayout(new GridLayout(0, 1, 10, 10));
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(BG_COLOR);
        southPanel.add(actionPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        String name = (config.getPlayerName() == null || config.getPlayerName().isBlank()) ? "プレイヤー"
                : config.getPlayerName();
        nameLabel.setText("名前: " + name);
        difficultyLabel.setText("難易度: " + config.getDifficulty().getLabel());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initActionButtons(ActionListener listener) {
        JButton saveButton = createActionButton("セーブ", "SAVE", listener);
        JButton loadButton = createActionButton("ロード", "LOAD", listener);
        JButton historyButton = createActionButton("履歴", "HISTORY", listener);
        JButton luckyButton = createActionButton("運試し", "LUCKY", listener);
        JButton storyButton = createActionButton("物語", "STORY", listener);

        actionPanel.add(saveButton);
        actionPanel.add(loadButton);
        actionPanel.add(historyButton);
        actionPanel.add(luckyButton);
        actionPanel.add(storyButton);

        locationBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        locationBox.setBackground(new Color(230, 230, 230));
        locationBox.addActionListener(e -> {
            String label = (String) locationBox.getSelectedItem();
            Location loc = Location.fromLabel(label);
            ActionEvent ev = new ActionEvent(locationBox, ActionEvent.ACTION_PERFORMED, "LOC_" + loc.name());
            listener.actionPerformed(ev);
        });
        actionPanel.add(locationBox);
    }

    private JButton createActionButton(String text, String command, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBackground(new Color(230, 230, 230));
        button.setForeground(Color.BLACK);
        button.setActionCommand(command);
        button.addActionListener(listener);
        return button;
    }

    private JPanel createBarPanel(String labelText, JProgressBar bar, Color color) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(BG_COLOR);
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(60, 20));
        bar.setStringPainted(false);
        bar.setForeground(color);
        bar.setBackground(Color.DARK_GRAY);
        p.add(label, BorderLayout.WEST);
        p.add(bar, BorderLayout.CENTER);
        return p;
    }

    public void updateMeta(Player player, Difficulty difficulty) {
        this.difficulty = (difficulty != null) ? difficulty : Difficulty.NORMAL;
        nameLabel.setText("名前: " + player.getName());
        difficultyLabel.setText("難易度: " + this.difficulty.getLabel());
    }

    public void updateDisplay(Player player) {
        ageLabel.setText(player.getAge() + "歳");
        moneyLabel.setText(String.format("%,d円", player.getMoney()));
        statusLabel.setText("身分: " + player.getStatus());

        healthBar.setValue(player.getHealth());
        stressBar.setValue(player.getStress());

        String tendency = computeTendency(player);
        String relation = computeRelation(player);
        tendencyLabel.setText("傾向: " + tendency);
        relationLabel.setText("人間関係: " + relation);
        if (player.getLocation() != null) {
            locationBox.setSelectedItem(player.getLocation().getLabel());
        }
    }

    public void showEvent(LifeEvent event, ActionListener listener, Function<Choice, ChoiceEffect> effectMapper) {
        eventArea.setText(event.getText());
        applyEventTheme(event.getTitle());
        buttonPanel.removeAll();
        List<Choice> choices = event.getChoices();
        java.util.List<Integer> order = new java.util.ArrayList<>();
        for (int i = 0; i < choices.size(); i++) {
            order.add(i);
        }
        java.util.Collections.shuffle(order);
        for (int idx = 0; idx < order.size(); idx++) {
            int i = order.get(idx);
            Choice choice = choices.get(i);
            ChoiceEffect effect = effectMapper != null ? effectMapper.apply(choice)
                    : new ChoiceEffect(choice.healthDelta, choice.stressDelta, choice.moneyDelta, choice.ageDelta,
                            choice.nextStatus);

            JButton b = new JButton(formatChoiceText(choice, effect));
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.setFocusPainted(false);
            if (isBranchEvent(event.getTitle())) {
                b.setBackground(new Color(255, 236, 179));
            } else {
                b.setBackground(new Color(240, 240, 240));
            }
            b.setForeground(Color.BLACK);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setPreferredSize(new Dimension(100, 50));
            b.setActionCommand(String.valueOf(i));
            b.addActionListener(listener);
            buttonPanel.add(b);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void showChoiceResult(ChoiceOutcome outcome) {
        StringBuilder sb = new StringBuilder();
        sb.append("結果: ");
        sb.append(formatDelta("体力", outcome.effect.healthDelta));
        sb.append(" / ");
        sb.append(formatDelta("ストレス", outcome.effect.stressDelta));
        sb.append(" / ");
        sb.append(formatDelta("お金", outcome.effect.moneyDelta));
        sb.append(" / ");
        sb.append(formatDelta("年齢", outcome.effect.ageDelta));
        if (outcome.statusBefore != null && !outcome.statusBefore.equals(outcome.statusAfter)) {
            sb.append(" / 身分: ").append(outcome.statusBefore).append("→").append(outcome.statusAfter);
        }
        resultArea.setText(sb.toString());

        if (outcome.effect.healthDelta != 0) {
            flashBar(healthBar, outcome.effect.healthDelta > 0 ? POSITIVE_COLOR : NEGATIVE_COLOR);
        }
        if (outcome.effect.stressDelta != 0) {
            flashBar(stressBar, outcome.effect.stressDelta > 0 ? NEGATIVE_COLOR : POSITIVE_COLOR);
        }
        if (outcome.effect.moneyDelta != 0) {
            flashLabel(moneyLabel, outcome.effect.moneyDelta > 0 ? POSITIVE_COLOR : NEGATIVE_COLOR);
        }
    }

    public void showGameOver(FinalResult result, Player player, Runnable onRestart) {
        new ResultView(result, player, onRestart);
        dispose();
    }

    public void showSystemMessage(String message) {
        resultArea.setText(message);
        flashLabel(moneyLabel, ACCENT_COLOR);
    }

    public void showHistory(Player player, GameStats stats, Set<Achievement> achievements) {
        new HistoryView(player, stats, achievements);
    }

    public void updateEconomy(Economy economy) {
        if (economy == null) {
            economyLabel.setText("景気: -");
            return;
        }
        economyLabel.setText("景気: " + economy.getLabel() + " / " + economy.getInflationLabel());
    }

    private void flashBar(JProgressBar bar, Color flashColor) {
        Color original = bar.getForeground();
        bar.setForeground(flashColor);
        Timer timer = new Timer(200, e -> bar.setForeground(original));
        timer.setRepeats(false);
        timer.start();
    }

    private void flashLabel(JLabel label, Color flashColor) {
        Color original = label.getForeground();
        label.setForeground(flashColor);
        Timer timer = new Timer(200, e -> label.setForeground(original));
        timer.setRepeats(false);
        timer.start();
    }

    private String formatChoiceText(Choice choice, ChoiceEffect effect) {
        String effectText = buildEffectText(effect);
        if (effectText.isEmpty()) {
            effectText = "変化なし";
        }
        return "<html><b>" + escapeHtml(choice.getText()) + "</b><br><span style='color:#888888; font-size:10px;'>"
                + escapeHtml(effectText) + "</span></html>";
    }

    private String buildEffectText(ChoiceEffect effect) {
        StringBuilder sb = new StringBuilder();
        appendDelta(sb, "体力", effect.healthDelta);
        appendDelta(sb, "ストレス", effect.stressDelta);
        appendDelta(sb, "お金", effect.moneyDelta);
        appendDelta(sb, "年齢", effect.ageDelta);
        if (effect.nextStatus != null) {
            if (sb.length() > 0) {
                sb.append(" / ");
            }
            sb.append("身分→").append(effect.nextStatus);
        }
        return sb.toString();
    }

    private void appendDelta(StringBuilder sb, String label, long delta) {
        if (delta == 0) {
            return;
        }
        if (sb.length() > 0) {
            sb.append(" / ");
        }
        sb.append(label).append(" ").append(formatArrow(delta));
    }

    private String formatDelta(String label, long delta) {
        return label + " " + formatArrow(delta);
    }

    private String formatArrow(long value) {
        if (value > 0) {
            return "↑";
        }
        if (value < 0) {
            return "↓";
        }
        return "→";
    }

    private void applyEventTheme(String title) {
        if (title == null) {
            return;
        }
        Color themed = new Color(60, 64, 72);
        String mood = "日常";
        if (title.contains("恋愛") || title.contains("合コン") || title.contains("告白")) {
            themed = new Color(70, 40, 55);
            mood = "恋愛";
        } else if (title.contains("病院") || title.contains("健康") || title.contains("再検査")) {
            themed = new Color(35, 45, 70);
            mood = "健康";
        } else if (title.contains("借金") || title.contains("闇金") || title.contains("督促")) {
            themed = new Color(70, 35, 35);
            mood = "危機";
        } else if (title.contains("宝くじ") || title.contains("ボーナス")) {
            themed = new Color(70, 60, 25);
            mood = "金運";
        } else if (title.contains("分岐") || title.contains("進路") || title.contains("選択")) {
            themed = new Color(50, 50, 75);
            mood = "分岐";
        }
        eventArea.setBackground(themed);
        moodLabel.setText("ムード: " + mood);
        if (isBranchEvent(title)) {
            Color borderColor = title.contains("分岐") || title.contains("進路") ? new Color(241, 196, 15)
                    : new Color(231, 76, 60);
            eventArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        } else {
            eventArea.setBorder(defaultEventBorder);
        }
    }

    private String computeTendency(Player player) {
        int health = player.countTag("health");
        int social = player.countTag("social");
        int invest = player.countTag("invest");
        int family = player.countTag("family");
        int max = Math.max(Math.max(health, social), Math.max(invest, family));
        if (max == 0) {
            return "未定";
        }
        if (max == health) {
            return "健康派";
        }
        if (max == social) {
            return "社交派";
        }
        if (max == invest) {
            return "堅実派";
        }
        return "家族派";
    }

    private String computeRelation(Player player) {
        int social = player.countTag("social");
        if (social >= 12) {
            return "高";
        }
        if (social >= 5) {
            return "中";
        }
        return "低";
    }

    private boolean isBranchEvent(String title) {
        if (title == null) {
            return false;
        }
        return title.contains("分岐") || title.contains("進路") || title.contains("選択") || title.contains("卒業")
                || title.contains("入学");
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
