package ism.manager.schedule;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.StaticVariables;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShiftDetailsFragment extends AppBaseFragment {
    //for UI component
    Button btn_GiveAway, btn_GiveTo, btn_TradeWith;

    TextView txt_Day, txt_Date, txt_ShiftType, txt_Server, txt_WorkStation;
    TextView txt_ShiftStartTime, txt_ShiftEndTime, txt_SplitStartTime, txt_SplitEndTime;
    TextView txt_ShiftStartDate, txt_ShiftEndDate, txt_SplitStartDate, txt_SplitEndDate;
    TextView txt_Overtime, txt_Trainee, txt_Status;
    ListView lst_Tasklist;
    LinearLayout lyl_SplitStart, lyl_SplitEnd;
    TextView txt_MainBanner;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    String shiftId;
    List<HashMap<String, String>> fillmap = new ArrayList<HashMap<String, String>>();
    Context mContext;

    public ShiftDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = ShiftDetailsFragment.this.getActivity();
        if (getArguments().containsKey(INFO)) {
            stringInfo = (ActivityStringInfo) getArguments().getSerializable(INFO);
        }
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }
        shiftId = stringInfo.getSelectedId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shift_details, container, false);
        try {
            this.initViews(view);
            this.setListeners();
            this.setValues();
            this.fillShiftDetails();
        } catch (Exception e) {
            Utility.saveExceptionDetails(ShiftDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!ActivityStringInfo.mTwoPane) {
                if (ActivityStringInfo.shouldRefreshed) {
                    //TODO remove finish if crashed, use back()
                    //                GiveToResponseFragment.this.getActivity().finish();
                    ((ScheduleDetailsActivity) mContext).back();
                }
            }else{
                if (ActivityStringInfo.shouldRefreshed) {
                    FragmentActivity activity = (FragmentActivity) mContext;
                    activity.getSupportFragmentManager().popBackStackImmediate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews(View v) {
        btn_GiveAway = (Button) v.findViewById(R.id.btnGiveAway);
        btn_GiveTo = (Button) v.findViewById(R.id.btnGiveTo);
        btn_TradeWith = (Button) v.findViewById(R.id.btnTradeWith);

        txt_Day = (TextView) v.findViewById(R.id.txtDay);
        txt_Date = (TextView) v.findViewById(R.id.txtDate);
        txt_ShiftType = (TextView) v.findViewById(R.id.txtShiftType);
        txt_Server = (TextView) v.findViewById(R.id.txtServer);
        txt_WorkStation = (TextView) v.findViewById(R.id.txtWorkStation);

        txt_ShiftStartTime = (TextView) v.findViewById(R.id.txtShiftStartTime);
        txt_ShiftEndTime = (TextView) v.findViewById(R.id.txtShiftEndTime);

        txt_SplitStartTime = (TextView) v.findViewById(R.id.txtSplitStartTime);
        txt_SplitEndTime = (TextView) v.findViewById(R.id.txtSplitEndTime);

        txt_ShiftStartDate = (TextView) v.findViewById(R.id.txtShiftStartDate);
        txt_ShiftEndDate = (TextView) v.findViewById(R.id.txtShiftEndDate);

        txt_SplitStartDate = (TextView) v.findViewById(R.id.txtSplitStartDate);
        txt_SplitEndDate = (TextView) v.findViewById(R.id.txtSplitEndDate);

        txt_Overtime = (TextView) v.findViewById(R.id.txtOvertime);
        txt_Trainee = (TextView) v.findViewById(R.id.txtTrainee);
        txt_Status = (TextView) v.findViewById(R.id.txtStatus);

        lyl_SplitStart = (LinearLayout) v.findViewById(R.id.lylSplitStart);
        lyl_SplitEnd = (LinearLayout) v.findViewById(R.id.lylSplitEnd);

        lst_Tasklist = (ListView) v.findViewById(R.id.lstTaskList);
        lst_Tasklist.setDivider(null);
        lst_Tasklist.setDividerHeight(0);

        btn_GiveAway.setVisibility(View.VISIBLE);
        btn_GiveTo.setVisibility(View.VISIBLE);
        btn_TradeWith.setVisibility(View.VISIBLE);
    }

    private void setValues() {
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
    }

    private void setListeners() {
        btn_GiveAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stringInfo.strCommentForWhich = "GA";
                    Intent i = new Intent(ShiftDetailsFragment.this.getActivity(), ShiftCommentActivity.class);
                    i.putExtra(INFO, stringInfo);
                    startActivity(i);
                    ShiftDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ShiftDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });

        btn_GiveTo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    stringInfo.strCommentForWhich = "GT";
                    Intent i = new Intent(ShiftDetailsFragment.this.getActivity(),ScheduleGiveToActivity.class);
                    i.putExtra(INFO, stringInfo);
                    startActivity(i);
                    ShiftDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                }
                catch (Exception e)
                {
                    Utility.saveExceptionDetails(ShiftDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });

        btn_TradeWith.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    stringInfo.strCommentForWhich = "T";
                    Intent i = new Intent(ShiftDetailsFragment.this.getActivity(),ScheduleGiveToActivity.class);
                    i.putExtra(INFO, stringInfo);
                    startActivity(i);
                    ShiftDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                }
                catch (Exception e)
                {
                    Utility.saveExceptionDetails(ShiftDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * FILLS THE SHIFT DETAILS
     **/
    public void fillShiftDetails() {
        Cursor cursor = null;

        try {
            int totalrecords = 0;

            cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getMyShiftDetail(shiftId);

            totalrecords = cursor.getCount();
            if (totalrecords == 0) {
                showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            } else {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            String date = cursor.getString(1);
                            Date dateObj = StaticVariables.dbDateFormat.parse(date);

                            txt_Day.setText(StaticVariables.getDayName(dateObj));

                            SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                            String shiftDate = newDateFormat.format(dateObj);
                            txt_Date.setText(shiftDate);

                            Calendar cal = Calendar.getInstance();
                            String currentDate = StaticVariables.generalDateFormat.format(cal.getTime());

                            if (shiftDate.equals(currentDate) || !cursor.getString(9).equals("")) {
                                btn_GiveAway.setVisibility(View.GONE);
                                btn_GiveTo.setVisibility(View.GONE);
                                btn_TradeWith.setVisibility(View.GONE);
                            }

                            txt_ShiftType.setText(cursor.getString(12));
                            txt_Server.setText(cursor.getString(13));
                            if (cursor.getString(7).equals(""))
                                txt_WorkStation.setVisibility(View.GONE);

                            SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
                            SimpleDateFormat newTimeFormat = new SimpleDateFormat("hh:mm a  MM/dd/yyyy", Locale.US);

                            txt_ShiftStartTime.setText(newTimeFormat.format(newFormat.parse(cursor.getString(2))));
                            txt_ShiftEndTime.setText(newTimeFormat.format(newFormat.parse(cursor.getString(3))));

                            if (cursor.getString(4).equals("")) {
                                lyl_SplitStart.setVisibility(View.GONE);
                                lyl_SplitEnd.setVisibility(View.GONE);
                            } else {
                                txt_SplitStartTime.setText(newTimeFormat.format(newFormat.parse(cursor.getString(4))));
                                txt_SplitEndTime.setText(newTimeFormat.format(newFormat.parse(cursor.getString(5))));
                            }

                            txt_Trainee.setText(cursor.getString(6));
                            txt_WorkStation.setText(cursor.getString(7));
                            String isOvertime = cursor.getString(8);
                            if (isOvertime.toLowerCase().contains("y"))
                                txt_Overtime.setText("Yes");
                            else
                                txt_Overtime.setText("No");
                            txt_Status.setText(cursor.getString(9));

                            // fill task list
                            fillTaskList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public void fillTaskList() {
        try {
            fillmap.clear();
            Cursor cursor_tasklist = null;
            cursor_tasklist = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftTaskList(shiftId);
            if (cursor_tasklist.getCount() != 0) {
                String columns[] = {DatabaseConstant.key_TITLE};
                if (cursor_tasklist.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        for (int i = 0; i < columns.length; i++) {
                            String value = cursor_tasklist.getString(2);
                            if (value == null) {
                                map.put(columns[i], "");
                            } else {
                                map.put(columns[i], " - " + value);
                            }
                        }
                        fillmap.add(map);
                    } while (cursor_tasklist.moveToNext());
                }
            }
            cursor_tasklist.close();
            lst_Tasklist.setAdapter(new MyArrayAdapter(ShiftDetailsFragment.this.getActivity(), R.layout.list_item_schedule_shift_task, fillmap));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_Tasklist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private class MyArrayAdapter extends ArrayAdapter<HashMap<String, String>> {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context context;

        public MyArrayAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            myData = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.list_item_schedule_shift_task, null);
                }

                final TextView txt_TaskName = (TextView) v.findViewById(R.id.txtTaskName);
                HashMap<String, String> hasValues = myData.get(position);
                if (hasValues != null) {
                    txt_TaskName.setText(hasValues.get(DatabaseConstant.key_TITLE));
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return v;
        }
    }


}
