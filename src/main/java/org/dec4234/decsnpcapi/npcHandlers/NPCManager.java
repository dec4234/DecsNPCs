package org.dec4234.decsnpcapi.npcHandlers;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.dec4234.decsnpcapi.decsNpcsSrc.DecsNpcsMain;

import java.util.ArrayList;
import java.util.HashMap;

public class NPCManager {

	public static ArrayList<NPC> npcs = new ArrayList<>();
	public static HashMap<Integer, NPC> npcsID = new HashMap<>();

	public ArrayList<NPC> getNPCS() {
		return npcs;
	}

	/**
	 * Spawns all NPCs for all players using the spawn() method
	 */
	public void spawnToAll() {
		for(Player pl : Bukkit.getOnlinePlayers()) {
			spawn(pl);
		}
	}

	/**
	 * Removes all NPCs for all players using the destroy() method
	 */
	public void destroyToAll() {
		for(Player pl : Bukkit.getOnlinePlayers()) {
			destroy(pl);
		}
	}

	/**
	 * Sends all NPCs to the player specified
	 * @param pl The name of the player you want to send the NPC information to
	 */
	@SuppressWarnings("deprecation")
	public void spawn(Player pl) {
		PlayerConnection c = ((CraftPlayer) pl).getHandle().playerConnection;

		for (NPC npc : npcs) {
			EntityPlayer entity = npc.getEntityPlayer();
			c.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
			c.sendPacket(new PacketPlayOutNamedEntitySpawn(entity));
			c.sendPacket(new PacketPlayOutEntityHeadRotation(entity, (byte) (npc.getYaw() * 256 / 360)));
			DataWatcher watcher = entity.getDataWatcher();
			watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);
			c.sendPacket(new PacketPlayOutEntityMetadata(npc.getEntityID(), watcher, true));

			Bukkit.getScheduler().scheduleAsyncDelayedTask(DecsNpcsMain.getInstance(), () -> {
				c.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entity));
			}, 80); 
		}
	}

	/**
	 * Gets the NPC object that matches the specified String name
	 * Returns null if there is no NPC with such name
	 * @param name The name of the NPC you are looking for
	 */
	public NPC getNPC(String name) {
		for(NPC npc : npcs) {
			if(npc.getName().contains(name)) {
				return npc;
			}
		}
		return null;
	}

	/**
	* Gets the NPC object that matches the specified entityID
	 * Returns null if there is no NPC with such entityID
	* @param id The entityID of the NPC
	 */
	public NPC getNPC(int id) {
		for(NPC npc : npcs) {
			if(npc.getEntityID() == id) {
				return npc;
			}
		}
		return null;
	}

	/**
	 * Removes all NPCs from view for the player specified
	 * @param pl The player who you want to block from seeing NPCs
	 */
	public void destroy(Player pl) {
		PlayerConnection c = ((CraftPlayer) pl).getHandle().playerConnection;
		for(NPC npc : npcs) {
			c.sendPacket(new PacketPlayOutEntityDestroy(npc.getEntityID()));
		}
	}

	/**
	 * Returns an ArrayList containing all NPCs which are in the world given to it
	 * @param world The world which you want to check for NPCs
	 */
	public ArrayList<NPC> getNPCsInWorld(World world) {
		ArrayList<NPC> npcsW = new ArrayList<>();
		for(NPC npc : getNPCS()) {
			if(npc.getLocation().getWorld().equals(world)) {
				npcsW.add(npc);
			}
		}

		return npcsW;
	}
}
