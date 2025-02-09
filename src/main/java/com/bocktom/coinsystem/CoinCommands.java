package com.bocktom.coinsystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinCommands implements CommandExecutor {

	private final CoinSystemPlugin plugin;

	public CoinCommands(CoinSystemPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player player))
			return true;

		// Read amount of coins
		if(args.length == 0) {

			if(!player.hasPermission("coinsystem.use")) {
				player.sendMessage("You don't have permission to do that");
				return true;
			}

			plugin.readBalance(player);
			return true;
		}

		if(args.length != 2) {
			player.sendMessage("Usage: /coins [add/remove] <amount>");
			return true;
		}

		// Add or remove coins
		if(!player.hasPermission("coinsystem.admin")) {
			player.sendMessage("You don't have permission to do that");
			return true;
		}

		boolean isAdd = args[0].equals("add");
		boolean isRemove = args[0].equals("remove");

		if(!isAdd && !isRemove) {
			player.sendMessage("Usage: /coins [add/remove] <amount>");
			return true;
		}

		// check if args[1] is a number
		if(!args[1].matches("\\d+")) {
			player.sendMessage("Amount must be a number");
			return true;
		}

		int amount = Integer.parseInt(args[1]);

		plugin.addCoins(player, isAdd ? amount : -amount);
		return true;
	}
}
