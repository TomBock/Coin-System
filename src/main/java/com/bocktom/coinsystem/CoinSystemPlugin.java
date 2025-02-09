package com.bocktom.coinsystem;

import com.bocktom.coinsystem.db.Messages;
import com.bocktom.coinsystem.db.MySQL;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;

public class CoinSystemPlugin extends JavaPlugin {

	public static CoinSystemPlugin instance;

	@Override
	public void onEnable() {
		instance = this;

		setupDefaultConfig();

		//noinspection DataFlowIssue
		getCommand("coins").setExecutor(new CoinCommands(this));

		MySQL.setupDatabase();
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

	public void readBalance(Player player) {
		int balance = MySQL.readCoins(player.getUniqueId());
		player.sendMessage(Messages.load("messages.read", player, balance));
	}

	public void addCoins(Player player, int i) {
		boolean result = MySQL.addCoinTransaction(player.getUniqueId(), i);

		if(result) {
			player.sendMessage(Messages.load(i >= 0 ? "messages.add" : "messages.remove", player, -1, i));
		} else {
			player.sendMessage(Messages.load("messages.error", player, -1, i));
		}
	}
}
