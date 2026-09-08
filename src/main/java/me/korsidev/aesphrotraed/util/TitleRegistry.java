package me.korsidev.aesphrotraed.util;

import me.korsidev.aesphrotraed.Aesphrotraed;

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

    // Get display
    public String getDisplay(String titleId) {
        return plugin.getConfig().getString("titles." + titleId.toLowerCase() + ".display", "");
    }

    // Register title
    public void registerTitle(String titleId, String display) {
        plugin.getConfig().set("titles." + titleId.toLowerCase() + ".display", display);
        plugin.saveConfig();
    }

    // Unregister title
    public void unregisterTitle(String titleId) {
        plugin.getConfig().set("titles." + titleId.toLowerCase(), null);
        plugin.saveConfig();
    }

    // Get a list of all existing Title IDs for tab completion
    public Set<String> getAllTitleIds() {
        if (!plugin.getConfig().contains("titles")) return Set.of();
        return plugin.getConfig().getConfigurationSection("titles").getKeys(false);
    }
}
