package ism.manager.webservices;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract.Calendars;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.ksoap2.SoapFault;
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

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.backgroundservices.NotificationServiceForShift;
import ism.manager.utils.AppUtil;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.JsonLogs;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.ServerException;
import ism.manager.utils.StaticVariables;

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

        try {
            if (checkConnection(context)) {
                // Log.v("getInformation ", "checkconnection");
                //////Daily Logs Updates
                JsonLogs Jujson = new JsonLogs(context);
                String strJuJson = Jujson.sendLogsDataToServer("U");
                ////System.out.println("strJuJson====>"+strJuJson);
                LinkedHashMap<String, String> Juparams = new LinkedHashMap<String, String>();

                Juparams.put("logdata", strJuJson);

                if (!strJuJson.equals("")) {
                    processor = new RequestProcessor("Insert_Log_Data", Juparams);
                    SoapSerializationEnvelope Juenvelope = processor.makeRequest(context);
                    String errorJuMsg = AppUtil.getHeaderText(Juenvelope, "ErrorMsg");
                    if (null != errorJuMsg) {
                        strMsg = errorJuMsg;
                        //throw new ServerException(errorJuMsg);
                    }

                    if (Juenvelope.bodyIn instanceof SoapFault) {
                        String str = ((SoapFault) Juenvelope.bodyIn).faultstring;
                        Log.i("Synchronization.java", str);
                    } else {
                        SoapObject Juresponse = (SoapObject) Juenvelope.bodyIn;

                        ////System.out.println("response jsonnnnnnnn update complete=="+Juresponse);
                        if (Juresponse.getProperty("Insert_Log_DataResult").toString().toLowerCase().equals("true")) {
                            ////System.out.println("update Logs Data To Server====>");
                            MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetailsOnSync("U", "C");
                        }
                    }
                }
                //////Daily Logs Inserts
                JsonLogs json = new JsonLogs(context);
                String strJson = json.sendLogsDataToServer("I");
                ////System.out.println("strJson====>"+strJson);
                LinkedHashMap<String, String> Jparams = new LinkedHashMap<String, String>();

                Jparams.put("logdata", strJson);

                if (!strJson.equals("")) {
                    processor = new RequestProcessor("Insert_Log_Data", Jparams);
                    SoapSerializationEnvelope jenvelope = processor.makeRequest(context);
                    String errorJMsg = AppUtil.getHeaderText(jenvelope, "ErrorMsg");
                    if (null != errorJMsg) {
                        strMsg = errorJMsg;
                        //throw new ServerException(errorJMsg);
                    }
                    SoapObject jresponse = (SoapObject) jenvelope.bodyIn;
                    ////System.out.println("response jsonnnnnnnn insert complete=="+jresponse);
                    if (jresponse.getProperty("Insert_Log_DataResult").toString().toLowerCase().equals("true")) {
                        ////System.out.println("insert Logs Data To Server");
                        MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetailsOnSync("I", "C");
                    }
                }

                //TODO Newly added on 15july14

                //////Daily Logs Pending Inserts
                JsonLogs Pjson = new JsonLogs(context);
                String strPJson = Pjson.sendLogsDataToServer("PI");
                ////System.out.println("strPJson====>"+strPJson);
                LinkedHashMap<String, String> PJparams = new LinkedHashMap<String, String>();

                PJparams.put("logdata", strPJson);

                if (!strPJson.equals("")) {
                    processor = new RequestProcessor("Insert_Log_Data", PJparams);
                    SoapSerializationEnvelope pjenvelope = processor.makeRequest(context);
                    String errorPJMsg = AppUtil.getHeaderText(pjenvelope, "ErrorMsg");
                    if (null != errorPJMsg) {
                        strMsg = errorPJMsg;
                        //throw new ServerException(errorJMsg);
                    }

                    if (pjenvelope.bodyIn instanceof SoapFault) {
                        String str = ((SoapFault) pjenvelope.bodyIn).faultstring;
                        Log.i("Synchronization.java", str);
                    }
                    SoapObject pjresponse = (SoapObject) pjenvelope.bodyIn;
                    ////System.out.println("response jsonnnnnnnn insert pending=="+pjresponse);
                    if (pjresponse.getProperty("Insert_Log_DataResult").toString().toLowerCase().equals("true")) {
                        long up = MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetailsOnSync("PI", "P");
                        ////System.out.println("insert pending Logs Data to Server&&&&&&&&&&&&&&&&&&&");
                        //ActivityStringInfo.selectedLogStatus = "P";
                    }

                }

                //////Daily Logs Pending Updates
                JsonLogs PUJujson = new JsonLogs(context);
                String strPUJuJson = PUJujson.sendLogsDataToServer("PU");
                ////System.out.println("strPUJuJson====>"+strPUJuJson);
                LinkedHashMap<String, String> PUJuparams = new LinkedHashMap<String, String>();

                PUJuparams.put("logdata", strPUJuJson);

                if (!strPUJuJson.equals("")) {
                    processor = new RequestProcessor("Insert_Log_Data", PUJuparams);
                    SoapSerializationEnvelope PUJuenvelope = processor.makeRequest(context);
                    String errorJuMsg = AppUtil.getHeaderText(PUJuenvelope, "ErrorMsg");
                    if (null != errorJuMsg) {
                        strMsg = errorJuMsg;
                        //throw new ServerException(errorJuMsg);
                    }
                    SoapObject PUJuresponse = (SoapObject) PUJuenvelope.bodyIn;

                    ////System.out.println("response jsonnnnnnnn update pending=="+PUJuresponse);
                    if (PUJuresponse.getProperty("Insert_Log_DataResult").toString().toLowerCase().equals("true")) {

                        long up = MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetailsOnSync("PU", "P");
                        ////System.out.println("update pendingUP*******************");
                    }
                }


                ////Sync
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
                    // Get a date 15 days ago
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, -15);
                    Date startDate = cal.getTime();

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
                    //					System.out.println("15 days ago date for synch"+sdf.format(startDate));
                    //					params.put("last_sync_date", sdf.format(startDate));

                    params.put("last_sync_date", "01/01/2000 00:00:00");
                    params.put("MessageDeleteID", "");
                    params.put("MessageReadID", "");
                    params.put("MessageAttachmentID", "");
                }

                ////System.out.println("strDate===="+strDate);
                ////System.out.println("ActivityStringInfo.strCalIdName===="+ActivityStringInfo.strCalIdName);
                if (ActivityStringInfo.strCalIdName.equals("")) {
                    new SplashActivity().setValue();
                    ////System.out.println("ActivityStringInfo.e===="+ActivityStringInfo.strCalIdName);
                }

                int iTestCalendarID = Integer.parseInt(ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")));

                processor = new RequestProcessor("Sync_ISM_MGR_Data", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                //System.out.println("response=="+response);

                if (null != response.getProperty("Sync_ISM_MGR_DataResult")) {
                    //response.getProperty("Sync_ISM_DataResult").toString().replace("anyType{}", "");

                    SoapObject Sync_ISM_MGR_Data = (SoapObject) response.getProperty("Sync_ISM_MGR_DataResult");
                    //					Utility.saveLogInTextFile(mContext, Sync_ISM_MGR_Data, "Sync_ISM_MGR_DataResult");
                    //					System.out.println("SOAP OBJECT  :  "+Sync_ISM_MGR_Data.toString());
                    try {
                        /** Data for Document Table**/
                        SoapObject Document_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Document_List");
                        ////System.out.println("Document_List=="+Document_List.toString());
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllDocument();
                        for (int i = 0; i < Document_List.getPropertyCount(); i++) {
                            SoapObject Document = (SoapObject) Document_List.getProperty(i);

                            String docId = Document.getProperty("DOC_ID").toString().replace("anyType{}", "");
                            String parentId = Document.getProperty("PARENT_ID").toString().replace("anyType{}", "");
                            String fileName = Document.getProperty("NAME").toString().replace("anyType{}", "");
                            String fileType = Document.getProperty("TYPE").toString().replace("anyType{}", "");
                            String fileLink = Document.getProperty("FILE_LINK").toString().replace("anyType{}", "");
                            fileLink = fileLink.replace(" ", "%20");

                            ////System.out.println("docId=="+docId);
                            MyDatabaseInstanceHolder.getDatabaseHelper().insertDocumentRecords(docId, parentId, fileName, fileType, fileLink);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ActivityStringInfo.strNewsBanner = Sync_ISM_MGR_Data.getProperty("NewsBannerList").toString().replace("anyType{}", "");
                    ////System.out.println("NewsBannerList=="+Sync_ISM_MGR_Data.getProperty("NewsBannerList"));

                    try {
                        Utility.setBanner(context);
                        //Broadcast to MainActivity to refresh News Banner
                        Intent myIntent = new Intent("appRefreshed");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    strDate = Sync_ISM_MGR_Data.getProperty("Last_Sync_Date").toString().replace("anyType{}", "");
                    ////System.out.println("LastSyncDate=="+Sync_ISM_MGR_Data.getProperty("Last_Sync_Date"));
                    ActivityStringInfo.strLastSyncDate = strDate;

                    ActivityStringInfo.strDocRights = Sync_ISM_MGR_Data.getProperty("DocRights").toString().replace("anyType{}", "");
                    ActivityStringInfo.strLogRights = Sync_ISM_MGR_Data.getProperty("DailyLogRights").toString().replace("anyType{}", "");
                    ActivityStringInfo.strSuggestright = Sync_ISM_MGR_Data.getProperty("Suggestright").toString().replace("anyType{}", "");

                    ////System.out.println(" **** **** DocRights = "+ActivityStringInfo.strDocRights);
                    ////System.out.println(" **** **** LogRights = "+ActivityStringInfo.strLogRights);

                    ActivityStringInfo.strDailyLogHour = Sync_ISM_MGR_Data.getProperty("DAILY_LOG_HOURS").toString().replace("anyType{}", "");
                    ////System.out.println("ActivityStringInfo.strDailyLogHour===="+ActivityStringInfo.strDailyLogHour);
                    ActivityStringInfo.strMandatoryMsgRight = Sync_ISM_MGR_Data.getProperty("MandatoryMsgRight").toString().replace("anyType{}", "");
                    ////System.out.println("ActivityStringInfo.strMandatoryMsgRight===="+ActivityStringInfo.strMandatoryMsgRight);

                    String mobileNum = Sync_ISM_MGR_Data.getProperty("Mobile_Num").toString().replace("anyType{}", "");
                    String notifyEmail = Sync_ISM_MGR_Data.getProperty("Notify_Email").toString().replace("anyType{}", "");
                    String notifySMS = Sync_ISM_MGR_Data.getProperty("Notify_Sms").toString().replace("anyType{}", "");
                    String secQues1 = Sync_ISM_MGR_Data.getProperty("Sec_Ques_1").toString().replace("anyType{}", "");
                    String secQues2 = Sync_ISM_MGR_Data.getProperty("Sec_Ques_2").toString().replace("anyType{}", "");
                    String secQues3 = Sync_ISM_MGR_Data.getProperty("Sec_Ques_3").toString().replace("anyType{}", "");
                    String secAns1 = Sync_ISM_MGR_Data.getProperty("Sec_Ans_1").toString().replace("anyType{}", "");
                    String secAns2 = Sync_ISM_MGR_Data.getProperty("Sec_Ans_2").toString().replace("anyType{}", "");
                    String secAns3 = Sync_ISM_MGR_Data.getProperty("Sec_Ans_3").toString().replace("anyType{}", "");
                    String email = Sync_ISM_MGR_Data.getProperty("Email").toString().replace("anyType{}", "");

                    ////System.out.println("Email ---------- "+email);
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateUsersRecords(strDate, ActivityStringInfo.strDocRights, ActivityStringInfo.strLogRights, ActivityStringInfo.strDailyLogHour, mobileNum, notifyEmail, notifySMS, secQues1, secQues2, secQues3, secAns1, secAns2, secAns3, email, ActivityStringInfo.strMandatoryMsgRight);

                    try {
                        ///////Update delete date of Message
                        String strMsgDeleteId = Sync_ISM_MGR_Data.getProperty("MessageDeleteID").toString().replace("anyType{}", "");
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
                        String strMsgReadId = Sync_ISM_MGR_Data.getProperty("MessageReadID").toString().replace("anyType{}", "");
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
                        /** Data for Message Table**/
                        ActivityStringInfo.strMandatoryMessageId.clear();
                        SoapObject Message_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Emessage_List");
                        //						Utility.saveLogInTextFile(mContext, Message_List, "Emessage_List");
                        //						System.out.println("Emessage_List=="+Message_List.toString());
                        //MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageList();

                        String temp = Message_List.toString();
                        for (int i = 0; i < Message_List.getPropertyCount(); i++) {

                            SoapObject Emessage = (SoapObject) Message_List.getProperty(i);
                            String messageId = Emessage.getProperty("MESSAGE_ID").toString().replace("anyType{}", "");
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
                            //	Log.v("Synchronization.java", " " + subject + " " + replyUserId + " " + replyUserName);
                            //						System.out.println("Emessaget=="+Emessage.toString());
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetail(messageId);

                            if (c1.getCount() == 0) {
                                if (type.equals(StaticVariables.MANDATORY_MESSAGE))
                                    ActivityStringInfo.strMandatoryMessageId.add(messageId);
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
                        SoapObject Message_To_User_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("EMessage_To_User_List");
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

                    try {
                        /** Data for Message Attachment Table **/
                        SoapObject Message_Attachment_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("EMessage_Attachment_List");
                        ////System.out.println("EMessage_Attachment_List=="+Message_Attachment_List.toString());
                        //MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageAttachmentList();
                        for (int i = 0; i < Message_Attachment_List.getPropertyCount(); i++) {
                            SoapObject EMessage_Attachment = (SoapObject) Message_Attachment_List.getProperty(i);
                            ////System.out.println("Emessage=="+EMessage_Attachment.toString());

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
                                ////System.out.println("Message attachment insert Successfully.");
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
                        /** Data for TRADE SHIFT Table **/
                        SoapObject Trade_Shift_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Trade_Shift_List");
                        ////System.out.println("TRADE_SHIFT =="+Trade_Shift_List.toString());
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllTradeShiftList();
                        for (int i = 0; i < Trade_Shift_List.getPropertyCount(); i++)
                        //for (int i = 0; i < 1; i++)
                        {
                            SoapObject tradeShift = (SoapObject) Trade_Shift_List.getProperty(i);

                            String shiftTradeId = tradeShift.getProperty("SHIFT_TRADE_ID").toString().replace("anyType{}", "");
                            String shiftId = tradeShift.getProperty("SHIFT_ID").toString().replace("anyType{}", "");
                            String date = tradeShift.getProperty("DATE").toString().replace("anyType{}", "");
                            String ofrUserId = tradeShift.getProperty("OFR_USER_ID").toString().replace("anyType{}", "");
                            String ofrUserName = tradeShift.getProperty("OFR_USER_NAME").toString().replace("anyType{}", "");
                            String ofrUserSkillLevel = tradeShift.getProperty("OFR_USER_SKILL_LEVEL").toString().replace("anyType{}", "");
                            String comment = tradeShift.getProperty("OFR_COMMENT").toString().replace("anyType{}", "");
                            String ofrUserIsOvertime = tradeShift.getProperty("OFR_USER_IS_OVERTIME").toString().replace("anyType{}", "");
                            String ofrShiftStartDatetime = tradeShift.getProperty("OFR_SHIFT_START_DATETIME").toString().replace("anyType{}", "");
                            String ofrShiftEndDateTime = tradeShift.getProperty("OFR_SHIFT_END_DATETIME").toString().replace("anyType{}", "");
                            String ofrSplitStartDateTime = tradeShift.getProperty("OFR_SPLIT_START_DATETIME").toString().replace("anyType{}", "");
                            String ofrSplitEndDateTime = tradeShift.getProperty("OFR_SPLIT_END_DATETIME").toString().replace("anyType{}", "");
                            String ofrWorkstation = tradeShift.getProperty("OFR_WORKSTATION").toString().replace("anyType{}", "");
                            String reqUserId = tradeShift.getProperty("REQ_USER_ID").toString().replace("anyType{}", "");
                            String reqUserName = tradeShift.getProperty("REQ_USER_NAME").toString().replace("anyType{}", "");
                            String reqUserSkillLevel = tradeShift.getProperty("REQ_USER_SKILL_LEVEL").toString().replace("anyType{}", "");
                            String reqComment = tradeShift.getProperty("REQ_COMMENT").toString().replace("anyType{}", "");
                            String reqUserIsOvertime = tradeShift.getProperty("REQ_USER_IS_OVERTIME").toString().replace("anyType{}", "");
                            String reqShiftStartDateTime = tradeShift.getProperty("REQ_SHIFT_START_DATETIME").toString().replace("anyType{}", "");
                            String reqShiftEndDateTime = tradeShift.getProperty("REQ_SHIFT_END_DATETIME").toString().replace("anyType{}", "");
                            String reqSplitStartDateTime = tradeShift.getProperty("REQ_SPLIT_START_DATETIME").toString().replace("anyType{}", "");
                            String reqSplitEndDateTime = tradeShift.getProperty("REQ_SPLIT_END_DATETIME").toString().replace("anyType{}", "");
                            String reqWorkstation = tradeShift.getProperty("REQ_WORKSTATION").toString().replace("anyType{}", "");


                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getTradeShiftList(shiftTradeId);

                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertTradeShiftData(shiftTradeId, shiftId, date, ofrUserId, ofrUserName,
                                        ofrUserSkillLevel, comment, ofrUserIsOvertime, ofrShiftStartDatetime,
                                        ofrShiftEndDateTime, ofrSplitStartDateTime, ofrSplitEndDateTime, ofrWorkstation,
                                        reqUserId, reqUserName, reqUserSkillLevel, reqComment,
                                        reqUserIsOvertime, reqShiftStartDateTime, reqShiftEndDateTime, reqSplitStartDateTime,
                                        reqSplitEndDateTime, reqWorkstation);
                                ////System.out.println("Trade_Shift_List insert Successfully.");
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        /** Data for SHIFT OFFERING Table **/
                        SoapObject Shift_Offering_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Shift_Offering_List");
                        ////System.out.println("++++++ Shift_Offering_List +++++++\n"+Shift_Offering_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllShiftOfferingList();
                        for (int i = 0; i < Shift_Offering_List.getPropertyCount(); i++)
                        //for (int i = 0; i < 1; i++)
                        {
                            SoapObject shiftOffering = (SoapObject) Shift_Offering_List.getProperty(i);

                            String shiftTradeId = shiftOffering.getProperty("SHIFT_TRADE_ID").toString().replace("anyType{}", "");
                            String shiftId = shiftOffering.getProperty("SHIFT_ID").toString().replace("anyType{}", "");
                            String date = shiftOffering.getProperty("DATE").toString().replace("anyType{}", "");
                            String userId = shiftOffering.getProperty("USER_ID").toString().replace("anyType{}", "");
                            String userName = shiftOffering.getProperty("USER_NAME").toString().replace("anyType{}", "");
                            String skillLevel = shiftOffering.getProperty("SKILL_LEVEL").toString().replace("anyType{}", "");
                            String isOvertime = shiftOffering.getProperty("IS_OVERTIME").toString().replace("anyType{}", "");
                            String comment = shiftOffering.getProperty("COMMENT").toString().replace("anyType{}", "");
                            String shiftStartDateTime = shiftOffering.getProperty("SHIFT_START_DATETIME").toString().replace("anyType{}", "");
                            String shiftEndDateTime = shiftOffering.getProperty("SHIFT_END_DATETIME").toString().replace("anyType{}", "");
                            String splitStartDateTime = shiftOffering.getProperty("SPLIT_START_DATETIME").toString().replace("anyType{}", "");
                            String splitEndDateTime = shiftOffering.getProperty("SPLIT_END_DATETIME").toString().replace("anyType{}", "");
                            String workstation = shiftOffering.getProperty("WORKSTATION").toString().replace("anyType{}", "");
                            String type = shiftOffering.getProperty("TYPE").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftOfferingList(shiftTradeId);

                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftOfferingData(shiftTradeId, shiftId, date, userId, userName, skillLevel,
                                        isOvertime, comment, shiftStartDateTime, shiftEndDateTime,
                                        splitStartDateTime, splitEndDateTime, workstation, type);
                                ////System.out.println("Shift_Offering_List insert Successfully.");

                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for SHIFT REQUEST Table **/
                        SoapObject Shift_Request_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Shift_Request_List");
                        ////System.out.println("++++++ Shift_Request_List +++++++\n"+Shift_Request_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllShiftRequestList();
                        for (int i = 0; i < Shift_Request_List.getPropertyCount(); i++)
                        //for (int i = 0; i < 1; i++)
                        {
                            SoapObject shiftRequest = (SoapObject) Shift_Request_List.getProperty(i);

                            String shiftTradeId = shiftRequest.getProperty("SHIFT_TRADE_ID").toString().replace("anyType{}", "");
                            String shiftId = shiftRequest.getProperty("SHIFT_ID").toString().replace("anyType{}", "");
                            String userId = shiftRequest.getProperty("USER_ID").toString().replace("anyType{}", "");
                            String userName = shiftRequest.getProperty("USER_NAME").toString().replace("anyType{}", "");
                            String skillLevel = shiftRequest.getProperty("SKILL_LEVEL").toString().replace("anyType{}", "");
                            String comment = shiftRequest.getProperty("COMMENT").toString().replace("anyType{}", "");
                            String isOvertime = shiftRequest.getProperty("IS_OVERTIME").toString().replace("anyType{}", "");
                            String type = shiftRequest.getProperty("TYPE").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftRequestList(shiftTradeId, shiftId, userId);

                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftRequestData(shiftTradeId, shiftId, userId, userName, skillLevel,
                                        comment, isOvertime, type);
                                ////System.out.println("Shift_Request_List insert Successfully.");
                            }
                            c1.close();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for REQUEST OFF Table **/
                        SoapObject Request_Off_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Request_Off_List");
                        ////System.out.println(" Request_Off_List == "+Request_Off_List.toString());
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllRequestOffList();
                        for (int i = 0; i < Request_Off_List.getPropertyCount(); i++)
                        //for (int i = 0; i < 1; i++)
                        {
                            SoapObject requestOff = (SoapObject) Request_Off_List.getProperty(i);

                            String id = requestOff.getProperty("ID").toString().replace("anyType{}", "");
                            String userId = requestOff.getProperty("USER_ID").toString().replace("anyType{}", "");
                            String userName = requestOff.getProperty("USER_NAME").toString().replace("anyType{}", "");
                            String skillLevel = requestOff.getProperty("SKILL_LEVEL").toString().replace("anyType{}", "");
                            String date = requestOff.getProperty("DATE").toString().replace("anyType{}", "");
                            String comment = requestOff.getProperty("COMMENT").toString().replace("anyType{}", "");
                            String leaveDate = requestOff.getProperty("LEAVE_DATE").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getRequestOffList(id);

                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertRequestOffData(id, userId, userName, skillLevel, date, comment, leaveDate);
                                ////System.out.println("Request_Off_List insert Successfully.");
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for ADDRESS BOOK Table **/
                        SoapObject Distribution_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Distribution_List");
                        ////System.out.println("Distribution_List=="+Distribution_List.toString());
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllAddressBook();//newly added 21/01/16
                        for (int i = 0; i < Distribution_List.getPropertyCount(); i++) {
                            SoapObject EAddress_Book = (SoapObject) Distribution_List.getProperty(i);
                            String userID = EAddress_Book.getProperty("DistributionListID").toString().replace("anyType{}", "");
                            String firstName = EAddress_Book.getProperty("ListName").toString().replace("anyType{}", "");
                            String orgID = EAddress_Book.getProperty("OrgId").toString().replace("anyType{}", "");
                            String orgName = EAddress_Book.getProperty("OrgName").toString().replace("anyType{}", "");

                            String positionTitle = "";
                            String lastName = "";
                            String status = "";

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getAddressBookRecord(userID, orgID);
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertAddressBookData(userID, firstName, lastName, status, orgID, positionTitle, orgName);
                                ////System.out.println("Distribution_List insert Successfully.");
                            } else {
                                while (c1.moveToNext()) {
                                    if (!c1.getString(1).equals(firstName) || !c1.getString(2).equals(lastName)) {
                                        MyDatabaseInstanceHolder.getDatabaseHelper().updateAddressBookRecords(userID, firstName, lastName);
                                    }
                                }
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        SoapObject AddressBookUser_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("AddressBookUser_List");
                        ////System.out.println("AddressBookUser_List=="+AddressBookUser_List.toString());
//						MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllAddressBook();newly removed 21/01/16
                        for (int i = 0; i < AddressBookUser_List.getPropertyCount(); i++) {
                            SoapObject EAddress_Book = (SoapObject) AddressBookUser_List.getProperty(i);
                            String userID = EAddress_Book.getProperty("userid").toString().replace("anyType{}", "") + "-0";
                            String firstName = EAddress_Book.getProperty("FirstName").toString().replace("anyType{}", "");
                            String lastName = EAddress_Book.getProperty("LastName").toString().replace("anyType{}", "");
                            String status = EAddress_Book.getProperty("Active").toString().replace("anyType{}", "");
                            String OrgID = EAddress_Book.getProperty("OrgID").toString().replace("anyType{}", "");
                            String positionTitle = EAddress_Book.getProperty("PositionTitle").toString().replace("anyType{}", "");
                            String orgName = EAddress_Book.getProperty("OrgName").toString().replace("anyType{}", "");

                            if (ActivityStringInfo.strFirstName.equals(firstName)) {
                                ActivityStringInfo.strCompanyName = orgName;
                                MyDatabaseInstanceHolder.getDatabaseHelper().updateUserCompanyName(orgName);
                            }
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getAddressBookRecord(userID, OrgID);
                            ////System.out.println("userID==="+userID+"===firstName==="+firstName+"===lastName"+lastName+"===status==="+status);
                            if (c1.getCount() == 0) {
                                if (!status.equals("0"))
                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertAddressBookData(userID, firstName, lastName, status, OrgID, positionTitle, orgName);
                                ////System.out.println("AddressBookUser_List insert Successfully.");
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

                    try {
                        /** Data for MEETING Table **/
                        String strEventId = "";
                        SoapObject Meeting_Detail_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Meeting_Detail_List");
                        ////System.out.println("Meeting_Detail_List=="+Meeting_Detail_List.toString());
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
                        for (int i = 0; i < Meeting_Detail_List.getPropertyCount(); i++)
                        //for (int i = 0; i < 1; i++)
                        {
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

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMeetingList(meetingId);
                            if (c1.getCount() == 0) {

                                if (iTestCalendarID != 0) {
                                    //Newly added
                                    //								if(Utility.compareLastSyncDateForShift(context, meetingDate))
                                    //									{
                                    Uri newEvent = MakeNewMeetingCalendarEntry(iTestCalendarID, date, startTime, endTime, title, location, owner, isMandatory);
                                    int eventID = Integer.parseInt(newEvent.getLastPathSegment());//removable
                                    strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                    //System.out.println("eventID add successfuuly==="+eventID);
                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertMeetingData(meetingId, title, owner, isMandatory, date, startTime, endTime, location, status, "" + eventID);
                                    Utility.addEntryInCalendarLog(mContext, "Add", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventID + " EventName:- ISM MEETING Date:- " + meetingDate + " " + startTime);
                                    //System.out.println("Meeting_Detail_List insert Successfully.");
                                    //								}
                                }

                            } else {
                                while (c1.moveToNext()) {
                                    String eventId = c1.getString(9);
                                    if (!c1.getString(4).equals(date) || !c1.getString(5).equals(startTime) || !c1.getString(6).equals(endTime) || !c1.getString(8).equals(status)) {
                                        if (iTestCalendarID != 0) {
                                            //Newly added
                                            //										if(Utility.compareLastSyncDateForShift(context, meetingDate))
                                            //											{
                                            DeleteCalendarEntry(Integer.parseInt(eventId));
                                            Uri newEvent = MakeNewMeetingCalendarEntry(iTestCalendarID, date, startTime, endTime, title, location, owner, isMandatory);
                                            int eventID = Integer.parseInt(newEvent.getLastPathSegment()); //removable
                                            strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                            //System.out.println("Meeting eventID add successfuuly==="+eventID);
                                            MyDatabaseInstanceHolder.getDatabaseHelper().updateMeetingRecords(meetingId, date, startTime, endTime, status, "" + eventID);//removable
                                            Utility.addEntryInCalendarLog(mContext, "Update", "CalId:- " + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + " CalName:- " + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + " EventId:- " + eventID + " EventName:- ISM MEETING Date:- " + meetingDate + " " + startTime);
                                            //											}
                                        }

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
                                    String eventDate = c1.getString(4);
                                    String meetingDate = "";

                                    Date objDate = currentDateFormat.parse(eventDate);
                                    Date cDate = currentDateFormat.parse(currentDateFormat.format(Calendar.getInstance().getTime()));
                                    meetingDate = newDateFormat.format(objDate);
                                    if (iTestCalendarID != 0) {
                                        if (!Utility.compareLastSyncDateForShift(context, meetingDate) || objDate.after(cDate)) {
                                            if (!eventId.equals(""))
                                                DeleteCalendarEntry(Integer.parseInt(eventId));

                                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteMsgMeetingRecord(strMeetingId);
                                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteMeetingRecord(strMeetingId);//removable
                                        }
                                    }
                                }
                                c1.close();
                            }
                        }

                        ////System.out.println("strEventId=="+strEventId);

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

                    /** Data for SHIFT TASK Table **/
                    String strEventId = "";
                    try {
                        /** Data for SHIFT TASK Table **/
                        SoapObject Shift_Task_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Shift_Task_List");
                        //System.out.println("Shift_Task_List=="+Shift_Task_List.toString());
                        //MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageAttachmentList();
                        if (null != Shift_Task_List) {
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
                                        MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftTaskData(shiftId, taskId, title, Description);
                                    }
                                } else {
                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertShiftTaskData(shiftId, taskId, title, Description);
                                }
                                c1.close();
                            }
                        }

                        /** Data for MY_SHIFT Table **/
                        SoapObject Shift_Detail_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Shift_Detail_Emp_List");
                        //System.out.println("Shift_Detail_List=="+Shift_Detail_List.toString());
                        if (null != Shift_Detail_List) {
                            HashSet<String> hashShiftId = new HashSet<String>();
                            try {

                                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMyShiftList();
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

                                Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMyShiftDetail(shiftId);

                                if (c1.getCount() == 0) {

                                    if (iTestCalendarID != 0) {
                                        Uri newEvent = null;
//                                        if (!SplitStartTime.equals(""))
//                                            newEvent = MakeNewCalendarEntry(context, iTestCalendarID, Date, ShiftStartTime, SplitEndTime, "", ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
//                                        else
//                                            newEvent = MakeNewCalendarEntry(context, iTestCalendarID, Date, ShiftStartTime, ShiftEndTime, "", ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
//                                        int eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                        int eventID = 0;
                                        System.out.println("shift eventID add successfuuly===" + eventID);

//                                        strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
                                        MyDatabaseInstanceHolder.getDatabaseHelper().insertMyShiftData(shiftId, Date, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, "" + eventID, "O", Schedule_Name, Position_Title);
                                        //System.out.println("Shift_Detail_List insert Successfully.");
                                    }

                                } else {
                                    while (c1.moveToNext()) {
                                        if (!c1.getString(2).equals(ShiftStartTime) || !c1.getString(3).equals(ShiftEndTime) || !c1.getString(4).equals(SplitStartTime) || !c1.getString(5).equals(SplitEndTime) || !c1.getString(9).equals(Status)) {
                                            String eventId = c1.getString(10);
                                            if (iTestCalendarID != 0) {
                                                DeleteCalendarEntry(Integer.parseInt(eventId));
                                                Uri newEvent = null;
//                                                if (!SplitStartTime.equals(""))
//                                                    newEvent = MakeNewCalendarEntry(context, iTestCalendarID, Date, ShiftStartTime, SplitEndTime, "", ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
//                                                else
//                                                    newEvent = MakeNewCalendarEntry(context, iTestCalendarID, Date, ShiftStartTime, ShiftEndTime, "", ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Trainee, WorkStation, IsOverTime, Status, Schedule_Name, taskList, Position_Title);
//                                                int eventID = Integer.parseInt(newEvent.getLastPathSegment());
                                                int eventID = 0;
                                                System.out.println("eventID add successfuuly===" + eventID);

//                                                strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";

                                                MyDatabaseInstanceHolder.getDatabaseHelper().updateMyShiftRecords(shiftId, ShiftStartTime, ShiftEndTime, SplitStartTime, SplitEndTime, Status, "" + eventID, Position_Title);
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

                                    Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMyShiftDetail(strShiftId);
                                    while (c1.moveToNext()) {
                                        String shiftDate = "";

                                        String eventId = c1.getString(c1.getColumnIndex(DatabaseConstant.key_CAL_EVENT_ID));
                                        String Date = c1.getString(c1.getColumnIndex(DatabaseConstant.key_DATE));

                                        Date objDate = currentDateFormat.parse(Date);
                                        Date cDate = currentDateFormat.parse(currentDateFormat.format(Calendar.getInstance().getTime()));

                                        shiftDate = newDateFormat.format(objDate);

                                        if (iTestCalendarID != 0) {
                                            if (!Utility.compareLastSyncDateForShift(context, shiftDate) || objDate.after(cDate)) {
                                                System.out.println("delete eventId===" + eventId);
                                                System.out.println("delete strShiftId===" + strShiftId);
                                                if (!eventId.equals(""))
                                                    DeleteCalendarEntry(Integer.parseInt(eventId));

                                                MyDatabaseInstanceHolder.getDatabaseHelper().deleteMyShiftRecord(strShiftId);
                                            }
                                        }

                                    }
                                    c1.close();
                                }
                            }
                        }

                        /** Data for MY_LEAVE REQUEST Table **/
                        SoapObject Leave_Request_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Leave_Request_Emp_List"); //Leave_Request_Mgr_List

                        if (null != Leave_Request_List) {
                            //System.out.println("Meeting_Detail_List=="+Meeting_Detail_List.toString());
                            HashSet<String> hashLeaveRequestId = new HashSet<String>();
                            try {

                                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getAllMyLeaveRequestList();
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
//                                String userName = Leave_Request.getProperty("USER_NAME") == null ? "" : Leave_Request.getProperty("USER_NAME").toString().replace("anyType{}", "");
                                String startDate = Leave_Request.getProperty("START_DATE").toString().replace("anyType{}", "");
                                String endDate = Leave_Request.getProperty("END_DATE").toString().replace("anyType{}", "");
                                String status = Leave_Request.getProperty("STATUS").toString().replace("anyType{}", "");

                                try {
                                    Date objDate = currentDateFormat.parse(endDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (hashLeaveRequestId.contains(leaveId)) {
                                    hashLeaveRequestId.remove(leaveId);
                                }

                                Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMyLeaveRequestList(leaveId);
                                if (c1.getCount() == 0) {

                                    int eventID = 0;
                                    //Newly code commented to test duplicate entries of Request off

//                                    if (iTestCalendarID != 0) {
//                                        Uri newEvent = MakeNewLeaveRequestCalendarEntry(iTestCalendarID, startDate, endDate, ActivityStringInfo.strFirstName);
//                                        eventID = Integer.parseInt(newEvent.getLastPathSegment());
//                                        System.out.println("LeaveEventID add successfuuly===" + eventID);
//
//                                        strEventId += eventID + "-" + ActivityStringInfo.strCalIdName.substring(0, ActivityStringInfo.strCalIdName.indexOf("-")) + "+" + ActivityStringInfo.strCalIdName.substring(ActivityStringInfo.strCalIdName.indexOf("-") + 1) + "\n";
//                                        MyDatabaseInstanceHolder.getDatabaseHelper().insertMyLeaveRequestData(leaveId, userId, startDate, endDate, status, "" + eventID);
//                                        //System.out.println("Meeting_Detail_List insert Successfully.");
//                                    }
                                    //Newly added code only insert myleaveRequest into local database only.

                                    MyDatabaseInstanceHolder.getDatabaseHelper().insertMyLeaveRequestData(leaveId, userId, startDate, endDate, status, "" + eventID);
                                }
                                c1.close();

                                //Update My Leave Request status in case of pending
                                Cursor c2 = MyDatabaseInstanceHolder.getDatabaseHelper().getMyLeaveRequestbyStatus(leaveId, status);
                                if (c2.getCount() == 0) {
                                    MyDatabaseInstanceHolder.getDatabaseHelper().updateMyLeaveRequestStatus(leaveId, status);
                                }
                                c2.close();
                            }
                            if (hashLeaveRequestId.size() > 0) {
                                Iterator<String> it = hashLeaveRequestId.iterator();
                                while (it.hasNext()) {
                                    String strLeaveId = it.next();

                                    Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMyLeaveRequestList(strLeaveId);
                                    while (c1.moveToNext()) {
                                        String eventId = c1.getString(5);
                                        if (iTestCalendarID != 0) {
                                            if (!eventId.equals("")) {
//									DeleteCalendarEntry(Integer.parseInt(eventId),context); //Newly removed
                                            }

//								 MyDatabaseInstanceHolder.getDatabaseHelper().deleteLeaveRecord(strLeaveId);//Newly removed
                                        }

                                    }
                                    c1.close();
                                }
                            }
                        }

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

                    ////
                    try {
                        /** Data for LOG_PRODUCTIVITY_FIELDS Table **/
                        SoapObject log_Productivity_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Productivity_List");
                        ////System.out.println(" Log_Productivity_List == "+log_Productivity_List.toString());
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogProductivityFieldsList();
                        for (int i = 0; i < log_Productivity_List.getPropertyCount(); i++) {
                            SoapObject productivityField = (SoapObject) log_Productivity_List.getProperty(i);

                            String fieldId = productivityField.getProperty("FIELDID").toString().replace("anyType{}", "");
                            String fieldTitle = productivityField.getProperty("FIELDTITLE").toString().replace("anyType{}", "");
                            String fieldType = productivityField.getProperty("FIELDTYPE").toString().replace("anyType{}", "");
                            String trackAnnually = productivityField.getProperty("TRACKANNUALLY").toString().replace("anyType{}", "");
                            String isActive = productivityField.getProperty("ISACTIVE").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogProductivityFieldsRecord(fieldId);
                            ////System.out.println("Log_Productivity_List : in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogProductivityFieldData(fieldId, fieldTitle, fieldType, trackAnnually, isActive);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for LOG_USERTYPE Table **/
                        SoapObject log_Usertype_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Usertype_List");
                        ////System.out.println("++++++ Log_Usertype_List +++++++\n"+log_Usertype_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogUserTypeList();
                        for (int i = 0; i < log_Usertype_List.getPropertyCount(); i++) {
                            SoapObject logUserType = (SoapObject) log_Usertype_List.getProperty(i);

                            String userTypeId = logUserType.getProperty("USERTYPEID").toString().replace("anyType{}", "");
                            String userType = logUserType.getProperty("USERTYPE").toString().replace("anyType{}", "");
                            String typeDescription = logUserType.getProperty("TYPEDESCRIPTION").toString().replace("anyType{}", "");
                            String flagBank = logUserType.getProperty("FLAGBANK").toString().replace("anyType{}", "");
                            String isActive = logUserType.getProperty("ISACTIVE").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogUsertypeRecord(userTypeId);
                            ////System.out.println("UserType Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogUserTypeData(userTypeId, userType, typeDescription, flagBank, isActive);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for LOG_COMMUNICATION_DET Table **/
                        SoapObject log_Communication_Det_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Communication_Det_List");
                        ////System.out.println("++++++ Log_Communication_Det_List +++++++\n"+log_Communication_Det_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogCommunicationDetList();
                        for (int i = 0; i < log_Communication_Det_List.getPropertyCount(); i++) {
                            SoapObject logCommDet = (SoapObject) log_Communication_Det_List.getProperty(i);

                            String id = logCommDet.getProperty("ID").toString().replace("anyType{}", "");
                            String login = logCommDet.getProperty("LOGID").toString().replace("anyType{}", "");
                            String commid = logCommDet.getProperty("COMMID").toString().replace("anyType{}", "");
                            String flagStatus = logCommDet.getProperty("FLAGSTATUS").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogCommunicationDetRecord(id);
                            ////System.out.println("LogCommunicationDet Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogCommunicationDetData(id, login, commid, flagStatus);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for LOG_EMP_RELATION_DESC Table **/
                        SoapObject log_Emp_Relation_Desc_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Emp_Relation_Desc_List");
                        ////System.out.println("++++++ Log_Emp_Relation_Desc_List +++++++\n"+log_Emp_Relation_Desc_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogEmpRelationList();
                        for (int i = 0; i < log_Emp_Relation_Desc_List.getPropertyCount(); i++) {
                            SoapObject logEmpRelation = (SoapObject) log_Emp_Relation_Desc_List.getProperty(i);

                            String empLogId = logEmpRelation.getProperty("EMPLOGID").toString().replace("anyType{}", "");
                            String login = logEmpRelation.getProperty("LOGID").toString().replace("anyType{}", "");
                            String empFieldId = logEmpRelation.getProperty("EMPFIELDID").toString().replace("anyType{}", "");
                            String employee = logEmpRelation.getProperty("EMPLOYEE").toString().replace("anyType{}", "");
                            String addedById = logEmpRelation.getProperty("ADDEDBYID").toString().replace("anyType{}", "");
                            String description = logEmpRelation.getProperty("DESCRIPTION").toString().replace("anyType{}", "");

                            ////System.out.println("Log_Emp_Relation_Desc_List Record====EMPLOGID ============"+empLogId);
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogEmpRelationDescRecord(empLogId);
                            //							//System.out.println("Log_Emp_Relation_Desc_List Record in count====get ============"+c1.getCount());

                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogEmpRelationDescData(empLogId, login, empFieldId, employee, addedById, description);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for LOG_FOLLOW_UP_LOG Table **/
                        SoapObject log_Follow_Up_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Follow_Up_Log_List");
                        ////System.out.println("++++++ Log_Follow_Up_List +++++++\n"+log_Follow_Up_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogFollowUpList();
                        for (int i = 0; i < log_Follow_Up_List.getPropertyCount(); i++) {
                            SoapObject logFollowUp = (SoapObject) log_Follow_Up_List.getProperty(i);

                            String id = logFollowUp.getProperty("ID").toString().replace("anyType{}", "");
                            String login = logFollowUp.getProperty("LOGID").toString().replace("anyType{}", "");
                            String followUpId = logFollowUp.getProperty("FOLLOWUPID").toString().replace("anyType{}", "");
                            String flagStatus = logFollowUp.getProperty("FLAGSTATUS").toString().replace("anyType{}", "");
                            String taskId = logFollowUp.getProperty("TASKID").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogFollowUpRecord(id);
                            ////System.out.println("Log_Follow_Up_List Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogFollowUpData(id, login, followUpId, flagStatus, taskId);
                            }
                            c1.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for LOG_MAINTENANCE_DET Table **/
                        SoapObject log_Maintenance_Det_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Maintenance_Det_List");
                        ////System.out.println("++++++ Log_Maintenance_Det_List +++++++\n"+log_Maintenance_Det_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogMaintenanceDetList();
                        for (int i = 0; i < log_Maintenance_Det_List.getPropertyCount(); i++) {
                            SoapObject logMaintenance = (SoapObject) log_Maintenance_Det_List.getProperty(i);

                            String mrrDetailsId = logMaintenance.getProperty("MRRDETAILSID").toString().replace("anyType{}", "");
                            String login = logMaintenance.getProperty("LOGID").toString().replace("anyType{}", "");
                            String mrrId = logMaintenance.getProperty("MRRID").toString().replace("anyType{}", "");
                            String employee = logMaintenance.getProperty("EMPLOYEE").toString().replace("anyType{}", "");
                            String description = logMaintenance.getProperty("DESCRIPTION").toString().replace("anyType{}", "");
                            String taskId = logMaintenance.getProperty("TASKID").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogMaintenanceDetRecord(mrrDetailsId);
                            ////System.out.println("Log_Maintenance_Det_List Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogMaintenanceDetData(mrrDetailsId, login, mrrId, employee, description, taskId);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        /** Data for LOG_MAINTENANCE_LOG Table **/
                        SoapObject log_Maintenance_log_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Maintenance_Log_List");
                        ////System.out.println("++++++ Log_Maintenance_log_List +++++++\n"+log_Maintenance_log_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogMaintenanceLogList();
                        for (int i = 0; i < log_Maintenance_log_List.getPropertyCount(); i++) {
                            SoapObject logMaintenanceLog = (SoapObject) log_Maintenance_log_List.getProperty(i);

                            String mrrLogId = logMaintenanceLog.getProperty("MRRLOGID").toString().replace("anyType{}", "");
                            String login = logMaintenanceLog.getProperty("LOGID").toString().replace("anyType{}", "");
                            String mrrId = logMaintenanceLog.getProperty("MRRID").toString().replace("anyType{}", "");
                            String flagstatus = logMaintenanceLog.getProperty("FLAGSTATUS").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogMaintenanceLogRecord(mrrLogId);
                            ////System.out.println("Log_Maintenance_log_List Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogMaintenanceLogData(mrrLogId, login, mrrId, flagstatus);
                            }
                            c1.close();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for LOG_NOTES Table **/
                        SoapObject log_notes_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Notes_List");
                        ////System.out.println("++++++ log_notes_List +++++++\n"+log_notes_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogNotesList();
                        for (int i = 0; i < log_notes_List.getPropertyCount(); i++) {
                            SoapObject lognotes = (SoapObject) log_notes_List.getProperty(i);

                            String id = lognotes.getProperty("ID").toString().replace("anyType{}", "");
                            String login = lognotes.getProperty("LOGID").toString().replace("anyType{}", "");
                            String employee = lognotes.getProperty("EMPLOYEE").toString().replace("anyType{}", "");
                            String description = lognotes.getProperty("DESCRIPTION").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogNotesRecord(id);
                            ////System.out.println("log_notes_List Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogNotesData(id, login, employee, description);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        /** Data for LOG_PRODUCTIVITY_DET Table **/
                        SoapObject log_Productivity_Det_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Productivity_Det_List");
                        ////System.out.println("++++++ log_Productivity_Det_List Row Count +++++++\n"+log_Productivity_Det_List.getPropertyCount());
                        ////System.out.println("++++++ log_Productivity_Det_List +++++++\n"+log_Productivity_Det_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogProductivityDetList();
                        for (int i = 0; i < log_Productivity_Det_List.getPropertyCount(); i++) {
                            SoapObject logProducitivityDet = (SoapObject) log_Productivity_Det_List.getProperty(i);

                            String id = logProducitivityDet.getProperty("ID").toString().replace("anyType{}", "");
                            String logId = logProducitivityDet.getProperty("LOGID").toString().replace("anyType{}", "");
                            String fieldId = logProducitivityDet.getProperty("FIELDID").toString().replace("anyType{}", "");
                            String fieldValue = logProducitivityDet.getProperty("FIELDVALUE").toString().replace("anyType{}", "");
                            //Log.v("Synchronization.java", " " + fieldValue);
                            String fieldType = logProducitivityDet.getProperty("FIELDTYPE").toString().replace("anyType{}", "");

                            //Log.v("Synchronization", "productivity det logid: " + logId + " id " + id + " fieldvalue " + fieldValue);

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogProductivityDetRecord(id);
                            ////System.out.println("log_Productivity_Det_List Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogProductivityDetData(id, logId, fieldId, fieldValue, fieldType);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        /** Data for LOG_QUALITY Table **/
                        SoapObject log_Quality_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Quality_List");
                        ////System.out.println("++++++ log_Quality_List +++++++\n"+log_Quality_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogQualityList();
                        for (int i = 0; i < log_Quality_List.getPropertyCount(); i++) {
                            SoapObject logQuality = (SoapObject) log_Quality_List.getProperty(i);

                            String qualityId = logQuality.getProperty("QUALITYID").toString().replace("anyType{}", "");
                            String login = logQuality.getProperty("LOGID").toString().replace("anyType{}", "");
                            String userId = logQuality.getProperty("USERID").toString().replace("anyType{}", "");
                            String complaint = logQuality.getProperty("COMPLAINT").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogQualityRecord(qualityId);
                            ////System.out.println("log_Quality_List Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogQualityData(qualityId, login, userId, complaint);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        /** Data for LOG_SAFETY_DESC Table **/
                        SoapObject log_Safety_Desc_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Safety_Desc_List");
                        ////System.out.println("++++++ log_Safety_Desc_List +++++++\n"+log_Safety_Desc_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogSafetyDescList();
                        for (int i = 0; i < log_Safety_Desc_List.getPropertyCount(); i++) {
                            SoapObject logSafety = (SoapObject) log_Safety_Desc_List.getProperty(i);

                            String id = logSafety.getProperty("ID").toString().replace("anyType{}", "");
                            String safetyLogId = logSafety.getProperty("SAFETYLOGID").toString().replace("anyType{}", "");
                            String employeeId = logSafety.getProperty("EMPLOYEEID").toString().replace("anyType{}", "");
                            String description = logSafety.getProperty("DESCRIPTION").toString().replace("anyType{}", "");
                            String addedById = logSafety.getProperty("ADDEDBYID").toString().replace("anyType{}", "");


                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogSafetyDescRecord(id);
                            if (c1.getCount() == 0) {
                                ////System.out.println("log_Safety_Desc_List Record in count====get ============"+c1.getCount());
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogSafetyDescData(id, safetyLogId, employeeId, description, addedById);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        /** Data for LOG_SAFETY_LOG Table **/
                        SoapObject log_Safety_log_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Safety_Log_List");
                        ////System.out.println("++++++ log_Safety_log_List +++++++\n"+log_Safety_log_List);
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogSafetyLogList();
                        for (int i = 0; i < log_Safety_log_List.getPropertyCount(); i++) {
                            SoapObject logSafetyLog = (SoapObject) log_Safety_log_List.getProperty(i);

                            String safetyLogId = logSafetyLog.getProperty("SAFETYLOGID").toString().replace("anyType{}", "");
                            String logid = logSafetyLog.getProperty("LOGID").toString().replace("anyType{}", "");
                            String safetyId = logSafetyLog.getProperty("SAFETYID").toString().replace("anyType{}", "");
                            String flagSafety = logSafetyLog.getProperty("FLAGSAFETY").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogSafetyLogRecord(safetyLogId);
                            ////System.out.println("log_Safety_log_List Record in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogSafetyLogData(safetyLogId, logid, safetyId, flagSafety);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        /** Data for LOG_DETAILS Table **/
                        SoapObject log_Detail_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Detail_List");
                        ////System.out.println("++++++ Log_Detail_List +++++++\n"+log_Detail_List);

                        //Log.v("Synchronization.java", "getInformation()======> Data for LOG_DETAILS Table;Log_Detail_List: "+log_Detail_List.getPropertyCount());
                        //Newly added
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogDetailsList();

                        //MyDatabaseInstanceHolder.getDatabaseHelper().deleteCompletedLogDetailsList();
                        for (int i = 0; i < log_Detail_List.getPropertyCount(); i++) {
                            SoapObject logDetail = (SoapObject) log_Detail_List.getProperty(i);

                            String logId = logDetail.getProperty("LOG_ID").toString().replace("anyType{}", "");
                            String logDate = logDetail.getProperty("LOG_DATE").toString().replace("anyType{}", "");
                            String createDate = logDetail.getProperty("CREATE_DATE").toString().replace("anyType{}", "");
                            String createUserId = logDetail.getProperty("CREATE_USER_ID").toString().replace("anyType{}", "");
                            String createUserName = logDetail.getProperty("CREATE_USER_NAME").toString().replace("anyType{}", "");
                            String userTypeId = logDetail.getProperty("USER_TYPE_ID").toString().replace("anyType{}", "");
                            String lastUpdateDate = logDetail.getProperty("LAST_UPD_DATE").toString().replace("anyType{}", "");
                            String status = logDetail.getProperty("STATUS").toString().replace("anyType{}", "");

                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogDetailRecord(logId);

                            //New added//Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogDetailRecordbyDate_UserId(logDate, createUserId);
                            //Log.v("Synchronization.java", "getInformation()======> Data for LOG_DETAILS Table; Status: "+status+" LogId: "+logId+" Log_date: "+logDate);
                            ////System.out.println("Log detials in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                //  Old //MyDatabaseInstanceHolder.getDatabaseHelper().insertLogDetailData(logId, logDate, createDate, createUserId, createUserName, userTypeId,lastUpdateDate,  "C");
                                //New
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogDetailData(logId, logDate, createDate, createUserId, createUserName, userTypeId, lastUpdateDate, status);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        /** Data for LOGIN_QUESTIONS Table **/
                        SoapObject Login_Questions_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("LoginQuestions_List");
                        ////System.out.println("\n\n****** ***** LoginQuestions_List=="+Login_Questions_List.toString());
                        for (int i = 0; i < Login_Questions_List.getPropertyCount(); i++) {
                            SoapObject login_Questions = (SoapObject) Login_Questions_List.getProperty(i);
                            String loginQuestionId = login_Questions.getProperty("LoginQuestionID").toString().replace("anyType{}", "");
                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLoginQuestionsList(loginQuestionId);
                            if (c1.getCount() == 0) {
                                String question = login_Questions.getProperty("Question").toString().replace("anyType{}", "");

                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLoginQuestionsData(loginQuestionId, question);
                                ////System.out.println("LoginQuestions_List insert Successfully.");
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /**Data for LOG_FLAGS**/
                    try {

                        SoapObject log_Flag_List = (SoapObject) Sync_ISM_MGR_Data.getProperty("Log_Flag_List");
                        ////System.out.println("++++++ Log_Detail_List +++++++\n"+log_Detail_List);

                        //Log.v("Synchronization.java", "getInformation()======> Data for LOG_DETAILS Table;Log_Detail_List: "+log_Detail_List.getPropertyCount());
                        //Newly added
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogFlagsList();

                        //MyDatabaseInstanceHolder.getDatabaseHelper().deleteCompletedLogDetailsList();
                        for (int i = 0; i < log_Flag_List.getPropertyCount(); i++) {
                            SoapObject logFlag = (SoapObject) log_Flag_List.getProperty(i);

                            String flagDescription = logFlag.getProperty("DESCRIPTION").toString().replace("anyType{}", "");
                            String flag = logFlag.getProperty("FLAG").toString().replace("anyType{}", "");
                            String flaggedDate = logFlag.getProperty("FLAGGEDDATE").toString().replace("anyType{}", "");
                            String flag_id = logFlag.getProperty("ID").toString().replace("anyType{}", "");
                            String flag_logid = logFlag.getProperty("LOGID").toString().replace("anyType{}", "");
                            String flag_userid = logFlag.getProperty("USERID").toString().replace("anyType{}", "");
                            String userName = logFlag.getProperty("USERNAME").toString().replace("anyType{}", "");


                            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogFlag(flag_id);

                            //New added//Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogDetailRecordbyDate_UserId(logDate, createUserId);
                            //	Log.v("Synchronization.java", "getInformation()======> Data for LOG_DETAILS Table; Status: "+status+" LogId: "+logId+" Log_date: "+logDate);
                            ////System.out.println("Log detials in count====get ============"+c1.getCount());
                            if (c1.getCount() == 0) {
                                //  Old //MyDatabaseInstanceHolder.getDatabaseHelper().insertLogDetailData(logId, logDate, createDate, createUserId, createUserName, userTypeId,lastUpdateDate,  "C");
                                //New
                                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogFlags(flagDescription, flag, flaggedDate, flag_id, flag_logid, flag_userid, userName);
                            }
                            c1.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        ActivityStringInfo.strLeaveRequestList = (SoapObject) Sync_ISM_MGR_Data.getProperty("Leave_Request_Mgr_List");

                        ActivityStringInfo.strShiftList = (SoapObject) Sync_ISM_MGR_Data.getProperty("Shift_Detail_List");

                        Utility.deleteDownloadFile(context);
                        if (strMsg.equals(""))
                            strMsg = "true";
                        context.startService(new Intent(context, NotificationServiceForShift.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            if (android.os.Build.VERSION.SDK_INT >= 14) {
                eventsUri = Uri.parse(Calendars.CONTENT_URI + "events");
                System.out.println("Synchronization class-->DeleteCalendarEntry----> New Api>=14");
            }
            eventsUri = Uri.parse(getCalendarUriBase() + "events");
            Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
            iNumRowsDeleted = mContext.getContentResolver().delete(eventUri, null, null);
        } catch (Exception e) {
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

    private Uri MakeNewCalendarEntry(Context context, int calId, String shiftDate, String starttime, String endtime, String userName, String ShiftStartTime, String ShiftEndTime, String SplitStartTime, String SplitEndTime, String Trainee, String WorkStation, String IsOverTime, String Status, String Schedule_Name, String TaskList, String Position_Tital) {
        Uri insertedUri = null;
        String strDescription;
        String titleDesc;
        try {

            if (userName != null && userName.length() > 0) {
                titleDesc = userName.substring(0, userName.indexOf(" ")) + " " + userName.substring(userName.indexOf(" "), userName.indexOf(" ") + 2).toUpperCase().replace(" ", "") + ".";
                strDescription = "\nUser Name : " + userName + "\n\n" + Schedule_Name + "\n" + Position_Tital + "\n" + WorkStation + "\n\nSHIFT Start : " + ShiftStartTime + "\nSHIFT End : " + ShiftEndTime + "\nSPLIT Start : " + SplitStartTime + "\nSPLIT End : " + SplitEndTime + "\n\nOVERTIME : " + IsOverTime + "\nTRAINEE : " + Trainee + "\nTASKS : " + TaskList + "\n\nSTATUS : " + Status;
            } else {
                titleDesc = ActivityStringInfo.strCompanyName.toUpperCase() + " SHIFT";
                strDescription = "\n" + Schedule_Name + "\n" + Position_Tital + "\n" + WorkStation + "\n\nSHIFT Start : " + ShiftStartTime + "\nSHIFT End : " + ShiftEndTime + "\nSPLIT Start : " + SplitStartTime + "\nSPLIT End : " + SplitEndTime + "\n\nOVERTIME : " + IsOverTime + "\nTRAINEE : " + Trainee + "\nTASKS : " + TaskList + "\n\nSTATUS : " + Status;
            }

            ContentValues event = new ContentValues();
            event.put("calendar_id", calId);
            event.put("title", titleDesc);


            if (IsOverTime.toLowerCase().equals("y"))
                IsOverTime = "Yes";
            else if (IsOverTime.toLowerCase().equals("n"))
                IsOverTime = "No";

            event.put("description", strDescription + "\n\nThis event can be automatically deleted when a schedule is updated. Please do not add any notes.");

            Date sdate = null;
            Date edate = null;
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
            try {
                sdate = df.parse(starttime);
                edate = df.parse(endtime);
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
            System.out.println("Synchronization class-->MakeNewCalendarEntry()----> Api<14");
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

    private Uri MakeNewLeaveRequestCalendarEntry(int calId, String StartDate, String EndDate, String userName) {
        Uri insertedUri = null;
        try {
            ContentValues event = new ContentValues();
            event.put("calendar_id", calId);
            event.put("title", "Approved Day Off");
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
            event.put("hasAlarm", 1); // 0 for false, 1 for true
            event.put("eventTimezone", TimeZone.getDefault().getDisplayName());

            Uri eventsUri = Uri.parse(getCalendarUriBase() + "events");
            System.out.println("Synchronization class-->MakeNewLeaveRequestCalendarEntry()----> Api<14");
            insertedUri = mContext.getContentResolver().insert(eventsUri, event);

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
