package ism.manager.settings;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ism.manager.ActivityStringInfo;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.MessageInfo;
import ism.manager.webservices.ServicesHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordChangeFragment extends AppBaseFragment {

    private Button btn_Cancel, btn_Save;
    private EditText ed_OldPassword, ed_NewPassword, ed_ConfirmPassword;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    private Context mContext;

    public PasswordChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments().containsKey(INFO)) {
            stringInfo = (ActivityStringInfo) getArguments().getSerializable(INFO);
        }
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }
        mContext = PasswordChangeFragment.this.getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_change, container, false);
        try {
            this.initViews(view);
            this.setListeners();
        } catch (Exception e) {
            Utility.saveExceptionDetails(PasswordChangeFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return view;
    }

    private void initViews(View v) {
        btn_Save = (Button) v.findViewById(R.id.btnSave);
        ed_OldPassword = (EditText) v.findViewById(R.id.edOldPassword);
        ed_NewPassword = (EditText) v.findViewById(R.id.edNewPassword);
        ed_ConfirmPassword = (EditText) v.findViewById(R.id.edConfirmPassword);
    }

    private void setListeners() {
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Validation()) {
                        String oldPwd = ed_OldPassword.getText().toString().trim();
                        String newPwd = ed_NewPassword.getText().toString().trim();
                        ed_OldPassword.setText("");
                        ed_NewPassword.setText("");
                        ed_ConfirmPassword.setText("");
                        new SelectDataTaskForSetChangeTempPassword().execute(oldPwd, newPwd);
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(PasswordChangeFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean Validation() {
        boolean bln = true;
        try {
            if (ed_OldPassword.getText().toString().equals("")) {
                showToast("Please enter old password.", Toast.LENGTH_SHORT);
                return false;
            } else if (ed_NewPassword.getText().toString().equals("")) {
                showToast("Please enter new password.", Toast.LENGTH_SHORT);
                return false;
            } else if (ed_ConfirmPassword.getText().toString().equals("")) {
                showToast("Please enter confirm password.", Toast.LENGTH_SHORT);
                return false;
            } else if (ed_NewPassword.length() < 5) {
                showToast("Please check the values you have entered.", Toast.LENGTH_SHORT);
                return false;
            } else if (!ed_NewPassword.getText().toString().equals(ed_ConfirmPassword.getText().toString())) {
                showToast(MessageInfo.strNewConfimPassIncorrect, Toast.LENGTH_SHORT);
                return false;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(PasswordChangeFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return bln;
    }

    private class SelectDataTaskForSetChangeTempPassword extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(PasswordChangeFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                ServicesHelper servicesHelper = new ServicesHelper();
                strMsg = servicesHelper.setChangeTempPassword(mContext, args[0], args[1]);
            } catch (Exception e) {
                Utility.saveExceptionDetails(PasswordChangeFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true")) {
                showToast("Password changed successfully.", Toast.LENGTH_SHORT);
//                Intent intent2 = new Intent(getApplicationContext(), MessageList.class);
//                intent2.putExtra(INFO, stringInfo);
//                startActivity(intent2);
//                finish();
//                Password.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            } else if (strMsg.equals("false")) {
                showToast(MessageInfo.strOldPassIncorrect, Toast.LENGTH_SHORT);
            } else {
                if (Utility.Connectivity_Internet(mContext)) {
                    if (!strMsg.equals("")) {
                        showToast(strMsg, Toast.LENGTH_SHORT);
                    }
                } else {
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            }
        }
    }

}
