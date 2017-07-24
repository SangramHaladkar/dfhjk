package ism.android;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ism.android.baseclasses.AppBaseActivity;
import ism.android.document.DocumentFragment;
import ism.android.login.LoginActivity;
import ism.android.message.MessagesFragment;
import ism.android.schedule.ScheduleFragment;
import ism.android.settings.SettingsFragment;
import ism.android.utils.AlertDialogManager;
import ism.android.utils.MessageInfo;
import ism.android.utils.StaticVariables;
import ism.android.webservices.SyncServiceHelper;

public class MainActivity extends AppBaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView txt_MainBanner;
    private Fragment fragment;
    ActivityStringInfo stringInfo;
    LinearLayout lyl_MainIdeaPopup, lyl_MsgIdeaPopup;
    Button btn_cancel, btn_Submit, btn_Ok;
    EditText ed_Subject, ed_Message;
    TextView txtIdeaSuccessMessage;
    private String fragmentTag;
    private BroadcastReceiver mReceiver;
    Context mContext;

    TextView tabMessageText, tabScheduleText, tabDocumentText, tabLogText, tabDashboardText;
    ImageView tabMessageIcon, tabScheduleIcon, tabDocumentIcon, tabLogIcon, tabDashboardIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar();
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }
        mContext = this;
        LocalBroadcastManager.getInstance(this).registerReceiver(pushNotificationMessageReceiver, new IntentFilter("pushNotification"));
        setBanner();
        setupBottomTabs();

        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.v("onCreate", "maxMemory:" + Long.toString(maxMemory));

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        Log.v("onCreate", "memoryClass:" + Integer.toString(memoryClass));

    }

    //    private BroadcastReceiver pushNotificationMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //TODO
//            if(getAlertDialogManager().getAlertDialog().isShowing()){
//                getAlertDialogManager().getAlertDialog().dismiss();
//            }
//            String intentMsg = intent.getStringExtra("msg");
//            getAlertDialogManager().showAlertDialog(getResources().getString(R.string.app_name),intentMsg,true);
//        }
//    };

    @Override
    protected void onResume() {
        super.onResume();


        LocalBroadcastManager.getInstance(this).registerReceiver(appRefreshReceiver, new IntentFilter("appRefreshed"));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        // If the Receiver is nor created, create one
        if (mReceiver == null) {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // extract our message from intent
                    ActivityStringInfo.shouldRefreshed = true;
                    System.out.println("MainActivity BroadcastReceiver-->onReceive()=====>");
                }
            };
            this.registerReceiver(mReceiver, intentFilter);
            SharedPreferences preferences = this.getSharedPreferences(this.getString(R.string.PREFS_NAME), this.MODE_PRIVATE);
            String regGUID = preferences.getString(this.getString(R.string.REGISTERED_GUID), null);
            int orgID = preferences.getInt(this.getString(R.string.ORG_ID), 0);
            String serviceLocation = preferences.getString(this.getString(R.string.WEB_SERVICE_LOCATION), null);
            boolean appRunning = preferences.getBoolean(this.getString(R.string.APP_RUNNING), true);
            if (null == regGUID && orgID == 0 && serviceLocation == null) {
                Log.i("App exiting", " about to finish");
                Intent i = new Intent(MainActivity.this, SplashActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                this.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu != null) {
            if (ActivityStringInfo.strSuggestright != null) {
                if (ActivityStringInfo.strSuggestright.contains("N")) {
                    MenuItem menuIdeaItem = menu.findItem(R.id.action_idea).setVisible(false);
                    if (menuIdeaItem != null) {
                        menuIdeaItem.setVisible(false);
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pushNotificationMessageReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(pushNotificationMessageReceiver);

        if (mReceiver != null) {
            this.unregisterReceiver(this.mReceiver);
            System.out.println("MessageList onDestroy()2===>");
        }

        if (appRefreshReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(appRefreshReceiver);
        }

        hideTransparentProgressDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            doLogOut();
            return true;
        } else if (id == R.id.action_idea) {
            showIdeaPopUp();
            return true;
        } else if (id == R.id.action_settings) {
            showSettings();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void setBanner() {
        txt_MainBanner = (TextView) findViewById(R.id.txt_MainBanner);
        if (StaticVariables.getNEWS_BANNER(MainActivity.this) != null && StaticVariables.getNEWS_BANNER(MainActivity.this).length() > 0) {
            txt_MainBanner.setText(StaticVariables.getNEWS_BANNER(MainActivity.this));
        } else {
            txt_MainBanner.setText("");
        }
    }

    private BroadcastReceiver appRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setBanner();
        }
    };


    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setElevation(0);
    }

    private void setupBottomTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_messages)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_schedules)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_docs)));
