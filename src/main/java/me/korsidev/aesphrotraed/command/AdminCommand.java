package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.PlayerMemory;
import me.korsidev.aesphrotraed.data.TitleData;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;

import java.util.Collections;
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

        if (!hasEnoughArguments(sender, args, 2)) {
            return;
        }

        String action = args[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "create" -> handleTitleCreate(sender, args);

            case "edit" -> handleTitleEdit(sender, args);

            default -> sender.sendMessage(
                    "§c! §8› §cUnknown title action. Use §ecreate, edit, delete, give §cor §etake§c."
            );
        }

    }

    // Title Create
    private void handleTitleCreate(CommandSender sender, String[] args) {

        if (!hasEnoughArguments(sender, args, 7)) {
            return;
        }

        String titleId = args[2].toLowerCase(Locale.ROOT);
        String display = args[3];
        String format = args[4];

        Boolean bold = parseBoolean(args[5]);
        Boolean italic = parseBoolean(args[6]);

        if (bold == null || italic == null) {
            sender.sendMessage(
                    "§c! §8› §cBold and italic must be §etrue §cor §efalse§c."
            );
            return;
        }

        if (!isValidTitleId(titleId)) {
            sender.sendMessage(
                    "§c! §8› §cInvalid title ID. Use only lowercase letters, numbers, §e- §cand §e_§c."
            );
            return;
        }

        if (plugin.getTitleRegistry().titleExists(titleId)) {
            sender.sendMessage(
                    "§c! §8› §cA title with the ID §e"
                            + titleId
                            + " §calready exists."
            );
            return;
        }

        if (!isValidTitleFormat(format)) {
            sender.sendMessage(
                    "§c! §8› §cInvalid title format."
            );
            return;
        }

        TitleData title = new TitleData(
                titleId,
                display,
                format,
                bold,
                italic
        );

        plugin.getTitleRegistry().registerTitle(title);

        sender.sendMessage(
                "§a✓ §8› §aCreated title §e"
                        + titleId
                        + "§a."
        );

    }

    private Boolean parseBoolean(String input) {

        if (input.equalsIgnoreCase("true")) {
            return true;
        }

        if (input.equalsIgnoreCase("false")) {
            return false;
        }

        return null;
    }

    private boolean isValidTitleId(String titleId) {

        return titleId.matches("[a-z0-9_-]+");
    }

    private boolean isValidTitleFormat(String format) {

        String lower = format.toLowerCase(Locale.ROOT);

        if (lower.matches("[a-z_]+")) {
            return true;
        }

        if (lower.matches("#[0-9a-f]{6}")) {
            return true;
        }

        return lower.matches(
                "gradient:#[0-9a-f]{6}:#[0-9a-f]{6}"
        );
    }

    // Title Edit
    private void handleTitleEdit(CommandSender sender, String[] args) {
        if (!hasEnoughArguments(sender, args, 7)) return;

        String titleId = args[2].toLowerCase(Locale.ROOT);
        String display = args[3];
        String format = args[4];

        Boolean bold = parseBoolean(args[5]);
        Boolean italic = parseBoolean(args[6]);

        if (bold == null || italic == null) {
            sender.sendMessage(
                    "§c! §8› §cBold and italic must be §etrue §cor §efalse§c."
            );
            return;
        }

        if (!plugin.getTitleRegistry().titleExists(titleId)) {
            sender.sendMessage(
                    "§c! §8› §cNo title with the ID §e" + titleId + " §cexists."
            );
            return;
        }

        if (!isValidTitleFormat(format)) {
            sender.sendMessage(
                    "§c! §8› §cInvalid title format."
            );
            return;
        }

        TitleData title = new TitleData(
                titleId,
                display,
                format,
                bold,
                italic
        );

        plugin.getTitleRegistry().registerTitle(title);

        sender.sendMessage(
                "§a✓ §8› §aUpdated title §e" + titleId + "§a."
        );
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
        if (!commandSender.hasPermission("aesphrotraed.admin")) {
            return Collections.emptyList();
        }

        if (strings.length == 1) {
            return filter(
                    strings[0],
                    "bal",
                    "xp",
                    "lvl",
                    "titles"
            );
        }

        switch (strings[0].toLowerCase()) {

            case "bal":
            case "xp":
            case "lvl":

                return completeStandardAdminCommand(strings);

            case "titles":

                return completeTitleCommand(strings);

            default:

                return Collections.emptyList();
        }
    }

    private List<String> completeStandardAdminCommand(String[] args) {

        if (args.length == 2) {
            return filter(
                    args[1],
                    "get",
                    "set",
                    "add",
                    "remove"
            );
        }

        if (args.length == 3) {
            return completePlayers(args[2]);
        }

        return Collections.emptyList();
    }

    private List<String> completeTitleCommand(String[] args) {

        if (args.length == 2) {
            return filter(
                    args[1],
                    "create",
                    "edit",
                    "delete",
                    "give",
                    "take"
            );
        }

        if (args.length == 3) {
            String action = args[1].toLowerCase(Locale.ROOT);

            switch (action) {
                case "delete":
                case "edit":
                    return plugin.getTitleRegistry()
                            .getAllTitleIds()
                            .stream()
                            .filter(id -> id.toLowerCase(Locale.ROOT)
                                    .startsWith(args[2].toLowerCase(Locale.ROOT)))
                            .sorted()
                            .toList();

                case "give":
                case "take":
                    return completePlayers(args[2]);

                case "create":
                    return filter(args[2], "<TitleId>");

                default:
                    return Collections.emptyList();
            }
        }

        if (args.length == 4) {
            String action = args[1].toLowerCase(Locale.ROOT);

            if (action.equals("create") || action.equals("edit")) {
                return filter(args[3], "<Display>");
            }

            if (action.equals("give")) {
                return plugin.getTitleRegistry()
                        .getAllTitleIds()
                        .stream()
                        .filter(id -> id.toLowerCase(Locale.ROOT)
                                .startsWith(args[3].toLowerCase(Locale.ROOT)))
                        .sorted()
                        .toList();
            }

            if (action.equals("take")) {
                return completeOwnedTitles(args[2], args[3]);
            }
        }

        if (args.length == 5) {
            String action = args[1].toLowerCase(Locale.ROOT);

            if (action.equals("create") || action.equals("edit")) {
                return filter(args[4],
                        "<Format>",
                        "black",
                        "dark_blue",
                        "dark_green",
                        "dark_aqua",
                        "dark_red",
                        "dark_purple",
                        "gold",
                        "gray",
                        "dark_gray",
                        "blue",
                        "green",
                        "aqua",
                        "red",
                        "light_purple",
                        "yellow",
                        "white",
                        "#FFFFFF",
                        "gradient:#FFFFFF:#000000");
            }
        }

        if (args.length == 6) {
            String action = args[1].toLowerCase(Locale.ROOT);

            if (action.equals("create") || action.equals("edit")) {
                return filter(
                        args[5],
                        "<Bold>",
                        "true",
                        "false"
                );
            }
        }

        if (args.length == 7) {
            String action = args[1].toLowerCase(Locale.ROOT);

            if (action.equals("create") || action.equals("edit")) {
                return filter(
                        args[6],
                        "<Italic>",
                        "true",
                        "false"
                );
            }
        }

        return Collections.emptyList();
    }
    private List<String> completeOwnedTitles(String playerName, String input) {

        Player player = Bukkit.getPlayerExact(playerName);

        if (player == null) {
            return Collections.emptyList();
        }

        PlayerMemory memory = PlayerUtility.getPlayerMemory(player);

        if (memory == null) {
            return Collections.emptyList();
        }

        String lowerInput = input.toLowerCase(Locale.ROOT);

        return memory.getOwnedTitles()
                .stream()
                .filter(titleId -> titleId.toLowerCase(Locale.ROOT)
                        .startsWith(lowerInput))
                .sorted()
                .toList();
    }

    private List<String> completePlayers(String input) {

        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .filter(name ->
                        name.toLowerCase(Locale.ROOT)
                                .startsWith(input.toLowerCase(Locale.ROOT))
                )
                .sorted()
                .toList();
    }

    private List<String> filter(
            String input,
            String... options
    ) {

        String lowerInput =
                input.toLowerCase(Locale.ROOT);

        return Arrays.stream(options)
                .filter(option ->
                        option.toLowerCase(Locale.ROOT)
                                .startsWith(lowerInput)
                )
                .toList();
    }
}
