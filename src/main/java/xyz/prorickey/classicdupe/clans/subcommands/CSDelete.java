package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CSDelete extends ClanSub {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You are not in a clan. You can't delete a clan that doesn't exist"));
            return;
        }
        if(cmem.getLevel() != 3) {
            player.sendMessage(Utils.cmdMsg("<red>You must be the owner to delete a clan"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<yellow>You are about to delete your clan. Are you sure you want to do this?"));
            player.sendMessage(Utils.format("  <yellow>- Confirm: ")
                    .append(Utils.format("<red><b>/clan delete confirm"))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan delete confirm")));
        } else if(args[0].equalsIgnoreCase("confirm")) {
            Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
            ClassicDupe.getClanDatabase().deleteClan(clan);
            player.sendMessage(Utils.cmdMsg("<yellow>Your clan has been deleted"));
        } else {
            player.sendMessage(Utils.cmdMsg("<yellow>You are about to delete your clan. Are you sure you want to do this?"));
            player.sendMessage(Utils.format("  <yellow>- Confirm: ")
                    .append(Utils.format("<red><b>/clan delete confirm"))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan delete confirm")));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("confirm"));
        return new ArrayList<>();
    }
}
