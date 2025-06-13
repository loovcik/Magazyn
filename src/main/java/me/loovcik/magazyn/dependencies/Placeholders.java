package me.loovcik.magazyn.dependencies;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import me.loovcik.magazyn.Magazyn;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class Placeholders extends PlaceholderExpansion
{
	private final Magazyn plugin;

	public Placeholders(Magazyn plugin){
		this.plugin = plugin;
	}

	/**
	 * Pobiera identyfikator placeholdera
	 * @return Identyfikator placeholdera
	 */
	@Override
	public @NotNull String getIdentifier()
	{
		return "magazyn";
	}

	/**
	 * Pobiera autora
	 * @return Nazwa autora
	 */
	@Override public @NotNull String getAuthor() { return String.join(", ", plugin.getPluginMeta().getAuthors()); }

	/**
	 * Pobiera wersję pluginu
	 * @return Wersja pluginu
	 */
	@Override public @NotNull String getVersion()
	{
		return plugin.getPluginMeta().getVersion();
	}

	@Override public boolean persist(){
		return true;
	}

	/**
	 * Obsługuje żądania dotyczące placeholdera
	 * @param player Kontekst gracza
	 * @param params Parametry
	 * @return Zawartość placeholdera
	 */
	public String onRequest(OfflinePlayer player, @NotNull String params){
		List<String> parts = List.of(params.split("_"));

		return "N/A";
	}
}