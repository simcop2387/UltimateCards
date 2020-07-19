package com.github.norbo11.commands.poker;

import java.util.ArrayList;
import java.util.Arrays;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class PokerHand extends PluginCommand {
    public PokerHand() {
        getAlises().add("cards");
        getAlises().add("hand");

        setDescription("Displays your hand.");

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

        if (pokerPlayer == null || pokerPlayer.getHand().getCards().isEmpty()) {
            ErrorMessages.playerHasNoHand(getPlayer());
            return false;
        }

        return true;
    }

    // Displays the player's hand (to himself only)
    @Override
    public void perform() {
        Messages.sendMessage(getPlayer(), "Your hand:");
        Messages.sendMessage(getPlayer(), "&6" + UltimateCards.LINE_STRING);
        Messages.sendMessage(getPlayer(), new ArrayList<>(Arrays.asList(pokerPlayer.getHand().getHand())));
    }
}
