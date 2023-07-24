package xyz.prorickey.classicdupe.events;

import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.moderator.StaffChatCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

import java.util.HashMap;
import java.util.Map;

public class Chat implements Listener {

    public static Boolean mutedChat = false;

    public static final Map<Player, Long> chatCooldown = new HashMap<>();

    @EventHandler
    public void onAsyncChatDecorate(AsyncChatDecorateEvent e){
        MiniMessage mm = MiniMessage.miniMessage();
        // serialize it to normal string format (STRING)
        String serialized = mm.serialize(e.originalMessage());
        // Remove stupid tags
        //if (!e.player().hasPermission("classicdupe.admin.tags")) {
        serialized = mm.stripTags(serialized);
        //}
        if (e.player() == null){
            e.result(mm.deserialize(serialized));
            return;
            // getLogger().severe("Player is null in the chat decorate event! Player: " + e.player().getName())
        }
        PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.player().getUniqueId());
        
        // Checking all players in the message
        // Old for loop due to streaming needs final variables.
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(serialized.contains("@" + onlinePlayer.getName())) {
                serialized = serialized.replace("@" + onlinePlayer.getName(), "<yellow>@"+onlinePlayer.getName()+"</yellow>");
                PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(onlinePlayer.getUniqueId());
                if(!playerData.getMutePings())
                   onlinePlayer.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1, 1));
            }
        }
        ChatType chatType = ChatType.DEFAULT;
        if(ChatColorCMD.colorProfiles.containsKey(e.player().getUniqueId().toString())) chatType = ChatType.COLOR;
        if(ChatGradientCMD.gradientProfiles.containsKey(e.player().getUniqueId().toString())) chatType = ChatType.GRADIENT;
        if(chatType.equals(ChatType.DEFAULT)) {
            serialized = "<gray>" + serialized;
        } else if (chatType.equals(ChatType.COLOR)){
            serialized = "<white>" + data.chatcolor + serialized;
        } else {
            serialized ="<gradient:" +
                    ChatGradientCMD.gradientProfiles.get(e.player().getUniqueId().toString()).gradientFrom + ":" +
                    ChatGradientCMD.gradientProfiles.get(e.player().getUniqueId().toString()).gradientTo + ">" +
                    serialized;
        }

        e.result(mm.deserialize(serialized));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(AsyncChatEvent e) {
        if(!ClassicDupe.getDatabase().getFilterDatabase().checkMessage(PlainTextComponentSerializer.plainText().serialize(e.message()).toLowerCase())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>Your message has been blocked by the filter"));
            return;
        }
        if(mutedChat && !e.getPlayer().hasPermission("mod.mutechat.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>The chat is currently muted"));
            return;
        }
        if(chatCooldown.containsKey(e.getPlayer()) && chatCooldown.get(e.getPlayer()) > System.currentTimeMillis()) {
            e.setCancelled(true);
            long timeLeft = chatCooldown.get(e.getPlayer())-System.currentTimeMillis();
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>You are currently on chat cooldown for " + Math.round(timeLeft/1000.0) + " second(s)"));
            return;
        }
        if(StaffChatCMD.staffChatPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
            StaffChatCMD.sendToStaffChat(
                    Utils.format("<dark_gray>[<red>SC<dark_gray>] ")
                            .append(MiniMessage.miniMessage().deserialize(((Utils.getPrefix(e.getPlayer()) != null) ? Utils.getPrefix(e.getPlayer()) : "") + e.getPlayer().getName()))
                            .append(Utils.format(" <gray>\u00BB "))
                            .append(e.message().color(TextColor.color(0x10F60E))));
            ClassicDupeBot.getJDA().getChannelById(TextChannel.class, Config.getConfig().getLong("discord.staffchat"))
                    .sendMessage("**" + e.getPlayer().getName() + "** \u00BB " + PlainTextComponentSerializer.plainText().serialize(e.message())).queue();
            return;
        }
        if(ClassicDupe.getClanDatabase().clanChat(e.getPlayer())) {
            e.setCancelled(true);
            ClassicDupe.getClanDatabase().sendClanChat(PlainTextComponentSerializer.plainText().serialize(e.message()), e.getPlayer());
            return;
        }

        String clanName = ClassicDupe.getClanDatabase().getClanMember(e.getPlayer().getUniqueId()).getClanName();
        String clanColor = "<yellow>";
        if(clanName != null &&
                ClassicDupe.getClanDatabase().getClanMember(e.getPlayer().getUniqueId()).getClanID() != null ) {
            clanColor = ClassicDupe.getClanDatabase().getClan(ClassicDupe.getClanDatabase().getClanMember(e.getPlayer().getUniqueId()).getClanID()).getClanColor();
        }

        String pgroup = ClassicDupe.getLPAPI().getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup();
        if(pgroup.equalsIgnoreCase("default")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+4000);
        else if(pgroup.equalsIgnoreCase("vip")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+3000);
        else if(pgroup.equalsIgnoreCase("mvp")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+2000);
        else if(pgroup.equalsIgnoreCase("legend")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+1000);

        MiniMessage mm = MiniMessage.miniMessage();
        Component name;
        PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId());
        if(data.nickname != null) {
            name = mm.deserialize(Utils.getPrefix(e.getPlayer()) + data.nickname)
                    .hoverEvent(HoverEvent.showText(Utils.format("<yellow>Real Name: " + e.getPlayer().getName())));
        }
        else name = Utils.format(Utils.getPrefix(e.getPlayer()) + e.getPlayer().getName());

        String finalClanColor2 = clanColor;
        Component finalName = name;
        e.renderer((player, sourceDisplayName, message, viewer) ->
                Utils.format((clanName != null ? "<dark_gray>[" + finalClanColor2 + clanName + "<dark_gray>] " : ""))
                        .append(finalName)
                        .append(Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.convertColorCodesToAdventure(Utils.getSuffix(player))  : ""))
                        .append(Utils.format(" <gray>\u00BB <white>"))
                        .append(message)
        );
    }

    private enum ChatType {
        GRADIENT,
        COLOR,
        DEFAULT
    }

}
