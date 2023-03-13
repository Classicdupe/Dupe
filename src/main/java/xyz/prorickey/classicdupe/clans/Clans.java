package xyz.prorickey.classicdupe.clans;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.commands.CreateCCMD;

import java.util.*;

public class Clans implements CommandExecutor, TabCompleter {

    private ClansData data;
    private ClassicDupe plugin;
    private Map<String, ClanCommand> commands = new HashMap<>();

    public Clans(ClassicDupe plugin1) {

        plugin = plugin1;
        data = new ClansData();

        register(new CreateCCMD());

    }

    public ClansData getData() { return data; }
    private void register(ClanCommand command) { commands.put(command.getCommandName(), command); }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> args2 = new ArrayList<>(Arrays.stream(args).toList());
        if(args2.size() > 0) args2.remove(0);
        if(args.length == 0 || !commands.containsKey(args[0].toLowerCase())) {
            sender.sendMessage(Utils.cmdMsg("&eClans"));
            return true;
        }
        ClanCommand cmd = commands.get(args[0].toLowerCase());
        cmd.execute(this, sender, args2.toArray(String[]::new));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return new ArrayList<>();
        if(args.length == 1) {
            List<String> ret = new ArrayList<>();
            commands.forEach((name, cmd) -> {
                if(cmd.getPermission() == null || sender.hasPermission(cmd.getPermission())) {
                    if(cmd.getNeedClan() == null) ret.add(name);
                    else {
                        ClansData.ClanMember cmem = data.getPlayer(player.getUniqueId().toString());
                        if(Boolean.TRUE.equals(cmd.getNeedClan()) && cmem.getClanID() != null) ret.add(name);
                        else if(Boolean.FALSE.equals(cmd.getNeedClan()) && cmem.getClanID() == null) ret.add(name);
                    }
                }
            });
            return ret;
        }
        if(!commands.containsKey(args[0].toLowerCase())) return new ArrayList<>();
        ClanCommand cmd = commands.get(args[0].toLowerCase());
        List<String> args2 = new ArrayList<>(Arrays.stream(args).toList());
        if(args2.size() > 0) args2.remove(0);
        return cmd.tabComplete(this, sender, args2.toArray(String[]::new));
    }
}
