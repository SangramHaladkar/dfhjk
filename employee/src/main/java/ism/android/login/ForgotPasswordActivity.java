package ism.android.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import ism.android.ActivityStringInfo;
import ism.android.R;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseActivity;
import ism.android.customview.TransparentProgressDialog;
import ism.android.utils.AlertDialogManager;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.utils.StaticVariables;
import ism.android.webservices.ServicesHelper;


public class ForgotPasswordActivity extends AppBaseActivity {

    EditText edtUserId;
    EditText edtCompanyName;
    EditText edtAnswer;

    TextView txt_Question;
    TextView txtError;
    TextView btnVerify;
    TextView btnNextPage;

    LinearLayout questionLayout;

    Context mContext;
    int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            this.initialize();
            this.setListeners();

        } catch (Exception e) {
            Utility.saveExceptionDetails(ForgotPasswordActivity.this, e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgotpwd, menu);
        return true;
    }

    private void initialize() {
        mContext = this;
        edtUserId = (EditText) findViewById(R.id.edtUserId);
        edtCompanyName = (EditText) findViewById(R.id.edtCompanyName);
        edtAnswer = (EditText) findViewById(R.id.edtAnswer);
        txt_Question = (TextView) findViewById(R.id.txtQuestion);
        txtError = (TextView) findViewById(R.id.txtError);
        btnVerify = (TextView) findViewById(R.id.btnVerify);
        btnNextPage = (TextView) findViewById(R.id.btnNextPage);
        questionLayout = (LinearLayout) findViewById(R.id.questionLayout);
    }

    private void setListeners() {

        this.edtCompanyName.setImeActionLabel(getResources().getString(R.string.verify), EditorInfo.IME_ACTION_DONE);
        this.edtCompanyName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtCompanyName.getWindowToken(), 0);
                    btnVerify.requestFocus();
                    btnVerify.performClick();
                    return true;
                }
                return false;
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 0;
                edtAnswer.setText("");
                if (validation()) {
                    StaticVariables.setURL(ActivityStringInfo.wsLocation + "/smartphone.svc");
                    new SelectDataTaskForForgotQuestion().execute(edtUserId.getText().toString().trim(), edtCompanyName.getText().toString().trim());
                }
            }
        });

        this.edtAnswer.setImeActionLabel(getResources().getString(R.string.next), EditorInfo.IME_ACTION_DONE);
        this.edtAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtAnswer.getWindowToken(), 0);
                    btnNextPage.requestFocus();
                    btnNextPage.performClick();
                    return true;
                }
                return false;
            }
        });

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAnswer.getText().toString().equals("")) {
                    showToast("Please enter answer.", Toast.LENGTH_SHORT);
                } else {
                    HashMap<String, String> hashQuestion1 = ActivityStringInfo.ForgotPasswordQuestionList.get(counter);
                    if (hashQuestion1.get(DatabaseConstant.key_SEC_ANS_1).toString().equals(edtAnswer.getText().toString())) {
                        new SelectDataTaskForGetResetPassword().execute(edtUserId.getText().toString().trim());
                    } else if (ActivityStringInfo.ForgotPasswordQuestionList.size() == counter + 1) {
                        edtAnswer.setText("");
                        txt_Question.setText("");
                        questionLayout.setVisibility(View.GONE);
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(MessageInfo.contact_system_admin);
                        btnVerify.setVisibility(View.INVISIBLE);
                        btnNextPage.setVisibility(View.GONE);
                    } else {
                        alertMsg();
                    }
                }
            }
        });
    }

    private void alertMsg() {
        try {
            getAlertDialogManager().showAlertDialog(MessageInfo.security_alert, MessageInfo.security_alert_msg, MessageInfo.ok, MessageInfo.no, new AlertDialogManager.OnCustomDialogClicklistenr() {
                @Override
                public void onPositiveClick() {
                    counter++;
                    getNextQuestion();
                }

                @Override
                public void onNegativeClick() {
                    finish();
                }
            });
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForgotPasswordActivity.this, e);
            e.printStackTrace();
        }
    }

    public void getNextQuestion() {
        try {
            HashMap<String, String> hashQuestion1 = new HashMap<String, String>();
            if (counter == 1) {
                edtAnswer.setText("");
                hashQuestion1 = ActivityStringInfo.ForgotPasswordQuestionList.get(counter);
                txt_Question.setText(hashQuestion1.get(DatabaseConstant.key_SEC_QUES_1).toString());
            }
            if (counter == 2) {
                edtAnswer.setText("");
                hashQuestion1 = ActivityStringInfo.ForgotPasswordQuestionList.get(counter);
                txt_Question.setText(hashQuestion1.get(DatabaseConstant.key_SEC_QUES_1).toString());
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForgotPasswordActivity.this, e);
            e.printStackTrace();
        }
    }

    public boolean validation() {
        boolean bln = true;
        try {
            if (edtUserId.getText().toString().equals("")) {
                bln = false;
                showToast(MessageInfo.strEnterUserId, Toast.LENGTH_SHORT);
            } else if (edtCompanyName.getText().toString().equals("")) {
                bln = false;
                showToast(MessageInfo.strEnterCompany, Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForgotPasswordActivity.this, e);
            e.printStackTrace();
        }
        return bln;
    }


    private class SelectDataTaskForForgotQuestion extends AsyncTask<String, Void, String> {
        TransparentProgressDialog pDialog;
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {

            pDialog = new TransparentProgressDialog(mContext);
            pDialog.setMessage(MessageInfo.registrationProgress_txt);
            pDialog.setCancelable(false);
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                ServicesHelper servicesHelper = new ServicesHelper();
                strMsg = servicesHelper.getForgotPasswordQuestionList(mContext, args[0], args[1]);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ForgotPasswordActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (strMsg.equals("True")) {
                if (ActivityStringInfo.ForgotPasswordQuestionList.size() <= 0) {
                    questionLayout.setVisibility(View.GONE);
                    txt_Question.setText("");
                    edtAnswer.setText("");

                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText("Security questions unavailable. Cannot proceed.");
                } else {
                    questionLayout.setVisibility(View.VISIBLE);
                    HashMap<String, String> hashQuestion1 = ActivityStringInfo.ForgotPasswordQuestionList.get(counter);
                    txt_Question.setText(hashQuestion1.get(DatabaseConstant.key_SEC_QUES_1).toString());
                    edtAnswer.setText("");

                    txtError.setVisibility(View.GONE);
                    txtError.setText("");
                }
            } else if (strMsg.equals("False")) {
                questionLayout.setVisibility(View.GONE);
                txt_Question.setText("");
                edtAnswer.setText("");
                txtError.setVisibility(View.VISIBLE);
                txtError.setText("The information you have entered does not match our records. Please contact your supervisor or System Administrator.");
            } else {
                if (Utility.Connectivity_Internet(mContext)) {
                    showToast(strMsg, Toast.LENGTH_SHORT);
                } else {
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            }
        }
    }

    private class SelectDataTaskForGetResetPassword extends AsyncTask<String, Void, String> {

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                ServicesHelper servicesHelper = new ServicesHelper();
                return servicesHelper.getResetPassword(mContext, args[0]);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ForgotPasswordActivity.this, e);
                e.printStackTrace();
                return "";
            }
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String strMsg) {
            hideTransparentProgressDialog();
            if (strMsg.equals("True")) {
                Intent forgotPwdConfirmationIntent = new Intent(getApplicationContext(), ForgotPasswordConfirmationActivity.class);
                startActivity(forgotPwdConfirmationIntent);
                finish();
                ForgotPasswordActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            } else {
                if (Utility.Connectivity_Internet(mContext)) {
                    if (!strMsg.equals(""))
                        showToast(strMsg, Toast.LENGTH_SHORT);
                } else {
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
