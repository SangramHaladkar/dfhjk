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
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.MessageInfo;
import ism.android.webservices.SyncServiceHelper;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class TradeWithRequestFragment extends AppBaseFragment {

    //for UI component
    TextView btn_Compose, btn_Delete;
    Button btn_Deny, btn_Approve;

    LinearLayout lyl_Menu, lyl_WorkStation, lyl_WorkStationTr;
    LinearLayout lyl_SplitStartRequester, lyl_SplitEndRequester;
    LinearLayout lyl_SplitStartAccepter, lyl_SplitEndAccepter;

    TextView txt_Requester, txt_SkillLevelRequester, txt_ShiftStartTimeRequester, txt_ShiftStartDateRequester;
    TextView txt_ShiftEndTimeRequester, txt_ShiftEndDateRequester, txt_OvertimeRequester, txt_CommentRequester;
    TextView txt_SplitStartTimeRequester, txt_SplitStartDateRequester;
    TextView txt_SplitEndTimeRequester, txt_SplitEndDateRequester, txt_WorkStation, txt_WorkStationTr;

    TextView txt_Accepter, txt_SkillLevelAccepter, txt_ShiftStartTimeAccepter, txt_ShiftStartDateAccepter;
    TextView txt_ShiftEndTimeAccepter, txt_ShiftEndDateAccepter, txt_OvertimeAccepter, txt_CommentAccepter;
    TextView txt_SplitStartTimeAccepter, txt_SplitStartDateAccepter;
    TextView txt_SplitEndTimeAccepter, txt_SplitEndDateAccepter;
    TextView txt_MainBanner;

    EditText ed_ManagerComment;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;
    String shiftTradeId = "";

    public TradeWithRequestFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trade_with_request, container, false);
        mContext = TradeWithRequestFragment.this.getActivity();
        try {
            this.initView(view);
            this.setListener();
            this.fillTradeWithRequestDetails();
//            setButtonDisEna();
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View v) {
        lyl_WorkStation = (LinearLayout) v.findViewById(R.id.lylWorkStation);
        lyl_WorkStationTr = (LinearLayout) v.findViewById(R.id.lylWorkStationTr);

        btn_Compose = (TextView) v.findViewById(R.id.btnCompose);
        btn_Delete = (TextView) v.findViewById(R.id.btnDelete);


        btn_Deny = (Button) v.findViewById(R.id.btnDeny);
        btn_Approve = (Button) v.findViewById(R.id.btnApprove);

        ed_ManagerComment = (EditText) v.findViewById(R.id.edManagerComment);

        lyl_SplitStartRequester = (LinearLayout) v.findViewById(R.id.lylSplitStartRequester);
        lyl_SplitEndRequester = (LinearLayout) v.findViewById(R.id.lylSplitEndRequester);

        lyl_SplitStartAccepter = (LinearLayout) v.findViewById(R.id.lylSplitStartAccepter);
        lyl_SplitEndAccepter = (LinearLayout) v.findViewById(R.id.lylSplitEndAccepter);

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

        txt_ShiftStartTimeAccepter = (TextView) v.findViewById(R.id.txtShiftStartTimeAccepter);
        txt_ShiftStartDateAccepter = (TextView) v.findViewById(R.id.txtShiftStartDateAccepter);

        txt_ShiftEndTimeAccepter = (TextView) v.findViewById(R.id.txtShiftEndTimeAccepter);
        txt_ShiftEndDateAccepter = (TextView) v.findViewById(R.id.txtShiftEndDateAccepter);

        txt_SplitStartTimeAccepter = (TextView) v.findViewById(R.id.txtSplitStartTimeAccepter);
        txt_SplitStartDateAccepter = (TextView) v.findViewById(R.id.txtSplitStartDateAccepter);
        txt_SplitEndTimeAccepter = (TextView) v.findViewById(R.id.txtSplitEndTimeAccepter);
        txt_SplitEndDateAccepter = (TextView) v.findViewById(R.id.txtSplitEndDateAccepter);

        txt_OvertimeAccepter = (TextView) v.findViewById(R.id.txtOvertimeAccepter);
        txt_CommentAccepter = (TextView) v.findViewById(R.id.txtCommentAccepter);

        txt_WorkStation = (TextView) v.findViewById(R.id.txtWorkStation);
        txt_WorkStationTr = (TextView) v.findViewById(R.id.txtWorkStationTr);
    }


    private void setListener() {
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


        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectDataTaskForDeny().execute(ed_ManagerComment.getText().toString());
            }
        });

        btn_Compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, ComposeActivity.class);
                intent2.putExtra(INFO, stringInfo);
                startActivity(intent2);
                TradeWithRequestFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });
    }

    /**
     * FILLS THE Trade With Request DETAILS
     **/
    public void fillTradeWithRequestDetails() {
        Cursor cursor = null;
        try {
            int totalrecords = 0;

            cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getShiftDetail(shiftTradeId);   //  previous getTradeShiftDetail(shiftTradeId)

            totalrecords = cursor.getCount();
            if (totalrecords == 0) {
                showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            } else {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            txt_Requester.setText(cursor.getString(4));
                            txt_SkillLevelRequester.setText(cursor.getString(5));

                            String isovertime = cursor.getString(7).toLowerCase();
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

                            String splitStartDateTimeRequester = cursor.getString(10);
                            if (!splitStartDateTimeRequester.equals("")) {
                                String splitStartTime = splitStartDateTimeRequester.substring(splitStartDateTimeRequester.indexOf(" ") + 1);
                                String splitStartDate = splitStartDateTimeRequester.substring(0, splitStartDateTimeRequester.indexOf(" "));

                                txt_SplitStartTimeRequester.setText(getFormattedTime(splitStartTime));
                                txt_SplitStartDateRequester.setText(splitStartDate);

                                String splitEndDateTimeRequester = cursor.getString(11);
                                if (!splitEndDateTimeRequester.equals("")) {
                                    String splitEndTime = splitEndDateTimeRequester.substring(splitEndDateTimeRequester.indexOf(" ") + 1);
                                    String splitEndDate = splitEndDateTimeRequester.substring(0, splitEndDateTimeRequester.indexOf(" "));

                                    txt_SplitEndTimeRequester.setText(getFormattedTime(splitEndTime));
                                    txt_SplitEndDateRequester.setText(splitEndDate);
                                } else {
                                    txt_SplitEndTimeRequester.setText("");
                                    txt_SplitEndDateRequester.setText("");
                                }
                            } else {
                                lyl_SplitStartRequester.setVisibility(View.GONE);
                                lyl_SplitEndRequester.setVisibility(View.GONE);
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

                                txt_CommentRequester.setText("  " + cursor.getString(6));

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
                                lyl_WorkStation.setVisibility(View.VISIBLE);
                                txt_CommentRequester.setText("       " + cursor.getString(6));
                                txt_WorkStation.setText(cursor.getString(12));
                            }

                            txt_Accepter.setText(cursor.getString(14));
                            txt_SkillLevelAccepter.setText(cursor.getString(15));

                            String isovertimeAccepter = cursor.getString(17).toLowerCase();
                            if (isovertimeAccepter.contains("y")) {
                                txt_OvertimeAccepter.setText("Yes");
                            } else if (isovertimeAccepter.contains("n")) {
                                txt_OvertimeAccepter.setText("No");
                            } else {
                                txt_OvertimeAccepter.setText("");
                            }

                            String shiftStartDateTimeAccepter = cursor.getString(18);
                            String shiftStartTimeAccepter = shiftStartDateTimeAccepter.substring(shiftStartDateTimeAccepter.indexOf(" ") + 1);
                            String shiftStartDateAccepter = shiftStartDateTimeAccepter.substring(0, shiftStartDateTimeAccepter.indexOf(" "));

                            String shiftEndDateTimeAccepter = cursor.getString(19);
                            String shiftEndTimeAccepter = shiftEndDateTimeAccepter.substring(shiftEndDateTimeAccepter.indexOf(" ") + 1);
                            String shiftEndDateAccepter = shiftEndDateTimeAccepter.substring(0, shiftEndDateTimeAccepter.indexOf(" "));

                            txt_ShiftStartTimeAccepter.setText(getFormattedTime(shiftStartTimeAccepter));
                            txt_ShiftStartDateAccepter.setText(shiftStartDateAccepter);
                            txt_ShiftEndTimeAccepter.setText(getFormattedTime(shiftEndTimeAccepter));
                            txt_ShiftEndDateAccepter.setText(shiftEndDateAccepter);

                            String splitStartDateTimeAccepter = cursor.getString(20);
                            if (!splitStartDateTimeAccepter.equals("")) {
                                String splitStartTime = splitStartDateTimeAccepter.substring(splitStartDateTimeAccepter.indexOf(" ") + 1);
                                String splitStartDate = splitStartDateTimeAccepter.substring(0, splitStartDateTimeAccepter.indexOf(" "));

                                txt_SplitStartTimeAccepter.setText(getFormattedTime(splitStartTime));
                                txt_SplitStartDateAccepter.setText(splitStartDate);

                                String splitEndDateTimeAccepter = cursor.getString(21);
                                if (!splitEndDateTimeAccepter.equals("")) {
                                    String splitEndTime = splitEndDateTimeAccepter.substring(splitEndDateTimeAccepter.indexOf(" ") + 1);
                                    String splitEndDate = splitEndDateTimeAccepter.substring(0, splitEndDateTimeAccepter.indexOf(" "));

                                    txt_SplitEndTimeAccepter.setText(getFormattedTime(splitEndTime));
                                    txt_SplitEndDateAccepter.setText(splitEndDate);
                                } else {
                                    txt_SplitEndTimeAccepter.setText("");
                                    txt_SplitEndDateAccepter.setText("");
                                }
                            } else {
                                lyl_SplitStartAccepter.setVisibility(View.GONE);
                                lyl_SplitEndAccepter.setVisibility(View.GONE);
                            }

//                            TextView txt_Accepter = (TextView) findViewById(R.id.txtAccepterForWidth);
//                            TextView txt_CommentAccepter1 = (TextView) findViewById(R.id.txtCommentAccepterForWidth);
//                            TextView txt_OvertimeAccepter = (TextView) findViewById(R.id.txtOvertimeAccepterForWidth);
//                            TextView txt_ShiftEndTimeAccepter = (TextView) findViewById(R.id.txtShiftEndTimeAccepterForWidth);
//                            TextView txt_ShiftStartTimeAccepter = (TextView) findViewById(R.id.txtShiftStartTimeAccepterForWidth);
//                            TextView txt_SplitEndTimeAccepter = (TextView) findViewById(R.id.txtSplitEndTimeAccepterForWidth);
//                            TextView txt_SplitStartTimeAccepter = (TextView) findViewById(R.id.txtSplitStartTimeAccepterForWidth);
//                            TextView txt_WorkStationTr1 = (TextView) findViewById(R.id.txtWorkStationTrForWidth);

                            if (cursor.getString(22).equals("")) {
                                lyl_WorkStationTr.setVisibility(View.GONE);

//                                txt_Accepter.setWidth(110);
//                                txt_CommentAccepter1.setWidth(110);
//                                txt_OvertimeAccepter.setWidth(110);
//                                txt_ShiftEndTimeAccepter.setWidth(110);
//                                txt_ShiftStartTimeAccepter.setWidth(110);
//                                txt_SplitEndTimeAccepter.setWidth(110);
//                                txt_SplitStartTimeAccepter.setWidth(110);
//                                txt_WorkStationTr1.setWidth(110);

                                txt_CommentAccepter.setText("  " + cursor.getString(16));
                            } else {
//                                txt_Accepter.setWidth(135);
//                                txt_CommentAccepter1.setWidth(135);
//                                txt_OvertimeAccepter.setWidth(135);
//                                txt_ShiftEndTimeAccepter.setWidth(135);
//                                txt_ShiftStartTimeAccepter.setWidth(135);
//                                txt_SplitEndTimeAccepter.setWidth(135);
//                                txt_SplitStartTimeAccepter.setWidth(135);
//                                txt_WorkStationTr1.setWidth(135);
                                lyl_WorkStationTr.setVisibility(View.VISIBLE);
                                txt_CommentAccepter.setText("       " + cursor.getString(16));
                                txt_WorkStationTr.setText(cursor.getString(22));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        } catch (SQLException e) {
            Utility.saveExceptionDetails(TradeWithRequestFragment.this.getActivity(), e);
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
            Utility.saveExceptionDetails(TradeWithRequestFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return time;
    }

    private class SelectDataTaskForApprove extends AsyncTask<String, Void, String> {
        boolean responseResult = false;

        // can use UI thread here
        protected void onPreExecute() {
            try {

                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(TradeWithRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                String managerComment = args[0];
                SyncServiceHelper syncHelper = new SyncServiceHelper();
                String strMsg = syncHelper.sendTradeShiftApproval(TradeWithRequestFragment.this.getActivity(), shiftTradeId, managerComment);

                if (strMsg.toLowerCase().equals("true")) {
                    responseResult = true;

//                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();  // Not in employee app code.

                    Synchronization syc = new Synchronization(mContext);
                    syc.getInformation(TradeWithRequestFragment.this.getActivity());
                    return "true";
                } else {
                    responseResult = false;
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(TradeWithRequestFragment.this.getActivity(), e);
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
                    if (ActivityStringInfo.mTwoPane) {
                        ((MainActivity) mContext).messageListRefreshed();
                    } else {
                        ActivityStringInfo.shouldRefreshed = true;
                        if (TradeWithRequestFragment.this.getActivity() != null)
                            TradeWithRequestFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strApproveFailed, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(TradeWithRequestFragment.this.getActivity(), e);
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
                Utility.saveExceptionDetails(TradeWithRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                String managerComment = args[0];
                SyncServiceHelper syncHelper = new SyncServiceHelper();
                String strMsg = syncHelper.denyTradeShiftApproval(TradeWithRequestFragment.this.getActivity(), shiftTradeId, managerComment);
                if (strMsg.toLowerCase().equals("true")) {
                    responseResult = true;
//                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();   // not in employee app code

                    Synchronization syc = new Synchronization(mContext);
                    syc.getInformation(TradeWithRequestFragment.this.getActivity());
                } else {
                    responseResult = false;
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(TradeWithRequestFragment.this.getActivity(), e);
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
                    if (ActivityStringInfo.mTwoPane) {
                        ((MainActivity) mContext).messageListRefreshed();
                    } else {
                        ActivityStringInfo.shouldRefreshed = true;
                        if (TradeWithRequestFragment.this.getActivity() != null)
                            TradeWithRequestFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strDenyFailed, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(TradeWithRequestFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

}
