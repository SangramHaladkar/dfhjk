package ism.manager.webservices;

import ism.manager.ActivityStringInfo;
import ism.manager.Utility;
import ism.manager.backgroundservices.ServiceForDocumentRead;
import ism.manager.utils.AppUtil;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.ServerException;

import java.util.Calendar;
import java.util.LinkedHashMap;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncServiceHelper {

	RequestProcessor processor;

	public boolean checkConnection(Context context)
	{
		try 
		{
			if(Utility.Connectivity_Internet(context))
				return true;
			else
				return false;
		} 
		catch (Exception e)
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
			return false;
		}
	}

	/** send the compose mail data**/
	@SuppressLint("NewApi")
	public String sendComposeMail(Context context, String flgForForward,String strUserId,String strDistributionId, String arrayAttachementFileName,String strMsgType, String flagTextArea) throws ServerException
	{
		String strMsg = "";
		//Remove last comma from To email id's.
		if(!strUserId.isEmpty()) {
		 strUserId = strUserId.substring(0, strUserId.lastIndexOf(","));
		}
		
				
		if(!strDistributionId.isEmpty()) {
		 strDistributionId = strDistributionId.substring(0, strDistributionId.lastIndexOf(","));
		}
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("subject",ActivityStringInfo.strSubject);
				params.put("body",ActivityStringInfo.strBody);
				params.put("from_user_id",ActivityStringInfo.strUser_id);
				if(strMsgType.equals(""))
					params.put("type","G");
				else
					params.put("type",strMsgType);
				params.put("sub_type","");
				params.put("process_id","");
				params.put("ip_added",Utility.getLocalIpAddress(context));
				params.put("oprid_entered_by",ActivityStringInfo.strLogin);
				params.put("to",strUserId);
				params.put("todist",strDistributionId);
				String strFileLink = arrayAttachementFileName.replace("%20", " ");
				System.out.println("strFileLink==="+strFileLink);
				if(!strFileLink.isEmpty())
				strFileLink = strFileLink.substring(0, strFileLink.lastIndexOf(","));
				params.put("filename",strFileLink);
				params.put("fwd_flg",flgForForward);
				
				params.put("sendTextAlert", flagTextArea);//   put yes no flag value
               
				System.out.println("params==="+params);

				processor = new RequestProcessor("Insert_Message", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("Insert_MessageResult"))
				{
					if(composeResponse.getProperty("Insert_MessageResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/** send the new idea mail **/
	public String sendNewIdea(Context context, String strSubject,String strMessage) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("subject",strSubject);
				params.put("idea",strMessage);
				params.put("ip_added",Utility.getLocalIpAddress(context));
				params.put("oprid_entered_by",ActivityStringInfo.strLogin);

			System.out.println("params==="+params);

			processor = new RequestProcessor("Insert_Idea_Review", params);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
			if (null != errorMsg)
			{
				throw new ServerException(errorMsg);
			}
			SoapObject composeResponse = (SoapObject) envelope.bodyIn;
			System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("Insert_Idea_ReviewResult"))
				{
					if(composeResponse.getProperty("Insert_Idea_ReviewResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	/** send the approval for give away and give to  **/
	public String sendGAApproval(Context context, String offShiftID,String reqUserID, String approverComments, String gAwayType) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("offShiftID",offShiftID);
				params.put("reqUserID",reqUserID);
				params.put("approverComments",approverComments);
				params.put("gAwayType",gAwayType);

			System.out.println("params==="+params);

			processor = new RequestProcessor("ApprovedGARequest", params);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
			if (null != errorMsg)
			{
				throw new ServerException(errorMsg);
			}
			SoapObject approvalResponse = (SoapObject) envelope.bodyIn;
			System.out.println("@@@@@@@ response=="+approvalResponse);

				if (null != approvalResponse.getProperty("ApprovedGARequestResult"))
				{
					if(approvalResponse.getProperty("ApprovedGARequestResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	/** Deny the approval give away and give to  **/
	public String denyGAApproval(Context context, String offShiftID, String isSpecEmployee,String comment) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("offShiftID",offShiftID);
				params.put("isSpecEmployee",isSpecEmployee);
				params.put("comment",comment);

			System.out.println("params==="+params);

			processor = new RequestProcessor("DenyGARequest", params);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
			if (null != errorMsg)
			{
				throw new ServerException(errorMsg);
			}
			SoapObject approvalResponse = (SoapObject) envelope.bodyIn;
			System.out.println("@@@@@@@ response=="+approvalResponse);

				if (null != approvalResponse.getProperty("DenyGARequestResult"))
				{
					if(approvalResponse.getProperty("DenyGARequestResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	/** send the approval for Trade shift  **/
	public String sendTradeShiftApproval(Context context, String offShiftID, String approverComments) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("offShiftID",offShiftID);
				params.put("approverComments",approverComments);

			System.out.println("params==="+params);

			processor = new RequestProcessor("ApprovedTRRequest", params);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
			if (null != errorMsg)
			{
				throw new ServerException(errorMsg);
			}
			SoapObject approvalResponse = (SoapObject) envelope.bodyIn;
			System.out.println("@@@@@@@ response=="+approvalResponse);

				if (null != approvalResponse.getProperty("ApprovedTRRequestResult"))
				{
					if(approvalResponse.getProperty("ApprovedTRRequestResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	/** Deny the Trade Shift Request  **/
	public String denyTradeShiftApproval(Context context, String offShiftID,String comment) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("offShiftID",offShiftID);
				params.put("comment",comment);

			System.out.println("params==="+params);

			processor = new RequestProcessor("DenyTRRequest", params);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
			if (null != errorMsg)
			{
				throw new ServerException(errorMsg);
			}
			SoapObject approvalResponse = (SoapObject) envelope.bodyIn;
			System.out.println("@@@@@@@ response=="+approvalResponse);

				if (null != approvalResponse.getProperty("DenyTRRequestResult"))
				{
					if(approvalResponse.getProperty("DenyTRRequestResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}	

	/** send the approval for Request Off  **/
	public String sendRequestOffApproval(Context context, String dayOffID, String approverComments) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("dayOffID",dayOffID);
				params.put("approverComments",approverComments);

			System.out.println("params==="+params);

			processor = new RequestProcessor("ApprovedDayOffRequest", params);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
			if (null != errorMsg)
			{
				throw new ServerException(errorMsg);
			}
			SoapObject approvalResponse = (SoapObject) envelope.bodyIn;
			System.out.println("@@@@@@@ response=="+approvalResponse);

				if (null != approvalResponse.getProperty("ApprovedDayOffRequestResult"))
				{
					if(approvalResponse.getProperty("ApprovedDayOffRequestResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	/** Deny the Request Off  **/
	public String denyRequestOffApproval(Context context, String dayOffID, String approverComments) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("dayOffID",dayOffID);
				params.put("approverComments",approverComments);

			System.out.println("params==="+params);

			processor = new RequestProcessor("DenyDayOffRequest", params);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
			if (null != errorMsg)
			{
				throw new ServerException(errorMsg);
			}
			SoapObject approvalResponse = (SoapObject) envelope.bodyIn;
			System.out.println("@@@@@@@ response=="+approvalResponse);

				if (null != approvalResponse.getProperty("DenyDayOffRequestResult"))
				{
					if(approvalResponse.getProperty("DenyDayOffRequestResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	/**  Message List Accept Meeting Detail **/
	public String sendAcceptMeetingDetail(Context context, String meetingId) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("meetingID",meetingId);

				System.out.println("params==="+params);

				processor = new RequestProcessor("MeetingAttend", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}

				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);
				System.out.println("composeResponse.getProperty(MeetingAttendResult)=="+composeResponse.getProperty("MeetingAttendResult"));
				if (null != composeResponse.getProperty("MeetingAttendResult"))
				{
					if(composeResponse.getProperty("MeetingAttendResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	/**  Message List Reply Meeting Detail **/
	public String sendReplyMeetingDetail(Context context, String meetingId ,String comments) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("meetingID",meetingId);
				params.put("comments",comments);

				System.out.println("params==="+params);

				processor = new RequestProcessor("MeetingSendManager", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("MeetingSendManagerResult"))
				{
					System.out.println("## ## ## Meeting Reply from Server : "+composeResponse.getProperty("MeetingSendManagerResult").toString());
					if(composeResponse.getProperty("MeetingSendManagerResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	/**  Message List  Shift Give Away Comment **/
	public String sendShiftGiveAwayComment(Context context, String shiftId ,String comments) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("shiftID",shiftId);
				params.put("comments",comments);

				System.out.println("params==="+params);

				processor = new RequestProcessor("OpportunityDetails", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("OpportunityDetailsResult"))
				{
					strMsg =  composeResponse.getProperty("OpportunityDetailsResult").toString();
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/** Schedules Shift Give Away**/
	public String sendScheduleShiftGiveAwayComment(Context context, String shiftId ,String comments) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("shiftID",shiftId);
				params.put("comments",comments);

				System.out.println("params==="+params);

				processor = new RequestProcessor("GiveAwayShiftVolunteer", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("GiveAwayShiftVolunteerResult"))
				{
					if(composeResponse.getProperty("GiveAwayShiftVolunteerResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/**  Message List Shift Give To Comment **/
	public String sendShiftGiveToComment(Context context, String shiftId ,String comments) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("offeringShiftID",shiftId);
				params.put("comments",comments);

				System.out.println("params==="+params);

				processor = new RequestProcessor("GAwaySendManager", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("GAwaySendManagerResult"))
				{
					strMsg =  composeResponse.getProperty("GAwaySendManagerResult").toString();
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/** Schedules Shift Give To**/
	public String sendScheduleShiftGiveToComment(Context context, String shiftId ,String comments) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("shiftID",shiftId);
				params.put("comments",comments);
				params.put("employeeId",ActivityStringInfo.strEmployeeId);

				System.out.println("params==="+params);

				processor = new RequestProcessor("GiveAwayShiftSpecific", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("GiveAwayShiftSpecificResult"))
				{
					if(composeResponse.getProperty("GiveAwayShiftSpecificResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/** Message List Trade shift Accept **/
	public String sendTradeShiftAccept(Context context, String offeringShiftID,String exchangeShiftID,String comments) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("offeringShiftID",offeringShiftID);
				params.put("exchangeShiftID",exchangeShiftID);
				params.put("comments",comments);

				System.out.println("params==="+params);

				processor = new RequestProcessor("TradeSendManager", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("TradeSendManagerResult"))
				{
					strMsg =  composeResponse.getProperty("TradeSendManagerResult").toString();
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/** Schedules Trade shift**/
	public String sendScheduleTradeComment(Context context, String shiftId ,String comments) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("shiftID",shiftId);
				params.put("comments",comments);
				params.put("employeeId",ActivityStringInfo.strEmployeeId);

				System.out.println("params==="+params);

				processor = new RequestProcessor("TradeShift", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("TradeShiftResult"))
				{
					if(composeResponse.getProperty("TradeShiftResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/**  Message List Shift Give To No Thanks **/
	public String sendShiftGiveToNoThanks(Context context, String shiftId) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("giveAwayID",shiftId);

				System.out.println("params==="+params);

				processor = new RequestProcessor("GAwayNoThanks", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("GAwayNoThanksResult"))
				{
					if(composeResponse.getProperty("GAwayNoThanksResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/** Message List Trade shift No Thanks **/
	public String sendTradeShiftNoThanks(Context context, String shiftId) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("tradeID",shiftId);

				System.out.println("params==="+params);

				processor = new RequestProcessor("TradeNoThanks", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("TradeNoThanksResult"))
				{
					if(composeResponse.getProperty("TradeNoThanksResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}


	/** Request Off**/
	public String sendRequestOff(Context context, String requestOffDate ,String comments) throws ServerException
	{
		String strMsg = "";
		try
		{
			if(checkConnection(context))
			{
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("requestOffDate",requestOffDate+" 12:00");
				params.put("comments",comments);

				System.out.println("params==="+params);

				processor = new RequestProcessor("RequestOffDay", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					System.out.println("errorMsg==="+errorMsg);
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("RequestOffDayResult"))
				{
					if(composeResponse.getProperty("RequestOffDayResult").toString().equals("true"))
						strMsg =  "true";
					else
						strMsg = "false";
				}
			}
			else
			{
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e)
		{
			if(strMsg.equals(""))
				strMsg = MessageInfo.strObjectNull;
			e.printStackTrace();
			return strMsg;
		}
		return strMsg;
	}

	/**  Document Attachment read date update **/
	public String sendDocumentReadDetail(Context context) throws ServerException
	{
		String strMsg = "";
		try 
		{
			if(checkConnection(context))
			{
				Calendar cal = Calendar.getInstance();
				LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
				params.put("MessageID",ActivityStringInfo.strMsgId);
				params.put("userid",ActivityStringInfo.strUser_id);
				params.put("fileid",ActivityStringInfo.strFileId);
				params.put("readdate",ActivityStringInfo.sdfDate.format(cal.getTime()));

				System.out.println("params==="+params);

				processor = new RequestProcessor("MarkReadAttachment", params);
				SoapSerializationEnvelope envelope = processor.makeRequest(context);
				String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
				if (null != errorMsg)
				{
					strMsg = errorMsg;
					throw new ServerException(errorMsg);
				}
				SoapObject composeResponse = (SoapObject) envelope.bodyIn;
				System.out.println("response=="+composeResponse);

				if (null != composeResponse.getProperty("MarkReadAttachmentResult"))
				{
					if(composeResponse.getProperty("MarkReadAttachmentResult").toString().equals("true"))
						context.stopService(new Intent(context, ServiceForDocumentRead.class));
					else
						context.stopService(new Intent(context, ServiceForDocumentRead.class));
				}
			}
			else
			{
				context.stopService(new Intent(context, ServiceForDocumentRead.class));
				strMsg =  MessageInfo.strError;
			}
		}
		catch (Exception e) 
		{
			context.stopService(new Intent(context, ServiceForDocumentRead.class));
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}
}
