package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.LinkingDatabase;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

import java.util.ArrayList;
import java.util.List;

public class UnlinkCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }

        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString());
        if(link == null) {
            player.sendMessage(Utils.cmdMsg("<red>Your account is not linked"));
            return true;
        }
        ClassicDupe.getDatabase().getLinkingDatabase().unlinkByUUID(player.getUniqueId().toString());
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getPlugin(), () -> {
            ClassicDupeBot.getJDA().getGuildById(1068991438391623836L).removeRoleFromMember(
                    ClassicDupeBot.getJDA().retrieveUserById(link.id).complete(),
                    ClassicDupeBot.getJDA().getRoleById(1078109485144473620L)
            ).queue();
        });
        player.sendMessage(Utils.cmdMsg("<green>Unlinked your account from " + ClassicDupeBot.getJDA().getUserById(link.id).getName()));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
