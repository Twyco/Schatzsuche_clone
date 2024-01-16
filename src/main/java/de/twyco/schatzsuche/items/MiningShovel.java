package de.twyco.schatzsuche.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MiningShovel extends ItemStack{

    public MiningShovel(){
        super(Material.GOLDEN_SHOVEL, 1);
        ItemMeta itemMeta = getItemMeta();
        if(itemMeta == null) {
            return;
        }
        itemMeta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Schaufel");
        itemMeta.setUnbreakable(true);
        itemMeta.addEnchant(Enchantment.DIG_SPEED, 10, true);
        setItemMeta(itemMeta);
    }

}
