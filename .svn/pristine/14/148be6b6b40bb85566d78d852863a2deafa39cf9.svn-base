package ism.manager.message;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.StaticVariables;

public class TradeSchedulesListActivity extends AppBaseActivity {

    ListView lst_TradeShift;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";

    List<HashMap<String, String>> fillTradeScheduleList = new ArrayList<HashMap<String, String>>();

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_trade_schedules_list);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            mContext = this;
            lst_TradeShift = (ListView) findViewById(R.id.tradeSchedule_list);
            setValues();
        } catch (Exception e) {
            Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
            e.printStackTrace();
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
            fillTradeScheduleList.clear();
            if (stringInfo.strCommentForWhich.equals("T"))
                cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getSelectedShift(stringInfo.strShiftId);
            else
                cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getPostedSchedules(ActivityStringInfo.strFirstName + " " + ActivityStringInfo.strLastName);

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
                            fillTradeScheduleList.add(map);
                        } catch (Exception e) {
                            Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            ActivityStringInfo.shiftTypeMap.clear();
        } catch (Exception e) {
            Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
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
                Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                bindListAdapter();
            } catch (Exception e) {
                Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
                e.printStackTrace();
            }

            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (fillTradeScheduleList.size() > 0) {
                    lst_TradeShift.setAdapter(new MyArrayAdapter(TradeSchedulesListActivity.this, R.layout.listitem_trade_schedule_shift, fillTradeScheduleList));
//                    Utility.setListViewHeightBasedOnChildren(mContext, lst_TradeShift);
                } else
                    showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            } catch (Exception e) {
                Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
                e.printStackTrace();
            }
        }
    }

    private class MyArrayAdapter extends ArrayAdapter<HashMap<String, String>> {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        public MyArrayAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            context = getContext();
            myData = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.listitem_trade_schedule_shift, null);
                }
                LinearLayout lylScheduleList = (LinearLayout) v.findViewById(R.id.lylScheduleListItem);
                TextView txt_Day = (TextView) v.findViewById(R.id.txtShiftDay);
                TextView txt_Date = (TextView) v.findViewById(R.id.txtShiftDate);
                final TextView txt_ShiftType = (TextView) v.findViewById(R.id.txtShift);
                TextView txt_TimeRange = (TextView) v.findViewById(R.id.txtShiftTimeRange);

                final HashMap<String, String> hasValues = myData.get(position);
                if (hasValues != null) {
                    String dateStr = hasValues.get(DatabaseConstant.key_START_DATE);
//
//                    if(position % 2 == 0)
//                    {
//                        lylScheduleList.setBackgroundColor(Color.parseColor("#cccccc"));
//                        txt_Day.setTextColor(Color.parseColor("#000000"));
//                        txt_Date.setTextColor(Color.parseColor("#000000"));
//                        txt_ShiftType.setTextColor(Color.parseColor("#000000"));
//                        txt_TimeRange.setTextColor(Color.parseColor("#000000"));
//                    }
//                    else
//                    {
//                        lylScheduleList.setBackgroundColor(Color.parseColor("#00000000"));
//                        txt_Day.setTextColor(Color.parseColor("#005597"));
//                        txt_Date.setTextColor(Color.parseColor("#005597"));
//                        txt_ShiftType.setTextColor(Color.parseColor("#005597"));
//                        txt_TimeRange.setTextColor(Color.parseColor("#005597"));
//                    }

                    final Date dateObj = StaticVariables.dbDateFormat.parse(dateStr);

                    SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd", Locale.US);
                    String newDateStr = newDateFormat.format(dateObj);

                    // set the day
                    txt_Day.setText(StaticVariables.getDayName(dateObj));

                    // set date
                    txt_Date.setText(newDateStr);

                    SimpleDateFormat newTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);

                    // set start & end time
                    if (!hasValues.get(DatabaseConstant.key_START_TIME).equals("") && hasValues.get(DatabaseConstant.key_TYPE).equals("S")) {
                        txt_TimeRange.setText(StaticVariables.generalTimeFormat.format(newTimeFormat.parse(hasValues.get(DatabaseConstant.key_START_TIME))) + " - " + StaticVariables.generalTimeFormat.format(newTimeFormat.parse(hasValues.get(DatabaseConstant.key_END_TIME))));
                    } else if (!hasValues.get(DatabaseConstant.key_START_TIME).equals("") && hasValues.get(DatabaseConstant.key_TYPE).equals("M")) {
                        txt_TimeRange.setText(hasValues.get(DatabaseConstant.key_START_TIME) + " - " + hasValues.get(DatabaseConstant.key_END_TIME));
                    } else {
                        txt_TimeRange.setText("");
                    }

                    String shiftType = new String();
                    shiftType = "";

                    // set off day as gray colored
                    if (hasValues.get(DatabaseConstant.key_START_TIME).equals("")) {
                        lylScheduleList.setBackgroundColor(Color.parseColor("#A7A7A7"));
                        shiftType = "";
                    }

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
                        }
                    }
                    if (shiftType.equals(""))
                        shiftType = " ";
                    txt_ShiftType.setText(shiftType);

                    ActivityStringInfo.shiftTypeMap.put(position, shiftType);

                    v.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                SimpleDateFormat newDateFormatDisplay = new SimpleDateFormat("MM/dd/yyyy");
                                stringInfo.setSelectedDate(newDateFormatDisplay.format(dateObj));
                                stringInfo.setSelectedId(hasValues.get(DatabaseConstant.key_SHIFT_ID));
                                if (hasValues.get(DatabaseConstant.key_TYPE).toString().equals("M")) {
                                    showToast("Not Available", Toast.LENGTH_SHORT);
                                } else if (hasValues.get(DatabaseConstant.key_TYPE).toString().equals("S")) {
                                    try {
                                        if (stringInfo.strCommentForWhich.equals("T")) {
                                            ActivityStringInfo.previousActivity = TradeSchedulesListActivity.class;
                                            Intent i = new Intent(getApplicationContext(), ShiftAcceptCommentActivity.class);
                                            i.putExtra(INFO, stringInfo);
                                            startActivity(i);
                                            finish();
                                            TradeSchedulesListActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                                        } else {
                                            showToast("Not Available", Toast.LENGTH_SHORT);

                                        }
                                    } catch (Exception e) {
                                        Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(TradeSchedulesListActivity.this, e);
                e.printStackTrace();
            }
            return v;
        }
    }
}
