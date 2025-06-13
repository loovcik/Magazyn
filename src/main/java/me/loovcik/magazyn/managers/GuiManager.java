package me.loovcik.magazyn.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.guis.ActionGui;
import me.loovcik.magazyn.guis.DepositGui;
import me.loovcik.magazyn.guis.MainGui;
import me.loovcik.magazyn.guis.SellGui;
import me.loovcik.magazyn.items.ItemConfig;

public final class GuiManager
{
	public static void ShowMainMenu(Player viewer, OfflinePlayer owner){
		if (viewer == null) return;
		new MainGui(Magazyn.getInstance(), owner).Show(viewer);
	}

	public static void ShowActionsMenu(Player viewer, OfflinePlayer owner, ItemConfig itemConfig){
		if (viewer == null) return;
		new ActionGui(Magazyn.getInstance(), owner, itemConfig).Show(viewer);
	}

	public static void ShowSellMenu(Player viewer, OfflinePlayer owner){
		if (viewer == null) return;
		new SellGui(Magazyn.getInstance(), owner).Show(viewer);
	}

	public static void ShowDepositMenu(Player viewer, OfflinePlayer owner){
		if (viewer == null) return;
		new DepositGui(Magazyn.getInstance(), owner).Show(viewer);
	}

	public static void closeMenu(Inventory inventory){
		if (inventory == null) return;
		inventory.close();
	}

	private GuiManager() {}
}