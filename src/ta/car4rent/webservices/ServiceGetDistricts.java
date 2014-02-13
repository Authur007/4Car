package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

public class ServiceGetDistricts extends WebserviceBaseGET{

	/**
	 * Get districs of cityId
	 * 
	 * You must be use addOnGetJsonListener(OnGetJsonListener) for this Object
	 * to handle when GET completed or fail
	 *
	 * @param cityId
	 */
	public void getDistricts(int cityId) {
		mToken = ConfigureData.token;
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetDistricts/";		
		getJSONObject(url + cityId);
	}
	
}
