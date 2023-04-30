package xyz.prorickey.classicdupe.commands.default1;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.apache.commons.lang3.RandomStringUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkCMD implements CommandExecutor, TabCompleter {

    public static final Map<String, LinkCode> linkCodes = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        for(int i = 0; i < linkCodes.size(); i++) {
            String code = linkCodes.keySet().stream().toList().get(i);
            LinkCode linkCode = linkCodes.get(code);
            if(linkCode.player.equals(player)) linkCodes.remove(code);
        }
        if(ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString()) != null) {
            player.sendMessage(Utils.cmdMsg("<red>You must unlink your account with /unlink before you can link again"));
            return true;
        }
        String code = genCode();
        linkCodes.put(code, new LinkCode(code, player, System.currentTimeMillis()));
        player.sendMessage(Utils.cmdMsg("<green>Please go onto the ClassicDupe discord and execute the command ")
                .append(Utils.format("<yellow>/link " + code))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "/link " + code))
                        .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Utils.format("<gray>Copy this command into discord"))));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    public static class LinkCodeTask extends BukkitRunnable {
        @Override
        public void run() {
            for(int i = 0; i < linkCodes.size(); i++) {
                String code = linkCodes.keySet().stream().toList().get(i);
                LinkCode linkCode = linkCodes.get(code);
                if(linkCode.time + (1000*60*5) < System.currentTimeMillis()) linkCodes.remove(code);
            }
        }
    }

    public static String genCode() {
        String code = RandomStringUtils.randomAlphabetic(5);
        if(!linkCodes.containsKey(code)) return code;
        else return genCode();
    }

    public static class LinkCode {
        public final String code;
        public final Player player;
        public final Long time;
        public LinkCode(String code1, Player player1, Long time1) {
            code = code1;
            player = player1;
            time = time1;
        }
    }

}
