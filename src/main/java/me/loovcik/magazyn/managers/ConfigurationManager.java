package me.loovcik.magazyn.managers;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import me.loovcik.magazyn.Magazyn;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationManager
{
	private final Magazyn plugin;
	private FileConfiguration config;

	public boolean debug;
	public boolean useCapitals;
	public String prefix;
	public final Messages messages = new Messages();
	public final Guis guis = new Guis();
	public final List<String> bypass = new ArrayList<>();
	public int maxAmount;

	public void load(){
		debug = getBoolean("debug", false);
		useCapitals = getBoolean("useCapitals", true);
		prefix = getString("prefix", "&8[&lM&8]&r");
		maxAmount = getInt("maxAmount", 1000000);

		guis.storage.title = getString("gui.storage.title", "Magazyn | %player%");
		guis.storage.rows = getInt("gui.storage.rows", 6);

		guis.storage.deposit.material = Material.getMaterial(getString("gui.storage.deposit.material", "CHEST"));
		guis.storage.deposit.name = getString("gui.storage.deposit.name", "&aZdeponuj");
		guis.storage.deposit.lore = getList("gui.storage.deposit.lore", List.of("", "&6LPM&7 - Zdeponuj wybrane", "&6PPM&7 - Zdeponuj wszystko", ""));
		guis.storage.sell.material = Material.getMaterial(getString("gui.storage.sell.material", "GOLD_NUGGET"));
		guis.storage.sell.name = getString("gui.storage.sell.name", "&9Sprzedaj");
		guis.storage.sell.lore = getList("gui.storage.sell.lore", List.of("", "&7- Wartość: &6%cost%", "", "&6LPM&7 - Sprzedaj wybrane z ekwipunku", "&6Shift+LPM&7 - Sprzedaj wszystko z ekwipunku", "&6PPM&7 - Sprzedaj wszystko z magazynu", ""));
		guis.storage.status.name = getString("gui.storage.status.name", "&eStatus magazynu");
		guis.storage.status.enabledMaterial = Material.getMaterial(getString("gui.storage.enabledMaterial", "LIME_WOOL"));
		guis.storage.status.disabledMaterial = Material.getMaterial(getString("gui.storage.disabledMaterial", "RED_WOOL"));
		guis.storage.status.lore = getList("gui.storage.status.lore", List.of("", "&7- Status: %status%", "", "&6LPM&7 - Zmiana statusu", ""));
		guis.storage.notification.material = Material.getMaterial(getString("gui.storage.notification.material", "PAPER"));
		guis.storage.notification.name = getString("gui.storage.notification.name", "&aPokazuj powiadomienia");
		guis.storage.notification.lore = getList("gui.storage.notification.lore", List.of("", "&7- Aktualnie: &6%status%", "", "&6LPM&7 - Zmień", ""));
		guis.storage.lore.amount = getString("gui.storage.lore.amount", "&7- Posiadasz: &6%amount%");
		guis.storage.lore.cost = getString("gui.storage.lore.cost", "&7- Wartość: &6$%cost%");
		guis.storage.lore.price = getString("gui.storage.lore.price", "&7- Cena: &6$%price%");
		guis.storage.lore.autoDeposit = getString("gui.storage.lore.autoDeposit", "&7- Automatyczny depozyt: &6%status%");
		guis.storage.lore.noSellable = getString("gui.storage.lore.noSellable", "&cNie można sprzedać");
		guis.storage.lore.actions.lpmDepositWithdraw = getString("gui.storage.lore.actions.depositWithdraw", "&6LPM&7 - Deponuj/Zabierz");
		guis.storage.lore.actions.lpmDepositWithdrawSell = getString("gui.storage.lore.actions.depositWithdrawSell", "&6LPM&7 - Deponuj/Zabierz/Sprzedaj");
		guis.storage.lore.actions.ppmAutoDeposit = getString("gui.storage.lore.actions.autoDeposit", "&6PPM&7 - Automatycznie/Manualnie");
		guis.storage.lore.actions.shiftPpmSellAll = getString("gui.storage.lore.actions.sellAll", "&6Shift+PPM&7 - Sprzedaj wszystko");

		guis.action.title = getString("gui.actions.title", "Magazyn | Akcje");
		guis.action.rows = getInt("gui.actions.rows", 6);
		guis.action.deposit.material = Material.getMaterial(getString("gui.actions.deposit.material", "LIME_STAINED_GLASS_PANE"));
		guis.action.deposit.name = getString("gui.actions.deposit.name", "&aZdeponuj");
		guis.action.deposit.lore = getList("gui.actions.deposit.lore", List.of("", "&7- Do zdeponowania: &6%amountInInventory%"));
		guis.action.sell.material = Material.getMaterial(getString("gui.actions.sell.material", "GOLD_NUGGET"));
		guis.action.sell.name = getString("gui.actions.sell.name", "&eSprzedaj");
		guis.action.sell.lore = getList("gui.actions.sell.lore", List.of("", "&7- Ilość: &6%amount%", "&7- Cena: &6$%price%", "&7- Wartość: &6$%cost%", "", "&6LPM&7 - sprzedaj 1 szt.", "&6PPM&7 - Sprzedaj stak", "&6Shift+PPM&7 - Sprzedaj wszystko"));
		guis.action.take.material = Material.getMaterial(getString("gui.actions.take.material", "RED_STAINED_GLASS_PANE"));
		guis.action.take.name = getString("gui.actions.take.name", "&9Zabierz");
		guis.action.take.lore = getList("gui.actions.take.lore", List.of("", "&7- W magazynie: &6%amount%", "&7Miejsce w ekwipunku: &6%spaceInInventory%", "", "&6LPM&7 - Zabierz 1 szt.", "&6PPM&7 - Zabierz stak", "&6Shift+PPM&7 - Zabierz wszystko"));

		guis.back.material = Material.getMaterial(getString("gui.back.material", "ARROW"));
		guis.back.name = getString("gui.back.name", "&eWróć");
		guis.back.lore = getList("gui.back.lore", List.of());

		guis.close.material = Material.getMaterial(getString("gui.close.material", "BARRIER"));
		guis.close.name = getString("gui.close.name", "&cZamknij");
		guis.close.lore = getList("gui.close.lore", List.of());

		guis.sellTitle = getString("gui.sell.title", "Magazyn - Sprzedaj");
		guis.depositTitle = getString("gui.deposit.title", "Magazyn - Zdeponuj");

		bypass.clear();
		bypass.addAll(getList("bypass", List.of()));

		messages.deposit_auto = getString("messages.deposit.success", "&aZabrano &6%amount%x %name%&a do magazynu");
		messages.deposit_all = getString("messages.deposit.all", "&aZebrano wszystkie przedmioty do magazynu");
		messages.withdraw_noitems = getString("messages.withdraw.no-items", "&cW magazynie nie masz &6%name%");
		messages.withdraw_nospace = getString("messages.withdraw.no-space", "&cNie masz miejsca w ekwipunku na &6%amount%x %name%");
		messages.withdraw_success = getString("messages.withdraw.success", "&aZabrano &6%amount%x %name%&a z magazynu");
		messages.sell_success = getString("messages.sell.success", "&aSprzedano &6%amount%x %name%&a za kwotę &6$%cost%");
		messages.sell_noItems = getString("messages.sell.no-items", "&cNie masz nic do sprzedania");
		messages.sell_allEq = getString("messages.sell.eq", "&aSprzedano cały ekwipunek i otrzymano &6$%cost%");
		messages.sell_selectedEq = getString("messages.sell.selectedEq", "&aSprzedano wybrane przedmioty i otrzymano &6$%cost%");
		messages.sell_unavailable = getString("messages.sell.unavailable", "&cSprzedaż tego przedmiotu nie jest możliwa");
		messages.sell_allStorage = getString("messages.sell.storage", "&aSprzedano cały magazyn i otrzymano &6$%cost%");
		messages.yes = getString("messages.yes", "&aTak");
		messages.no = getString("messages.no", "&cNie");
		plugin.saveConfig();
	}

	/**
	 * Zapewnia bezpośredni dostęp do pliku konfiguracyjnego
	 * @return Zwraca interfejs do manipulacji w pliku konfiguracyjnym
	 */
	public FileConfiguration getConfig(){
		return config;
	}

	/**
	 * Wczytanie konfiguracji z pliku yml
	 */
	public void loadConfig()
	{
		plugin.reloadConfig();
		this.config = plugin.getConfig();
		config.options().copyDefaults(true);
		load();
		plugin.saveConfig();
	}

	/**
	 * Pobiera wartość boolean z określonej ścieżki w pliku konfiguracyjnym
	 * @param path Ścieżka w pliku konfiguracyjnym
	 * @param def Domyślna wartość
	 * @return Odczytana wartość lub domyślna, jeśli nie znaleziono
	 */
	public boolean getBoolean(String path, boolean def){
		config.addDefault(path, def);
		return config.getBoolean(path, def);
	}

	/**
	 * Pobiera wartość tekstową z określonej ścieżki w pliku konfiguracyjnym
	 * @param path Ścieżka w pliku konfiguracyjnym
	 * @param def Domyślna wartość
	 * @return Odczytana wartość lub domyślna, jeśli nie znaleziono
	 */
	public String getString(String path, String def){
		config.addDefault(path, def);
		return config.getString(path, def);
	}

	/**
	 * Pobiera wartość typu zmiennoprzecinkowego z pliku konfiguracyjnego
	 * @param path Ścieżka w pliku konfiguracyjnym
	 * @param def Wartość domyślna
	 * @return Odczytana wartość lub domyślna, jeśli nie znaleziono
	 */
	public double getDouble(String path, double def){
		config.addDefault(path, def);
		return config.getDouble(path, def);
	}

	/**
	 * Pobiera wartość typu numerycznego z pliku konfiguracyjnego
	 * @param path Ścieżka w pliku konfiguracyjnym
	 * @param def Wartość domyślna
	 * @return Odczytana wartość lub domyślna, jeśli nie znaleziono
	 */
	public int getInt(String path, int def){
		config.addDefault(path, def);
		return config.getInt(path, def);
	}

	/**
	 * Pobiera wartość typu List z pliku konfiguracyjnego
	 * @param path Ścieżka w pliku konfiguracyjnym
	 * @param def Wartość domyślna
	 * @return Odczytana wartość lub domyślna, jeśli nie znaleziono
	 */
	public List<String> getList(String path, List<String> def){
		config.addDefault(path, def);
		return config.getStringList(path);
	}

	/**
	 * Pobiera wartość z pliku konfiguracyjnego
	 * @param path Ścieżka w pliku konfiguracyjnym
	 * @param def Wartość domyślna
	 * @return Odczytana wartość lub domyślna, jeśli nie znaleziono
	 */
	public Object get(String path, Object def){
		config.addDefault(path, def);
		return config.get(path, def);
	}

	/**
	 * Sprawdza, czy ścieżka istnieje w pliku konfiguracyjnym
	 * @param path Ścieżka
	 * @return True, jeśli ścieżka istnieje
	 */
	public boolean isSet(String path){
		return config.isSet(path);
	}

	/**
	 * Sprawdza, czy dane pod podaną ścieżką są typu list
	 * @param path Ścieżka
	 * @return True, jeśli dane są typu list
	 */
	public boolean isList(String path){
		return config.isList(path);
	}

	/**
	 * Default constructor
	 * @param plugin Main plugin
	 */
	public ConfigurationManager(Magazyn plugin)
	{
		this.plugin = plugin;
		plugin.saveDefaultConfig();
	}

	public static class Messages {
		public String deposit_auto;
		public String deposit_all;
		public String withdraw_noitems;
		public String withdraw_nospace;
		public String withdraw_success;
		public String sell_success;
		public String sell_noItems;
		public String sell_allStorage;
		public String sell_allEq;
		public String sell_selectedEq;
		public String sell_unavailable;
		public String yes;
		public String no;
	}

	public static class Guis {
		public final StorageGui storage = new StorageGui();
		public final ActionGui action = new ActionGui();
		public final MaterialNameLore back = new MaterialNameLore();
		public final MaterialNameLore close = new MaterialNameLore();
		public String sellTitle;
		public String depositTitle;
	}

	public static class StorageGui {
		public int rows;
		public String title;
		public final MaterialNameLore sell = new MaterialNameLore();
		public final MaterialNameLore deposit = new MaterialNameLore();
		public final StorageStatus status = new StorageStatus();
		public final MaterialNameLore notification = new MaterialNameLore();
		public final StorageGuiDynamicLore lore = new StorageGuiDynamicLore();
	}

	public static class StorageGuiDynamicLore {
		public String amount;
		public String autoDeposit;
		public String price;
		public String cost;
		public String noSellable;
		public StorageLore actions = new StorageLore();
	}

	public static class ActionGui {
		public String title;
		public int rows;
		public final MaterialNameLore deposit = new MaterialNameLore();
		public final MaterialNameLore sell = new MaterialNameLore();
		public final MaterialNameLore take = new MaterialNameLore();
	}

	public static class MaterialNameLore {
		public String name;
		public Material material;
		public List<String> lore = new ArrayList<>();
	}

	public static class StorageStatus {
		public Material enabledMaterial;
		public Material disabledMaterial;
		public String name;
		public List<String> lore = new ArrayList<>();
	}

	public static class StorageLore {
		public String lpmDepositWithdraw;
		public String lpmDepositWithdrawSell;
		public String ppmAutoDeposit;
		public String shiftPpmSellAll;
	}
}