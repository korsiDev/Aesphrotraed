package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.PlayerDataManager;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import me.korsidev.aesphrotraed.util.TitleRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerTitlesCommand implements TabExecutor {
    private final Aesphrotraed plugin;
    private final TitleRegistry registry;
    private final PlayerDataManager playerDataManager;

    public ServerTitlesCommand(Aesphrotraed plugin, TitleRegistry registry, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.registry = registry;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!commandSender.hasPermission("aesphrotraed.serverTitles")) {
            commandSender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (strings.length < 2) {
            commandSender.sendMessage("§cUsage: /servertitles <give|take> <args>");
            return true;
        }

        String subCommand = strings[0].toLowerCase();
        String titleId = strings[1].toLowerCase();

        switch (subCommand) {
            case "give":
            case "take":
                if (strings.length < 3) {
                    commandSender.sendMessage("§cUsage: /servertitles " + subCommand + " <id> <player>");
                    return true;
                }

                if (!registry.titleExists(titleId)) {
                    commandSender.sendMessage("§cThat title does not exist.");
                    return true;
                }

                Player target = Bukkit.getPlayer(strings[2]);
                if (target == null) {
                    commandSender.sendMessage("§cPlayer not found.");
                    return true;
                }

                // Interact with players data
                var memory = PlayerUtility.getPlayerMemory(target);
                if (subCommand.equals("give")) {
                    if (memory.getOwnedTitles().contains(titleId)) {
                        commandSender.sendMessage("§c" + target.getName() + " already owns that title.");
                        return true;
                    }
                    memory.getOwnedTitles().add(titleId);
                    commandSender.sendMessage("§aGave title '" + titleId + "' to " + target.getName());

                    playerDataManager.savePlayer(target);

                } else {
                    if(!memory.getOwnedTitles().contains(titleId)) {
                        commandSender.sendMessage("§c" + target.getName() + " does not own that title.");
                        return true;
                    }
                    memory.getOwnedTitles().remove(titleId);

                    // If they have it equipped, unequip
                    if(memory.getEquippedTitle() != null && memory.getEquippedTitle().equals(titleId)) {
                        memory.setEquippedTitle("newbie");
                    }
                    plugin.getNametagUtility().updateNametag(target);

                    playerDataManager.savePlayer(target);

                    commandSender.sendMessage("§aTook title '" + titleId + "' from " + target.getName());
                }
                break;

            default:
                commandSender.sendMessage("§cUnknown subcommand");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(strings.length == 1) {
            return Arrays.asList("give", "take").stream()
                    .filter(x -> x.startsWith(strings[0].toLowerCase())).collect(Collectors.toList());
        }
        if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("give") || strings[0].equalsIgnoreCase("take")) {
                return registry.getAllTitleIds().stream()
                        .filter(x -> x.startsWith(strings[1].toLowerCase())).collect(Collectors.toList());
            }
        }
        if(strings.length == 3) {
            if(strings[0].equalsIgnoreCase("give") || strings[0].equalsIgnoreCase("take")) {
                return null;
            }
        }
        return new ArrayList<>();
    }
}
