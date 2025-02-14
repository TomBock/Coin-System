package com.bocktom.coinsystem.api;

import com.bocktom.coinsystem.CoinSystemPlugin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoinAPI {

	public static void addCoins(UUID playerUUID, long amount) {
		CoinSystemPlugin.instance.addCoins(playerUUID, amount);
	}

	public static void removeCoins(UUID playerUUID, long amount) {
		CoinSystemPlugin.instance.addCoins(playerUUID, -amount);
	}

	public static CompletableFuture<Long> readBalance(UUID playerUUID) {
		return CoinSystemPlugin.instance.readBalance(playerUUID);
	}

}
