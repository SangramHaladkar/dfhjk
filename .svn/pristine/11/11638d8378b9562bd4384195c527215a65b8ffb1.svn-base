package ism.manager.log;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import ism.manager.ActivityStringInfo;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;

public class LogDetailsActivity extends AppBaseActivity {
    private LogDetailsFragment logDetailViewFragment;
    ActivityStringInfo stringInfo;
    //Variable Declaration
    public static final String INFO = "INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_log_details);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }

            logDetailViewFragment = null;
            logDetailViewFragment = new LogDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isDisable", false);
            bundle.putSerializable(INFO, stringInfo);
            logDetailViewFragment.setArguments(bundle);
            if (logDetailViewFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.log_container, this.logDetailViewFragment).commit();
            } else {
                showToast("Not Available..", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsActivity.this, e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!ActivityStringInfo.isCreateNewLog) {
            if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                menu.findItem(R.id.action_save).setVisible(false);
                menu.findItem(R.id.action_complete).setVisible(false);
            } else {
                if (Utility.compareLastSyncForDisable(this)) {
                    menu.findItem(R.id.action_save).setVisible(false);
                    menu.findItem(R.id.action_complete).setVisible(false);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            finish();
            return true;
        } else if (id == R.id.action_save) {
            if (logDetailViewFragment != null)
                logDetailViewFragment.saveLog();

            return true;
        } else if (id == R.id.action_complete) {
            if (logDetailViewFragment != null)
                logDetailViewFragment.completeLog();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
