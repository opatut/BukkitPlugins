package me.opatut.bukkit.ParentalControl;

import java.util.HashMap;

import org.bukkit.util.config.Configuration;

public class OnlineTimeRule extends Rule {
	public void ReadFromFile(Configuration c, String name) {
		super.ReadFromFile(c, name);
		String path = "rules." + name;
		mKeepRemainder = c.getBoolean(path + ".keep_remainder", false);
		mMinutesPerDay = c.getInt(path + ".minutes_per_day", 0);
		mPreventLoginIfLessThan = c.getInt(path + ".prevent_login_if_less_than", 0);
		mPreventLoginMessage = c.getString(path + ".prevent_login_message", "You have less than %n minutes left today. You cannot play now.");
	}
	
	public boolean CanPlayerBeOnlineNow(String player) {
		HashMap<String, Integer> m = ParentalControl.mPlayerRulesManager.mPlayerRuleBudget;
		String pr = player + "." + mName;
		if(m.containsKey(pr)) {
			int budget_on_start = m.get(pr);
			int time_online_now = 0;
			if(ParentalControl.mPlayerRulesManager.mPlayerOnlineSince.containsKey(player)) {
				time_online_now = ParentalControl.GetTime() 
					- ParentalControl.mPlayerRulesManager.mPlayerOnlineSince.get(player);
			}
			int new_budget = budget_on_start - time_online_now;
			return new_budget > 0;
		}
		return true;
	}
	
	public int mMinutesPerDay = 60;
	public boolean mKeepRemainder = false;
	public int mPreventLoginIfLessThan = 0;
	public String mPreventLoginMessage = "You have less than %n minutes left today. You cannot play now.";
}

