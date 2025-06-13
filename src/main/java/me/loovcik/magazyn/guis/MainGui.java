package me.loovcik.magazyn.guis;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.items.ItemConfig;
import me.loovcik.magazyn.items.Storage;
import me.loovcik.magazyn.managers.GuiManager;
import me.loovcik.magazyn.managers.players.StoragePlayer;
import me.loovcik.magazyn.utils.Replacement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainGui extends Gui
{
	private Map<Integer, ItemStack> createItems(){
		Map<Integer, ItemStack> result = Maps.newHashMap();
		StoragePlayer storagePlayer = StoragePlayer.get(this.owner.getUniqueId());
		Storage storage = storagePlayer.getStorage();
		for (ItemConfig itemConfig : plugin.itemsManager.Config)
			result.put(itemConfig.getSlot(), getSlotItem(storage, itemConfig, itemConfig.getSlot()));

		result.putAll(createNavigation());
		return result;
	}

	private Map<Integer, ItemStack> createNavigation(){
		Map<Integer, ItemStack> result = new HashMap<>();
		StoragePlayer storagePlayer = StoragePlayer.get(this.owner.getUniqueId());
		Storage storage = storagePlayer.getStorage();

		Map<String, String> replacements = new HashMap<>();

		int slot = Gui.getSlot(size, 1);
		List<String> lores = new ArrayList<>(plugin.configuration.guis.close.lore);
		result.put(slot, createItem(plugin.configuration.guis.close.material, plugin.configuration.guis.close.name, lores, slot, Actions.CLOSE));

		slot = Gui.getSlot(size, 2);
		result.put(slot, getNotificationSlot(storage, slot));

		slot = Gui.getSlot(size, 7);
		result.put(slot, getStatusItem(storage, slot));

		slot = Gui.getSlot(size, 8);
		lores = new ArrayList<>(plugin.configuration.guis.storage.sell.lore);
		replacements.put("%cost%", String.format("%,.4f", plugin.itemsManager.Sell.getCost(storagePlayer.getPlayer())));
		lores.replaceAll(input -> Replacement.capitals(Replacement.replace(input, replacements)));
		result.put(slot, createItem(plugin.configuration.guis.storage.sell.material, plugin.configuration.guis.storage.sell.name, lores, slot, Actions.SELL));

		slot = Gui.getSlot(size, 9);
		lores = new ArrayList<>(plugin.configuration.guis.storage.deposit.lore);
		lores.replaceAll(Replacement::capitals);
		result.put(slot, createItem(plugin.configuration.guis.storage.deposit.material, plugin.configuration.guis.storage.deposit.name, lores, slot, Actions.DEPOSIT));

		return result;
	}

	private ItemStack getSlotItem(Storage storage, ItemConfig itemConfig, int slot){
		List<String> lore = new ArrayList<>();

		Map<String, String> replacements = new HashMap<>();
		int amount = storage.getAmount(itemConfig.getMaterial());
		double price = itemConfig.getPrice();
		double cost = price * amount;

		replacements.put("%amount%", String.valueOf(amount));
		replacements.put("%max%", String.valueOf(plugin.configuration.maxAmount));
		replacements.put("%price%", String.format("%,.4f", price));
		replacements.put("%cost%", String.format("%,.4f", cost));

		lore.add("");
		lore.add(Replacement.capitals(Replacement.replace(plugin.configuration.guis.storage.lore.amount, replacements)));
		lore.add(Replacement.capitals(Replacement.replace(plugin.configuration.guis.storage.lore.autoDeposit, Map.of("%status%", Replacement.booleanToString(storage.isEnabled(itemConfig.getMaterial()))))));

		if (price > 0){
			lore.add(Replacement.capitals(Replacement.replace(plugin.configuration.guis.storage.lore.price, replacements)));
			lore.add(Replacement.capitals(Replacement.replace(plugin.configuration.guis.storage.lore.cost, replacements)));
		}
		else
			lore.add(Replacement.capitals(plugin.configuration.guis.storage.lore.noSellable));

		lore.add("");
		if (price <= 0)
			lore.add(Replacement.capitals(plugin.configuration.guis.storage.lore.actions.lpmDepositWithdraw));
		else lore.add(Replacement.capitals(plugin.configuration.guis.storage.lore.actions.lpmDepositWithdrawSell));
		lore.add(Replacement.capitals(plugin.configuration.guis.storage.lore.actions.ppmAutoDeposit));
		if (price > 0)
			lore.add(Replacement.capitals(plugin.configuration.guis.storage.lore.actions.shiftPpmSellAll));


		ItemStack item = createItem(itemConfig.getMaterial(), itemConfig.getName(), lore, itemConfig.getSlot(), Actions.ITEM);
		if (storage.isEnabled(itemConfig.getMaterial())){
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(Enchantment.MENDING, 1, false);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
		}
		return item;
	}

	private ItemStack getNotificationSlot(Storage storage, int slot){
		List<String> lores = new ArrayList<>(plugin.configuration.guis.storage.notification.lore);
		Map<String, String> replacements = new HashMap<>();
		replacements.put("%status%", Replacement.booleanToString(storage.hasNotification()));
		lores.replaceAll(input -> Replacement.capitals(Replacement.replace(input, replacements)));
		return createItem(plugin.configuration.guis.storage.notification.material, plugin.configuration.guis.storage.notification.name, lores, slot, Actions.NOTIFICATION);
	}

	private ItemStack getStatusItem(Storage storage, int slot){
		Material statusMaterial = storage.isEnabled() ? plugin.configuration.guis.storage.status.enabledMaterial : plugin.configuration.guis.storage.status.disabledMaterial;
		List<String> lores = new ArrayList<>(plugin.configuration.guis.storage.status.lore);
		Map<String, String> replacements = new HashMap<>();
		replacements.put("%status%", Replacement.booleanToString(storage.isEnabled()));
		lores.replaceAll(input -> Replacement.capitals(Replacement.replace(input, replacements)));
		return createItem(statusMaterial, plugin.configuration.guis.storage.status.name, lores, slot, Actions.STATUS);
	}

	@Override
	public void handleMenu(InventoryClickEvent event){
		ItemStack item = event.getCurrentItem();
		if (item == null || !item.hasItemMeta()) return;

		StoragePlayer storagePlayer = StoragePlayer.get(event.getWhoClicked().getUniqueId());
		Storage storage = storagePlayer.getStorage();
		NamespacedKey slotKey = new NamespacedKey(plugin, "slot");
		NamespacedKey actionKey = new NamespacedKey(plugin, "action");
		final ItemMeta meta = item.getItemMeta();
		@SuppressWarnings("DataFlowIssue") int slot = meta.getPersistentDataContainer().has(slotKey, PersistentDataType.INTEGER) ? meta.getPersistentDataContainer().get(slotKey, PersistentDataType.INTEGER) : 0;
		Actions action = Actions.valueOf(meta.getPersistentDataContainer().has(actionKey, PersistentDataType.STRING) ? meta.getPersistentDataContainer().get(actionKey, PersistentDataType.STRING) : "NONE");
		if (action == Actions.NONE) return;

		switch (action) {
			case CLOSE -> GuiManager.closeMenu(event.getClickedInventory());
			case STATUS -> {
				storage.setEnabled(!storage.isEnabled());
				updateSlot(event.getClickedInventory(), event.getSlot(), getStatusItem(storage, event.getSlot()));
			}
			case ITEM -> {
				if (event.isRightClick() && event.isShiftClick()) {
					plugin.itemsManager.Sell.sell((Player) event.getWhoClicked(), event.getCurrentItem().getType());
					updateSlot(event.getClickedInventory(), event.getSlot(), getSlotItem(storage, plugin.itemsManager.Config.get(event.getCurrentItem().getType()), event.getSlot()));
				}
				else if (event.isRightClick() && !event.isShiftClick()) {
					storage.setEnabled(event.getCurrentItem().getType(), !storage.isEnabled(event.getCurrentItem().getType()));
					updateSlot(event.getClickedInventory(), event.getSlot(), getSlotItem(storage, plugin.itemsManager.Config.get(event.getCurrentItem().getType()), event.getSlot()));
				}
				else if (event.isLeftClick() && !event.isShiftClick()) {
					GuiManager.ShowActionsMenu((Player) event.getWhoClicked(), Bukkit.getOfflinePlayer(event.getWhoClicked().getUniqueId()), plugin.itemsManager.Config.get(event.getCurrentItem().getType()));
				}
			}
			case NOTIFICATION -> {
				storage.setNotification(!storage.hasNotification());
				updateSlot(event.getClickedInventory(), event.getSlot(), getNotificationSlot(storage, event.getSlot()));
			}
			case SELL -> {
				if (event.isShiftClick() && event.isLeftClick()){
					plugin.itemsManager.Sell.sellEq(storagePlayer.getPlayer());
				}
				else if (!event.isShiftClick() && event.isRightClick()){
					if (plugin.itemsManager.Sell.sell(storagePlayer.getPlayer()))
						GuiManager.ShowMainMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer());
				}
				else if (!event.isShiftClick() && event.isLeftClick()){
					GuiManager.ShowSellMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer());
				}
			}
			case DEPOSIT -> {
				if (event.isShiftClick()) return;
				if (event.isRightClick()) {
					if (plugin.itemsManager.Deposit.deposit(storagePlayer.getPlayer(), false, false))
						GuiManager.ShowMainMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer());
				}
				else if (event.isLeftClick()){
					GuiManager.ShowDepositMenu(storagePlayer.getPlayer(), storagePlayer.getPlayer());
				}
			}
			default -> {
				return;
			}
		}

	}

	public MainGui(Magazyn plugin, OfflinePlayer owner){
		super(plugin, owner, plugin.configuration.guis.storage.title, plugin.configuration.guis.storage.rows, GuiType.CLICKABLE);
		this.setItems(createItems());
	}
}