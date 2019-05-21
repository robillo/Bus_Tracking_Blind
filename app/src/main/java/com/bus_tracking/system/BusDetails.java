package com.bus_tracking.system;

public class BusDetails {
    String bus_no,source,destination,fare,driver_id,route;
long latitude,longitude;
    public BusDetails() {
    }

    public BusDetails(String bus_no, String source, String destination, String fare, String driver_id,String route,long latitude,long longitude) {
        this.bus_no = bus_no;
        this.source = source;
        this.destination = destination;
        this.fare = fare;
        this.driver_id = driver_id;
        this.route = route;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getBus_no() {
        return bus_no;
    }

    public void setBus_no(String bus_no) {
        this.bus_no = bus_no;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }
}
