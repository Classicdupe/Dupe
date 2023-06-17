package xyz.prorickey.classicdupe.clans;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.clans.adminsubcommands.CSForceDelete;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.classicdupe.clans.subcommands.*;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

public class Clans implements CommandExecutor, TabCompleter {

    public static final Map<String, ClanSub> clanSubs = new HashMap<>();
    public static final Map<String, ClanSub> adminClanSubs = new HashMap<>();

    @SuppressWarnings("unchecked")
    public Clans(JavaPlugin p) {
        p.getCommand("clan").setExecutor(this);
        p.getCommand("clan").setTabCompleter(this);
        p.getCommand("clanadmin").setExecutor(this);
        p.getCommand("clanadmin").setTabCompleter(this);

        clanSubs.put("accept", new CSAccept());
        clanSubs.put("create", new CSCreate());
        clanSubs.put("decline", new CSDecline());
        clanSubs.put("delete", new CSDelete());
        clanSubs.put("help", new CSHelp());
        clanSubs.put("info", new CSInfo());
        clanSubs.put("invite", new CSInvite());
        clanSubs.put("kick", new CSKick());
        clanSubs.put("leave", new CSLeave());
        clanSubs.put("promote", new CSPromote());
        clanSubs.put("chat", new CSChat());
        clanSubs.put("settings", new CSSettings());
        clanSubs.put("demote", new CSDemote());
        clanSubs.put("delhome", new CSDelHome());
        clanSubs.put("home", new CSHome());
        clanSubs.put("sethome", new CSSethome());

        adminClanSubs.put("forcedelete", new CSForceDelete());

        new CSInvite.InviteTask().runTaskTimer(ClassicDupe.getPlugin(), 0, 20);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equals("clanadmin")) {
            if(args.length == 0) adminClanSubs.get("help").execute(sender, args);
            else {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                if(!adminClanSubs.containsKey(args[0].toLowerCase())) adminClanSubs.get("help").execute(sender, subArgs);
                else adminClanSubs.get(args[0].toLowerCase()).execute(sender, subArgs);
            }
        } else {
            if(args.length == 0) clanSubs.get("help").execute(sender, args);
            else {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                if(!clanSubs.containsKey(args[0].toLowerCase())) clanSubs.get("help").execute(sender, subArgs);
                else clanSubs.get(args[0].toLowerCase()).execute(sender, subArgs);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equals("clanadmin")) {
            if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], adminClanSubs.keySet().stream().toList());
            else if(args.length > 1) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                ClanSub sub = adminClanSubs.get(args[0].toLowerCase());
                if(sub != null) return sub.tabComplete(sender, subArgs);
            }
        } else {
            if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], clanSubs.keySet().stream().toList());
            else if(args.length > 1) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                ClanSub sub = clanSubs.get(args[0].toLowerCase());
                if(sub != null) return sub.tabComplete(sender, subArgs);
            }
        }
        return new ArrayList<>();
    }

}
