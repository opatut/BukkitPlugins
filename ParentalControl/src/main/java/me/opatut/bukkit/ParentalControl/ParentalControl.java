package me.opatut.bukkit.ParentalControl;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ParentalControl extends JavaPlugin {
	public void onDisable() {
		
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		
		MainPlayerListener mpl = new MainPlayerListener(this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, mpl, Priority.Highest, this);
	}
	
	class MainPlayerListener extends PlayerListener {
		public MainPlayerListener(ParentalControl plugin) {
			mPlugin = plugin;
		}
		
		public void onPlayerLogin(PlayerLoginEvent event) {
			Player p = event.getPlayer();
			event.disallow(Result.KICK_OTHER, "You cannot join now (ParentalControl).");
		}
		
		ParentalControl mPlugin;
	}
	
}
