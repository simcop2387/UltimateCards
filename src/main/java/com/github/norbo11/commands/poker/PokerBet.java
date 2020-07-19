package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class PokerBet extends PluginCommand {

    public PokerBet() {
        getAlises().add("raiseto");
        getAlises().add("bet");
        getAlises().add("raise");
        getAlises().add("b");

        setDescription("Bets or raises to the specified amount.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    private PokerPlayer pokerPlayer;

    private double amountToBet;

    @Override
    public boolean conditions() {
        if (getArgs().length != 2) {
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

        PokerTable pokerTable = pokerPlayer.getPokerTable();

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

        amountToBet = NumberMethods.getDouble(getArgs()[1]);

        if (amountToBet == -99999) {
            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
            return false;
        }
        if (!pokerPlayer.hasMoney(amountToBet - pokerPlayer.getCurrentBet())) {
            ErrorMessages.notEnoughMoney(getPlayer(), amountToBet, pokerPlayer.getMoney());
            return false;
        }
        // Raise
        if (amountToBet > pokerTable.getCurrentBet()) {
            if (amountToBet - pokerTable.getCurrentBet() < pokerTable.getSettings().minRaise.getValue()) {
                ErrorMessages.betBelowMinRaise(getPlayer(), pokerTable.getSettings().minRaise.getValue(),
                        pokerTable.getCurrentBet());
                return false;
            }
            return true;

        // Call
        } else if (amountToBet == pokerTable.getCurrentBet()) {
            return true;
        } else {
            ErrorMessages.betBelowCurrentBet(getPlayer());
            return false;
        }
    }

    // Bets the specified amountToBet in the name of the specified player.
    // This method is only called when raising or betting for the first time in
    // the phase. You cannot bet less than the current bet of the table
    @Override
    public void perform() {
        pokerPlayer.bet(amountToBet, null);
    }

}
