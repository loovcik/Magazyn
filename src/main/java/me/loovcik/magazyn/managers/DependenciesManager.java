package me.loovcik.magazyn.managers;

import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.dependencies.PlaceholderAPI;
import me.loovcik.magazyn.dependencies.VaultAPI;

public class DependenciesManager
{
	private final Magazyn plugin;
	public PlaceholderAPI placeholderAPI;
	public VaultAPI vault;

	private void create(){
		vault = new VaultAPI();
		vault.initialize();
		placeholderAPI = new PlaceholderAPI(plugin);
	}

	public DependenciesManager(Magazyn plugin){
		this.plugin = plugin;
		create();
	}
}