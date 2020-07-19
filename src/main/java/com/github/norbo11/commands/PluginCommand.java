package com.github.norbo11.commands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.norbo11.util.Messages;

public abstract class PluginCommand {
    protected PluginCommand() {
    }

    protected PluginCommand(Player player, String[] args) {
        setPlayer(player);
        setArgs(args);
    }

    protected static final String PERMISSIONS_BASE_NODE = "ucards.";
    private Player player;
    private String[] args;
    private String description;
    private String argumentsString;

    private ArrayList<String> permissionNodes = new ArrayList<>();

    private ArrayList<String> aliases = new ArrayList<>();

    public abstract boolean conditions();

    public boolean containsAlias(String action) {
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(action)) return true;
        }
        return false;
    }

    public String getAliasesString() {
        StringBuilder returnValue = new StringBuilder();
        for (String alias : aliases) {
            returnValue.append(alias).append("&b | &6");
        }

        if (!aliases.isEmpty()) {
            return returnValue.substring(0, returnValue.length() - 7);
        }

        return returnValue.toString();
    }

    public ArrayList<String> getAlises() {
        return aliases;
    }

    protected String[] getArgs() {
        return args;
    }

    public String getArgumentsString() {
        return argumentsString;
    }

    public String getCommandString() {
        if (PluginExecutor.commandsTable.contains(this)) return "&6/table " + getAlises().get(0);
        else if (PluginExecutor.commandsPoker.contains(this)) return "&6/poker " + getAlises().get(0);
        else if (PluginExecutor.commandsBlackjack.contains(this)) return "&6/bj " + getAlises().get(0);
        else return "&6ERROR";
    }

    private String getDescription() {
        return description;
    }

    public ArrayList<String> getPermissionNodes() {
        return permissionNodes;
    }

    protected Player getPlayer() {
        return player;
    }

    public String getUsage() {
        return "&cUsage: &6" + getAliasesString() + " &b" + getArgumentsString() + " - &f" + getDescription();
    }

    public boolean hasPermission(Player player) {
        if (player.hasPermission("ucards")) return true;
        
        for (String node : permissionNodes)
            if (player.hasPermission(node)) return true;
        
        return false;
    }

    public abstract void perform() throws Exception;

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setArgumentsString(String argumentsString) {
        this.argumentsString = argumentsString;
    }

    protected void setArgumentString(String argumentsString) {
        this.argumentsString = argumentsString;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    protected void showUsage() {
        Messages.sendMessage(player, getUsage());
    }
}
