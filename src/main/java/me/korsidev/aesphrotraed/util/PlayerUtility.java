package me.korsidev.aesphrotraed.util;

import me.korsidev.aesphrotraed.data.PlayerMemory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerUtility {
    private static final Map<UUID, PlayerMemory> playerMemory = new HashMap<>();

    public static PlayerMemory getPlayerMemory(Player player) {
        return playerMemory.get(player.getUniqueId());
    }

    public static void setPlayerMemory(
            Player player,
            PlayerMemory memory
    ) {
        UUID uuid = player.getUniqueId();

        if (memory == null) {
            playerMemory.remove(uuid);
            return;
        }

        playerMemory.put(uuid, memory);
    }

    public static String getFolderPath(Player p) {
        return Bukkit.getPluginsFolder().getAbsolutePath() + "/Aesphrotraed/players/" + p.getUniqueId();
    }
}
