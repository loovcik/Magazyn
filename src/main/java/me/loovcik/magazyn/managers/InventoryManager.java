package me.loovcik.magazyn.managers;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;

public class InventoryManager
{
	/**
	 * Oblicza całkowitą ilość danego rodzaju przedmiotów w ekwipunku
	 */
	public static int calculateItemsOnInventory(Inventory inventory, Material material){
		if (inventory == null || material == null) return 0;
		int result = 0;
		for (var entry : inventory.getContents()){
			if (entry == null || isBypassed(entry)) continue;
			if (entry.getItemMeta().hasEnchants()) continue;
			if (entry.getType() == material)
				result += entry.getAmount();
		}
		return result;
	}

	public static int calculateSpaceForItem(Inventory inventory, Material material){
		if (inventory == null || material == null) return 0;
		int available = 0;
		for (var entry : inventory.getContents()){
			if (entry == null || entry.getType() == Material.AIR) available += material.getMaxStackSize();
			else if (entry.getType() == material && !entry.getItemMeta().hasDisplayName()){
				available += entry.getMaxStackSize() - entry.getAmount();
			}
		}
		return available;
	}

	/**
	 * Usuwa pełne stacki przedmiotów z ekwipunku gracza
	 */
	public static int removeFromInventory(Inventory inventory, Material material){
		if (inventory == null || material == null) return 0;
		int totalAmount = calculateItemsOnInventory(inventory, material);
		int toRemove = Math.floorDiv(totalAmount, material.getMaxStackSize()) * material.getMaxStackSize();
		return removeFromInventory(inventory, material, toRemove);
	}

	/**
	 * Usuwa żądaną ilość przedmiotów z ekwipunku gracza
	 */
	public static int removeFromInventory(Inventory inventory, Material material, int amount){
		if (inventory == null || material == null || amount == 0) return 0;
		amount = Integer.min(amount, calculateItemsOnInventory(inventory, material));
		int remCount = amount;
		int removed = 0;
		for (var slot : inventory.getContents()){
			if (remCount == 0) break;
			if (slot == null || slot.getType() == Material.AIR) continue;
			if (isBypassed(slot)) continue;
			if (slot.getType() == material)
			{
				if (slot.getItemMeta().hasEnchants()) continue;
				int slotCount = slot.getAmount();
				if (remCount >= slotCount)
				{
					slot.setAmount(0);
					removed += slotCount;
					remCount -= slotCount;
				}
				else
				{
					int remind = slotCount - remCount;
					slot.setAmount(remind);
					removed += remind;
					remCount = 0;
				}
			}
		}
		return removed;
	}

	public static boolean isBypassed(ItemStack itemStack){
		if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return false;
		Magazyn plugin = Magazyn.getInstance();
		ItemMeta itemMeta = itemStack.getItemMeta();
		for (String bypassName : plugin.configuration.bypass){
			if (ChatHelper.plainText(itemMeta.displayName()).equalsIgnoreCase(bypassName)) return true;
		}
		return false;
	}
}