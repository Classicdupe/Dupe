package xyz.prorickey.classicdupe.commands.moderator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.default1.TpaCMD;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class StaffTeleportCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            p.sendMessage(Utils.cmdMsg("<red>Who would you like to teleport to?"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null || !target.isOnline()) {
            p.sendMessage(Utils.cmdMsg("<red>That player is not currently online"));
            return true;
        }
        TpaCMD.saveLocation(p);
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(target);
        p.setGameMode(GameMode.SPECTATOR);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3.0F, 0.533F);
        p.sendMessage(Utils.cmdMsg("<green>You were successfully teleported! use /back to get to your old location!"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
