package ism.android;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.CalendarContract.Calendars;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import ism.android.message.MandatoryMessageDetailActivity;
import ism.android.utils.MessageInfo;
import ism.android.utils.StaticVariables;
import ism.android.webservices.Synchronization;

public class Utility {
    private static int NOTIFICATION_ID = 1;

    public static ArrayList<HashMap<String, String>> arrList_Picture = new ArrayList<HashMap<String, String>>();
    public static HashMap<String, String> getDownloadedFileName = new HashMap<String, String>();
    public static String[] strArr_SurveyPictures;
    public static String[] file_Name;

    /***
     * SET FOR THE LIST VIEW HEIGHT
     **/
    public static void setListViewHeightBasedOnChildren(Context context, ListView listView) {
        try {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + 2 + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
    }

    public static void setListViewHeightBasedOnChildrenForLog(Context context, ListView listView) {
        try {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + 75 + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
    }

    public static boolean checkCalendarPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkReadExternalStorage(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkWriteExternalStorage(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkReadPhoneState(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    public static String requestAppPermission(Context context) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_CALENDAR)) {

            return "Calendar permission allows us to access calendar data. Please allow in App Settings for additional functionality.";

        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_CALENDAR)) {

            return "Calendar permission allows us to access calendar data. Please allow in App Settings for additional functionality.";

        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            return "External Storage permission allows us to access storage data. Please allow in App Settings for additional functionality.";

        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            return "External Storage permission allows us to access storage data. Please allow in App Settings for additional functionality.";

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MessageInfo.calendar_permission_request_code);
            return "Requesting for Permission";
        }
    }

