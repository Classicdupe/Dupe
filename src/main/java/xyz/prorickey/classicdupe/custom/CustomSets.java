package xyz.prorickey.classicdupe.custom;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.default1.DupeCMD;

import java.io.File;
import java.util.*;

public class CustomSets {

    private static File customSetsFile;
    private static YamlConfiguration customSetsConfig;
    public static List<CustomSets> sets = new ArrayList<>();
    public static List<NamespacedKey> keys = new ArrayList<>();
    public static Map<NamespacedKey, CustomSets> keysToSets = new HashMap<>();

    public static void init() {
        customSetsFile = new File(ClassicDupe.getPlugin().getDataFolder() + "/sets.yml");
        if(!customSetsFile.exists()) ClassicDupe.getPlugin().saveResource("sets.yml", false);
        reload();
    }

    @SuppressWarnings("ConstantConditions")
    public static void reload() {
        customSetsConfig = YamlConfiguration.loadConfiguration(customSetsFile);
        keys.clear();
        keysToSets.clear();
        sets.clear();
        customSetsConfig.getKeys(false).forEach(key -> {
            CustomSets set = new CustomSets(customSetsConfig.getConfigurationSection(key));
            keys.add(set.key);
            keysToSets.put(set.key, set);
            sets.add(set);
        });
    }


    private final NamespacedKey key;
    private final String name;
    private final String color;
    private final Boolean smithable;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final ItemStack sword;

    public CustomSets(ConfigurationSection section) {
        this.key = new NamespacedKey(ClassicDupe.getPlugin(), section.getName());
        this.name = section.getString("name");
        this.color = section.getString("color");
        this.smithable = section.getBoolean("smithable");

        ItemStack helmet = new ItemStack(Material.valueOf(section.getString("helmet.material")));
        helmet.editMeta(meta -> {
            meta.displayName(Utils.format("<color:" + this.color + ">" + this.name + " Helmet"));
            meta.getPersistentDataContainer().set(this.key, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", section.getInt("helmet.armor"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", section.getInt("helmet.toughness"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockback_resistance", section.getInt("helmet.kbresistance"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        });
        this.helmet = helmet;

        ItemStack chestplate = new ItemStack(Material.valueOf(section.getString("chestplate.material")));
        chestplate.editMeta(meta -> {
            meta.displayName(Utils.format("<color:" + this.color + ">" + this.name + " Chestplate"));
            meta.getPersistentDataContainer().set(this.key, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", section.getInt("chestplate.armor"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", section.getInt("chestplate.toughness"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockback_resistance", section.getInt("chestplate.kbresistance"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        });
        this.chestplate = chestplate;

        ItemStack leggings = new ItemStack(Material.valueOf(section.getString("leggings.material")));
        leggings.editMeta(meta -> {
            meta.displayName(Utils.format("<color:" + this.color + ">" + this.name + " Leggings"));
            meta.getPersistentDataContainer().set(this.key, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", section.getInt("leggings.armor"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", section.getInt("leggings.toughness"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockback_resistance", section.getInt("leggings.kbresistance"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        });
        this.leggings = leggings;

        ItemStack boots = new ItemStack(Material.valueOf(section.getString("boots.material")));
        boots.editMeta(meta -> {
            meta.displayName(Utils.format("<color:" + this.color + ">" + this.name + " Boots"));
            meta.getPersistentDataContainer().set(this.key, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", section.getInt("boots.armor"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armor_toughness", section.getInt("boots.toughness"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
            meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockback_resistance", section.getInt("boots.kbresistance"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        });
        this.boots = boots;

        ItemStack sword = new ItemStack(Material.valueOf(section.getString("sword.material")));
        sword.editMeta(meta -> {
            meta.displayName(Utils.format("<color:" + this.color + ">" + this.name + " Sword"));
            meta.getPersistentDataContainer().set(this.key, PersistentDataType.BOOLEAN, true);
            meta.getPersistentDataContainer().set(DupeCMD.undupableKey, PersistentDataType.BOOLEAN, true);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attack_damage", section.getInt("sword.damage"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.attack_speed", section.getInt("sword.speed"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        });
        this.sword = sword;
    }

    public ItemStack getBoots() { return boots; }
    public ItemStack getChestplate() { return chestplate; }
    public ItemStack getHelmet() { return helmet; }
    public ItemStack getLeggings() { return leggings; }
    public ItemStack getSword() { return sword; }
    public Boolean getSmithable() { return smithable; }
    public String getName() { return name; }
    public String getColor() { return color; }

}
