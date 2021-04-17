package fr.koora.FactionStats;

public class Config {
	public static void checkConfig() {
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Hote") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Hote", "host");
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Identifiant") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Identifiant", "user");
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Password") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Password", "password");
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Port") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Port", 3306);
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Base") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Base", "base");
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Table") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Table", "factionstats");
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.Points.Kill") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Kill", 1);
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.Points.Death") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Death", -1);
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.Points.Claim") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Claim", 2);
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.Points.avantPoste") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.avantPoste", 3);
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.Points.Membre") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Membre", 10);
		}
		if (Main.getInstance().getConfig().getString("Configuration.Stats-Factions.Points.Money") == null) {
			Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Money", 1);
		}
		Main.getInstance().saveConfig();
	}

}
