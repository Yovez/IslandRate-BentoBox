package com.yovez.islandrate.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.yovez.islandrate.IslandRate;
import com.yovez.islandrate.util.DbUtils;

public class IslandRateAPI {

	private static IslandRateAPI instance;

	public static IslandRateAPI getInstance() {
		if (instance == null)
			new IslandRateAPI(IslandRate.getInstance());
		return instance;
	}

	private IslandRate plugin;

	protected IslandRateAPI(IslandRate plugin) {
		this.plugin = plugin;
		instance = this;
	}

	public OfflinePlayer getTopRated() {
		return getTopRated(1);
	}

	public OfflinePlayer getTopRated(int topPlace) {
		OfflinePlayer op = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = plugin.getMySQL().getConnection();
			ps = conn.prepareStatement("SELECT * FROM island_owners ORDER BY total_ratings DESC LIMIT "
					+ String.valueOf(topPlace - 1) + ", 1;");
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

	public double getAverageRating(OfflinePlayer p) {
		if (p == null)
			return 0.0;
		if (getTotalNumOfRaters(p) == 0)
			return getTotalRatings(p);
		return getTotalRatings(p) / getTotalNumOfRaters(p);
	}

	public int getTotalNumOfRaters(OfflinePlayer p) {
		if (p == null)
			return 0;
		int raters = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = plugin.getMySQL().getConnection();
			ps = conn.prepareStatement("SELECT * FROM island_ratings WHERE player_uuid = ?;");
			ps.setString(1, p.getUniqueId().toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				raters++;
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
		return raters;
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
