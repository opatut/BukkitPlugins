package me.opatut.bukkit.ParentalControl;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.util.config.Configuration;

public class TimeRule extends Rule {
	public void ReadFromFile(Configuration c, String name) {
		super.ReadFromFile(c, name);
		String path = "rules." + name;
		mStartTime = StringToTime(c.getString(path + ".start"));
		mEndTime = StringToTime(c.getString(path + ".end"));
	}
	
	public boolean CanPlayerBeOnlineNow(String player) {
		int mins = ParentalControl.GetTime();
		boolean inbetween = mins > mStartTime && mins < mEndTime;
		
		if(mStartTime > mEndTime) {
			// start and end are on different days / midnight is inbetween
			inbetween = !inbetween;
		} 
		return !inbetween;
	}
	
	/*public void SaveToFile(Configuration c) {
		super.SaveToFile(c);
		c.setProperty(arg0, arg1)
	}*/
	
	public int mStartTime = 0;
	public int mEndTime = 24*60; // end of day
}
