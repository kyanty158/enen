import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/*
 * ==========================================
 * DATA STRUCTURES
 * ==========================================
 */
class DeathResult {
    public enum Type {
        ALIVE, DEAD
    }

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
    public String nextStatus; // この選択肢を選んだ後のステータス変更（nullなら変更なし）

    public Choice(String text, int h, int s, long m, int ageDelta, String nextStatus) {
        this.text = text;
        this.healthDelta = h;
        this.stressDelta = s;
        this.moneyDelta = m;
        this.ageDelta = ageDelta;
        this.nextStatus = nextStatus;
    }

    // 既存のコンストラクタ互換用（ステータス変更なし）
    public Choice(String text, int h, int s, long m, int ageDelta) {
        this(text, h, s, m, ageDelta, null);
    }

    public String getText() {
        return text;
    }
}

class LifeEvent {
    private String title;
    private List<Choice> choices;

    public LifeEvent(String title) {
        this.title = title;
        this.choices = new ArrayList<>();
    }

    // ステータス変更ありの選択肢追加
    public void addChoice(String text, int h, int s, long m, int ageDelta, String nextStatus) {
        choices.add(new Choice(text, h, s, m, ageDelta, nextStatus));
    }

    // 通常の選択肢追加
    public void addChoice(String text, int h, int s, long m, int ageDelta) {
        choices.add(new Choice(text, h, s, m, ageDelta, null));
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return title;
    }

    public List<Choice> getChoices() {
        return choices;
    }
}

class Player {
    public static final int MAX_LIFESPAN = 100;

    private int age;
    private int health;
    private int stress;
    private long money;
    private boolean isAlive;
    private String status; // 現在の身分（学生、社会人、ニートなど）
    private List<String> history;

    public Player() {
        this.age = 0;
        this.health = 100;
        this.stress = 0;
        this.money = 0;
        this.isAlive = true;
        this.status = "幼児"; // 初期ステータス
        this.history = new ArrayList<>();
    }

    public void addHistory(String eventTitle, String choiceText) {
        String log = String.format("%d歳 [%s]: %s\n   ↳ %s", age, status, eventTitle, choiceText);
        history.add(log);
    }

    public List<String> getHistory() {
        return history;
    }

    public void incrementAge(int years) {
        this.age += years;
    }

    public void setStatus(String status) {
        if (status != null) {
            this.status = status;
        }
    }

    public String getStatus() {
        return status;
    }

    public void changeHealth(int amount) {
        this.health += amount;
        if (this.health > 100)
            this.health = 100;
    }

    public void changeStress(int amount) {
        this.stress += amount;
        if (this.stress < 0)
            this.stress = 0;
    }

    public void changeMoney(long amount) {
        this.money += amount;
    }

    public void checkVitality() {
        // 老化ダメージ: 60歳以降
        if (age > 60) {
            int agingDamage = (age - 60) / 4 + 1;
            this.health -= agingDamage;
        }

        if (this.health <= 0 || this.stress >= 100) {
            this.isAlive = false;
        }
    }

    public int getAge() {
        return age;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getHealth() {
        return health;
    }

    public int getStress() {
        return stress;
    }

    public long getMoney() {
        return money;
    }
}

/*
 * ==========================================
 * LOGIC (Chronologically Consistent Logic)
 * ==========================================
 */
class GameLogic {
    private double random() {
        return Math.random();
    }

