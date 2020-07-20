package org.dec4234.decsnpcapi.npcEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.dec4234.decsnpcapi.decsNpcsCustomEvents.PacketReader;
import org.dec4234.decsnpcapi.npcHandlers.NPCManager;


public class JoinEvents implements Listener {

	NPCManager npcm = new NPCManager();

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		npcm.spawn(p);

		PacketReader reader = new PacketReader();
		reader.inject(p);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		PacketReader reader = new PacketReader();
		reader.uninject(e.getPlayer());
	}
}
