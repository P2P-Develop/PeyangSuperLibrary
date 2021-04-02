package tokyo.peya.lib;

import org.slf4j.*;

/**
 * ロガーを変更
 */
public class LoggerChanger
{
    /**
     * ダミーにする
     * @param target ターゲット
     * @param field フィールド名
     * @param dummyLogger ダミーのロガー
     * @return 結果
     */
    public static <T, K extends Logger> boolean changeToDummy(T target, String field, K dummyLogger)
    {
        return FieldModifier.modify(target, field, dummyLogger);
    }

    /**
     * ログを黙らせる
     * @param target ターゲット
     * @param field フィールド
     * @return 可否
     */
    public static <T> boolean shutUpLog(T target, String field)
    {
        return changeToDummy(target, field, new DummyLogger());
    }

}
