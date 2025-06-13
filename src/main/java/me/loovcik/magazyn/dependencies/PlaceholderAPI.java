package me.loovcik.magazyn.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import me.loovcik.core.ChatHelper;
import me.loovcik.magazyn.Magazyn;

@SuppressWarnings("UnstableApiUsage")
public class PlaceholderAPI
{
	private PlaceholderAPIHook hook;

	/** Sprawdza, czy PlaceholderAPI jest włączone */
	public boolean isEnabled() { return hook != null; }

	/** Parsuje placeholdery w kontekście gracza */
	public String process(OfflinePlayer op, String input){
		if (isEnabled()) return hook.process(op, input);
		return input;
	}

	/** Parsuje placeholdery */
	public String process(String input){ return process(null, input); }

	/** Rejestruje placeholdery pluginu **/
	public void register(){	if (isEnabled()) hook.placeholders.register(); }

	/** Wyrejestrowuje placeholdery z pluginu */
	public void unregister(){ if (isEnabled()) hook.placeholders.unregister(); }

	public PlaceholderAPI(Magazyn plugin){
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			hook = new PlaceholderAPIHook(plugin);
			ChatHelper.console("PlaceholderAPI support: <green>Yes</green> ("+Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getPluginMeta().getVersion() + ")");
		}
		else ChatHelper.console("PlaceholderAPI support: <red>No</red>");
	}
}

class PlaceholderAPIHook {
	public final Placeholders placeholders;

	@SuppressWarnings("UnstableApiUsage")
	public String getVersion(){
		return Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getPluginMeta().getVersion();
	}

	public String process(OfflinePlayer op, String input){
		return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(op, input);
	}

	public PlaceholderAPIHook(Magazyn plugin){
		placeholders = new Placeholders(plugin);
	}
}