package ism.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import ism.android.baseclasses.AppBaseActivity;
import ism.android.login.LoginActivity;
import ism.android.utils.StaticVariables;

public class SplashActivity extends AppBaseActivity {

    /**
     * Called when the activity is first created.
     */

    //Class Declaration

    //Variables Declaration
    Intent myIntent = new Intent();
    Context mContext;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    private static final String TAG = "SplashActivity";

    long startTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mContext = this;
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.v(TAG, getString(R.string.gcm_send_message));
                } else {
                    Log.v(TAG, getString(R.string.token_error_message));
                }
            }
        };
        // Registering BroadcastReceiver
        registerReceiver();


        if (Utility.checkPlayServices(this)) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }else{
            showToast("This device is not supported. Google Play Services not available.", Toast.LENGTH_LONG);
            finish();
        }

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    startTimeMillis = System.currentTimeMillis();

                    SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
                    String regGUID = preferences.getString(getString(R.string.REGISTERED_GUID), null);
                    String strNewsBanner = preferences.getString(getString(R.string.NEWS_BANNER), null);
                    int orgID = preferences.getInt(getString(R.string.ORG_ID), 0);
                    String serviceLocation = preferences.getString(getString(R.string.WEB_SERVICE_LOCATION), null);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(getString(R.string.APP_RUNNING), true);
                    editor.commit();

                    if (null != serviceLocation && null != regGUID) {
                        StaticVariables.setURL(serviceLocation);
                        StaticVariables.setDeviceID(regGUID);
                        StaticVariables.setOrgID(orgID);
                        StaticVariables.setNEWS_BANNER(strNewsBanner);
                    }
                    // Set intent to registration.
                    myIntent.setClass(mContext, LoginActivity.class);      // Registration.class in employee app.
                    // If preferences are present and user is authenticated show shift activity
                    if (null != regGUID && orgID != 0 && serviceLocation != null) {
                        setValue();
                        myIntent.setClass(mContext, MainActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    // Make thread sleep if execution completes before expected time.
                    sleepForSpecifiedTime(startTimeMillis, 2000);
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(myIntent);
                            finish();
                            SplashActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                        }
                    });
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SplashActivity.this, e);
                    e.printStackTrace();
                }

            }
        };
        splashThread.start();
    }

    private void sleepForSpecifiedTime(long startTimeMillis, long totalTimeMillis) {
        try {
            long sleepTimeMillis = totalTimeMillis - (System.currentTimeMillis() - startTimeMillis);
            if (sleepTimeMillis > 0) {
                Thread.currentThread();
                Thread.sleep(sleepTimeMillis);
            }
        } catch (InterruptedException e) {
            Utility.saveExceptionDetails(SplashActivity.this, e);
            e.printStackTrace();
        }
    }

    //Set the Static variable For application
    public void setValue() {
        /** Get the record from user. if already exist user, set the userid if not then nothing **/
        Log.v("SplashActivity.java", "");
        Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getUserRecord();
        try {
            /*for(int i=0;i<c.getColumnCount();i++)
            {
				Log.d("============Column Name & value=========", c.getColumnName(i).toString()+" , "+c.getString(i));
			}*/

            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    ActivityStringInfo.strFirstName = c.getString(2);
                    ActivityStringInfo.strLastName = c.getString(4);
                    ActivityStringInfo.strLogin = c.getString(5);
                    ActivityStringInfo.strUser_id = c.getString(0);
                    ActivityStringInfo.strCalIdName  =c.getString(10);
                    ActivityStringInfo.strPositionTitle  =c.getString(12);
                    ActivityStringInfo.strDocRights = c.getString(13);
                    ActivityStringInfo.strSuggestright = c.getString(14);
                    ActivityStringInfo.strCompanyName = c.getString(8);

                }
            }
            //Log.v("Initialize.java", "check : " + ActivityStringInfo.strCompanyName + " first name: " + ActivityStringInfo.strFirstName);
        } catch (Exception e) {
            Utility.saveExceptionDetails(SplashActivity.this, e);
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
}
