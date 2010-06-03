package com.citysearch.webwidget.bean;

/**
 * Response bean class for Profile API
 * 
 * @author Aspert
 * 
 */
public class Profile {
    private Address address;
    private String phone;
    private String profileUrl;
    private String sendToFriendUrl;
    private String imageUrl;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getSendToFriendUrl() {
        return sendToFriendUrl;
    }

    public void setSendToFriendUrl(String sendToFriendUrl) {
        this.sendToFriendUrl = sendToFriendUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
