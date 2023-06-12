package xyz.prorickey.classicdupe.commands.default1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ShopCMD implements CommandExecutor, TabCompleter {

    private static final List<Player> shopUsers = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
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
        for(int i = 0; i < 44; i++) inv.setItem(i, blank);

        shopPage.getItems().forEach((slot, shopItem) -> {
            ItemStack item = shopItem.itemStack;
            item.editMeta(meta -> {
                meta.lore(List.of(
                        Utils.format("<green>Cost: <yellow>" + shopItem.price)
                ));
            });
            inv.setItem(slot, item);
        });
        
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(player);
        PlayerDatabase.PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayer(player.getUniqueId().toString());
        skullMeta.displayName(Utils.format("<color:#11DE22>Your Balance <color:#8A8A8A>- <color:#E7D715>" + data.balance));
        head.setItemMeta(skullMeta);

        ItemStack moreBlank = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        moreBlank.editMeta(meta -> meta.displayName(Utils.format(" ")));
        for(int i = 45; i < 53; i++) inv.setItem(i, moreBlank);

        inv.setItem(49, head);

        player.openInventory(inv);
        shopUsers.add(player);
    }

    public static Map<Integer, ShopPage> shop = new HashMap<>();

    public static void reloadShop() {
        shop.clear();
        List<ConfigurationSection> pages = (List<ConfigurationSection>) Config.getConfig().getList("economy.shop.pages");
        AtomicInteger i = new AtomicInteger(0);
        pages.forEach(page -> {
            Map<Integer, ShopItem> items = new HashMap<>();
            List<ConfigurationSection> itemsList = (List<ConfigurationSection>) page.getList("items");
            AtomicInteger i2 = new AtomicInteger(0);
            itemsList.forEach(item -> {
               ItemStack itemStack = new ItemStack(Material.valueOf(item.getString("material")));
               itemStack.editMeta(meta -> {
                   meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
               });
               items.put(i2.getAndIncrement(), new ShopItem(itemStack, item.getInt("cost")));
            });
            shop.put(i.getAndIncrement(), new ShopPage(page.getString("name"), items));
        });
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

        public ShopItem(ItemStack itemStack, int price) {
            this.itemStack = itemStack;
            this.price = price;
        }

        public ItemStack getItemStack() { return itemStack; }
        public int getPrice() { return price; }
    }

}
