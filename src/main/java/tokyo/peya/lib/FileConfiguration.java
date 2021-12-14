package tokyo.peya.lib;

import org.yaml.snakeyaml.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * ファイルコンフィグ
 */
public class FileConfiguration
{
    private final File cfg;
    private Map<String, Object> config = null;
    private final String fileStr;

    /**
     * コンストラクタ。
     * @param parent ディレクトリ
     * @param name ファイル名
     */
    public FileConfiguration(File parent, String name)
    {
        this.fileStr = name;
        this.cfg = new File(parent, name);
    }

    /**
     * jarファイル内のconfig.ymlを保存
     */
    public boolean saveDefaultConfig()
    {
        if (cfg.exists())
            return true;

        try
        {
            copyFromInJar(fileStr);
            return true;
        }
        catch (IOException ignored)
        {
            return false;
        }
    }

    /**
     * configを読み込む
     */
    public boolean loadConfig()
    {
        return reloadConfig();
    }

    /**
     * 再読み込み
     */
    public boolean reloadConfig()
    {
        try (InputStream stream = new FileInputStream(cfg);
            InputStreamReader reader = new InputStreamReader(stream))
        {
            Yaml yaml = new Yaml();
            this.config = yaml.load(reader);
            return true;
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    /**
     * configをmapで取得。
     * @return map
     */
    public Map<String, Object> getConfig()
    {
        if (config == null)
            if (!loadConfig())
                throw new NullPointerException("Failed to parsing yaml files");

        return this.config;
    }

    /**
     * configを得る。
     * キーは"."で区切る。
     * @param key キー
     * @return 結果 またはnull
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key)
    {
        try
        {
            if (!key.contains("."))
            {
                if (getConfig().get(key) == null)
                    throw new NullPointerException("Failed to parsing yaml files");
                return (T) getConfig().get(key);
            }

            ArrayList<String> keys = new ArrayList<>(Arrays.asList(key.split("\\.")));

            Map<String, Object> preCfg = getConfig();
            int count = 0;
            for (String preKey: keys)
            {
                if (keys.size() == count + 1)
                    return (T) preCfg.get(preKey);
                preCfg = (Map<String, Object>) preCfg.get(preKey);
                count++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * stringでget
     * @param key キー
     * @return string
     */
    public String getString(String key)
    {
        return get(key);
    }

    private void copyFromInJar(String name) throws IOException
    {
        Path to = cfg.toPath();
        Files.copy(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(name)), to);

    }
}
