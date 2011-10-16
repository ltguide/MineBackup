package alexoft.Minebackup;

public class TaskBackups extends Thread {
	private MineBackup plugin;
	public String userStarted;
	
	public TaskBackups(MineBackup plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		if (plugin.isBackupStarted || (!plugin.isDirty && plugin.getServer().getOnlinePlayers().length == 0)) return;
		
		plugin.executeBackup("");
	}
}
