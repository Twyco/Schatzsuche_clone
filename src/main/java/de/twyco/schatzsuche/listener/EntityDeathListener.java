package de.twyco.schatzsuche.listener;

import de.twyco.stegisagt.GameStatus;
import de.twyco.stegisagt.Stegisagt;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event){
        if(Stegisagt.getGameStatus().equals(GameStatus.PLAYING_SCHATZSUCHE)){
            if(event.getEntity() instanceof Shulker){
                event.setDroppedExp(0);
                event.getDrops().clear();
            }
        }
    }
}
