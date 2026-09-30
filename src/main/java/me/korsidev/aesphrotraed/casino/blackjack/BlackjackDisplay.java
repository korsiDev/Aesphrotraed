package me.korsidev.aesphrotraed.casino.blackjack;

import me.korsidev.aesphrotraed.casino.cards.Card;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlackjackDisplay {

    private final BlackjackManager manager;

    private final List<ItemDisplay> playerDisplays = new ArrayList<>();

    public BlackjackDisplay(BlackjackManager manager) {
        this.manager = manager;
    }

    public void createPlayerDisplay(Player player, BlackjackGame game) {

        List<Card> playerHand = game.getPlayerHand();

        Location loc = player.getLocation();

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

            playerDisplays.add(display);
        }

    }

    public void clearPlayerDisplay() {

        for (ItemDisplay display : playerDisplays) {

            if (!display.isDead()) {
                display.remove();
            }
        }

        playerDisplays.clear();
    }

    public void updatePlayerDisplay(Player player, BlackjackGame game) {

        clearPlayerDisplay();

        createPlayerDisplay(player, game);
    }

}
