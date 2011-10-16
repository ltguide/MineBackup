package alexoft.Minebackup;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Alexandre
 */
public class MineBackup extends JavaPlugin {
	public Config config;
	private final Logger log = Logger.getLogger("Minecraft");
	
	public boolean isBackupStarted;
	public boolean isDirty;
	
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		sendLog("v" + getDescription().getVersion() + " disabled");
	}
	
	public void onEnable() {
		config = new Config(this);
		isBackupStarted = false;
		isDirty = true;
		
		resetSchedule();
		
		getServer().getPluginCommand("mbck").setExecutor(new MineBackupCommandListener(this));
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, new MineBackupPlayerListener(this), Priority.Monitor, this);
		
		sendLog("v" + getDescription().getVersion() + " enabled (by ThisIsAreku; unofficial build by ltguide)");
	}
	
	public void resetSchedule() {
		getServer().getScheduler().cancelTasks(this);
		
		if (config.daystokeep != 0) getServer().getScheduler().scheduleAsyncRepeatingTask(this, new BackupsCleaner(this), 0, config.interval * 2);
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Backups(this, ""), config.firstDelay, config.interval);
	}
	
	public void executeBackup(String name) {
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Backups(this, name));
	}
	
	private String format(int i) {
		String r = String.valueOf(i);
		
		if (r.length() == 1) {
			r = "0" + r;
		}
		return r;
	}
	
	public String getBackupName(World world) {
		Calendar today = Calendar.getInstance();
		Map<String, String> formats = new HashMap<String, String>();
		formats.put("%Y", format(today.get(Calendar.YEAR)));
		formats.put("%M", format(today.get(Calendar.MONTH) + 1));
		formats.put("%D", format(today.get(Calendar.DAY_OF_MONTH)));
		formats.put("%H", format(today.get(Calendar.HOUR_OF_DAY)));
		formats.put("%m", format(today.get(Calendar.MINUTE)));
		formats.put("%S", format(today.get(Calendar.SECOND)));
		formats.put("%W", world.getName());
		formats.put("%U", world.getUID().toString());
		formats.put("%s", String.valueOf(world.getSeed()));
		
		String fname = config.bckFormat;
		for (Entry<String, String> entry : formats.entrySet())
			fname = fname.replaceAll(entry.getKey(), entry.getValue());
		
		return fname;
	}
	
	public void sendLog(String msg) {
		sendLog(Level.INFO, msg);
	}
	
	public void sendLog(Level level, String msg) {
		log.log(level, "[" + getDescription().getName() + "] " + ChatColor.stripColor(msg));
	}
	
	public void logException(Throwable e, String debugText) {
		sendLog(Level.SEVERE, "---------------------------------------");
		sendLog(Level.SEVERE, "--- an unexpected error has occured ---");
		sendLog(Level.SEVERE, "-- please send line below to the dev --");
		sendLog(Level.SEVERE, e.toString() + " : " + e.getLocalizedMessage());
		for (StackTraceElement t : e.getStackTrace()) {
			sendLog(Level.SEVERE, "\t" + t.toString());
		}
		if (config.debug && !debugText.equals("")) {
			sendLog(Level.SEVERE, "--------- DEBUG ---------");
			sendLog(Level.SEVERE, debugText);
			
		}
		sendLog(Level.SEVERE, "---------------------------------------");
	}
	
	public void logException(Throwable e) {
		logException(e, "");
	}
}
