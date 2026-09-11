package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.EconomyManager;
import me.korsidev.aesphrotraed.util.NametagUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PayCommand implements TabExecutor {
    private final EconomyManager economyManager;
    private final NametagUtility nametagUtility;

    public PayCommand(Aesphrotraed plugin) {
        this.economyManager = plugin.getEconomyManager();
        this.nametagUtility = plugin.getNametagUtility();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        if (strings.length != 2) {
            player.sendMessage("§cUsage: /pay <player> <amount>.");
            return true;
        }

        Player target = Bukkit.getPlayerExact(strings[0]);

        if (target == null) {
            player.sendMessage("§c" + strings[0] + " is not online.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cYou cannot pay yourself.");
            return true;
        }

        Long amount = economyManager.parseAmount(strings[1]);

        if (amount == null || amount <= 0) {
            player.sendMessage("§cPlease enter a valid amount.");
            return true;
        }

        if (!economyManager.hasBalance(player, amount)) {
            player.sendMessage("§cYou do not have enough money.");
            return true;
        }

        if(!economyManager.transfer(player, target, amount)) {
            player.sendMessage("§cThe transaction could not be completed!");
            return true;
        }

        String formattedAmount = economyManager.formatBalanceFull(amount);

        player.sendMessage("§aYou paid §a§oA$" + amount + " §ato §e" + target.getName());
        target.sendMessage("§e" + player.getName() + " §asent you §oA$" + amount);

        nametagUtility.updateNametag(player);
        nametagUtility.updateNametag(target);

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (strings.length != 1) {
            return List.of();
        }

        String input = strings[0].toLowerCase();

        List<String> completions = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (commandSender instanceof Player senderPlayer && player.equals(senderPlayer)) {
                continue;
            }

            if (player.getName().toLowerCase().startsWith(input)) {
                completions.add(player.getName());
            }
        }

        return completions;

    }
}
