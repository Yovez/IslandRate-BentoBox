package com.yovez.islandrate.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.yovez.islandrate.IslandRate;

import net.md_5.bungee.api.ChatColor;

public class TopMenu implements InventoryHolder, Listener {

	final IslandRate plugin;
	private Inventory inv;
	private List<ItemStack> items;

	public TopMenu(IslandRate plugin) {
		this.plugin = plugin;
		inv = Bukkit.createInventory(this, 27, getTitle());
		items = new ArrayList<ItemStack>();
	}

	@EventHandler
	public void onTopMenuClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null)
			return;
		if (e.getClickedInventory().getHolder() instanceof TopMenu) {
			e.setCancelled(true);
			if (plugin.getConfig().getBoolean("top_menu.teleport", false) == true) {
				SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
				Location loc = plugin.getIslands()
						.getIsland(e.getWhoClicked().getWorld(), meta.getOwningPlayer().getUniqueId())
						.getSpawnPoint(Environment.NORMAL);
				if (loc != null) {
					e.getWhoClicked().teleport(loc);
				}
			}
		}
	}

	private String getTitle() {
		if (plugin.getMessage("top_menu.title", null, null, 0, 0).length() > 32) {
			Bukkit.getConsoleSender().sendMessage(new String[] {
					"§2[IslandRate] §4WARNING: §cAn error occured when opening Top Menu.",
					"§2[IslandRate] §4Error: §cIsland Menu's Inventory title cannot be longer than 32 characters.",
					"§2[IslandRate] §cPlease adjust the Title via the config.yml file to be no longer than 32 characters." });
			return plugin.getMessage("top_menu.title", null, null, 0, 0).substring(0, 32);
		}
		return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("top_menu.title"));
	}

	public ItemStack getSkull(OfflinePlayer player, int place) {
		if (player == null)
			return null;
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(plugin.getMessage("top_menu.items.skull.display_name", null, player, 0, place));
		meta.setOwningPlayer(player);
		meta.setLore(plugin.getConvertedLore("top_menu.items.skull", player));
		item.setItemMeta(meta);
		if (!items.contains(item))
			items.add(item);
		return item;
	}

	public void openInv(Player p) {
		p.openInventory(createInv());
	}

	public void populateItems() {
		// try {
		// ResultSet rs = api.getTopTenSQL();
		for (int i = 1; i < 11; i++) {
			if (plugin.getAPI().getTotalRatings(plugin.getAPI().getTopRated(i)) == 0)
				break;
			getSkull(plugin.getAPI().getTopRated(i), i);
		}
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
	}

	public Inventory createInv() {
		inv = Bukkit.createInventory(null, 27, getTitle());
		int place[] = { 4, 12, 14, 19, 20, 21, 22, 23, 24, 25 };
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

	@Override
	public Inventory getInventory() {
		return inv;
	}

}
