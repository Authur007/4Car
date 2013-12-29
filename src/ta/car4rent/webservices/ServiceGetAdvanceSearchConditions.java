package ta.car4rent.webservices;

public class ServiceGetAdvanceSearchConditions extends WebserviceBaseGET {
	
	/**
	 * Get default advance search conditions from server
	 * 
	 * You must be use addOnGetJsonListener(OnGetJsonListener)
	 * to handle GET task completed or fail
	 * 
	 * 
	 */
	public void getAdvanceSearchConditions() {
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetAdvanceSearchConditions";		
		getJSONObject(url);
	}
}
