package ta.car4rent.webservices;

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
		String uploadURL = ApiUrl.SERVER_URL + "UploadReportPhoto/";
		uploadURL += reportId;
		uploadBitmap(bitmap, uploadURL);
	}
}
