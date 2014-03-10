package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

public class ServiceManagerGetCarRequested extends WebserviceBaseGET{
	
	public void getCarRequesteds(int mPageIndex, int mPageSize) {
		mToken = ConfigureData.token;
		String url = ApiUrl.SERVER_URL + "GetCarRequests/" + mPageIndex + "/" + mPageSize;		
		getJSONObject(url);
	}
	
	public void getCarRequestedDetail(int newsId){
		mToken = ConfigureData.token;
		String url = ApiUrl.SERVER_URL + "GetDetailCarRequest/" + newsId;		
		getJSONObject(url);
		
	}
	
	public void closeCarRequested(int newsId){
		mToken = ConfigureData.token;
		String url = ApiUrl.SERVER_URL + "CloseCarRequest/" + newsId;		
		getJSONObject(url);
		
	}

}
