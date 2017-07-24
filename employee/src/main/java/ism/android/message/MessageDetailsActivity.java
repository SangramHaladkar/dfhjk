package ism.android.message;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import ism.android.ActivityStringInfo;
import ism.android.R;
import ism.android.baseclasses.AppBaseActivity;
import ism.android.utils.StaticVariables;


public class MessageDetailsActivity extends AppBaseActivity {

    ActivityStringInfo stringInfo;
    //Variable Declaration
    public static final String INFO = "INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        Intent i = getIntent();
        stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Fragment detailViewFragment = null;
        if (stringInfo.strMessageType != null && stringInfo.strMessageType.length() > 0) {
            if (stringInfo.strMessageType.equals(StaticVariables.BREAK_NOTIFICATION_MESSAGE)) {
                detailViewFragment = new MessageDetailsFragment();
            } else if (stringInfo.strMessageType.equals(StaticVariables.MEETING_MESSAGE)) {
                detailViewFragment = new MeetingDetailsFragment();
            } else if (stringInfo.strMessageType.equals(StaticVariables.IDEA_MESSAGE)) {
                detailViewFragment = new MessageDetailsFragment();
            } else if (stringInfo.strMessageType.equals(StaticVariables.GIVE_AWAY_MESSAGE)) {
                detailViewFragment = new GiveAwayRequestFragment();
            } else if (stringInfo.strMessageType.equals(StaticVariables.GIVE_TO_MESSAGE)) {
                detailViewFragment = new GiveToRequestFragment();
            } else if (stringInfo.strMessageType.equals(StaticVariables.TRADE_REQUEST_MESSAGE)) {
                detailViewFragment = new TradeWithRequestFragment();
            } else if (stringInfo.strMessageType.equals(StaticVariables.REQUEST_OFF_MESSAGE)) {
                detailViewFragment = new RequestOffFragment();
            } else if (stringInfo.strMessageType.equals(StaticVariables.MANDATORY_MESSAGE)) {
                detailViewFragment = new MandatoryMsgDetailFragment();
            } else if (stringInfo.strMessageType.equals("S") && (stringInfo.strMessageSubType != null && stringInfo.strMessageSubType.length() > 0)) {
                if (stringInfo.strMessageSubType.equals("GA")) {
                    detailViewFragment = new GiveAwayResponseFragment();
                } else if (stringInfo.strMessageSubType.equals("GT") && !stringInfo.strMeetingId.equals("")) {
                    detailViewFragment = new GiveToResponseFragment();
                } else if (stringInfo.strMessageSubType.equals("T") && !stringInfo.strMeetingId.equals("")) {
                    detailViewFragment = new TradeShiftResponseFragment();
                }
            } else {
                detailViewFragment = new MessageDetailsFragment();
            }
        } else {
            detailViewFragment = new MessageDetailsFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(INFO, stringInfo);
        if (detailViewFragment != null) {
            detailViewFragment.setArguments(bundle);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.msg_container, detailViewFragment).commit();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void back(){
        NavUtils.navigateUpFromSameTask(this);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

}