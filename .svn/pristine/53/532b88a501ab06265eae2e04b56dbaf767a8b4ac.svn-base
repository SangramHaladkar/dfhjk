package ism.manager.backgroundservices;


import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.Utility;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.StaticVariables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.ksoap2.serialization.SoapObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.provider.CalendarContract.Calendars;
import android.text.format.Time;
import android.util.Log;

//Service for Store Shift for employee in calendar
public class NotificationServiceForShift extends Service {
    Context context;
    private Timer timer = new Timer();
    private final int TIME_INTERVAL = 1000;
    private SimpleDateFormat currentDateFormat;
    private SimpleDateFormat newDateFormat;

    private class RemindTask extends TimerTask {
        @Override
        public void run() {
            try {
                int startime = (int) System.currentTimeMillis();
                System.out.println("TIMER START FOR SHIFT");
                Looper.prepare();
                if (timer != null) {
                    timer.cancel();
                    callSynchronization();
                }
                Looper.loop();
                int stoptime = (int) System.currentTimeMillis();
                Log.v("NotificationServiceForShift time", String.valueOf((stoptime - startime) / 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        /** Create application level object to access the DatabaseHelper class to insert current time in database. **/
        timer = new Timer();
        timer.schedule(new RemindTask(), TIME_INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void callSynchronization() {
        int calendarId = 0;
        currentDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        newDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        System.out.println("In NotificationServiceForShift............_------->");
        Cursor cget = MyDatabaseInstanceHolder.getDatabaseHelper().getUserRecord();
        try {
            if (cget.getCount() > 0) {
                while (cget.moveToNext()) {
                    ActivityStringInfo.strCalIdName = cget.getString(10);
                }
            }
            cget.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            calendarId = Integer.parseInt(ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-"))); //removable
        } catch (Exception e) {
            e.printStackTrace();
        }

        String strEventId = "";
        try {
            /** Data for SHIFT Table **/
            SoapObject Shift_Detail_List = ActivityStringInfo.strShiftList;
            //System.out.println("Shift_Detail_List=="+Shift_Detail_List.toString());
            if (null != Shift_Detail_List) {
                HashSet<String> hashShiftId = new HashSet<String>();
                try {

                    Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftList();
                    while (c.moveToNext()) {
                        hashShiftId.add(c.getString(0));
                    }
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < Shift_Detail_List.getPropertyCount(); i++) {
                    SoapObject Shift_Detail = (SoapObject) Shift_Detail_List.getProperty(i);
                    //System.out.println("Emessage=="+Shift_Detail_List.toString());
                    String shiftId = Shift_Detail.getProperty("SHIFT_ID").toString().replace("anyType{}", "");
                    String Date = Shift_Detail.getProperty("DATE").toString().replace("anyType{}", "");
                    String Phone = "";
                    if (Shift_Detail.hasProperty("PHONE")) {
                        Phone = Shift_Detail.getProperty("PHONE").toString().replace("anyType{}", "");
                    }

                    String ShiftStartTime = Shift_Detail.getProperty("SHIFT_START_TIME").toString().replace("anyType{}", "");
                    String ShiftEndTime = Shift_Detail.getProperty("SHIFT_END_TIME").toString().replace("anyType{}", "");
                    String SplitStartTime = Shift_Detail.getProperty("SPLIT_START_TIME").toString().replace("anyType{}", "");
                    String SplitEndTime = Shift_Detail.getProperty("SPLIT_END_TIME").toString().replace("anyType{}", "");
                    String Trainee = Shift_Detail.getProperty("TRAINEE").toString().toString().replace("anyType{}", "");
                    String WorkStation = Shift_Detail.getProperty("WORKSTATION").toString().replace("anyType{}", "");
                    String IsOverTime = Shift_Detail.getProperty("ISOVERTIME").toString().replace("anyType{}", "");
                    String Status = Shift_Detail.getProperty("STATUS").toString().replace("anyType{}", "");
                    String Schedule_Name = Shift_Detail.getProperty("SCHEDULE_NAME").toString().replace("anyType{}", "");
                    String UserName = Shift_Detail.getProperty("USER_NAME").toString().replace("anyType{}", "");
                    String Position_Title = Shift_Detail.getProperty("POSITION_TITLE").toString().replace("anyType{}", "");
                    //Newly added
                    String shiftDate = "";
                    try {
                        Date objDate = currentDateFormat.parse(Date);
                        shiftDate = newDateFormat.format(objDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }//

                    if (hashShiftId.contains(shiftId)) {
                        hashShiftId.remove(shiftId);
                    }


                    Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftTaskList(shiftId);

                    String taskList = "";

                    if (c.getCount() != 0) {
                        while (c.moveToNext()) {
                            taskList += "\n    - " + c.getString(2);
                        }
                    }
                    c.close();

                    Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftDetail(shiftId);

                    if (c1.getCount() == 0) {

                        if (calendarId != 0) {
                            Uri newEvent;
                            //Newly added
//							if(Utility.compareLastSyncDateForShift(context, shiftDate))
//								{
                            if (!SplitStartTime.equals(""))
                                newEvent = MakeNewCalendarEntry(context, calendarId, Date, ShiftStartTime, SplitEndTime, UserName, Phone, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
                            else
                                newEvent = MakeNewCalendarEntry(context, calendarId, Date, ShiftStartTime, ShiftEndTime, UserName, Phone, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
                            int eventID = Integer.parseInt(newEvent.getLastPathSegment());
                            System.out.println("shift eventID add successfuuly===" + eventID);

                            strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                            MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftData(shiftId, Date, Phone, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, "" + eventID, "O", Schedule_Name, UserName, Position_Title);
                            //System.out.println("Shift_Detail_List insert Successfully.");
//							}
                        }

                    } else {
                        while (c1.moveToNext()) {
                            if (!c1.getString(3).equals(ShiftStartTime) || !c1.getString(4).equals(ShiftEndTime) || !c1.getString(5).equals(SplitStartTime) || !c1.getString(6).equals(SplitEndTime) || !c1.getString(10).equals(Status) || !c1.getString(14).equals(UserName)) {
                                String eventId = c1.getString(11);
                                if (calendarId != 0) {
                                    DeleteCalendarEntry(Integer.parseInt(eventId), context);
                                    Uri newEvent;
                                    //Newly added
//									if(Utility.compareLastSyncDateForShift(context, shiftDate))
//										{
                                    if (!SplitStartTime.equals(""))
                                        newEvent = MakeNewCalendarEntry(context, calendarId, Date, ShiftStartTime, SplitEndTime, UserName, Phone, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
                                    else
                                        newEvent = MakeNewCalendarEntry(context, calendarId, Date, ShiftStartTime, ShiftEndTime, UserName, Phone, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
                                    int eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                    System.out.println("eventID add successfuuly===" + eventID);

                                    strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";

                                    MyDatabaseInstanceHolder.getDatabaseHelper().updateShiftRecords(shiftId, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Status, "" + eventID, UserName, Position_Title);
//										}
                                }

                            }
                        }
                    }
                    c1.close();
                }
                if (hashShiftId.size() > 0) {
                    Iterator<String> it = hashShiftId.iterator();
                    while (it.hasNext()) {
                        String strShiftId = it.next();

                        Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftDetail(strShiftId);
                        while (c1.moveToNext()) {
                            String shiftDate = "";

                            String eventId = c1.getString(c1.getColumnIndex(DatabaseConstant.key_CAL_EVENT_ID));
                            String Date = c1.getString(c1.getColumnIndex(DatabaseConstant.key_DATE));

                            Date objDate = currentDateFormat.parse(Date);
                            Date cDate = currentDateFormat.parse(currentDateFormat.format(Calendar.getInstance().getTime()));
                            shiftDate = newDateFormat.format(objDate);

                            if (calendarId != 0) {
                                if (!Utility.compareLastSyncDateForShift(context, shiftDate) || objDate.after(cDate)) {

                                    System.out.println("delete eventId===" + eventId);
                                    System.out.println("delete strShiftId===" + strShiftId);
                                    if (!eventId.equals(""))
                                        DeleteCalendarEntry(Integer.parseInt(eventId), context);

                                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRecord(strShiftId);
                                }
                            }

                        }
                        c1.close();
                    }
                }
            }

            /** Data for LEAVE REQUEST Table **/


            SoapObject Leave_Request_List = ActivityStringInfo.strLeaveRequestList;

            if (null != Leave_Request_List) {
                //System.out.println("Meeting_Detail_List=="+Meeting_Detail_List.toString());
                HashSet<String> hashLeaveRequestId = new HashSet<String>();
                try {

                    Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getAllLeaveRequestList();
                    while (c.moveToNext()) {
                        hashLeaveRequestId.add(c.getString(0));
                    }
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < Leave_Request_List.getPropertyCount(); i++) {
                    SoapObject Leave_Request = (SoapObject) Leave_Request_List.getProperty(i);
                    String leaveId = Leave_Request.getProperty("LEAVE_ID").toString().replace("anyType{}", "");
                    String userId = Leave_Request.getProperty("USER_ID").toString().replace("anyType{}", "");
                    String userName = Leave_Request.getProperty("USER_NAME").toString().replace("anyType{}", "");
                    String startDate = Leave_Request.getProperty("START_DATE").toString().replace("anyType{}", "");
                    String endDate = Leave_Request.getProperty("END_DATE").toString().replace("anyType{}", "");

                    String formatedEndDate = "";
                    try {
                        Date objDate = currentDateFormat.parse(endDate);
                        formatedEndDate = newDateFormat.format(objDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (hashLeaveRequestId.contains(leaveId)) {
                        hashLeaveRequestId.remove(leaveId);
                    }

                    Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLeaveRequestList(leaveId);
                    if (c1.getCount() == 0) {

                        int eventID = 0;
                        if (calendarId != 0) {
                            //Newly added
//							if(Utility.compareLastSyncDateForShift(context, formatedEndDate))
//								{
                            Uri newEvent = MakeNewLeaveRequestCalendarEntry(calendarId, startDate, endDate, userName);
                            eventID = Integer.parseInt(newEvent.getLastPathSegment());
                            System.out.println("LeaveEventID add successfuuly===" + eventID);

                            strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                            MyDatabaseInstanceHolder.getDatabaseHelper().insertLeaveRequestData(leaveId, userId, userName, startDate, endDate, "" + eventID);
                            //System.out.println("Meeting_Detail_List insert Successfully.");
//								}
                        }

                    }

					/*else
            {
				while(c1.moveToNext())
				{
					if(!c1.getString(4).equals(status))
					{
						MyDatabaseInstanceHolder.getDatabaseHelper().updateLeaveRequestData(leaveId, startDate, endDate, status);
					}
				}
			}*/
                    c1.close();
                }
                if (hashLeaveRequestId.size() > 0) {
                    Iterator<String> it = hashLeaveRequestId.iterator();
                    while (it.hasNext()) {
                        String strLeaveId = it.next();
                        String leaveDate = "";
                        Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLeaveRequestList(strLeaveId);
                        while (c1.moveToNext()) {
                            String eventId = c1.getString(5);
                            String Date = c1.getString(3);

                            Date objDate = currentDateFormat.parse(Date);
                            Date cDate = currentDateFormat.parse(currentDateFormat.format(Calendar.getInstance().getTime()));

                            leaveDate = newDateFormat.format(objDate);

                            if (calendarId != 0) {
                                if (!Utility.compareLastSyncDateForShift(context, leaveDate) || objDate.after(cDate)) {
                                    if (!eventId.equals("")) {
                                        DeleteCalendarEntry(Integer.parseInt(eventId), context);
                                    }
                                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteLeaveRecord(strLeaveId);
                                }
                            }

                        }
                        c1.close();
                    }
                }
            }
            context.stopService(new Intent(context, NotificationServiceForShift.class));

            String PATH = Environment.getExternalStorageDirectory() + "/download/StaffTAP/";
            File file = new File(PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileWriter writer = new FileWriter(PATH + "/CalendarEventId.txt", true);
            BufferedWriter br = new BufferedWriter(writer);
            br.write(strEventId);
            br.close();
        } catch (Exception e) {
            context.stopService(new Intent(context, NotificationServiceForShift.class));
            e.printStackTrace();
        }

    }

    @SuppressLint("NewApi")
    private Uri MakeNewCalendarEntry(Context context, int calId, String shiftDate, String starttime, String endtime, String userName, String userPhone, String ShiftStartTime, String ShiftEndTime, String SplitStartTime, String SplitEndTime, String Trainee, String WorkStation, String IsOverTime, String Status, String Schedule_Name, String TaskList, String Position_Tital) {
        Uri insertedUri = null;
        try {
            ContentValues event = new ContentValues();
            event.put("calendar_id", calId);
            event.put("title", userName.substring(0, userName.indexOf(" ")) + " " + userName.substring(userName.indexOf(" "), userName.indexOf(" ") + 2).toUpperCase().replace(" ", "") + ".");

            if (IsOverTime.toLowerCase().equals("y"))
                IsOverTime = "Yes";
            else if (IsOverTime.toLowerCase().equals("n"))
                IsOverTime = "No";

            if (userPhone != null && userPhone.length() == 10) {
                userPhone = String.format("(%s) %s-%s", userPhone.substring(0, 3), userPhone.substring(3, 6), userPhone.substring(6, 10));
            }

            String strDescription = "\nUser Name : " + userName + "\nPhone : " + userPhone + "\n\n" + Schedule_Name + "\n" + Position_Tital + "\n" + WorkStation + "\n\nSHIFT Start : " + ShiftStartTime + "\nSHIFT End : " + ShiftEndTime + "\nSPLIT Start : " + SplitStartTime + "\nSPLIT End : " + SplitEndTime + "\n\nOVERTIME : " + IsOverTime + "\nTRAINEE : " + Trainee + "\nTASKS : " + TaskList + "\n\nSTATUS : " + Status;

            event.put("description", strDescription + "\n\nThis event can be automatically deleted when a schedule is updated. Please do not add any notes.");

            //event.put("description", "User Name : "+userName+"\n\nThis event can be automatically deleted when a schedule is updated. Please do not add any notes.");
            //  event.put("eventLocation", "Online");


            //System.out.println("startdate=="+starttime);
            //System.out.println("enddate=="+endtime);

            Date sdate = null;
            Date edate = null;
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            try {
                sdate = df.parse(starttime);
                edate = df.parse(endtime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //System.out.println("day=="+sdate);
            //System.out.println("edate=="+edate);
            long startTime = sdate.getTime();
            long endTime = edate.getTime();

            event.put("dtstart", startTime);
            event.put("dtend", endTime);

            event.put("allDay", 0); // 0 for false, 1 for true
            event.put("eventStatus", 1);
            //event.put("visibility", 0);
            //event.put("transparency", 0);
            event.put("hasAlarm", 1); // 0 for false, 1 for true
            event.put("eventTimezone", Time.getCurrentTimezone());

            //Uri eventsUri = Uri.parse(getCalendarUriBase(context)+"events");
            Uri eventsUri = Uri.parse(getCalendarUriBase(context) + "events");
            System.out.println("NotificationServiceForShift class-->MakeNewCalendarEntry()----> Api<14");
            insertedUri = context.getContentResolver().insert(eventsUri, event);

			/* // reminder insert
            Uri REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
		    event = new ContentValues();
		    event.put( "event_id", Long.parseLong(insertedUri.getLastPathSegment()));
		    event.put( "method", 1 );
		    event.put( "minutes", 10 );
		    mContext.getContentResolver().insert( REMINDERS_URI, event );*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertedUri;
    }

    /*
     * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
     */
    private String getCalendarUriBase(Context context) {
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

    @SuppressLint("NewApi")
    private int DeleteCalendarEntry(int entryID, Context context) {
        int iNumRowsDeleted = 0;
        try {
            Uri eventsUri = Uri.parse(getCalendarUriBase(context) + "events");
            System.out.println("NotificationServiceForShift class-->DeleteCalendarEntry()----> Api<14");
            Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
            iNumRowsDeleted = context.getContentResolver().delete(eventUri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iNumRowsDeleted;
    }

    @SuppressLint("NewApi")
    private Uri MakeNewLeaveRequestCalendarEntry(int calId, String StartDate, String EndDate, String userName) {
        Uri insertedUri = null;
        try {
            ContentValues event = new ContentValues();
            event.put("calendar_id", calId);
            event.put("title", "Approved Day Off");
            //event.put("eventLocation", location);
            event.put("description", "\nUser Name : " + userName + "\n\nThis event can be automatically deleted when a schedule is updated. Please do not add any notes.");
            Date dateObj = null;
            try {
                dateObj = StaticVariables.dbDateFormat.parse(StartDate);
                newDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newDateStr = newDateFormat.format(dateObj);

            Date sdate = newDateFormat.parse(newDateStr);
            long startTime = sdate.getTime();

            event.put("dtstart", startTime);
            event.put("dtend", startTime);

            event.put("allDay", 0); // 0 for false, 1 for true
            event.put("eventStatus", 1);
            //event.put("visibility", 0);
            //event.put("transparency", 0);
            event.put("hasAlarm", 1); // 0 for false, 1 for true
            event.put("eventTimezone", TimeZone.getDefault().getDisplayName());

            //Uri eventsUri = Uri.parse(getCalendarUriBase(context)+"events");

            Uri eventsUri = Uri.parse(getCalendarUriBase(context) + "events");
            System.out.println("NotificationServiceForShift class-->MakeNewLeaveRequestCalendarEntry()----> Api<14");
            insertedUri = context.getContentResolver().insert(eventsUri, event);

			/* // reminder insert
            Uri REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
		    event = new ContentValues();
		    event.put( "event_id", Long.parseLong(insertedUri.getLastPathSegment()));
		    event.put( "method", 1 );
		    event.put( "minutes", 10 );
		    mContext.getContentResolver().insert( REMINDERS_URI, event );*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertedUri;
    }

}