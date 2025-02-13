package com.github.norbo11.game.cards;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.norbo11.util.Messages;
import com.github.norbo11.util.PlayerControlled;
import com.github.norbo11.util.SoundEffect;

public abstract class CardsPlayer extends PlayerControlled {
    protected CardsPlayer(Player player) {
        super(player);
    }

    private Location startLocation; // Used to teleport the player back to the location where he/she joined
    private CardsTable table;       // This holds the table that the player is sitting at
    protected double money;         // This is the player's stack
    private int ID;                 // ID of the player every single statistic associated with this player
    private BukkitTask turnTimer;

    public static CardsPlayer getCardsPlayer(int id, CardsTable table) {
        if (table != null) {
            for (CardsPlayer cardsPlayer : table.getPlayers())
                if (cardsPlayer.getID() == id) return cardsPlayer;
        }
        return null;
    }

    public static CardsPlayer getCardsPlayer(String toCheck) {
        for (CardsTable cardsTable : CardsTable.getTables()) {
            for (CardsPlayer cardsPlayer : cardsTable.getPlayers())
                if (cardsPlayer.getPlayerName().equalsIgnoreCase(toCheck)) return cardsPlayer;
        }
        return null;
    }

    public static CardsPlayer getCardsPlayer(String toCheck, CardsTable table) {
        if (table != null) {
            for (CardsPlayer cardsPlayer : table.getPlayers())
                if (cardsPlayer.getPlayerName().equalsIgnoreCase(toCheck)) return cardsPlayer;
        }
        return null;
    }

    protected void cancelTurnTimer() {
        if (turnTimer != null) {
            turnTimer.cancel();
            turnTimer = null;
        }
    }

    public abstract boolean canPlay();

    public int getID() {
        return ID;
    }

    public double getMoney() {
        return money;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public CardsTable getTable() {
        return table;
    }

    public BukkitTask getTurnTimer() {
        return turnTimer;
    }

    protected void giveMoney(double amount) {
        money += amount;
    }

    public boolean hasMoney(double amount) {
        return getMoney() >= amount;
    }

    public boolean isAction() {
        return getTable().getActionPlayer() == this;
    }

    public boolean isButton() {
        return getTable().getButtonPlayer() == this;
    }

    public boolean isEliminated() {
        return !getTable().getPlayersThisHand().contains(this);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    protected void removeMoney(double amount) {
        money -= amount;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    protected void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    protected void setTable(CardsTable table) {
        this.table = table;
    }

    protected void setTurnTimer(BukkitTask turnTimer) {
        this.turnTimer = turnTimer;
    }

    protected abstract void startTurnTimer();

    // Sets the player's action flag to true and gives them an eye-catching message
    public void takeAction() {
        CardsTableSettings settings = getTable().getCardsTableSettings();
        int turnSeconds = table.getSettings().turnSeconds.getValue();
        String message;

        if (turnSeconds > 0) {
            message = "&6" + getPlayerName() + " " + ChatColor.DARK_PURPLE + "has &6" + turnSeconds + " seconds" + ChatColor.DARK_PURPLE + " to act!";
        } else {
            message = ChatColor.DARK_PURPLE + "It is your turn to act!";
        }

        if (settings.displayTurnsPublicly.getValue()) {
            getTable().sendTableMessage(message);
        } else {
            Messages.sendMessage(getPlayer(), message);
        }

        SoundEffect.tableTurnSounds(getTable(), getPlayerName());
        SoundEffect.turn(getPlayer());
        startTurnTimer();
    }

}
