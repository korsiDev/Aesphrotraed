package me.korsidev.aesphrotraed.events;

import me.korsidev.aesphrotraed.Aesphrotraed;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LuckPermsListener {

    private final Aesphrotraed plugin;
    private final LuckPerms luckPerms;

    public LuckPermsListener(Aesphrotraed plugin) {
        this.plugin = plugin;
        this.luckPerms = plugin.getLuckPerms();

        register();
    }

    private void register() {

        luckPerms.getEventBus().subscribe(
                plugin,
                UserDataRecalculateEvent.class,
                this::onUserDataRecalculate
        );
    }

    private void onUserDataRecalculate(UserDataRecalculateEvent event) {

        Player player = Bukkit.getPlayer(event.getUser().getUniqueId());

        if (player == null || !player.isOnline()) {
            return;
        }

        Bukkit.getScheduler().runTask(
                plugin,
                () -> plugin.getNametagUtility().updateNametag(player)
        );
    }
}