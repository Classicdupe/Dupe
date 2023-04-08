package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.Combat;

import java.util.ArrayList;
import java.util.List;

public class DupeCMD implements CommandExecutor, TabCompleter {

    public static List<Material> forbiddenDupes = new ArrayList<>();
    public static List<Material> forbiddenDupesInCombat = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int dupeNum = 1;
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
            return true;
        }
        if(forbiddenDupes.contains(p.getInventory().getItemInMainHand().getType())) {
            p.sendMessage(Utils.cmdMsg("&cYou cannot dupe that item"));
            return true;
        }
        if(Combat.inCombat.containsKey(p.getPlayer()) && forbiddenDupesInCombat.contains(p.getInventory().getItemInMainHand().getType())) {
            p.sendMessage(Utils.cmdMsg("&cYou cannot dupe that item while in combat"));
            return true;
        }
        if(args.length > 0) {
            try {
                dupeNum = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
            if(dupeNum < 1) dupeNum = 1;
            if(dupeNum > 6) dupeNum = 6;
        }
        for(int i = 0; i < dupeNum; i++) p.getInventory().addItem(p.getInventory().getItemInMainHand());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return List.of("1", "2", "3", "4", "5", "6");
        }
        return new ArrayList<>();
    }
}
