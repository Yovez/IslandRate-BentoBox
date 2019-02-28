package com.yovez.islandrate.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.yovez.islandrate.IslandRate;
import com.yovez.islandrate.menu.IslandMenu;
import com.yovez.islandrate.menu.RateMenu;
import com.yovez.islandrate.menu.TopMenu;
import com.yovez.islandrate.misc.ConfigItem;

import world.bentobox.bentobox.database.objects.Island;

public class MenuListener implements Listener {

	final IslandRate addon;

	public MenuListener(IslandRate addon) {
		this.addon = addon;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMenuClick(InventoryClickEvent e) {
		
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

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onIslandMenuClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		IslandMenu menu = new IslandMenu(addon, p);
		if (!e.getInventory().getTitle().equals(menu.getInv().getTitle())) {
			return;
		}
		Island island = addon.getIslands().getIslandAt(p.getLocation()).get();
		if (island == null)
			return;
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
