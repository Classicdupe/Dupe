package xyz.prorickey.classicdupe.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.Database;

public class CommandSendEvent implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e)
    {
        if (Database.blockedToUseCommands.contains(e.getPlayer()) && e.getMessage() != "/blockcommands" && e.getMessage() != "/blockcmd")
        {
            e.getPlayer().sendMessage(Utils.format("<red>You are blocked from using commands."));
            e.setCancelled(true);
        }
    }
}
