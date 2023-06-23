package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class HelpCMD implements CommandExecutor, TabCompleter, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            Inventory gui = Bukkit.createInventory(new HelpGUIInventoryHolder(), 54, Utils.format("<color:#23C2F1><b>Help Menu"));
            for(int i = 0; i < 54; i++) gui.setItem(i, Utils.getGuiFiller());

            // World Information
            ItemStack world = new ItemStack(Material.GREEN_GLAZED_TERRACOTTA);
            world.editMeta(meta -> {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
                meta.displayName(Utils.format("<color:#26DE09><b>World Information"));
                meta.lore(List.of(
                        Utils.format("<color:#26DE09>Overworld Border: 10,000 x 10,000"),
                        Utils.format("<color:#26DE09>Nether Border: Infinite x Infinite"),
                        Utils.format("<color:#26DE09>"),
                        Utils.format("<color:#26DE09>Hub Spawn Command: /spawn"),
                        Utils.format("<color:#26DE09>Overworld Spawn Command: /overworld"),
                        Utils.format("<color:#26DE09>Nether Spawn Command: /nether")
                ));
            });
            gui.setItem(13, world);

            // Dupe Explanation
            ItemStack dupe = new ItemStack(Material.ANVIL);
            dupe.editMeta(meta -> {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
                meta.displayName(Utils.format("<color:#E6EC18><b>Duping"));
                meta.lore(List.of(
                   Utils.format("<color:#E6EC18>Dupe the item in your hand with /dupe"),
                   Utils.format("<color:#E6EC18>Not all items are dupable though!")
                ));
            });
            gui.setItem(22, dupe);

            // Rules
            ItemStack rules = new ItemStack(Material.BARRIER);
            rules.editMeta(meta -> {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
                meta.displayName(Utils.format("<color:#F92626><b>Rules"));
                meta.lore(List.of(
                    Utils.format("<color:#F92626>Make sure you take a look at the rules!"),
                    Utils.format("<color:#F92626>Click to view the rules")
                ));
            });
            gui.setItem(31, rules);

            // Armor Trims
            ItemStack armorTrims = new ItemStack(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
            armorTrims.editMeta(meta -> {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
                meta.displayName(Utils.format("<color:#F98326><b>Armor Trims"));
                meta.lore(List.of(
                    Utils.format("<color:#F98326>Armor Trims are a way to give your armor special perks!"),
                    Utils.format("<color:#F98326>Click to view all the armor trim perks")
                ));
            });
            gui.setItem(40, armorTrims);

            // Shop
            ItemStack shop = new ItemStack(Material.EMERALD);
            shop.editMeta(meta -> {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
                meta.displayName(Utils.format("<color:#F926F8><b>Shop"));
                meta.lore(List.of(
                    Utils.format("<color:#F926F8>Open the shop to buy special items!"),
                    Utils.format("<color:#F926F8>Click to view the shop")
                ));
            });
            gui.setItem(21, shop);

            // Discord
            ItemStack discord = new ItemStack(Material.COMPASS);
            discord.editMeta(meta -> {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
                meta.displayName(Utils.format("<color:#4A70FC><b>Discord"));
                meta.lore(List.of(
                    Utils.format("<color:#4A70FC>Join the discord to stay up to date with the server!"),
                    Utils.format("<color:#4A70FC>Click to join the discord")
                ));
            });
            gui.setItem(23, discord);

            // Store
            ItemStack store = new ItemStack(Material.GOLD_INGOT);
            store.editMeta(meta -> {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
                meta.displayName(Utils.format("<color:#F9F926><b>Store"));
                meta.lore(List.of(
                    Utils.format("<color:#F9F926>Visit the store to buy ranks and perks!"),
                    Utils.format("<color:#F9F926>Click to visit the store")
                ));
            });
            gui.setItem(30, store);

            // Naked off
            ItemStack nakedOff = new ItemStack(Material.GOLDEN_SWORD);
            nakedOff.editMeta(meta -> {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ITEM_SPECIFICS);
                meta.displayName(Utils.format("<color:#F9F926><b>Naked Off"));
                meta.lore(List.of(
                    Utils.format("<color:#F9F926>Turn off naked mode to pvp with other players!"),
                    Utils.format("<color:#F9F926>You will also lose your 10 minute grace period"),
                    Utils.format("<color:#F9F926>Click to turn naked off")
                ));
            });
            gui.setItem(32, nakedOff);

            player.openInventory(gui);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], List.of("world", "dupe", "armor", "shop", "discord", "store", "naked", "rules"));
        return new ArrayList<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getInventory().getHolder() instanceof HelpGUIInventoryHolder)) return;
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if(event.getCurrentItem() == null) return;
        switch(event.getRawSlot()) {
            case 13 -> {
                player.closeInventory();
                player.sendMessage(Utils.format("<color:#26DE09><b>World Information"));
                player.sendMessage(Utils.format("<color:#26DE09>- Overworld Border: 10,000 x 10,000"));
                player.sendMessage(Utils.format("<color:#26DE09>- Nether Border: Infinite x Infinite"));
                player.sendMessage(Utils.format("<color:#26DE09>- Hub Spawn Command: /spawn"));
                player.sendMessage(Utils.format("<color:#26DE09>- Overworld Spawn Command: /overworld"));
                player.sendMessage(Utils.format("<color:#26DE09>- Nether Spawn Command: /nether"));
            }
            case 21 -> {
                player.closeInventory();
                player.performCommand("shop");
            }
            case 22 -> {
                player.closeInventory();
                player.sendMessage(Utils.format("<color:#E6EC18><b>Duping"));
                player.sendMessage(Utils.format("<color:#26DE09>- Dupe the item in your hand with /dupe"));
                player.sendMessage(Utils.format("<color:#26DE09>- Not all items are dupable though!"));
            }
            case 23 -> {
                player.closeInventory();
                player.performCommand("discord");
            }
            case 30 -> {
                player.closeInventory();
                player.performCommand("buy");
            }
            case 31 -> {
                player.closeInventory();
                player.performCommand("rules");
            }
            case 32 -> {
                player.closeInventory();
                player.performCommand("nakedoff");
            }
            case 40 -> {
                player.closeInventory();
                player.sendMessage(Utils.format("<color:#F98326><b>Armor Trims"));
                player.sendMessage(Utils.format("<color:#F98326>- Armor Trims are a way to give your armor special perks!"));
                player.sendMessage(Utils.format("<color:#F98326>- You can view all the armor trim perks in the shop with /shop!"));
            }
        }
    }

    private static class HelpGUIInventoryHolder implements InventoryHolder {
            @Override
            public @NotNull Inventory getInventory() {
                return null;
            }
    }

}
