package xyz.prorickey.classicdupe.clans.builders;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class ClanSub {

    public ClanSub() {
    }

    public abstract void execute(CommandSender sender, String[] args);
    public abstract List<String> tabComplete(CommandSender sender, String[] args);

}
