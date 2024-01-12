package com.github.norbo11.util;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class SoundEffect {
    private static UltimateCards plugin = UltimateCards.getInstance();
    private static HashMap<String, BukkitTask> soundTasks = new HashMap<>();

    public static void lost(final Player player) {
        if (player != null) {
            soundTasks.put(player.getName(), Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
                int i = 0;
                float pitch = 2.0F;

                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, pitch);
                    pitch -= 0.1F;
                    i++;
                    if (i == 7) {
                        soundTasks.get(player.getName()).cancel();
                    }
                }
            }, 0L, 1L));
        }
    }

    private static void otherTurn(final Player player) {
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0F, 1.0F);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0F, 2.0F);
        }
    }

    // Plays turn sounds at a table apart from the player specified in the argument
    public static void tableTurnSounds(CardsTable table, String player) {
        for (CardsPlayer p : table.getPlayersThisHand()) {
            if (player.equals(p.getPlayerName())) {
                SoundEffect.otherTurn(p.getPlayer());
            }
        }
    }

    public static void turn(Player player) {
        if (player != null) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, 1.0F);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, 2.0F);
        }
    }

    public static void won(final Player player) {
        if (player != null) {
            soundTasks.put(player.getName(), Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
                int i = 0;
                float pitch = 1.0F;

                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0F, pitch);
                    pitch += 0.1F;
                    i++;
                    if (i == 7) {
                        soundTasks.get(player.getName()).cancel();
                    }
                }
            }, 0L, 1L));
        }
    }
}
