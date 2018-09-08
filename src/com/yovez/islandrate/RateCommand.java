package com.yovez.islandrate;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wasteofplastic.askyblock.Island;

public class RateCommand implements CommandExecutor {

	final Main plugin;
	String prefix;
	boolean menu;
	boolean topMenu;
	boolean disableCommand;

	public RateCommand(Main plugin) {
		this.plugin = plugin;
		setupPrefix();
	}

	private void setupPrefix() {
		prefix = ChatColor.translateAlternateColorCodes('&', plugin.getMessages().getConfig().getString("prefix"));
		menu = plugin.getConfig().getBoolean("menu.enabled", false);
		topMenu = plugin.getConfig().getBoolean("top_menu.enabled", false);
		disableCommand = plugin.getConfig().getBoolean("disable-command-rating", false);
	}

	private String getMessage(String path, Player p, OfflinePlayer t, int rating, int topPlace) {
		return plugin.getMessage(path, p, t, rating, topPlace);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rate")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You need to be a player to rate islands!");
				return true;
			}
			Player p = (Player) sender;
			String commandUsage = getMessage("command-usage", p, null, 0, 0);
			String noPermission = getMessage("no-permission", p, null, 0, 0);
			String noIsland = getMessage("no-island", p, null, 0, 0);
			String ownedIsland = getMessage("owned-island", p, null, 0, 0);
			String teamIsland = getMessage("team-island", p, null, 0, 0);
			String numberNotFound = getMessage("number-not-found", p, null, 0, 0);
			String resetUsage = getMessage("reset-usage", p, null, 0, 0);
			// String resetAll = getMessage("reset-all", p, null, 0, 0);
			// String resetPlayerNotFound = getMessage("reset-player-not-found", p, null, 0,
			// 0);
			String topHeader = getMessage("top.header", p, null, 0, 0);
			String topFooter = getMessage("top.footer", p, null, 0, 0);
			String topNoTop = getMessage("top.no-top", p, null, 0, 0);
			String commandDisabled = getMessage("command-disabled", p, null, 0, 0);
			if (!p.hasPermission("islandrate.use")) {
				p.sendMessage(noPermission);
				return true;
			}
			if (args.length == 0) {
				if (menu) {
					if (!p.getLocation().getWorld().getName()
							.equals(plugin.getAskyblock().getIslandWorld().getName())) {
						p.sendMessage(noIsland);
						return true;
					}
					if (plugin.getAskyblock().getOwner(p.getLocation()) == null) {
						p.sendMessage(noIsland);
						return true;
					}
					if (plugin.getAskyblock().getIslandAt(p.getLocation()).getOwner().equals(p.getUniqueId())) {
						if (plugin.getConfig().getBoolean("island_menu.enabled", false) == true) {
							IslandMenu im = new IslandMenu(plugin, p);
							im.openInv();
						} else {
							p.sendMessage(ownedIsland);
						}
						return true;
					}
					RateMenu rm = new RateMenu(plugin,
							Bukkit.getOfflinePlayer(plugin.getAskyblock().getIslandAt(p.getLocation()).getOwner()));
					if (plugin.getConfig().getBoolean("menu.custom", false) == false)
						rm.openInv(p);
					else
						rm.openCustomInv(p);
					return true;
				}
				p.sendMessage(commandUsage);
				return true;
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("average")) {
					if (!p.hasPermission("islandrate.average")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage(getMessage("average-rating", p, null, 0, 0));
					return true;
				}
				if (args[0].equalsIgnoreCase("total")) {
					if (!p.hasPermission("islandrate.total")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage(getMessage("total-ratings", p, null, 0, 0));
					return true;
				}
				if (args[0].equalsIgnoreCase("migrate")) {
					if (!p.hasPermission("islandrate.migrate")) {
						p.setDisplayName(noPermission);
						return true;
					}
					try {
						plugin.getMySQL().convertFromFile();
						p.sendMessage("§aMigrated from file storage to MySQL/SQLite storage successfully!");
					} catch (SQLException | ClassNotFoundException e) {
						e.printStackTrace();
						p.sendMessage("§cMigrated from file storage to MySQL/SQLite storage unsuccessfully :("
								+ " Please contact the Developer!");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("reload")) {
					if (!p.hasPermission("islandrate.reload")) {
						p.sendMessage(noPermission);
						return true;
					}
					plugin.reloadConfig();
					plugin.getMessages().reloadConfig();
					plugin.getOptOut().reloadConfig();
					setupPrefix();
					p.sendMessage("§aSuccessfully Reloaded IslandRate Configs!");
					plugin.getMySQL();
					return true;
				}
				if (args[0].equalsIgnoreCase("reset")) {
					if (!p.hasPermission("islandrate.reset")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage(resetUsage);
					return true;
				}
				if (args[0].equalsIgnoreCase("add")) {
					if (!p.hasPermission("islandrate.admin.add")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate add <player> <# of stars>");
					return true;
				}
				if (args[0].equalsIgnoreCase("take")) {
					if (!p.hasPermission("islandrate.admin.take")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate take <player> <# of stars>");
					return true;
				}
				if (args[0].equalsIgnoreCase("set")) {
					if (!p.hasPermission("islandrate.admin.set")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate set <player> <# of stars>");
					return true;
				}
				/*
				 * if (args[0].equalsIgnoreCase("inftop")) { if
				 * (!p.hasPermission("islandrate.infinitetop")) { p.sendMessage(noPermission);
				 * return true; } InfiniteTopMenu itm = new InfiniteTopMenu(plugin);
				 * itm.openInv(p); return true; }
				 */
				if (args[0].equalsIgnoreCase("top")) {
					if (!p.hasPermission("islandrate.top")) {
						p.sendMessage(noPermission);
						return true;
					}
					if (plugin.getAPI().getTopRated() == null) {
						p.sendMessage(topNoTop);
						return true;
					}
					if (topMenu) {
						TopMenu tm = new TopMenu(plugin);
						tm.openInv(p);
						return true;
					}
					p.sendMessage(topHeader);
					// try {
					for (int i = 1; i < 11; i++) {
						if (plugin.getAPI().getTotalRatings(plugin.getAPI().getTopRated(i)) == 0)
							break;
						p.sendMessage(getMessage("top.entry", null, plugin.getAPI().getTopRated(i),
								plugin.getAPI().getTotalRatings(plugin.getAPI().getTopRated(i)), i));
					}
					p.sendMessage(topFooter);
					// } catch (SQLException e) {
					// e.printStackTrace();
					// }
					return true;
				}
				if (disableCommand) {
					p.sendMessage(commandDisabled);
					return true;
				}
				if (!p.getLocation().getWorld().getName().equals(plugin.getAskyblock().getIslandWorld().getName())) {
					p.sendMessage(noIsland);
					return true;
				}
				if (plugin.getAskyblock().getOwner(p.getLocation()) == null) {
					p.sendMessage(noIsland);
					return true;
				}
				if (plugin.getAskyblock().getIslandAt(p.getLocation()).getOwner().equals(p.getUniqueId())) {
					p.sendMessage(ownedIsland);
					return true;
				}
				Island island = plugin.getAskyblock().getIslandAt(p.getLocation());
				if (island == null) {
					p.sendMessage(noIsland);
					return true;
				}
				if (island.getMembers().contains(p.getUniqueId())) {
					p.sendMessage(teamIsland);
					return true;
				}
				if (!plugin.getAPI().isInt(args[0])) {
					p.sendMessage(numberNotFound);
					return true;
				}
				if (Integer.parseInt(args[0]) <= 0
						|| Integer.parseInt(args[0]) > plugin.getConfig().getInt("max-command-rating", 5)) {
					p.sendMessage(commandUsage);
					return true;
				}
				plugin.rateIsland(p, Bukkit.getOfflinePlayer(island.getOwner()), Integer.parseInt(args[0]));
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("average")) {
					if (!p.hasPermission("islandrate.average")) {
						p.sendMessage(noPermission);
						return true;
					}
					@SuppressWarnings("deprecation")
					OfflinePlayer t = Bukkit.getServer().getOfflinePlayer(args[1]);
					if (t == null) {
						p.sendMessage(getMessage("average-player-not-found", p, null, 0, 0));
						return true;
					}
					p.sendMessage(getMessage("average-rating-target", p, t, 0, 0));
					return true;
				}
				if (args[0].equalsIgnoreCase("total")) {
					if (!p.hasPermission("islandrate.total.other")) {
						p.sendMessage(noPermission);
						return true;
					}
					@SuppressWarnings("deprecation")
					OfflinePlayer t = Bukkit.getServer().getOfflinePlayer(args[1]);
					if (t == null) {
						p.sendMessage(getMessage("total-player-not-found", p, null, 0, 0));
						return true;
					}
					p.sendMessage(getMessage("total-ratings-other", p, t, 0, 0));
					return true;
				}
				if (args[0].equalsIgnoreCase("add")) {
					if (!p.hasPermission("islandrate.admin.add")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate add <player> <# of stars>");
					return true;
				}
				if (args[0].equalsIgnoreCase("take")) {
					if (!p.hasPermission("islandrate.admin.take")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate take <player> <# of stars>");
					return true;
				}
				if (args[0].equalsIgnoreCase("set")) {
					if (!p.hasPermission("islandrate.admin.set")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate set <player> <# of stars>");
					return true;
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("add")) {
					if (!p.hasPermission("islandrate.admin.add")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate add <player> <# of stars>");
					return true;
				}
				if (args[0].equalsIgnoreCase("take")) {
					if (!p.hasPermission("islandrate.admin.take")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate take <player> <# of stars>");
					return true;
				}
				if (args[0].equalsIgnoreCase("set")) {
					if (!p.hasPermission("islandrate.admin.set")) {
						p.sendMessage(noPermission);
						return true;
					}
					p.sendMessage("§cCorrect usage is /rate set <player> <# of stars>");
					return true;
				}
			} else {
				p.sendMessage(commandUsage);
				return true;
			}
		}
		return true;
	}

}
