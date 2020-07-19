package com.github.norbo11.game.blackjack;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.DateMethods;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.Timers;

public class BlackjackTable extends CardsTable {

    public BlackjackTable(String owner, String name, int id, Location location, double buyin) {
        super(owner, name, id);

        if (buyin != 0) {
            if (Bukkit.getPlayer(owner) != null) {
                setOwnerPlayer(new BlackjackPlayer(Bukkit.getPlayer(owner), this, buyin));
                getPlayers().add(getOwnerPlayer()); // Add the owner to the sitting players list
            }
        }

        setCardsTableSettings(new BlackjackTableSettings(this));
        getSettings().startLocation.setValue(location);
    }

    public BlackjackTable(String owner, String name, int id, Location location) throws Exception {
        this(owner, name, id, location, 0);
    }
    
    // Generic vars
    private BlackjackDealer dealer = new BlackjackDealer(this);

    public static ArrayList<BlackjackTable> getBlackjackTables() {
        ArrayList<BlackjackTable> returnValue = new ArrayList<>();

        for (CardsTable table : getTables()) {
            returnValue.add((BlackjackTable) table);
        }

        return returnValue;
    }

    @Override
    public void autoStart() {
        if (getSettings().autoStart.getValue() > 0) {
            sendTableMessage("Next round in &6" + getSettings().autoStart.getValue() + "&f seconds... or use &6/table start");

            if (getTimerTask() != null) {
                getTimerTask().cancel();
                setTimerTask(null);
            }

            setTimerTask(Timers.startTimerAsync(() -> {
                if (canDeal()) {
                    deal();
                }
            }, getSettings().autoStart.getValue()));
        }
    }

    @Override
    public boolean canDeal() {
        // If there are not enough players to continue the hand
        if (getPlayersThisHand().size() < getMinPlayers()) {
            sendTableMessage("&cNobody has placed a bet (&6/bj bed [amount]&c)! Cannot start the game.");
            return false;
        }

        return true;
    }

    public boolean canPlay(CardsPlayer player) {
        return player.getMoney() > getSettings().minBet.getValue();
    }

    private void clearDealerVars() {
        dealer.getHand().getCards().clear();
        dealer.setBust(false);
        dealer.setScore(0);
    }

    @Override
    public void clearPlayerVars() {
        for (BlackjackPlayer player : getBlackjackPlayers()) {
            player.clearHandBets();
            player.setHitted(false);
            player.setDoubled(false);
        }
        setActionPlayer(null);
    }

    public void continueHand() {
        setToBeContinued(false);
        deal();
    }

    // Method to deal a brand new hand
    @Override
    public void deal() {
        shiftIDs();

        // If there are enough players to play another hand, then do so
        if (canDeal()) {
            setHandNumber(getHandNumber() + 1);
            sendTableMessage("Dealing hand number &6" + getHandNumber());
            sendTableMessage("&6" + UltimateCards.LINE_STRING);

            setInProgress(true);
            getDeck().shuffle();
            clearDealerVars();
            moveButton();
            dealCards();
            displayScores();

            sendTableMessage("&6" + UltimateCards.LINE_STRING);
            nextPersonTurn(getNextPlayer(getButton()));
        }
    }

    @Override
    public void dealCards() {
        // Go through all players, clear their hands and add their cards.
        for (BlackjackPlayer blackjackPlayer : getBjPlayersThisHand()) {
            blackjackPlayer.clearHands();
            blackjackPlayer.getHands().get(0).addCards(getDeck().generateCards(2));
        }

        // Gives the dealer 2 cards
        dealer.addInitialCards();
    }

    private void displayScores() {
        for (BlackjackPlayer player : getBjPlayersThisHand()) {
            player.displayScore();
        }
    }

    private BlackjackPlayer getActionBlackjackPlayer() {
        return (BlackjackPlayer) getActionPlayer();
    }

    public ArrayList<BlackjackPlayer> getBjPlayersThisHand() {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<>();

        for (CardsPlayer player : getPlayersThisHand()) {
            returnValue.add((BlackjackPlayer) player);
        }

        return returnValue;
    }

