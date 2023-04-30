package xyz.prorickey.classicdupe.commands.perk;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.*;

public class KillEffectsCMD implements CommandExecutor, TabCompleter, Listener {

    private static final Map<Player, Inventory> guis = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }

        MiniMessage mm = MiniMessage.miniMessage();
        Inventory gui = Bukkit.createInventory(null, 54, mm.deserialize("<gradient:#FF4646:#FF2B2B>Kill Effects</gradient>"));
        ItemStack blankSlot = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        blankSlot.editMeta(meta -> meta.displayName(Component.text(" ")));
        for(int i = 0; i < 54; i++) gui.setItem(i, blankSlot);

        String activeKillEffect = ClassicDupe.getDatabase().getPlayerDatabase().getKillEffect(p.getUniqueId());

        // Toggle
        ItemStack toggle = new ItemStack(Material.NETHER_STAR);
        toggle.editMeta(meta -> {
            if(!Objects.equals(activeKillEffect, "none")) {
                meta.displayName(mm.deserialize("<gradient:#FF4646:#FF2B2B>Toggle Off</gradient>"));
                meta.lore(List.of(
                        Utils.format("<yellow>Current Kill Effect: " + activeKillEffect))
                );
            }
            else meta.displayName(mm.deserialize("<gradient:#2FD83F:#15D927>Toggle Off</gradient>"));
        });
        gui.setItem(49, toggle);

        // Tier 1 Kill effects
        ItemStack noteblockrunup = new ItemStack(Material.NOTE_BLOCK);
        noteblockrunup.editMeta(meta -> {
            if(activeKillEffect.equalsIgnoreCase("noteblockrunup")) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.displayName(mm.deserialize("<gradient:#FA54FF:#F923FF>Noteblock Run Up</gradient>"));
            meta.lore(List.of(
                    Utils.format("<yellow>When a player is killed, everyone in a"),
                    Utils.format("<yellow>25 block radius hears a short noteblock"),
                    Utils.format("<yellow>tune"))
            );
        });
        gui.setItem(3, noteblockrunup);

        ItemStack flames = new ItemStack(Material.CAMPFIRE);
        flames.editMeta(meta -> {
            if(activeKillEffect.equalsIgnoreCase("flames")) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.displayName(mm.deserialize("<gradient:#FF7A23:#FF5323>Flames</gradient>"));
            meta.lore(List.of(
                    Utils.format("<yellow>When a player is killed, flames appear"),
                    Utils.format("<yellow>around where the victim was")
            ));
        });
        gui.setItem(4, flames);

        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        firework.editMeta(meta -> {
            if(activeKillEffect.equalsIgnoreCase("firework")) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.displayName(mm.deserialize("<gradient:#F3FF23:#F5FF4A>Flames</gradient>"));
            meta.lore(List.of(
                    Utils.format("<yellow>When a player is killed, a firework"),
                    Utils.format("<yellow>is launched from where they were")
            ));
        });
        gui.setItem(5, firework);

        p.openInventory(gui);
        guis.put(p, gui);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(guis.get(e.getWhoClicked().getUniqueId().toString()))) return;
        if(e.getRawSlot() == 49) {
            // TODO: Shennanigans
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(e.getPlayer().getKiller() == null) return;
        Player killer = e.getEntity().getKiller();
        String activeKillEffect = ClassicDupe.getDatabase().getPlayerDatabase().getKillEffect(killer.getUniqueId());
        if(activeKillEffect.equalsIgnoreCase("none")) return;
        switch(activeKillEffect) {
            case "noteblockrunup" -> {
                killer.getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.5F);
                for(int i = 1; i < 15; i++) {
                    int finalI = i;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(ClassicDupe.getPlugin(), () -> killer.getWorld().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, Float.parseFloat(String.valueOf(0.5+(0.1* finalI)))), 5*i);
                }
            }
            case "flames" -> {
                killer.getWorld().playEffect(e.getPlayer().getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                new ParticleBuilder(ParticleEffect.FLAME, e.getPlayer().getLocation())
                        .setOffsetY(1F)
                        .setAmount(20)
                        .setSpeed(0.1f)
                        .display();
            }
            case "firework" -> {
                Firework fw = e.getPlayer().getWorld().spawn(e.getPlayer().getLocation(), Firework.class);
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .with(FireworkEffect.Type.BURST)
                        .withColor(Color.RED)
                        .build());
                fw.setFireworkMeta(meta);
                fw.detonate();
            }
        }
    }

}
