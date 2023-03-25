package xyz.prorickey.classicdupe.clans;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.clans.events.PlayerJoin;
import xyz.prorickey.classicdupe.clans.subcommands.CSCreate;
import xyz.prorickey.classicdupe.clans.subcommands.CSHelp;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

public class MainCommand implements CommandExecutor, TabCompleter {

    public static Map<String, ClanSub> clanSubs = new HashMap<>();

    public MainCommand(JavaPlugin p) {
        ClansDatabase.init(p);
        p.getCommand("clan").setExecutor(this);
        p.getCommand("clan").setTabCompleter(this);
        p.getServer().getPluginManager().registerEvents(new PlayerJoin(), p);

        clanSubs.put("help", new CSHelp());
        clanSubs.put("create", new CSCreate());
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
