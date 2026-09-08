package me.korsidev.aesphrotraed.data;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class PlayerDataManager implements Listener {
    private final Aesphrotraed plugin;

    public PlayerDataManager(Aesphrotraed plugin) {
        this.plugin = plugin;
    }

    private void createDefaultData(PlayerMemory memory) {
        memory.setBalance(100);

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

    /*public void savePlayer(Player player) {
        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);
        File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);

        cfg.set("stats.coins", memory.getBalance());
        cfg.set("stats.currentStreak", memory.getCurrentStreak());
        cfg.set("stats.longestStreak", memory.getLongestStreak());
        cfg.set("stats.totalGamesPlayed", memory.getTotalGamesPlayed());
        cfg.set("stats.totalLoss", memory.getTotalLoss());
        cfg.set("stats.totalWagered", memory.getTotalWagered());
        cfg.set("stats.totalWon", memory.getTotalWon());
        cfg.set("other.friends", memory.getFriends());
        cfg.set("titles.ownedTitles", memory.getOwnedTitles());
        cfg.set("titles.equippedTitle", memory.getEquippedTitle());

        try {
            cfg.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PlayerUtility.setPlayerMemory(player, null);
    }

    public void loadPlayer(Player player) {
        PlayerMemory memory = new PlayerMemory();
        File f = new File(PlayerUtility.getFolderPath(player) + "/general.yml");

        File playerFolder = new File(PlayerUtility.getFolderPath(player));

        if (!playerFolder.exists() && !playerFolder.mkdirs()) {
            plugin.getLogger().severe(
                    "Could not create player folder for " + player.getName()
            );
        }

        if(f.exists()){
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            memory.setBalance(cfg.getLong("stats.balance"));
            memory.setCurrentStreak(cfg.getInt("stats.currentStreak"));
            memory.setLongestStreak(cfg.getInt("stats.longestStreak"));
            memory.setTotalGamesPlayed(cfg.getInt("stats.totalGamesPlayed"));
            memory.setTotalLoss(cfg.getLong("stats.totalLoss"));
            memory.setTotalWagered(cfg.getLong("stats.totalWagered"));
            memory.setTotalWon(cfg.getLong("stats.totalWon"));
            memory.setFriends(cfg.getStringList("other.friends"));
            memory.setOwnedTitles(cfg.getStringList("titles.ownedTitles"));
            if (!memory.getOwnedTitles().contains("newbie")) {
                memory.getOwnedTitles().add("newbie");
            }

            memory.setEquippedTitle(cfg.getString("titles.equippedTitle"));
        } else {
            memory.setBalance(100);
            memory.setCurrentStreak(0);
            memory.setLongestStreak(0);
            memory.setTotalGamesPlayed(0);
            memory.setTotalLoss(0);
            memory.setTotalWagered(0);
            memory.setTotalWon(0);
            memory.setFriends(new ArrayList<>());
            memory.setOwnedTitles(new ArrayList<>());
            memory.getOwnedTitles().add("newbie");
            memory.setEquippedTitle("§aNewbie");
        }

        PlayerUtility.setPlayerMemory(player, memory);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getNametagUtility().createNametag(player);
        }, 2L);

    }*/

}
