package xyz.prorickey.classicdupe.commands.default1;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.Combat;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

public class TpaCMD implements CommandExecutor, TabCompleter {

    // Sender to Recipient
    public static final Map<Player, Player> tpaRequests = new HashMap<>();
    // Sender and time
    public static final Map<Player, Long> tpaRequestTimes = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            p.sendMessage(Utils.cmdMsg("<red>You must include who you would like to teleport to"));
            return true;
        }
        if(Combat.inCombat.containsKey(p)) {
            p.sendMessage(Utils.cmdMsg("<red>You cannot execute this command in combat"));
            return true;
        }
        Player recipient = Bukkit.getServer().getPlayer(args[0]);
        if(recipient == null || !recipient.isOnline()) {
            sender.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <red>is not currently online"));
            return true;
        }
        recipient.sendMessage(
                Utils.cmdMsg("<yellow>" + p.getName() + "<green> is asking to teleport to you ")
                        .append(Utils.format("<dark_gray>[<green><b>ACCEPT<dark_gray>]")
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + p.getName()))
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Utils.format("<green>Click to accept teleport request"))))
                        .append(Component.text(" "))
                        .append(Utils.format("<dark_gray>[<red><b>DECLINE<dark_gray>]"))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadecline " + p.getName()))
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Utils.format("<green>Click to decline teleport request"))));
        p.sendMessage(Utils.cmdMsg("<green>TPA request has been send to <yellow>" + recipient.getName()));
        tpaRequests.put(p, recipient);
        tpaRequestTimes.put(p, System.currentTimeMillis());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }

    private static HashMap<Player, Location> lastLocation = new HashMap<>();

    public static void saveLocation(Player p) {
        lastLocation.put(p, p.getLocation());
    }

    public static void removeLocation(Player p) {
        lastLocation.remove(p);
    }

    public static Location getLocation(Player p) {
        return lastLocation.get(p);
    }

    public static class TPATask extends BukkitRunnable {
        @Override
        public void run() {
            Map<Player, Long> tempTpaRequestTimes = new HashMap<>(TpaCMD.tpaRequestTimes);
            tempTpaRequestTimes.forEach((player, time) -> {
                if((time + (1000*60)) < System.currentTimeMillis() && TpaCMD.tpaRequests.containsKey(player)) {
                    player.sendMessage(Utils.cmdMsg("<red>TPA request to <yellow>" + TpaCMD.tpaRequests.get(player).getName() + "<red> has timed out"));
                    TpaCMD.tpaRequests.remove(player);
                    TpaCMD.tpaRequestTimes.remove(player);
                }
            });
        }
    }
}
