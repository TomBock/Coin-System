package com.bocktom.coinsystem.api;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CoinEconomy implements Economy {
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getName() {
		return "Coin-System";
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return 0;
	}

	@Override
	public String format(double v) {
		return String.valueOf(v);
	}

	@Override
	public String currencyNamePlural() {
		return "Coins";
	}

	@Override
	public String currencyNameSingular() {
		return "Coin";
	}

	@Override
	public boolean hasAccount(String s) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer) {
		return true;
	}

	@Override
	public boolean hasAccount(String s, String s1) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
		return true;
	}

	@Override
	public double getBalance(String s) {
		Player player = Bukkit.getPlayer(s);
		if(player != null)
			return getBalance(player);
		return 0;
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		try {
			return CoinAPI.readBalance(offlinePlayer.getUniqueId()).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getBalance(String s, String s1) {
		return getBalance(s);
	}

	@Override
	public double getBalance(OfflinePlayer offlinePlayer, String s) {
		return getBalance(offlinePlayer);
	}

	@Override
	public boolean has(String s, double v) {
		return getBalance(s) >= v;
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, double v) {
		return getBalance(offlinePlayer) >= v;
	}

	@Override
	public boolean has(String s, String s1, double v) {
		return getBalance(s) >= v;
	}

	@Override
	public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
		return getBalance(offlinePlayer) >= v;
	}

	@Override
	public EconomyResponse withdrawPlayer(String s, double v) {
		Player player = Bukkit.getPlayer(s);
		if(player != null)
			return withdrawPlayer(player, v);

		return new EconomyResponse(
				v,
			 0,
			 EconomyResponse.ResponseType.FAILURE,
			 "Spieler nicht gefunden");
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		boolean success = false;
		try {
			success = CoinAPI.removeCoinsR(offlinePlayer.getUniqueId(), (long) amount).get();
		} catch (Exception ignored) {}

		return new EconomyResponse(
				amount,
				0,
				success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE,
				success ? null : "Es ist ein Fehler aufgetreten"
		);
	}

	@Override
	public EconomyResponse withdrawPlayer(String s, String s1, double v) {
		return withdrawPlayer(s, v);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
		return withdrawPlayer(offlinePlayer, v);
	}

	@Override
	public EconomyResponse depositPlayer(String s, double v) {
		Player player = Bukkit.getPlayer(s);
		if(player != null)
			return depositPlayer(player, v);

		return new EconomyResponse(
				v,
				0,
			 EconomyResponse.ResponseType.FAILURE,
			 "Spieler nicht gefunden");
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		boolean success = false;
		try {
			success = CoinAPI.addCoinsR(offlinePlayer.getUniqueId(), (long) amount).get();
		} catch (Exception ignored) {}

		return new EconomyResponse(
				amount,
				0,
				success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE,
				success ? null : "Es ist ein Fehler aufgetreten"
		);
	}

	@Override
	public EconomyResponse depositPlayer(String s, String s1, double v) {
		return depositPlayer(s, v);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
		return depositPlayer(offlinePlayer, v);
	}

	@Override
	public EconomyResponse createBank(String s, String s1) {
		return null;
	}

	@Override
	public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
		return null;
	}

	@Override
	public EconomyResponse deleteBank(String s) {
		return null;
	}

	@Override
	public EconomyResponse bankBalance(String s) {
		return null;
	}

	@Override
	public EconomyResponse bankHas(String s, double v) {
		return null;
	}

	@Override
	public EconomyResponse bankWithdraw(String s, double v) {
		return null;
	}

	@Override
	public EconomyResponse bankDeposit(String s, double v) {
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String s, String s1) {
		return null;
	}

	@Override
	public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String s, String s1) {
		return null;
	}

	@Override
	public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
		return null;
	}

	@Override
	public List<String> getBanks() {
		return List.of();
	}

	@Override
	public boolean createPlayerAccount(String s) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(String s, String s1) {
		return false;
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
		return false;
	}
}
