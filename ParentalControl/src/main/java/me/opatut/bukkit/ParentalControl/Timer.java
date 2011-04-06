package me.opatut.bukkit.ParentalControl;

import java.util.TimerTask;

public class Timer extends TimerTask {
	public Timer(int interval, TimerCallback callback) {
		mInterval = interval;
		mCallback = callback;
		Enable();
	}
	
	private void StartTimeout() {
		mTimer = new java.util.Timer();
		mTimer.schedule(this, mInterval, mInterval); // delay in ms
	}
	
	public void Enable() {
		if(!mEnabled) {
			mEnabled = true;
			StartTimeout();
		}
	}
	public void Disable() {
		mEnabled = false;
		mTimer.cancel();
	}
	
	public void run() {
		if(mEnabled) {
			// only do work if enabled
			mCallback.tick(mInterval);
		} else {
			mTimer.cancel();
		}
	}
	
	private int mInterval;
	private TimerCallback mCallback;
	private boolean mEnabled;
	private java.util.Timer mTimer;
	
	public interface TimerCallback {
		public void tick(int interval);
	}

}
