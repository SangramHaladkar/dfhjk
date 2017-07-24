package ism.android;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;
import ism.android.utils.DatabaseHelper;


public class MyDatabaseInstanceHolder extends Application
{
	private final String TAG = getClass().getSimpleName();
	private static DatabaseHelper dbHelper;
	
	@Override
	public void onCreate() 
	{
		
		super.onCreate();
		Crashlytics crashlyticsKit = new Crashlytics.Builder()
				.core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
				.build();
		Fabric.with(this,crashlyticsKit);
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
