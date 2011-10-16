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
public class CleanBackups {
	private final MineBackup plugin;
	
	public CleanBackups(MineBackup plugin) {
		this.plugin = plugin;
	}
	
	public static long getDifference(long a, long b, TimeUnit units) {
		return units.convert(b - a, TimeUnit.MILLISECONDS);
	}
	
	public void clean() {
		long now = System.currentTimeMillis();
		long diffDays;
		int bckDeleted = 0;
		for (File f : recursiveListFiles(new File(plugin.config.bckDir))) {
			if (f.toString() != plugin.config.bckDir) {
				diffDays = getDifference(f.lastModified(), now, TimeUnit.DAYS);
				if (plugin.config.debug) plugin.sendLog(f + " : " + diffDays + " days");
				if (diffDays > plugin.config.daystokeep) {
					if (f.delete()) plugin.sendLog(" + deleted " + f + " due to age limitation (" + diffDays + " day(s))");
					else plugin.sendLog("Cannot delete " + f + " !");
					
					bckDeleted += 1;
				}
			}
		}
		for (int i = 0; i < 3; i++)
			//remove empty directories
			for (File f : recursiveListDir(new File(plugin.config.bckDir)))
				if (f.list().length == 0) f.delete();
		
		plugin.sendLog(" + " + bckDeleted + " backup(s) deleted");
	}
	
	private List<File> recursiveListFiles(File path) {
		List<File> o = new ArrayList<File>();
		if (path.isDirectory()) {
			File[] list = path.listFiles();
			if (list != null) {
				for (int i = 0; i < list.length; i++)
					o.addAll(recursiveListFiles(list[i]));
			}
			else plugin.sendLog(Level.WARNING, "Cannot acces to " + path);
		}
		else o.add(path);
		
		return o;
	}
	
	private List<File> recursiveListDir(File path) {
		List<File> o = new ArrayList<File>();
		if (path.isDirectory()) {
			o.add(path);
			File[] list = path.listFiles();
			if (list != null) {
				for (int i = 0; i < list.length; i++)
					o.addAll(recursiveListDir(list[i]));
			}
			else plugin.sendLog(Level.WARNING, "Cannot acces to " + path);
		}
		return o;
	}
	
}
