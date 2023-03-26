package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSDelete extends ClanSub {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(ChatFormat.format("&cYou cannot execute this command from console"));
            return;
        }
        ClansDatabase.ClanMember cmem = ClansDatabase.getClanMember(player.getUniqueId());
        if(cmem.getClanId() == null) {
            player.sendMessage(Utils.cmdMsg("&cYou are not in a clan. You can't delete a clan that doesn't exist"));
            return;
        }
        if(cmem.getLevel() != 4) {
            player.sendMessage(Utils.cmdMsg("&cYou must be the owner to delete a clan"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&eYou are about to delete your clan. Are you sure you want to do this?"));
            player.sendMessage(Component.text(ChatFormat.format("  &e- Confirm: "))
                    .append(Component.text(ChatFormat.format("&c&l/clan delete confirm"))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan delete confirm"))));
        } else if(args[0].equalsIgnoreCase("confirm")) {
            ClansDatabase.Clan clan = ClansDatabase.getClanByID(cmem.getClanId());
            clan.deleteClan();
            player.sendMessage(Utils.cmdMsg("&eYour clan has been deleted"));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("confirm"));
        return new ArrayList<>();
    }
}
