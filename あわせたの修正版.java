import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/*
 * ==========================================
 * DATA STRUCTURES (Helper Classes)
 * ==========================================
 */

class DeathResult {
    public enum Type { ALIVE, DEAD }
    
    public Type type;
    public String title;
    public String message;
    public int age;

    public DeathResult(Type type, String title, String message, int age) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.age = age;
    }
}

class Choice {
    public String text;
    public int healthDelta;
    public int stressDelta;
    public long moneyDelta;
    public int ageDelta;

    public Choice(String text, int h, int s, long m, int ageDelta) {
        this.text = text;
        this.healthDelta = h;
        this.stressDelta = s;
        this.moneyDelta = m;
        this.ageDelta = ageDelta;
    }
    
    // ボタン表示用にテキストを整形
    public String getText() {
        return text + " (+" + ageDelta + "年)";
    }
}

class LifeEvent {
    private String title;
    private List<Choice> choices;

    public LifeEvent(String title) {
        this.title = title;
        this.choices = new ArrayList<>();
    }

    public void addChoice(String text, int h, int s, long m, int ageDelta) {
        choices.add(new Choice(text, h, s, m, ageDelta));
    }

    public String getTitle() { return title; }
    // Viewでの表示用に詳細なテキストを返す
    public String getText() { return title; }
    public List<Choice> getChoices() { return choices; }
}

class Player {
    public static final int HUMAN_LIFESPAN = 100;

    private int age;
    private int health;
    private int stress;
    private long money;
    private boolean isAlive;

    public Player() {
        this.age = 0;       // 0歳スタート
        this.health = 50;   // 赤ちゃんは体力低め
        this.stress = 0;
        this.money = 0;
        this.isAlive = true;
    }

    public void incrementAge(int years) {
        this.age += years;
    }

    public void changeHealth(int amount) {
        this.health += amount;
        if (this.health > 100) this.health = 100;
    }

    public void changeStress(int amount) {
        this.stress += amount;
        if (this.stress < 0) this.stress = 0;
    }

    public void changeMoney(long amount) {
        this.money += amount;
        // 借金も可能にする（マイナス許容）
    }

    public void checkVitality() {
        if (this.health <= 0 || this.stress >= 100) {
            this.isAlive = false;
        }
    }

    public int getAge() { return age; }
    public boolean isAlive() { return isAlive; }
    public int getHealth() { return health; }
    public int getStress() { return stress; }
    public long getMoney() { return money; }
}

/*
 * ==========================================
 * LOGIC (Event Repository)
 * ==========================================
 */
class GameLogic {

    // メインのイベント振り分けメソッド
    public LifeEvent getEventForAge(int age) {
        if (age < 23) {
            return getChildhoodEvent(age);
        } else if (age < 65) {
            return getAdultEvent(age);
        } else if (age < 100) {
            return getSeniorEvent(age);
        } else {
            return getEndingEvent();
        }
    }

