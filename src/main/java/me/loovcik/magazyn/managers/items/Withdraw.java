package me.loovcik.magazyn.managers.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.managers.ItemsManager;
import me.loovcik.magazyn.managers.players.StoragePlayer;

import java.util.HashMap;

import static me.loovcik.magazyn.managers.InventoryManager.calculateSpaceForItem;

public class Withdraw
{
	private final Magazyn plugin;
	private final ItemsManager manager;

	public boolean withdraw(Player player, Material material){
		return withdraw(player, material, StoragePlayer.get(player).getStorage().getAmount(material));
	}

	public boolean withdraw(Player player, Material material, int amount){
		if (player == null || material == null || amount < 1) return false;
		int onStorage = StoragePlayer.get(player).getStorage().getAmount(material);
		if (onStorage < 0){
			var replacement = new HashMap<String, String>();
			replacement.put("%player%", player.getName());
			replacement.put("%name%", manager.Config.getName(material));
			ChatHelper.message(player, plugin.configuration.messages.withdraw_noitems, replacement);
			return false;
		}

		amount = Math.min(amount, onStorage);

		int availableSpace = calculateSpaceForItem(player.getInventory(), material);
		if (availableSpace == 0){
			var replacements = new HashMap<String, String>();
			replacements.put("%player%", player.getName());
			replacements.put("%amount%", String.valueOf(amount));
			replacements.put("%name%", manager.Config.getName(material));
			ChatHelper.message(player, plugin.configuration.messages.withdraw_nospace, replacements);
		}

		amount = Math.min(amount, availableSpace);

		if (amount == 0) return false;

		int toWithdraw = amount;
		int maxIterations = 100;
		int iterations = 0;
		int withdrawAmount = 0;
		while (toWithdraw > 0){
			if (toWithdraw < material.getMaxStackSize()){
				player.getInventory().addItem(new ItemStack(material, toWithdraw));
				StoragePlayer.get(player).getStorage().remove(material, toWithdraw);
				withdrawAmount += toWithdraw;
				toWithdraw = 0;
			}
			else {
				player.getInventory().addItem(new ItemStack(material, material.getMaxStackSize()));
				StoragePlayer.get(player).getStorage().remove(material, material.getMaxStackSize());
				toWithdraw -= material.getMaxStackSize();
				withdrawAmount += material.getMaxStackSize();
			}
			iterations++;
			if (iterations >= maxIterations) break;
		}

		var replacements = new HashMap<String, String>();
		replacements.put("%player%", player.getName());
		replacements.put("%amount%", String.valueOf(withdrawAmount));
		replacements.put("%name%", manager.Config.getName(material));
		ChatHelper.message(player, plugin.configuration.messages.withdraw_success, replacements);
		return withdrawAmount > 0;
	}

	public Withdraw(Magazyn plugin, ItemsManager manager){
		this.plugin = plugin;
		this.manager = manager;
	}
}