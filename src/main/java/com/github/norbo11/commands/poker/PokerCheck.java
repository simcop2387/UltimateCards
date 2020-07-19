package com.github.norbo11.commands.poker;

import org.bukkit.entity.Player;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerCheck extends PluginCommand {

    public PokerCheck() {
        this(null, null);
    }

    public PokerCheck(Player player, String[] args) {
        super(player, args);

        getAlises().add("check");
        getAlises().add("ch");

        setDescription("Checks your hand.");

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
        if (pokerTable.getCurrentPhase() == PokerPhase.SHOWDOWN) {
            ErrorMessages.tableAtShowdown(getPlayer());
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
        if (pokerPlayer.isAllIn()) {
            ErrorMessages.playerIsAllIn(getPlayer());
            return false;
        }
        if (pokerPlayer.getCurrentBet() != pokerTable.getCurrentBet()) {
            ErrorMessages.cantCheck(pokerPlayer);
            return false;
        }

        return true;
    }

    // Checks the turn of the specified player()
    @Override
    public void perform() {
        pokerPlayer.check();
    }
}
