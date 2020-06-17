package develop.p2p.lib;

import org.slf4j.*;

import java.lang.reflect.*;

public class LoggerChanger
{
    public static <T, K extends Logger> boolean changeToDummy(T target, String field, K dummyLogger)
    {
        try
        {
            Field f = target.getClass().getDeclaredField(field);

            f.setAccessible(true);

            f.set(target, dummyLogger);

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static <T> boolean shutUpLog(T target, String field)
    {
        return changeToDummy(target, field, new DummyLogger());
    }

}
