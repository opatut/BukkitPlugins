package me.opatut.bukkit.ParentalControl;

import java.io.File;
import java.util.Calendar;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ParentalControl extends JavaPlugin {
	public ParentalControl() {
		mPlayerRulesManager = new PlayerRulesManager(this);
	}
	
	public void onDisable() {
		
	}

	public void onEnable() {
		Configuration c = new Configuration(new File(getDataFolder(), "config.yml"));
		c.load();
		mPlayerRulesManager.Load(c);
		
		PluginManager pm = getServer().getPluginManager();
		MainPlayerListener mpl = new MainPlayerListener(this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, mpl, Priority.Highest, this);
		
		Logger.getLogger("Minecraft").info("[" + getDescription().getName() + "] v" + getDescription().getVersion() + " enabled.");
	}
	
	public static int GetTime() {
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
	}
	
	public static PlayerRulesManager mPlayerRulesManager;
	
	class MainPlayerListener extends PlayerListener {
		public MainPlayerListener(ParentalControl plugin) {
			mPlugin = plugin;
		}
		
		public void onPlayerLogin(PlayerLoginEvent event) {
			Player p = event.getPlayer();
			ParentalControl.mPlayerRulesManager.mPlayerOnlineSince.put(p.getName(), ParentalControl.GetTime());
			if(! ParentalControl.mPlayerRulesManager.CanPlayerBeOnlineNow(p.getName()))
				event.disallow(Result.KICK_OTHER, "You cannot join now (ParentalControl).");
		}
		
		ParentalControl mPlugin;
	}
	
}
