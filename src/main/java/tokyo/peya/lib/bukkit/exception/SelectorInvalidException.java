package tokyo.peya.lib.bukkit.exception;

import java.util.Map;

/**
 * 無効なセレクタの例外
 */
public class SelectorInvalidException extends SelectorException
{
    private final Map<String, String> invalidKeys;

    /**
     * コンストラクタ
     * @param invalidKeys 無効だったセレクタ
     */
    public SelectorInvalidException(Map<String, String> invalidKeys)
    {
        this.invalidKeys = invalidKeys;
    }

    /**
     * 無効だったセレクタを取得
     * @return セレクタ
     */
    public Map<String, String> getInvalidKeys()
    {
        return invalidKeys;
    }
}
