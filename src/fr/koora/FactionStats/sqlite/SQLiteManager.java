package fr.koora.FactionStats.sqlite;

import org.bukkit.Bukkit;

import fr.koora.FactionStats.Main;
import lib.PatPeter.SQLibrary.SQLite;

public class SQLiteManager {
	public SQLiteManager() {
		sqlConnection();
	}

	private void sqlConnection() {
		Main.sqlite = new SQLite(Main.getInstance().getLogger(), "FactionStats",
				Main.getInstance().getDataFolder().getAbsolutePath(), "data");
		try {
			Main.getSQLite().open();
		} catch (Exception a) {
			a.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
		if (Main.getSQLite().checkTable("players")) {
			return;
		} else {
			try {
				Main.getSQLite().query("CREATE TABLE `players` ( `id` INTEGER PRIMARY KEY, `pseudo` TEXT NOT NULL , `kills` INTEGER NOT NULL , `deaths` INTEGER NOT NULL);");
			} catch (Exception z) {
				z.printStackTrace();
			}
		}
	}

}
