package alexoft.Minebackup;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

public class MineBackup extends JavaPlugin {
	public Config config;
	private final Logger log = Logger.getLogger("Minecraft");
	
	public boolean isBackupStarted;
	public boolean isDirty;
	public boolean shouldCleanBackups;
	
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		sendLog("v" + getDescription().getVersion() + " disabled");
	}
	
	public void onEnable() {
		config = new Config(this);
		isBackupStarted = false;
		isDirty = true;
		shouldCleanBackups = true;
		
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new TaskBackups(this), config.firstDelay, config.interval);
		
		getServer().getPluginCommand("minebackup").setExecutor(new MineBackupCommandListener(this));
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, new MineBackupPlayerListener(this), Priority.Monitor, this);
		
		sendLog("v" + getDescription().getVersion() + " enabled (by ThisIsAreku; unofficial build by ltguide)");
	}
	
	public void executeBackup(String name) {
		if (isBackupStarted) return;
		
		isBackupStarted = true;
		spawnBackupStage("begin", name);
	}
	
	public void spawnAutoSave(boolean save, long delay) {
		getServer().getScheduler().scheduleSyncDelayedTask(this, new TaskAutoSave(this, save), delay);
	}
	
	public void spawnBackupStage(String stage, String name) {
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new TaskBackupStage(this, stage, name));
	}
	
	public void sendLog(String msg) {
		sendLog(Level.INFO, msg);
	}
	
	public void sendLog(Level level, String msg) {
		log.log(level, "[" + getDescription().getName() + "] " + ChatColor.stripColor(msg));
	}
	
	public void logException(Throwable ex, String msg) {
		sendLog(Level.SEVERE, "---------------------------------------");
		if (!"".equals(msg)) sendLog(Level.SEVERE, "debug: " + msg);
		
		sendLog(Level.SEVERE, ex.toString() + " : " + ex.getLocalizedMessage());
		for (StackTraceElement stack : ex.getStackTrace())
			sendLog(Level.SEVERE, "\t" + stack.toString());
		
		sendLog(Level.SEVERE, "---------------------------------------");
	}
	
	public void logException(Throwable e) {
		logException(e, "");
	}
}
