package me.korsidev.aesphrotraed.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.PlayerMemory;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreboardManager {

    private static final String OBJECTIVE_NAME = "aesphrotraed";

    private final Aesphrotraed plugin;

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("HH:mm MM/dd/yy");

    public ScoreboardManager(Aesphrotraed plugin) {
        this.plugin = plugin;
    }

    public void createScoreboard(Player player) {

        Scoreboard scoreboard =
                Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(
                OBJECTIVE_NAME,
                Criteria.DUMMY,
                Component.text("Aesphrotraed", NamedTextColor.GOLD)
        );

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(scoreboard);

        updateScoreboard(player);
    }

    private void updateScoreboard(Player player) {

        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        if (objective == null) {
            createScoreboard(player);
            return;
        }

        clearScoreboard(scoreboard);

        PlayerMemory memory =
                PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return;
        }

        int score = 10;

        addLine(objective, " ", score--);

        // Purse
        addLine(
                objective,
                "§8› §fPurse: §e◆ " +
                        plugin.getEconomyManager()
                                .formatBalanceCompact(memory.getBalance()),
                score--
        );

        addLine(objective, "  ", score--);

        addLine(objective, "§8› §fQuest", score--);

        addLine(
                objective,
                "§7No active quest.",
                score--
        );

        addLine(objective, "   ", score--);

        addLine(
                objective,
                "§8› §fLevel §51",
                score--
        );

        addLine(
                objective,
                "§5◇ §d100 §8/ §d1.5K",
                score--
        );

        addLine(objective, "    ", score--);

        addLine(
                objective,
                "§8" + LocalDateTime.now().format(dateTimeFormatter),
                score
        );
    }

    private void addLine(
            Objective objective,
            String text,
            int scoreValue
    ) {

        Score score = objective.getScore(text);

        score.setScore(scoreValue);

        score.numberFormat(NumberFormat.blank());
    }

    private void clearScoreboard(Scoreboard scoreboard) {

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(
                Bukkit.getScoreboardManager().getNewScoreboard()
        );
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