    // ----------------------------------------------------------------
    // 0歳〜22歳：子供・学生時代（1年〜数年刻み）
    // ----------------------------------------------------------------
    private LifeEvent getChildhoodEvent(int age) {
        LifeEvent event = new LifeEvent("イベントエラー");

        switch (age) {
            case 0:
                event = new LifeEvent("【0歳】誕生。光に包まれて、あなたの人生が始まります。");
                event.addChoice("大声で泣いて自己主張", 5, 0, 0, 3); // 3歳へ
                event.addChoice("静かに親を見つめる", 0, 0, 0, 3);
                break;
            case 3:
                event = new LifeEvent("【3歳】自我の芽生え。「イヤイヤ期」です。");
                event.addChoice("何でも「イヤ！」と拒否", 5, 10, 0, 3); // 6歳へ
                event.addChoice("いい子にして褒められる", 0, -5, 0, 3);
                event.addChoice("積み木に熱中する", 0, 0, 0, 3);
                break;
            case 6:
                event = new LifeEvent("【6歳】小学校入学。ランドセルが歩いているようです。");
                event.addChoice("すぐに隣の子と話す", 5, 5, 0, 2); // 8歳へ
                event.addChoice("緊張して固まる", -2, 10, 0, 2);
                break;
            case 8:
                event = new LifeEvent("【8歳】低学年。習い事を始めますか？");
                event.addChoice("スイミングに通う", 10, 5, -50000, 2); // 10歳へ
                event.addChoice("ピアノを習う", 0, 5, -100000, 2);
                event.addChoice("公園で毎日遊ぶ", 5, -5, 0, 2);
                break;
            case 10:
                event = new LifeEvent("【10歳】1/2成人式。クラスでの立ち位置が決まってきました。");
                event.addChoice("目立ちたがり屋のリーダー", 0, 10, 0, 2); // 12歳へ
                event.addChoice("聞き上手なサポーター", 0, 0, 0, 2);
                event.addChoice("我が道を行く一匹狼", 5, -5, 0, 2);
                break;
            case 12:
                event = new LifeEvent("【12歳】卒業間近。バレンタイン/ホワイトデーの思い出。");
                event.addChoice("好きな人に告白する", 5, 20, -5000, 1); // 13歳へ
                event.addChoice("友達とチョコ交換", 0, 0, -3000, 1);
                event.addChoice("興味ないフリをする", 0, -5, 0, 1);
                break;
            case 13:
                event = new LifeEvent("【13歳】中学生。部活動の厳しさを知ります。");
                event.addChoice("レギュラー目指して朝練", 10, 15, 0, 1);
                event.addChoice("サボりながら楽しくやる", -5, -5, 0, 1);
                event.addChoice("幽霊部員になる", 0, -5, 0, 1);
                break;
            case 14:
                event = new LifeEvent("【14歳】中二病の季節。「自分は特別だ」と思い始めます。");
                event.addChoice("深夜ラジオに投稿する", -5, -5, 0, 1);
                event.addChoice("ポエムを書き溜める", 0, 0, 0, 1);
                event.addChoice("悪い先輩とつるむ", -10, 5, 0, 1);
                break;
            case 15:
                event = new LifeEvent("【15歳】高校受験。人生最初の試練です。");
                event.addChoice("トップ高を目指して猛勉強", -10, 40, -200000, 1);
                event.addChoice("確実な学校を選ぶ", 5, 10, 0, 1);
                event.addChoice("勉強より思い出作り", 0, -10, 0, 1);
                break;
            case 16:
                event = new LifeEvent("【16歳】高校入学。環境がガラリと変わりました。");
                event.addChoice("バイトを始めて社会を知る", -5, 10, 300000, 1);
                event.addChoice("帰宅部でゲーム三昧", -5, -10, 0, 1);
                event.addChoice("恋人を作る！", 5, 20, -50000, 1);
                break;
            case 17:
                event = new LifeEvent("【17歳】修学旅行の夜。消灯時間を過ぎて...");
                event.addChoice("好きな人の話で盛り上がる", 5, -5, 0, 1);
                event.addChoice("先生に見つかり正座", -5, 10, 0, 1);
                event.addChoice("一人で早く寝る", 5, -5, 0, 1);
                break;
            case 18:
                event = new LifeEvent("【18歳】進路選択。大人への第一歩。");
                event.addChoice("大学進学(奨学金なし)", 0, 10, -5000000, 1); // 親の金
                event.addChoice("大学進学(奨学金あり)", 0, 20, -1000000, 1); // 借金少なめスタート
                event.addChoice("就職して自立", -5, 30, 2000000, 5);     // 社会人へジャンプ
                break;
            // 19-22歳は大卒ルート限定
            case 19:
                event = new LifeEvent("【19歳】大学1年。サークルの新歓コンパ。");
                event.addChoice("飲みサーでウェイウェイ", -10, -10, -100000, 1);
                event.addChoice("真面目な研究会に入る", 0, 5, 0, 1);
                break;
            case 20:
                event = new LifeEvent("【20歳】成人式。地元の友人と再会。");
                event.addChoice("朝まで飲み明かす", -10, -5, -30000, 1);
                event.addChoice("行かずに家で過ごす", 5, -5, 0, 1);
                break;
            case 21:
                event = new LifeEvent("【21歳】就職活動。お祈りメールが届きます。");
                event.addChoice("自己分析をやり直す", -5, 10, 0, 1);
                event.addChoice("面接を受けまくる", -10, 30, -50000, 1); // 交通費
                event.addChoice("現実逃避して旅に出る", 5, -20, -200000, 1);
                break;
            case 22:
                event = new LifeEvent("【22歳】卒業論文と卒業旅行。");
                event.addChoice("卒論を完璧に仕上げる", -10, 20, 0, 1);
                event.addChoice("海外へ卒業旅行", 5, -10, -300000, 1);
                break;
            default:
                // 年齢がズレた場合のセーフティネット
                event = new LifeEvent("【" + age + "歳】時は流れます...");
                event.addChoice("次へ進む", 0, 0, 0, 1);
                break;
        }
        return event;
    }

