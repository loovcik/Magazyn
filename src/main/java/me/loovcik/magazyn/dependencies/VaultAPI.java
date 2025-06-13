package me.loovcik.magazyn.dependencies;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import me.loovcik.magazyn.Magazyn;

import static org.bukkit.Bukkit.getServer;

public class VaultAPI
{
	private static Economy econ = null;

	public Economy getEconomy(){
		return econ;
	}

	public void initialize()
	{
		if (!setupEconomy()) getServer().getPluginManager().disablePlugin(Magazyn.getInstance());
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ.isEnabled();
	}
}