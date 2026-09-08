package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import me.korsidev.aesphrotraed.util.TitleRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetTitleCommand implements CommandExecutor {
    private final Aesphrotraed plugin;
    private final TitleRegistry registry;

    public SetTitleCommand(Aesphrotraed plugin, TitleRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        var memory = PlayerUtility.getPlayerMemory(player);
        if (memory == null) {
            player.sendMessage("§cYour profile data hasn't fully loaded yet.");
            return true;
        }

        // List titles
        if (strings.length == 0) {
            List<String> ownedIds = memory.getOwnedTitles();

            if(ownedIds == null || ownedIds.isEmpty()) {
                player.sendMessage("§cYou don't own any titles.");
                return true;
            }

            player.sendMessage("§b§l--- Your unlocked titles ---");
            player.sendMessage("§7(Click on a title to equip it.)\n");

            for (String titleId : ownedIds) {
                // Get global display color
                String rawDisplay = registry.getDisplay(titleId);
                if (rawDisplay == null || rawDisplay.isEmpty()) {
                    rawDisplay = "§7" + titleId;
                }

                // Parse into Component
                Component titleComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(rawDisplay);

                // Attach text metadata
                Component interactiveLine = titleComponent
                        .hoverEvent(HoverEvent.showText(Component.text("§a§oClick to equip §r" + rawDisplay)))
                        .clickEvent(ClickEvent.runCommand("/settitle " + titleId));

                player.sendMessage(interactiveLine);
            }
            player.sendMessage("§b");
            return true;
        }

        // Equip Title
        String selectedId = strings[0].toLowerCase();

        // Security check
        if(!memory.getOwnedTitles().contains(selectedId)) {
            player.sendMessage("§cYou do not own that title.");
            return true;
        }

        // Ensure ID exists
        if (!registry.titleExists(selectedId)) {
            player.sendMessage("§cThat title no longer exists.");
            memory.getOwnedTitles().remove(selectedId);
            return true;
        }

        String newDisplay = registry.getDisplay(selectedId);
        memory.setEquippedTitle(selectedId);

        // Force update their nametag
        plugin.getNametagUtility().updateNametag(player);

        player.sendMessage("§aSuccessfully equipped '" + newDisplay + "' as your title!");
        return true;
    }
}
