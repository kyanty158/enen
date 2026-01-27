import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

class TitleView extends JFrame {
    private final Color BG_COLOR = new Color(25, 28, 35);
    private final Color ACCENT_COLOR = new Color(97, 175, 239);
    private final Color GOLD_COLOR = new Color(255, 215, 0);

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
        getContentPane().setBackground(BG_COLOR);

        // メインパネル
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // タイトルラベル
        JLabel titleLabel = new JLabel("人生100年");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titleLabel.setForeground(GOLD_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("サバイバル");
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        subtitleLabel.setForeground(ACCENT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // キャッチコピー
        JLabel catchLabel = new JLabel("〜 選択が運命を変える 〜");
        catchLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        catchLabel.setForeground(Color.LIGHT_GRAY);
        catchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // バージョン情報
        JLabel versionLabel = new JLabel("フルカスタム版 v3.0");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 設定パネル
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel nameLabel = new JLabel("名前");
        nameLabel.setForeground(Color.LIGHT_GRAY);
        JTextField nameField = new JTextField("プレイヤー", 14);

        JLabel diffLabel = new JLabel("難易度");
        diffLabel.setForeground(Color.LIGHT_GRAY);
        JComboBox<Difficulty> diffBox = new JComboBox<>(Difficulty.values());
        diffBox.setSelectedItem(Difficulty.NORMAL);

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

        // 説明テキスト
        JTextArea descArea = new JTextArea(
                "【ルール】\n" +
                        "・0歳から100歳を目指して生き抜け！\n" +
                        "・体力が0になると死亡\n" +
                        "・ストレスが100になると死亡\n" +
                        "・難易度/初期ステータスで人生が変化" +
                        "\n\n【新機能】\n" +
                        "・セーブ/ロード\n" +
                        "・履歴ログ閲覧\n" +
                        "・実績＆称号＆統計\n" +
                        "・選択結果の演出");
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descArea.setForeground(Color.LIGHT_GRAY);
        descArea.setBackground(new Color(35, 38, 45));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        descArea.setMaximumSize(new Dimension(420, 200));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 配置
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(catchLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(versionLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(loadButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(descArea);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
