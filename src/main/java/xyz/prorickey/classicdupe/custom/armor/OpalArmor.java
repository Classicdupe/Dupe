package xyz.prorickey.classicdupe.custom.armor;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.default1.DupeCMD;

public class OpalArmor implements Listener {

    private static NamespacedKey opalKey = new NamespacedKey(ClassicDupe.getPlugin(), "opalArmor");
    public static NamespacedKey getOpalKey() { return opalKey; }

    public static ItemStack getOpalHelmet() {
        ItemStack opalHelmet = new ItemStack(Material.DIAMOND_HELMET);
        opalHelmet.editMeta(meta -> {
            meta.displayName(Utils.format("<color:#0CB0F6>Opal Helmet"));
            meta.getPersistentDataContainer().set(opalKey, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", 1, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier("generic.armor_toughness", 1, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier("generic.knockback_resistance", 1, AttributeModifier.Operation.ADD_NUMBER));
        });
        return opalHelmet;
    }

    public static ItemStack getOpalChestplate() {
        ItemStack opalChestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        opalChestplate.editMeta(meta -> {
            meta.displayName(Utils.format("<color:#0CB0F6>Opal Chestplate"));
            meta.getPersistentDataContainer().set(opalKey, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", 3, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier("generic.armor_toughness", 1, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier("generic.knockback_resistance", 1, AttributeModifier.Operation.ADD_NUMBER));
        });
        return opalChestplate;
    }

    public static ItemStack getOpalLeggings() {
        ItemStack opalLeggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        opalLeggings.editMeta(meta -> {
            meta.displayName(Utils.format("<color:#0CB0F6>Opal Leggings"));
            meta.getPersistentDataContainer().set(opalKey, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", 2, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier("generic.armor_toughness", 1, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier("generic.knockback_resistance", 1, AttributeModifier.Operation.ADD_NUMBER));
        });
        return opalLeggings;
    }

    public static ItemStack getOpalBoots() {
        ItemStack opalBoots = new ItemStack(Material.DIAMOND_BOOTS);
        opalBoots.editMeta(meta -> {
            meta.displayName(Utils.format("<color:#0CB0F6>Opal Boots"));
            meta.getPersistentDataContainer().set(opalKey, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", 1, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier("generic.armor_toughness", 1, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier("generic.knockback_resistance", 1, AttributeModifier.Operation.ADD_NUMBER));
        });
        return opalBoots;
    }

}
