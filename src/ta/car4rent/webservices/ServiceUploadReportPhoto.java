package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;
import android.graphics.Bitmap;

public class ServiceUploadReportPhoto extends WebserviceBaseBitmapUploader {

	/**
	 * Upload the bitmap to server
	 * 
	 * You must be use addOnUploadBitmapListener(OnUploadBitmapListener) for
	 * this Object to handle when UPLOAD task completed or fail
	 * 
	 * @param bitmap
	 */
	public void uploadReportPhoto(Bitmap bitmap, int reportId) {
		String uploadURL = "http://4carsvn.cloudapp.net:4411/Common/UploadReportPhoto/";
		uploadURL += reportId;
		uploadBitmap(bitmap, uploadURL);
	}
}
