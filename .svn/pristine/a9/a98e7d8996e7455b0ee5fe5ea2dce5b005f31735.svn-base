package ism.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ism.android.R;


public class StaticVariables {
    private static String URL = "";
    private static String DEVICE_ID = "";
    private static int ORG_ID = 0;
    private static String NEWS_BANNER = "";


    public static final int DATE_DIALOG_ID = 0;
    public static final String overtime = "Overtime - ";
    public static final String split = " - Split";
    public static final String meeting = "Meeting : ";
    public static final String standard = "Standard";
    public static final String trainee = "Trainee";
    public static final String requestOffApproved = "Request Off Approved";
    public static final String requestOffRejected = "Request Off Rejected";
    public static final String approvalPending = "Approval Pending";

    public static SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
    public static SimpleDateFormat generalDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    public static SimpleDateFormat dayWithDateFormat = new SimpleDateFormat("EEEE, MM/dd/yyyy", Locale.US);
    public static SimpleDateFormat generalTimeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    /* All message type constants */
    public static String BREAK_NOTIFICATION_MESSAGE = "B";
    public static String GIVE_AWAY_MESSAGE = "GA";
    public static String GIVE_TO_MESSAGE = "GT";
    public static String TRADE_REQUEST_MESSAGE = "TR";
    public static String REQUEST_OFF_MESSAGE = "R";
    public static String MEETING_MESSAGE = "M";
    public static String IDEA_MESSAGE = "I";
    public static String GENERAL_MESSAGE = "G";
    public static String MANDATORY_MESSAGE = "MM";
    public static String TRADE_SHIFT_MESSAGE = "T";
    public static String SHIFT_MESSAGE = "S";


    public static String getDayName(Date dateObj) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateObj);
//			calendar.set(Calendar.DAY_OF_YEAR, dateObj.getYear());
//			calendar.set(Calendar.MONTH, dateObj.getMonth());
//			calendar.set(Calendar.DAY_OF_WEEK, dateObj.getDay());
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (day) {
                case 1:
                    return "Sunday";
                case 2:
                    return "Monday";
                case 3:
                    return "Tuesday";
                case 4:
                    return "Wednesday";
                case 5:
                    return "Thursday";
                case 6:
                    return "Friday";
                case 7:
                    return "Saturday";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getNEWS_BANNER(Context context) {
        if (StaticVariables.NEWS_BANNER.length() <= 0) {
            SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
            String newsBanner = preferences.getString(context.getString(R.string.NEWS_BANNER), "");
            StaticVariables.setNEWS_BANNER(newsBanner);
        }
        return StaticVariables.NEWS_BANNER;
    }

    public static void setNEWS_BANNER(String nEWSBANNER) {
        StaticVariables.NEWS_BANNER = nEWSBANNER;
    }

    public static void setURL(String url) {
        StaticVariables.URL = url;
    }

    public static String getURL(Context context) {
        if (StaticVariables.URL.length() <= 0) {
            SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
            String url = preferences.getString(context.getString(R.string.WEB_SERVICE_LOCATION), "");
            StaticVariables.setURL(url);
        }
        return StaticVariables.URL;
    }

    public static void setDeviceID(String deviceID) {
        StaticVariables.DEVICE_ID = deviceID;
    }

    public static String getDeviceID(Context context) {
        if (StaticVariables.DEVICE_ID.length() <= 0) {
            SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
            String regGUID = preferences.getString(context.getString(R.string.REGISTERED_GUID), "");
            StaticVariables.setDeviceID(regGUID);
        }
        return StaticVariables.DEVICE_ID;
    }

    public static void setOrgID(int orgID) {
        StaticVariables.ORG_ID = orgID;
    }

    public static int getOrgID(Context context) {
        if (StaticVariables.ORG_ID == 0) {
            SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREFS_NAME), Context.MODE_PRIVATE);
            int orgID = preferences.getInt(context.getString(R.string.ORG_ID), 0);
            StaticVariables.setOrgID(orgID);
        }
        return StaticVariables.ORG_ID;
    }


}
