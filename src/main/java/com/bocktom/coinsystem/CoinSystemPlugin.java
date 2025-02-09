package com.bocktom.coinsystem;

import com.bocktom.coinsystem.db.Messages;
import com.bocktom.coinsystem.db.MySQL;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.Reader;

public class CoinSystemPlugin extends JavaPlugin {

	public static CoinSystemPlugin instance;
	private BukkitScheduler scheduler;

	@Override
	public void onEnable() {
		instance = this;
		scheduler = getServer().getScheduler();

		setupDefaultConfig();

		//noinspection DataFlowIssue
		getCommand("coins").setExecutor(new CoinCommands(this));

		scheduler.runTaskAsynchronously(this, MySQL::setupDatabase);
	}

	private void setupDefaultConfig() {
		FileConfiguration config = getConfig();
		Reader defaultConfig = getTextResource("config.yml");

		if(defaultConfig != null) {
			config.setDefaults(YamlConfiguration.loadConfiguration(defaultConfig));
			config.options().copyDefaults(true);
			saveConfig();
		}
	}

	public void readBalance(CommandSender sender, Player player) {
		scheduler.runTaskAsynchronously(this, () -> {
			int balance = MySQL.readCoins(player.getUniqueId());
			sender.sendMessage(Messages.load("messages.read", player, balance));
		});
	}

	public void addCoins(CommandSender sender, Player player, int i) {
		scheduler.runTaskAsynchronously(this, () -> {
			boolean result = MySQL.addCoinTransaction(player.getUniqueId(), i);

			if(result) {
				sender.sendMessage(Messages.load(i >= 0 ? "messages.add" : "messages.remove", player, -1, i));
			} else {
				sender.sendMessage(Messages.load("messages.error", player, -1, i));
			}
		});
	}
}
