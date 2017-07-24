package ism.manager.settings;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ism.manager.ActivityStringInfo;
import ism.manager.MainActivity;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginDetailsFragment extends AppBaseFragment {

    private TextView txtLoginName;
    private TextView txtOrganization;
    private Button btnLogOut;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;

    public LoginDetailsFragment() {
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
        mContext = LoginDetailsFragment.this.getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_details, container, false);
        try {
            txtLoginName = (TextView) v.findViewById(R.id.txtLoginName);
            txtOrganization = (TextView) v.findViewById(R.id.txtOrganization);
            btnLogOut = (Button) v.findViewById(R.id.btnLogOut);
            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)LoginDetailsFragment.this.getActivity()).doLogOut();
                }
            });
            setLoginDetails();
        } catch (Exception e) {
            Utility.saveExceptionDetails(LoginDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return v;
    }


    private void setLoginDetails(){
        Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getUserRecord();
        try {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    txtLoginName.setText(c.getString(5));
                    txtOrganization.setText(c.getString(8));
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LoginDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            c.close();
        }
    }

}
