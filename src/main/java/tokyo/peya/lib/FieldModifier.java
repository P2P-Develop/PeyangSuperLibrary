package tokyo.peya.lib;

import java.lang.reflect.*;

/**
 * フィールドのUtil.
 */
public class FieldModifier
{
    /**
     * フィールドをModify
     * @param target ターゲットのインスタンス
     * @param field フィールド名
     * @param dummy ぶち込むやつ
     * @return 成功したかどうか
     */
    public static <T, K> boolean modify(T target, String field, K dummy)
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

    /**
     * 黒魔術でModifyする。
     * @param clazz クラス
     * @param field フィールド名
     * @param as ぶち込むやつ
     */
    public static <T> void modifyAsGod(Class<?> clazz, String field, T as)
    {
        try
        {
            Field f = clazz.getDeclaredField(field);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f,
                    f.getModifiers() & ~Modifier.PRIVATE & ~Modifier.FINAL & ~Modifier.STATIC);
            f.set(null, as);
        }
        catch (Exception ignored)
        {
        }
    }
}
