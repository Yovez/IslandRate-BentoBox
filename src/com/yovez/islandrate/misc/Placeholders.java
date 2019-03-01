package com.yovez.islandrate.misc;

import org.bukkit.entity.Player;

import com.yovez.islandrate.IslandRate;
import com.yovez.islandrate.api.IslandRateAPI;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;

public class Placeholders extends PlaceholderHook {

	IslandRate plugin;

	public Placeholders(IslandRate plugin) {
		this.plugin = plugin;
		if (plugin.getConfig().getBoolean("placeholderapi_shortened", false) == true)
			PlaceholderAPI.registerPlaceholderHook("ir", this);
		else
			PlaceholderAPI.registerPlaceholderHook("islandrate", this);
	}

	@Override
	public String onPlaceholderRequest(Player p, String label) {
		IslandRateAPI api = IslandRateAPI.getInstance();
		if (label.equalsIgnoreCase("top_rated_player")) {
			if (api.getTopRated() == null) {
				return "No top player found";
			}
			return api.getTopRated().getName();
		}
		if (label.startsWith("top_rated_player_"))
			if (api.isInt(String.valueOf(label.charAt(label.length() - 1)))) {
				int num = Character.getNumericValue(label.charAt(label.length() - 1));
				return String.valueOf(api.getTopRated(num).getName());
			}
		if (label.startsWith("top_rated_amount_"))
			if (api.isInt(String.valueOf(label.charAt(label.length() - 1)))) {
				int num = Character.getNumericValue(label.charAt(label.length() - 1));
				return String.valueOf(api.getTotalRatings(api.getTopRated(num)));
			}
		if (label.equalsIgnoreCase("total_voters"))
			return String.valueOf(api.getTotalNumOfRaters(p));
		if (label.equalsIgnoreCase("top_rated_amount"))
			return String.valueOf(api.getTotalRatings(api.getTopRated()));
		if (label.startsWith("top_rated_amount_"))
			if (api.isInt(String.valueOf(label.charAt(label.length())))) {
				if (label.charAt(label.length()) == 0)
					return String.valueOf(api.getTopRated(10));
				return String.valueOf(api.getTopRated(label.charAt(label.length())));
			}
		if (label.equalsIgnoreCase("average_rating"))
			return String.valueOf(api.getAverageRating(p));
		if (label.equalsIgnoreCase("total_ratings_server"))
			return String.valueOf(api.getTotalRatings());
		if (label.equalsIgnoreCase("total_ratings_player"))
			return String.valueOf(api.getTotalRatings(p));
		return null;
	}

}
