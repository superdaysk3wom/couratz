package fr.koora.FactionStats.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;

import fr.koora.FactionStats.Main;

public class MySQLManager {

	public MySQLManager() {
		mysqlConnect();
	}

	private void mysqlConnect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			String url = "jdbc:mysql://" + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Hote") + "/" + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Base") + "?autoReconnect=true";
			String user = Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Identifiant");
			String passwd = Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Password");
			Main.conn = DriverManager.getConnection(url, user, passwd);
			Main.state = Main.conn.createStatement();
			Main.state.executeUpdate("CREATE TABLE IF NOT EXISTS " + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Table") + " (Faction_ID varchar(100) NOT NULL, Faction_NAME varchar(50) NOT NULL, Faction_LEADER varchar(50) NOT NULL,Faction_DESCRIPTION varchar(1000) NOT NULL,Faction_CLAIM int(5) NOT NULL,Faction_AP int(5) NOT NULL, Faction_MEMBRE varchar(2) NOT NULL, Faction_POWER int(2) NOT NULL, Faction_POWERMAX int(2) NOT NULL, Faction_ARGENT int(15) NOT NULL, Faction_TOTALMEMBRE varchar(500) NOT NULL, Faction_KILLS int(10) NOT NULL, Faction_MORTS int(10) NOT NULL, Faction_POINTS int(10) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			Main.state.close();
			Main.log.info("Connexion à la base de donnée réussite !");
			Main.sqlStatus = true;
		} catch (SQLException e) {
			Main.log.severe("SQL error while connecting to database: " + e);
			Main.sqlStatus = false;
		}
	}

}