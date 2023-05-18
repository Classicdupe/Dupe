package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.JoinEvent;

import java.util.ArrayList;
import java.util.List;

public class NightVisionCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }

        if(JoinEvent.nightVision.get(player)) {
            JoinEvent.nightVision.put(player, false);
            ClassicDupe.getDatabase().getPlayerDatabase().setNightVision(player.getUniqueId().toString(), false);
            player.sendMessage(Utils.cmdMsg("<green>Disabled your night vision effect. Use /night to turn it back on."));
        } else {
            JoinEvent.nightVision.put(player, true);
            ClassicDupe.getDatabase().getPlayerDatabase().setNightVision(player.getUniqueId().toString(), true);
            player.sendMessage(Utils.cmdMsg("<green>Enabled your night vision effect. Use /night to turn it back off."));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
