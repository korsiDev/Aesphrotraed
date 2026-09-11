package me.korsidev.aesphrotraed.util;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreboardManager {
    private final Aesphrotraed plugin;
    private final EconomyManager economyManager;

    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm");

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ScoreboardManager(Aesphrotraed plugin) {
        this.plugin = plugin;
        this.economyManager = plugin.getEconomyManager();
    }

    public void createScoreboard(Player player) {

        Scoreboard scoreboard =
                Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(
                "aesphrotraed",
                "dummy",
                ChatColor.GOLD + "" + ChatColor.BOLD + "AESPHROTRAED"
        );

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(scoreboard);

        updateScoreboard(player);
    }


    public void updateScoreboard(Player player) {

        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective("aesphrotraed");

        if (objective == null) {
            createScoreboard(player);
            return;
        }

        // Remove old entities
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        LocalDateTime now = LocalDateTime.now();

        String balance =
                economyManager.formatBalanceCompact(
                        economyManager.getBalance(player)
                );

        objective.getScore(
                " "
        ).setScore(11);

        objective.getScore(
                ChatColor.GREEN + "Purse"
        ).setScore(10);

        objective.getScore(
                ChatColor.GREEN + "" + ChatColor.ITALIC + "A$" + balance
        ).setScore(9);

        objective.getScore(
                " "
        ).setScore(8);

        objective.getScore(
                ChatColor.YELLOW + "Quest"
        ).setScore(7);

        objective.getScore(
                ChatColor.YELLOW + "" + ChatColor.ITALIC + "No active quest"
        ).setScore(6);

        objective.getScore(
                "  "
        ).setScore(5);

        objective.getScore(
                ChatColor.AQUA + "Level"
        ).setScore(4);

        objective.getScore(
                ChatColor.AQUA + "" + ChatColor.ITALIC + "1"
        ).setScore(3);

        objective.getScore(
                "   "
        ).setScore(2);

        objective.getScore(
                ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + now.format(timeFormatter)
        ).setScore(1);

        objective.getScore(
                ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + now.format(dateFormatter)
        ).setScore(0);

    }

    public void startUpdateTask() {

        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        updateScoreboard(player);
                    }

                },
                20L,
                20L
        );
    }

}
