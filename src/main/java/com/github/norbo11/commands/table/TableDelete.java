package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableDelete extends PluginCommand {
    public TableDelete() {
        getAlises().add("delete");
        getAlises().add("del");
        getAlises().add("d");

        setDescription("Deletes your table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private CardsPlayer cardsPlayer;

    @Override
    public boolean conditions() {
        if (getArgs().length != 1) {
            showUsage();
            return false;
        }

        cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());

        if (cardsPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }

        CardsTable cardsTable = cardsPlayer.getTable();

        if (!cardsTable.isOwner(cardsPlayer.getPlayerName())) {
            ErrorMessages.playerNotOwner(getPlayer());
            return false;
        }

        return cardsPlayer.getTable().canBeDeleted();
    }

    // Deletes the players's table
    @Override
    public void perform() {
        cardsPlayer.getTable().restoreAllMaps();
        cardsPlayer.getTable().deleteTable();
    }
}
