package me.korsidev.aesphrotraed.casino.blackjack;

import me.korsidev.aesphrotraed.casino.cards.Card;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class BlackjackListener implements Listener {

    private final BlackjackManager manager;

    public BlackjackListener(BlackjackManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        Action action = e.getAction();

        if(manager.hasGame(player)) {

            BlackjackGame game = manager.getGame(player);

            if (action.isLeftClick() && manager.getBlackjackInventory().isBlackjackCard(item)) {
                e.setCancelled(true);

                Card card = game.hit();
                if (card != null) {
                    manager.getBlackjackInventory().updatePlayerHand(player, game);
                }

            } else if (action.isRightClick() && manager.getBlackjackInventory().isBlackjackCard(item)) {
                e.setCancelled(true);

                game.stand();
            }
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        Player player = e.getPlayer();
        ItemStack item = e.getItemDrop().getItemStack();

        if(manager.hasGame(player)) {

            e.setCancelled(true);

        }

    }

}
