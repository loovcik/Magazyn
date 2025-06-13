package me.loovcik.magazyn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import me.loovcik.magazyn.managers.players.StoragePlayer;

public class PlayerJoinListener implements Listener
{

	@EventHandler(ignoreCancelled = true)
	private void onPlayerLeave(PlayerQuitEvent event){
		StoragePlayer storagePlayer = StoragePlayer.get(event.getPlayer());
		storagePlayer.save();
		storagePlayer.unload();
	}
}