package alexoft.Minebackup;

import java.io.File;

/**
 * 
 * @author Alexandre
 */
public class TaskCopyDir extends Thread {
	public final MineBackup plugin;
	public String srcDir;
	public String destDir;
	public int method;
	public int level;
	
	public TaskCopyDir(MineBackup plugin, String source, String dest) {
		this.plugin = plugin;
		this.destDir = dest;
		this.srcDir = source;
	}
	
	@Override
	public void run() {
		try {
			new File(this.destDir).mkdirs();
			alexoft.Minebackup.DirUtils.copyDirectory(this.srcDir, this.destDir);
			alexoft.Minebackup.DirUtils.deleteDirectory(new File(this.srcDir));
		}
		catch (Exception ex) {
			plugin.logException(ex);
			new File(this.destDir).delete();
		}
	}
}
