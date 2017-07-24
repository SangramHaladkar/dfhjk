package ism.manager.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ism.manager.ActivityStringInfo;
import ism.manager.MainActivity;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;
import ism.manager.customview.ToastMessage;
import ism.manager.utils.AlertDialogManager;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.StaticVariables;
import ism.manager.webservices.ServicesHelper;
import ism.manager.webservices.Synchronization;

public class ChangePasswordActivity extends AppBaseActivity {

    Button btnChangePassword;
    EditText edTempPassword, edNewPassword, edConfirmPassword;

    //Variable Declaration

    String[] calendarList;
    HashMap<Integer, String> getcalInfo = new HashMap<Integer, String>();
    boolean blncheckStatus = true;

    Context mContext;
    private String updatedCalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        try {
            mContext = this;
            this.initialize();
            this.setValues();
            this.setListeners();
        } catch (Exception e) {
            Utility.saveExceptionDetails(ChangePasswordActivity.this, e);
            e.printStackTrace();
        }
    }

    private void initialize() {
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        edTempPassword = (EditText) findViewById(R.id.edTempPassword);
        edNewPassword = (EditText) findViewById(R.id.edNewPassword);
        edConfirmPassword = (EditText) findViewById(R.id.edConfirmPassword);
        this.updatedCalName ="";
    }

    private void setValues() {

    }

    private void setListeners() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blncheckStatus) {
                    if (validation()) {
                        StaticVariables.setURL(ActivityStringInfo.wsLocation + "/smartphone.svc");
                        new SelectDataTaskForSetChangeTempPassword().execute(edTempPassword.getText().toString().trim(), edNewPassword.getText().toString().trim());
                    }
                } else {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                    ChangePasswordActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                }
            }
        });

    }

    private class SelectDataTaskForSetChangeTempPassword extends AsyncTask<String, Void, String> {

        // can use UI thread here
        protected void onPreExecute() {
            showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                ServicesHelper servicesHelper = new ServicesHelper();
                return servicesHelper.setChangeTempPassword(mContext, args[0], args[1]);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ChangePasswordActivity.this, e);
                e.printStackTrace();
                return "";
            }
        }

        // can use UI thread here
        protected void onPostExecute(final String strMsg) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true")) {
                blncheckStatus = false;
                showToast("Password changed successfully.", Toast.LENGTH_SHORT);
                ListSelectedCalendars();
