package com.github.norbo11.commands.blackjack;

import org.bukkit.entity.Player;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class BlackjackStand extends PluginCommand {

    public BlackjackStand() {
        this(null, null);
    }

    public BlackjackStand(Player player, String[] args) {
        super(player, args);

        getAlises().add("stand");
        getAlises().add("stay");
        getAlises().add("s");

        setDescription("Keeps your current score, and stands.");

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
        if (blackjackPlayer.getHands().get(hand).isStayed()) {
            ErrorMessages.playerIsStayed(getPlayer());
            return false;
        }

        blackjackTable = blackjackPlayer.getTable();
        return true;
    }

    @Override
    public void perform() {
        blackjackPlayer.stand(hand);
    }
}
