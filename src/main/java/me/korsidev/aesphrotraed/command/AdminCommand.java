package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.PlayerMemory;
import me.korsidev.aesphrotraed.progression.ProgressionManager;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdminCommand implements TabExecutor {
    private final Aesphrotraed plugin;

    public AdminCommand(Aesphrotraed plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (!commandSender.hasPermission("aesphrotraed.admin")) {
            commandSender.sendMessage("§c! §8› §cYou don't have permission to use this command.");
            return true;
        }

        if (strings.length == 0) {
            sendAdminHelp(commandSender);
            return true;
        }

        switch (strings[0].toLowerCase()) {

            case "balance", "bal" -> handleBalance(commandSender, strings);

            case "exp", "xp" -> handleExperience(commandSender, strings);

            case "level", "lvl" -> handleLevel(commandSender, strings);

            case "titles" -> handleTitles(commandSender, strings);

            default -> sendAdminHelp(commandSender);

        }

        return true;
    }

    private void sendAdminHelp(CommandSender sender) {

        sender.sendMessage(ChatColor.YELLOW + "/admin bal <get|set|add|remove> <player> <amount>");
        sender.sendMessage(ChatColor.YELLOW + "/admin xp <get|set|add|remove> <player> <amount>");
        sender.sendMessage(ChatColor.YELLOW + "/admin lvl <get|set|add|remove> <player> <level>");

    }

    private void handleBalance(CommandSender sender, String[] args) {
        if (!hasEnoughArguments(sender, args, 3)) {
            return;
        }

        String action = args[1].toLowerCase();
        Player target = getTargetPlayer(sender, args[2]);

        if (target == null) {
            return;
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(target);

        if (memory == null) {
            sender.sendMessage("§c! §8› §cCould not load player data.");
            return;
        }

        switch (action) {

            case "get" -> {
                sender.sendMessage("§a! §8› §e"
                        + target.getName()
                        + "'s §apurse: §e◆ "
                        + plugin.getEconomyManager()
                            .formatBalanceFull(memory.getBalance())
                );
            }

            case "set" -> {
                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                Long amount = plugin.getEconomyManager().parseAmount(args[3]);

                if (amount == null) {
                    return;
                }

                memory.setBalance(amount);

                sender.sendMessage("§a✓ §8› §aSet §e" + target.getName()
                + "'s§a balance to §e◆ "
                + plugin.getEconomyManager().formatBalanceFull(amount));

                plugin.getNametagUtility().updateNametag(target);
            }

            case "add" -> {

                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                Long amount = plugin.getEconomyManager().parseAmount(args[3]);

                if (amount == null) {
                    return;
                }

                plugin.getEconomyManager()
                        .addBalance(target, amount);

                sender.sendMessage(
                        "§a✓ §8› §aAdded §e◆ "
                                + plugin.getEconomyManager()
                                .formatBalanceFull(amount)
                                + " §ato §e"
                                + target.getName()
                                + "§a."
                );
                plugin.getNametagUtility().updateNametag(target);
            }

            case "remove" -> {

                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                Long amount = plugin.getEconomyManager().parseAmount(args[3]);

                if (amount == null) {
                    return;
                }

                boolean success =
                        plugin.getEconomyManager()
                                .removeBalance(target, amount);

                if (!success) {

                    sender.sendMessage("§c! §8› §cThe player does not have enough money.");

                    return;
                }

                sender.sendMessage("§a✓ §8› §aRemoved §e◆ "
                                + plugin.getEconomyManager()
                                .formatBalanceFull(amount)
                                + " §afrom§e "
                                + target.getName()
                                + "§a."
                );
                plugin.getNametagUtility().updateNametag(target);
            }

            default -> sender.sendMessage("§c! §8› §cUnknown balance action. Use §eget, set, add §cor §eremove."
            );
        }
    }

    private void handleExperience(CommandSender sender, String[] args) {

        if (!hasEnoughArguments(sender, args, 3)) {
            return;
        }

        String action = args[1].toLowerCase();

        Player target = Bukkit.getPlayerExact(args[2]);

        if (target == null) {
            return;
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(target);

        if (memory == null) {
            sender.sendMessage(
                    "§c! §8› §cCould not load player data."
            );
            return;
        }

        switch (action) {

            case "get" -> {
                int level = plugin.getProgressionManager().getLevel(memory);

                sender.sendMessage(
                        "§a! §8› §e"
                                + target.getName()
                                + "'s §aXP: §5◇ "
                                + plugin.getProgressionManager()
                                .formatExperience(memory.getExperience())
                                + " §8(§fLevel "
                                + plugin.getProgressionManager()
                                .formatLevel(level)
                                + "§8)"
                );
            }

            case "set" -> {

                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                Long amount = plugin.getProgressionManager().parseExperience(args[3]);

                if (amount == null) {
                    sender.sendMessage(
                            "§c! §8› §cInvalid XP amount: §e" + args[3]
                    );
                    return;
                }

                plugin.getProgressionManager()
                        .setExperience(target, amount);

                sender.sendMessage(
                        "§a✓ §8› §aSet §e"
                                + target.getName()
                                + "'s §aXP to §d"
                                + plugin.getProgressionManager()
                                .formatExperience(amount)
                                + "§a."
                );
            }

            case "add" -> {

                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                Long amount = plugin.getProgressionManager().parseExperience(args[3]);

                if (amount == null) {
                    sender.sendMessage(
                            "§c! §8› §cInvalid XP amount: §e" + args[3]
                    );
                    return;
                }

                plugin.getProgressionManager()
                        .addExperience(target, amount);

                sender.sendMessage(
                        "§a✓ §8› §aAdded §d◇ "
                                + plugin.getProgressionManager()
                                .formatExperience(amount)
                                + " §aXP to §e"
                                + target.getName()
                                + "§a."
                );
            }

            case "remove" -> {

                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                Long amount = plugin.getProgressionManager().parseExperience(args[3]);

                if (amount == null) {
                    sender.sendMessage(
                            "§c! §8› §cInvalid XP amount: §e" + args[3]
                    );
                    return;
                }

                plugin.getProgressionManager()
                        .removeExperience(target, amount);

                sender.sendMessage(
                        "§a✓ §8› §aRemoved §d◇ "
                                + plugin.getProgressionManager()
                                .formatExperience(amount)
                                + " §aXP from §e"
                                + target.getName()
                                + "§a."
                );
            }

            default -> sender.sendMessage(
                    "§c! §8› §cUnknown XP action. Use §eget, set, add §cor §eremove§c."
            );

        }

    }

    private void handleLevel(CommandSender sender, String[] args) {

        if (!hasEnoughArguments(sender, args, 3)) {
            return;
        }

        String action = args[1].toLowerCase();

        Player target = Bukkit.getPlayerExact(args[2]);

        if (target == null) {
            return;
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(target);

        if (memory == null) {
            sender.sendMessage(
                    "§c! §8› §cCould not load player data."
            );
            return;
        }

        switch (action) {

            case "get" -> {

                int level =
                        plugin.getProgressionManager()
                                .getLevel(memory);

                sender.sendMessage(
                        "§a! §8› §e"
                                + target.getName()
                                + "'s §alevel: "
                                + plugin.getProgressionManager().formatLevel(level)
                );
            }

            case "set" -> {

                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                int level;

                try {
                    level = Integer.parseInt(args[3]);
                } catch (NumberFormatException exception) {
                    sender.sendMessage(
                            "§c! §8› §cInvalid level."
                    );
                    return;
                }

                if (level < 1) {
                    sender.sendMessage(
                            "§c! §8› §cLevel must be at least 1."
                    );
                    return;
                }

                long experience =
                        plugin.getProgressionManager()
                                .getTotalExperienceForLevel(level);

                plugin.getProgressionManager()
                        .setExperience(target, experience);

                sender.sendMessage(
                        "§a✓ §8› §aSet §e"
                                + target.getName()
                                + "'s §alevel to "
                                + plugin.getProgressionManager().formatLevel(level)
                                + "§a."
                );
                plugin.getNametagUtility().updateNametag(target);
            }

            case "add" -> {

                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                int amount;

                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException exception) {
                    sender.sendMessage("§c! §8› §cInvalid level amount.");
                    return;
                }

                if (amount <= 0) {
                    sender.sendMessage("§c! §8› §cAmount must be greater than 0.");
                    return;
                }

                plugin.getProgressionManager()
                        .addLevels(target, amount);

                sender.sendMessage(
                        "§a✓ §8› §aAdded §5"
                                + amount
                                + " §alevels to §e"
                                + target.getName()
                                + "§a."
                );
            }

            case "remove" -> {

                if (!hasEnoughArguments(sender, args, 4)) {
                    return;
                }

                int amount;

                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException exception) {
                    sender.sendMessage("§c! §8› §cInvalid level amount.");
                    return;
                }

                if (amount <= 0) {
                    sender.sendMessage("§c! §8› §cAmount must be greater than 0.");
                    return;
                }

                plugin.getProgressionManager()
                        .removeLevels(target, amount);

                sender.sendMessage(
                        "§a✓ §8› §aRemoved §5"
                                + amount
                                + " §alevels from §e"
                                + target.getName()
                                + "§a."
                );
            }

        }

    }

    private void handleTitles(CommandSender sender, String[] args) {

    }

    private Player getTargetPlayer(CommandSender sender, String name) {
        Player player = Bukkit.getPlayerExact(name);

        if (player == null) {
            sender.sendMessage("§c! §8› §cPlayer not found.");
        }

        return player;
    }

    private boolean hasEnoughArguments(CommandSender sender, String[] args, int required) {
        if (args.length < required) {
            sender.sendMessage("§c! §8› §cNot enough arguments.");
            sendAdminHelp(sender);
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
