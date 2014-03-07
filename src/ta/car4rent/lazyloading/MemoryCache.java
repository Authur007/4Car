package ta.car4rent.lazyloading;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

	private static final String TAG = "MemoryCache";

	// Last argument true for LRU ordering
	private Map<String, Bitmap> cache = Collections
			.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
	// Current allocated size
	private long size = 0;
	// Max memory in bytes
	private long limit = 1000000;

	public MemoryCache() {
		// Use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory() / 4);
	}

	/**
	 * Set limit of caching images
	 * 
	 * @param new_limit
	 */
	public void setLimit(long new_limit) {
		limit = new_limit;
		Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
	}

	/**
	 * Get image
	 * 
	 * @param id
	 * @return
	 */
	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id))
				// Return null if this image is not available
				return null;
			// NullPointerException sometimes happen here
			// http://code.google.com/p/osmdroid/issues/detail?id=78
			// Return bitmap from memory
			return cache.get(id);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Put image
	 * 
	 * @param id
	 * @param bitmap
	 */
	public void put(String id, Bitmap bitmap) {
		try {
			// Decrease memory if image is existed
			if (cache.containsKey(id)) {
				size -= getSizeInBytes(cache.get(id));
			}
			cache.put(id, bitmap);
			// Increase memory by the side of image
			size += getSizeInBytes(bitmap);
			checkSize();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	private void checkSize() {
		Log.i(TAG, "cache size=" + size + " length=" + cache.size());
		if (size > limit) {
			// least recently accessed item will be the first one iterated
			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();

			while (iter.hasNext()) {
				Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
				if (size <= limit)
					break;
			}
			Log.i(TAG, "Clean cache. New size " + cache.size());
		}
	}

	/**
	 * 
	 */
	public void clear() {
		try {
			// NullPointerException sometimes happen here
			// http://code.google.com/p/osmdroid/issues/detail?id=78
			cache.clear();
			size = 0;
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Return size of image
	 * @param bitmap
	 * @return int
	 */
	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return 0;
		}
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}