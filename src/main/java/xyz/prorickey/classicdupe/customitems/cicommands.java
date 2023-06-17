package xyz.prorickey.classicdupe.customitems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class cicommands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().toString().toLowerCase().equals("customitem")) {

            if (sender.hasPermission("mod.customitem")) {

                if (args.length == 0) {
                    sender.sendMessage(ChatColor.YELLOW + "Usage: /customitem <give|list>");
                    return false;
                }


                switch (args[0]) {
                    case "give":
                        String name = args[1];

                        switch (name) {
                            case "FBwand":
                                List<String> lore = new ArrayList<>();
                                lore.add("A wand that shoots fireballs");
                                lore.add("Cooldown of 10 seconds");

                                ItemStack item = returnItem(ChatColor.DARK_PURPLE+ChatColor.BOLD.toString()+"Fireball Wand", lore, "stick", CIKeys.FBWAND);
                                Player plr = (Player) sender;
                                plr.getInventory().addItem(item);
                                sender.sendMessage(ChatColor.GREEN + "Gave " + plr.getName() + " a Fireball Wand!");
                                return true;
                            case "burstbow":
                                List<String> lore2 = new ArrayList<>();
                                lore2.add("A bow that shoots 3 arrows at once");
                                lore2.add("Cooldown of 20 seconds");

                                ItemStack item2 = returnItem(ChatColor.DARK_PURPLE+ChatColor.BOLD.toString()+"Burst Bow", lore2, "bow", CIKeys.BURSTBOW);
                                Player plr2 = (Player) sender;
                                plr2.getInventory().addItem(item2);
                                sender.sendMessage(ChatColor.GREEN + "Gave " + plr2.getName() + " a Burst Bow!");
                                return true;
                            case "pvppot":
                                List<String> lore3 = new ArrayList<>();
                                lore3.add("Gives you the basic pvp's essential buffs!");
                                lore3.add("Speed 2, Strength 2, Duration: 1:30");
                                lore3.add("Cooldown of 2 minutes.");

                                ItemStack item3 = returnItem(ChatColor.DARK_PURPLE+ChatColor.BOLD.toString()+"PVP Pot Lvl 1.", lore3, "star", CIKeys.PVPPOT);
                                Player plr3 = (Player) sender;
                                plr3.getInventory().addItem(item3);
                                sender.sendMessage(ChatColor.GREEN + "Gave " + plr3.getName() + " a PVP Pot Lvl 1!");
                                return true;
                            case "pvppot2":
                                List<String> lore4 = new ArrayList<>();
                                lore4.add("Gives you amazing pvp buffs!");
                                lore4.add("Speed 2, Strength 2, Regeneration 2, Duration: 2:00");
                                lore4.add("Cooldown of 3 minutes.");

                                ItemStack item4 = returnItem(ChatColor.DARK_PURPLE+ChatColor.BOLD.toString()+"PVP Pot Lvl 2.", lore4, "star", CIKeys.PVPPOT2);
                                Player plr4 = (Player) sender;
                                plr4.getInventory().addItem(item4);
                                sender.sendMessage(ChatColor.GREEN + "Gave " + plr4.getName() + " a PVP Pot Lvl 2!");
                                return true;


                            default:
                                sender.sendMessage(ChatColor.RED + "Usage: /customitem give <name> <player>");
                                return false;
                        }


                    case "list":
                        List<String> items = null;
                        items.add("FBwand -- FireBall Wand");
                        items.add("BurstBow -- Bow that shoots 3 arrows at once");

                        sender.sendMessage(ChatColor.GOLD + "-------------------------");
                        sender.sendMessage(ChatColor.GOLD + "Custom Items:");
                        for (String item : items) {
                            sender.sendMessage(ChatColor.YELLOW + item);
                        }
                        sender.sendMessage(ChatColor.GOLD + "-------------------------");
                        return true;

                    default:
                        sender.sendMessage(ChatColor.RED + "Usage: /customitem <give|list>");
                        return false;
                } // waiting for upload
                //waiting for server start cuz /reload no worky


            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
        }





        return false;
    }


    public ItemStack returnItem(String name, List<String> lore, String item, NamespacedKey key) {
        // Make a giveable item with the name and lore
        if (item.equals("stick")) {
            // Create a stick with the name and lore
            ItemStack itemStack = new ItemStack(Material.STICK);

            ItemMeta itemMeta = itemStack.getItemMeta();

            // Set the display name
            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);

            // Get the PersistentDataContainer for the item
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

            // Set the PDC value to "FBwand"
            dataContainer.set(key, PersistentDataType.STRING, "CustomStick");



            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        if (item.equals("bow")) {
            // Create a stick with the name and lore
            ItemStack itemStack = new ItemStack(Material.BOW);

            ItemMeta itemMeta = itemStack.getItemMeta();

            // Set the display name
            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);

            // Get the PersistentDataContainer for the item
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

            // Set the PDC value to "FBwand"
            dataContainer.set(key, PersistentDataType.STRING, "CustomBow");



            itemStack.setItemMeta(itemMeta);

            return itemStack;
        }

        if (item.equals("star")) {

            ItemStack itemStack = new ItemStack(Material.NETHER_STAR);

            ItemMeta itemMeta = itemStack.getItemMeta();

            // Set the display name
            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);

            // Get the PersistentDataContainer for the item
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();

            // Set the PDC value to "FBwand"
            dataContainer.set(key, PersistentDataType.STRING, "CustomBow");



            itemStack.setItemMeta(itemMeta);

            return itemStack;

        }

        return null;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) return TabComplete.tabCompletionsSearch(args[0], List.of("give", "list"));
        else if(args.length == 1 && args[0].equalsIgnoreCase("give")) return TabComplete.tabCompletionsSearch(args[0], List.of("FBwand"));
        else return new ArrayList<>();
    }
}
