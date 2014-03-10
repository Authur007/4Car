package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

public class ServiceGetGenerateToken extends WebserviceBaseGET{
	
	public void getTokenByFBId(String facebookID) {
		mToken = ConfigureData.token;
		String url = ApiUrl.SERVER_URL + "GenerateToken/";		
		getJSONObject(url + facebookID);
	}
	
	

}