//                pending implementation
            } else if (strMsg.equals("false")) {
                showToast(MessageInfo.strTempPassIncorrect, Toast.LENGTH_SHORT);
            } else {
                if (Utility.Connectivity_Internet(mContext)) {
                    showToast(strMsg, Toast.LENGTH_SHORT);
                }//toastMsg.showToastMsg(strMsg, Toast.LENGTH_SHORT);
                else {
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            }
        }
    }

    private boolean validation() {
        boolean bln = true;
        try {
            if (edTempPassword.getText().toString().equals("")) {
                bln = false;
                showToast("Please enter temporary password.", Toast.LENGTH_SHORT);
            } else if (edNewPassword.getText().toString().equals("")) {
                bln = false;
                showToast("Please enter new password.", Toast.LENGTH_SHORT);
            } else if (edConfirmPassword.getText().toString().equals("")) {
                bln = false;
                showToast("Please enter confrim password.", Toast.LENGTH_SHORT);
            } else if (edNewPassword.length() < 5) {
                showToast("Please check the values you have entered.", Toast.LENGTH_SHORT);
                return false;
            } else if (!edNewPassword.getText().toString().equals(edConfirmPassword.getText().toString())) {
                showToast(MessageInfo.strNewConfimPassIncorrect, Toast.LENGTH_SHORT);
                return false;
            }

        } catch (Exception e) {
            Utility.saveExceptionDetails(ChangePasswordActivity.this, e);
            e.printStackTrace();
        }
        return bln;
    }
    public int ListSelectedCalendars()
    {
        int result = 0;
        try
        {
            getcalInfo = new HashMap<Integer, String>();
            Cursor managedCursor = Utility.getcalendars(ChangePasswordActivity.this);
            List<String> wordList = Arrays.asList(managedCursor.getColumnNames());

            if (managedCursor != null && managedCursor.moveToFirst())
            {
                int nameColumn = managedCursor.getColumnIndex("name");
                int idColumn = managedCursor.getColumnIndex("_id");
                String calName = "";
                String calId = "";
                int visibleColumn = -1;
                int accesslevel = -1;
                int count = 0;

                if(wordList.contains(CalendarContract.Calendars.NAME))
                {
                    visibleColumn = managedCursor.getColumnIndex(CalendarContract.Calendars.NAME);
                    accesslevel = managedCursor.getColumnIndex(CalendarContract.Calendars.NAME);
                }

                do {
                    calName = managedCursor.getString(nameColumn);
                    calId = managedCursor.getString(idColumn);
                    String visibleStatus ="";

                    if(visibleColumn!= -1)
                        visibleStatus = managedCursor.getString(visibleColumn);

                    String strAccessLevel = managedCursor.getString(accesslevel);

                    System.out.println(calName+ ".....VISIBILE STATUS : "+visibleStatus +"     strAccessLevel : "+strAccessLevel);

                    managedCursor.getCount();

					/*if(visibleStatus.equals("1") || visibleStatus.equals(""))
					{
						getcalInfo.put(count, calId+"-"+calName);
						count++;
					}*/

                    if(!strAccessLevel.equals("200"))
                    {
                        getcalInfo.put(count, calId+"-"+calName);
                        count++;
                    }

                } while (managedCursor.moveToNext());

                if(ActivityStringInfo.strLogin.equals(ActivityStringInfo.strLoginName))
                    ActivityStringInfo.strCalIdName = ActivityStringInfo.strCalendarName;

                if(ActivityStringInfo.strCalIdName.equals(""))
                {
                    System.out.println("in 111111");
                    alertbox();
                }
                else if(!ActivityStringInfo.strLogin.equals(ActivityStringInfo.strLoginName))
                {
                    System.out.println("in 22222");
                    ActivityStringInfo.strCalIdName = "";
                    alertbox();
                }
                else if(!getcalInfo.containsValue(ActivityStringInfo.strCalIdName))
                {
                    System.out.println("in 33333");
                    ActivityStringInfo.strCalIdName = "";
                    alertForNotExistCalendar();
                }
                else
                {
                    System.out.println("in 4444");
                    new SelectDataTaskForRefresh().execute();
                }
            }
            else
            {
                showToast("Please make sure that 'Calendar' checkbox is checked in Calendar's settings.", Toast.LENGTH_LONG);
            }
        }
        catch (Exception e)
        {
            Utility.saveExceptionDetails(this, e);
            e.printStackTrace();
        }
        return result;
    }

    public void alertbox()
    {
        try
        {
            calendarList = new String[getcalInfo.size()];
            for(int i =0 ;i <getcalInfo.size();i++)
            {
                calendarList[i] = getcalInfo.get(i).substring(getcalInfo.get(i).indexOf("-")+1);
            }
            final AlertDialog.Builder alert = new AlertDialog.Builder(ChangePasswordActivity.this);
            alert.setTitle(MessageInfo.strSelectCalDialogTitle);
            alert.setIcon(R.drawable.ic_ism);
            alert.setSingleChoiceItems(calendarList,-1, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if(calendarList[which].toString().equals(getcalInfo.get(which).substring(getcalInfo.get(which).indexOf("-")+1).toString()))
                    {
                        ActivityStringInfo.strCalIdName = getcalInfo.get(which).toString();
                        updatedCalName = getcalInfo.get(which).toString();
                    }
                }
            });
            alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        dialog.cancel();
                        logout();
                        return true;
                    }
                    return false;
                }
            });
            alert.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int m) {
                    if(ActivityStringInfo.strCalIdName.equals(""))
                    {
                        showToast("Please select calendar option.", Toast.LENGTH_SHORT);
                        alert.show();
                    }
                    else
                    {
                        new SelectDataTaskForRefresh().execute();
                    }
                }
            });
            alert.show();
        }
        catch (Exception e)
        {
            Utility.saveExceptionDetails(this, e);
            e.printStackTrace();
        }
    }

    public void alertForNotExistCalendar()
    {
        try
        {
            final AlertDialog.Builder alert = new AlertDialog.Builder(ChangePasswordActivity.this);
            alert.setTitle("Calendar Alert");
            alert.setIcon(R.drawable.ic_ism);
            alert.setMessage("Previously selected calendar is not exists so please select new calendar.");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int m) {
                    alertbox();
                }
            });
            alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        dialog.cancel();
                        logout();
                        return true;
                    }
                    return false;
                }
            });
            alert.show();
        }
        catch (Exception e)
        {
            Utility.saveExceptionDetails(this, e);
            e.printStackTrace();
        }
    }

    public void logout()
    {
        try
        {
            SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.REGISTERED_GUID), null);
            editor.putInt(getString(R.string.ORG_ID), 0);
            editor.putString(getString(R.string.WEB_SERVICE_LOCATION), null);
            editor.commit();
            Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent2);
            finish();
            ChangePasswordActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        }
        catch (Exception e)
        {
            Utility.saveExceptionDetails(this, e);
            e.printStackTrace();
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


                if (Utility.firstTimeLogin(mContext)) {
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
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertUserData(updatedCalName);
                    System.out.println("database successfully flushed...");
                }

                Synchronization syc = new Synchronization(ChangePasswordActivity.this);
                strMsg = syc.getInformation(ChangePasswordActivity.this);
                if (strMsg.equals("true")) {
                    System.out.println("data successfully inserted newly for first time login...");
                    StaticVariables.setNEWS_BANNER(ActivityStringInfo.strNewsBanner);
                    final Calendar cal = Calendar.getInstance();
                    /** If new user then insert the record otherwise set the login status in record**/
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFS_NAME), MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(getString(R.string.REGISTERED_GUID), StaticVariables.getDeviceID(ChangePasswordActivity.this));
                    editor.putInt(getString(R.string.ORG_ID), StaticVariables.getOrgID(ChangePasswordActivity.this));
                    editor.putString(getString(R.string.WEB_SERVICE_LOCATION), StaticVariables.getURL(ChangePasswordActivity.this));
                    editor.putString(getString(R.string.NEWS_BANNER), ActivityStringInfo.strNewsBanner);
                    editor.putLong(getString(R.string.TIMER), cal.getTimeInMillis());
                    editor.commit();
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ChangePasswordActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true")) {
                getAlertDialogManager().showAlertDialog(MessageInfo.company_name, MessageInfo.calenderEvent, MessageInfo.ok, null, new AlertDialogManager.OnCustomDialogClicklistenr() {
                    @Override
                    public void onPositiveClick() {
                        Intent myIntent = new Intent();
                        myIntent.setClass(getApplicationContext(), MainActivity.class);
                        startActivity(myIntent);
                        ChangePasswordActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
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

}
