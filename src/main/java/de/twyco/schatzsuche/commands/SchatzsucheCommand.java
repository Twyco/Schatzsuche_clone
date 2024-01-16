package de.twyco.schatzsuche.commands;

import de.twyco.schatzsuche.Schatzsuche;
import de.twyco.schatzsuche.items.GlowItem;
import de.twyco.schatzsuche.items.SelectionTool;
import de.twyco.schatzsuche.items.StartItem;
import de.twyco.schatzsuche.items.StopGame;
import de.twyco.stegisagt.Stegisagt;
import de.twyco.stegisagt.Util.Config;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class SchatzsucheCommand implements CommandExecutor, TabCompleter {

    private final Schatzsuche instance;

    public SchatzsucheCommand() {
        instance = Schatzsuche.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (s instanceof Player) {
            Player p = (Player) s;
            if (args.length == 0) {
                if (p.isOp()) {
                    p.getInventory().addItem(new SelectionTool());
                    return true;
                }
                return false;
            } else if (args.length == 1) {
                Config config = instance.getsConfig();
                if (args[0].equalsIgnoreCase("startPos")) {
                    Location location = config.getFileConfiguration().getLocation("Schatzsuche.Spawn.Location.Start");
                    if (location == null) {
                        p.sendMessage(Schatzsuche.getInstance().getPrefix() + ChatColor.RED + "Die Startposition wurde noch nicht festgelegt!");
                        return false;
                    }
                    p.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    return true;
                } else if (args[0].equalsIgnoreCase("finishPos")) {
                    Location location = config.getFileConfiguration().getLocation("Schatzsuche.Spawn.Location.Finish");
                    config.save();
                    if (location == null) {
                        p.sendMessage(Schatzsuche.getInstance().getPrefix() + ChatColor.RED + "Die Endposition wurde noch nicht festgelegt!");
                        return false;
                    }
                    p.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    return true;
                } else if (args[0].equalsIgnoreCase("toggleglow")) {
                    if (Schatzsuche.getInstance().isGlow()) {
                        Schatzsuche.getInstance().clearGlow();
                    } else {
                        Schatzsuche.getInstance().glowChests();
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("items")) {
                    Stegisagt.getInstance().giveModItems(p);
                    p.getInventory().setItem(6, new StartItem());
                    p.getInventory().setItem(7, new GlowItem());
                    p.getInventory().setItem(8, new StopGame());
                    return true;
                } else if (args[0].equalsIgnoreCase("stop")) {
                    Schatzsuche.stopGame();
                    return true;
                }
                return false;
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    Config config = instance.getsConfig();
                    if (args[1].equalsIgnoreCase("start")) {
                        Location location = p.getLocation();
                        config.getFileConfiguration().set("Schatzsuche.Spawn.Location.Start", location);
                        config.save();
                        p.sendMessage(Schatzsuche.getInstance().getPrefix() + ChatColor.GREEN + "Die Startposition wurde festgelegt!");
                        return true;
                    } else if (args[1].equalsIgnoreCase("finish")) {
                        Location location = p.getLocation();
                        config.getFileConfiguration().set("Schatzsuche.Spawn.Location.Finish", location);
                        config.save();
                        p.sendMessage(Schatzsuche.getInstance().getPrefix() + ChatColor.GREEN + "Die Finish position wurde festgelegt!");
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 0) return list;
        if (args.length == 1) {
            list.add("items");
            list.add("toggleglow");
            list.add("stop");
        }else if (args.length == 2) {
            if(args[0].equals("set")){
                list.add("start");
                list.add("finish");
            }
        }
        ArrayList<String> completerList = new ArrayList<>();
        String currentArg = args[args.length - 1].toLowerCase();
        for (String s : list) {
            String s1 = s.toLowerCase();
            if (s1.startsWith(currentArg)) {
                completerList.add(s);
            }
        }
        return completerList;
    }
}