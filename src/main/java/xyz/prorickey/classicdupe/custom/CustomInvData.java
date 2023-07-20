package xyz.prorickey.classicdupe.custom;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomInvData implements InventoryHolder {
    public String uuid;
    public String type = "pvsee";
    public int vault;


    public CustomInvData(String uuid, int vault) {
        this.uuid = uuid;
        this.vault = vault;

    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
