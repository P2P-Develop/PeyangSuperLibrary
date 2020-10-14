package develop.p2p.lib;

public class Intellij
{
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
