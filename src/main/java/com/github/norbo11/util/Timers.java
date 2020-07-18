package com.github.norbo11.util;

import com.github.norbo11.UltimateCards;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class Timers {
    private static UltimateCards plugin = UltimateCards.getInstance();

    public static BukkitTask startTimerAsync(final Runnable method, final int seconds) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, method, seconds * 20);
    }

    public static BukkitTask startTimerSync(final Runnable method, final int seconds) {
        return Bukkit.getScheduler().runTaskLater(plugin, method, seconds * 20);
    }
}
