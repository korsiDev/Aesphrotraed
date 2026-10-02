package me.korsidev.aesphrotraed.casino.blackjack;

import me.korsidev.aesphrotraed.Aesphrotraed;
import me.korsidev.aesphrotraed.casino.cards.Card;
import me.korsidev.aesphrotraed.casino.cards.CardRank;
import me.korsidev.aesphrotraed.casino.cards.Deck;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlackjackGame {

    public enum State {
        WAITING,
        PLAYER_TURN,
        DEALER_TURN,
        WON,
        LOST,
        PUSH,
        BLACKJACK,
        BUST
    }

    private final Player player;
    private final long bet;

    private final Deck deck;

    private final List<Card> playerHand = new ArrayList<>();
    private final List<Card> dealerHand = new ArrayList<>();

    private State state;

    private String loggerPrefix;

    public BlackjackGame(Player player, long bet) {
        this.player = player;
        this.bet = bet;

        this.deck = new Deck();
        this.deck.shuffle();

        this.state = State.WAITING;

        loggerPrefix = "[BJ " + player.getName() + "] ";
    }

    public void start() {
        if (state != State.WAITING) {
            return;
        }

        playerHand.clear();
        dealerHand.clear();

        Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + player.getName() + " started a Blackjack game.");

        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());

        playerHand.add(deck.draw());
        dealerHand.add(deck.draw());

        Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Player: " + getPlayerHand().toString());
        Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Dealer: " + getDealerHand().toString());

        if (isBlackjack(playerHand)) {
            state = State.BLACKJACK;
            return;
        }

        state = State.PLAYER_TURN;
    }

    public Card hit() {
        if (state != State.PLAYER_TURN) {
            return null;
        }

        Card card = deck.draw();
        playerHand.add(card);

        Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Player hit");

        Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Player: " + getPlayerHand().toString());
        Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Dealer: " + getDealerHand().toString());

        if (isBust(playerHand)) {
            state = State.BUST;

            Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Player bust");

        }

        return card;
    }

    public void stand() {
        if (state != State.PLAYER_TURN) {
            return;
        }

        Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Player stand");

        state = State.DEALER_TURN;

        playDealer();
        determineWinner();
    }

    private void playDealer() {
        while (calculateHandValue(dealerHand) < 17) {
            dealerHand.add(deck.draw());
            Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Player: " + getPlayerHand().toString());
            Aesphrotraed.getPlugin(Aesphrotraed.class).getLogger().info(loggerPrefix + "Dealer: " + getDealerHand().toString());
        }
    }

    private void determineWinner() {

        int playerValue = calculateHandValue(playerHand);
        int dealerValue = calculateHandValue(dealerHand);

        if (playerValue > 21) {
            state = State.BUST;
            return;
        }

        if (dealerValue > 21) {
            state = State.WON;
            return;
        }

        if (playerValue > dealerValue) {
            state = State.WON;
        } else if (playerValue < dealerValue) {
            state = State.LOST;
        } else {
            state = State.PUSH;
        }
    }

    private boolean isBust(List<Card> hand) {
        return calculateHandValue(hand) > 21;
    }

    private boolean isBlackjack(List<Card> hand) {
        return hand.size() == 2
                && calculateHandValue(hand) == 21;
    }

    public int calculateHandValue(List<Card> hand) {

        int value = 0;
        int aces = 0;

        for (Card card : hand) {

            CardRank rank = card.getRank();

            switch (rank) {
                case TWO -> value += 2;
                case THREE -> value += 3;
                case FOUR -> value += 4;
                case FIVE -> value += 5;
                case SIX -> value += 6;
                case SEVEN -> value += 7;
                case EIGHT -> value += 8;
                case NINE -> value += 9;

                case TEN, JACK, QUEEN, KING -> value += 10;

                case ACE -> {
                    value += 11;
                    aces++;
                }
            }
        }

        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }

        return value;

    }

    public Player getPlayer() {
        return player;
    }

    public long getBet() {
        return bet;
    }

    public List<Card> getPlayerHand() {
        return List.copyOf(playerHand);
    }

    public List<Card> getDealerHand() {
        return List.copyOf(dealerHand);
    }

    public State getState() {
        return state;
    }

}
