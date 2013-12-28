package ta.car4rent.webservices;

import org.json.JSONObject;

public class ServiceLogin extends WebserviceBasePOST {
	
	/**
	 * Login to 4cars system
	 * 
	 * You must be use addOnPostJsonListener(OnPostJsonListener) for this Object
	 * to handle when POST completed or fail
	 * 
	 * @param loginInfo
	 */
	public void logon(JSONObject loginInfo) {
		String Url = "http://4carsvn.cloudapp.net:4411/Common/LogOn";
		postJSONObject(Url, loginInfo);
		
	}
}
