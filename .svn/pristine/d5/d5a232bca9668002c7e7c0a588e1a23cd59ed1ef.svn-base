package ism.android;

import android.os.Bundle;

import org.ksoap2.serialization.SoapObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public class ActivityStringInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String VERSION = "Version 1.1.11";

    public static final String COPYRIGHT = "Copyright StaffTAP 2017";


    //For Synchronization Link
    //public static String wsLocation = "http://ism.test.septium.net";
    //public static String wsLocation = "http://ism.septium.net";
    //public static String wsLocation = "http://192.168.15.22/ism_m";
    //public static String wsLocation = "http://www.family.ourstaffing.biz";

    // TODO Stagging server url

//    public static String wsLocation ="https://staging.stafftap.biz";
//    public static String wsLocationImageUpload = "staging.stafftap.biz";
//    public static String wsHost = "staging.stafftap.biz";

    // TODO Production server url

    public static String wsLocation = "https://www.stafftap.biz";
    public static String wsLocationImageUpload = "www.stafftap.biz";
    public static String wsHost = "apps.stafftap.biz";

    public static Class<?> previousActivity;
    public static Class<?> previousActivityNew;
    public static Class<?> previousActivityForMM;

    //For Login Error
    public static String strLoginError = "";
    public static boolean isMMAttachment = false;

    public static Bundle bundleSI;

    //Date Formate for Message Detail
    public static SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);

    // For selected schedule item
    public String selectedId = "";
    public String selectedDate = "";
    public String selectedDay = "";
    public String requestOffStatus = "";


    public String strFrom;
    public static int strCount;
    public static Date strUpdateTime = null;
    public static int lCount=0;


    public static int mmCount = 0;
    public static int pnCount = 0;

    //For User Table
    public static String regGUID = "";
    public static String strUser_id = "";
    public static String strTempPassword = "";
    public static String strWsLocation = "";
    public static String strCompanyName = "";
    public static String strLocationName = "";
    public static String strFirstName = "";
    public static String strLastName = "";
    public static String strLogin = "";
    public static String strLocation = "";
    public static String strNewsBanner = "";
    public static String strCalIdName = "";
    public static String strPositionTitle = "";
    public static String strLastSyncDate = "";
    public static String strDocRights = "";
    public static String strSuggestright = "";
    public static String strLogRights = "";
    public static String strDailyLogHour = "";
    public static String strAcceptTerms = "";
    public static String strAutoGenPwd = "";
    public static String strMandatoryMsgRight = "";


    public static SoapObject strShiftTaskList;
    public static SoapObject strShiftList;
    public static SoapObject strLeaveRequestList;

    public static int ORG_ID = 0;

    //GCM Registration Id
    public static String gcm_reg_id = "";
    public static final String TAG = "GCMIntentService";
    //SENDER_ID
    public static final String SENDER_ID = "54669690417"; //editable405574022878

    //Shift Table  newly added from employee app.
    public static LinkedHashSet<String> strShiftTaskId = new LinkedHashSet<String>();

    //For Forgot Password
    public static ArrayList<HashMap<String, String>> ForgotPasswordQuestionList = new ArrayList<HashMap<String, String>>();
    public static String strResetPasswordEmail = "";

    public static String strLoginName = "";
    public static String strCalendarName = "";

    //Document Manager
    public static String lastSelectedParentId = "0", lastLocationId = "";
    public static List<String> locationId = new ArrayList<String>();
    public static String lastLocationString = "";

    //Message List
    public String strMessageId = "";
    public String strMeetingId = "";
    public String strMessageType = "";
    public String strMessageSubType = "";

    public String strMyInfoType = "";

    public String strCommentForWhich = "";
    public String strShiftId = "";
    public String strScheduleType = "";
    public static String strEmployeeId = "";
    public static HashMap<String, String> hasMessageList = new HashMap<String, String>();
    public static HashMap<String, String> hashDocumentList = new HashMap<String, String>();

    //AddressBook List
    public String strAddressBookName = "";
    public static ArrayList<HashMap<String, String>> AddressBookList = new ArrayList<HashMap<String, String>>();
    public static HashMap<String, String> hasAddressBook = new HashMap<String, String>();
    public static HashSet<String> strAddressUserId = new HashSet<String>();
    public static HashSet<String> strCompositeUserId = new HashSet<String>();

    // Compose Mail
    public static String strSubject = "";
    public static String strBody = "";

    //Check Deatil send to reply
    public static String strSendFor = "";

    public static int selectedTabIndex = 0;

    //Mandatory Message
    public static LinkedHashSet<String> strMandatoryMessageId = new LinkedHashSet<String>();
    public static LinkedHashSet<String> strNewScheduleMessageId = new LinkedHashSet<String>();

    public static ArrayList<HashMap<String, String>> MandatoryMessageAttachmentList = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> MandatoryMessageDetailList = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> MandatoryMessageDetailNotReadList = new ArrayList<HashMap<String, String>>();

    //Store the message list current

    public static int strMsgArrListSize = 0;
    public static int msgCurrentPageStartIndex = 0;
    public static int msgCurrentPageNumber = 1;
    public static String getMaxMsgId = "";
    public static String getMinMsgId = "";

    // ServiceForDocumentRead

    public static String strMsgId = "";
    public static String strFileId = "";


    // alertbox status for MM details body (Newly set for pushnotification )
    public static boolean alertopen = false;

    public static String alertMMMsg = "";


    public static boolean shouldRefreshed = false;

    public static boolean isLoggingOut = false;

    public static boolean mTwoPane = false;

    public static int selectedListItemIndex = 0;
    public static boolean isListItemSelected = false;


    //Added by dhiraj 12 Aug, 2015
    public static int CONN_TIMEOUT = 90000;

    //Test

    public static boolean itemSelected;
    public static String itemId = "";


    public void clear() {
        strCommentForWhich = "";
        AddressBookList.clear();
        strAddressUserId.clear();
        strCompositeUserId.clear();
        strBody = "";
        strSubject = "";
        strAddressBookName = "";
        strSendFor = "";
        ForgotPasswordQuestionList.clear();
        hashDocumentList.clear();

        //mandatory
        MandatoryMessageAttachmentList.clear();
        MandatoryMessageDetailList.clear();
        MandatoryMessageDetailNotReadList.clear();
    }

    // Store shift info
    public static HashMap<Integer, String> shiftTypeMap = new HashMap<Integer, String>();

    public static String bankDepositloaded_DB;

    public void setSelectedId(String id) {
        this.selectedId = id;
    }

    public String getSelectedId() {
        return this.selectedId;
    }

    public void setSelectedDate(String date) {
        this.selectedDate = date;
    }

    public String getSelectedDate() {
        return this.selectedDate;
    }

    public String getSelectedDay() {
        return selectedDay;
    }

    public void setSelectedDay(String selectedDay) {
        this.selectedDay = selectedDay;
    }

    public String getRequestOffStatus() {
        return requestOffStatus;
    }

    public void setRequestOffStatus(String requestOffStatus) {
        this.requestOffStatus = requestOffStatus;
    }
}
