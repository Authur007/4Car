package ta.car4rent.webservices;

import org.json.JSONObject;

public class ServiceSearch extends WebserviceBasePOST {
	
	/**
	 * Search about rent car available info
	 * 
	 * You must be use addOnPostJsonListener(OnPostJsonListener) for this Object
	 * to handle when POST completed or fail
	 * 
	 * @param searchInfo : JSONObject
	 */
	public void advanceSearch(JSONObject searchInfo) {
		String url = "http://4carsvn.cloudapp.net:4411/Common/AdvanceSearch";
		postJSONObject(url, searchInfo);		
	}
}
