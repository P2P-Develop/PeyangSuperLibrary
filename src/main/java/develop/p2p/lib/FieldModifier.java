package develop.p2p.lib;

import org.slf4j.*;

import java.lang.reflect.*;

public class FieldModifier
{
    public static  <T, K> boolean modify(T target, String field, K dummy)
    {
        try
        {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f,
                    f.getModifiers() & ~Modifier.PRIVATE & ~Modifier.FINAL & ~Modifier.STATIC);
            f.set(target, dummy);

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
