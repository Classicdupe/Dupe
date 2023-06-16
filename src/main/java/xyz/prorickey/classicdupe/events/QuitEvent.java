package xyz.prorickey.classicdupe.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.default1.PrivateMessageCMD;
import xyz.prorickey.classicdupe.commands.moderator.CspyCMD;
import xyz.prorickey.classicdupe.commands.moderator.StaffChatCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;

import java.util.HashMap;

public class QuitEvent implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(JoinEvent.randomTaskMap.get(e.getPlayer()) != null) JoinEvent.randomTaskMap.get(e.getPlayer()).cancel();
        ChatColorCMD.colorProfiles.remove(e.getPlayer().getUniqueId().toString());
        ChatGradientCMD.gradientProfiles.remove(e.getPlayer().getUniqueId().toString());
        StaffChatCMD.staffChatPlayers.remove(e.getPlayer());
        PrivateMessageCMD.lastInConvo.remove(e.getPlayer());
        ClassicDupe.getClanDatabase().removeFromClanChat(e.getPlayer());
        ClassicDupe.getDatabase().getHomesDatabase().unloadPlayer(e.getPlayer());
        ClassicDupe.getDatabase().getPlayerDatabase().playerDataUnload(e.getPlayer().getUniqueId());
        CspyCMD.cspyList.remove(e.getPlayer());
        if(PrivateMessageCMD.lastInConvo.containsValue(e.getPlayer())) new HashMap<>(PrivateMessageCMD.lastInConvo).forEach((sender, recipient) -> PrivateMessageCMD.lastInConvo.remove(sender));
        e.quitMessage(Utils.format("<dark_gray>[<red>-<dark_gray>] ")
                .append(Utils.format(Utils.getPrefix(e.getPlayer()) + e.getPlayer().getName())));
        if(Combat.inCombat.containsKey(e.getPlayer())) {
            e.quitMessage(Utils.format("<dark_gray>[<red>-<dark_gray>] ")
                    .append(Utils.format(Utils.getPrefix(e.getPlayer()) + e.getPlayer().getName()))
                    .append(Utils.format(" <dark_gray>| <red><bold>COMBAT LOG")));
            ClassicDupe.getDatabase().getPlayerDatabase().addDeath(e.getPlayer().getUniqueId().toString());
        }
    }

}
