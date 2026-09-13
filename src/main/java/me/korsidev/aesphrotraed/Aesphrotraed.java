package me.korsidev.aesphrotraed;

import me.korsidev.aesphrotraed.command.*;
import me.korsidev.aesphrotraed.data.EconomyManager;
import me.korsidev.aesphrotraed.data.PlayerDataManager;
import me.korsidev.aesphrotraed.progression.ProgressionManager;
import me.korsidev.aesphrotraed.events.ChatEvent;
import me.korsidev.aesphrotraed.events.ChunkCleanupEvent;
import me.korsidev.aesphrotraed.events.GeneralEvents;
import me.korsidev.aesphrotraed.scoreboard.ScoreboardManager;
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
    private EconomyManager economyManager;
    private ScoreboardManager scoreboardManager;
    private ProgressionManager progressionManager;
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
        this.economyManager = new EconomyManager();

        this.scoreboardManager = new ScoreboardManager(this);
        this.scoreboardManager.startUpdateTask();

        this.progressionManager = new ProgressionManager(this);

        this.friendManager = new FriendManager();
        this.luckPerms = LuckPermsProvider.get();

        this.generalEvents = new GeneralEvents(
                this,
                playerDataManager
        );

        Bukkit.getPluginManager().registerEvents(
                generalEvents,
                this
        );

        Bukkit.getPluginManager().registerEvents(new ChatEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new ChunkCleanupEvent(), this);

        getCommand("serverTitles").setExecutor(new ServerTitlesCommand(this, titleRegistry, playerDataManager));
        getCommand("setTitle").setExecutor(new SetTitleCommand(this, titleRegistry, playerDataManager));
        getCommand("friends").setExecutor(new FriendsCommand(this, friendManager));
        getCommand("economy").setExecutor(new EconomyCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("admin").setExecutor(new AdminCommand(this));

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

    public EconomyManager getEconomyManager() {
        return this.economyManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    public ProgressionManager getProgressionManager() {
        return progressionManager;
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
