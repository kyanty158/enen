import javax.swing.*;
import java.awt.*;

class StorybookView extends JFrame {
    private final Color BG_COLOR = new Color(20, 24, 32);
    private final Color TEXT_COLOR = new Color(230, 232, 236);

    public StorybookView(Player player, Economy economy) {
        setTitle("人生ストーリーブック");
        setSize(520, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(new Color(35, 38, 45));
        area.setForeground(TEXT_COLOR);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setMargin(new Insets(12, 12, 12, 12));

        String story = StoryBuilder.build(player, economy);
        area.setText(story);
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton saveButton = new JButton("テキスト保存");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> {
            boolean ok = SaveManager.saveStory(story);
            JOptionPane.showMessageDialog(this, ok ? "保存しました。" : "保存に失敗しました。\n" + SaveManager.getStoryLocation());
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        footer.setBackground(BG_COLOR);
        footer.add(saveButton);

        add(scroll, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
