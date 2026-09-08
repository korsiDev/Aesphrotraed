package me.korsidev.aesphrotraed.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkCleanupEvent implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Look through every entity currently loading inside this chunk
        for (Entity entity : event.getChunk().getEntities()) {
            // Check if it's a TextDisplay
            if (entity instanceof TextDisplay textDisplay) {

                // If it doesn't have a vehicle (a player riding underneath it), 
                // it is an orphaned zombie display from a prior server crash!
                if (textDisplay.getVehicle() == null) {
                    textDisplay.remove();
                }
            }
        }
    }
}