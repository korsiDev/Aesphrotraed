package me.korsidev.aesphrotraed.data;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class PlayerDataManager {
    private final Aesphrotraed plugin;

    public PlayerDataManager(Aesphrotraed plugin) {
        this.plugin = plugin;
    }

    private void createDefaultData(PlayerMemory memory) {
        memory.setBalance(100);
        memory.setExperience(0);

        memory.setCurrentStreak(0);
        memory.setLongestStreak(0);
        memory.setTotalGamesPlayed(0);
        memory.setTotalLoss(0);
        memory.setTotalWagered(0);
        memory.setTotalWon(0);

        memory.setFriends(new ArrayList<>());

        memory.setOwnedTitles(new ArrayList<>());
        memory.getOwnedTitles().add("newbie");

        memory.setEquippedTitle("newbie");
    }

    public PlayerMemory loadPlayer(Player player) {
        File playerFolder = new File(PlayerUtility.getFolderPath(player));

        if (!playerFolder.exists() && !playerFolder.mkdirs()) {
            plugin.getLogger().severe(
                    "Could not create player folder for " + player.getName()
            );
            return null;
        }

        File file = new File(playerFolder, "general.yml");

        PlayerMemory memory = new PlayerMemory();

        if (!file.exists()) {
            createDefaultData(memory);
            savePlayer(player, memory);
            return memory;
        }

        FileConfiguration config =
                YamlConfiguration.loadConfiguration(file);

        loadData(memory, config);

        return memory;
    }

    private void loadData(PlayerMemory memory, FileConfiguration config) {
        memory.setBalance(config.getLong("stats.balance", 100));
        memory.setExperience(config.getLong("stats.experience", 0));

        memory.setCurrentStreak(
                config.getInt("stats.currentStreak", 0)
        );
        memory.setLongestStreak(
                config.getInt("stats.longestStreak", 0)
        );
        memory.setTotalGamesPlayed(
                config.getInt("stats.totalGamesPlayed", 0)
        );
        memory.setTotalLoss(
                config.getLong("stats.totalLoss", 0)
        );
        memory.setTotalWagered(
                config.getLong("stats.totalWagered", 0)
        );
        memory.setTotalWon(
                config.getLong("stats.totalWon", 0)
        );

        memory.setFriends(
                config.getStringList("other.friends")
        );

        memory.setOwnedTitles(
                config.getStringList("titles.ownedTitles")
        );

        if (memory.getOwnedTitles().isEmpty()) {
            memory.getOwnedTitles().add("newbie");
        }

        memory.setEquippedTitle(
                config.getString("titles.equippedTitle", "newbie")
        );
    }

    public void savePlayer(Player player) {
        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return;
        }

        savePlayer(player, memory);
    }
    public void savePlayer(Player player, PlayerMemory memory) {
        File playerFolder =
                new File(PlayerUtility.getFolderPath(player));

        if (!playerFolder.exists() && !playerFolder.mkdirs()) {
            plugin.getLogger().severe(
                    "Could not create player folder for " + player.getName()
            );
            return;
        }

        File file = new File(playerFolder, "general.yml");

        FileConfiguration config =
                YamlConfiguration.loadConfiguration(file);

        config.set("stats.balance", memory.getBalance());
        config.set("stats.experience", memory.getExperience());

        config.set("stats.currentStreak", memory.getCurrentStreak());
        config.set("stats.longestStreak", memory.getLongestStreak());
        config.set("stats.totalGamesPlayed", memory.getTotalGamesPlayed());
        config.set("stats.totalLoss", memory.getTotalLoss());
        config.set("stats.totalWagered", memory.getTotalWagered());
        config.set("stats.totalWon", memory.getTotalWon());

        config.set("other.friends", memory.getFriends());

        config.set("titles.ownedTitles", memory.getOwnedTitles());
        config.set("titles.equippedTitle", memory.getEquippedTitle());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to save data for " + player.getName(),
                    e
            );
        }
    }

    public void saveAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            savePlayer(player);
        }
    }

}
