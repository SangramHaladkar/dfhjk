package ism.android.message;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.webservices.SyncServiceHelper;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class GiveToResponseFragment extends AppBaseFragment {

    //for UI component
    TextView txt_SubjectName, txt_From, txt_To, txt_MsgDateTime, txt_Message, btn_DeleteMsg;
    ImageView img_MessageImage;
    Button btn_Accept, btn_NoThanks;

    //Other Class Declaration
    ActivityStringInfo stringInfo;
    SyncServiceHelper serviceHelper;

    //Variable Declaration

    public static final String INFO = "INFO";
    HashMap<String, String> map = new HashMap<String, String>();
    Context mContext;
    private boolean submitPressed;

    public GiveToResponseFragment() {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_give_to_response, container, false);
        try {
            this.initView(view);
            this.getMessageInfo();
            this.setListeners();
        } catch (Exception e) {
            Utility.saveExceptionDetails(GiveToResponseFragment.this.getActivity(), e);
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
                    ((MessageDetailsActivity) mContext).back();
                }
            } else {
                if (submitPressed) {
                    // no need to try-catch this, because we are not in a callback
                    FragmentActivity activity = (FragmentActivity) mContext;
                    activity.getSupportFragmentManager().popBackStackImmediate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView(View v) {
        mContext = GiveToResponseFragment.this.getActivity();
        serviceHelper = new SyncServiceHelper();
        btn_Accept = (Button) v.findViewById(R.id.btnAccept);
        btn_NoThanks = (Button) v.findViewById(R.id.btnNoThank);
        btn_DeleteMsg = (TextView) v.findViewById(R.id.btnDeleteMsg);
        txt_SubjectName = (TextView) v.findViewById(R.id.txtSubjectName);
        txt_From = (TextView) v.findViewById(R.id.txtFrom);
        txt_To = (TextView) v.findViewById(R.id.txtTo);
        txt_MsgDateTime = (TextView) v.findViewById(R.id.txtMsgDateTime);
        txt_Message = (TextView) v.findViewById(R.id.txtMessage);
        img_MessageImage = (ImageView) v.findViewById(R.id.imgMessageImage);

        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
    }

    private void setListeners() {

        btn_DeleteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btn_NoThanks.performClick();
                } catch (Exception e) {
                    Utility.saveExceptionDetails(GiveToResponseFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });
        btn_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringInfo.strCommentForWhich = "GT";
                Intent intent2 = new Intent(GiveToResponseFragment.this.getActivity(), ShiftAcceptCommentActivity.class);
                intent2.putExtra(INFO, stringInfo);
                startActivity(intent2);
                GiveToResponseFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });
        btn_NoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SelectDataTaskForShiftGiveToNoThanks().execute();
                } catch (Exception e) {
                    Utility.saveExceptionDetails(GiveToResponseFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });

    }

    private void getMessageInfo() {
        try {
            /** Get the message list**/
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetail(stringInfo.strMessageId);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    map.put(DatabaseConstant.key_MESSAGE_ID, c.getString(0));
                    map.put(DatabaseConstant.key_SUBJECT, c.getString(1));
                    map.put(DatabaseConstant.key_BODY, c.getString(2));
                    map.put(DatabaseConstant.key_FROM_USER_ID, c.getString(3));
                    map.put(DatabaseConstant.key_FROM_USER_NAME, c.getString(4));
                    map.put(DatabaseConstant.key_MESSAGE_DATE, c.getString(5));
                    map.put(DatabaseConstant.key_TYPE, c.getString(6));
                    map.put(DatabaseConstant.key_SUB_TYPE, c.getString(7));
                    map.put(DatabaseConstant.key_PROCESS_ID, c.getString(8));
                    map.put(DatabaseConstant.key_REPLY_USER_ID, c.getString(9));
                    map.put(DatabaseConstant.key_REPLY_USER_NAME, c.getString(10));
                    txt_SubjectName.setText(map.get(DatabaseConstant.key_SUBJECT));
                    txt_From.setText(map.get(DatabaseConstant.key_FROM_USER_NAME));
                    txt_To.setText(map.get(DatabaseConstant.key_REPLY_USER_NAME));
                    txt_Message.setText(map.get(DatabaseConstant.key_BODY));
                    txt_MsgDateTime.setText(map.get(DatabaseConstant.key_MESSAGE_DATE));
                    stringInfo.strShiftId = map.get(DatabaseConstant.key_PROCESS_ID);
                    if (map.get(DatabaseConstant.key_TYPE).equals("I"))
                        img_MessageImage.setBackgroundResource(R.drawable.ic_action_bulb);
                    else if (map.get(DatabaseConstant.key_TYPE).equals("M"))
                        img_MessageImage.setBackgroundResource(R.drawable.ic_action_meeting);
                    else if (map.get(DatabaseConstant.key_TYPE).equals("S"))
                        img_MessageImage.setBackgroundResource(R.drawable.ic_action_exchange);
                    else
                        img_MessageImage.setBackgroundResource(R.drawable.ic_action_drafts);
                }
            }
            c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(GiveToResponseFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private class SelectDataTaskForShiftGiveToNoThanks extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveToResponseFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                strMsg = serviceHelper.sendShiftGiveToNoThanks(GiveToResponseFragment.this.getActivity(), map.get(DatabaseConstant.key_PROCESS_ID));
                if (strMsg.equals("true")) {
                    Synchronization syc = new Synchronization(mContext);
                    strMsg = syc.getInformation(GiveToResponseFragment.this.getActivity());
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(GiveToResponseFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (GiveToResponseFragment.this.getActivity() != null)
                GiveToResponseFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);


            if (strMsg.equals("true")) {
                MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                showToast("Sent successfully.", Toast.LENGTH_SHORT);
                if (ActivityStringInfo.strMsgArrListSize == 1) {
                    if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                        ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                        ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                    }
                }
                ActivityStringInfo.shouldRefreshed = true;
                if (ActivityStringInfo.mTwoPane) {
                    try {
                        submitPressed = true;
                        FragmentActivity activity = (FragmentActivity) mContext;
                        activity.getSupportFragmentManager().popBackStackImmediate();
                        Intent intent = new Intent("shouldrefresh");
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    } catch (IllegalStateException ignored) {
                        // There's no way to avoid getting this if saveInstanceState has already been called.
                    }
                } else {
//                    if (GiveToResponseFragment.this.getActivity() != null) {
//                        GiveToResponseFragment.this.getActivity().finish();
//                    }
                    ((MessageDetailsActivity) mContext).back();
                }
            } else if (!strMsg.equals("false")) {
                if (Utility.Connectivity_Internet(mContext)) {
                    if (!strMsg.equals(""))
                        showToast(strMsg, Toast.LENGTH_SHORT);
                } else
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("SUBMIT_PRESSED", submitPressed);
    }
}
