package com.bocktom.coinsystem.util;

import com.bocktom.coinsystem.CoinSystemPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class CoinPlaceholderExpansion extends PlaceholderExpansion {
	@Override
	public @NotNull String getIdentifier() {
		return "coin";
	}

	@Override
	public @NotNull String getAuthor() {
		return "TomBock";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0";
	}

	@Override
	public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
		if(player == null)
			return "";

		switch (identifier) {
			case "balance":
				try {
					return String.valueOf(CoinSystemPlugin.instance.readBalance(player.getUniqueId()).get());
				} catch (InterruptedException | ExecutionException ignored) {}
				return "0";
		}
		return super.onPlaceholderRequest(player, identifier);
	}
}
