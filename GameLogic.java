import java.util.ArrayList;
import java.util.List;

class GameLogic {
    private double random() {
        return Math.random();
    }

    public LifeEvent getEventForAge(Player player) {
        LifeEvent event = null;
        for (int i = 0; i < 8; i++) {
            event = getEventForAgeOnce(player);
            if (event == null) {
                continue;
            }
            if (!player.hasSeenEvent(event.getTitle())) {
                return event;
            }
        }
        return event;
    }

    private LifeEvent getEventForAgeOnce(Player player) {
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
        // ステータス分岐イベント（中確率で発生）
        // ==============================================
        if (age >= 18 && dice < 0.25) {
            LifeEvent statusEvent = getStatusEvent(player);
            if (statusEvent != null) {
                return statusEvent;
            }
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
                // ランダム幼児イベント（増量）
                List<LifeEvent> pool = new ArrayList<>();
                event = new LifeEvent("【発見】ダンゴムシを見つけました。");
                event.addChoice("ポケットに入れる", 0, -5, 0, 1);
                event.addChoice("食べる", -10, 0, 0, 1);
                pool.add(event);

                event = new LifeEvent("【おつかい】初めてのおつかいを頼まれました。");
                event.addChoice("無事に買って帰る", 5, 5, 0, 1);
                event.addChoice("道草して迷子", 0, 10, 0, 1);
                pool.add(event);

                event = new LifeEvent("【昼寝】お昼寝の時間です。");
                event.addChoice("ぐっすり寝る", 10, -10, 0, 1);
                event.addChoice("寝たふりして遊ぶ", -5, 5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【ヒーロー】テレビのヒーローごっこをしています。");
                event.addChoice("高い所からジャンプ", -10, 5, 0, 1);
                event.addChoice("必殺技を叫ぶ", 2, -2, 0, 1);
                pool.add(event);

                event = new LifeEvent("【おやつ】おやつのプリンが冷蔵庫にあります。");
                event.addChoice("こっそり食べる", 0, 5, 0, 1);
                event.addChoice("我慢して褒められる", 5, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【遊び場】公園のブランコが空いています。");
                event.addChoice("全力で漕ぐ", 2, -2, 0, 1);
                event.addChoice("砂場に移動", 1, -1, 0, 1);
                pool.add(event);

                event = new LifeEvent("【雨】長靴で水たまりに突入しました。");
                event.addChoice("バシャバシャする", -2, 5, 0, 1);
                event.addChoice("濡れるのが嫌で戻る", 1, -1, 0, 1);
                pool.add(event);

                event = new LifeEvent("【発熱】少し熱っぽい気がします。");
                event.addChoice("早めに寝る", 5, -5, 0, 1);
                event.addChoice("元気アピールで遊ぶ", -5, 5, 0, 1);
                pool.add(event);

                event = pickEvent(player, pool);
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
                // ランダム小学生イベント（増量）
                List<LifeEvent> pool = new ArrayList<>();
                event = new LifeEvent("【お正月】親戚が集まりました。お年玉タイム！");
                event.addChoice("おじいちゃんにおねだり", 0, -5, 10000, 1);
                event.addChoice("親戚巡りをする", -5, 5, 30000, 1);
                pool.add(event);

                event = new LifeEvent("【給食】揚げパンが出ました！争奪戦です。");
                event.addChoice("じゃんけんに参加", 0, 5, 0, 1);
                event.addChoice("譲って徳を積む", 2, -2, 0, 1);
                pool.add(event);

                event = new LifeEvent("【夏休み】ラジオ体操に行く時間です。");
                event.addChoice("早起きして行く", 5, 0, 500, 1);
                event.addChoice("寝坊する", 0, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【掃除】掃除の時間にホウキでチャンバラごっこ。");
                event.addChoice("先生にバレて怒られる", 0, 10, 0, 1);
                event.addChoice("華麗に勝利する", 2, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【習い事】親にそろばん塾に行けと言われました。");
                event.addChoice("真面目に通う", -5, 5, 0, 1);
                event.addChoice("サボって公園へ", 5, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【秘密基地】森の中に秘密基地を作りました。");
                event.addChoice("お菓子を持ち寄る", 5, -10, 0, 1);
                event.addChoice("友達と遊ぶ", 5, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【お手伝い】家のお手伝いをしました。");
                event.addChoice("お皿洗いを頑張る", 0, 0, 500, 1);
                event.addChoice("お風呂掃除をする", -2, 0, 1000, 1);
                pool.add(event);

                event = new LifeEvent("【図書室】好きな本を見つけました。");
                event.addChoice("毎日通って読む", 5, -5, 0, 1);
                event.addChoice("マンガだけ読む", 0, 5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【運動会】リレーの選手に選ばれました。");
                event.addChoice("全力で走る", -2, 5, 0, 1);
                event.addChoice("ケガが怖いので控えめ", 2, -2, 0, 1);
                pool.add(event);

                event = new LifeEvent("【校外学習】遠足でお弁当が楽しみです。");
                event.addChoice("早起きして手伝う", 3, -3, 0, 1);
                event.addChoice("朝寝坊で怒られる", -2, 5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【テスト】漢字テストの結果が返ってきました。");
                event.addChoice("くやしくて練習する", 2, -3, 0, 1);
                event.addChoice("気にしない", 0, 3, 0, 1);
                pool.add(event);

                event = new LifeEvent("【ゲーム】新しいゲームが欲しいです。");
                event.addChoice("貯金して買う", 2, -2, -3000, 1);
                event.addChoice("おねだりする", 0, 4, 0, 1);
                pool.add(event);

                event = pickEvent(player, pool);
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
                // ランダム中学生イベント（増量）
                List<LifeEvent> pool = new ArrayList<>();
                event = new LifeEvent("【部活】顧問が厳しすぎて辞めたいです。");
                event.addChoice("耐えてレギュラー", -10, 10, 0, 1);
                event.addChoice("幽霊部員になる", 5, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【中二病】右腕が疼くような気がします。");
                event.addChoice("包帯を巻いて登校", 0, -10, -500, 1);
                event.addChoice("黒歴史になるので我慢", 0, 10, 0, 1);
                pool.add(event);

                event = new LifeEvent("【試験】定期テストの前日です。");
                event.addChoice("徹夜で詰め込む", -10, 20, 0, 1);
                event.addChoice("諦めて寝る", 5, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【恋愛】隣の席の人と消しゴムを貸し借りしました。");
                event.addChoice("意識してしまう", 0, 10, 0, 1);
                event.addChoice("ただの事務作業", 0, 0, 0, 1);
                pool.add(event);

                event = new LifeEvent("【スマホ】初めてのスマホが欲しいです。");
                event.addChoice("親を説得する", 0, 5, 0, 1);
                event.addChoice("我慢して勉強する", 5, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【文化祭】出し物で出番が回ってきました。");
                event.addChoice("目立って盛り上げる", -5, 10, 0, 1);
                event.addChoice("裏方に徹する", 2, -2, 0, 1);
                pool.add(event);

                event = new LifeEvent("【SNS】クラスのグループチャットが荒れています。");
                event.addChoice("仲裁に入る", -3, 8, 0, 1);
                event.addChoice("既読スルーする", 2, -1, 0, 1);
                pool.add(event);

                event = new LifeEvent("【部屋】勉強机の片付けを命じられました。");
                event.addChoice("片付けて集中", 5, -5, 0, 1);
                event.addChoice("適当にごまかす", -2, 5, 0, 1);
                pool.add(event);

                event = pickEvent(player, pool);
            }

            // ==============================================
            // 16-18歳: 高校生 (status="高校生")
            // ※中卒社会人の場合はここはスキップされる
            // ==============================================
        } else if (age <= 18) {
            // もし中卒で社会人になっていたら、社会人イベントへ飛ばすための処理
            if (status.equals("社会人")) {
                return getAdultEvent(player);
            }

            if (age == 18) {
                // 大学進学分岐イベント
                event = new LifeEvent("【進路選択】高校3年生。人生の大きな岐路です。");
                event.addChoice("大学進学を目指す", -20, 30, -1000000, 1, "大学生");
                event.addChoice("専門学校へ行く", -10, 10, -500000, 1, "専門学生");
                event.addChoice("就職する", 0, 10, 0, 1, "社会人");
            } else {
                // ランダム高校生イベント（増量）
                List<LifeEvent> pool = new ArrayList<>();
                event = new LifeEvent("【バイト】今月のシフトが増えました！");
                event.addChoice("たくさん働いて稼ぐ", -10, 10, 80000, 1);
                event.addChoice("ほどほどにする", -5, 5, 40000, 1);
                pool.add(event);

                event = new LifeEvent("【バイト】放課後に内緒でバイトを始めました。");
                event.addChoice("コンビニで働く", -5, 5, 50000, 1);
                event.addChoice("先生に見つかる", -5, 20, 0, 1);
                pool.add(event);

                event = new LifeEvent("【修学旅行】夜の恋バナで盛り上がっています。");
                event.addChoice("好きな人を暴露", 0, -10, 0, 1);
                event.addChoice("先生が巡回に来た", 0, 10, 0, 1);
                pool.add(event);

                event = new LifeEvent("【通学】自転車通学中にパンクしました。");
                event.addChoice("遅刻して歩く", -5, 5, 0, 1);
                event.addChoice("親を呼ぶ", 0, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【赤点】追試の危機です。");
                event.addChoice("先生に土下座", 0, 20, 0, 1);
                event.addChoice("友人に教えてもらう", -5, 5, -1000, 1);
                pool.add(event);

                event = new LifeEvent("【文化祭】クラスTシャツを作ることになりました。");
                event.addChoice("デザイン係に立候補", -10, 10, 0, 1);
                event.addChoice("面倒なので任せる", 0, 0, -2000, 1);
                pool.add(event);

                event = new LifeEvent("【お正月】親戚からお年玉をもらいました。");
                event.addChoice("貯金する", 0, -5, 20000, 1);
                event.addChoice("すぐ使う", 5, -10, 5000, 1);
                pool.add(event);

                event = new LifeEvent("【部活】大会直前で練習がハードです。");
                event.addChoice("限界まで追い込む", -10, 15, 0, 1);
                event.addChoice("身体を労わる", 5, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【進路相談】担任との面談がありました。");
                event.addChoice("現実的に考える", 3, 0, 0, 1);
                event.addChoice("夢を語る", -2, 5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【模試】偏差値が思ったより低いです。");
                event.addChoice("危機感で勉強", -5, 10, 0, 1);
                event.addChoice("気分転換で遊ぶ", 5, -5, 0, 1);
                pool.add(event);

                event = new LifeEvent("【スマホ】通信量が足りません。");
                event.addChoice("節約モードで我慢", 2, -2, 0, 1);
                event.addChoice("課金して増やす", 0, 5, -3000, 1);
                pool.add(event);

                event = new LifeEvent("【告白】文化祭の後夜祭で告白しようか迷います。");
                event.addChoice("告白する", -5, 15, 0, 1);
                event.addChoice("踏みとどまる", 3, -2, 0, 1);
                pool.add(event);

                event = pickEvent(player, pool);
            }

            // ==============================================
            // 19-22歳: 大学生/専門学生/社会人 (ステータス分岐)
            // ==============================================
        } else if (age <= 22) {
            if (status.equals("社会人")) {
                // 高卒・中卒社会人ルート
                return getAdultEvent(player);
            } else if (status.equals("大学生") || status.equals("専門学生")) {
                // 学生ルート
                if (age == 22 && status.equals("大学生")) {
                    event = new LifeEvent("【卒業】大学生活も終わりです。いよいよ社会へ。");
                    event.addChoice("大手企業に入社", -10, 20, 0, 1, "社会人");
                    event.addChoice("留年する", 10, -20, -1000000, 1, "大学生"); // ステータス維持
                    event.addChoice("起業する", -20, 40, -500000, 1, "社長");
                } else {
                    List<LifeEvent> pool = new ArrayList<>();
                    event = new LifeEvent("【サークル】飲み会のコールが止まりません。");
                    event.addChoice("一気飲みする", -20, -10, -5000, 1);
                    event.addChoice("ウーロン茶で逃げる", 5, 5, -3000, 1);
                    pool.add(event);

                    event = new LifeEvent("【講義】1限の必修科目に遅刻しそうです。");
                    event.addChoice("ダッシュで行く", -5, 10, 0, 1);
                    event.addChoice("諦めて二度寝", 10, -20, 0, 1);
                    pool.add(event);

                    event = new LifeEvent("【バイト】時給の良いバイトを見つけました！");
                    event.addChoice("居酒屋の深夜帯", -15, 15, 150000, 1);
                    event.addChoice("家庭教師", -5, 5, 100000, 1);
                    pool.add(event);

                    event = new LifeEvent("【合コン】他大学との合コンに参加しました。");
                    event.addChoice("盛り上げ役に徹する", -5, 10, -5000, 1);
                    event.addChoice("運命の人を探す", 0, 20, -5000, 1);
                    pool.add(event);

                    event = new LifeEvent("【レポート】締め切りまであと1時間です。");
                    event.addChoice("コピペで凌ぐ", 0, 30, 0, 1);
                    event.addChoice("教授に土下座メール", 0, 20, 0, 1);
                    pool.add(event);

                    event = new LifeEvent("【ゼミ】発表の順番が回ってきました。");
                    event.addChoice("堂々と発表する", -5, 10, 0, 1);
                    event.addChoice("原稿を見ながら読む", 2, -2, 0, 1);
                    pool.add(event);

                    event = new LifeEvent("【研究】卒論のテーマが決まりません。");
                    event.addChoice("教授に相談する", -3, 8, 0, 1);
                    event.addChoice("とりあえず仮で決める", 2, -2, 0, 1);
                    pool.add(event);

                    event = new LifeEvent("【就活】説明会の予約が埋まっています。");
                    event.addChoice("早起きして並ぶ", -5, 10, 0, 1);
                    event.addChoice("別の企業を探す", 2, -2, 0, 1);
                    pool.add(event);

                    event = new LifeEvent("【一人暮らし】冷蔵庫が空っぽです。");
                    event.addChoice("節約料理に挑戦", 3, -3, -2000, 1);
                    event.addChoice("コンビニで済ます", -2, 5, -4000, 1);
                    pool.add(event);

                    event = new LifeEvent("【旅行】友達から旅行の誘いが来ました。");
                    event.addChoice("行ってリフレッシュ", 5, -10, -50000, 1);
                    event.addChoice("お金がないので断る", 0, 5, 0, 1);
                    pool.add(event);

                    event = pickEvent(player, pool);
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
            return getAdultEvent(player);

            // ==============================================
            // 60-99歳: 老後
            // ==============================================
        } else {
            return getSeniorEvent(player);
        }

        return event;
    }

    private LifeEvent pickEvent(Player player, List<LifeEvent> pool) {
        if (pool.isEmpty()) {
            return new LifeEvent("【平穏】特に何も起きませんでした。");
        }
        List<LifeEvent> unseen = new ArrayList<>();
        for (LifeEvent ev : pool) {
            if (!player.hasSeenEvent(ev.getTitle())) {
                unseen.add(ev);
            }
        }
        List<LifeEvent> target = unseen.isEmpty() ? pool : unseen;
        int index = (int) Math.floor(random() * target.size());
        return target.get(Math.max(0, Math.min(index, target.size() - 1)));
    }

    private LifeEvent getStatusEvent(Player player) {
        String status = player.getStatus();
        int age = player.getAge();
        long money = player.getMoney();

        List<LifeEvent> pool = new ArrayList<>();
        LifeEvent event;

        if ("社長".equals(status)) {
            event = new LifeEvent("【経営】会社の資金繰りが厳しいです。");
            event.addChoice("追加融資を受ける", -10, 20, 2000000, 1);
            event.addChoice("固定費を削る", 0, 10, 500000, 1);
            pool.add(event);

            event = new LifeEvent("【採用】優秀な人材が応募してきました。");
            event.addChoice("好待遇で即採用", 0, 10, -1000000, 1);
            event.addChoice("慎重に面接する", -2, 5, 0, 1);
            pool.add(event);

            event = new LifeEvent("【上場】上場の打診が来ました。");
            event.addChoice("挑戦する", -10, 30, 5000000, 1);
            event.addChoice("まだ早いと断る", 5, -5, 0, 1);
            pool.add(event);
        } else if ("無職".equals(status) || "ニート".equals(status) || "フリーター".equals(status)) {
            event = new LifeEvent("【再起】職業訓練校の案内が届きました。");
            event.addChoice("通ってスキルをつける", -5, 10, -20000, 1, "社会人");
            event.addChoice("スルーする", 5, -5, 0, 1);
            pool.add(event);

            event = new LifeEvent("【面接】久しぶりの面接で緊張しています。");
            event.addChoice("正直に話す", -2, 10, 0, 1);
            event.addChoice("話を盛る", 0, 15, 0, 1);
            pool.add(event);

            event = new LifeEvent("【日常】生活リズムが崩れてきました。");
            event.addChoice("早寝早起きに戻す", 5, -5, 0, 1);
            event.addChoice("夜型を続ける", -5, 5, 0, 1);
            pool.add(event);
        } else if ("既婚者".equals(status)) {
            event = new LifeEvent("【家庭】家族旅行の計画を立てています。");
            event.addChoice("奮発して温泉旅行", 5, -10, -200000, 1);
            event.addChoice("日帰りで節約", 2, -3, -50000, 1);
            pool.add(event);

            event = new LifeEvent("【家計】家計簿を見直す時期です。");
            event.addChoice("固定費を削減", 0, 5, 100000, 1);
            event.addChoice("我慢できず散財", -2, 10, -100000, 1);
            pool.add(event);
        } else if ("独身".equals(status)) {
            event = new LifeEvent("【独身】趣味に全力投資したい。");
            event.addChoice("高額な趣味に挑戦", 0, 10, -300000, 1);
            event.addChoice("ほどほどに楽しむ", 2, -2, -50000, 1);
            pool.add(event);

            event = new LifeEvent("【出会い】友人から紹介の話が来ました。");
            event.addChoice("会ってみる", -2, 10, -20000, 1);
            event.addChoice("今はいいかな", 2, -2, 0, 1);
            pool.add(event);
        } else if ("逃亡者".equals(status)) {
            event = new LifeEvent("【逃亡】身を隠すために引っ越しが必要です。");
            event.addChoice("遠くに逃げる", -5, 20, -200000, 1);
            event.addChoice("地元で潜む", 0, 15, 0, 1);
            pool.add(event);
        }

        if (money >= 20000000) {
            event = new LifeEvent("【資産家】資産運用の見直しを迫られます。");
            event.addChoice("安全運用に切り替え", 2, -2, 300000, 1);
            event.addChoice("攻めの投資を続行", -3, 12, 800000, 1);
            pool.add(event);
        }

        if (pool.isEmpty()) {
            return null;
        }
        return pickEvent(player, pool);
    }

    // 社会人・大人時代のイベントプール（数が多いのでメソッド分離）
    private LifeEvent getAdultEvent(Player player) {
        LifeEvent event;
        int age = player.getAge();

        // 年代別補正
        if (age < 30) { // 若手社員時代
            List<LifeEvent> pool = new ArrayList<>();
            event = new LifeEvent("【新人】電話対応が怖くて出られません。");
            event.addChoice("勇気を出して取る", -2, 10, 0, 1);
            event.addChoice("トイレに逃げ込む", 0, 5, 0, 1);
            pool.add(event);

            event = new LifeEvent("【給料日】お給料が入りました！");
            event.addChoice("親にプレゼント", 0, -10, -30000, 1);
            event.addChoice("全部趣味に使う", 5, -5, -200000, 1);
            pool.add(event);

            event = new LifeEvent("【ミス】発注数を一桁間違えました。");
            event.addChoice("正直に報告", -5, 30, 0, 1);
            event.addChoice("自腹で隠蔽工作", 0, 50, -100000, 1);
            pool.add(event);

            event = new LifeEvent("【同期】同期が先に昇進しました。");
            event.addChoice("祝ってあげる", 0, 10, -5000, 1);
            event.addChoice("嫉妬で狂う", -5, 20, 0, 1);
            pool.add(event);

            event = new LifeEvent("【合コン】「医者・弁護士限定」の会に潜入しました。");
            event.addChoice("見栄を張る", 0, 15, -10000, 1);
            event.addChoice("聞き役に回る", 0, 0, -5000, 1);
            pool.add(event);

            event = new LifeEvent("【残業】終電が危ない時間になりました。");
            event.addChoice("タクシーで帰る", 0, 5, -15000, 1);
            event.addChoice("頑張って終電", -5, 10, 0, 1);
            pool.add(event);

            event = new LifeEvent("【評価】上司からフィードバックを受けました。");
            event.addChoice("素直に受け止める", 3, -2, 0, 1);
            event.addChoice("反発する", -2, 8, 0, 1);
            pool.add(event);

            event = new LifeEvent("【引っ越し】職場の近くに引っ越すべき？");
            event.addChoice("引っ越して通勤短縮", 5, -5, -200000, 1);
            event.addChoice("今の家で我慢", 0, 5, 0, 1);
            pool.add(event);

            event = new LifeEvent("【資格】資格取得の案内が回ってきました。");
            event.addChoice("勉強して受験", -5, 10, -20000, 1);
            event.addChoice("後回しにする", 2, -2, 0, 1);
            pool.add(event);

            event = pickEvent(player, pool);
        } else if (age < 45) { // 中堅時代
            List<LifeEvent> pool = new ArrayList<>();
            event = new LifeEvent("【ボーナス】夏のボーナスが支給されました！");
            event.addChoice("全額貯金", 0, -5, 500000, 1);
            event.addChoice("欲しいものを買う", 5, -10, 200000, 1);
            event.addChoice("投資に回す", 0, 10, 300000, 1);
            pool.add(event);

            event = new LifeEvent("【結婚】そろそろ身を固める時期です。");
            event.addChoice("婚活アプリに課金", 0, 5, -50000, 1, "既婚者");
            event.addChoice("独身貴族を貫く", 5, -5, 0, 1, "独身");
            pool.add(event);

            event = new LifeEvent("【住宅】マイホームの購入を検討しています。");
            event.addChoice("35年ローン地獄", 0, 40, -30000000, 1);
            event.addChoice("一生賃貸派", 0, 0, -1000000, 1);
            pool.add(event);

            event = new LifeEvent("【中間管理職】上司と部下の板挟みです。");
            event.addChoice("胃薬を飲む", 0, -5, -1000, 1);
            event.addChoice("部下にキレる", 0, 20, 0, 1);
            pool.add(event);

            event = new LifeEvent("【健康診断】メタボ判定を受けました。");
            event.addChoice("ジムに通う", 5, -5, -100000, 1);
            event.addChoice("ベルトを緩める", -5, 0, 0, 1);
            pool.add(event);

            event = new LifeEvent("【転勤】海外赴任の話が来ました。");
            event.addChoice("栄転を受け入れる", -10, 30, 1000000, 1);
            event.addChoice("家庭の事情で断る", 0, -5, 0, 1);
            pool.add(event);

            event = new LifeEvent("【同窓会】昔好きだった人に再会しました。");
            event.addChoice("二次会に誘う", -5, 20, -20000, 1);
            event.addChoice("遠くから見つめる", 0, 5, -10000, 1);
            pool.add(event);

            event = new LifeEvent("【昇進】管理職の話が来ました。");
            event.addChoice("挑戦する", -5, 15, 200000, 1);
            event.addChoice("責任が重いので断る", 2, 0, 0, 1);
            pool.add(event);

            event = new LifeEvent("【家庭】子どもの習い事が増えました。");
            event.addChoice("全力で支援", -3, 8, -50000, 1);
            event.addChoice("ほどほどにする", 2, -2, 0, 1);
            pool.add(event);

            event = new LifeEvent("【投資】周囲で投資ブームが来ています。");
            event.addChoice("少額で始める", 0, 5, -100000, 1);
            event.addChoice("慎重に見送る", 2, -2, 0, 1);
            pool.add(event);

            event = new LifeEvent("【副業】副業解禁の話題が出ています。");
            event.addChoice("スキルで稼ぐ", -5, 10, 150000, 1);
            event.addChoice("本業に集中", 3, -3, 0, 1);
            pool.add(event);

            event = pickEvent(player, pool);
        } else { // ベテラン時代
            List<LifeEvent> pool = new ArrayList<>();
            event = new LifeEvent("【役職定年】役職を外され給料が下がりました。");
            event.addChoice("窓際族を楽しむ", 5, -20, 0, 1);
            event.addChoice("プライドが許さない", -10, 30, 0, 1);
            pool.add(event);

            event = new LifeEvent("【子供】子供が結婚資金を借りに来ました。");
            event.addChoice("快く貸す", 0, -10, -2000000, 1);
            event.addChoice("自分たちでやれと突き返す", 0, 10, 0, 1);
            pool.add(event);

            event = new LifeEvent("【病気】人間ドックで要精密検査が出ました。");
            event.addChoice("即入院する", 10, 10, -500000, 1);
            event.addChoice("民間療法に頼る", -20, 0, -100000, 1);
            pool.add(event);

            event = new LifeEvent("【趣味】そば打ちにハマり始めました。");
            event.addChoice("道具を全部揃える", 0, -10, -300000, 1);
            event.addChoice("家族に無理やり振る舞う", 0, 5, 0, 1);
            pool.add(event);

            event = new LifeEvent("【早期退職】退職金の割り増しオファーがあります。");
            event.addChoice("応募してリタイア", 5, -30, 20000000, 1, "無職");
            event.addChoice("定年までしがみつく", -5, 10, 0, 1);
            pool.add(event);

            event = new LifeEvent("【介護】親の介護の話が出ています。");
            event.addChoice("自分が引き受ける", -10, 20, -200000, 1);
            event.addChoice("施設を検討する", 2, 5, -500000, 1);
            pool.add(event);

            event = new LifeEvent("【健康】毎朝の散歩を始めました。");
            event.addChoice("継続して習慣化", 10, -10, 0, 1);
            event.addChoice("三日坊主で終了", -5, 5, 0, 1);
            pool.add(event);

            event = new LifeEvent("【資産】資産配分を見直す時期です。");
            event.addChoice("安定運用へシフト", 2, -2, 300000, 1);
            event.addChoice("攻めの投資を継続", -2, 10, 500000, 1);
            pool.add(event);

            event = new LifeEvent("【旅行】夫婦で温泉旅行を計画中です。");
            event.addChoice("行って癒される", 8, -15, -200000, 1);
            event.addChoice("家でのんびり", 3, -5, 0, 1);
            pool.add(event);

            event = new LifeEvent("【後継】後任への引き継ぎが始まりました。");
            event.addChoice("丁寧に教える", -2, 5, 0, 1);
            event.addChoice("最低限だけ伝える", 2, -2, 0, 1);
            pool.add(event);

            event = pickEvent(player, pool);
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
    private LifeEvent getSeniorEvent(Player player) {
        LifeEvent event;
        List<LifeEvent> pool = new ArrayList<>();
        event = new LifeEvent("【孫】孫にお年玉をせがまれます。");
        event.addChoice("見栄を張って1万円", 0, -5, -10000, 1);
        event.addChoice("500円玉を渡す", 0, 5, -500, 1);
        pool.add(event);

        event = new LifeEvent("【詐欺】怪しいリフォーム業者が来ました。");
        event.addChoice("契約してしまう", 0, 20, -5000000, 1);
        event.addChoice("塩を撒いて追い返す", -2, -5, 0, 1);
        pool.add(event);

        event = new LifeEvent("【病院】待合室が社交場になっています。");
        event.addChoice("病気自慢をする", 0, -5, -2000, 1);
        event.addChoice("若い看護師と話す", 5, -5, 0, 1);
        pool.add(event);

        event = new LifeEvent("【終活】エンディングノートを書き始めます。");
        event.addChoice("遺産は全て寄付", 0, -10, 0, 1);
        event.addChoice("恨みつらみを書き連ねる", -5, 10, 0, 1);
        pool.add(event);

        event = new LifeEvent("【徘徊】散歩に出たら帰り道がわからなくなりました。");
        event.addChoice("警察に保護される", 0, 10, 0, 1);
        event.addChoice("勘で歩く", -10, 20, 0, 1);
        pool.add(event);

        event = new LifeEvent("【同窓】昔の友人から連絡が来ました。");
        event.addChoice("会いに行く", 5, -5, -20000, 1);
        event.addChoice("手紙で済ませる", 2, -2, -2000, 1);
        pool.add(event);

        event = new LifeEvent("【健康体操】自治体の体操教室に参加します。");
        event.addChoice("毎週通う", 10, -10, 0, 1);
        event.addChoice("一度で満足", 2, -2, 0, 1);
        pool.add(event);

        event = new LifeEvent("【囲碁】近所の囲碁サークルに誘われました。");
        event.addChoice("参加して頭を使う", 5, -5, 0, 1);
        event.addChoice("家でテレビを見る", 0, 5, 0, 1);
        pool.add(event);

        event = new LifeEvent("【年金】年金の見直し案内が届きました。");
        event.addChoice("手続きをする", -2, 5, 50000, 1);
        event.addChoice("よく分からず放置", 0, 10, 0, 1);
        pool.add(event);

        event = new LifeEvent("【庭いじり】庭の手入れを始めました。");
        event.addChoice("丹精込めて育てる", 5, -5, 0, 1);
        event.addChoice("腰を痛めて中断", -10, 10, 0, 1);
        pool.add(event);

        return pickEvent(player, pool);
    }
}
