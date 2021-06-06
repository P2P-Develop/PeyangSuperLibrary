package tokyo.peya.lib;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.SerializationUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * PluginYamlをPojoに変換します。
 */
@SuppressWarnings("unused")
public class PluginYamlParser implements Serializable
{
    /**
     * プラグインの名前。
     */
    public String name;
    /**
     * プラグインのバージョン。
     */
    public String version;
    /**
     * プラグインの概要。
     */
    public String description;
    /**
     * プラグインの使用するAPIバージョン。
     * 1.12以下のプラグインの場合は確定nullです。
     */
    public String api_version;
    /**
     * プラグインの読み込みタイミング。
     */
    public Load load;
    /**
     * プラグインの作成者。
     */
    public String author;
    /**
     * プラグインの作成者(リスト)。
     */
    public String[] authors;
    /**
     * プラグインのウェブサイト。
     */
    public String website;
    /**
     * プラグインのメインクラス。
     */
    public String main;
    /**
     * プラグインがデータベースを利用するか。
     */
    public boolean databases;
    /**
     * プラグインのログ接頭辞。
     */
    public String prefix;
    /**
     * プラグインの依存先。
     */
    public String[] depend;
    /**
     * 先に起動すべきプラグイン。
     */
    public String[] softdepend;
    /**
     * 後に起動すべきプラグイン。
     */
    public String[] loadbefore;
    /**
     * プラグインのコマンド一覧。
     */
    public HashMap<String, Command> commands;
    /**
     * プラグインの権限一覧。
     */
    public HashMap<String, Permission> permissions;
    /**
     * プラグインのコマンドのデフォルト権限。
     */
    public Permission defaultPermission;

    @SerializedName("default-permission")
    private PrePermission defaultPrePermission;

    /**
     * Mapから変換
     * @param map マップ
     * @return 変換後
     */
    public static PluginYamlParser fromMap(Map<String, Object> map)
    {
        return new PluginYamlParser().parse(map);
    }

    /**
     * plugin.yml等の Yamlファイルから変換。
     * @param yaml Yamlファイル
     * @return 変換後
     * @throws IOException ファイルが見つからない
     */
    public static PluginYamlParser fromYaml(File yaml) throws IOException
    {
        if (!yaml.exists())
            throw new FileNotFoundException("plugin.yml not found.");

        try (InputStream stream = new FileInputStream(yaml))
        {
            HashMap<String, Object> pluginYamlParser = new Yaml().load(stream);
            return new PluginYamlParser().parse(pluginYamlParser);
        }
    }

    /**
     * プラグインのJarファイル
     * @param file jar
     * @return 変換後
     * @throws IOException ファイルが見つからない
     */
    public static PluginYamlParser fromJar(File file) throws IOException
    {
        if (!file.exists())
            throw new FileNotFoundException("Plugin file not found.");

        try (ZipFile zip = new ZipFile(file))
        {
            ZipEntry ent = zip.getEntry("plugin.yml");

            if (ent == null)
                throw new FileNotFoundException("plugin.yml not found.");

            try (InputStream stream = zip.getInputStream(ent))
            {
                HashMap<String, Object> pluginYamlParser = new Yaml().load(stream);
                return new PluginYamlParser().parse(pluginYamlParser);
            }
        }
    }

    private PluginYamlParser parse(Map<String, Object> kv)
    {
        PluginYamlParser pluginYamlParser = new Gson().fromJson(new Gson().toJson(kv), PluginYamlParser.class);
        pluginYamlParser.defaultPermission = flatLine(pluginYamlParser.defaultPrePermission);

        return flatLine(pluginYamlParser);
    }

    private PluginYamlParser flatLine(PluginYamlParser pluginYamlParser)
    {
        PluginYamlParser newParser = SerializationUtils.clone(pluginYamlParser);

        if (pluginYamlParser.permissions == null)
            return pluginYamlParser;

        newParser.permissions = new HashMap<>();

        pluginYamlParser.permissions.forEach((s, permission) -> {
            HashMap<String, Object> children = permission.children;
            if (children == null)
            {
                newParser.permissions.put(s, permission);
                return;
            }

            permission.children = flatLine(children);

            newParser.permissions.put(s, permission);
        });

        return newParser;
    }

    private HashMap<String, Object> flatLine(HashMap<String, Object> children)
    {
        if (children == null)
            return null;

        HashMap<String, Object> newChildren = new HashMap<>(children);

        children.forEach((s1, o) -> {
            if (o instanceof LinkedTreeMap)
            {
                PrePermission perm = new Gson().fromJson(new Gson().toJson(o), PrePermission.class);
                perm.children = flatLine(perm.children);

                newChildren.put(s1, flatLine(perm));
            }
            else
                newChildren.put(s1, o);
        });

        return newChildren;
    }

    private Permission flatLine(PrePermission permission)
    {
        if (permission == null)
            return null;

        Permission perm = new Permission();

        perm.description = permission.description;
        perm.children = permission.children;

        if (defaultPermission == null)
            return perm;

        if (permission.defaultPermission.equalsIgnoreCase("op"))
            perm.defaultPermission = DefaultPermission.OP;
        else if (permission.defaultPermission.equalsIgnoreCase("not op") ||
                permission.defaultPermission.equalsIgnoreCase("not_op"))
            perm.defaultPermission = DefaultPermission.NOTOP;
        else if (permission.defaultPermission.equalsIgnoreCase("true"))
            perm.defaultPermission = DefaultPermission.TRUE;
        else if (permission.defaultPermission.equalsIgnoreCase("false"))
            perm.defaultPermission = DefaultPermission.FALSE;
        return perm;
    }

    /**
     * 読み込みタイミング。
     */
    public enum Load
    {
        /**
         * Bukkitスタート時に読み込み。
         */
        STARTUP,
        /**
         * ワールド読み込み後に読み込み。
         */
        POSTWORLD
    }

    /**
     * デフォルトの権限。
     */
    public enum DefaultPermission
    {
        /**
         * OPを持つ人。
         */
        OP,
        /**
         * OPを持たない人。
         */
        NOTOP,
        /**
         * 権限なし。
         */
        FALSE,
        /**
         * 権限あり。
         */
        TRUE
    }

    /**
     * プラグインのコマンド。
     */
    public static class Command implements Serializable
    {
        /**
         * コマンドの概要。
         */
        public String description;
        /**
         * コマンドのエイリアス。
         */
        public String[] aliases;
        /**
         * コマンドの権限。
         */
        public String permission;
        /**
         * 権限がない場合のメッセージ。
         */
        @SerializedName("permission-message")
        public String permissionMessage;
        /**
         * コマンドの使用法。
         */
        public String usage;
    }

    /**
     * プラグインの権限。
     */
    public static class Permission implements Serializable
    {
        /**
         * 権限の概要。
         */
        public String description;
        /**
         * デフォルトの権限。
         */
        public DefaultPermission defaultPermission;
        /**
         * 子権限。
         * {@code Boolean}または{@code Permission}
         */
        public HashMap<String, Object> children;
    }


    private static class PrePermission extends Permission
    {
        @SerializedName("default")
        public String defaultPermission;
    }
}

