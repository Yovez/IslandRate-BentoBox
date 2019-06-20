package com.yovez.islandrate.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.yovez.islandrate.IslandRate;
import com.yovez.islandrate.misc.ConfigItem;

import net.md_5.bungee.api.ChatColor;
import world.bentobox.bentobox.database.objects.Island;

public class RateMenu implements InventoryHolder, Listener {

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
		inv = Bukkit.createInventory(this, 9, getTitle());
		items = new ArrayList<ItemStack>();
	}

	@Override
	public Inventory getInventory() {
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

	@EventHandler
	public void onMenuClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null)
			return;
		if (e.getClickedInventory().getHolder() instanceof RateMenu) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			Island island = plugin.getIslands().getIslandAt(p.getLocation()).get();
			if (island == null)
				return;
			OfflinePlayer op = Bukkit.getOfflinePlayer(island.getOwner());
			RateMenu menu = new RateMenu(plugin, op);
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
	}

	private String getTitle() {
		if (plugin.getMessage("menu.title", null, player, 0, 0).length() > 32) {
			Bukkit.getConsoleSender().sendMessage(new String[] {
					"§2[IslandRate] §4WARNING: §cAn error occured when opening " + player.getName() + "'s Rate Menu.",
					"§2[IslandRate] §4Error: §cRate Menu's Inventory title cannot be longer than 32 characters.",
					"§2[IslandRate] §cPlease adjust the Title via the config.yml file to be no longer than 32 characters." });
			return plugin.getMessage("menu.title", null, player, 0, 0).substring(0, 32);
		}
		return ChatColor.translateAlternateColorCodes('&', plugin.getMessage("menu.title", null, player, 0, 0));
	}

	public ItemStack getSkull() {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName("§r§f" + player.getName());
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
						ItemStack item = new ItemStack(Material.PLAYER_HEAD, plugin.getConfig().getInt(s + ".amount"));
						SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
						skullMeta.setDisplayName(plugin.getMessage(s + ".display_name", null, player, 0, 0));
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
