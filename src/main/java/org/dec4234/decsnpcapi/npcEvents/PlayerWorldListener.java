package org.dec4234.decsnpcapi.npcEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.dec4234.decsnpcapi.npcHandlers.NPC;
import org.dec4234.decsnpcapi.npcHandlers.NPCManager;

public class PlayerWorldListener implements Listener {

	NPCManager npcm = new NPCManager();

	@EventHandler
	public void onWorldSwitch(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();

		for (NPC npc : npcm.getNPCsInWorld(p.getWorld())) {
			npcm.spawn(p);
		}
	}

}
