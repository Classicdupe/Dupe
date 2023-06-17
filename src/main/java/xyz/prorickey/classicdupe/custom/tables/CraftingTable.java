package xyz.prorickey.classicdupe.custom.tables;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

public class CraftingTable implements Listener {

    private static final NamespacedKey noMoveKey = new NamespacedKey(ClassicDupe.getPlugin(), "noMoveKey");
    private static final NamespacedKey closeKey = new NamespacedKey(ClassicDupe.getPlugin(), "closeKey");
    private static final NamespacedKey craftKey = new NamespacedKey(ClassicDupe.getPlugin(), "craftKey");
    private static final NamespacedKey craftToDefKey = new NamespacedKey(ClassicDupe.getPlugin(), "craftToDefKey");
    private static final NamespacedKey craftToNewKey = new NamespacedKey(ClassicDupe.getPlugin(), "craftToNewKey");
    private static final NamespacedKey craftedKey = new NamespacedKey(ClassicDupe.getPlugin(), "craftedKey");

    @EventHandler
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                event.getClickedBlock() != null &&
                event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE)
        ) {
            event.setCancelled(true);
            loadSwitch(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null &&
                (clickedInventory.getHolder() instanceof CraftingInventory || clickedInventory.getHolder() instanceof SwitchInventory) &&
                event.getCurrentItem() != null
        ) {
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(noMoveKey, PersistentDataType.STRING)) event.setCancelled(true);
            if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(closeKey, PersistentDataType.STRING)) loadSwitch(player);
            if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(craftToDefKey, PersistentDataType.STRING)) player.openWorkbench(null, true);
            if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(craftToNewKey, PersistentDataType.STRING)) loadCustom(player);
            if (event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(craftKey, PersistentDataType.STRING)) {
                ItemStack[] craftingMatrix = new ItemStack[9];
                Integer[] slots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                for(int i = 0; i < 9; i++) craftingMatrix[i] = clickedInventory.getItem(slots[i]);
                Recipe recipe = Bukkit.getServer().getCraftingRecipe(craftingMatrix, player.getWorld());
                if (recipe != null) {
                    ItemStack result = recipe.getResult();
                    result.editMeta(meta -> meta.getPersistentDataContainer().set(craftedKey, PersistentDataType.STRING, "craftedKey"));
                    clickedInventory.setItem(25, result);
                }
            }
            if(event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(craftedKey, PersistentDataType.STRING)) {

            }
        }
    }

    public static void loadCustom(Player player) {
        Inventory inventory = Bukkit.createInventory(new CraftingInventory(), 54,  Utils.format("<green>Crafting Table"));
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta gMeta = glass.getItemMeta();
        ItemStack close = new ItemStack(Material.ARROW);
        ItemMeta aMeta = close.getItemMeta();
        gMeta.displayName(Utils.format("<green>Crafting Table"));
        gMeta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
        aMeta.displayName(Utils.format("<red>Back"));
        aMeta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
        aMeta.getPersistentDataContainer().set(closeKey, PersistentDataType.STRING, "closeKey");
        close.setItemMeta(aMeta);
        glass.setItemMeta(gMeta);
        for(int i = 0; i < 54; i++) inventory.setItem(i, glass);
        Integer[] slots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
        for(int i = 0; i < slots.length; i++) inventory.setItem(slots[i], null);
        inventory.setItem(25, null);
        inventory.setItem(49, close);
        ItemStack craft = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta cMeta = craft.getItemMeta();
        cMeta.displayName(Utils.format("<yellow>Craft"));
        cMeta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
        cMeta.getPersistentDataContainer().set(craftKey, PersistentDataType.STRING, "craftKey");
        craft.setItemMeta(cMeta);
        inventory.setItem(23, craft);
        player.openInventory(inventory);
    }

    private static void loadSwitch(Player player) {
        Inventory gui = Bukkit.createInventory(new SwitchInventory(), 27, "Crafting");
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta gMeta = glass.getItemMeta();
        gMeta.displayName(Utils.format("<green>Crafting Table"));
        gMeta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
        glass.setItemMeta(gMeta);
        for(int i = 0; i < 27; i++) gui.setItem(i, glass);
        ItemStack defaultItem = new ItemStack(Material.CRAFTING_TABLE);
        ItemStack newItem = new ItemStack(Material.BEACON);
        ItemMeta defMeta = defaultItem.getItemMeta();
        ItemMeta newMeta = newItem.getItemMeta();
        defMeta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
        defMeta.getPersistentDataContainer().set(craftToDefKey, PersistentDataType.STRING, "craftToDefKey");
        newMeta.getPersistentDataContainer().set(noMoveKey, PersistentDataType.STRING, "noMoveKey");
        newMeta.getPersistentDataContainer().set(craftToNewKey, PersistentDataType.STRING, "craftToNewKey");
        defMeta.displayName(Utils.format("<yellow>Legacy Crafting"));
        newMeta.displayName(Utils.format("<blue>Custom Crafting"));
        defaultItem.setItemMeta(defMeta);
        newItem.setItemMeta(newMeta);
        gui.setItem(12, defaultItem);
        gui.setItem(14, newItem);
        player.openInventory(gui);
    }

    public static class CraftingInventory implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class SwitchInventory implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

}
