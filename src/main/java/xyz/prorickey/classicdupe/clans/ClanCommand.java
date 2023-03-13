package xyz.prorickey.classicdupe.clans;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public interface ClanCommand {

    String getCommandName();
    @Nullable
    Boolean getNeedClan();
    @Nullable
    String getPermission();
    void execute(Clans clans, CommandSender sender, String[] args);
    ArrayList<String> tabComplete(Clans clans, CommandSender sender, String[] args);

}