    public LifeEvent getEventForAge(Player player) {
        int age = player.getAge();
        String status = player.getStatus();
        long money = player.getMoney();
        LifeEvent event;
        double dice = random();

        // ==============================================
        // 借金イベント（優先発生）: 500万円以上の借金
        // ==============================================
        if (money <= -5000000 && age >= 18 && dice < 0.4) {
            return getDebtEvent(money);
        }

        // ==============================================
        // ランダム金運イベント（低確率で発生）
        // ==============================================
        if (dice < 0.08 && age >= 6) {
            LifeEvent luckyEvent = getLuckyMoneyEvent(age, status);
            if (luckyEvent != null)
                return luckyEvent;
        }

        // ==============================================
        // 100歳: 寿命
        // ==============================================
        if (age >= 100) {
            event = new LifeEvent("【大往生】100年の時を生き抜きました。");
            event.addChoice("眠るように逝く", -999, 0, 0, 0);
            return event;
        }

        // ==============================================
        // 0-6歳: 幼児期 (強制イベントあり)
        // ==============================================
        if (age <= 6) {
            if (age == 6) {
                // 小学校入学確定イベント
                event = new LifeEvent("【入学】ピカピカのランドセルを背負って小学校へ入学します。");
                event.addChoice("友達100人できるかな", 5, 5, 0, 1, "小学生");
                event.addChoice("行きたくないと泣く", -5, 10, 0, 1, "小学生");
            } else {
                // ランダム幼児イベント
                if (dice < 0.25) {
                    event = new LifeEvent("【発見】ダンゴムシを見つけました。");
                    event.addChoice("ポケットに入れる", 0, -5, 0, 1);
                    event.addChoice("食べる", -10, 0, 0, 1);
                } else if (dice < 0.5) {
                    event = new LifeEvent("【おつかい】初めてのおつかいを頼まれました。");
                    event.addChoice("無事に買って帰る", 5, 5, 0, 1); // 成功体験
                    event.addChoice("道草して迷子", 0, 10, 0, 1);
                } else if (dice < 0.75) {
                    event = new LifeEvent("【昼寝】お昼寝の時間です。");
                    event.addChoice("ぐっすり寝る", 10, -10, 0, 1);
                    event.addChoice("寝たふりして遊ぶ", -5, 5, 0, 1);
                } else {
                    event = new LifeEvent("【ヒーロー】テレビのヒーローごっこをしています。");
                    event.addChoice("高い所からジャンプ", -10, 5, 0, 1);
                    event.addChoice("必殺技を叫ぶ", 2, -2, 0, 1);
                }
            }

            // ==============================================
            // 7-12歳: 小学生 (status="小学生")
            // ==============================================
        } else if (age <= 12) {
            if (age == 12) {
                // 中学入学確定イベント
                event = new LifeEvent("【卒業・入学】小学校を卒業し、中学生になります。");
                event.addChoice("制服に袖を通す", 5, 5, 0, 1, "中学生");
                event.addChoice("部活選びに悩む", 0, 10, 0, 1, "中学生");
            } else {
                // ランダム小学生イベント
                if (dice < 0.15) {
                    // 【追加】お年玉イベント
                    event = new LifeEvent("【お正月】親戚が集まりました。お年玉タイム！");
                    event.addChoice("おじいちゃんにおねだり", 0, -5, 10000, 1);
                    event.addChoice("親戚巡りをする", -5, 5, 30000, 1);
                } else if (dice < 0.3) {
                    event = new LifeEvent("【給食】揚げパンが出ました！争奪戦です。");
                    event.addChoice("じゃんけんに参加", 0, 5, 0, 1);
                    event.addChoice("譲って徳を積む", 2, -2, 0, 1);
                } else if (dice < 0.45) {
                    event = new LifeEvent("【夏休み】ラジオ体操に行く時間です。");
                    event.addChoice("早起きして行く", 5, 0, 500, 1);
                    event.addChoice("寝坊する", 0, -5, 0, 1);
                } else if (dice < 0.6) {
                    event = new LifeEvent("【掃除】掃除の時間にホウキでチャンバラごっこ。");
                    event.addChoice("先生にバレて怒られる", 0, 10, 0, 1);
                    event.addChoice("華麗に勝利する", 2, -5, 0, 1);
                } else if (dice < 0.75) {
                    event = new LifeEvent("【習い事】親にそろばん塾に行けと言われました。");
                    event.addChoice("真面目に通う", -5, 5, 0, 1);
                    event.addChoice("サボって公園へ", 5, -5, 0, 1);
                } else if (dice < 0.9) {
                    event = new LifeEvent("【秘密基地】森の中に秘密基地を作りました。");
                    event.addChoice("お菓子を持ち寄る", 5, -10, 0, 1);
                    event.addChoice("友達と遊ぶ", 5, -5, 0, 1);
                } else {
                    // 【追加】お小遣いイベント
                    event = new LifeEvent("【お手伝い】家のお手伝いをしました。");
                    event.addChoice("お皿洗いを頑張る", 0, 0, 500, 1);
                    event.addChoice("お風呂掃除をする", -2, 0, 1000, 1);
                }
            }

            // ==============================================
            // 13-15歳: 中学生 (status="中学生")
            // ==============================================
        } else if (age <= 15) {
            if (age == 15) {
                // 高校進学分岐イベント
                event = new LifeEvent("【分岐点】中学3年生。進路を決める時です。");
                event.addChoice("進学校を受験する", -10, 20, -100000, 1, "高校生");
                event.addChoice("地元の工業高校へ", -5, 5, -50000, 1, "高校生");
                event.addChoice("中卒で働く（鳶職）", -20, 30, 0, 1, "社会人"); // ここで社会人ルートへ
            } else {
                // ランダム中学生イベント
                if (dice < 0.25) {
                    event = new LifeEvent("【部活】顧問が厳しすぎて辞めたいです。");
                    event.addChoice("耐えてレギュラー", -10, 10, 0, 1);
                    event.addChoice("幽霊部員になる", 5, -5, 0, 1);
                } else if (dice < 0.5) {
                    event = new LifeEvent("【中二病】右腕が疼くような気がします。");
                    event.addChoice("包帯を巻いて登校", 0, -10, -500, 1); // 満足度は高い
                    event.addChoice("黒歴史になるので我慢", 0, 10, 0, 1);
                } else if (dice < 0.75) {
                    event = new LifeEvent("【試験】定期テストの前日です。");
                    event.addChoice("徹夜で詰め込む", -10, 20, 0, 1);
                    event.addChoice("諦めて寝る", 5, -5, 0, 1);
                } else {
                    event = new LifeEvent("【恋愛】隣の席の人と消しゴムを貸し借りしました。");
                    event.addChoice("意識してしまう", 0, 10, 0, 1);
                    event.addChoice("ただの事務作業", 0, 0, 0, 1);
                }
            }

            // ==============================================
            // 16-18歳: 高校生 (status="高校生")
            // ※中卒社会人の場合はここはスキップされる
            // ==============================================
        } else if (age <= 18) {
            // もし中卒で社会人になっていたら、社会人イベントへ飛ばすための処理
            if (status.equals("社会人")) {
                return getAdultEvent(age, dice);
            }

            if (age == 18) {
                // 大学進学分岐イベント
                event = new LifeEvent("【進路選択】高校3年生。人生の大きな岐路です。");
                event.addChoice("大学進学を目指す", -20, 30, -1000000, 1, "大学生");
                event.addChoice("専門学校へ行く", -10, 10, -500000, 1, "専門学生");
                event.addChoice("就職する", 0, 10, 0, 1, "社会人");
            } else {
                // ランダム高校生イベント
                if (dice < 0.15) {
                    // 【追加】バイト大成功
                    event = new LifeEvent("【バイト】今月のシフトが増えました！");
                    event.addChoice("たくさん働いて稼ぐ", -10, 10, 80000, 1);
                    event.addChoice("ほどほどにする", -5, 5, 40000, 1);
                } else if (dice < 0.3) {
                    event = new LifeEvent("【バイト】放課後に内緒でバイトを始めました。");
                    event.addChoice("コンビニで働く", -5, 5, 50000, 1);
                    event.addChoice("先生に見つかる", -5, 20, 0, 1);
                } else if (dice < 0.45) {
                    event = new LifeEvent("【修学旅行】夜の恋バナで盛り上がっています。");
                    event.addChoice("好きな人を暴露", 0, -10, 0, 1);
                    event.addChoice("先生が巡回に来た", 0, 10, 0, 1);
                } else if (dice < 0.6) {
                    event = new LifeEvent("【通学】自転車通学中にパンクしました。");
                    event.addChoice("遅刻して歩く", -5, 5, 0, 1);
                    event.addChoice("親を呼ぶ", 0, -5, 0, 1);
                } else if (dice < 0.75) {
                    event = new LifeEvent("【赤点】追試の危機です。");
                    event.addChoice("先生に土下座", 0, 20, 0, 1);
                    event.addChoice("友人に教えてもらう", -5, 5, -1000, 1);
                } else if (dice < 0.9) {
                    event = new LifeEvent("【文化祭】クラスTシャツを作ることになりました。");
                    event.addChoice("デザイン係に立候補", -10, 10, 0, 1);
                    event.addChoice("面倒なので任せる", 0, 0, -2000, 1);
                } else {
                    // 【追加】お年玉（高校生版）
                    event = new LifeEvent("【お正月】親戚からお年玉をもらいました。");
                    event.addChoice("貯金する", 0, -5, 20000, 1);
                    event.addChoice("すぐ使う", 5, -10, 5000, 1);
                }
            }

            // ==============================================
            // 19-22歳: 大学生/専門学生/社会人 (ステータス分岐)
            // ==============================================
        } else if (age <= 22) {
            if (status.equals("社会人")) {
                // 高卒・中卒社会人ルート
                return getAdultEvent(age, dice);
            } else if (status.equals("大学生") || status.equals("専門学生")) {
                // 学生ルート
                if (age == 22 && status.equals("大学生")) {
                    event = new LifeEvent("【卒業】大学生活も終わりです。いよいよ社会へ。");
                    event.addChoice("大手企業に入社", -10, 20, 0, 1, "社会人");
                    event.addChoice("留年する", 10, -20, -1000000, 1, "大学生"); // ステータス維持
                    event.addChoice("起業する", -20, 40, -500000, 1, "社長");
                } else {
                    if (dice < 0.25) {
                        event = new LifeEvent("【サークル】飲み会のコールが止まりません。");
                        event.addChoice("一気飲みする", -20, -10, -5000, 1); // 急性アルコールの危険
                        event.addChoice("ウーロン茶で逃げる", 5, 5, -3000, 1);
                    } else if (dice < 0.5) {
                        event = new LifeEvent("【講義】1限の必修科目に遅刻しそうです。");
                        event.addChoice("ダッシュで行く", -5, 10, 0, 1);
                        event.addChoice("諦めて二度寝", 10, -20, 0, 1);
                    } else if (dice < 0.65) {
                        // 【追加】バイト大成功（大学生版）
                        event = new LifeEvent("【バイト】時給の良いバイトを見つけました！");
                        event.addChoice("居酒屋の深夜帯", -15, 15, 150000, 1);
                        event.addChoice("家庭教師", -5, 5, 100000, 1);
                    } else if (dice < 0.8) {
                        event = new LifeEvent("【合コン】他大学との合コンに参加しました。");
                        event.addChoice("盛り上げ役に徹する", -5, 10, -5000, 1);
                        event.addChoice("運命の人を探す", 0, 20, -5000, 1);
                    } else {
                        event = new LifeEvent("【レポート】締め切りまであと1時間です。");
                        event.addChoice("コピペで凌ぐ", 0, 30, 0, 1);
                        event.addChoice("教授に土下座メール", 0, 20, 0, 1);
                    }
                }
            } else {
                // その他の場合（留年ループなど）
                event = new LifeEvent("【モラトリアム】自分は何者なのでしょうか。");
                event.addChoice("旅に出る", 0, -10, -100000, 1, "フリーター");
                event.addChoice("実家でゴロゴロ", 5, 0, 0, 1, "ニート");
            }

            // ==============================================
            // 23-59歳: 社会人・大人時代 (汎用プール)
            // ==============================================
        } else if (age <= 59) {
            return getAdultEvent(age, dice);

            // ==============================================
            // 60-99歳: 老後
            // ==============================================
        } else {
            return getSeniorEvent(age, dice);
        }

        return event;
    }

