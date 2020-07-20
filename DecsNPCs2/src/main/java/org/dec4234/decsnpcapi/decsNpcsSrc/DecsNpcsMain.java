package org.dec4234.decsnpcapi.decsNpcsSrc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dec4234.decsnpcapi.decsNpcsCustomEvents.PacketReader;
import org.dec4234.decsnpcapi.npcEvents.JoinEvents;
import org.dec4234.decsnpcapi.npcEvents.PlayerWorldListener;
import org.dec4234.decsnpcapi.npcHandlers.LookAtPlayers;
import org.dec4234.decsnpcapi.npcHandlers.NPCManager;


public class DecsNpcsMain extends JavaPlugin {

	private static DecsNpcsMain instance;
	
	NPCManager npcm = new NPCManager();
	
	@Override
	public void onEnable() {
		instance = this;
		npcm.spawnToAll();
		Bukkit.getPluginManager().registerEvents(new JoinEvents(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerWorldListener(), this);
		
		new LookAtPlayers();
		validatePlayers();
	}
	
	@Override
	public void onDisable() {
		npcm.destroyToAll();
		Bukkit.getScheduler().cancelTasks(this);
		invalidatePlayers();
	}
	
	public static DecsNpcsMain getInstance() { return instance; }
	
	public void validatePlayers() {
		if (!Bukkit.getOnlinePlayers().isEmpty()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				PacketReader reader = new PacketReader();
				reader.inject(p);
			}
		}
	}

	public void invalidatePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PacketReader reader = new PacketReader();
			reader.uninject(p);
		}
	}
}
