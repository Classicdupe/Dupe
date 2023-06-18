package xyz.prorickey.classicdupe.events;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
public class FixArmorTrimCrafting implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onArmorTrim(CraftItemEvent event){
        ItemStack itemStack = event.getRecipe().getResult();
        // Netherite template isn't registered as ARMOR_TRIM but as _UPGRADE_SMITHING_TEMPLATE
        // Checking the name is enough because it would be over 20 lines long for no reason
        if (itemStack.getType().name().toUpperCase().endsWith("ARMOR_TRIM_SMITHING_TEMPLATE")) {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);
        }
    }
}