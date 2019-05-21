package com.bus_tracking.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by inspirin on 11/16/2017.
 */

public class Constant {

    public static final String STORAGE_PATH_UPLOADS = "upload/";
    //28.70322,77.12866666666667altitude=378.1
  public   static  String DB="bus_tracking";
  public   static  String bus_details="bus_details";
  public   static  String drivers="drivers";
  public   static  String users="users";
  public   static  double latitude=28.6778;
  public   static  double longitude=77.2613;
  public static double altitude=0;
  public static String getDate() {
    DateFormat dateFormat = new SimpleDateFormat(
            "yyyy/MM/dd");

    Calendar cal = Calendar.getInstance();

    return dateFormat.format(cal.getTime());// "11/03/14 12:33:43";
  }
  public static String getTime() {
    DateFormat dateFormat = new SimpleDateFormat(
            "hh:mm:ss");

    Calendar cal = Calendar.getInstance();

    return dateFormat.format(cal.getTime());// "11/03/14 12:33:43";
  }
}
