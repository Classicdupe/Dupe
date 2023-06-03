package xyz.prorickey.classicdupe.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.FilterDatabase;

public class PlayerChat implements Listener {

    @EventHandler
    public void ChatEvent(PlayerChatEvent e)
    {
        for (String s:
                FilterDatabase.blockedText) {
            if(e.getMessage().contains(s)){
                e.setCancelled(true);
                e.getPlayer().sendMessage(Utils.format("<red>This message was blocked."));
            }
        }
    }
}
