package ta.car4rent.webservices;

import org.json.JSONObject;

import ta.car4rent.configures.ConfigureData;

public class ServiceChangePassword extends WebserviceBasePOST {
	
	/**
	 * Login to 4cars system
	 * 
	 * You must be use addOnPostJsonListener(OnPostJsonListener) for this Object
	 * to handle when POST completed or fail
	 * 
	 * @param loginInfo
	 */
	public void change(JSONObject changePasswordInfo) {
		mToken = ConfigureData.token;
		String Url = ApiUrl.SERVER_URL + "ChangePassword";
		postJSONObject(Url, changePasswordInfo);
		
	}
}
