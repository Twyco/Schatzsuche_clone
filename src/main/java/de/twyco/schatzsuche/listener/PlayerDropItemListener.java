package de.twyco.schatzsuche.listener;

import de.twyco.schatzsuche.items.GlowItem;
import de.twyco.schatzsuche.items.MiningShovel;
import de.twyco.schatzsuche.items.StopGame;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event){
        if(Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SCHATZSUCHE)){
            ItemStack is = event.getItemDrop().getItemStack();
            if (is.getItemMeta() == null) {
                return;
            }
            if (is.getType().equals(new StopGame().getType())) {
                if (is.getItemMeta().equals(new StopGame().getItemMeta())) {
                    event.setCancelled(true);
                }
            } else if (is.getType().equals(new GlowItem().getType())) {
                if (is.getItemMeta().equals(new GlowItem().getItemMeta())) {
                    event.setCancelled(true);
                }
            } else if (is.getType().equals(new MiningShovel().getType())) {
                if (is.getItemMeta().equals(new MiningShovel().getItemMeta())) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