    // ----------------------------------------------------------------
    // 23歳〜64歳：社会人時代（ランダムイベントで密度を出す）
    // ----------------------------------------------------------------
    private LifeEvent getAdultEvent(int age) {
        LifeEvent event = new LifeEvent("社会人生活");
        double rnd = Math.random();

        // 20代〜30代前半：キャリアと結婚
        if (age < 35) {
            if (rnd < 0.4) {
                event = new LifeEvent("【" + age + "歳】仕事で大きなミスをしてしまった...");
                event.addChoice("正直に謝る", 0, 20, 0, 3);
                event.addChoice("隠蔽しようとする", -5, 40, 0, 3);
                event.addChoice("同僚のせいにする", -5, 10, 0, 3);
            } else if (rnd < 0.7) {
                event = new LifeEvent("【" + age + "歳】友人たちが次々と結婚していく。");
                event.addChoice("婚活パーティーに参加", -5, 10, -50000, 3);
                event.addChoice("仕事こそが恋人", -10, 20, 500000, 3); // 昇給
                event.addChoice("一人の時間を楽しむ", 5, -10, -100000, 3);
            } else {
                event = new LifeEvent("【" + age + "歳】初めての大きな昇進チャンス！");
                event.addChoice("休日返上で働く", -20, 30, 2000000, 3);
                event.addChoice("ワークライフバランス重視", 5, -5, 500000, 3);
            }
        } 
        // 35歳〜49歳：責任と家庭、マイホーム
        else if (age < 50) {
            if (rnd < 0.4) {
                event = new LifeEvent("【" + age + "歳】マイホーム購入を検討中。");
                event.addChoice("35年ローンで一軒家", 5, 30, -40000000, 4); // 大借金
                event.addChoice("都心の中古マンション", 5, 10, -25000000, 4);
                event.addChoice("気楽な賃貸暮らし", 0, -5, -2000000, 4);
            } else if (rnd < 0.7) {
                event = new LifeEvent("【" + age + "歳】子供の反抗期と教育費。");
                event.addChoice("塾に課金する", -5, 10, -3000000, 4);
                event.addChoice("家族旅行で絆を深める", 5, -5, -500000, 4);
                event.addChoice("放任主義", 0, 10, 0, 4);
            } else {
                event = new LifeEvent("【" + age + "歳】中間管理職の悲哀。上と下からの板挟み。");
                event.addChoice("心を無にして耐える", -5, 20, 1000000, 4);
                event.addChoice("部下を飲みに連れて行く", -10, 5, -200000, 4);
                event.addChoice("副業を始める", -10, 10, 2000000, 4);
            }
        } 
        // 50歳〜64歳：健康、介護、役職定年
        else {
            if (rnd < 0.4) {
                event = new LifeEvent("【" + age + "歳】親の介護が必要になってきた。");
                event.addChoice("同居して介護する", -20, 40, 1000000, 5); // 遺産前借り？
                event.addChoice("施設にお願いする", 5, 10, -10000000, 5); // 金がかかる
                event.addChoice("兄弟に任せる", 0, 20, 0, 5); // 揉める
            } else if (rnd < 0.7) {
                event = new LifeEvent("【" + age + "歳】健康診断でE判定が出た。");
                event.addChoice("お酒・タバコをやめる", 10, 20, 500000, 5);
                event.addChoice("気にせず豪遊する", -20, -10, -1000000, 5);
                event.addChoice("高いサプリを買う", 0, 0, -500000, 5);
            } else {
                event = new LifeEvent("【" + age + "歳】役職定年。給料が下がり、部下が上司になった。");
                event.addChoice("プライドを捨てて働く", -5, 10, 500000, 5);
                event.addChoice("早期リタイアして田舎へ", 10, -10, -2000000, 5);
                event.addChoice("会社にしがみつく", -10, 30, 1000000, 5);
            }
        }
        return event;
    }

