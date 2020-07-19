package com.github.norbo11.commands.poker;

import org.bukkit.entity.Player;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerReveal extends PluginCommand {
    public PokerReveal(Player player, String[] args) {
        super(player, args);
        getAlises().add("reveal");
        getAlises().add("show");
        getAlises().add("display");

        setDescription("Shows your hand to everyone around the table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    public PokerReveal() {
        this(null, null);
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

        PokerTable pokerTable = pokerPlayer.getPokerTable();

        if (pokerPlayer.isEliminated()) {
            ErrorMessages.playerIsEliminated(getPlayer());
            return false;
        }
        // If it is showdown
        if (pokerTable.getCurrentPhase() != PokerPhase.SHOWDOWN) {
            ErrorMessages.cantReveal(getPlayer());
            return false;
        }
        if (!pokerPlayer.isAction()) {
            ErrorMessages.notYourTurn(getPlayer());
            return false;
        }

        return true;
    }

    // Publicly reveals the player's hand to everybody
    @Override
    public void perform() {
        pokerPlayer.reveal();
    }
}
