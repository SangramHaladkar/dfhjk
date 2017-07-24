package ism.manager.message;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import ism.manager.ActivityStringInfo;
import ism.manager.MainActivity;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;
import ism.manager.webservices.SyncServiceHelper;
import ism.manager.webservices.Synchronization;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiveAwayRequestFragment extends AppBaseFragment {

    TextView txt_Requester, txt_SkillLevelRequester, txt_ShiftStartTimeRequester, txt_WorkStation;
    TextView txt_ShiftStartDateRequester, txt_ShiftEndTimeRequester, txt_ShiftEndDateRequester;
    TextView txt_SplitStartTimeRequester, txt_SplitStartDateRequester, txt_SplitEndTimeRequester;
    TextView txt_SplitEndDateRequester, txt_OvertimeRequester, txt_CommentRequester,btnDelete;
    Button btn_DenyAll, btn_Approve;

    EditText ed_ManagerComment;
    LinearLayout lyl_SplitStart, lyl_SplitEnd, lyl_WorkStation;
    ListView lst_Volunteer;


    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;
    String shiftTradeId = "", selectedVolunteerId = "";
    List<HashMap<String, String>> fillmap = new ArrayList<HashMap<String, String>>();
    List<View> viewList = new ArrayList<View>();

    public GiveAwayRequestFragment() {
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
        shiftTradeId = stringInfo.strMessageId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_give_away_request, container, false);
        mContext = GiveAwayRequestFragment.this.getActivity();
        this.initView(view);
        this.setListeners();
        this.fillGiveAwayRequestDetails(view);
        //TODO pending
        //setButtonDisEna();
        return view;
    }

    private void initView(View v) {

        txt_Requester = (TextView) v.findViewById(R.id.txtRequester);
        txt_SkillLevelRequester = (TextView) v.findViewById(R.id.txtSkillLevelRequester);

        txt_ShiftStartTimeRequester = (TextView) v.findViewById(R.id.txtShiftStartTimeRequester);
        txt_ShiftStartDateRequester = (TextView) v.findViewById(R.id.txtShiftStartDateRequester);

        txt_ShiftEndTimeRequester = (TextView) v.findViewById(R.id.txtShiftEndTimeRequester);
        txt_ShiftEndDateRequester = (TextView) v.findViewById(R.id.txtShiftEndDateRequester);

        txt_SplitStartTimeRequester = (TextView) v.findViewById(R.id.txtSplitStartTimeRequester);
        txt_SplitStartDateRequester = (TextView) v.findViewById(R.id.txtSplitStartDateRequester);

        txt_SplitEndTimeRequester = (TextView) v.findViewById(R.id.txtSplitEndTimeRequester);
        txt_SplitEndDateRequester = (TextView) v.findViewById(R.id.txtSplitEndDateRequester);

        txt_OvertimeRequester = (TextView) v.findViewById(R.id.txtOvertimeRequester);
        txt_CommentRequester = (TextView) v.findViewById(R.id.txtCommentRequester);

        txt_WorkStation = (TextView) v.findViewById(R.id.txtWorkStation);

        lyl_WorkStation = (LinearLayout) v.findViewById(R.id.lylWorkStation);

        ed_ManagerComment = (EditText) v.findViewById(R.id.edManagerComment);

        lyl_SplitStart = (LinearLayout) v.findViewById(R.id.lylSplitStart);
        lyl_SplitEnd = (LinearLayout) v.findViewById(R.id.lylSplitEnd);
        lyl_SplitStart.setVisibility(View.VISIBLE);
        lyl_SplitEnd.setVisibility(View.VISIBLE);

        btnDelete = (TextView) v.findViewById(R.id.btnDelete);
        btn_DenyAll = (Button) v.findViewById(R.id.btnDenyAll);
        btn_Approve = (Button) v.findViewById(R.id.btnApprove);
        lst_Volunteer = (ListView) v.findViewById(R.id.lstVolunteer);

    }

    private void setListeners() {
        lst_Volunteer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                HashMap<String, String> map = fillmap.get(position);
                selectedVolunteerId = map.get(DatabaseConstant.key_USER_ID);

                CheckedTextView chkTextView = (CheckedTextView) view.findViewById(R.id.chkTxtVolunteer);
                if (!chkTextView.isChecked()) {
                    chkTextView.setChecked(true);
                    if (viewList.size() != 0) {
                        View viewTemp = viewList.get(0);
                        CheckedTextView chk = (CheckedTextView) viewTemp.findViewById(R.id.chkTxtVolunteer);
                        chk.setChecked(false);
                    }
                    viewList.clear();
                    viewList.add(view);
                } else {
                    chkTextView.setChecked(false);
                    selectedVolunteerId = "";
                    viewList.clear();
                }
            }
        });

        btn_DenyAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SelectDataTaskForDeny().execute(ed_ManagerComment.getText().toString());
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SelectDataTaskForDeny().execute(ed_ManagerComment.getText().toString());
            }
        });

        btn_Approve.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!selectedVolunteerId.equals("")) {
                    new SelectDataTaskForApprove().execute(ed_ManagerComment.getText().toString());
                } else {
                    showToast("Please select one volunteer.", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * FILLS THE GIVE AWAY REQUEST DETAILS
     **/
    public void fillGiveAwayRequestDetails(View vw) {
        Cursor cursor = null;
        try {
            int totalrecords = 0;

            cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftOfferingDetail(shiftTradeId);

            totalrecords = cursor.getCount();
            if (totalrecords == 0) {
                showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            } else {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            txt_Requester.setText(cursor.getString(4));
                            txt_SkillLevelRequester.setText(cursor.getString(5));

                            String isovertime = cursor.getString(6).toLowerCase();
                            if (isovertime.contains("y")) {
                                txt_OvertimeRequester.setText("Yes");
                            } else if (isovertime.contains("n")) {
                                txt_OvertimeRequester.setText("No");
                            } else {
                                txt_OvertimeRequester.setText("");
                            }

                            String shiftStartDateTime = cursor.getString(8);
                            String shiftStartTime = shiftStartDateTime.substring(shiftStartDateTime.indexOf(" ") + 1);
                            String shiftStartDate = shiftStartDateTime.substring(0, shiftStartDateTime.indexOf(" "));

                            String shiftEndDateTime = cursor.getString(9);
                            String shiftEndTime = shiftEndDateTime.substring(shiftEndDateTime.indexOf(" ") + 1);
                            String shiftEndDate = shiftEndDateTime.substring(0, shiftEndDateTime.indexOf(" "));

                            txt_ShiftStartTimeRequester.setText(getFormattedTime(shiftStartTime));
                            txt_ShiftStartDateRequester.setText(shiftStartDate);

                            txt_ShiftEndTimeRequester.setText(getFormattedTime(shiftEndTime));
                            txt_ShiftEndDateRequester.setText(shiftEndDate);

                            String splitStartDateTime = cursor.getString(10);
                            if (!splitStartDateTime.equals("")) {
                                String splitStartTime = splitStartDateTime.substring(splitStartDateTime.indexOf(" ") + 1);
                                String splitStartDate = splitStartDateTime.substring(0, splitStartDateTime.indexOf(" "));

                                txt_SplitStartTimeRequester.setText(getFormattedTime(splitStartTime));
                                txt_SplitStartDateRequester.setText(splitStartDate);

                                String splitEndDateTime = cursor.getString(11);
                                if (!splitEndDateTime.equals("")) {
                                    String splitEndTime = splitEndDateTime.substring(splitEndDateTime.indexOf(" ") + 1);
                                    String splitEndDate = splitEndDateTime.substring(0, splitEndDateTime.indexOf(" "));

                                    txt_SplitEndTimeRequester.setText(getFormattedTime(splitEndTime));
                                    txt_SplitEndDateRequester.setText(splitEndDate);
                                } else {
                                    txt_SplitEndTimeRequester.setText("");
                                    txt_SplitEndDateRequester.setText("");
                                }
                            } else {
                                lyl_SplitStart.setVisibility(View.GONE);
                                lyl_SplitEnd.setVisibility(View.GONE);
                            }

                            TextView txt_CommentRequester1 = (TextView) vw.findViewById(R.id.txtCommentRequesterForWidth);
                            TextView txt_OvertimeRequester = (TextView) vw.findViewById(R.id.txtOvertimeRequesterForWidth);
                            TextView txt_Requester = (TextView) vw.findViewById(R.id.txtRequesterForWidth);
                            TextView txt_ShiftEndTimeRequester = (TextView) vw.findViewById(R.id.txtShiftEndTimeRequesterForWidth);
                            TextView txt_ShiftStartTimeRequester = (TextView) vw.findViewById(R.id.txtShiftStartTimeRequesterForWidth);
                            TextView txt_SplitEndTimeRequester = (TextView) vw.findViewById(R.id.txtSplitEndTimeRequesterForWidth);
                            TextView txt_SplitStartTimeRequester = (TextView) vw.findViewById(R.id.txtSplitStartTimeRequesterForWidth);
                            TextView txt_WorkStation1 = (TextView) vw.findViewById(R.id.txtWorkStationForWidth);

                            if (cursor.getString(12).equals("")) {
                                lyl_WorkStation.setVisibility(View.GONE);
                                txt_CommentRequester.setText("  " + cursor.getString(7));

                                txt_CommentRequester1.setWidth(110);
                                txt_OvertimeRequester.setWidth(110);
                                txt_Requester.setWidth(110);
                                txt_ShiftEndTimeRequester.setWidth(110);
                                txt_ShiftStartTimeRequester.setWidth(110);
                                txt_SplitEndTimeRequester.setWidth(110);
                                txt_SplitStartTimeRequester.setWidth(110);
                                txt_WorkStation1.setWidth(110);
                            } else {
                                txt_CommentRequester1.setWidth(135);
                                txt_OvertimeRequester.setWidth(135);
                                txt_Requester.setWidth(135);
                                txt_ShiftEndTimeRequester.setWidth(135);
                                txt_ShiftStartTimeRequester.setWidth(135);
                                txt_SplitEndTimeRequester.setWidth(135);
                                txt_SplitStartTimeRequester.setWidth(135);
                                txt_WorkStation1.setWidth(135);

                                txt_CommentRequester.setText("       " + cursor.getString(7));
                                txt_WorkStation.setText(cursor.getString(12));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            fillVolunteerList();
        } catch (SQLException e) {
            Utility.saveExceptionDetails(GiveAwayRequestFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public String getFormattedTime(String shiftTime) {
        String time = "";

        try {
            Pattern p = Pattern.compile(":");
            String[] item = p.split(shiftTime);
            for (int i = 0; i < 2; i++) {
                time += item[i];
                if (i == 0)
                    time += ":";
            }
            time += shiftTime.substring(shiftTime.indexOf(" "));
        } catch (Exception e) {
            Utility.saveExceptionDetails(GiveAwayRequestFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return time;
    }

    public void fillVolunteerList() {
        try {
            Cursor cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftRequestDetail(shiftTradeId);
            int rows = cursor.getCount();
            if (rows == 0) {
                lst_Volunteer.setVisibility(View.GONE);
            } else {
                fillmap.clear();
                lst_Volunteer.clearChoices();
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(DatabaseConstant.key_SHIFT_TRADE_ID, cursor.getString(0));
                            map.put(DatabaseConstant.key_SHIFT_ID, cursor.getString(1));
                            map.put(DatabaseConstant.key_USER_ID, cursor.getString(2));
                            map.put(DatabaseConstant.key_USER_NAME, cursor.getString(3));
                            map.put(DatabaseConstant.key_SKILL_LEVEL, cursor.getString(4));
                            map.put(DatabaseConstant.key_COMMENT, cursor.getString(5));
                            map.put(DatabaseConstant.key_IS_OVERTIME, cursor.getString(6));
                            map.put(DatabaseConstant.key_TYPE, cursor.getString(7));
                            fillmap.add(map);
                        } while (cursor.moveToNext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();

            lst_Volunteer.setAdapter(new MyArrayAdapter(GiveAwayRequestFragment.this.getActivity(), R.layout.list_item_msg_giveaway_volunteers, fillmap));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_Volunteer);
        } catch (Exception e) {
            Utility.saveExceptionDetails(GiveAwayRequestFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private class MyArrayAdapter extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context context;

        public MyArrayAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            try {
                this.context = context;
                myData = objects;
            } catch (Exception e) {
                Utility.saveExceptionDetails(this.context, e);
                e.printStackTrace();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.list_item_msg_giveaway_volunteers, null);
                }

                HashMap<String, String> hasValues = myData.get(position);

                if (hasValues != null) {

                    TextView txt_VolunteerName = (TextView) v.findViewById(R.id.txtVolunteerName);
                    txt_VolunteerName.setText(hasValues.get(DatabaseConstant.key_USER_NAME));

                    TextView txt_SkillLevel = (TextView) v.findViewById(R.id.txtSkillLevel);
                    txt_SkillLevel.setText(hasValues.get(DatabaseConstant.key_SKILL_LEVEL));

                    TextView txt_Comment = (TextView) v.findViewById(R.id.txtComment);
                    txt_Comment.setText(hasValues.get(DatabaseConstant.key_COMMENT));
                } else {
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(this.context, e);
                e.printStackTrace();
            }
            return v;
        }
    }

    public void setListViewHeight(ListView listView) {
        try {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }

            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int display_height = displaymetrics.heightPixels;
            int display_width = displaymetrics.widthPixels;

            int totalHeight = 0;

            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);

                TextView txt_Comment = (TextView) listItem.findViewById(R.id.txtComment);

                double perlinewords = (int) (display_width * 99) / 800;
                double noOfLines = (txt_Comment.getText().length()) / perlinewords;
                long iPart = (long) noOfLines;
                double fPart = noOfLines - iPart;
                int nol = (int) noOfLines;
                if (fPart > 0) {
                    nol++;
                }
                int commentHeight = nol * 25;

                totalHeight += commentHeight + 75;

            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            int finalHeight = totalHeight + 2 + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            params.height = finalHeight; // totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        } catch (Exception e) {
            Utility.saveExceptionDetails(GiveAwayRequestFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private class SelectDataTaskForApprove extends AsyncTask<String, Void, String> {
        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveAwayRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        //        ed_ManagerComment.getText().toString();
        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                String managerComment = args[0];
                SyncServiceHelper syncHelper = new SyncServiceHelper();
                String strMsg = syncHelper.sendGAApproval(mContext, shiftTradeId, selectedVolunteerId, managerComment, "0");

                if (strMsg.toLowerCase().equals("true")) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();
                    Synchronization syc = new Synchronization(mContext);
                    String result = syc.getInformation(mContext);
                    return (result != null) ? result : "false";
                } else {
                    return "false";
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(mContext, e);
                e.printStackTrace();
                return "false";
            }

        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (result != null && result.equalsIgnoreCase("true")) {
                    showToast(MessageInfo.strApprovedSuccessfully, Toast.LENGTH_SHORT);
                    if (ActivityStringInfo.strMsgArrListSize == 1) {
                        if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                            ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                            ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                        }
                    }
                    if(ActivityStringInfo.mTwoPane) {
                        ((MainActivity) mContext).messageListRefreshed();
                    }else{
                        ActivityStringInfo.shouldRefreshed =true;
                        GiveAwayRequestFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strApproveFailed, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveAwayRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    private class SelectDataTaskForDeny extends AsyncTask<String, Void, String> {
        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveAwayRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                String managerComment = args[0];
                SyncServiceHelper syncHelper = new SyncServiceHelper();
                String strMsg = syncHelper.denyGAApproval(mContext, shiftTradeId, "false", managerComment);
                if (strMsg.toLowerCase().equals("true")) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();
                    Synchronization syc = new Synchronization(mContext);
                    String result = syc.getInformation(mContext);
                    return (result != null) ? result : "false";
                } else {
                    return "false";
                }

            } catch (Exception e) {
                Utility.saveExceptionDetails(mContext, e);
                e.printStackTrace();
                return "false";
            }
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (result != null && result.equalsIgnoreCase("true")) {
                    showToast(MessageInfo.strDeniedSuccessfully, Toast.LENGTH_SHORT);
                    if (ActivityStringInfo.strMsgArrListSize == 1) {
                        if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                            ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                            ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                        }
                    }
                    if(ActivityStringInfo.mTwoPane) {
                        ((MainActivity) mContext).messageListRefreshed();
                    }else{
                        ActivityStringInfo.shouldRefreshed =true;
                        GiveAwayRequestFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strDenyFailed, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(mContext, e);
                e.printStackTrace();
            }
        }
    }

}
