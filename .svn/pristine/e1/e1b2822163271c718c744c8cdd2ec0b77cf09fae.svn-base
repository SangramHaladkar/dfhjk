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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
public class MeetingDetailsFragment extends AppBaseFragment {
    private LinearLayout btnLayout;
    private TextView txtSubjectName;
    private TextView txtFrom;
    private TextView txtTo;
    private TextView txtMsgDateTime;
    private ImageView imgMessageImage;
    private TextView btnAccept;
    private TextView btnReply;
    private TextView btnDelete;

    private TextView txtMeeting;
    private TextView txtMandatory;
    private TextView txtMeetingStartTime;
    private TextView txtMeetingStartDate;
    private TextView txtMeetingEndTime;
    private TextView txtMeetingEndDate;
    private TextView txtLocation;
    private TextView txtOwner;


    ActivityStringInfo stringInfo;
    Context mContext;
    public static final String INFO = "INFO";


    public MeetingDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = MeetingDetailsFragment.this.getContext();
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
        View view = inflater.inflate(R.layout.fragment_meeting_details, container, false);
        try {
            this.initViews(view);
            this.fillMeetingDetails();
            this.setListeners();
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return view;
    }

    private void initViews(View v) {
        txtSubjectName = (TextView) v.findViewById(R.id.txtSubjectName);
        txtFrom = (TextView) v.findViewById(R.id.txtFrom);
        txtMsgDateTime = (TextView) v.findViewById(R.id.txtMsgDateTime);
        txtTo = (TextView) v.findViewById(R.id.txtTo);
        imgMessageImage = (ImageView) v.findViewById(R.id.imgMessageImage);

        btnLayout = (LinearLayout) v.findViewById(R.id.btnLayout);

        btnAccept = (TextView) v.findViewById(R.id.btnAccept);
        btnReply = (TextView) v.findViewById(R.id.btnReply);
        btnDelete = (TextView) v.findViewById(R.id.btnDelete);

        txtMeeting = (TextView) v.findViewById(R.id.txtMeeting);
        txtMandatory = (TextView) v.findViewById(R.id.txtMandatory);
        txtMeetingStartTime = (TextView) v.findViewById(R.id.txtMeetingStartTime);
        txtMeetingStartDate = (TextView) v.findViewById(R.id.txtMeetingStartDate);
        txtMeetingEndTime = (TextView) v.findViewById(R.id.txtMeetingEndTime);
        txtMeetingEndDate = (TextView) v.findViewById(R.id.txtMeetingEndDate);
        txtLocation = (TextView) v.findViewById(R.id.txtLocation);
        txtOwner = (TextView) v.findViewById(R.id.txtOwner);
    }

