package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.Combat;

import java.util.ArrayList;
import java.util.List;

public class BackCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(Combat.inCombat.containsKey(p)) {
            p.sendMessage(Utils.cmdMsg("<red>You cannot execute this command in combat"));
            return true;
        }
        if(TpaCMD.getLocation(p) == null) {
            p.sendMessage(Utils.cmdMsg("<red>You have no location to teleport back to"));
            return true;
        }
        p.teleport(TpaCMD.getLocation(p));
        p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 3.0F, 0.533F);
        p.sendMessage(Utils.cmdMsg("<green>Teleported you back to your previous location"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
