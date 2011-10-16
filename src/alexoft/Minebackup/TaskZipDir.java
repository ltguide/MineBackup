package alexoft.Minebackup;

import java.io.File;

/**
 * 
 * @author Alexandre
 */
public class TaskZipDir extends Thread {
	public final MineBackup plugin;
	public String srcDir;
	public String destDir;
	public int method;
	public int level;
	
	public TaskZipDir(MineBackup plugin, String source, String dest) {
		this.plugin = plugin;
		this.destDir = dest;
		this.srcDir = source;
	}
	
	@Override
	public void run() {
		try {
			new File(destDir).createNewFile();
			alexoft.Minebackup.ZipUtils.zipDir(srcDir, destDir, plugin.config.compressionMode, plugin.config.compressionLevel);
			alexoft.Minebackup.DirUtils.deleteDirectory(new File(srcDir));
		}
		catch (Exception ex) {
			new File(destDir).delete();
			plugin.logException(ex, destDir);
		}
	}
}
