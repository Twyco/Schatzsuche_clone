package de.twyco.schatzsuche.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StartItem extends ItemStack {

    public StartItem(){
        super(Material.LIME_DYE, 1);
        ItemMeta itemMeta = getItemMeta();
        if(itemMeta == null){
            return;
        }
        itemMeta.setDisplayName(ChatColor.GREEN + "Start Game");
        setItemMeta(itemMeta);
    }

}
