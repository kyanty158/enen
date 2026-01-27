import java.io.Serializable;

class StoryBuilder {
    public static String build(Player player, Economy economy) {
        StringBuilder sb = new StringBuilder();
        Chapter chapter = player.getChapter();
        sb.append("【人生ストーリー】\n");
        sb.append("名前: ").append(player.getName()).append(" / 年齢: ").append(player.getAge()).append("歳\n");
        sb.append("章: ").append(chapter != null ? chapter.getTitle() : "-")
                .append(" / 身分: ").append(player.getStatus()).append("\n");
        if (economy != null) {
            sb.append("景気: ").append(economy.getLabel()).append(" / ")
                    .append(economy.getInflationLabel()).append("\n");
        }
        sb.append("\n【人生のハイライト】\n");
        java.util.List<String> history = player.getHistory();
        int start = Math.max(0, history.size() - 8);
        if (history.isEmpty()) {
            sb.append("まだ記録がありません。\n");
        } else {
            for (int i = start; i < history.size(); i++) {
                String log = history.get(i);
                int marker = log.indexOf(": ");
                String line = (marker >= 0) ? log.substring(marker + 2) : log;
                int br = line.indexOf("\n");
                if (br >= 0) {
                    line = line.substring(0, br);
                }
                sb.append("・").append(line).append("\n");
            }
        }

        sb.append("\n【人間関係】\n");
        java.util.List<Npc> npcs = player.getNpcs();
        if (npcs.isEmpty()) {
            sb.append("まだ出会いがありません。\n");
        } else {
            for (Npc npc : npcs) {
                sb.append("・").append(npc.getType()).append(": ")
                        .append(npc.getName()).append(" (")
                        .append(npc.getTraitsText()).append(") 関係値 ")
                        .append(npc.getRelation()).append("\n");
            }
        }

        sb.append("\n【締めの一文】\n");
        sb.append("人生は選択の連続。次の一歩が物語を変える。\n");
        return sb.toString();
    }
}
