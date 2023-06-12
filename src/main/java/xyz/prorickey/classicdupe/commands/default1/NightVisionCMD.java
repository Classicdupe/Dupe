package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.events.JoinEvent;

import java.util.ArrayList;
import java.util.List;

public class NightVisionCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }

        PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());

        if(JoinEvent.nightVision.contains(player)) {
            JoinEvent.nightVision.remove(player);
            playerData.setNightVision(false);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(Utils.cmdMsg("<green>Disabled your night vision effect. Use /night to turn it back on."));
        } else {
            JoinEvent.nightVision.add(player);
            playerData.setNightVision(true);
            player.sendMessage(Utils.cmdMsg("<green>Enabled your night vision effect. Use /night to turn it back off."));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