    // ----------------------------------------------------------------
    // 65歳〜99歳：老後（バリエーション強化）
    // ----------------------------------------------------------------
    private LifeEvent getSeniorEvent(int age) {
        LifeEvent event = new LifeEvent("老後の日々");
        double rnd = Math.random();

        // 65歳〜74歳：アクティブシニア期
        if (age < 75) {
            if (rnd < 0.3) {
                event = new LifeEvent("【" + age + "歳】再雇用期間も終了。毎日が日曜日です。");
                event.addChoice("地域のボランティアに参加", 5, -5, -10000, 3);
                event.addChoice("シルバー人材で働く", -5, 5, 500000, 3);
                event.addChoice("家でテレビ三昧", -10, 5, 0, 3); // 認知機能低下リスク
            } else if (rnd < 0.6) {
                event = new LifeEvent("【" + age + "歳】孫が遊びに来ました。");
                event.addChoice("おもちゃを買い与える", 5, -10, -100000, 3);
                event.addChoice("昔話を聞かせる", 5, 0, 0, 3);
                event.addChoice("疲れるので居留守を使う", 0, 5, 0, 3);
            } else {
                event = new LifeEvent("【" + age + "歳】夫婦で久しぶりの旅行。");
                event.addChoice("豪華クルーズ世界一周", 10, -10, -5000000, 3);
                event.addChoice("近場の温泉旅館", 5, -5, -100000, 3);
            }
        } 
        // 75歳〜89歳：後期高齢者期（喪失と健康）
        else if (age < 90) {
            if (rnd < 0.3) {
                event = new LifeEvent("【" + age + "歳】運転免許の返納を家族に迫られています。");
                event.addChoice("素直に返納する", 0, -5, 0, 3);
                event.addChoice("まだ運転できると怒る", 0, 10, 0, 3);
                event.addChoice("電動カートを買う", 0, 0, -200000, 3);
            } else if (rnd < 0.6) {
                event = new LifeEvent("【" + age + "歳】親しい友人の葬式がありました。");
                event.addChoice("自分の終活を始める", 0, -5, -500000, 3);
                event.addChoice("寂しさを紛らわすため飲む", -10, -5, -20000, 3);
                event.addChoice("死後の世界について考える", 0, 10, 0, 3);
            } else {
                event = new LifeEvent("【" + age + "歳】足腰が弱ってきました。");
                event.addChoice("リハビリに励む", 5, 5, -100000, 3);
                event.addChoice("老人ホーム入居を検討", 0, -10, -10000000, 3); // 高額
                event.addChoice("家から出なくなる", -15, 0, 0, 3);
            }
        } 
        // 90歳〜99歳：人生の締めくくり
        else {
            event = new LifeEvent("【" + age + "歳】100歳は目前です。今の心境は？");
            event.addChoice("人生に感謝する", 10, -20, 0, 2);
            event.addChoice("まだやり残したことがある", 5, 10, 0, 2);
            event.addChoice("家族に「ありがとう」と伝える", 10, -10, 0, 2);
        }

        return event;
    }

    private LifeEvent getEndingEvent() {
        LifeEvent event = new LifeEvent("【100歳】大往生");
        event.addChoice("静かに目を閉じる...", 0, 0, 0, 0);
        return event;
    }
}

/*
 * ==========================================
 * MODEL
 * ==========================================
 */
class GameModel {
    private final Player player;
    private final GameLogic logic;

    public GameModel() {
        this.player = new Player();
        this.logic = new GameLogic();
    }

    public Player getPlayer() {
        return player;
    }

    public LifeEvent nextEvent() {
        return logic.getEventForAge(player.getAge());
    }

