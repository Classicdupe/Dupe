package xyz.prorickey.classicdupe.events;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntitySpawnEvent implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if(e.getEntityType().equals(EntityType.ENDER_DRAGON) || e.getEntityType().equals(EntityType.WITHER)) e.setCancelled(true);
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) e.setCancelled(true);
    }

}