package me.korsidev.aesphrotraed.progression;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.PlayerMemory;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ProgressionManager {

    private final Aesphrotraed plugin;
    double BASE_XP;
    double MULTIPLIER;

    public ProgressionManager(Aesphrotraed plugin) {
        this.plugin = plugin;
        BASE_XP = plugin.getConfig().getDouble("progression.base-xp", 1500);
        MULTIPLIER = plugin.getConfig().getDouble("progression.multiplier", 1.08);
    }


    public int getLevel(PlayerMemory memory) {

        long experience = memory.getExperience();

        if (experience <= 0) {
            return 1;
        }

        if (MULTIPLIER == 1.0) {
            return (int) Math.floor(experience / BASE_XP) + 1;
        }

        double level = 1
                + Math.log(
                1 + experience * (MULTIPLIER - 1) / BASE_XP
        ) / Math.log(MULTIPLIER);

        return Math.max(1, (int) Math.floor(level));
    }

    public long getRequiredExperience(int level) {
        if (level < 1) {
            return 0;
        }

        return Math.round(
                BASE_XP * Math.pow(MULTIPLIER, level - 1)
        );
    }

    public long getTotalExperienceForLevel(int level) {

        if (level <= 1) {
            return 0;
        }

        if (MULTIPLIER == 1.0) {
            return Math.round(BASE_XP * (level - 1));
        }

        return Math.round(
                BASE_XP
                        * (Math.pow(MULTIPLIER, level - 1) - 1)
                        / (MULTIPLIER - 1)
        );
    }

    public long getExperienceInCurrentLevel(PlayerMemory memory) {

        int level = getLevel(memory);

        return memory.getExperience()
                - getTotalExperienceForLevel(level);
    }

    public long getExperienceRequiredForCurrentLevel(PlayerMemory memory) {

        int level = getLevel(memory);

        return getRequiredExperience(level);
    }

    public void addExperience(Player player, long amount) {

        if (amount <= 0) {
            return;
        }

        PlayerMemory memory =
                PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return;
        }

        int oldLevel = getLevel(memory);

        long newExperience =
                memory.getExperience() + amount;

        memory.setExperience(newExperience);

        int newLevel = getLevel(memory);

        if (newLevel > oldLevel) {
            handleLevelUp(player, oldLevel, newLevel);
        }
    }

    public void setExperience(Player player, long amount) {

        PlayerMemory memory =
                PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return;
        }

        int oldLevel = getLevel(memory);

        memory.setExperience(Math.max(0, amount));

        int newLevel = getLevel(memory);

        if (newLevel > oldLevel) {
            handleLevelUp(player, oldLevel, newLevel);
        }
    }

    public void removeExperience(Player player, long amount) {
        if (amount <= 0) {
            return;
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if(memory == null) {
            return;
        }

        memory.setExperience(
                Math.max(0, memory.getExperience() - amount)
        );
    }

    public void addLevels(Player player, int amount) {

        if (amount <= 0) {
            return;
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return;
        }

        int currentLevel = getLevel(memory);
        int newLevel = currentLevel + amount;

        long experience = getTotalExperienceForLevel(newLevel);

        setExperience(player, experience);
        plugin.getNametagUtility().updateNametag(player);
    }

    public void removeLevels(Player player, int amount) {

        if (amount <= 0) {
            return;
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return;
        }

        int currentLevel = getLevel(memory);
        int newLevel = Math.max(1, currentLevel - amount);

        long experience = getTotalExperienceForLevel(newLevel);

        setExperience(player, experience);
        plugin.getNametagUtility().updateNametag(player);
    }

    private void handleLevelUp(Player player, int oldLevel, int newLevel) {

        for (int level = oldLevel + 1; level <= newLevel; level++) {

            player.sendMessage(
                    "§d✦ §fYou reached level " + plugin.getProgressionManager().formatLevel(level) + "§f!"
            );
            player.playSound(Sound.sound(Key.key("block.amethyst_cluster.hit"), Sound.Source.MASTER, 0.8f, 1.2f));
            plugin.getNametagUtility().updateNametag(player);
        }

    }

    public String formatExperience(long experience) {
        if (experience < 1_000) {
            return String.valueOf(experience);
        }

        if (experience < 1_000_000) {
            double value = experience / 1_000.0;

            if (value % 1 == 0) {
                return String.format(Locale.US, "%.0fK", value);
            }

            return String.format(Locale.US, "%.1fK", value);
        }

        double value = experience / 1_000_000.0;

        if (value % 1 == 0) {
            return String.format(Locale.US, "%.0fM", value);
        }

        return String.format(Locale.US, "%.2fM", value);
    }

    public Long parseExperience(String input) {

        try {

            String value = input.toLowerCase();

            double multiplier = 1;

            if (value.endsWith("k")) {
                multiplier = 1_000;
                value = value.substring(0, value.length() - 1);

            } else if (value.endsWith("m")) {
                multiplier = 1_000_000;
                value = value.substring(0, value.length() - 1);
            }

            double number = Double.parseDouble(value);

            if (number < 0) {
                throw new NumberFormatException();
            }

            return Math.round(number * multiplier);

        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public String formatLevel(int level) {
        if (level < 5) {
            return "§f" + level;
        }
        if (level < 25) {
            return "§6" + level;
        }
        if (level < 50) {
            return "§5" + level;
        }
        if (level < 75) {
            return "§d" + level;
        }
        return "§b" + level;
    }
}