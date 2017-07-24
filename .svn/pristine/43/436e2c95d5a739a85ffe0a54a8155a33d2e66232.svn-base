package ism.manager.message;


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
import android.widget.TextView;
import android.widget.Toast;

import ism.manager.ActivityStringInfo;
import ism.manager.MainActivity;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.MessageInfo;
import ism.manager.webservices.SyncServiceHelper;
import ism.manager.webservices.Synchronization;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestOffFragment extends AppBaseFragment {

    //for UI component
    TextView btn_Compose, btn_Delete;
    TextView txt_Requester, txt_SkillLevelRequester, txt_Date, txt_CommentRequester;
    EditText ed_ManagerComment;
    Button btn_Deny, btn_Approve;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;
    String dayOffId = "";

    public RequestOffFragment() {
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
        mContext = RequestOffFragment.this.getActivity();
        dayOffId = stringInfo.strMessageId;
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_off, container, false);
        try {
            this.initView(view);
            this.setListeners();
            this.fillRequestOffDetails();
        } catch (Exception e) {
            Utility.saveExceptionDetails(RequestOffFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View v) {
        btn_Compose = (TextView) v.findViewById(R.id.btnCompose);
        btn_Delete = (TextView) v.findViewById(R.id.btnDelete);
        btn_Deny = (Button) v.findViewById(R.id.btnDeny);
        btn_Approve = (Button) v.findViewById(R.id.btnApprove);
        ed_ManagerComment = (EditText) v.findViewById(R.id.edManagerComment);
        txt_Requester = (TextView) v.findViewById(R.id.txtRequester);
        txt_SkillLevelRequester = (TextView) v.findViewById(R.id.txtSkillLevelRequester);
        txt_Date = (TextView) v.findViewById(R.id.txtDate);
        txt_CommentRequester = (TextView) v.findViewById(R.id.txtCommentRequester);
    }

    private void setListeners() {
        btn_Deny.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SelectDataTaskForDeny().execute(ed_ManagerComment.getText().toString());

            }
        });

        btn_Delete.setOnClickListener(new View.OnClickListener() {
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
                Intent intent2 = new Intent(RequestOffFragment.this.getActivity(), ComposeActivity.class);
                intent2.putExtra(INFO, stringInfo);
                startActivity(intent2);
                RequestOffFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });
    }

    /**
     * FILLS THE REQUEST OFF DETAILS
     **/
    private void fillRequestOffDetails() {
        Cursor cursor = null;

        try {
            int totalrecords = 0;

            cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getRequestOffDetail(dayOffId);

            totalrecords = cursor.getCount();
            if (totalrecords == 0) {
                showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            } else {
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            txt_Requester.setText(cursor.getString(2).replace(",", " "));
                            txt_SkillLevelRequester.setText(cursor.getString(3));

                            txt_CommentRequester.setText("  " + cursor.getString(5));

                            String requestOffDateTime = cursor.getString(6);

                            txt_Date.setText(requestOffDateTime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLException e) {
            Utility.saveExceptionDetails(RequestOffFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    //ed_ManagerComment.getText().toString()
    private class SelectDataTaskForApprove extends AsyncTask<String, Void, String> {
        boolean responseResult = false;

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                String managerComment = args[0];
                SyncServiceHelper syncHelper = new SyncServiceHelper();

                String strMsg = syncHelper.sendRequestOffApproval(RequestOffFragment.this.getActivity(), dayOffId, managerComment);

                if (strMsg.toLowerCase().equals("true")) {
                    responseResult = true;

                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();

                    Synchronization syc = new Synchronization(mContext);
                    syc.getInformation(RequestOffFragment.this.getActivity());
                    return "true";
                } else {
                    responseResult = false;
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffFragment.this.getActivity(), e);
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
                        RequestOffFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strApproveFailed, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffFragment.this.getActivity(), e);
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
                Utility.saveExceptionDetails(RequestOffFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                String managerComment = args[0];
                SyncServiceHelper syncHelper = new SyncServiceHelper();

                String strMsg = syncHelper.denyRequestOffApproval(RequestOffFragment.this.getActivity(), dayOffId, managerComment);

                if (strMsg.toLowerCase().equals("true")) {
                    responseResult = true;

                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();

                    Synchronization syc = new Synchronization(mContext);
                    syc.getInformation(RequestOffFragment.this.getActivity());
                } else {
                    responseResult = false;
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffFragment.this.getActivity(), e);
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
                        RequestOffFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strDenyFailed, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(RequestOffFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

}
