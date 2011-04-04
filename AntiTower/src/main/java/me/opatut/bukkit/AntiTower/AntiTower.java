package me.opatut.bukkit.AntiTower;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class AntiTower extends JavaPlugin {
	public AntiTower() {
		mTowers = new HashMap<String, Location>();
	}
	
	public File GetConfigFile() {
		return new File(getDataFolder(), "config.yml");
	}
	public void Load() {
		Configuration c = new Configuration(GetConfigFile());
		c.load();
		MIN_TOWER_HEIGHT = c.getInt("config.min_tower_height", MIN_TOWER_HEIGHT);
		SAME_MATERIAL = c.getBoolean("config.only_same_material", SAME_MATERIAL);
		CAN_KEEP = c.getBoolean("config.can_keep", CAN_KEEP);
		CAN_DELETE = c.getBoolean("config.can_delete", CAN_DELETE);
		
		TOWER_BUILDING = c.getString("messages.tower_building", TOWER_BUILDING);
		KEEP_COMMAND_INFO = c.getString("messages.keep_command_info", KEEP_COMMAND_INFO);
		TOWER_REMOVED = c.getString("messages.tower_removed", TOWER_REMOVED);
		NO_TOWER_TO_KEEP = c.getString("messages.no_tower_to_keep", NO_TOWER_TO_KEEP);
		NO_TOWER_TO_DELETE = c.getString("messages.no_tower_to_delete", NO_TOWER_TO_DELETE);
		KEEP_TOWER_SUCCESS = c.getString("messages.keep_tower_success", KEEP_TOWER_SUCCESS);
		KEEP_TOWER_DISABLED = c.getString("messages.keep_tower_disabled", KEEP_TOWER_DISABLED);
		DELETE_TOWER_DISABLED = c.getString("messages.delete_tower_disabled", DELETE_TOWER_DISABLED);
		
	}
	public void Save() {
		Configuration c = new Configuration(GetConfigFile());
		c.setProperty("config.min_tower_height", MIN_TOWER_HEIGHT);
		c.setProperty("config.only_same_material", SAME_MATERIAL);
		c.setProperty("config.can_keep", CAN_KEEP);
		c.setProperty("config.can_delete", CAN_DELETE);
		
		c.setProperty("messages.tower_building", TOWER_BUILDING);
		c.setProperty("messages.keep_command_info", KEEP_COMMAND_INFO);
		c.setProperty("messages.tower_removed", TOWER_REMOVED);
		c.setProperty("messages.no_tower_to_keep", NO_TOWER_TO_KEEP);
		c.setProperty("messages.no_tower_to_delete", NO_TOWER_TO_DELETE);
		c.setProperty("messages.keep_tower_success", KEEP_TOWER_SUCCESS);
		c.setProperty("messages.keep_tower_disabled", KEEP_TOWER_DISABLED);
		c.setProperty("messages.delete_tower_disabled", DELETE_TOWER_DISABLED);
		c.save();
	}
	
	public void onDisable() {
		Save();
	}

	public void onEnable() {
		Load(); 
		
		PluginManager pm = getServer().getPluginManager();
		
		MainBlockListener mbl = new MainBlockListener(this);
		MainCommandExecutor mce = new MainCommandExecutor(this);
		
		pm.registerEvent(Event.Type.BLOCK_PLACE, mbl, Priority.Normal, this);
		getCommand("keeptower").setExecutor(mce);
		getCommand("deletetower").setExecutor(mce);
		
		PluginDescriptionFile desc = getDescription();
		Logger.getLogger("Minecraft").info("["+desc.getName() + "] v" + desc.getVersion() + " enabled.");
	}
	
	public void CheckForTower(Block block, Player player) {
		String pn = player.getName();
		if(mTowers.containsKey(pn) && 
				(mTowers.get(pn).getBlockX() != block.getX() ||
						mTowers.get(pn).getBlockZ() != block.getZ())) {
			// the block is not on the recently built tower -> remove it
			RemoveTower(pn);
		}
		
		int h = GetTowerHeightBelowBlock(block);
		if(h >= MIN_TOWER_HEIGHT) {
			if(!mTowers.containsKey(pn)) {
				SendMessage(player, ChatColor.RED, TOWER_BUILDING);
				if(CAN_KEEP)
					SendMessage(player, ChatColor.RED, KEEP_COMMAND_INFO);
			}
			Location loc = new Location(block.getWorld(), block.getX(), block.getY() - h + 1, block.getZ());
			mTowers.put(pn, loc);
		}
	}
	
	public boolean RemoveTower(String pn) {
		if(!mTowers.containsKey(pn))
			return false;
		Location lowest = mTowers.get(pn);
		Material mat = lowest.getWorld().getBlockAt(lowest).getType();
		for(;lowest.getWorld().getBlockAt(lowest).getType() == mat || 
				(!SAME_MATERIAL && lowest.getWorld().getBlockAt(lowest).getType() != Material.AIR); 
				lowest.setY(lowest.getY() + 1)) {
			lowest.getWorld().getBlockAt(lowest).setType(Material.AIR);
		}
		mTowers.remove(pn);
		SendMessage(getServer().getPlayer(pn), ChatColor.RED, TOWER_REMOVED);
		return true;
	}
	
	public int GetTowerHeightBelowBlock(Block block) {
		int height = 0;
		for(int y = block.getY(); y >= 0; --y) {
			Block nb = block.getWorld().getBlockAt(block.getX(), y, block.getZ());
			if( !BlockHasNoNeighbors(nb)) {
				// we struck the first block without neighbors
				break;
			} else if(block.getType() != nb.getType() && SAME_MATERIAL) {
				// different material
				break;
			} else {
				height++;
			}
		}
		return height;
	}
	
	public boolean BlockHasNoNeighbors(Block block) {
		World w = block.getWorld();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		return (w.getBlockAt(x-1,y,z+0).getType() == Material.AIR &&
				w.getBlockAt(x+0,y,z-1).getType() == Material.AIR && 
				w.getBlockAt(x+1,y,z+0).getType() == Material.AIR &&
				w.getBlockAt(x+0,y,z+1).getType() == Material.AIR);
	}
	
	public void SendMessage(Player player, ChatColor color, String message) {
		String[] split = message.split("\\\\n");
		for(String s: split) {
			if(s.length() == 0)
				continue;
			s = s.replaceAll("%c", color.toString());
			player.sendMessage(color + s);
		}
	}

	public HashMap<String, Location> mTowers; // the location is the lowest block of the tower
	public static int MIN_TOWER_HEIGHT = 3;
	public static boolean SAME_MATERIAL = false;
	public static boolean CAN_KEEP = true;
	public static boolean CAN_DELETE = true;
	
	public static String TOWER_BUILDING = "* AntiTower has noticed you are building a 1x1 tower.\\n* It will be removed when you place a block somewhere else.";
	public static String KEEP_COMMAND_INFO = "* Type §f/keeptower%c when finished to keep it.";
	public static String TOWER_REMOVED = "Your tower has been removed.";
	public static String NO_TOWER_TO_KEEP= "You haven't built a tower recently.";
	public static String NO_TOWER_TO_DELETE = "You haven't built a tower recently.";
	public static String KEEP_TOWER_SUCCESS = "You can keep what you built so far.";
	public static String KEEP_TOWER_DISABLED = "You are not allowed to keep your tower. Sorry.";
	public static String DELETE_TOWER_DISABLED = "You are not allowed to delete your own tower on command. Sorry.";
	
}
