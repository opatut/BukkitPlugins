package me.opatut.bukkit.ParentalControl;

import org.bukkit.util.config.Configuration;

public abstract class Rule {
	public static Rule ReadFromConfiguration(Configuration c, String name) {
		String path = "rules." + name;
		String type = c.getString(path + ".type", "");
		if(type.equalsIgnoreCase("time")) {
			TimeRule r = new TimeRule();
			r.ReadFromFile(c, name);
			return r;
		} else if(type.equalsIgnoreCase("onlinetime")) {
			OnlineTimeRule r = new OnlineTimeRule();
			r.ReadFromFile(c, name);
			return r;
		} else  {
			System.out.println("Could not find rule type: " + type);
		}
		return null;
	}
	
	public abstract boolean CanPlayerBeOnlineNow(String player);
	
	public void ReadFromFile(Configuration c, String name) {
		String path = "rules." + name;
		mName = name;
		mWarning = c.getBoolean(path + ".warning", true);
		mMessage = c.getString(path + ".message");
	}
	
	/* public void SaveToFile(Configuration c) {
		String path = "rules." + mName;
		c.setProperty(path + ".warning", mWarning);
	} */
	
	public static int StringToTime(String s) {
		int n = 0;
		try {
			n = new Integer(s);
		} catch (NumberFormatException e) {
			// could not read n as a number -> split it
			String[] split = s.split(":");
			if(split.length == 1)
				n = new Integer(split[0]);
			if(split.length == 2)
				n = new Integer(split[0]) * 60 + new Integer(split[1]);
		}
		return n;
	}
	
	public static String TimeToString(int i) {
		int mins = i % 60;
		int hours = (i-mins)%24;
		return hours + ":" + mins;
	}
	
	public String mName;
	public String mMessage = "[ParentalControl] You may not play now.";
	public boolean mWarning = true;
}
