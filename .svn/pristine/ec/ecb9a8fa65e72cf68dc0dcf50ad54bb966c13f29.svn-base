package ism.manager.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.Utility;

public class JsonLogs implements Serializable {

    private static final long serialVersionUID = 1L;
    JSONObject jObjectData, jObjectCmd, jObjectCommand, jObjectMain;
    Context mContext;

    public JsonLogs(Context context) {
        mContext = context;
    }

    public String sendLogsDataToServer(String type) {
        String strMsg = "";
        Log.v("JsonLogs ", "in sendLogsToSeerver success1");
        try {
            jObjectMain = new JSONObject();
            jObjectCommand = new JSONObject();
            jObjectCmd = new JSONObject();
            jObjectData = new JSONObject();

            if (Utility.Connectivity_Internet(mContext)) {
                String logsId = "";
                if (type.equals("I") || type.equals("PI"))
                    jObjectCmd.put("CMD", "1");
                else
                    jObjectCmd.put("CMD", "2");

                JSONArray jArray = new JSONArray();
                JSONArray jArrayComm = new JSONArray();
                JSONArray jArrayEmpRelDec = new JSONArray();
                JSONArray jArrayLogFollowUp = new JSONArray();
                JSONArray jArrayLogMainDet = new JSONArray();
                JSONArray jArrayLogMainLog = new JSONArray();
                JSONArray jArrayLogNotes = new JSONArray();
                JSONArray jArrayLogProductDet = new JSONArray();
                JSONArray jArrayLogQuality = new JSONArray();
                JSONArray jArrayLogSafeLog = new JSONArray();
                JSONArray jArrayLogSafeDes = new JSONArray();
                JSONArray jArrayLogFlag = new JSONArray();


                Cursor c = null;

                if (type.equals("I"))
                    c = MyDatabaseInstanceHolder.getDatabaseHelper().getIncompleteLogDetailRecord();
                else if (type.equals("U"))
                    c = MyDatabaseInstanceHolder.getDatabaseHelper().getUpdatedLogDetailRecord();
                else if (type.equals("PI"))
                    c = MyDatabaseInstanceHolder.getDatabaseHelper().getPendingInsLogDetailRecord();
                else if (type.equals("PU"))
                    c = MyDatabaseInstanceHolder.getDatabaseHelper().getPendingUpdLogDetailRecord();


                Log.v("TAG JsonLog Cursor:- ", " " + c.getCount());
                Log.v("TAG Log Type:- ", type);
                if (c.getCount() > 0) {
                    ///get Logs Detail Record
                    while (c.moveToNext()) {
                        logsId = c.getString(0);
                        JSONObject jobject_LogsDetails = new JSONObject();
                        jobject_LogsDetails.put("CREATE_DATE", c.getString(2));
                        jobject_LogsDetails.put("CREATE_USER_ID", c.getString(3));
                        jobject_LogsDetails.put("CREATE_USER_NAME", c.getString(4));
                        jobject_LogsDetails.put("LAST_UPD_DATE", c.getString(6));
                        jobject_LogsDetails.put("LOG_DATE", c.getString(1));
                        jobject_LogsDetails.put("LOG_ID", c.getString(0));
                        jobject_LogsDetails.put("STATUS", c.getString(7));
                        jobject_LogsDetails.put("USER_TYPE_ID", c.getString(5));
                        jArray.put(jobject_LogsDetails);


                        Log.v("LOG_DETAILS ", "in sendLogsToSeerver success2" + jArray.toString() + " status " + c.getString(7));
                        System.out.println("LOG_DETAILS " + jArray.toString());
                        c.close();

                        //Get Logs Communication Record
                        Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getCommunicationLogsRecord(logsId);
                        if (c1.getCount() > 0) {
                            while (c1.moveToNext()) {
                                JSONObject jobject_CommLogs = new JSONObject();

                                jobject_CommLogs.put("COMMID", c1.getString(2));
                                jobject_CommLogs.put("FLAGSTATUS", c1.getString(3));
                                jobject_CommLogs.put("ID", c1.getString(0));
                                jobject_CommLogs.put("LOGID", c1.getString(1));

                                jArrayComm.put(jobject_CommLogs);
                            }
                            //mHandler.sendEmptyMessage(BG);

                            System.out.println("LOG_COMMUNICATION_DET " + jArrayComm.toString());
                        }
                        c1.close();

                        //Get Logs LOG_EMP_RELATION_DESC record
                        Cursor c2 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogEmpRelationDescRecord(logsId);

                        if (c2.getCount() > 0) {
                            while (c2.moveToNext()) {
                                JSONObject jobject_LogEmpRelationDesc = new JSONObject();
                                jobject_LogEmpRelationDesc.put("ADDEDBYID", c2.getString(4));
                                jobject_LogEmpRelationDesc.put("DESCRIPTION", c2.getString(5));
                                jobject_LogEmpRelationDesc.put("EMPFIELDID", c2.getString(2));
                                jobject_LogEmpRelationDesc.put("EMPLOGID", c2.getString(0));
                                jobject_LogEmpRelationDesc.put("EMPLOYEE", c2.getString(3));
                                jobject_LogEmpRelationDesc.put("LOGID", c2.getString(1));
                                jArrayEmpRelDec.put(jobject_LogEmpRelationDesc);
                                //mHandler.sendEmptyMessage(EX);
                            }

                            System.out.println("LOG_EMP_RELATION_DESC " + jArrayEmpRelDec.toString());
                        }
                        c2.close();


                        //Get Logs LOG_FOLLOW_UP_LOG record
                        Cursor c3 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogFollowUpRecord(logsId);

                        if (c3.getCount() > 0) {
                            while (c3.moveToNext()) {
                                JSONObject jobject_LogFollowUp = new JSONObject();
                                jobject_LogFollowUp.put("FLAGSTATUS", c3.getString(3));
                                jobject_LogFollowUp.put("FOLLOWUPID", c3.getString(2));
                                jobject_LogFollowUp.put("ID", c3.getString(0));
                                jobject_LogFollowUp.put("LOGID", c3.getString(1));
                                jobject_LogFollowUp.put("TASKID", c3.getString(4));
                                jArrayLogFollowUp.put(jobject_LogFollowUp);
                                //mHandler.sendEmptyMessage(EX);
                            }

                            System.out.println("LOG_FOLLOW_UP_LOG " + jArrayLogFollowUp.toString());
                        }
                        c3.close();


                        //Get Logs LOG_MAINTENANCE_DET record
                        Cursor c4 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogMainDetRecord(logsId);

                        if (c4.getCount() > 0) {
                            while (c4.moveToNext()) {
                                JSONObject jobject_LogMainDet = new JSONObject();
                                jobject_LogMainDet.put("DESCRIPTION", c4.getString(4));
                                jobject_LogMainDet.put("EMPLOYEE", c4.getString(3));
                                jobject_LogMainDet.put("LOGID", c4.getString(1));
                                jobject_LogMainDet.put("MRRDETAILSID", c4.getString(0));
                                jobject_LogMainDet.put("MRRID", c4.getString(2));
                                jobject_LogMainDet.put("TASKID", c4.getString(5));
                                jArrayLogMainDet.put(jobject_LogMainDet);
                                //mHandler.sendEmptyMessage(EX);
                            }

                            System.out.println("LOG_MAINTENANCE_DET " + jArrayLogMainDet.toString());
                        }
                        c4.close();

                        //Get Logs LOG_MAINTENANCE_LOG record

                        Cursor c5 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogMainLogRecord(logsId);

                        if (c5.getCount() > 0) {
                            while (c5.moveToNext()) {
                                JSONObject jobject_LogMainLog = new JSONObject();
                                jobject_LogMainLog.put("FLAGSTATUS", c5.getString(3));
                                jobject_LogMainLog.put("LOGID", c5.getString(1));
                                jobject_LogMainLog.put("MRRID", c5.getString(2));
                                jobject_LogMainLog.put("MRRLOGID", c5.getString(0));
                                jArrayLogMainLog.put(jobject_LogMainLog);
                                //mHandler.sendEmptyMessage(EX);
                            }

                            System.out.println("LOG_MAINTENANCE_LOG " + jArrayLogMainLog.toString());
                        }
                        c5.close();


                        //Get Logs LOG_NOTES record

                        Cursor c6 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogNotesRecord(logsId);

                        if (c6.getCount() > 0) {
                            while (c6.moveToNext()) {
                                JSONObject jobject_LogNotes = new JSONObject();
                                jobject_LogNotes.put("DESCRIPTION", c6.getString(3));
                                jobject_LogNotes.put("EMPLOYEE", c6.getString(2));
                                jobject_LogNotes.put("ID", c6.getString(0));
                                jobject_LogNotes.put("LOGID", c6.getString(1));
                                jArrayLogNotes.put(jobject_LogNotes);
                                //mHandler.sendEmptyMessage(EX);
                            }

                            System.out.println("LOG_NOTES " + jArrayLogNotes.toString());
                        }
                        c6.close();


                        //Get Logs LOG_PRODUCTIVITY_DET record

                        Cursor c7 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogProduDelRecord(logsId);

                        System.out.println("jsonlogs....logsId====" + logsId);
                        if (c7.getCount() > 0) {
                            while (c7.moveToNext()) {
                                JSONObject jobject_LogProductDet = new JSONObject();
                                jobject_LogProductDet.put("FIELDID", c7.getString(2));
                                jobject_LogProductDet.put("FIELDTYPE", c7.getString(4));
                                jobject_LogProductDet.put("FIELDVALUE", c7.getString(3));
                                jobject_LogProductDet.put("ID", c7.getString(0));
                                jobject_LogProductDet.put("LOGID", c7.getString(1));
                                jArrayLogProductDet.put(jobject_LogProductDet);
                                //mHandler.sendEmptyMessage(EX);
                            }

                            System.out.println("LOG_PRODUCTIVITY_DET " + jArrayLogProductDet.toString());
                        }
                        c7.close();

                        //Get Logs LOG_QUALITY record

                        Cursor c8 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogQualityRecord(logsId);

                        if (c8.getCount() > 0) {
                            while (c8.moveToNext()) {
                                JSONObject jobject_LogQuality = new JSONObject();
                                jobject_LogQuality.put("COMPLAINT", c8.getString(3));
                                jobject_LogQuality.put("LOGID", c8.getString(1));
                                jobject_LogQuality.put("QUALITYID", c8.getString(0));
                                jobject_LogQuality.put("USERID", c8.getString(2));
                                jArrayLogQuality.put(jobject_LogQuality);
                                //mHandler.sendEmptyMessage(EX);
                            }

                            System.out.println("LOG_QUALITY " + jArrayLogQuality.toString());
                        }
                        c8.close();

                        // Get Log_Flag record
                        Cursor cLogFlag = MyDatabaseInstanceHolder.getDatabaseHelper().getLogFlag(logsId);
                        if (cLogFlag.getCount() > 0) {
                            while (cLogFlag.moveToNext()) {
                                JSONObject jobject_LogFlag = new JSONObject();
                                jobject_LogFlag.put("DESCRIPTION", cLogFlag.getString(0));
                                jobject_LogFlag.put("FLAG", cLogFlag.getString(1));
                                jobject_LogFlag.put("FLAGGEDDATE", cLogFlag.getString(2));
                                jobject_LogFlag.put("ID", cLogFlag.getString(3));
                                jobject_LogFlag.put("LOGID", cLogFlag.getString(4));
                                jobject_LogFlag.put("USERID", cLogFlag.getString(5));
                                jobject_LogFlag.put("USERNAME", cLogFlag.getString(6));
                                jArrayLogFlag.put(jobject_LogFlag);
                            }
                            System.out.println("LOG_FLAG " + jArrayLogFlag.toString());
                        }
                        cLogFlag.close();
                        //Get Logs LOG_SAFETY_LOG record

                        Cursor c10 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyLogRecord(logsId);

                        if (c10.getCount() > 0) {
                            while (c10.moveToNext()) {
                                JSONObject jobject_LogSafetyLog = new JSONObject();
                                jobject_LogSafetyLog.put("FLAGSAFETY", c10.getString(3));
                                jobject_LogSafetyLog.put("LOGID", c10.getString(1));
                                jobject_LogSafetyLog.put("SAFETYID", c10.getString(2));
                                jobject_LogSafetyLog.put("SAFETYLOGID", c10.getString(0));
                                jArrayLogSafeLog.put(jobject_LogSafetyLog);


                                //Get Logs LOG_SAFETY_DESC record

                                Cursor c9 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyDecRecord(c10.getString(0));

                                if (c9.getCount() > 0) {
                                    while (c9.moveToNext()) {
                                        JSONObject jobject_LogSafetyDec = new JSONObject();
                                        jobject_LogSafetyDec.put("ADDEDBYID", c9.getString(4));
                                        jobject_LogSafetyDec.put("DESCRIPTION", c9.getString(3));
                                        jobject_LogSafetyDec.put("EMPLOYEEID", c9.getString(2));
                                        jobject_LogSafetyDec.put("ID", c9.getString(0));
                                        jobject_LogSafetyDec.put("SAFETYLOGID", c9.getString(1));
                                        jArrayLogSafeDes.put(jobject_LogSafetyDec);
                                        //mHandler.sendEmptyMessage(EX);
                                    }

                                    System.out.println("LOG_SAFETY_DESC " + jArrayLogSafeDes.toString());
                                }
                                c9.close();
                                //mHandler.sendEmptyMessage(EX);
                            }

                            System.out.println("LOG_SAFETY_LOG " + jArrayLogSafeLog.toString());
                        }
                        c10.close();
                    }

                    jObjectData.put("LOG_DETAILS", jArray);
                    jObjectData.put("LOG_COMMUNICATION_DET", jArrayComm);
                    jObjectData.put("LOG_EMP_RELATION_DESC", jArrayEmpRelDec);
                    jObjectData.put("LOG_FOLLOW_UP_LOG", jArrayLogFollowUp);
                    jObjectData.put("LOG_MAINTENANCE_DET", jArrayLogMainDet);
                    jObjectData.put("LOG_MAINTENANCE_LOG", jArrayLogMainLog);
                    jObjectData.put("LOG_NOTES", jArrayLogNotes);
                    jObjectData.put("LOG_PRODUCTIVITY_DET", jArrayLogProductDet);
                    jObjectData.put("LOG_QUALITY", jArrayLogQuality);
                    jObjectData.put("LOG_SAFETY_DESC", jArrayLogSafeDes);
                    jObjectData.put("LOG_SAFETY_LOG", jArrayLogSafeLog);
                    jObjectData.put("LOG_FLAGS", jArrayLogFlag);

                    jObjectMain.put("COMMAND", jObjectCmd);
                    jObjectMain.put("DATA", jObjectData);

                    System.out.println("jobject===========" + jObjectMain.toString());
                    strMsg = jObjectMain.toString();

                } else {
                    c.close();
                    strMsg = "";
                }

            } else {
                strMsg = MessageInfo.strError;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return strMsg;
    }
}
