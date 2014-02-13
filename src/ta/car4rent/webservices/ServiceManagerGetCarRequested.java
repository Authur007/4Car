package ta.car4rent.webservices;

import ta.car4rent.configures.ConfigureData;

public class ServiceManagerGetCarRequested extends WebserviceBaseGET{
	
	public void getCarRequesteds(int mPageIndex, int mPageSize) {
		mToken = ConfigureData.token;
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetCarRequests/" + mPageIndex + "/" + mPageSize;		
		getJSONObject(url);
	}
	
	public void getCarRequestedDetail(int newsId){
		mToken = ConfigureData.token;
		String url = "http://4carsvn.cloudapp.net:4411/Common/GetDetailCarRequest/" + newsId;		
		getJSONObject(url);
		
	}
	
	public void closeCarRequested(int newsId){
		mToken = ConfigureData.token;
		String url = "http://4carsvn.cloudapp.net:4411/Common/CloseCarRequest/" + newsId;		
		getJSONObject(url);
		
	}

}