//        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_logs)));
//        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_dashboard)));
//        tabLayout.addTab(tabLayout.newTab().setText("Idea"));
//        tabLayout.addTab(tabLayout.newTab().setText("Settings"));

        LinearLayout tabMessage = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabMessageText = (TextView) tabMessage.findViewById(R.id.tabText);
        tabMessageIcon = (ImageView) tabMessage.findViewById(R.id.tabIcon);
        tabMessageIcon.setImageResource(R.drawable.tabmessageicon_unselected);
        tabMessageText.setText(getResources().getString(R.string.tab_messages));
        tabLayout.getTabAt(0).setCustomView(tabMessage);

        LinearLayout tabSchedule = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabScheduleText = (TextView) tabSchedule.findViewById(R.id.tabText);
        tabScheduleIcon = (ImageView) tabSchedule.findViewById(R.id.tabIcon);
        tabScheduleIcon.setImageResource(R.drawable.tabschdlicon_unselected);
        tabScheduleText.setText(getResources().getString(R.string.tab_schedules));
        tabLayout.getTabAt(1).setCustomView(tabSchedule);

        LinearLayout tabDocument = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabDocumentText = (TextView) tabDocument.findViewById(R.id.tabText);
        tabDocumentIcon = (ImageView) tabDocument.findViewById(R.id.tabIcon);
        tabDocumentIcon.setImageResource(R.drawable.tabdocuments_unselected);
        tabDocumentText.setText(getResources().getString(R.string.tab_docs));
        tabLayout.getTabAt(2).setCustomView(tabDocument);


        this.fragment = null;
        if (ActivityStringInfo.selectedTabIndex == 3) {
            showSettings();
        } else {
//            ActivityStringInfo.selectedListItemIndex = 0;
            tabLayout.getTabAt(ActivityStringInfo.selectedTabIndex).select();
            switch (tabLayout.getSelectedTabPosition()) {
                case 0:
                    tabMessageIcon.setImageResource(R.drawable.tabmessageicon_selected);
                    tabMessageText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                    getSupportActionBar().setTitle(tabMessageText.getText());
                    getSupportActionBar().setTitle(tabMessageText.getText());
                    fragment = new MessagesFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MessagesFragment.INFO, stringInfo);
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    tabScheduleIcon.setImageResource(R.drawable.tabschdlicon_selected);
                    tabScheduleText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                    getSupportActionBar().setTitle(tabScheduleText.getText());
                    fragment = new ScheduleFragment();
                    Bundle bundleSchdl = new Bundle();
                    bundleSchdl.putSerializable(ScheduleFragment.INFO, stringInfo);
                    fragment.setArguments(bundleSchdl);
                    break;
                case 2:
                    tabDocumentIcon.setImageResource(R.drawable.tabdocuments_selected);
                    tabDocumentText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                    getSupportActionBar().setTitle(tabDocumentText.getText());
                    fragment = new DocumentFragment();
                    Bundle bundleDocs = new Bundle();
                    bundleDocs.putSerializable(DocumentFragment.INFO, stringInfo);
                    fragment.setArguments(bundleDocs);
                    break;

            }
        }

        if (this.fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, this.fragment).commit();
        }


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabSelected();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        tabMessageIcon.setImageResource(R.drawable.tabmessageicon_unselected);
                        tabMessageText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));
                        break;
                    case 1:
                        tabScheduleIcon.setImageResource(R.drawable.tabschdlicon_unselected);
                        tabScheduleText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));
                        break;
                    case 2:
                        tabDocumentIcon.setImageResource(R.drawable.tabdocuments_unselected);
                        tabDocumentText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));
                        break;

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int selectedPos = tabLayout.getSelectedTabPosition();
                if (ActivityStringInfo.selectedTabIndex != selectedPos) {
                    tabSelected();
                }
            }
        });

    }

    private void tabSelected() {
        ActivityStringInfo.selectedListItemIndex = 0;
        ActivityStringInfo.isListItemSelected = false;
        ActivityStringInfo.selectedTabIndex = tabLayout.getSelectedTabPosition();
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                tabMessageIcon.setImageResource(R.drawable.tabmessageicon_selected);
                tabMessageText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                getSupportActionBar().setTitle(tabMessageText.getText());
                fragment = new MessagesFragment();
                Bundle bundleMsg = new Bundle();
                bundleMsg.putSerializable(MessagesFragment.INFO, stringInfo);
                fragment.setArguments(bundleMsg);
                break;
            case 1:
                tabScheduleIcon.setImageResource(R.drawable.tabschdlicon_selected);
                tabScheduleText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                getSupportActionBar().setTitle(tabScheduleText.getText());
                fragment = new ScheduleFragment();
                Bundle bundleSchdl = new Bundle();
                bundleSchdl.putSerializable(ScheduleFragment.INFO, stringInfo);
                fragment.setArguments(bundleSchdl);
                break;
            case 2:
                tabDocumentIcon.setImageResource(R.drawable.tabdocuments_selected);
                tabDocumentText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlack));
                getSupportActionBar().setTitle(tabDocumentText.getText());
                fragment = new DocumentFragment();
                Bundle bundleDocs = new Bundle();
                bundleDocs.putSerializable(DocumentFragment.INFO, stringInfo);
                fragment.setArguments(bundleDocs);
                break;

        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (tabLayout != null) {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (tabLayout != null) {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
        }

//        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
//        int orientation = display.getRotation();
//        if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
//            // TODO: add logic for landscape mode here
//            if (tabLayout != null) {
//                tabLayout.setTabMode(TabLayout.MODE_FIXED);
//            }
//        } else {
//            if (tabLayout != null) {
//                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//            }
//        }
    }


    public void doLogOut() {

        getAlertDialogManager().showAlertDialog(MessageInfo.company_name, MessageInfo.calenderEventafterlogout, MessageInfo.ok, null, new AlertDialogManager.OnCustomDialogClicklistenr() {
            @Override
            public void onPositiveClick() {
                new SelectDataTaskForLogout().execute();
            }

            @Override
            public void onNegativeClick() {

            }
        });

    }

    private class SelectDataTaskForLogout extends AsyncTask<String, Void, String> {

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
                ActivityStringInfo.isLoggingOut = true;
            } catch (Exception e) {
                Utility.saveExceptionDetails(MainActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                Utility.ListCalendarEntryDeleteByID(MainActivity.this);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MainActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            ActivityStringInfo.selectedTabIndex = 0;
            ActivityStringInfo.isListItemSelected = false;
            ActivityStringInfo.isLoggingOut = false;
            Intent loginIntent = new Intent();
            loginIntent.setClass(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            MainActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            finish();
        }
    }

    public void messageListRefreshed() {
        ActivityStringInfo.selectedListItemIndex = 0;
        ActivityStringInfo.isListItemSelected = false;
        fragment = null;
        fragment = new MessagesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MessagesFragment.INFO, stringInfo);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
    }

    public void scheduleListRefreshed() {
        ActivityStringInfo.selectedListItemIndex = 0;
        ActivityStringInfo.isListItemSelected = false;
        fragment = null;
//        fragment = new ScheduleFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(ScheduleFragment.INFO, stringInfo);
//        fragment.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
    }

    private void showSettings() {
        getSupportActionBar().setTitle(getResources().getString(R.string.action_settings));
        fragment = new SettingsFragment();
        Bundle bundleMsg = new Bundle();
        bundleMsg.putSerializable(SettingsFragment.INFO, stringInfo);
        fragment.setArguments(bundleMsg);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
        }

        ActivityStringInfo.selectedTabIndex = 3;

        tabLayout.setSelected(false);

        tabMessageIcon.setImageResource(R.drawable.tabmessageicon_unselected);
        tabMessageText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));

        tabScheduleIcon.setImageResource(R.drawable.tabschdlicon_unselected);
        tabScheduleText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));

        tabDocumentIcon.setImageResource(R.drawable.tabdocuments_unselected);
        tabDocumentText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));


    }

    private void showIdeaPopUp() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View myDialog = inflater.inflate(R.layout.new_idea, null);
            alertDialogBuilder.setView(myDialog);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
