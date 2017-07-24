package ism.android.utils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

import ism.android.ActivityStringInfo;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static String DB_PATH = "/data/data/ism.android/databases/";

	private static String DB_NAME = "ism.db";
	private static int DB_VERSION = 1;

	private SQLiteDatabase database;

	Context myContext;
	DatabaseConstant dbConstant;

	//Table Name
	public static final String TABLE_NAME = "TABLE_UPDATE_INFO";

	public static final String DOCUMENTS = "DOCUMENTS";
	public static final String USERS = "USERS";
	public static final String MESSAGE = "MESSAGE";
	public static final String MESSAGE_TO_USER = "MESSAGE_TO_USER";
	public static final String MESSAGE_ATTACHMENT = "MESSAGE_ATTACHMENT";
	public static final String FORCE_CLOSE_DATA = "FORCE_CLOSE_DATA";

	public static final String SHIFTS = "SHIFTS";
	public static final String SHIFT_TASK = "SHIFT_TASK";
	public static final String MEETING = "MEETING";
	public static final String LEAVE_REQUEST = "LEAVE_REQUEST";
	public static final String ADDRESS_BOOK = "ADDRESS_BOOK";
	public static final String GLOBALS = "GLOBALS";
	public static final String LOGIN_QUESTIONS = "LOGIN_QUESTIONS";


	public DatabaseHelper(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
		dbConstant = new DatabaseConstant(context);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	}

	public void createDataBase() throws IOException
	{
		boolean dbExist = checkDataBase();

		if(dbExist)
		{
			//do nothing - database already exist
		}
		else
		{
			this.getReadableDatabase();
			try
			{
				System.out.println("NEW DATABASE CREATED");
				copyDataBase();
			}
			catch (IOException e)
			{
				throw new Error("Error copying database");
			}
		}
	}

	private boolean checkDataBase()
	{
		String myPath = DB_PATH + DB_NAME;
		try
		{
			File file = new File(myPath);
			return file.exists();
		}
		catch (Exception e)
		{
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

	private void copyDataBase() throws IOException
	{
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;

		OutputStream myOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];

		int length;
		while ((length = myInput.read(buffer))>0)
		{
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException
	{
		try
		{
			String mPath = DB_PATH + DB_NAME;
			database = SQLiteDatabase.openDatabase(mPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void close()
	{
		try
		{
			if (database != null)
				database.close();
			SQLiteDatabase.releaseMemory();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		super.close();
	}

	/*** INSERT : Record in Documents ***/
	public long insertDocumentRecords(String docId,String parentId, String docName,String docType, String docFileLink)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_DOCUMENT_ID, docId);
			values.put(DatabaseConstant.key_DOCUMENT_PARENT_ID, parentId);
			values.put(DatabaseConstant.key_DOCUMENT_NAME, docName);
			values.put(DatabaseConstant.key_DOCUMENT_TYPE, docType);
			values.put(DatabaseConstant.key_DOCUMENT_FILE_LINK, docFileLink);

			return database.insert(DOCUMENTS, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}

	}

	/*** INSERT : Record in USERS ***/
	public long insertUserData()
	{
		ContentValues values = new ContentValues();
		try
		{
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
			values.put(DatabaseConstant.key_CAL_NAME, ActivityStringInfo.strCalIdName);
			values.put(DatabaseConstant.key_LAST_SYNC_DT, "");
			values.put(DatabaseConstant.key_POSITION_TITLE, ActivityStringInfo.strPositionTitle);
			values.put(DatabaseConstant.key_ACCEPTTERMS, ActivityStringInfo.strAcceptTerms);
			values.put(DatabaseConstant.key_AUTOGENPWD, ActivityStringInfo.strAutoGenPwd);
			return database.insert(USERS, null, values);
		}
		catch (Exception e)
		{
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

	/*** INSERT : Record in Message ***/
	public long insertMessageData(String strMessageId,String strSubject,String strBody,String strFromUserId, String strFromUserName, String strMsgDate,String strType,String strSubType,String strProcessID,String strreplyUserId,String strreplyUserName)
	{
		ContentValues values = new ContentValues();
		try
		{
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT : Record in Message To User ***/
	public long insertMessageToUserData(String strMessageId,String strToUserId,String strReadDate,String strDeleteDate)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_MESSAGE_ID, strMessageId);
			values.put(DatabaseConstant.key_TO_USER_ID, strToUserId);
			values.put(DatabaseConstant.key_READ_DATE, strReadDate);
			values.put(DatabaseConstant.key_DELETE_DATE, strDeleteDate);
			return database.insert(MESSAGE_TO_USER, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT : Record in Message Attachment ***/
	public long insertMessageAttachmentData(String strFileId,String strMessageId,String strFileName,String strAttachmentLink,String strUserId,String strReadDate)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_FILE_ID, strFileId);
			values.put(DatabaseConstant.key_MESSAGE_ID, strMessageId);
			values.put(DatabaseConstant.key_FILE_NAME, strFileName);
			values.put(DatabaseConstant.key_ATTACHMENT_LINK, strAttachmentLink);
			values.put(DatabaseConstant.key_USER_ID, strUserId);
			values.put(DatabaseConstant.key_READ_DATE, strReadDate);
			//	System.out.println("values==="+values);
			return database.insert(MESSAGE_ATTACHMENT, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT DATA INTO FORCE_CLOSE_DATA ***/
	public long insertForceClosedData(String deviceId, String applicationName, String className,
			String emailId, String details, String addedDate, String addedIP)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_DEVICE_ID, deviceId);
			values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_APPLICATION_NAME, applicationName);
			values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_CLASS_NAME, className);
			values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_EMAIL_ID, emailId);
			values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_DETAILS, details);
			values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_ADDED_DATE, addedDate);
			values.put(DatabaseConstant.key_FORCE_CLOSE_DATA_ADDED_IP, addedIP);
			return database.insert(FORCE_CLOSE_DATA, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT DATA INTO SHIFTS ***/
	public long insertShiftData(String shiftId, String Date,
			String ShiftStartTime, String ShiftEndTime, String SplitStartTime, String SplitEndTime, String Trainee, String WorkStation,String IsOverTime,String Status, String CalEventId, String StatusOldNew,String Schedule_Name,String Position_Tital)
	{
		ContentValues values = new ContentValues();
		try
		{
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
			return database.insert(SHIFTS, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}

	}

	/*** INSERT DATA INTO LEAVE_REQUEST ***/
	public long insertLeaveRequestData(String LeaveId, String userId, String StartDate, String EndDate,String Status,String calId)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_LEAVE_ID, LeaveId);
			values.put(DatabaseConstant.key_USER_ID, userId);
			values.put(DatabaseConstant.key_START_DATE, StartDate);
			values.put(DatabaseConstant.key_END_DATE, EndDate);
			values.put(DatabaseConstant.key_STATUS, Status);
			values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
			return database.insert(LEAVE_REQUEST, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT DATA INTO SHIFT_TASK ***/
	public long insertShiftTaskData(String shiftId, String taskId, String title,String Description)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_SHIFT_ID, shiftId);
			values.put(DatabaseConstant.key_TASK_ID, taskId);
			values.put(DatabaseConstant.key_TITLE, title);
			values.put(DatabaseConstant.key_DESCRIPTION, Description);
			return database.insert(SHIFT_TASK, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT DATA INTO METTING ***/
	public long insertMeetingData(String meetingId, String title, String MeetingOwner,String IsMandatory,String MeetingDate,String Start_Time, String EndTime, String Location, String Status ,String calId)
	{
		ContentValues values = new ContentValues();
		try
		{
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT DATA INTO ADDRESS BOOK ***/
	public long insertAddressBookData(String userID, String firstName ,String lastName,String status,
			String positionTitle, String orgName, String orgId)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_USER_ID, userID);
			values.put(DatabaseConstant.key_FIRST_NAME, firstName);
			values.put(DatabaseConstant.key_LAST_NAME, lastName);
			values.put(DatabaseConstant.key_STATUS, status);
			values.put(DatabaseConstant.key_POSITION_TITLE, positionTitle);
			values.put(DatabaseConstant.key_ORG_NAME, orgName);
			values.put(DatabaseConstant.key_ORG_ID, orgId);
			return database.insert(ADDRESS_BOOK, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT DATA INTO GLOBALS ***/
	public long insertDownloadFilePath(String key, String value)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_KEY, key);
			values.put(DatabaseConstant.key_VALUE, value);
			return database.insert(GLOBALS, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** INSERT DATA INTO LOGIN_QUESTIONS ***/
	public long insertLoginQuestionsData(String loginQuestionId, String question)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_LOGIN_QUESTION_ID, loginQuestionId);
			values.put(DatabaseConstant.key_QUESTION, question);
			return database.insert(LOGIN_QUESTIONS, null, values);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}


	/*** GET DATA FROM DOCUMENT FOR SPINNER ***/
	public Cursor getDocumentParentDirectories(String orderby, String order)
	{
		String sql = "SELECT * FROM "+DOCUMENTS+ " where "+DatabaseConstant.key_DOCUMENT_PARENT_ID +" = 0 ORDER BY "+orderby +" "+ order ;
		return database.rawQuery(sql, null);
	}
	/*** GET DATA FROM DOCUMENT FOR LIST VIEW ***/
	public Cursor getSubDirectory(String parentId, String orderby, String order)
	{
		String sql = "SELECT * FROM "+DOCUMENTS+ " where "+DatabaseConstant.key_DOCUMENT_PARENT_ID +" = "+parentId+" ORDER BY "+orderby +" "+ order ;
		return database.rawQuery(sql, null);
	}

	/*** GET DATA FOR LOGIN IS_LOGGED_IN STATUS***/
	/*public Cursor getActiveLoginSatus()
	{
		return database.rawQuery("select * from "+USERS+" where "+DatabaseConstant.key_IS_LOGGED_IN +" = 'Login'", null);
	}*/
	public Cursor getAlredayExistUser()
	{
		return database.rawQuery("select * from "+USERS+" where "+DatabaseConstant.key_USER_ID +" = '"+ActivityStringInfo.strUser_id+"'", null);
	}

	/** get user records **/
	public Cursor getUserRecord()
	{
		return database.rawQuery("select * from "+USERS+" ", null);
	}


	/** Get addressbook records**/
	public Cursor getAddressBookRecord()
	{
		return database.rawQuery("select * FROM " + ADDRESS_BOOK, null); //newly added on 22Jan2016
		// query changed as per client request on 22Jan2016
		//		return database.rawQuery("select * ,'1' as SRNo, "  + DatabaseConstant.key_ORG_ID +  " AS FULLNAME " +
		//				" FROM " + ADDRESS_BOOK + " where " + DatabaseConstant.key_USER_ID + " not like '%-0%' " +
		//				" UNION "
		//				+ " select * ,'2' as SRNo, " + DatabaseConstant.key_FIRST_NAME + " AS FULLNAME " +
		//				" FROM " + ADDRESS_BOOK + " where " + DatabaseConstant.key_USER_ID + " like '%-0%'" +
		//				" order by SRNo, FULLNAME, " + DatabaseConstant.key_LAST_NAME, null);

	}

	/** Set User LogOut**//*
	public long setUserLogout(String Device_Id, String Status)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseConstant.key_IS_LOGGED_IN, Status);
		System.out.println(values.toString());
		System.out.println();
		return database.update(USERS, values, DatabaseConstant.key_DEVICE_ID+" = '"+Device_Id+"'", null);
	}*/


	/** Get Message Records **/
	public Cursor getMessageList()
	{
		return database.rawQuery("select * from "+MESSAGE+" order by "+DatabaseConstant.key_MESSAGE_ID+" DESC ", null);
	}

	/** Get Shift Records **/
	public Cursor getShiftList()
	{
		return database.rawQuery("select * from "+SHIFTS+" order by "+DatabaseConstant.key_SHIFT_ID+" DESC ", null);
	}

	/** Get Meeting Records **/
	public Cursor getMeetingList()
	{
		return database.rawQuery("select * from "+MEETING+" order by "+DatabaseConstant.key_MEETING_ID+" DESC ", null);
	}

	// Get Not read MM Message
	public Cursor getNotReadMandatoryMessage()
	{
		return database.rawQuery("select MESSAGE_ID from MESSAGE where TYPE = 'MM' and MESSAGE_ID in (select MESSAGE_ID from MESSAGE_TO_USER where READ_DATE = '');", null);
	}

	/** Get Message Detail **/
	public Cursor getMessageDetail(String msgId)
	{
		return database.rawQuery("select * from "+MESSAGE+" where "+ DatabaseConstant.key_MESSAGE_ID +" = '"+msgId+"'", null);
	}

	/** Get Message Detail File Attachment List **/
	public Cursor getMessageDetailFileAttachmentList(String msgId)
	{
		return database.rawQuery("select * from "+MESSAGE_ATTACHMENT+" where "+ DatabaseConstant.key_MESSAGE_ID +" = '"+msgId+"' and "+ DatabaseConstant.key_USER_ID +" = '"+ActivityStringInfo.strUser_id+"'", null);
	}

	/** Get Message Detail File Attachment List BY fileId **/
	public Cursor getMessageDetailFileAttachmentListByFileId(String msgId,String fileId)
	{
		return database.rawQuery("select * from "+MESSAGE_ATTACHMENT+" where "+ DatabaseConstant.key_MESSAGE_ID +" = '"+msgId+"' and "+ DatabaseConstant.key_USER_ID +" = '"+ActivityStringInfo.strUser_id+"' and "+ DatabaseConstant.key_FILE_ID +" = '"+fileId+"'", null);
	}

	/** Get Message TO user List **/
	public Cursor getMessageToUserList(String msgId)
	{
		return database.rawQuery("select * from "+MESSAGE_TO_USER+" where "+ DatabaseConstant.key_MESSAGE_ID +" = '"+msgId+"'", null);
	}

	/*** GET DATA FROM DOCUMENT FOR LIST VIEW ***/
	public Cursor getAllDocument()
	{
		String sql = "SELECT * FROM "+DOCUMENTS ;
		return database.rawQuery(sql, null);
	}

	/*** GET DATA FROM DOCUMENT USING DOC ID ***/
	public Cursor getDocumentRecord(String docId)
	{
		String sql = "SELECT * FROM "+DOCUMENTS+" where "+DatabaseConstant.key_DOCUMENT_ID+" = '"+docId+"'" ;
		return database.rawQuery(sql, null);
	}


	/** Get Meeting List **/
	public Cursor getMeetingList(String meetingId)
	{
		return database.rawQuery("select * from "+MEETING+" where "+ DatabaseConstant.key_MEETING_ID +" = '"+meetingId+"'", null);
	}


	/** Get Leave Request List **/
//	public Cursor getLeaveRequestList(String leaveRequestId)
//	{
//		return database.rawQuery("select * from "+LEAVE_REQUEST+" where "+ DatabaseConstant.key_LEAVE_ID +" = '"+leaveRequestId+"'", null);
//	}
//
//	/** Get Leave Request List **/
//	public Cursor getAllLeaveRequestList()
//	{
//		return database.rawQuery("select * from "+LEAVE_REQUEST+" ", null);
//	}

	/**/  // Leave Record
	/**
	 * Get MyLeave Request List
	 **/
	public Cursor getAllMyLeaveRequestList() {
		return database.rawQuery("select * from " + LEAVE_REQUEST + " ", null);
	}

	/**
	 * Get MyLeave Request Record
	 **/
	public Cursor getMyLeaveRequestList(String leaveRequestId) {
		return database.rawQuery("select * from " + LEAVE_REQUEST + " where " + DatabaseConstant.key_LEAVE_ID + " = '" + leaveRequestId + "'", null);
	}

	/**
	 * Get MyLeave Request Record by Status
	 **/
	public Cursor getMyLeaveRequestbyStatus(String leaveRequestId, String status) {
		return database.rawQuery("select * from " + LEAVE_REQUEST + " where " + DatabaseConstant.key_LEAVE_ID + " = '" + leaveRequestId + "' AND " + DatabaseConstant.key_STATUS + " = '" + status + "'", null);
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
/**/

	//	/** Get Address Book Record **/
	//	public Cursor getAddressBookRecord(String userId)
	//	{
	//		return database.rawQuery("select * from "+ADDRESS_BOOK+" where "+ DatabaseConstant.key_USER_ID +" = '"+userId+"'", null);
	//	}

	/** Get Address Book Record **/
	public Cursor getAddressBookRecord(String userId, String orgId)
	{
		return database.rawQuery("select * from "+ADDRESS_BOOK+" where "+ DatabaseConstant.key_USER_ID +" = '"+userId+"' and " + DatabaseConstant.key_ORG_ID + " = '" + orgId+ "'", null);
	}

	/*** GET DATA FROM FORCE_CLOSE_DATA ***/
	public Cursor getForceClosedRecords(){
		String sql = "SELECT * FROM "+FORCE_CLOSE_DATA;
		return database.rawQuery(sql, null);
	}

	/** Get Meeting Records **/
	public Cursor getGlobalsRecords(String strKey)
	{
		return database.rawQuery("select * from "+GLOBALS+" where "+ DatabaseConstant.key_KEY +" = '"+strKey+"'", null);
	}

	/** Delete User Records **/
	public void deleteUsersRecords()
	{
		database.delete(USERS,null,null);
	}


	/** Delete Documents Records **/
	public void deleteAllDocument()
	{
		database.delete(DOCUMENTS,null,null);
	}

	/** Delete Address Book Records **/
	public void deleteAllAddressBook()
	{
		database.delete(ADDRESS_BOOK,null,null);
	}

	/** Delete Message Records **/
	public void deleteAllMessageList()
	{
		database.delete(MESSAGE,null,null);
	}

	/** Delete Message To User Records **/
	public void deleteAllMessageToUserList()
	{
		database.delete(MESSAGE_TO_USER,null,null);
	}

	/** Delete Message Attachment Records **/
	public void deleteAllMessageAttachmentList()
	{
		database.delete(MESSAGE_ATTACHMENT,null,null);
	}
	/** Delete Meeting Records **/
	public void deleteAllMeetingList()
	{
		database.delete(MEETING,null,null);
	}
	/** Delete Shift Records **/
	public void deleteAllShiftList()
	{
		database.delete(SHIFTS,null,null);
	}
	/** Delete Shift Task Records **/
	public void deleteAllShiftTaskList()
	{
		database.delete(SHIFT_TASK,null,null);
	}
	/** Delete Leave request Records **/
	public void deleteAllLeaveRequestList()
	{
		database.delete(LEAVE_REQUEST,null,null);
	}

	/** Delete LOGIN_QUESTIONS **/
	public void deleteAllLoginQuestion()
	{
		database.delete(LOGIN_QUESTIONS,null,null);
	}

	/** Update Delete date in message_to_user list Message **/
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


	/** Delete Meeting Record**/
	public int deleteMeetingRecord(String strMeetingID)
	{
		return database.delete(MEETING, DatabaseConstant.key_MEETING_ID +" = "+ strMeetingID, null);
	}

	/** Delete Meeting Record**/
	public int deleteLeaveRecord(String strLeaveID)
	{
		return database.delete(LEAVE_REQUEST, DatabaseConstant.key_LEAVE_ID +" = "+ strLeaveID, null);
	}

	/** Delete Message Record before meeting record delete**/
	public int deleteMsgMeetingRecord(String strMeetingID)
	{
		return database.delete(MESSAGE, DatabaseConstant.key_PROCESS_ID +" = "+ strMeetingID +" and "+ DatabaseConstant.key_TYPE +" = 'M'", null);
	}

	/** Delete Shift Record**/
	public int deleteShiftRecord(String strShiftID)
	{
		return database.delete(SHIFTS, DatabaseConstant.key_SHIFT_ID +" = "+ strShiftID, null);
	}

	/** Delete Address Book Record**/
	public int deleteAddressBookRecord(String strUserId)
	{
		return database.delete(ADDRESS_BOOK, DatabaseConstant.key_USER_ID +" = '"+ strUserId+"'", null);
	}

	/** Update Delete date in message_to_user list Message **/
	public long updateMessageToUserRecords(String msgId)
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 5);
			ContentValues values = new ContentValues();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			values.put(DatabaseConstant.key_READ_DATE, sdf.format(cal.getTime()));
			return database.update(MESSAGE_TO_USER, values, DatabaseConstant.key_MESSAGE_ID+" = '"+msgId+"'", null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/*** UPDATE DATA INTO LEAVE_REQUEST ***/
	public long updateLeaveRequestData(String LeaveId, String StartDate, String EndDate,String Status)
	{
		ContentValues values = new ContentValues();
		try
		{
			values.put(DatabaseConstant.key_START_DATE, StartDate);
			values.put(DatabaseConstant.key_END_DATE, EndDate);
			values.put(DatabaseConstant.key_STATUS, Status);
			return database.update(LEAVE_REQUEST, values, DatabaseConstant.key_LEAVE_ID+" = '"+LeaveId+"'",null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}


	/** Update In to user Table **/
	public long updateUsersRecords(String strDate,String docRights,String mobileNum,String notifyEmail,String notifySMS, String secQues1,String secQues2,String secQues3,String secAns1,String secAns2,String secAns3,String email)
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			ContentValues values = new ContentValues();
			if(!strDate.equals(""))
			{
				values.put(DatabaseConstant.key_LAST_SYNC_DT, strDate);
				values.put(DatabaseConstant.key_DOC_RIGHTS, docRights);
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
			}
			else
				values.put(DatabaseConstant.key_LAST_SYNC_DT, ActivityStringInfo.sdfDate.format(cal.getTime()));
			return database.update(USERS, values, DatabaseConstant.key_USER_ID+" = '"+ActivityStringInfo.strUser_id+"'", null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}


	/** Update Status to user Table **/
	public long updateUsersRecordsForStatus(String strFeild,String strStatus)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put(strFeild, strStatus);
			return database.update(USERS, values, DatabaseConstant.key_USER_ID+" = '"+ActivityStringInfo.strUser_id+"'", null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/** Update Shift Table **/
	public long updateShiftRecords(String shiftid,String shiftStart,String shiftEnd,String spiltStart,String splitEnd,String Status,String calId,String Position_Tital)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put(DatabaseConstant.key_SHIFT_START_TIME, shiftStart);
			values.put(DatabaseConstant.key_SHIFT_END_TIME, shiftEnd);
			values.put(DatabaseConstant.key_SPLIT_START_TIME, spiltStart);
			values.put(DatabaseConstant.key_SPLIT_END_TIME, splitEnd);
			values.put(DatabaseConstant.key_STATUS, Status);
			values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
			values.put(DatabaseConstant.key_POSITION_TITLE, Position_Tital);
			return database.update(SHIFTS, values, DatabaseConstant.key_SHIFT_ID+" = '"+shiftid+"'", null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/** Update MEETING Table **/
	public long updateMeetingRecords(String meetingId,String Date,String Start,String end,String Status,String calId)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put(DatabaseConstant.key_MEETING_DATE, Date);
			values.put(DatabaseConstant.key_MEETING_START_TIME, Start);
			values.put(DatabaseConstant.key_MEETING_END_TIME, end);
			values.put(DatabaseConstant.key_STATUS, Status);
			values.put(DatabaseConstant.key_CAL_EVENT_ID, calId);
			return database.update(MEETING, values, DatabaseConstant.key_MEETING_ID+" = '"+meetingId+"'", null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/** Update ADDRESS BOOK Table **/
	public long updateAddressBookRecords(String userId,String firstName,String lastName)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put(DatabaseConstant.key_FIRST_NAME, firstName);
			values.put(DatabaseConstant.key_LAST_NAME, lastName);
			return database.update(ADDRESS_BOOK, values, DatabaseConstant.key_USER_ID+" = '"+userId+"'", null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	/*** GET DATA FROM USERS FOR LIST VIEW ***/
	public Cursor getSearchedUsersDetail(String searchString)
	{
		if(searchString.equals("")){
			String sql = "SELECT * FROM "+USERS;
			//System.out.println("******** SQL : "+sql);
			return database.rawQuery(sql, null);
		}
		else
		{
			String sql = "SELECT * FROM "+USERS+" WHERE "+DatabaseConstant.key_FIRST_NAME+" LIKE "+"'"+searchString+"%'"+" OR "+DatabaseConstant.key_LAST_NAME+" LIKE "+"'"+searchString+"%'";
			//System.out.println("******** SQL : "+sql);
			return database.rawQuery(sql, null);
		}
	}

	/*** GET DATA FROM MEETING FOR MEETING DETAILS ***/
	public Cursor getMeetingDetail(String meetingId)
	{
		String sql = "SELECT * FROM "+MEETING+" WHERE "+DatabaseConstant.key_MEETING_ID+"="+meetingId;
		//System.out.println(MEETING +"******** SQL : "+sql);
		return database.rawQuery(sql, null);
	}

	/*** GET DATA FROM SHIFTS FOR SHIFT DETAILS ***/
	public Cursor getShiftDetail(String shiftId)
	{
		String sql = "SELECT * FROM "+SHIFTS+" WHERE "+DatabaseConstant.key_SHIFT_ID+"="+shiftId;
		//System.out.println(SHIFTS +"******** SQL : "+sql);
		return database.rawQuery(sql, null);
	}

	/*** GET DATA FROM SHIFT_TASK FOR SHIFT TASK LIST ***/
	public Cursor getShiftTaskList(String shiftId)
	{
		String sql = "SELECT * FROM "+SHIFT_TASK+" WHERE "+DatabaseConstant.key_SHIFT_ID+"="+shiftId;
		//System.out.println(SHIFT_TASK +"******** SQL : "+sql);
		return database.rawQuery(sql, null);
	}

	/*** GET DATA FROM SHIFTS, MEETING & LEAVE_REQUEST FOR SCHEDULE LIST ***/
	public Cursor getPostedSchedules()
	{
		String sql ="SELECT * FROM (SELECT "+ DatabaseConstant.key_SHIFT_ID+" AS ID,"+
				"\"\"" +" AS TITLE," +
				DatabaseConstant.key_DATE +" AS START_DATE,"+
				DatabaseConstant.key_SHIFT_START_TIME +" AS START_TIME,"+
				DatabaseConstant.key_SHIFT_END_TIME +" AS END_TIME,"+
				DatabaseConstant.key_SPLIT_START_TIME +" AS SPLIT_START_TIME,"+
				DatabaseConstant.key_SPLIT_END_TIME +" AS SPLIT_END_TIME,"+
				DatabaseConstant.key_TRAINEE +" AS TRAINEE,"+
				DatabaseConstant.key_ISOVERTIME +" AS ISOVERTIME,"+
				"\"\"" +" AS END_DATE,"+
				"'S'" +" AS TYPE,"+
				DatabaseConstant.key_STATUS +" AS STATUS"+
				" FROM "+SHIFTS + " where status <> 'NOUSE'" +
				" UNION "+
				"SELECT "+ DatabaseConstant.key_MEETING_ID+" AS ID,"+
				DatabaseConstant.key_TITLE +" AS TITLE," +
				DatabaseConstant.key_MEETING_DATE +" AS START_DATE,"+
				DatabaseConstant.key_MEETING_START_TIME +" AS START_TIME,"+
				DatabaseConstant.key_MEETING_END_TIME +" AS END_TIME,"+
				"\"\"" +" AS SPLIT_START_TIME,"+
				"\"\"" +" AS SPLIT_END_TIME,"+
				"\"\"" +" AS TRAINEE,"+
				"\"\"" +" AS ISOVERTIME,"+
				"\"\"" +" AS END_DATE,"+
				"'M'" +" AS TYPE,"+
				DatabaseConstant.key_STATUS +" AS STATUS"+
				" FROM "+MEETING +
				" UNION "+
				"SELECT "+ DatabaseConstant.key_LEAVE_ID+" AS ID,"+
				"\"\"" +" AS TITLE," +
				DatabaseConstant.key_START_DATE +" AS START_DATE,"+
				"\"\"" +" AS START_TIME,"+
				"\"\"" +" AS END_TIME,"+
				"\"\"" +" AS SPLIT_START_TIME,"+
				"\"\"" +" AS SPLIT_END_TIME,"+
				"\"\"" +" AS TRAINEE,"+
				"\"\"" +" AS ISOVERTIME,"+
				DatabaseConstant.key_END_DATE +" AS END_DATE,"+
				"'R'" +" AS TYPE,"+
				DatabaseConstant.key_STATUS +" AS STATUS"+
				" FROM "+LEAVE_REQUEST +") where CAST(START_DATE AS DATE)  >= CAST(strftime('%Y%m%d') AS DATE)"+
				" ORDER BY CAST(START_DATE AS DATE) ";
		//System.out.println(SHIFTS +"******** SQL : "+sql);
		return database.rawQuery(sql, null);
	}

	/*** GET DATA FROM SHIFTS, MEETING & LEAVE_REQUEST FOR SCHEDULE LIST ***/
	public Cursor getSelectedShift(String shiftId)
	{
		String sql ="SELECT * FROM (SELECT "+ DatabaseConstant.key_SHIFT_ID+" AS ID,"+
				"\"\"" +" AS TITLE," +
				DatabaseConstant.key_DATE +" AS START_DATE,"+
				DatabaseConstant.key_SHIFT_START_TIME +" AS START_TIME,"+
				DatabaseConstant.key_SHIFT_END_TIME +" AS END_TIME,"+
				DatabaseConstant.key_SPLIT_START_TIME +" AS SPLIT_START_TIME,"+
				DatabaseConstant.key_SPLIT_END_TIME +" AS SPLIT_END_TIME,"+
				DatabaseConstant.key_TRAINEE +" AS TRAINEE,"+
				DatabaseConstant.key_ISOVERTIME +" AS ISOVERTIME,"+
				"\"\"" +" AS END_DATE,"+
				"'S'" +" AS TYPE,"+
				DatabaseConstant.key_STATUS +" AS STATUS"+
				" FROM "+SHIFTS + " where status <> 'NOUSE' and status = '' and "+DatabaseConstant.key_SCH_NAME+" = (Select "+DatabaseConstant.key_SCH_NAME+" from SHIFTS where SHIFT_ID = "+shiftId+")) where CAST(START_DATE AS DATE)  >= CAST(strftime('%Y%m%d') AS DATE)"+
				" ORDER BY CAST(START_DATE AS DATE) ";
		//System.out.println(SHIFTS +"******** SQL : "+sql);
		return database.rawQuery(sql, null);
	}

	/*** GET DATA FROM MESSAGE LIST FOR DELETE MESSAGE ***/
	public Cursor getDeleteMsgId(String date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		SimpleDateFormat sdfparse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="";
		try
		{
			sql ="SELECT "+ DatabaseConstant.key_MESSAGE_ID+","+ DatabaseConstant.key_DELETE_DATE +" FROM "+MESSAGE_TO_USER + " where datetime("+ DatabaseConstant.key_DELETE_DATE +")  >= datetime('"+sdfparse.format(sdf.parse(date))+"')";
			System.out.println("sql==="+sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return database.rawQuery(sql, null);
	}

	/*** GET DATA FROM MESSAGE LIST FOR READ MESSAGE ***/
	public Cursor getReadMsgId(String date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		SimpleDateFormat sdfparse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="";
		try
		{
			sql ="SELECT "+ DatabaseConstant.key_MESSAGE_ID+","+DatabaseConstant.key_READ_DATE +	" FROM "+MESSAGE_TO_USER + " where datetime("+DatabaseConstant.key_READ_DATE+")  >= datetime('"+sdfparse.format(sdf.parse(date))+"')";
			System.out.println("sql==="+sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return database.rawQuery(sql, null);
	}



	////////*******Insert Update for Contact,Security and Password**********/////////
	/** Get LOGIN_QUESTIONS Records**/
	public Cursor getLoginQuestionsRecords()
	{
		return database.rawQuery("select * from "+LOGIN_QUESTIONS, null);
	}

	/** Get Login Questions List **/
	public Cursor getLoginQuestionsList(String loginQuestionId)
	{
		return database.rawQuery("select * from "+LOGIN_QUESTIONS+" where "+ DatabaseConstant.key_LOGIN_QUESTION_ID +" = '"+loginQuestionId+"'", null);
	}

	/*** INSERT DATA INTO USERS For Myinfo Detail ***/
	public long updateQuestionAnswer(String id,String strQuestion, String StrAnswer)
	{
		ContentValues values = new ContentValues();
		if(id.equals("1"))
		{
			values.put(DatabaseConstant.key_SEC_QUES_1, strQuestion);
			values.put(DatabaseConstant.key_SEC_ANS_1, StrAnswer);
		}
		if(id.equals("2"))
		{
			values.put(DatabaseConstant.key_SEC_QUES_2, strQuestion);
			values.put(DatabaseConstant.key_SEC_ANS_2, StrAnswer);
		}
		if(id.equals("3"))
		{
			values.put(DatabaseConstant.key_SEC_QUES_3, strQuestion);
			values.put(DatabaseConstant.key_SEC_ANS_3, StrAnswer);
		}
		return database.update(USERS, values, DatabaseConstant.key_USER_ID+" = '"+ActivityStringInfo.strUser_id+"'", null);
	}

	/** Update Shift Table **/
	public long updateContactInfo(String strEmail,String strNotifyByEmail,String strMobileNum,String strNotifyBySMS)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put(DatabaseConstant.key_EMAIL, strEmail);
			values.put(DatabaseConstant.key_NOTIFY_EMAIL, strNotifyByEmail);
			values.put(DatabaseConstant.key_MOBILE_NUM, strMobileNum);
			values.put(DatabaseConstant.key_NOTIFY_SMS, strNotifyBySMS);
			return database.update(USERS, values, DatabaseConstant.key_USER_ID+" = '"+ActivityStringInfo.strUser_id+"'", null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/********************* Mandatory Message *********************/

	/*** UPDATE DATA INTO Message Attachment ***/
	public long updateMessageAttachmentReadDate(String strMsgId,String strFileId)
	{

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 5);
		ContentValues values = new ContentValues();
		try
		{
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			values.put(DatabaseConstant.key_READ_DATE, sdfDate.format(cal.getTime()));
			return database.update(MESSAGE_ATTACHMENT, values, DatabaseConstant.key_MESSAGE_ID+" = '"+strMsgId+"'" +" and "+DatabaseConstant.key_FILE_ID+" = '"+strFileId+"'" +" and "+DatabaseConstant.key_USER_ID+" = '"+ActivityStringInfo.strUser_id+"'"  ,null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/*** GET DATA FROM MESSAGE LIST FOR DELETE MESSAGE ***/
	public Cursor getMsgAttchmentReadId(String date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		SimpleDateFormat sdfparse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="";
		try {
			sql = "SELECT * FROM "+MESSAGE_ATTACHMENT + " where datetime("+ DatabaseConstant.key_READ_DATE +")  >= datetime('"+sdfparse.format(sdf.parse(date))+"')";
			System.out.println("sql==="+sql);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}

		return database.rawQuery(sql, null);
	}

}
