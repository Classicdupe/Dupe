package xyz.prorickey.classicdupe.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

public class Chat implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if(!ClassicDupe.getDatabase().getFilterDatabase().checkMessage(PlainTextComponentSerializer.plainText().serialize(e.message()).toLowerCase())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.format("&cYour message has been blocked by the filter."));
            return;
        }
        e.renderer((player, sourceDisplayName, message, viewer) -> Component.text(
                Utils.format((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") +
                        player.getName() +
                        Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.getSuffix(player)  : "") +
                        Utils.format(" &7\u00BB &f")
        ).append(message));
    }

}
