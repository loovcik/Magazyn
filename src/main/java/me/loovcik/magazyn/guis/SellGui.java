package me.loovcik.magazyn.guis;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;

public class SellGui extends Gui
{
	@Override
	public void onClose(InventoryCloseEvent event){
		if (event.getPlayer() instanceof Player player){
			int soldAmount = 0;
			double earnedMoney = 0d;
			for (int i = 0; i < event.getInventory().getSize(); i++) {
				ItemStack itemStack = event.getInventory().getItem(i);
				if (itemStack == null || itemStack.getType() == Material.AIR) continue;
				int sold = plugin.itemsManager.Sell.sellFromGui(player, itemStack);
				if (sold != itemStack.getAmount()) {
					int toRecover = itemStack.getAmount() - sold;
					ItemStack recoveryItem = new ItemStack(itemStack);
					recoveryItem.setAmount(toRecover);
					player.getInventory().addItem(recoveryItem);
				}
				earnedMoney += plugin.itemsManager.Sell.getCost(itemStack.getType(), sold);
				soldAmount += sold;
			}

			if (soldAmount != 0){
				ChatHelper.message(player, plugin.configuration.messages.sell_selectedEq.replaceAll("%cost%", String.format("%.4f", earnedMoney)));
				player.sendActionBar(ChatHelper.minimessage("&a+$" + String.format("%.4f", earnedMoney)));
			}
		}
	}

	public SellGui(Magazyn plugin, OfflinePlayer owner){
		super(plugin, owner, plugin.configuration.guis.sellTitle, 9, GuiType.NONE);
	}
}