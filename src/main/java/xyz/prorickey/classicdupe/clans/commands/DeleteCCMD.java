package xyz.prorickey.classicdupe.clans.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.clans.ClanCommand;
import xyz.prorickey.classicdupe.clans.Clans;

import java.util.ArrayList;

public class DeleteCCMD implements ClanCommand {
    @Override
    public String getCommandName() { return "delete"; }

    @Override
    public @Nullable Boolean getNeedClan() { return true; }

    @Override
    public @Nullable String getPermission() { return null; }

    @Override
    public void execute(Clans clans, CommandSender sender, String[] args) {

    }

    @Override
    public ArrayList<String> tabComplete(Clans clans, CommandSender sender, String[] args) {
        return null;
    }
}
