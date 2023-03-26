package xyz.prorickey.classicdupe.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClansDatabase;
import xyz.prorickey.classicdupe.commands.moderator.StaffChatCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.proutils.ChatFormat;

import java.util.HashMap;
import java.util.Map;

public class Chat implements Listener {

    public static Boolean mutedChat = false;

    public static Map<Player, Long> chatCooldown = new HashMap<>();

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if(!ClassicDupe.getDatabase().getFilterDatabase().checkMessage(PlainTextComponentSerializer.plainText().serialize(e.message()).toLowerCase())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("&cYour message has been blocked by the filter"));
            return;
        }
        if(mutedChat && !e.getPlayer().hasPermission("mod.mutechat.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("&cThe chat is currently muted"));
            return;
        }
        if(chatCooldown.containsKey(e.getPlayer()) && chatCooldown.get(e.getPlayer()) > System.currentTimeMillis()) {
            e.setCancelled(true);
            Long timeLeft = chatCooldown.get(e.getPlayer())-System.currentTimeMillis();
            e.getPlayer().sendMessage(Utils.cmdMsg("&cYou are currently on chat cooldown for " + Math.round(timeLeft/1000) + " second(s)"));
            return;
        }

        String clanName = ClansDatabase.getClanMember(e.getPlayer().getUniqueId()).getClanName();
        String clanColor = "&e";
        if(clanName != null) clanColor = ClansDatabase.getClanByID(ClansDatabase.getClanMember(e.getPlayer().getUniqueId()).getClanId()).getClanSettings().getClanColor();

        String pgroup = ClassicDupe.getLPAPI().getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup();
        if(pgroup.equalsIgnoreCase("default")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+4000);
        else if(pgroup.equalsIgnoreCase("vip")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+3000);
        else if(pgroup.equalsIgnoreCase("mvp")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+2000);
        else if(pgroup.equalsIgnoreCase("legend")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+1000);

        if(StaffChatCMD.staffChatPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
            StaffChatCMD.sendToStaffChat(
                "&8[&cSC&8] " +
                ((Utils.getPrefix(e.getPlayer()) != null) ? Utils.getPrefix(e.getPlayer()) : "") +
                e.getPlayer().getName() +
                ChatFormat.format(" &7\u00BB &a") +
                PlainTextComponentSerializer.plainText().serialize(e.message())
            );
            return;
        }

        ChatType chatType = ChatType.DEFAULT;
        if(ChatColorCMD.colorProfiles.containsKey(e.getPlayer().getUniqueId().toString())) chatType = ChatType.COLOR;
        if(ChatGradientCMD.gradientProfiles.containsKey(e.getPlayer().getUniqueId().toString())) chatType = ChatType.GRADIENT;

        String name = e.getPlayer().getName();
        PlayerDatabase.PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(e.getPlayer().getUniqueId().toString());
        if(data.nickname != null) name = data.nickname;

        if(chatType.equals(ChatType.DEFAULT)) {
            String finalName = name;
            String finalClanColor = clanColor;
            e.renderer((player, sourceDisplayName, message, viewer) -> Component.text(
        ChatFormat.format((clanName != null ? "&8[" + finalClanColor + clanName + "&8] " : "")) +
                ChatFormat.format((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") +
                ChatFormat.format(finalName) +
                ChatFormat.format((Utils.getSuffix(player) != null) ? " " + Utils.getSuffix(player)  : "") +
                ChatFormat.format(" &7\u00BB &7")

            ).append(message));
        } else if(chatType.equals(ChatType.COLOR)) {
            String finalName1 = name;
            String finalClanColor1 = clanColor;
            e.renderer((player, sourceDisplayName, message, viewer) -> Component.text(
        ChatFormat.format((clanName != null ? "&8[" + finalClanColor1 + clanName + "&8] " : "")) +
                ChatFormat.format((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") +
                ChatFormat.format(finalName1) +
                ChatFormat.format((Utils.getSuffix(player) != null) ? " " + Utils.getSuffix(player)  : "") +
                ChatFormat.format(" &7\u00BB " + ChatColorCMD.colorProfiles.get(e.getPlayer().getUniqueId().toString())) +
                PlainTextComponentSerializer.plainText().serialize(message)
            ));
        } else {
            MiniMessage mm = MiniMessage.miniMessage();
            String finalName2 = name;
            String finalClanColor2 = clanColor;
            e.renderer((player, sourceDisplayName, message, viewer) -> Component.text(
        ChatFormat.format((clanName != null ? "&8[" + finalClanColor2 + clanName + "&8] " : "")) +
                ChatFormat.format((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") +
                ChatFormat.format(finalName2) +
                ChatFormat.format((Utils.getSuffix(player) != null) ? " " + Utils.getSuffix(player)  : "") +
                ChatFormat.format(" &7\u00BB ")
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
