package develop.p2p.lib;

import org.yaml.snakeyaml.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileConfiguration
{
    private final File cfg;
    private Map<String, Object> config = null;
    private final String fileStr;

    public FileConfiguration(String name)
    {
        this.fileStr = name;
        this.cfg = new File("./" + name);
    }

    public void saveDefaultConfig()
    {
        if (cfg.exists())
            return;
        copyFromInJar(fileStr);
    }

    public void loadConfig()
    {
        reloadConfig();
    }

    public void reloadConfig()
    {
        try (InputStream stream = new FileInputStream(cfg);
            InputStreamReader reader = new InputStreamReader(stream))
        {
            Yaml yaml = new Yaml();
            this.config = yaml.load(reader);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getConfig()
    {
        if (config == null)
            reloadConfig();

        return this.config;
    }

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

    public String getString(String key)
    {
        return get(key);
    }

    private void copyFromInJar(String name)
    {
        try
        {
            Path to = cfg.toPath();
            Files.copy(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(name)), to);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
