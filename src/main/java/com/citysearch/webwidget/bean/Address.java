package com.citysearch.webwidget.bean;

public class Address {
	
	private String street;
	private String city;
	private String state;
	private String postal_code;
	
	public void setStreet(String street) {
		this.street = street;
	}
	public String getStreet() {
		return street;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return city;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getState() {
		return state;
	}
	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}
	public String getPostal_code() {
		return postal_code;
	}

}
