package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanSub;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.proutils.ChatFormat;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class CSInfo extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        ClansDatabase.Clan clan;
        if(args.length == 0) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage(ChatFormat.format("&cYou cannot execute this command without an argument from console"));
                return;
            }
            ClansDatabase.ClanMember cmem = ClansDatabase.getClanMember(player.getUniqueId());
            if(cmem.getClanId() == null) {
                player.sendMessage(Utils.cmdMsg("&cYou must be in a clan to view your own clans info"));
                return;
            }
            clan = ClansDatabase.getClanByID(cmem.getClanId());
        } else clan = ClansDatabase.getClanByName(args[0]);
        if(clan == null) {
            sender.sendMessage(Utils.cmdMsg("&cThat clan does not exist"));
            return;
        }
        Component comp = Component.text(ChatFormat.format("&e\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D &a&l" + clan.getClanName() + " &e\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\n"));
        comp = comp.append(Component.text(ChatFormat.format("&6Clan Owner: &e" + clan.getClanOwner().getName() + "\n")));

        List<OfflinePlayer> clanAdmins = clan.getClanAdmins();
        if(clanAdmins != null) {
            StringBuilder admins = new StringBuilder();
            clanAdmins .forEach(offlinePlayer -> admins.append("&e").append(offlinePlayer.getName()).append("&7, "));
            if(!admins.isEmpty()) comp = comp.append(Component.text(ChatFormat.format("&cClan Admins: &e" + admins.substring(0, admins.length()-2) + "\n")));
        }

        List<OfflinePlayer> clanVips = clan.getClanVips();
        if(clanVips != null) {
            StringBuilder vips = new StringBuilder();
            clanVips.forEach(offlinePlayer -> vips.append("&e").append(offlinePlayer.getName()).append("&7, "));
            if(!vips.isEmpty()) comp = comp.append(Component.text(ChatFormat.format("&aClan VIPs: &e" + vips.substring(0, vips.length()-2) + "\n")));
        }

        List<OfflinePlayer> clanDefaults = clan.getClanDefaults();
        if(clanDefaults != null) {
            StringBuilder defaults = new StringBuilder();
            clanDefaults.forEach(offlinePlayer -> defaults.append("&e").append(offlinePlayer.getName()).append("&7, "));
            if(!defaults.isEmpty()) comp = comp.append(Component.text(ChatFormat.format("&7Clan Defaults: &e" + defaults.substring(0, defaults.length()-2) + "\n")));
        }

        sender.sendMessage(comp);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> clanNames = ClansDatabase.getLoadedClanNames();
            return TabComplete.tabCompletionsSearch(args[0], clanNames);
        }
        return new ArrayList<>();
    }
}
