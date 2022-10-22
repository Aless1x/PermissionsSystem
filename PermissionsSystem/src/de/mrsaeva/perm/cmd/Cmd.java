package de.mrsaeva.perm.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.mrsaeva.perm.main.Main;
import de.mrsaeva.perm.utils.Register;

public class Cmd implements CommandExecutor{

	FileConfiguration config = Main.getPlugin().getConfig();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			List<String> allgroups = config.getStringList("Groups.Allgroups");
			if(p.hasPermission("perm.admin")) {
				
				if(arg.length == 0) {
					getHelp(p);
					return false;
				}
				
				if(arg.length == 1) {
					if(arg[0].equalsIgnoreCase("groups")) {
						p.sendMessage("\n§eGruppen§7:");
						for (String string : allgroups) {
							p.sendMessage("§7- " + string);
						}
						return false;
					}
					p.sendMessage(Main.PREFIX + "§cBitte benutze §6/perm");
					return false;
				}
				
				if(arg.length == 2) {
					if(arg[0].equalsIgnoreCase("user")) {
						Player target = Bukkit.getPlayer(arg[1]);
						if(target != null) {
							List<String> targetperms = config.getStringList("Userdata.Users." + target.getUniqueId().toString() + ".Permissions");
							String targetgroup = config.getString("Userdata.Users." + target.getUniqueId().toString() + ".Group");
							p.sendMessage("\n§e" + target.getName() + "§7's Permissions:");
							if(!targetperms.isEmpty()) {
								for (String string : targetperms) {
									p.sendMessage("§7- " + string);
								}
							} else
								p.sendMessage("§cKeine Permissions vorhanden.");
							p.sendMessage("\n§eGroup: §7" + targetgroup);
						} else {
							String name = arg[1].toUpperCase();
							if(config.contains("Userdata.Users." + name)) {
								String uuid = config.getString("Userdata.Users." + name);
								List<String> targetperms = config.getStringList("Userdata.Users." + uuid + ".Permissions");
								String targetgroup = config.getString("Userdata.Users." + uuid + ".Group");
								p.sendMessage("\n§e" + name + "§7's Permissions:");
								if(!targetperms.isEmpty()) {
									for (String string : targetperms) {
										p.sendMessage("§7- " + string);
									}
								} else
									p.sendMessage("§cKeine Permissions vorhanden.");
								p.sendMessage("\n§eGroup: §7" + targetgroup);
							} else
								p.sendMessage(Main.PREFIX + "§cDer Spieler war noch nie auf dem Server!");
						}
						return false;
					}
					if(arg[0].equalsIgnoreCase("group")) {
						String group = arg[1].toUpperCase();
						if(allgroups.contains(group)) {
							List<String> groupperms = config.getStringList("Groups.Groups." + group + ".Permissions");
							List<String> inheritances = config.getStringList("Groups.Groups." + group + ".Inheritances");
							p.sendMessage("\n§e" + group + "§7's Permissions");
							if(!groupperms.isEmpty()) {
								for (String string : groupperms) {
									p.sendMessage("§7- " + string);
								}
							} else
								p.sendMessage("§cKeine Permissions vorhanden.");
							p.sendMessage("\n§e" + group + "§7's Inheritances");
							if(!inheritances.isEmpty()) {
								for (String string : inheritances) {
									p.sendMessage("§7- " + string);
								}
							} else
								p.sendMessage("§cKeine Inheritances vorhanden.");
						} else
							p.sendMessage(Main.PREFIX + "§cDiese Gruppe existiert nicht!");
						return false;
					}
					if(arg[0].equalsIgnoreCase("create")) {
						String group = arg[1].toUpperCase();
						if(!allgroups.contains(group)) {
							createGroup(group);
							p.sendMessage(Main.PREFIX + "§aGruppe §e" + group + " §aerfolgreich erstellt!");
						} else
							p.sendMessage(Main.PREFIX + "§cDie Gruppe existiert bereits!");
						return false;
					}
					if(arg[0].equalsIgnoreCase("delete")) {
						String group = arg[1].toUpperCase();
						if(allgroups.contains(group)) {
							removeGroup(group);
							p.sendMessage(Main.PREFIX + "§aGruppe §e" + group + " §aerfolgreich geloescht!");
						} else
							p.sendMessage(Main.PREFIX + "§cDie Gruppe existiert nicht!");
						return false;
					}
					p.sendMessage(Main.PREFIX + "§cBitte benutze §6/perm");
					return false;
				}
				
				if(arg.length == 3) {
					if((arg[0].equalsIgnoreCase("user")) && (arg[1].equalsIgnoreCase("reset"))) {
						Player target = Bukkit.getPlayer(arg[2]);
						if(target != null) {
							removePlayer(p);
							p.sendMessage(Main.PREFIX + "§e" + target.getName() + " §aerfolgreich zurueckgesetzt!");
						} else
							p.sendMessage(Main.NOTONLINE);
					} else
						p.sendMessage(Main.PREFIX + "§cBitte benutze §6/perm");
					return false;
				}
				
				if(arg.length == 4) {
					if(arg[0].equalsIgnoreCase("user")) {
						Player target = Bukkit.getPlayer(arg[1]);
						if(target != null) {
							List<String> targetperms = config.getStringList("Userdata.Users." + target.getUniqueId().toString() + ".Permissions");
							String targetgroup = config.getString("Userdata.Users." + target.getUniqueId().toString() + ".Group");
							if(arg[2].equalsIgnoreCase("add")) {
								String perm = arg[3].toLowerCase();
								if(!targetperms.contains(perm)) {
									addPermissionToPlayer(target, perm);
									p.sendMessage(Main.PREFIX + "§e" + perm + " §aerfolgreich " + target.getName() + " gesetzt!");
								} else
									p.sendMessage(Main.PREFIX + "§cDer Spieler besitzt die Permission bereits.");
								return false;
							}
							if(arg[2].equalsIgnoreCase("remove")) {
								String perm = arg[3].toLowerCase();
								if(targetperms.contains(perm)) {
									removePermissionFromPlayer(target, perm);
									p.sendMessage(Main.PREFIX + "§e" + perm + " §aerfolgreich " + target.getName() + " entfernt!");
								} else
									p.sendMessage(Main.PREFIX + "§cDer Spieler besitzt die Permission nicht.");
								return false;
							}
							if(arg[2].equalsIgnoreCase("set")) {
								String group = arg[3].toUpperCase();
								if(allgroups.contains(group)) {
									if(!targetgroup.equals(group)) {
										setPlayerToGroup(target, group);
										p.sendMessage(Main.PREFIX + "§e" + target.getName() + " §aerfolgreich der Gruppe §e" + group + " §ahinzugefuegt.");
									} else
										p.sendMessage(Main.PREFIX + "§cDer Spieler ist bereits in dieser Gruppe.");
								} else
									p.sendMessage(Main.PREFIX + "§cDie Gruppe existiert nicht.");
								return false;
							}
							p.sendMessage(Main.PREFIX + "§cBitte benutze §6/perm");
						} else {
							String name = arg[1].toUpperCase();
							if(config.contains("Userdata.Users." + name)) {
								String uuid = config.getString("Userdata.Users." + name);
								List<String> targetperms = config.getStringList("Userdata.Users." + uuid + ".Permissions");
								String targetgroup = config.getString("Userdata.Users." + uuid + ".Group");
								if(arg[2].equalsIgnoreCase("add")) {
									String perm = arg[3].toLowerCase();
									if(!targetperms.contains(perm)) {
										targetperms.add(perm);
										config.set("Userdata.Users." + uuid + ".Permissions", targetperms);
										saveConfig();
										p.sendMessage(Main.PREFIX + "§e" + perm + " §aerfolgreich " + name + " gesetzt!");
									} else
										p.sendMessage(Main.PREFIX + "§cDer Spieler besitzt die Permission bereits.");
									return false;
								}
								if(arg[2].equalsIgnoreCase("remove")) {
									String perm = arg[3].toLowerCase();
									if(targetperms.contains(perm)) {
										targetperms.remove(perm);
										config.set("Userdata.Users." + uuid + ".Permissions", targetperms);
										saveConfig();
										p.sendMessage(Main.PREFIX + "§e" + perm + " §aerfolgreich " + name + " entfernt!");
									} else
										p.sendMessage(Main.PREFIX + "§cDer Spieler besitzt die Permission nicht.");
									return false;
								}
								if(arg[2].equalsIgnoreCase("set")) {
									String group = arg[3].toUpperCase();
									if(allgroups.contains(group)) {
										if(!targetgroup.equals(group)) {
											config.set("Userdata.Users." + uuid + ".Group", group);
											saveConfig();
											p.sendMessage(Main.PREFIX + "§e" + name + " §aerfolgreich der Gruppe §e" + group + " §ahinzugefuegt.");
										} else
											p.sendMessage(Main.PREFIX + "§cDer Spieler ist bereits in dieser Gruppe.");
									} else
										p.sendMessage(Main.PREFIX + "§cDie Gruppe existiert nicht.");
									return false;
								}
								p.sendMessage(Main.PREFIX + "§cBitte benutze §6/perm");
							} else
								p.sendMessage(Main.PREFIX + "§cDer Spieler war noch nie auf dem Server!");
						}
						return false;
					}
					if(arg[0].equalsIgnoreCase("group")) {
						String group = arg[1].toUpperCase();
						if(allgroups.contains(group)) {
							List<String> groupperms = config.getStringList("Groups.Groups." + group + ".Permissions");
							List<String> inheritances = config.getStringList("Groups.Groups." + group + ".Inheritances");
							if(arg[2].equalsIgnoreCase("add")) {
								String perm = arg[3].toLowerCase();
								if(!groupperms.contains(perm)) {
									addPermissionToGroup(group, perm);
									p.sendMessage(Main.PREFIX + "§e" + perm + " §aerfolgreich gesetzt!");
								} else
									p.sendMessage(Main.PREFIX + "§cDiese Permission besitzt die Gruppe bereits.");
								return false;
							}
							if(arg[2].equalsIgnoreCase("remove")) {
								String perm = arg[3].toLowerCase();
								if(groupperms.contains(perm)) {
									removePermissionFromGroup(group, perm);
									p.sendMessage(Main.PREFIX + "§e" + perm + " §aerfolgreich entfernt!");
								} else
									p.sendMessage(Main.PREFIX + "§cDiese Permission besitzt die Gruppe nicht.");
								return false;
							}
							if(arg[2].equalsIgnoreCase("addinherit")) {
								String inherit = arg[3].toUpperCase();
								if(allgroups.contains(inherit)) {
									if(!inheritances.contains(inherit)) {
										addInheritToGroup(group, inherit);
										p.sendMessage(Main.PREFIX + "§e" + inherit + " §aerfolgreich hinzugefuegt!");
									} else
										p.sendMessage(Main.PREFIX + "§cDiese Gruppe besitzt diesen Inherit bereits.");
								} else
									p.sendMessage(Main.PREFIX + "§cDiese Gruppe existiert nicht!");
								return false;
							}
							if(arg[2].equalsIgnoreCase("removeinherit")) {
								String inherit = arg[3].toUpperCase();
								if(allgroups.contains(inherit)) {
									if(inheritances.contains(inherit)) {
										removeInheritFromGroup(group, inherit);
										p.sendMessage(Main.PREFIX + "§e" + inherit + " §aerfolgreich entfernt!");
									} else
										p.sendMessage(Main.PREFIX + "§cDiese Gruppe besitzt diesen Inherit nicht.");
								} else
									p.sendMessage(Main.PREFIX + "§cDiese Gruppe existiert nicht!");
								return false;
							}
							p.sendMessage(Main.PREFIX + "§cBitte benutze §6/perm");
						} else
							p.sendMessage(Main.PREFIX + "§cDiese Gruppe existiert nicht!");
						return false;
					}
					p.sendMessage(Main.PREFIX + "§cBitte benutze §6/perm");
					return false;
				}
				
			} else
				p.sendMessage(Main.NOPERM);
		}
		return false;
	}

	private void getHelp(Player p) {
		p.sendMessage("§6-----===== PERMS =====-----" 
				+ "\n§7/perm"											// 0 DONE
				+ "\n§7/perm user [Spieler]"							// 2 DONE
				+ "\n§7/perm user [Spieler] add [Perm]"					// 4 DONE
				+ "\n§7/perm user [Spieler] remove [Perm]"				// 4 DONE
				+ "\n§7/perm user [Spieler] set [Gruppe]"				// 4 DONE
				+ "\n§7/perm user reset [Spieler]"						// 3 DONE
				+ "\n§7/perm groups"									// 1 
				+ "\n§7/perm group [Gruppe]"							// 2 DONE
				+ "\n§7/perm create [Gruppe]"							// 2 DONE
				+ "\n§7/perm delete [Gruppe]"							// 2 DONE
				+ "\n§7/perm group [Gruppe] add [Perm]"					// 4 DONE
				+ "\n§7/perm group [Gruppe] remove [Perm]"				// 4 DONE
				+ "\n§7/perm group [Gruppe] addinherit [Inherit]"		// 4 DONE
				+ "\n§7/perm group [Gruppe] removeinherit [Inherit]"	// 4 DONE
				+ "\n§6-----===== PERMS =====-----");
	}
	
	private void addPermissionToPlayer(Player p, String perm) {
		
		/*
		 * private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
		 */
	}
	
	private void removePermissionFromPlayer(Player p, String perm) {
		List<String> userperms = config.getStringList("Userdata.Users." + p.getUniqueId().toString() + ".Permissions");
		userperms.remove(perm);
		config.set("Userdata.Users." + p.getUniqueId().toString() + ".Permissions", userperms);
		saveConfig();
		Register.reloadPerms(p);
	}
	
	private void setPlayerToGroup(Player p, String group) {
		config.set("Userdata.Users." + p.getUniqueId().toString() + ".Group", group);
		saveConfig();
		p.kickPlayer("§e§lCityCore"
				+ "\n    §eDein Rang wurde aktualisiert!"
				+ "\n"
				+ "\n"
				+ "\n§aNeuer Rang: §e" + group);
	}
	
	private void removePlayer(Player p) {
		Register.setUserToDefault(p);
	}
	
	private void createGroup(String group) {
		List<String> groupperms = new ArrayList<>();
		List<String> inheritances = new ArrayList<>();
		List<String> allgroups = config.getStringList("Groups.Allgroups");
		config.set("Groups.Groups." + group + ".Permissions", groupperms);
		config.set("Groups.Groups." + group + ".Inheritances", inheritances);
		allgroups.add(group);
		config.set("Groups.Allgroups", allgroups);
		saveConfig();
	}
	
	private void removeGroup(String group) {
		List<String> allgroups = config.getStringList("Groups.Allgroups");
		allgroups.remove(group);
		config.set("Groups.Allgroups", allgroups);
		config.set("Groups.Groups." + group, null);
		saveConfig();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if(config.getString("Userdata.Users." + player.getUniqueId().toString() + ".Group").equals(group)) {
				Register.reloadPerms(player);
			}
		}
	}
	
	private void addPermissionToGroup(String group, String perm) {
		List<String> groupperms = config.getStringList("Groups.Groups." + group + ".Permissions");
		groupperms.add(perm);
		config.set("Groups.Groups." + group + ".Permissions", groupperms);
		saveConfig();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if(config.getString("Userdata.Users." + player.getUniqueId().toString() + ".Group").equals(group)) {
				Register.reloadPerms(player);
			}
		}
	}
	
	private void removePermissionFromGroup(String group, String perm) {
		List<String> groupperms = config.getStringList("Groups.Groups." + group + ".Permissions");
		groupperms.remove(perm);
		config.set("Groups.Groups." + group + ".Permissions", groupperms);
		saveConfig();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if(config.getString("Userdata.Users." + player.getUniqueId().toString() + ".Group").equals(group)) {
				Register.reloadPerms(player);
			}
		}
	}
	
	private void addInheritToGroup(String group, String inherit) {
		List<String> inheritances = config.getStringList("Groups.Groups." + group + ".Inheritances");
		inheritances.add(inherit);
		config.set("Groups.Groups." + group + ".Inheritances", inheritances);
		saveConfig();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if(config.getString("Userdata.Users." + player.getUniqueId().toString() + ".Group").equals(group)) {
				Register.reloadPerms(player);
			}
		}
	}
	
	private void removeInheritFromGroup(String group, String inherit) {
		List<String> inheritances = config.getStringList("Groups.Groups." + group + ".Inheritances");
		inheritances.remove(inherit);
		config.set("Groups.Groups." + group + ".Inheritances", inheritances);
		saveConfig();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if(config.getString("Userdata.Users." + player.getUniqueId().toString() + ".Group").equals(group)) {
				Register.reloadPerms(player);
			}
		}
	}
	
	private void saveConfig() {
		Main.getPlugin().saveConfig();
	}
	
}
