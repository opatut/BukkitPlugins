package me.opatut.bukkit.AntiTower;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommandExecutor implements CommandExecutor {
	public MainCommandExecutor(AntiTower plugin) {
		mPlugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("keeptower")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(AntiTower.CAN_KEEP) {
					if(mPlugin.mTowers.containsKey(p.getName())) {
						mPlugin.mTowers.remove(p.getName());
						mPlugin.SendMessage(p, ChatColor.GREEN, AntiTower.KEEP_TOWER_SUCCESS);
					} else {
						mPlugin.SendMessage(p, ChatColor.GREEN, AntiTower.NO_TOWER_TO_KEEP);
					}
				} else {
					mPlugin.SendMessage(p, ChatColor.RED, AntiTower.KEEP_TOWER_DISABLED);
				}
				return true;				
			} 
		} else if(label.equalsIgnoreCase("deletetower")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(AntiTower.CAN_DELETE) {
					if(!mPlugin.RemoveTower(p.getName()))
						mPlugin.SendMessage(p, ChatColor.RED, AntiTower.NO_TOWER_TO_DELETE);
				} else {
					mPlugin.SendMessage(p, ChatColor.RED, AntiTower.DELETE_TOWER_DISABLED);
				}
				return true;
			} 
		}
		
		return false;
	}

	AntiTower mPlugin;
}
