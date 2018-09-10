package com.yovez.islandrate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class IslandRateAPI {

	private static IslandRateAPI instance;

	public static IslandRateAPI getInstance() {
		if (instance == null)
			new IslandRateAPI(Main.getPlugin());
		return instance;
	}

	private Main plugin;

	protected IslandRateAPI(Main plugin) {
		this.plugin = plugin;
		instance = this;
	}

	/**
	 * Get the top rated player
	 * 
	 * @return top rated player (OfflinePlayer)
	 * @since 1.3.2.0
	 */

	public OfflinePlayer getTopRated() {
		return getTopRated(1);
	}

	/**
	 * Gets the top rated player from a specific place/position
	 * 
	 * @param topPlace place/position of the top rated list to retrieve from
	 * @return top rated player via topPlace
	 * @since 1.3.2.0
	 */

	public OfflinePlayer getTopRated(int topPlace) {
		OfflinePlayer op = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = plugin.getMySQL().getConnection();
			ps = conn.prepareStatement("SELECT * FROM island_owners ORDER BY total_ratings DESC LIMIT "
					+ String.valueOf(topPlace - 1) + ", 1;");
			ps.executeQuery();
			rs = ps.executeQuery();
			if (rs.next()) {
				if (UUID.fromString(rs.getString("player_uuid")) == null)
					return null;
				return Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("player_uuid")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtils.close(rs);
				DbUtils.close(ps);
				DbUtils.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return op;
	}

	/**
	 * Get the total amount of ratings on the server
	 * 
	 * @return the sum of all player's ratings
	 * @since 1.3.2.0
	 */

	public int getTotalRatings() {
		int votes = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = plugin.getMySQL().getConnection();
			ps = conn.prepareStatement("SELECT total_ratings FROM island_owners WHERE total_ratings > 0;");
			rs = ps.executeQuery();
			while (rs.next()) {
				votes += rs.getInt("total_ratings");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtils.close(rs);
				DbUtils.close(ps);
				DbUtils.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return votes;
	}

	/**
	 * Get the total ratings of a specific player
	 * 
	 * @param p the player to get total ratings of
	 * @return the sum of the ratings of the player
	 * @since 1.3.2.0
	 */

	public int getTotalRatings(OfflinePlayer p) {
		if (p == null)
			return 0;
		int votes = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = plugin.getMySQL().getConnection();
			ps = conn.prepareStatement("SELECT * FROM island_owners WHERE player_uuid = ?;");
			ps.setString(1, p.getUniqueId().toString());
			rs = ps.executeQuery();
			if (rs.next())
				votes = rs.getInt("total_ratings");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtils.close(rs);
				DbUtils.close(ps);
				DbUtils.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return votes;
	}

	/**
	 * Get the average rating of a specific player
	 * 
	 * @param p the player to get total ratings of
	 * @return the average of the total ratings
	 * @since 1.3.2.5
	 */

	public double getAverageRating(OfflinePlayer p) {
		if (p == null)
			return 0.0;
		if (getTotalNumOfVoters(p) == 0)
			return getTotalRatings(p);
		return (double) (getTotalRatings(p) / getTotalNumOfVoters(p));
	}

	public int getTotalNumOfVoters(OfflinePlayer p) {
		if (p == null)
			return 0;
		int voters = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = plugin.getMySQL().getConnection();
			ps = conn.prepareStatement("SELECT * FROM island_ratings WHERE player_uuid = ?;");
			ps.setString(1, p.getUniqueId().toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				voters++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtils.close(rs);
				DbUtils.close(ps);
				DbUtils.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return voters;
	}

	/**
	 * Simple method to check if a String is an Integer
	 * 
	 * @param s the string to check
	 * @return true/false depending if the String is an Integer
	 * @since 1.3.2.0
	 */

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
