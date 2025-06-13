package me.loovcik.magazyn.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import me.loovcik.magazyn.guis.Gui;

public class InventoryCloseEventListener implements Listener
{
	@EventHandler
	private void inventoryClose(InventoryCloseEvent event){
		if(event.getPlayer() instanceof Player)
		{
			InventoryHolder holder = event.getInventory().getHolder();
			if (holder instanceof Gui gui)
				gui.onClose(event);
		}
	}
}