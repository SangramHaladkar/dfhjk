package ism.android.webservices;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.utils.AppUtil;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.utils.ServerException;
import ism.android.utils.StaticVariables;


public class Synchronization {

    RequestProcessor processor;
    private final Context mContext;
    private SimpleDateFormat currentDateFormat;
    private SimpleDateFormat newDateFormat;

    /**
     * Database create
     **/

    public Synchronization(Context context) {
        this.mContext = context;
        this.currentDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        this.newDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    }

    public boolean checkConnection(Context context) {
        try {
            if (Utility.Connectivity_Internet(context))
                return true;
            else
                return false;
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
            return false;
        }
    }

    public String getLast_Sync_Date() {
        /** Get the last_sync_date from user. **/
        String lastSyncDate = "";
        try {
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getUserRecord();
            //			////System.out.println("count=="+c.getCount());
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    lastSyncDate = c.getString(11);
                    ActivityStringInfo.strCalIdName = c.getString(10);
                }
            }
            c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return lastSyncDate;
    }

    public String getMsgAttachmentReadDate() {
        /** Get the last_sync_date from user. **/
        String strReadId = "";
        try {
            String lstDate = getLast_Sync_Date();
            if (!lstDate.equals("")) {
                ////System.out.println("lstDate)====="+lstDate);
                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMsgAttchmentReadId(lstDate);
                ////System.out.println("count=="+c.getCount());
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        ////System.out.println("read date ===="+c.getString(4));
                        strReadId += c.getString(0) + "~" + c.getString(2) + ",";
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return strReadId;
    }

    public String getMsgDeleteDate() {
        /** Get the last_sync_date from user. **/
        String deleteId = "";
        try {
            String lstDate = getLast_Sync_Date();
            if (!lstDate.equals("")) {
                ////System.out.println("lstDate)====="+lstDate);
                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getDeleteMsgId(lstDate);
                ////System.out.println("count=="+c.getCount());
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        deleteId += c.getString(0) + ",";
                    }
                }
                c.close();
            }

        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return deleteId;
    }

    public String getMsgReadDate() {
        /** Get the last_sync_date from user. **/
        String readId = "";
        try {
            String lstDate = getLast_Sync_Date();
            if (!lstDate.equals("")) {
                Calendar cal = Calendar.getInstance();
                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getReadMsgId(lstDate);
                ////System.out.println("count=="+c.getCount());
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetail(c.getString(0));
                        if (c1.getCount() > 0) {
                            readId += c.getString(0) + "~" + ActivityStringInfo.sdfDate.format(cal.getTime()) + ",";
                        }
                        c1.close();
                    }
                }
                c.close();
            }

        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return readId;
    }

    public String getInformation(Context context) throws ServerException {

        String strMsg = "";
//        String lsynchdt="";

        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                String strDate = getLast_Sync_Date();
                String msgDeleteId = getMsgDeleteDate();
                String msgReadId = getMsgReadDate();
                String msgAttachmentReadDateId = getMsgAttachmentReadDate();
                System.out.println("strDate (Last Synch Date)====" + strDate);
                System.out.println("msgDeleteId====" + msgDeleteId);
                System.out.println("msgReadId====***********" + msgReadId);
                System.out.println("msgAttachmentReadDateId====***********" + msgReadId);

                if (!strDate.equals("")) {
                    params.put("last_sync_date", strDate);
                    params.put("MessageDeleteID", msgDeleteId);
                    params.put("MessageReadID", msgReadId);
                    params.put("MessageAttachmentID", msgAttachmentReadDateId);
                } else {
                    // Get a date 30 days ago
                    //					Calendar cal = Calendar.getInstance();
                    //					cal.add(Calendar.DAY_OF_MONTH, -30);
                    //					Date startDate = cal.getTime();
                    //
                    //					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    //					System.out.println("30 days ago date for synch when user logs first time is : "+sdf.format(startDate));
                    //					params.put("last_sync_date", sdf.format(startDate));
                    //					params.put("last_sync_date", "01/05/2011 16:34:54");
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, -15);
                    Date startDate = cal.getTime();

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
                    params.put("last_sync_date", "01/01/2000 00:00:00");
                    params.put("MessageDeleteID", "");
                    params.put("MessageReadID", "");
                    params.put("MessageAttachmentID", "");
//                    strDate=ActivityStringInfo.strLastSyncDate;
                }
                System.out.println("ActivityStringInfo.strLastSyncDate====" + strDate);
                System.out.println("strDate====" + strDate);

