package alexoft.Minebackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DirUtils {
	
	public static void deleteDirectory(File tarDir) {
		if (tarDir.isDirectory()) {
			for (File child : tarDir.listFiles())
				deleteDirectory(child);
		}
		
		tarDir.delete();
	}
	
	public static void copyDirectory(File srcDir, File destDir) throws FileNotFoundException, IOException {
		destDir.mkdirs();
		
		for (String child : srcDir.list()) {
			File srcFile = new File(srcDir, child);
			if (srcFile.isDirectory()) copyDirectory(srcFile, new File(destDir, child));
			else copyFile(srcFile, new File(destDir, child));
		}
	}
	
	public static void copyFile(File srcFile, File destFile) throws FileNotFoundException, IOException {
		InputStream inStream = new FileInputStream(srcFile);
		OutputStream outStream = new FileOutputStream(destFile);
		try {
			byte[] buf = new byte[1024];
			int len;
			
			while ((len = inStream.read(buf)) > -1)
				if (len > 0) outStream.write(buf, 0, len);
		}
		finally {
			inStream.close();
			outStream.close();
		}
	}
}
