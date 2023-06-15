package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.JoinEvent;

import java.util.ArrayList;
import java.util.List;

public class RandomCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof ConsoleCommandSender) return false;
        Player player = (Player) sender;
        Boolean to = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId()).swapRandomItem();
        if(to) {
            player.sendMessage(Utils.cmdMsg("<green>Turned on random items. You will now recieve a random item every 60 seconds."));
            JoinEvent.randomItemList.add(player);
        } else {
            player.sendMessage(Utils.cmdMsg("<green>Turned off random items. You will no longer recieve a random item every 60 seconds."));
            JoinEvent.randomItemList.remove(player);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
