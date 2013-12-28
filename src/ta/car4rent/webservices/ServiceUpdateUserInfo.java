package ta.car4rent.webservices;

import org.json.JSONObject;

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
		String Url = "http://4carsvn.cloudapp.net:4411/Common/UpdateInfo";
		postJSONObject(Url, userInfo);
	}
}
