package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.NumberMethods;

public class TablePlayers extends PluginCommand {
    public TablePlayers() {
        getAlises().add("players");
        getAlises().add("listplayers");
        getAlises().add("lp");

        setDescription("Lists all players at the specified table. If no table is specified, lists players at the table you're sitting on.");

        setArgumentString("(table ID)");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    private CardsTable cardsTable;

    @Override
    public boolean conditions() {
        // If a table was not specified, make sure that the player is sitting at a
        // table, then display all players on that table to the player
        if (getArgs().length == 1) {
            CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer == null) {
                ErrorMessages.notSittingAtTable(getPlayer());
                return false;
            }
            cardsTable = cardsPlayer.getTable();
            return true;

        } else if (getArgs().length == 2) { // If a table was specified, make sure that the specified table is a real
                                            // table before displaying all it's players

            int tableID = NumberMethods.getPositiveInteger(getArgs()[1]);
            if (tableID == -99999) {
                ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                return false;
            }

            cardsTable = CardsTable.getTable(tableID);
            if (cardsTable == null) {
                ErrorMessages.notTable(getPlayer(), getArgs()[1]);
                return false;
            }
            return true;
        } else {
            showUsage();
            return false;
        }
    }

    // Displays the list of players to the specified player
    @Override
    public void perform() {
        Messages.sendMessage(getPlayer(), cardsTable.listPlayers());
    }
}
