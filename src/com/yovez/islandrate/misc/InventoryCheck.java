package com.yovez.islandrate.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.yovez.islandrate.IslandRate;
import com.yovez.islandrate.menu.RateMenu;

public class InventoryCheck implements Runnable {

	IslandRate plugin;

	public InventoryCheck(IslandRate plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		Map<UUID, Integer> list = new HashMap<UUID, Integer>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getInventory();
			RateMenu menu = new RateMenu(plugin, p);
			if (inv.getContents().length > 0)
				for (ItemStack item : inv.getContents()) {
					if (item.equals(menu.getHelp())) {
						inv.remove(menu.getHelp());
						list.put(p.getUniqueId(), list.get(p.getUniqueId()) + 1);
					}
					if (item.equals(menu.getSkull())) {
						inv.remove(menu.getSkull());
						list.put(p.getUniqueId(), list.get(p.getUniqueId()) + 1);
					}
					for (int i = 0; i < 5; i++) {
						if (item.equals(menu.getStar(i))) {
							inv.remove(menu.getStar(i));
							list.put(p.getUniqueId(), list.get(p.getUniqueId()) + 1);
						}
					}
				}
		}
	}

	public Map<UUID, Integer> runCheck() {
		Map<UUID, Integer> list = new HashMap<UUID, Integer>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			Inventory inv = p.getInventory();
			RateMenu menu = new RateMenu(plugin, p);
			if (inv.getContents().length > 0)
				for (ItemStack item : inv.getContents()) {
					if (item == null)
						continue;
					if (item.equals(menu.getHelp())) {
						inv.remove(menu.getHelp());
						list.put(p.getUniqueId(), list.get(p.getUniqueId()) + 1);
					}
					if (item.equals(menu.getSkull())) {
						inv.remove(menu.getSkull());
						list.put(p.getUniqueId(), list.get(p.getUniqueId()) + 1);
					}
					for (int i = 0; i < 5; i++) {
						if (item.equals(menu.getStar(i))) {
							inv.remove(menu.getStar(i));
							list.put(p.getUniqueId(), list.get(p.getUniqueId()) + 1);
						}
					}
				}
		}
		return list;
	}

}
