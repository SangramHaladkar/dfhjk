package ism.android.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ism.android.ActivityStringInfo;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseActivity;
import ism.android.utils.DatabaseConstant;


public class MandatoryMsgReadStatusActivity extends AppBaseActivity {

    //for UI component
    TextView btnClose;
    TextView txt_SubjectName, txt_Date, txt_MMNotReadStatus;
    TextView txt_MainBanner;

    ListView lst_Attachment;
    ImageView img_MessageImage;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;
    HashMap<String, String> map = new HashMap<String, String>();
    String strNotReadUserList = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mandatory_msg_read_status);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            mContext = this;
            this.initView();
            this.setListeners();
            if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
                new SplashActivity().setValue();
            }
            this.fillAttachmentList();
            if (ActivityStringInfo.MandatoryMessageDetailNotReadList.size() > 0) {
                HashMap<String, String> hashNotReadList = new HashMap<String, String>();
                for (int j = 0; j < ActivityStringInfo.MandatoryMessageDetailNotReadList.size(); j++) {
                    hashNotReadList = ActivityStringInfo.MandatoryMessageDetailNotReadList.get(j);
                    strNotReadUserList += "&nbsp;&nbsp;" + hashNotReadList.get(DatabaseConstant.key_FIRST_NAME) + "  " + hashNotReadList.get(DatabaseConstant.key_LAST_NAME) + "<br/>";
                    txt_Date.setText(hashNotReadList.get(DatabaseConstant.key_MESSAGE_DATE));
                    txt_SubjectName.setText(hashNotReadList.get(DatabaseConstant.key_SUBJECT));
                }
            }

            if (strNotReadUserList.equals(""))
                txt_MMNotReadStatus.setVisibility(View.GONE);
            else
                txt_MMNotReadStatus.setText(Html.fromHtml(strNotReadUserList));
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgReadStatusActivity.this, e);
        }


    }

    private void initView() {
        btnClose = (TextView) findViewById(R.id.btnClose);
        lst_Attachment = (ListView) findViewById(R.id.lstAttachments);
        txt_SubjectName = (TextView) findViewById(R.id.txtSubjectName);
        txt_Date = (TextView) findViewById(R.id.txtDate);
        txt_MMNotReadStatus = (TextView) findViewById(R.id.txtMMNotReadStatus);
    }

    private void setListeners() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void fillAttachmentList() {
        try {
            lst_Attachment.setAdapter(new MyArrayAdapter(this, R.layout.list_item_message_mandatory_read_status, ActivityStringInfo.MandatoryMessageDetailList));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_Attachment);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgReadStatusActivity.this, e);
            e.printStackTrace();
        }
    }

    /**
     * Display the message Detail Attachment List
     **/
    @SuppressWarnings("unchecked")
    private class MyArrayAdapter extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context context;

        public MyArrayAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            myData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.list_item_message_mandatory_read_status, null);
                }
                final HashMap<String, String> hasValues = myData.get(position);

                if (hasValues != null) {
                    txt_SubjectName.setText(hasValues.get(DatabaseConstant.key_SUBJECT));
                    txt_Date.setText(hasValues.get(DatabaseConstant.key_MESSAGE_DATE));

                    TextView txt_Name = (TextView) v.findViewById(R.id.txtMMReadStatusName);
                    TextView txt_MessageAttachmentFileName = (TextView) v.findViewById(R.id.txtMMReadAttachment);
                    TextView txt_MessageAttachment = (TextView) v.findViewById(R.id.txtAttachment);
                    //LinearLayout lyl_MandatoryMessage = (LinearLayout)v.findViewById(R.id.lylMandatoryMessage);

                    txt_Name.setText(Html.fromHtml("<b>" + hasValues.get(DatabaseConstant.key_FIRST_NAME) + "  " + hasValues.get(DatabaseConstant.key_LAST_NAME) + "</b>&nbsp;&nbsp;&nbsp;<font color='#000000'> Read </font>: " + hasValues.get(DatabaseConstant.key_READ_DATE)));

                    String userId = hasValues.get(DatabaseConstant.key_USER_ID);
                    String strAttachmentList = "";

                    HashMap<String, String> hasAttachmentList = new HashMap<String, String>();
                    for (int i = 0; i < ActivityStringInfo.MandatoryMessageAttachmentList.size(); i++) {
                        hasAttachmentList = ActivityStringInfo.MandatoryMessageAttachmentList.get(i);
                        if (userId.equals(hasAttachmentList.get(DatabaseConstant.key_USER_ID))) {
                            if (hasAttachmentList.get(DatabaseConstant.key_READ_DATE).equals(""))
                                strAttachmentList += "&nbsp; <font color='#000000'>-</font> " + hasAttachmentList.get(DatabaseConstant.key_FILE_NAME).replace("%20", " ") + " :<font color='#000000'> Not Read </font><br/>";
                            else
                                strAttachmentList += "&nbsp; <font color='#000000'>-</font> " + hasAttachmentList.get(DatabaseConstant.key_FILE_NAME).replace("%20", " ") + " :<font color='#000000'> Read</font> " + hasAttachmentList.get(DatabaseConstant.key_READ_DATE) + "<br/>";
                        }
                    }
                    if (strAttachmentList.equals("")) {
                        txt_MessageAttachmentFileName.setVisibility(View.GONE);
                        txt_MessageAttachment.setVisibility(View.GONE);
                    } else
                        txt_MessageAttachmentFileName.setText(Html.fromHtml(strAttachmentList));
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgReadStatusActivity.this, e);
                e.printStackTrace();
            }
            return v;
        }
    }
}
