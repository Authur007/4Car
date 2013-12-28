package ta.car4rent.lazyloading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ta.car4rent.R;
import ta.car4rent.utils.Utils;

import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Handler handler = new Handler();// handler to display images in UI thread

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(1);
	}

	final int stub_id = R.drawable.ic_no_avatar;

	public void displayImage(String url, ImageView imageView) {

		// Put imageView to Map of imageViews
		this.imageViews.put(imageView, url);

		// Get bitmap of imageView from Cache
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			// If this imageView is available on Cache, return bitmap
			imageView.setImageBitmap(bitmap);
		} else {
			// If not, put on queue and display temporary image
			queueImage(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	private void queueImage(String url, ImageView imageView) {
		// Declare link and object of image
		ImageToLoad imageToLoad = new ImageToLoad(url, imageView);
		// Start queue
		executorService.submit(new ImagesLoader(imageToLoad));
	}

	private Bitmap getBitmap(String url) {
		// Get File object from input hashcode
		File f = fileCache.getFile(url);

		// Get bitmap from SD cache
		Bitmap b = decodeFile(f);
		if (b != null) {
			return b;
		}

		// Get bitmap from web
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream iStream = conn.getInputStream();
			OutputStream oStream = new FileOutputStream(f);
			Utils.CopyStream(iStream, oStream);
			oStream.close();
			conn.disconnect();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class ImageToLoad {
		public String url;
		public ImageView imageView;

		public ImageToLoad(String url, ImageView imageView) {
			this.url = url;
			this.imageView = imageView;
		}
	}

	class ImagesLoader implements Runnable {
		ImageToLoad imageToLoad;

		ImagesLoader(ImageToLoad imageToLoad) {
			this.imageToLoad = imageToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(imageToLoad)) {
					// Return if image is not in Map of images
					return;
				}
				// Get bitmap of image
				Bitmap bitmap = getBitmap(imageToLoad.url);
				// Put image on Cache
				memoryCache.put(imageToLoad.url, bitmap);
				// Return if image is not in Map of images
				if (imageViewReused(imageToLoad))
					return;
				BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bitmap, imageToLoad);
				// Display on UI
				handler.post(bitmapDisplayer);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(ImageToLoad imageToLoad) {
		// Get URL of image
		String tag = imageViews.get(imageToLoad.imageView);
		if (tag == null || !tag.equals(imageToLoad.url)) {
			// Return if image is not in Map of images
			return true;
		}
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageToLoad imageToLoad;

		public BitmapDisplayer(Bitmap bitmap, ImageToLoad imageToLoad) {
			this.bitmap = bitmap;
			this.imageToLoad = imageToLoad;
		}

		public void run() {
			if (imageViewReused(imageToLoad)) {
				// Return if image was be in Map of images
				return;
			}
			if (bitmap != null) {
				// If bitmap is available, display image
				imageToLoad.imageView.setImageBitmap(bitmap);
			} else {
				// If not, display temporary image
				imageToLoad.imageView.setImageResource(stub_id);
			}
		}
	}

	public void clearCache() {
		memoryCache.clear(); //
		fileCache.clear(); // Delete file on SD card
	}
}
