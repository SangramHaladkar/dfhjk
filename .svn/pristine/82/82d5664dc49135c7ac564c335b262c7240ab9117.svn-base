package ism.android.webservices;


import android.content.Context;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.Hashtable;
import java.util.LinkedHashMap;

import ism.android.ActivityStringInfo;
import ism.android.Utility;
import ism.android.utils.AppUtil;
import ism.android.utils.MessageInfo;
import ism.android.utils.ServerException;
import ism.android.utils.StaticVariables;

public class RegistrationHelper
{
	RequestProcessor processor;

	public String doLogin(Context context,String regGUID)
	{
		String strMsg = "";
		try
		{
			RequestProcessor processor = new RequestProcessor("Authenticate", null);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			SoapObject response = (SoapObject) envelope.bodyIn;
			strMsg =  response.getProperty("AuthenticateResult").toString();
		}
		catch (Exception e)
		{
			strMsg = MessageInfo.strObjectNull;
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return strMsg;
	}

	public Hashtable<String, String> registerUser(Context context, String loginName, String password, String location, String wsLocation) throws ServerException
	{
		System.out.println("wsLocation=="+wsLocation);
		Hashtable<String, String> ht = new Hashtable<String, String>();
		StaticVariables.setURL(wsLocation);
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("loginName", loginName);
		params.put("password", password);
		params.put("host", wsLocation);
//		params.put("organizations", location);
//		params.put("deviceToken", GCMRegistrar.getRegistrationId(context));
		params.put("deviceToken", ActivityStringInfo.gcm_reg_id);
		params.put("deviceType", "Android");

		try
		{
			processor = new RequestProcessor("RegisterSmartPhone", params);
			SoapSerializationEnvelope envelope = processor.makeRequest(context);
			String errorMsg = AppUtil.getHeaderText(envelope, "ErrorMsg");
			if (null != errorMsg)
			{
				ActivityStringInfo.strLoginError = errorMsg;
				throw new ServerException(errorMsg);
			}
			SoapObject response = (SoapObject) envelope.bodyIn;
			System.out.println("registerSmartPhone=="+response.toString());
			if (null != (SoapObject) response.getProperty("RegisterSmartPhoneResult"))
			{

				SoapObject registerSmartPhone = (SoapObject) response.getProperty("RegisterSmartPhoneResult");
				System.out.println("registerSmartPhone=="+response.toString());
				String deviceID = registerSmartPhone.getProperty("DeviceID").toString();
				String orgID = registerSmartPhone.getProperty("OrgID").toString();
				String firstName = registerSmartPhone.getProperty("First_Name").toString();
				String lastName = registerSmartPhone.getProperty("Last_Name").toString();
				String userId = registerSmartPhone.getProperty("User_id").toString();
				String positionTitle = registerSmartPhone.getProperty("Position_Title").toString();
				String acceptTerms = registerSmartPhone.getProperty("AcceptTerms").toString();
				String autoGenPwd = registerSmartPhone.getProperty("AutoGenPwd").toString();
				String companyName=registerSmartPhone.getProperty("CompanyName").toString();

				ht.put("DeviceID", deviceID);
				ht.put("OrgID", orgID);
				ht.put("First_Name", firstName);
				ht.put("Last_Name", lastName);
				ht.put("User_id", userId);
				ht.put("Position_Title", positionTitle);
				ht.put("AcceptTerms", acceptTerms);
				ht.put("AutoGenPwd", autoGenPwd);
				ht.put("CompanyName",companyName);

			}
			System.out.println("ht=="+ht);
		}
		catch (Exception e)
		{
			Utility.saveExceptionDetails(context, e);
			e.printStackTrace();
		}
		return ht;
	}

}
