package xyz.prorickey.classicdupe.commands.perk;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatColorCMD implements CommandExecutor, Listener, TabCompleter {

    public static final Map<String, String> colorProfiles = new HashMap<>();

    private static final Map<String, Inventory> chatcolorGUIS = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        String currentColor;
        currentColor = colorProfiles.getOrDefault(p.getUniqueId().toString(), "<gray>");
        if(args.length == 0) {
            Inventory gui = Bukkit.createInventory(null, 9, Utils.format("<green>ChatColor Menu"));

            //White
            ItemStack whiteWool = new ItemStack(Material.WHITE_WOOL);
            whiteWool.editMeta(meta -> {
                meta.displayName(Utils.format("<white>White Chat Color"));
                if(currentColor.equals("<white>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(0, whiteWool);

            //Pink
            ItemStack pinkWool = new ItemStack(Material.PINK_WOOL);
            pinkWool.editMeta(meta -> {
                meta.displayName(Utils.format("<light_purple>Pink Chat Color"));
                if(currentColor.equals("<light_purple>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(1, pinkWool);

            //Red
            ItemStack redWool = new ItemStack(Material.RED_WOOL);
            redWool.editMeta(meta -> {
                meta.displayName(Utils.format("<light_purple>Red Chat Color"));
                if(currentColor.equals("<red>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(2, redWool);

            //Aqua
            ItemStack aquaWool = new ItemStack(Material.LIGHT_BLUE_WOOL);
            aquaWool.editMeta(meta -> {
                meta.displayName(Utils.format("<aqua>Aqua Chat Color"));
                if(currentColor.equals("<aqua>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(3, aquaWool);

            //Blue
            ItemStack blueWool = new ItemStack(Material.BLUE_WOOL);
            blueWool.editMeta(meta -> {
                meta.displayName(Utils.format("<blue>Blue Chat Color"));
                if(currentColor.equals("<blue>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(4, blueWool);

            //Green
            ItemStack greenWool = new ItemStack(Material.GREEN_WOOL);
            greenWool.editMeta(meta -> {
                meta.displayName(Utils.format("<green>Green Chat Color"));
                if(currentColor.equals("<green>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(5, greenWool);

            //Yellow
            ItemStack yellowWool = new ItemStack(Material.YELLOW_WOOL);
            yellowWool.editMeta(meta -> {
                meta.displayName(Utils.format("<yellow>Yellow Chat Color"));
                if(currentColor.equals("<yellow>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(6, yellowWool);

            //Gold
            ItemStack goldWool = new ItemStack(Material.ORANGE_WOOL);
            goldWool.editMeta(meta -> {
                meta.displayName(Utils.format("<gold>Gold Chat Color"));
                if(currentColor.equals("<gold>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(7, goldWool);

            //Gray
            ItemStack grayWool = new ItemStack(Material.LIGHT_GRAY_WOOL);
            grayWool.editMeta(meta -> {
                meta.displayName(Utils.format("<gray>Gray Chat Color"));
                if(currentColor.equals("<gray>")) meta.lore(List.of(Utils.format("<green>Enabled")));
                else meta.lore(List.of(Utils.format("<red>Disabled")));
            });
            gui.setItem(8, grayWool);

            p.openInventory(gui);
            chatcolorGUIS.put(p.getUniqueId().toString(), gui);

        } else {
            switch (args[0].toLowerCase()) {
                case "white" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<white>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <white>White"));
                }
                case "pink" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<light_purple>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <light_purple>Pink"));
                }
                case "red" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<red>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <red>Red"));
                }
                case "aqua" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<aqua>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <aqua>Aqua"));
                }
                case "blue" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<blue>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <blue>Blue"));
                }
                case "green" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<green>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <green>Green"));
                }
                case "yellow" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<yellow>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <yellow>Yellow"));
                }
                case "gold" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<gold>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <gold>Gold"));
                }
                case "gray" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "<gray>");
                    p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <gray>Gray"));
                }
            }
            ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(p.getUniqueId()).setChatColor(colorProfiles.get(p.getUniqueId().toString()));
        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!e.getInventory().equals(chatcolorGUIS.get(e.getWhoClicked().getUniqueId().toString()))) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        switch (e.getRawSlot()) {
            case 0 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<white>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <white>White"));
            }
            case 1 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<light_purple>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <light_purple>Pink"));
            }
            case 2 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<red>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <red>Red"));
            }
            case 3 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<aqua>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <aqua>Aqua"));
            }
            case 4 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<blue>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <blue>Blue"));
            }
            case 5 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<green>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <green>Green"));
            }
            case 6 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<yellow>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <yellow>Yellow"));
            }
            case 7 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<gold>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <gold>Gold"));
            }
            case 8 -> {
                colorProfiles.put(p.getUniqueId().toString(), "<gray>");
                p.sendMessage(Utils.cmdMsg("<green>Set your chat color to <gray>Gray"));
            }
        }
        ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(p.getUniqueId()).setChatColor(colorProfiles.get(p.getUniqueId().toString()));
        e.getInventory().close();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!e.getInventory().equals(chatcolorGUIS.get(e.getPlayer().getUniqueId().toString()))) return;
        chatcolorGUIS.remove(e.getPlayer().getUniqueId().toString());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return List.of("white", "pink", "red", "aqua", "blue", "green", "yellow", "gold", "gray");
        return new ArrayList<>();
    }
}
