package org.dec4234.decsnpcapi.npcHandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.dec4234.decsnpcapi.decsNpcsSrc.DecsNpcsMain;


public class LookAtPlayers extends NPCManager {

	public LookAtPlayers() {
		lookAtPlayers();
	}

	public void lookAtPlayers() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(DecsNpcsMain.getInstance(), () -> {
			for (NPC npc : npcs) {
				if (npc.looksAtPlayers) {
					second: for (Entity e : npc.getNearbyEntities()) {
						if (e.getType() == EntityType.PLAYER) {
							npc.lookAtPlayer((Player) e);
							break second;
						}
					}
				}
			}
		}, 100, 4);
	}
}
