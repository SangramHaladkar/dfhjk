package ism.manager.message;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ism.manager.ActivityStringInfo;
import ism.manager.MainActivity;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;
import ism.manager.baseclasses.AppBaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiveAwayResponseFragment extends AppBaseFragment {

    //for UI component
    private TextView txtSubjectName;
    private TextView txtFrom;
    private TextView txtTo;
    private TextView txtMsgDateTime;
    private TextView txtMessage;
    private ImageView imgMessageImage;
    private TextView btnDeleteMsg;
    private Button btn_Volunteer_Accept;

    ActivityStringInfo stringInfo;

    public static final String INFO = "INFO";

    Context mContext;

    public GiveAwayResponseFragment() {
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
        View view = inflater.inflate(R.layout.fragment_give_away_response, container, false);
        try {
            this.initViews(view);
            this.setValues();
            this.setListeners();
        } catch (Exception e) {
            Utility.saveExceptionDetails(GiveAwayResponseFragment.this.getActivity(), e);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ActivityStringInfo.mTwoPane){

        }else{
            if(ActivityStringInfo.shouldRefreshed){
                GiveAwayResponseFragment.this.getActivity().finish();
            }
        }
    }

    private void initViews(View v) {
        mContext = GiveAwayResponseFragment.this.getActivity();
        txtSubjectName = (TextView) v.findViewById(R.id.txtSubjectName);
        txtFrom = (TextView) v.findViewById(R.id.txtFrom);
        txtMsgDateTime = (TextView) v.findViewById(R.id.txtMsgDateTime);
        txtTo = (TextView) v.findViewById(R.id.txtTo);
        txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        imgMessageImage = (ImageView) v.findViewById(R.id.imgMessageImage);
        btnDeleteMsg = (TextView) v.findViewById(R.id.btnDeleteMsg);
        btn_Volunteer_Accept = (Button) v.findViewById(R.id.btnVolunteer_Accept);
    }

    private void setValues() {
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
        this.getMessageInfo();
    }

    private void setListeners() {

        btn_Volunteer_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringInfo.strCommentForWhich = "GA";
                Intent intent2 = new Intent(mContext, ShiftAcceptCommentActivity.class);
                intent2.putExtra(INFO, stringInfo);
                startActivity(intent2);
                GiveAwayResponseFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });

        btnDeleteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                    if (ActivityStringInfo.strMsgArrListSize == 1) {
                        if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                            ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                            ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                        }
                    }
                    showToast("Message Deleted Successfully.", Toast.LENGTH_SHORT);
                    if (ActivityStringInfo.mTwoPane) {
                        ((MainActivity) mContext).messageListRefreshed();
                    } else {
                        ActivityStringInfo.shouldRefreshed = true;
                        GiveAwayResponseFragment.this.getActivity().finish();
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(GiveAwayResponseFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });
    }

    public void getMessageInfo() {
        try {
            /** Get the message list**/
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetail(stringInfo.strMessageId);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {

                    txtSubjectName.setText(c.getString(1));
                    txtMessage.setText(c.getString(2));
                    txtFrom.setText(c.getString(4));
                    txtTo.setText(c.getString(10));
                    txtMsgDateTime.setText(c.getString(5));
                    ActivityStringInfo.strSubject = c.getString(1);
                    stringInfo.strShiftId = c.getString(8);

                    if (c.getString(6).equals("I"))
                        imgMessageImage.setBackgroundResource(R.drawable.ic_action_bulb);
                    else if (c.getString(6).equals("M"))
                        imgMessageImage.setBackgroundResource(R.drawable.ic_action_meeting);
                    else if (c.getString(6).equals("S"))
                        imgMessageImage.setBackgroundResource(R.drawable.ic_action_exchange);
                    else
                        imgMessageImage.setBackgroundResource(R.drawable.ic_action_drafts);
                }
            }
            c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(GiveAwayResponseFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }


}
