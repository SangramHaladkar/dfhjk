package ism.android.message;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import ism.android.ActivityStringInfo;
import ism.android.MainActivity;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.utils.StaticVariables;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageDetailsFragment extends AppBaseFragment {


    private LinearLayout attachmentLayout;
    private LinearLayout btnLayout;
    private TextView txtSubjectName;
    private TextView txtFrom;
    private TextView txtTo;
    private TextView txtMsgDateTime;
    private TextView txtMessage;
    private ListView lstAttachments;
    private ImageView imgMessageImage;
    private TextView btnReplyMsg;
    private TextView btnReplyAllMsg;
    private TextView btnForwardMsg;
    private TextView btnDeleteMsg;

    ActivityStringInfo stringInfo;

    ArrayList<HashMap<String, String>> fillMessageDetailAttachmentList;
    HashMap<String, String> map = new HashMap<String, String>();

    String fileName;
    String strFileLink;

    public static final String INFO = "INFO";

    Context mContext;

    public MessageDetailsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_message_details, container, false);
        this.initViews(view);
        this.setValues();
        this.setListeners();
        return view;
    }

    private void initViews(View v) {
        mContext = MessageDetailsFragment.this.getActivity();
        txtSubjectName = (TextView) v.findViewById(R.id.txtSubjectName);
        txtFrom = (TextView) v.findViewById(R.id.txtFrom);
        txtMsgDateTime = (TextView) v.findViewById(R.id.txtMsgDateTime);
        txtTo = (TextView) v.findViewById(R.id.txtTo);
        txtMessage = (TextView) v.findViewById(R.id.txtMessage);

        lstAttachments = (ListView) v.findViewById(R.id.lstAttachments);
        imgMessageImage = (ImageView) v.findViewById(R.id.imgMessageImage);

        btnLayout = (LinearLayout) v.findViewById(R.id.btnLayout);
        attachmentLayout = (LinearLayout) v.findViewById(R.id.attachmentLayout);
        attachmentLayout.setVisibility(View.GONE);
        btnReplyMsg = (TextView) v.findViewById(R.id.btnReplyMsg);
        btnReplyAllMsg = (TextView) v.findViewById(R.id.btnReplyAllMsg);
        btnForwardMsg = (TextView) v.findViewById(R.id.btnForwardMsg);
        btnDeleteMsg = (TextView) v.findViewById(R.id.btnDeleteMsg);
    }

    private void setValues() {
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
        this.getMessageInfo();

        if (fillMessageDetailAttachmentList == null) {
            fillMessageDetailAttachmentList = new ArrayList<HashMap<String, String>>();
            bindAdapter();
        }
    }

    private void setListeners() {
        btnReplyMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReply();
            }
        });
        btnReplyAllMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReplyAll();
            }
        });
        btnForwardMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForward();
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
                    showToast("Message deleted successfully.", Toast.LENGTH_SHORT);
                    if(ActivityStringInfo.mTwoPane) {
                        getMessageDetailsRefresh();
                    }else{
                        ActivityStringInfo.shouldRefreshed =true;
                        MessageDetailsFragment.this.getActivity().finish();
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });
    }

    public void getMessageDetailsRefresh() {
        new SelectDataTaskForRefreshMessageDetails().execute();
    }

    private void getMessageInfo() {
        try {
            /** Get the message list**/
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetail(stringInfo.strMessageId);
            if (c.getCount() > 0) {
                String strName = "";
                String strAllName = "";

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
                    //	Log.v("Message Details ", " " + c.getString(10));

                    txtSubjectName.setText(map.get(DatabaseConstant.key_SUBJECT));
                    txtFrom.setText(map.get(DatabaseConstant.key_FROM_USER_NAME));
                    StringTokenizer strUserName = new StringTokenizer(map.get(DatabaseConstant.key_REPLY_USER_NAME), ",");
                    //Log.v("Message Details ", " " + strUserName);
                    while (strUserName.hasMoreElements()) {
                        strName = strUserName.nextElement().toString();
                        //Log.v("Message Details ", " " + strName);
                        if (!strAllName.contains(strName)) {
                            strAllName += strName + ", ";
                        }
                    }
                    txtTo.setText(strAllName);
                    txtMessage.setText(map.get(DatabaseConstant.key_BODY));
                    txtMsgDateTime.setText(map.get(DatabaseConstant.key_MESSAGE_DATE));
//                    txt_Date.setText(map.get(DatabaseConstant.key_MESSAGE_DATE).substring(0,map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")));
//                    txt_Time.setText(map.get(DatabaseConstant.key_MESSAGE_DATE).substring(map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")+2));

                    if (map.get(DatabaseConstant.key_TYPE).equals("I"))
                        imgMessageImage.setBackgroundResource(R.drawable.ic_action_bulb);
                    else if (map.get(DatabaseConstant.key_TYPE).equals("M"))
                        imgMessageImage.setBackgroundResource(R.drawable.ic_action_meeting);
                    else if (map.get(DatabaseConstant.key_TYPE).equals("B"))
                        imgMessageImage.setBackgroundResource(R.drawable.ic_action_tea_break);
                    else
                        imgMessageImage.setBackgroundResource(R.drawable.ic_action_drafts);
                }
            }
            c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }


    public void bindAdapter() {
        try {
            /** Get the message Detail File Attachment List**/
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetailFileAttachmentList(stringInfo.strMessageId);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    System.out.println("count==" + c.getCount());
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(DatabaseConstant.key_MESSAGE_ID, c.getString(0));
                    map.put(DatabaseConstant.key_FILE_ID, c.getString(2));
                    map.put(DatabaseConstant.key_FILE_NAME, c.getString(3).replace("%20", " "));
                    map.put(DatabaseConstant.key_ATTACHMENT_LINK, c.getString(5));
                    fillMessageDetailAttachmentList.add(map);
                    if (!c.getString(3).equals("")) {
                        attachmentLayout.setVisibility(View.VISIBLE);
//                        img_PaperClip.setBackgroundResource(R.drawable.paperclip);
                    }
                }
            } else {
                attachmentLayout.setVisibility(View.GONE);
//                img_PaperClip.setBackgroundResource(0);
            }
            c.close();
            fillAttachmentList();
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillAttachmentList() {
        try {
            lstAttachments.setAdapter(new MyArrayAdapter(MessageDetailsFragment.this.getActivity(), R.layout.list_message_attechments, fillMessageDetailAttachmentList));
            Utility.setListViewHeightBasedOnChildren(MessageDetailsFragment.this.getActivity(), lstAttachments);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    /**
     * Display the message Detail Attachment List
     **/
    @SuppressWarnings("unchecked")
    private class MyArrayAdapter extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context mContext;

        public MyArrayAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            mContext = context;
            myData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.list_message_attechments, null);
                }
                final HashMap<String, String> hasValues = myData.get(position);

                if (hasValues != null) {
                    TextView txt_MessageAttachmentFileName = (TextView) v.findViewById(R.id.txtMessageAttachmentFileName);
                    String strFileName = hasValues.get(DatabaseConstant.key_FILE_NAME);
                    SpannableString content = new SpannableString(strFileName);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    txt_MessageAttachmentFileName.setText(content);

                    ImageView img_ItemIcon = (ImageView) v.findViewById(R.id.imgDocIcon);

                    if (strFileName.toUpperCase().contains(".DOC") || strFileName.toUpperCase().contains(".DOCX")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_doc);
                    } else if (strFileName.toUpperCase().contains(".JPG") || strFileName.toUpperCase().contains(".JPEG")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_jpg);
                    } else if (strFileName.toUpperCase().contains(".PNG")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_png);
                    } else if (strFileName.toUpperCase().contains(".PDF")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_pdf);
                    } else if (strFileName.toUpperCase().contains(".TXT")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_txt);
                    } else if (strFileName.toUpperCase().contains(".XLS")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_xls);
                    } else if (strFileName.toUpperCase().contains(".GIF")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_gif);
                    } else if (strFileName.toUpperCase().contains(".BMP")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_bmp);
                    } else if (strFileName.toUpperCase().contains(".FLV")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_flv);
                    } else if (strFileName.toUpperCase().contains(".HTML")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_html);
                    } else if (strFileName.toUpperCase().contains(".RTF")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_rtf);
                    } else if (strFileName.toUpperCase().contains(".RAR")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_rar);
                    } else if (strFileName.toUpperCase().contains(".ZIP")) {
                        img_ItemIcon.setImageResource(R.drawable.file_extension_zip);
                    } else {
                        img_ItemIcon.setImageResource(R.drawable.icon_file);
                    }

                    txt_MessageAttachmentFileName.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            stringInfo.strMessageId = hasValues.get(DatabaseConstant.key_MESSAGE_ID);
                            fileName = hasValues.get(DatabaseConstant.key_FILE_NAME);
                            strFileLink = hasValues.get(DatabaseConstant.key_ATTACHMENT_LINK);
                            try {
                                downloadFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

            return v;
        }
    }

    private class SelectDataTaskForDownloadAttachment extends AsyncTask<String, Void, String> {

        boolean error = false;

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.loadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                URL u = new URL(strFileLink);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                //c.setDoOutput(true);
                c.connect();
                String PATH = Environment.getExternalStorageDirectory() + "/download/StaffTAP/";

                Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getGlobalsRecords("download_path");
                if (c1.getCount() == 0) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertDownloadFilePath("download_path", PATH);
                }
                c1.close();

                File file = new File(PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File outputFile = new File(file, fileName);
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream instream = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = instream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                instream.close();

                Utility.notification(MessageDetailsFragment.this.getActivity(), fileName);

            } catch (Exception e) {
                error = true;
                Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
                e.printStackTrace();

            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (error) {
                    if (Utility.Connectivity_Internet(MessageDetailsFragment.this.getContext()))
                        showToast(MessageInfo.fileDownloadingFail, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                    error = false;
                } else {
                    showToast(MessageInfo.fileDownloadingSuccess, Toast.LENGTH_SHORT);

                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    //method to download selected file.
    public void downloadFile() throws IOException {
        File logFile;
        BufferedWriter writer = null;
        try {
            logFile = new File(Environment.getExternalStorageDirectory() + "/download/StaffTAP/", fileName + "_log.txt");
            writer = new BufferedWriter(new FileWriter(logFile));
            if (Utility.Connectivity_Internet(MessageDetailsFragment.this.getContext()))
                new SelectDataTaskForDownloadAttachment().execute();
            else
                showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            writer.write(e.toString());
            e.printStackTrace();
        }
    }


    public void getCompose() {
        try {
            Intent compose = new Intent(MessageDetailsFragment.this.getActivity(), ComposeActivity.class);
            compose.putExtra(INFO, stringInfo);
            startActivity(compose);
            MessageDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void getReply() {
        try {

            if (map.get(DatabaseConstant.key_TYPE).equals(StaticVariables.MEETING_MESSAGE)) {
                stringInfo.strCommentForWhich = "Meeting";
                ActivityStringInfo.strSubject = map.get(DatabaseConstant.key_SUBJECT);
                stringInfo.strMeetingId = map.get(DatabaseConstant.key_PROCESS_ID);
                Intent composeReply = new Intent(MessageDetailsFragment.this.getActivity(), ComposeActivity.class);
                composeReply.putExtra(INFO, stringInfo);
                startActivity(composeReply);
                MessageDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);

            } else if (map.get(DatabaseConstant.key_FROM_USER_ID).equals("0")) {
                fillGeneralForwardMessage();
            } else {
                fillGeneralReplyMessage();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillGeneralReplyMessage() {
        try {
            stringInfo.clear();
            ActivityStringInfo.strAddressUserId.add(map.get(DatabaseConstant.key_FROM_USER_ID) + "-0");
            //ActivityStringInfo.strCompositeUserId.add(map.get(DatabaseConstant.key_FROM_USER_ID) + ":" + map.get(DatabaseConstant.key_ORG_ID));
            stringInfo.strAddressBookName = txtFrom.getText().toString();

            String subject = map.get(DatabaseConstant.key_SUBJECT);

            String strForwardFormat = "\n\n\n---------------------------------------------\n\nFrom : " + map.get(DatabaseConstant.key_FROM_USER_NAME) +
                    "\nTo : " + txtTo.getText().toString() +
                    "\nDate : " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(0, map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")) +
                    ", " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",") + 2) +
                    "\nSubject : " + subject;

            ActivityStringInfo.strBody = strForwardFormat + "\n\n" + map.get(DatabaseConstant.key_BODY);
            ActivityStringInfo.strSendFor = "Reply";

            if (subject.substring(0, subject.indexOf(":") + 1).contains("Re:"))
                ActivityStringInfo.strSubject = subject;
            else
                ActivityStringInfo.strSubject = "Re: " + subject;

            Intent forwardIntent = new Intent(MessageDetailsFragment.this.getActivity(), ForwardActivity.class);
            forwardIntent.putExtra(INFO, stringInfo);
            startActivity(forwardIntent);
            MessageDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void getReplyAll() {
        try {
            if (map.get(DatabaseConstant.key_TYPE).equals(StaticVariables.MEETING_MESSAGE)) {
                stringInfo.strCommentForWhich = "Meeting";

                ActivityStringInfo.strSubject = map.get(DatabaseConstant.key_SUBJECT);
                stringInfo.strMeetingId = map.get(DatabaseConstant.key_PROCESS_ID);

                Intent composeMeeting = new Intent(MessageDetailsFragment.this.getActivity(), ComposeActivity.class);
                composeMeeting.putExtra(INFO, stringInfo);
                startActivity(composeMeeting);
                MessageDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            } else if (map.get(DatabaseConstant.key_FROM_USER_ID).equals("0")) {
                fillGeneralForwardMessage();
            } else {
                fillGeneralReplyAllMessage();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillGeneralReplyAllMessage() {
        try {
            stringInfo.clear();
            String strId = "";
            StringTokenizer strUserId = new StringTokenizer(map.get(DatabaseConstant.key_REPLY_USER_ID), ",");
            while (strUserId.hasMoreElements()) {
                strId = strUserId.nextElement().toString();
                if (!strId.contains("-0"))
                    ActivityStringInfo.strAddressUserId.add(strId.substring(strId.indexOf("-") + 1));
                else
                    ActivityStringInfo.strAddressUserId.add(strId);
            }
            ActivityStringInfo.strAddressUserId.add(map.get(DatabaseConstant.key_FROM_USER_ID) + "-0");
            System.out.println("storeCheckId==" + ActivityStringInfo.strAddressUserId);

            if (txtTo.getText().toString().contains(txtFrom.getText().toString()))
                stringInfo.strAddressBookName = txtTo.getText().toString();
            else
                stringInfo.strAddressBookName = txtTo.getText().toString() + txtFrom.getText().toString();

            String subject = map.get(DatabaseConstant.key_SUBJECT);

            String strForwardFormat = "\n\n\n---------------------------------------------\n\nFrom : " + map.get(DatabaseConstant.key_FROM_USER_NAME) +
                    "\nTo : " + txtTo.getText().toString() +
                    "\nDate : " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(0, map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")) +
                    ", " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",") + 2) +
                    "\nSubject : " + subject;
            ActivityStringInfo.strBody = strForwardFormat + "\n\n" + map.get(DatabaseConstant.key_BODY);
            ActivityStringInfo.strSendFor = "ReplyAll";
            if (subject.substring(0, subject.indexOf(":") + 1).contains("Re:"))
                ActivityStringInfo.strSubject = subject;
            else
                ActivityStringInfo.strSubject = "Re: " + subject;

            Intent forwardIntent = new Intent(MessageDetailsFragment.this.getActivity(), ForwardActivity.class);
            forwardIntent.putExtra(INFO, stringInfo);
            startActivity(forwardIntent);
            MessageDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void getForward() {
        try {
            fillGeneralForwardMessage();
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillGeneralForwardMessage() {
        try {
            stringInfo.clear();
            stringInfo.strAddressBookName = "";
            String subject = map.get(DatabaseConstant.key_SUBJECT);

            String strForwardFormat = "\n\n\n-------Forwarded Message-------\n\nFrom : " + map.get(DatabaseConstant.key_FROM_USER_NAME) +
                    "\nTo : " + txtTo.getText().toString() +
                    "\nDate : " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(0, map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")) +
                    ", " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",") + 2) +
                    "\nSubject : " + subject;
            ActivityStringInfo.strBody = strForwardFormat + "\n\n" + map.get(DatabaseConstant.key_BODY);
            ActivityStringInfo.strSendFor = "Forward";
            if (subject.substring(0, subject.indexOf(":") + 1).contains("Fw:"))
                ActivityStringInfo.strSubject = subject;
            else
                ActivityStringInfo.strSubject = "Fwd: " + subject;

            Intent forwardIntent = new Intent(MessageDetailsFragment.this.getActivity(), ForwardActivity.class);
            forwardIntent.putExtra(INFO, stringInfo);
            startActivity(forwardIntent);
            MessageDetailsFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private class SelectDataTaskForRefreshMessageDetails extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strRefresh);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
//                MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();  // Not in employy app code.
                Synchronization syc = new Synchronization(MessageDetailsFragment.this.getActivity());
                strMsg = syc.getInformation(MessageDetailsFragment.this.getActivity());
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();

                if (strMsg.equals("true")) {
                    //&& ActivityStringInfo.strMandatoryMessageId.size() == 0) {
                    stringInfo.clear();
                    ActivityStringInfo.selectedListItemIndex = 0;
                    ActivityStringInfo.isListItemSelected = false;
                    ((MainActivity) mContext).messageListRefreshed();
                } else if (strMsg != "true") {
                    if (Utility.Connectivity_Internet(MessageDetailsFragment.this.getActivity())) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessageDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }


}
