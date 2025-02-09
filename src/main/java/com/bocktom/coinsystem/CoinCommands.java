package com.bocktom.coinsystem;

import org.bukkit.Bukkit;
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
		// NORMAL USER
		if((sender instanceof Player player) && !player.hasPermission("coinsystem.use")) {
			player.sendMessage("You do not have permission to use this command");
			return true;
		}

		// Read own balance
		if(args.length == 0) {
			if(sender instanceof Player player) {
				plugin.readBalance(sender, player);
			} else {
				sender.sendMessage("Usage: /coins <player?> or /coins [add/remove] <amount> <player?>");
			}
		}

		// ADMIN USER
		if((sender instanceof Player player) && !player.hasPermission("coinsystem.admin")) {
			player.sendMessage("You do not have permission to use this command");
			return true;
		}

		// Read another balance
		if(args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				sender.sendMessage("Player " + args[0] + " not found");
			} else {
				plugin.readBalance(sender, target);
			}
		}

		if(args.length >= 2) {

			boolean isAdd = args[0].equals("add");
			boolean isRemove = args[0].equals("remove");
			int amount = Integer.parseInt(args[1]);

			if(isAdd || isRemove) {
				if(args.length == 2) {

					// Oneself
					if(sender instanceof Player player) {
						plugin.addCoins(sender, player, isAdd ?  amount: -amount);
					} else {
						sender.sendMessage("Usage: /coins <player?> or /coins [add/remove] <amount> <player?>");
					}
				} else {

					// Another player
					Player target = Bukkit.getPlayer(args[2]);
					if(target == null) {
						sender.sendMessage("Player " + args[2] + " not found");
					} else {
						plugin.addCoins(sender, target, isAdd ? amount : -amount);
					}
				}
			}
		}

		return true;
	}
}
