package de.twyco.schatzsuche;

import de.twyco.schatzsuche.commands.SchatzsucheCommand;
import de.twyco.schatzsuche.items.GlowItem;
import de.twyco.schatzsuche.items.MiningShovel;
import de.twyco.schatzsuche.items.StartItem;
import de.twyco.schatzsuche.items.StopGame;
import de.twyco.schatzsuche.listener.*;
import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.ScoreboardManager;
import de.twyco.stegisagt.Stegisagt;
import de.twyco.stegisagt.Util.Config;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Random;

public final class Schatzsuche extends JavaPlugin {

    private static Schatzsuche instance;
    private static final String prefix = ChatColor.BOLD.toString() + ChatColor.DARK_GRAY + "[" + ChatColor.BOLD + ChatColor.GOLD + "Schatz Suche" + ChatColor.BOLD + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
    private Config sConfig;
    private int chestCount;
    private int restChestCount;
    private int maxFinishPlayerCount;
    private ArrayList<Player> playingPlayers;
    private ArrayList<Player> finishedPlayers;
    private ArrayList<Player> modPlayers;
    private ArrayList<Player> allPlayers;
    private ArrayList<Shulker> shulkers;
    private boolean glow;
    private int countdown;
    private boolean start;
    private BukkitTask bukkitTask;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Schatzsuche plugin wird geladen...");
        instance = this;
        sConfig = new Config("Config.yml", getDataFolder());
        registerCommands();
        registerListener();
        resetGame();
    }

    @Override
    public void onDisable() {
        clearGlow();
        killShulkerInArea();
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("schatzsuche").setExecutor(new SchatzsucheCommand());
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
    }

    public Config getsConfig() {
        return sConfig;
    }

    public String getPrefix() {
        return prefix;
    }

    ////////////////////////

    public int getPlayingPlayerCount() {
        return playingPlayers.size();
    }

    public boolean isPlayingPlayer(Player player) {
        return playingPlayers.contains(player);
    }

    public void addPlayingPlayer(Player player) {
        playingPlayers.add(player);
    }

    public void removePlayingPlayer(Player player) {
        playingPlayers.remove(player);
    }

    ////////////////////////

    public int getFinishedPlayerCount() {
        return finishedPlayers.size();
    }

    public boolean isFinishedPlayer(Player player) {
        return finishedPlayers.contains(player);
    }

    public void addFinishedPlayer(Player player) {
        finishedPlayers.add(player);
    }

    public void removeFinishedPlayer(Player player) {
        finishedPlayers.remove(player);
    }

    ////////////////////////

    public ArrayList<Player> getModPlayers() {
        return modPlayers;
    }

    public boolean isModPlayer(Player player) {
        return modPlayers.contains(player);
    }

    public void addModPlayer(Player player) {
        modPlayers.add(player);
    }

    ////////////////////////

    public ArrayList<Player> getAllPlayers() {
        return allPlayers;
    }

    public void addAllPlayer(Player player) {
        allPlayers.add(player);
    }

    public void removeAllPlayer(Player player) {
        allPlayers.remove(player);
    }

    ////////////////////////

    public int getMaxFinishPlayerCount() {
        return maxFinishPlayerCount;
    }

    public void setMaxFinishPlayerCount(int maxFinishPlayerCount) {
        this.maxFinishPlayerCount = maxFinishPlayerCount;
    }

    public int getChestCount() {
        return chestCount;
    }

    public void setChestCount(int chestCount) {
        this.chestCount = chestCount;
    }

    public int getRestChestCount() {
        return restChestCount;
    }

    public void setRestChestCount(int restChestCount) {
        this.restChestCount = restChestCount;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    ////////////////////////

    public ArrayList<Shulker> getShulkers() {
        return shulkers;
    }

    public void removeShulker(Shulker shulker) {
        shulkers.remove(shulker);
    }

    public void addShulker(Shulker shulker) {
        shulkers.add(shulker);
    }

    ////////////////////////


    public static Schatzsuche getInstance() {
        return instance;
    }

    public static void startGame(ArrayList<Player> players, ArrayList<Player> mods, int chestCount) {
        startGame(players, mods, chestCount, chestCount);
    }

    public static void startGame(ArrayList<Player> players, ArrayList<Player> mods, int chestCount, int maxFinishPlayer) {
        instance.setListenerSettings();
        Stegisagt.setGameStatus(GameStatus.PLAYING_SCHATZSUCHE);
        Schatzsuche.getInstance().setChestCount(chestCount);
        Schatzsuche.getInstance().setRestChestCount(chestCount);
        Schatzsuche.getInstance().setMaxFinishPlayerCount(maxFinishPlayer);
        Schatzsuche.getInstance().hideChests();
        ActionBarManager.showActionBar();
        Config config = getInstance().getsConfig();
        Location location = config.getFileConfiguration().getLocation("Schatzsuche.Spawn.Location.Start");
        for (Player player : players) {
            player.getInventory().clear();
            Schatzsuche.getInstance().addAllPlayer(player);
            Schatzsuche.getInstance().addPlayingPlayer(player);
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setGameMode(GameMode.SURVIVAL);
        }
        for (Player player : mods) {
            Schatzsuche.getInstance().addModPlayer(player);
            Schatzsuche.getInstance().addAllPlayer(player);
            player.getInventory().setItem(6, new StartItem());
            player.getInventory().setItem(7, new GlowItem());
            player.getInventory().setItem(8, new StopGame());
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setGameMode(GameMode.CREATIVE);
        }
    }

    public static void stopGame() {
        Stegisagt.setGameStatus(GameStatus.PLAYING);
        Stegisagt.getInstance().setPlayerVisibility(true);
        Config config = getInstance().getsConfig();
        Location loc1 = config.getFileConfiguration().getLocation("Schatzsuche.Box.Location.1");
        Location loc2 = config.getFileConfiguration().getLocation("Schatzsuche.Box.Location.2");
        if (loc1 == null || loc2 == null) {
            throw new RuntimeException("Some Locations not set!");
        }
        int maxY = (int) Math.max(loc1.getY(), loc2.getY());

        Schatzsuche.getInstance().clearGlow();
        ActionBarManager.hideActionBar();
        for (Player player : getInstance().getModPlayers()) {
            Stegisagt.getInstance().giveModItems(player);
        }
        for (Player player : getInstance().getAllPlayers()) {
            if (instance.isModPlayer(player)) {
                player.sendTitle("",
                        ChatColor.RED + "Teleport in 5 Sekunden.", 20, 30, 20);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Schatzsuche.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Stegisagt.teleportToAliveOrDead(player);
                    }
                }, 5 * 20L);
            } else if (instance.isFinishedPlayer(player)) {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
                player.sendTitle(ChatColor.GOLD + "Du hast es geschafft!",
                        ChatColor.RED + "Teleport in 5 Sekunden.", 20, 30, 20);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Schatzsuche.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Stegisagt.revivePlayer(player);
                    }
                }, 5 * 20L);
            } else {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
                player.sendTitle(ChatColor.DARK_RED + "Du hast es nicht geschafft!",
                        ChatColor.RED + "Teleport in 5 Sekunden.", 20, 30, 20);
                Location location = player.getLocation();
                location.setY(maxY + 1);
                player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Schatzsuche.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Stegisagt.killPlayer(player);
                    }
                }, 5 * 20L);
            }
        }
        Schatzsuche.getInstance().resetGame();
    }

    public void finishPlayer(Player player) {
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25F, 1F);
        for (Player p : getAllPlayers()) {
            p.sendMessage(getPrefix() + ChatColor.GRAY + player.getName() + ChatColor.YELLOW + " hat eine Schatztruhe gefunden. "
                    + ChatColor.GRAY + "[" + (getFinishedPlayerCount() + 1) + "/" + getMaxFinishPlayerCount() + "]");

        }
        Config config = getInstance().getsConfig();
        Location location = config.getFileConfiguration().getLocation("Schatzsuche.Spawn.Location.Finish");
        if (location == null) {
            throw new RuntimeException("Location not set!");
        }
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        setRestChestCount(getRestChestCount() - 1);
        addFinishedPlayer(player);
        removePlayingPlayer(player);
        if (getMaxFinishPlayerCount() == getFinishedPlayerCount() || getRestChestCount() == 0 ||
                getPlayingPlayerCount() == 0) {
            Schatzsuche.stopGame();
        }
    }

    public static void killPlayer(Player player) {
        Schatzsuche.getInstance().removeAllPlayer(player);
        Schatzsuche.getInstance().removePlayingPlayer(player);
        Schatzsuche.getInstance().removeFinishedPlayer(player);
    }

    private void hideChests() {
        int upperLimit = 5;
        int n = getChestCount();
        Config config = getInstance().getsConfig();
        Location loc1 = config.getFileConfiguration().getLocation("Schatzsuche.Box.Location.1");
        Location loc2 = config.getFileConfiguration().getLocation("Schatzsuche.Box.Location.2");
        if (loc1 == null || loc2 == null) {
            throw new RuntimeException("Some Locations not set!");
        }
        World world = loc1.getWorld();
        if (world == null) {
            throw new RuntimeException("World is null!");
        }

        int minX = (int) Math.min(loc1.getX(), loc2.getX());
        int minY = (int) Math.min(loc1.getY(), loc2.getY());
        int minZ = (int) Math.min(loc1.getZ(), loc2.getZ());
        int maxX = (int) Math.max(loc1.getX(), loc2.getX());
        int maxY = (int) Math.max(loc1.getY(), loc2.getY());
        int maxZ = (int) Math.max(loc1.getZ(), loc2.getZ());

        Random random = new Random();
        ArrayList<Location> chestLocations = new ArrayList<>();
        while (n > 0) {
            double randomX = random.nextInt(maxX + 1 - minX) + minX;
            double randomY = random.nextInt((maxY - upperLimit) + 1 - minY) + minY;
            double randomZ = random.nextInt(maxZ + 1 - minZ) + minZ;
            Location chestLocation = new Location(world, randomX, randomY, randomZ);
            if (!chestLocations.contains(chestLocation)) {
                chestLocations.add(new Location(world, randomX, randomY, randomZ));
                n--;
            }
        }
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    boolean chest = chestLocations.contains(new Location(world, x, y, z));
                    world.getBlockAt(x, y, z).setType(chest ? Material.CHEST : Material.SANDSTONE);
                }
            }
        }
    }

    private void resetGame() {
        setChestCount(0);
        setRestChestCount(0);
        setGlow(false);
        playingPlayers = new ArrayList<>();
        finishedPlayers = new ArrayList<>();
        modPlayers = new ArrayList<>();
        allPlayers = new ArrayList<>();
        shulkers = new ArrayList<>();
        killShulkerInArea();
        hideChests();
        countdown = 10;
        setStart(false);
    }

    public void glowChests() {
        setGlow(true);
        Config config = getInstance().getsConfig();
        Location loc1 = config.getFileConfiguration().getLocation("Schatzsuche.Box.Location.1");
        Location loc2 = config.getFileConfiguration().getLocation("Schatzsuche.Box.Location.2");
        if (loc1 == null || loc2 == null) {
            throw new RuntimeException("Some Locations not set!");
        }
        World world = loc1.getWorld();
        if (world == null) {
            throw new RuntimeException("World is null!");
        }

        int minX = (int) Math.min(loc1.getX(), loc2.getX());
        int minY = (int) Math.min(loc1.getY(), loc2.getY());
        int minZ = (int) Math.min(loc1.getZ(), loc2.getZ());
        int maxX = (int) Math.max(loc1.getX(), loc2.getX());
        int maxY = (int) Math.max(loc1.getY(), loc2.getY());
        int maxZ = (int) Math.max(loc1.getZ(), loc2.getZ());

        Scoreboard scoreboard = ScoreboardManager.setScoreboard();
        Team team = scoreboard.registerNewTeam("ShulkersGlowColor");
        team.setColor(ChatColor.GOLD);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType().equals(Material.CHEST)) {
                        Shulker entity = (Shulker) world.spawnEntity(block.getLocation(), EntityType.SHULKER, false);
                        entity.setGlowing(true);
                        entity.setInvulnerable(true);
                        entity.setAI(false);
                        entity.setInvisible(true);
                        entity.setSilent(true);
                        addShulker(entity);
                        team.addEntry(entity.getUniqueId().toString());
                    }
                }
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreboardManager.setTeam(p);
        }
    }

    public void clearGlow() {
        setGlow(false);
        for (Shulker entity : getShulkers()) {
            entity.setGlowing(false);
            entity.setHealth(0);
        }
        getShulkers().clear();
    }

    private void killShulkerInArea() {
        Config config = getInstance().getsConfig();
        Location loc1 = config.getFileConfiguration().getLocation("Schatzsuche.Box.Location.1");
        Location loc2 = config.getFileConfiguration().getLocation("Schatzsuche.Box.Location.2");
        if (loc1 == null || loc2 == null) {
            throw new RuntimeException("Some Locations not set!");
        }
        World world = loc1.getWorld();
        if (world == null) {
            throw new RuntimeException("World is null!");
        }

        int minX = (int) Math.min(loc1.getX(), loc2.getX());
        int minY = (int) Math.min(loc1.getY(), loc2.getY());
        int minZ = (int) Math.min(loc1.getZ(), loc2.getZ());
        int maxX = (int) Math.max(loc1.getX(), loc2.getX());
        int maxY = (int) Math.max(loc1.getY(), loc2.getY());
        int maxZ = (int) Math.max(loc1.getZ(), loc2.getZ());
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Shulker) {
                Location location = entity.getLocation().add(-0.5, 0, -0.5);
                boolean validX = (minX <= location.getX() && location.getX() <= maxX);
                boolean validY = (minY <= location.getY() && location.getY() <= maxY);
                boolean validZ = (minZ <= location.getZ() && location.getZ() <= maxZ);
                if (validX && validY && validZ) {
                    ((Shulker) entity).setHealth(0);
                }
            }
        }
    }

    public void countDown() {
        instance.countdown = 10;

        bukkitTask = Bukkit.getScheduler().runTaskTimer(instance, new Runnable() {
            @Override
            public void run() {
                for (Player player : instance.getAllPlayers()) {
                    if (instance.countdown <= 0) {
                        if (!isModPlayer(player)) {
                            player.getInventory().setItem(0, new MiningShovel());
                            setStart(true);
                            bukkitTask.cancel();
                        }
                    } else if (instance.countdown == 1 || instance.countdown == 2 || instance.countdown == 3 ||
                            instance.countdown == 5 || instance.countdown == 10) {
                        player.sendTitle(ChatColor.GOLD + "Start in:", ChatColor.RED.toString() + instance.countdown + " Sekunden",
                                5, 10, 5);
                    }
                }
                instance.countdown--;
            }
        }, 0, 20L);

    }


    public void setListenerSettings() {
        Stegisagt.getInstance().setPvp(false);
        Stegisagt.getInstance().setFallDamage(false);
        Stegisagt.getInstance().setHunger(false);
        Stegisagt.getInstance().setBuildPlace(false);
        Stegisagt.getInstance().setBuildBreak(false);
        Stegisagt.getInstance().setBlockDrop(false);
        Stegisagt.getInstance().setPlayerCollision(false);
        Stegisagt.getInstance().setEntityDrop(false);
        Stegisagt.getInstance().setPlayerVisibility(true);
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}
