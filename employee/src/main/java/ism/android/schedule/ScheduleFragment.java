package ism.android.schedule;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.utils.StaticVariables;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends AppBaseFragment {

    private FragmentManager schdlFragmentManager;

    private Button requestOffBtn;

    private RecyclerView scheduleListRecyclerView;

    public boolean shouldSchdlListRefreshed;

    // Variable Declaration
    private ActivityStringInfo stringInfo;

    private ArrayList<HashMap<String, String>> fillScheduleList;

    public static final String INFO = "INFO";

    private Context mContext;

    private boolean isFirstSelected = true;
    private Activity mActivity;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = ScheduleFragment.this.getActivity();
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
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        this.initViews(view);
//        this.setValues();
//        this.setListeners();
        if (view.findViewById(R.id.schdl_item_detail_container) != null) {
            ActivityStringInfo.mTwoPane = true;
        } else {
            ActivityStringInfo.mTwoPane = false;
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityStringInfo.shouldRefreshed) {
            ActivityStringInfo.shouldRefreshed = false;
            shouldSchdlListRefreshed = true;
            getSchedulesRefresh();
        } else {
            setSchdlAll();
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
            getSchedulesRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            mActivity = getActivity();
        }
    }

    private void setListeners() {
        this.requestOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(ScheduleFragment.this.getActivity(), RequestOffActivity.class);
                    i.putExtra(INFO, stringInfo);
                    startActivity(i);
                    ScheduleFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });
    }


    private void initViews(View v) {
//        this.fillScheduleList = new ArrayList<HashMap<String, String>>();
        this.requestOffBtn = (Button) v.findViewById(R.id.btnRequestOff);
        this.scheduleListRecyclerView = (RecyclerView) v.findViewById(R.id.scheduleItem_list);
        assert scheduleListRecyclerView != null;
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
        if (stringInfo.strCommentForWhich.equals("T")) {
            //TODO Pending implementation
//            txt_ShiftListTitle.setVisibility(View.VISIBLE);
//            txt_ShiftListTitle.setText("Select Shift to Trade");
//            btn_Request_Off.setVisibility(View.GONE);
        }
    }

    public void setSchdlAll() {
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
        if (Utility.getMandatoryMessage(mContext) != null) {
            Intent intent2 = Utility.getMandatoryMessage(mContext);
            intent2.putExtra(INFO, stringInfo);
            startActivity(intent2);
            ScheduleFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            ActivityStringInfo.alertopen = true;
        } else {
            loadSchdlListData();
        }
        setListeners();
    }

    private void loadSchdlListData() {
        if (shouldSchdlListRefreshed) {
            shouldSchdlListRefreshed = false;
            if (fillScheduleList == null) {
                fillScheduleList = new ArrayList<HashMap<String, String>>();
            } else {
                fillScheduleList.clear();
            }
            new SelectDataTaskForScheduleData().execute();
        } else {
            if (fillScheduleList == null) {
                fillScheduleList = new ArrayList<HashMap<String, String>>();
                new SelectDataTaskForScheduleData().execute();
            }
        }
    }

    private void setValues() {
        new SelectDataTaskForScheduleData().execute();
    }

    /**
     * Bind the data for the shift list
     **/
    public void bindListAdapter() {
        Cursor cursor = null;
        try {
            fillScheduleList.clear();
            if (stringInfo.strCommentForWhich.equals("T"))
                cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getSelectedShift(stringInfo.strShiftId);
            else
                cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getPostedSchedules();    // ActivityStringInfo.strFirstName + " " + ActivityStringInfo.strLastName

            if (cursor.getCount() > 0) {
                String columns[] = {DatabaseConstant.key_SHIFT_ID,
                        DatabaseConstant.key_TITLE,
                        DatabaseConstant.key_START_DATE,
                        DatabaseConstant.key_START_TIME,
                        DatabaseConstant.key_END_TIME,
                        DatabaseConstant.key_SPLIT_START_TIME,
                        DatabaseConstant.key_SPLIT_END_TIME,
                        DatabaseConstant.key_TRAINEE,
                        DatabaseConstant.key_ISOVERTIME,
                        DatabaseConstant.key_END_DATE,
                        DatabaseConstant.key_TYPE,
                        DatabaseConstant.key_STATUS};
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            HashMap<String, String> map = new HashMap<String, String>();
                            for (int i = 0; i < columns.length; i++) {
                                String value = cursor.getString(i);
                                if (value == null) {
                                    map.put(columns[i], "");
                                } else {
                                    map.put(columns[i], value);
                                }
                            }
                            fillScheduleList.add(map);
                        } catch (Exception e) {
                            Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            ActivityStringInfo.shiftTypeMap.clear();
        } catch (Exception e) {
            Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    private class SelectDataTaskForScheduleData extends AsyncTask<String, Void, String> {
        // can use UI thread here
        protected void onPreExecute() {

            try {
                showTransparentProgressDialog(MessageInfo.loading);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                bindListAdapter();
            } catch (Exception e) {
                Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                e.printStackTrace();
            }

            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (fillScheduleList.size() > 0) {
                    setUpScheduleRecyclerView(scheduleListRecyclerView, fillScheduleList);
                } else
                    showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    private void setDefaultScheduleListItemView(HashMap<String, String> scheduleArrListItem) {

        try {
            String dateStr = scheduleArrListItem.get(DatabaseConstant.key_START_DATE);
            final Date dateObj = StaticVariables.dbDateFormat.parse(dateStr);
            SimpleDateFormat newDateFormatDisplay = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            stringInfo.setSelectedDay(StaticVariables.getDayName(dateObj));
            stringInfo.setSelectedDate(newDateFormatDisplay.format(dateObj));
            stringInfo.setSelectedId(scheduleArrListItem.get(DatabaseConstant.key_SHIFT_ID));
            if (ActivityStringInfo.mTwoPane) {
                Fragment scheduleDetailFragment = null;
                if (scheduleArrListItem.get(DatabaseConstant.key_TYPE).toString().equals("M")) {
                    try {
                        scheduleDetailFragment = new ScheduleMeetingDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(INFO, stringInfo);
                        scheduleDetailFragment.setArguments(bundle);
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                } else if (scheduleArrListItem.get(DatabaseConstant.key_TYPE).toString().equals("S")) {
                    try {
                        if (stringInfo.strCommentForWhich.equals("T")) {

                        } else {
                            scheduleDetailFragment = new ShiftDetailsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(INFO, stringInfo);
                            scheduleDetailFragment.setArguments(bundle);
                        }
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                } else if (scheduleArrListItem.get(DatabaseConstant.key_TYPE).toString().equals("R")) {
                    try {
                        scheduleDetailFragment = new RequestOffDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(INFO, stringInfo);
                        scheduleDetailFragment.setArguments(bundle);
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                }
                if (this.getActivity() != null && !getActivity().isFinishing()) {
                    if (scheduleDetailFragment != null) {
                        schdlFragmentManager = getActivity().getSupportFragmentManager();
                        schdlFragmentManager.beginTransaction().replace(R.id.schdl_item_detail_container, scheduleDetailFragment).commitAllowingStateLoss();
                    } else {
                        showToast("Not Available..", Toast.LENGTH_SHORT);
                    }
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private void setUpScheduleRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<HashMap<String, String>> scheduleArrayList) {
        try {
            recyclerView.setAdapter(new ScheduleListItemRecyclerViewAdapter(scheduleArrayList));
            if (ActivityStringInfo.selectedListItemIndex > 3)
                recyclerView.scrollToPosition(ActivityStringInfo.selectedListItemIndex);
            if (ActivityStringInfo.mTwoPane) {
                if (scheduleArrayList != null && scheduleArrayList.size() > 0)
                    setDefaultScheduleListItemView(scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex));
            } else {
//                Configuration configuration = getResources().getConfiguration();
//                if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT)

                if (ActivityStringInfo.isListItemSelected) {
                    String dateStr = scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_START_DATE);
                    final Date dateObj = StaticVariables.dbDateFormat.parse(dateStr);
                    SimpleDateFormat newDateFormatDisplay = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    if (scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_TYPE).toString().equals("M") || scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_TYPE).toString().equals("S") || scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_TYPE).toLowerCase().equals("r")) {
                        if (scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_TYPE).toString() != null && scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_TYPE).toString().length() > 0) {
                            stringInfo.setSelectedDay(StaticVariables.getDayName(dateObj));
                            stringInfo.setSelectedDate(newDateFormatDisplay.format(dateObj));
                            stringInfo.setSelectedId(scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_SHIFT_ID));
                            stringInfo.setRequestOffStatus(scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_STATUS));
                            Intent messageDetails = new Intent(getContext(), ScheduleDetailsActivity.class);
                            stringInfo.strScheduleType = scheduleArrayList.get(ActivityStringInfo.selectedListItemIndex).get(DatabaseConstant.key_TYPE).toString();
                            messageDetails.putExtra(INFO, stringInfo);
                            getContext().startActivity(messageDetails);
//                            ActivityStringInfo.isListItemSelected =false;
                        }
                    }

                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(getContext(), e);
            e.printStackTrace();
        }
    }

    public class ScheduleListItemRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleListItemRecyclerViewAdapter.ViewHolder> {

        private final List<HashMap<String, String>> scheduleItems;

        private SparseBooleanArray selectedItems;


        public ScheduleListItemRecyclerViewAdapter(List<HashMap<String, String>> items) {
            this.scheduleItems = items;
            this.selectedItems = new SparseBooleanArray();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_schedule_shift, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            try {
                final HashMap<String, String> hasValues = scheduleItems.get(position);
                if (ActivityStringInfo.mTwoPane) {
                    if (ActivityStringInfo.selectedListItemIndex == position) {
                        holder.mView.setSelected(true);
                    } else {
                        holder.mView.setSelected(false);
                    }
                } else {
                    if (ActivityStringInfo.selectedListItemIndex == position && ActivityStringInfo.isListItemSelected) {
                        holder.mView.setSelected(true);
                    } else {
                        holder.mView.setSelected(false);
                    }
                }

//                if (ActivityStringInfo.selectedListItemIndex == position) {
//                    holder.mView.setSelected(true);
//                } else {
//                    holder.mView.setSelected(false);
//                }
//                if (ActivityStringInfo.mTwoPane) {
//                    if (position == 0 && isFirstSelected) {
//                        this.selectedItems.put(position, true);
//                    }
//
//                    if (this.selectedItems.get(position)) {
//                        holder.mView.setSelected(true);
//                    } else {
//                        holder.mView.setSelected(false);
//                    }
//                }
                if (hasValues != null) {
                    String dateStr = hasValues.get(DatabaseConstant.key_START_DATE);
                    final Date dateObj = StaticVariables.dbDateFormat.parse(dateStr);

                    SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd", Locale.US);
                    String newDateStr = newDateFormat.format(dateObj);

                    // set the day
                    holder.txtShiftDay.setText(StaticVariables.getDayName(dateObj));

                    // set date
                    holder.txtShiftDate.setText(newDateStr);

                    SimpleDateFormat newTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);

                    // set start & end time
                    if (!hasValues.get(DatabaseConstant.key_START_TIME).equals("") && hasValues.get(DatabaseConstant.key_TYPE).equals("S")) {
                        holder.txtShiftTimeRange.setText(StaticVariables.generalTimeFormat.format(newTimeFormat.parse(hasValues.get(DatabaseConstant.key_START_TIME))) + " - " + StaticVariables.generalTimeFormat.format(newTimeFormat.parse(hasValues.get(DatabaseConstant.key_END_TIME))));
                    } else if (!hasValues.get(DatabaseConstant.key_START_TIME).equals("") && hasValues.get(DatabaseConstant.key_TYPE).equals("M")) {
                        holder.txtShiftTimeRange.setText(hasValues.get(DatabaseConstant.key_START_TIME) + " - " + hasValues.get(DatabaseConstant.key_END_TIME));
                    } else {
                        holder.txtShiftTimeRange.setText("");
                    }

                    String shiftType = new String();
                    shiftType = "";

                    // set off day as gray colored
                    if (hasValues.get(DatabaseConstant.key_START_TIME).equals("")) {
//                        holder.lylSchdlSubLayout.setBackgroundColor(Color.parseColor("#E6E6E6"));
                        shiftType = "";
                    }
//                    else {
//                        if (!holder.mView.isSelected())
//                            holder.lylSchdlSubLayout.setBackgroundColor(Color.parseColor("#ffffff"));
//                    }

                    // set overtime info
                    if (hasValues.get(DatabaseConstant.key_ISOVERTIME).toLowerCase().contains("y")) {
                        shiftType += StaticVariables.overtime;
                    }

                    // set trainee info

                    if ((hasValues.get(DatabaseConstant.key_TRAINEE).toString().trim().length()) > 0) {
                        if (shiftType.equals(""))
                            shiftType += StaticVariables.trainee;
                        else
                            shiftType += " - " + StaticVariables.trainee;
                    }

                    //set shift split info
                    if (!hasValues.get(DatabaseConstant.key_SPLIT_START_TIME).equals("")) {
                        if (shiftType.equals(""))
                            shiftType += StaticVariables.split;
                        else
                            shiftType += " - " + StaticVariables.split;
                    }

                    //set shift split info
                    if (hasValues.get(DatabaseConstant.key_SPLIT_START_TIME).equals("") && !hasValues.get(DatabaseConstant.key_START_TIME).equals("")) {
                        shiftType += "";
                    }

                    // set meeting info
                    if (!hasValues.get(DatabaseConstant.key_TITLE).equals("")) {
                        shiftType += StaticVariables.meeting + hasValues.get(DatabaseConstant.key_TITLE);
                    }

                    // set standard shift info
                    if (!hasValues.get(DatabaseConstant.key_START_TIME).equals("") && hasValues.get(DatabaseConstant.key_TITLE).equals("") && hasValues.get(DatabaseConstant.key_SPLIT_START_TIME).equals("") && hasValues.get(DatabaseConstant.key_TRAINEE).equals("") && hasValues.get(DatabaseConstant.key_ISOVERTIME).equals("")) {
                        shiftType += StaticVariables.standard;
                    }

                    if (hasValues.get(DatabaseConstant.key_TYPE).toLowerCase().equals("r")) {
                        if (hasValues.get(DatabaseConstant.key_STATUS).toLowerCase().equals("a")) {
                            shiftType = StaticVariables.requestOffApproved;
                        } else if (hasValues.get(DatabaseConstant.key_STATUS).toLowerCase().equals("d")) {
                            shiftType = StaticVariables.requestOffRejected;
                        } else {
                            shiftType = StaticVariables.approvalPending;
                        }
                    }
                    if (shiftType.equals(""))
                        shiftType = " ";

                    holder.txtShift.setText(shiftType);

                    ActivityStringInfo.shiftTypeMap.put(position, shiftType);

                    holder.mView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ActivityStringInfo.selectedListItemIndex = position;
                            ActivityStringInfo.isListItemSelected = true;
                            toggleSelection(position, v);
                            try {
                                SimpleDateFormat newDateFormatDisplay = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                                stringInfo.setSelectedDay(StaticVariables.getDayName(dateObj));
                                stringInfo.setSelectedDate(newDateFormatDisplay.format(dateObj));
                                stringInfo.setSelectedId(hasValues.get(DatabaseConstant.key_SHIFT_ID));
                                stringInfo.setRequestOffStatus(hasValues.get(DatabaseConstant.key_STATUS));
                                if (ActivityStringInfo.mTwoPane) {
                                    Fragment scheduleDetailFragment = null;
                                    if (hasValues.get(DatabaseConstant.key_TYPE).toString().equals("M")) {
                                        try {
                                            scheduleDetailFragment = new ScheduleMeetingDetailFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable(INFO, stringInfo);
                                            scheduleDetailFragment.setArguments(bundle);
                                        } catch (Exception e) {
                                            Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                                            e.printStackTrace();
                                        }
                                    } else if (hasValues.get(DatabaseConstant.key_TYPE).toString().equals("S")) {
                                        try {
                                            if (stringInfo.strCommentForWhich.equals("T")) {
//                                            ActivityStringInfo.previousActivity = Schedule_Home.class;
//                                            Intent i = new Intent(getApplicationContext(), ShiftEmpAcceptComment.class);
//                                            i.putExtra(INFO, stringInfo);
//                                            startActivity(i);
//                                            finish();
//                                            Schedule_Home.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                                            } else {
                                                scheduleDetailFragment = new ShiftDetailsFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(INFO, stringInfo);
                                                scheduleDetailFragment.setArguments(bundle);
//                                            Intent i = new Intent(getApplicationContext(), ShiftDetail.class);
//                                            i.putExtra(INFO, stringInfo);
//                                            startActivity(i);
//                                            finish();
//                                            Schedule_Home.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                                            }
                                        } catch (Exception e) {
                                            Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                                            e.printStackTrace();
                                        }
                                    } else if (hasValues.get(DatabaseConstant.key_TYPE).toLowerCase().equals("r")) {
                                        try {
                                            scheduleDetailFragment = new RequestOffDetailsFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable(INFO, stringInfo);
                                            scheduleDetailFragment.setArguments(bundle);
                                        } catch (Exception e) {
                                            Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                                            e.printStackTrace();
                                        }
                                    }
                                    if (scheduleDetailFragment != null) {
                                        schdlFragmentManager = getFragmentManager();
                                        schdlFragmentManager.beginTransaction().replace(R.id.schdl_item_detail_container, scheduleDetailFragment).commit();
                                    } else {
                                        showToast("Not Available..", Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    if (hasValues.get(DatabaseConstant.key_TYPE).toString().equals("M") || hasValues.get(DatabaseConstant.key_TYPE).toString().equals("S") || hasValues.get(DatabaseConstant.key_TYPE).toLowerCase().equals("r")) {
                                        if (hasValues.get(DatabaseConstant.key_TYPE).toString() != null && hasValues.get(DatabaseConstant.key_TYPE).toString().length() > 0) {
                                            Context context = v.getContext();
                                            Intent messageDetails = new Intent(context, ScheduleDetailsActivity.class);
                                            stringInfo.strScheduleType = hasValues.get(DatabaseConstant.key_TYPE).toString();
                                            messageDetails.putExtra(INFO, stringInfo);
                                            context.startActivity(messageDetails);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return scheduleItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView txtShiftDay;
            public final TextView txtShiftDate;
            public final TextView txtShiftTimeRange;
            public final ImageView imgMessageImage;
            public final TextView txtShift;
            public final LinearLayout lylScheduleListItem;

            public HashMap<String, String> hasValues;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                txtShiftDay = (TextView) view.findViewById(R.id.txtShiftDay);
                txtShiftDate = (TextView) view.findViewById(R.id.txtShiftDate);
                txtShiftTimeRange = (TextView) view.findViewById(R.id.txtShiftTimeRange);
                imgMessageImage = (ImageView) view.findViewById(R.id.imgMessageImage);
                txtShift = (TextView) view.findViewById(R.id.txtShift);
                lylScheduleListItem = (LinearLayout) view.findViewById(R.id.lylScheduleListItem);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + hasValues.toString() + "'";
            }
        }

        public void toggleSelection(int pos, View view) {
            isFirstSelected = false;
            selectedItems.clear();
            selectedItems.put(pos, true);
//            view.setSelected(true);
            notifyDataSetChanged();
        }

    }

    public void getSchedulesRefresh() {
        new SelectDataTaskForScheduleRefresh().execute();
    }

    private class SelectDataTaskForScheduleRefresh extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strRefresh);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
//                MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();
                Synchronization syc = new Synchronization(ScheduleFragment.this.getActivity());
                strMsg = syc.getInformation(ScheduleFragment.this.getActivity());
            } catch (Exception e) {
                Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
//
//                if (ScheduleFragment.this.getActivity() != null)
//                    ScheduleFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                if (strMsg.equals("true")) {
                    //&& ActivityStringInfo.strMandatoryMessageId.size() == 0) {
                    stringInfo.clear();
                    shouldSchdlListRefreshed = true;
                    isFirstSelected = true;
                    ActivityStringInfo.selectedListItemIndex = 0;
                    ActivityStringInfo.isListItemSelected = false;
                    setSchdlAll();
                } else if (strMsg != "true") {
                    if (Utility.Connectivity_Internet(ScheduleFragment.this.getActivity())) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ScheduleFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
