package me.loovcik.magazyn.managers.items;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.items.ItemConfig;
import me.loovcik.magazyn.managers.ItemsManager;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class Config implements Iterable<ItemConfig>
{
	private final Magazyn plugin;

	/** Przechowuje konfigurację magazynowalnych przedmiotów **/
	private final Map<Material, ItemConfig> config;

	/**
	 * Określa, czy dany przedmiot jest magazynowalny
	 */
	public boolean isStorageItem(Material material){
		return config.containsKey(material);
	}

	/**
	 * Pobiera wyświetlaną nazwę przedmiotu
	 */
	public String getName(Material material){
		if (material == null) return "";
		if (!isStorageItem(material)) return material.name();
		return config.get(material).getName();
	}

	/**
	 * Pobiera konfigurację dla danego typu przedmiotu
	 */
	public ItemConfig get(Material material){
		if (material == null || !isStorageItem(material)) return null;
		return config.get(material);
	}

	/**
	 * Wczytuje konfigurację dotyczącą magazynowalnych przedmiotów
	 */
	public void loadConfigs(){
		try {
			File file = new File(plugin.getDataFolder(), "items.yml");
			if (!file.exists()){
				createExampleFile();
			}
			YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
			ConfigurationSection section = yml.getConfigurationSection("items");
			if (section == null) return;

			for (var item : Objects.requireNonNull(section).getKeys(false)){
				if (plugin.configuration.debug) ChatHelper.console("&7Reading &6'"+item+"'&7 configuration");
				Material material = Material.getMaterial(item.toUpperCase());
				if (material == null){
					if (plugin.configuration.debug) ChatHelper.console("&cMaterial '"+item+"' is not a valid minecraft material");
					continue;
				}
				ConfigurationSection entry = section.getConfigurationSection(item);
				if (entry == null){
					if (plugin.configuration.debug) ChatHelper.console("&cError on '"+item+"' configuration");
					continue;
				}
				ItemConfig itemConfig = new ItemConfig(material);
				itemConfig.setName(entry.getString("name"));
				itemConfig.setSlot(entry.getInt("slot") - 1);
				itemConfig.setPrice(entry.getString("price"));
				config.put(material, itemConfig);
				if (plugin.configuration.debug)
					ChatHelper.console("&aConfiguration for '"+item+"' loaded");
			}
			ChatHelper.console("&aConfiguration for "+config.size()+" items loaded");
		}
		catch (Exception e){
			ChatHelper.console(e.getMessage());
		}
	}

	/**
	 * Tworzy przykładowy plik konfiguracji<br>
	 * magazynowalnych przedmiotów
	 */
	private void createExampleFile(){
		try {
			File file = new File(plugin.getDataFolder(), "items.yml");
			YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
			yml.set("items.netherite_block.name", "&8Blok netherytu");
			yml.set("items.netherite_block.slot", 1);
			yml.set("items.netherite_block.price", "0.015 / 64");
			yml.save(file);
		}
		catch (Exception e){
			ChatHelper.console("&cNie można zapisać przykładowego pliku items.yml");
		}
	}

	public Config(Magazyn plugin, ItemsManager manager) {
		this.plugin = plugin;
		config = new HashMap<>();
	}

	public Set<Map.Entry<Material, ItemConfig>> getEntrySet(){
		return config.entrySet();
	}

	@Override
	public @NotNull Iterator<ItemConfig> iterator()
	{
		return config.values().iterator();
	}

	@Override
	public void forEach(Consumer<? super ItemConfig> action)
	{
		Iterable.super.forEach(action);
	}

	@Override
	public Spliterator<ItemConfig> spliterator()
	{
		return Iterable.super.spliterator();
	}
}