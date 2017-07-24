package ism.manager.schedule;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.StaticVariables;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleMeetingDetailFragment extends AppBaseFragment {

    //for UI component
    TextView txt_Meeting, txt_Mandatory, txt_MeetingStartTime;
    TextView txt_MeetingEndTime, txt_Location, txt_Owner, txt_Status;
    TextView txt_MeetingStartDate, txt_MeetingEndDate, txt_Day;

    //Other Class Declaration
    ActivityStringInfo stringInfo;
    Context mContext;

    //Variable Declaration
    public static final String INFO = "INFO";
    String meetingId = "";

    public ScheduleMeetingDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = ScheduleMeetingDetailFragment.this.getActivity();
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
        View view = inflater.inflate(R.layout.fragment_schedule_meeting_detail, container, false);
        try {
            this.initViews(view);
            this.fillMeetingDetails();
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return view;
    }

    private void initViews(View v) {
        meetingId = stringInfo.getSelectedId();
        txt_Day = (TextView) v.findViewById(R.id.txtDay);
        txt_Meeting = (TextView) v.findViewById(R.id.txtMeeting);
        txt_Mandatory = (TextView) v.findViewById(R.id.txtMandatory);
        txt_MeetingStartTime = (TextView) v.findViewById(R.id.txtMeetingStartTime);
        txt_MeetingStartDate = (TextView) v.findViewById(R.id.txtMeetingStartDate);
        txt_MeetingEndTime = (TextView) v.findViewById(R.id.txtMeetingEndTime);
        txt_MeetingEndDate = (TextView) v.findViewById(R.id.txtMeetingEndDate);
        txt_Location = (TextView) v.findViewById(R.id.txtLocation);
        txt_Owner = (TextView) v.findViewById(R.id.txtOwner);
        txt_Status = (TextView) v.findViewById(R.id.txtStatus);

    }

    /** FILLS THE MEETING DETAILS **/
    public void fillMeetingDetails()
    {
        Cursor cursor = null;
        try
        {
            int totalrecords=0;
            cursor  = MyDatabaseInstanceHolder.getDatabaseHelper().getMeetingDetail(meetingId);

            totalrecords = cursor.getCount();
            if(totalrecords==0)
            {
                showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            }
            else
            {
                if (cursor.moveToFirst())
                {
                    do
                    {
                        try
                        {
                            txt_Meeting.setText(cursor.getString(1));
                            txt_Owner.setText(cursor.getString(2));
                            if(cursor.getString(3).equals("Y"))
                                txt_Mandatory.setText("Yes");
                            else
                                txt_Mandatory.setText("No");

                            Date dateObj = StaticVariables.dbDateFormat.parse(cursor.getString(4));
                            String dateString = StaticVariables.generalDateFormat.format(dateObj);

                            txt_Day.setText(StaticVariables.getDayName(dateObj));
                            txt_MeetingStartDate.setText(dateString);
                            txt_MeetingEndDate.setText(dateString);
                            txt_MeetingStartTime.setText(cursor.getString(5));
                            txt_MeetingEndTime.setText(cursor.getString(6));
                            txt_Location.setText(cursor.getString(7));
                            txt_Status.setText(cursor.getString(8));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }while(cursor.moveToNext());
                }
            }
        }
        catch (Exception e)
        {
            Utility.saveExceptionDetails(ScheduleMeetingDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        finally
        {
            cursor.close();
        }
    }
}
