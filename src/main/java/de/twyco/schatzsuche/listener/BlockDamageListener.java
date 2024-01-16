package de.twyco.schatzsuche.listener;

import de.twyco.schatzsuche.Schatzsuche;
import de.twyco.schatzsuche.items.MiningShovel;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

public class BlockDamageListener implements Listener {

    @EventHandler
    public void onBlockDamage(final BlockDamageEvent event){
        if (Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SCHATZSUCHE)) {
            ItemStack is = event.getPlayer().getInventory().getItemInMainHand();
            if (is.getItemMeta() == null) {
                return;
            }
            if (!is.getType().equals(new MiningShovel().getType()) &&
                    !is.getItemMeta().equals(new MiningShovel().getItemMeta())) {
                return;
            }
            if(event.getBlock().getType().equals(Material.SANDSTONE)){
                event.setInstaBreak(true);
                for (Player player : Schatzsuche.getInstance().getAllPlayers()){
                    player.playSound(event.getBlock().getLocation(), Sound.BLOCK_SAND_BREAK, SoundCategory.BLOCKS, 0.5F, 1F);
                }
            }
        }
    }

}
