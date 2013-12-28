package ta.car4rent.webservices;

public class ServiceGetUserInfo extends WebserviceBaseGET {
	
	/**
	 * Get user info from server
	 * 
	 * You must be use addOnGetJsonListener(OnGetJsonListener)
	 * to handle GET task completed or fail
	 * 
	 * @param userId : id's user you need to get info
	 */
	public void get(String userId) {
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetUserInfo/";		
		getJSONObject(url + userId);
	}
}
