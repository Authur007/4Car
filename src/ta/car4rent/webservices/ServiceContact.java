package ta.car4rent.webservices;

import org.json.JSONObject;

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
		String Url = "http://4carsvn.cloudapp.net:4411/Common/Contact";
		postJSONObject(Url, contactInfo);
	}
}
