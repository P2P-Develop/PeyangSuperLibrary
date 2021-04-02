package tokyo.peya.lib;

/**
 * Intellijのユーティリティ
 */
public class Intellij
{
    /**
     * デバッグ中かどうか
     * @return 合否
     */
    public static boolean isDebugging()
    {
        try
        {
            return Package.getPackage("com.intellij.rt.debugger.agent") != null;
        }
        catch (Exception ignored)
        {
            return false;
        }
    }
}
