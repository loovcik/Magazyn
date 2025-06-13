package me.loovcik.magazyn;

import me.loovcik.magazyn.managers.ConfigurationManager;
import me.loovcik.magazyn.managers.DependenciesManager;
import me.loovcik.magazyn.managers.ItemsManager;
import org.bukkit.plugin.java.JavaPlugin;
import me.loovcik.core.ChatHelper;
import me.loovcik.core.commands.SimpleCommand;
import me.loovcik.magazyn.commands.MagazynCommand;
import me.loovcik.magazyn.commands.SellAllCommand;
import me.loovcik.magazyn.commands.SellGuiCommand;
import me.loovcik.magazyn.listeners.InventoryClickEventListener;
import me.loovcik.magazyn.listeners.InventoryCloseEventListener;
import me.loovcik.magazyn.listeners.PlayerJoinListener;
import me.loovcik.magazyn.managers.players.StoragePlayer;

import java.util.ArrayList;
import java.util.List;

public final class Magazyn extends JavaPlugin
{
	private static Magazyn instance;

	/** Zapewnia obsługę opcjonalnych zależności */
	public DependenciesManager dependencies;

	/** Zapewnia obsługę itemów w magazynie */
	public ItemsManager itemsManager;

	/** Zapewnia obsługę konfiguracji */
	public ConfigurationManager configuration;

	/** Dostęp do instancji pluginu */
	public static Magazyn getInstance() { return instance; }

	@Override
	public void onLoad() {
		instance = this;
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void onEnable()
	{
		ChatHelper.setPlugin(this);
		ChatHelper.console("Author: <gold>Loovcik</gold>");
		ChatHelper.console("Version: <gold>" + getPluginMeta().getVersion());
		saveDefaultConfig();
		dependencies = new DependenciesManager(this);

		itemsManager = new ItemsManager(this);
		configuration = new ConfigurationManager(this);
		configuration.loadConfig();
		ChatHelper.setPrefix(configuration.prefix);
		//dependencies.placeholderAPI.register();
		itemsManager.Config.loadConfigs();
		registerCommands();
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryClickEventListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryCloseEventListener(), this);
		getLogger().info("Plugin loaded!");
	}

	@Override
	public void onDisable()
	{
		StoragePlayer.saveAndUnloadAll();

		unregisterCommands();
		if (dependencies.placeholderAPI != null && dependencies.placeholderAPI.isEnabled())
			dependencies.placeholderAPI.unregister();
		instance = null;

		getLogger().info("Plugin successfully disabled");
	}

	/** Rejestruje komendy pluginu */
	private void registerCommands(){
		new MagazynCommand(this, "magazyn", null, "Dostęp do podręcznego magazynu", "").register();
		new SellAllCommand(this, "sellall", new String[] { "sprzedajwszystko"}, "Pozwala na sprzedanie wszystkich magazynowalnych przedmiotów z ekwipunku gracza", "").register();
		new SellGuiCommand(this, "sellgui", new String[] { "sprzedaj"}, "Otwiera GUI sprzedaży magazynowalnych przedmiotów", "").register();
		SimpleCommand.scheduleCommandSync(this);
	}

	/** Wyrejestrowuje komendy pluginu */
	private void unregisterCommands(){
		List<SimpleCommand> copy = new ArrayList<>(SimpleCommand.getCommands());
		copy.forEach(SimpleCommand::unregister);
		SimpleCommand.scheduleCommandSync(this);
	}
}