    public static String requestPhoneNetworkAccessPermission(Context context) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_PHONE_STATE)) {

            return "Read Phone State permission allows us to access phone network status. Please allow in App Settings for additional functionality.";

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, MessageInfo.phone_state_permission_request_code);
            return "Requesting for Permission";
        }
    }

    public static String requestReadWriteExternalStoragePermission(Context context) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            return "External Storage permission allows us to access storage data. Please allow in App Settings for additional functionality.";

        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            return "External Storage permission allows us to access storage data. Please allow in App Settings for additional functionality.";

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MessageInfo.external_storage_permission_request_code);
            return "Requesting for Permission";
        }
    }


    public static Cursor getcalendars(Context context) {
        Cursor mgdc = null;
        String[] projection;
        String path = "calendars";
        try {

            if (android.os.Build.VERSION.SDK_INT >= 14) {
                projection =
                        new String[]{
                                Calendars._ID,
                                Calendars.NAME,
                                Calendars.ACCOUNT_NAME,
                                Calendars.ACCOUNT_TYPE};

                mgdc = context.getContentResolver().query(Calendars.CONTENT_URI, projection, null, null, null);
            } else {
                projection = new String[]{"*"};
//			mgdc= managedQuery(Uri.parse(getCalendarUriBase() + path), projection, null, null, null);
            }

        } catch (SecurityException e) {
            Utility.saveExceptionDetails(context, e);
        }
        return mgdc;
    }


    /**
     *
     */
    public static boolean filterCalenderEventDate(Context context, String currentDate) {
        boolean bln = false;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println("filterCalenderEventDate()====>  currentdate :" + currentDate);
        // Get a date 30 days ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        //				Date startDate = cal.getTime();
        String startDate = sdf.format(cal.getTime());
        System.out.println("filterCalenderEventDate()====>  startDate :" + startDate);


        try {
            if (sdf.parse(startDate).before(sdf.parse(currentDate))) {
                bln = true;
            }
            if (sdf.parse(startDate).equals(sdf.parse(currentDate))) {
                bln = true;
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
            bln = false;
        }
        return bln;
    }


    /*
     * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
     */
    private String getCalendarUriBase(Context context) {
        String calendarUriBase = null;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            //the old way
            try {
                calendarUriBase = "content://calendar/";
            } catch (Exception e) {
                Utility.saveExceptionDetails(context, e);
                e.printStackTrace();
            }
        } else {
            //the new way
            try {
                calendarUriBase = "content://com.android.calendar/";
            } catch (Exception e) {
                Utility.saveExceptionDetails(context, e);
                e.printStackTrace();
            }
        }
        return calendarUriBase;
    }

    public static boolean firstTimeLogin(Context context) {
        boolean bln = true;
        try {
            Synchronization syc = new Synchronization(context);
            String lastSynchDate = syc.getLast_Sync_Date();
            System.out.println("ActivityStringInfo.strLogin=====>: " + ActivityStringInfo.strLogin);
            System.out.println("ActivityStringInfo.strLoginName=====>: " + ActivityStringInfo.strLoginName);
            System.out.println("firstTimeLogin()===>Last Synched Date=====" + lastSynchDate);

            if (!lastSynchDate.equals("") && ActivityStringInfo.strLogin.equals(ActivityStringInfo.strLoginName)) {
                bln = false;
            } else if (!lastSynchDate.equals("") && !ActivityStringInfo.strLogin.equals(ActivityStringInfo.strLoginName)) {
                bln = true;
            }
            System.out.println("firstTimeLogin()==" + bln);
        } catch (Exception e) {
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return bln;
    }

    /**
     * CALL WHEN WE DOWNLOAD THE ANY DOCUMENT
     **/
    public static void notification(Context context, String strFileName) {
        try {
            //Showing notification is notification bar.
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
            int icon = R.drawable.white;
            CharSequence tickerText = "File downloaded.";
            long when = System.currentTimeMillis();
            CharSequence contentTitle = "StaffTAP notification";
            CharSequence contentText = strFileName + " downloaded Successfully.";

            /**  Second way give option for open the file **/
            Intent intent1 = new Intent();
            intent1.setAction(Intent.ACTION_VIEW);

            String path = Environment.getExternalStorageDirectory() + "/download/StaffTAP/";
            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getGlobalsRecords("download_path");
            System.out.println("in count====get ============" + c1.getCount());
            if (c1.getCount() != 0) {
                while (c1.moveToNext()) {
                    path = c1.getString(1);
                }
            }
            c1.close();

            File file = new File(path + strFileName);

            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String ext = file.getName().substring(file.getName().indexOf(".") + 1);
            String type = mime.getMimeTypeFromExtension(ext);

            intent1.setDataAndType(Uri.fromFile(file), type);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setAutoCancel(false);
            builder.setTicker(tickerText);
            builder.setContentTitle(contentTitle);
            builder.setContentText(contentText);
            builder.setSmallIcon(icon);
            builder.setContentIntent(pendingIntent);
            builder.setOngoing(true);
            builder.setSubText("");   //API level 16
            builder.setNumber(100);
            builder.build();

            Notification notification = builder.build();
            notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND;
//            mNotificationManager.notify(11, notification);

            if (getDownloadedFileName.get(strFileName) == null)
                getDownloadedFileName.put(strFileName, "" + NOTIFICATION_ID);
            System.out.println("getDownloadedFileName==" + getDownloadedFileName.toString());
            if (getDownloadedFileName.get(strFileName) != null) {
                System.out.println("in not null==" + strFileName);
                mNotificationManager.notify(Integer.parseInt(getDownloadedFileName.get(strFileName)), notification);
            } else {
                System.out.println("in else==" + strFileName);
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
            NOTIFICATION_ID++;
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
    }

    /**
     * SEND THE MAIL IF ANY EXCEPTION IS GENERATE IN APPLICATION
     **/
    static String strException = "";

    public static void saveExceptionDetails(final Context context, final Exception exception) {
        try {
            Thread t = new Thread() {
                @SuppressLint("NewApi")
                @SuppressWarnings("static-access")
                @Override
                public void run() {
                    try {
                        String mailBody = "";
                        System.out.println("in exception=========");
                        exception.printStackTrace();
                        String deviceId = "";
                        String appName = "", appClassName = "", details = "";
                        String addedDate = "", emailId = "", ip = "";
                        //String phone_no="";


//                        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                        deviceId = "";
                        //phone_no = tManager.getLine1Number();
                        if (context != null) {
                            appName = context.getResources().getString(R.string.app_name);
                            appClassName = context.getClass().getName();
                        }


                        //Calendar cal =  Calendar.getInstance();
                        //Date date = cal.getTime();
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
                        addedDate = sdf.format(date);

                        if (context != null) {
                            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            int ipAddress = wifiInfo.getIpAddress();
                            ip = intToIp(ipAddress);
                        }

                        if (context != null) {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                        }
                        if (context != null) {
                            Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
                            for (Account account : accounts) {
                                emailId = account.name;
                            }
                        }
                        mailBody += "\n\nDate : " + addedDate + "\r\n";
                        mailBody += "Device id  : " + deviceId + "\r\n";
                        mailBody += "IP address : " + ip + "\r\n";
                        mailBody += "Application name : " + appName + "\r\n";
                        mailBody += "Application version : " + ActivityStringInfo.VERSION + "\r\n";
                        mailBody += "Class name : " + appClassName + "\r\n";
                        mailBody += "Email id : " + emailId + "\r\n\r\n";

                        details = exception.getClass().getName() + " : " + exception.getMessage();
                        mailBody += "Exception :  " + details + "\r\n";

                        strException = mailBody;


                        StackTraceElement[] stackTrace = exception.getStackTrace();
                        for (int i = 0; i <= (stackTrace.length) - 1; i++) {
                            mailBody += "at " + stackTrace[i] + "\r\n";
                            details += "," + stackTrace[i];
                        }


                        MyDatabaseInstanceHolder.getDatabaseHelper().insertForceClosedData(deviceId, appName, appClassName, emailId, details, addedDate, ip);


//                        String[] toArr = {"vipsha21@yahoo.com"};
//
//                        Mail mail = new Mail("sendmail@vipsha.com", "reset123", context);
//                        mail.setEmailFrom(emailId);
//                        mail.setEmailTo(toArr);
//                        mail.setEmailSubject("Exception generated in application : " + appName);
//                        mail.setEmailBody(mailBody);
//
//                        mail.send();

                        System.out.println("Application name : " + appName);
                        System.out.println("Details : " + details);
                        System.out.println("device Id :" + deviceId);
                        System.out.println("emailId  :" + emailId);
                        System.out.println("Date : " + addedDate);
                        System.out.println("From wifi IP  : " + ip);

                        super.run();
                    } catch (Exception e) {
                        System.out.println("in exception mail");
                        writeErrorInDirectory(strException, context);
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        } catch (Exception e) {
            System.out.println("in exception mail...");
            writeErrorInDirectory(strException, context);
            e.printStackTrace();
        }
    }

    /**
     * IF INTERNET IS NOT AVAILABLE THEN THAT ERROR IS WRITE IN TEXT FILE
     **/
    public static void writeErrorInDirectory(String mailBody, Context context) {
        FileWriter writer = null;
        try {
            String path = Environment.getExternalStorageDirectory() + "/download/StaffTAP/";
            File file = new File(path + "Logs/");
            if (!file.exists()) {
                file.mkdirs();
            }
            writer = new FileWriter(path + "Logs/ErrorLog.txt", true);
            BufferedWriter br = new BufferedWriter(writer);
            br.write(mailBody);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String intToIp(int i) {
        return
                (i & 0xFF) + "." +
                        ((i >> 8) & 0xFF) + "." +
                        ((i >> 16) & 0xFF) + "." +
                        ((i >> 24) & 0xFF);
    }

    public static void OpenCreateFile(Context mContext) {
        boolean mExternalStorage = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorage = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorage = false;
        } else {
            mExternalStorage = false;
        }

        if (mExternalStorage == false) {
            String msg = "SD Card is NOT Accessable, Please Remount and Try Again";
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        } else {
            boolean exists = (new File(Environment.getExternalStorageDirectory() + "/ISM_Image")).exists();

            if (exists) {
                //Toast.makeText(getApplicationContext(), "Accessing Data Files...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Creating directory...", Toast.LENGTH_SHORT).show();

                File root = Environment.getExternalStorageDirectory();
                boolean success = (new File(root, "ISM_Image")).mkdirs();

                if (!success) {
                    Toast.makeText(mContext, "Failed to create directory.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static String getDate() {
        String strDate = "";
        Date date = Calendar.getInstance(Locale.US).getTime();
//        DateFormat dateFormat = DateFormat.getDateTimeInstance();
//        strDate = dateFormat.format(date);

        try {
//            SimpleDateFormat sdfSource = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss", Locale.US);
//            if (strDate.contains("a.m.") || strDate.contains("p.m.")) {
//                sdfSource = new SimpleDateFormat("d MMM y hh:mm:ss a", Locale.US);
//            }
//            Date d = sdfSource.parse(strDate);
            SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyyddMMhhmmss", Locale.US);
            strDate = ActivityStringInfo.strLogin + "_" + sdfDestination.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            strDate = ActivityStringInfo.strLogin + "_" + date.getTime();
        }
        return strDate;
    }

    public static void refreshList_Picture(Context mContext, SimpleAdapter adapter_AddPicture, ListView lstView_Picture) {
        try {
            adapter_AddPicture = new SimpleAdapter(mContext, arrList_Picture, R.layout.list_message_attachments_new, new String[]{"no", "image", "image_name", "delete_image"}, new int[]{R.id.txtView1, R.id.imgDocIcon, R.id.txtMessageAttachmentFileName, R.id.btnDelete});
            lstView_Picture.setAdapter(adapter_AddPicture);
            adapter_AddPicture.notifyDataSetChanged();
            Utility.setListViewHeightBasedOnChildren(mContext, lstView_Picture);
        } catch (Exception e) {
            saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
    }

    /***
     * DELETE SELECTED ROW FROM PICTURE LIST
     ***/
    public static void deleteRow_Picture(int position, Context mContext, SimpleAdapter adapter_AddPicture, ListView lstView_Picture) {
        try {
            HashMap<String, String> map = new HashMap<String, String>();
            map = arrList_Picture.get(position);
            String strImagePath = map.get("file_path");
            ActivityStringInfo.hashDocumentList.remove(map.get("image_name"));
            System.out.println("strImagePath===" + strImagePath + "==position==" + position);
            File imgDelete = new File(strImagePath);
            imgDelete.delete();
            arrList_Picture.remove(position);
            for (int i = 0; i < arrList_Picture.size(); i++) {
                map = arrList_Picture.get(i);
                map.put("no", "" + (i + 1));
            }
            refreshList_Picture(mContext, adapter_AddPicture, lstView_Picture);
        } catch (Exception e) {
            saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
    }

    public static boolean browsImages(Context context) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/ISM_Image");
            File[] arrFiles = file.listFiles();

            if (arrFiles.length > 0) {
                strArr_SurveyPictures = new String[arrFiles.length];
                file_Name = new String[arrFiles.length];
                for (int i = 0; i < arrFiles.length; i++) {
                    File f = arrFiles[i];
                    System.out.println("f==" + f.getName());
                    String strName = f.getName();
                    file_Name[i] = strName;
                    strArr_SurveyPictures[i] = f.getAbsolutePath();
                }
                return true;
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static String getLocalIpAddress(Context context) {
        String strIPAddress = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        strIPAddress = inetAddress.getHostAddress().toString();
                        return strIPAddress;
                    }
                }
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return null;
    }

    public static void scaleImage(Context context, String strImagePath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 7;
        System.out.println("strImagePath=in scale=======" + strImagePath);
        Bitmap mBitmap = BitmapFactory.decodeFile(strImagePath, opts);
        FileOutputStream out;
        try {
            out = new FileOutputStream(strImagePath);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
    }

    // METHOD NOT FOUND getNextPreviousMessage()

    /**
     * DELETE ENTRY FROM THE CALENDAR USEING ID
     **/
    @SuppressWarnings("static-access")
    public static void ListCalendarEntryDeleteByID(Context context) {
        try {
            try {
                Synchronization syc = new Synchronization(context);
                //ActivityStringInfo.isNoLogOut=false;
                try {
                    syc.getInformation(context);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(context, e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(context, e);
                e.printStackTrace();
            }

            if (ActivityStringInfo.strMandatoryMessageId.size() == 0 && ActivityStringInfo.strNewScheduleMessageId.size() == 0) {
                SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.PREFS_NAME), context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(context.getString(R.string.REGISTERED_GUID), null);
                editor.putInt(context.getString(R.string.ORG_ID), 0);
                editor.putString(context.getString(R.string.WEB_SERVICE_LOCATION), null);
                editor.putLong(context.getString(R.string.TIMER), 0);
                editor.putBoolean(context.getString(R.string.APP_RUNNING), false);// newly
                editor.commit();
                System.out.println("editor.commit();.....>>>>>>>>>>>>>>>>>>>>>>");
//						MyDatabaseInstanceHolder.getDatabaseHelper().close();
//						System.out.println("MyDatabaseInstanceHolder.getDatabaseHelper().close();;.....>>>>>>>>>>>>>>>>>>>>>>");
            }
        } catch (Exception e) {
            MyDatabaseInstanceHolder.getDatabaseHelper().close();
            saveExceptionDetails(context, e);
            System.exit(0);
            e.printStackTrace();
        }

    }

    /**
     * DELETE THE CALENDAR EVENT ID TEXT FILE
     **/
    public static void deleteEventIdTextFile(Context context) {
        try {
            String path = Environment.getExternalStorageDirectory() + "/download/StaffTAP/CalendarEventId.txt";
            System.out.println("path===" + path);
            File file = new File(path);
            boolean exists = file.exists();
            if (exists) {
                file.delete();
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
    }

    /**
     * ADD THE CALENDAR ENTRY IN CALENDAR LOG TEXT FILE
     **/
    public static void addEntryInCalendarLog(Context context, String strMsgType, String strMsg) {
        try {
            String path = Environment.getExternalStorageDirectory() + "/download/StaffTAP/Logs";
            File filelog = new File(path);
            if (!filelog.exists()) {
                filelog.mkdirs();
            }
            FileWriter writerlog = new FileWriter(path + "/CalendarLog.txt", true);
            BufferedWriter brlog = new BufferedWriter(writerlog);
            if (strMsgType.equals("Add"))
                brlog.write(strMsg + " is added.\n");
            else if (strMsgType.equals("Delete"))
                brlog.write(strMsg + " is deleted.\n");
            else if (strMsgType.equals("Update"))
                brlog.write(strMsg + " is updated.\n");
            brlog.close();
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * DELETE THE CALENDAR ENTRY USING CALENDAR EVENT TEXT FILE
     **/
    @SuppressLint("NewApi")
    public static void ListCalendarEntryDeleteByFile(Context context) {
        try {
            System.out.println("Delete by ListCalendarEntryDeleteByFile");
            String path = Environment.getExternalStorageDirectory() + "/download/StaffTAP/CalendarEventId.txt";
            System.out.println("path===" + path);
            File file = new File(path);
            boolean exists = file.exists();
            if (exists) {
                // Open the file that is the first
                // command line parameter
                FileInputStream fstream = new FileInputStream(Environment.getExternalStorageDirectory() + "/download/StaffTAP/CalendarEventId.txt");
                // Get the object of DataInputStream

                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    // Print the content on the console
                    System.out.println("ListCalendarEntryDeleteByFile====" + strLine.substring(0, strLine.indexOf("-")));
                    Uri calendars = Uri.parse(getCalendarUriBase() + "events");//}
                    Uri uri = ContentUris.withAppendedId(calendars, Integer.parseInt(strLine.substring(0, strLine.indexOf("-"))));
                    context.getContentResolver().delete(uri, null, null);
                    addEntryInCalendarLog(context, "Delete", "CalId:- " + strLine.substring(strLine.indexOf("-") + 1, strLine.indexOf("+")) + " CalName:- " + strLine.substring(strLine.indexOf("+") + 1) + " EventId:- " + strLine.substring(0, strLine.indexOf("-")));
                }
                //Close the input stream
                in.close();
                deleteEventIdTextFile(context);
            }
            /*else
            {
				ListCalendarEntry(context);
			}*/
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    public static void ListCalendarEntry(Context context) {
        try {
            System.out.println("Delete by ListCalendarEntry");
            Uri calendars = Uri.parse(getCalendarUriBase() + "events");
            Cursor c = context.getContentResolver().query(calendars, null, null, null, null);
            if (c.moveToFirst()) {
                while (c.moveToNext()) {
                    String title = c.getString(c.getColumnIndex("title"));
                    String calId = c.getString(c.getColumnIndex("calendar_id"));
                    String calName = c.getString(c.getColumnIndex("name"));
                    // event id
                    String id = c.getString(c.getColumnIndex("_id"));
                    if ((title == null)) {
                    } else {
                        if (title.startsWith("ISM ")) {
                            Uri uri = ContentUris.withAppendedId(calendars, Integer.parseInt(id));
                            context.getContentResolver().delete(uri, null, null);
                            addEntryInCalendarLog(context, "Delete", "CalId:- " + calId + " CalName:- " + calName + " EventId:- " + id);
                        }
                    }
                }
            }

        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }

    }

    /**
     * CHECK THE INTERNET CONNECTIVITY
     **/
    public static boolean Connectivity_Internet(Context mContext) {
        try {
            if (mContext != null) {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnectedOrConnecting();
            }
            return false;
        } catch (Exception e) {
            saveExceptionDetails(mContext, e);
            e.printStackTrace();
            return false;
        }

    }

    /*
     * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
     */
    private static String getCalendarUriBase() {
        String calendarUriBase = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                //the old way
                calendarUriBase = "content://calendar/";
            } else {
                //the new way
                calendarUriBase = "content://com.android.calendar/";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarUriBase;
    }

    /**
     * DELETE DOWNLOAD FILE FROM STAFFTAP FOLDER AFTER 24 HR
     **/
    public static void deleteDownloadFile(Context context) {
        try {
            // Used to examplify deletion of files more than 1 month old
            // Note the L that tells the compiler to interpret the number as a Long
            final int MAXFILEAGE = 86400000; // 24 Hr in milliseconds

            String path = Environment.getExternalStorageDirectory() + "/download/StaffTAP/";
            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getGlobalsRecords("download_path");
            System.out.println("in count====get ============" + c1.getCount());
            if (c1.getCount() != 0) {
                while (c1.moveToNext()) {
                    path = c1.getString(1);
                }
            }
            c1.close();

            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }


            System.out.println("path===" + path);
            // Get file handle to the directory. In this case the application files dir
            File dir = new File(path);

            // Optain list of files in the directory.
            // listFiles() returns a list of File objects to each file found.
            File[] files = dir.listFiles();

            // Loop through all files
            for (File f : files) {

                // Get the last modified date. Miliseconds since 1970
                Long lastmodified = f.lastModified();

                // Do stuff here to deal with the file..
                // For instance delete files older than 1 month
                if (lastmodified + MAXFILEAGE < System.currentTimeMillis()) {
                    System.out.println("file delete====" + f.getName());
                    if (!f.getName().equals("CalendarEventId.txt"))
                        f.delete();
                    System.out.println("file delete successfully");
                }
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }

    }

    public static String getDayName(Context context, Date dateObj) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, dateObj.getYear());
            calendar.set(Calendar.MONTH, dateObj.getMonth());
            calendar.set(Calendar.DAY_OF_WEEK, dateObj.getDay());
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (day) {
                case 1:
                    return "Monday";
                case 2:
                    return "Tuesday";
                case 3:
                    return "Wednesday";
                case 4:
                    return "Thursday";
                case 5:
                    return "Friday";
                case 6:
                    return "Saturday";
                case 7:
                    return "Sunday";
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return "";
    }

    public static String getMonthName(Context context, String strDate) {
        try {
            String[] split = strDate.split("/");

			/*System.out.println("Year : "+ Integer.parseInt(split[0]));
            System.out.println("Month : "+ Integer.parseInt(split[1]));
			System.out.println("DATE : "+ Integer.parseInt(split[2]));*/

            //			Calendar calendar = Calendar.getInstance();
            //			calendar.set(Calendar.YEAR, Integer.parseInt(split[0]));
            //			calendar.set(Calendar.MONTH, Integer.parseInt(split[1]));
            //			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[2]));
            //int month = calendar.get(Calendar.MONTH);

            int month = Integer.parseInt(split[1]);

            switch (month) {
                case 1:
                    return "January";
                case 2:
                    return "February";
                case 3:
                    return "March";
                case 4:
                    return "April";
                case 5:
                    return "May";
                case 6:
                    return "June";
                case 7:
                    return "July";
                case 8:
                    return "August";
                case 9:
                    return "September";
                case 10:
                    return "October";
                case 11:
                    return "November";
                case 12:
                    return "December";
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return "";
    }


    public static boolean compareLastSyncForDisable(Context context) {
        boolean bln = false;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        try {
            if (sdf.parse(ActivityStringInfo.strLastSyncDate).after(ActivityStringInfo.strUpdateTime)) {
                bln = true;
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
            bln = false;
        }
        return bln;
    }


    /**
     * SET THE BANNER VALUE
     **/
    @SuppressWarnings("static-access")
    public static void setBanner(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.PREFS_NAME), context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(context.getString(R.string.NEWS_BANNER), ActivityStringInfo.strNewsBanner);
            editor.commit();
            StaticVariables.setNEWS_BANNER(ActivityStringInfo.strNewsBanner);
            System.out.println("banner====" + StaticVariables.getNEWS_BANNER(context));
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
    }


    // 	/**NOTIFICATION FOR THE MANDATORY MESSAGE**/  not found
    //  	/**NOTIFICATION FOR THE NEW SCHEDULE OF SHIFT**/  - not found
    //  cancelAlarmService not found

    //**GET THE MANDATORY MESSAGE**//*
    public static Intent getMandatoryMessage(Context context) {
        Intent intent2 = null;
        try {
            ActivityStringInfo.strMandatoryMessageId.clear();
            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getNotReadMandatoryMessage();
            System.out.println("in count====MM ============" + c1.getCount());
            if (c1.getCount() != 0) {
                while (c1.moveToNext()) {
                    ActivityStringInfo.strMandatoryMessageId.add(c1.getString(0));
                }
            }
            c1.close();

            if (ActivityStringInfo.strMandatoryMessageId.size() > 0) {
                intent2 = new Intent(context.getApplicationContext(), MandatoryMessageDetailActivity.class);
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return intent2;
    }

    public static boolean compareLastSyncDateForShift(Context context, String shiftDate) {
        boolean bln = false;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        // Get a date 30 days ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        String thirtyDayBackDate = sdf.format(cal.getTime());
        try {
            if (sdf.parse(thirtyDayBackDate).before(sdf.parse(shiftDate))) {
                bln = true;
            }
            if (sdf.parse(thirtyDayBackDate).equals(sdf.parse(shiftDate))) {
                bln = true;
            }
        } catch (Exception e) {
            saveExceptionDetails(context, e);
            e.printStackTrace();
            bln = false;
        }
        return bln;
    }

    public static String getThirtyDayBeforeDate(String dateFormat) {
        // Get a date 30 days ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date startDate = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        return sdf.format(startDate);
    }

	/*public static void saveLogInTextFile(Context context,SoapObject soap,String filename)
    {
		
	try
	{
		String path = Environment.getExternalStorageDirectory()+ "/download/StaffTAP/Logs";
		File filelog = new File(path);
		if(!filelog.exists())
		{
			filelog.mkdirs();   
		}
		FileWriter writerlog = new FileWriter(path+"/"+filename+".txt",true);
		BufferedWriter brlog = new BufferedWriter(writerlog);
		brlog.write(soap.toString());
		brlog.close();
	}
	catch (Exception e)
	{
		saveExceptionDetails(context, e);
		e.printStackTrace();
		System.err.println("Error in Soaplog txt: " + e.getMessage());
	}
	}*/


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, 9000).show();
            } else {
//                Log.i(TAG, "This device is not supported.");
//                finish();
            }
            return false;
        }
        return true;
    }
}