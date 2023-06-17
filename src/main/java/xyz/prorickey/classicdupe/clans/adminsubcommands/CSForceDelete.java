package xyz.prorickey.classicdupe.clans.adminsubcommands;

import org.bukkit.command.CommandSender;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class CSForceDelete extends ClanSub {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("admin.clanadmin.forcedelete")) {
            sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to force delete clans"));
            return;
        }
        if(args.length == 0) {
            sender.sendMessage(Utils.cmdMsg("<red>Please specify a clan to force delete"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(args[0]);
        if(clan == null) {
            sender.sendMessage(Utils.cmdMsg("<red>Clan <yellow>" + args[0] + " <red>does not exist"));
            return;
        }
        ClassicDupe.getClanDatabase().deleteClan(clan);
        sender.sendMessage(Utils.cmdMsg("<green>Clan <yellow>" + clan.getClanName() + " <green>has been forcefully deleted"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getClanDatabase().getLoadedClanNames());
        return new ArrayList<>();
    }

}