    public DeathResult applyChoice(Choice choice) {
        // ステータス反映
        player.changeHealth(choice.healthDelta);
        player.changeStress(choice.stressDelta);
        player.changeMoney(choice.moneyDelta);
        
        // 死亡判定
        player.checkVitality();

        if (!player.isAlive()) {
            return new DeathResult(
                DeathResult.Type.DEAD, 
                "過労死・ストレス死", 
                "あなたの人生はここで幕を閉じました。\n無理をしすぎたようです...", 
                player.getAge()
            );
        }

        // 年齢加算
        player.incrementAge(choice.ageDelta);

        // 寿命判定 (100歳到達でゴール)
        if (player.getAge() >= 100) {
             return new DeathResult(
                DeathResult.Type.DEAD, 
                "大往生", 
                "100年の人生を全うしました。\n素晴らしい人生でしたね。", 
                player.getAge()
            );
        }

        // 生存
        return new DeathResult(DeathResult.Type.ALIVE, "", "", player.getAge());
    }
}

/*
 * ==========================================
 * VIEW
 * ==========================================
 */
class GameView extends JFrame {
    private final JLabel ageLabel = new JLabel();
    private final JLabel statsLabel = new JLabel(); 
    private final JTextArea eventArea = new JTextArea();
    private final JPanel buttonPanel = new JPanel();

    public GameView(ActionListener listener) {
        setTitle("人生ロングラン (0-100歳)");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 上部パネル（年齢とステータス）
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        ageLabel.setFont(new Font("Meiryo", Font.BOLD, 28));
        ageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        statsLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(ageLabel);
        topPanel.add(statsLabel);
        add(topPanel, BorderLayout.NORTH);

        // 中央パネル（イベントテキスト）
        eventArea.setEditable(false);
        eventArea.setLineWrap(true);
        eventArea.setWrapStyleWord(true);
        eventArea.setFont(new Font("Meiryo", Font.PLAIN, 18));
        eventArea.setMargin(new Insets(30, 30, 30, 30));
        add(new JScrollPane(eventArea), BorderLayout.CENTER);

        // 下部パネル（選択肢ボタン）
        buttonPanel.setLayout(new GridLayout(0, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateDisplay(Player player) {
        ageLabel.setText("年齢: " + player.getAge() + "歳");
        
        // お金の表示色（赤字かどうか）
        String moneyColor = player.getMoney() < 0 ? "red" : "black";
        
        statsLabel.setText(String.format(
            "<html>HP: %d | ストレス: %d | 所持金: <font color='%s'>%,d円</font></html>", 
            player.getHealth(), player.getStress(), moneyColor, player.getMoney()));
    }

    public void showEvent(LifeEvent event, ActionListener listener) {
        eventArea.setText(event.getText());
        buttonPanel.removeAll();

        List<Choice> choices = event.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            JButton b = new JButton(choices.get(i).getText());
            b.setFont(new Font("Meiryo", Font.PLAIN, 16));
            b.setPreferredSize(new Dimension(0, 50));
            b.setActionCommand(String.valueOf(i));
            b.addActionListener(listener);
            buttonPanel.add(b);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void showGameOver(String title, String message, int age) {
        JOptionPane.showMessageDialog(
                this,
                message + "\n\n最終年齢: " + age + "歳",
                title,
                JOptionPane.PLAIN_MESSAGE
        );
        System.exit(0);
    }
}

/*
 * ==========================================
 * CONTROLLER
 * ==========================================
 */
class GameController implements ActionListener {
    private final GameModel model;
    private final GameView view;
    private LifeEvent currentEvent;

    public GameController() {
        model = new GameModel();
        view = new GameView(this);
        nextTurn();
    }

    private void nextTurn() {
        view.updateDisplay(model.getPlayer()); // 年齢とステータスを更新
        currentEvent = model.nextEvent();
        view.showEvent(currentEvent, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = Integer.parseInt(e.getActionCommand());
        
        if (index < 0 || index >= currentEvent.getChoices().size()) return;

        Choice selected = currentEvent.getChoices().get(index);
        DeathResult result = model.applyChoice(selected);

        if (result.type == DeathResult.Type.ALIVE) {
            nextTurn();
        } else {
            view.updateDisplay(model.getPlayer()); // 最期のステータスを表示
            view.showGameOver(result.title, result.message, result.age);
        }
    }
}

/*
 * ==========================================
 * MAIN
 * ==========================================
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}
