package ism.manager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import ism.manager.ActivityStringInfo;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/ism.manager/databases/";

    private static String DB_NAME = "ism_manager.db";
    private static int DB_VERSION = 1;
    private SQLiteDatabase database;

    Context myContext;

    //Table Name
    public static final String TABLE_NAME = "TABLE_UPDATE_INFO";

    public static final String DOCUMENTS = "DOCUMENTS";
    public static final String USERS = "USERS";
    public static final String MESSAGE = "MESSAGE";
    public static final String MESSAGE_TO_USER = "MESSAGE_TO_USER";
    public static final String MESSAGE_ATTACHMENT = "MESSAGE_ATTACHMENT";
    public static final String FORCE_CLOSE_DATA = "FORCE_CLOSE_DATA";
    public static final String MEETING = "MEETING";
    public static final String TRADE_SHIFT = "TRADE_SHIFT";
    public static final String SHIFT_OFFERING = "SHIFT_OFFERING";
    public static final String SHIFT_REQUEST = "SHIFT_REQUEST";
    public static final String REQUEST_OFF = "REQUEST_OFF";
    public static final String ADDRESS_BOOK = "ADDRESS_BOOK";

    public static final String LEAVE_REQUEST = "LEAVE_REQUEST";
    public static final String MY_LEAVE_REQUEST = "MY_LEAVE_REQUEST";

    public static final String SHIFTS = "SHIFTS";
    public static final String MY_SHIFTS = "MY_SHIFTS";
    public static final String SHIFT_TASK = "SHIFT_TASK";
    public static final String LOG_COMMUNICATION_DET = "LOG_COMMUNICATION_DET";
    public static final String LOG_DETAILS = "LOG_DETAILS";
    public static final String LOG_EMP_RELATION_DESC = "LOG_EMP_RELATION_DESC";
    public static final String LOG_FOLLOW_UP_LOG = "LOG_FOLLOW_UP_LOG";
    public static final String LOG_MAINTENANCE_DET = "LOG_MAINTENANCE_DET";
    public static final String LOG_MAINTENANCE_LOG = "LOG_MAINTENANCE_LOG";
    public static final String LOG_NOTES = "LOG_NOTES";
    public static final String LOG_PRODUCTIVITY_DET = "LOG_PRODUCTIVITY_DET";
    public static final String LOG_PRODUCTIVITY_FIELDS = "LOG_PRODUCTIVITY_FIELDS";
    public static final String LOG_QUALITY = "LOG_QUALITY";
    public static final String LOG_SAFETY_DESC = "LOG_SAFETY_DESC";
    public static final String LOG_SAFETY_LOG = "LOG_SAFETY_LOG";
    public static final String LOG_USERTYPE = "LOG_USERTYPE";
    public static final String GLOBALS = "GLOBALS";
    public static final String LOGIN_QUESTIONS = "LOGIN_QUESTIONS";
    public static final String LOG_FLAG = "LOG_FLAG";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        //this.DB_PATH = this.myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        //Log.e("Path 1", DB_PATH);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("onCreate()-DatabaseHelper.java: path :- " + db.getPath());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DB_NAME);
    }


    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            System.out.println("File Exists");
            //do nothing - database already exist
        } else {
            this.getReadableDatabase();
            try {
                System.out.println("NEW DATABASE CREATED");
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        String myPath = DB_PATH + DB_NAME;
        try {
            File file = new File(myPath);
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        /*SQLiteDatabase checkDB = null;
        try
		{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		}
		catch(SQLiteException e)
		{
			e.printStackTrace();
		}
		if(checkDB != null)
		{
			checkDB.close();
		}
		return checkDB != null ? true : false;*/
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];

        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        try {
            String mPath = DB_PATH + DB_NAME;
            database = SQLiteDatabase.openDatabase(mPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() {
        try {
            if (database != null)
                database.close();
            SQLiteDatabase.releaseMemory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.close();
    }

    /***
     * INSERT : Record in Documents
     ***/
    public long insertDocumentRecords(String docId, String parentId, String docName, String docType, String docFileLink) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_DOCUMENT_ID, docId);
            values.put(DatabaseConstant.key_DOCUMENT_PARENT_ID, parentId);
            values.put(DatabaseConstant.key_DOCUMENT_NAME, docName);
            values.put(DatabaseConstant.key_DOCUMENT_TYPE, docType);
            values.put(DatabaseConstant.key_DOCUMENT_FILE_LINK, docFileLink);
            return database.insert(DOCUMENTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }


    /***
     * INSERT : Record in USERS
     ***/
    public long insertUserData(String calIdName) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_USER_ID, ActivityStringInfo.strUser_id);
            values.put(DatabaseConstant.key_ACTIVE, "Yes");
            values.put(DatabaseConstant.key_FIRST_NAME, ActivityStringInfo.strFirstName);
            values.put(DatabaseConstant.key_MIDDLE_NAME, "");
            values.put(DatabaseConstant.key_LAST_NAME, ActivityStringInfo.strLastName);
            values.put(DatabaseConstant.key_LOGIN, ActivityStringInfo.strLogin);
            values.put(DatabaseConstant.key_ORG_ID, ActivityStringInfo.ORG_ID);
            values.put(DatabaseConstant.key_LOCATION, ActivityStringInfo.strWsLocation);
            values.put(DatabaseConstant.key_COMPANY_NAME, ActivityStringInfo.strCompanyName);
            values.put(DatabaseConstant.key_DEVICE_ID, ActivityStringInfo.regGUID);
            values.put(DatabaseConstant.key_CAL_NAME, calIdName);
            values.put(DatabaseConstant.key_LAST_SYNC_DT, "");
            values.put(DatabaseConstant.key_DOC_RIGHTS, ActivityStringInfo.strDocRights);
            values.put(DatabaseConstant.key_LOG_RIGHTS, ActivityStringInfo.strLogRights);


            return database.insert(USERS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long updateUserCompanyName(String orgName) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_COMPANY_NAME, orgName);

            return database.update(USERS, values, DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    /*public long updateUserData()
    {
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_ACTIVE, "No");
			values.put(DatabaseConstant.key_CAL_NAME, ActivityStringInfo.strCalIdName);

			return database.update(USERS, values, DatabaseConstant.key_CAL_NAME+" = '"+ActivityStringInfo.strCalIdName+"'", null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}*/

    public long updateUserCalendarName(String calendarName) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ACTIVE, "Yes");
            values.put(DatabaseConstant.key_CAL_NAME, calendarName);

            return database.update(USERS, values, DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT : Record in Message
     ***/
    public long insertMessageData(String strMessageId, String strSubject, String strBody, String strFromUserId, String strFromUserName, String strMsgDate, String strType, String strSubType, String strProcessID, String strreplyUserId, String strreplyUserName) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_MESSAGE_ID, strMessageId);
            values.put(DatabaseConstant.key_SUBJECT, strSubject);
            values.put(DatabaseConstant.key_BODY, strBody);
            values.put(DatabaseConstant.key_FROM_USER_ID, strFromUserId);
            values.put(DatabaseConstant.key_FROM_USER_NAME, strFromUserName);
            values.put(DatabaseConstant.key_MESSAGE_DATE, strMsgDate);
            values.put(DatabaseConstant.key_TYPE, strType);
            values.put(DatabaseConstant.key_SUB_TYPE, strSubType);
            values.put(DatabaseConstant.key_PROCESS_ID, strProcessID);
            values.put(DatabaseConstant.key_REPLY_USER_ID, strreplyUserId);
            values.put(DatabaseConstant.key_REPLY_USER_NAME, strreplyUserName);
            return database.insert(MESSAGE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT : Record in Message To User
     ***/
    public long insertMessageToUserData(String strMessageId, String strToUserId, String strReadDate, String strDeleteDate) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_MESSAGE_ID, strMessageId);
            values.put(DatabaseConstant.key_TO_USER_ID, strToUserId);
            values.put(DatabaseConstant.key_READ_DATE, strReadDate);
            values.put(DatabaseConstant.key_DELETE_DATE, strDeleteDate);
            return database.insert(MESSAGE_TO_USER, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT : Record in Message Attachment
     ***/
    public long insertMessageAttachmentData(String strFileId, String strMessageId, String strFileName, String strAttachmentLink, String strUserId, String strReadDate) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_FILE_ID, strFileId);
            values.put(DatabaseConstant.key_MESSAGE_ID, strMessageId);
            values.put(DatabaseConstant.key_FILE_NAME, strFileName);
            values.put(DatabaseConstant.key_ATTACHMENT_LINK, strAttachmentLink);
            values.put(DatabaseConstant.key_USER_ID, strUserId);
            values.put(DatabaseConstant.key_READ_DATE, strReadDate);
            return database.insert(MESSAGE_ATTACHMENT, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO FORCE_CLOSE_DATA
     ***/
    public long insertForceClosedData(String deviceId, String applicationName, String className,
                                      String emailId, String details, String addedDate, String addedIP) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_DEVICE_ID, deviceId);
            values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_APPLICATION_NAME, applicationName);
            values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_CLASS_NAME, className);
            values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_EMAIL_ID, emailId);
            values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_DETAILS, details);
            values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_ADDED_DATE, addedDate);
            values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_ADDED_IP, addedIP);
            return database.insert(FORCE_CLOSE_DATA, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO TRADE_SHIFT
     ***/
    public long insertTradeShiftData(String shiftTradeId, String shiftId, String date,
                                     String ofrUserId, String ofrUserName,
                                     String ofrUserSkillLevel, String comment,
                                     String ofrUserIsOvertime, String ofrShiftStartDatetime,
                                     String ofrShiftEndDateTime, String ofrSplitStartDateTime,
                                     String ofrSplitEndDateTime, String ofrWorkstation,
                                     String reqUserId, String reqUserName,
                                     String reqUserSkillLevel, String reqComment,
                                     String reqUserIsOvertime, String reqShiftStartDateTime,
                                     String reqShiftEndDateTime, String reqSplitStartDateTime,
                                     String reqSplitEndDateTime, String reqWorkstation) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_SHIFT_TRADE_ID, shiftTradeId);
            values.put(DatabaseConstant.key_SHIFT_ID, shiftId);
            values.put(DatabaseConstant.key_DATE, date);
            values.put(DatabaseConstant.key_OFR_USER_ID, ofrUserId);
            values.put(DatabaseConstant.key_OFR_USER_NAME, ofrUserName);
            values.put(DatabaseConstant.key_OFR_USER_SKILL_LEVEL, ofrUserSkillLevel);
            values.put(DatabaseConstant.key_OFR_COMMENT, comment);
            values.put(DatabaseConstant.key_OFR_USER_IS_OVERTIME, ofrUserIsOvertime);
            values.put(DatabaseConstant.key_OFR_SHIFT_START_DATETIME, ofrShiftStartDatetime);
            values.put(DatabaseConstant.key_OFR_SHIFT_END_DATETIME, ofrShiftEndDateTime);
            values.put(DatabaseConstant.key_OFR_SPLIT_START_DATETIME, ofrSplitStartDateTime);
            values.put(DatabaseConstant.key_OFR_SPLIT_END_DATETIME, ofrSplitEndDateTime);
            values.put(DatabaseConstant.key_OFR_WORKSTATION, ofrWorkstation);
            values.put(DatabaseConstant.key_REQ_USER_ID, reqUserId);
            values.put(DatabaseConstant.key_REQ_USER_NAME, reqUserName);
            values.put(DatabaseConstant.key_REQ_USER_SKILL_LEVEL, reqUserSkillLevel);
            values.put(DatabaseConstant.key_REQ_COMMENT, reqComment);
            values.put(DatabaseConstant.key_REQ_USER_IS_OVERTIME, reqUserIsOvertime);
            values.put(DatabaseConstant.key_REQ_SHIFT_START_DATETIME, reqShiftStartDateTime);
            values.put(DatabaseConstant.key_REQ_SHIFT_END_DATETIME, reqShiftEndDateTime);
            values.put(DatabaseConstant.key_REQ_SPLIT_START_DATETIME, reqSplitStartDateTime);
            values.put(DatabaseConstant.key_REQ_SPLIT_END_DATETIME, reqSplitEndDateTime);
            values.put(DatabaseConstant.key_REQ_WORKSTATION, reqWorkstation);
            return database.insert(TRADE_SHIFT, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /***
     * INSERT DATA INTO SHIFT_OFFERING
     ***/
    public long insertShiftOfferingData(String shiftTradeId, String shiftId, String date, String userId,
                                        String userName, String skillLevel,
                                        String isOvertime, String comment,
                                        String shiftStartDateTime, String shiftEndDateTime,
                                        String splitStartDateTime, String splitEndDateTime,
                                        String workstation, String type) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_SHIFT_TRADE_ID, shiftTradeId);
            values.put(DatabaseConstant.key_SHIFT_ID, shiftId);
            values.put(DatabaseConstant.key_DATE, date);
            values.put(DatabaseConstant.key_USER_ID, userId);
            values.put(DatabaseConstant.key_USER_NAME, userName);
            values.put(DatabaseConstant.key_SKILL_LEVEL, skillLevel);
            values.put(DatabaseConstant.key_IS_OVERTIME, isOvertime);
            values.put(DatabaseConstant.key_COMMENT, comment);
            values.put(DatabaseConstant.key_SHIFT_START_DATETIME, shiftStartDateTime);
            values.put(DatabaseConstant.key_SHIFT_END_DATETIME, shiftEndDateTime);
            values.put(DatabaseConstant.key_SPLIT_START_DATETIME, splitStartDateTime);
            values.put(DatabaseConstant.key_SPLIT_END_DATETIME, splitEndDateTime);
            values.put(DatabaseConstant.key_WORKSTATION, workstation);
            values.put(DatabaseConstant.key_TYPE, type);

            return database.insert(SHIFT_OFFERING, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /***
     * INSERT DATA INTO SHIFT REQUEST
     ***/
    public long insertShiftRequestData(String shiftTradeId, String shiftId, String userId,
                                       String userName, String skillLevel,
                                       String comment, String isOvertime, String type) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_SHIFT_TRADE_ID, shiftTradeId);
            values.put(DatabaseConstant.key_SHIFT_ID, shiftId);
            values.put(DatabaseConstant.key_USER_ID, userId);
            values.put(DatabaseConstant.key_USER_NAME, userName);
            values.put(DatabaseConstant.key_SKILL_LEVEL, skillLevel);
            values.put(DatabaseConstant.key_COMMENT, comment);
            values.put(DatabaseConstant.key_IS_OVERTIME, isOvertime);
            values.put(DatabaseConstant.key_TYPE, type);

            return database.insert(SHIFT_REQUEST, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /***
     * INSERT DATA INTO REQUEST OFF
     ***/
    public long insertRequestOffData(String id, String userId, String userName, String skillLevel,
                                     String date, String comment, String leaveDate) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ID, id);
            values.put(DatabaseConstant.key_USER_ID, userId);
            values.put(DatabaseConstant.key_USER_NAME, userName);
            values.put(DatabaseConstant.key_SKILL_LEVEL, skillLevel);
            values.put(DatabaseConstant.key_DATE, date);
            values.put(DatabaseConstant.key_COMMENT, comment);
            values.put(DatabaseConstant.key_LEAVE_DATE, leaveDate);

            return database.insert(REQUEST_OFF, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO METTING
     ***/
    public long insertMeetingData(String meetingId, String title, String MeetingOwner, String IsMandatory, String MeetingDate, String Start_Time, String EndTime, String Location, String Status, String calId) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_MEETING_ID, meetingId);
            values.put(DatabaseConstant.key_TITLE, title);
            values.put(DatabaseConstant.key_MEETING_OWNER, MeetingOwner);
            values.put(DatabaseConstant.key_ISMANDATORY, IsMandatory);
            values.put(DatabaseConstant.key_MEETING_DATE, MeetingDate);
            values.put(DatabaseConstant.key_MEETING_START_TIME, Start_Time);
            values.put(DatabaseConstant.key_MEETING_END_TIME, EndTime);
            values.put(DatabaseConstant.key_MEETING_LOCATION, Location);
            values.put(DatabaseConstant.key_STATUS, Status);
            values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
            return database.insert(MEETING, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO ADDRESS BOOK
     ***/
    public long insertAddressBookData(String userID, String firstName, String lastName, String status, String OrgID,
                                      String positionTitle, String orgName) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_USER_ID, userID);
            values.put(DatabaseConstant.key_FIRST_NAME, firstName);
            values.put(DatabaseConstant.key_LAST_NAME, lastName);
            values.put(DatabaseConstant.key_STATUS, status);
            values.put(DatabaseConstant.key_ORG_ID, OrgID);
            values.put(DatabaseConstant.key_POSITION_TITLE, positionTitle);
            values.put(DatabaseConstant.key_ORG_NAME, orgName);
            return database.insert(ADDRESS_BOOK, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // Check

    /***
     * INSERT DATA INTO LOG_DETAILS
     ***/
    public long insertLogDetailData(String logId, String logDate, String createDate, String createUserId,
                                    String createUserName, String userTypeId, String lastUpdateDate, String status) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_LOG_ID, logId);
            values.put(DatabaseConstant.key_LOG_DATE, logDate);
            values.put(DatabaseConstant.key_CREATE_DATE, createDate);
            values.put(DatabaseConstant.key_CREATE_USER_ID, createUserId);
            values.put(DatabaseConstant.key_CREATE_USER_NAME, createUserName);
            values.put(DatabaseConstant.key_USER_TYPE_ID, userTypeId);
            values.put(DatabaseConstant.key_LAST_UPD_DATE, lastUpdateDate);
            values.put(DatabaseConstant.key_STATUS, status);
            return database.insert(LOG_DETAILS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_PRODUCTIVITY_FIELDS
     ***/
    public long insertLogProductivityFieldData(String fieldId, String fieldTitle, String fieldType, String trackAnnually, String isActive) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_FIELDID, fieldId);
            values.put(DatabaseConstant.key_FIELDTITLE, fieldTitle);
            values.put(DatabaseConstant.key_FIELDTYPE, fieldType);
            values.put(DatabaseConstant.key_TRACKANNUALLY, trackAnnually);
            values.put(DatabaseConstant.key_ISACTIVE, isActive);
            return database.insert(LOG_PRODUCTIVITY_FIELDS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_USERTYPE
     ***/
    public long insertLogUserTypeData(String userTypeId, String userType, String typeDescription, String flagBank, String isActive) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_USERTYPEID, userTypeId);
            values.put(DatabaseConstant.key_USERTYPE, userType);
            values.put(DatabaseConstant.key_TYPEDESCRIPTION, typeDescription);
            values.put(DatabaseConstant.key_FLAGBANK, flagBank);
            values.put(DatabaseConstant.key_ISACTIVE, isActive);
            return database.insert(LOG_USERTYPE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //LOG_MAINTENANCE_LOG	
    //Check

    /***
     * INSERT DATA INTO LOG_COMMUNICATION_DET
     ***/
    public long insertLogCommunicationDetData(String id, String login, String commid, String flagStatus) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ID, id);
            values.put(DatabaseConstant.key_LOGID, login);
            values.put(DatabaseConstant.key_COMMID, commid);
            values.put(DatabaseConstant.key_FLAGSTATUS, flagStatus);
            return database.insert(LOG_COMMUNICATION_DET, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //LOG_COMMUNICATION_DET	
    //Check

    /***
     * INSERT DATA INTO LOG_EMP_RELATION_DESC
     ***/
    public long insertLogEmpRelationDescData(String empLogId, String login, String empFieldId, String employee, String addedById, String description) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_EMPLOGID, empLogId);
            values.put(DatabaseConstant.key_LOGID, login);
            values.put(DatabaseConstant.key_EMPFIELDID, empFieldId);
            values.put(DatabaseConstant.key_EMPLOYEE, employee);
            values.put(DatabaseConstant.key_ADDEDBYID, addedById);
            values.put(DatabaseConstant.key_DESCRIPTION, description);
            return database.insert(LOG_EMP_RELATION_DESC, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // LOG_NOTES	
    //check

    /***
     * INSERT DATA INTO LOG_FOLLOW_UP_LOG
     ***/
    public long insertLogFollowUpData(String id, String login, String followUpId, String flagStatus, String taskId) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ID, id);
            values.put(DatabaseConstant.key_LOGID, login);
            values.put(DatabaseConstant.key_FOLLOWUPID, followUpId);
            values.put(DatabaseConstant.key_FLAGSTATUS, flagStatus);
            values.put(DatabaseConstant.key_TASKID, taskId);
            return database.insert(LOG_FOLLOW_UP_LOG, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // LOG_FOLLOW_UP_LOG	
    //check

    /***
     * INSERT DATA INTO LOG_MAINTENANCE_DET
     ***/
    public long insertLogMaintenanceDetData(String mrrDetailsId, String login, String mrrId, String employee, String description, String taskId) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_MRRDETAILSID, mrrDetailsId);
            values.put(DatabaseConstant.key_LOGID, login);
            values.put(DatabaseConstant.key_MRRID, mrrId);
            values.put(DatabaseConstant.key_EMPLOYEE, employee);
            values.put(DatabaseConstant.key_DESCRIPTION, description);
            values.put(DatabaseConstant.key_TASKID, taskId);
            return database.insert(LOG_MAINTENANCE_DET, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // LOG_MAINTENANCE_DET	
    //Check

    /***
     * INSERT DATA INTO LOG_MAINTENANCE_LOG
     ***/
    public long insertLogMaintenanceLogData(String mrrLogId, String login, String mrrId, String flagstatus) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_MRRLOGID, mrrLogId);
            values.put(DatabaseConstant.key_LOGID, login);
            values.put(DatabaseConstant.key_MRRID, mrrId);
            values.put(DatabaseConstant.key_FLAGSTATUS, flagstatus);
            return database.insert(LOG_MAINTENANCE_LOG, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //Check 

    /***
     * INSERT DATA INTO LOG_NOTES
     ***/
    public long insertLogNotesData(String id, String login, String employee, String description) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ID, id);
            values.put(DatabaseConstant.key_LOGID, login);
            values.put(DatabaseConstant.key_EMPLOYEE, employee);
            values.put(DatabaseConstant.key_DESCRIPTION, description);
            return database.insert(LOG_NOTES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //LOG_EMP_RELATION_DESC	
    //Check

    /***
     * INSERT DATA INTO LOG_PRODUCTIVITY_DET
     ***/
    public long insertLogProductivityDetData(String id, String logId, String fieldId, String fieldValue, String fieldType) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ID, id);
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_FIELDID, fieldId);
            values.put(DatabaseConstant.key_FIELDVALUE, fieldValue);
            values.put(DatabaseConstant.key_FIELDTYPE, fieldType);
            return database.insert(LOG_PRODUCTIVITY_DET, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // LOG_PRODUCTIVITY_DET
    //Check

    /***
     * INSERT DATA INTO LOG_QUALITY
     ***/
    public long insertLogQualityData(String qualityId, String login, String userId, String complaint) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_QUALITYID, qualityId);
            values.put(DatabaseConstant.key_LOGID, login);
            values.put(DatabaseConstant.key_USERID, userId);
            values.put(DatabaseConstant.key_COMPLAINT, complaint);
            return database.insert(LOG_QUALITY, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //	LOG_QUALITY
    // Check

    /***
     * INSERT DATA INTO LOG_SAFETY_DESC
     ***/
    public long insertLogSafetyDescData(String id, String safetyLogin, String employeeId, String description, String addedById) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ID, id);
            values.put(DatabaseConstant.key_SAFETYLOGID, safetyLogin);
            values.put(DatabaseConstant.key_EMPLOYEEID, employeeId);
            values.put(DatabaseConstant.key_DESCRIPTION, description);
            values.put(DatabaseConstant.key_ADDEDBYID, addedById);

            //System.out.println("values==="+values);

            return database.insert(LOG_SAFETY_DESC, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //	LOG_SAFETY_DESC
    //Check

    /***
     * INSERT DATA INTO LOG_SAFETY_LOG
     ***/
    public long insertLogSafetyLogData(String safetyLogId, String login, String safetyId, String flagSafety) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_SAFETYLOGID, safetyLogId);
            values.put(DatabaseConstant.key_LOGID, login);
            values.put(DatabaseConstant.key_SAFETYID, safetyId);
            values.put(DatabaseConstant.key_FLAGSAFETY, flagSafety);
            return database.insert(LOG_SAFETY_LOG, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO GLOBALS
     ***/
    public long insertDownloadFilePath(String key, String value) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_KEY, key);
            values.put(DatabaseConstant.key_VALUE, value);
            return database.insert(GLOBALS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOGIN_QUESTIONS
     ***/
    public long insertLoginQuestionsData(String loginQuestionId, String question) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_LOGIN_QUESTION_ID, loginQuestionId);
            values.put(DatabaseConstant.key_QUESTION, question);
            return database.insert(LOGIN_QUESTIONS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * GET DATA FROM SHIFT_TASK FOR SHIFT TASK LIST
     ***/
    public Cursor getShiftTaskList(String shiftId) {
        String sql = "SELECT * FROM " + SHIFT_TASK + " WHERE " + DatabaseConstant.key_SHIFT_ID + "=" + shiftId;
        //System.out.println(SHIFT_TASK +"******** SQL : "+sql);
        return database.rawQuery(sql, null);
    }

    /***
     * INSERT DATA INTO MY_SHIFTS
     ***/
    public long insertMyShiftData(String shiftId, String Date,
                                  String ShiftStartTime, String ShiftEndTime, String SplitStartTime, String SplitEndTime, String Trainee, String WorkStation, String IsOverTime, String Status, String CalEventId, String StatusOldNew, String Schedule_Name, String Position_Tital) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_SHIFT_ID, shiftId);
            values.put(DatabaseConstant.key_DATE, Date);
            values.put(DatabaseConstant.key_SHIFT_START_TIME, ShiftStartTime);
            values.put(DatabaseConstant.key_SHIFT_END_TIME, ShiftEndTime);
            values.put(DatabaseConstant.key_SPLIT_START_TIME, SplitStartTime);
            values.put(DatabaseConstant.key_SPLIT_END_TIME, SplitEndTime);
            values.put(DatabaseConstant.key_TRAINEE, Trainee);
            values.put(DatabaseConstant.key_WORKSTATION, WorkStation);
            values.put(DatabaseConstant.key_ISOVERTIME, IsOverTime);
            values.put(DatabaseConstant.key_STATUS, Status);
            values.put(DatabaseConstant.key_CAL_EVENT_ID, CalEventId);
            values.put(DatabaseConstant.key_STATUS_OLD_NEW, StatusOldNew);
            values.put(DatabaseConstant.key_SCH_NAME, Schedule_Name);
            values.put(DatabaseConstant.key_POSITION_TITLE, Position_Tital);
            return database.insert(MY_SHIFTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO SHIFTS
     ***/
    public long insertShiftData(String shiftId, String Date, String Phone,
                                String ShiftStartTime, String ShiftEndTime, String SplitStartTime, String SplitEndTime, String Trainee, String WorkStation, String IsOverTime, String Status, String CalEventId, String StatusOldNew, String Schedule_Name, String UserName, String Position_Tital) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_SHIFT_ID, shiftId);
            values.put(DatabaseConstant.key_PHONE, Phone);
            values.put(DatabaseConstant.key_DATE, Date);
            values.put(DatabaseConstant.key_SHIFT_START_TIME, ShiftStartTime);
            values.put(DatabaseConstant.key_SHIFT_END_TIME, ShiftEndTime);
            values.put(DatabaseConstant.key_SPLIT_START_TIME, SplitStartTime);
            values.put(DatabaseConstant.key_SPLIT_END_TIME, SplitEndTime);
            values.put(DatabaseConstant.key_TRAINEE, Trainee);
            values.put(DatabaseConstant.key_WORKSTATION, WorkStation);
            values.put(DatabaseConstant.key_ISOVERTIME, IsOverTime);
            values.put(DatabaseConstant.key_STATUS, Status);
            values.put(DatabaseConstant.key_CAL_EVENT_ID, CalEventId);
            values.put(DatabaseConstant.key_STATUS_OLD_NEW, StatusOldNew);
            values.put(DatabaseConstant.key_SCH_NAME, Schedule_Name);
            values.put(DatabaseConstant.key_USER_NAME, UserName);
            values.put(DatabaseConstant.key_POSITION_TITLE, Position_Tital);
            return database.insert(SHIFTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO SHIFT_TASK
     ***/
    public long insertShiftTaskData(String shiftId, String taskId, String title, String Description) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_SHIFT_ID, shiftId);
            values.put(DatabaseConstant.key_TASK_ID, taskId);
            values.put(DatabaseConstant.key_TITLE, title);
            values.put(DatabaseConstant.key_DESCRIPTION, Description);
            return database.insert(SHIFT_TASK, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LEAVE_REQUEST
     ***/
    public long insertLeaveRequestData(String LeaveId, String userId, String userName, String StartDate, String EndDate, String calId) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_LEAVE_ID, LeaveId);
            values.put(DatabaseConstant.key_USER_ID, userId);
            values.put(DatabaseConstant.key_USER_NAME, userName);
            values.put(DatabaseConstant.key_START_DATE, StartDate);
            values.put(DatabaseConstant.key_END_DATE, EndDate);
            values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
            return database.insert(LEAVE_REQUEST, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO MY_LEAVE_REQUEST
     ***/
    public long insertMyLeaveRequestData(String LeaveId, String userId, String StartDate, String EndDate, String Status, String calId) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_STATUS, Status);
            values.put(DatabaseConstant.key_LEAVE_ID, LeaveId);
            values.put(DatabaseConstant.key_USER_ID, userId);
            values.put(DatabaseConstant.key_START_DATE, StartDate);
            values.put(DatabaseConstant.key_END_DATE, EndDate);
            values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
            return database.insert(MY_LEAVE_REQUEST, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * UPDATE DATA INTO MY_LEAVE_REQUEST
     ***/
    public long updateMyLeaveRequestStatus(String LeaveId, String Status) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_STATUS, Status);
            return database.update(MY_LEAVE_REQUEST, values, DatabaseConstant.key_LEAVE_ID + " = '" + LeaveId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /***
     * GET DATA FROM DOCUMENT FOR SPINNER
     ***/
    public Cursor getDocumentParentDirectories(String orderby, String order) {
        String sql = "SELECT * FROM " + DOCUMENTS + " where " + DatabaseConstant.key_DOCUMENT_PARENT_ID + " = 0 ORDER BY " + orderby + " " + order;
        return database.rawQuery(sql, null);
    }

    /***
     * GET DATA FROM DOCUMENT FOR LIST VIEW
     ***/
    public Cursor getSubDirectory(String parentId, String orderby, String order) {
        String sql = "SELECT * FROM " + DOCUMENTS + " where " + DatabaseConstant.key_DOCUMENT_PARENT_ID + " = " + parentId + " ORDER BY " + orderby + " " + order;
        return database.rawQuery(sql, null);
    }

    public Cursor getUserRecord() {
        return database.rawQuery("select * from " + USERS + " ", null);
    }

    /**
     * Get addressbook records
     **/
    public Cursor getAddressBookRecord() {
        return database.rawQuery("select * FROM " + ADDRESS_BOOK, null); //newly added on 22Jan2016
        //		newly added query as per client request on 22Jan2016
        //		return database.rawQuery("select * ,'1' as SRNo, "  + DatabaseConstant.key_ORG_ID +  " AS FULLNAME " +
        //				" FROM " + ADDRESS_BOOK + " where " + DatabaseConstant.key_USER_ID + " not like '%-0%' " + 
        //				" UNION "
        //				+ " select * ,'2' as SRNo, " + DatabaseConstant.key_FIRST_NAME + " AS FULLNAME " +
        //				" FROM " + ADDRESS_BOOK + " where " + DatabaseConstant.key_USER_ID + " like '%-0%'" + 
        //				" order by SRNo, FULLNAME, " + DatabaseConstant.key_LAST_NAME, null); 

    }

    /**
     * Get Shifts List
     **/
    public Cursor getMyShiftList() {
        return database.rawQuery("select * from " + MY_SHIFTS + " order by " + DatabaseConstant.key_SHIFT_ID + " DESC ", null);
    }

    /**
     * Get My Shift Records
     **/
    public Cursor getMyShiftDetail(String shiftId) {
        return database.rawQuery("select * from " + MY_SHIFTS + " where " + DatabaseConstant.key_SHIFT_ID + " = '" + shiftId + "'", null);
    }

    /**
     * Get Shift Records
     **/
    public Cursor getShiftList() {
        return database.rawQuery("select * from " + SHIFTS + " order by " + DatabaseConstant.key_SHIFT_ID + " DESC ", null);
    }

    /**
     * Get Shifts List
     **/
    public Cursor getShiftDetail(String shiftId) {
        return database.rawQuery("select * from " + SHIFTS + " where " + DatabaseConstant.key_SHIFT_ID + " = '" + shiftId + "'", null);
    }

    /**
     * Get MyLeave Request List
     **/
    public Cursor getAllMyLeaveRequestList() {
        return database.rawQuery("select * from " + MY_LEAVE_REQUEST + " ", null);
    }

    /**
     * Get MyLeave Request Record
     **/
    public Cursor getMyLeaveRequestList(String leaveRequestId) {
        return database.rawQuery("select * from " + MY_LEAVE_REQUEST + " where " + DatabaseConstant.key_LEAVE_ID + " = '" + leaveRequestId + "'", null);
    }

    /**
     * Get MyLeave Request Record by Status
     **/
    public Cursor getMyLeaveRequestbyStatus(String leaveRequestId, String status) {
        return database.rawQuery("select * from " + MY_LEAVE_REQUEST + " where " + DatabaseConstant.key_LEAVE_ID + " = '" + leaveRequestId + "' AND " + DatabaseConstant.key_STATUS + " = '" + status + "'", null);
    }

    /**
     * Get Leave Request List
     **/
    public Cursor getAllLeaveRequestList() {
        return database.rawQuery("select * from " + LEAVE_REQUEST + " ", null);
    }

    /**
     * Get Leave Request Record
     **/
    public Cursor getLeaveRequestList(String leaveRequestId) {
        return database.rawQuery("select * from " + LEAVE_REQUEST + " where " + DatabaseConstant.key_LEAVE_ID + " = '" + leaveRequestId + "'", null);
    }

    /**
     * Delete Meeting Record
     **/
    public int deleteLeaveRecord(String strLeaveID) {
        return database.delete(LEAVE_REQUEST, DatabaseConstant.key_LEAVE_ID + " = " + strLeaveID, null);
    }

    /**
     * Delete Shift Record
     **/
    public int deleteShiftRecord(String strShiftID) {
        return database.delete(SHIFTS, DatabaseConstant.key_SHIFT_ID + " = " + strShiftID, null);
    }

    /**
     * Delete MyShift Record
     **/
    public int deleteMyShiftRecord(String strShiftID) {
        return database.delete(MY_SHIFTS, DatabaseConstant.key_SHIFT_ID + " = " + strShiftID, null);
    }

    /**
     * Update MY_Shift Table
     **/
    public long updateMyShiftRecords(String shiftid, String shiftStart, String shiftEnd, String spiltStart, String splitEnd, String Status, String calId, String Position_Tital) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseConstant.key_SHIFT_START_TIME, shiftStart);
            values.put(DatabaseConstant.key_SHIFT_END_TIME, shiftEnd);
            values.put(DatabaseConstant.key_SPLIT_START_TIME, spiltStart);
            values.put(DatabaseConstant.key_SPLIT_END_TIME, splitEnd);
            values.put(DatabaseConstant.key_STATUS, Status);
            values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
            values.put(DatabaseConstant.key_POSITION_TITLE, Position_Tital);
            return database.update(MY_SHIFTS, values, DatabaseConstant.key_SHIFT_ID + " = '" + shiftid + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Update Shift Table
     **/
    public long updateShiftRecords(String shiftid, String shiftStart, String shiftEnd, String spiltStart, String splitEnd, String Status, String calId, String userName, String Position_Tital) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseConstant.key_SHIFT_START_TIME, shiftStart);
            values.put(DatabaseConstant.key_SHIFT_END_TIME, shiftEnd);
            values.put(DatabaseConstant.key_SPLIT_START_TIME, spiltStart);
            values.put(DatabaseConstant.key_SPLIT_END_TIME, splitEnd);
            values.put(DatabaseConstant.key_STATUS, Status);
            values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
            values.put(DatabaseConstant.key_USER_NAME, userName);
            values.put(DatabaseConstant.key_POSITION_TITLE, Position_Tital);
            return database.update(SHIFTS, values, DatabaseConstant.key_SHIFT_ID + " = '" + shiftid + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * GET DATA FROM MESSAGE, SHIFT_OFFERING, TRADE_SHIFT, REQUEST_OFF FOR MESSAGE LIST
     **/
    public Cursor getMessageList() {
        String sql = "select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'1' as ord from message where type ='B'" +
                " union " +

                "select shift_trade_id,'Shift Give Away Request','','',user_name,DATE," +
                " 'GA','','','','','2' as ord from shift_offering where type = 'GA'" +
                "union " +

                "select shift_trade_id,'Shift Give To Request','','',user_name,DATE," +
                " 'GT','','','','','3' as ord from shift_offering where type = 'GT'" +
                " union " +

                "select shift_trade_id,'Shift Trade With','','',OFR_USER_NAME,DATE," +
                " 'TR','','','','','4' as ord from TRADE_SHIFT" +

                " union " +

                "select id,'Request Off','','',USER_NAME,DATE," +
                " 'R','','','','','5' as ord from REQUEST_OFF" +
                " union " +

                "select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'6' as ord from message where type ='M'" +

                " union " +

                "select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'7' as ord from message where type ='I'" +

                " union " +

                "select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'8' as ord from message where type ='MM'" +

                " union " +

                "select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'9' as ord from message where type <> 'B' and type <> 'M' and type <> 'I' and type <> 'MM'" +

                " order by ord,message_id desc ";

        return database.rawQuery(sql, null);
    }


    /**
     * Get Message Detail
     **/
    public Cursor getMessageDetail(String msgId) {
        return database.rawQuery("select * from " + MESSAGE + " where " + DatabaseConstant.key_MESSAGE_ID + " = '" + msgId + "'", null);
    }

    public Cursor getSelectedMessageDetail(String msgId, String msgType) {
        String sql = "select * from ( select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'1' as ord from message where type ='B'" +
                " union " +

                "select shift_trade_id,'Shift Give Away Request','','',user_name,DATE," +
                " 'GA','','','','','2' as ord from shift_offering where type = 'GA'" +
                "union " +

                "select shift_trade_id,'Shift Give To Request','','',user_name,DATE," +
                " 'GT','','','','','3' as ord from shift_offering where type = 'GT'" +
                " union " +

                "select shift_trade_id,'Shift Trade With','','',OFR_USER_NAME,DATE," +
                " 'TR','','','','','4' as ord from TRADE_SHIFT" +

                " union " +

                "select id,'Request Off','','',USER_NAME,DATE," +
                " 'R','','','','','5' as ord from REQUEST_OFF" +
                " union " +

                "select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'6' as ord from message where type ='M'" +

                " union " +

                "select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'7' as ord from message where type ='I'" +

                " union " +

                "select MESSAGE_ID ,SUBJECT ,BODY ,FROM_USER_ID ,FROM_USER_NAME ,MESSAGE_DATE" +
                " ,TYPE ,SUB_TYPE ,PROCESS_ID ,REPLY_USER_ID ,REPLY_USER_NAME,'8' as ord from message where type <> 'B' and type <> 'M' and type <> 'I'" +

                " order by ord,message_id  ) as tab where message_id = " + msgId + " and type = '" + msgType + "'";


        return database.rawQuery(sql, null);
    }

    /***
     * GET DATA FROM SHIFTS, MEETING & LEAVE_REQUEST FOR SCHEDULE LIST
     ***/
    public Cursor getPostedSchedules(String username) {
        String sql = "SELECT * FROM (SELECT " + DatabaseConstant.key_SHIFT_ID + " AS ID," +
                "\"\"" + " AS TITLE," +
                DatabaseConstant.key_DATE + " AS START_DATE," +
                DatabaseConstant.key_SHIFT_START_TIME + " AS START_TIME," +
                DatabaseConstant.key_SHIFT_END_TIME + " AS END_TIME," +
                DatabaseConstant.key_SPLIT_START_TIME + " AS SPLIT_START_TIME," +
                DatabaseConstant.key_SPLIT_END_TIME + " AS SPLIT_END_TIME," +
                DatabaseConstant.key_TRAINEE + " AS TRAINEE," +
                DatabaseConstant.key_ISOVERTIME + " AS ISOVERTIME," +
                "\"\"" + " AS END_DATE," +
                "'S'" + " AS TYPE," +
                DatabaseConstant.key_STATUS + " AS STATUS" +
                " FROM " + MY_SHIFTS + " where status <> 'NOUSE'" +
                " UNION " +
                "SELECT " + DatabaseConstant.key_MEETING_ID + " AS ID," +
                DatabaseConstant.key_TITLE + " AS TITLE," +
                DatabaseConstant.key_MEETING_DATE + " AS START_DATE," +
                DatabaseConstant.key_MEETING_START_TIME + " AS START_TIME," +
                DatabaseConstant.key_MEETING_END_TIME + " AS END_TIME," +
                "\"\"" + " AS SPLIT_START_TIME," +
                "\"\"" + " AS SPLIT_END_TIME," +
                "\"\"" + " AS TRAINEE," +
                "\"\"" + " AS ISOVERTIME," +
                "\"\"" + " AS END_DATE," +
                "'M'" + " AS TYPE," +
                DatabaseConstant.key_STATUS + " AS STATUS" +
                " FROM " + MEETING +
                " UNION " +
                "SELECT " + DatabaseConstant.key_LEAVE_ID + " AS ID," +
                "\"\"" + " AS TITLE," +
                DatabaseConstant.key_START_DATE + " AS START_DATE," +
                "\"\"" + " AS START_TIME," +
                "\"\"" + " AS END_TIME," +
                "\"\"" + " AS SPLIT_START_TIME," +
                "\"\"" + " AS SPLIT_END_TIME," +
                "\"\"" + " AS TRAINEE," +
                "\"\"" + " AS ISOVERTIME," +
                DatabaseConstant.key_END_DATE + " AS END_DATE," +
                "'R'" + " AS TYPE," +
                DatabaseConstant.key_STATUS + " AS STATUS" +
                " FROM " + MY_LEAVE_REQUEST + ") where CAST(START_DATE AS DATE)  >= CAST(strftime('%Y%m%d') AS DATE)" +
                " ORDER BY CAST(START_DATE AS DATE) ";
        //System.out.println(SHIFTS +"******** SQL : "+sql);
        return database.rawQuery(sql, null);
    }

    /***
     * GET DATA FROM MY_SHIFTS, MEETING & LEAVE_REQUEST FOR SCHEDULE LIST
     ***/
    public Cursor getSelectedShift(String shiftId) {
        String sql = "SELECT * FROM (SELECT " + DatabaseConstant.key_SHIFT_ID + " AS ID," +
                "\"\"" + " AS TITLE," +
                DatabaseConstant.key_DATE + " AS START_DATE," +
                DatabaseConstant.key_SHIFT_START_TIME + " AS START_TIME," +
                DatabaseConstant.key_SHIFT_END_TIME + " AS END_TIME," +
                DatabaseConstant.key_SPLIT_START_TIME + " AS SPLIT_START_TIME," +
                DatabaseConstant.key_SPLIT_END_TIME + " AS SPLIT_END_TIME," +
                DatabaseConstant.key_TRAINEE + " AS TRAINEE," +
                DatabaseConstant.key_ISOVERTIME + " AS ISOVERTIME," +
                "\"\"" + " AS END_DATE," +
                "'S'" + " AS TYPE," +
                DatabaseConstant.key_STATUS + " AS STATUS" +
                " FROM " + MY_SHIFTS + " where status <> 'NOUSE' and status = '' and " + DatabaseConstant.key_SCH_NAME + " = (Select " + DatabaseConstant.key_SCH_NAME + " from MY_SHIFTS where SHIFT_ID = " + shiftId + ")) where CAST(START_DATE AS DATE)  >= CAST(strftime('%Y%m%d') AS DATE)" +
                " ORDER BY CAST(START_DATE AS DATE) ";
        //System.out.println(SHIFTS +"******** SQL : "+sql);
        return database.rawQuery(sql, null);
    }

    // CMD

    /**
     * Get Log Records
     **/
    public Cursor getLogDetailRecords() {
        String sql = "select LOG_ID, LOG_DATE , CREATE_DATE , CREATE_USER_ID , CREATE_USER_NAME , USER_TYPE_ID ," +
                " LAST_UPD_DATE , STATUS from LOG_DETAILS ORDER BY LOG_DATE";

       /* String sql = "select LOG_ID, SUBSTR(LOG_DATE,7,4)||'/'||SUBSTR(LOG_DATE,1,2)||'/'||SUBSTR(LOG_DATE,4,2) AS LOGDT ," +
                " CREATE_DATE , CREATE_USER_ID , CREATE_USER_NAME , USER_TYPE_ID ," +
                " SUBSTR(LAST_UPD_DATE,7,4)||'/'||SUBSTR(LAST_UPD_DATE,1,2)||'/'||SUBSTR(LAST_UPD_DATE,4,2)" +
                " ||' '||SUBSTR(LAST_UPD_DATE,11,9) AS LAST_UPD_DT ," +
                " STATUS from LOG_DETAILS ORDER BY LOGDT desc, LAST_UPD_DT desc";*/

        return database.rawQuery(sql, null);
//        return database.rawQuery("select * from "+LOG_DETAILS, null);
    }

    /**
     * Get all flags
     **/
    public Cursor getLogFlags() {
        String sql = "Select * from LOG_FLAG order by FLAGGEDDATE desc";
        return database.rawQuery(sql, null);
    }

    public Cursor getLogDetailsFromCalDate(String calDate) {
        String sql = "select * from LOG_DETAILS where LOG_DATE='" + calDate + "'";
        return database.rawQuery(sql, null);
    }

    public Cursor getSelectedLogDetail(String logId) {
        String sql = "select * from LOG_DETAILS where " + DatabaseConstant.key_LOG_ID + "=" + logId;
        return database.rawQuery(sql, null);
    }

    public Cursor getLogProductivityFieldRecords() {
        return database.rawQuery("select * from " + LOG_PRODUCTIVITY_FIELDS, null);
    }

    public Cursor getSelectedProductivityDetailRecord(String fieldId, String logId) {
        String sql = "select * from " + LOG_PRODUCTIVITY_DET + " where " + DatabaseConstant.key_FIELDID + "=" + fieldId + " and " + DatabaseConstant.key_LOGID + "=" + logId;
        //System.out.println("PRODUC SQL : "+sql);
        return database.rawQuery(sql, null);
    }

    /**
     * Get Log User Type Records
     **/
    public Cursor getLogUserTypeRecords(String userTypeId) {
        return database.rawQuery("select * from " + LOG_USERTYPE + " where " + DatabaseConstant.key_USERTYPEID + " = '" + userTypeId + "'", null);
    }

    /**
     * Get All Log User Type Records
     **/
    public Cursor getAllUserTypeRecords() {
        return database.rawQuery("select * from " + LOG_USERTYPE + " order by " + DatabaseConstant.key_USERTYPE, null);
    }

    /**
     * Get Log Safety Log Records
     **/
    public Cursor getLogSafetyLogRecords(String logId, String safetyId) {
        String sql = "select * from " + LOG_SAFETY_LOG + " where " + DatabaseConstant.key_LOGID + " = '" + logId + "'" + " and " + DatabaseConstant.key_SAFETYID + " = '" + safetyId + "'";
        //System.out.println("SQL ::: "+sql); 
        return database.rawQuery(sql, null);
    }

    /**
     * Get Log Safety Log Records by logId
     **/
    public Cursor getLogSafetyLogRecordsByLogId(String logId) {
        String sql = "select * from " + LOG_SAFETY_LOG + " where " + DatabaseConstant.key_LOGID + " = '" + logId + "'";
        return database.rawQuery(sql, null);
    }

    /**
     * Get Log Safety Description Records
     **/
    public Cursor getLogSafetyDescRecords(String logId, String safetyId) {
        return database.rawQuery("select *,(select first_name || ' ' || last_name from address_book where user_id like'%-%' and replace(user_id,'-0','') = LOG_SAFETY_DESC.employeeid) as EMPLOYEE_NAME,(select first_name || ' ' || last_name from address_book where user_id like'%-%' and replace(user_id,'-0','') = LOG_SAFETY_DESC.addedbyid) as ADDEDBY_NAME  from " + LOG_SAFETY_DESC + " where " + DatabaseConstant.key_SAFETYLOGID + " in (select SAFETYLOGID from log_safety_log where logid = " + logId + " and safetyid = " + safetyId + ")", null);
    }

    /**
     * Get Log Quality Records
     **/
    public Cursor getLogQualityRecords(String logId) {
        return database.rawQuery("select *,(select first_name || ' ' || last_name from address_book where user_id like'%-%' and replace(user_id,'-0','') = LOG_QUALITY.userid) as ADDEDBY_NAME from " + LOG_QUALITY + " where " + DatabaseConstant.key_LOGID + " = " + logId + "", null);
    }

    /**
     * Get Log Employee Relation Description Records
     **/
    public Cursor getLogEmpRelationDescRecords(String logId, String empFieldId) {
        return database.rawQuery("select *,(select first_name || ' ' || last_name from address_book where user_id like'%-%' and replace(user_id,'-0','') = LOG_EMP_RELATION_DESC.employee) as EMPLOYEE_NAME,(select first_name || ' ' || last_name from address_book where user_id like'%-%' and replace(user_id,'-0','') = LOG_EMP_RELATION_DESC.addedbyid) as ADDEDBY_NAME  from " + LOG_EMP_RELATION_DESC + " where logid = " + logId + " and EMPFIELDID = " + empFieldId, null);
    }

    /**
     * Get Log Communication Detail Records
     **/
    public Cursor getLogCommunicationDetRecords(String logId) {
        return database.rawQuery("select * from " + LOG_COMMUNICATION_DET + " where " + DatabaseConstant.key_LOGID + " = '" + logId + "'", null);
    }

    /**
     * Get Log Maintenance Detail Records
     **/
    public Cursor getLogMaintenanceDetRecords(String logId, String mrrId) {
        return database.rawQuery("select *,(select first_name || ' ' || last_name from address_book where user_id like'%-%' and replace(user_id,'-0','') = LOG_MAINTENANCE_DET.employee) as EMPLOYEE_NAME from " + LOG_MAINTENANCE_DET + " where " + DatabaseConstant.key_LOGID + " = '" + logId + "' and MRRID = " + mrrId, null);
    }

    /**
     * Get Log Maintenance Log Records
     **/
    public Cursor getLogMaintenanceLogRecords(String logId, String mrrId) {
        return database.rawQuery("select * from " + LOG_MAINTENANCE_LOG + " where " + DatabaseConstant.key_LOGID + " = '" + logId + "' and MRRID = " + mrrId, null);
    }

    /**
     * Get Log Follow Up Log Records
     **/
    public Cursor getLogFollowUpLogRecords(String logId, String followUpId) {
        return database.rawQuery("select * from " + LOG_FOLLOW_UP_LOG + " where " + DatabaseConstant.key_LOGID + " = '" + logId + "' and FOLLOWUPID = " + followUpId, null);
    }


    /**
     * Get Log Notes Records
     **/
    public Cursor getLogNoteRecords(String logId) {
        return database.rawQuery("select *,(select first_name || ' ' || last_name from address_book where user_id like'%-%' and replace(user_id,'-0','') = LOG_NOTES.employee) as EMPLOYEE_NAME from " + LOG_NOTES + " where " + DatabaseConstant.key_LOGID + " = '" + logId + "'", null);
    }


    /**
     * Get Message Detail File Attachment List
     **/
    public Cursor getMessageDetailFileAttachmentList(String msgId) {
        return database.rawQuery("select * from " + MESSAGE_ATTACHMENT + " where " + DatabaseConstant.key_MESSAGE_ID + " = '" + msgId + "' and " + DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "'", null);
    }

    /**
     * Get Message Detail File Attachment List BY fileId
     **/
    public Cursor getMessageDetailFileAttachmentListByFileId(String msgId, String fileId) {
        return database.rawQuery("select * from " + MESSAGE_ATTACHMENT + " where " + DatabaseConstant.key_MESSAGE_ID + " = '" + msgId + "' and " + DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "' and " + DatabaseConstant.key_FILE_ID + " = '" + fileId + "'", null);
    }

    /**
     * Get Message TO user List
     **/
    public Cursor getMessageToUserList(String msgId) {
        return database.rawQuery("select * from " + MESSAGE_TO_USER + " where " + DatabaseConstant.key_MESSAGE_ID + " = '" + msgId + "'", null);
    }


    /**
     * GET TRADE SHIFT LIST
     **/
    public Cursor getTradeShiftList(String shiftTradeId) {
        return database.rawQuery("select * from " + TRADE_SHIFT + " where " + DatabaseConstant.key_SHIFT_TRADE_ID + " = '" + shiftTradeId + "'", null);
    }

    /**
     * GET SHIFT OFFERING LIST
     **/
    public Cursor getShiftOfferingList(String shiftTradeId) {
        return database.rawQuery("select * from " + SHIFT_OFFERING + " where " + DatabaseConstant.key_SHIFT_TRADE_ID + " = '" + shiftTradeId + "'", null);
    }


    /**
     * GET SHIFT REQUEST LIST
     **/
    public Cursor getShiftRequestList(String shiftTradeId, String shiftId, String userId) {
        return database.rawQuery("select * from " + SHIFT_REQUEST + " where " + DatabaseConstant.key_SHIFT_TRADE_ID + " = '" + shiftTradeId + "'" +
                " and " + DatabaseConstant.key_SHIFT_ID + " = '" + shiftId + "'" +
                " and " + DatabaseConstant.key_USER_ID + " = '" + userId + "'", null);
    }

    /**
     * GET REQUEST OFF LIST
     **/
    public Cursor getRequestOffList(String id) {
        return database.rawQuery("select * from " + REQUEST_OFF + " where " + DatabaseConstant.key_ID + " = '" + id + "'", null);
    }

    /**
     * GET MEETING LIST
     **/
    public Cursor getMeetingList(String meetingId) {
        return database.rawQuery("select * from " + MEETING + " where " + DatabaseConstant.key_MEETING_ID + " = '" + meetingId + "'", null);
    }

    /**
     * Get Meeting Records
     **/
    public Cursor getMeetingList() {
        return database.rawQuery("select * from " + MEETING + " order by " + DatabaseConstant.key_MEETING_ID + " DESC ", null);
    }

    /**
     * Get Address Book Record
     **/
    public Cursor getAddressBookRecord(String userId, String orgId) {
        return database.rawQuery("select * from " + ADDRESS_BOOK + " where " + DatabaseConstant.key_USER_ID + " = '" + userId + "' and " + DatabaseConstant.key_ORG_ID + " = '" + orgId + "'", null);
    }


    /**
     * Get Log Detail Record
     **/
    public Cursor getLogDetailRecord(String logId) {
        return database.rawQuery("select * from " + LOG_DETAILS + " where " + DatabaseConstant.key_LOG_ID + " = '" + logId + "'", null);
    }

    /**
     * Get Log Flag Record
     **/
    public Cursor getLogFlag(String logId) {
        return database.rawQuery("select * from " + LOG_FLAG + " where " + DatabaseConstant.key_FLAG_LOGID + " = '" + logId + "'", null);
    }

    /**
     * Get Log Detail Record using Log Date
     **/
    public Cursor getLogDetailRecordbyDate_UserId(String logDate, String createUserId) {
        return database.rawQuery("select * from " + LOG_DETAILS + " where " + DatabaseConstant.key_LOG_DATE + " = '" + logDate + "' and " + DatabaseConstant.key_CREATE_USER_ID + " = '" + createUserId + "'", null);
    }


    /**
     * Get Log Productivity Fields Record
     **/
    public Cursor getLogProductivityFieldsRecord(String fieldId) {
        return database.rawQuery("select * from " + LOG_PRODUCTIVITY_FIELDS + " where " + DatabaseConstant.key_FIELDID + " = '" + fieldId + "'", null);
    }

    /**
     * Get Log Usertype Record
     **/
    public Cursor getlogUsertypeRecord(String userTypeId) {
        return database.rawQuery("select * from " + LOG_USERTYPE + " where " + DatabaseConstant.key_USERTYPEID + " = '" + userTypeId + "'", null);
    }

    /**
     * Get Log LOG_COMMUNICATION_DET Record
     **/
    public Cursor getlogCommunicationDetRecord(String id) {
        return database.rawQuery("select * from " + LOG_COMMUNICATION_DET + " where " + DatabaseConstant.key_ID + " = '" + id + "'", null);
    }

    /**
     * Get Log LOG_EMP_RELATION_DESC Record
     **/
    public Cursor getlogEmpRelationDescRecord(String empLogId) {
        return database.rawQuery("select * from " + LOG_EMP_RELATION_DESC + " where " + DatabaseConstant.key_EMPLOGID + " = '" + empLogId + "'", null);
    }

    /**
     * Get Log LOG_FOLLOW_UP_LOG Record
     **/
    public Cursor getlogFollowUpRecord(String id) {
        return database.rawQuery("select * from " + LOG_FOLLOW_UP_LOG + " where " + DatabaseConstant.key_ID + " = '" + id + "'", null);
    }

    /**
     * Get Log LOG_MAINTENANCE_DET Record
     **/
    public Cursor getlogMaintenanceDetRecord(String mrrDetailsId) {
        return database.rawQuery("select * from " + LOG_MAINTENANCE_DET + " where " + DatabaseConstant.key_MRRDETAILSID + " = '" + mrrDetailsId + "'", null);
    }

    /**
     * Get Log LOG_MAINTENANCE_LOG Record
     **/
    public Cursor getlogMaintenanceLogRecord(String mrrLogId) {
        return database.rawQuery("select * from " + LOG_MAINTENANCE_LOG + " where " + DatabaseConstant.key_MRRLOGID + " = '" + mrrLogId + "'", null);
    }

    /**
     * Get Log LOG_NOTES Record
     **/
    public Cursor getlogNotesRecord(String id) {
        return database.rawQuery("select * from " + LOG_NOTES + " where " + DatabaseConstant.key_ID + " = '" + id + "'", null);
    }

    /**
     * Get Log LOG_PRODUCTIVITY_DET Record
     **/
    public Cursor getlogProductivityDetRecord(String id) {
        return database.rawQuery("select * from " + LOG_PRODUCTIVITY_DET + " where " + DatabaseConstant.key_ID + " = '" + id + "'", null);
    }

    /**
     * Get Log LOG_PRODUCTIVITY_DET Record
     **/
    public Cursor getlogProductivityDetRecordByFieldId(String fieldId) {
        return database.rawQuery("select * from " + LOG_PRODUCTIVITY_DET + " where " + DatabaseConstant.key_FIELDID + " = '" + fieldId + "'", null);
    }

    /**
     * Get Log LOG_PRODUCTIVITY_DET Record
     **/
    public Cursor getlogProductivityDetRecordByFieldIdLogId(String fieldId, String logId) {
        return database.rawQuery("select * from " + LOG_PRODUCTIVITY_DET + " where " + DatabaseConstant.key_FIELDID + " = '" + fieldId + "' and " + DatabaseConstant.key_LOGID + " = '" + logId + "'", null);
    }

    /**
     * Get Log LOG_QUALITY Record
     **/
    public Cursor getlogQualityRecord(String qualityId) {
        return database.rawQuery("select * from " + LOG_QUALITY + " where " + DatabaseConstant.key_QUALITYID + " = '" + qualityId + "'", null);
    }

    /**
     * Get Log LOG_SAFETY_DESC Record
     **/
    public Cursor getlogSafetyDescRecord(String id) {
        return database.rawQuery("select * from " + LOG_SAFETY_DESC + " where " + DatabaseConstant.key_ID + " = '" + id + "'", null);
    }

    /**
     * Get Log LOG_SAFETY_LOG Record
     **/
    public Cursor getlogSafetyLogRecord(String safetyLogId) {
        return database.rawQuery("select * from " + LOG_SAFETY_LOG + " where " + DatabaseConstant.key_SAFETYLOGID + " = '" + safetyLogId + "'", null);
    }

    /**
     * Get GlobalsRecords Records
     **/
    public Cursor getGlobalsRecords(String strKey) {
        return database.rawQuery("select * from " + GLOBALS + " where " + DatabaseConstant.key_KEY + " = '" + strKey + "'", null);
    }

    // Get Not read MM Message
    public Cursor getNotReadMandatoryMessage() {
        return database.rawQuery("select MESSAGE_ID from MESSAGE where TYPE = 'MM' and MESSAGE_ID in (select MESSAGE_ID from MESSAGE_TO_USER where READ_DATE = '');", null);
    }

    /***
     * GET DATA FROM FORCE_CLOSE_DATA
     ***/
    public Cursor getForceClosedRecords() {
        String sql = "SELECT * FROM " + FORCE_CLOSE_DATA;
        return database.rawQuery(sql, null);
    }


    /**
     * Get Log Details Records
     **/
    public Cursor getLogDetailsRecords(String userId, String logDate) {
        return database.rawQuery("select * from " + LOG_DETAILS + " where " + DatabaseConstant.key_CREATE_USER_ID + " ='" + userId + "' and " + DatabaseConstant.key_LOG_DATE + " ='" + logDate + "'", null);
    }


    public Cursor getAllLogDetailsRecords() {
        return database.rawQuery("select * from " + LOG_DETAILS, null);
    }


    /**
     * Delete All User Records
     **/
    public void deleteUsersRecords() {
        database.delete(USERS, null, null);
    }


    /**
     * Delete All Documents Records
     **/
    public void deleteAllDocument() {
        database.delete(DOCUMENTS, null, null);
    }

    /**
     * Delete Address Book Records
     **/
    public void deleteAllAddressBook() {
        database.delete(ADDRESS_BOOK, null, null);
    }

    /**
     * Delete Message Records
     **/
    public void deleteAllMessageList() {
        database.delete(MESSAGE, null, null);
    }

    /**
     * Delete All Message To User Records
     **/
    public void deleteAllMessageToUserList() {
        database.delete(MESSAGE_TO_USER, null, null);
    }

    /**
     * Delete All  Message Attachment Records
     **/
    public void deleteAllMessageAttachmentList() {
        database.delete(MESSAGE_ATTACHMENT, null, null);
    }

    /**
     * Delete  All Trade Shift Records
     **/
    public void deleteAllTradeShiftList() {
        database.delete(TRADE_SHIFT, null, null);
    }

    /**
     * Delete All  Shift Offering Records
     **/
    public void deleteAllShiftOfferingList() {
        database.delete(SHIFT_OFFERING, null, null);
    }

    /**
     * Delete  Completed Log Details Records
     **/
    public void deleteCompletedLogDetailsList() {
        database.delete(LOG_DETAILS, DatabaseConstant.key_STATUS + " = 'C'", null);
    }

    /**
     * Delete  All Log Details Records
     **/
    public void deleteAllLogDetailsList() {
        database.delete(LOG_DETAILS, null, null);
    }

    /**
     * Delete  All Log Flag Records
     **/
    public void deleteAllLogFlagsList() {
        database.delete(LOG_FLAG, null, null);
    }

    /**
     * Delete  All LOG_PRODUCTIVITY_FIELDS Records
     **/
    public void deleteAllLogProductivityFieldsList() {
        database.delete(LOG_PRODUCTIVITY_FIELDS, null, null);
    }

    /**
     * Delete  All LOG_USERTYPE Records
     **/
    public void deleteAllLogUserTypeList() {
        database.delete(LOG_USERTYPE, null, null);
    }

    /**
     * Delete  All LOG_COMMUNICATION_DET Records
     **/
    public void deleteAllLogCommunicationDetList() {
        String query = "Delete from LOG_COMMUNICATION_DET where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
        database.execSQL(query);
    }

    //	/** Delete  All LOG_EMP_RELATION_DESC Records **/
    //	public void deleteAllLogEmpRelationList()
    //	{
    //		String query = "Delete from LOG_EMP_RELATION_DESC where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
    //		database.execSQL(query);
    //	}

    /**
     * Delete  All LOG_EMP_RELATION_DESC Records
     **/
    public void deleteAllLogEmpRelationList() {
        String query = "Delete from LOG_EMP_RELATION_DESC ";
        database.execSQL(query);
    }

    //	/** Delete  All LOG_FOLLOW_UP_LOG Records **/
    //	public void deleteAllLogFollowUpList()
    //	{
    //		String query = "Delete from LOG_FOLLOW_UP_LOG where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
    //		database.execSQL(query);
    //	}

    /**
     * Delete  All LOG_FOLLOW_UP_LOG Records
     **/
    public void deleteAllLogFollowUpList() {
        String query = "Delete from LOG_FOLLOW_UP_LOG ";
        database.execSQL(query);
    }

    //	/** Delete  All LOG_MAINTENANCE_DET Records **/
    //	public void deleteAllLogMaintenanceDetList()
    //	{
    //		String query = "Delete from LOG_MAINTENANCE_DET where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
    //		database.execSQL(query);
    //	}	

    /**
     * Delete  All LOG_MAINTENANCE_DET Records
     **/
    public void deleteAllLogMaintenanceDetList() {
        String query = "Delete from LOG_MAINTENANCE_DET ";
        database.execSQL(query);
    }

    //	/** Delete  All LOG_MAINTENANCE_LOG Records **/
    //	public void deleteAllLogMaintenanceLogList()
    //	{
    //		String query = "Delete from LOG_MAINTENANCE_LOG where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
    //		database.execSQL(query);
    //	}

    /**
     * Delete  All LOG_MAINTENANCE_LOG Records
     **/
    public void deleteAllLogMaintenanceLogList() {
        String query = "Delete from LOG_MAINTENANCE_LOG ";
        database.execSQL(query);
    }

    //	/** Delete  All LOG_NOTES Records **/
    //	public void deleteAllLogNotesList()
    //	{
    //		String query = "Delete from LOG_NOTES where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
    //		database.execSQL(query);
    //	}

    /**
     * Delete  All LOG_NOTES Records
     **/
    public void deleteAllLogNotesList() {
        String query = "Delete from LOG_NOTES";
        database.execSQL(query);
    }

    /**
     * Delete  All LOG_PRODUCTIVITY_DET Records
     **/
    //	public void deleteAllLogProductivityDetList()
    //	{
    //		String query = "Delete from LOG_PRODUCTIVITY_DET where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
    //		database.execSQL(query);
    //	}
    public void deleteAllLogProductivityDetList() {
        String query = "delete from LOG_PRODUCTIVITY_DET";
        database.execSQL(query);
    }

    //	/** Delete  All LOG_QUALITY Records **/
    //	public void deleteAllLogQualityList()
    //	{
    //		String query = "delete from LOG_QUALITY where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
    //		database.execSQL(query);
    //	}

    /**
     * Delete  All LOG_QUALITY Records
     **/
    public void deleteAllLogQualityList() {
        String query = "delete from LOG_QUALITY";
        database.execSQL(query);
    }

    //	/** Delete  All LOG_SAFETY_DESC Records **/
    //	public void deleteAllLogSafetyDescList()
    //	{
    //		String query = "Delete from LOG_SAFETY_DESC where SAFETYLOGID in (select SAFETYLOGID from LOG_SAFETY_LOG where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C'))";
    //		database.execSQL(query);
    //	}
    //	

    /**
     * Delete  All LOG_SAFETY_DESC Records
     **/
    public void deleteAllLogSafetyDescList() {
        String query = "Delete from LOG_SAFETY_DESC ";
        database.execSQL(query);
    }

    /**
     * Delete  All LOG_SAFETY_LOG Records
     **/
    public void deleteAllLogSafetyLogList() {
        String query = "Delete from LOG_SAFETY_LOG where LOGID in (select LOG_ID from LOG_DETAILS where status = 'C')";
        database.execSQL(query);
    }

    //	/** Delete  All LOG_SAFETY_LOG Records **/
    //	public void deleteAllLogSafetyLogList()
    //	{
    //		String query = "Delete from LOG_SAFETY_LOG ";
    //		database.execSQL(query);
    //	}


    /**********
     * Delete  Logs Records /////////
     **/

    public void allDeleteLogCommunicationDet() {
        database.delete(LOG_COMMUNICATION_DET, null, null);
    }

    public void allDeleteLogEmpRelation() {
        database.delete(LOG_EMP_RELATION_DESC, null, null);
    }

    public void allDeleteLogFollowUp() {
        database.delete(LOG_FOLLOW_UP_LOG, null, null);
    }

    public void allDeleteLogMaintenanceDet() {
        database.delete(LOG_MAINTENANCE_DET, null, null);
    }

    public void allDeleteLogMaintenanceLog() {
        database.delete(LOG_MAINTENANCE_LOG, null, null);
    }

    public void allDeleteLogNotes() {
        database.delete(LOG_NOTES, null, null);
    }

    public void allDeleteLogProductivityDet() {
        database.delete(LOG_PRODUCTIVITY_DET, null, null);
    }

    public void allDeleteLogQuality() {
        database.delete(LOG_QUALITY, null, null);
    }

    public void allDeleteLogSafetyDesc() {
        database.delete(LOG_SAFETY_DESC, null, null);
    }

    public void allDeleteLogSafetyLog() {
        database.delete(LOG_SAFETY_LOG, null, null);
    }

    public void allDeleteCompletedLogDetails() {
        database.delete(LOG_DETAILS, null, null);
    }

    public void allDeleteLogUserRecord() {
        database.delete(LOG_USERTYPE, null, null);
    }

    //*********************************///


    /**
     * Delete All Request Off Records
     **/
    public void deleteAllRequestOffList() {
        database.delete(REQUEST_OFF, null, null);
    }


    /**
     * Delete All Meeting Records
     **/
    public void deleteAllMeetingList() {
        database.delete(MEETING, null, null);
    }

    /**
     * Delete Shift Records
     **/
    public void deleteAllShiftList() {
        database.delete(SHIFTS, null, null);
    }

    /**
     * Delete Shift Task Records
     **/
    public void deleteAllShiftTaskList() {
        database.delete(SHIFT_TASK, null, null);
    }

    /**
     * Delete Leave request Records
     **/
    public void deleteAllLeaveRequestList() {
        database.delete(LEAVE_REQUEST, null, null);
    }

    /**
     * Delete LOGIN_QUESTIONS
     **/
    public void deleteAllLoginQuestion() {
        database.delete(LOGIN_QUESTIONS, null, null);
    }

    /**
     * Delete  All Shift Request Records
     **/
    public void deleteAllShiftRequestList() {
        database.delete(SHIFT_REQUEST, null, null);
    }


    /**
     * Delete Message Record before meeting record delete
     **/
    public int deleteMsgMeetingRecord(String strMeetingID) {
        return database.delete(MESSAGE, DatabaseConstant.key_PROCESS_ID + " = " + strMeetingID + " and " + DatabaseConstant.key_TYPE + " = 'M'", null);
    }

    /**
     * Delete Meeting Record
     **/
    public int deleteMeetingRecord(String strMeetingID) {
        return database.delete(MEETING, DatabaseConstant.key_MEETING_ID + " = " + strMeetingID, null);
    }

    public int deleteShiftOfferingRecord(String strTradeShiftId) {
        return database.delete(SHIFT_OFFERING, DatabaseConstant.key_SHIFT_TRADE_ID + " = " + strTradeShiftId, null);
    }

    public int deleteShiftRequestRecord(String strTradeShiftId) {
        return database.delete(SHIFT_REQUEST, DatabaseConstant.key_SHIFT_TRADE_ID + " = " + strTradeShiftId, null);
    }

    public int deleteTradeShiftRecord(String strTradeShiftId) {
        return database.delete(TRADE_SHIFT, DatabaseConstant.key_SHIFT_TRADE_ID + " = " + strTradeShiftId, null);
    }

    public int deleteRequestOffRecord(String id) {
        return database.delete(REQUEST_OFF, DatabaseConstant.key_ID + " = " + id, null);
    }

    /**
     * Delete Address Book Record
     **/
    public int deleteAddressBookRecord(String strUserId) {
        return database.delete(ADDRESS_BOOK, DatabaseConstant.key_USER_ID + " = '" + strUserId + "'", null);
    }

    public void deleteShiftRelatedRecords() {
        deleteAllDocument();
        deleteAllTradeShiftList();
        deleteAllShiftOfferingList();
        deleteAllShiftRequestList();
        deleteAllRequestOffList();
        //deleteAllMeetingList();

        //System.out.println("shift related, meeting, document tables...........deleted...");
    }


    /**
     * Update Delete date in message_to_user list Message
     **/
    public long deleteMessageRecords(HashSet<String> msgId, String messageId) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 5);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            System.out.println("messageId==" + messageId);
            if (!messageId.equals("")) {
                ContentValues values = new ContentValues();
                values.put(DatabaseConstant.key_DELETE_DATE, sdf.format(cal.getTime()));
                database.update(MESSAGE_TO_USER, values, DatabaseConstant.key_MESSAGE_ID + " = '" + messageId + "'", null);
            } else {
                Iterator<String> it = msgId.iterator();
                while (it.hasNext()) {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseConstant.key_DELETE_DATE, sdf.format(cal.getTime()));
                    String strMsgId = it.next().toString();

                    database.update(MESSAGE_TO_USER, values, DatabaseConstant.key_MESSAGE_ID + " = '" + strMsgId + "'", null);
                }
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Update MEETING Table
     **/
    public long updateMeetingRecords(String meetingId, String Date, String Start, String end, String Status, String calId) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseConstant.key_MEETING_DATE, Date);
            values.put(DatabaseConstant.key_MEETING_START_TIME, Start);
            values.put(DatabaseConstant.key_MEETING_END_TIME, end);
            values.put(DatabaseConstant.key_STATUS, Status);
            values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
            return database.update(MEETING, values, DatabaseConstant.key_MEETING_ID + " = '" + meetingId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Update Read date in message_to_user list Message
     **/
    public long updateMessageToUserRecords(String msgId) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 5);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            ContentValues values = new ContentValues();
            values.put(DatabaseConstant.key_READ_DATE, sdf.format(cal.getTime()));

            System.out.println("REaddddddddddddddddd date   =============" + values.toString());

            return database.update(MESSAGE_TO_USER, values, DatabaseConstant.key_MESSAGE_ID + " = '" + msgId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Update Last Sync Date In to user Table
     **/
    public long updateUsersRecords(String strDate, String strDocRights, String strLogRights, String strDailyLogHour, String mobileNum, String notifyEmail, String notifySMS, String secQues1, String secQues2, String secQues3, String secAns1, String secAns2, String secAns3, String email, String MandatoryMsgRight) {
        try {
            Calendar cal = Calendar.getInstance();
            ContentValues values = new ContentValues();
            if (!strDate.equals("")) {
                values.put(DatabaseConstant.key_LAST_SYNC_DT, strDate);
            } else {
                values.put(DatabaseConstant.key_LAST_SYNC_DT, ActivityStringInfo.sdfDate.format(cal.getTime()));
            }

            values.put(DatabaseConstant.key_DOC_RIGHTS, strDocRights);
            values.put(DatabaseConstant.key_LOG_RIGHTS, strLogRights);
            values.put(DatabaseConstant.key_DAILY_LOG_HOURS, strDailyLogHour);
            values.put(DatabaseConstant.key_MOBILE_NUM, mobileNum);
            values.put(DatabaseConstant.key_NOTIFY_EMAIL, notifyEmail);
            values.put(DatabaseConstant.key_NOTIFY_SMS, notifySMS);
            values.put(DatabaseConstant.key_SEC_QUES_1, secQues1);
            values.put(DatabaseConstant.key_SEC_QUES_2, secQues2);
            values.put(DatabaseConstant.key_SEC_QUES_3, secQues3);
            values.put(DatabaseConstant.key_SEC_ANS_1, secAns1);
            values.put(DatabaseConstant.key_SEC_ANS_2, secAns2);
            values.put(DatabaseConstant.key_SEC_ANS_3, secAns3);
            values.put(DatabaseConstant.key_EMAIL, email);
            values.put(DatabaseConstant.key_MANDATORYMSGRIGHT, MandatoryMsgRight);

            return database.update(USERS, values, DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Update ADDRESS BOOK Table
     **/
    public long updateAddressBookRecords(String userId, String firstName, String lastName) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseConstant.key_FIRST_NAME, firstName);
            values.put(DatabaseConstant.key_LAST_NAME, lastName);
            //values.put(DatabaseConstant.key_ORG_NAME, orgName);
            return database.update(ADDRESS_BOOK, values, DatabaseConstant.key_USER_ID + " = '" + userId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /***
     * GET DATA FROM SHIFT_OFFERING FOR SHIFT_GIVE_AWAY/ SHIFT_GIVE_TO DETAILS
     ***/
    public Cursor getShiftOfferingDetail(String shiftTradeId) {
        String sql = "SELECT * FROM " + SHIFT_OFFERING + " WHERE " + DatabaseConstant.key_SHIFT_TRADE_ID + "=" + shiftTradeId;
        //System.out.println(SHIFT_OFFERING +"******** SQL : "+sql);
        return database.rawQuery(sql, null);
    }

    /***
     * GET DATA FROM SHIFT_REQUEST FOR SHIFT_GIVE_TO DETAILS
     ***/
    public Cursor getShiftRequestDetail(String shiftTradeId) {
        String sql = "SELECT * FROM " + SHIFT_REQUEST + " WHERE " + DatabaseConstant.key_SHIFT_TRADE_ID + "=" + shiftTradeId;
        //System.out.println(SHIFT_REQUEST +"******** SQL : "+sql);
        return database.rawQuery(sql, null);
    }

    /***
     * GET DATA FROM TRADE_SHIFT FOR SHIFT_GIVE_TO DETAILS
     ***/
    public Cursor getTradeShiftDetail(String shiftTradeId) {
        String sql = "SELECT * FROM " + TRADE_SHIFT + " WHERE " + DatabaseConstant.key_SHIFT_TRADE_ID + "=" + shiftTradeId;
        //System.out.println(TRADE_SHIFT +"******** SQL : "+sql);
        return database.rawQuery(sql, null);
    }

    /***
     * GET DATA FROM REQUEST_OFF FOR REQUEST OFF DETAILS
     ***/
    public Cursor getRequestOffDetail(String id) {
        String sql = "SELECT * FROM " + REQUEST_OFF + " WHERE " + DatabaseConstant.key_ID + "=" + id;
        //System.out.println(REQUEST_OFF +"******** SQL : "+sql);
        return database.rawQuery(sql, null);
    }


    /***
     * GET DATA FROM MEETING FOR MEETING DETAILS
     ***/
    public Cursor getMeetingDetail(String meetingId) {
        String sql = "SELECT * FROM " + MEETING + " WHERE " + DatabaseConstant.key_MEETING_ID + "=" + meetingId;
        //System.out.println(MEETING +"******** SQL : "+sql);
        return database.rawQuery(sql, null);
    }

    /***
     * GET DATA FROM DOCUMENT FOR LIST VIEW
     ***/
    public Cursor getAllDocument() {
        String sql = "SELECT * FROM " + DOCUMENTS;
        return database.rawQuery(sql, null);
    }

    /***
     * GET MAX LOD_ID from Logs details
     ***/
    public Cursor getMaxLogID() {
        //String sql = "SELECT max("+DatabaseConstant.key_LOG_ID+") FROM "+LOG_DETAILS+"";
        String sql = "SELECT (case when ((select max(LOG_ID) FROM LOG_DETAILS) is null) then 0 else (select max(LOG_ID) FROM LOG_DETAILS) end) as LOG_ID";
        return database.rawQuery(sql, null);
    }

    /***
     * GET MAX SafetyLogId from Logs details
     ***/
    public Cursor getMaxSafetyLogId() {
        //String sql = "SELECT max("+DatabaseConstant.key_SAFETYLOGID+") FROM "+LOG_SAFETY_LOG+"";
        String sql = "SELECT (case when ((select max(SAFETYLOGID) FROM LOG_SAFETY_LOG) is null) then 0 else (select max(SAFETYLOGID) FROM LOG_SAFETY_LOG) end) as safetylogid";
        return database.rawQuery(sql, null);
    }

    /***
     * GET MAX SafetyLogId from Logs details
     ***/
    public Cursor getMaxCommDetId() {
        //String sql = "SELECT max("+DatabaseConstant.key_ID+") FROM "+LOG_COMMUNICATION_DET+"";
        String sql = "SELECT (case when ((select max(ID) FROM LOG_COMMUNICATION_DET) is null) then 0 else (select max(ID) FROM LOG_COMMUNICATION_DET) end) as ID";
        return database.rawQuery(sql, null);
    }

    /***
     * GET MAX SafetyLogId from Logs details
     ***/
    public Cursor getMaxMainLogId() {
        //String sql = "SELECT max("+DatabaseConstant.key_MRRLOGID+") FROM "+LOG_MAINTENANCE_LOG+"";
        String sql = "SELECT (case when ((select max(MRRLOGID) FROM LOG_MAINTENANCE_LOG) is null) then 0 else (select max(MRRLOGID) FROM LOG_MAINTENANCE_LOG) end) as MRRLOGID";
        return database.rawQuery(sql, null);
    }

    /***
     * GET MAX SafetyLogId from Logs details
     ***/
    public Cursor getMaxFollowUpLogId() {
        //String sql = "SELECT max("+DatabaseConstant.key_ID+") FROM "+LOG_FOLLOW_UP_LOG+"";
        String sql = "SELECT (case when ((select max(ID) FROM LOG_FOLLOW_UP_LOG) is null) then 0 else (select max(ID) FROM LOG_FOLLOW_UP_LOG) end) as ID";
        return database.rawQuery(sql, null);
    }

    /***
     * GET Incomplete record from Logs details
     ***/
    public Cursor getIncompleteLogDetailRecord() {
        String sql = "SELECT * FROM " + LOG_DETAILS + " where " + DatabaseConstant.key_STATUS + " = 'I'";
        return database.rawQuery(sql, null);
    }

    /***
     * GET Updated record from Logs details
     ***/
    public Cursor getUpdatedLogDetailRecord() {
        String sql = "SELECT * FROM " + LOG_DETAILS + " where " + DatabaseConstant.key_STATUS + " = 'U'";
        return database.rawQuery(sql, null);
    }

    /***
     * GET Pending record from Logs details
     ***/
    public Cursor getPendingLogDetailRecord() {
        String sql = "SELECT * FROM " + LOG_DETAILS + " where " + DatabaseConstant.key_STATUS + " = 'P'";
        return database.rawQuery(sql, null);
    }

    /***
     * GET Pending Inserted record from Logs details
     ***/
    public Cursor getPendingInsLogDetailRecord() {
        String sql = "SELECT * FROM " + LOG_DETAILS + " where " + DatabaseConstant.key_STATUS + " = 'PI'";
        return database.rawQuery(sql, null);
    }

    /***
     * GET Pending Updated record from Logs details
     ***/
    public Cursor getPendingUpdLogDetailRecord() {
        String sql = "SELECT * FROM " + LOG_DETAILS + " where " + DatabaseConstant.key_STATUS + " = 'PU'";
        return database.rawQuery(sql, null);
    }

    /***
     * GET Incomplete record from Logs details
     ***/
    public Cursor getCommunicationLogsRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_COMMUNICATION_DET + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }

    /***
     * GET employee relation record from LOG_EMP_RELATION_DESC
     ***/
    public Cursor getLogEmpRelationDescRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_EMP_RELATION_DESC + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }

    /***
     * GET record from LOG_FOLLOW_UP_LOG
     ***/
    public Cursor getLogFollowUpRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_FOLLOW_UP_LOG + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }


    /***
     * GET record from LOG_MAINTENANCE_DET
     ***/
    public Cursor getLogMainDetRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_MAINTENANCE_DET + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }

    /***
     * GET record from LOG_MAINTENANCE_Log
     ***/
    public Cursor getLogMainLogRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_MAINTENANCE_LOG + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }

    /***
     * GET record from LOG_NOTES
     ***/
    public Cursor getLogNotesRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_NOTES + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }

    /***
     * GET record from LOG_PRODUCTIVITY_DET
     ***/
    public Cursor getLogProduDelRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_PRODUCTIVITY_DET + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }

    /***
     * GET record from LOG_QUALITY
     ***/
    public Cursor getLogQualityRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_QUALITY + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }

    /***
     * GET record from LOG_SAFETY_DESC
     ***/
    public Cursor getLogSafetyDecRecord(String safetyLogId) {
        String sql = "SELECT * FROM " + LOG_SAFETY_DESC + " where " + DatabaseConstant.key_SAFETYLOGID + " = '" + safetyLogId + "'";
        return database.rawQuery(sql, null);
    }

    /***
     * GET record from LOG_SAFETY_LOG
     ***/
    public Cursor getLogSafetyLogRecord(String logId) {
        String sql = "SELECT * FROM " + LOG_SAFETY_LOG + " where " + DatabaseConstant.key_LOGID + " = " + logId;
        return database.rawQuery(sql, null);
    }


    /**************************Insert Logs Details*********************************/


    /***
     * INSERT DATA INTO LOG_COMMUNICATION_DET
     ***/
    public long insertLogCommunicationDet(String id, String logId, String commId, String flagStatus) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ID, id);
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_COMMID, commId);
            values.put(DatabaseConstant.key_FLAGSTATUS, flagStatus);
            //System.out.println("values====="+values);
            return database.insert(LOG_COMMUNICATION_DET, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_DETAILS
     ***/
    public long insertLogDetails(String logId, String lodDate, String createDate, String createUserId,
                                 String createUserName, String userTypeId, String lastUpdDate, String status) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_LOG_ID, logId);
            values.put(DatabaseConstant.key_LOG_DATE, lodDate);
            values.put(DatabaseConstant.key_CREATE_DATE, createDate);
            values.put(DatabaseConstant.key_CREATE_USER_ID, createUserId);
            values.put(DatabaseConstant.key_CREATE_USER_NAME, createUserName);
            values.put(DatabaseConstant.key_USER_TYPE_ID, userTypeId);
            values.put(DatabaseConstant.key_LAST_UPD_DATE, lastUpdDate);
            values.put(DatabaseConstant.key_STATUS, status);
            return database.insert(LOG_DETAILS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_EMP_RELATION_DESC
     ***/
    public long insertLogEmpRelationDesc(String empLogId, String logId, String empFieldId, String employee,
                                         String addedById, String description) {
        ContentValues values = new ContentValues();
        try {
            //values.put(DatabaseConstant.key_EMPLOGID, empLogId);   
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_EMPFIELDID, empFieldId);
            values.put(DatabaseConstant.key_EMPLOYEE, employee);
            values.put(DatabaseConstant.key_ADDEDBYID, addedById);
            values.put(DatabaseConstant.key_DESCRIPTION, description);
            return database.insert(LOG_EMP_RELATION_DESC, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_FOLLOW_UP_LOG
     ***/
    public long insertLogFollowUpLog(String id, String logId, String followUpId, String flagStatus, String taskId) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_ID, id);
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_FOLLOWUPID, followUpId);
            values.put(DatabaseConstant.key_FLAGSTATUS, flagStatus);
            values.put(DatabaseConstant.key_TASKID, taskId);
            return database.insert(LOG_FOLLOW_UP_LOG, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_MAINTENANCE_DET
     ***/
    public long insertLogMaintenanceDet(String mrrDetailId, String logId, String mrrId, String employee,
                                        String description, String taskId) {
        ContentValues values = new ContentValues();
        try {
            //values.put(DatabaseConstant.key_MRRDETAILSID, mrrDetailId);
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_MRRID, mrrId);
            values.put(DatabaseConstant.key_EMPLOYEE, employee);
            values.put(DatabaseConstant.key_DESCRIPTION, description);
            values.put(DatabaseConstant.key_TASKID, taskId);
            return database.insert(LOG_MAINTENANCE_DET, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_MAINTENANCE_LOG
     ***/
    public long insertLogMaintenanceLog(String mrrLogId, String logId, String mrrId, String flagStatus) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_MRRLOGID, mrrLogId);
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_MRRID, mrrId);
            values.put(DatabaseConstant.key_FLAGSTATUS, flagStatus);
            return database.insert(LOG_MAINTENANCE_LOG, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_NOTES
     ***/
    public long insertLogNotes(String id, String logId, String employee, String description) {
        ContentValues values = new ContentValues();
        try {
            //values.put(DatabaseConstant.key_ID, id);         
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_EMPLOYEE, employee);
            values.put(DatabaseConstant.key_DESCRIPTION, description);
            return database.insert(LOG_NOTES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_PRODUCTIVITY_DET
     ***/
    public long insertLogProductivityDet(String id, String logId, String fieldId, String fieldValue, String fieldType) {
        ContentValues values = new ContentValues();
        try {
            //values.put(DatabaseConstant.key_ID, id);        
            values.put(DatabaseConstant.key_LOGID, logId);

            values.put(DatabaseConstant.key_FIELDID, fieldId);
            values.put(DatabaseConstant.key_FIELDVALUE, fieldValue);
            values.put(DatabaseConstant.key_FIELDTYPE, fieldType);

            if (logId.equals("3299")) {
                Log.v("DatabaseHelper", " logid " + logId + " fieldvalue ");

            }
            return database.insert(LOG_PRODUCTIVITY_DET, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_QUALITY
     ***/
    public long insertLogQuality(String qualityId, String logId, String userId, String complaint) {
        ContentValues values = new ContentValues();
        try {
            //values.put(DatabaseConstant.key_QUALITYID, qualityId);
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_USERID, userId);
            values.put(DatabaseConstant.key_COMPLAINT, complaint);
            //System.out.println("valuesQuality===="+values);
            return database.insert(LOG_QUALITY, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_SAFETY_DESC
     ***/
    public long insertLogSafetyDesc(String id, String safetyLogId, String employeeId, String description, String addedById) {
        ContentValues values = new ContentValues();
        try {
            //values.put(DatabaseConstant.key_ID, id);         
            values.put(DatabaseConstant.key_SAFETYLOGID, safetyLogId);
            values.put(DatabaseConstant.key_EMPLOYEEID, employeeId);
            values.put(DatabaseConstant.key_DESCRIPTION, description);
            values.put(DatabaseConstant.key_ADDEDBYID, addedById);
            //	System.out.println("values===="+values);
            return database.insert(LOG_SAFETY_DESC, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_SAFETY_LOG
     ***/
    public long insertLogSafetyLog(String safetyLogId, String logId, String safetyId, String flagSafety) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_SAFETYLOGID, safetyLogId);
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_SAFETYID, safetyId);
            values.put(DatabaseConstant.key_FLAGSAFETY, flagSafety);
            //	System.out.println("values====*****"+values);
            return database.insert(LOG_SAFETY_LOG, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_USERTYPE
     ***/
    public long insertLogUserType(String userTypeId, String userType, String typeDescription, String flagBank, String isActive) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_USERTYPEID, userTypeId);
            values.put(DatabaseConstant.key_USERTYPE, userType);
            values.put(DatabaseConstant.key_TYPEDESCRIPTION, typeDescription);
            values.put(DatabaseConstant.key_FLAGBANK, flagBank);
            values.put(DatabaseConstant.key_ISACTIVE, isActive);
            return database.insert(LOG_USERTYPE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * INSERT DATA INTO LOG_FLAGS
     ***/
    public long insertLogFlags(String flagDesc, String flag, String flaggedDate, String flagId, String flagLogId,
                               String flagUserId, String userName) {
        ContentValues values = new ContentValues();
        long id = 0;
        try {
            values.put(DatabaseConstant.key_FLAG_DESCRIPTION, flagDesc);
            values.put(DatabaseConstant.key_FLAG, flag);
            values.put(DatabaseConstant.key_FLAGGEDDATE, flaggedDate);
            values.put(DatabaseConstant.key_FLAG_ID, flagId);
            values.put(DatabaseConstant.key_FLAG_LOGID, flagLogId);
            values.put(DatabaseConstant.key_FLAG_USERID, flagUserId);
            values.put(DatabaseConstant.key_FLAG_USERNAME, userName);
            id = database.insert(LOG_FLAG, null, values);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("insertLogFlags error " + e.getMessage().toString());

        }
        return id;
    }


    /**************************Update Logs Details*********************************/

    /***
     * INSERT DATA INTO LOG_FLAGS
     ***/
    public long updateLogFlags(String flagLogId, String flag, String flagDesc) {
        ContentValues values = new ContentValues();

        try {
            values.put(DatabaseConstant.key_FLAG_DESCRIPTION, flagDesc);
            values.put(DatabaseConstant.key_FLAG, flag);
            return database.update(LOG_FLAG, values, DatabaseConstant.key_FLAG_LOGID + " = '" + flagLogId + "'", null);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage().toString());
            return 0;
        }

    }


    /***
     * Update DATA INTO LOG_SAFETY_LOG
     ***/
    public long updateLogSafetyLog(String safetyLogId, String logId, String safetyId, String flagSafety) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_FLAGSAFETY, flagSafety);
            return database.update(LOG_SAFETY_LOG, values, DatabaseConstant.key_LOGID + " = '" + logId + "'" + " and " + DatabaseConstant.key_SAFETYLOGID + " = '" + safetyLogId + "'" + " and " + DatabaseConstant.key_SAFETYID + " = '" + safetyId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /***
     * UPDATE DATA INTO LOG_DETAILS
     ***/
    public long updateLogDetails(String logId, String userTypeId, String status) {
        Calendar cal = Calendar.getInstance();
        ContentValues values = new ContentValues();
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            values.put(DatabaseConstant.key_USER_TYPE_ID, userTypeId);
            values.put(DatabaseConstant.key_LAST_UPD_DATE, sdfDate.format(cal.getTime()));
            values.put(DatabaseConstant.key_STATUS, status);
            return database.update(LOG_DETAILS, values, DatabaseConstant.key_LOG_ID + " = " + logId, null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * UPDATE DATA INTO LOG_COMMUNICATION_DET
     ***/
    public long updateLogCommunicationDet(String logId, String commId, String flagStatus) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_FLAGSTATUS, flagStatus);
            return database.update(LOG_COMMUNICATION_DET, values, DatabaseConstant.key_LOGID + " = " + logId + " and " + DatabaseConstant.key_COMMID + " = '" + commId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * UPDATE DATA INTO LOG_MAINTENANCE_LOG
     ***/
    public long updateLogMaintenanceLog(String logId, String mrrId, String flagStatus) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_FLAGSTATUS, flagStatus);
            return database.update(LOG_MAINTENANCE_LOG, values, DatabaseConstant.key_LOGID + " = " + logId + " and " + DatabaseConstant.key_MRRID + " = '" + mrrId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * UPDATE DATA INTO LOG_FOLLOW_UP_LOG
     ***/
    public long updateLogFollowUpLog(String logId, String followUpId, String flagStatus) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_FLAGSTATUS, flagStatus);
            return database.update(LOG_FOLLOW_UP_LOG, values, DatabaseConstant.key_LOGID + " = " + logId + " and " + DatabaseConstant.key_FOLLOWUPID + " = '" + followUpId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /***
     * UPDATE DATA INTO LOG_PRODUCTIVITY_DET
     ***/
    public long updateLogProductivityDet(String logId, String fieldId, String fieldValue) {
        ContentValues values = new ContentValues();
        try {
            values.put(DatabaseConstant.key_LOGID, logId);
            values.put(DatabaseConstant.key_FIELDID, fieldId);
            values.put(DatabaseConstant.key_FIELDVALUE, fieldValue);
            System.out.println("values=====" + values.toString());
            return database.update(LOG_PRODUCTIVITY_DET, values, DatabaseConstant.key_LOGID + " = " + logId + " and " + DatabaseConstant.key_FIELDID + " = '" + fieldId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /*************************
     * Delete logs record
     *********************************/

	/* delete safety record id wise */
    public int deleteLogSafetyDesc(String Id) {
        return database.delete(LOG_SAFETY_DESC, DatabaseConstant.key_ID + " = " + Id, null);
    }

    /**
     * Delete Quality Record
     **/
    public int deleteLogQuality(String Id) {
        return database.delete(LOG_QUALITY, DatabaseConstant.key_QUALITYID + " = " + Id, null);
    }

    /**
     * Delete LogEmpRelationDesc Record
     **/
    public int deleteLogEmpRelationDesc(String Id) {
        return database.delete(LOG_EMP_RELATION_DESC, DatabaseConstant.key_EMPLOGID + " = " + Id, null);
    }

    /***
     * INSERT DATA INTO LOG_MAINTENANCE_DET
     ***/
    public long deleteLogMaintenanceDet(String Id) {
        return database.delete(LOG_MAINTENANCE_DET, DatabaseConstant.key_MRRDETAILSID + " = " + Id, null);
    }

    /***
     * INSERT DATA INTO LOG_MAINTENANCE_DET
     ***/
    public long deleteLogNotes(String Id) {
        return database.delete(LOG_NOTES, DatabaseConstant.key_ID + " = " + Id, null);
    }


    /***
     * UPDATE DATA INTO LOG_DETAILS
     ***/
    public long updateLogDetailsOnSync(String TypeId, String status) {
        ContentValues values = new ContentValues();
        values.put(DatabaseConstant.key_STATUS, status);
        return database.update(LOG_DETAILS, values, DatabaseConstant.key_STATUS + " = '" + TypeId + "'", null);
    }

    /***
     * GET DATA FROM MESSAGE LIST FOR DELETE MESSAGE
     ***/
    public Cursor getDeleteMsgId(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        SimpleDateFormat sdfparse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String sql = "";
        try {
            sql = "SELECT " + DatabaseConstant.key_MESSAGE_ID + "," + DatabaseConstant.key_DELETE_DATE + " FROM " + MESSAGE_TO_USER + " where datetime(" + DatabaseConstant.key_DELETE_DATE + ")  >= datetime('" + sdfparse.format(sdf.parse(date)) + "')";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return database.rawQuery(sql, null);
    }

    /***
     * GET DATA FROM MESSAGE LIST FOR READ MESSAGE
     ***/
    public Cursor getReadMsgId(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        SimpleDateFormat sdfparse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String sql = "";
        try {
            sql = "SELECT " + DatabaseConstant.key_MESSAGE_ID + "," + DatabaseConstant.key_READ_DATE + " FROM " + MESSAGE_TO_USER + " where datetime(" + DatabaseConstant.key_READ_DATE + ")  >= datetime('" + sdfparse.format(sdf.parse(date)) + "')";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return database.rawQuery(sql, null);
    }


    ////////*******Insert Update for Contact,Security and Password**********/////////

    /**
     * Get LOGIN_QUESTIONS Records
     **/
    public Cursor getLoginQuestionsRecords() {
        return database.rawQuery("select * from " + LOGIN_QUESTIONS, null);
    }

    /**
     * Get Login Questions List
     **/
    public Cursor getLoginQuestionsList(String loginQuestionId) {
        return database.rawQuery("select * from " + LOGIN_QUESTIONS + " where " + DatabaseConstant.key_LOGIN_QUESTION_ID + " = '" + loginQuestionId + "'", null);
    }

    /***
     * INSERT DATA INTO USERS For Myinfo Detail
     ***/
    public long updateQuestionAnswer(String id, String strQuestion, String StrAnswer) {
        ContentValues values = new ContentValues();
        if (id.equals("1")) {
            values.put(DatabaseConstant.key_SEC_QUES_1, strQuestion);
            values.put(DatabaseConstant.key_SEC_ANS_1, StrAnswer);
        }
        if (id.equals("2")) {
            values.put(DatabaseConstant.key_SEC_QUES_2, strQuestion);
            values.put(DatabaseConstant.key_SEC_ANS_2, StrAnswer);
        }
        if (id.equals("3")) {
            values.put(DatabaseConstant.key_SEC_QUES_3, strQuestion);
            values.put(DatabaseConstant.key_SEC_ANS_3, StrAnswer);
        }
        return database.update(USERS, values, DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "'", null);
    }

    /**
     * Update Shift Table
     **/
    public long updateContactInfo(String strEmail, String strNotifyByEmail, String strMobileNum, String strNotifyBySMS) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseConstant.key_EMAIL, strEmail);
            values.put(DatabaseConstant.key_NOTIFY_EMAIL, strNotifyByEmail);
            values.put(DatabaseConstant.key_MOBILE_NUM, strMobileNum);
            values.put(DatabaseConstant.key_NOTIFY_SMS, strNotifyBySMS);
            return database.update(USERS, values, DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Update Status to user Table
     **/
    public long updateUsersRecordsForStatus(String strFeild, String strStatus) {
        try {
            ContentValues values = new ContentValues();
            values.put(strFeild, strStatus);
            return database.update(USERS, values, DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /********************* Mandatory Message *********************/

    /***
     * UPDATE DATA INTO Message Attachment
     ***/
    public long updateMessageAttachmentReadDate(String strMsgId, String strFileId) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
        ContentValues values = new ContentValues();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        values.put(DatabaseConstant.key_READ_DATE, sdfDate.format(cal.getTime()));
        return database.update(MESSAGE_ATTACHMENT, values, DatabaseConstant.key_MESSAGE_ID + " = '" + strMsgId + "'" + " and " + DatabaseConstant.key_FILE_ID + " = '" + strFileId + "'" + " and " + DatabaseConstant.key_USER_ID + " = '" + ActivityStringInfo.strUser_id + "'", null);
    }

    /***
     * GET DATA FROM MESSAGE LIST FOR DELETE MESSAGE
     ***/
    public Cursor getMsgAttchmentReadId(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        SimpleDateFormat sdfparse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String sql = "";
        try {
            sql = "SELECT * FROM " + MESSAGE_ATTACHMENT + " where datetime(" + DatabaseConstant.key_READ_DATE + ")  >= datetime('" + sdfparse.format(sdf.parse(date)) + "')";
            //			System.out.println("sql==="+sql);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return database.rawQuery(sql, null);
    }

}
