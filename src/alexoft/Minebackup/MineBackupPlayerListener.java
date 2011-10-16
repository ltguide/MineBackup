package alexoft.Minebackup;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MineBackupPlayerListener extends PlayerListener {
	private final MineBackup plugin;
	
	public MineBackupPlayerListener(MineBackup plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.isDirty = true;
	}
}
