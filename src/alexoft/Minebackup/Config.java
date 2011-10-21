package alexoft.Minebackup;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	private final MineBackup plugin;
	private FileConfiguration cfg;
	
	public Config(MineBackup plugin) {
		this.plugin = plugin;
		loadConfig();
	}
	
	/* configuration fields */
	public List<String> worlds;
	
	public String bckDir;
	public String bckTempDir;
	public String bckFormat;
	
	public long interval;
	public long firstDelay;
	public long daystokeep;
	
	public boolean backupPlugins;
	public boolean debug;
	
	public boolean msg_enable;
	public String msg_BackupStarted;
	public String msg_BackupEnded;
	public String msg_BackupStartedUser;
	
	public boolean compressionEnabled;
	public int compressionMode;
	public int compressionLevel;
	
	/* end configuration fields */

	@SuppressWarnings("unchecked") //prevents warning about worlds being an unchecked conversion, line 54
  public void loadConfig() {
		try {
			plugin.sendLog("Loading configuration...");
			boolean rewrite = false;
			
			cfg = this.plugin.getConfig();
			
			worlds = cfg.getList("worlds", new ArrayList<String>());
			
			bckDir = cfg.getString("backup.dir", "minebackup");
			bckTempDir = cfg.getString("backup.temp-dir", "minebackup_temp");
			bckFormat = cfg.getString("backup.format", "%W/%Y-%M-%D_%H-%m-%S");
			
			interval = cfg.getInt("time.interval", -1);
			firstDelay = cfg.getInt("time.delay", -1);
			daystokeep = cfg.getInt("time.days-to-keep", -1);
			
			debug = cfg.getBoolean("options.debug", false);
			backupPlugins = cfg.getBoolean("options.backup-plugins", true);
			
			msg_enable = cfg.getBoolean("messages.enabled", true);
			msg_BackupEnded = cfg.getString("messages.backup-ended", ChatColor.GREEN + "[MineBackup] Backup ended");
			msg_BackupStarted = cfg.getString("messages.backup-started", ChatColor.GREEN + "[MineBackup] Backup started");
			msg_BackupStartedUser = cfg.getString("messages.backup-started-user", ChatColor.GREEN + "[MineBackup] Backup started by %player%");
			
			compressionEnabled = cfg.getBoolean("compression.enabled", true);
			String s_compressionMode = cfg.getString("compression.mode", null);
			String s_compressionLevel = cfg.getString("compression.level", null);
			
			compressionLevel = Deflater.BEST_COMPRESSION;
			compressionMode = ZipOutputStream.DEFLATED;
			
			if (compressionEnabled) {
				if (s_compressionLevel == null) {
					s_compressionLevel = "BEST_COMPRESSION";
					cfg.set("compression.level", s_compressionLevel);
					rewrite = true;
				}
				else {
					if (s_compressionLevel == "BEST_COMPRESSION") {
						compressionLevel = Deflater.BEST_COMPRESSION;
					}
					else if (s_compressionLevel == "BEST_SPEED") {
						compressionLevel = Deflater.BEST_SPEED;
					}
					else if (s_compressionLevel == "NO_COMPRESSION") {
						compressionLevel = Deflater.NO_COMPRESSION;
					}
				}
				
				if (s_compressionMode == null) {
					s_compressionMode = "DEFLATED";
					cfg.set("compression.mode", s_compressionMode);
					rewrite = true;
				}
				else {// TODO: compressionMethod
				}
			}
			if (worlds.isEmpty()) {
				for (World w : plugin.getServer().getWorlds()) {
					worlds.add(w.getName());
				}
				cfg.set("worlds", worlds);
				rewrite = true;
			}
			
			if (interval <= 0) {
				interval = 3600;
				cfg.set("time.interval", interval);
				rewrite = true;
			}
			
			if (firstDelay < 0) {
				firstDelay = 10;
				cfg.set("time.delay", firstDelay);
				rewrite = true;
			}
			
			if (daystokeep < 0) {
				daystokeep = 5;
				cfg.set("time.days-to-keep", firstDelay);
				rewrite = true;
			}
			
			interval *= 20;
			firstDelay *= 20;
			if (rewrite) {
				this.plugin.saveConfig();
			}
			plugin.sendLog(worlds.size() + " worlds loaded.");
		}
		catch (Exception e) {
			plugin.logException(e, "Error while loading config");
		}
	}
}
