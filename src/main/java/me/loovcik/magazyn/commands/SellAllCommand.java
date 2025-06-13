package me.loovcik.magazyn.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.loovcik.core.commands.SimpleCommand;
import me.loovcik.magazyn.Magazyn;

public class SellAllCommand extends SimpleCommand
{
	private final Magazyn plugin;

	@Override
	public boolean execute(@NotNull CommandSender sender, String[] args) {
		if (sender instanceof Player player) {
			this.plugin.itemsManager.Sell.sellEq(player);
			return true;
		}
		return false;
	}

	public SellAllCommand(@NotNull Magazyn plugin, @NotNull String command, @Nullable String[] aliases, String description, String permission)
	{
		super(plugin, command, aliases, description, permission);
		this.plugin = plugin;
	}
}