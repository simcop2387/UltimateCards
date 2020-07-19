package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.NumberMethods;

public class TableWithdraw extends PluginCommand {

    public TableWithdraw() {
        getAlises().add("withdraw");
        getAlises().add("cashin");
        getAlises().add("w");

        setDescription("Withdraws the specified amount from your stack.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    private CardsPlayer cardsPlayer;
    private CardsTable cardsTable;

    private double amountToWithdraw;

    // cards withdraw <amount>
    @Override
    public boolean conditions() {
        if (getArgs().length != 2) {
            showUsage();
            return false;
        }
        
        cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
        if (cardsPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }
        
        cardsTable = cardsPlayer.getTable();
        if (!cardsTable.getSettings().allowRebuys.getValue()) {
            ErrorMessages.tableDoesntAllowRebuys(getPlayer());
            return false;
        }
        if (cardsPlayer.getTable().isInProgress()) {
            ErrorMessages.tableInProgress(getPlayer());
            return false;
        }
        
        amountToWithdraw = NumberMethods.getDouble(getArgs()[1]);
        if (amountToWithdraw == -99999) {
            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
            return false;
        }
        if (amountToWithdraw > cardsPlayer.getMoney()) {
            ErrorMessages.notEnoughMoney(getPlayer(), cardsPlayer.getMoney(), amountToWithdraw);
            return false;
        }
        
        return true;
    }

    @Override
    public void perform() {
        cardsPlayer.setMoney(cardsPlayer.getMoney() - amountToWithdraw);
        MoneyMethods.depositMoney(getPlayer(), amountToWithdraw);
        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f withdraws " + "&6" + Formatter.formatMoney(amountToWithdraw) + "&f New balance: " + "&6" + Formatter.formatMoney(cardsPlayer.getMoney()));
    }
}
