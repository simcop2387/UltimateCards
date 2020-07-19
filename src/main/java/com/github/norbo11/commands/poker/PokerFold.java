package com.github.norbo11.commands.poker;

import org.bukkit.entity.Player;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerFold extends PluginCommand {

    public PokerFold() {
        this(null, null);
    }

    public PokerFold(Player player, String[] args) {
        super(player, args);

        getAlises().add("fold");
        getAlises().add("muck");
        getAlises().add("f");

        setDescription("Folds your hand.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    private PokerPlayer pokerPlayer;

    @Override
    public boolean conditions() {
        if (getArgs().length != 1) {
            showUsage();
            return false;
        }

        pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());

        if (pokerPlayer == null) {
            ErrorMessages.notSittingAtTable(getPlayer());
            return false;
        }
        if (pokerPlayer.isEliminated()) {
            ErrorMessages.playerIsEliminated(getPlayer());
            return false;
        }

        PokerTable pokerTable = pokerPlayer.getPokerTable();

        if (!pokerTable.isInProgress()) {
            ErrorMessages.tableNotInProgress(getPlayer());
            return false;
        }
        if (!pokerPlayer.isAction()) {
            ErrorMessages.notYourTurn(getPlayer());
            return false;
        }
        if (pokerPlayer.isFolded()) {
            ErrorMessages.playerIsFolded(getPlayer());
            return false;
        }
        if (!pokerPlayer.isAllIn() & pokerTable.getCurrentPhase() != PokerPhase.SHOWDOWN) {
            ErrorMessages.playerIsAllIn(getPlayer());
            return false;
        }

        return true;
    }

    // Folds the hand of the specified player
    // Clears the player's hand, sets their folded flag to true and
    // displays a message. Then goes to the turn of the next player
    @Override
    public void perform() {
        pokerPlayer.fold();
    }
}
