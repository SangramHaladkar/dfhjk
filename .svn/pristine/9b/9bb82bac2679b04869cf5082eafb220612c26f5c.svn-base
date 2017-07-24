package ism.manager.schedule;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ism.manager.ActivityStringInfo;
import ism.manager.R;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.StaticVariables;
import ism.manager.webservices.SyncServiceHelper;
import ism.manager.webservices.Synchronization;

public class RequestOffActivity extends AppBaseActivity {

    //for UI component
    Button btn_Submit;
    EditText ed_Date, ed_Comments;
    ImageButton btn_SelectDate;

    //Other Class Declaration
    ActivityStringInfo stringInfo;
    SyncServiceHelper serviceHelper;

    //Variable Declaration
    public static final String INFO = "INFO";
    int mMonth, mYear, mDay;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_request_off);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            mContext = this;
            serviceHelper = new SyncServiceHelper();

            /** Initialize the UI component **/
            btn_Submit = (Button) findViewById(R.id.btnSubmit);
            ed_Date = (EditText) findViewById(R.id.edDate);
            ed_Comments = (EditText) findViewById(R.id.edComments);
            btn_SelectDate = (ImageButton) findViewById(R.id.btnSelectDate);

            ed_Date.requestFocus();
            ed_Comments.clearFocus();

            if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
                new SplashActivity().setValue();
            }

            setLocalSystemDate();

            ed_Date.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    showDialog(StaticVariables.DATE_DIALOG_ID);
                }
            });

            btn_SelectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showDialog(StaticVariables.DATE_DIALOG_ID);
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(RequestOffActivity.this, e);
                        e.printStackTrace();
                    }
                }
            });

            ed_Comments.setImeActionLabel(getResources().getString(R.string.submit), EditorInfo.IME_ACTION_DONE);
            ed_Comments.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // hide virtual keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(ed_Comments.getWindowToken(), 0);
                        //Code for submit
                        btn_Submit.requestFocus();
                        btn_Submit.performClick();
                        return true;
                    }
                    return false;
                }
            });

            btn_Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if (validation()) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                            new SelectDataTaskForRequestOff().execute(simpleDateFormat.format(StaticVariables.generalDateFormat.parse(ed_Date.getText().toString())), ed_Comments.getText().toString());
                        }
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(RequestOffActivity.this, e);
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Utility.saveExceptionDetails(RequestOffActivity.this, e);
            e.printStackTrace();
        }

    }

    public boolean validation() {
        boolean bln = true;
        try {
            if (ed_Comments.getText().length() <= 0) {
                showToast(MessageInfo.enter_comment_txt, Toast.LENGTH_SHORT);
                bln = false;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(RequestOffActivity.this, e);
            e.printStackTrace();
        }
        return bln;
    }

    public void setLocalSystemDate() {
        try {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 1);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            updateDate(StaticVariables.generalDateFormat.format(c.getTime()));
        } catch (Exception e) {
            Utility.saveExceptionDetails(RequestOffActivity.this, e);
            e.printStackTrace();
        }
    }

    private void updateDate(String Date) {
        try {
            ed_Date.setText(Date);
        } catch (Exception e) {
            Utility.saveExceptionDetails(RequestOffActivity.this, e);
            e.printStackTrace();
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, mYear);
                cal.set(Calendar.MONTH, mMonth);
                cal.set(Calendar.DAY_OF_MONTH, mDay);
                updateDate(StaticVariables.generalDateFormat.format(cal.getTime()));
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffActivity.this, e);
                e.printStackTrace();
            }
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        try {
            switch (id) {
                case StaticVariables.DATE_DIALOG_ID:
                    return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(RequestOffActivity.this, e);
            e.printStackTrace();
        }
        return null;
    }

    private class SelectDataTaskForRequestOff extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {

            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                try {
                    strMsg = serviceHelper.sendRequestOff(RequestOffActivity.this, args[0], args[1]);
                    if (strMsg.equals("true")) {
                        Synchronization syc = new Synchronization(mContext);
                        try {
                            strMsg = syc.getInformation(RequestOffActivity.this);
                        } catch (Exception e) {
                            Utility.saveExceptionDetails(RequestOffActivity.this, e);
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(RequestOffActivity.this, e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.equals("true")) {
                    showToast("Request submitted successfully.", Toast.LENGTH_SHORT);
                    ActivityStringInfo.shouldRefreshed = true;
                    onBackPressed();
                } else if (!strMsg.equals("false")) {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffActivity.this, e);
                e.printStackTrace();
            }
        }
    }


}
