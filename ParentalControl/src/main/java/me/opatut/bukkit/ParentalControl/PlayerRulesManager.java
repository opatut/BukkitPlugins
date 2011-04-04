package me.opatut.bukkit.ParentalControl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.util.config.Configuration;

public class PlayerRulesManager {
	public PlayerRulesManager(ParentalControl plugin) {
		mPlugin = plugin;
		mPlayerRules = new HashMap<String, List<String>>();
		mRules = new HashMap<String, Rule>();
		mPlayerRuleBudget = new HashMap<String, Integer>();
		mPlayerOnlineSince = new HashMap<String, Integer>();
	}
			
	public void Load(Configuration c) {
		List<String> rules = c.getKeys("rules");
		if(rules != null) {
			for(String rule: rules) {
				Rule r = Rule.ReadFromConfiguration(c, rule);
				mRules.put(rule, r);
			}
		}
		
		List<String> players = c.getKeys("players");
		if(players != null) {
			for(String player: players) {
				List<String> prules = c.getStringList("players."+player, null);
				for(String rule: prules) {
					AddPlayerRule(player, rule);
				}
			}
		}
		
		LoadBudgets();
	}
	
	public void LoadBudgets() {
		Configuration c = new Configuration(new File(mPlugin.getDataFolder(), "budgets.yml"));
		c.load();
		List<String> players = c.getKeys(null);
		if(players == null)
			return;
		for(String player: players) {
			List<String> rules = c.getKeys(player);
			if (rules == null) 
				continue;
			for(String rule: rules) {
				String pr = player + "." + rule;
				int b = c.getInt(pr, 0);
				mPlayerRuleBudget.put(pr, b);
			}
		}
	}
	
	public void SaveBudgets() {
		Configuration c = new Configuration(new File(mPlugin.getDataFolder(), "budgets.yml"));
		for(String k: mPlayerRuleBudget.keySet()) {
			c.setProperty(k, mPlayerRuleBudget.get(k));
		}
		c.save();
	}
	
	public boolean CanPlayerBeOnlineNow(String player) {
		if(!mPlayerRules.containsKey(player))
			return true;
		for(String r: mPlayerRules.get(player)) {
			if(mRules.containsKey(r) && !mRules.get(r).CanPlayerBeOnlineNow(player)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean AddPlayerRule(String player, String rule) {
		if(! mPlayerRules.containsKey(player)) {
			mPlayerRules.put(player, new ArrayList<String>());
		}
		if(mRules.containsKey(rule)) {
			mPlayerRules.get(player).add(rule);
			return true;
		}
		return false;
	}
	public void RemovePlayerRule(String player, String rule) {
		if(! mPlayerRules.containsKey(player)) 
			return;
		if(! mPlayerRules.get(player).contains(rule))
			return;
		mPlayerRules.get(player).remove(rule);
		if(mPlayerRules.get(player).size() == 0)
			mPlayerRules.remove(player);
	}
	
	public HashMap<String, List<String>> mPlayerRules;
	public HashMap<String, Rule> mRules;
	public HashMap<String, Integer> mPlayerRuleBudget;
	public HashMap<String, Integer> mPlayerOnlineSince;
	
	ParentalControl mPlugin;
}
