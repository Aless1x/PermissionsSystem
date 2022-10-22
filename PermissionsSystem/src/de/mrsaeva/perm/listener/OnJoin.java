package de.mrsaeva.perm.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.mrsaeva.perm.main.Main;
import de.mrsaeva.perm.utils.Register;

public class OnJoin implements Listener{

	FileConfiguration config = Main.getPlugin().getConfig();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Register.registerPerms(p);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Register.att.put(e.getPlayer().getUniqueId(), null);
		config.set("Userdata.Users." + e.getPlayer().getName().toUpperCase(), e.getPlayer().getUniqueId().toString());
		Main.getPlugin().saveConfig();
	}
}
