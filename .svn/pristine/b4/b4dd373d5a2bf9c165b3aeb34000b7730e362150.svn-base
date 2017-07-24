package ism.android.webservices;

import android.content.Context;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import ism.android.ActivityStringInfo;
import ism.android.utils.StaticVariables;


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

    public SoapSerializationEnvelope makeRequest(Context context)
    {
        // Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, MethodName);

        // Use this to add parameters
        if (null != PARAMS && PARAMS.size() > 0)
        {
            Set<String> st = PARAMS.keySet();

            Iterator<String> iter = st.iterator();
            while (iter.hasNext())
            {
                String key = iter.next().toString();
                request.addProperty(key, PARAMS.get(key));
            }
        }

        Element[] header = new Element[4];
        // Setting Up The Device ID in Header
        header[0] = new Element().createElement(NAMESPACE, "AuthHeader");
        //header[0].addChild(Node.TEXT, "11aced6e-0ac2-4991-8823-0a9736de1bbf");
        header[0].addChild(Node.TEXT, StaticVariables.getDeviceID(context));
        System.out.println("device id = :" +  StaticVariables.getDeviceID(context));
        //Setting Organization ID in the header
        header[1] = new Element().createElement(NAMESPACE, "OrgID");
        //header[1].addChild(Node.TEXT, String.valueOf(6));
        header[1].addChild(Node.TEXT, String.valueOf(StaticVariables.getOrgID(context)));
        System.out.println("org id = :" +  StaticVariables.getOrgID(context));
        //Setting URL in the header
        header[2] = new Element().createElement(NAMESPACE, "URL");
        header[2].addChild(Node.TEXT, String.valueOf(StaticVariables.getURL(context)));
        System.out.println("url string : " + String.valueOf(StaticVariables.getURL(context)));
        header[3] = new Element().createElement(NAMESPACE, "IsManagerApp");
        header[3].addChild(Node.TEXT, String.valueOf("0"));
        // Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.headerOut = header;
        envelope.setOutputSoapObject(request);

        // Needed to make the internet call
		/*
		 * Edit by dhiraj.
		 * timeout added
		 * date: 12 August, 2015
		 */
        HttpTransportSE androidHttpTransport = new HttpTransportSE(StaticVariables.getURL(context), ActivityStringInfo.CONN_TIMEOUT);

        try
        {
            // call the webservice
            androidHttpTransport.call(SoapAction, envelope);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // Get the SoapResult from the envelope body.
        // SoapObject response = (SoapObject) envelope.bodyIn;
        return envelope;
    }
}
