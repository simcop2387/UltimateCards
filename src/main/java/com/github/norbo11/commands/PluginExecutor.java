package com.github.norbo11.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.norbo11.commands.blackjack.BlackjackBet;
import com.github.norbo11.commands.blackjack.BlackjackDouble;
import com.github.norbo11.commands.blackjack.BlackjackHit;
import com.github.norbo11.commands.blackjack.BlackjackSplit;
import com.github.norbo11.commands.blackjack.BlackjackStand;
import com.github.norbo11.commands.poker.PokerAllin;
import com.github.norbo11.commands.poker.PokerBet;
import com.github.norbo11.commands.poker.PokerBoard;
import com.github.norbo11.commands.poker.PokerCall;
import com.github.norbo11.commands.poker.PokerCheck;
import com.github.norbo11.commands.poker.PokerFold;
import com.github.norbo11.commands.poker.PokerHand;
import com.github.norbo11.commands.poker.PokerPot;
import com.github.norbo11.commands.poker.PokerReveal;
import com.github.norbo11.commands.table.TableBan;
import com.github.norbo11.commands.table.TableClose;
import com.github.norbo11.commands.table.TableCreate;
import com.github.norbo11.commands.table.TableDelete;
import com.github.norbo11.commands.table.TableDetails;
import com.github.norbo11.commands.table.TableInvite;
import com.github.norbo11.commands.table.TableKick;
import com.github.norbo11.commands.table.TableLeave;
import com.github.norbo11.commands.table.TableListSettings;
import com.github.norbo11.commands.table.TableMoney;
import com.github.norbo11.commands.table.TableOpen;
import com.github.norbo11.commands.table.TablePlayers;
import com.github.norbo11.commands.table.TableRebuy;
import com.github.norbo11.commands.table.TableReload;
import com.github.norbo11.commands.table.TableSave;
import com.github.norbo11.commands.table.TableSet;
import com.github.norbo11.commands.table.TableSit;
import com.github.norbo11.commands.table.TableStart;
import com.github.norbo11.commands.table.TableTables;
import com.github.norbo11.commands.table.TableTeleport;
import com.github.norbo11.commands.table.TableUnban;
import com.github.norbo11.commands.table.TableUnsave;
import com.github.norbo11.commands.table.TableWithdraw;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.ExceptionCatcher;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.Messages;
import org.jetbrains.annotations.NotNull;

public class PluginExecutor implements CommandExecutor {

    public static ArrayList<PluginCommand> commandsTable = new ArrayList<>();
    public static ArrayList<PluginCommand> commandsPoker = new ArrayList<>();
    public static ArrayList<PluginCommand> commandsBlackjack = new ArrayList<>();
    public static ArrayList<ArrayList<PluginCommand>> commands = new ArrayList<>();

    static {
        commandsTable.add(new TableDetails());
        commandsTable.add(new TableInvite());
        commandsTable.add(new TableLeave());
        commandsTable.add(new TableMoney());
        commandsTable.add(new TablePlayers());
        commandsTable.add(new TableRebuy());
        commandsTable.add(new TableSit());
        commandsTable.add(new TableTables());
        commandsTable.add(new TableTeleport());
        commandsTable.add(new TableWithdraw());
        commandsTable.add(new TableReload());
    
        commandsPoker.add(new PokerHand());
        commandsPoker.add(new PokerAllin());
        commandsPoker.add(new PokerBet());
        commandsPoker.add(new PokerBoard());
        commandsPoker.add(new PokerCall());
        commandsPoker.add(new PokerCheck());
        commandsPoker.add(new PokerFold());
        commandsPoker.add(new PokerPot());
        commandsPoker.add(new PokerReveal());
    
        commandsTable.add(new TableBan());
        commandsTable.add(new TableClose());
        commandsTable.add(new TableCreate());
        commandsTable.add(new TableDelete());
        commandsTable.add(new TableKick());
        commandsTable.add(new TableListSettings());
        commandsTable.add(new TableOpen());
        commandsTable.add(new TableSet());
        commandsTable.add(new TableStart());
        commandsTable.add(new TableUnban());
        commandsTable.add(new TableSave());
        commandsTable.add(new TableUnsave());
    
        commandsBlackjack.add(new BlackjackHit());
        commandsBlackjack.add(new BlackjackStand());
        commandsBlackjack.add(new BlackjackBet());
        commandsBlackjack.add(new BlackjackSplit());
        commandsBlackjack.add(new BlackjackDouble());

        commands.add(commandsTable);
        commands.add(commandsPoker);
        commands.add(commandsBlackjack);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length >= 1) {
                    String action = args[0];
                    Log.logCommand(sender, command, args);

                    if (action.equalsIgnoreCase("help")) {
                        String commandToHelpWith = args.length == 2 ? args[1] : command.getName();
                        ErrorMessages.displayHelp(player, commandToHelpWith);
                        return true;
                    }

                    if (command.getName().equalsIgnoreCase("table") || command.getName().equalsIgnoreCase("cards")) {
                        for (PluginCommand cmd : commandsTable) {
                            if (cmd.containsAlias(action)) {
                                if (cmd.hasPermission(player)) {
                                    performCommand(cmd, args, player);
                                } else {
                                    ErrorMessages.noPermission(player);
                                }
                                return true;
                            }
                        }
                        Messages.sendMessage(player, "&cNo such table command. Check help with &6/table help&c.");
                    }

                    if (command.getName().equalsIgnoreCase("poker")) {
                        for (PluginCommand cmd : commandsPoker) {
                            if (cmd.containsAlias(action)) {
                                if (cmd.hasPermission(player)) {
                                    performCommand(cmd, args, player);
                                } else {
                                    ErrorMessages.noPermission(player);
                                }
                                return true;
                            }
                        }
                        Messages.sendMessage(player, "&cNo such poker command. Check help with &6/poker help&c.");
                    }

                    if (command.getName().equalsIgnoreCase("blackjack") || command.getName().equalsIgnoreCase("bj")) {
                        for (PluginCommand cmd : commandsBlackjack) {
                            if (cmd.containsAlias(action)) {
                                if (cmd.hasPermission(player)) {
                                    performCommand(cmd, args, player);
                                } else {
                                    ErrorMessages.noPermission(player);
                                }
                                return true;
                            }
                        }
                        Messages.sendMessage(player, "&cNo such blackjack command. Check help with &6/blackjack | /bj help&c.");
                    }
                } else {
                    ErrorMessages.displayHelp(player, command.getName());
                }
            } else {
                ErrorMessages.notHumanPlayer(sender);
            }
        } catch (Exception e) {
            ExceptionCatcher.catchException(e, command, sender, args);
        }
        return true;
    }

    private void performCommand(PluginCommand cmd, String[] args, Player player) throws Exception {
        cmd.setArgs(args);
        cmd.setPlayer(player);
        if (cmd.conditions()) {
            cmd.perform();
        }
    }
}
