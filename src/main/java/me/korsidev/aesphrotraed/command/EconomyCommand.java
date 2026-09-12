package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.EconomyManager;
import me.korsidev.aesphrotraed.util.NametagUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EconomyCommand implements TabExecutor {

    private final Aesphrotraed plugin;
    private final EconomyManager economyManager;
    private final NametagUtility nametagUtility;

    public EconomyCommand(Aesphrotraed plugin) {
        this.plugin = plugin;
        this.economyManager = plugin.getEconomyManager();
        this.nametagUtility = plugin.getNametagUtility();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§c! §8› §cThis command can only be used by players.");
            return true;
        }

        if(strings.length == 0) {
            commandSender.sendMessage("§c! §8› §cUsage: §e/economy <balance|add|remove|set> <value>");
            return true;
        }

        switch (strings[0].toLowerCase()) {

            case "balance", "bal" -> {

                long balance = economyManager.getBalance(player);

                player.sendMessage("§6Your balance: §aA$" + economyManager.formatBalanceFull(balance));

            }

            case "add" -> {

                if (strings.length != 2) {
                    player.sendMessage("§cUsage: /economy add <value>");
                    return true;
                }

                Long amount = economyManager.parseAmount(strings[1]);

                if (amount == null || amount <= 0) {
                    player.sendMessage("§cPlease enter a valid positive amount. §o('" + strings[1] + "').");
                    return true;
                }

                economyManager.addBalance(player, amount);

                player.sendMessage("§aAdded §oA$" + economyManager.formatBalanceFull(amount) + "§r§a.");
                player.sendMessage("§aNew balance: §oA$" + economyManager.formatBalanceFull(economyManager.getBalance(player)) + "§r§a.");

                nametagUtility.updateNametag(player);

            }

            case "remove" -> {
                if (strings.length != 2) {
                    player.sendMessage("§cUsage: /economy remove <amount>");
                    return true;
                }

                Long amount = economyManager.parseAmount(strings[1]);

                if (amount == null || amount <= 0) {
                    player.sendMessage("§cPlease enter a valid positive amount. §o('" + strings[1] + "').");
                    return true;
                }

                if (!economyManager.removeBalance(player, amount)) {
                    player.sendMessage("§cYou do not have enough money.");
                    return true;
                }

                player.sendMessage(
                        "§cRemoved §a§oA$" +
                                economyManager.formatBalanceFull(amount) +
                                "§c."
                );

                player.sendMessage(
                        "§aNew balance: §a§oA$" +
                                economyManager.formatBalanceFull(
                                        economyManager.getBalance(player)
                                ) + "§r§a."
                );

                nametagUtility.updateNametag(player);

            }

            case "set" -> {

                if (strings.length != 2) {
                    player.sendMessage("§cUsage: /economy set <amount>");
                    return true;
                }

                Long amount = economyManager.parseAmount(strings[1]);

                if (amount == null || amount < 0) {
                    player.sendMessage("§cPlease enter a valid amount. §o('" + strings[1] + "').");
                    return true;
                }

                economyManager.setBalance(player, amount);

                player.sendMessage(
                        "§aBalance set to §oA$" +
                                economyManager.formatBalanceFull(amount) +
                                "§r§a."
                );

                nametagUtility.updateNametag(player);

            }

            default -> player.sendMessage("§cUsage: /economy <balance|add|remove|set> <value>");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            completions.add("balance");
            completions.add("add");
            completions.add("remove");
            completions.add("set");
        }

        return completions;
    }
}
