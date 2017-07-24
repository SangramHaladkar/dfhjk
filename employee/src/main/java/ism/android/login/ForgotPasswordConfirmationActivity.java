package ism.android.login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ism.android.ActivityStringInfo;
import ism.android.R;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseActivity;
import ism.android.utils.AlertDialogManager;
import ism.android.utils.MessageInfo;


public class ForgotPasswordConfirmationActivity extends AppBaseActivity {

    TextView btnLogin;
    TextView txtInfoTitle;
    TextView txtInformation;
    TextView txtEmailId;

    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_confirmation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            this.initialize();
            this.setValues();
            this.setListeners();

        } catch (Exception e) {
            Utility.saveExceptionDetails(ForgotPasswordConfirmationActivity.this, e);
            e.printStackTrace();
        }
    }

    private void initialize(){
        mContext = this;
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        txtInfoTitle = (TextView) findViewById(R.id.txtInfoTitle);
        txtInformation = (TextView) findViewById(R.id.txtInformation);
        txtEmailId = (TextView) findViewById(R.id.txtEmailId);
    }

    private void setValues(){
        txtEmailId.setText(ActivityStringInfo.strResetPasswordEmail);
        if(!ActivityStringInfo.strResetPasswordEmail.equals(""))
        {
            txtInformation.setText("A new temporary password has been sent to your email at :");
        }
        else
        {
            txtInformation.setText("A new temporary password has been generated.  Please contact your System Administrator for assistance.");
        }
    }

    private void setListeners(){
       btnLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               calendarEvent();
           }
       });
    }

    public void calendarEvent()
    {
        getAlertDialogManager().showAlertDialog(MessageInfo.company_name, MessageInfo.calenderEvent, MessageInfo.ok, null, new AlertDialogManager.OnCustomDialogClicklistenr() {
            @Override
            public void onPositiveClick() {
//                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(loginIntent);
                finish();
                ForgotPasswordConfirmationActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }

            @Override
            public void onNegativeClick() {

            }
        });

    }

}
