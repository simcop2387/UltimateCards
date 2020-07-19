package com.github.norbo11.commands.blackjack;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class BlackjackBet extends PluginCommand {

    public BlackjackBet() {
        getAlises().add("bet");
        getAlises().add("b");

        setDescription("Bets an amount of money and puts you in the next hand.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack." + getAlises().get(0));
    }

    private BlackjackPlayer blackjackPlayer;

    private double amountToBet;

    @Override
    public boolean conditions() {
        if (getArgs().length != 2) {
            showUsage();
            return false;
        }

        blackjackPlayer = BlackjackPlayer.getBlackjackPlayer(getPlayer().getName());

        if (blackjackPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }
        if (blackjackPlayer.getTable().getOwnerPlayer() != blackjackPlayer) {
            ErrorMessages.playerIsBlackjackDealer(getPlayer());
            return false;
        }

        BlackjackTable blackjackTable = blackjackPlayer.getTable();
        amountToBet = NumberMethods.getDouble(getArgs()[1]);

        if (amountToBet == -99999) {
            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
            return false;
        }
        if (blackjackPlayer.getMoney() < amountToBet) {
            ErrorMessages.notEnoughMoney(getPlayer(), amountToBet, blackjackPlayer.getMoney());
            return false;
        }

        blackjackTable = blackjackPlayer.getTable();

        if (!blackjackTable.getDealer().hasEnoughMoney(amountToBet)) {
            ErrorMessages.dealerHasNotEnoughMoney(getPlayer(),
                    blackjackTable.getOwnerPlayer().getMoney() / ((blackjackTable.getPlayers().size() - 1) * 2));

            return false;
        }
        if (amountToBet < blackjackTable.getSettings().minBet.getValue()) {
            ErrorMessages.tooSmallBet(getPlayer(), blackjackTable.getSettings().minBet.getValue());
            return false;
        }
        if (!blackjackTable.isInProgress()) {
            ErrorMessages.tableInProgress(getPlayer());
            return false;
        }

        return true;
    }

    @Override
    public void perform() {
        blackjackPlayer.bet(amountToBet);
    }
}
