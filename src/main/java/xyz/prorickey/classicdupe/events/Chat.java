package xyz.prorickey.classicdupe.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.commands.moderator.StaffChatCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatColorCMD;
import xyz.prorickey.classicdupe.commands.perk.ChatGradientCMD;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat implements Listener {

    public static Boolean mutedChat = false;

    public static final Map<Player, Long> chatCooldown = new HashMap<>();

    @EventHandler
    public void onChat(AsyncChatEvent e) {
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
        if(ClanDatabase.isInClanChat(e.getPlayer())) {
            e.setCancelled(true);
            ClanDatabase.sendToClanChat(PlainTextComponentSerializer.plainText().serialize(e.message()), e.getPlayer());
            return;
        }

        String clanName = ClanDatabase.getClanMember(e.getPlayer().getUniqueId()).getClanName();
        String clanColor = "<yellow>";
        if(clanName != null &&
                ClanDatabase.getClanMember(e.getPlayer().getUniqueId()).getClanID() != null
        ) clanColor = ClanDatabase.getClan(ClanDatabase.getClanMember(e.getPlayer().getUniqueId()).getClanID()).getClanColor();

        String pgroup = ClassicDupe.getLPAPI().getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup();
        if(pgroup.equalsIgnoreCase("default")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+4000);
        else if(pgroup.equalsIgnoreCase("vip")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+3000);
        else if(pgroup.equalsIgnoreCase("mvp")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+2000);
        else if(pgroup.equalsIgnoreCase("legend")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+1000);

        ChatType chatType = ChatType.DEFAULT;
        if(ChatColorCMD.colorProfiles.containsKey(e.getPlayer().getUniqueId().toString())) chatType = ChatType.COLOR;
        if(ChatGradientCMD.gradientProfiles.containsKey(e.getPlayer().getUniqueId().toString())) chatType = ChatType.GRADIENT;

        String name = e.getPlayer().getName();
        PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId());
        if(data.nickname != null) name = data.nickname;

        MiniMessage mm = MiniMessage.miniMessage();
        if(chatType.equals(ChatType.DEFAULT)) {
            String finalName = name;
            String finalClanColor = clanColor;
            e.renderer((player, sourceDisplayName, message, viewer) ->
                    Utils.format((clanName != null ? "<dark_gray>[" + finalClanColor + clanName + "<dark_gray>] " : ""))
                            .append(mm.deserialize(((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") + finalName))
                            .append(Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.convertColorCodesToAdventure(Utils.getSuffix(player))  : ""))
                            .append(Utils.format(" <gray>\u00BB <gray>"))
                            .append(Component.text(mm.stripTags(PlainTextComponentSerializer.plainText().serialize(message)))));
        } else if(chatType.equals(ChatType.COLOR)) {
            String finalName1 = name;
            String finalClanColor1 = clanColor;
            e.renderer((player, sourceDisplayName, message, viewer) ->
                    Utils.format((clanName != null ? "<dark_gray>[" + finalClanColor1 + clanName + "<dark_gray>] " : ""))
                            .append(mm.deserialize(((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") + finalName1))
                            .append(Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.convertColorCodesToAdventure(Utils.getSuffix(player))  : ""))
                            .append(Utils.format(" <gray>\u00BB <white>"))
                            .append(Utils.format(data.chatcolor +
                                mm.stripTags(PlainTextComponentSerializer.plainText().serialize(message))
                            )));
        } else {
            String finalName2 = name;
            String finalClanColor2 = clanColor;
            e.renderer((player, sourceDisplayName, message, viewer) ->
                    Utils.format((clanName != null ? "<dark_gray>[" + finalClanColor2 + clanName + "<dark_gray>] " : ""))
                            .append(mm.deserialize(((Utils.getPrefix(player) != null) ? Utils.getPrefix(player) : "") + finalName2))
                            .append(Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.convertColorCodesToAdventure(Utils.getSuffix(player))  : ""))
                            .append(Utils.format(" <gray>\u00BB <white>"))
                            .append(mm.deserialize( "<gradient:" +
                                ChatGradientCMD.gradientProfiles.get(e.getPlayer().getUniqueId().toString()).gradientFrom + ":" +
                                ChatGradientCMD.gradientProfiles.get(e.getPlayer().getUniqueId().toString()).gradientTo + ">" +
                                mm.stripTags(PlainTextComponentSerializer.plainText().serialize(message)) + "</gradient>"
                            )));
        }

    }

    private enum ChatType {
        GRADIENT,
        COLOR,
        DEFAULT
    }

}
