package ism.manager.settings;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ism.manager.ActivityStringInfo;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;

public class SettingsDetailsActivity extends AppBaseActivity {

    private Fragment settingsDetailViewFragment;
    ActivityStringInfo stringInfo;
    //Variable Declaration
    public static final String INFO = "INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_details);
        Intent i = getIntent();
        stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }

        settingsDetailViewFragment = null;
        if (stringInfo.strMyInfoType != null && stringInfo.strMyInfoType.length() > 0) {
            if (stringInfo.strMyInfoType.equals("Contact")) {
                try {
                    getSupportActionBar().setTitle("Contact");
                    settingsDetailViewFragment = new ContactFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailViewFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsDetailsActivity.this, e);
                    e.printStackTrace();
                }
            } else if (stringInfo.strMyInfoType.equals("Security")) {
                try {
                    getSupportActionBar().setTitle("Security");
                    settingsDetailViewFragment = new SecurityFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailViewFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsDetailsActivity.this, e);
                    e.printStackTrace();
                }
            }else if (stringInfo.strMyInfoType.equals("Password")) {
                try {
                    getSupportActionBar().setTitle("Password");
                    settingsDetailViewFragment = new PasswordChangeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailViewFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsDetailsActivity.this, e);
                    e.printStackTrace();
                }
            }else if (stringInfo.strMyInfoType.equals("Password")) {
                try {
                    getSupportActionBar().setTitle("Password");
                    settingsDetailViewFragment = new PasswordChangeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    settingsDetailViewFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsDetailsActivity.this, e);
                    e.printStackTrace();
                }
            }else if (stringInfo.strMyInfoType.equals("About")) {
                try {
//                    getSupportActionBar().setTitle("About");
//                    settingsDetailViewFragment = new AboutApp_Fragment();
                    settingsDetailViewFragment = null;
                    Intent appAboutDetail = new Intent(this, AboutApp_Activity.class);
                    appAboutDetail.putExtra(INFO, stringInfo);
                    this.startActivity(appAboutDetail);
                    finish();
//
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(INFO, stringInfo);
//                    settingsDetailViewFragment.setArguments(bundle);
                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SettingsDetailsActivity.this, e);
                    e.printStackTrace();
                }
            }
        }

        if (settingsDetailViewFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, this.settingsDetailViewFragment).commit();
        } else {
//            showToast("Not Available..", Toast.LENGTH_SHORT);
        }

    }
}