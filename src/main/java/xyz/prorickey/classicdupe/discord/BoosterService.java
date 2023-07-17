package xyz.prorickey.classicdupe.discord;

import net.luckperms.api.node.Node;
import org.antlr.v4.runtime.misc.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class BoosterService implements Listener {

    //Array of Item Stacks of Armor Trims
    public static ItemStack[] armorTrims = {
            new ItemStack(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, 2),
            new ItemStack(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, 2),
            new ItemStack(Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, 2),
            new ItemStack(Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, 2),
            new ItemStack(Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,2),
            new ItemStack(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, 2),
            new ItemStack(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 2),
            new ItemStack(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, 2),
    };

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        if (isPlayerInGroup(player, "booster")) {
            if (!LinkRewards.checkRewardsForBoosting(player)) {
                //REMOVE BOOSTER GROUP
                ClassicDupe
                        .getLPAPI().getUserManager()
                        .getUser(player.getUniqueId()).data().remove(
                                Node.builder("group.booster").build()
                        );

            }

        }

        // Get plugin folder
        Path dataFolder = ClassicDupe.getPlugin().getDataFolder().toPath();

        //check if it exists
        if (!dataFolder.toFile().exists()) {
            //if not, create it
            dataFolder.toFile().mkdirs();
        }

        Path dataFile = Path.of(ClassicDupe.getPlugin().getDataFolder().toPath() + "/boosterdata.txt");

        if (!dataFile.toFile().exists()) {
            //Create the file
            try {
                dataFile.toFile().createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }

        if (LinkRewards.checkRewardsForBoosting(player)) {
            //for lines in file
            File file = new File(ClassicDupe.getPlugin().getDataFolder().toPath() + "/boosterdata.txt");

            //read file
            try {
                String[] lines = Utils.readFile(String.valueOf(file.toPath())).toString().split("\n");
                boolean found = false;
                for (String line : lines) {
                    if (line == player.getUniqueId().toString()) {
                        found = true;
                    }
                }

                if (found == false) {
                    ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId()).balance += 250;
                    ClassicDupe.getPVDatabase().addVault(String.valueOf(player.getUniqueId()));

                    //Generate a random number between 0 and length of array
                    int random = (int) (Math.random() * armorTrims.length);

                    //Check if the player has room in their inventory
                    if (player.getInventory().firstEmpty() != -1) {
                        //Give the player the armor trim
                        player.getInventory().addItem(armorTrims[random]);
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "----------------------------------------");
                        player.sendMessage(ChatColor.RED + "You do not have enough room in your inventory to receive your Discord Booster gift! Please open a Discord ticket on the ClassicDupe discord server to claim your armor trims!");
                        player.sendMessage(ChatColor.DARK_RED + "----------------------------------------");
                    }


                    //add the uuid to the file
                    Utils.writeFile(String.valueOf(file.toPath()), player.getUniqueId().toString() + "\n");
                }
            } catch (IOException ex) {
                System.out.println("Error reading file");
                System.out.println(ex);
                System.out.println("Booster Service failed to read file");
            }

        }




    }


    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

}
