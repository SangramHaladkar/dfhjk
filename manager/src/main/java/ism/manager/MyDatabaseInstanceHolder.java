package ism.manager;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ism.manager.utils.DatabaseHelper;

public class MyDatabaseInstanceHolder extends Application
{
	private final String TAG = getClass().getSimpleName();
	private static DatabaseHelper dbHelper;
	
	@Override
	public void onCreate() 
	{
		
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		Log.d(TAG, "onCreate()");
		dbHelper = new DatabaseHelper(this);
		try 
		{
			dbHelper.createDataBase();
			dbHelper.openDataBase();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, "onTerminate()");
		dbHelper.close();
	}

	public static DatabaseHelper getDatabaseHelper() {
		return dbHelper;
	}

}
