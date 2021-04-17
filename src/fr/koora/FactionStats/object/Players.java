package fr.koora.FactionStats.object;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.koora.FactionStats.Main;

public class Players {
	public static HashMap<UUID, Players> players = new HashMap<UUID, Players>();
	public UUID uuid;
	public int kill = 0;
	public int death = 0;
	public String playerName = "";

	public Players(Player p) {
		this.uuid = p.getUniqueId();
		this.playerName = p.getName();
		players.put(this.uuid, this);
		loadData();
	}

	public void loadData() {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			public void run() {
				try {
					ResultSet result = Main.getSQLite().query("SELECT * FROM players WHERE pseudo='" + playerName + "';");
					boolean exist = false;
					while (result.next()) {
						exist = true;
						kill = result.getInt("kills");
						death = result.getInt("deaths");
					}
					if (!exist) {
						Main.getSQLite().query("INSERT INTO `players` (`pseudo`, `kills`, `deaths`) VALUES ('" + playerName + "',0,0);");
					}
					result.close();
				} catch (Exception a) {
					a.printStackTrace();
				}
			}
		});
	}

	public void saveData() {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			public void run() {
				try {
					Main.getSQLite().query("UPDATE `players` SET kills = " + Players.this.kill + " , deaths = " + Players.this.death + " WHERE pseudo = '" + Players.this.playerName + "'");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

}