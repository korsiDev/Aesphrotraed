package me.korsidev.aesphrotraed.profile;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class ProfileCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }

        Component guiTitle = Component.text("\uE001\uE000")
                .font(Key.key("aes", "gui"));

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                guiTitle
        );

        player.openInventory(inventory);

        return true;
    }
}
