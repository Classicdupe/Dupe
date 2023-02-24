package xyz.prorickey.classicdupe.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;

public class Chat implements Listener {

    public static Boolean mutedChat = false;

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if(!ClassicDupe.getDatabase().getFilterDatabase().checkMessage(PlainTextComponentSerializer.plainText().serialize(e.message()).toLowerCase())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.format("&cYour message has been blocked by the filter."));
            return;
        }
        if(mutedChat && !e.getPlayer().hasPermission("mod.mutechat.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.format("&cThe chat is currently muted"));
            return;
        }
        ChatType chatType = ChatType.DEFAULT;
        if(ChatColorCMD.colorProfiles.containsKey(e.getPlayer().getUniqueId().toString())) chatType = ChatType.COLOR;
        if(ChatGradientCMD.gradientProfiles.containsKey(e.getPlayer().getUniqueId().toString())) chatType = ChatType.GRADIENT;

        if(chatType.equals(ChatType.DEFAULT)) {
            e.renderer((player, sourceDisplayName, message, viewer) -> Component.text(
                    Utils.format((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") +
                            player.getName() +
                            Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.getSuffix(player)  : "") +
                            Utils.format(" &7\u00BB &7")
            ).append(message));
        } else if(chatType.equals(ChatType.COLOR)) {
            e.renderer((player, sourceDisplayName, message, viewer) -> Component.text(
                    Utils.format((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") +
                            player.getName() +
                            Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.getSuffix(player)  : "") +
                            Utils.format(" &7\u00BB " + ChatColorCMD.colorProfiles.get(e.getPlayer().getUniqueId().toString())) +
                            PlainTextComponentSerializer.plainText().serialize(message)
            ));
        } else if(chatType.equals(ChatType.GRADIENT)) {
            MiniMessage mm = MiniMessage.miniMessage();
            e.renderer((player, sourceDisplayName, message, viewer) -> Component.text(
                    Utils.format((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") +
                            player.getName() +
                            Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.getSuffix(player)  : "") +
                            Utils.format(" &7\u00BB ")
            ).append(mm.deserialize( "<gradient:" +
                    ChatGradientCMD.gradientProfiles.get(e.getPlayer().getUniqueId().toString()).gradientFrom + ":" +
                    ChatGradientCMD.gradientProfiles.get(e.getPlayer().getUniqueId().toString()).gradientTo + ">" +
                    PlainTextComponentSerializer.plainText().serialize(message) + "</gradient>"
            )));
        }
    }

    private enum ChatType {
        GRADIENT,
        COLOR,
        DEFAULT
    }

}