    public ArrayList<BlackjackPlayer> getBlackjackPlayers() {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<>();

        for (CardsPlayer player : getPlayers()) {
            returnValue.add((BlackjackPlayer) player);
        }

        return returnValue;
    }

    private ArrayList<BlackjackPlayer> getBustedPlayers() {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<>();

        for (BlackjackPlayer blackjackPlayer : getBjPlayersThisHand())
            if (blackjackPlayer.isBustOnAllHands()) {
                returnValue.add(blackjackPlayer);
            }

        return returnValue;
    }

    public BlackjackDealer getDealer() {
        return dealer;
    }

    @Override
    public int getMinPlayers() {
        return 1;
    }

    // Method to get the player 1 after the index specified, and loop back to
    // the beginning if the end is reached
    @Override
    public BlackjackPlayer getNextPlayer(int index) {
        if (index + 1 >= getPlayersThisHand().size()) return getBjPlayersThisHand().get((index + 1) % getBjPlayersThisHand().size()); // If
        else return getBjPlayersThisHand().get(index + 1); // If the end of the players is not reached simply return the player 1 after the given index
    }

    @Override
    public ArrayList<CardsPlayer> getPlayersThisHand() {
        ArrayList<CardsPlayer> returnValue = new ArrayList<>();

        for (BlackjackPlayer player : getBlackjackPlayers()) {
            if (player.playingThisHand()) {
                returnValue.add(player);
            }
        }

        return returnValue;
    }

    private ArrayList<BlackjackPlayer> getReadyPlayers() {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<>();

        for (BlackjackPlayer player : getBjPlayersThisHand())
            if (player.isDoubled() || player.isStayedOnAllHands() || player.isBustOnAllHands()) {
                returnValue.add(player);
            }

        return returnValue;
    }

    @Override
    public BlackjackTableSettings getSettings() {
        return (BlackjackTableSettings) getCardsTableSettings();
    }

    public ArrayList<BlackjackPlayer> getStayedPlayers() {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<>();

        for (BlackjackPlayer blackjackPlayer : getBjPlayersThisHand())
            if (blackjackPlayer.isStayedOnAllHands()) {
                returnValue.add(blackjackPlayer);
            }

        return returnValue;
    }

    private void handEnd() {
        // If not all players are bust
        if (getBustedPlayers().size() != getPlayersThisHand().size()) {
            sendTableMessage("&6" + UltimateCards.LINE_STRING);
            dealer.reveal();
            while (dealer.isUnderStayValue()) {
                dealer.hit();
            }
        }

        sendTableMessage("&6" + UltimateCards.LINE_STRING);
        for (BlackjackPlayer pushingPlayer : payPlayers()) {
            sendTableMessage("&6" + pushingPlayer.getPlayerName() + "&f is pushing for &6" + Formatter.formatMoney(pushingPlayer.getPushingAmount()) + "&f next hand.");
        }

        setToBeContinued(true);
        setInProgress(false);
        clearPlayerVars();
        sendTableMessage("Hand ended. Please place bets again (&6/bj bed [amount]&f).");
    }

    @Override
    public void kick(CardsPlayer player) {
        BlackjackPlayer blackjackPlayer = (BlackjackPlayer) player;

        sendTableMessage("&6" + blackjackPlayer + " &fhas been kicked from the table!", player.getPlayerName());

        returnMoney(blackjackPlayer);
        removePlayer(blackjackPlayer);
        shiftIDs();

        if (blackjackPlayer.isOnline()) {
            blackjackPlayer.getPlayer().teleport(blackjackPlayer.getStartLocation());
            Messages.sendMessage(blackjackPlayer.getPlayer(), "&6You have been kicked from this blackjack table! You receive your remaining stack of &6" + Formatter.formatMoney(blackjackPlayer.getMoney()));
        }

        if (blackjackPlayer == getActionPlayer()) {
            nextPersonTurn(blackjackPlayer);
        }
        removePlayer(player);
    }

