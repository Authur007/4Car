package ta.car4rent.webservices;

import org.json.JSONObject;

import ta.car4rent.configures.ConfigureData;

public class ServicePostCarRequest extends WebserviceBasePOST {
	
	/**
	 * Post new report to server
	 * 
	 * You must be use addOnPostJsonListener(OnPostJsonListener) for this Object
	 * to handle when POST completed or fail
	 * 
	 * @param reportInfo : JSONObject
	 */
	public void postCarRequest(JSONObject carRequestInfo) {
		mToken = ConfigureData.token;
		String Url = "http://4carsvn.cloudapp.net:4411/Common/PostCarRequest";
		postJSONObject(Url, carRequestInfo);
	}
}
