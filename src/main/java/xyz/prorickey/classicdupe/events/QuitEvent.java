package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

public class QuitEvent implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(JoinEvent.randomTaskMap.get(e.getPlayer()) != null) JoinEvent.randomTaskMap.get(e.getPlayer()).cancel();
        e.quitMessage(Component.text(
                Utils.format("&8[&c-&8] " +
                        ClassicDupe.getLPAPI().getUserManager().getUser(e.getPlayer().getUniqueId()).getCachedData().getMetaData().getPrefix() +
                        e.getPlayer().getName())
        ));

    }

}
