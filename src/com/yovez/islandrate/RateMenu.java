package com.yovez.islandrate;

import java.util.ArrayList;
import java.util.Arrays;
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

public class RateMenu {

	final IslandRate plugin;
	private Inventory inv;
	private OfflinePlayer player;
	private List<ItemStack> items;

	public RateMenu(IslandRate plugin) {
		this.plugin = plugin;
	}

	public RateMenu(IslandRate plugin, OfflinePlayer player) {
		this.plugin = plugin;
		this.player = player;
		inv = Bukkit.createInventory(null, 9, getTitle());
		items = new ArrayList<ItemStack>();
	}

	private String getTitle() {
		return ChatColor.translateAlternateColorCodes('&', plugin.getMessage("menu.title", null, player, 0, 0));
	}

	@SuppressWarnings("deprecation")
	public ItemStack getSkull() {
		ItemStack item = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (byte) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName("§r§f" + player.getName());
		if (Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.8"))
			meta.setOwner(player.getName());
		else
			meta.setOwningPlayer(player);
		meta.setLore(Arrays.asList("§6Total Ratings: §e" + plugin.getAPI().getTotalRatings(player)));
		item.setItemMeta(meta);
		if (!items.contains(item))
			items.add(item);
		return item;
	}

	public ItemStack getHelp() {
		ItemStack item = new ItemStack(Material.BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§2Rating Info");
		meta.setLore(Arrays.asList("§aRate the island.", "§aChoose your rating 1-5."));
		item.setItemMeta(meta);
		if (!items.contains(item))
			items.add(item);
		return item;
	}

	public ItemStack getStar(int stars) {
		ItemStack item = new ItemStack(Material.EMERALD, stars);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(plugin.getMessage("ratings." + stars + "-star", null,
				Bukkit.getServer().getOfflinePlayer(player.getUniqueId()), 0, 0));
		item.setItemMeta(meta);
		if (!items.contains(item))
			items.add(item);
		return item;
	}

	public void openInv(Player p) {
		p.openInventory(createInv(p));
	}

	public void openCustomInv(Player p) {
		p.openInventory(createCustomInv(p));
	}

	public void populateItems() {
		if (plugin.getConfig().getBoolean("menu.custom", false) == false) {
			getSkull();
			getHelp();
			for (int i = 1; i < 6; i++)
				getStar(i);
		} else {

		}
	}

	public Inventory createInv(Player p) {
		inv = Bukkit.createInventory(p, 9, getTitle());
		int place[] = { 0, 2, 4, 5, 6, 7, 8 };
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				if (items == null || items.isEmpty())
					populateItems();
				for (int i = 0; i < items.size(); i++) {
					inv.setItem(place[i], items.get(i));
				}
			}

		});
		return inv;
	}

	@SuppressWarnings("deprecation")
	public Inventory createCustomInv(Player p) {
		inv = Bukkit.createInventory(p, plugin.getConfig().getInt("menu.size", 9), getTitle());
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				for (String s : plugin.getConfig().getConfigurationSection("menu.items").getKeys(false)) {
					if (s == null)
						continue;
					s = "menu.items." + s;
					if (s.equalsIgnoreCase("menu.items.skull")) {
						ItemStack item = new ItemStack(Material.LEGACY_SKULL_ITEM,
								plugin.getConfig().getInt(s + ".amount"),
								(byte) plugin.getConfig().getInt(s + "durability"));
						SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
						skullMeta.setDisplayName(plugin.getMessage(s + ".display_name", null, player, 0, 0));
						if (!Bukkit.getVersion().contains("1.12"))
							skullMeta.setOwner(player.getName());
						else
							skullMeta.setOwningPlayer(player);
						skullMeta.setLore(plugin.getConvertedLore(s, player));
						item.setItemMeta(skullMeta);
						inv.setItem(plugin.getConfig().getInt(s + ".slot"), item);
					} else {
						if (plugin.getConfigItem(s, player) != null)
							inv.setItem(plugin.getConfig().getInt(s + ".slot"), plugin.getConfigItem(s, player));
					}
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

	public OfflinePlayer getPlayer() {
		return player;
	}

	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

}
