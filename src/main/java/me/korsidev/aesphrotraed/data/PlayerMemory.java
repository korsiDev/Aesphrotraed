package me.korsidev.aesphrotraed.data;

import java.util.ArrayList;
import java.util.List;

public class PlayerMemory {
    // General Stats
    private long balance;

    // Casino Stats
    private int currentStreak;
    private int longestStreak;

    private long totalWon;
    private long totalLoss;
    private long totalWagered;

    private int totalGamesPlayed;

    private List<String> friends = new ArrayList<>();

    private List<String> ownedTitles = new ArrayList<>();
    private String equippedTitle;


    // Getters and Setters

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public long getTotalWon() {
        return totalWon;
    }

    public void setTotalWon(long totalWon) {
        this.totalWon = totalWon;
    }

    public long getTotalLoss() {
        return totalLoss;
    }

    public void setTotalLoss(long totalLoss) {
        this.totalLoss = totalLoss;
    }

    public long getTotalWagered() {
        return totalWagered;
    }

    public void setTotalWagered(long totalWagered) {
        this.totalWagered = totalWagered;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getOwnedTitles() {
        return ownedTitles;
    }

    public void setOwnedTitles(List<String> ownedTitles) {
        this.ownedTitles = ownedTitles;
    }

    public String getEquippedTitle() {
        return equippedTitle;
    }

    public void setEquippedTitle(String equippedTitle) {
        this.equippedTitle = equippedTitle;
    }
}
