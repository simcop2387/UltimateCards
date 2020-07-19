package com.github.norbo11.commands.blackjack;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class BlackjackHit extends PluginCommand {

    public BlackjackHit() {
        getAlises().add("hit");
        getAlises().add("hitme");
        getAlises().add("h");

        setDescription("Gives you an additional card.");

        setArgumentString("(hand ID)");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack." + getAlises().get(0));
    }

    private BlackjackPlayer blackjackPlayer;

    private int hand = 0;

    @Override
    public boolean conditions() {
        hand = 0;
        if (getArgs().length != 1 && getArgs().length != 2) {
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

        if (getArgs().length == 2) {
            if (!blackjackPlayer.isSplit()) {
                ErrorMessages.cannotSpecifyHand(getPlayer());
                return false;
            }
            hand = NumberMethods.getPositiveInteger(getArgs()[1]);
            if (hand != 0 && hand != 1) {
                ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                return false;
            }
        } else if (blackjackPlayer.isSplit()) {
            ErrorMessages.needToSpecifyHand(getPlayer());
            return false;
        }

        if (blackjackPlayer.getHands().get(hand).isBust()) {
            ErrorMessages.playerIsBust(getPlayer());
            return false;
        }
        if (!blackjackPlayer.getHands().get(hand).isStayed()) {
            ErrorMessages.playerIsStayed(getPlayer());
        }
        
        return true;
    }

    @Override
    public void perform() {
        blackjackPlayer.hit(hand);
    }
}
