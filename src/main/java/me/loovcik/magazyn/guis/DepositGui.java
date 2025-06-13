package me.loovcik.magazyn.guis;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;

public class DepositGui extends Gui
{
	@Override
	public void onClose(InventoryCloseEvent event){
		if (event.getPlayer() instanceof Player player){
			int deposited = 0;
			for (int i = 0; i < event.getInventory().getSize(); i++) {
				ItemStack itemStack = event.getInventory().getItem(i);
				if (itemStack == null || itemStack.getType() == Material.AIR) continue;
				int deposit = plugin.itemsManager.Deposit.depositFromGui(player, itemStack, true, false);
				if (deposit != itemStack.getAmount()) {
					int toRecover = itemStack.getAmount() - deposit;
					ItemStack recoveryItem = new ItemStack(itemStack);
					recoveryItem.setAmount(toRecover);
					player.getInventory().addItem(recoveryItem);
				}
				deposited += deposit;
			}

			if (deposited != 0){
				ChatHelper.message(player, plugin.configuration.messages.deposit_all);
			}
		}
	}

	public DepositGui(Magazyn plugin, OfflinePlayer owner){
		super(plugin, owner, plugin.configuration.guis.depositTitle, 9, GuiType.NONE);
	}
}