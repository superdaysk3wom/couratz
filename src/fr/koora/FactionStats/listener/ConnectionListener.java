package fr.koora.FactionStats.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;

import fr.koora.FactionStats.Main;

public class ConnectionListener implements Listener {
	@EventHandler
	public void playerPreLogin(PlayerPreLoginEvent e) {
		if (Main.updating) {
			e.disallow(Result.KICK_WHITELIST, "§cLes statistiques factions sont en cours de mise à jours...");
		}
	}
}
