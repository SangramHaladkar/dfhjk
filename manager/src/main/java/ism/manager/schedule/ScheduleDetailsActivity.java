package ism.manager.schedule;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import ism.manager.ActivityStringInfo;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;

public class ScheduleDetailsActivity extends AppBaseActivity {

    private Fragment scheduleDetailViewFragment;
    ActivityStringInfo stringInfo;
    //Variable Declaration
    public static final String INFO = "INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_details);
        Intent i = getIntent();
        stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }

        scheduleDetailViewFragment = null;
        if (stringInfo.strScheduleType != null && stringInfo.strScheduleType.length() > 0) {
            if (stringInfo.strScheduleType.equals("M")) {
                try {
                    getSupportActionBar().setTitle("Meeting");
                    scheduleDetailViewFragment = new ScheduleMeetingDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    scheduleDetailViewFragment.setArguments(bundle);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ScheduleDetailsActivity.this, e);
                    e.printStackTrace();
                }
            } else if (stringInfo.strScheduleType.equals("S")) {
                try {
                    if (stringInfo.strCommentForWhich.equals("T")) {
                        //TODO pending works for ShiftTrade With
                    } else {
                        scheduleDetailViewFragment = new ShiftDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(INFO, stringInfo);
                        scheduleDetailViewFragment.setArguments(bundle);
                        ScheduleDetailsActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ScheduleDetailsActivity.this, e);
                    e.printStackTrace();
                }
            } else if (stringInfo.strScheduleType.equals("R")) {
                try {
                    getSupportActionBar().setTitle("Request Off");
                    scheduleDetailViewFragment = new RequestOffDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INFO, stringInfo);
                    scheduleDetailViewFragment.setArguments(bundle);
                    ScheduleDetailsActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ScheduleDetailsActivity.this, e);
                    e.printStackTrace();
                }
            }
        }

        if (scheduleDetailViewFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.schdl_container, this.scheduleDetailViewFragment).commit();
        } else {
            showToast("Not Available..", Toast.LENGTH_SHORT);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public void back() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityStringInfo.selectedListItemIndex = 0;
        ActivityStringInfo.isListItemSelected = false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        finish();

//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            finish();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//
//        }
    }
}
