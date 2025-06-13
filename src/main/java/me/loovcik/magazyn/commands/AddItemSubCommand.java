package me.loovcik.magazyn.commands;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import me.loovcik.core.ChatHelper;
import me.loovcik.core.commands.SimpleSubCommand;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.items.ItemConfig;
import me.loovcik.magazyn.managers.players.StoragePlayer;

import java.util.List;

public class AddItemSubCommand extends SimpleSubCommand {
	private final Magazyn plugin;

	@Override
	public boolean execute(@NotNull CommandSender sender, String[] args) {

		if (args == null || args.length == 0) {
			ChatHelper.message(sender, "<red>Nie określono nazwy itemu");
			return false;
		}

		String itemName = args[0].toLowerCase();
		NamespacedKey namespacedKey = new NamespacedKey("minecraft", itemName.toLowerCase());
		Material material = Registry.MATERIAL.get(namespacedKey);
		if (material == null || !plugin.itemsManager.Config.isStorageItem(material)) {
			ChatHelper.message(sender, "<red>Nieprawidłowy item");
			return true;
		}
		ItemConfig itemConfig = plugin.itemsManager.Config.get(material);

		if (args.length < 2){
			ChatHelper.message(sender, "<red>Nie określono gracza");
			return true;
		}

		String playerName = args[1];
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
		StoragePlayer storagePlayer = StoragePlayer.get(offlinePlayer.getUniqueId());
		if (storagePlayer == null) {
			ChatHelper.message(sender, "<red>Nie odnaleziono gracza "+playerName);
			return true;
		}

		if (args.length < 3) {
			ChatHelper.message(sender, "<red>Należy określić ilość");
			return true;
		}

		int amount;
		try
		{
			amount = Integer.parseInt(args[2]);
		}
		catch (Exception e) {
			ChatHelper.message(sender, "<red>Nieprawidłowa ilość");
			return true;
		}

		storagePlayer.getStorage().add(material, amount);
		ChatHelper.message(sender, "<green>Dodano </green><gold>"+amount+"x</gold> "+itemConfig.getName()+"<reset><green> graczowi <gold>"+playerName+"</gold>. Aktualna ilość: <gold>"+storagePlayer.getStorage().get(material).amount+"</gold>.");
		storagePlayer.save();
		return true;
	}

	public AddItemSubCommand(Magazyn plugin){
		super(plugin, "addItem", List.of(), "magazyn.command.additem");
		this.plugin = plugin;
	}
}