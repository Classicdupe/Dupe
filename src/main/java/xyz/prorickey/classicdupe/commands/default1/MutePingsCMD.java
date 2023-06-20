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
import xyz.prorickey.classicdupe.database.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class MutePingsCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
        if(playerData.getMutePings()) {
            playerData.setMutePings(false);
            player.sendMessage(Utils.cmdMsg("<green>Unmuted pings, you will not get dinged when someone pings you"));
        } else {
            playerData.setMutePings(true);
            player.sendMessage(Utils.cmdMsg("<green>Pings are now muted"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
