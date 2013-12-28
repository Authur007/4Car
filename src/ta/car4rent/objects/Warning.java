package ta.car4rent.objects;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

public class Warning {
	private LatLng location;
	private String content;
	private String title;
	private int type;

	public Warning() {
		this.setLocation(new LatLng(10.823358f, 106.629841f));
		this.setContent("My Content");
		this.setTitle("Hello World");
		this.setType(0);
	}

	public Warning(LatLng loca, String content, String title, int type) {
		this.setLocation(loca);
		this.setContent(content);
		this.setTitle(title);
		this.setType(type);
	}

	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}
	
	
	

}
