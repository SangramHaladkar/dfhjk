package ism.manager.schedule;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ism.manager.ActivityStringInfo;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.StaticVariables;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestOffDetailsFragment extends AppBaseFragment {

    TextView txt_Day,txt_Date,txt_Status;
    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    String shiftId;
    String status;
    Context mContext;

    public RequestOffDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = RequestOffDetailsFragment.this.getActivity();
        if (getArguments().containsKey(INFO)) {
            stringInfo = (ActivityStringInfo) getArguments().getSerializable(INFO);
        }
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }
        shiftId = stringInfo.getSelectedId();
        status = stringInfo.getRequestOffStatus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_off_details, container, false);
        try{
            this.initViews(view);
            this.setValues();
        }catch (Exception e){
            Utility.saveExceptionDetails(RequestOffDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return view;
    }

    private void initViews(View v) {
        this.txt_Day = (TextView) v.findViewById(R.id.txtDay);
        this.txt_Date = (TextView) v.findViewById(R.id.txtDate);
        this.txt_Status =(TextView)v.findViewById(R.id.txtStatus);
    }

    private void setValues(){
        this.txt_Day.setText(stringInfo.getSelectedDay());
        this.txt_Date.setText(stringInfo.getSelectedDate());
        if (status.toLowerCase().equals("a")) {
            this.txt_Status.setText(StaticVariables.requestOffApproved);
        } else if (status.toLowerCase().equals("d")) {
            this.txt_Status.setText(StaticVariables.requestOffRejected);
        }else {
            this.txt_Status.setText(StaticVariables.approvalPending);
        }

    }

}