//            final Dialog myDialog = new Dialog(mContext, R.style.FullHeightDialog);
//            myDialog.setContentView(R.layout.new_idea);
//            myDialog.show();

            txtIdeaSuccessMessage = (TextView) myDialog.findViewById(R.id.txtMessage);

            lyl_MainIdeaPopup = (LinearLayout) myDialog.findViewById(R.id.lylMainPopup);
            lyl_MsgIdeaPopup = (LinearLayout) myDialog.findViewById(R.id.lylMsgPopup);

            ed_Subject = (EditText) myDialog.findViewById(R.id.edSubject);
            ed_Message = (EditText) myDialog.findViewById(R.id.edMessage);
            btn_cancel = (Button) myDialog.findViewById(R.id.btnCancel);
            btn_Submit = (Button) myDialog.findViewById(R.id.btnSubmit);
            btn_Ok = (Button) myDialog.findViewById(R.id.btnOk);
            btn_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btn_Ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btn_Submit
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                if (validation()) {
                                    new SelectDataTaskForSendNewIdea().execute(ed_Subject.getText().toString(), ed_Message.getText().toString());
                                }
                            } catch (Exception e) {
                                Utility.saveExceptionDetails(
                                        MainActivity.this, e);
                                e.printStackTrace();
                            }
                        }
                    });

        } catch (Exception e) {
            Utility.saveExceptionDetails(MainActivity.this, e);
            e.printStackTrace();
        }
    }

    public boolean validation() {
        boolean bln = true;
        try {
            if (ed_Subject.getText().toString().equals("")) {
                showToast(MessageInfo.strEnterSubject, Toast.LENGTH_SHORT);
                ed_Subject.requestFocus();
                bln = false;
            } else if (ed_Message.getText().toString().equals("")) {
                showToast(MessageInfo.strEnterMessage, Toast.LENGTH_SHORT);
                ed_Message.requestFocus();
                bln = false;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return bln;
    }

    private class SelectDataTaskForSendNewIdea extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MainActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                SyncServiceHelper syc = new SyncServiceHelper();
                strMsg = syc.sendNewIdea(MainActivity.this, args[0], args[1]);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MainActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.toLowerCase().equals("true")) {
                    lyl_MainIdeaPopup.setVisibility(View.GONE);
                    lyl_MsgIdeaPopup.setVisibility(View.VISIBLE);
                    txtIdeaSuccessMessage.setText(MessageInfo.strIdeaSendSuccessfully);
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strIdeaSendErrorMsg, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MainActivity.this, e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            List<Fragment> fragments = fm.getFragments();
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (fragment != null) {
                    // if you want to check for specific fragment class
                    if (fragment instanceof MessagesFragment && fragment.isVisible()) {
                        super.onBackPressed();
                        break;
                    } else {
                        tabLayout.getTabAt(0).select();
                        break;
                    }
                }
            }
        }
    }
}
