import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

class TitleView extends JFrame {
    private final Color ACCENT_COLOR = new Color(97, 175, 239);
    private final Color GOLD_COLOR = new Color(255, 215, 0);
    private final Color PANEL_COLOR = new Color(20, 24, 30);

    private static class GradientPanel extends JPanel {
        private final Color top = new Color(20, 24, 34);
        private final Color bottom = new Color(12, 14, 20);

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bottom);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            g2.setComposite(AlphaComposite.SrcOver.derive(0.12f));
            g2.setColor(new Color(97, 175, 239));
            g2.fillOval((int) (w * 0.6), (int) (h * -0.1), (int) (w * 0.6), (int) (w * 0.6));
            g2.setColor(new Color(255, 215, 0));
            g2.fillOval((int) (w * -0.2), (int) (h * 0.55), (int) (w * 0.5), (int) (w * 0.5));
            g2.dispose();
        }
    }

    private static class StatusOption {
        final String label;
        final String status;
        final int age;

        StatusOption(String label, String status, int age) {
            this.label = label;
            this.status = status;
            this.age = age;
        }
    }

    public TitleView(Consumer<GameConfig> onStart, Runnable onLoad, boolean canLoad) {
        setTitle("人生100年サバイバル");
        setSize(520, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setContentPane(new GradientPanel());
        getContentPane().setLayout(new BorderLayout());

        // メインパネル
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // タイトルラベル
        JLabel titleLabel = new JLabel("人生100年");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 54));
        titleLabel.setForeground(GOLD_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("サバイバル");
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        subtitleLabel.setForeground(ACCENT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // キャッチコピー
        JLabel catchLabel = new JLabel("〜 選択が運命を変える 〜");
        catchLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        catchLabel.setForeground(Color.LIGHT_GRAY);
        catchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JPanel accentBar = new JPanel();
        accentBar.setBackground(ACCENT_COLOR);
        accentBar.setMaximumSize(new Dimension(120, 4));
        accentBar.setPreferredSize(new Dimension(120, 4));
        accentBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 設定パネル
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 85, 95)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel nameLabel = new JLabel("名前");
        nameLabel.setForeground(Color.LIGHT_GRAY);
        JTextField nameField = new JTextField("プレイヤー", 14);
        nameField.setBackground(new Color(30, 34, 40));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);

        JLabel diffLabel = new JLabel("難易度");
        diffLabel.setForeground(Color.LIGHT_GRAY);
        JComboBox<Difficulty> diffBox = new JComboBox<>(Difficulty.values());
        diffBox.setSelectedItem(Difficulty.NORMAL);
        diffBox.setBackground(new Color(30, 34, 40));
        diffBox.setForeground(Color.WHITE);

        StatusOption[] options = new StatusOption[] {
                new StatusOption("幼児 (0歳)", "幼児", 0),
                new StatusOption("小学生 (7歳)", "小学生", 7),
                new StatusOption("中学生 (13歳)", "中学生", 13),
                new StatusOption("高校生 (16歳)", "高校生", 16),
                new StatusOption("大学生 (19歳)", "大学生", 19),
                new StatusOption("社会人 (23歳)", "社会人", 23),
                new StatusOption("フリーター (19歳)", "フリーター", 19),
                new StatusOption("ニート (19歳)", "ニート", 19)
        };
        JLabel statusLabel = new JLabel("開始ステータス");
        statusLabel.setForeground(Color.LIGHT_GRAY);
        String[] statusLabels = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            statusLabels[i] = options[i].label;
        }
        JComboBox<String> statusBox = new JComboBox<>(statusLabels);
        statusBox.setBackground(new Color(30, 34, 40));
        statusBox.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(diffLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(diffBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(statusBox, gbc);

        // スタートボタン
        JButton startButton = new JButton("▶ ゲームスタート");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(260, 60));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ホバー効果
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(39, 174, 96));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(46, 204, 113));
            }
        });

        startButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                name = "プレイヤー";
            }
            Difficulty diff = (Difficulty) diffBox.getSelectedItem();
            int index = statusBox.getSelectedIndex();
            StatusOption option = options[index];

            dispose();
            onStart.accept(new GameConfig(name, diff, option.status, option.age));
        });

        JButton loadButton = new JButton("▶ つづきから");
        loadButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loadButton.setForeground(Color.WHITE);
        loadButton.setBackground(new Color(52, 152, 219));
        loadButton.setFocusPainted(false);
        loadButton.setBorderPainted(false);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setMaximumSize(new Dimension(260, 48));
        loadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadButton.setEnabled(canLoad);

        loadButton.addActionListener(e -> {
            dispose();
            onLoad.run();
        });

        JButton endingsButton = new JButton("▶ 図鑑");
        endingsButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        endingsButton.setForeground(Color.WHITE);
        endingsButton.setBackground(new Color(155, 89, 182));
        endingsButton.setFocusPainted(false);
        endingsButton.setBorderPainted(false);
        endingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        endingsButton.setMaximumSize(new Dimension(260, 44));
        endingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        endingsButton.addActionListener(e -> {
            new EndingsView();
        });

        // 配置
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(12));
        headerPanel.add(accentBar);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(catchLabel);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(loadButton);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(endingsButton);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
