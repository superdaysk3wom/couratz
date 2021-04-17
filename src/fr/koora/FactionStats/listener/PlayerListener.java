package fr.koora.FactionStats.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import fr.koora.FactionStats.Main;
import fr.koora.FactionStats.object.Players;

public class PlayerListener implements Listener {

	@EventHandler
	public void playerJoin(final PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		new Players(p);
		if (p.isOp() && !Main.sqlStatus) {
			p.sendMessage(Main.prefixe + "§cMerci de mettre des identifiants MySQL valides pour l'utilisation du plugin");
		}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		final Player p = e.getPlayer();
		Players dP = Players.players.get(p.getUniqueId());
		dP.saveData();
		Players.players.remove(p.getUniqueId());
	}

	@EventHandler
	public void playerKill(PlayerDeathEvent e) {
		if (e.getEntity().getKiller() instanceof Player) {
			Player deathPlayer = e.getEntity();
			Player killerPlayer = e.getEntity().getKiller();
			FPlayer fpk = FPlayers.i.get(deathPlayer);
			if (deathPlayer.getAddress().toString().equalsIgnoreCase(killerPlayer.getAddress().toString()) || fpk.getPowerRounded() <= -5)return;
			Players dPDeath = Players.players.get(deathPlayer.getUniqueId());
			Players dPKiller = Players.players.get(killerPlayer.getUniqueId());
			dPDeath.death++;
			dPKiller.kill++;
		}
	}

}