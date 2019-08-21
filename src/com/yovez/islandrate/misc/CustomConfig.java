package com.yovez.islandrate.misc;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class CustomConfig {

	private YamlConfiguration customConfig;
	private File customConfigFile;
	private Plugin plugin;
	private String configName;
	private String path;
	private boolean isResource;

	public CustomConfig(Plugin plugin, String configName, boolean isResource) {
		this.plugin = plugin;
		this.configName = configName;
		this.isResource = isResource;
		path = "";
	}

	public CustomConfig(Plugin plugin, String configName, String path) {
		this.plugin = plugin;
		this.configName = configName;
		this.path = path;
		isResource = false;
	}

	public void reloadConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder() + "/" + path, configName + ".yml");
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
		if (isResource == true) {
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
	}

	public YamlConfiguration getConfig() {
		if (customConfig == null) {
			reloadConfig();
		}
		return customConfig;
	}

	public void saveConfig() {
		try {
			getConfig().save(customConfigFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
		}
	}

	public void saveDefaultConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder() + "/" + path, configName + ".yml");
		}
		if (!customConfigFile.exists()) {
			plugin.saveResource(configName + ".yml", false);
		}
	}

	public File getCustomConfigFile() {
		return customConfigFile;
	}

}
