package com.citysearch.webwidget.bean;

/**
 * Bean class for holding Address related fields
 * 
 * @author Aspert Benjamin
 * 
 */
public class Address {

    private String street;
    private String city;
    private String state;
    private String postalCode;

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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
