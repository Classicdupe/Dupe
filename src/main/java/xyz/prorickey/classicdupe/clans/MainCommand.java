package xyz.prorickey.classicdupe.clans;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.clans.subcommands.*;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

public class MainCommand implements CommandExecutor, TabCompleter {

    public static Map<String, ClanSub> clanSubs = new HashMap<>();

    public MainCommand(JavaPlugin p) {
        ClansDatabase.init(p);
        p.getCommand("clan").setExecutor(this);
        p.getCommand("clan").setTabCompleter(this);

        clanSubs.put("help", new CSHelp());
        clanSubs.put("create", new CSCreate());
        clanSubs.put("delete", new CSDelete());
        clanSubs.put("setting", new CSSettings());
        clanSubs.put("invite", new CSInvite());
        clanSubs.put("accept", new CSAccept());
        clanSubs.put("decline", new CSDecline());
        clanSubs.put("leave", new CSLeave());
        clanSubs.put("promote", new CSPromote());
        clanSubs.put("kick", new CSKick());
        clanSubs.put("home", new CSHome());
        clanSubs.put("sethome", new CSSethome());
        clanSubs.put("delhome", new CSDelHome());
        clanSubs.put("info", new CSInfo());
        clanSubs.put("demote", new CSDemote());

        new CSInvite.InviteTask().runTaskTimer(ClassicDupe.getPlugin(), 0, 20);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) clanSubs.get("help").execute(sender, args);
        else {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            ClanSub sub = clanSubs.get(args[0].toLowerCase());
            if(sub == null) clanSubs.get("help").execute(sender, subArgs);
            sub.execute(sender, subArgs);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], clanSubs.keySet().stream().toList());
        else if(args.length > 1) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            ClanSub sub = clanSubs.get(args[0].toLowerCase());
            if(sub != null) return sub.tabComplete(sender, subArgs);
        }
        return new ArrayList<>();
    }

}
