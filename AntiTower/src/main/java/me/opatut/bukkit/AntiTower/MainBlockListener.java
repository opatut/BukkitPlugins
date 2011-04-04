package me.opatut.bukkit.AntiTower;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class MainBlockListener extends BlockListener {
	public MainBlockListener(AntiTower plugin) {
		mPlugin = plugin;
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		mPlugin.CheckForTower(event.getBlockPlaced(), event.getPlayer());
	}
	
	AntiTower mPlugin;
}
