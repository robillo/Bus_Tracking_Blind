package com.bus_tracking.system;

/**
 * Created by inspirin on 10/18/2017.
 */

public class Driver {
    String name,userid,url,phone,email, utype,vehicle,token_id;
public Driver(){

}
//name, userid, url_image, phone, email, vehicle, strUtype,  null
    public Driver(String name, String userid, String url, String phone, String email, String vehicle, String utype, String token_id) {
        this.name = name;
        this.userid = userid;
        this.url = url;
        this.phone = phone;
        this.email = email;
        this.utype = utype;
        this.vehicle = vehicle;
        this.token_id = token_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }
}
