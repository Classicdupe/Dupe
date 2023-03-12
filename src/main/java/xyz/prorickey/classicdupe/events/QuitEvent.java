package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.default1.PrivateMessageCMD;
import xyz.prorickey.classicdupe.commands.moderator.StaffChatCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;

public class QuitEvent implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(JoinEvent.randomTaskMap.get(e.getPlayer()) != null) JoinEvent.randomTaskMap.get(e.getPlayer()).cancel();
        ChatColorCMD.colorProfiles.remove(e.getPlayer().getUniqueId().toString());
        ChatGradientCMD.gradientProfiles.remove(e.getPlayer().getUniqueId().toString());
        StaffChatCMD.staffChatPlayers.remove(e.getPlayer());
        PrivateMessageCMD.lastInConvo.remove(e.getPlayer());
        if(PrivateMessageCMD.lastInConvo.containsValue(e.getPlayer())) PrivateMessageCMD.lastInConvo.forEach((sender, recipient) -> PrivateMessageCMD.lastInConvo.remove(sender));
        e.quitMessage(Component.text(
                Utils.format("&8[&c-&8] " +
                        ClassicDupe.getLPAPI().getUserManager().getUser(e.getPlayer().getUniqueId()).getCachedData().getMetaData().getPrefix() +
                        e.getPlayer().getName())
        ));
        if(Combat.inCombat.containsKey(e.getPlayer())) {
            e.quitMessage(Component.text(
                    Utils.format("&8[&c-&8] " +
                            ClassicDupe.getLPAPI().getUserManager().getUser(e.getPlayer().getUniqueId()).getCachedData().getMetaData().getPrefix() +
                            e.getPlayer().getName() +
                            " &8| &c&lCOMBAT LOG")));
            ClassicDupe.getDatabase().getPlayerDatabase().addDeath(e.getPlayer().getUniqueId().toString());
        }
    }

}
