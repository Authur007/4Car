package ta.car4rent.lazyloading;

import java.io.File;

import android.content.Context;

/**
 * This class used for saving (cached) loaded image onto memory. Reduce time for
 * requesting image from Internet
 * @author Anh
 */

public class FileCache {

	// Declare caching directory
	private File cacheDir;

	public FileCache(Context context) {
		
		// Find destination to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			// Use SD card
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"ShareBoard");
		} else {
			// Use cache memory
			cacheDir = context.getDir("ShareBoard", Context.MODE_PRIVATE);
		}

		// Create directory if memory is available
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	/**
	 * Return File object, which name is its hashcode
	 * @param String
	 * @return File
	 */
	public File getFile(String url) {
		File f = new File(cacheDir, String.valueOf(url.hashCode()));
		return f;
	}
	
	/**
	 * Delete all files in cacheDir directory
	 */
	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			f.delete();
		}
	}

}