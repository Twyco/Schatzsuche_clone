package de.twyco.schatzsuche.listener;

import de.twyco.schatzsuche.Schatzsuche;
import de.twyco.schatzsuche.items.SelectionTool;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    @EventHandler()
    public void onBlockBreak(final BlockBreakEvent event) {
        if (Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SCHATZSUCHE)) {
            if (event.getBlock().getType().equals(Material.SANDSTONE) && Schatzsuche.getInstance().isStart()) {
                event.setCancelled(false);
                return;
            }
            event.setCancelled(true);
        } else if (Stegisagt.getGameStatus().equals(GameStatus.CLOSED)) {
            Player player = event.getPlayer();
            ItemStack is = player.getInventory().getItemInMainHand();
            if (!player.isOp()) {
                return;
            }
            if (is.getType().equals(new SelectionTool().getType())) {
                if (is.getItemMeta() == null) {
                    return;
                }
                if (is.getItemMeta().equals(new SelectionTool().getItemMeta())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
