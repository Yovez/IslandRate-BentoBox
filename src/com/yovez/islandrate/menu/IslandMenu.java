package com.yovez.islandrate.menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import net.md_5.bungee.api.ChatColor;
import world.bentobox.bentobox.database.objects.Island;

public class IslandMenu implements InventoryHolder, Listener {

	final IslandRate plugin;
	private Inventory inv;
	private Player player;
	private Map<ItemStack, Integer> items;

	public IslandMenu(IslandRate plugin) {
		this.plugin = plugin;
	}

	public IslandMenu(IslandRate plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
		inv = Bukkit.createInventory(this, 9, getTitle());
		items = new HashMap<ItemStack, Integer>();
	}

	@EventHandler
	public void onIslandMenuClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null)
			return;
		if (e.getClickedInventory().getHolder() instanceof IslandMenu) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			IslandMenu menu = new IslandMenu(plugin, p);
			Island island = plugin.getIslands().getIslandAt(p.getLocation()).get();
			if (island == null)
				return;
			Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {

				@Override
				public void run() {
					if (plugin.getConfig().getBoolean("island_menu.custom", false) == false)
						menu.openInv();
					else
						menu.openCustomInv();
				}

			});
			ItemStack item = e.getCurrentItem();
			if (item.equals(menu.getOptOut())) {
				plugin.getOptOut().getConfig().set(p.getUniqueId().toString(),
						!plugin.getOptOut().getConfig().getBoolean(p.getUniqueId().toString(), false));
				plugin.getOptOut().saveConfig();
			}
		}
	}

	private String getTitle() {
		if (plugin.getMessage("island_menu.title", null, player, 0, 0).length() > 32) {
			Bukkit.getConsoleSender().sendMessage(new String[] {
					"§2[IslandRate] §4WARNING: §cAn error occured when opening " + player.getName() + "'s Island Menu.",
					"§2[IslandRate] §4Error: §cIsland Menu's Inventory title cannot be longer than 32 characters.",
					"§2[IslandRate] §cPlease adjust the Title via the config.yml file to be no longer than 32 characters." });
			return plugin.getMessage("island_menu.title", null, player, 0, 0).substring(0, 32);
		}
		return ChatColor.translateAlternateColorCodes('&', plugin.getMessage("island_menu.title", player, null, 0, 0));
	}

	public ItemStack getSkull() {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName("§r§f" + player.getName());
		meta.setOwningPlayer(player);
		meta.setLore(Arrays.asList("§6Total Ratings: §e" + plugin.getAPI().getTotalRatings(player)));
		item.setItemMeta(meta);
		if (!items.containsKey(item))
			items.put(item, plugin.getConfig().getInt("island_menu.items.skull.slot"));
		return item;
	}

	public ItemStack getOptOut() {
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bToggle Opted-Out");
		meta.setLore(Arrays.asList("§6Opted-Out: §r"
				+ (plugin.getOptOut().getConfig().getBoolean(player.getUniqueId().toString(), false) ? "§aTrue"
						: "§cFalse")));
		item.setItemMeta(meta);
		if (!items.containsKey(item))
			items.put(item, 2);
		return item;
	}

	public void openInv() {
		player.openInventory(createInv());
	}

	public void openCustomInv() {
		player.openInventory(createCustomInv());
	}

	public void populateItems() {
		if (plugin.getConfig().getBoolean("island_menu.custom", false) == false) {
			getSkull();
			getOptOut();
		} else {
			setupItems();
		}
	}

	public void setupItems() {
		items = new HashMap<ItemStack, Integer>();
		for (String s : plugin.getConfig().getConfigurationSection("island_menu.items").getKeys(false)) {
			s = "island_menu.items." + s;
			if (s.equalsIgnoreCase("island_menu.items.skull"))
				continue;
			if (plugin.getConfigItem(s, player) != null)
				if (!items.containsKey(plugin.getConfigItem(s, player)))
					items.put(plugin.getConfigItem(s, player), plugin.getConfig().getInt(s + ".slot"));
		}
	}

	public Inventory createInv() {
		inv = Bukkit.createInventory(player, 9, getTitle());
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				if (items == null || items.isEmpty())
					populateItems();
				for (ItemStack item : items.keySet()) {
					inv.setItem(items.get(item), item);
				}
			}

		});
		return inv;
	}

	public Inventory createCustomInv() {
		inv = Bukkit.createInventory(player, plugin.getConfig().getInt("island_menu.size", 9), getTitle());
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				for (String s : plugin.getConfig().getConfigurationSection("island_menu.items").getKeys(false)) {
					if (s == null)
						continue;
					s = "island_menu.items." + s;
					if (s.equalsIgnoreCase("island_menu.items.skull")) {
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

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

}
