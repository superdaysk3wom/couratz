package fr.koora.FactionStats.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

import fr.koora.FactionStats.command.FactionStats;

public class ChatListener implements Listener {
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player p = e.getPlayer();
		FPlayer fp = FPlayers.i.get(p);
		Faction f = fp.getFaction();
		int rank = FactionStats.getRank(f);
		ChatColor cc = rank > 3 ? (rank > 25 ? ChatColor.RED : ChatColor.GOLD) : ChatColor.GREEN;
		if (!f.isSafeZone() && !f.isWarZone() && f.isNormal() && !f.isNone()) {
			e.setFormat(ChatColor.GRAY + "[" + cc + FactionStats.getRank(f) + ChatColor.GRAY + "] " + ChatColor.RESET + e.getFormat());
		} else {
			e.setFormat(e.getFormat().replace(ChatColor.GRAY + "[" + cc + FactionStats.getRank(f) + ChatColor.GRAY + "] " + ChatColor.RESET, ""));
		}
	}

}