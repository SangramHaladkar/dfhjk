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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import ism.android.ActivityStringInfo;
import ism.android.MainActivity;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.backgroundservices.ServiceForDocumentRead;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.utils.StaticVariables;
import ism.android.webservices.ServicesHelper;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class MandatoryMsgDetailFragment extends AppBaseFragment {


    //for UI component
    TextView btn_Delete, btn_Reply, btn_ReplyAll, btn_Forward, btn_Status, btn_Done;
    TextView txt_SubjectName, txt_From, txt_Date;
    TextView txt_To, txt_Message, txt_Attachments;
    TextView txtMsgDateTime;

    ListView lst_Attachment;
    ImageView img_MessageImage;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;
    ArrayList<HashMap<String, String>> fillMessageDetailAttachmentList;
    HashMap<String, String> map = new HashMap<String, String>();
    String fileName = "", fileId = "";
    String strFileLink;

    public MandatoryMsgDetailFragment() {
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
        mContext = MandatoryMsgDetailFragment.this.getActivity();
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mandatory_msg_detail, container, false);
        try {
            this.initView(view);
            this.setListeners();
            this.getMessageInfo();
            if (fillMessageDetailAttachmentList == null) {
                fillMessageDetailAttachmentList = new ArrayList<HashMap<String, String>>();
                bindAdapter();
            }
            //TODO pending implementation
            //setButtonDisEna();
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
        return view;
    }

    private void initView(View v) {

        btn_Reply = (TextView) v.findViewById(R.id.btnReplyMsg);
        btn_ReplyAll = (TextView) v.findViewById(R.id.btnReplyAllMsg);
        btn_Forward = (TextView) v.findViewById(R.id.btnForwardMsg);
        btn_Delete = (TextView) v.findViewById(R.id.btnDeleteMsg);
        btn_Status = (TextView) v.findViewById(R.id.btnStatus);
        btn_Status.setVisibility(View.GONE);
        btn_Done = (TextView) v.findViewById(R.id.btnDone);
        btn_Done.setVisibility(View.GONE);


        txt_SubjectName = (TextView) v.findViewById(R.id.txtSubjectName);
        txt_From = (TextView) v.findViewById(R.id.txtFrom);
        txt_Date = (TextView) v.findViewById(R.id.txtDate);
        txtMsgDateTime = (TextView) v.findViewById(R.id.txtMsgDateTime);

        txt_To = (TextView) v.findViewById(R.id.txtTo);
        txt_Message = (TextView) v.findViewById(R.id.txtMessage);
        txt_Attachments = (TextView) v.findViewById(R.id.txtAttachments);

        img_MessageImage = (ImageView) v.findViewById(R.id.imgMessageImage);

        lst_Attachment = (ListView) v.findViewById(R.id.lstAttachments);

        btn_Reply.setVisibility(View.VISIBLE);
        btn_ReplyAll.setVisibility(View.VISIBLE);
        btn_Forward.setVisibility(View.VISIBLE);
    }

    private void setListeners() {

        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                    showToast("Message deleted successfully.", Toast.LENGTH_SHORT);
                    if (ActivityStringInfo.mTwoPane) {
                        getMMessageDetailsRefresh();
                    } else {
                        ActivityStringInfo.shouldRefreshed = true;
                        MandatoryMsgDetailFragment.this.getActivity().finish();
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });

//        btn_Status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new SelectDataTaskForGetMandatoryMsgDet().execute();
//            }
//        });

        btn_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityStringInfo.mTwoPane) {
                    ((MainActivity) mContext).messageListRefreshed();
                } else {
                    ActivityStringInfo.shouldRefreshed = true;
                    MandatoryMsgDetailFragment.this.getActivity().finish();
                }
            }
        });

        btn_Reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReply();
            }
        });

        btn_ReplyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReplyAll();
            }
        });

        btn_Forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForward();
            }
        });

    }

    public void getReply() {
        try {

            if (map.get(DatabaseConstant.key_TYPE).equals(StaticVariables.MEETING_MESSAGE)) {
                stringInfo.strCommentForWhich = "Meeting";
                ActivityStringInfo.strSubject = map.get(DatabaseConstant.key_SUBJECT);
                stringInfo.strMeetingId = map.get(DatabaseConstant.key_PROCESS_ID);
                Intent composeReply = new Intent(MandatoryMsgDetailFragment.this.getActivity(), ComposeActivity.class);
                composeReply.putExtra(INFO, stringInfo);
                startActivity(composeReply);
                MandatoryMsgDetailFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);

            } else if (map.get(DatabaseConstant.key_FROM_USER_ID).equals("0")) {
                fillGeneralForwardMessage();
            } else {
                fillGeneralReplyMessage();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillGeneralReplyMessage() {
        try {
            stringInfo.clear();
            ActivityStringInfo.strAddressUserId.add(map.get(DatabaseConstant.key_FROM_USER_ID) + "-0");
            //ActivityStringInfo.strCompositeUserId.add(map.get(DatabaseConstant.key_FROM_USER_ID) + ":" + map.get(DatabaseConstant.key_ORG_ID));
            stringInfo.strAddressBookName = txt_From.getText().toString();

            String subject = map.get(DatabaseConstant.key_SUBJECT);

            String strForwardFormat = "\n\n\n---------------------------------------------\n\nFrom : " + map.get(DatabaseConstant.key_FROM_USER_NAME) +
                    "\nTo : " + txt_To.getText().toString() +
                    "\nDate : " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(0, map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")) +
                    ", " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",") + 2) +
                    "\nSubject : " + subject;

            ActivityStringInfo.strBody = strForwardFormat + "\n\n" + map.get(DatabaseConstant.key_BODY);
            ActivityStringInfo.strSendFor = "Reply";

            if (subject.substring(0, subject.indexOf(":") + 1).contains("Re:"))
                ActivityStringInfo.strSubject = subject;
            else
                ActivityStringInfo.strSubject = "Re: " + subject;

            Intent forwardIntent = new Intent(MandatoryMsgDetailFragment.this.getActivity(), ForwardActivity.class);
            forwardIntent.putExtra(INFO, stringInfo);
            startActivity(forwardIntent);
            MandatoryMsgDetailFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void getReplyAll() {
        try {
            if (map.get(DatabaseConstant.key_TYPE).equals(StaticVariables.MEETING_MESSAGE)) {
                stringInfo.strCommentForWhich = "Meeting";

                ActivityStringInfo.strSubject = map.get(DatabaseConstant.key_SUBJECT);
                stringInfo.strMeetingId = map.get(DatabaseConstant.key_PROCESS_ID);

                Intent composeMeeting = new Intent(MandatoryMsgDetailFragment.this.getActivity(), ComposeActivity.class);
                composeMeeting.putExtra(INFO, stringInfo);
                startActivity(composeMeeting);
                MandatoryMsgDetailFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            } else if (map.get(DatabaseConstant.key_FROM_USER_ID).equals("0")) {
                fillGeneralForwardMessage();
            } else {
                fillGeneralReplyAllMessage();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
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

            if (txt_To.getText().toString().contains(txt_From.getText().toString()))
                stringInfo.strAddressBookName = txt_To.getText().toString();
            else
                stringInfo.strAddressBookName = txt_To.getText().toString() + txt_From.getText().toString();

            String subject = map.get(DatabaseConstant.key_SUBJECT);

            String strForwardFormat = "\n\n\n---------------------------------------------\n\nFrom : " + map.get(DatabaseConstant.key_FROM_USER_NAME) +
                    "\nTo : " + txt_To.getText().toString() +
                    "\nDate : " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(0, map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")) +
                    ", " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",") + 2) +
                    "\nSubject : " + subject;
            ActivityStringInfo.strBody = strForwardFormat + "\n\n" + map.get(DatabaseConstant.key_BODY);
            ActivityStringInfo.strSendFor = "ReplyAll";
            if (subject.substring(0, subject.indexOf(":") + 1).contains("Re:"))
                ActivityStringInfo.strSubject = subject;
            else
                ActivityStringInfo.strSubject = "Re: " + subject;

            Intent forwardIntent = new Intent(MandatoryMsgDetailFragment.this.getActivity(), ForwardActivity.class);
            forwardIntent.putExtra(INFO, stringInfo);
            startActivity(forwardIntent);
            MandatoryMsgDetailFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void getForward() {
        try {
            fillGeneralForwardMessage();
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillGeneralForwardMessage() {
        try {
            stringInfo.clear();
            stringInfo.strAddressBookName = "";
            String subject = map.get(DatabaseConstant.key_SUBJECT);

            String strForwardFormat = "\n\n\n-------Forwarded Message-------\n\nFrom : " + map.get(DatabaseConstant.key_FROM_USER_NAME) +
                    "\nTo : " + txt_To.getText().toString() +
                    "\nDate : " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(0, map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")) +
                    ", " + map.get(DatabaseConstant.key_MESSAGE_DATE).substring(map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",") + 2) +
                    "\nSubject : " + subject;
            ActivityStringInfo.strBody = strForwardFormat + "\n\n" + map.get(DatabaseConstant.key_BODY);
            ActivityStringInfo.strSendFor = "Forward";
            if (subject.substring(0, subject.indexOf(":") + 1).contains("Fw:"))
                ActivityStringInfo.strSubject = subject;
            else
                ActivityStringInfo.strSubject = "Fwd: " + subject;

            Intent forwardIntent = new Intent(MandatoryMsgDetailFragment.this.getActivity(), ForwardActivity.class);
            forwardIntent.putExtra(INFO, stringInfo);
            startActivity(forwardIntent);
            MandatoryMsgDetailFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void getMessageInfo() {
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

                    txt_SubjectName.setText(map.get(DatabaseConstant.key_SUBJECT));
                    txt_From.setText(map.get(DatabaseConstant.key_FROM_USER_NAME));
                    StringTokenizer strUserName = new StringTokenizer(map.get(DatabaseConstant.key_REPLY_USER_NAME), ",");

                    while (strUserName.hasMoreElements()) {
                        strName = strUserName.nextElement().toString();
                        if (!strAllName.contains(strName)) {
                            strAllName += strName + ", ";
                        }
                    }

                    txt_To.setText(strAllName);
                    txt_Message.setText(map.get(DatabaseConstant.key_BODY));
                    txtMsgDateTime.setText(map.get(DatabaseConstant.key_MESSAGE_DATE));

//                    txt_Date.setText(map.get(DatabaseConstant.key_MESSAGE_DATE).substring(0,map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")));
//                    txt_Time.setText(map.get(DatabaseConstant.key_MESSAGE_DATE).substring(map.get(DatabaseConstant.key_MESSAGE_DATE).indexOf(",")+2));

                    if (map.get(DatabaseConstant.key_TYPE).equals("I"))
                        img_MessageImage.setBackgroundResource(R.drawable.ic_action_idea_message);
                    else if (map.get(DatabaseConstant.key_TYPE).equals("M"))
                        img_MessageImage.setBackgroundResource(R.drawable.conference);
                    else if (map.get(DatabaseConstant.key_TYPE).equals("MM"))
                        img_MessageImage.setBackgroundResource(R.drawable.ic_action_pin);
                    else if (map.get(DatabaseConstant.key_TYPE).equals("B"))
                        img_MessageImage.setBackgroundResource(R.drawable.breaktime);
                    else
                        img_MessageImage.setBackgroundResource(R.drawable.ic_action_drafts);
                }
            }
            c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void bindAdapter() {
        try {
            /** Get the message Detail File Attachment List**/
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetailFileAttachmentList(stringInfo.strMessageId);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(DatabaseConstant.key_MESSAGE_ID, c.getString(0));
                    map.put(DatabaseConstant.key_FILE_ID, c.getString(2));
                    map.put(DatabaseConstant.key_FILE_NAME, c.getString(3).replace("%20", " "));
                    map.put(DatabaseConstant.key_ATTACHMENT_LINK, c.getString(5));
                    fillMessageDetailAttachmentList.add(map);
                    if (!c.getString(3).equals("")) {
                        txt_Attachments.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                txt_Attachments.setVisibility(View.GONE);
            }
            c.close();
            fillAttachmentList();
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillAttachmentList() {
        try {
            lst_Attachment.setAdapter(new MyArrayAdapter(MandatoryMsgDetailFragment.this.getActivity(), R.layout.list_message_attechments, fillMessageDetailAttachmentList));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_Attachment);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    /**
     * Display the message Detail Attachment List
     **/
    @SuppressWarnings("unchecked")
    private class MyArrayAdapter extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        public MyArrayAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            context = getContext();
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
                            fileId = hasValues.get(DatabaseConstant.key_FILE_ID);
                            strFileLink = hasValues.get(DatabaseConstant.key_ATTACHMENT_LINK);
                            try {
                                downloadFile();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return v;
        }
    }

    private class SelectDataTask extends AsyncTask<String, Void, String> {
        boolean error = false;

        // can use UI thread here
        protected void onPreExecute() {

            try {
                showTransparentProgressDialog(MessageInfo.loadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                URL u = new URL(strFileLink);
                HttpsURLConnection c = (HttpsURLConnection) u.openConnection();
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

                Utility.notification(MandatoryMsgDetailFragment.this.getActivity(), fileName);

                Cursor cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetailFileAttachmentListByFileId(stringInfo.strMessageId, fileId);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        if (cursor.getString(4).equals("")) {
                            MyDatabaseInstanceHolder.getDatabaseHelper().updateMessageAttachmentReadDate(stringInfo.strMessageId, fileId);
                            ActivityStringInfo.strMsgId = stringInfo.strMessageId;
                            ActivityStringInfo.strFileId = fileId;
                            mContext.startService(new Intent(MandatoryMsgDetailFragment.this.getActivity(), ServiceForDocumentRead.class));
                        }
                    }
                }
                cursor.close();
            } catch (Exception e) {
                error = true;
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
                e.printStackTrace();

            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (error) {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.fileDownloadingFail, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                    error = false;
                } else
                    showToast(MessageInfo.fileDownloadingSuccess, Toast.LENGTH_SHORT);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
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
            if (Utility.Connectivity_Internet(mContext))
                new SelectDataTask().execute();
            else
                showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
            writer.write(e.toString());
            e.printStackTrace();
        }
    }

    private class SelectDataTaskForGetMandatoryMsgDet extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.loadingProgress_txt);
            } catch (Exception e) {

            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                ServicesHelper servicesHelper = new ServicesHelper();
                strMsg = servicesHelper.getMandatoryMsgDet(mContext, stringInfo.strMessageId);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
                e.printStackTrace();

            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.toLowerCase().equals("true")) {
                    Intent i = new Intent(MandatoryMsgDetailFragment.this.getActivity(), MandatoryMsgReadStatusActivity.class);
                    i.putExtra(INFO, stringInfo);
                    startActivity(i);
                    MandatoryMsgDetailFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                } else {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    public void getMMessageDetailsRefresh() {
        new SelectDataTaskForRefreshMessageDetails().execute();
    }

    private class SelectDataTaskForRefreshMessageDetails extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strRefresh);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
//                MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();   // Not in employee app code.
                Synchronization syc = new Synchronization(MandatoryMsgDetailFragment.this.getActivity());
                strMsg = syc.getInformation(MandatoryMsgDetailFragment.this.getActivity());
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
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
                    if (Utility.Connectivity_Internet(MandatoryMsgDetailFragment.this.getActivity())) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMsgDetailFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

}
