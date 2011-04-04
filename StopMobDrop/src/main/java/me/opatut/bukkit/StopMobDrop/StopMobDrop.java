package me.opatut.bukkit.StopMobDrop;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class StopMobDrop extends JavaPlugin {
	public void onDisable() {
		
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		MainEntityListener mel = new MainEntityListener();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, mel, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, mel, Priority.Normal, this);
		
		Logger.getLogger("Minecraft").info("["+getDescription().getName() + "] v" 
				+ getDescription().getVersion() +" enabled.");
	}
	
	class MainEntityListener extends EntityListener {
		public MainEntityListener() {
			mEntitiesWithoutDrop = new ArrayList<Integer>();
		}
		
		@Override
		public void onEntityDamage(EntityDamageEvent event) {
			if(!(event.getEntity() instanceof Creature))
				return;
			Creature c = (Creature)event.getEntity();
			
			if(c.getHealth() - event.getDamage() <= 0) {
				// evil entity will die
				int id = c.getEntityId();
				mEntitiesWithoutDrop.add(id);
				if(event instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
					if(e.getDamager() instanceof Player) {
						mEntitiesWithoutDrop.remove(new Integer(id));
					}
				}
				if(event instanceof EntityDamageByProjectileEvent) {
					EntityDamageByProjectileEvent e = (EntityDamageByProjectileEvent) event;
					if(e.getDamager() instanceof Player) {
						mEntitiesWithoutDrop.remove(new Integer(id));
					}
				}
			}
		}
		
		@Override
		public void onEntityDeath(EntityDeathEvent event) {
			int id = event.getEntity().getEntityId();
			if(mEntitiesWithoutDrop.contains(id)) {
				mEntitiesWithoutDrop.remove(new Integer(id));
				event.getDrops().clear();
			}
		}
		
		List<Integer> mEntitiesWithoutDrop;
		
	}
}

