package tokyo.peya.lib.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * タイトルを同時に出せるようにする
 */

public class TitleNotification
{
    private final Map<UUID, String> mainTitleMap;
    private final Map<UUID, String> subTitleMap;
    private final List<UUID> isShowing;

    private final Plugin plugin;

    /**
     * コンストラクタ
     * @param plugin 指定するプラグイン
     */
    public TitleNotification(Plugin plugin)
    {
        this.mainTitleMap = new HashMap<>();
        this.subTitleMap = new HashMap<>();
        this.isShowing = new ArrayList<>();
        this.plugin = plugin;
    }

    /**
     * サブタイトルをセット
     * @param player 対象プレイヤー
     * @param string タイトル
     */
    public void setSubTitle(Player player, String string)
    {
        isShowing.add(player.getUniqueId());
        subTitleMap.put(player.getUniqueId(), string);

        player.sendTitle(
                mainTitleMap.getOrDefault(player.getUniqueId(), ""),
                string,
                0,
                10,
                0
        );

        new BukkitRunnable()
        {
            private final UUID uuid = player.getUniqueId();
            private int time = 0;
            @Override
            public void run()
            {
                if (!mainTitleMap.containsKey(uuid) && (!isShowing.contains(uuid) || !subTitleMap.containsKey(uuid)))
                {
                    isShowing.remove(uuid);
                    this.cancel();
                    return;
                }
                if (time++ < 10)
                    return;
                subTitleMap.remove(uuid);

                if (!mainTitleMap.containsKey(uuid))
                    isShowing.remove(uuid);
                this.cancel();
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    /**
     * メインタイトルをセット
     * @param player プレイヤー
     * @param string タイトル
     */
    public void setMainTitle(Player player, String string)
    {
        isShowing.add(player.getUniqueId());
        mainTitleMap.put(player.getUniqueId(), string);

        player.sendTitle(
                string,
                subTitleMap.getOrDefault(player.getUniqueId(), ""),
                0,
                10,
                0
        );

        new BukkitRunnable()
        {
            private UUID uuid = player.getUniqueId();
            private int time = 0;
            @Override
            public void run()
            {
                if (!subTitleMap.containsKey(uuid) && (!isShowing.contains(uuid) || !mainTitleMap.containsKey(uuid)))
                {
                    isShowing.remove(uuid);
                    this.cancel();
                    return;
                }
                if (time++ < 10)
                    return;
                mainTitleMap.remove(uuid);
                if (!subTitleMap.containsKey(uuid))
                    isShowing.remove(uuid);
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
