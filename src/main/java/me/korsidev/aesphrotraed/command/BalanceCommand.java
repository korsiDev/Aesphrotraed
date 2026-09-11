package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.EconomyManager;
import me.korsidev.aesphrotraed.util.NametagUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public BalanceCommand(Aesphrotraed plugin) {
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        long balance = economyManager.getBalance(player);

        player.sendMessage("§6Your balance: §aA$" + economyManager.formatBalanceFull(balance));


        return true;
    }
}
