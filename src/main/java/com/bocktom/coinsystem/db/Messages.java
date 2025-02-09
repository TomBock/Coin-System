package com.bocktom.coinsystem.db;

import com.bocktom.coinsystem.CoinSystemPlugin;
import org.bukkit.entity.Player;

public class Messages {

	public static String load(String config, Player player, int balance, int amount) {
		return load(config, player, balance)
				.replace("{amount}", String.valueOf(amount));
	}

	public static String load(String config, Player player, int balance) {
		String msg = CoinSystemPlugin.instance.getConfig().getString(config);
		assert msg != null;

		return msg.replace("{balance}", String.valueOf(balance))
				.replace("{player}", player.getName());
	}
}
