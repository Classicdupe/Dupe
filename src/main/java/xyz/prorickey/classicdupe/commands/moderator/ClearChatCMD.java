package xyz.prorickey.classicdupe.commands.moderator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

public class ClearChatCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(p -> {
            if(!p.hasPermission("mod.clearchat.bypass")) {
                for(int i = 0; i < 300; i++) p.sendMessage(" ");
            }
            p.sendMessage(Utils.cmdMsg("<red><b>The chat has been cleared"));
        });
        return true;
    }

}
