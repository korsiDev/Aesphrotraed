package me.korsidev.aesphrotraed.casino.blackjack;

import me.korsidev.aesphrotraed.Aesphrotraed;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlackjackManager {

    private final Aesphrotraed plugin;

    private final Map<UUID, BlackjackGame> activeGames = new HashMap<>();

    private final BlackjackInventory blackjackInventory;

    private final BlackjackDisplay blackjackDisplay;

    public BlackjackManager(Aesphrotraed plugin) {
        this.plugin = plugin;
        this.blackjackInventory = new BlackjackInventory(plugin);
        this.blackjackDisplay = new BlackjackDisplay(this);
    }

    public boolean hasGame(Player player) {
        return activeGames.containsKey(player.getUniqueId());
    }

    public BlackjackGame getGame(Player player) {
        return activeGames.get(player.getUniqueId());
    }

    public boolean startGame(Player player, long bet) {

        if (hasGame(player)) {
            return false;
        }

        blackjackInventory.saveAndClear(player);

        BlackjackGame game = new BlackjackGame(player, bet);
        game.start();

        activeGames.put(player.getUniqueId(), game);

        blackjackInventory.updatePlayerHand(player, game);

        blackjackDisplay.createPlayerDisplay(player, game);

        return true;
    }

    public void endGame(Player player) {
        removeGame(player);

        blackjackDisplay.clearPlayerDisplay(player);

        blackjackInventory.restore(player);
    }

    public void removeGame(Player player) {

        activeGames.remove(player.getUniqueId());
    }

    public BlackjackInventory getBlackjackInventory() {
        return blackjackInventory;
    }

    public BlackjackDisplay getBlackjackDisplay() {
        return blackjackDisplay;
    }

    public Map<UUID, BlackjackGame> getActiveGames() {
        return Map.copyOf(activeGames);
    }
}