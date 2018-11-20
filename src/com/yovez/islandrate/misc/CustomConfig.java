package com.yovez.islandrate.misc;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.yovez.islandrate.IslandRate;

public class CustomConfig {

	private FileConfiguration customConfig;
	private File customConfigFile;
	private IslandRate plugin;
	private String configName;

	public CustomConfig(IslandRate plugin, String configName) {
		this.plugin = plugin;
		this.configName = configName;

	}

	public void reloadConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), configName + ".yml");
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

		Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource(configName + ".yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			customConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getConfig() {
		if (customConfig == null) {
			reloadConfig();
		}
		return customConfig;
	}

	public void saveConfig() {
		if (customConfig == null || customConfigFile == null)
			return;
		try {
			getConfig().save(customConfigFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
		}
	}

	public void saveDefaultConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), configName + ".yml");
		}
		if (!customConfigFile.exists()) {
			plugin.saveResource(configName + ".yml", false);
		}
	}

}
