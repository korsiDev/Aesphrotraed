package me.korsidev.aesphrotraed.events;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.PlayerDataManager;
import me.korsidev.aesphrotraed.data.PlayerMemory;
import me.korsidev.aesphrotraed.util.NametagUtility;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import me.korsidev.aesphrotraed.util.ScoreboardManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class GeneralEvents implements Listener {

    private final Aesphrotraed plugin;
    private final NametagUtility nametagUtility;
    private final PlayerDataManager playerDataManager;
    private final ScoreboardManager scoreboardManager;

    public GeneralEvents(
            Aesphrotraed plugin,
            PlayerDataManager playerDataManager
    ) {
        this.plugin = plugin;
        this.nametagUtility = plugin.getNametagUtility();
        this.playerDataManager = playerDataManager;
        this.scoreboardManager = plugin.getScoreboardManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerMemory memory = playerDataManager.loadPlayer(player);

        if (memory == null) {
            player.kick(
                    Component.text("Your player data could not be loaded.")
            );
            return;
        }

        PlayerUtility.setPlayerMemory(player, memory);

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> nametagUtility.createNametag(player),
                2L
        );
        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> scoreboardManager.createScoreboard(player),
                2L
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

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
