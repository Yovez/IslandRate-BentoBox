package com.yovez.islandrate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class InfiniteTopMenu {

	final IslandRate plugin;
	private Inventory inv;
	private List<ItemStack> items;

	public InfiniteTopMenu(IslandRate plugin) {
		this.plugin = plugin;
		inv = Bukkit.createInventory(null, 54, getTitle());
		items = new ArrayList<ItemStack>();
	}

	private String getTitle() {
		return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("infinite_top_menu.title"));
	}

	@SuppressWarnings("deprecation")
	public ItemStack getSkull(OfflinePlayer player, int place) {
		if (player == null)
			return null;
		if (place == 0)
			return null;
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(plugin.getMessage("infinite_top_menu.items.skull.display_name", null, player, 0, place));
		if (!Bukkit.getVersion().contains("1.12"))
			meta.setOwner(player.getName());
		else
			meta.setOwningPlayer(player);
		meta.setLore(plugin.getConvertedLore("infinite_top_menu.items.skull", player));
		item.setItemMeta(meta);
		if (!items.contains(item))
			items.add(item);
		return item;
	}

	public void openInv(Player p) {
		p.openInventory(createInv());
	}

	public void populateItems() {
		for (int i = 1; i < 37; i++) {
			if (plugin.getAPI().getTotalRatings(plugin.getAPI().getTopRated(i)) == 0)
				break;
			getSkull(plugin.getAPI().getTopRated(i), i);
		}
	}

	public Inventory createInv() {
		inv = Bukkit.createInventory(null, 54, getTitle());

		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
				ItemMeta paneM = pane.getItemMeta();
				paneM.setDisplayName(" ");
				pane.setItemMeta(paneM);
				inv.setItem(0, pane);
				inv.setItem(2, pane);
				inv.setItem(3, pane);
				inv.setItem(5, pane);
				inv.setItem(6, pane);
				inv.setItem(8, pane);
				for (int i = 45; i < 54; i++)
					inv.setItem(i, pane);

				ItemStack weekly = new ItemStack(Material.PAPER);
				ItemMeta weeklyM = weekly.getItemMeta();
				weeklyM.setDisplayName("§eTop Rated §e(Weekly)");
				weekly.setItemMeta(weeklyM);
				inv.setItem(1, weekly);

				ItemStack monthly = new ItemStack(Material.PAPER);
				ItemMeta monthlyM = monthly.getItemMeta();
				monthlyM.setDisplayName("§eTop Rated §e(Monthly)");
				monthly.setItemMeta(monthlyM);
				inv.setItem(4, monthly);

				ItemStack allTime = new ItemStack(Material.PAPER);
				ItemMeta allTimeM = allTime.getItemMeta();
				allTimeM.setDisplayName("§eTop Rated §e(All-Time)");
				allTime.setItemMeta(allTimeM);
				inv.setItem(7, allTime);

				if (items == null || items.isEmpty())
					populateItems();
				for (int i = 0; i < items.size(); i++) {
					inv.addItem(items.get(i));
				}
			}

		});
		return inv;
	}

	public Inventory getInv() {
		return inv;
	}

	public void setInv(Inventory inv) {
		this.inv = inv;
	}

	public List<ItemStack> getItems() {
		return items;
	}

	public void setItems(List<ItemStack> items) {
		this.items = items;
	}

}