    // 社会人・大人時代のイベントプール（数が多いのでメソッド分離）
    private LifeEvent getAdultEvent(int age, double dice) {
        LifeEvent event;

        // 年代別補正
        if (age < 30) { // 若手社員時代
            if (dice < 0.2) {
                event = new LifeEvent("【新人】電話対応が怖くて出られません。");
                event.addChoice("勇気を出して取る", -2, 10, 0, 1);
                event.addChoice("トイレに逃げ込む", 0, 5, 0, 1);
            } else if (dice < 0.4) {
                event = new LifeEvent("【給料日】お給料が入りました！");
                event.addChoice("親にプレゼント", 0, -10, -30000, 1);
                event.addChoice("全部趣味に使う", 5, -5, -200000, 1);
            } else if (dice < 0.6) {
                event = new LifeEvent("【ミス】発注数を一桁間違えました。");
                event.addChoice("正直に報告", -5, 30, 0, 1);
                event.addChoice("自腹で隠蔽工作", 0, 50, -100000, 1);
            } else if (dice < 0.8) {
                event = new LifeEvent("【同期】同期が先に昇進しました。");
                event.addChoice("祝ってあげる", 0, 10, -5000, 1);
                event.addChoice("嫉妬で狂う", -5, 20, 0, 1);
            } else {
                event = new LifeEvent("【合コン】「医者・弁護士限定」の会に潜入しました。");
                event.addChoice("見栄を張る", 0, 15, -10000, 1);
                event.addChoice("聞き役に回る", 0, 0, -5000, 1);
            }
        } else if (age < 45) { // 中堅時代
            if (dice < 0.12) {
                // 【追加】ボーナス支給
                event = new LifeEvent("【ボーナス】夏のボーナスが支給されました！");
                event.addChoice("全額貯金", 0, -5, 500000, 1);
                event.addChoice("欲しいものを買う", 5, -10, 200000, 1);
                event.addChoice("投資に回す", 0, 10, 300000, 1);
            } else if (dice < 0.22) {
                event = new LifeEvent("【結婚】そろそろ身を固める時期です。");
                event.addChoice("婚活アプリに課金", 0, 5, -50000, 1, "既婚者");
                event.addChoice("独身貴族を貫く", 5, -5, 0, 1, "独身");
            } else if (dice < 0.35) {
                event = new LifeEvent("【住宅】マイホームの購入を検討しています。");
                event.addChoice("35年ローン地獄", 0, 40, -30000000, 1);
                event.addChoice("一生賃貸派", 0, 0, -1000000, 1);
            } else if (dice < 0.45) {
                event = new LifeEvent("【中間管理職】上司と部下の板挟みです。");
                event.addChoice("胃薬を飲む", 0, -5, -1000, 1);
                event.addChoice("部下にキレる", 0, 20, 0, 1);
            } else if (dice < 0.6) {
                event = new LifeEvent("【健康診断】メタボ判定を受けました。");
                event.addChoice("ジムに通う", 5, -5, -100000, 1);
                event.addChoice("ベルトを緩める", -5, 0, 0, 1);
            } else if (dice < 0.75) {
                event = new LifeEvent("【転勤】海外赴任の話が来ました。");
                event.addChoice("栄転を受け入れる", -10, 30, 1000000, 1);
                event.addChoice("家庭の事情で断る", 0, -5, 0, 1);
            } else {
                event = new LifeEvent("【同窓会】昔好きだった人に再会しました。");
                event.addChoice("二次会に誘う", -5, 20, -20000, 1);
                event.addChoice("遠くから見つめる", 0, 5, -10000, 1);
            }
        } else { // ベテラン時代
            if (dice < 0.2) {
                event = new LifeEvent("【役職定年】役職を外され給料が下がりました。");
                event.addChoice("窓際族を楽しむ", 5, -20, 0, 1);
                event.addChoice("プライドが許さない", -10, 30, 0, 1);
            } else if (dice < 0.4) {
                event = new LifeEvent("【子供】子供が結婚資金を借りに来ました。");
                event.addChoice("快く貸す", 0, -10, -2000000, 1);
                event.addChoice("自分たちでやれと突き返す", 0, 10, 0, 1);
            } else if (dice < 0.6) {
                event = new LifeEvent("【病気】人間ドックで要精密検査が出ました。");
                event.addChoice("即入院する", 10, 10, -500000, 1);
                event.addChoice("民間療法に頼る", -20, 0, -100000, 1);
            } else if (dice < 0.8) {
                event = new LifeEvent("【趣味】そば打ちにハマり始めました。");
                event.addChoice("道具を全部揃える", 0, -10, -300000, 1);
                event.addChoice("家族に無理やり振る舞う", 0, 5, 0, 1);
            } else {
                event = new LifeEvent("【早期退職】退職金の割り増しオファーがあります。");
                event.addChoice("応募してリタイア", 5, -30, 20000000, 1, "無職");
                event.addChoice("定年までしがみつく", -5, 10, 0, 1);
            }
        }
        return event;
    }

