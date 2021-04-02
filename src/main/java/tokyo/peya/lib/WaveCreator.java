package tokyo.peya.lib;

/**
 * 波を作成
 */
public class WaveCreator
{
    private boolean flag;

    private final double max;
    private final double min;

    private double now;

    /**
     * コンストラクタ
     * @param now 現在の値
     * @param max 最大値
     * @param min 最小値
     */
    public WaveCreator(double now, double max, double min)
    {
        this.max = max;
        this.now = now;
        this.min = min;
    }

    /**
     * 取得
     * @param db 変動させる値
     * @param def ダミーかどうか
     * @return 値
     */
    public double get(double db, boolean def)
    {
        if (def)
            return now;

        if (flag)
            now += db;
        else
            now -= db;

        if (now + db > max)
        {
            if (flag)
                flag = false;
            return max;
        }
        else if (now - db < min)
        {
            if (!flag)
                flag = true;
            return min;
        }


        return now;
    }

    /**
     * そのまま取得
     * @return 値
     */
    public double getStatic()
    {
        return now;
    }

}
