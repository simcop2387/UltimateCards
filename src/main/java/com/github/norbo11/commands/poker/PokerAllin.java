package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerAllin extends PluginCommand {

    public PokerAllin() {
        getAlises().add("allin");
        getAlises().add("shove");
        getAlises().add("a");

        setDescription("Bets the rest of your stack and puts you in all in mode.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    private PokerPlayer pokerPlayer;

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

        return true;
    }

    // Declares the specified player all in
    @Override
    public void perform() {
        double betAmount = pokerPlayer.getMoney() + pokerPlayer.getCurrentBet();
        pokerPlayer.bet(betAmount, null);
    }
}
