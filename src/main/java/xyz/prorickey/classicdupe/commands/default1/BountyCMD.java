package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.database.PlayerDatabase;
import xyz.prorickey.proutils.TabComplete;

import java.util.*;

public class BountyCMD implements CommandExecutor, TabCompleter, Listener {

    public static NamespacedKey noMoveKey = new NamespacedKey(ClassicDupe.getPlugin(), "noMoveKey");;
    public static NamespacedKey closeKey = new NamespacedKey(ClassicDupe.getPlugin(), "closeKey");
    public static NamespacedKey nextKey = new NamespacedKey(ClassicDupe.getPlugin(), "nextKey");
    public static NamespacedKey backKey = new NamespacedKey(ClassicDupe.getPlugin(), "backKey");
    public static NamespacedKey uuidKey = new NamespacedKey(ClassicDupe.getPlugin(), "uuidKey");
    List<ItemStack> lastPage = new ArrayList<>();

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if(args.length == 0) {
            createBountyGUI(player, 0);
            return true;
        } else {
            switch(args[0].toLowerCase()) {
                case "set" -> {
                    if(args.length < 3) {
                        player.sendMessage(Utils.cmdMsg("<red>You need to provide a player and an amount"));
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if(target == null || ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(target.getUniqueId()) == null) {
                        player.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                        return true;
                    }
                    int amount;
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch(NumberFormatException e) {
                        player.sendMessage(Utils.cmdMsg("<red>You need to provide a valid number"));
                        return true;
                    }
                    if(amount < 1) {
                        player.sendMessage(Utils.cmdMsg("<red>You need to provide a number greater than 0"));
                        return true;
                    }
                    PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
                    if(playerData.getBalance() < amount) {
                        player.sendMessage(Utils.cmdMsg("<red>You do not have enough money to place that bounty"));
                        return true;
                    }
                    playerData.subtractBalance(amount);
                    ClassicDupe.getDatabase().getBountyDatabase().addBounty(target.getUniqueId(), amount);
                    player.sendMessage(Utils.cmdMsg("<green>You placed a bounty of <yellow>" + amount + "<green> on <yellow>" + target.getName()));
                    if(target.isOnline()) target.getPlayer().sendMessage(Utils.cmdMsg("<green>A bounty of <yellow>" + amount + "<green> has been placed on you by <yellow>" + player.getName()));
                }
                case "list" -> {
                    Map<UUID, Integer> sorted = ClassicDupe.getDatabase().getBountyDatabase().getBountiesSorted();
                    if(sorted.isEmpty()) {
                        player.sendMessage(Utils.cmdMsg("<red>There are no bounties currently"));
                        return true;
                    }
                    player.sendMessage(Utils.format("<yellow>Top 5 Bounties"));
                    int amount = Math.min(sorted.size(), 5);
                    for(int i = 0; i < amount; i++) {
                        UUID uuid = (UUID) sorted.keySet().toArray()[i];
                        String bitchass = Bukkit.getOfflinePlayer(uuid).getName();
                        player.sendMessage(Utils.format("  <green>" + (i+1) + ". <yellow>" + bitchass + " <gray>- <yellow>" + sorted.get(uuid)));
                    }
                }
                case "forceset" -> {
                    if(!player.hasPermission("default.bounty.forceset")) {
                        player.sendMessage(Utils.cmdMsg("<red>You do not have permission to use this command"));
                        return true;
                    }
                    if(args.length < 3) {
                        player.sendMessage(Utils.cmdMsg("<red>You need to provide a player and an amount"));
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if(target == null || ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(target.getUniqueId()) == null) {
                        player.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                        return true;
                    }
                    int amount;
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch(NumberFormatException e) {
                        player.sendMessage(Utils.cmdMsg("<red>You need to provide a valid number"));
                        return true;
                    }
                    if(amount < 1) {
                        player.sendMessage(Utils.cmdMsg("<red>You need to provide a number greater than 0"));
                        return true;
                    }
                    ClassicDupe.getDatabase().getBountyDatabase().setBounty(target.getUniqueId(), amount);
                    player.sendMessage(Utils.cmdMsg("<green>You force the bounty of <yellow>" + amount + "<green> to be <yellow>" + target.getName()));
                }
                case "delete" -> {
                    if(!player.hasPermission("default.bounty.delete")) {
                        player.sendMessage(Utils.cmdMsg("<red>You do not have permission to use this command"));
                        return true;
                    }
                    if(args.length < 2) {
                        player.sendMessage(Utils.cmdMsg("<red>You need to provide a player"));
                        return true;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if(target == null || ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(target.getUniqueId()) == null) {
                        player.sendMessage(Utils.cmdMsg("<red>That player does not exist"));
                        return true;
                    }
                    ClassicDupe.getDatabase().getBountyDatabase().deleteBounty(target.getUniqueId());
                    player.sendMessage(Utils.cmdMsg("<green>You deleted the bounty on <yellow>" + target.getName()));
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], Arrays.asList("set", "list", "forceset", "delete"));
        else if(args.length == 2) {
            switch(args[0].toLowerCase()) {
                case "set", "forceset", "delete" -> {
                    List<String> list = new ArrayList<>();
                    for(Player player : Bukkit.getOnlinePlayers()) list.add(player.getName());
                    return TabComplete.tabCompletionsSearch(args[1], list);
                }
            }
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("ConstantConditions")
    public static void createBountyGUI(Player player, Integer page) {
        Inventory gui = Bukkit.createInventory(new BountiesGUI(), 54, Utils.format("<yellow>Bounty List"));

        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        glass.editMeta(meta -> {
            meta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
            meta.displayName(Utils.format(" "));
        });
        for(int i = 0; i < 54; i++) gui.setItem(i, glass);

        ItemStack close = new ItemStack(Material.ARROW);
        close.editMeta(meta -> {
            meta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
            meta.getPersistentDataContainer().set(closeKey, PersistentDataType.STRING, "closeKey");
            meta.displayName(Utils.format("<red>Close"));
        });
        gui.setItem(49, close);

        Map<UUID, Integer> bountiesSorted = ClassicDupe.getDatabase().getBountyDatabase().getBountiesSorted();

        ItemStack next;
        if(bountiesSorted.size()>page*45) next = new ItemStack(Material.EMERALD_BLOCK);
        else next = new ItemStack(Material.BARRIER);
        next.editMeta(meta -> {
            meta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
            meta.getPersistentDataContainer().set(nextKey, PersistentDataType.STRING, "nextKey");
            meta.displayName(Utils.format("<green>Next"));
        });
        gui.setItem(53, next);

        ItemStack back;
        if(page != 0) back = new ItemStack(Material.REDSTONE_BLOCK);
        else back = new ItemStack(Material.BARRIER);
        back.editMeta(meta -> {
            meta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
            meta.getPersistentDataContainer().set(backKey, PersistentDataType.STRING, "backKey");
            meta.displayName(Utils.format("<red>Back"));
        });
        gui.setItem(45, back);

        int start = page*45;
        int end = Math.min(start+45, bountiesSorted.size());
        for(int i = start; i < end; i++) {
            UUID uuid = (UUID) bountiesSorted.keySet().toArray()[i];
            String bitchass = Bukkit.getOfflinePlayer(uuid).getName();
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            PlayerDatabase.PlayerStats stats = ClassicDupe.getDatabase().getPlayerDatabase().getStats(uuid.toString());
            skull.editMeta(meta -> {
                meta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
                meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, uuid.toString());
                meta.displayName(Utils.format("<yellow>" + bitchass));
                meta.lore(List.of(
                        Utils.format("<green>Bounty: <yellow>" + bountiesSorted.get(uuid)),
                        Utils.format("<green>Kills: <yellow>" + stats.kills),
                        Utils.format("<green>Deaths: <yellow>" + stats.deaths),
                        Utils.format("<green>KDR: <yellow>" + stats.kdr)
                ));
            });
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            skull.setItemMeta(skullMeta);
            gui.setItem(i-start, skull);
        }

        currentPage.put(player, page);
        player.openInventory(gui);
    }

    private static Map<Player, Integer> currentPage = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getHolder() instanceof BountiesGUI) {
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(noMoveKey, PersistentDataType.STRING)) event.setCancelled(true);
                if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(closeKey, PersistentDataType.STRING)) clickedInventory.close();
                if (
                        event.getCurrentItem().getType().equals(Material.EMERALD_BLOCK) &&
                                event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(nextKey, PersistentDataType.STRING)
                ) createBountyGUI(player, currentPage.get(player)+1);
                if(
                        event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK) &&
                                event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(backKey, PersistentDataType.STRING)
                ) createBountyGUI(player, currentPage.get(player)-1);
            }
        }
    }

    public static class BountiesGUI implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

}
