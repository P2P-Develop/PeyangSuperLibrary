package develop.p2p.lib;

/**
 * グラフ
 */
public class OptGraphGenerator
{
    /**
     * グラフを計算
     * @param VL VL
     * @param max 最大値
     * @return グラフの値
     */
    private static int calcVLGraph(int VL, int max)
    {
        double tendNum = 10.0 / (double) max;

        double VlMeta = tendNum * (double) VL;

        return Math.toIntExact(Math.round(VlMeta));
    }

    /**
     * グラフをつくる。String
     * @param VL VL
     * @param max 最大値
     * @param materChar 区切り
     * @param lowChar 最初
     * @param mediumChar 真ん中
     * @param highChar 最後
     * @return グラフ
     */
    static String genGraph(int VL, int max, String materChar, String lowChar, String mediumChar, String highChar)
    {
        int genVL = calcVLGraph(VL, max);

        StringBuilder builder = new StringBuilder("[");

        for (int i = 1; i < 11; i++)
        {
            if (VL >= max && i == 10)
                builder.append(materChar).append("|");
            else if (VL == 0 && i == 1)
                builder.append(materChar).append("|");
            if (i == genVL)
                builder.append(materChar).append("|");
            else if (i < 5)
                builder.append(lowChar).append("=");
            else if (i < 8)
                builder.append(mediumChar).append("=");
            else
                builder.append(highChar).append("=");
        }

        builder.append(materChar).append("]");
        return builder.toString();
    }
}
