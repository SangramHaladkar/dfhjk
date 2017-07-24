package ism.android.backgroundservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

import ism.android.Utility;
import ism.android.webservices.SyncServiceHelper;


//Call this service when Mandatory attachment document is read
public class ServiceForDocumentRead extends Service
{

	private Timer timer = new Timer();
	private final int TIME_INTERVAL = 100;
	private class RemindTask extends TimerTask
	{
		@Override
		public void run()
		{
			try 
			{
				System.out.println("TIMER START");
				Looper.prepare();
				if(timer != null)
				{
					timer.cancel();
					callSynchronization();
				}
				Looper.loop();
			} 
			catch (Exception e)
			{
				Utility.saveExceptionDetails(ServiceForDocumentRead.this, e);
				e.printStackTrace();
			}
		}
	}

	RemindTask remind = new RemindTask();
	Timer t = new Timer();
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}
	@Override
	public void onCreate() 
	{
		super.onCreate();
		timer = new Timer();
		timer.schedule(new RemindTask(), TIME_INTERVAL);
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

	private void callSynchronization()
	{
		try 
		{
			SyncServiceHelper syc = new SyncServiceHelper();
			syc.sendDocumentReadDetail(ServiceForDocumentRead.this);
			this.stopService(new Intent(this, ServiceForDocumentRead.class));
		} 
		catch (Exception e) 
		{
			Utility.saveExceptionDetails(ServiceForDocumentRead.this, e);
			e.printStackTrace();
		}
	}
	
}