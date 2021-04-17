package fr.koora.FactionStats;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

import fr.koora.FactionStats.command.FactionStats;
import fr.koora.FactionStats.listener.ChatListener;
import fr.koora.FactionStats.listener.ConnectionListener;
import fr.koora.FactionStats.listener.PlayerListener;
import fr.koora.FactionStats.mysql.MySQLManager;
import fr.koora.FactionStats.object.Players;
import fr.koora.FactionStats.sqlite.SQLiteManager;
import fr.koora.FactionStats.task.FactionUpdater;
import lib.PatPeter.SQLibrary.SQLite;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	public static String prefixe = "§7[§6FactionStats§7] ";
	private static Main plugin;
	public static Economy economy = null;
	public static Connection conn;
	public static Statement state = null;
	public static boolean sqlStatus = false;
	public static boolean updating = true;
	public static int indiceFactionsUpload = 1;
	public static int indiceFactionCurrentMax = 150;
	public static long time = 0;
	public static HashMap<Faction, Integer> factions = new HashMap<Faction, Integer>();
	public static HashSet<FLocation> apList = new HashSet<FLocation>();
	public static SQLite sqlite;
	public static Logger log;

	public void onEnable() {
		log = getLogger();
		plugin = this;
		new SQLiteManager();
		new MySQLManager();
		setupEconomy();
		saveDefaultConfig();
		Config.checkConfig();
		getCommand("fs").setExecutor(new FactionStats());
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new ConnectionListener(), this);
		pm.registerEvents(new ChatListener(), this);
		for (Player p : Bukkit.getOnlinePlayers()) {
			new Players(p);
		}
		new FactionUpdater();
	}

	public void onDisable() {
		saveConfig();
		sqlite.close();
		try {
			Main.state.close();
			Main.conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Plugin getInstance() {
		return plugin;
	}

	public static SQLite getSQLite() {
		return sqlite;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return economy != null;
	}

	public static String sendMConsole(String msg) {
		Bukkit.getConsoleSender().sendMessage(msg);
		return msg;
	}

	public static HashSet<FLocation> getClaimsLocation(Location from, Location to) {
		FLocation fFrom = new FLocation(from);
		FLocation fTo = new FLocation(to);
		return FLocation.getArea(fFrom, fTo);
	}

	public static boolean isNumber(String x) {
		try {
			Integer.parseInt(x);
			return true;
		} catch (Exception a) {
			a.printStackTrace();
		}
		return false;
	}

	public static int toNumber(String z) {
		try {
			return Integer.parseInt(z);
		} catch (Exception a) {
			a.printStackTrace();
		}
		return 0;
	}

	public static Faction getFactionInCollection(int index, Collection<Faction> collection) {
		int i = 0;
		for (Faction f : collection) {
			if (index == i) {
				return f;
			}
			i++;
		}
		return null;
	}

}
