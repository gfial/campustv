package dbc.data.campus;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileOps {

	public static String getFile(String filename) {
		if(filename == null) return null;
		try {
			RandomAccessFile raf = new RandomAccessFile(filename, "r");
			byte[] file = new byte[(int) raf.length()];
			raf.readFully(file);
			raf.close();
			return new String(file);
		} catch (IOException e) {
		}
		return null;
	}

}
