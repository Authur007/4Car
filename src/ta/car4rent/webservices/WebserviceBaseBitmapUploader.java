package ta.car4rent.webservices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;


public class WebserviceBaseBitmapUploader implements SubjectUploadBitmap {
	public static final int NOTIFY_UPLOAD_BITMAP_COMPLETED = 0;
	public static final int NOTIFY_UPLOAD_BITMAP_FAIL = 1;
	

	// Observers which need to notify change
	protected Vector<OnUploadBitmapListener> mOnUploadBitmapListeners = new Vector<OnUploadBitmapListener>();
	
	protected Bitmap mUploadBitmap;
	protected String response = "OK";
	
	public void uploadBitmap(Bitmap uploadBitmap, String uploadURL) {
		mUploadBitmap = uploadBitmap;
		
		UploadBitmapTask task = new UploadBitmapTask();
		task.execute(new String[] { uploadURL });
	}
	
	/**
	 * This is Asyntask Upload web-page source via URL run in Background
	 * 
	 * @author anhle
	 * 
	 */
	protected class UploadBitmapTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urlArray) {
			for (String url : urlArray) {
				try {
					response = uploadFile(url);
					notifyChange(NOTIFY_UPLOAD_BITMAP_COMPLETED);
				} catch (Exception e) {
					response = e.toString();
					notifyChange(NOTIFY_UPLOAD_BITMAP_FAIL);
				}
			}
			
			return response;
		}

		@Override
		protected void onPostExecute(String r) {
		}
	}
	
	/**
	 * URL of image after upload
	 * 
	 * @param uploadUrl: upload link
	 * @return
	 */
	/**
	 * URL of image after upload
	 * 
	 * @param uploadLink : Link up load image to server
	 * @return
	 */
	private String uploadFile(String uploadLink) {
		String strResponse = "";

        try {
        	// Initial for Http
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uploadLink);
            
        	// Create Input stream from Bitmap Image
        	ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        	mUploadBitmap.compress(CompressFormat.JPEG, 80, bos); 
        	byte[] bitmapdata = bos.toByteArray();
        	ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
        	InputStream inputStream = (InputStream) bs;

            InputStreamEntity reqEntity = new InputStreamEntity(inputStream, bos.size());
            reqEntity.setContentType("binary/octet-stream");
            reqEntity.setChunked(true); // Send in multiple parts if needed
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            //Do something with response...
            HttpEntity entity = response.getEntity();

			strResponse = EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return strResponse;
	}

	@Override
	public void addOnUploadBitmapListener(OnUploadBitmapListener o) {
		mOnUploadBitmapListeners.add(o);

	}

	@Override
	public void removeOnUploadBitmapListener(OnUploadBitmapListener o) {
		mOnUploadBitmapListeners.remove(o);

	}

	@Override
	public void notifyChange(int status) {
		for (OnUploadBitmapListener o : mOnUploadBitmapListeners) {
			switch(status) {
			case NOTIFY_UPLOAD_BITMAP_COMPLETED:
				o.onUploadImageBitmapCompleted(response);
				break;
			case NOTIFY_UPLOAD_BITMAP_FAIL:
				o.onUploadImageBitmapFail(response);
				break;
			}
		}
	}
	
	
}