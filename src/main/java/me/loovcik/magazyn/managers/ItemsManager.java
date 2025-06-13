package me.loovcik.magazyn.managers;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.items.ItemData;
import me.loovcik.magazyn.managers.items.Config;
import me.loovcik.magazyn.managers.items.Deposit;
import me.loovcik.magazyn.managers.items.Sell;
import me.loovcik.magazyn.managers.items.Withdraw;
import me.loovcik.magazyn.managers.players.StoragePlayer;

public class ItemsManager
{
	/** Uchwyt pluginu **/
	private final Magazyn plugin;

	public final Config Config;
	public final Deposit Deposit;
	public final Withdraw Withdraw;
	public final Sell Sell;

	/**
	 * Wczytuje magazyn gracza
	 */
	public void load(StoragePlayer player, YamlConfiguration yml){
		if (player == null || yml == null) return;
		ConfigurationSection section = yml.getConfigurationSection("items");
		if (section == null) {
			ChatHelper.console("&c'items' section for '"+player.getName()+"' data not found!");
			yml.createSection("items");
		}
		for (var entry : Config.getEntrySet()){
			String matKey = "items."+entry.getKey().toString().toLowerCase();
			if (yml.contains(matKey)){
				int amount = yml.getInt(matKey+".amount", 0);
				boolean enabled = yml.getBoolean(matKey+".enabled", true);
				Material material = entry.getKey();
				if (material == null){
					ChatHelper.console("&cUnknown material in: "+matKey);
					continue;
				}
				player.getStorage().add(material, amount);
				player.getStorage().setEnabled(material, enabled);
				if (plugin.configuration.debug)
					ChatHelper.console("&7Set "+player.getName()+"'s item "+matKey+" to "+amount+" ("+player.getStorage().isEnabled(material)+")");
			}
			else if (plugin.configuration.debug)
				ChatHelper.console("&ePlayer "+player.getName()+" doesn't have item "+matKey);
		}
	}

	/**
	 * Zapisuje magazyn gracza
	 */
	public void save(StoragePlayer player, YamlConfiguration yml){
		if (player == null || yml == null) return;
		if (!player.getStorage().isChanged()) return;
		for (var entry : Config.getEntrySet()){
			String matKey = "items."+entry.getKey().toString().toLowerCase();
			ItemData itemData = player.getStorage().get(entry.getKey());
			if (itemData == null) continue;
			yml.set(matKey+".amount", itemData.amount);
			yml.set(matKey+".enabled", itemData.enabled);
		}
	}

	public ItemsManager(Magazyn plugin){
		this.plugin = plugin;
		this.Config = new Config(plugin, this);
		this.Deposit = new Deposit(plugin, this);
		this.Withdraw = new Withdraw(plugin, this);
		this.Sell = new Sell(plugin, this);
	}
}