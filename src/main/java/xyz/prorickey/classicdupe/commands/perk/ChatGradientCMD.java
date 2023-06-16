package xyz.prorickey.classicdupe.commands.perk;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;

import java.util.*;

public class ChatGradientCMD implements CommandExecutor, TabCompleter, Listener {

    public static final Map<String, GradientProfiles> gradientProfiles = new HashMap<>();
    private static final Map<String, Inventory> chatgradientGUIS = new HashMap<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    public static class GradientProfiles {
        public String gradientFrom;
        public String gradientTo;
        public GradientProfiles(String gradientFrom1, String gradientTo1) {
            gradientFrom = gradientFrom1;
            gradientTo = gradientTo1;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage("<red>You cannot execute this command from console.");
            return true;
        }

        PlayerData pdata = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(p.getUniqueId());

        Inventory gui = Bukkit.createInventory(null, 27, Utils.format("<green>ChatGradient Menu"));
        List.of(3, 4, 5, 12, 14, 21, 22, 23).forEach(n -> gui.setItem(n, new ItemStack(Material.RED_STAINED_GLASS_PANE)));

        //Toggle
        ItemStack star = new ItemStack(Material.NETHER_STAR);
        star.editMeta(meta -> {
            if(pdata.gradient) meta.displayName(Utils.format("<red>Turn Gradient Chat Off"));
            else meta.displayName(Utils.format("<green>Turn Gradient Chat On"));
        });
        gui.setItem(13, star);

        //Wools
        MiniMessage mm = MiniMessage.miniMessage();
        List<Integer> firstSec = List.of(0, 1, 2, 9, 10, 11, 18, 19, 20);
        List<Integer> secondSec = List.of(6, 7, 8, 15, 16, 17, 24, 25, 26);
        List<String> options = List.of("white", "pink", "red", "aqua", "blue", "green", "yellow", "gold", "gray");
        Map<String, Material> optionsToMaterial = new HashMap<>();
        optionsToMaterial.put("white", Material.WHITE_WOOL);
        optionsToMaterial.put("pink", Material.PINK_WOOL);
        optionsToMaterial.put("red", Material.RED_WOOL);
        optionsToMaterial.put("aqua", Material.LIGHT_BLUE_WOOL);
        optionsToMaterial.put("blue", Material.BLUE_WOOL);
        optionsToMaterial.put("green", Material.GREEN_WOOL);
        optionsToMaterial.put("yellow", Material.YELLOW_WOOL);
        optionsToMaterial.put("gold", Material.ORANGE_WOOL);
        optionsToMaterial.put("gray", Material.LIGHT_GRAY_WOOL);

        for(int i = 0; i < 9; i++) {
            String name = options.get(i);
            String opt = name.equals("pink") ? "light_purple" : name;
            Material mat = optionsToMaterial.get(name);
            ItemStack wool = new ItemStack(mat);
            wool.editMeta(meta -> meta.displayName(mm.deserialize("<" + opt + ">" + name.substring(0, 1).toUpperCase() + name.substring(1))));
            wool.editMeta(meta -> {
                if(Objects.equals(pdata.gradientfrom, opt)) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(firstSec.get(i), wool);
            wool.editMeta(meta -> {
                if(Objects.equals(pdata.gradientto, opt)) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(secondSec.get(i), wool);
        }

        p.openInventory(gui);
        chatgradientGUIS.put(p.getUniqueId().toString(), gui);

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!e.getInventory().equals(chatgradientGUIS.get(e.getWhoClicked().getUniqueId().toString()))) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        List<Integer> firstSec = List.of(0, 1, 2, 9, 10, 11, 18, 19, 20);
        List<Integer> secondSec = List.of(6, 7, 8, 15, 16, 17, 24, 25, 26);
        PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(p.getUniqueId());
        if(firstSec.contains(e.getRawSlot())) {
            String name = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName()).toLowerCase();
            String color = name.equals("pink") ? "light_purple" : name;
            if(gradientProfiles.containsKey(p.getUniqueId().toString())) gradientProfiles.put(p.getUniqueId().toString(), new GradientProfiles(color, gradientProfiles.get(p.getUniqueId().toString()).gradientTo));
            else gradientProfiles.put(p.getUniqueId().toString(), new GradientProfiles(color, "white"));
            playerData.setGradientProfile(gradientProfiles.get(p.getUniqueId().toString()));
        } else if(secondSec.contains(e.getRawSlot())) {
            String name = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName()).toLowerCase();
            String color = name.equals("pink") ? "light_purple" : name;
            if(gradientProfiles.containsKey(p.getUniqueId().toString())) gradientProfiles.put(p.getUniqueId().toString(), new GradientProfiles(gradientProfiles.get(p.getUniqueId().toString()).gradientFrom, color));
            else gradientProfiles.put(p.getUniqueId().toString(), new GradientProfiles("white", color));
            playerData.setGradientProfile(gradientProfiles.get(p.getUniqueId().toString()));
        } else if(e.getRawSlot() == 13) {
            GradientProfiles profile = playerData.getGradientProfile();
            if(profile.gradientFrom == null) profile.gradientFrom = "white";
            if(profile.gradientTo == null) profile.gradientTo = "white";
            if(playerData.toggleGradient()) gradientProfiles.put(p.getUniqueId().toString(), profile);
            else gradientProfiles.remove(p.getUniqueId().toString());
        }
        e.getInventory().close();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!e.getInventory().equals(chatgradientGUIS.get(e.getPlayer().getUniqueId().toString()))) return;
        chatgradientGUIS.remove(e.getPlayer().getUniqueId().toString());
    }

}
