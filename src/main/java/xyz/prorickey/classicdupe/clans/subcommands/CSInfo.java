package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
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
public class CSInfo extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        Clan clan;
        if(args.length == 0) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(Utils.format("<red>You cannot execute this command without an argument from console"));
                return;
            }
            ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
            if(cmem.getClanID() == null) {
                player.sendMessage(Utils.cmdMsg("<red>You must be in a clan to view your own clans info"));
                return;
            }
            clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        } else clan = ClassicDupe.getClanDatabase().getClan(args[0]);
        if(clan == null) {
            sender.sendMessage(Utils.cmdMsg("<red>That clan does not exist"));
            return;
        }
        Component comp = Utils.format("<yellow>\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D <green><b>" + clan.getClanName() + "</b> <yellow>\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n");
        comp = comp.append(Utils.format("<gold>Clan Owner: <yellow>" + clan.getOwner().getName() + "\n"));

        List<OfflinePlayer> clanAdmins = clan.getAdmins();
        if(clanAdmins != null) {
            StringBuilder admins = new StringBuilder();
            clanAdmins .forEach(offlinePlayer -> admins.append("<yellow>").append(offlinePlayer.getName()).append("<gray>, "));
            if(!admins.isEmpty()) comp = comp.append(Utils.format("<red>Clan Admins: <yellow>" + admins.substring(0, admins.length()-2) + "\n"));
        }

        List<OfflinePlayer> clanVips = clan.getVips();
        if(clanVips != null) {
            StringBuilder vips = new StringBuilder();
            clanVips.forEach(offlinePlayer -> vips.append("<yellow>").append(offlinePlayer.getName()).append("<gray>, "));
            if(!vips.isEmpty()) comp = comp.append(Utils.format("<green>Clan VIPs: <yellow>" + vips.substring(0, vips.length()-2) + "\n"));
        }

        List<OfflinePlayer> clanDefaults = clan.getDefaults();
        if(clanDefaults != null) {
            StringBuilder defaults = new StringBuilder();
            clanDefaults.forEach(offlinePlayer -> defaults.append("<yellow>").append(offlinePlayer.getName()).append("<gray>, "));
            if(!defaults.isEmpty()) comp = comp.append(Utils.format("<gray>Clan Defaults: <yellow>" + defaults.substring(0, defaults.length()-2) + "\n"));
        }

        sender.sendMessage(comp);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getClanDatabase().getLoadedClanNames());
        return new ArrayList<>();
    }
}
