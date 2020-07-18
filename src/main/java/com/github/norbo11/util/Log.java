package com.github.norbo11.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.util.config.PluginConfig;

public class Log {

    private static Logger logger = UltimateCards.getInstance().getLogger();
    private static File fileLog = null;
    
    public static void addToLog(String message) {
        // Only send to the log if it is enabled in the config
        if (PluginConfig.isEnableLog()) {
            try {
                // Attempt to write the supplied message as a new line at the
                // end of the log
                FileWriter writer = new FileWriter(getFileLog(), true);
                writer.write(DateMethods.getDate() + " " + message + "\r\n");
                writer.flush();
                writer.close();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Something went wrong when trying to write to the log file! " + e);
            }
        }
    }

    private static File getFileLog() {
        if (fileLog == null) {
            fileLog = new File(UltimateCards.getInstance().getDataFolder(), "log.txt");
            
            if (!fileLog.exists()) {
                try {
                    fileLog.createNewFile();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Something went wrong when trying to create the log file!", e);
                    fileLog = null;
                }
            }
        }

        return fileLog;
    }

    // Logs the supplied command, it's sender and all of it's arguments
    public static void logCommand(CommandSender sender, Command command, String args[]) {
        String arguments = "";
        for (String argument : args) {
            // Goes through the array of given arguments, appends them at the
            // back of the arguments variable
            arguments = arguments + " " + argument;
        }
        addToLog(DateMethods.getDate() + " " + sender.getName() + ": /" + command.getName() + arguments);
    }

}
