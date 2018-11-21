package com.yovez.islandrate.util;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.yovez.islandrate.IslandRate;

public class Parser {

	private static IslandRate plugin;

	public Parser() {
		plugin = IslandRate.getInstance();
	}

	private static FileConfiguration getConfig() {
		return plugin.getConfig();
	}

	public static String getMessage(String msg, Player p, OfflinePlayer t, int rating, int topPlace) {
		if (plugin == null)
			return "null";
		if (getConfig().getString(msg) != null)
			msg = getConfig().getString(msg);
		else if (plugin.getMessages().getConfig().getString(msg) != null)
			msg = plugin.getMessages().getConfig().getString(msg);
		msg = msg.replaceAll("%prefix%", plugin.getMessages().getConfig().getString("prefix"));
		if (p != null) {
			if (msg.contains("%player%"))
				msg = msg.replaceAll("%player%", p.getName());
			if (msg.contains("%player-stars%"))
				msg = msg.replaceAll("%player-stars%", String.valueOf(plugin.getAPI().getTotalRatings(p)));
			if (msg.contains("%player-average%"))
				msg = msg.replaceAll("%player-average%", String.valueOf(plugin.getAPI().getAverageRating(p)));
			if (msg.contains("%player-total-voters%"))
				msg = msg.replaceAll("%player-total-voters%", String.valueOf(plugin.getAPI().getTotalNumOfRaters(p)));
			if (msg.contains("%cooldown%"))
				msg = msg.replaceAll("%cooldown%",
						String.valueOf(plugin.getCooldowns().get(p.getUniqueId()) + getConfig().getInt("cooldown")
								- TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
			if (msg.contains("%opted-out-player%"))
				msg = msg.replaceAll("%opted-out-player%",
						plugin.getOptOut().getConfig().getBoolean(p.getUniqueId().toString(), false) ? "True"
								: "False");
		}
		if (t != null) {
			if (msg.contains("%target%"))
				msg = msg.replaceAll("%target%", t.getName());
			if (msg.contains("%target-stars%"))
				msg = msg.replaceAll("%target-stars%", String.valueOf(plugin.getAPI().getTotalRatings(t)));
			if (msg.contains("%target-average%"))
				msg = msg.replaceAll("%target-average%", String.valueOf(plugin.getAPI().getAverageRating(t)));
			if (msg.contains("%target-total-voters%"))
				msg = msg.replaceAll("%target-total-voters%", String.valueOf(plugin.getAPI().getTotalNumOfRaters(t)));
			if (msg.contains("%cooldown%"))
				msg = msg.replaceAll("%cooldown%",
						String.valueOf(plugin.getCooldowns().get(t.getUniqueId()) + getConfig().getInt("cooldown")
								- TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
			if (msg.contains("%opted-out-target%"))
				msg = msg.replaceAll("%opted-out-target%",
						plugin.getOptOut().getConfig().getBoolean(t.getUniqueId().toString(), false) ? "True"
								: "False");
		}
		if (rating > 0)
			msg = msg.replaceAll("%rating%", String.valueOf(rating));
		msg = msg.replaceAll("%top-place%", String.valueOf(topPlace));
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

}
