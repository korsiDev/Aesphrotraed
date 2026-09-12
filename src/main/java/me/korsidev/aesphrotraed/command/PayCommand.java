package me.korsidev.aesphrotraed.command;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.EconomyManager;
import me.korsidev.aesphrotraed.util.NametagUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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
            commandSender.sendMessage("§c! §8› §cThis command can only be used by players.");
            return true;
        }

        if (strings.length != 2) {
            player.sendMessage("§c! §8› §cUsage: §e/pay <player> <amount>.");
            return true;
        }

        Player target = Bukkit.getPlayerExact(strings[0]);

        if (target == null) {
            player.sendMessage("§c! §8› §e" + strings[0] + "§c is not online.");
            player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 0.5f, 0.8f));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§c! §8› §cYou cannot pay yourself.");
            player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 0.5f, 0.8f));
            return true;
        }

        Long amount = economyManager.parseAmount(strings[1]);

        if (amount == null || amount <= 0) {
            player.sendMessage("§c! §8› §cPlease enter a valid amount.");
            player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 0.5f, 0.8f));
            return true;
        }

        if (!economyManager.hasBalance(player, amount)) {
            player.sendMessage("§c! §8› §cYou do not have enough money.");
            player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 0.5f, 0.8f));
            return true;
        }

        if(!economyManager.transfer(player, target, amount)) {
            player.sendMessage("§c! §8› §cThe transaction could not be completed!");
            player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 0.5f, 0.8f));
            return true;
        }

        String formattedAmount = economyManager.formatBalanceFull(amount);

        player.sendMessage("&a✓ §8› §7You paid §e◆ " + amount + " §7to §e" + target.getName());
        player.playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 1f, 1f));

        target.sendMessage("&a! §8› §e" + player.getName() + " §7sent you §e◆ " + amount);
        target.playSound(Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MASTER, 0.5f, 1f));


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