    private void setListeners() {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new SelectDataTaskForSendMeetingAccept().execute();
                } catch (Exception e) {
                    Utility.saveExceptionDetails(MeetingDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReply();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReply();
            }
        });
    }

    /**
     * FILLS THE MEETING DETAILS
     **/
    public void fillMeetingDetails() {
        Cursor c = null;
        Cursor c1 = null;
        try {
            c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetail(stringInfo.strMessageId);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    String subject = c.getString(1);
                    ActivityStringInfo.strSubject = subject;
                    txtSubjectName.setText(subject);
                    txtFrom.setText(c.getString(4));
                    txtTo.setText(c.getString(10));
                    String msgDateTime = c.getString(5);
                    txtMsgDateTime.setText(msgDateTime);
//                    txtDate.setText(msgDateTime.substring(0,msgDateTime.indexOf(",")));
//                    txtTime.setText(msgDateTime.substring(msgDateTime.indexOf(",")+2));

                    /** Get Meeting Information **/

                    stringInfo.strMeetingId = c.getString(8);
                    if (!c.getString(8).equals("")) {
                        c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMeetingDetail(c.getString(8));
                        if (c1.getCount() > 0) {
                            while (c1.moveToNext()) {
                                txtMeeting.setText(c1.getString(1));
                                txtOwner.setText(c1.getString(2));

                                String isMandatory = c1.getString(3).toLowerCase();
                                if (isMandatory.contains("y")) {
                                    txtMandatory.setText("Yes");
                                } else if (isMandatory.contains("n")) {
                                    txtMandatory.setText("No");
                                } else {
                                    txtMandatory.setText("");
                                }

                                String meetingDate = c1.getString(4);
                                String meetingStartTime = c1.getString(5);
                                String meetingEndTime = c1.getString(6);

                                SimpleDateFormat curDateformat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                                SimpleDateFormat newDateformat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

                                try {
                                    Date objDate = curDateformat.parse(meetingDate);
                                    meetingDate = newDateformat.format(objDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                txtMeetingStartDate.setText(meetingDate);
                                txtMeetingEndDate.setText(meetingDate);
                                txtMeetingStartTime.setText(meetingStartTime);
                                txtMeetingEndTime.setText(meetingEndTime);

                                txtLocation.setText(c1.getString(7));
                            }
                        }
                        c1.close();
                    }
                }
            }
            c.close();
        } catch (SQLException e) {
            Utility.saveExceptionDetails(MeetingDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            c.close();
            c1.close();
        }
    }

    public String getFormattedTime(String shiftTime) {
        String time = "";
        try {
            Pattern p = Pattern.compile(":");
            String[] item = p.split(shiftTime);
            time = "";
            for (int i = 0; i < 2; i++) {
                time += item[i];
                if (i == 0)
                    time += ":";
            }
            time += shiftTime.substring(shiftTime.indexOf(" "));
        } catch (Exception e) {
            Utility.saveExceptionDetails(MeetingDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }

        return time;
    }

    private class SelectDataTaskForSendMeetingAccept extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {

            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MeetingDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                SyncServiceHelper syncHelper = new SyncServiceHelper();
                strMsg = syncHelper.sendAcceptMeetingDetail(MeetingDetailsFragment.this.getActivity(), stringInfo.strMeetingId);

                if (strMsg.equals("true")) {
                    try {
//                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();  // Not in employy app code.
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                        if (ActivityStringInfo.strMsgArrListSize == 1) {
                            if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                                ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                                ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                            }
                        }
                        Synchronization syc = new Synchronization(mContext);
                        syc.getInformation(MeetingDetailsFragment.this.getActivity());
                        System.out.println("Synchroinzation done................");
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(MeetingDetailsFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                Utility.saveExceptionDetails(MeetingDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.equals("true")) {
                    showToast(MessageInfo.strMeetingAcceptedSuccessfully, Toast.LENGTH_SHORT);
                    if (ActivityStringInfo.mTwoPane) {
                        ((MainActivity) mContext).messageListRefreshed();
                    } else {
                        ActivityStringInfo.shouldRefreshed = true;
                        MeetingDetailsFragment.this.getActivity().finish();
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals("") && !strMsg.equals(MessageInfo.strObjectNull)) {
//                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();   // Not in employee app code.
                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                            showToast(strMsg, Toast.LENGTH_SHORT);
                            if (ActivityStringInfo.mTwoPane) {
                                ((MainActivity) mContext).messageListRefreshed();
                            } else {
                                ActivityStringInfo.shouldRefreshed = true;
                                MeetingDetailsFragment.this.getActivity().finish();
                            }
                        } else {
                            showToast(MessageInfo.strObjectNull, Toast.LENGTH_SHORT);
                        }
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (SQLException e) {
                Utility.saveExceptionDetails(MeetingDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    public void getReply() {
        try {
            stringInfo.strCommentForWhich = "Meeting";
//            ActivityStringInfo.previousActivity = MeetingDetailsFragment.class;
            if (ActivityStringInfo.strSubject == null || ActivityStringInfo.strSubject.length() == 0) {
                if (txtSubjectName != null)
                    ActivityStringInfo.strSubject = txtSubjectName.getText().toString();
            }
            Intent intent2 = new Intent(MeetingDetailsFragment.this.getActivity(), ComposeActivity.class);
            intent2.putExtra(INFO, stringInfo);
            startActivity(intent2);
            MeetingDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MeetingDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

}
