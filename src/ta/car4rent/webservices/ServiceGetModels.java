package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

public class ServiceGetModels extends WebserviceBaseGET{

	public void getModels(int id) {
		mToken = ConfigureData.token;
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetModels/";		
		getJSONObject(url + id);
	}
}
