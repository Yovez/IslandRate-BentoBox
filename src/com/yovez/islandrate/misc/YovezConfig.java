package com.yovez.islandrate.misc;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YovezConfig {

	private String fileName;
	private String path;
	private File file;
	private FileConfiguration config;

	public YovezConfig(String fileName) {
		this.fileName = fileName;
		path = Bukkit.getServer().getPluginManager().getPlugin("IslandRate").getDataFolder() + "/";
		file = new File(path, fileName + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void setConfig(YamlConfiguration config) {
		this.config = config;
	}

	public void saveConfig() {
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reloadConfig() {
		setConfig(YamlConfiguration.loadConfiguration(file));
	}

	public boolean exists() {
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

}
