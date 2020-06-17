package develop.p2p.lib;


public class OptGraphGenerator
{
    private static int calcVLGraph(int VL, int max)
    {
        double tendNum = 10.0 / (double) max;

        double VlMeta = tendNum * (double) VL;

        return Math.toIntExact(Math.round(VlMeta));
    }

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
