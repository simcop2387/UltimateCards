package com.github.norbo11.commands.blackjack;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.util.ErrorMessages;

public class BlackjackSplit extends PluginCommand {

    public BlackjackSplit() {
        getAlises().add("split");
        getAlises().add("sp");

        setDescription("Splits your cards and puts each card in a seperate hand.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack." + getAlises().get(0));
    }

    private BlackjackPlayer blackjackPlayer;

    @Override
    public boolean conditions() {
        if (getArgs().length != 1) {
            showUsage();
            return false;
        }
        
        blackjackPlayer = BlackjackPlayer.getBlackjackPlayer(getPlayer().getName());
        
        if (blackjackPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }

        BlackjackTable blackjackTable = blackjackPlayer.getTable();

        if (!blackjackTable.isInProgress()) {
            ErrorMessages.tableNotInProgress(getPlayer());
            return false;
        }
        if (!blackjackPlayer.isAction()) {
            ErrorMessages.notYourTurn(getPlayer());
            return false;
        }
        if (blackjackPlayer.isSplit()) {
            ErrorMessages.playerIsStayed(getPlayer());
            return false;
        }
        if (blackjackPlayer.isHitted()) {
            ErrorMessages.playerIsSplit(getPlayer());
            return false;
        }
        if (!blackjackPlayer.sameHoleCards()) {
            ErrorMessages.holeCardsNotMatching(getPlayer());
            return false;
        }
        if (!blackjackPlayer.hasMoney(blackjackPlayer.getTotalAmountBet())) {
            ErrorMessages.notEnoughMoney(getPlayer(), blackjackPlayer.getTotalAmountBet(), blackjackPlayer.getMoney());
            return false;
        }
        
        blackjackTable = blackjackPlayer.getTable();
        return true;
    }

    @Override
    public void perform() {
        blackjackPlayer.split();
    }
}
