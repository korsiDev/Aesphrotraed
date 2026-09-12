package me.korsidev.aesphrotraed.events;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.PlayerDataManager;
import me.korsidev.aesphrotraed.data.PlayerMemory;
import me.korsidev.aesphrotraed.scoreboard.ScoreboardManager;
import me.korsidev.aesphrotraed.util.NametagUtility;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GeneralEvents implements Listener {

    private final Aesphrotraed plugin;
    private final NametagUtility nametagUtility;
    private final PlayerDataManager playerDataManager;
    private final ScoreboardManager scoreboardManager;
    private final LuckPerms luckPerms;

    public GeneralEvents(
            Aesphrotraed plugin,
            PlayerDataManager playerDataManager
    ) {
        this.plugin = plugin;
        this.nametagUtility = plugin.getNametagUtility();
        this.playerDataManager = playerDataManager;
        this.scoreboardManager = plugin.getScoreboardManager();
        this.luckPerms = plugin.getLuckPerms();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerMemory memory = playerDataManager.loadPlayer(player);

        User user = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        String lpPrefix = "§e";
        if (user != null && user.getCachedData().getMetaData().getPrefix() != null) {
            lpPrefix = user.getCachedData().getMetaData().getPrefix();
        }

        String styledNameString = lpPrefix + player.getName();

        event.setJoinMessage("§a§l+ §r§8› §r" + styledNameString);

        if (memory == null) {
            player.kick(
                    Component.text("§c! §8› §cYour player data could not be loaded.")
            );
            return;
        }

        PlayerUtility.setPlayerMemory(player, memory);

        scoreboardManager.createScoreboard(player);

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> nametagUtility.createNametag(player),
                2L
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        User user = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        String lpPrefix = "§e";
        if (user != null && user.getCachedData().getMetaData().getPrefix() != null) {
            lpPrefix = user.getCachedData().getMetaData().getPrefix();
        }

        String styledNameString = lpPrefix + player.getName();

        event.setQuitMessage("§c§l- §r§8› §r" + styledNameString);

        nametagUtility.removeNametag(player);
        playerDataManager.savePlayer(player);

        PlayerUtility.setPlayerMemory(player, null);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> nametagUtility.createNametag(player),
                2L
        );
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        nametagUtility.removeNametag(player);

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> nametagUtility.createNametag(player),
                2L
        );
    }
}
