package com.yovez.islandrate.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World.Environment;
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

import com.yovez.islandrate.IslandRate;
import com.yovez.islandrate.menu.IslandMenu;
import com.yovez.islandrate.menu.RateMenu;
import com.yovez.islandrate.menu.TopMenu;
import com.yovez.islandrate.misc.ConfigItem;

import world.bentobox.bentobox.database.objects.Island;

public class EventListener implements Listener {

	final IslandRate addon;

	public EventListener(IslandRate addon) {
		this.addon = addon;
	}

	@EventHandler
	public void onSignCreate(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[islandrate]")) {
			e.setLine(0, "§8[§aIslandRate§8]");
			if (e.getLine(1).equalsIgnoreCase("menu")) {
				if (!e.getPlayer().hasPermission("islandrate.sign.create.menu")) {
					e.getPlayer().sendMessage(addon.getMessage("no-permission", e.getPlayer(), null, 0, 0));
					return;
				}
				if (addon.getIslands().getIslandAt(e.getBlock().getLocation()).get().getOwner() == null) {
					e.setLine(1, addon.getMessage("sign.no-island-found.line-1", e.getPlayer(), null, 0, 0));
					e.setLine(2, addon.getMessage("sign.no-island-found.line-2", e.getPlayer(), null, 0, 0));
					e.setLine(3, addon.getMessage("sign.no-island-found.line-3", e.getPlayer(), null, 0, 0));
					return;
				}
				e.setLine(1, addon.getMessage("sign.open-rate-menu.line-1", e.getPlayer(), null, 0, 0));
				e.setLine(2, addon.getMessage("sign.open-rate-menu.line-2", e.getPlayer(), null, 0, 0));
				e.setLine(3, addon.getMessage("sign.open-rate-menu.line-3", e.getPlayer(), null, 0, 0));
			} else if (e.getLine(1).equalsIgnoreCase("topmenu")) {
				if (!e.getPlayer().hasPermission("islandrate.sign.create.topmenu")) {
					e.getPlayer().sendMessage(addon.getMessage("no-permission", e.getPlayer(), null, 0, 0));
					return;
				}
				e.setLine(1, addon.getMessage("sign.open-top-menu.line-1", e.getPlayer(), null, 0, 0));
				e.setLine(2, addon.getMessage("sign.open-top-menu.line-2", e.getPlayer(), null, 0, 0));
				e.setLine(3, addon.getMessage("sign.open-top-menu.line-3", e.getPlayer(), null, 0, 0));
			} else if (e.getLine(1).equalsIgnoreCase("toplist")) {
				if (!e.getPlayer().hasPermission("islandrate.sign.create.toplist")) {
					e.getPlayer().sendMessage(addon.getMessage("no-permission", e.getPlayer(), null, 0, 0));
					return;
				}
				e.setLine(1, addon.getMessage("sign.show-top-list.line-1", e.getPlayer(), null, 0, 0));
				e.setLine(2, addon.getMessage("sign.show-top-list.line-2", e.getPlayer(), null, 0, 0));
				e.setLine(3, addon.getMessage("sign.show-top-list.line-3", e.getPlayer(), null, 0, 0));
			} else if (e.getLine(1).isEmpty() && e.getLine(2).isEmpty() && e.getLine(3).isEmpty()) {
				if (addon.getIslands().getIslandAt(e.getBlock().getLocation()).get().getOwner() != null) {
					if (!e.getPlayer().hasPermission("islandrate.sign.create.rate")) {
						e.getPlayer().sendMessage(addon.getMessage("no-permission", e.getPlayer(), null, 0, 0));
						return;
					}
					if (addon.getIslands().getIslandAt(e.getBlock().getLocation()).get().getOwner() == null) {
						e.getPlayer().sendMessage(addon.getMessage("other-error", e.getPlayer(), null, 0, 0));
						return;
					}
					e.setLine(1,
							addon.getMessage("sign.rate-island.line-1", e.getPlayer(), Bukkit.getOfflinePlayer(
									addon.getIslands().getIslandAt(e.getBlock().getLocation()).get().getOwner()), 0,
									0));
					e.setLine(2,
							addon.getMessage("sign.rate-island.line-2", e.getPlayer(), Bukkit.getOfflinePlayer(
									addon.getIslands().getIslandAt(e.getBlock().getLocation()).get().getOwner()), 0,
									0));
					e.setLine(3,
							addon.getMessage("sign.rate-island.line-3", e.getPlayer(), Bukkit.getOfflinePlayer(
									addon.getIslands().getIslandAt(e.getBlock().getLocation()).get().getOwner()), 0,
									0));
				} else {
					e.setLine(1, addon.getMessage("sign.no-island-found.line-1", e.getPlayer(), null, 0, 0));
					e.setLine(2, addon.getMessage("sign.no-island-found.line-2", e.getPlayer(), null, 0, 0));
					e.setLine(3, addon.getMessage("sign.no-island-found.line-3", e.getPlayer(), null, 0, 0));
				}
			}
		}
	}

	@EventHandler
	public void onSignInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getType().equals(Material.SIGN)
					|| e.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				if (sign.getLine(0).equals("§8[§aIslandRate§8]")) {
					if (sign.getLine(1)
							.equals(addon.getMessage("sign.open-rate-menu.line-1", e.getPlayer(), null, 0, 0))) {
						if (!e.getPlayer().hasPermission("islandrate.sign.use.menu")) {
							e.getPlayer().sendMessage(addon.getMessage("no-permission", e.getPlayer(), null, 0, 0));
							return;
						}
						RateMenu menu = null;
						if (!addon.getIslands().getIslandAt(sign.getLocation()).isPresent()) {
							e.getPlayer().sendMessage(addon.getMessage("no-island", e.getPlayer(), null, 0, 0));
							return;
						}
						menu = new RateMenu(addon, Bukkit
								.getOfflinePlayer(addon.getIslands().getIslandAt(sign.getLocation()).get().getOwner()));
						if (e.getPlayer().getUniqueId().toString()
								.equalsIgnoreCase(menu.getPlayer().getUniqueId().toString())) {
							e.getPlayer().sendMessage(addon.getMessage("owned-island", e.getPlayer(), null, 0, 0));
							return;
						}
						if (addon.getConfig().getBoolean("menu.custom", false) == false)
							menu.openInv(e.getPlayer());
						else
							menu.openCustomInv(e.getPlayer());
						return;
					}
					if (sign.getLine(1)
							.equals(addon.getMessage("sign.open-top-menu.line-1", e.getPlayer(), null, 0, 0))) {
						if (!e.getPlayer().hasPermission("islandrate.sign.use.topmenu")) {
							e.getPlayer().sendMessage(addon.getMessage("no-permission", e.getPlayer(), null, 0, 0));
							return;
						}
						TopMenu menu = new TopMenu(addon);
						menu.openInv(e.getPlayer());
						return;
					}
					if (sign.getLine(1)
							.equals(addon.getMessage("sign.show-top-list.line-1", e.getPlayer(), null, 0, 0))) {
						if (!e.getPlayer().hasPermission("islandrate.sign.use.toplist")) {
							e.getPlayer().sendMessage(addon.getMessage("no-permission", e.getPlayer(), null, 0, 0));
							return;
						}
						if (addon.getAPI().getTopRated() == null) {
							e.getPlayer().sendMessage(addon.getMessage("top.no-top", e.getPlayer(), null, 0, 0));
							return;
						}
						e.getPlayer().sendMessage(addon.getMessage("top.header", e.getPlayer(), null, 0, 0));
						for (int i = 1; i < 11; i++) {
							if (addon.getAPI().getTotalRatings(addon.getAPI().getTopRated(i)) == 0) {
								break;
							}
							e.getPlayer().sendMessage(addon.getMessage("top.entry", null, addon.getAPI().getTopRated(i),
									addon.getAPI().getTotalRatings(addon.getAPI().getTopRated(i)), i));
						}
						e.getPlayer().sendMessage(addon.getMessage("top.footer", e.getPlayer(), null, 0, 0));
						return;
					}
					if (sign.getLine(1)
							.equals(addon.getMessage("sign.rate-island.line-1", e.getPlayer(), null, 0, 0))) {
						if (addon.getIslands().getIslandAt(sign.getLocation()).isPresent()) {
							if (sign.getLine(2).equals(addon.getMessage("sign.rate-island.line-2", e.getPlayer(),
									Bukkit.getOfflinePlayer(
											addon.getIslands().getIslandAt(sign.getLocation()).get().getOwner()),
									0, 0))) {
								if (sign.getLine(3).equals(
										addon.getMessage("sign.rate-island.line-3", e.getPlayer(), null, 0, 0))) {
									if (!e.getPlayer().hasPermission("islandrate.sign.use.rate")) {
										e.getPlayer().sendMessage(
												addon.getMessage("no-permission", e.getPlayer(), null, 0, 0));
										return;
									}
									RateMenu menu = new RateMenu(addon, Bukkit.getOfflinePlayer(
											addon.getIslands().getIslandAt(sign.getLocation()).get().getOwner()));
									if (e.getPlayer().getUniqueId().toString()
											.equalsIgnoreCase(menu.getPlayer().getUniqueId().toString())) {
										e.getPlayer().sendMessage(
												addon.getMessage("owned-island", e.getPlayer(), null, 0, 0));
										return;
									}
									if (addon.getConfig().getBoolean("menu.custom", false) == false)
										menu.openInv(e.getPlayer());
									else
										menu.openCustomInv(e.getPlayer());
									return;
								}
							} else {
								e.getPlayer().sendMessage(addon.getMessage("other-error", e.getPlayer(), null, 0, 0));
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
							e.getPlayer().sendMessage(addon.getMessage("no-island", e.getPlayer(), null, 0, 0));
							return;
						}
					}

				}
			}
		}

	}

	@EventHandler
	public void onMenuClick(InventoryClickEvent e) {
		/*
		 * if (e.getInventory() == null) return; if (e.getClickedInventory() == null)
		 * return; if (e.getCurrentItem() == null ||
		 * e.getCurrentItem().getType().equals(Material.AIR)) return; if
		 * (e.getWhoClicked() == null) return; if (!(e.getWhoClicked() instanceof
		 * Player)) return; if
		 * (e.getInventory().getType().equals(InventoryType.CREATIVE)) return; if
		 * (e.getClickedInventory().getType().equals(InventoryType.CREATIVE)) return;
		 */
		Player p = (Player) e.getWhoClicked();
		Island island = addon.getIslands().getIslandAt(p.getLocation()).get();
		if (island == null)
			return;
		OfflinePlayer op = Bukkit.getOfflinePlayer(island.getOwner());
		RateMenu menu = new RateMenu(addon, op);
		if (!e.getInventory().getTitle().equals(menu.getInv().getTitle())) {
			return;
		}
		e.setCancelled(true);
		Bukkit.getServer().getScheduler().runTask(addon, new Runnable() {

			@Override
			public void run() {
				if (addon.getConfig().getBoolean("menu.custom", false) == false)
					menu.openInv(p);
				else
					menu.openCustomInv(p);
			}

		});
		ConfigItem item = new ConfigItem(addon, p);
		if (item.getItems().containsKey(e.getCurrentItem()))
			if (item.getItems().get(e.getCurrentItem()) > 0)
				addon.rateIsland(p, op, item.getItems().get(e.getCurrentItem()));

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
		TopMenu menu = new TopMenu(addon);
		if (!e.getInventory().getTitle().equals(menu.getInv().getTitle())) {
			return;
		}
		e.setCancelled(true);
		if (addon.getConfig().getBoolean("top_menu.teleport", false) == true) {
			SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
			Location loc;
			if (!Bukkit.getVersion().contains("1.12")) {
				loc = addon.getIslands()
						.getIsland(e.getWhoClicked().getWorld(), Bukkit.getOfflinePlayer(meta.getOwner()).getUniqueId())
						.getSpawnPoint(Environment.NORMAL);
			} else
				loc = addon.getIslands().getIsland(e.getWhoClicked().getWorld(), meta.getOwningPlayer().getUniqueId())
						.getSpawnPoint(Environment.NORMAL);
			if (loc != null) {
				e.getWhoClicked().teleport(loc);
			}
		}
	}

	@EventHandler
	public void onIslandMenuClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Island island = addon.getIslands().getIslandAt(p.getLocation()).get();
		if (island == null)
			return;
		IslandMenu menu = new IslandMenu(addon, p);
		if (!e.getInventory().getTitle().equals(menu.getInv().getTitle())) {
			return;
		}
		e.setCancelled(true);
		Bukkit.getServer().getScheduler().runTask(addon, new Runnable() {

			@Override
			public void run() {
				if (addon.getConfig().getBoolean("island_menu.custom", false) == false)
					menu.openInv();
				else
					menu.openCustomInv();
			}

		});
		ItemStack item = e.getCurrentItem();
		if (item.equals(menu.getOptOut())) {
			addon.getOptOut().getConfig().set(p.getUniqueId().toString(),
					!addon.getOptOut().getConfig().getBoolean(p.getUniqueId().toString(), false));
			addon.getOptOut().saveConfig();
		}
	}
}
