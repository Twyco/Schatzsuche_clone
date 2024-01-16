package de.twyco.schatzsuche;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionBarManager {

    private static int taskID;

    public static void showActionBar() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Schatzsuche.getInstance(), new Runnable() {
            @Override
            public void run() {
                String message = ChatColor.GOLD + "Es sind noch " + ChatColor.BLUE +
                    Schatzsuche.getInstance().getRestChestCount() + ChatColor.GOLD + " Schatztruhen vergraben";
                for (Player player : Schatzsuche.getInstance().getAllPlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                }
            }
        }, 0L, 20L);
    }

    public static void hideActionBar() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
