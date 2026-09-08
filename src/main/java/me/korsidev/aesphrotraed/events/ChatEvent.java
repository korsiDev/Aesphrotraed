package me.korsidev.aesphrotraed.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.korsidev.aesphrotraed.Aesphrotraed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class ChatEvent implements Listener {
    private final Aesphrotraed plugin;

    public ChatEvent(Aesphrotraed plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // LuckPerms
        User user = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        String lpPrefix = "";
        if (user != null && user.getCachedData().getMetaData().getPrefix() != null) {
            lpPrefix = user.getCachedData().getMetaData().getPrefix();
        }

        // Convert Strings into MiniMessages
        String styledNameString = lpPrefix + player.getName();

    // Deserialize the combined string into a unified component
        Component line2 = LegacyComponentSerializer.legacyAmpersand().deserialize(styledNameString);


        // Construct
        event.renderer((source, sourceDisplayName, message, viewer) ->
                line2
                        .append(Component.text(": "))
                        .append(message)
        );
    }

}