    // 【追加】借金取り立てイベント
    private LifeEvent getDebtEvent(long money) {
        LifeEvent event;
        if (money <= -10000000) {
            // 1000万円以上の借金
            event = new LifeEvent("【闇金】怖い人たちが家に押しかけてきました...");
            event.addChoice("土下座して待ってもらう", -20, 50, 0, 1);
            event.addChoice("夜逃げする", -30, 30, 0, 1, "逃亡者");
            event.addChoice("臓器を売る（嘘）", -10, 40, 500000, 1);
        } else {
            // 500万円以上の借金
            event = new LifeEvent("【督促】消費者金融から督促状が届きました。");
            event.addChoice("必死に働いて返す", -15, 30, 200000, 1);
            event.addChoice("親に泣きつく", 0, 20, 1000000, 1);
            event.addChoice("無視する（悪手）", 0, 40, 0, 1);
        }
        return event;
    }

    // 【追加】金運イベント（宝くじ、投資、お年玉など）
    private LifeEvent getLuckyMoneyEvent(int age, String status) {
        LifeEvent event;
        double subDice = random();

        // 年齢によって発生するイベントを分岐
        if (age <= 12) {
            // 子供向け：お年玉
            event = new LifeEvent("【幸運】お年玉を沢山もらえる年でした！");
            event.addChoice("貯金箱に入れる", 0, -5, 15000, 1);
            event.addChoice("ゲームソフトを買う", 5, -10, 5000, 1);
        } else if (age <= 22) {
            // 学生向け：バイト大成功 or 臨時収入
            if (subDice < 0.5) {
                event = new LifeEvent("【臨時収入】親戚からお小遣いをもらいました！");
                event.addChoice("ありがたく受け取る", 0, -5, 50000, 1);
                event.addChoice("遠慮する(嘘)", 0, 0, 30000, 1);
            } else {
                event = new LifeEvent("【バイト】店長に気に入られてボーナスが出た！");
                event.addChoice("嬉しい！", 5, -10, 30000, 1);
                event.addChoice("もっと欲しい...", 0, 5, 30000, 1);
            }
        } else {
            // 大人向け：宝くじ or 投資成功
            if (subDice < 0.4) {
                // 宝くじ
                event = new LifeEvent("【宝くじ】なんと宝くじが当たりました！");
                event.addChoice("3等当選！", 10, -20, 1000000, 1);
                event.addChoice("末等だった...", 0, 5, 3000, 1);
            } else if (subDice < 0.7) {
                // 投資成功
                event = new LifeEvent("【投資】買っていた株が急騰しました！");
                event.addChoice("今すぐ売る", 0, -10, 500000, 1);
                event.addChoice("まだ持っておく", 0, 20, 0, 1);
            } else {
                // ボーナス
                event = new LifeEvent("【臨時ボーナス】会社の業績が良く臨時ボーナスが出ました！");
                event.addChoice("家族サービスに使う", 5, -10, 200000, 1);
                event.addChoice("自分へのご褒美", 10, -5, 100000, 1);
            }
        }
        return event;
    }

