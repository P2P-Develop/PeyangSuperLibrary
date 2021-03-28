package develop.p2p.lib.bukkit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemのUtil
 */
public class ItemUtils
{
    /**
     * 光るやつをつける
     * @param stack Item
     * @return つけたやつ
     */
    public static ItemStack setGlow(ItemStack stack)
    {
        stack.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return stack;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * アイテムが壊れなくなるようにする
     * @param b アイテム
     * @return 壊れなくなったアイテム
     */
    public static ItemStack setUnbreakable(ItemStack b)
    {
        if (b == null || b.getType() == Material.AIR)
            return b;
        ItemMeta meta = b.getItemMeta();
        if (meta == null)
            return b;
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        ItemStack stack = b.clone();
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * ディスプレイ名を追加する
     * @param b アイテム
     * @param append 追加する内容
     * @return 追加されたアイテム
     */
    public static ItemStack appendDisplayName(ItemStack b, String append)
    {
        ItemStack copy = b.clone();
        ItemMeta meta = copy.getItemMeta();
        if (meta == null)
            return b;
        if (meta.getDisplayName().equals(""))
            meta.setDisplayName(append);
        else
            meta.setDisplayName(append + meta.getDisplayName());
        copy.setItemMeta(meta);
        return copy;
    }

    /**
     * 名前をセット
     * @param b アイテム
     * @param name 名前
     * @return セットされたやつ
     */
    public static ItemStack setDisplayName(ItemStack b, String name)
    {
        ItemStack copy = b.clone();
        ItemMeta meta = copy.getItemMeta();
        if (meta == null)
            return b;
        meta.setDisplayName(name);
        copy.setItemMeta(meta);
        return copy;
    }

    /**
     * アトリビュートを非表示
     * @param b アイテム
     * @return アイテム
     */
    public static ItemStack hideAttribute(ItemStack b)
    {
        ItemStack copy = b.clone();
        ItemMeta meta = copy.getItemMeta();
        if (meta == null)
            return b;
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        copy.setItemMeta(meta);
        return b;
    }

    /**
     * Loreをセット
     * @param b アイテム
     * @param t Lore
     * @return アイテム
     */
    public static ItemStack lore(ItemStack b, List<String> t)
    {
        ItemMeta meta = b.getItemMeta();
        if (b.getType() == Material.AIR)
            return b;
        if (meta == null)
            return b;
        meta.setLore(t);
        ItemStack stack = b.clone();
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * 1行だけのLoreを簡単にセット
     * @param b アイテム
     * @param t lore
     * @return  アイテム
     */
    public static ItemStack quickLore(ItemStack b, String t)
    {
        ItemMeta meta = b.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(t);
        lore.add("");
        if (b.getType() == Material.AIR || meta == null)
            return b;
        if (meta.hasLore())
            lore.addAll(meta.getLore());
        meta.setLore(lore);
        ItemStack stack = b.clone();
        stack.setItemMeta(meta);
        return stack;
    }

}
