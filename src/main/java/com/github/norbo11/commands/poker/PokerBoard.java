package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.util.ErrorMessages;

public class PokerBoard extends PluginCommand {

    public PokerBoard() {
        getAlises().add("board");
        getAlises().add("community");

        setDescription("Shows the community cards at your table.");

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

        return true;
    }

    // Displays the board to the specified player
    @Override
    public void perform() {
        pokerPlayer.getPokerTable().displayBoard(getPlayer(), pokerPlayer.getPokerTable().getBoard().getCards());
    }
}
