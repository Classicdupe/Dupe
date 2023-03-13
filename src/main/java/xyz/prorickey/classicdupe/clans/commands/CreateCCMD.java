package xyz.prorickey.classicdupe.clans.commands;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanCommand;
import xyz.prorickey.classicdupe.clans.Clans;
import xyz.prorickey.classicdupe.clans.ClansData;

import java.util.ArrayList;

public class CreateCCMD implements ClanCommand {

    @Override
    public String getCommandName() { return "create"; }

    @Override
    public @Nullable Boolean getNeedClan() { return false; }

    @Override
    public @Nullable String getPermission() { return null; }

    @Override
    public void execute(Clans clans, CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
            return;
        }
        ClansData.ClanMember cmem = clans.getData().getPlayer(player.getUniqueId().toString());
        if(cmem.getClanID() != null) {
            player.sendMessage(Component.text(Utils.cmdMsg("&cYou cannot create a clan while you are in a clan. You must leave your clan using "))
                    .append(Component.text(Utils.format("&e/clan leave"))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/clan leave"))));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("&cYou must include what you would like to call your clan"));
            return;
        }
        if(clans.getData().getClanByName(args[0]) != null) {
            player.sendMessage(Utils.cmdMsg("&cThat clan name is already taken"));
            return;
        }
        boolean success = clans.getData().createClan(args[0], player);
        if(success) {
            ClassicDupe.getPlugin().getServer().getOnlinePlayers().forEach(p -> {
                p.sendMessage(Utils.format("&e" + player.getName() + "&a just created the clan &6" + args[0]));
                p.playSound(player, "block.note_block.pling", SoundCategory.MASTER, 1F, 0.5F);
            });
        } else {
            player.sendMessage(Utils.cmdMsg("&cThere was an error creating your clan, please report this"));
        }
    }

    @Override
    public ArrayList<String> tabComplete(Clans clans, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

}
