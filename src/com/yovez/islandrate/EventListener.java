package com.yovez.islandrate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.wasteofplastic.askyblock.Island;

public class EventListener implements Listener {

	final Main plugin;

	public EventListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignCreate(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[islandrate]")) {
			e.setLine(0, "§8[§aIslandRate§8]");
			if (e.getLine(1).equalsIgnoreCase("menu")) {
				if (!e.getPlayer().hasPermission("islandrate.sign.create.menu")) {
					e.getPlayer().sendMessage(plugin.getMessage("no-permission", e.getPlayer(), null, 0, 0));
					return;
				}
				if (plugin.getAskyblock().getOwner(e.getBlock().getLocation()) == null) {
					e.setLine(1, plugin.getMessage("sign.no-island-found.line-1", e.getPlayer(), null, 0, 0));
					e.setLine(2, plugin.getMessage("sign.no-island-found.line-2", e.getPlayer(), null, 0, 0));
					e.setLine(3, plugin.getMessage("sign.no-island-found.line-3", e.getPlayer(), null, 0, 0));
					return;
				}
				e.setLine(1, plugin.getMessage("sign.open-rate-menu.line-1", e.getPlayer(), null, 0, 0));
				e.setLine(2, plugin.getMessage("sign.open-rate-menu.line-2", e.getPlayer(), null, 0, 0));
				e.setLine(3, plugin.getMessage("sign.open-rate-menu.line-3", e.getPlayer(), null, 0, 0));
			} else if (e.getLine(1).equalsIgnoreCase("topmenu")) {
				if (!e.getPlayer().hasPermission("islandrate.sign.create.topmenu")) {
					e.getPlayer().sendMessage(plugin.getMessage("no-permission", e.getPlayer(), null, 0, 0));
					return;
				}
				e.setLine(1, plugin.getMessage("sign.open-top-menu.line-1", e.getPlayer(), null, 0, 0));
				e.setLine(2, plugin.getMessage("sign.open-top-menu.line-2", e.getPlayer(), null, 0, 0));
				e.setLine(3, plugin.getMessage("sign.open-top-menu.line-3", e.getPlayer(), null, 0, 0));
			} else if (e.getLine(1).equalsIgnoreCase("toplist")) {
				if (!e.getPlayer().hasPermission("islandrate.sign.create.toplist")) {
					e.getPlayer().sendMessage(plugin.getMessage("no-permission", e.getPlayer(), null, 0, 0));
					return;
				}
				e.setLine(1, plugin.getMessage("sign.show-top-list.line-1", e.getPlayer(), null, 0, 0));
				e.setLine(2, plugin.getMessage("sign.show-top-list.line-2", e.getPlayer(), null, 0, 0));
				e.setLine(3, plugin.getMessage("sign.show-top-list.line-3", e.getPlayer(), null, 0, 0));
			} else if (e.getLine(1).isEmpty() && e.getLine(2).isEmpty() && e.getLine(3).isEmpty()) {
				if (plugin.getAskyblock().getOwner(e.getBlock().getLocation()) != null) {
					if (!e.getPlayer().hasPermission("islandrate.sign.create.rate")) {
						e.getPlayer().sendMessage(plugin.getMessage("no-permission", e.getPlayer(), null, 0, 0));
						return;
					}
					if (plugin.getAskyblock().getOwner(e.getBlock().getLocation()) == null) {
						e.getPlayer().sendMessage(plugin.getMessage("other-error", e.getPlayer(), null, 0, 0));
						return;
					}
					e.setLine(1, plugin.getMessage("sign.rate-island.line-1", e.getPlayer(),
							Bukkit.getOfflinePlayer(plugin.getAskyblock().getOwner(e.getBlock().getLocation())), 0, 0));
					e.setLine(2, plugin.getMessage("sign.rate-island.line-2", e.getPlayer(),
							Bukkit.getOfflinePlayer(plugin.getAskyblock().getOwner(e.getBlock().getLocation())), 0, 0));
					e.setLine(3, plugin.getMessage("sign.rate-island.line-3", e.getPlayer(),
							Bukkit.getOfflinePlayer(plugin.getAskyblock().getOwner(e.getBlock().getLocation())), 0, 0));
				} else {
					e.setLine(1, plugin.getMessage("sign.no-island-found.line-1", e.getPlayer(), null, 0, 0));
					e.setLine(2, plugin.getMessage("sign.no-island-found.line-2", e.getPlayer(), null, 0, 0));
					e.setLine(3, plugin.getMessage("sign.no-island-found.line-3", e.getPlayer(), null, 0, 0));
				}
			}
		}
	}

	@EventHandler
	public void onSignInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getType().equals(Material.SIGN_POST)
					|| e.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				if (sign.getLine(0).equals("§8[§aIslandRate§8]")) {
					if (sign.getLine(1)
							.equals(plugin.getMessage("sign.open-rate-menu.line-1", e.getPlayer(), null, 0, 0))) {
						if (!e.getPlayer().hasPermission("islandrate.sign.use.menu")) {
							e.getPlayer().sendMessage(plugin.getMessage("no-permission", e.getPlayer(), null, 0, 0));
							return;
						}
						RateMenu menu = null;
						if (plugin.getAskyblock().getOwner(sign.getLocation()) == null) {
							e.getPlayer().sendMessage(plugin.getMessage("no-island", e.getPlayer(), null, 0, 0));
							return;
						}
						menu = new RateMenu(plugin,
								Bukkit.getOfflinePlayer(plugin.getAskyblock().getOwner(sign.getLocation())));
						if (e.getPlayer().getUniqueId().toString()
								.equalsIgnoreCase(menu.getPlayer().getUniqueId().toString())) {
							e.getPlayer().sendMessage(plugin.getMessage("owned-island", e.getPlayer(), null, 0, 0));
							return;
						}
						if (plugin.getConfig().getBoolean("menu.custom", false) == false)
							menu.openInv(e.getPlayer());
						else
							menu.openCustomInv(e.getPlayer());
						return;
					}
					if (sign.getLine(1)
							.equals(plugin.getMessage("sign.open-top-menu.line-1", e.getPlayer(), null, 0, 0))) {
						if (!e.getPlayer().hasPermission("islandrate.sign.use.topmenu")) {
							e.getPlayer().sendMessage(plugin.getMessage("no-permission", e.getPlayer(), null, 0, 0));
							return;
						}
						TopMenu menu = new TopMenu(plugin);
						menu.openInv(e.getPlayer());
						return;
					}
					if (sign.getLine(1)
							.equals(plugin.getMessage("sign.show-top-list.line-1", e.getPlayer(), null, 0, 0))) {
						if (!e.getPlayer().hasPermission("islandrate.sign.use.toplist")) {
							e.getPlayer().sendMessage(plugin.getMessage("no-permission", e.getPlayer(), null, 0, 0));
							return;
						}
						if (plugin.getAPI().getTopRated() == null) {
							e.getPlayer().sendMessage(plugin.getMessage("top.no-top", e.getPlayer(), null, 0, 0));
							return;
						}
						e.getPlayer().sendMessage(plugin.getMessage("top.header", e.getPlayer(), null, 0, 0));
						for (int i = 1; i < 11; i++) {
							if (plugin.getAPI().getTotalRatings(plugin.getAPI().getTopRated(i)) == 0) {
								break;
							}
							e.getPlayer()
									.sendMessage(plugin.getMessage("top.entry", null, plugin.getAPI().getTopRated(i),
											plugin.getAPI().getTotalRatings(plugin.getAPI().getTopRated(i)), i));
						}
						e.getPlayer().sendMessage(plugin.getMessage("top.footer", e.getPlayer(), null, 0, 0));
						return;
					}
					if (sign.getLine(1)
							.equals(plugin.getMessage("sign.rate-island.line-1", e.getPlayer(), null, 0, 0))) {
						if (plugin.getAskyblock().getOwner(sign.getLocation()) != null) {
							if (sign.getLine(2)
									.equals(plugin.getMessage("sign.rate-island.line-2", e.getPlayer(),
											Bukkit.getOfflinePlayer(plugin.getAskyblock().getOwner(sign.getLocation())),
											0, 0))) {
								if (sign.getLine(3).equals(
										plugin.getMessage("sign.rate-island.line-3", e.getPlayer(), null, 0, 0))) {
									if (!e.getPlayer().hasPermission("islandrate.sign.use.rate")) {
										e.getPlayer().sendMessage(
												plugin.getMessage("no-permission", e.getPlayer(), null, 0, 0));
										return;
									}
									RateMenu menu = new RateMenu(plugin, Bukkit
											.getOfflinePlayer(plugin.getAskyblock().getOwner(sign.getLocation())));
									if (e.getPlayer().getUniqueId().toString()
											.equalsIgnoreCase(menu.getPlayer().getUniqueId().toString())) {
										e.getPlayer().sendMessage(
												plugin.getMessage("owned-island", e.getPlayer(), null, 0, 0));
										return;
									}
									if (plugin.getConfig().getBoolean("menu.custom", false) == false)
										menu.openInv(e.getPlayer());
									else
										menu.openCustomInv(e.getPlayer());
									return;
								}
							} else {
								e.getPlayer().sendMessage(plugin.getMessage("other-error", e.getPlayer(), null, 0, 0));
								Bukkit.getConsoleSender()
										.sendMessage("§2[IslandRate] §4WARNING: §cAn error occured when "
												+ e.getPlayer().getName()
												+ " tried interacting with a SIGN located at §6X:"
												+ sign.getLocation().getBlockX() + " Y:"
												+ sign.getLocation().getBlockY() + " Z:"
												+ sign.getLocation().getBlockZ());
								return;
							}
						} else {
							e.getPlayer().sendMessage(plugin.getMessage("no-island", e.getPlayer(), null, 0, 0));
							return;
						}
					}

				}
			}
		}

	}

	@EventHandler
	public void onMenuClick(InventoryClickEvent e) {
		if (e.getInventory() == null)
			return;
		if (e.getClickedInventory() == null)
			return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (e.getWhoClicked() == null)
			return;
		if (!(e.getWhoClicked() instanceof Player))
			return;
		if (e.getInventory().getType().equals(InventoryType.CREATIVE))
			return;
		if (e.getClickedInventory().getType().equals(InventoryType.CREATIVE))
			return;
		Player p = (Player) e.getWhoClicked();
		if (plugin.getAskyblock().getOwner(p.getLocation()) == null) {
			return;
		}
		Island island = plugin.getAskyblock().getIslandOwnedBy(plugin.getAskyblock().getOwner(p.getLocation()));
		OfflinePlayer op = Bukkit.getOfflinePlayer(island.getOwner());
		RateMenu menu = new RateMenu(plugin, op);
		if (!e.getInventory().getTitle().equals(menu.getInv().getTitle())) {
			return;
		}
		e.setCancelled(true);
		Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {

			@Override
			public void run() {
				if (plugin.getConfig().getBoolean("menu.custom", false) == false)
					menu.openInv(p);
				else
					menu.openCustomInv(p);
			}

		});
		ConfigItem item = new ConfigItem(plugin, p);
		if (item.getItems().containsKey(e.getCurrentItem()))
			if (item.getItems().get(e.getCurrentItem()) > 0)
				plugin.rateIsland(p, op, item.getItems().get(e.getCurrentItem()));

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onTopMenuClick(InventoryClickEvent e) {
		if (e.getInventory() == null)
			return;
		if (e.getClickedInventory() == null)
			return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (e.getWhoClicked() == null)
			return;
		if (!(e.getWhoClicked() instanceof Player))
			return;
		if (e.getInventory().getType().equals(InventoryType.CREATIVE))
			return;
		if (e.getClickedInventory().getType().equals(InventoryType.CREATIVE))
			return;
		TopMenu menu = new TopMenu(plugin);
		if (!e.getInventory().getTitle().equals(menu.getInv().getTitle())) {
			return;
		}
		e.setCancelled(true);
		if (plugin.getConfig().getBoolean("top_menu.teleport", false) == true) {
			SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
			Location loc;
			if (!Bukkit.getVersion().contains("1.12")) {
				loc = plugin.getAskyblock().getHomeLocation(Bukkit.getOfflinePlayer(meta.getOwner()).getUniqueId());
			} else
				loc = plugin.getAskyblock().getHomeLocation(meta.getOwningPlayer().getUniqueId());
			if (loc != null) {
				e.getWhoClicked().teleport(loc);
			}
		}
	}

	@EventHandler
	public void onIslandMenuClick(InventoryClickEvent e) {
		if (e.getInventory() == null)
			return;
		if (e.getClickedInventory() == null)
			return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (e.getWhoClicked() == null)
			return;
		if (!(e.getWhoClicked() instanceof Player))
			return;
		if (e.getInventory().getType().equals(InventoryType.CREATIVE))
			return;
		if (e.getClickedInventory().getType().equals(InventoryType.CREATIVE))
			return;
		Player p = (Player) e.getWhoClicked();
		IslandMenu im = new IslandMenu(plugin, p);
		if (!e.getInventory().getTitle().equals(im.getInv().getTitle())) {
			return;
		}
		e.setCancelled(true);
		Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {

			@Override
			public void run() {
				if (plugin.getConfig().getBoolean("island_menu.custom", false) == false)
					im.openInv();
				else
					im.openCustomInv();
			}
		});
		ItemStack item = e.getCurrentItem();
		if (item.equals(im.getOptOut())) {
			plugin.getOptOut().getConfig().set(p.getUniqueId().toString(),
					!plugin.getOptOut().getConfig().getBoolean(p.getUniqueId().toString(), false));
			plugin.getOptOut().saveConfig();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInfTopMenuClick(InventoryClickEvent e) {
		if (e.getInventory() == null)
			return;
		if (e.getClickedInventory() == null)
			return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (e.getWhoClicked() == null)
			return;
		if (!(e.getWhoClicked() instanceof Player))
			return;
		if (e.getInventory().getType().equals(InventoryType.CREATIVE))
			return;
		if (e.getClickedInventory().getType().equals(InventoryType.CREATIVE))
			return;
		InfiniteTopMenu menu = new InfiniteTopMenu(plugin);
		if (!e.getInventory().getTitle().equals(menu.getInv().getTitle())) {
			return;
		}
		e.setCancelled(true);
		if (plugin.getConfig().getBoolean("infinite_top_menu.teleport", false) == true) {
			SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
			Location loc;
			if (!Bukkit.getVersion().contains("1.12")) {
				loc = plugin.getAskyblock().getHomeLocation(Bukkit.getOfflinePlayer(meta.getOwner()).getUniqueId());
			} else
				loc = plugin.getAskyblock().getHomeLocation(meta.getOwningPlayer().getUniqueId());
			if (loc != null) {
				e.getWhoClicked().teleport(loc);
			}
		}
	}
}
