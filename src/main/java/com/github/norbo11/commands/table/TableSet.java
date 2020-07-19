package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableSet extends PluginCommand {
    public TableSet() {
        getAlises().add("set");

        setDescription("Sets the [setting] to the [value]");

        setArgumentString("[setting] [value]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private CardsTable cardsTable;

    // table set <Setting> <value>
    @Override
    public boolean conditions() {
        if (getArgs().length != 2 & getArgs().length != 3) {
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
        if (cardsTable.isInProgress()) {
            ErrorMessages.tableInProgress(getPlayer());
            return false;
        }

        return true;
    }

    // Sets the specified setting on the player's table, to the specified
    // value.
    @Override
    public void perform() {
        String setting = getArgs()[1];

        if (setting.equalsIgnoreCase("startLocation")) {
            cardsTable.getSettings().setStartLocation(getPlayer().getLocation());
        } else if (setting.equalsIgnoreCase("leaveLocation")) {
            cardsTable.getSettings().setLeaveLocation(getPlayer().getLocation());
        } else if (getArgs().length == 3) {
            String value = getArgs()[2];
            cardsTable.getSettings().setSetting(setting, value);
        }

    }
}
