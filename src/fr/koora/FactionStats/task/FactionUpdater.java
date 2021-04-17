package fr.koora.FactionStats.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import fr.koora.FactionStats.Main;

public class FactionUpdater implements Runnable {
	public FactionUpdater() {
		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(Main.getInstance(), this, 80L);
	}

	public void run() {
		if (Main.indiceFactionsUpload == 1 && Main.sqlStatus == true) {
			Main.sendMConsole(Main.prefixe + "§cEssai de mise à jours des statistiques faction...");
			Main.time = System.currentTimeMillis();
			Main.updating = true;
			Location from = new Location(Bukkit.getWorld("world"), -271, 100, -416);
			Location to = new Location(Bukkit.getWorld("world"), 429, 73, 301);
			Main.apList = Main.getClaimsLocation(from, to);
			try {
				Main.state = Main.conn.createStatement();
				Main.state.executeUpdate("CREATE TABLE IF NOT EXISTS " + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Table") + " (Faction_ID varchar(100) NOT NULL, Faction_NAME varchar(50) NOT NULL, Faction_LEADER varchar(50) NOT NULL,Faction_DESCRIPTION varchar(1000) NOT NULL,Faction_CLAIM int(5) NOT NULL,Faction_AP int(5) NOT NULL, Faction_MEMBRE varchar(2) NOT NULL, Faction_POWER int(2) NOT NULL, Faction_POWERMAX int(2) NOT NULL, Faction_ARGENT int(15) NOT NULL, Faction_TOTALMEMBRE varchar(500) NOT NULL, Faction_KILLS int(10) NOT NULL, Faction_MORTS int(10) NOT NULL, Faction_POINTS int(10) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
				Main.state.executeUpdate("DELETE FROM " + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Table"));
				Main.state.close();
				Main.state = Main.conn.createStatement(1005, 1008);
				Main.conn.setAutoCommit(false);
				Main.sendMConsole(Main.prefixe + "§aMise à jour de la table effectuée avec succès !");
			} catch (Exception e) {
				e.printStackTrace();
				Main.indiceFactionsUpload = 1;
				Main.getInstance().saveConfig();
				Main.updating = false;
				return;
			}
		}
		for (int i = 1; i <= Main.indiceFactionCurrentMax; i++) {
			Faction f = Main.getFactionInCollection(Main.indiceFactionsUpload, Factions.i.get());
			if ((!f.getId().equals("-1")) && (!f.getId().equals("-2")) && (f.isNormal())) {
				try {
					updateFactions(f);
				} catch (SQLException a) {
					a.printStackTrace();
				}
				if (Main.indiceFactionsUpload >= Factions.i.get().size() - 3) {
					try {
						Main.state.executeBatch();
						Main.conn.commit();
					} catch (Exception e) {
						Main.sqlStatus = false;
						e.printStackTrace();
					}
					Main.time = System.currentTimeMillis() - Main.time;
					Main.sendMConsole(Main.prefixe + "§aLe classement Factions contient §6" + (Factions.i.get().size() - 3) + "/" + (Factions.i.get().size() - 3) + " §afactions.");
					Main.sendMConsole(Main.prefixe + "§aLa mise à jour a été effectuée avec succès en §c" + Main.time + "ms§a !");
					Main.indiceFactionsUpload = 1;
					Main.getInstance().saveConfig();
					Main.updating = false;
					return;
				}
			}
			Main.indiceFactionsUpload += 1;
		}
	}

	private static int getMoney(String name) {
		int money = 0;
		try {
			money = (int) Main.economy.getBalance(name);
		} catch (Exception e) {
			return money;
		}
		return money;
	}

	private void updateFactions(Faction f) throws SQLException {
		Main.sendMConsole(Main.prefixe + "§cEssai de mise à jour de la faction §6" + f.getTag());
		int i = 0;
		int power = 0;
		int powermax = 0;
		int claim = f.getLandRounded();
		int nombre_membre = f.getFPlayers().size();
		int faction_kills = 0;
		int faction_deaths = 0;
		int total_points = 0;
		int faction_money = 0;
		int ap = 0;
		String description = f.getDescription();
		String membre_liste = "";
		description = description.replace("'", "&#39;").replace("\\", "&#92;");
		description = encode(description);
		ap = getFactionAp(f);
		for (FPlayer fp : f.getFPlayers()) {
			i++;
			boolean exist = false;
			try {
				ResultSet result = Main.getSQLite()
						.query("SELECT * FROM `players` WHERE pseudo = '" + fp.getName() + "'");
				while (result.next()) {
					faction_kills += result.getInt("kills");
					faction_deaths += result.getInt("deaths");
					exist = true;
				}
				if (!exist) {
					Main.getSQLite().query(
							"INSERT INTO `players` (`pseudo`, `kills`, `deaths`) VALUES ('" + fp.getName() + "',0,0);");
					faction_kills += 0;
					faction_deaths += 0;
				}
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			power = (int) (power + fp.getPower());
			powermax = (int) (powermax + fp.getPowerMax());
			faction_money += getMoney(fp.getName());
			if (i < nombre_membre) {
				membre_liste = membre_liste + fp.getName() + ", ";
			} else {
				membre_liste = membre_liste + fp.getName();
			}
		}
		total_points = faction_kills * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Kill");
		total_points += faction_deaths * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Death");
		total_points += claim * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Claim");
		total_points += nombre_membre * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Membre");
		total_points += ap * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.avantPoste");
		if (faction_money != 0) {
			total_points += faction_money / 1000 * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Money");
		}
		Main.factions.put(f, total_points);
		try {
			Main.state.addBatch(
					"INSERT INTO " + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Table")
							+ " (`Faction_ID`, `Faction_NAME`, `Faction_LEADER`, `Faction_DESCRIPTION`, `Faction_CLAIM`, `Faction_AP`, `Faction_MEMBRE`, `Faction_POWER`, `Faction_POWERMAX`, `Faction_ARGENT`, `Faction_TOTALMEMBRE`, `Faction_KILLS`, `Faction_MORTS`, `Faction_POINTS`) VALUES ('"
							+ f.getId() + "','" + f.getTag() + "','" + f.getFPlayerAdmin().getName() + "','"
							+ description + "'," + claim + "," + ap + "," + nombre_membre + "," + power + "," + powermax
							+ ",'" + faction_money + "','" + membre_liste + "', " + faction_kills + ", "
							+ faction_deaths + ", " + total_points + ")");
		} catch (SQLException e) {
			Main.state.close();
			Main.indiceFactionsUpload = 1;
			Main.updating = false;
			e.printStackTrace();
		}
		Main.sendMConsole(Main.prefixe + "§aMise à jour de la faction §6" + f.getTag() + "§a effectué avec succès !");
	}

	public static int getFactionMoney(Faction f) {
		int money = 0;
		for (FPlayer fp : f.getFPlayers()) {
			money += getMoney(fp.getName());
		}
		return money;
	}

	public static int getFactionKills(Faction f) {
		int kills = 0;
		for (FPlayer fp : f.getFPlayers()) {
			boolean exist = false;
			ResultSet result;
			try {
				result = Main.getSQLite().query("SELECT * FROM `players` WHERE pseudo ='" + fp.getName() + "'");
				while (result.next()) {
					kills += result.getInt("kills");
					exist = true;
				}
				if (!exist) {
					kills += 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return kills;
	}

	public static int getFactionDeaths(Faction f) {
		int deaths = 0;
		for (FPlayer fp : f.getFPlayers()) {
			boolean exist = false;
			ResultSet result;
			try {
				result = Main.getSQLite().query("SELECT * FROM `players` WHERE pseudo ='" + fp.getName() + "'");
				while (result.next()) {
					deaths += result.getInt("deaths");
					exist = true;
				}
				if (!exist) {
					deaths += 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return deaths;
	}

	public static double getFactionRatio(Faction f) {
		int fKills = getFactionKills(f);
		int fDeaths = getFactionDeaths(f);
		if (fDeaths == 0) {
			return fKills;
		} else if (fKills == 0 && fDeaths > 0) {
			return 0;
		} else {
			return Math.round((fKills / fDeaths) * 100) / 100;
		}
	}

	public static int getFactionClaim(Faction f) {
		return f.getLandRounded();
	}

	public static int getFactionPlayersCount(Faction f) {
		return f.getFPlayers().size();
	}

	public static int getFactionAp(Faction f) {
		int apNb = 0;
		for (FLocation fLoc : Main.apList) {
			if (Board.getFactionAt(fLoc).getTag().equalsIgnoreCase(f.getTag()))
				apNb++;
		}
		return apNb;
	}

	public static int getFactionPoints(Faction f) {
		int fPoints = 0;
		fPoints = getFactionKills(f) * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Kill");
		fPoints += getFactionDeaths(f) * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Death");
		fPoints += getFactionClaim(f) * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Claim");
		fPoints += getFactionPlayersCount(f) * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Membre");
		fPoints += getFactionAp(f) * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.avantPoste");
		if (getFactionMoney(f) != 0) {
			fPoints += getFactionMoney(f) / 1000 * Main.getInstance().getConfig().getInt("Configuration.Stats-Factions.Points.Money");
		}
		return fPoints;
	}

	public static int getFactionPower(Faction f) {
		return f.getPowerRounded();
	}

	public static String encode(String s) {
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>') {
				out.append("&#" + (int) c + ";");
			} else {
				out.append(c);
			}
		}
		return out.toString();
	}

}
