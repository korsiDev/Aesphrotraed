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

    public String formatBalanceCompact(long amount) {
        if (amount < 100_000) {
            return String.format("%,d", amount);
        }

        if (amount < 1_000_000) {
            return formatCompact(amount, 1_000, "K");
        }

        if (amount < 1_000_000_000) {
            return formatCompact(amount, 1_000_000, "M");
        }

        return formatCompact(amount, 1_000_000_000, "B");
    }

    public String formatBalanceFull(long amount) {
        return String.format(Locale.GERMANY, "%,d", amount);
    }

    public Long parseAmount(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        input = input.trim().toLowerCase(Locale.US);

        double multiplier = 1;

        if (input.endsWith("k")) {
            multiplier = 1_000;
            input = input.substring(0, input.length() - 1);
        } else if (input.endsWith("m")) {
            multiplier = 1_000_000;
            input = input.substring(0, input.length() - 1);
        } else if (input.endsWith("b")) {
            multiplier = 1_000_000_000;
            input = input.substring(0, input.length() - 1);
        }

        try {

            double value = Double.parseDouble(input);
            double result = value * multiplier;

            if (result < 0 || result > Long.MAX_VALUE) {
                return null;
            }

            return (long) result;

        } catch(NumberFormatException e) {
            return null;
        }

    }

    public boolean transfer(Player from, Player to, long amount) {
        if (from == null || to == null) {
            return false;
        }

        if (amount <= 0) {
            return false;
        }

        if (!hasBalance(from, amount)) {
            return false;
        }

        removeBalance(from, amount);
        addBalance(to, amount);

        return true;

    }

}
