package alexoft.Minebackup;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TaskBackupStage extends Thread {
	private final MineBackup plugin;
	private final String stage;
	private final String userStarted;
	
	public TaskBackupStage(MineBackup plugin, String stage, String userStarted) {
		this.plugin = plugin;
		this.stage = stage;
		this.userStarted = userStarted;
	}
	
	@Override
	public void run() {
		if ("begin".equals(stage)) begin();
		else if ("backup".equals(stage)) backup();
		else done();
	}
	
	private void begin() {
		//pre backup actions
		if (!"".equals(userStarted)) SendMessage(plugin.config.msg_BackupStartedUser.replaceAll("%player%", userStarted));
		else SendMessage(plugin.config.msg_BackupStarted);
		
		plugin.spawnAutoSave(false, 3L * 20L);
	}
	
	private void backup() {
		for (World world : Bukkit.getWorlds())
			doBackup(world, world.getName());
		
		deleteDir(new File(plugin.config.bckTempDir));
		
		plugin.spawnAutoSave(true, 0L);
	}
	
	private void done() {
		//post backup actions
		
		if (plugin.config.daystokeep > 0) {
			if (plugin.shouldCleanBackups) new CleanBackups(plugin).clean();
			plugin.shouldCleanBackups = !plugin.shouldCleanBackups;
		}
		
		SendMessage(plugin.config.msg_BackupEnded);
		plugin.isDirty = false;
		plugin.isBackupStarted = false;
	}
	
	private void SendMessage(String msg) {
		if (plugin.config.msg_enable) plugin.getServer().broadcastMessage(msg);
		else plugin.sendLog(msg);
	}
	
	private void doBackup(World world, String name) {
		if (!plugin.config.worlds.contains(name)) {
			plugin.sendLog(" * " + world.getName() + " -- skipping");
			return;
		}
		
		String format = getFormat(world);
		File worldDir = new File(".", name); //getServer().getWorldContainer()
		
		if (plugin.config.compressionEnabled) {
			plugin.sendLog(" * " + name + " -- compressing");
			File tempDir = new File(plugin.config.bckTempDir, format);
			if (copyDir(worldDir, new File(tempDir, name))) compressDir(tempDir, new File(plugin.config.bckDir, format + ".zip"));
		}
		else {
			plugin.sendLog(" * " + name + " -- copying");
			copyDir(worldDir, new File(plugin.config.bckDir, format));
		}
	}
	
	private boolean copyDir(File srcDir, File destDir) {
		try {
			DirUtils.copyDirectory(srcDir, destDir);
		}
		catch (Exception ex) {
			plugin.sendLog("\t\\ failed");
			plugin.logException(ex);
			deleteDir(destDir);
			return false;
		}
		
		return true;
	}
	
	private void deleteDir(File tarDir) {
		DirUtils.deleteDirectory(tarDir);
		if (tarDir.exists()) plugin.sendLog(Level.WARNING, "unable to delete directory: " + tarDir);
	}
	
	private void compressDir(File tempDir, File destFile) {
		try {
			ZipUtils.zipDir(tempDir, destFile, plugin.config.compressionMode, plugin.config.compressionLevel);
            if(!plugin.dropbox.requiresAuth){
                FileInputStream fis = new FileInputStream(destFile);
                plugin.dropbox.uploadFile(fis, destFile.getPath().substring(plugin.config.bckDir.length()).replace("\\","/"), destFile.length());
                fis.close();
            }
		}
		catch (Exception ex) {
			plugin.sendLog("\t\\ failed");
			plugin.logException(ex);
			destFile.delete();
		}
	}
	
	private String padZero(int i) {
		return String.format("%02d", i).toString();
	}
	
	public String getFormat(World world) {
		Calendar date = Calendar.getInstance();
		Map<String, String> formats = new HashMap<String, String>();
		formats.put("%Y", padZero(date.get(Calendar.YEAR)));
		formats.put("%M", padZero(date.get(Calendar.MONTH) + 1));
		formats.put("%D", padZero(date.get(Calendar.DAY_OF_MONTH)));
		formats.put("%H", padZero(date.get(Calendar.HOUR_OF_DAY)));
		formats.put("%m", padZero(date.get(Calendar.MINUTE)));
		formats.put("%S", padZero(date.get(Calendar.SECOND)));
		formats.put("%W", world.getName());
		formats.put("%U", world.getUID().toString());
		formats.put("%s", String.valueOf(world.getSeed()));
		
		String format = plugin.config.bckFormat;
		for (Entry<String, String> entry : formats.entrySet())
			format = format.replaceAll(entry.getKey(), entry.getValue());
		
		return format;
	}
}
