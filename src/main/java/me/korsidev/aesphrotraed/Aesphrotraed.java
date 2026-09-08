package me.korsidev.aesphrotraed;

import me.korsidev.aesphrotraed.command.FriendsCommand;
import me.korsidev.aesphrotraed.command.ServerTitlesCommand;
import me.korsidev.aesphrotraed.command.SetTitleCommand;
import me.korsidev.aesphrotraed.data.PlayerDataManager;
import me.korsidev.aesphrotraed.data.PlayerMemory;
import me.korsidev.aesphrotraed.events.ChatEvent;
import me.korsidev.aesphrotraed.events.ChunkCleanupEvent;
import me.korsidev.aesphrotraed.events.GeneralEvents;
import me.korsidev.aesphrotraed.util.FriendManager;
import me.korsidev.aesphrotraed.util.NametagUtility;
import me.korsidev.aesphrotraed.util.PlayerUtility;
import me.korsidev.aesphrotraed.util.TitleRegistry;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Aesphrotraed extends JavaPlugin {
    private NametagUtility nametagUtility;
    private TitleRegistry titleRegistry;
    private GeneralEvents generalEvents;
    private PlayerDataManager pdManager;
    private FriendManager friendManager;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.titleRegistry = new TitleRegistry(this);
        this.nametagUtility = new NametagUtility(this, titleRegistry);
        this.pdManager = new PlayerDataManager(this);
        this.friendManager = new FriendManager();
        this.luckPerms = LuckPermsProvider.get();

        this.generalEvents = new GeneralEvents(
                this,
                nametagUtility,
                pdManager
        );

        Bukkit.getPluginManager().registerEvents(
                generalEvents,
                this
        );

        Bukkit.getPluginManager().registerEvents(new PlayerDataManager(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new ChunkCleanupEvent(), this);

        getCommand("serverTitles").setExecutor(new ServerTitlesCommand(this, titleRegistry));
        getCommand("setTitle").setExecutor(new SetTitleCommand(this, titleRegistry));
        getCommand("friends").setExecutor(new FriendsCommand(this, friendManager));

        getLogger().info("Plugin has been enabled.");

        org.bukkit.Bukkit.getScheduler().runTaskLater(this, () -> {
            this.getLogger().info("Generating text-display nametags for online players...");
            for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                this.nametagUtility.createNametag(player);
            }
        }, 2L);

        // Plugin startup logic
    }

    public void reloadPluginConfig() {
        // 1. Lädt die config.yml frisch von der Festplatte in den RAM
        this.reloadConfig();

        this.nametagUtility.removeAllNametags();
        org.bukkit.Bukkit.getScheduler().runTaskLater(this, () -> {
            this.getLogger().info("Generating text-display nametags for online players...");
            for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                this.nametagUtility.createNametag(player);
            }
        }, 2L);

        this.getLogger().info("Configuration successfully reloaded!");
    }

    public LuckPerms getLuckPerms() {
        return this.luckPerms;
    }

    public NametagUtility getNametagUtility() {
        return this.nametagUtility;
    }

    @Override
    public void onDisable() {
        if (this.nametagUtility != null) {
            this.getLogger().info("Removing all active TextDisplay nametags before shutdown...");
            this.nametagUtility.removeAllNametags();
        }


        getLogger().info("Plugin has been disabled.");

        // Plugin shutdown logic
    }
}
