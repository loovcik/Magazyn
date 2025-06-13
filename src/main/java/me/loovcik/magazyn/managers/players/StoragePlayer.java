package me.loovcik.magazyn.managers.players;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.items.Storage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Wrapper gracza
 */
public class StoragePlayer
{
	private static final Map<UUID, StoragePlayer> cache;

	/** Uchwyt pluginu **/
	private final Magazyn plugin;

	/** Uchwyt do klasy gracza **/
	private final UUID uuid;

	/** Nazwa gracza **/
	private final String name;

	/** Przechowywanie informacji o zawartości magazynu **/
	private final Storage storage;

	/** Zmienna przechowująca informacje, czy dane są wczytane **/
	private boolean loaded = false;

	/** Pobiera klasę magazynu gracza */
	public Storage getStorage(){ return storage; }

	/** Pobiera nazwę gracza */
	public String getName(){ return this.name; }

	/** Pobiera UUID gracza **/
	public UUID getUUID() { return uuid; }

	/** Pobiera oryginalnego gracza */
	public Player getPlayer() { return Bukkit.getPlayer(uuid); }

	/**
	 * Pobiera wewnętrznego wrappera gracza.<br><br>
	 * Jeśli dany gracz nie ma aktualnie wrappera,<br>
	 * funkcja go utworzy, jednak <u>nie będzie</u> wczytywać<br>
	 * jego danych.
	 */
	public static StoragePlayer get(UUID uuid){
		if(!cache.containsKey(uuid)) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			StoragePlayer storagePlayer = new StoragePlayer(Magazyn.getInstance(), uuid, offlinePlayer.getName());
			cache.put(uuid, storagePlayer);
			return storagePlayer;
		}
		return cache.get(uuid);
	}

	/**
	 * Pobiera wrapper gracza
	 */
	public static StoragePlayer get(Player player){
		return get(player.getUniqueId());
	}

	/** Wczytuje ustawienia i stan magazynu gracza */
	private void load() {
		if (loaded) return;
		try {
			File file = new File(plugin.getDataFolder(), "data/"+getUUID()+".yml");
			boolean shouldSave = false;
			if (!file.exists()) {
				if (plugin.configuration.debug)
					ChatHelper.console("&cTworzenie pliki danych dla " + getPlayer().getName());
				shouldSave = true;
			}

			YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
			getStorage().setEnabled(yml.getBoolean("enabled", true));
			getStorage().setNotification(yml.getBoolean("notify", true));
			if (shouldSave){
				yml.set("enabled", getStorage().isEnabled());
				yml.set("notify", getStorage().hasNotification());
				yml.save(file);
			}
			plugin.itemsManager.load(this, yml);
			if (plugin.configuration.debug)
				ChatHelper.console("&aDane gracza "+getPlayer().getName()+" załadowane");
			loaded = true;
		}
		catch (Exception e){
			if (plugin.configuration.debug)
				ChatHelper.console("&cBłąd odczytu: "+e.getMessage());
		}
	}

	/** Zapisuje ustawienia i stan magazynu gracza */
	public void save(){
		if (!loaded) return;
		if (!getStorage().isChanged()) return;

		try
		{
			File file = new File(plugin.getDataFolder(), "data/" + getUUID() + ".yml");
			YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
			yml.set("enabled", getStorage().isEnabled());
			yml.set("notify", getStorage().hasNotification());
			plugin.itemsManager.save(this, yml);
			yml.save(file);
			if (plugin.configuration.debug)
				ChatHelper.console("&aDane gracza &6"+getPlayer().getName()+"&a zapisane");
		}
		catch (Exception e){
			if (plugin.configuration.debug) ChatHelper.console("&cBłąd zapisu danych gracza "+getUUID());
		}
	}

	/** Zapisuje ustawienia i stan magazynów wszystkich graczy, a następnie wyładowuje dane graczy */
	public static void saveAndUnloadAll() {
		Map<UUID, StoragePlayer> copy = new HashMap<>(cache);
		for (Map.Entry<UUID, StoragePlayer> entry : copy.entrySet()){
			entry.getValue().save();
			entry.getValue().unload();
		}
		cache.clear();
	}

	/**
	 * Usunięcie danych gracza z pamięci
	 */
	public void unload(){
		cache.remove(getUUID());
		if (plugin.configuration.debug)
			ChatHelper.console("&aGracz "+getName()+" usunięty");
	}

	@Override
	public int hashCode() { return this.uuid.hashCode(); }

	@Override
	public boolean equals(Object obj){
		if (this == obj) return true;

		if (obj == null || this.getClass() != obj.getClass())
			return false;
		StoragePlayer user = (StoragePlayer) obj;
		return this.uuid.equals(user.uuid);
	}

	@Override
	public String toString(){
		return "User={uuid="+this.uuid+", name='"+this.name+"'}";
	}

	static {
		cache = new ConcurrentHashMap<>();
	}

	public StoragePlayer(Magazyn plugin, UUID uuid, String name){
		this.plugin = plugin;
		this.uuid = uuid;
		this.name = name;
		storage = new Storage();
		load();
	}
}