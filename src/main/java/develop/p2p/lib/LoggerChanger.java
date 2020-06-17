package develop.p2p.lib;

import org.slf4j.*;

public class LoggerChanger
{
    public static <T, K extends Logger> boolean changeToDummy(T target, String field, K dummyLogger)
    {
        return FieldModifier.modify(target, field, dummyLogger);
    }

    public static <T> boolean shutUpLog(T target, String field)
    {
        return changeToDummy(target, field, new DummyLogger());
    }

}
