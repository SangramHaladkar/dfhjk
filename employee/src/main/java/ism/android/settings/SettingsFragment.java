package ism.android.settings;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ism.android.ActivityStringInfo;
import ism.android.MainActivity;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.MessageInfo;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends AppBaseFragment {

    private TextView txtLoginName;
    private TextView txtOrganization;
    private Button btnLogOut;

    private RelativeLayout contactLayout;
    private RelativeLayout securityLayout;
    private RelativeLayout pwdLayout;
    private RelativeLayout loginDetailLayout;
    private RelativeLayout appNameLayout;

    private FragmentManager settingsFragmentManager;

    // Variable Declaration
    private ActivityStringInfo stringInfo;

    public static final String INFO = "INFO";

    private Context mContext;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = SettingsFragment.this.getActivity();
        if (getArguments().containsKey(INFO)) {
            stringInfo = (ActivityStringInfo) getArguments().getSerializable(INFO);
        }
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        if (savedInstanceState == null) {

            try {
                if (view.findViewById(R.id.settings_item_detail_container) != null) {
                    ActivityStringInfo.mTwoPane = true;
                } else {
                    ActivityStringInfo.mTwoPane = false;
                }

                this.initViews(view);
                this.setListeners();
                showSettingsDetails(SettingsFragment.this.getActivity(), "Login");
            } catch (Exception e) {
                Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ActivityStringInfo.isLoggingOut){
            showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideTransparentProgressDialog();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_messages_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            getSettingDataRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(View v) {
        if (v.findViewById(R.id.txtLoginName) != null && v.findViewById(R.id.txtOrganization) != null && v.findViewById(R.id.btnLogOut) != null) {
            txtLoginName = (TextView) v.findViewById(R.id.txtLoginName);
            txtOrganization = (TextView) v.findViewById(R.id.txtOrganization);
            btnLogOut = (Button) v.findViewById(R.id.btnLogOut);
            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) SettingsFragment.this.getActivity()).doLogOut();
                }
            });
            setLoginDetails();
        }

        contactLayout = (RelativeLayout) v.findViewById(R.id.contactLayout);
        securityLayout = (RelativeLayout) v.findViewById(R.id.securityLayout);
        pwdLayout = (RelativeLayout) v.findViewById(R.id.pwdLayout);
        loginDetailLayout = (RelativeLayout) v.findViewById(R.id.loginDetailLayout);
        appNameLayout = (RelativeLayout) v.findViewById(R.id.appNameLayout);


    }

    private void setListeners() {

        loginDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDetails(v.getContext(), "Login");
            }
        });

        contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDetails(v.getContext(), "Contact");

            }
        });

        securityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDetails(v.getContext(), "Security");

            }
        });

        pwdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDetails(v.getContext(), "Password");

            }
        });

        appNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDetails(v.getContext(), "About");
            }
        });
    }

    private void setLoginDetails() {
        Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getUserRecord();
        try {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (txtLoginName != null)
                        txtLoginName.setText(c.getString(5));

                    if (txtOrganization != null)
                        txtOrganization.setText(c.getString(8));
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            c.close();
        }
    }

    private void showSettingsDetails(Context sContext, String myInfoType) {
        stringInfo.strMyInfoType = myInfoType;
        if (ActivityStringInfo.mTwoPane) {
            Fragment settingsDetailFragment = null;
            if (myInfoType.equals("Contact")) {
                try {
                    settingsDetailFragment = new ContactFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            } else if (myInfoType.equals("Security")) {
                try {
                    settingsDetailFragment = new SecurityFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            } else if (myInfoType.equals("Password")) {
                try {
                    settingsDetailFragment = new PasswordChangeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            } else if (myInfoType.equals("Login")) {
                try {
                    settingsDetailFragment = new LoginDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }else if (myInfoType.equals("About")) {
                try {
                    settingsDetailFragment = new AboutApp_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
            if (settingsDetailFragment != null) {
                settingsFragmentManager = getFragmentManager();
                settingsFragmentManager.beginTransaction().replace(R.id.settings_item_detail_container, settingsDetailFragment).commit();
            } else {
                showToast("Not Available..", Toast.LENGTH_SHORT);
            }
        } else {
            if (myInfoType.equals("Login")) {

            } else {
                Intent settingsDetails = new Intent(sContext, SettingsDetailsActivity.class);
                settingsDetails.putExtra(INFO, stringInfo);
                sContext.startActivity(settingsDetails);
            }
        }

    }

    private void getSettingDataRefresh() {
        if (Utility.getMandatoryMessage(mContext) != null) {
            Intent intent2 = Utility.getMandatoryMessage(mContext);
            intent2.putExtra(INFO, stringInfo);
            startActivity(intent2);
            SettingsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            ActivityStringInfo.alertopen = true;
        } else {
            new SelectDataTaskForRefresh().execute();
        }
    }

    private class SelectDataTaskForRefresh extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strRefresh);
            } catch (Exception e) {
                Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                Synchronization syc = new Synchronization(mContext);
                strMsg = syc.getInformation(SettingsFragment.this.getActivity());
            } catch (Exception e) {
                Utility.saveExceptionDetails(SettingsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true"))
            {
                setLoginDetails();
                showSettingsDetails(SettingsFragment.this.getActivity(), "Login");
            } else if (strMsg != "true") {
                if (Utility.Connectivity_Internet(mContext)) {
                    if (!strMsg.equals(""))
                        showToast(strMsg, Toast.LENGTH_SHORT);
                } else {
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            }
        }
    }

}
