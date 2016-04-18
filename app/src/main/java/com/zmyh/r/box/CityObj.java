package com.zmyh.r.box;

import java.util.List;

public class CityObj {

	public final static String ID = "_id";
	public final static String AREA_CODE = "area_code";
	public final static String AREA_NAME = "area_name";
	public final static String AREA_LEVEL = "area_level";
	public final static String V = "__v";
	public final static String CREATE_AT = "createAt";
	public final static String CITIES = "cities";
	public final static String AREA_LNG = "area_lng";
	public final static String AREA_LAT = "area_lat";

	private String _id;
	private String area_code;
	private String area_name;
	private int area_level;
	private int __v;
	private long createAt;
	private List<CityObj> cities;
	private double area_lng;
	private double area_lat;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public int getArea_level() {
		return area_level;
	}

	public void setArea_level(int area_level) {
		this.area_level = area_level;
	}

	public int get__v() {
		return __v;
	}

	public void set__v(int __v) {
		this.__v = __v;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	public List<CityObj> getCities() {
		return cities;
	}

	public void setCities(List<CityObj> cities) {
		this.cities = cities;
	}

	public double getArea_lng() {
		return area_lng;
	}

	public void setArea_lng(double area_lng) {
		this.area_lng = area_lng;
	}

	public double getArea_lat() {
		return area_lat;
	}

	public void setArea_lat(double area_lat) {
		this.area_lat = area_lat;
	}

}