    // 老後のイベントプール
    private LifeEvent getSeniorEvent(int age, double dice) {
        LifeEvent event;
        if (dice < 0.2) {
            event = new LifeEvent("【孫】孫にお年玉をせがまれます。");
            event.addChoice("見栄を張って1万円", 0, -5, -10000, 1);
            event.addChoice("500円玉を渡す", 0, 5, -500, 1);
        } else if (dice < 0.4) {
            event = new LifeEvent("【詐欺】怪しいリフォーム業者が来ました。");
            event.addChoice("契約してしまう", 0, 20, -5000000, 1);
            event.addChoice("塩を撒いて追い返す", -2, -5, 0, 1);
        } else if (dice < 0.6) {
            event = new LifeEvent("【病院】待合室が社交場になっています。");
            event.addChoice("病気自慢をする", 0, -5, -2000, 1);
            event.addChoice("若い看護師と話す", 5, -5, 0, 1);
        } else if (dice < 0.8) {
            event = new LifeEvent("【終活】エンディングノートを書き始めます。");
            event.addChoice("遺産は全て寄付", 0, -10, 0, 1);
            event.addChoice("恨みつらみを書き連ねる", -5, 10, 0, 1);
        } else {
            event = new LifeEvent("【徘徊】散歩に出たら帰り道がわからなくなりました。");
            event.addChoice("警察に保護される", 0, 10, 0, 1);
            event.addChoice("勘で歩く", -10, 20, 0, 1);
        }
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
        return logic.getEventForAge(player);
    }

