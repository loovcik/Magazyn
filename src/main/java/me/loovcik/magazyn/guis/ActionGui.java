package me.loovcik.magazyn.guis;

import com.google.common.collect.Maps;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.items.ItemConfig;
import me.loovcik.magazyn.items.Storage;
import me.loovcik.magazyn.managers.GuiManager;
import me.loovcik.magazyn.managers.InventoryManager;
import me.loovcik.magazyn.managers.players.StoragePlayer;
import me.loovcik.magazyn.utils.Replacement;

import java.util.*;

public class ActionGui extends Gui implements Listener
{
	private final ItemConfig item;
	private final UUID owner;

	private Map<Integer, ItemStack> createItems(){
		Map<Integer, ItemStack> result = Maps.newHashMap();
		if (owner == null || item == null) return result;

		StoragePlayer storagePlayer = StoragePlayer.get(this.owner);
		Storage storage = storagePlayer.getStorage();

		Map<String, String> replacements = new HashMap<>();
		int amount = storage.getAmount(item.getMaterial());
		int amountInInv = InventoryManager.calculateItemsOnInventory(storagePlayer.getPlayer().getInventory(), item.getMaterial());
		double price = item.getPrice();
		double cost = plugin.itemsManager.Sell.getCost(item.getMaterial(), amount);
		int availableSpace = InventoryManager.calculateSpaceForItem(storagePlayer.getPlayer().getInventory(), item.getMaterial());

		int slot = Gui.getSlot(2, 3);
		replacements.put("%amountInInventory%", String.valueOf(amountInInv));
		List<String> lore = new ArrayList<>(plugin.configuration.guis.action.deposit.lore);
		lore.replaceAll(input -> Replacement.capitals(Replacement.replace(input, replacements)));
		result.put(slot, createItem(plugin.configuration.guis.action.deposit.material, plugin.configuration.guis.action.deposit.name, lore, slot, Actions.DEPOSIT));

		slot = Gui.getSlot(2, 5);
		replacements.put("%amount%", String.valueOf(amount));
		replacements.put("%price%", String.format("%,.4f", price));
		replacements.put("%cost%", String.format("%,.4f", cost));
		lore.clear();
		lore.addAll(plugin.configuration.guis.action.sell.lore);
		lore.replaceAll(input -> Replacement.capitals(Replacement.replace(input, replacements)));
		result.put(slot, createItem(plugin.configuration.guis.action.sell.material, plugin.configuration.guis.action.sell.name, lore, slot, Actions.SELL));

		slot = Gui.getSlot(2, 7);
		replacements.put("%spaceInInventory%", String.valueOf(availableSpace));
		lore.clear();
		lore.addAll(plugin.configuration.guis.action.take.lore);
		lore.replaceAll(input -> Replacement.capitals(Replacement.replace(input, replacements)));
		result.put(slot, createItem(plugin.configuration.guis.action.take.material, plugin.configuration.guis.action.take.name, lore, slot, Actions.TAKE));
		result.putAll(createNavigation());
		return result;
	}

	private Map<Integer, ItemStack> createNavigation(){
		Map<Integer, ItemStack> result = new HashMap<>();
		int slot = Gui.getSlot(size, 1);

		List<String> lore = new ArrayList<>(plugin.configuration.guis.back.lore);
		lore.replaceAll(Replacement::capitals);
		result.put(slot, createItem(plugin.configuration.guis.back.material, plugin.configuration.guis.back.name, lore, slot, Actions.BACK));

		return result;
	}

	@Override
	public void handleMenu(InventoryClickEvent event) {
		ItemStack itemStack = event.getCurrentItem();
		if (itemStack == null || !itemStack.hasItemMeta()) return;
		final ItemMeta meta = itemStack.getItemMeta();

		StoragePlayer storagePlayer = StoragePlayer.get(event.getWhoClicked().getUniqueId());
		Storage storage = storagePlayer.getStorage();
		NamespacedKey actionKey = new NamespacedKey(plugin, "action");
		Actions action = Actions.valueOf(meta.getPersistentDataContainer().has(actionKey, PersistentDataType.STRING) ? meta.getPersistentDataContainer().get(actionKey, PersistentDataType.STRING) : "NONE");
		if (action == Actions.NONE) return;

		switch (action) {
			case DEPOSIT -> {
				if (!event.isShiftClick() && event.isLeftClick()){
					if (plugin.itemsManager.Deposit.deposit(storagePlayer.getPlayer(), item.getMaterial(), InventoryManager.calculateItemsOnInventory(event.getWhoClicked().getInventory(), item.getMaterial()), false, false) > 0)
						GuiManager.ShowActionsMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer(), item);
				}
			}
			case SELL -> {
				if (event.isShiftClick() && event.isRightClick()){
					// Sprzedaj wszystko
					if (plugin.itemsManager.Sell.sell(storagePlayer.getPlayer(), item.getMaterial()))
						GuiManager.ShowMainMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer());

				}
				else if (event.isRightClick()){
					// Sprzedaj stak
					int amount = storage.getAmount(item.getMaterial());
					if (amount <= 0) return;
					boolean sold = false;
					if (amount < item.getStackSize())
						sold = plugin.itemsManager.Sell.sell(storagePlayer.getPlayer(), item.getMaterial(), amount);
					else
					if (plugin.itemsManager.Sell.sell(storagePlayer.getPlayer(), item.getMaterial(), item.getStackSize()))
						GuiManager.ShowMainMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer());
				}
				else if (!event.isShiftClick() && event.isLeftClick()){
					// Sprzedaj 1 sztukę
					if (plugin.itemsManager.Sell.sell(storagePlayer.getPlayer(), item.getMaterial(), 1))
						GuiManager.ShowMainMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer());
				}
			}
			case TAKE -> {
				if (event.isShiftClick() && event.isRightClick()){
					// Zabierz wszystko
					if (plugin.itemsManager.Withdraw.withdraw(storagePlayer.getPlayer(), item.getMaterial()))
						GuiManager.ShowActionsMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer(), item);
				}
				else if (!event.isShiftClick()){
					if (event.isLeftClick()){
						// Zabierz 1 sztukę
						if (plugin.itemsManager.Withdraw.withdraw(storagePlayer.getPlayer(), item.getMaterial(), 1))
							GuiManager.ShowActionsMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer(), item);
					}
					else if (event.isRightClick()){
						// Zabierz stak
						if (plugin.itemsManager.Withdraw.withdraw(storagePlayer.getPlayer(), item.getMaterial(), item.getStackSize()))
							GuiManager.ShowActionsMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer(), item);
					}
				}

			}
			case BACK -> GuiManager.ShowMainMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer());
		}
	}

	public ActionGui(Magazyn plugin, OfflinePlayer owner, ItemConfig item){
		super(plugin, owner, plugin.configuration.guis.action.title, plugin.configuration.guis.action.rows, GuiType.CLICKABLE);
		this.owner = owner.getUniqueId();
		this.item = item;
		this.setItems(createItems());
	}
}