    @Override
    public ArrayList<String> listPlayers() {
        ArrayList<String> list = new ArrayList<>();

        for (BlackjackPlayer player : getBlackjackPlayers()) {
            String temp = "[" + player.getID() + "] ";

            if (player.isOnline()) {
                temp = temp + "&6" + player.getPlayerName() + " - ";
            } else {
                temp = temp + "&c" + player.getPlayerName() + "&f - ";
            }

            if (player.isAction()) {
                temp = temp.replace(player.getPlayerName(), ChatColor.BOLD + player.getPlayerName() + "&6");
            }

            if (getChipLeader() == player) {
                temp = temp.replace(player.getPlayerName(), ChatColor.BOLD + player.getPlayerName() + "&6");
            }

            temp = temp + "&6" + Formatter.formatMoney(player.getMoney());
            list.add(temp);
        }

        list.add("Average stack size: &6" + Formatter.formatMoney(getAverageStack()));
        list.add("&cOFFLINE &f| &6&nACTION&r &f| &6&lCHIP LEADER");

        return list;
    }

    @Override
    // Move the action to the players after the one specified in the argument
    public void nextPersonTurn(CardsPlayer lastPlayer) {
        if (getReadyPlayers().size() == getPlayersThisHand().size()) {
            handEnd();
            return;
        }

        for (CardsPlayer cardsPlayer : getRearrangedPlayers(getNextPlayer(getPlayersThisHand().indexOf(lastPlayer)))) {
            BlackjackPlayer player = (BlackjackPlayer) cardsPlayer;
            if (!player.isBustOnAllHands() && !player.isStayedOnAllHands() && !player.isDoubled()) {
                setActionPlayer(player);
                break;
            }
        }

        getActionBlackjackPlayer().takeAction();
    }

    // Pays players and returns pushing list
    private ArrayList<BlackjackPlayer> payPlayers() {
        ArrayList<BlackjackPlayer> pushingPlayers = new ArrayList<>();

        for (BlackjackPlayer blackjackPlayer : getBjPlayersThisHand()) {
            for (BlackjackHand hand : blackjackPlayer.getHands())
                if (!hand.isBust()) {
                    if (!dealer.isBust()) {
                        if (hand.getScore() > dealer.getScore()) {
                            blackjackPlayer.pay(hand);
                        } else if (hand.getScore() == dealer.getScore() && blackjackPlayer.isHitted()) {
                            blackjackPlayer.setPushingAmount(hand.getAmountBet());
                            pushingPlayers.add(blackjackPlayer);
                        } else {
                            dealer.pay(blackjackPlayer, hand);
                        }
                    } else {
                        blackjackPlayer.pay(hand);
                    }
                } else {
                    dealer.pay(blackjackPlayer, hand);
                }
        }

        return pushingPlayers;
    }

    // Showdown method
    public void phaseShowdown() {
        sendTableMessage("Showdown time!");
        sendTableMessage(dealer.getHand().getHand());
        nextPersonTurn(getNextPlayer(getButton()));
    }

    @Override
    public void playerLeave(CardsPlayer player) {
        MapMethods.restoreMap(player.getPlayer().getName(), true);
    }

    @Override
    public BlackjackPlayer playerSit(Player player, double buyin) {
        BlackjackPlayer blackjackPlayer = new BlackjackPlayer(player, this, buyin);
        getPlayers().add(blackjackPlayer);
        return blackjackPlayer;
    }

    @Override
    public void returnMoney(CardsPlayer cardsPlayer) {
        BlackjackPlayer blackjackPlayer = (BlackjackPlayer) cardsPlayer;

        if (blackjackPlayer.isOnline()) {
            blackjackPlayer.getPlayer().teleport(blackjackPlayer.getStartLocation());
            Messages.sendMessage(blackjackPlayer.getPlayer(), "You have been paid your remaining stack of &6" + Formatter.formatMoney(blackjackPlayer.getMoney() + blackjackPlayer.getTotalAmountBet()));
        }
        UltimateCards.getInstance().getEconomy().depositPlayer(blackjackPlayer.getPlayer(), blackjackPlayer.getMoney() + blackjackPlayer.getTotalAmountBet());
        Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + (blackjackPlayer.getMoney() + blackjackPlayer.getTotalAmountBet()) + " to " + blackjackPlayer.getPlayerName());
    }
}
