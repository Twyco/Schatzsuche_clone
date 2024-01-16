package de.twyco.schatzsuche.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SelectionTool extends ItemStack{

    public SelectionTool(){
        super(Material.WOODEN_SHOVEL, 1);
        ItemMeta itemMeta = getItemMeta();
        if(itemMeta == null) {
            return;
        }
        itemMeta.setDisplayName(ChatColor.GOLD + ChatColor.ITALIC.toString() + "Schatzsuche");
        setItemMeta(itemMeta);
    }

}
