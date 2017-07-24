package ism.android.message;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import ism.android.ActivityStringInfo;
import ism.android.MainActivity;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.MessageInfo;
import ism.android.webservices.SyncServiceHelper;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class GiveToRequestFragment extends AppBaseFragment {

    //for UI component
    Button btn_Deny, btn_Approve;

    TextView txt_Requester, txt_SkillLevelRequester, txt_ShiftStartTimeRequester;
    TextView txt_ShiftStartDateRequester, txt_ShiftEndTimeRequester, txt_ShiftEndDateRequester;
    TextView txt_OvertimeRequester, txt_CommentRequester, txt_Accepter;
    TextView txt_SkillLevelAccepter, txt_OvertimeAccepter, txt_CommentAccepter;
    EditText ed_ManagerComment;
    TextView txt_SplitStartTimeRequester, txt_SplitStartDateRequester;
    TextView txt_SplitEndTimeRequester, txt_SplitEndDateRequester, txt_WorkStation;
    TextView btn_Compose, btn_Delete;

    LinearLayout lyl_SplitStart, lyl_SplitEnd, lyl_WorkStation;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;
    String shiftTradeId = "";
    String accepterId = "";

    public GiveToRequestFragment() {
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
        mContext = GiveToRequestFragment.this.getActivity();
        shiftTradeId = stringInfo.strMessageId;
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_give_to_request, container, false);
        try {
            this.initView(view);
            this.setListeners();
            this.fillGiveToRequesterDetails();
            //TODO Pending implementation
//        setButtonDisEna();
        } catch (Exception e) {
            Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View v) {

        btn_Compose = (TextView) v.findViewById(R.id.btnCompose);
        btn_Delete = (TextView) v.findViewById(R.id.btnDelete);
        btn_Approve = (Button) v.findViewById(R.id.btnApprove);
        btn_Deny = (Button) v.findViewById(R.id.btnDeny);


        ed_ManagerComment = (EditText) v.findViewById(R.id.edManagerComment);

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

        txt_Accepter = (TextView) v.findViewById(R.id.txtAccepter);
        txt_SkillLevelAccepter = (TextView) v.findViewById(R.id.txtSkillLevelAcceptor);

        txt_OvertimeAccepter = (TextView) v.findViewById(R.id.txtOvertimeAccepter);
        txt_CommentAccepter = (TextView) v.findViewById(R.id.txtCommentAccepter);

        txt_WorkStation = (TextView) v.findViewById(R.id.txtWorkStation);
        lyl_WorkStation = (LinearLayout) v.findViewById(R.id.lylWorkStation);
        lyl_SplitStart = (LinearLayout) v.findViewById(R.id.lylSplitStart);
        lyl_SplitEnd = (LinearLayout) v.findViewById(R.id.lylSplitEnd);
        lyl_SplitStart.setVisibility(View.VISIBLE);
        lyl_SplitEnd.setVisibility(View.VISIBLE);
    }

    private void setListeners() {

        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectDataTaskForDeny().execute(ed_ManagerComment.getText().toString());
            }
        });

        btn_Deny.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SelectDataTaskForDeny().execute(ed_ManagerComment.getText().toString());
            }
        });

        btn_Approve.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SelectDataTaskForApprove().execute(ed_ManagerComment.getText().toString());
            }
        });

        btn_Compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringInfo.clear();
                Intent intent2 = new Intent(GiveToRequestFragment.this.getActivity(), ComposeActivity.class);
                intent2.putExtra(INFO, stringInfo);
                startActivity(intent2);
                GiveToRequestFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });

    }

    /**
     * FILLS THE GIVE TO REQUESTER DETAILS
     **/
    private void fillGiveToRequesterDetails() {
        Cursor cursor = null;
        Cursor cursorAccepter = null;
        try {
            int totalrecords = 0;
            cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftDetail(shiftTradeId);

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

//                            TextView txt_CommentRequester1 			= (TextView)findViewById(R.id.txtCommentRequesterForWidth);
//                            TextView txt_OvertimeRequester 			= (TextView)findViewById(R.id.txtOvertimeRequesterForWidth);
//                            TextView txt_Requester 					= (TextView)findViewById(R.id.txtRequesterForWidth);
//                            TextView txt_ShiftEndTimeRequester 		= (TextView)findViewById(R.id.txtShiftEndTimeRequesterForWidth);
//                            TextView txt_ShiftStartTimeRequester 	= (TextView)findViewById(R.id.txtShiftStartTimeRequesterForWidth);
//                            TextView txt_SplitEndTimeRequester 		= (TextView)findViewById(R.id.txtSplitEndTimeRequesterForWidth);
//                            TextView txt_SplitStartTimeRequester 	= (TextView)findViewById(R.id.txtSplitStartTimeRequesterForWidth);
//                            TextView txt_WorkStation1 				= (TextView)findViewById(R.id.txtWorkStationForWidth);

                            if (cursor.getString(12).equals("")) {
                                lyl_WorkStation.setVisibility(View.GONE);

                                txt_CommentRequester.setText("  " + cursor.getString(7));

//                                txt_CommentRequester1.setWidth(110);
//                                txt_OvertimeRequester.setWidth(110);
//                                txt_Requester.setWidth(110);
//                                txt_ShiftEndTimeRequester.setWidth(110);
//                                txt_ShiftStartTimeRequester.setWidth(110);
//                                txt_SplitEndTimeRequester.setWidth(110);
//                                txt_SplitStartTimeRequester.setWidth(110);
//                                txt_WorkStation1.setWidth(110);
                            } else {

//                                txt_CommentRequester1.setWidth(135);
//                                txt_OvertimeRequester.setWidth(135);
//                                txt_Requester.setWidth(135);
//                                txt_ShiftEndTimeRequester.setWidth(135);
//                                txt_ShiftStartTimeRequester.setWidth(135);
//                                txt_SplitEndTimeRequester.setWidth(135);
//                                txt_SplitStartTimeRequester.setWidth(135);
//                                txt_WorkStation1.setWidth(135);

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

            cursorAccepter = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftDetail(shiftTradeId);
            totalrecords = cursorAccepter.getCount();
            if (totalrecords == 0) {
                txt_Accepter.setText("");
                txt_SkillLevelAccepter.setText("");
                txt_CommentAccepter.setText("");
                txt_OvertimeAccepter.setText("");
            } else {
                if (cursorAccepter.moveToFirst()) {
                    do {
                        try {
                            accepterId = cursorAccepter.getString(2);
                            System.out.println("Accepter ID :::: ...... @@@@ " + accepterId);
                            txt_Accepter.setText(cursorAccepter.getString(3));
                            txt_SkillLevelAccepter.setText(cursorAccepter.getString(4));

                            txt_CommentAccepter.setText("  " + cursorAccepter.getString(5));

                            String isovertime = cursorAccepter.getString(6).toLowerCase();
                            if (isovertime.contains("y")) {
                                txt_OvertimeAccepter.setText("Yes");
                            } else if (isovertime.contains("n")) {
                                txt_OvertimeAccepter.setText("No");
                            } else {
                                txt_OvertimeAccepter.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (cursorAccepter.moveToNext());
                }
            }
            cursorAccepter.close();
        } catch (SQLException e) {
            Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            cursor.close();
            cursorAccepter.close();
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
            Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
            e.printStackTrace();
        }

        return time;
    }

    //ed_ManagerComment.getText().toString();
    private class SelectDataTaskForApprove extends AsyncTask<String, Void, String> {
        boolean responseResult = false;

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                String managerComment = args[0];
                SyncServiceHelper syncHelper = new SyncServiceHelper();

                String strMsg = syncHelper.sendGAApproval(GiveToRequestFragment.this.getActivity(), shiftTradeId, accepterId, managerComment, "1");

                if (strMsg.toLowerCase().equals("true")) {
                    responseResult = true;

//                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();   // Not in employee app code.

                    Synchronization syc = new Synchronization(mContext);
                    syc.getInformation(GiveToRequestFragment.this.getActivity());
                    return "true";
                } else {
                    responseResult = false;
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (responseResult) {
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
                        GiveToRequestFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strApproveFailed, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    private class SelectDataTaskForDeny extends AsyncTask<String, Void, String> {
        boolean responseResult = false;

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                String managerComment = args[0];
                SyncServiceHelper syncHelper = new SyncServiceHelper();
                String strMsg = syncHelper.denyGAApproval(GiveToRequestFragment.this.getActivity(), shiftTradeId, "true", managerComment);
                if (strMsg.toLowerCase().equals("true")) {
                    responseResult = true;

//                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();   // Not in employee app code.

                    Synchronization syc = new Synchronization(mContext);
                    syc.getInformation(GiveToRequestFragment.this.getActivity());
                } else {
                    responseResult = false;
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (responseResult) {
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
                        GiveToRequestFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strDenyFailed, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveToRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

}
