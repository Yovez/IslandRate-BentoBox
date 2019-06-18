package com.yovez.islandrate.command;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.yovez.islandrate.IslandRate;
import com.yovez.islandrate.menu.IslandMenu;
import com.yovez.islandrate.menu.RateMenu;
import com.yovez.islandrate.menu.TopMenu;
import com.yovez.islandrate.misc.InventoryCheck;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;

public class RateCommand implements CommandExecutor {

	final IslandRate plugin;
	String prefix;
	boolean menu;
	boolean topMenu;
	boolean disableCommand;

	public RateCommand(IslandRate plugin) {
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
				if (args.length == 0) {
					sender.sendMessage("IslandRate console commands:");
					sender.sendMessage(new String[] { "/rate reset <player|all>" });
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("reset")) {
						sender.sendMessage(new String[] { "Try /rate reset <player|all>" });
						return true;
					}
					sender.sendMessage("IslandRate console commands:");
					sender.sendMessage(new String[] { "/rate reset <player|all>" });
					return true;
				}
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("reset")) {
						if (args[1].equalsIgnoreCase("all")) {
							return true;
						}
						@SuppressWarnings("deprecation")
						OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
						if (t == null) {
							sender.sendMessage(args[1] + " is not a valid player. Try /rate reset <player|all>");
							return true;
						}

					}
				}
				return true;
			}
			Player p = (Player) sender;
			String commandUsage = getMessage("command-usage", p, null, 0, 0);
			String noPermission = getMessage("no-permission", p, null, 0, 0);
			String noIsland = getMessage("no-island", p, null, 0, 0);
			String ownedIsland = getMessage("owned-island", p, null, 0, 0);
			String teamIsland = getMessage("team-island", p, null, 0, 0);
			String numberNotFound = getMessage("number-not-found", p, null, 0, 0);
			// String resetUsage = getMessage("reset-usage", p, null, 0, 0);
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
					if (plugin.getAskyblock().getIslandAt(p.getLocation()) == null) {
						p.sendMessage(noIsland);
						return true;
					}
					if (plugin.getAskyblock().getIslandAt(p.getLocation()).get().getOwner().equals(p.getUniqueId())) {
						if (plugin.getConfig().getBoolean("island_menu.enabled", false) == true) {
							IslandMenu im = new IslandMenu(plugin, p);
							im.openInv();
						} else {
							p.sendMessage(ownedIsland);
						}
						return true;
					}
					if (plugin.getAskyblock().userIsOnIsland(p.getWorld(), User.getInstance(p))) {
						p.sendMessage(ownedIsland);
						return true;
					}
					RateMenu rm = new RateMenu(plugin, Bukkit
							.getOfflinePlayer(plugin.getAskyblock().getIslandAt(p.getLocation()).get().getOwner()));
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
				if (args[0].equalsIgnoreCase("invcheck")) {
					if (!p.hasPermission("islandrate.invcheck")) {
						p.sendMessage(noPermission);
						return true;
					}
					InventoryCheck ic = new InventoryCheck(plugin);
					p.sendMessage("§aSuccessfully ran an inv check on all online players...");
					p.sendMessage("§bNumber of Players caught: §e" + ic.runCheck().keySet().size());
					p.sendMessage("§bNumber of Items removed: §e" + ic.runCheck().values().size());
					return true;
				}
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
					} catch (SQLException | ClassNotFoundException e) {
						e.printStackTrace();
						p.sendMessage("§cMigrated from file storage to MySQL/SQLite storage unsuccessfully :("
								+ " Please contact the Developer!");
					} finally {
						p.sendMessage("§aMigrated from file storage to MySQL/SQLite storage successfully!");
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
					plugin.getStorage().reloadConfig();
					setupPrefix();
					p.sendMessage("§aSuccessfully Reloaded IslandRate Configs!");
					plugin.getMySQL();
					return true;
				}

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
					for (int i = 1; i < 11; i++) {
						if (plugin.getAPI().getTotalRatings(plugin.getAPI().getTopRated(i)) == 0)
							break;
						p.sendMessage(getMessage("top.entry", null, plugin.getAPI().getTopRated(i),
								plugin.getAPI().getTotalRatings(plugin.getAPI().getTopRated(i)), i));
					}
					p.sendMessage(topFooter);
					return true;
				}
				if (disableCommand) {
					p.sendMessage(commandDisabled);
					return true;
				}
				if (plugin.getAskyblock().userIsOnIsland(p.getWorld(), User.getInstance(p))) {
					p.sendMessage(ownedIsland);
					return true;
				}
				Island island = plugin.getAskyblock().getIslandAt(p.getLocation()).get();
				if (island == null) {
					p.sendMessage(noIsland);
					return true;
				}
				if (island.getOwner().equals(p.getUniqueId())) {
					p.sendMessage(ownedIsland);
					return true;
				}
				if (island.getMembers().containsKey(p.getUniqueId())) {
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

			} // else if (args.length == 3) {

			else {
				p.sendMessage(commandUsage);
				return true;
			}
		}
		return true;
	}

}
