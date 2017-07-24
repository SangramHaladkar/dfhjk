package ism.manager.webservices;

import ism.manager.ActivityStringInfo;
import ism.manager.Utility;
import ism.manager.utils.StaticVariables;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import android.content.Context;
import android.util.Log;

public class RequestProcessor {
    private String SoapAction = "http://tempuri.org/ISmartPhone/";
    private String MethodName;
    private static String NAMESPACE = "http://tempuri.org/";
    private LinkedHashMap<String, String> PARAMS;

    public RequestProcessor(String methodName, LinkedHashMap<String, String> params) {
        SoapAction += methodName;
        MethodName = methodName;
        PARAMS = params;
    }

    public SoapSerializationEnvelope makeRequest(Context context) {


        int startime = (int) System.currentTimeMillis();
        // Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, MethodName);

        // Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = null;

        try {
            // Use this to add parameters
            if (null != PARAMS && PARAMS.size() > 0) {
                Set<String> st = PARAMS.keySet();

                Iterator<String> iter = st.iterator();
                while (iter.hasNext()) {
                    String key = iter.next().toString();
                    request.addProperty(key, PARAMS.get(key));
                }
            }

            Element[] header = new Element[4];
            // Setting Up The Device ID in Header
            header[0] = new Element().createElement(NAMESPACE, "AuthHeader");
            //header[0].addChild(Node.TEXT, "11aced6e-0ac2-4991-8823-0a9736de1bbf");
            header[0].addChild(Node.TEXT, StaticVariables.getDeviceID(context));
//			System.out.println("AuthHeader(DeviceID) = :" +  StaticVariables.getDeviceID(context));
            //Setting Organization ID in the header
            header[1] = new Element().createElement(NAMESPACE, "OrgID");
            //header[1].addChild(Node.TEXT, String.valueOf(6));
            header[1].addChild(Node.TEXT, String.valueOf(StaticVariables.getOrgID(context)));
//			System.out.println("OrgID = :" +  StaticVariables.getOrgID(context));
            //Setting URL in the header
            header[2] = new Element().createElement(NAMESPACE, "URL");
            header[2].addChild(Node.TEXT, String.valueOf(StaticVariables.getURL(context)));
//			System.out.println("URL string : " + String.valueOf(StaticVariables.getURL(context)));

            header[3] = new Element().createElement(NAMESPACE, "IsManagerApp");
            header[3].addChild(Node.TEXT, String.valueOf("1"));

            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.headerOut = header;
            envelope.setOutputSoapObject(request);

            // Needed to make the internet call
            HttpTransportSE androidHttpTransport = new HttpTransportSE(StaticVariables.getURL(context), ActivityStringInfo.CONN_TIMEOUT);

//			Enable debug true when want to print requestDump and responseDump value
			androidHttpTransport.debug =true;

            androidHttpTransport.call(SoapAction, envelope);

			/*
            Get raw xml string for request and response from envelope

			String requestXml=androidHttpTransport.requestDump.toString();

			String responseXml=androidHttpTransport.responseDump.toString();
			*/

        } catch (Exception e) {
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
        int stoptime = (int) System.currentTimeMillis();
        Log.v("ism.manager.webservices.RequestProcessor", "*****soap call time :" + String.valueOf((stoptime - startime) / 1000));
        return envelope;
    }
}
