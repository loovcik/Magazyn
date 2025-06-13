package me.loovcik.magazyn.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import me.loovcik.core.ChatHelper;
import me.loovcik.core.commands.SimpleSubCommand;
import me.loovcik.magazyn.Magazyn;

import java.util.List;

public class DropSubCommand extends SimpleSubCommand
{
	private final Magazyn plugin;

	@Override
	public boolean execute(@NotNull CommandSender sender, String[] args) {
		if (args == null || args.length < 2) return false;


		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			ChatHelper.message(sender, "&cNie znaleziono gracza "+args[0]);
			return false;
		}
		Material material = Material.getMaterial(args[1].toUpperCase());
		if (material == null){
			ChatHelper.message(sender, "&cBłędny material: "+args[1]);
			return false;
		}
		plugin.itemsManager.Deposit.deposit(target, material, false, true);
		return true;
	}

	public DropSubCommand(Magazyn plugin){
		super(plugin, "drop", List.of(), "magazyn.command.drop");
		this.plugin = plugin;
	}
}