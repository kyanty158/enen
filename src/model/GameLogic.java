package model;

/**
 * ゲームのイベントロジックを管理するクラス（Event Repository）
 */
public class GameLogic {

    /**
     * 年齢に応じたイベントを取得する
     */
    public LifeEvent getEventForAge(int age) {
        LifeEvent event;

        if (age <= 18) {
            event = new LifeEvent("【高校時代】進路選択の時期です。どうしますか？");
            event.addChoice("4年制大学へ進学", -10, 10, -4000000, 4);
            event.addChoice("高卒で就職する", -5, 20, 2000000, 1);
            event.addChoice("医学部を目指す(浪人覚悟)", -20, 40, -1000000, 2);
        } else if (age <= 22) {
            event = new LifeEvent("【若者時代】自由な時間ができました。");
            event.addChoice("留学に行く", 10, -10, -1000000, 1);
            event.addChoice("資格の勉強に集中", -10, 10, 0, 1);
            event.addChoice("ブラックバイト漬け", -30, 30, 1000000, 1);
        } else if (age <= 30) {
            event = new LifeEvent("【社会人】キャリアの岐路です。");
            event.addChoice("転職してキャリアアップ", -10, 20, 500000, 3);
            event.addChoice("現状維持でのんびり", 5, -5, 200000, 5);
            event.addChoice("起業する", -20, 50, -500000, 2);
        } else if (age < 100) {
            event = new LifeEvent("【壮年・老年期】時は流れていきます...");
            event.addChoice("健康第一で過ごす", 5, -5, -10000, 5);
            event.addChoice("バリバリ働く", -10, 10, 100000, 5);
        } else {
            event = new LifeEvent("【超越者】悠久の時を過ごします。");
            event.addChoice("100年の眠りにつく", 10, -50, 0, 100);
            event.addChoice("文明を観察する", 0, 0, 0, 10);
        }
        return event;
    }
}
