package ta.car4rent.webservices;

import org.json.JSONObject;

public class ServicePostNewReport extends WebserviceBasePOST {
	
	/**
	 * Post new report to server
	 * 
	 * You must be use addOnPostJsonListener(OnPostJsonListener) for this Object
	 * to handle when POST completed or fail
	 * 
	 * @param reportInfo : JSONObject
	 */
	public void postNewReport(JSONObject reportInfo) {
		String Url = "http://4carsvn.cloudapp.net:4411/Common/SaveReport";
		postJSONObject(Url, reportInfo);
	}
}
