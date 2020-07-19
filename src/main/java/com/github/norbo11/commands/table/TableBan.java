package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableBan extends PluginCommand {
    public TableBan() {
        getAlises().add("ban");
        getAlises().add("b");

        setDescription("Bans the player from the table.");

        setArgumentString("[player name]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private String toBan;
    private CardsPlayer cardsPlayer;
    private CardsTable cardsTable;

    @Override
    public boolean conditions() {
        if (getArgs().length != 2) {
            showUsage();
            return false;
        }

        toBan = getArgs()[1];
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
        if (cardsTable.getBannedList().contains(toBan)) {
            ErrorMessages.playerAlreadyBanned(getPlayer(), toBan);
            return false;
        }

        return true;
    }

    // Bans the specified player
    @Override
    public void perform() {
        cardsTable.getBannedList().add(toBan);
        cardsTable.sendTableMessage("&6" + cardsPlayer.getPlayerName() + "&f has banned &6" + toBan + "&f from the table!");

    }
}
