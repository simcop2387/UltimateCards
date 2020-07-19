package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableUnban extends PluginCommand {
    public TableUnban() {
        getAlises().add("unban");
        getAlises().add("pardon");
        getAlises().add("forgive");
        getAlises().add("u");

        setDescription("Unbans the player from the table.");

        setArgumentString("[player name]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private CardsPlayer cardsPlayer;
    private CardsTable cardsTable;

    private String toUnBan;

    @Override
    public boolean conditions() {
        if (getArgs().length != 2) {
            showUsage();
            return false;
        }
        
        toUnBan = getArgs()[1];
        cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
        if (cardsPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }
        
        cardsTable = cardsPlayer.getTable();
        if (!cardsTable.isOwner(cardsPlayer.getPlayerName())) {
            ErrorMessages.playerNotOwner(getPlayer());
            return false;
        }
        if (cardsTable.getBannedList().contains(toUnBan)) {
            ErrorMessages.playerNotBanned(getPlayer(), toUnBan);
            return false;
        }
        
        return true;
    }

    // Unbans the specified player from the player's table specified
    // in the first argument
    @Override
    public void perform() {
        cardsTable.getBannedList().remove(toUnBan);
        cardsTable.sendTableMessage("&6" + cardsPlayer.getPlayerName() + " &fhas unbanned &6" + toUnBan + " &ffrom the table!");

    }
}
