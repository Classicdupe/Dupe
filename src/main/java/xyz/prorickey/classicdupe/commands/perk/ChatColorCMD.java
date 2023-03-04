package xyz.prorickey.classicdupe.commands.perk;

import net.kyori.adventure.text.Component;
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

    public static Map<String, String> colorProfiles = new HashMap<>();

    private static final Map<String, Inventory> chatcolorGUIS = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("&cYou cannot execute this command from console"));
            return true;
        }
        String currentColor;
        currentColor = colorProfiles.getOrDefault(p.getUniqueId().toString(), "&7");
        if(args.length == 0) {
            Inventory gui = Bukkit.createInventory(null, 9, Component.text(Utils.format("&aChatColor Menu")));

            //White
            ItemStack whiteWool = new ItemStack(Material.WHITE_WOOL);
            whiteWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&fWhite Chat Color")));
                if(currentColor.equals("&f")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(0, whiteWool);

            //Pink
            ItemStack pinkWool = new ItemStack(Material.PINK_WOOL);
            pinkWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&dPink Chat Color")));
                if(currentColor.equals("&d")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(1, pinkWool);

            //Red
            ItemStack redWool = new ItemStack(Material.RED_WOOL);
            redWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&dRed Chat Color")));
                if(currentColor.equals("&c")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(2, redWool);

            //Aqua
            ItemStack aquaWool = new ItemStack(Material.LIGHT_BLUE_WOOL);
            aquaWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&bAqua Chat Color")));
                if(currentColor.equals("&b")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(3, aquaWool);

            //Blue
            ItemStack blueWool = new ItemStack(Material.BLUE_WOOL);
            blueWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&9Blue Chat Color")));
                if(currentColor.equals("&9")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(4, blueWool);

            //Green
            ItemStack greenWool = new ItemStack(Material.GREEN_WOOL);
            greenWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&aGreen Chat Color")));
                if(currentColor.equals("&a")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(5, greenWool);

            //Yellow
            ItemStack yellowWool = new ItemStack(Material.YELLOW_WOOL);
            yellowWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&eYellow Chat Color")));
                if(currentColor.equals("&e")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(6, yellowWool);

            //Gold
            ItemStack goldWool = new ItemStack(Material.ORANGE_WOOL);
            goldWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&6Gold Chat Color")));
                if(currentColor.equals("&6")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(7, goldWool);

            //Gray
            ItemStack grayWool = new ItemStack(Material.LIGHT_GRAY_WOOL);
            grayWool.editMeta(meta -> {
                meta.displayName(Component.text(Utils.format("&7Gray Chat Color")));
                if(currentColor.equals("&7")) meta.lore(List.of(Component.text(Utils.format("&aEnabled"))));
                else meta.lore(List.of(Component.text(Utils.format("&cDisabled"))));
            });
            gui.setItem(8, grayWool);

            p.openInventory(gui);
            chatcolorGUIS.put(p.getUniqueId().toString(), gui);

        } else {
            switch (args[0].toLowerCase()) {
                case "white" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&f");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &fWhite"));
                }
                case "pink" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&d");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &dPink"));
                }
                case "red" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&c");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &cRed"));
                }
                case "aqua" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&b");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &bAqua"));
                }
                case "blue" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&9");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &9Blue"));
                }
                case "green" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&a");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &aGreen"));
                }
                case "yellow" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&e");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &eYellow"));
                }
                case "gold" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&6");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &6Gold"));
                }
                case "gray" -> {
                    colorProfiles.put(p.getUniqueId().toString(), "&7");
                    p.sendMessage(Utils.cmdMsg("&aSet your chat color to &7Gray"));
                }
            }
            ClassicDupe.getDatabase().getPlayerDatabase().setChatColor(p.getUniqueId().toString(), colorProfiles.get(p.getUniqueId().toString()));
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
                colorProfiles.put(p.getUniqueId().toString(), "&f");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &fWhite"));
            }
            case 1 -> {
                colorProfiles.put(p.getUniqueId().toString(), "&d");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &dPink"));
            }
            case 2 -> {
                colorProfiles.put(p.getUniqueId().toString(), "&c");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &cRed"));
            }
            case 3 -> {
                colorProfiles.put(p.getUniqueId().toString(), "&b");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &bAqua"));
            }
            case 4 -> {
                colorProfiles.put(p.getUniqueId().toString(), "&9");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &9Blue"));
            }
            case 5 -> {
                colorProfiles.put(p.getUniqueId().toString(), "&a");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &aGreen"));
            }
            case 6 -> {
                colorProfiles.put(p.getUniqueId().toString(), "&e");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &eYellow"));
            }
            case 7 -> {
                colorProfiles.put(p.getUniqueId().toString(), "&6");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &6Gold"));
            }
            case 8 -> {
                colorProfiles.put(p.getUniqueId().toString(), "&7");
                p.sendMessage(Utils.cmdMsg("&aSet your chat color to &7Gray"));
            }
        }
        ClassicDupe.getDatabase().getPlayerDatabase().setChatColor(p.getUniqueId().toString(), colorProfiles.get(p.getUniqueId().toString()));
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
