package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

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
		mToken = ConfigureData.token;
		String url = ApiUrl.SERVER_URL + "GetUserInfo/";		
		getJSONObject(url + userId);
	}
}
