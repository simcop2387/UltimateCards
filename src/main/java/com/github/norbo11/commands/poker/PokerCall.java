package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerCall extends PluginCommand {

    public PokerCall() {
        getAlises().add("call");
        getAlises().add("match");
        getAlises().add("ca");

        setDescription("Matches your total bet with the rest of the table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    private PokerPlayer pokerPlayer;

    private PokerTable pokerTable;

    @Override
    public boolean conditions() {
        if (getArgs().length != 1) {
            showUsage();
            return false;
        }

        pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());

        if (pokerPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }
        if (pokerPlayer.isEliminated()) {
            ErrorMessages.playerIsEliminated(getPlayer());
            return false;
        }

        pokerTable = pokerPlayer.getPokerTable();

        if (!pokerTable.isInProgress()) {
            ErrorMessages.tableNotInProgress(getPlayer());
            return false;
        }
        if (pokerTable.getCurrentPhase() == PokerPhase.SHOWDOWN) {
            ErrorMessages.tableAtShowdown(getPlayer());
            return false;
        }
        if (!pokerPlayer.isAction()) {
            ErrorMessages.notYourTurn(getPlayer());
            return false;
        }
        if (pokerPlayer.isFolded()) {
            ErrorMessages.playerIsFolded(getPlayer());
            return false;
        }
        if (pokerPlayer.isAllIn()) {
            ErrorMessages.playerIsAllIn(getPlayer());
            return false;
        }
        // Check if the player hasn't already called
        if (pokerPlayer.getCurrentBet() >= pokerTable.getCurrentBet()) {
            ErrorMessages.cantCall(getPlayer());
            return false;
        }
        if (!pokerPlayer.hasMoney(pokerTable.getCurrentBet() - pokerPlayer.getCurrentBet())) {
            ErrorMessages.notEnoughMoney(getPlayer(), pokerTable.getCurrentBet(), pokerPlayer.getMoney());
            return false;
        }

        return true;
    }

    // Calls the latest bet in the name of the player.
    @Override
    public void perform() {
        double amountCalled = pokerTable.getCurrentBet();
        pokerPlayer.bet(amountCalled, null);
    }
}
