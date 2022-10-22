package de.mrsaeva.perm.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.mrsaeva.perm.cmd.Cmd;
import de.mrsaeva.perm.listener.OnJoin;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin{
	
	private static Permission perms = null;
	public static String PREFIX = "§6CityCore §8» ";
	public static String NOPERM = PREFIX + "§cDazu hast du keine Rechte!";
	public static String NOTONLINE = PREFIX + "§cDieser Spieler ist nicht online!";
	private static Main plugin;
	
	@Override
	public void onEnable() {
		System.out.println("[Perms] Enabled");
		plugin = this;
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		createConfig();
		setupPermissions();
		init();
	}
	
	@Override
	public void onDisable() {
		System.out.println("[Perms] Disabled");
	}
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	private void init() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new OnJoin(), this);
		getCommand("perm").setExecutor(new Cmd());
	}
	
	
	private void createConfig() {
		FileConfiguration config = Main.getPlugin().getConfig();
		if(!config.contains("Groups.Allgroups")) {
			List<String> allgroups = new ArrayList<>();
			List<String> spielergroupperms = new ArrayList<>();
			List<String> admingroupperms = new ArrayList<>();
			List<String> admininheritancegroups = new ArrayList<>();
			List<String> spielerinheritancegroups = new ArrayList<>();
			allgroups.add("SPIELER");
			allgroups.add("ADMIN");
			spielergroupperms.add("warp.use");
			spielergroupperms.add("ec.use");
			admingroupperms.add("*");
			admininheritancegroups.add("SPIELER");
			config.set("Groups.Allgroups", allgroups);
			config.set("Groups.Groups.SPIELER.Permissions", spielergroupperms);
			config.set("Groups.Groups.ADMIN.Permissions", admingroupperms);
			config.set("Groups.Groups.SPIELER.Inheritances", spielerinheritancegroups);
			config.set("Groups.Groups.ADMIN.Inheritances", admininheritancegroups);
			config.set("Groups.Default", "SPIELER");
		}
		this.saveConfig();
	}
	
	public static Main getPlugin() {
		return plugin;
	}
	
	public static Permission getPerms() {
		return perms;
	}
	
}
