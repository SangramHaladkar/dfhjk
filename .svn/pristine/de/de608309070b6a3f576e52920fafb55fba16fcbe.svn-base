package ism.manager.login;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Calendars;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import ism.manager.ActivityStringInfo;
import ism.manager.GCMPreferences;
import ism.manager.MainActivity;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.RegistrationIntentService;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;
import ism.manager.utils.AlertDialogManager;
import ism.manager.utils.AppUtil;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.ServerException;
import ism.manager.utils.StaticVariables;
import ism.manager.webservices.RegistrationHelper;
import ism.manager.webservices.Synchronization;

public class LoginActivity extends AppBaseActivity {

    TextView appVersionName;
    TextView btnLogin;
    TextView btnForgotPwd;
    EditText edtUserId;
    EditText edtPassword;

    //Variables Declaration
    boolean userRegistered = false;
    boolean blnCheckForgot = true;
    boolean gcmRegistered = false;
    boolean isCalenderNotExist = false;

    String[] calendarList;
    private static final String DEBUG_TAG = "LoginActivity";
    HashMap<Integer, String> getcalInfo = new HashMap<Integer, String>();
    Context mContext;
    private String updatedCalIdName;
    private boolean isBReceiverRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            mContext = this;
            LocalBroadcastManager.getInstance(this).registerReceiver(pushNotificationMessageReceiver, new IntentFilter("pushNotification"));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.actionbar_icon);
            this.initializeView();
            this.setValues();
            this.setListeners();
        } catch (Exception e) {
            Utility.saveExceptionDetails(LoginActivity.this, e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pushNotificationMessageReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(pushNotificationMessageReceiver);

        if (mTokenRegBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mTokenRegBroadcastReceiver);

        isBReceiverRegistered = false;
    }

    private void initializeView() {
        appVersionName = (TextView) findViewById(R.id.txtLoginVer);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnForgotPwd = (TextView) findViewById(R.id.btnForgotPwd);
        edtUserId = (EditText) findViewById(R.id.edtUserId);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        this.updatedCalIdName = "";
    }

    private void setValues() {
        appVersionName.setText(ActivityStringInfo.VERSION);
        edtUserId.setNextFocusDownId(R.id.edtPassword);
        edtPassword.setNextFocusDownId(R.id.btnLogin);
        this.setUser();
    }

    private void setListeners() {
        this.edtPassword.setImeActionLabel(getResources().getString(R.string.login), EditorInfo.IME_ACTION_DONE);
        this.edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtPassword.getWindowToken(), 0);
                    //Code for login
                    btnLogin.requestFocus();
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        });
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });

        btnForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPwdIntent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(forgotPwdIntent);
                LoginActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });
    }

    /**
     * Login User with User Id and Password
     */
    private void doLogin() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtPassword.getWindowToken(), 0);
            if (validation()) {

                showTransparentProgressDialog(MessageInfo.loginProgress_txt);
                Thread regThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (AppUtil.pingURL(ActivityStringInfo.wsLocation)) {
                                if (isGCMTokenAvailable()) {
                                    userRegistered = registerUserToDevice(edtUserId.getText().toString(), edtPassword.getText().toString(), "", ActivityStringInfo.wsLocation);
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                /** Notify user if registered successfully and navigate **/
                                                if (userRegistered) {
                                                    hideTransparentProgressDialog();
//
                                                    if (Utility.checkCalendarPermission(mContext)) {
                                                        ListSelectedCalendars();
                                                    } else {
                                                        String msg = Utility.requestAppPermission(mContext);
                                                        showToast(msg, Toast.LENGTH_SHORT);
                                                    }
//
                                                } else {
                                                    /** Notify - if registration is unsuccessful **/
                                                    hideTransparentProgressDialog();
                                                    if (blnCheckForgot) {
                                                        if (ActivityStringInfo.strLoginError.equals(""))
                                                            showToast(MessageInfo.creden_incorrect_txt, Toast.LENGTH_SHORT);
                                                        else
                                                            showToast(ActivityStringInfo.strLoginError, Toast.LENGTH_SHORT);

                                                    }
                                                }
                                            } catch (Exception e) {
                                                Utility.saveExceptionDetails(LoginActivity.this, e);
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    /** Notify send back to generate GCM Token if it is not previously existed **/
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            registerReceiver();
                                            generateGCMToken();
                                        }
                                    });

                                }
                            } else {
                                /** Notify user URL is incorrect. **/
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        hideTransparentProgressDialog();
                                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                        } catch (final ServerException e) {
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    hideTransparentProgressDialog();
                                    showToast(e.getError(), Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                };
                regThread.start();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LoginActivity.this, e);
            e.printStackTrace();
        }
    }

    private void runLoginThread() {

    }

    /***
     * Get the record from user. if already exist user, set the userid if not then nothing
     */
    private void setUser() {
        Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getUserRecord();
        try {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    edtUserId.setText(c.getString(5));
                    ActivityStringInfo.strCompanyName = c.getString(8);
                    ActivityStringInfo.strCalendarName = c.getString(10);
                    ActivityStringInfo.strCalIdName = c.getString(10);
                    ActivityStringInfo.strLoginName = c.getString(5);
                }
            }
        } catch (Exception e) {
//            Utility.saveExceptionDetails(Registration.this, e);
            e.printStackTrace();
        } finally {
            c.close();
        }
    }

    public boolean validation() {
        boolean bln = true;
        try {
            //System.out.println("ActivityStringInfo.strCalIdName==="+ActivityStringInfo.strCalIdName);
            if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                alertBoxForSdCard();
                bln = false;
            } else if (edtUserId.getText().toString().equals("")) {
                showToast(MessageInfo.strEnterUserId, Toast.LENGTH_SHORT);
                bln = false;
            } else if (edtPassword.getText().toString().equals("")) {
                showToast(MessageInfo.strEnterPassword, Toast.LENGTH_SHORT);
                bln = false;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(this, e);
            e.printStackTrace();
        }
        return bln;
    }

    public void alertBoxForSdCard() {
        getAlertDialogManager().showAlertDialog(MessageInfo.information_, MessageInfo.sdCardError, MessageInfo.ok, null, new AlertDialogManager.OnCustomDialogClicklistenr() {
            @Override
            public void onPositiveClick() {
                finish();
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }

    private boolean registerUserToDevice(String loginName, String password, String location, String wsLocation) throws ServerException {
        String guid = "";
        int orgID = 0;
        RegistrationHelper helper = new RegistrationHelper();
        String wsURL = AppUtil.formatWebServiceLocation(wsLocation);

        try {
            Hashtable<String, String> regUser = helper.registerUser(LoginActivity.this, loginName, password, location, wsURL);

            if (null != regUser && regUser.size() > 0) {
                try {
                    guid = regUser.get("DeviceID");
                    orgID = Integer.parseInt(regUser.get("OrgID"));
                    ActivityStringInfo.strUser_id = regUser.get("User_id");
                    ActivityStringInfo.ORG_ID = orgID;
                    ActivityStringInfo.regGUID = guid;
                    ActivityStringInfo.strWsLocation = wsURL;
                    ActivityStringInfo.strTempPassword = password;
                    ActivityStringInfo.strCompanyName = "";
                    ActivityStringInfo.strFirstName = regUser.get("First_Name");
                    ActivityStringInfo.strLastName = regUser.get("Last_Name");
                    ActivityStringInfo.strLogin = edtUserId.getText().toString();
                    ActivityStringInfo.strPositionTitle = regUser.get("Position_Title");
                    ActivityStringInfo.strAcceptTerms = regUser.get("AcceptTerms");
                    ActivityStringInfo.strAutoGenPwd = regUser.get("AutoGenPwd");

                    Log.v(DEBUG_TAG, "ActivityStringInfo.regGUID: " + ActivityStringInfo.regGUID);
                    Log.v(DEBUG_TAG, "ActivityStringInfo.GCM_REG_ID: " + ActivityStringInfo.gcm_reg_id);

                    if (ActivityStringInfo.strAcceptTerms.toLowerCase().equals("false")) {

                        StaticVariables.setURL(wsURL);
                        StaticVariables.setDeviceID(guid);
                        StaticVariables.setOrgID(orgID);

						/*SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
						editor.putString(getString(R.string.REGISTERED_GUID), StaticVariables.getDeviceID(Registration.this));
						editor.putInt(getString(R.string.ORG_ID), StaticVariables.getOrgID(Registration.this));
						editor.putString(getString(R.string.WEB_SERVICE_LOCATION), StaticVariables.getURL(Registration.this));
						editor.commit();*/

                        blnCheckForgot = false;
                        Intent myIntent = new Intent();
                        myIntent.setClass(getApplicationContext(), AgreementActivity.class);
                        startActivity(myIntent);
                        LoginActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                        finish();
                        return false;
                    }
                    if (ActivityStringInfo.strAutoGenPwd.toLowerCase().equals("true")) {

                        StaticVariables.setURL(wsURL);
                        StaticVariables.setDeviceID(guid);
                        StaticVariables.setOrgID(orgID);

						/*SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
						editor.putString(getString(R.string.REGISTERED_GUID), StaticVariables.getDeviceID(Registration.this));
						editor.putInt(getString(R.string.ORG_ID), StaticVariables.getOrgID(Registration.this));
						editor.putString(getString(R.string.WEB_SERVICE_LOCATION), StaticVariables.getURL(Registration.this));
						editor.commit();*/

                        blnCheckForgot = false;
                        Intent myIntent = new Intent();
                        myIntent.setClass(getApplicationContext(), ChangePasswordActivity.class);
                        startActivity(myIntent);
                        LoginActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                        finish();
                        return false;
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(LoginActivity.this, e);
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }

            /**  Save Device ID, Org ID and Web Service Location in ActivityStringInfo. **/
            if (null != guid && guid.length() > 0 && orgID > 0) {
                try {
                    /** Set the Web Service Location and necessary info. **/
                    StaticVariables.setURL(wsURL);
                    StaticVariables.setDeviceID(guid);
                    StaticVariables.setOrgID(orgID);

                } catch (Exception e) {
                    Utility.saveExceptionDetails(LoginActivity.this, e);
                    e.printStackTrace();
                }
                return true;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LoginActivity.this, e);
            e.printStackTrace();
        }
        return false;
    }

    private int ListSelectedCalendars() {
        int result = 0;
        try {
            getcalInfo = new HashMap<Integer, String>();
            Cursor managedCursor = Utility.getcalendars(mContext);
            List<String> wordList = Arrays.asList(managedCursor.getColumnNames());
            if (managedCursor != null && managedCursor.moveToFirst()) {

                int nameColumn = managedCursor.getColumnIndex(Calendars.NAME);
                int idColumn = managedCursor.getColumnIndex(Calendars._ID);
                int visibleColumn = -1;
                int accesslevel = -1;
                int count = 0;

                if (wordList.contains(Calendars.NAME)) {
                    visibleColumn = managedCursor.getColumnIndex(Calendars.NAME);
                    accesslevel = managedCursor.getColumnIndex(Calendars.NAME);
                }

                do {
                    String calName = managedCursor.getString(nameColumn);
                    String calId = managedCursor.getString(idColumn);
                    String visibleStatus = "";
                    if (visibleColumn != -1)
                        visibleStatus = managedCursor.getString(visibleColumn);
                    String strAccessLevel = managedCursor.getString(accesslevel);

                    managedCursor.getCount();

                    if (!strAccessLevel.equals("200")) {
                        getcalInfo.put(count, calId + "-" + calName);
                        count++;
                    }

                } while (managedCursor.moveToNext());

                if (ActivityStringInfo.strLogin.equals(ActivityStringInfo.strLoginName))
                    ActivityStringInfo.strCalIdName = ActivityStringInfo.strCalendarName;

// 					To DO
//					Need to change name of the calendar dynamically....
//					that application is storing in the calendar or not.
                ActivityStringInfo.strCalendarName = "StaffTap<" + edtUserId.getText().toString().trim() + ">ACTIVE";
                Log.i(DEBUG_TAG, "Calender Name is " + ActivityStringInfo.strCalendarName);
                if (ActivityStringInfo.strCalIdName.equals("")) {
                    alertbox();
                } else if (!ActivityStringInfo.strLogin.equals(ActivityStringInfo.strLoginName)) {
                    ActivityStringInfo.strCalIdName = "";
                    ActivityStringInfo.strCalendarName = "StaffTap<" + edtUserId.getText().toString().trim() + ">ACTIVE";
                    alertbox();
                } else if (!getcalInfo.containsValue(ActivityStringInfo.strCalIdName)) {
                    isCalenderNotExist = true;
                    alertForNotExistCalendar();
                } else {
                    new SelectDataTaskForRefresh().execute();
                }
            } else {
                showToast("Please make sure that 'Calendar' checkbox is checked in Calendar's settings.", Toast.LENGTH_LONG);
                Log.i(DEBUG_TAG, "No Calendars");
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(this, e);
            e.printStackTrace();
        }

        return result;
    }

    public void alertbox() {
        calendarList = new String[getcalInfo.size()];
        for (int i = 0; i < getcalInfo.size(); i++) {
            calendarList[i] = getcalInfo.get(i).substring(getcalInfo.get(i).indexOf("-") + 1);
        }
        for (int i = 0; i < calendarList.length; i++) {
            if (calendarList[i].toString().equals(getcalInfo.get(i).substring(getcalInfo.get(i).indexOf("-") + 1).toString())) {
                System.out.println("calendar id===" + getcalInfo.get(i).substring(0, getcalInfo.get(i).indexOf("-")));
                System.out.println("calendar Name===" + getcalInfo.get(i).substring(getcalInfo.get(i).indexOf("-") + 1));
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(MessageInfo.strSelectCalDialogTitle);
        builder.setIcon(R.mipmap.main_icon);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(calendarList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (calendarList[which].toString().equals(getcalInfo.get(which).substring(getcalInfo.get(which).indexOf("-") + 1).toString())) {
                    ActivityStringInfo.strCalIdName = getcalInfo.get(which).toString();
                    updatedCalIdName = getcalInfo.get(which).toString();
                    System.out.println("selected calendar id===" + getcalInfo.get(which).substring(0, getcalInfo.get(which).indexOf("-")));
                    System.out.println("selected calendar Name===" + getcalInfo.get(which).substring(getcalInfo.get(which).indexOf("-") + 1));
                }
            }
        });
        builder.setPositiveButton(MessageInfo.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityStringInfo.strCalIdName.equals("")) {
                    showToast("Please select calendar option.", Toast.LENGTH_SHORT);
                    builder.create().show();
                } else {
                    dialog.dismiss();
                    new SelectDataTaskForRefresh().execute();
                }
            }
        });
        builder.create().show();
    }

    public void alertForNotExistCalendar() {
        getAlertDialogManager().showAlertDialog(MessageInfo.calendar_alert, MessageInfo.calendar_not_exists_alert_msg, MessageInfo.ok, null, new AlertDialogManager.OnCustomDialogClicklistenr() {
            @Override
            public void onPositiveClick() {
                alertbox();
            }

            @Override
            public void onNegativeClick() {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MessageInfo.calendar_permission_request_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ListSelectedCalendars();
                } else {
                    String msg = Utility.requestAppPermission(mContext);
                    showToast(msg, Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    private class SelectDataTaskForRefresh extends AsyncTask<String, Void, String> {

        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            showTransparentProgressDialog(MessageInfo.registrationProgress_txt);

        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                if (Utility.firstTimeLogin(mContext) || isCalenderNotExist) {
                    Utility.ListCalendarEntryDeleteByFile(mContext);
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteUsersRecords();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllDocument();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllAddressBook();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageAttachmentList();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageList();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMessageToUserList();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllMeetingList();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllShiftList();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllShiftTaskList();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLeaveRequestList();
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLoginQuestion();
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertUserData(updatedCalIdName);
                    System.out.println("database successfully flushed...");
                    isCalenderNotExist = false;
                }
                Synchronization syc = new Synchronization(LoginActivity.this);
                strMsg = syc.getInformation(LoginActivity.this);
                if (strMsg.equals("true")) {
                    System.out.println("data successfully inserted newly for first time login...");
                    StaticVariables.setNEWS_BANNER(ActivityStringInfo.strNewsBanner);
                    final Calendar cal = Calendar.getInstance();
                    /** If new user then insert the record otherwise set the login status in record**/
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.REGISTERED_GUID), StaticVariables.getDeviceID(LoginActivity.this));
                    editor.putInt(getString(R.string.ORG_ID), StaticVariables.getOrgID(LoginActivity.this));
                    editor.putString(getString(R.string.WEB_SERVICE_LOCATION), StaticVariables.getURL(LoginActivity.this));
                    editor.putString(getString(R.string.NEWS_BANNER), ActivityStringInfo.strNewsBanner);
                    editor.putLong(getString(R.string.TIMER), cal.getTimeInMillis());
                    editor.commit();
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LoginActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true")) {
                getAlertDialogManager().showAlertDialog(MessageInfo.company_name, MessageInfo.calenderEvent, MessageInfo.ok, null, false, new AlertDialogManager.OnCustomDialogClicklistenr() {
                    @Override
                    public void onPositiveClick() {
                        ActivityStringInfo.isListItemSelected = false;
                        Intent myIntent = new Intent();
                        myIntent.setClass(getApplicationContext(), MainActivity.class);
                        startActivity(myIntent);
                        LoginActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                        finish();
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });
            } else if (strMsg.equals("false")) {
                showToast(MessageInfo.creden_incorrect_txt, Toast.LENGTH_SHORT);
            } else {
                if (Utility.Connectivity_Internet(mContext)) {
                    if (!strMsg.equals(""))
                        showToast(strMsg, Toast.LENGTH_SHORT);
                } else
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
            }
        }
    }

    public boolean isGCMTokenAvailable() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
    }

    public void generateGCMToken() {
        if (Utility.checkPlayServices(this)) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        } else {
            hideTransparentProgressDialog();
            showToast("This device is not supported. Google Play Services not available.", Toast.LENGTH_LONG);
        }
    }

    private void registerReceiver() {
        if (!isBReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mTokenRegBroadcastReceiver, new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
            isBReceiverRegistered = true;
        }
    }

    private BroadcastReceiver mTokenRegBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.v(DEBUG_TAG, getString(R.string.gcm_send_message));
                    doLogin();
                } else {
                    Log.v(DEBUG_TAG, getString(R.string.token_error_message));
                    hideTransparentProgressDialog();
                    showToast(getString(R.string.token_error_message), Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                Log.v(DEBUG_TAG, e.getMessage());
                showToast(e.getMessage(), Toast.LENGTH_LONG);
            }
        }
    };

}
