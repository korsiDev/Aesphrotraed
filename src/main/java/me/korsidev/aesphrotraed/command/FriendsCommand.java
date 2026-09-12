package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.util.FriendManager;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FriendsCommand implements TabExecutor {
    private final Aesphrotraed plugin;
    private final FriendManager friendManager;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy, HH:mm");

    public FriendsCommand(Aesphrotraed plugin, FriendManager friendManager) {
        this.plugin = plugin;
        this.friendManager = friendManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§c! §8› §cThis command can only be used by players.");
            return true;
        }

        var memory = PlayerUtility.getPlayerMemory(player);
        if (memory == null) {
            player.sendMessage("§c! §8› §cYour data profile has not loaded yet.");
            return true;
        }

        // List friends
        if (strings.length == 0) {
            List<String> friendUuids = memory.getFriends();

            if (friendUuids == null || friendUuids.isEmpty()) {
                player.sendMessage("§c! §8› §cYou have no friends yet. Use §e/friend add <player>§c to request someone.");
                return true;
            }

            player.sendMessage("§b§l--- Your friends ---");
            for (String uuidStr : friendUuids) {
                UUID fUuid = UUID.fromString(uuidStr);
                OfflinePlayer friendOffline = Bukkit.getOfflinePlayer(fUuid);

                Component friendLine;
                if (friendOffline.isOnline()) {
                    friendLine = Component.text("§8› ")
                            .append(Component.text("§a" + friendOffline.getName() + "\n §7- §aOnline"));
                } else {
                    long lastSeen = friendOffline.getLastSeen();
                    String dateStr = lastSeen > 0 ? dateFormat.format(new Date(lastSeen)) : "Unknown";

                    friendLine = Component.text("§8› ")
                            .append(Component.text("§c" + friendOffline.getName() + "\n §7- §cOffline since: " + dateStr));
                }

                Component removeButton = Component.text(" §c[§l✖§r§c]")
                        .hoverEvent(HoverEvent.showText(Component.text("§cClick to remove " + friendOffline.getName())))
                        .clickEvent(ClickEvent.runCommand("/friends remove " + friendOffline.getName()));

                player.sendMessage(friendLine.append(removeButton));
            }
            player.sendMessage("§b");
            return true;
        }

        String sub = strings[0].toLowerCase();

        // Add/Send request
        if (sub.equals("add")) {
            if (strings.length < 2) {
                player.sendMessage("§c! §8› §cUsage: §e/friend add <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(strings[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage("§c! §8› §cThat player is currently offline or does not exist.");
                return true;
            }
            if (target.equals(player)) {
                player.sendMessage("§c! §8› §cYou cannot friend yourself.");
                return true;
            }
            if (memory.getFriends().contains(target.getUniqueId().toString())) {
                player.sendMessage("§c! §8› §cYou are already friends with that player.");
                return true;
            }

            friendManager.sendRequest(player.getUniqueId(), target.getUniqueId());
            player.sendMessage("§a✓ §8› §aSent a friend request to §e" + target.getName() + "§a!");

            // Send clickable promt to target
            Component incoming = Component.text("§a! §8› §e" + player.getName() + "§a has sent you a friend request.")
                    .append(Component.newline()
                            .append(Component.text("§a§l[Accept]")
                                    .clickEvent(ClickEvent.runCommand("/friend accept " + player.getName())))
                            .append(Component.text("  "))
                            .append(Component.text("§c§l[Deny]")
                                    .clickEvent(ClickEvent.runCommand("/friend deny " + player.getName()))));
            target.sendMessage(incoming);
            return true;
        }

        // Accept
        if (sub.equals("accept")) {
            if (strings.length < 2) return true;
            Player target = Bukkit.getPlayer(strings[1]);
            if (target == null) {
                player.sendMessage("§c! §8› §cThat player is no longer online.");
                return true;
            }

            if (!friendManager.hasPendingRequestFrom(player.getUniqueId(), target.getUniqueId())) {
                player.sendMessage("§c! §8› §cYou have no pending requests from that player.");
                return true;
            }

            var targetMemory = PlayerUtility.getPlayerMemory(target);
            if (targetMemory != null) {
                // Add to both data arrays
                memory.getFriends().add(target.getUniqueId().toString());
                targetMemory.getFriends().add(player.getUniqueId().toString());

                player.sendMessage("§a✓ §8› §aYou are now friends with §e" + target.getName() + "§a!");
                target.sendMessage("§a✓ §8› §e" + player.getName() + "§a accepted your friend request!");
            }
            friendManager.removeRequest(player.getUniqueId());
            return true;
        }

        // Deny
        if (sub.equals("deny")) {
            if (strings.length < 2) return true;
            friendManager.removeRequest(player.getUniqueId());
            player.sendMessage("§c! §8› §cDenied friend request.");
            return true;
        }

        // Remove
        if (sub.equals("remove")) {
            if (strings.length < 2) {
                player.sendMessage("§c! §8› §cUsage: §e/friend remove <name>");
                return true;
            }

            // Look up UUID
            OfflinePlayer targetOffline = Bukkit.getOfflinePlayer(strings[1]);
            String targetUuidStr = targetOffline.getUniqueId().toString();

            if (!memory.getFriends().contains(targetUuidStr)) {
                player.sendMessage("§c! §8› §cThat player is not on your friend list.");
                return true;
            }

            if (targetOffline.isOnline() && targetOffline.getPlayer() != null) {
                var targetMemory = PlayerUtility.getPlayerMemory(targetOffline.getPlayer());
                if (targetMemory != null) {
                    targetMemory.getFriends().remove(player.getUniqueId().toString());
                    memory.getFriends().remove(targetOffline.getUniqueId().toString());
                    player.sendMessage("§a✓ §8› §cYou have removed " + targetOffline.getName() + " from your friends list.");
                }
            } else {
                java.io.File file = new java.io.File(plugin.getDataFolder() + "/players/" + targetUuidStr + "/general.yml");

                if (file.exists()) {
                    YamlConfiguration offlineConfig = YamlConfiguration.loadConfiguration(file);

                    List<String> offlineFriends = offlineConfig.getStringList("other.friends");

                    if (offlineFriends.contains(player.getUniqueId().toString())) {
                        offlineFriends.remove(player.getUniqueId().toString());
                        memory.getFriends().remove(targetOffline.getUniqueId().toString());
                        player.sendMessage("§a✓ §8› §cYou have removed " + targetOffline.getName() + " from your friends list.");

                        offlineConfig.set("other.friends", offlineFriends);
                        try {
                            offlineConfig.save(file);
                        } catch (java.io.IOException e) {
                            plugin.getLogger().severe("Failed to save offline friend modifications for UUID: " + targetUuidStr);
                            e.printStackTrace();
                        }
                    }
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            return Arrays.asList("add", "remove", "accept", "deny").stream()
                    .filter(x -> x.startsWith(strings[0].toLowerCase())).collect(Collectors.toList());
        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("add")) {
            return null; // Completes online players
        }
        return new ArrayList<>();
    }
}
