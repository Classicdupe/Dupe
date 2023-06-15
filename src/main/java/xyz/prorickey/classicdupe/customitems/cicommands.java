package xyz.prorickey.classicdupe.customitems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class cicommands implements CommandExecutor {

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

                                ItemStack item = returnItem(ChatColor.DARK_PURPLE+ChatColor.BOLD.toString()+"Fireball Wand", lore, "stick");
                                Player plr = (Player) sender;
                                plr.getInventory().addItem(item);
                                sender.sendMessage(ChatColor.GREEN + "Gave " + plr.getName() + " a Fireball Wand!");
                                return true;


                            default:
                                sender.sendMessage(ChatColor.RED + "Usage: /customitem give <name> <player>");
                                return false;
                        }


                    case "list":
                        List<String> items = null;
                        items.add("FBwand -- FireBall Wand");

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

    public ItemStack returnItem(String name, List<String> lore, String item) {

        //Make a giveable item with the name and lore
        if (item == "stick") {
            //create a stick with the name and lore
            ItemStack itemStack = new ItemStack(Material.STICK); // Replace with the desired material


            ItemMeta itemMeta = itemStack.getItemMeta();
            // Set the display name
            itemMeta.setDisplayName(name);
            itemMeta.setLore(lore);

            itemStack.setItemMeta(itemMeta);

            return itemStack;


        }

        return null;
    }
}
