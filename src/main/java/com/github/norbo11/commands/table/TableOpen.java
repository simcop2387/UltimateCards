package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableOpen extends PluginCommand {
    public TableOpen() {
        getAlises().add("open");
        getAlises().add("unlock");
        getAlises().add("o");

        setDescription("Opens your table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private CardsTable cardsTable;

    @Override
    public boolean conditions() {
        if (getArgs().length != 1) {
            showUsage();
            return false;
        }

        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
        if (cardsPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }

        cardsTable = cardsPlayer.getTable();
        if (!cardsTable.isOwner(cardsPlayer.getPlayerName())) {
            ErrorMessages.playerNotOwner(getPlayer());
            return false;
        }
        if (cardsTable.isOpen()) {
            ErrorMessages.tableAlreadyOpen(getPlayer());
            return false;
        }
        return true;
    }

    // Opens the specified player's table
    @Override
    public void perform() {
        cardsTable.setOpen(true);
        cardsTable.sendTableMessage("Table named &6" + cardsTable.getName() + "&f, ID #&6" + cardsTable.getId() + "&f is now open! Players can now join!");

    }
}
