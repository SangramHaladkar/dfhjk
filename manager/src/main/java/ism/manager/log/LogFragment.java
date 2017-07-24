package ism.manager.log;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;
import ism.manager.webservices.Synchronization;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends AppBaseFragment {

    private RecyclerView recyclerViewLog;
    private FloatingActionButton createNewLog;
    private FloatingActionButton editRecentLog;

    private FragmentManager fragmentManagerLog;

    private boolean isFirstSelected = true;

    // Variable Declaration
    private Context mContext;
    private ActivityStringInfo stringInfo;
    public static final String INFO = "INFO";
    //Arraylis for flags
    ArrayList<AddFlagItem> showFlagList;


    public LogFragment() {
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
        this.initFlagIconList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        try {
            mContext = LogFragment.this.getActivity();
            recyclerViewLog = (RecyclerView) view.findViewById(R.id.logItem_list);
            createNewLog = (FloatingActionButton) view.findViewById(R.id.newLog);
            createNewLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewLog();
                }
            });
            assert recyclerViewLog != null;

//            if (Utility.getMandatoryMessage(mContext) != null && ActivityStringInfo.strMandatoryMsgRight.toLowerCase().equals("y")) {
//                Intent intent2 = Utility.getMandatoryMessage(mContext);
//                intent2.putExtra(INFO, stringInfo);
//                startActivity(intent2);
//                LogFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
//                ActivityStringInfo.alertopen = true;
//            } else {
//                new GetLogRecentListDataTask().execute();
//            }

            if (view.findViewById(R.id.log_item_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                editRecentLog = (FloatingActionButton) view.findViewById(R.id.editLog);
                editRecentLog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent2 = new Intent(LogFragment.this.getActivity(), LogDetailsActivity.class);
                        intent2.putExtra(INFO, stringInfo);
                        startActivity(intent2);
                        LogFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                    }
                });

                ActivityStringInfo.mTwoPane = true;
            } else {
                ActivityStringInfo.mTwoPane = false;
            }

        } catch (Exception e) {

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityStringInfo.shouldRefreshed) {
            ActivityStringInfo.shouldRefreshed = false;
            getLogListRefresh();
        } else {
        setAllLogs();
        }
    }

    private void setAllLogs() {
        if (Utility.getMandatoryMessage(mContext) != null && ActivityStringInfo.strMandatoryMsgRight.toLowerCase().equals("y")) {
            Intent intent2 = Utility.getMandatoryMessage(mContext);
            intent2.putExtra(INFO, stringInfo);
            startActivity(intent2);
            LogFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            ActivityStringInfo.alertopen = true;
        } else {
            new GetLogRecentListDataTask().execute();
        }
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
            getLogListRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFlagIconList() {
        ActivityStringInfo.flagTextwithIcons.put("SecurityIssue", R.drawable.flag_security);
        ActivityStringInfo.flagTextwithIcons.put("EmployeeIncidentInjury", R.drawable.flag_emp_injry);
        ActivityStringInfo.flagTextwithIcons.put("GuestIncidentInjury", R.drawable.flag_guest_injury);
        ActivityStringInfo.flagTextwithIcons.put("MajorPropertyDamage", R.drawable.flag_major_prop_dmg);
        ActivityStringInfo.flagTextwithIcons.put("MajorEquipmentIssueBreakdown", R.drawable.flag_major_equip_brkdwn);
        ActivityStringInfo.flagTextwithIcons.put("Other", R.drawable.flag_others);
    }

    public void createNewLog() {
        try {
            ActivityStringInfo.isCreateNewLog = true;
            ActivityStringInfo.selectedLogStatus = "I";
            ActivityStringInfo.selectedCreatedUserId = "";
            ActivityStringInfo.selectedLogForDate = "";
            ActivityStringInfo.selectedUserTypeId ="";
            createLogsDetails();
            Intent intent1 = new Intent(LogFragment.this.getActivity(), LogDetailsActivity.class);
            intent1.putExtra(INFO, stringInfo);
            startActivity(intent1);
            LogFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void createLogsDetails() {
        String maxLogId = "";
        Cursor c = null;
        try {

            c = MyDatabaseInstanceHolder.getDatabaseHelper().getMaxLogID();
            System.out.println("MAX count : " + c.getCount());
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getString(0).equals(null))
                        maxLogId = "0";
                    else
                        maxLogId = c.getString(0);
                }
            } else {
                maxLogId = "0";
            }
            System.out.println("maxLogId===" + maxLogId);
            if (c != null)
                c.close();

        } catch (Exception e) {
            maxLogId = "0";
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        try {
            ActivityStringInfo.selectedLogId = "" + (Integer.parseInt(maxLogId) + 1);
        } catch (Exception e) {
            ActivityStringInfo.selectedLogId = "1";
        }
    }

    /**
     * Get the logs list
     **/
    public void bindAdapter() {
        ActivityStringInfo.fillLogDetailList.clear();
        Cursor c = null;
        try {
            c = MyDatabaseInstanceHolder.getDatabaseHelper().getLogDetailRecords();
            System.out.println("LogDetail: No of Records :: " + c.getCount());

            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(DatabaseConstant.key_LOG_ID, c.getString(0));
                        map.put(DatabaseConstant.key_LOG_DATE, c.getString(1));
                        map.put(DatabaseConstant.key_CREATE_DATE, c.getString(2));
                        map.put(DatabaseConstant.key_CREATE_USER_ID, c.getString(3));
                        map.put(DatabaseConstant.key_CREATE_USER_NAME, c.getString(4));
                        map.put(DatabaseConstant.key_USER_TYPE_ID, c.getString(5));
                        map.put(DatabaseConstant.key_LAST_UPD_DATE, c.getString(6));
                        map.put(DatabaseConstant.key_STATUS, c.getString(7));

                        ActivityStringInfo.fillLogDetailList.add(map);

                    } while (c.moveToNext());
                }
            }
            if (c != null)
                c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
    }

    //Get all flags
    public void getLogFlagDetails() {
        ActivityStringInfo.fillFlagDetailList.clear();
        Cursor c = null;
        try {
            c = MyDatabaseInstanceHolder.getDatabaseHelper().getLogFlags();

            showFlagList = new ArrayList<AddFlagItem>();
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {


//                        Log.v("LogList", "hello " + c.getString(1));
                        //ActivityStringInfo.flagTextwithIcons.get(c.getString(1)));
                        //	AddFlagItem flagItem = new AddFlagItem(ActivityStringInfo.flagTextwithIcons.get(c.getString(1)), c.getString(0), true);
                        AddFlagItem flagItem;
                        if (ActivityStringInfo.flagTextwithIcons.get(c.getString(1)) != null) {
                            flagItem = new AddFlagItem(ActivityStringInfo.flagTextwithIcons.get(c.getString(1)), c.getString(0), true);
                        } else {
                            flagItem = new AddFlagItem(ActivityStringInfo.flagTextwithIcons.get("Other"), c.getString(0), true);
                        }
                        flagItem.setDescription(c.getString(0));
                        flagItem.setFlag(c.getString(1));
                        flagItem.setFlaggedDate(c.getString(2));
                        flagItem.setFlagId(c.getString(3));
                        flagItem.setFlagLogId(c.getString(4));
                        flagItem.setFlagUserId(c.getString(5));
                        flagItem.setFlagUserName(c.getString(6));
                        showFlagList.add(flagItem);
                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public void getUptoUpdateTime() {
        try {
            System.out.println("ActivityStringInfo.selectedLogForDate=====" + ActivityStringInfo.selectedLogForDate);
            String[] strDate = ActivityStringInfo.selectedLogForDate.split("/");

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, Integer.parseInt(strDate[0]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strDate[1]));
            cal.set(Calendar.YEAR, Integer.parseInt(strDate[2]));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            System.out.println("cal=====" + cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            if (ActivityStringInfo.strDailyLogHour.equals(""))
                ActivityStringInfo.strDailyLogHour = "24";
            System.out.println("ActivityStringInfo.strDailyLogHour)==" + Math.round(Double.parseDouble(ActivityStringInfo.strDailyLogHour)));
            cal.add(Calendar.HOUR, (int) Math.round(Double.parseDouble(ActivityStringInfo.strDailyLogHour)));
            ActivityStringInfo.strUpdateTime = cal.getTime();
            System.out.println("strUpdateTime==" + ActivityStringInfo.strUpdateTime);
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private class GetLogRecentListDataTask extends AsyncTask<String, Void, String> {
        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                ActivityStringInfo.clearLogsFields();

                bindAdapter();
                getLogFlagDetails();

            } catch (Exception e) {
                Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (ActivityStringInfo.fillLogDetailList.size() > 0) {
//                    lst_RecentLogList.setAdapter(new MyArrayAdapter(LogList.this,R.layout.list_logs, ActivityStringInfo.fillLogDetailList));
                    setupLogRecyclerView(recyclerViewLog, ActivityStringInfo.fillLogDetailList);
//                    Utility.setListViewHeightBasedOnChildren(mContext,lst_RecentLogList);
                } else {
                    showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }


    private void setupLogRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<HashMap<String, String>> logArrayList) {
        Collections.reverse(logArrayList);
        recyclerView.setAdapter(new LogListItemRecyclerViewAdapter(logArrayList));
        if (ActivityStringInfo.mTwoPane) {
            if (logArrayList != null && logArrayList.size() > 0) {
                editRecentLog.setVisibility(View.VISIBLE);
                setDefaultLogListItemView(logArrayList.get(0));
            } else {
                editRecentLog.setVisibility(View.GONE);
            }
        }
    }

    private void setDefaultLogListItemView(HashMap<String, String> logArrayListItem) {

        ActivityStringInfo.clearLogsFields();

        ActivityStringInfo.selectedLogId = logArrayListItem.get(DatabaseConstant.key_LOG_ID);
        ActivityStringInfo.isCreateNewLog = false;

        ActivityStringInfo.selectedLogStatus = logArrayListItem.get(DatabaseConstant.key_STATUS);
        ActivityStringInfo.selectedLogForDate =  logArrayListItem.get(DatabaseConstant.key_LOG_DATE);

        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat sdfDatenew = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        try {
            ActivityStringInfo.selectedLogForDate = sdfDate.format(sdfDatenew.parse("" + logArrayListItem.get(DatabaseConstant.key_LOG_DATE)));
        } catch (ParseException e) {
            Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
            e.printStackTrace();
        }

        getUptoUpdateTime();
        LogDetailsFragment logDetailsFragment = new LogDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDisable", true);
        bundle.putSerializable(INFO, stringInfo);
        logDetailsFragment.setArguments(bundle);
        fragmentManagerLog = getFragmentManager();
        fragmentManagerLog.beginTransaction().replace(R.id.log_item_detail_container, logDetailsFragment).commit();
    }


    private class LogListItemRecyclerViewAdapter extends RecyclerView.Adapter<LogListItemRecyclerViewAdapter.ViewHolder> {

        private final List<HashMap<String, String>> logItems;
        private SparseBooleanArray selectedItems;

        public LogListItemRecyclerViewAdapter(List<HashMap<String, String>> items) {
            logItems = items;
            selectedItems = new SparseBooleanArray();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_log_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            try {
                Cursor c1 = null;
                final HashMap<String, String> hasValues = logItems.get(position);
                if (ActivityStringInfo.mTwoPane) {
                    if (position == 0 && isFirstSelected) {
                        this.selectedItems.put(position, true);
                    }

                    if (this.selectedItems.get(position)) {
                        holder.mView.setSelected(true);
                    } else {
                        holder.mView.setSelected(false);
                    }
                }

                SimpleDateFormat logSf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);
                SimpleDateFormat newsf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
                SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, MM/dd/yyyy", Locale.US);
                String logCreatedDate = hasValues.get(DatabaseConstant.key_LOG_DATE);

                String logDate = hasValues.get(DatabaseConstant.key_LAST_UPD_DATE);

                Date createdDate = logSf.parse(logCreatedDate);
                Date tempDate = sf.parse(logDate);

                //txt_LogDayDate.setText(sdfDate.format(tempDate));
                holder.txtLogDayDate.setText(sdfDate.format(createdDate));
                String fdate = newsf.format(tempDate);
                holder.txtLastUpdateDateTime.setText(logDate);
                String createUsername = hasValues.get(DatabaseConstant.key_CREATE_USER_NAME);
                String createUserId = hasValues.get(DatabaseConstant.key_USER_TYPE_ID);
                String createUserType = "";
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogUserTypeRecords(createUserId);
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            createUserType = c1.getString(1);
                        } while (c1.moveToNext());
                    }
                }
                c1.close();

                holder.txtByUserDesignation.setText(createUsername + ",\t" + createUserType);
                final String logStatus = hasValues.get(DatabaseConstant.key_STATUS);
                final String logId = hasValues.get(DatabaseConstant.key_LOG_ID);
                //	Log.v("LogList.java", "LogId=======>"+logId+"  LogStatus=======>"+logStatus);
                if (logStatus != null && logStatus.toLowerCase().contains("c")) {
                    holder.imgIncompleteLog.setVisibility(View.GONE);
                } else {
                    holder.imgIncompleteLog.setVisibility(View.VISIBLE);
                }
                //Log.v("Log list", " " + showFlagList.size());
                /**update log flags**/

                holder.imgFlagSecurityIssue.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_grey_new));
                holder.imgFlagEmpInjury.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_grey_new));
                holder.imgFlagGuestInjury.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_grey_new));
                holder.imgFlagMajorPropDamage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_grey_new));
                holder.imgFlagMajorEquipIssueBrkdwn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_grey_new));
                holder.imgFlagOther.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_grey_new));

                if (showFlagList != null) {
                    if (showFlagList.size() > 0) {

                        for (int i = 0; i < showFlagList.size(); i++) {
                            if (logId.trim().equals(showFlagList.get(i).getFlagLogId().trim())) {

                                if (showFlagList.get(i).getFlag().trim().equals("SecurityIssue")) {
                                    holder.imgFlagSecurityIssue.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_security));
                                } else if (showFlagList.get(i).getFlag().trim().equals("EmployeeIncidentInjury")) {
                                    holder.imgFlagEmpInjury.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_emp_injry));
                                } else if (showFlagList.get(i).getFlag().trim().equals("GuestIncidentInjury")) {
                                    holder.imgFlagGuestInjury.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_guest_injury));
                                } else if (showFlagList.get(i).getFlag().trim().equals("MajorPropertyDamage")) {
                                    holder.imgFlagMajorPropDamage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_major_prop_dmg));
                                } else if (showFlagList.get(i).getFlag().trim().equals("MajorEquipmentIssueBreakdown")) {
                                    holder.imgFlagMajorEquipIssueBrkdwn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_major_equip_brkdwn));
                                } else if (showFlagList.get(i).getFlag().trim().equals("Other")) {
                                    holder.imgFlagOther.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.flag_others));
                                }
                            }
                        }
                    }
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleSelection(position, v);

                        ActivityStringInfo.clearLogsFields();

                        ActivityStringInfo.selectedLogId = hasValues.get(DatabaseConstant.key_LOG_ID);
                        ActivityStringInfo.isCreateNewLog = false;

                        ActivityStringInfo.selectedLogStatus = logStatus;
                        ActivityStringInfo.selectedLogForDate =  hasValues.get(DatabaseConstant.key_LOG_DATE);

                       /* SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                        SimpleDateFormat sdfDatenew = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

                        try {
                            ActivityStringInfo.selectedLogForDate = sdfDate.format(sdfDatenew.parse("" + hasValues.get(DatabaseConstant.key_LOG_DATE)));
                        } catch (ParseException e) {
                            Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
                            e.printStackTrace();
                        }*/

                        getUptoUpdateTime();
                        if (ActivityStringInfo.mTwoPane) {
                            LogDetailsFragment logDetailsFragment = new LogDetailsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDisable", true);
                            bundle.putSerializable(INFO, stringInfo);
                            logDetailsFragment.setArguments(bundle);
                            fragmentManagerLog = getFragmentManager();
                            fragmentManagerLog.beginTransaction().replace(R.id.log_item_detail_container, logDetailsFragment).commit();
                        } else {
                            Intent intent2 = new Intent(LogFragment.this.getActivity(), LogDetailsActivity.class);
                            intent2.putExtra(INFO, stringInfo);
                            startActivity(intent2);
                            LogFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                        }
                    }
                });


            } catch (Exception e) {
                Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
            }
        }

        public void toggleSelection(int pos, View view) {
            isFirstSelected = false;
            selectedItems.clear();
            selectedItems.put(pos, true);
//            view.setSelected(true);
            notifyDataSetChanged();
        }

        public void clearSelections() {

        }

        public int getSelectedItemCount() {
            return selectedItems.size();
        }

        public List<Integer> getSelectedItems() {
            List<Integer> items =
                    new ArrayList<Integer>(selectedItems.size());
            for (int i = 0; i < selectedItems.size(); i++) {
                items.add(selectedItems.keyAt(i));
            }
            return items;
        }

        @Override
        public int getItemCount() {
            return logItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView txtLogDayDate;
            public final TextView txtByUserDesignation;
            public final TextView txtLastUpdateDateTime;
            public final ImageView imgFlagSecurityIssue;
            public final ImageView imgFlagEmpInjury;
            public final ImageView imgFlagGuestInjury;
            public final ImageView imgFlagMajorPropDamage;
            public final ImageView imgFlagMajorEquipIssueBrkdwn;
            public final ImageView imgFlagOther;
            public final ImageView imgIncompleteLog;
            public HashMap<String, String> hasValues;

            public ViewHolder(View view) {
                super(view);
                view.setClickable(true);
                mView = view;
                txtLogDayDate = (TextView) view.findViewById(R.id.txtLogDayDate);
                txtByUserDesignation = (TextView) view.findViewById(R.id.txtByUserDesignation);
                txtLastUpdateDateTime = (TextView) view.findViewById(R.id.txtLastUpdateDateTime);

                imgFlagSecurityIssue = (ImageView) view.findViewById(R.id.imgFlagSecurityIssue);
                imgFlagEmpInjury = (ImageView) view.findViewById(R.id.imgFlagEmpInjury);
                imgFlagGuestInjury = (ImageView) view.findViewById(R.id.imgFlagGuestInjury);
                imgFlagMajorPropDamage = (ImageView) view.findViewById(R.id.imgFlagMajorPropDamage);
                imgFlagMajorEquipIssueBrkdwn = (ImageView) view.findViewById(R.id.imgFlagMajorEquipIssueBrkdwn);
                imgFlagOther = (ImageView) view.findViewById(R.id.imgFlagOther);
                imgIncompleteLog = (ImageView) view.findViewById(R.id.imgIncompleteLog);


            }

            @Override
            public String toString() {
                return super.toString() + " '" + hasValues.toString() + "'";
            }
        }
    }

    private void getLogListRefresh() {
        new SelectDataTaskForRefresh().execute();
    }

    private class SelectDataTaskForRefresh extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strRefresh);
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                Synchronization syc = new Synchronization(mContext);
                strMsg = syc.getInformation(LogFragment.this.getActivity());
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true"))
            //&& ActivityStringInfo.strMandatoryMessageId.size()==0)
            {
                isFirstSelected = true;
                setAllLogs();
                if (!ActivityStringInfo.strLogRights.toLowerCase().contains("y")) {
//                    btn_Messages.performClick();
                } else {
//                    Intent intent2 = new Intent(getApplicationContext(), LogList.class);
//                    intent2.putExtra(INFO, stringInfo);
//                    startActivity(intent2);
//                    finish();
                }
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
