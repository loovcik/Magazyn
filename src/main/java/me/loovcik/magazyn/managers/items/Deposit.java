package me.loovcik.magazyn.managers.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.items.ItemConfig;
import me.loovcik.magazyn.managers.InventoryManager;
import me.loovcik.magazyn.managers.ItemsManager;
import me.loovcik.magazyn.managers.players.StoragePlayer;

import java.util.HashMap;

import static me.loovcik.magazyn.managers.InventoryManager.*;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class Deposit
{
	private final Magazyn plugin;
	private final ItemsManager manager;

	/**
	 * Pobiera przedmioty z ekwipunku gracza i umieszcza je w magazynie
	 */
	public boolean deposit(Player player, boolean ignoreDisable, boolean strict){
		if (player == null) return false;
		int total = 0;

		StoragePlayer storagePlayer = StoragePlayer.get(player);

		if (!storagePlayer.getStorage().isEnabled()) return false;

		for (var entry : manager.Config.getEntrySet()){
			Material material = entry.getKey();
			if (!canDeposit(player, material, ignoreDisable)){
				if (plugin.configuration.debug)
					ChatHelper.console("&eMaterial "+material+" is disabled for "+player.getName());
				continue;
			}

			int amount = InventoryManager.calculateItemsOnInventory(player.getInventory(), material);
			if (player.getInventory().contains(entry.getKey(), amount)){
				if (plugin.configuration.maxAmount > -1 && storagePlayer.getStorage().getAmount(material) + amount > plugin.configuration.maxAmount){
					ChatHelper.message(player, "&cOsiągnięto maksymalną ilość "+entry.getValue().getName()+" w magazynie!");
					if (!strict)
						amount = plugin.configuration.maxAmount - storagePlayer.getStorage().getAmount(material);
					else
						continue;
				}
				int removed = removeFromInventory(player.getInventory(), entry.getKey(), amount);
				total += removed;
				storagePlayer.getStorage().add(material, removed);
			}
		}
		if (total != 0 && StoragePlayer.get(player).getStorage().hasNotification() && plugin.configuration.messages.deposit_all != null && !plugin.configuration.messages.deposit_all.isEmpty())
		{
			var replacements = new HashMap<String, String>();
			replacements.put("%player%", player.getName());
			ChatHelper.message(player, plugin.configuration.messages.deposit_all, replacements);
		}
		return total > 0;
	}

	public int deposit(Player player, Material material, int amount, boolean ignoreDisable, boolean strict){
		if (player == null) return 0;
		StoragePlayer storagePlayer = StoragePlayer.get(player);

		if (!storagePlayer.getStorage().isEnabled()) return 0;

		if (!canDeposit(player, material, ignoreDisable)){
			if (plugin.configuration.debug)
				ChatHelper.console("&eMaterial "+material+" is disabled for "+player.getName());
			return 0;
		}

		int total = 0;
		ItemConfig itemConfig = plugin.itemsManager.Config.get(material);

		if (strict && calculateItemsOnInventory(player.getInventory(), material) < itemConfig.getStackSize()) return 0;
		if (player.getInventory().contains(material, amount)){
			if (plugin.configuration.maxAmount > -1 && StoragePlayer.get(player).getStorage().getAmount(material) + amount > plugin.configuration.maxAmount){
				ChatHelper.message(player, "&cOsiągnięto maksymalną ilość "+itemConfig.getName()+" w magazynie!");
				if (!strict)
					amount = plugin.configuration.maxAmount - StoragePlayer.get(player).getStorage().getAmount(material);
				else {
					return 0;
				}
			}
			int removed = removeFromInventory(player.getInventory(), material, amount);
			total += removed;
			storagePlayer.getStorage().add(material, removed);
		}
		if (total != 0 && StoragePlayer.get(player).getStorage().hasNotification() && plugin.configuration.messages.deposit_auto != null && !plugin.configuration.messages.deposit_auto.isEmpty())
		{
			var replacements = new HashMap<String, String>();
			replacements.put("%player%", player.getName());
			replacements.put("%amount%", String.valueOf(total));
			replacements.put("%name%", manager.Config.getName(material));
			ChatHelper.message(player, plugin.configuration.messages.deposit_auto, replacements);
			if (plugin.configuration.debug)
				ChatHelper.console("deposit={item="+manager.Config.getName(material)+", amount="+total+", target="+player.getName()+"}");
		}
		return total;
	}
	/*
	TODO: Określić maksymalną ilość możliwą do umieszczenia w magazynie na podstawie maksymalnej pojemności
	 */
	/**
	 * Pobiera przedmioty z ekwipunku gracza i umieszcza je w magazynie
	 */
	public int deposit(Player player, Material material, boolean ignoreDisable, boolean strict){
		return deposit(player, material, plugin.itemsManager.Config.get(material).getStackSize(), ignoreDisable, strict);
	}

	public int depositFromGui(Player player, ItemStack itemStack, boolean ignoreDisable, boolean strict){
		if (player == null) return 0;
		StoragePlayer storagePlayer = StoragePlayer.get(player);

		if (!storagePlayer.getStorage().isEnabled() || !plugin.itemsManager.Config.isStorageItem(itemStack.getType())) return 0;

		if (!canDeposit(player, itemStack.getType(), ignoreDisable)){
			if (plugin.configuration.debug)
				ChatHelper.console("&eMaterial "+itemStack.getType()+" is disabled for "+player.getName());
			return 0;
		}
		if (!canDeposit(player, itemStack, ignoreDisable)) return 0;
		int total = 0;
		int amount = itemStack.getAmount();

		ItemConfig itemConfig = plugin.itemsManager.Config.get(itemStack.getType());
		if(strict && amount < itemConfig.getStackSize()) return 0;


		if (plugin.configuration.maxAmount > -1 && StoragePlayer.get(player).getStorage().getAmount(itemStack.getType()) + amount > plugin.configuration.maxAmount){
			ChatHelper.message(player, "&cOsiągnięto maksymalną ilość "+itemConfig.getName()+" w magazynie!");
			if (!strict)
				amount = plugin.configuration.maxAmount - StoragePlayer.get(player).getStorage().getAmount(itemStack.getType());
			else return 0;
		}
		int processed = amount;
		total += processed;
		StoragePlayer.get(player).getStorage().add(itemStack.getType(), processed);

		if (total != 0 && plugin.configuration.messages.deposit_auto != null && !plugin.configuration.messages.deposit_auto.isEmpty())
		{
			var replacements = new HashMap<String, String>();
			replacements.put("%player%", player.getName());
			replacements.put("%amount%", String.valueOf(total));
			replacements.put("%name%", manager.Config.getName(itemStack.getType()));
			ChatHelper.message(player, plugin.configuration.messages.deposit_auto, replacements);
		}
		return total;
	}

	/**
	 * Określa, czy przedmiot może zostać zdeponowany
	 */
	public boolean canDeposit(Player player, Material material, boolean ignoreDisable){
		if (player == null || material == null || !manager.Config.isStorageItem(material)) return false;
		if (!StoragePlayer.get(player).getStorage().isExists(material)) StoragePlayer.get(player).getStorage().createItemData(material);
		if (!ignoreDisable) return StoragePlayer.get(player).getStorage().isEnabled(material);
		return true;
	}

	/**
	 * Określa, czy przedmiot może zostać zdeponowany
	 */
	public boolean canDeposit(Player player, ItemStack itemStack, boolean ignoreDisable){
		if (player == null || itemStack == null || !manager.Config.isStorageItem(itemStack.getType())) return false;
		if (isBypassed(itemStack)) return false;
		if (!StoragePlayer.get(player).getStorage().isExists(itemStack.getType())) StoragePlayer.get(player).getStorage().createItemData(itemStack.getType());
		if (!ignoreDisable) return StoragePlayer.get(player).getStorage().isEnabled(itemStack.getType());
		return true;
	}

	public Deposit(Magazyn plugin, ItemsManager manager){
		this.plugin = plugin;
		this.manager = manager;
	}
}