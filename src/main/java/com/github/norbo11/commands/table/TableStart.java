package com.github.norbo11.commands.table;

import org.bukkit.scheduler.BukkitTask;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableStart extends PluginCommand {

    public TableStart() {
        getAlises().add("start");
        getAlises().add("go");
        getAlises().add("s");

        setDescription("Starts the game at your table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    private CardsTable cardsTable;

    // Starts the player's table if they are the owner.
    // table start
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
        if (cardsTable.isInProgress()) {
            ErrorMessages.tableInProgress(getPlayer());
            return false;
        }
        if (cardsTable.getPlayers().size() < cardsTable.getMinPlayers()) {
            ErrorMessages.notEnoughPlayers(getPlayer());
            return false;
        }

        return true;
    }

    @Override
    public void perform() {
        BukkitTask timerTask = cardsTable.getTimerTask();
        if (timerTask != null) {
            timerTask.cancel();
            cardsTable.setTimerTask(null);
        }
        cardsTable.deal();
    }
}
