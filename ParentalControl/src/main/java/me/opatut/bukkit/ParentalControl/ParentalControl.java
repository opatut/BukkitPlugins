package me.opatut.bukkit.ParentalControl;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import me.opatut.bukkit.ParentalControl.Timer.TimerCallback;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ParentalControl extends JavaPlugin implements TimerCallback {
	public ParentalControl() {
		mPlayerRulesManager = new PlayerRulesManager(this);
		mLastMinuteDay = GetNowDay();
	}
	
	public void tick(int interval) {
		MinuteTimer();
	}
	
	public void MinuteTimer() {
		System.out.println("tick!");
		mPlayerRulesManager.MinuteTimer();
		if (mLastMinuteDay != GetNowDay()) {
			// a new day has started :D reward every player with the new online time
			mPlayerRulesManager.NewDay();
		}
		mLastMinuteDay = GetNowDay();
	}
	
	public void Load(Configuration c) {
		WARNING_MESSAGE = c.getString("warning.message", WARNING_MESSAGE);
		String intervals = c.getString("warning.intervals", "20,10,5,2,1");
		WARNING_INTERVALS = new ArrayList<Integer>();
		for(String s: intervals.split("[^0-9]")) {
			int i = Integer.parseInt(s, 0);
			if(i != 0)
				WARNING_INTERVALS.add(i);
		}
		WARNING_ENABLED = c.getBoolean("warning.enabled", WARNING_ENABLED);
	}
	
	public void Save() {
		Configuration c = new Configuration(new File(getDataFolder(), "config.yml"));
		c.setProperty("warning.message", WARNING_MESSAGE);
		String tmp = "";
		for(int i: WARNING_INTERVALS)
			tmp += i + ",";
		c.setProperty("warning.intervals", tmp);
		c.setProperty("warning.enabled", WARNING_ENABLED);
		c.save();
	}
	
	public void onDisable() {
		mPlayerRulesManager.SaveBudgets();
		Save();
	}

	public void onEnable() {
		Configuration c = new Configuration(new File(getDataFolder(), "config.yml"));
		c.load();
		Load(c);
		mPlayerRulesManager.Load(c);
		
		PluginManager pm = getServer().getPluginManager();
		MainPlayerListener mpl = new MainPlayerListener(this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, mpl, Priority.Highest, this);
		
		Logger.getLogger("Minecraft").info("[" + getDescription().getName() + "] v" + getDescription().getVersion() + " enabled.");
		
		mTimer = new Timer(10000, this);
	}
	
	public void WarnAboutBudget(String player, int new_budget) {
		if(!WARNING_ENABLED)
			return;
		
		if(!WARNING_INTERVALS.contains(new_budget))
			return;
		
		for(String line: WARNING_MESSAGE.split("\\\\n")) {
			line = line.replaceAll("%n", Integer.toString(new_budget));
			line = line.replaceAll("%p", player);
			line = line.replaceAll("%c", ChatColor.RED.toString());
			getServer().getPlayer(player).sendMessage(line);
		}
	}
	
	public static int GetTime() {
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
	}
	public int GetNowDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
	}
	
	public static PlayerRulesManager mPlayerRulesManager;
	public int mLastMinuteDay;
	private Timer mTimer;
	
	public static List<Integer> WARNING_INTERVALS = new ArrayList<Integer>();
	public static String WARNING_MESSAGE = "[ParentalControl] You have %n minutes left to play.";
	public static boolean WARNING_ENABLED = true;
	
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