    public DeathResult applyChoice(Choice choice, String eventTitle) {
        // 0. ステータス更新（もしあれば）
        if (choice.nextStatus != null) {
            player.setStatus(choice.nextStatus);
        }

        // 1. 履歴に追加
        player.addHistory(eventTitle, choice.text);

        // 2. ステータス反映
        player.changeHealth(choice.healthDelta);
        player.changeStress(choice.stressDelta);
        player.changeMoney(choice.moneyDelta);

        // 3. 生存チェック
        player.checkVitality();

        if (!player.isAlive()) {
            String reason = (player.getHealth() <= 0) ? "病死・衰弱死" : "ストレス死";
            return new DeathResult(
                    DeathResult.Type.DEAD,
                    reason,
                    "志半ばで力尽きました...",
                    player.getAge());
        }

        // 4. 年齢を加算
        player.incrementAge(choice.ageDelta);

        return new DeathResult(DeathResult.Type.ALIVE, "", "", player.getAge());
    }
}

/*
 * ==========================================
 * VIEW (Enhanced UI)
 * ==========================================
 */
class GameView extends JFrame {
    private final JLabel ageLabel = new JLabel();
    private final JLabel moneyLabel = new JLabel();
    private final JLabel statusLabel = new JLabel(); // ステータス表示追加
    private final JProgressBar healthBar = new JProgressBar(0, 100);
    private final JProgressBar stressBar = new JProgressBar(0, 100);
    private final JTextArea eventArea = new JTextArea();
    private final JPanel buttonPanel = new JPanel();

