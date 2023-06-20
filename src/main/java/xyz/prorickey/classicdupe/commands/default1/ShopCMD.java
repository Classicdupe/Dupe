package xyz.prorickey.classicdupe.commands.default1;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.text.WordUtils;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShopCMD implements CommandExecutor, TabCompleter, Listener {

    private static final Map<Player, Integer> shopUsers = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        openShop(player, 0);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    private static void openShop(Player player, Integer page) {
        ShopPage shopPage = shop.get(page);
        Inventory inv = Bukkit.createInventory(player, 54, Utils.format(Config.getConfig().getString("economy.shop.name") + " <gray>- " + shopPage.getName()));

        ItemStack blank = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        blank.editMeta(meta -> meta.displayName(Utils.format(" ")));
        for(int i = 0; i < 45; i++) inv.setItem(i, blank);

        shopPage.getItems().forEach((slot, shopItem) -> {
            ItemStack item = shopItem.itemStack;
            item.editMeta(meta -> {
                List<Component> lore = new ArrayList<>();
                lore.add(Utils.format("<green>Cost: <yellow>" + shopItem.price + " <green>dabloons"));
                Arrays.stream(shopItem.unparsedLore.split("<br>"))
                        .toList().forEach(str -> lore.add(MiniMessage.miniMessage().deserialize(str).decoration(TextDecoration.ITALIC, false)));
                meta.lore(lore);
            });
            inv.setItem(slot, item);
        });
        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(player);
        PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
        skullMeta.displayName(Utils.format("<color:#11DE22>Your Balance <color:#8A8A8A>- <color:#E7D715>" + data.getBalance()));
        head.setItemMeta(skullMeta);

        ItemStack moreBlank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        moreBlank.editMeta(meta -> meta.displayName(Utils.format(" ")));
        for(int i = 45; i < 54; i++) inv.setItem(i, moreBlank);

        inv.setItem(49, head);

        player.openInventory(inv);
        shopUsers.put(player, page);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null || !shopUsers.containsKey(e.getWhoClicked()) || e.getClickedInventory().equals(e.getWhoClicked().getInventory())) return;
        e.setCancelled(true);
        ShopPage shopPage = shop.get(shopUsers.get(e.getWhoClicked()));
        if(shopPage.items.containsKey(e.getSlot())) {
            ShopItem shopItem = shopPage.items.get(e.getSlot());
            PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getWhoClicked().getUniqueId());
            if(data.balance < shopItem.price) {
                e.getWhoClicked().sendMessage(Utils.cmdMsg("<red>You do not have enough money to buy this item"));
                return;
            }
            data.subtractBalance(shopItem.price);
            ItemStack item = new ItemStack(shopItem.material);
            item.editMeta(meta -> meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true));
            e.getWhoClicked().getInventory().addItem(item);
            e.getWhoClicked().sendMessage(Utils.cmdMsg("<green>You have successfully purchased this item"));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!shopUsers.containsKey(e.getPlayer())) return;
        shopUsers.remove(e.getPlayer());
    }

    public static Map<Integer, ShopPage> shop = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void reloadShop() {
        shop.clear();
        List<Map<String, ?>> pages = (List<Map<String, ?>>) Config.getConfig().getList("economy.shop.pages");
        AtomicInteger i = new AtomicInteger(0);
        assert pages != null;
        pages.forEach(page -> {
            Map<Integer, ShopItem> items = new HashMap<>();
            List<Map<String, ?>> itemsList = (List<Map<String, ?>>) page.get("items");
            AtomicInteger i2 = new AtomicInteger(0);
            itemsList.forEach(item -> {
               ItemStack itemStack = new ItemStack(Material.valueOf(((String) item.get("material")).toUpperCase()));
               itemStack.editMeta(meta -> {
                   meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
                   meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                   String name = (String) item.get("material");
                   name = firstLetterCapitalWithSingleSpace(name
                           .substring(0, name.length()-"_smithing_template".length())
                           .replaceAll("_", " "));
                   meta.displayName(Utils.format("<yellow>" + name));
               });
               items.put(i2.getAndIncrement(), new ShopItem(Material.valueOf(((String) item.get("material")).toUpperCase()), itemStack, (Integer) item.get("cost"), (String) item.get("lore")));
            });
            shop.put(i.getAndIncrement(), new ShopPage((String) page.get("name"), items));
        });
    }

    private static String firstLetterCapitalWithSingleSpace(final String words) {
        return Stream.of(words.trim().split("\\s"))
                .filter(word -> word.length() > 0)
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static class ShopPage {

        private final String name;
        private final Map<Integer, ShopItem> items;

        public ShopPage(String name, Map<Integer, ShopItem> items) {
            this.name = name;
            this.items = items;
        }

        public String getName() { return name; }
        public Map<Integer, ShopItem> getItems() { return items; }

    }

    public static class ShopItem {

        private final ItemStack itemStack;
        private final int price;
        private final String unparsedLore;
        private final Material material;

        public ShopItem(Material mat, ItemStack itemStack, int price, String unparsedLore) {
            this.itemStack = itemStack;
            this.price = price;
            this.unparsedLore = unparsedLore;
            this.material = mat;
        }

        public ItemStack getItemStack() { return itemStack; }
        public int getPrice() { return price; }
        public String getUnparsedLore() { return unparsedLore; }
        public Material getMaterial() { return material; }
    }

}
