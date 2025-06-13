package me.loovcik.magazyn.managers.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.items.ItemConfig;
import me.loovcik.magazyn.items.Storage;
import me.loovcik.magazyn.managers.InventoryManager;
import me.loovcik.magazyn.managers.ItemsManager;
import me.loovcik.magazyn.managers.players.StoragePlayer;

import java.util.HashMap;

public class Sell
{
	private final Magazyn plugin;
	private final ItemsManager manager;

	public boolean sell(Player player){
		if (player == null) return false;

		StoragePlayer storagePlayer = StoragePlayer.get(player.getUniqueId());
		Storage storage = storagePlayer.getStorage();

		double totalCost = 0;
		for (ItemConfig itemConfig : plugin.itemsManager.Config){
			int amount = storage.getAmount(itemConfig.getMaterial());
			if (amount > 0){
				double cost = plugin.itemsManager.Sell.getCost(itemConfig.getMaterial(), amount);
				plugin.dependencies.vault.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(storagePlayer.getUUID()), cost);
				storage.remove(itemConfig.getMaterial(), amount);
				totalCost += cost;
			}
		}

		if (totalCost == 0){
			ChatHelper.message(player, plugin.configuration.messages.sell_noItems);
			return false;
		}
		else {
			ChatHelper.message(player, plugin.configuration.messages.sell_allStorage.replaceAll("%cost%", String.format("%.4f", totalCost)));
			player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
			player.sendActionBar(ChatHelper.minimessage("&a+$" + String.format("%.4f", totalCost)));
			return true;
		}
	}

	public void sellEq(Player player){
		if (player == null) return;

		double totalCost = 0;
		for (ItemConfig itemConfig : plugin.itemsManager.Config){
			int amount = InventoryManager.calculateItemsOnInventory(player.getInventory(), itemConfig.getMaterial());
			if (amount > 0){
				double cost = plugin.itemsManager.Sell.getCost(itemConfig.getMaterial(), amount);
				plugin.dependencies.vault.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), cost);
				InventoryManager.removeFromInventory(player.getInventory(), itemConfig.getMaterial(), amount);
				totalCost += cost;
			}
		}

		if (totalCost == 0) {
			ChatHelper.message(player, plugin.configuration.messages.sell_noItems);
		}
		else {
			ChatHelper.message(player, plugin.configuration.messages.sell_allEq.replaceAll("%cost%", String.format("%.4f", totalCost)));
			player.sendActionBar(ChatHelper.minimessage("&a+$" + String.format("%.4f", totalCost)));
			player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
		}
	}

	public boolean sell(Player player, Material material){
		if (player == null || material == null) return false;
		return sell(player, material, StoragePlayer.get(player).getStorage().getAmount(material));
	}

	public boolean sell(Player player, Material material, int amount){
		if (player == null || material == null || !manager.Config.isStorageItem(material)) return false;
		if (plugin.itemsManager.Config.get(material).getPrice() < 0){
			ChatHelper.message(player, plugin.configuration.messages.sell_unavailable);
			return false;
		}
		amount = Integer.min(amount, StoragePlayer.get(player).getStorage().getAmount(material));
		if (amount <= 0){
			ChatHelper.message(player, plugin.configuration.messages.sell_noItems);
		}
		double cost = getCost(material, amount);
		if (cost > 0)
		{
			plugin.dependencies.vault.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), cost);
			StoragePlayer storagePlayer = StoragePlayer.get(player.getUniqueId());
			storagePlayer.getStorage().setAmount(material, storagePlayer.getStorage().getAmount(material)-amount);
			var replacements = new HashMap<String, String>();
			replacements.put("%amount%", String.valueOf(amount));
			replacements.put("%name%", plugin.itemsManager.Config.getName(material));
			replacements.put("%player%", player.getName());
			replacements.put("%cost%", String.format("%(,.2f", cost));
			ChatHelper.message(player, plugin.configuration.messages.sell_success, replacements);
			player.sendActionBar(ChatHelper.minimessage("&a+$" + String.format("%.4f", cost)));
			player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
			return true;
		}
		return false;
	}

	public int sellFromGui(Player player, ItemStack itemStack){
		if (player == null || itemStack == null) return 0;
		if (!plugin.itemsManager.Config.isStorageItem(itemStack.getType())) return 0;
		if (plugin.itemsManager.Config.get(itemStack.getType()).getPrice() < 0) return 0;

		int amount = Integer.min(itemStack.getAmount(), itemStack.getMaxStackSize());
		if (amount <= 0) return 0;
		double cost = getCost(itemStack.getType(), amount);
		if (cost > 0)
		{
			plugin.dependencies.vault.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), cost);
			return amount;
		}
		return 0;
	}

	/**
	 * Oblicza wartość danej ilości przedmiotów<br>
	 * wybranego typu
	 */
	public double getCost(Material material, int amount){
		if (material == null || !manager.Config.isStorageItem(material)) return 0f;
		double price = manager.Config.get(material).getPrice();
		amount = Integer.max(0, amount);
		if (price == 0 || amount == 0) return 0f;
		return price * amount;
	}

	/**
	 * Oblicza wartość staku wybranego przedmiotu
	 */
	public double getCost(Material material){
		if (material == null || !manager.Config.isStorageItem(material)) return 0f;
		double price = manager.Config.get(material).getPrice();
		int amount = manager.Config.get(material).getStackSize();
		if (price == 0 || amount == 0) return 0f;
		return price * amount;
	}

	/**
	 * Oblicza całkowitą wartość magazynu gracza
	 */
	public double getCost(Player player){
		StoragePlayer storagePlayer = StoragePlayer.get(player.getUniqueId());
		Storage storage = storagePlayer.getStorage();
		double totalCost = 0;
		for (ItemConfig itemConfig : plugin.itemsManager.Config){
			int amount = storage.getAmount(itemConfig.getMaterial());
			double cost = itemConfig.getPrice() * amount;
			totalCost += cost;
		}
		return totalCost;
	}

	/**
	 * Oblicza wartość przedmiotów danego typu<br>
	 * zgromadzonych w magazynie gracza
	 */
	public double getCost(Player player, Material material) {
		StoragePlayer storagePlayer = StoragePlayer.get(player.getUniqueId());
		return storagePlayer.getStorage().getAmount(material) * plugin.itemsManager.Config.get(material).getPrice();
	}

	public Sell(Magazyn plugin, ItemsManager manager){
		this.manager = manager;
		this.plugin = plugin;
	}
}