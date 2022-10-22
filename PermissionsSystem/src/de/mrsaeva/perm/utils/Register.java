package de.mrsaeva.perm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import de.mrsaeva.perm.main.Main;
import de.mrsaeva.perm.utils.Register;

public class Register {

	public static FileConfiguration config = Main.getPlugin().getConfig();
	public static HashMap<UUID, PermissionAttachment> att = new HashMap<>();
	
	public static void registerPerms(Player p) {
		PermissionAttachment att2 = p.addAttachment(Main.getPlugin());
		att.put(p.getUniqueId(), att2);
		PermissionAttachment attachment = att.get(p.getUniqueId());
		String uuid = p.getUniqueId().toString();
		List<String> allgroups = config.getStringList("Groups.Allgroups");
		if(config.contains("Userdata.Users." + uuid)) {
			String usergroup = config.getString("Userdata.Users." + uuid + ".Group").toUpperCase();
			List<String> userperms = config.getStringList("Userdata.Users." + uuid + ".Permissions"); // PERMISSIONS
			if(allgroups.contains(usergroup)) {
				List<String> groupperms = config.getStringList("Groups.Groups." + usergroup + ".Permissions"); // PERMISSIONS
				List<String> inheritperms = new ArrayList<>(); // PERMISSIONS
				List<String> groupinheritaces = config.getStringList("Groups.Groups." + usergroup + ".Inheritances");
				for (String group : groupinheritaces) {
					if(allgroups.contains(group)) {
						List<String> inheritpermss = config.getStringList("Groups.Groups." + group + ".Permissions");
						for (String string : inheritpermss) {
							inheritperms.add(string);
						}
					}
				}
				for (String string : userperms) {
					attachment.setPermission(string, true);
				}
				for (String string : groupperms) {
					attachment.setPermission(string, true);
				}
				for (String string : inheritperms) {
					attachment.setPermission(string, true);
				}
			} else
				setUserToDefault(p);
		} else
			setUserToDefault(p);
	}
	
	public static void reloadPerms(Player p) {
		PermissionAttachment att2 = p.addAttachment(Main.getPlugin());
		att2.getPermissions().clear();
		Register.att.put(p.getUniqueId(), null);
		registerPerms(p);
	}
	
	private static void saveConfig() {
		Main.getPlugin().saveConfig();
	}
	
	public static void setUserToDefault(Player p) {
		List<String> userperms = new ArrayList<>();
		PermissionAttachment att2 = p.addAttachment(Main.getPlugin());
		att2.getPermissions().clear();
		att.put(p.getUniqueId(), null);
		config.set("Userdata.Users." + p.getUniqueId().toString() + ".Group", config.get("Groups.Default"));
		config.set("Userdata.Users." + p.getUniqueId().toString() + ".Permissions", userperms);
		saveConfig();
		p.kickPlayer("§e§lCityCore"
				+ "\n    §eDein Rang wurde aktualisiert!"
				+ "\n"
				+ "\n"
				+ "\n§aNeuer Rang: §e" + config.getString("Groups.Default"));
	}
	
}
