package de.twyco.schatzsuche.listener;

import de.twyco.schatzsuche.items.GlowItem;
import de.twyco.schatzsuche.items.StopGame;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SCHATZSUCHE)) {
            ItemStack is = event.getItemInHand();
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
            }
        }
    }
}
