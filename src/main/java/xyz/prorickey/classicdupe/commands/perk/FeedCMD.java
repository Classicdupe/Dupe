package xyz.prorickey.classicdupe.commands.perk;

import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;

import java.util.*;

public class FeedCMD implements CommandExecutor, TabCompleter {

    private static final Map<UUID, Long> feedCooldown = new HashMap<>();

    // Basic: 1 minute cooldown
    // Vip: 1 minute cooldown
    // MVP: 30 second cooldown
    // Legend 0 minute cooldown

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (player.hasPermission("perks.feed.legend")) {
            player.setFoodLevel(20);
            player.sendMessage(Utils.cmdMsg("<green>You have been fed"));
            player.playSound(player, "entity.generic.eat", SoundCategory.MASTER, 1F, 1F);
        } else if (player.hasPermission("perks.feed.mvp")) {
            if (feedCooldown.containsKey(player.getUniqueId()) && feedCooldown.get(player.getUniqueId()) + (1000 * 30) > System.currentTimeMillis()) {
                long wait = feedCooldown.get(player.getUniqueId()) + (1000 * 30) - System.currentTimeMillis();
                player.sendMessage(Utils.cmdMsg("<red>You cannot execute this command for " + (wait / 1000) + " seconds(s)"));
                return true;
            }
            feedCooldown.put(player.getUniqueId(), System.currentTimeMillis());
            player.setFoodLevel(20);
            player.sendMessage(Utils.cmdMsg("<green>You have been fed"));
            player.playSound(player, "entity.generic.eat", SoundCategory.MASTER, 1F, 1F);
        } else {
            if (feedCooldown.containsKey(player.getUniqueId()) && feedCooldown.get(player.getUniqueId()) + (1000 * 60) > System.currentTimeMillis()) {
                long wait = feedCooldown.get(player.getUniqueId()) + (1000 * 60) - System.currentTimeMillis();
                player.sendMessage(Utils.cmdMsg("<red>You cannot execute this command for " + (wait / 1000) + " seconds(s)"));
                return true;
            }
            feedCooldown.put(player.getUniqueId(), System.currentTimeMillis());
            player.setFoodLevel(20);
            player.sendMessage(Utils.cmdMsg("<green>You have been fed"));
            player.playSound(player, "entity.generic.eat", SoundCategory.MASTER, 1F, 1F);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
