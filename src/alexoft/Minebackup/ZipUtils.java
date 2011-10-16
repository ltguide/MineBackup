package alexoft.Minebackup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for compressing Zip
 * stripped/refactored/modified by ltguide
 * 
 * @author iubito (Sylvain Machefert)
 * @author ltguide (Matthew Churilla)
 */
public class ZipUtils {
	private static final int BUFFER_SIZE = 8 * 1024;
	
	public static void zipDir(File srcDir, File destFile, int method, int level) throws FileNotFoundException, IOException {
		destFile.getParentFile().mkdirs();
		FileOutputStream outStream = new FileOutputStream(destFile);
		
		try {
			CheckedOutputStream checkedOutStream = new CheckedOutputStream(outStream, new Adler32());
			try {
				BufferedOutputStream bufOutStream = new BufferedOutputStream(checkedOutStream, BUFFER_SIZE);
				try {
					ZipOutputStream zipOutStream = new ZipOutputStream(bufOutStream);
					try {
						try {
							zipOutStream.setMethod(method);
							zipOutStream.setLevel(level);
						}
						catch (Exception ignor) {}
						zipDir(srcDir, "", zipOutStream);
					}
					finally {
						zipOutStream.close();
					}
				}
				finally {
					bufOutStream.close();
				}
			}
			finally {
				checkedOutStream.close();
			}
		}
		finally {
			outStream.close();
		}
	}
	
	private static void zipDir(File srcDir, String currentDir, ZipOutputStream zipOutStream) throws FileNotFoundException, IOException {
		if (!"".equals(currentDir)) {
			currentDir += "/";
			zipOutStream.putNextEntry(new ZipEntry(currentDir));
			zipOutStream.closeEntry();
		}
		
		File zipDir = new File(srcDir, currentDir);
		for (String child : zipDir.list()) {
			File srcFile = new File(zipDir, child);
			
			if (srcFile.isDirectory()) zipDir(srcDir, currentDir + child, zipOutStream);
			else {
				ZipEntry zipEntry = new ZipEntry(currentDir + child);
				zipEntry.setTime(srcFile.lastModified());
				zipFile(srcFile, zipEntry, zipOutStream);
			}
		}
	}
	
	private static void zipFile(File srcFile, ZipEntry zipEntry, ZipOutputStream zipOutStream) throws FileNotFoundException, IOException {
		InputStream inStream = new FileInputStream(srcFile);
		try {
			BufferedInputStream bufInStream = new BufferedInputStream(inStream, BUFFER_SIZE);
			try {
				zipOutStream.putNextEntry(zipEntry);
				
				byte[] buf = new byte[BUFFER_SIZE];
				int len;
				
				while ((len = bufInStream.read(buf)) > -1)
					if (len > 0) zipOutStream.write(buf, 0, len);
				
				zipOutStream.closeEntry();
			}
			finally {
				bufInStream.close();
			}
		}
		finally {
			inStream.close();
		}
	}
	
}
