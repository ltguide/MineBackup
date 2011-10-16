package alexoft.Minebackup;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TaskAutoSave extends Thread {
	private final MineBackup plugin;
	private final boolean save;
	
	public TaskAutoSave(MineBackup plugin, boolean save) {
		this.plugin = plugin;
		this.save = save;
	}
	
	@Override
	public void run() {
		if (!save) Bukkit.savePlayers();
		
		for (World world : Bukkit.getWorlds()) {
			world.setAutoSave(save);
			if (!save) world.save();
		}
		
		plugin.spawnBackupStage(!save ? "backup" : "done", "");
	}
}
