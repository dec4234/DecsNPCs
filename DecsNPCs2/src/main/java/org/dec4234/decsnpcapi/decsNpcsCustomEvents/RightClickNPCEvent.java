package org.dec4234.decsnpcapi.decsNpcsCustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.dec4234.decsnpcapi.npcHandlers.NPC;
import org.dec4234.decsnpcapi.npcHandlers.NPCManager;

public class RightClickNPCEvent extends Event implements Cancellable {

	private final Player player;
	private final EntityPlayer ep;
	private final NPCInteractType type;
	private boolean isCancelled;
	private static final HandlerList HANDLERS = new HandlerList();
	
	NPCManager npcm = new NPCManager();

	public RightClickNPCEvent(Player player, EntityPlayer ep, NPCInteractType type) {
		this.player = player;
		this.ep = ep;
		this.type = type;
	}

	public Player getPlayer() {
		return player;
	}

	public NPC getNPC() {
		for(NPC npc : NPCManager.npcs) {
			if(ep.getId() == npc.getEntityID()) {
				return npc;
			}
		}
		return null;
	}
	
	public String getName() {
		return ep.displayName;
	}

	
	public NPCInteractType getInteractType() {
		return type;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg) {
		isCancelled = arg;

	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	
}
