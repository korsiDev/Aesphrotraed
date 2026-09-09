package me.korsidev.aesphrotraed.data;

import me.korsidev.aesphrotraed.util.PlayerUtility;
import org.bukkit.entity.Player;

import java.util.Locale;

public class EconomyManager {

    public long getBalance(Player player) {
        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return 0;
        }

        return memory.getBalance();
    }

    public void setBalance(Player player, long amount) {
        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return;
        }

        if (amount < 0) {
            amount = 0;
        }

        memory.setBalance(amount);
    }

    public void addBalance(Player player, long amount) {
        if (amount < 0) {
            return;
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return;
        }

        memory.setBalance(memory.getBalance() + amount);
    }

    public boolean removeBalance(Player player, long amount) {
        if (amount < 0) {
            return false;
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return false;
        }

        if (memory.getBalance() < amount) {
            return false;
        }

        memory.setBalance(memory.getBalance() - amount);
        return true;
    }

    public boolean hasBalance(Player player, long amount) {
        if (amount < 0) {
            return false;
        }

        return getBalance(player) >= amount;
    }

    private String formatCompact(long amount, long divisor, String suffix) {
        double value = (double) amount / divisor;

        String formatted = String.format(Locale.US, "%.2f", value)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");

        return formatted + suffix;
    }

    public String formatBalance(long amount) {
        if (amount < 100_000) {
            return String.format("%d", amount);
        }

        if (amount < 1_000_000) {
            return formatCompact(amount, 1_000, "k");
        }

        if (amount < 1_000_000_000) {
            return formatCompact(amount, 1_000_000, "m");
        }

        return formatCompact(amount, 1_000_000_000, "b");
    }

}
