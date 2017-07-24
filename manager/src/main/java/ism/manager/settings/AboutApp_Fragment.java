package ism.manager.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ism.manager.ActivityStringInfo;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutApp_Fragment extends AppBaseFragment {


    private TextView txtLoginVer,txtCopyright;
    public AboutApp_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_about_app, container, false);
        try{
            this.txtCopyright =(TextView)view.findViewById(R.id.txtCopyright);
            this.txtLoginVer =(TextView)view.findViewById(R.id.txtLoginVer);

            this.txtCopyright.setText(ActivityStringInfo.COPYRIGHT);
            this.txtLoginVer.setText(ActivityStringInfo.VERSION);
        }catch(Exception e){
            Utility.saveExceptionDetails(getContext(),e);
            e.printStackTrace();
        }
        return view;
    }

}
