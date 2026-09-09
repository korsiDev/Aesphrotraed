package me.korsidev.aesphrotraed.util;

import me.korsidev.aesphrotraed.Aesphrotraed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.eclipse.aether.metadata.Metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NametagUtility {
    private final Aesphrotraed plugin;
    private final TitleRegistry registry;
    private final Map<UUID, TextDisplay> activeTags = new HashMap<>();
    private final NamespacedKey nametagKey;

    public NametagUtility(Aesphrotraed plugin, TitleRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
        this.nametagKey = new NamespacedKey(plugin, "nametag");
    }


    private void hideDefaultNametag(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("hide_tags");
        if (team == null) {
            team = scoreboard.registerNewTeam("hide_tags");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
        team.addEntry(player.getName());
    }

    private Component buildNametagText(Player player) {
        var memory = PlayerUtility.getPlayerMemory(player);

        String titleId = memory.getEquippedTitle();

        String title = titleId == null
                ? ""
                : registry.getDisplay(titleId);

        long balance = memory.getBalance();

        User user = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        String lpPrefix = (user != null && user.getCachedData().getMetaData().getPrefix() != null) ? user.getCachedData().getMetaData().getPrefix() : "";

        Component line1 = LegacyComponentSerializer.legacyAmpersand().deserialize(title);
        Component line2 = LegacyComponentSerializer.legacyAmpersand().deserialize(lpPrefix + player.getName());
        Component line3 = LegacyComponentSerializer.legacyAmpersand().deserialize("&r&aA$" + balance);

        return line1.append(Component.newline())
                .append(line2).append(Component.newline())
                .append(line3);
    }

    // Call this when a player joins, teleports, or respawns
    public void createNametag(Player player) {
        removeNametag(player);
        hideDefaultNametag(player);

        Component fullText = buildNametagText(player);

        TextDisplay textDisplay = player.getWorld().spawn(player.getLocation(), TextDisplay.class, display -> {
            display.text(fullText);
            display.setBillboard(Display.Billboard.CENTER);
            display.setShadowed(true);
            display.setSeeThrough(false);
            display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));

            Transformation transformation = display.getTransformation();
            transformation.getTranslation().set(0.0f, 0.2f, 0.0f);

            display.setTransformation(transformation);

            display.getPersistentDataContainer().set(
                    nametagKey,
                    PersistentDataType.BYTE,
                    (byte) 1
            );

            display.setPersistent(false);

        });

        player.addPassenger(textDisplay);
        activeTags.put(player.getUniqueId(), textDisplay);
    }

    // NEW FUNCTION: Call this whenever their money or title changes!
    public void updateNametag(Player player) {
        TextDisplay display = activeTags.get(player.getUniqueId());

        // If the display exists and is still valid, just update the text instantly
        if (display != null && display.isValid()) {
            display.text(buildNametagText(player));
        } else {
            // Fallback: If it somehow went missing, rebuild it
            createNametag(player);
        }
    }

    public void removeNametag(Player player) {
        TextDisplay display = activeTags.remove(player.getUniqueId());
        if (display != null && display.isValid()) {
            display.remove();
        }
    }

    public void removeAllNametags() {
        // Create a copy of the values to avoid ConcurrentModificationException while removing
        new java.util.ArrayList<>(activeTags.values()).forEach(display -> {
            if (display != null && display.getPersistentDataContainer().has(nametagKey, PersistentDataType.BYTE)) {
                display.remove();
            }
        });
        activeTags.clear();
    }

}
