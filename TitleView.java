import javax.swing.*;
import java.awt.*;

class TitleView extends JFrame {
    private final Color BG_COLOR = new Color(25, 28, 35);
    private final Color ACCENT_COLOR = new Color(97, 175, 239);
    private final Color GOLD_COLOR = new Color(255, 215, 0);

    public TitleView(Runnable onStart) {
        setTitle("人生100年サバイバル");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // メインパネル
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(80, 50, 80, 50));

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
        JLabel versionLabel = new JLabel("分岐強化版 v2.0");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // スタートボタン
        JButton startButton = new JButton("▶ ゲームスタート");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(250, 60));
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
            dispose(); // タイトル画面を閉じる
            onStart.run(); // ゲーム開始
        });

        // 説明テキスト
        JTextArea descArea = new JTextArea(
                "【ルール】\n" +
                        "・0歳から100歳を目指して生き抜け！\n" +
                        "・体力が0になると死亡\n" +
                        "・ストレスが100になると死亡\n" +
                        "・選択によって人生が大きく変わる");
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descArea.setForeground(Color.LIGHT_GRAY);
        descArea.setBackground(new Color(35, 38, 45));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        descArea.setMaximumSize(new Dimension(400, 150));
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
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(descArea);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
