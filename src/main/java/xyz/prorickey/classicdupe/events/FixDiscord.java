package xyz.prorickey.classicdupe.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.RegisteredListener;
import xyz.prorickey.classicdupe.ClassicDupe;

public class FixDiscord implements Listener {

    @EventHandler
    public void onCommandPreproccess(PlayerCommandPreprocessEvent event) {
        if(event.getMessage().startsWith("/discord")) {
            for (RegisteredListener registeredListener : event.getHandlers().getRegisteredListeners()) {
                if(registeredListener.getPlugin() != ClassicDupe.getPlugin()) event.getHandlers().unregister(registeredListener);
            }
        }
    }

}
