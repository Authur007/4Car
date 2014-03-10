package ta.car4rent.webservices;

import org.json.JSONObject;

import ta.car4rent.configures.ConfigureData;

public class ServiceContact extends WebserviceBasePOST {
	
	/**
	 * Send contact info to server
	 * 
	 * You must be use addOnPostJsonListener(OnPostJsonListener) for this Object
	 * to handle when POST completed or fail
	 * 
	 * @param contactInfo : JSONObject
	 */
	public void sendContact(JSONObject contactInfo) {
		mToken = ConfigureData.token;
		String Url = ApiUrl.SERVER_URL + "Contact";
		postJSONObject(Url, contactInfo);
	}
}
