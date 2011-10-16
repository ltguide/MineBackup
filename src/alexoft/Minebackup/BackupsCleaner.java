package alexoft.Minebackup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * 
 * @author Alexandre
 */
public class BackupsCleaner extends Thread {
	private MineBackup plugin;
	
	public BackupsCleaner(MineBackup plugin) {
		this.plugin = plugin;
	}
	
	public static long getDifference(long a, long b, TimeUnit units) {
		return units.convert(b - a, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void run() {
		long now = System.currentTimeMillis();
		long diffDays;
		int bckDeleted = 0;
		for (File f : recursiveListFiles(new File(this.plugin.config.bckDir))) {
			if (f.toString() != this.plugin.config.bckDir) {
				diffDays = getDifference(f.lastModified(), now, TimeUnit.DAYS);
				if (this.plugin.config.debug) this.plugin.log(f + " : " + diffDays + " days");
				if (diffDays > this.plugin.config.daystokeep) {
					if (alexoft.Minebackup.DirUtils.delete(f)) {
						this.plugin.log(" + deleted " + f + " due to age limitation (" + diffDays + " day(s))");
					}
					else {
						this.plugin.log("Cannot delete " + f + " !");
					}
					bckDeleted += 1;
				}
			}
		}
		for (int i = 0; i < 3; i++)
			//remove empty directories
			for (File f : recursiveListDir(new File(this.plugin.config.bckDir))) {
				if (f.list().length == 0) {
					f.delete();
				}
			}
		this.plugin.log(Level.INFO, " + " + bckDeleted + " backup(s) deleted");
	}
	
	private List<File> recursiveListFiles(File path) {
		List<File> o = new ArrayList<File>();
		if (path.isDirectory()) {
			File[] list = path.listFiles();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					o.addAll(recursiveListFiles(list[i]));
				}
			}
			else {
				this.plugin.log(Level.WARNING, "Cannot acces to " + path);
			}
		}
		else {
			o.add(path);
		}
		return o;
	}
	
	private List<File> recursiveListDir(File path) {
		List<File> o = new ArrayList<File>();
		if (path.isDirectory()) {
			o.add(path);
			File[] list = path.listFiles();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					o.addAll(recursiveListDir(list[i]));
				}
			}
			else {
				this.plugin.log(Level.WARNING, "Cannot acces to " + path);
			}
		}
		return o;
	}
	
}
