package ism.android.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import ism.android.ActivityStringInfo;
import ism.android.R;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseActivity;


public class AboutApp_Activity extends AppBaseActivity {

    private TextView txtLoginVer, txtCopyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        try {
            this.txtCopyright = (TextView) findViewById(R.id.txtCopyright);
            this.txtLoginVer = (TextView) findViewById(R.id.txtLoginVer);

            this.txtCopyright.setText(ActivityStringInfo.COPYRIGHT);
            this.txtLoginVer.setText(ActivityStringInfo.VERSION);
        } catch (Exception e) {
            Utility.saveExceptionDetails(this, e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
