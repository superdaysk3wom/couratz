package fr.koora.FactionStats.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import fr.koora.FactionStats.Main;
import fr.koora.FactionStats.mysql.MySQLManager;
import fr.koora.FactionStats.task.FactionUpdater;

public class FactionStats implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§7---------- §6FactionStats Aide §7----------");
			sender.sendMessage("§6/fs info §7- Afficher les informations principales du plugin");
			sender.sendMessage("§6/fs classement §7- Afficher le top 5 des meilleurs factions");
			sender.sendMessage("§6/fs show <faction> §7- Afficher les statistiques de la faction");

			if (sender.isOp()) {
				sender.sendMessage("§6/fs setpoints <attribut> <points> §7- Permet de définir les points rapportés par les factions");
				sender.sendMessage("§6/fs setsql <PARAMETRES> <DEFINIT> §7- Configuration de la connexion SQL");
				sender.sendMessage("§6/fs reconnect §7- Permet de se connecter aux paramètres MySQL définit");
				sender.sendMessage("§6/fs currentsql §7- Information à propos de la connexion MySQL");
				sender.sendMessage("§6/fs upload §7- Forcer l'upload des données");
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("info")) {
			sender.sendMessage("§7---------- §6FactionStats Infos §7----------");
			sender.sendMessage("§6Nom du plugin §7- FactionStats");
			sender.sendMessage("§6Version §7- 2.6");
			sender.sendMessage("§6Auteur §c- Koora_ ");
			sender.sendMessage("§6Dernière mise à jour §7- 03/10/2015 - 22:00");
			if (Main.sqlStatus)
				sender.sendMessage("§6MySQL §7- §2Actif");
			else if (!Main.sqlStatus)
				sender.sendMessage("§6MySQL §7- §cInactif");
			return true;

		} else if (args[0].equalsIgnoreCase("show")) {
			if (args.length == 2) {
				sender.sendMessage("§7---------- §6FactionStats Statistiques §7----------");
				Faction f = Factions.i.getByTag(args[1]);
				if (f == null) {
					sender.sendMessage("§cLa faction spécifié est introuvable.");
					return true;
				}
				if (!f.isSafeZone() || !f.isWarZone() && f.isNormal()) {
					sender.sendMessage("§7Faction - §6" + f.getTag());
					sender.sendMessage("§7Rang - §6" + FactionStats.getRank(f));
					sender.sendMessage("§7Chef - §6" + f.getFPlayerAdmin().getName());
					sender.sendMessage("§7Avant Poste(s) - §6" + FactionUpdater.getFactionAp(f));
					sender.sendMessage("§7Claim(s) - §6" + FactionUpdater.getFactionClaim(f));
					sender.sendMessage("§7Membre(s) - §6" + FactionUpdater.getFactionPlayersCount(f));
					sender.sendMessage("§7Power - §6" + FactionUpdater.getFactionPower(f));
					sender.sendMessage("§7Argent - §6" + FactionUpdater.getFactionMoney(f));
					sender.sendMessage("§7Tué(s) - §6" + FactionUpdater.getFactionKills(f));
					sender.sendMessage("§7Mort(s) - §6" + FactionUpdater.getFactionDeaths(f));
					sender.sendMessage("§7Ratio - §6" + FactionUpdater.getFactionRatio(f));
					sender.sendMessage("§7Point(s) - §6" + FactionUpdater.getFactionPoints(f));
				}
			} else if (args.length == 1) {
				sender.sendMessage("§7---------- §6FactionStats Statistiques §7----------");
				FPlayer fp = FPlayers.i.get((Player) sender);
				Faction f = fp.getFaction();
				if (!f.isSafeZone() || !f.isNone() || !f.isWarZone() && f.isNormal()) {
					sender.sendMessage("§6Faction §7- " + f.getTag());
					sender.sendMessage("§6Rang §7- " + FactionStats.getRank(f));
					sender.sendMessage("§6Chef §7- " + f.getFPlayerAdmin().getName());
					sender.sendMessage("§6Avant Poste(s) §7- " + FactionUpdater.getFactionAp(f));
					sender.sendMessage("§6Claim(s) §7- " + FactionUpdater.getFactionClaim(f));
					sender.sendMessage("§6Membre(s) §7- " + FactionUpdater.getFactionPlayersCount(f));
					sender.sendMessage("§6Power §7- " + FactionUpdater.getFactionPower(f));
					sender.sendMessage("§6Argent §7- " + FactionUpdater.getFactionMoney(f));
					sender.sendMessage("§6Tué(s) §7- " + FactionUpdater.getFactionKills(f));
					sender.sendMessage("§6Mort(s) §7- " + FactionUpdater.getFactionDeaths(f));
					sender.sendMessage("§6Ratio §7- " + FactionUpdater.getFactionRatio(f));
					sender.sendMessage("§6Point(s) §7- " + FactionUpdater.getFactionPoints(f));
				} else {
					sender.sendMessage("§cTu n'es pas dans une faction.");
				}
			}
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("classement")) {
			sender.sendMessage("§7---------- §6FactionStats Classement §7----------");
			FPlayer fp = FPlayers.i.get((Player) sender);
			Faction f = fp.getFaction();
			if (!f.isSafeZone() || !f.isWarZone() && f.isNormal())
				sender.sendMessage("§6Ta faction se situe au rang §cN°" + FactionStats.getRank(f));
			int i = 0;
			for (Entry<Faction, Integer> str : entriesSortedByValues(Main.factions)) {
				i++;
				if (i > 5)
					break;
				sender.sendMessage("§6N°" + i + " - §c" + str.getKey().getTag() + " avec " + str.getValue() + (str.getValue() > 1 ? " points." : " point."));
			}
			return true;
		}
		if (!sender.isOp()) {
			sender.sendMessage(Main.prefixe + "§cErreur: Tu n'as pas la permission d'effectuer cette commande.");
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reconnect")) {
				sender.sendMessage(Main.prefixe + " Reconnexion à la base de donnée...");
				new MySQLManager();
			} else if (args[0].equalsIgnoreCase("currentsql")) {
				String etoile = "";
				for (int i = 0; i < Main.getInstance().getConfig()
						.getString("Configuration.Stats-Factions.SQL.Password").length() - 1; i++) {
					etoile = etoile + "*";
				}
				sender.sendMessage("§7---------- §6FactionStats MySQL §7----------");
				sender.sendMessage("§6Hôte §7- " + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Hote"));
				sender.sendMessage("§6Identifiant §7- " + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Identifiant"));
				sender.sendMessage("§6Mot de passe §7- " + etoile);
				sender.sendMessage("§6Nom de la base §7- " + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Base"));
				sender.sendMessage("§6Port §7- " + Main.getInstance().getConfig().getString("Configuration.Stats-Factions.SQL.Port"));
				if (Main.sqlStatus)
					sender.sendMessage("§6MySQL - §2Actif");
				else if (!Main.sqlStatus)
					sender.sendMessage("§6MySQL - §cInactif");
			} else if (args[0].equalsIgnoreCase("uploadinfo")) {
				if (Main.updating) {
					sender.sendMessage("§7---------- §6FactionStats Upload §7----------");
					if (Main.indiceFactionsUpload > 0)
						sender.sendMessage(" §6En cours §7- " + Main.indiceFactionsUpload * 100 / (Factions.i.get().size() - 3) + "%");
					else
						sender.sendMessage(" §6En cours: §cErreur !");

					sender.sendMessage(" §6Faction N°" + Main.indiceFactionsUpload + "/" + (Factions.i.get().size() - 3));
				} else {
					sender.sendMessage(Main.prefixe + " §cAucun upload en cours !");
				}
			} else if (args[0].equalsIgnoreCase("stopupload")) {
				if (Main.updating) {
					Main.updating = false;
					sender.sendMessage(Main.prefixe + " §aUpload interrompu !");
				} else {
					sender.sendMessage(Main.prefixe + " §cAucun upload en cours, comment je l'arrête moi hein ? :p");
				}
			} else if (args[0].equalsIgnoreCase("upload")) {
				if (!Main.sqlStatus)
					sender.sendMessage(" §cLe service SQL est invalide actuellement. Faites /fs currentSQL !");
				else if (Main.updating)
					sender.sendMessage(" §cUne mise à jour du classement faction est déjà en cours d'exécution...");
				else {
					Main.updating = true;
					new FactionUpdater();
					sender.sendMessage(" §aTu as lancé manuellement la mise à jour du classement des factions !");
				}
			} else {
				sender.sendMessage("§cErreur: Commande inconnue.");
			}
		} else if (args.length == 2) {
			sender.sendMessage("§cErreur: Commande inconnue.");
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("setpoints")) {
				if (Main.isNumber(args[2])) {
					int points = 0;
					points = Main.toNumber(args[2]);
					if (args[1].equalsIgnoreCase("kill")) {
						Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Kill", points);
						Main.getInstance().saveConfig();
						sender.sendMessage(Main.prefixe + " §6Chaque kill rapportera désormais " + points + " à la faction !");
					} else if (args[1].equalsIgnoreCase("death")) {
						Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Death", points);
						Main.getInstance().saveConfig();
						sender.sendMessage(Main.prefixe + " §6Chaque mort rapportera désormais " + points + " à la faction !");
					} else if (args[1].equalsIgnoreCase("claim")) {
						Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Claim", points);
						Main.getInstance().saveConfig();
						sender.sendMessage(Main.prefixe + " §6Chaque claim rapportera désormais " + points + " à la faction !");
					} else if (args[1].equalsIgnoreCase("membre")) {
						Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Membre", points);
						Main.getInstance().saveConfig();
						sender.sendMessage(Main.prefixe + " §6Chaque membre rapportera désormais " + points + " à la faction !");
					} else if (args[1].equalsIgnoreCase("ap")) {
						Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.avantPoste", points);
						Main.getInstance().saveConfig();
						sender.sendMessage(Main.prefixe + " §6Chaque avant poste rapportera désormais " + points + " à la faction !");
					} else if (args[1].equalsIgnoreCase("money")) {
						Main.getInstance().getConfig().set("Configuration.Stats-Factions.Points.Money", points);
						Main.getInstance().saveConfig();
						sender.sendMessage(Main.prefixe + "§6Chaque unité de monais rapportera désormais " + points + " à la faction !");
					} else {
						sender.sendMessage(Main.prefixe + " §6Tu peux définir les points à attribuer seulement pour: KILL|DEATH|CLAIM|AP|MEMBRE|MONEY");
						sender.sendMessage("§6Exemple: §6/fs setpoints KILL 2");
					}
				} else {
					sender.sendMessage(Main.prefixe + "§cLe nombre de point ne peut pas se définir par: " + args[2]);
					sender.sendMessage("§cVeuillez définir un nombre entier positif ou négatif.");
				}
			} else if (args[0].equalsIgnoreCase("setsql")) {
				if (args[1].equalsIgnoreCase("host")) {
					Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Hote", args[2]);
					Main.getInstance().saveConfig();
					sender.sendMessage(Main.prefixe + " §6Désormais, l'hôte définit est " + args[2]);
				} else if (args[1].equalsIgnoreCase("username")) {
					Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Identifiant", args[2]);
					Main.getInstance().saveConfig();
					sender.sendMessage(Main.prefixe + " §6Désormais, l'identifiant MySQL est " + args[2]);
				} else if (args[1].equalsIgnoreCase("password")) {
					Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Password", args[2]);
					Main.getInstance().saveConfig();
					sender.sendMessage(Main.prefixe + " §6Désormais, le mot de passe MySQL est " + args[2]);
				} else if (args[1].equalsIgnoreCase("port")) {
					Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Port", args[2]);
					Main.getInstance().saveConfig();
					sender.sendMessage(Main.prefixe + " §6Désormais, le port MySQL est " + args[2]);
				} else if (args[1].equalsIgnoreCase("table")) {
					Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Table", args[2]);
					Main.getInstance().saveConfig();
					sender.sendMessage(Main.prefixe + " §6Désormais, le nom de la table MySQL est " + args[2]);
				} else if (args[1].equalsIgnoreCase("base")) {
					Main.getInstance().getConfig().set("Configuration.Stats-Factions.SQL.Base", args[2]);
					Main.getInstance().saveConfig();
					sender.sendMessage(Main.prefixe + " §6Désormais, le nom de la base MySQL est " + args[2]);
				} else {
					sender.sendMessage(Main.prefixe + "§cTu peux définir: HOST|USERNAME|PASSWORD|PORT|BASE|TABLE");
					sender.sendMessage("§6Exemple: /fs setsql USERNAME DIIZA");
				}
			}
		} else if (args.length < 3) {
			sender.sendMessage(Main.prefixe + "§cErreur: Commande inconnue.");
		}
		return true;
	}

	public static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

	public static int getRank(Faction f) {
		int i = 0;
		for (Entry<Faction, Integer> str : entriesSortedByValues(Main.factions)) {
			i++;
			if (str.getKey().equals(f))
				break;
		}
		return i;
	}

}
