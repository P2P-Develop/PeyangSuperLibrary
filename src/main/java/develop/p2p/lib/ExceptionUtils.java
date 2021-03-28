package develop.p2p.lib;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 例外のUtil
 */
public class ExceptionUtils
{
    /**
     * スタックトレースをStringにする
     *
     * @param e   スタックトレース
     * @return String
     */
    public static String toString(Exception e)
    {
        StringWriter str = new StringWriter();
        PrintWriter w = new PrintWriter(str);
        e.printStackTrace(w);
        w.flush();
        return str.toString();
    }
}
