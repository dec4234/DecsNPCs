package org.dec4234.decsnpcapi.npcHandlers;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.DataWatcherRegistry;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import org.dec4234.decsnpcapi.decsNpcsSrc.DecsNpcsMain;

public class NPC extends NPCManager {

	String npcName;
	UUID skinUUID;
	Location loc;
	float yaw;
	boolean looksAtPlayers;
	int entityID;
	EntityPlayer npcEntity;

	public NPC(String npcName, UUID skinUUID, Location loc, boolean looksAtPlayers) {
		this.npcName = npcName;
		this.skinUUID = skinUUID;
		this.loc = loc;
		this.yaw = loc.getYaw();
		this.looksAtPlayers = looksAtPlayers;

		createNpc();

		this.entityID = npcEntity.getId();
	}

	public void createNpc() {
		npcEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), // dec's NPC
				((CraftWorld) loc.getWorld()).getHandle(), new GameProfile(skinUUID, npcName),
				new PlayerInteractManager(((CraftWorld) loc.getWorld()).getHandle()));

		npcEntity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

		npcs.add(this);
		npcsID.put(entityID, this);
	}

	public String getName() {
		return npcName;
	}

	public UUID getSkinUUID() {
		return skinUUID;
	}

	public Location getLocation() {
		return loc;
	}

	public float getYaw() {
		return yaw;
	}

	public boolean isLookingAtPlayersEnabled() {
		return looksAtPlayers;
	}

	public int getEntityID() {
		return entityID;
	}

	public EntityPlayer getEntityPlayer() {
		return npcEntity;
	}

	public List<Entity> getNearbyEntities() {
		return npcEntity.getBukkitEntity().getNearbyEntities(4, 4, 4);
	}

	public void destroy(Player p) {
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entityID));
		npcs.remove(this);
		npcsID.remove(entityID);
	}

	public void destroyForAll() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			destroy(p);
		}
	}

	public void teleport(Location loc, Player p) {
		npcEntity.setPosition(loc.getX(), loc.getY(), loc.getZ());
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(npcEntity));
	}

	public void teleportForAll(Location loc) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			teleport(loc, p);
		}
	}

	@SuppressWarnings("deprecation")
	public void respawn(Player p) {
		PlayerConnection pc = ((CraftPlayer) p).getHandle().playerConnection;
		pc.sendPacket(new PacketPlayOutEntityDestroy(entityID));
		pc.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npcEntity));
		pc.sendPacket(new PacketPlayOutNamedEntitySpawn(npcEntity));
		pc.sendPacket(new PacketPlayOutEntityHeadRotation(npcEntity, (byte) (loc.getYaw() * 256 / 360)));
		DataWatcher watcher = npcEntity.getDataWatcher();
		watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);
		pc.sendPacket(new PacketPlayOutEntityMetadata(this.getEntityID(), watcher, true));

		Bukkit.getScheduler().scheduleAsyncDelayedTask(DecsNpcsMain.getInstance(), () -> {
			pc.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npcEntity));
		}, 80);
	}

	public void lookAtPlayer(Player player) {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) pl).getHandle().playerConnection;
			Location npcLocation = getEntityPlayer().getBukkitEntity().getLocation();
			Location newNpcLocation = npcLocation.setDirection(player.getLocation().subtract(npcLocation).toVector());
			float yaw = newNpcLocation.getYaw();
			float pitch = newNpcLocation.getPitch();
			connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(getEntityPlayer().getId(),
					(byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));
			connection.sendPacket(
					new PacketPlayOutEntityHeadRotation(getEntityPlayer(), (byte) ((yaw % 360.) * 256 / 360)));
		}
	}
}
