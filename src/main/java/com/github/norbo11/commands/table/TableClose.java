package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableClose extends PluginCommand {
    public TableClose() {
        getAlises().add("close");
        getAlises().add("lock");
        getAlises().add("c");

        setDescription("Closes your table.");

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
        if (!cardsTable.isOpen()) {
            ErrorMessages.tableAlreadyClosed(getPlayer());
            return false;
        }

        return true;
    }

    // Closes the table and doesn't allow any more people to sit
    @Override
    public void perform() {
        cardsTable.setOpen(false);
        cardsTable.sendTableMessage("Table named &6" + cardsTable.getName() + "&f, ID #&6" + cardsTable.getId() + "&f is now closed! Players now can't join!");
    }
}
