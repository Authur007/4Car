package ta.car4rent.webservices;

import android.graphics.Bitmap;

public class ServiceUploadUserAvatar extends WebserviceBaseBitmapUploader {

	/**
	 * Upload the bitmap to server
	 * 
	 * You must be use addOnUploadBitmapListener(OnUploadBitmapListener) for
	 * this Object to handle when UPLOAD task completed or fail
	 * 
	 * @param bitmap
	 */
	public void uploadUserAvatar(Bitmap bitmap, int userId) {
		String uploadURL = "http://4carsvn.cloudapp.net:4411/Common/UploadUserAvatar/";
		uploadURL += userId;
		uploadBitmap(bitmap, uploadURL);
	}
}
