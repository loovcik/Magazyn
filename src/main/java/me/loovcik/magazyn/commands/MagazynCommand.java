package me.loovcik.magazyn.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.loovcik.core.commands.SimpleCommand;
import me.loovcik.magazyn.Magazyn;
import me.loovcik.magazyn.managers.GuiManager;

public class MagazynCommand extends SimpleCommand
{

	@Override
	public boolean execute(@NotNull CommandSender sender, String[] args){
		if (!super.execute(sender, args))
			if (sender instanceof Player player)
				GuiManager.ShowMainMenu(player, player);
		else return true;
		return false;
	}

	public MagazynCommand(@NotNull Magazyn plugin, @NotNull String command, @Nullable String[] aliases, String description, String permission){
		super(plugin, command, aliases, description, permission);

		registerSubCommand(new DropSubCommand(plugin));
		registerSubCommand(new AddItemSubCommand(plugin));
	}
}