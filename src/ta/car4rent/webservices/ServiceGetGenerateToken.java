package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

public class ServiceGetGenerateToken extends WebserviceBaseGET{
	
	public void getTokenByFBId(String facebookID) {
		mToken = ConfigureData.token;
		String url = "http://4carsvn.cloudapp.net:4411/Common/GenerateToken/";		
		getJSONObject(url + facebookID);
	}
	
	

}
