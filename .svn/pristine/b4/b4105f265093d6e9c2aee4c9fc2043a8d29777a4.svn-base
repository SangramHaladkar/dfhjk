package ism.android.utils;


import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.kdom.Element;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import ism.android.ActivityStringInfo;

public class AppUtil
{
	public static String formatWebServiceLocation(String webServiceLocation)
	{
		String webServiceURL = "";
		try 
		{
			if (webServiceLocation.toLowerCase().contains("/smartphone.svc"))
				return webServiceLocation;
			else if (webServiceLocation.charAt(webServiceLocation.length() - 1) == '/')
				webServiceURL = webServiceLocation + "SmartPhone.svc";
			else
				webServiceURL = webServiceLocation + "/SmartPhone.svc";
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return webServiceURL;
	}

	public static String getHeaderText(SoapSerializationEnvelope envelope, String headerName)
	{
		try 
		{
			Element[] headers = envelope.headerIn;
			if (null != headers && headers.length > 0)
			{
				for (int i = 0; i < headers.length; i++)
				{
					if (headers[i].getName().equals(headerName.trim()))
						return headers[i].getText(0);
				}
			}
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean pingURL(String URL)
	{
		try
		{
			System.out.println("in appUtil...1");
			URL url = new URL(URL);
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
			System.out.println("in appUtil...2");
			//urlc.setRequestProperty("User-Agent", "Android Application:" + Z.APP_VERSION); 
			urlc.setRequestProperty("Connection", "close");
			System.out.println("in appUtil...3");
			//urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
			urlc.setConnectTimeout(ActivityStringInfo.CONN_TIMEOUT); // mTimeout is in seconds                                           added by dhiraj on 08 August, 2015
			System.out.println("in appUtil...4");
			urlc.connect();
			System.out.println("in appUtil...5");
			System.out.println("ourstaffing==="+urlc.getResponseCode());
			if (urlc.getResponseCode() == 200)
			{
				System.out.println("Application Login Success");
				//Main.Log("getResponseCode == 200");
				return new Boolean(true);
			}
			
		}
		catch (MalformedURLException e1)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
		return false;
	}

	public static String getDayOfWeek(int day)
	{
		String dayOfWeek = "";
		switch (day)
		{
		case 1:
			dayOfWeek = "Sunday";
			break;
		case 2:
			dayOfWeek = "Monday";
			break;
		case 3:
			dayOfWeek = "Tuesday";
			break;
		case 4:
			dayOfWeek = "Wednesday";
			break;
		case 5:
			dayOfWeek = "Thursday";
			break;
		case 6:
			dayOfWeek = "Friday";
			break;
		case 7:
			dayOfWeek = "Saturday";
			break;
		}
		return dayOfWeek;
	}

	public static Calendar getTodayDate()
	{
		Calendar todayCal = Calendar.getInstance();
		todayCal.set(Calendar.HOUR_OF_DAY, 0);  
		todayCal.set(Calendar.MINUTE, 0);  
		todayCal.set(Calendar.SECOND, 0);  
		todayCal.set(Calendar.MILLISECOND, 0); 
		return todayCal;
	}
}
