package de.twyco.schatzsuche.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GlowItem extends ItemStack {

    public GlowItem(){
        super(Material.LIGHT, 1);
        ItemMeta itemMeta = getItemMeta();
        if(itemMeta == null) {
            return;
        }
        itemMeta.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Toggle Kisten Glow");
        setItemMeta(itemMeta);
    }

}
