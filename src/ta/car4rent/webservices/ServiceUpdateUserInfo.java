package ta.car4rent.webservices;

import org.json.JSONObject;

import ta.car4rent.configures.ConfigureData;

public class ServiceUpdateUserInfo extends WebserviceBasePOST {
	
	/**
	 * Update user info
	 *
	 * You must be use addOnPostJsonListener(OnPostJsonListener) for this Object
	 * to handle when POST completed or fail
	 * 
	 * @param userInfo : JSONObject
	 */
	public void updateUserInfo(JSONObject userInfo) {
		mToken = ConfigureData.token;
		String Url =  ApiUrl.SERVER_URL + "UpdateInfo";
		postJSONObject(Url, userInfo);
	}
}
