package me.korsidev.aesphrotraed.casino.blackjack;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.casino.cards.Card;
import me.korsidev.aesphrotraed.casino.cards.CardRank;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlackjackInventory {

    private final Aesphrotraed plugin;

    private final NamespacedKey blackjackCardKey;

    private final Map<UUID, InventorySnapshot> savedInventories = new HashMap<>();

    public BlackjackInventory(Aesphrotraed plugin) {
        this.plugin = plugin;

        this.blackjackCardKey = new NamespacedKey(
                plugin,
                "blackjack_card"
        );
    }

    public void saveAndClear(Player player) {

        UUID uuid = player.getUniqueId();

        if (savedInventories.containsKey(uuid)) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        InventorySnapshot snapshot = new InventorySnapshot(
                cloneItems(inventory.getStorageContents()),
                cloneItems(inventory.getArmorContents()),
                cloneItems(inventory.getExtraContents())
        );

        savedInventories.put(uuid, snapshot);

        inventory.clear();
        inventory.setArmorContents(new ItemStack[4]);
        inventory.setExtraContents(new ItemStack[1]);

        player.setItemOnCursor(null);
    }

    public void restore(Player player) {

        UUID uuid = player.getUniqueId();

        InventorySnapshot snapshot = savedInventories.remove(uuid);

        if (snapshot == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.clear();

        inventory.setStorageContents(
                cloneItems(snapshot.storageContents())
        );

        inventory.setArmorContents(
                cloneItems(snapshot.armorContents())
        );

        inventory.setExtraContents(
                cloneItems(snapshot.extraContents())
        );

        player.setItemOnCursor(null);
    }

    public void clearSavedInventory(Player player) {
        savedInventories.remove(player.getUniqueId());
    }

    public boolean hasSavedInventory(Player player) {
        return savedInventories.containsKey(player.getUniqueId());
    }

    public ItemStack createCardItem(Card card) {

        ItemStack item = new ItemStack(Material.PAPER);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§6" + getCardName(card));
        /*
         * 1 - 52
         *
         * Suit:
         * HEARTS   = 0
         * DIAMONDS = 1
         * CLUBS    = 2
         * SPADES   = 3
         *
         * Rank:
         * TWO      = 0
         * ...
         * ACE      = 12
         */
        int modelData =
                (card.getSuit().ordinal() * CardRank.values().length)
                + card.getRank().ordinal()
                + 1;

        meta.setCustomModelData(modelData);

        meta.getPersistentDataContainer().set(
                blackjackCardKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createCardBackItem() {
        ItemStack item = new ItemStack(Material.PAPER);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§8Hidden Card");

        meta.setCustomModelData(53);

        meta.getPersistentDataContainer().set(
                blackjackCardKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        item.setItemMeta(meta);

        return item;
    }

    public boolean isBlackjackCard(ItemStack item) {

        if (item == null || item.getType() != Material.PAPER) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer().has(
                blackjackCardKey,
                PersistentDataType.BYTE
        );
    }

    private String getCardName(Card card) {

        String rank = switch (card.getRank()) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
        };

        String suit = switch (card.getSuit()) {
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
            case SPADES -> "♠";
        };

        return rank + " " + suit;

    }

    private ItemStack[] cloneItems(ItemStack[] items) {

        ItemStack[] cloned = new ItemStack[items.length];

        for (int i = 0; i < items.length; i++) {

            if (items[i] != null) {
                cloned[i] = items[i].clone();
            }
        }
        return cloned;
    }

    public void updatePlayerHand(Player player, BlackjackGame game) {

        PlayerInventory inventory = player.getInventory();

        // Hotbar leeren
        for (int slot = 0; slot < 9; slot++) {
            inventory.setItem(slot, null);
        }

        // Karten der Hand in die Hotbar legen
        for (int i = 0; i < game.getPlayerHand().size(); i++) {

            Card card = game.getPlayerHand().get(i);

            inventory.setItem(
                    i,
                    createCardItem(card)
            );
        }
    }

    private record InventorySnapshot(
            ItemStack[] storageContents,
            ItemStack[] armorContents,
            ItemStack[] extraContents
    ) {
    }
}
