package xyz.prorickey.classicdupe.clans.subcommands;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CSCreate extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() != null) {
            player.sendMessage(Utils.cmdMsg("<red>You are already in a clan! You must leave it before creating another one"));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<red>You must provide a clan name"));
            return;
        }
        if(PlainTextComponentSerializer.plainText().serialize(Utils.format(Utils.convertColorCodesToAdventure(args[0]))).length() != args[0].length()) {
            player.sendMessage(Utils.cmdMsg("<red>Your clan cannot include color codes in it's name"));
            return;
        }
        if(args[0].length() > ClassicDupe.getClanDatabase().getClanConfig().getInt("clans.maxChar")) {
            player.sendMessage(Utils.cmdMsg("<red>Your clan name can only be " + ClassicDupe.getClanDatabase().getClanConfig().getInt("clans.maxChar") + " characters long"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(args[0]);
        if(clan != null) {
            player.sendMessage(Utils.cmdMsg("<red>That clan already exists! Please pick a unique name"));
            return;
        }
        ClassicDupe.getClanDatabase().createClan(args[0], player);
        player.sendMessage(Utils.cmdMsg("<yellow>Created the clan <gold>" + args[0]));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
