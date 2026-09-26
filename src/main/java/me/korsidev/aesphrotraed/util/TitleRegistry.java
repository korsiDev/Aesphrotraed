package me.korsidev.aesphrotraed.util;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.data.TitleData;

import java.util.HashSet;
import java.util.Set;

public class TitleRegistry {
    private final Aesphrotraed plugin;

    public TitleRegistry(Aesphrotraed plugin) {
        this.plugin = plugin;
    }

    // Check if an ID exists
    public boolean titleExists(String titleId) {
        return plugin.getConfig().contains("titles." + titleId.toLowerCase());
    }

    public TitleData getTitle(String titleId) {
        String path = "titles." + titleId.toLowerCase();

        if (!plugin.getConfig().contains(path)) { return null; }

        String display = plugin.getConfig().getString(path + ".display", "");
        String format = plugin.getConfig().getString(path + ".format", "white");
        boolean bold = plugin.getConfig().getBoolean(path + ".bold", false);
        boolean italic = plugin.getConfig().getBoolean(path + ".italic", false);

        return new TitleData(titleId.toLowerCase(),
                display,
                format,
                bold,
                italic
        );

    }

    // Register title
    public void registerTitle(TitleData title) {

        String path = "titles." + title.getId().toLowerCase();

        plugin.getConfig().set(path + ".display", title.getDisplay());
        plugin.getConfig().set(path + ".format", title.getFormat());
        plugin.getConfig().set(path + ".bold", title.isBold());
        plugin.getConfig().set(path + ".italic", title.isItalic());

        plugin.saveConfig();

    }

    // Unregister title
    public void unregisterTitle(String titleId) {

        plugin.getConfig().set(
                "titles." + titleId.toLowerCase(),
                null
        );

        plugin.saveConfig();

    }

    // Get a list of all existing Title IDs for tab completion
    public Set<String> getAllTitleIds() {

        if (!plugin.getConfig().contains("titles")) {
            return Set.of();
        }

        if (plugin.getConfig().getConfigurationSection("titles") == null) {
            return Set.of();
        }

        return new HashSet<>(
                plugin.getConfig()
                        .getConfigurationSection("titles")
                        .getKeys(false)
        );
    }
}
