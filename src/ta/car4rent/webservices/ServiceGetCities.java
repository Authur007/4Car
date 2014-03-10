package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

public class ServiceGetCities extends WebserviceBaseGET{

	/**
	 * Get all City
	 * 
	 * You must be use addOnGetJsonListener(OnGetJsonListener) for this Object
	 * to handle when GET completed or fail
	 *
	 * @param cityId
	 */
	public void getCities() {
		mToken = ConfigureData.token;
		String url = ApiUrl.SERVER_URL + "GetCities";		
		getJSONObject(url);
	}
	
}
