package ism.manager.settings;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import ism.manager.ActivityStringInfo;
import ism.manager.MainActivity;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.MessageInfo;
import ism.manager.webservices.ServicesHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends AppBaseFragment {

    Button btn_Cancel, btn_Save;
    EditText ed_Email, ed_MobilePhone;
    TextView txt_MainBanner;
    Spinner sp_SMSAddress;
    SwitchCompat switchNotifyBySMS, switchNotifyByEMail;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;

    ArrayList<String> lstSMSAddress = new ArrayList<String>();
    ArrayAdapter<String> adepterSMSAddress;

    public ContactFragment() {
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
        mContext = ContactFragment.this.getActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        try {
            this.initViews(view);
            this.setListeners();
            this.setValues();
        } catch (Exception e) {
            Utility.saveExceptionDetails(ContactFragment.this.getActivity(), e);
        }
        return view;
    }

    private void initViews(View v) {

        btn_Save = (Button) v.findViewById(R.id.btnSave);
        btn_Cancel = (Button) v.findViewById(R.id.btnCancel);

        ed_Email = (EditText) v.findViewById(R.id.edEmail);
        ed_MobilePhone = (EditText) v.findViewById(R.id.edMobilePhone);

        sp_SMSAddress = (Spinner) v.findViewById(R.id.spSMSAddress);

        switchNotifyByEMail = (SwitchCompat) v.findViewById(R.id.switchNotifyByEMail);
        switchNotifyBySMS = (SwitchCompat) v.findViewById(R.id.switchNotifyBySMS);
    }

    private void setListeners() {
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    new SelectDataTaskForSetUpdateUserInfo().execute();
                }
            }
        });
    }


    private void setValues() {
        lstSMSAddress.add("");
        lstSMSAddress.add("AT&T @txt.att.net");
        lstSMSAddress.add("T-Mobile @tmomail.net");
        lstSMSAddress.add("Verizon @vtext.com");
        lstSMSAddress.add("Alltel @message.alltel.com");
        lstSMSAddress.add("Boost Mobile @myboostmobile.com");
        lstSMSAddress.add("Nextel @messaging.nextel.com");
        lstSMSAddress.add("Sprint PCS @messaging.sprintpcs.com");
        lstSMSAddress.add("US Cellular @email.uscc.net");
        lstSMSAddress.add("Virgin Mobile USA @vmobl.com");
        lstSMSAddress.add("MetroPCS @mymetropcs.com");

        adepterSMSAddress = new ArrayAdapter<String>(mContext, R.layout.spinner_selected_listitem, lstSMSAddress);
        adepterSMSAddress.setDropDownViewResource(R.layout.multiline_spinner_dropdown_listitem);
        sp_SMSAddress.setAdapter(adepterSMSAddress);
        sp_SMSAddress.setPrompt("SMS Address");

        this.getContactInformation();
    }

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public boolean validation() {
        boolean bln = true;
        boolean emailCheck = EMAIL_ADDRESS_PATTERN.matcher(ed_Email.getText().toString()).matches();
        try {
            if (ed_Email.getText().toString().equals("") && switchNotifyByEMail.isChecked()) {
                bln = false;
                showToast("Please enter email.", Toast.LENGTH_SHORT);
            } else if (emailCheck == false && !ed_Email.getText().toString().equals("")) {
                bln = false;
                showToast("Please enter valid email id.", Toast.LENGTH_SHORT);
            } else if (switchNotifyBySMS.isChecked()) {
                if (ed_MobilePhone.getText().toString().equals("")) {
                    bln = false;
                    showToast("Please enter mobile number.", Toast.LENGTH_SHORT);
                } else if (ed_MobilePhone.getText().toString().length() != 10) {
                    bln = false;
                    showToast("Please enter valid mobile number.", Toast.LENGTH_SHORT);
                } else if (sp_SMSAddress.getSelectedItem().toString().equals("")) {
                    bln = false;
                    showToast("Please select sms address.", Toast.LENGTH_SHORT);
                }
            } else if (!ed_MobilePhone.getText().toString().equals("")) {
                if (ed_MobilePhone.getText().toString().length() != 10) {
                    bln = false;
                    showToast("Please enter valid mobile number.", Toast.LENGTH_SHORT);
                }
            } else if (!sp_SMSAddress.getSelectedItem().toString().equals("") && ed_MobilePhone.getText().toString().equals("")) {
                bln = false;
                showToast("Please enter mobile number.", Toast.LENGTH_SHORT);
            }

        } catch (Exception e) {
            Utility.saveExceptionDetails(ContactFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return bln;
    }

    public void getContactInformation() {
        try {
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getUserRecord();
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    ed_Email.setText(c.getString(17));

                    String strMobNum[] = c.getString(19).split("@");

                    if (c.getString(18).toLowerCase().equals("true"))
                        switchNotifyByEMail.setChecked(true);

                    ed_MobilePhone.setText(strMobNum[0]);

                    if (c.getString(20).toLowerCase().equals("true"))
                        switchNotifyBySMS.setChecked(true);

                    String strSMSAddress = "";
                    if (c.getString(19).contains("@txt.att.net"))
                        strSMSAddress = "AT&T @txt.att.net";
                    if (c.getString(19).contains("@tmomail.net"))
                        strSMSAddress = "T-Mobile @tmomail.net";
                    if (c.getString(19).contains("@vtext.com"))
                        strSMSAddress = "Verizon @vtext.com";
                    if (c.getString(19).contains("@message.alltel.com"))
                        strSMSAddress = "Alltel @message.alltel.com";
                    if (c.getString(19).contains("@myboostmobile.com"))
                        strSMSAddress = "Boost Mobile @myboostmobile.com";
                    if (c.getString(19).contains("@messaging.nextel.com"))
                        strSMSAddress = "Nextel @messaging.nextel.com";
                    if (c.getString(19).contains("@messaging.sprintpcs.com"))
                        strSMSAddress = "Sprint PCS @messaging.sprintpcs.com";
                    if (c.getString(19).contains("@email.uscc.net"))
                        strSMSAddress = "US Cellular @email.uscc.net";
                    if (c.getString(19).contains("@vmobl.com"))
                        strSMSAddress = "Virgin Mobile USA @vmobl.com";
                    if (c.getString(19).contains("@mymetropcs.com"))
                        strSMSAddress = "MetroPCS @mymetropcs.com";

                    sp_SMSAddress.setSelection(adepterSMSAddress.getPosition(strSMSAddress));
                }
                c.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ContactFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private class SelectDataTaskForSetUpdateUserInfo extends AsyncTask<String, Void, String> {
        String strMsg = "";
        String strMobileNum = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ContactFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                strMsg = updateUserInfo();
//                if (!sp_SMSAddress.getSelectedItem().toString().equals(""))
//                    strMobileNum = ed_MobilePhone.getText().toString() + sp_SMSAddress.getSelectedItem().toString().substring(sp_SMSAddress.getSelectedItem().toString().indexOf("@"));
//                else
//                    strMobileNum = "";
//
//                System.out.println("sms address=====" + strMobileNum);
//                ServicesHelper servicesHelper = new ServicesHelper();
//                strMsg = servicesHelper.setUpdateUserInfo(mContext, ed_Email.getText().toString(), "" + chk_NotifyByEmail.isChecked(), strMobileNum, ed_MobilePhone.getText().toString(), "" + chk_NotifyBySMS.isChecked());
            } catch (Exception e) {
                Utility.saveExceptionDetails(ContactFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true")) {
                if (strMobileNum.equals(""))
                    strMobileNum = ed_MobilePhone.getText().toString();
                MyDatabaseInstanceHolder.getDatabaseHelper().updateContactInfo(ed_Email.getText().toString(), "" + switchNotifyByEMail.isChecked(), strMobileNum, "" + switchNotifyBySMS.isChecked());
                showToast(MessageInfo.strSaveSuccess, Toast.LENGTH_SHORT);
//                Intent intent2 = new Intent(getApplicationContext(), MessageList.class);
//                intent2.putExtra(INFO, stringInfo);
//                startActivity(intent2);
//                finish();
//                Contact.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
//                ((MainActivity)ContactFragment.this.getActivity()).
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

        String updateUserInfo() {
            String stm = "";
            if (!sp_SMSAddress.getSelectedItem().toString().equals(""))
                strMobileNum = ed_MobilePhone.getText().toString() + sp_SMSAddress.getSelectedItem().toString().substring(sp_SMSAddress.getSelectedItem().toString().indexOf("@"));
            else
                strMobileNum = "";

            try {
                System.out.println("sms address=====" + strMobileNum);
                ServicesHelper servicesHelper = new ServicesHelper();
                stm = servicesHelper.setUpdateUserInfo(mContext, ed_Email.getText().toString(), "" + switchNotifyByEMail.isChecked(), strMobileNum, ed_MobilePhone.getText().toString(), "" + switchNotifyBySMS.isChecked());
            } catch (Exception e) {
                Utility.saveExceptionDetails(ContactFragment.this.getActivity(), e);
                stm = "";
            }
            return stm;
        }
    }


}
