package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

public class ServiceGetUserByUserName extends WebserviceBaseGET{
	
	public void getUserByFBId(String facebookID) {
		mToken = ConfigureData.token;
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetUserInfoByUserName/";		
		getJSONObject(url + facebookID);
	}

}
