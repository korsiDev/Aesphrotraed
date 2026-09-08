package me.korsidev.aesphrotraed;

import me.korsidev.aesphrotraed.command.FriendsCommand;
import me.korsidev.aesphrotraed.command.ServerTitlesCommand;
import me.korsidev.aesphrotraed.command.SetTitleCommand;
import me.korsidev.aesphrotraed.data.PlayerDataManager;
import me.korsidev.aesphrotraed.events.ChatEvent;
import me.korsidev.aesphrotraed.events.ChunkCleanupEvent;
import me.korsidev.aesphrotraed.events.GeneralEvents;
import me.korsidev.aesphrotraed.util.FriendManager;
import me.korsidev.aesphrotraed.util.NametagUtility;
import me.korsidev.aesphrotraed.util.TitleRegistry;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Aesphrotraed extends JavaPlugin {
    private NametagUtility nametagUtility;
    private TitleRegistry titleRegistry;
    private GeneralEvents generalEvents;
    private PlayerDataManager playerDataManager;
    private FriendManager friendManager;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.titleRegistry = new TitleRegistry(this);
        this.nametagUtility = new NametagUtility(this, titleRegistry);
        this.playerDataManager = new PlayerDataManager(this);
        this.friendManager = new FriendManager();
        this.luckPerms = LuckPermsProvider.get();

        this.generalEvents = new GeneralEvents(
                this,
                nametagUtility,
                playerDataManager
        );

        Bukkit.getPluginManager().registerEvents(
                generalEvents,
                this
        );

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

    public LuckPerms getLuckPerms() {
        return this.luckPerms;
    }

    public NametagUtility getNametagUtility() {
        return this.nametagUtility;
    }

    @Override
    public void onDisable() {

        if (this.playerDataManager != null) {
            this.playerDataManager.saveAll();
        }

        if (this.nametagUtility != null) {
            this.nametagUtility.removeAllNametags();
        }

        getLogger().info("Plugin has been disabled.");
    }
}