//                System.out.println("lsynchdt===="+lsynchdt);
//                lsynchdt=strDate;
//                System.out.println("lsynchdt1===="+lsynchdt);

                System.out.println("ActivityStringInfo.strCalIdName====" + ActivityStringInfo.strCalIdName);
                if (ActivityStringInfo.strCalIdName.equals("")) {
                    new SplashActivity().setValue();
//                    System.out.println("ActivityStringInfo.e===="+ActivityStringInfo.strCalIdName);
                }

                int iTestCalendarID = Integer.parseInt(ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")));
                System.out.println("iTestCalendarID============" + iTestCalendarID);

                processor = new RequestProcessor("Sync_ISM_Data", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                //System.out.println("response=="+response);

                if (null != response.getProperty("Sync_ISM_DataResult")) {

                    SoapObject Sync_ISM_Data = (SoapObject) response.getProperty("Sync_ISM_DataResult");
                    //					System.out.println("Sync_ISM_DataResult=="+Sync_ISM_Data.toString());

                    ActivityStringInfo.strNewsBanner = Sync_ISM_Data.getProperty("NewsBannerList").toString().replace("anyType{}", "");
                    System.out.println("NewsBannerList==" + Sync_ISM_Data.getProperty("NewsBannerList"));

                    try {
                        Utility.setBanner(context);
                        //Broadcast to MainActivity to refresh News Banner
                        Intent myIntent = new Intent("appRefreshed");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    strDate = Sync_ISM_Data.getProperty("Last_Sync_Date").toString().replace("anyType{}", "");

                    String docRights = Sync_ISM_Data.getProperty("DocRights").toString().replace("anyType{}", "");
                    String suggestRights = Sync_ISM_Data.getProperty("SuggestRights").toString().replace("anyType{}", "");
                    String mobileNum = Sync_ISM_Data.getProperty("Mobile_Num").toString().replace("anyType{}", "");
                    String notifyEmail = Sync_ISM_Data.getProperty("Notify_Email").toString().replace("anyType{}", "");
                    String notifySMS = Sync_ISM_Data.getProperty("Notify_Sms").toString().replace("anyType{}", "");
                    String secQues1 = Sync_ISM_Data.getProperty("Sec_Ques_1").toString().replace("anyType{}", "");
                    String secQues2 = Sync_ISM_Data.getProperty("Sec_Ques_2").toString().replace("anyType{}", "");
                    String secQues3 = Sync_ISM_Data.getProperty("Sec_Ques_3").toString().replace("anyType{}", "");
                    String secAns1 = Sync_ISM_Data.getProperty("Sec_Ans_1").toString().replace("anyType{}", "");
                    String secAns2 = Sync_ISM_Data.getProperty("Sec_Ans_2").toString().replace("anyType{}", "");
                    String secAns3 = Sync_ISM_Data.getProperty("Sec_Ans_3").toString().replace("anyType{}", "");
                    String email = Sync_ISM_Data.getProperty("Email").toString().replace("anyType{}", "");

                    System.out.println("Email==" + email);
                    System.out.println("LastSyncDate==" + Sync_ISM_Data.getProperty("Last_Sync_Date"));
                    System.out.println("docRights==" + Sync_ISM_Data.getProperty("DocRights"));

                    MyDatabaseInstanceHolder.getDatabaseHelper().updateUsersRecords(strDate, docRights, mobileNum, notifyEmail, notifySMS, secQues1, secQues2, secQues3, secAns1, secAns2, secAns3, email);

                    ActivityStringInfo.strLastSyncDate = strDate;  //extra added.
                    ActivityStringInfo.strDocRights = docRights;
                    ActivityStringInfo.strSuggestright = suggestRights;

                    try {
                        ///////Update delete date of Message
                        String strMsgDeleteId = Sync_ISM_Data.getProperty("MessageDeleteID").toString().replace("anyType{}", "");
                        if (!strMsgDeleteId.equals("")) {
                            StringTokenizer strMsgId = new StringTokenizer(strMsgDeleteId, ",");
                            HashSet<String> deleteMsgId = new HashSet<String>();
                            while (strMsgId.hasMoreElements()) {
                                String strId = strMsgId.nextElement().toString();
                                deleteMsgId.add(strId);
                                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageToUserList(strId);
                                if (c.getCount() > 0) {
                                    while (c.moveToNext()) {
                                        if (c.getString(3).equals("")) {
                                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, strId);
                                        }
                                    }
                                }
                                c.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        ///////Update Read date of Message
                        String strMsgReadId = Sync_ISM_Data.getProperty("MessageReadID").toString().replace("anyType{}", "");
                        if (!strMsgReadId.equals("")) {
                            StringTokenizer strReadId = new StringTokenizer(strMsgReadId, ",");
                            while (strReadId.hasMoreElements()) {
                                String msgId = strReadId.nextElement().toString();
                                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageToUserList(msgId);
                                if (c.getCount() > 0) {
                                    while (c.moveToNext()) {
                                        if (c.getString(2).equals("")) {
                                            MyDatabaseInstanceHolder.getDatabaseHelper().updateMessageToUserRecords(msgId);
                                        }
                                    }
                                }
                                c.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for Document Table**/
                        SoapObject Document_List = (SoapObject) Sync_ISM_Data.getProperty("Document_List");
                        //System.out.println("Document_List=="+Document_List.toString());
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllDocument();
                        for (int i = 0; i < Document_List.getPropertyCount(); i++) {
                            SoapObject Document = (SoapObject) Document_List.getProperty(i);
                            //System.out.println("Document=="+Document.toString());
                            String docId = Document.getProperty("DOC_ID").toString().replace("anyType{}", "");
                            String parentId = Document.getProperty("PARENT_ID").toString().replace("anyType{}", "");
                            String fileName = Document.getProperty("NAME").toString().replace("anyType{}", "");
                            String fileType = Document.getProperty("TYPE").toString().replace("anyType{}", "");
                            String fileLink = Document.getProperty("FILE_LINK").toString().replace("anyType{}", "");
                            fileLink = fileLink.replace(" ", "%20");
                            //System.out.println("docId=="+docId);

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getDocumentRecord(docId);
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertDocumentRecords(docId, parentId, fileName, fileType, fileLink);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Date sdate, synchdt;
                    boolean isFirstLogin = false;
                    try {
                        /** Data for Message Table**/
                        ActivityStringInfo.strMandatoryMessageId.clear();
                        ActivityStringInfo.strNewScheduleMessageId.clear();   // not in manager.
                        SoapObject Message_List = (SoapObject) Sync_ISM_Data.getProperty("Emessage_List");
                        //							System.out.println("Emessage_List=="+Message_List.toString());
//                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageList();
                        for (int i = 0; i < Message_List.getPropertyCount(); i++) {

                            SoapObject Emessage = (SoapObject) Message_List.getProperty(i);
                            String messageId = Emessage.getProperty("MESSAGE_ID").toString().replace("anyType{}", "");
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetail(messageId);
                            if (c1.getCount() == 0) {
                                String subject = Emessage.getProperty("SUBJECT").toString().replace("anyType{}", "");
                                String body = Emessage.getProperty("BODY").toString().replace("anyType{}", "");
                                String fromUserId = Emessage.getProperty("FROM_USER_ID").toString().replace("anyType{}", "");
                                String fromUserName = Emessage.getProperty("FROM_USER_NAME").toString().replace("anyType{}", "");
                                String messageDate = Emessage.getProperty("MESSAGE_DATE").toString().replace("anyType{}", "");
                                String type = Emessage.getProperty("TYPE").toString().replace("anyType{}", "");
                                String subType = Emessage.getProperty("SUB_TYPE").toString().replace("anyType{}", "");
                                String processId = Emessage.getProperty("PROCESS_ID").toString().replace("anyType{}", "");
                                String replyUserId = Emessage.getProperty("REPLY_USER_ID").toString().replace("anyType{}", "");
                                String replyUserName = Emessage.getProperty("REPLY_USER_NAME").toString().replace("anyType{}", "");

                                System.out.println("Emessaget== " + Emessage.toString());

                                if (type.equals("MM"))
                                    ActivityStringInfo.strMandatoryMessageId.add(messageId);


//                                if(!lsynchdt.equals("") && subject.contains("New Schedule Posted"))
//                                {
//                                    String s=subject.substring(21, 40);
//                                    sdate =new Date(s);
//                                    synchdt=new Date(lsynchdt);
//                                    // synchdt=new Date(""+Sync_ISM_Data.getProperty("Last_Sync_Date"));
//                                    System.out.println("Synch DateTime in 24Hours :"+new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(synchdt));
//                                    System.out.println("Schedule DateTime in 24Hours ="+new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(sdate));
//                                    if(sdate.compareTo(synchdt)>0)
//                                    {
//                                        ActivityStringInfo.strNewScheduleMessageId.add(messageId);
//                                        System.out.println("New Schedule added==>"+ActivityStringInfo.strNewScheduleMessageId.size());
//                                    }
//                                }

                                if (ActivityStringInfo.strLastSyncDate.equals("") && subject.contains("New Schedule Posted")) {
                                    ActivityStringInfo.strNewScheduleMessageId.add(messageId);
                                    isFirstLogin = true;
                                    System.out.println("New Schedule added firstly==>" + ActivityStringInfo.strNewScheduleMessageId.size());

                                }


                                MyDatabaseInstanceHolder.getDatabaseHelper().insertMessageData(messageId, subject, body, fromUserId, fromUserName, messageDate, type, subType, processId, replyUserId, replyUserName);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        ////System.out.println("mandatory message ====="+ActivityStringInfo.strMandatoryMessageId.toString());
                        /** Data for Message To User Table **/
                        SoapObject Message_To_User_List = (SoapObject) Sync_ISM_Data.getProperty("EMessage_To_User_List");
                        System.out.println("EMessage_To_User_List==" + Message_To_User_List.toString());
                        //MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageToUserList();
                        for (int i = 0; i < Message_To_User_List.getPropertyCount(); i++) {
                            SoapObject EMessage_To_User = (SoapObject) Message_To_User_List.getProperty(i);

                            String messageId = EMessage_To_User.getProperty("MESSAGE_ID").toString().replace("anyType{}", "");
                            String toUserId = EMessage_To_User.getProperty("TO_USER_ID").toString().replace("anyType{}", "");
                            String readDate = EMessage_To_User.getProperty("READ_DATE").toString().replace("anyType{}", "");
                            String deleteDate = EMessage_To_User.getProperty("DELETE_DATE").toString().replace("anyType{}", "");
                            SimpleDateFormat sdfParse = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

                            if (ActivityStringInfo.strMandatoryMessageId.contains(messageId)) {
                                if (!readDate.equals("")) {
                                    ActivityStringInfo.strMandatoryMessageId.remove(messageId);
                                    ////System.out.println("remove mm id====="+ActivityStringInfo.strMandatoryMessageId.toString());
                                }
                            }


                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageToUserList(messageId);

                            String strReadDate = "";
                            String strDeleteDate = "";
                            if (c1.getCount() == 0) {
                                if (!readDate.equals(""))
                                    strReadDate = sdf.format(sdfParse.parse(readDate));
                                if (!deleteDate.equals(""))
                                    strDeleteDate = sdf.format(sdfParse.parse(deleteDate));
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertMessageToUserData(messageId, toUserId, strReadDate, strDeleteDate);
                                System.out.println("Records in MESSAGE_TO_USER table Successfully.");
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (ActivityStringInfo.strMandatoryMessageId.size() > 0) {

                        //Utility.notificationForMandatoryMessage(context);

                    }

                    if (ActivityStringInfo.strNewScheduleMessageId.size() > 0) {

                        //Utility.notificationForShift(context);

                    }

                    try {
                        /** Data for Message Attachment Table **/
                        SoapObject Message_Attachment_List = (SoapObject) Sync_ISM_Data.getProperty("EMessage_Attachment_List");
                        //System.out.println("Emessage_List=="+Message_Attachment_List.toString());
                        for (int i = 0; i < Message_Attachment_List.getPropertyCount(); i++) {
                            SoapObject EMessage_Attachment = (SoapObject) Message_Attachment_List.getProperty(i);
                            //System.out.println("Emessage=="+EMessage_Attachment.toString());

                            String messageId = EMessage_Attachment.getProperty("MESSAGE_ID").toString().replace("anyType{}", "");
                            String fileId = EMessage_Attachment.getProperty("FILE_ID").toString().replace("anyType{}", "");
                            String fileName = EMessage_Attachment.getProperty("FILE_NAME").toString().replace("anyType{}", "");
                            String attachmentLink = EMessage_Attachment.getProperty("ATTACHMENT_LINK").toString().replace("anyType{}", "");
                            attachmentLink = attachmentLink.replace(" ", "%20");
                            String userId = EMessage_Attachment.getProperty("USER_ID").toString().replace("anyType{}", "");

                            SimpleDateFormat sdfParse = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

                            String readDate = EMessage_Attachment.getProperty("READ_DATE").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetailFileAttachmentList(messageId);
                            if (c1.getCount() == 0) {
                                if (!readDate.equals(""))
                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertMessageAttachmentData(fileId, messageId, fileName, attachmentLink, userId, sdf.format(sdfParse.parse(readDate)));
                                else
                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertMessageAttachmentData(fileId, messageId, fileName, attachmentLink, userId, readDate);
                            } else {
                                boolean bln = true;
                                while (c1.moveToNext()) {
                                    String file = c1.getString(2);
                                    if (!file.equals(fileId)) {
                                        bln = true;
                                    } else {
                                        bln = false;
                                        break;
                                    }
                                }
                                if (bln) {
                                    if (!readDate.equals(""))
                                        MyDatabaseInstanceHolder.getDatabaseHelper().insertMessageAttachmentData(fileId, messageId, fileName, attachmentLink, userId, sdf.format(sdfParse.parse(readDate)));
                                    else
                                        MyDatabaseInstanceHolder.getDatabaseHelper().insertMessageAttachmentData(fileId, messageId, fileName, attachmentLink, userId, readDate);

                                }
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for ADDRESS BOOK Table **/
                        SoapObject Distribution_List = (SoapObject) Sync_ISM_Data.getProperty("Distribution_List");
                        //System.out.println("Distribution_List=="+Distribution_List.toString());
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllAddressBook();//newly added 21/01/16
                        for (int i = 0; i < Distribution_List.getPropertyCount(); i++) {
                            SoapObject EAddress_Book = (SoapObject) Distribution_List.getProperty(i);
                            String userID = EAddress_Book.getProperty("DistributionListID").toString().replace("anyType{}", "");
                            String firstName = EAddress_Book.getProperty("ListName").toString().replace("anyType{}", "");
                            String orgName = EAddress_Book.getProperty("OrgName").toString().replace("anyType{}", "");
                            String orgId = EAddress_Book.getProperty("OrgId").toString().replace("anyType{}", "");
                            String positionTitle = "";
                            String lastName = "";
                            String status = "";
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getAddressBookRecord(userID, orgId);
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertAddressBookData(userID, firstName, lastName, status, positionTitle, orgName, orgId);
                            } else {
                                while (c1.moveToNext()) {
                                    if (!c1.getString(1).equals(firstName) || !c1.getString(2).equals(lastName)) {
                                        MyDatabaseInstanceHolder.getDatabaseHelper().updateAddressBookRecords(userID, firstName, lastName);
                                    }
                                }
                            }
                            c1.close();
                        }

                        SoapObject AddressBookUser_List = (SoapObject) Sync_ISM_Data.getProperty("AddressBookUser_List");
                        //System.out.println("AddressBookUser_List=="+AddressBookUser_List.toString());
                        //						MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllAddressBook();newly removed 21/01/16
                        for (int i = 0; i < AddressBookUser_List.getPropertyCount(); i++) {
                            SoapObject EAddress_Book = (SoapObject) AddressBookUser_List.getProperty(i);
                            String userID = EAddress_Book.getProperty("userid").toString().replace("anyType{}", "") + "-0";
                            String firstName = EAddress_Book.getProperty("FirstName").toString().replace("anyType{}", "");
                            String lastName = EAddress_Book.getProperty("LastName").toString().replace("anyType{}", "");
                            String status = EAddress_Book.getProperty("Active").toString().replace("anyType{}", "");
                            String positionTitle = EAddress_Book.getProperty("PositionTitle").toString().replace("anyType{}", "");
                            String orgName = EAddress_Book.getProperty("OrgName").toString().replace("anyType{}", "");
                            String orgId = EAddress_Book.getProperty("OrgID").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getAddressBookRecord(userID, orgId);
                            if (c1.getCount() == 0) {
                                if (!status.equals("0"))
                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertAddressBookData(userID, firstName, lastName, status, positionTitle, orgName, orgId);
                            } else {
                                while (c1.moveToNext()) {
                                    if (status.equals("0"))
                                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAddressBookRecord(userID);
                                    else if (!c1.getString(1).equals(firstName) || !c1.getString(2).equals(lastName))
                                        MyDatabaseInstanceHolder.getDatabaseHelper().updateAddressBookRecords(userID, firstName, lastName);
                                }
                            }
                            c1.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    String strEventId = "";
                    try {
                        /** Data for LEAVE REQUEST Table **/
                        SoapObject Leave_Request_List = (SoapObject) Sync_ISM_Data.getProperty("Leave_Request_List");

                        for (int i = 0; i < Leave_Request_List.getPropertyCount(); i++) {
                            SoapObject Leave_Request = (SoapObject) Leave_Request_List.getProperty(i);
                            String leaveId = Leave_Request.getProperty("LEAVE_ID").toString().replace("anyType{}", "");
                            String userId = Leave_Request.getProperty("USER_ID").toString().replace("anyType{}", "");
                            String startDate = Leave_Request.getProperty("START_DATE").toString().replace("anyType{}", "");
                            String endDate = Leave_Request.getProperty("END_DATE").toString().replace("anyType{}", "");
                            String status = Leave_Request.getProperty("STATUS").toString().replace("anyType{}", "");

                            String leaveDate = "";
                            try {
                                Date objDate = currentDateFormat.parse(startDate);
                                leaveDate = newDateFormat.format(objDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLeaveRequestList(leaveId);
                            int eventID = 0;
                            if (c1.getCount() == 0) {
                                if (iTestCalendarID != 0) {
                                    if (status.toLowerCase().contains("a")) {
                                        Uri newEvent = MakeNewLeaveRequestCalendarEntry(iTestCalendarID, startDate, endDate);
                                        eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                        strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                        System.out.println("LeaveEventID add successfuuly===" + eventID);
                                        Utility.addEntryInCalendarLog(mContext, "Add", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventID + " EventName:- Approved Day Off Date:- " + leaveDate);
                                    }
                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLeaveRequestData(leaveId, userId, startDate, endDate, status, "" + eventID);
                                }
                            } else {
                                if (status.toLowerCase().contains("a")) {
                                    Cursor c2 = MyDatabaseInstanceHolder.getDatabaseHelper().getMyLeaveRequestbyStatus(leaveId, status);
                                    if (c2.getCount() == 0) {
                                        Uri newEvent = MakeNewLeaveRequestCalendarEntry(iTestCalendarID, startDate, endDate);
                                        eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                        strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                        System.out.println("LeaveEventID add successfuuly===" + eventID);
                                        Utility.addEntryInCalendarLog(mContext, "Add", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventID + " EventName:- Approved Day Off Date:- " + leaveDate);
                                        MyDatabaseInstanceHolder.getDatabaseHelper().updateLeaveRequestData(leaveId, startDate, endDate, status);
                                    }
                                    c2.close();
                                } else if (status.toLowerCase().contains("d")) {
                                    // Update Request off status as denied in local Database
                                    Cursor c3 = MyDatabaseInstanceHolder.getDatabaseHelper().getMyLeaveRequestbyStatus(leaveId, status);
                                    if (c3.getCount() == 0) {
                                        MyDatabaseInstanceHolder.getDatabaseHelper().updateLeaveRequestData(leaveId, startDate, endDate, status);
                                    }
                                    c3.close();
                                } else if (status.toLowerCase().contains("c")) {
                                    Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getLeaveRequestList(leaveId);
                                    while (c.moveToNext()) {
                                        String eventId = c.getString(5);
                                        if (iTestCalendarID != 0) {
                                            if (Utility.filterCalenderEventDate(context, leaveDate)) {
                                                if (!eventId.equals("")) {
                                                    DeleteCalendarEntry(Integer.parseInt(eventId));
                                                    Utility.addEntryInCalendarLog(mContext, "Delete", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventId + "");
                                                }
                                                MyDatabaseInstanceHolder.getDatabaseHelper().deleteLeaveRecord(leaveId);
                                            }
                                        }
                                    }
                                    c.close();
                                }
                            }

                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        /** Data for MEETING Table **/

                        SoapObject Meeting_Detail_List = (SoapObject) Sync_ISM_Data.getProperty("Meeting_Detail_List");
                        //System.out.println("Meeting_Detail_List=="+Meeting_Detail_List.toString());
                        HashSet<String> hashMeetingId = new HashSet<String>();
                        try {
                            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMeetingList();
                            while (c.moveToNext()) {
                                hashMeetingId.add(c.getString(0));
                            }
                            c.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            for (int i = 0; i < Meeting_Detail_List.getPropertyCount(); i++) {
                                SoapObject Meeting_Detail = (SoapObject) Meeting_Detail_List.getProperty(i);
                                String meetingId = Meeting_Detail.getProperty("MEETING_ID").toString().replace("anyType{}", "");
                                String title = Meeting_Detail.getProperty("TITLE").toString().replace("anyType{}", "");
                                String owner = Meeting_Detail.getProperty("MEETING_OWNER").toString().replace("anyType{}", "");
                                String isMandatory = Meeting_Detail.getProperty("ISMANDATORY").toString().replace("anyType{}", "");
                                String date = Meeting_Detail.getProperty("MEETING_DATE").toString().replace("anyType{}", "");
                                String startTime = Meeting_Detail.getProperty("MEETING_START_TIME").toString().replace("anyType{}", "");
                                String endTime = Meeting_Detail.getProperty("MEETING_END_TIME").toString().replace("anyType{}", "");
                                String location = Meeting_Detail.getProperty("MEETING_LOCATION").toString().replace("anyType{}", "");
                                String status = Meeting_Detail.getProperty("STATUS").toString().replace("anyType{}", "");

                                String meetingDate = "";
                                try {
                                    Date objDate = currentDateFormat.parse(date);
                                    meetingDate = newDateFormat.format(objDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (hashMeetingId.contains(meetingId)) {
                                    hashMeetingId.remove(meetingId);
                                }
                                //int eventID = 0;
                                Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMeetingList(meetingId);
                                if (c1.getCount() == 0) {
                                    int eventID = 0;
                                    if (iTestCalendarID != 0) {
                                        if (Utility.filterCalenderEventDate(context, meetingDate)) {
                                            Uri newEvent = MakeNewMeetingCalendarEntry(iTestCalendarID, date, startTime, endTime, title, location, owner, isMandatory);
                                            eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                            strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                            System.out.println("eventID add successfuuly===" + eventID);
                                            MyDatabaseInstanceHolder.getDatabaseHelper().insertMeetingData(meetingId, title, owner, isMandatory, date, startTime, endTime, location, status, "" + eventID);
                                            Utility.addEntryInCalendarLog(mContext, "Add", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventID + " EventName:- ISM MEETING Date:- " + meetingDate + " " + startTime);
                                            //System.out.println("Meeting_Detail_List insert Successfully.");
                                        }
                                    }
                                    //MyDatabaseInstanceHolder.getDatabaseHelper().insertMeetingData(meetingId, title, owner, isMandatory, date, startTime, endTime, location, status,""+eventID);
                                } else {
                                    while (c1.moveToNext()) {
                                        String eventId = c1.getString(9);
                                        if (!c1.getString(4).equals(date) || !c1.getString(5).equals(startTime) || !c1.getString(6).equals(endTime) || !c1.getString(8).equals(status)) {
                                            if (iTestCalendarID != 0) {
                                                DeleteCalendarEntry(Integer.parseInt(eventId));
                                                if (Utility.filterCalenderEventDate(context, meetingDate)) {
                                                    Uri newEvent = MakeNewMeetingCalendarEntry(iTestCalendarID, date, startTime, endTime, title, location, owner, isMandatory);
                                                    int eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                                    strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                                    System.out.println("Meeting eventID add successfuuly===" + eventID);
                                                    MyDatabaseInstanceHolder.getDatabaseHelper().updateMeetingRecords(meetingId, date, startTime, endTime, status, "" + eventID);
                                                    Utility.addEntryInCalendarLog(mContext, "Update", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventID + " EventName:- ISM MEETING Date:- " + meetingDate + " " + startTime);
                                                }
                                            }
                                            //MyDatabaseInstanceHolder.getDatabaseHelper().updateMeetingRecords(meetingId,date,startTime,endTime,status,""+eventID);
                                        }
                                    }
                                }
                                c1.close();

                            }
                            if (hashMeetingId.size() > 0) {
                                Iterator<String> it = hashMeetingId.iterator();
                                while (it.hasNext()) {
                                    String strMeetingId = it.next();
                                    Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMeetingList(strMeetingId);
                                    while (c1.moveToNext()) {
                                        String eventId = c1.getString(9);
                                        if (iTestCalendarID != 0) {
                                            //System.out.println("eventId==="+eventId);
                                            //System.out.println("strMeetingId==="+strMeetingId);
                                            if (!eventId.equals("")) {
                                                DeleteCalendarEntry(Integer.parseInt(eventId));
                                                Utility.addEntryInCalendarLog(mContext, "Delete", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventId + "");
                                            }
                                            try {
                                                MyDatabaseInstanceHolder.getDatabaseHelper().deleteMsgMeetingRecord(strMeetingId);
                                                MyDatabaseInstanceHolder.getDatabaseHelper().deleteMeetingRecord(strMeetingId);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    c1.close();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {


                        /** Data for SHIFT TASK Table **/
                        SoapObject Shift_Task_List = (SoapObject) Sync_ISM_Data.getProperty("Shift_Task_List");
                        System.out.println("Shift_Task_List==" + Shift_Task_List.toString());
                        System.out.println("Shift_Task_List count============" + Shift_Task_List.getPropertyCount());
                        //MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageAttachmentList();
                        for (int i = 0; i < Shift_Task_List.getPropertyCount(); i++) {
                            SoapObject Shift_Task = (SoapObject) Shift_Task_List.getProperty(i);
                            String shiftId = Shift_Task.getProperty("SHIFT_ID").toString().replace("anyType{}", "");
                            String taskId = Shift_Task.getProperty("TASK_ID").toString().replace("anyType{}", "");
                            String title = Shift_Task.getProperty("TITLE").toString().replace("anyType{}", "");
                            String Description = Shift_Task.getProperty("DESCRIPTION").toString().replace("anyType{}", "");
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftTaskList(shiftId);
                            //System.out.println("in count====get ============"+c1.getCount());
                            boolean bln = false;
                            if (c1.getCount() != 0) {
                                while (c1.moveToNext()) {
                                    if (c1.getString(0).equals(shiftId) && c1.getString(1).equals(taskId)) {
                                        bln = true;
                                        break;
                                    } else {
                                        bln = false;
                                    }
                                }
                                if (!bln) {
                                    ActivityStringInfo.strShiftTaskId.add(shiftId);
                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftTaskData(shiftId, taskId, title, Description);
                                    //System.out.println("Shift_Task_List insert Successfully.");
                                }

                            } else {
                                ActivityStringInfo.strShiftTaskId.add(shiftId);
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftTaskData(shiftId, taskId, title, Description);
                            }
                            c1.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {


                        /** Data for SHIFT Table **/
                        SoapObject Shift_Detail_List = (SoapObject) Sync_ISM_Data.getProperty("Shift_Detail_List");
                        //System.out.println("INitializer:---"+ActivityStringInfo.lCount);
                        //System.out.println("Shift_Detail_List count==" + Shift_Detail_List.getPropertyCount());
                        System.out.println("Shift_Detail_List" + Shift_Detail_List.toString());
                        /*
                        if(ActivityStringInfo.lCount!=0 )
						{
							if(ActivityStringInfo.lCount<Shift_Detail_List.getPropertyCount())
							{
								Utility.notificationForShift(context);
								System.out.println("You have new schedule......");
								ActivityStringInfo.isNoSchedule=false;
							}

						}
						 */
                        ActivityStringInfo.lCount = Shift_Detail_List.getPropertyCount();
                        System.out.println("Counter:---" + Shift_Detail_List.getPropertyCount());

                        HashSet<String> hashShiftId = new HashSet<String>();
                        try {
                            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftList();
                            System.out.println("Shiflist.size()" + c.getCount());
                            while (c.moveToNext()) {
                                hashShiftId.add(c.getString(0));
                            }
                            c.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < Shift_Detail_List.getPropertyCount(); i++) {
                            SoapObject Shift_Detail = (SoapObject) Shift_Detail_List.getProperty(i);
                            //System.out.println("Emessage=="+EMessage_Attachment.toString());
                            String shiftId = Shift_Detail.getProperty("SHIFT_ID").toString().replace("anyType{}", "");
                            String Date = Shift_Detail.getProperty("DATE").toString().replace("anyType{}", "");
                            String ShiftStartTime = Shift_Detail.getProperty("SHIFT_START_TIME").toString().replace("anyType{}", "");
                            String ShiftEndTime = Shift_Detail.getProperty("SHIFT_END_TIME").toString().replace("anyType{}", "");
                            String SplitStartTime = Shift_Detail.getProperty("SPLIT_START_TIME").toString().replace("anyType{}", "");
                            String SplitEndTime = Shift_Detail.getProperty("SPLIT_END_TIME").toString().replace("anyType{}", "");
                            String Trainee = Shift_Detail.getProperty("TRAINEE").toString().toString().replace("anyType{}", "");
                            String WorkStation = Shift_Detail.getProperty("WORKSTATION") == null ? "" : Shift_Detail.getProperty("WORKSTATION").toString().replace("anyType{}", "");
                            String IsOverTime = Shift_Detail.getProperty("ISOVERTIME").toString().replace("anyType{}", "");
                            String Status = Shift_Detail.getProperty("STATUS").toString().replace("anyType{}", "");
                            String Schedule_Name = Shift_Detail.getProperty("SCHEDULE_NAME").toString().replace("anyType{}", "");
                            String Position_Title = Shift_Detail.getProperty("POSITION_TITLE").toString().replace("anyType{}", "");
                            System.out.println("Position_Title===" + Position_Title);
                            System.out.println("ShiftId===" + shiftId);

                            String shiftDate = "";
                            try {
                                Date objDate = currentDateFormat.parse(Date);
                                shiftDate = newDateFormat.format(objDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }//
                            if (hashShiftId.contains(shiftId)) {
                                hashShiftId.remove(shiftId);
                                System.out.println("Removed shiftId:" + shiftId);
                            }

                            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftTaskList(shiftId);

                            String taskList = "";

                            if (c.getCount() != 0) {
                                while (c.moveToNext()) {
                                    taskList += "\n    - " + c.getString(2);
                                }
                            }
                            c.close();

                            //int eventID = 0;
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftDetail(shiftId);
                            if (c1.getCount() == 0) {
                                if (iTestCalendarID != 0) {
                                    if (Utility.filterCalenderEventDate(context, shiftDate)) {
                                        Uri newEvent;
                                        if (!SplitStartTime.equals(""))
                                            newEvent = MakeNewCalendarEntry(iTestCalendarID, Date, ShiftStartTime, SplitEndTime, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
                                        else
                                            newEvent = MakeNewCalendarEntry(iTestCalendarID, Date, ShiftStartTime, ShiftEndTime, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
                                        int eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                        strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                        System.out.println("eventID add successfuuly===" + eventID);
                                        MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftData(shiftId, Date, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, "" + eventID, "O", Schedule_Name, Position_Title);
                                        System.out.println("Shift added successfuuly===" + shiftId);
                                        Utility.addEntryInCalendarLog(mContext, "Add", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventID + " EventName:- ISM SHIFT   Date:- " + ShiftStartTime);
                                    }
                                }
                                //	MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftData(shiftId, Date, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, ""+eventID, "O",Schedule_Name,Position_Title);
                            } else {
                                while (c1.moveToNext()) {
                                    if (!c1.getString(2).equals(ShiftStartTime) || !c1.getString(3).equals(ShiftEndTime) || !c1.getString(4).equals(SplitStartTime) || !c1.getString(5).equals(SplitEndTime) || !c1.getString(9).equals(Status)) {
                                        String eventId = c1.getString(10);
                                        if (iTestCalendarID != 0) {
                                            if (Utility.filterCalenderEventDate(context, shiftDate)) {
                                                Uri newEvent;
//                                            if (!SplitStartTime.equals(""))
//                                                newEvent = MakeNewCalendarEntry(iTestCalendarID, Date, ShiftStartTime, SplitEndTime, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
//                                            else
//                                                newEvent = MakeNewCalendarEntry(iTestCalendarID, Date, ShiftStartTime, ShiftEndTime, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
                                                if (DeleteCalendarEntry(Integer.parseInt(eventId)) > 0) {
                                                    newEvent = MakeNewCalendarEntry(iTestCalendarID, Date, ShiftStartTime, ShiftEndTime, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
                                                    int eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                                    strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                                    System.out.println("eventID add successfuuly===" + eventID);
                                                    MyDatabaseInstanceHolder.getDatabaseHelper().updateShiftRecords(shiftId, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Status, "" + eventID, Position_Title);
                                                    System.out.println("Shift updated successfuuly===" + shiftId);
                                                    Utility.addEntryInCalendarLog(mContext, "Update", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventID + " EventName:- ISM SHIFT   Date:- " + ShiftStartTime);
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                            c1.close();
                        }
                        System.out.println("******^^^^^^^^^hashShiftId" + hashShiftId.size());
                        if (hashShiftId.size() > 0) {
                            Iterator<String> it = hashShiftId.iterator();
                            while (it.hasNext()) {
                                String strShiftId = it.next();
                                System.out.println("******^^^^^ShiftId: " + strShiftId);
                                Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftDetail(strShiftId);
                                while (c1.moveToNext()) {
                                    String shiftDate = "";

                                    String eventId = c1.getString(c1.getColumnIndex(DatabaseConstant.key_CAL_EVENT_ID));
                                    String Date = c1.getString(c1.getColumnIndex(DatabaseConstant.key_DATE));

                                    Date objDate = currentDateFormat.parse(Date);
                                    Date cDate = currentDateFormat.parse(currentDateFormat.format(Calendar.getInstance().getTime()));

                                    shiftDate = newDateFormat.format(objDate);


                                    if (iTestCalendarID != 0) {


                                        if (Utility.filterCalenderEventDate(context, shiftDate)) {
                                            System.out.println("ddd");
                                            if (objDate.after(cDate)) {

                                                if (!eventId.equals("")) {
                                                    DeleteCalendarEntry(Integer.parseInt(eventId));
                                                    Utility.addEntryInCalendarLog(mContext, "Delete", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventId + "");
                                                }
                                                MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRecord(strShiftId);
                                            }
                                        }
                                    }
                                }
                                c1.close();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //System.out.println("strEventId=="+strEventId);

                    try {
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
                        e.printStackTrace();
                    }

                    try {
                        /** Data for LOGIN_QUESTIONS Table **/
                        SoapObject Login_Questions_List = (SoapObject) Sync_ISM_Data.getProperty("LoginQuestions_List");
                        for (int i = 0; i < Login_Questions_List.getPropertyCount(); i++) {
                            SoapObject login_Questions = (SoapObject) Login_Questions_List.getProperty(i);
                            String loginQuestionId = login_Questions.getProperty("LoginQuestionID").toString().replace("anyType{}", "");
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLoginQuestionsList(loginQuestionId);
                            if (c1.getCount() == 0) {
                                String question = login_Questions.getProperty("Question").toString().replace("anyType{}", "");

                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLoginQuestionsData(loginQuestionId, question);
                                //System.out.println("LoginQuestions_List insert Successfully.");
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (strMsg.equals(""))
                        strMsg = "true";
                    Utility.deleteDownloadFile(context);


                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            if (strMsg.equals(""))
                strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
            return strMsg;
        }
        return strMsg;
    }

    /*
     * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
     */
    private String getCalendarUriBase() {
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
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return calendarUriBase;
    }

    /**
     * Deletes Calendar Entry
     *
     * @param entryID
     * @return
     */
    @SuppressLint("NewApi")
    private int DeleteCalendarEntry(int entryID) {
        int iNumRowsDeleted = 0;
        try {
            Uri eventsUri;
//            if (android.os.Build.VERSION.SDK_INT >= 14) {
//                eventsUri = Uri.parse(Calendars.CONTENT_URI + "events");
//                System.out.println("Synchronization class-->DeleteCalendarEntry----> New Api>=14");
//            } else {
//                eventsUri = Uri.parse(getCalendarUriBase() + "events");
//            }
            Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, entryID);
            iNumRowsDeleted = mContext.getContentResolver().delete(eventUri, null, null);
        } catch (Exception e) {
            Utility.writeErrorInDirectory(e.getMessage(),mContext);
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return iNumRowsDeleted;
    }

    /**
     * Makes New Meeting Calendar Entry
     *
     * @param calId
     * @param shiftDate
     * @param starttime
     * @param endtime
     * @param title
     * @param location
     * @param owner
     * @param isMandatory
     * @return
     */
    private Uri MakeNewMeetingCalendarEntry(int calId, String shiftDate, String starttime, String endtime, String title, String location, String owner, String isMandatory) {
        Uri insertedUri = null;
        try {
            ContentValues event = new ContentValues();
            event.put("calendar_id", calId);
            // changes has been made in the Synchronization .java class to make the calendar name unique.
            //			event.put("title", "StaffTAP"+ActivityStringInfo.strCompanyName.toUpperCase()+" MEETING");
            event.put("title", title);
            event.put("eventLocation", location);
            if (isMandatory.toLowerCase().contains("y"))
                isMandatory = "Yes";
            else
                isMandatory = "No";
            event.put("description", "Owner : " + owner + "\nMandatory : " + isMandatory + " \n\nThis event can be automatically deleted when a schedule is updated. Please do not add any notes.");
            Date dateObj = null;
            try {
                dateObj = StaticVariables.dbDateFormat.parse(shiftDate);
                newDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            String newDateStr = newDateFormat.format(dateObj);

            String startdate = newDateStr + " " + starttime;
            String enddate = newDateStr + " " + endtime;

            Date sdate = null;
            Date edate = null;
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            try {
                sdate = df.parse(startdate);
                edate = df.parse(enddate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long startTime = sdate.getTime();
            long endTime = edate.getTime();

            event.put("dtstart", startTime);
            event.put("dtend", endTime);

            event.put("allDay", 0); // 0 for false, 1 for true
            event.put("eventStatus", 1);
            event.put("hasAlarm", 1); // 0 for false, 1 for true
            event.put("eventTimezone", TimeZone.getDefault().getDisplayName());

            Uri eventsUri = Uri.parse(getCalendarUriBase() + "events");

            insertedUri = mContext.getContentResolver().insert(eventsUri, event);

			/* // reminder insert
            Uri REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
		    event = new ContentValues();
		    event.put( "event_id", Long.parseLong(insertedUri.getLastPathSegment()));
		    event.put( "method", 1 );
		    event.put( "minutes", 10 );
		    mContext.getContentResolver().insert( REMINDERS_URI, event );*/
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return insertedUri;
    }

    private Uri MakeNewCalendarEntry(int calId, String shiftDate, String starttime, String endtime, String ShiftStartTime, String ShiftEndTime, String SplitStartTime, String SplitEndTime, String Trainee, String WorkStation, String IsOverTime, String Status, String Schedule_Name, String TaskList, String Position_Tital) {
        Uri insertedUri = null;
        try {
            ContentValues event = new ContentValues();
            event.put("calendar_id", calId);
            event.put("title", ActivityStringInfo.strCompanyName.toUpperCase() + " SHIFT");
            //event.put("description", "This event can be automatically deleted when a schedule is updated. Please do not add any notes.");

            if (IsOverTime.toLowerCase().equals("y"))
                IsOverTime = "Yes";
            else if (IsOverTime.toLowerCase().equals("n"))
                IsOverTime = "No";

            String strDescription = "\n" + Schedule_Name + "\n" + Position_Tital + "\n" + WorkStation + "\n\nSHIFT Start : " + ShiftStartTime + "\nSHIFT End : " + ShiftEndTime + "\nSPLIT Start : " + SplitStartTime + "\nSPLIT End : " + SplitEndTime + "\n\nOVERTIME : " + IsOverTime + "\nTRAINEE : " + Trainee + "\nTASKS : " + TaskList + "\n\nSTATUS : " + Status;

            event.put("description", strDescription + "\n\nThis event can be automatically deleted when a schedule is updated. Please do not add any notes.");

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

            Uri eventsUri = Uri.parse(getCalendarUriBase() + "events");

            insertedUri = mContext.getContentResolver().insert(eventsUri, event);

			/* // reminder insert
            Uri REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
		    event = new ContentValues();
		    event.put( "event_id", Long.parseLong(insertedUri.getLastPathSegment()));
		    event.put( "method", 1 );
		    event.put( "minutes", 10 );
		    mContext.getContentResolver().insert( REMINDERS_URI, event );*/
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return insertedUri;
    }

    private Uri MakeNewLeaveRequestCalendarEntry(int calId, String StartDate, String EndDate) {
        Uri insertedUri = null;
        try {
            ContentValues event = new ContentValues();
            event.put("calendar_id", calId);
            event.put("title", "Approved Day Off");
            //event.put("eventLocation", location);
            event.put("description", "\nUser Name : " + ActivityStringInfo.strFirstName + " " + ActivityStringInfo.strLastName + "\n\nThis event can be automatically deleted when a schedule is updated. Please do not add any notes.");
            Date dateObj = null;
            SimpleDateFormat newDateFormat = null;
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
            event.put("eventTimezone", Time.getCurrentTimezone());

            Uri eventsUri = Uri.parse(getCalendarUriBase() + "events");

            insertedUri = mContext.getContentResolver().insert(eventsUri, event);

			/* // reminder insert
            Uri REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
		    event = new ContentValues();
		    event.put( "event_id", Long.parseLong(insertedUri.getLastPathSegment()));
		    event.put( "method", 1 );
		    event.put( "minutes", 10 );
		    mContext.getContentResolver().insert( REMINDERS_URI, event );*/
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return insertedUri;
    }


}
