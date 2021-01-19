package org.dec4234.decsnpcapi.npcHandlers;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.dec4234.decsnpcapi.decsNpcsSrc.DecsNpcsMain;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class NPC extends NPCManager {

	private String npcName;
	private UUID skinUUID;
	private Location loc;
	private float yaw;
	protected boolean looksAtPlayers;
	private int entityID;
	private EntityPlayer npcEntity;

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
		pc.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npcEntity));
		pc.sendPacket(new PacketPlayOutNamedEntitySpawn(npcEntity));
		pc.sendPacket(new PacketPlayOutEntityHeadRotation(npcEntity, (byte) (loc.getYaw() * 256 / 360)));
		DataWatcher watcher = npcEntity.getDataWatcher();
		watcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);
		pc.sendPacket(new PacketPlayOutEntityMetadata(this.getEntityID(), watcher, true));

		Bukkit.getScheduler().scheduleAsyncDelayedTask(DecsNpcsMain.getInstance(), () -> {
			pc.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npcEntity));
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

	public void damage() {
		PacketPlayOutAnimation pac = new PacketPlayOutAnimation(getEntityPlayer(), (byte) 1);
		for(Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(pac);
		}
	}

	public void setEquipment(EnumItemSlot eis, ItemStack is) {
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
		setField(packet, "a", getEntityID());
		setField(packet, "b", eis);
		setField(packet, "c", is);
		for(Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public void animate(byte animation) {
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
		setField(packet, "a", getEntityID());
		setField(packet, "b", animation);
		for(Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	private void setField(Object obj, String field_name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(field_name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
