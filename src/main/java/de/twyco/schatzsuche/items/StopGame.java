package de.twyco.schatzsuche.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StopGame extends ItemStack {

    public StopGame(){
        super(Material.BARRIER, 1);
        ItemMeta itemMeta = getItemMeta();
        if(itemMeta == null) {
            return;
        }
        itemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Stop Game");
        setItemMeta(itemMeta);
    }

}
