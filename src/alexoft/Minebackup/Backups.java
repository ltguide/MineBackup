package alexoft.Minebackup;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * 
 * @author Alexandre
 */
public class Backups extends Thread {
	private MineBackup plugin;
	public String userStarted = "";
	
	public Backups(MineBackup plugin, String userName) {
		this.plugin = plugin;
		this.userStarted = userName;
	}
	
	public void MakeBackup() {
		plugin.sendLog("Starting backup...");
		for (String w : plugin.config.worlds) {
			World world = plugin.getServer().getWorld(w);
			
			if (world == null) plugin.sendLog("world '" + w + "' not found.. check your config file");
			else MakeBackup(world);
		}
	}
	
	public void MakeBackup(World world) {
		try {
			plugin.sendLog(" * " + world);
			
			//if (plugin.config.compressionEnabled) {
			File tempDir = new File(plugin.config.bckTempDir, String.valueOf(Math.random()));
			tempDir.mkdirs();
			
			copyWorld(world, tempDir, false);
			compressDir(tempDir, world);
			//	}
			//	else copyWorld(world, new File(plugin.config.bckDir), true);
		}
		catch (Exception ex) {
			plugin.logException(ex);
		}
	}
	
	public void compressDir(File tempDir, World world) {
		String BACKUP_NAME = new File(plugin.config.bckDir, plugin.getBackupName(world)).toString();
		int last = BACKUP_NAME.lastIndexOf(File.separator);
		String dir = BACKUP_NAME.substring(0, last);
		String file = BACKUP_NAME.substring(last + 1);
		if (!new File(dir).exists()) new File(dir).mkdirs();
		if (plugin.config.compressionEnabled) {
			plugin.sendLog("\tCompressing...");
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new TaskZipDir(plugin, tempDir.getPath(), dir + "/" + file + ".zip"));
		}
		else {
			plugin.sendLog("\tCopying...");
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new TaskCopyDir(plugin, tempDir.getPath(), dir + "/" + file));
		}
	}
	
	private void copyWorld(World world, File destDir, boolean verbose) throws Exception {
		try {
			alexoft.Minebackup.DirUtils.copyDirectory(new File(world.getName()), new File(destDir.getPath() + "/" + world.getName()));
		}
		catch (Exception ex) {
			alexoft.Minebackup.DirUtils.deleteDirectory(destDir);
			throw ex;
		}
	}
	
	private void worldsSetAutoSave(boolean save) {
		for (World world : Bukkit.getWorlds())
			world.setAutoSave(true);
	}
	
	private void worldsSave() {
		for (World world : Bukkit.getWorlds())
			world.save();
	}
	
	private void SendMessage(String m) {
		if (plugin.config.msg_enable) plugin.getServer().broadcastMessage(m);
	}
	
	@Override
	public void run() {
		if (plugin.isBackupStarted || (!plugin.isDirty && plugin.getServer().getOnlinePlayers().length == 0)) return;
		
		plugin.isDirty = false;
		preBackup();
	}
	
	private void preBackup() {
		plugin.isBackupStarted = true;
		if (!"".equals(userStarted)) SendMessage(plugin.config.msg_BackupStartedUser.replaceAll("%player%", userStarted));
		else SendMessage(plugin.config.msg_BackupStarted);
		
		worldsSetAutoSave(false);
		Bukkit.savePlayers();
		worldsSave();
		
		MakeBackup();
		
		postBackup();
	}
	
	public void postBackup() {
		plugin.sendLog("Done!");
		worldsSetAutoSave(true);
		
		SendMessage(plugin.config.msg_BackupEnded);
		plugin.isBackupStarted = false;
	}
}