    private final Color BG_COLOR = new Color(40, 44, 52);
    private final Color TEXT_COLOR = new Color(220, 223, 228);
    private final Color ACCENT_COLOR = new Color(97, 175, 239);

    public GameView(ActionListener listener) {
        setTitle("人生100年サバイバル (分岐強化版)");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel infoPanel = new JPanel(new GridLayout(2, 2)); // グリッド変更
        infoPanel.setBackground(BG_COLOR);

        ageLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        ageLabel.setForeground(ACCENT_COLOR);

        moneyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        moneyLabel.setForeground(Color.YELLOW);
        moneyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(Color.LIGHT_GRAY);

        infoPanel.add(ageLabel);
        infoPanel.add(moneyLabel);
        infoPanel.add(statusLabel); // ステータス追加
        infoPanel.add(new JLabel(""));

        topPanel.add(infoPanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(createBarPanel("体力", healthBar, new Color(46, 204, 113)));
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(createBarPanel("ストレス", stressBar, new Color(231, 76, 60)));

        add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        eventArea.setEditable(false);
        eventArea.setLineWrap(true);
        eventArea.setWrapStyleWord(true);
        eventArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        eventArea.setBackground(new Color(60, 64, 72));
        eventArea.setForeground(TEXT_COLOR);
        eventArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        centerPanel.add(eventArea, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM PANEL ---
        buttonPanel.setLayout(new GridLayout(0, 1, 10, 10));
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createBarPanel(String labelText, JProgressBar bar, Color color) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(BG_COLOR);
        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(60, 20));
        bar.setStringPainted(true);
        bar.setForeground(color);
        bar.setBackground(Color.DARK_GRAY);
        p.add(label, BorderLayout.WEST);
        p.add(bar, BorderLayout.CENTER);
        return p;
    }

    public void updateDisplay(Player player) {
        ageLabel.setText(player.getAge() + "歳");
        moneyLabel.setText(String.format("%,d円", player.getMoney()));
        statusLabel.setText("身分: " + player.getStatus());

        healthBar.setValue(player.getHealth());
        healthBar.setString(player.getHealth() + "/100");
        stressBar.setValue(player.getStress());
        stressBar.setString(player.getStress() + "/100");
    }

    public void showEvent(LifeEvent event, ActionListener listener) {
        eventArea.setText(event.getText());
        buttonPanel.removeAll();
        List<Choice> choices = event.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            JButton b = new JButton(choices.get(i).getText());
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.setFocusPainted(false);
            b.setBackground(new Color(240, 240, 240));
            b.setForeground(Color.BLACK);
            b.setPreferredSize(new Dimension(100, 45));
            b.setActionCommand(String.valueOf(i));
            b.addActionListener(listener);
            buttonPanel.add(b);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void showGameOver(String title, String message, int age, Player player, Runnable onRestart) {
        new ResultView(title, message, age, player, onRestart);
        dispose(); // ゲーム画面を閉じる
    }
}

/*
 * ==========================================
 * RESULT VIEW (結果画面)
 * ==========================================
 */
class ResultView extends JFrame {
    private final Color BG_COLOR = new Color(25, 28, 35);
    private final Color TEXT_COLOR = new Color(220, 223, 228);
    private final Color ACCENT_COLOR = new Color(231, 76, 60); // 赤 (GameOver用)
    private final Color LEGEND_COLOR = new Color(241, 196, 15); // 金 (大往生用)

    public ResultView(String title, String message, int age, Player player, Runnable onRestart) {
        setTitle("結果発表");
        setSize(550, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        Color titleColor = ("伝説のエンド".equals(title) || "大往生".equals(title)) ? LEGEND_COLOR : ACCENT_COLOR;

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ageLabel = new JLabel("享年 " + age + " 歳");
        ageLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        ageLabel.setForeground(Color.WHITE);
        ageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel(
                "<html><div style='text-align: center; width: 400px;'>" + message + "</div></html>");
        msgLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        msgLabel.setForeground(Color.LIGHT_GRAY);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(ageLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(msgLabel);

        // --- HISTORY LOG (CENTER) ---
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(BG_COLOR);
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

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
        logArea.setCaretPosition(0); // 先頭を表示

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        historyPanel.add(logTitle, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER (BUTTONS) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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
        add(historyPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}

/*
 * ==========================================
 * TITLE VIEW (タイトル画面)
 * ==========================================
 */
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

/*
 * ==========================================
 * CONTROLLER
 * ==========================================
 */
class GameController implements ActionListener {
    private GameModel model;
    private GameView view;
    private LifeEvent currentEvent;

    public GameController() {
        // タイトル画面を表示
        showTitleScreen();
    }

    private void showTitleScreen() {
        new TitleView(() -> {
            // ゲーム開始時の処理
            model = new GameModel();
            view = new GameView(this);
            nextTurn();
        });
    }

    private void nextTurn() {
        view.updateDisplay(model.getPlayer());
        currentEvent = model.nextEvent();
        view.showEvent(currentEvent, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = Integer.parseInt(e.getActionCommand());
        if (index < 0 || index >= currentEvent.getChoices().size())
            return;

        Choice selected = currentEvent.getChoices().get(index);
        DeathResult result = model.applyChoice(selected, currentEvent.getTitle());

        if (result.type == DeathResult.Type.ALIVE) {
            nextTurn();
        } else {
            view.updateDisplay(model.getPlayer());
            // Restart用のコールバックを渡す
            view.showGameOver(result.title, result.message, result.age, model.getPlayer(), () -> {
                showTitleScreen();
            });
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
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback
        }
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}