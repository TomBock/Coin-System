package com.bocktom.coinsystem;

import com.bocktom.coinsystem.api.CoinEconomy;
import com.bocktom.coinsystem.db.Messages;
import com.bocktom.coinsystem.db.MySQL;
import com.bocktom.coinsystem.util.CoinPlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.Reader;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

		if(!setupEconomy()) {
			getLogger().severe("Vault not found! Disabling plugin...");
			getServer().getPluginManager().disablePlugin(this);
		}

		new CoinPlaceholderExpansion().register();

		scheduler.runTaskAsynchronously(this, MySQL::setupDatabase);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			getServer().getServicesManager().register(Economy.class, new CoinEconomy(), this, ServicePriority.Normal);
			getLogger().info("Registered CoinEconomy with Vault!");
			return true;
		}
		return false;
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
		MySQL.readBalance(player.getUniqueId()).thenAccept(balance -> {
			sender.sendMessage(Messages.load("messages.read", player, balance));
		});
	}

	public void addCoins(CommandSender sender, Player player, long i) {
		MySQL.addCoinTransaction(player.getUniqueId(), i).thenAccept(result -> {
			if(result)
				sender.sendMessage(Messages.load(i >= 0 ? "messages.add" : "messages.remove", player, -1, i));
			else
				sender.sendMessage(Messages.load("messages.error", player, -1, i));
		});
	}

	public CompletableFuture<Boolean> addCoins(UUID uuid, long amount) {
		return MySQL.addCoinTransaction(uuid, amount);
	}

	public CompletableFuture<Long> readBalance(UUID uuid) {
		return MySQL.readBalance(uuid);
	}
}
