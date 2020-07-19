package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class TableKick extends PluginCommand {
    public TableKick() {
        getAlises().add("kick");
        getAlises().add("boot");
        getAlises().add("k");

        setDescription("Kicks the specified player from your table.");

        setArgumentString("[player ID]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private CardsPlayer toKick;

    @Override
    public boolean conditions() {
        if (getArgs().length != 2) {
            showUsage();
            return false;
        }

        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
        if (cardsPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }

        CardsTable cardsTable = cardsPlayer.getTable();
        if (!cardsTable.isOwner(cardsPlayer.getPlayerName())) {
            ErrorMessages.playerNotOwner(getPlayer());
            return false;
        }

        int IDtoKick = NumberMethods.getPositiveInteger(getArgs()[1]);
        if (IDtoKick == -99999) {
            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
            return false;
        }

        toKick = CardsPlayer.getCardsPlayer(IDtoKick, cardsTable);
        // Check if the ID specified is a real poker player.
        if (toKick == null) {
            ErrorMessages.notPlayerID(getPlayer(), IDtoKick);
            return false;
        }

        return true;
    }

    // Kicks the specified player from the owner's table
    @Override
    public void perform() {
        toKick.getTable().kick(toKick);
    }
}
