package org.dec4234.decsnpcapi.decsNpcsCustomEvents;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import org.dec4234.decsnpcapi.decsNpcsSrc.DecsNpcsMain;
import org.dec4234.decsnpcapi.npcHandlers.NPC;
import org.dec4234.decsnpcapi.npcHandlers.NPCManager;

public class PacketReader {

	Channel channel;
	public static Map<UUID, Channel> channels = new HashMap<UUID, Channel>();
	NPCManager npcm = new NPCManager();
	private NPCInteractType type = null;

	public void inject(Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
		channels.put(player.getUniqueId(), channel);

		if (channel.pipeline().get("PacketInjector") != null)
			return;
		
		channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {

			@Override
			protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg)
					throws Exception {
				arg.add(packet);
				readPacket(player, packet);
			}

		});
	}

	public void uninject(Player player) {
		channel = channels.get(player.getUniqueId());
		channels.remove(player.getUniqueId());
		if (channel.pipeline().get("PacketInjector") != null)
			channel.pipeline().remove("PacketInjector");
	}

	public void readPacket(Player player, Packet<?> packet) {
		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
			int id = (int) getValue(packet, "a");
			NPC npc = getNPC(id);
			if(npc == null) return;
			
			if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")) {
				type = NPCInteractType.ATTACK;
			}
			/*
			if (getValue(packet, "d").toString().equalsIgnoreCase("OFF_HAND")) {
				type = NPCInteractType.OFFHAND;
			} */
				
			if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT")) {
				type = NPCInteractType.INTERACTAT;
			}
				

			if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")) {
				type = NPCInteractType.INTERACT;
			}
			
			if(type == null) {
				type = NPCInteractType.UNKNOWN;
			}	
			
			Bukkit.getScheduler().runTask(DecsNpcsMain.getInstance(), new Runnable() {

				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new RightClickNPCEvent(player, 
							npc.getEntityPlayer(), 
							type));
				}

			});
			
			type = null;
		}

	}

	private NPC getNPC(int id) {
		for(NPC npc : npcm.getNPCS()) {
			if(npc.getEntityID() == id) {
				return npc;
			}
		}
		return null;
	}
	
	private Object getValue(Object instance, String name) {
		Object result = null;

		try {
			Field field = instance.getClass().getDeclaredField(name);

			field.setAccessible(true);

			result = field.get(instance);

			field.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
