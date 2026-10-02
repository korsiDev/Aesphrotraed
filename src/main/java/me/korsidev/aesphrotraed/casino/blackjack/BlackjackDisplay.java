package me.korsidev.aesphrotraed.casino.blackjack;

import me.korsidev.aesphrotraed.casino.cards.Card;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BlackjackDisplay {

    private final BlackjackManager manager;

    private final Map<UUID, List<ItemDisplay>> playerDisplays = new HashMap<>();

    public BlackjackDisplay(BlackjackManager manager) {
        this.manager = manager;
    }

    public void createPlayerDisplay(Player player, BlackjackGame game) {

        List<Card> playerHand = game.getPlayerHand();

        Location loc = player.getLocation();

        List<ItemDisplay> displays = playerDisplays.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new ArrayList<>()
        );

        for (int i = 0; i < playerHand.size(); i++) {
            Card card = playerHand.get(i);
            double offset = (i - (playerHand.size() - 1) / 2.0) * 0.8;

            Location cardLocation = loc.clone().add(
                    offset,
                    2,
                    0
            );

            ItemDisplay display = player.getWorld().spawn(
                    cardLocation,
                    ItemDisplay.class
            );

            ItemStack cardItem = manager
                    .getBlackjackInventory()
                    .createCardItem(card);

            display.setItemStack(cardItem);

            display.setBillboard(Display.Billboard.CENTER);

            displays.add(display);
        }

    }

    public void clearPlayerDisplay(Player player) {

        List<ItemDisplay> displays = playerDisplays.remove(player.getUniqueId());

        if (displays == null) {
            return;
        }

        for (ItemDisplay display : displays) {

            if (!display.isDead()) {
                display.remove();
            }
        }

        playerDisplays.remove(player.getUniqueId());
    }

    public void updatePlayerDisplay(Player player, BlackjackGame game) {

        clearPlayerDisplay(player);

        createPlayerDisplay(player, game);
    }

}
