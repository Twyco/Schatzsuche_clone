package de.twyco.schatzsuche.listener;

import de.twyco.schatzsuche.Schatzsuche;
import de.twyco.schatzsuche.items.GlowItem;
import de.twyco.schatzsuche.items.SelectionTool;
import de.twyco.schatzsuche.items.StartItem;
import de.twyco.schatzsuche.items.StopGame;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import de.twyco.stegisagt.Util.Config;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    private final Schatzsuche instance;
    private boolean glowDelay;
    private boolean stopGame;
    private boolean stopMinDelay;

    public PlayerInteractListener() {
        instance = Schatzsuche.getInstance();
        glowDelay = false;
        stopGame = false;
        stopMinDelay = false;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (Stegisagt.getGameStatus().equals(GameStatus.CLOSED)) {
            Action action = event.getAction();
            if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.LEFT_CLICK_BLOCK)) {
                return;
            }
            Player player = event.getPlayer();
            if (!player.isOp()) {
                return;
            }
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.getItemMeta() == null) {
                return;
            }
            if (!is.getType().equals(new SelectionTool().getType())) {
                return;
            }
            if(!is.getItemMeta().equals(new SelectionTool().getItemMeta())){
                return;
            }
            Block block = event.getClickedBlock();
            if (event.getHand() != EquipmentSlot.HAND) {
                return;
            }
            Location location = block.getLocation();
            Config config = instance.getsConfig();
            boolean leftClick = action.equals(Action.LEFT_CLICK_BLOCK);
            config.getFileConfiguration().set(leftClick ?
                    "Schatzsuche.Box.Location.1" : "Schatzsuche.Box.Location.2", location);
            config.save();
            player.sendMessage(instance.getPrefix() + ChatColor.GREEN + "Du hast die " + ChatColor.YELLOW + (leftClick ? "1" : "2") +
                    ChatColor.GREEN + " Position gespeichert.");
        } else if (Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SCHATZSUCHE)) {
            Player player = event.getPlayer();

            if (instance.isPlayingPlayer(player)) {
                if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    return;
                }
                if (event.getHand() != EquipmentSlot.HAND) {
                    return;
                }
                if (event.getClickedBlock() == null) {
                    return;
                }
                if (event.getClickedBlock().getType().equals(Material.CHEST)) {
                    if (instance.isGlow()) {
                        for (Shulker entity : instance.getShulkers()) {
                            Location location = entity.getLocation().add(-0.5, 0, -0.5);
                            if (event.getClickedBlock().getLocation().equals(location)) {
                                instance.removeShulker(entity);
                                entity.setHealth(0);
                                break;
                            }
                        }
                    }

                    event.getClickedBlock().setType(Material.AIR, false);
                    instance.finishPlayer(player);
                }
            } else if (instance.isModPlayer(player)) {
                if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    return;
                }
                if (event.getHand() != EquipmentSlot.HAND) {
                    return;
                }
                ItemStack is = player.getInventory().getItemInMainHand();
                if (is.getItemMeta() == null) {
                    return;
                }
                if (is.getType().equals(new GlowItem().getType()) &&
                        is.getItemMeta().equals(new GlowItem().getItemMeta())) {
                    if (glowDelay) {
                        player.sendMessage(instance.getPrefix() + ChatColor.RED + "Du kannst nur alle 0.5 Sekunden Kisten Glow toggeln");
                    } else {
                        glowDelay = true;
                        if (instance.isGlow()) {
                            Schatzsuche.getInstance().clearGlow();
                        } else {
                            Schatzsuche.getInstance().glowChests();
                        }
                        instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                            @Override
                            public void run() {
                                glowDelay = false;
                            }
                        }, 10L);
                    }
                } else if (is.getType().equals(new StopGame().getType()) &&
                        is.getItemMeta().equals(new StopGame().getItemMeta())) {
                    if (stopGame && stopMinDelay) {
                        stopGame = false;
                        Schatzsuche.stopGame();
                        player.sendMessage(instance.getPrefix() + ChatColor.DARK_RED + "Das Spiel wurde Abgebrochen!");
                    } else {
                        stopGame = true;
                        stopMinDelay = false;
                        instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> stopMinDelay = true, 5L);
                        instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
                            if (stopGame) {
                                stopGame = false;
                                player.sendMessage(instance.getPrefix() + ChatColor.RED + "zum Abbrechen Doppelklicken");
                            }
                        }, 20L);
                    }
                } else if (is.getType().equals(new StartItem().getType()) &&
                        is.getItemMeta().equals(new StartItem().getItemMeta())) {
                    instance.countDown();
                    player.getInventory().getItemInMainHand().setType(Material.AIR);
                }
            }
        }
    }
}
