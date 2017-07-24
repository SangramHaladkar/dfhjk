package ism.manager.webservices;

import android.content.Context;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ism.manager.ActivityStringInfo;
import ism.manager.Utility;
import ism.manager.utils.AppUtil;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.ServerException;

public class ServicesHelper {
    RequestProcessor processor;

    public boolean checkConnection(Context context) {
        try {
            if (Utility.Connectivity_Internet(context))
                return true;
            else
                return false;
        } catch (Exception e) {
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
            return false;
        }
    }

    public String getAddressRecordForShiftTrade(Context context, String strId, String strType) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("shiftid", strId);
                params.put("type", strType);
                processor = new RequestProcessor("Sync_ShiftTradeUsers", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                System.out.println("response service==" + response);

                if (null != response.getProperty("Sync_ShiftTradeUsersResult")) {
                    SoapObject GetAddressBookUser_OrgIdResult = (SoapObject) response.getProperty("Sync_ShiftTradeUsersResult");
                    ActivityStringInfo.AddressBookList = new ArrayList<HashMap<String, String>>();

                    /** Data for Address book **/
                    SoapObject Address_Book_List = (SoapObject) GetAddressBookUser_OrgIdResult.getProperty("ShiftTradeUsers_List");
                    for (int i = 0; i < Address_Book_List.getPropertyCount(); i++) {
                        SoapObject EAddress_Book = (SoapObject) Address_Book_List.getProperty(i);
                        ActivityStringInfo.hasAddressBook = new HashMap<String, String>();
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_USER_ID, EAddress_Book.getProperty("userid").toString().replace("anyType{}", ""));
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_FIRST_NAME, EAddress_Book.getProperty("FirstName").toString().replace("anyType{}", ""));
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_LAST_NAME, EAddress_Book.getProperty("LastName").toString().replace("anyType{}", ""));
                        //	ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_POSITION_TITLE, EAddress_Book.getProperty("PositionTitle").toString().replace("anyType{}", ""));
                        //	ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_ORG_NAME, EAddress_Book.getProperty("LastName").toString().replace("anyType{}", ""));
                        ActivityStringInfo.AddressBookList.add(ActivityStringInfo.hasAddressBook);
                    }
                    strMsg = "true";
                    System.out.println("ActivityStringInfo.AddressBookList==" + ActivityStringInfo.AddressBookList);
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            e.printStackTrace();
        }
        return strMsg;
    }

    public String getAddressRecord(Context context) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {

                processor = new RequestProcessor("GetAddressBookUser_OrgId", null);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                System.out.println("response service==" + response);

                if (null != response.getProperty("GetAddressBookUser_OrgIdResult")) {
                    SoapObject GetAddressBookUser_OrgIdResult = (SoapObject) response.getProperty("GetAddressBookUser_OrgIdResult");
                    ActivityStringInfo.AddressBookList = new ArrayList<HashMap<String, String>>();

                    /** Data for Distribution book **/
                    SoapObject Distribution_List = (SoapObject) GetAddressBookUser_OrgIdResult.getProperty("Distribution_List");
                    for (int i = 0; i < Distribution_List.getPropertyCount(); i++) {
                        SoapObject EAddress_Book = (SoapObject) Distribution_List.getProperty(i);
                        ActivityStringInfo.hasAddressBook = new HashMap<String, String>();
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_USER_ID, EAddress_Book.getProperty("DistributionListID").toString().replace("anyType{}", ""));
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_FIRST_NAME, EAddress_Book.getProperty("ListName").toString().replace("anyType{}", ""));
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_LAST_NAME, "");
                        ActivityStringInfo.AddressBookList.add(ActivityStringInfo.hasAddressBook);
                    }

                    /** Data for Address book **/
                    SoapObject Address_Book_List = (SoapObject) GetAddressBookUser_OrgIdResult.getProperty("AddressBookUser_List");
                    for (int i = 0; i < Address_Book_List.getPropertyCount(); i++) {
                        SoapObject EAddress_Book = (SoapObject) Address_Book_List.getProperty(i);
                        ActivityStringInfo.hasAddressBook = new HashMap<String, String>();
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_USER_ID, EAddress_Book.getProperty("userid").toString() + "-0");
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_FIRST_NAME, EAddress_Book.getProperty("FirstName").toString().replace("anyType{}", ""));
                        ActivityStringInfo.hasAddressBook.put(DatabaseConstant.key_LAST_NAME, EAddress_Book.getProperty("LastName").toString().replace("anyType{}", ""));
                        ActivityStringInfo.AddressBookList.add(ActivityStringInfo.hasAddressBook);
                    }
                    System.out.println("ActivityStringInfo.AddressBookList==" + ActivityStringInfo.AddressBookList);
                    strMsg = "true";
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return strMsg;
    }

    public String setPastAnnualFieldDetails(Context mContext) {
        ActivityStringInfo.dailyLogProdDetAnnualMap.clear();
        String strMsg = "";
        try {
            if (checkConnection(mContext)) {
                Log.v("LogsDetaislAll ", "in check connection");
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                String strLogDate = ActivityStringInfo.selectedLogForDate;
                System.out.println("ActivityStringInfo.selectedLogForDate==" + ActivityStringInfo.selectedLogForDate);
                params.put("logdate", strLogDate);
                Log.v("TAG PARAM:- ", " " + params);

                processor = new RequestProcessor("Sync_DailyLogProdDetAnnual", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(mContext);
                Log.v("TAG Envelope:- ", "" + envelope);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    Log.v("LogsDetaislAll ", "error msg");
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;

                if (null != response.getProperty("Sync_DailyLogProdDetAnnualResult")) {
                    Log.v("LogsDetaislAll ", "in soap");
                    SoapObject sync_DailyLogProdDet = (SoapObject) response.getProperty("Sync_DailyLogProdDetAnnualResult");

                    SoapObject dailyLogProdDetAnnual_List = (SoapObject) sync_DailyLogProdDet.getProperty("DailyLogProdDetAnnual_List");

                    for (int i = 0; i < dailyLogProdDetAnnual_List.getPropertyCount(); i++) {
                        SoapObject dailyLogProd = (SoapObject) dailyLogProdDetAnnual_List.getProperty(i);

                        String fieldId = dailyLogProd.getProperty("FieldId").toString().replace("anyType{}", "");
                        String value = dailyLogProd.getProperty("UserName1").toString().replace("anyType{}", "") + "~" +
                                dailyLogProd.getProperty("Response1").toString().replace("anyType{}", "") + "~" +
                                dailyLogProd.getProperty("UserName2").toString().replace("anyType{}", "") + "~" +
                                dailyLogProd.getProperty("Response2").toString().replace("anyType{}", "");
                        //System.out.println("VALUES IN SEVICE HELPER ::: "+value);
                        ActivityStringInfo.dailyLogProdDetAnnualMap.put(fieldId, value);
                    }
                    strMsg = "true";
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return strMsg;
    }

    public String getForgotPasswordQuestionList(Context context, String strUserId, String strCompany) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("loginid", strUserId);
                params.put("company", strCompany);
                params.put("host", ActivityStringInfo.wsLocation);
                System.out.println("params===" + params);
                processor = new RequestProcessor("ForgotPasswordVerification", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;

                if (null != response.getProperty("ForgotPasswordVerificationResult")) {
                    SoapObject ForgotPasswordVerificationResult = (SoapObject) response.getProperty("ForgotPasswordVerificationResult");
                    ActivityStringInfo.ForgotPasswordQuestionList = new ArrayList<HashMap<String, String>>();
                    HashMap<String, String> questionList = new HashMap<String, String>();
                    /** Data for Forgot Password Question List **/
                    SoapObject LoginChallengeQuestion_List = (SoapObject) ForgotPasswordVerificationResult.getProperty("LoginChallengeQuestion_List");
                    for (int i = 0; i < LoginChallengeQuestion_List.getPropertyCount(); i++) {
                        SoapObject Qusetion_List = (SoapObject) LoginChallengeQuestion_List.getProperty(i);
                        questionList = new HashMap<String, String>();
                        questionList.put(DatabaseConstant.key_ID, Qusetion_List.getProperty("LoginQuestionID").toString().replace("anyType{}", ""));
                        questionList.put(DatabaseConstant.key_SEC_QUES_1, Qusetion_List.getProperty("Question").toString().replace("anyType{}", ""));
                        questionList.put(DatabaseConstant.key_SEC_ANS_1, Qusetion_List.getProperty("Answer").toString().replace("anyType{}", ""));
                        ActivityStringInfo.ForgotPasswordQuestionList.add(questionList);
                    }
                    strMsg = ForgotPasswordVerificationResult.getProperty("Status").toString().replace("anyType{}", "");
                    System.out.println("Status===" + strMsg);
                    System.out.println("ForgotPasswordQuestionList==" + ActivityStringInfo.ForgotPasswordQuestionList);
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return strMsg;
    }

    public String getResetPassword(Context context, String strUserId) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("LoginId", strUserId);
                params.put("host", ActivityStringInfo.wsLocation);
                System.out.println("params===" + params);
                processor = new RequestProcessor("ResetPassword", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                System.out.println("response service==" + response);

                if (null != response.getProperty("ResetPasswordResult")) {
                    ActivityStringInfo.strResetPasswordEmail = response.getProperty("ResetPasswordResult").toString().replace("anyType{}", "");
                    strMsg = "True";
                    System.out.println("Status===" + strMsg);
                    System.out.println("ForgotPasswordQuestionList==" + ActivityStringInfo.ForgotPasswordQuestionList);
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return strMsg;
    }

    public String getTermAcceptDeny(Context context, String strStatus) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("status", strStatus);
                System.out.println("params===" + params);
                processor = new RequestProcessor("TermAcceptDeny", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                System.out.println("response service==" + response);

                if (null != response.getProperty("TermAcceptDenyResult")) {
                    strMsg = response.getProperty("TermAcceptDenyResult").toString().replace("anyType{}", "");
                    System.out.println("strMsg===" + strMsg);
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            if (strMsg.equals(""))
                strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return strMsg;
    }

    public String setChangeTempPassword(Context context, String strOldPass, String strNewPass) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("OldPwd", strOldPass);
                params.put("NewPwd", strNewPass);
                System.out.println("params===" + params);
                processor = new RequestProcessor("ChangeTempPassword", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                System.out.println("response service==" + response);

                if (null != response.getProperty("ChangeTempPasswordResult")) {
                    strMsg = response.getProperty("ChangeTempPasswordResult").toString().replace("anyType{}", "");
                    System.out.println("strMsg===" + strMsg);
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return strMsg;
    }

    public String setUpdateUserInfo(Context context, String strEmail, String strNotifyByEmail, String strSmsAddress, String strCellNumber, String strNotifyBySms) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("Email", strEmail);
                params.put("NotifyByEmail", strNotifyByEmail);
                params.put("SmsAddress", strSmsAddress);
                params.put("CellNumber", strCellNumber);
                params.put("NotifyBySms", strNotifyBySms);
                System.out.println("params===" + params);
                processor = new RequestProcessor("UpdateUserInfo", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                System.out.println("response service==" + response);

                if (null != response.getProperty("UpdateUserInfoResult")) {
                    strMsg = response.getProperty("UpdateUserInfoResult").toString().replace("anyType{}", "");
                    System.out.println("strMsg===" + strMsg);
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return strMsg;
    }

    public String setUpdateLoginChallenge(Context context, String strQues1, String strAns1, String strQues2, String strAns2, String strQues3, String strAns3) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("Ques1", strQues1);
                params.put("Ans1", strAns1);
                params.put("Ques2", strQues2);
                params.put("Ans2", strAns2);
                params.put("Ques3", strQues3);
                params.put("Ans3", strAns3);
                System.out.println("params===" + params);
                processor = new RequestProcessor("UpdateLoginChallenge", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                System.out.println("response service==" + response);

                if (null != response.getProperty("UpdateLoginChallengeResult")) {
                    strMsg = response.getProperty("UpdateLoginChallengeResult").toString().replace("anyType{}", "");
                    System.out.println("strMsg===" + strMsg);
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return strMsg;
    }

    public String getMandatoryMsgDet(Context context, String strMsgId) throws ServerException {
        String strMsg = "";
        try {
            if (checkConnection(context)) {
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("MessageId", strMsgId);
                System.out.println("params===" + params);
                processor = new RequestProcessor("GetMandatoryMsgDet", params);
                SoapSerializationEnvelope envelope = processor.makeRequest(context);
                String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
                if (null != errorMsg) {
                    strMsg = errorMsg;
                    throw new ServerException(errorMsg);
                }
                SoapObject response = (SoapObject) envelope.bodyIn;
                System.out.println("response service==" + response);

                if (null != response.getProperty("GetMandatoryMsgDetResult")) {
                    SoapObject GetAddressBookUser_OrgIdResult = (SoapObject) response.getProperty("GetMandatoryMsgDetResult");
                    ActivityStringInfo.MandatoryMessageAttachmentList = new ArrayList<HashMap<String, String>>();
                    ActivityStringInfo.MandatoryMessageDetailList = new ArrayList<HashMap<String, String>>();
                    ActivityStringInfo.MandatoryMessageDetailNotReadList = new ArrayList<HashMap<String, String>>();

                    /** Data for message attachment list **/
                    SoapObject EMessage_Attachment_List = (SoapObject) GetAddressBookUser_OrgIdResult.getProperty("EMessage_Attachment_List");
                    for (int i = 0; i < EMessage_Attachment_List.getPropertyCount(); i++) {
                        SoapObject eMessage_Attachment = (SoapObject) EMessage_Attachment_List.getProperty(i);
                        HashMap<String, String> hasAttachmentList = new HashMap<String, String>();
                        hasAttachmentList.put(DatabaseConstant.key_ATTACHMENT_LINK, eMessage_Attachment.getProperty("ATTACHMENT_LINK").toString().replace("anyType{}", ""));
                        hasAttachmentList.put(DatabaseConstant.key_FILE_ID, eMessage_Attachment.getProperty("FILE_ID").toString().replace("anyType{}", ""));
                        hasAttachmentList.put(DatabaseConstant.key_FILE_NAME, eMessage_Attachment.getProperty("FILE_NAME").toString().replace("anyType{}", ""));
                        hasAttachmentList.put(DatabaseConstant.key_MESSAGE_ID, eMessage_Attachment.getProperty("MESSAGE_ID").toString().replace("anyType{}", ""));
                        hasAttachmentList.put(DatabaseConstant.key_READ_DATE, eMessage_Attachment.getProperty("READ_DATE").toString().replace("anyType{}", ""));
                        hasAttachmentList.put(DatabaseConstant.key_USER_ID, eMessage_Attachment.getProperty("USER_ID").toString().replace("anyType{}", ""));
                        ActivityStringInfo.MandatoryMessageAttachmentList.add(hasAttachmentList);
                    }

                    /** Data for message detail **/
                    SoapObject MessageDetail_List = (SoapObject) GetAddressBookUser_OrgIdResult.getProperty("MessageDetail_List");
                    for (int i = 0; i < MessageDetail_List.getPropertyCount(); i++) {
                        SoapObject messageDetail = (SoapObject) MessageDetail_List.getProperty(i);
                        HashMap<String, String> hasMessageDetailList = new HashMap<String, String>();
                        hasMessageDetailList.put(DatabaseConstant.key_FIRST_NAME, messageDetail.getProperty("FIRST_NAME").toString().replace("anyType{}", ""));
                        hasMessageDetailList.put(DatabaseConstant.key_LAST_NAME, messageDetail.getProperty("LAST_NAME").toString().replace("anyType{}", ""));
                        hasMessageDetailList.put(DatabaseConstant.key_MESSAGE_DATE, messageDetail.getProperty("MESSAGE_DATE").toString().replace("anyType{}", ""));
                        hasMessageDetailList.put(DatabaseConstant.key_READ_DATE, messageDetail.getProperty("READ_DATE").toString().replace("anyType{}", ""));
                        hasMessageDetailList.put(DatabaseConstant.key_SUBJECT, messageDetail.getProperty("SUBJECT").toString().replace("anyType{}", ""));
                        hasMessageDetailList.put(DatabaseConstant.key_USER_ID, messageDetail.getProperty("USER_ID").toString().replace("anyType{}", ""));
                        if (messageDetail.getProperty("READ_DATE").toString().replace("anyType{}", "").equals(""))
                            ActivityStringInfo.MandatoryMessageDetailNotReadList.add(hasMessageDetailList);
                        else
                            ActivityStringInfo.MandatoryMessageDetailList.add(hasMessageDetailList);
                    }
                    System.out.println("MandatoryMessageDetailList==" + ActivityStringInfo.MandatoryMessageDetailList.toString());
                    strMsg = "true";
                } else {
                    strMsg = MessageInfo.strObjectNull;
                }
            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {
            strMsg = MessageInfo.strObjectNull;
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        return strMsg;
    }
}
