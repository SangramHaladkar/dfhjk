package ism.manager.message;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.backgroundservices.ServiceForDocumentRead;
import ism.manager.baseclasses.AppBaseActivity;
import ism.manager.utils.AlertDialogManager;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;
import ism.manager.webservices.ServicesHelper;

public class MandatoryMessageDetailActivity extends AppBaseActivity {
    //for UI component
//    Button btn_Previous,btn_Next;
    ListView lst_Attachment;
    TextView txt_SubjectName, txt_From, txt_To, txt_MsgDateTime, txt_Message, txt_Attachments;
    ImageView img_MessageImage;
    LinearLayout mandatoryMsgButtons;
    Button btn_Status, btn_Done;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    ArrayList<HashMap<String, String>> fillMessageDetailAttachmentList;

    Uri notification;
    Ringtone ringtone;

    public static final String INFO = "INFO";
    HashMap<String, String> map = new HashMap<String, String>();
    String fileName = "", fileId = "";
    String strFileLink;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mandatory_message_detail);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(MandatoryMessageDetailActivity.this, R.color.appOrange)));
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            mContext = this;
            if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
                new SplashActivity().setValue();
            }
            initViews();
            setListeners();
            setMessageData();
            showAlertBox();
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
            e.printStackTrace();
        }
    }

    private void initViews() {
        lst_Attachment = (ListView) findViewById(R.id.lstAttachments);
        btn_Done = (Button) findViewById(R.id.btnDone);
        btn_Status = (Button) findViewById(R.id.btnStatus);
        txt_SubjectName = (TextView) findViewById(R.id.txtSubjectName);
        txt_From = (TextView) findViewById(R.id.txtFrom);
        txt_To = (TextView) findViewById(R.id.txtTo);
        txt_MsgDateTime = (TextView) findViewById(R.id.txtMsgDateTime);
        txt_Message = (TextView) findViewById(R.id.txtMessage);
        txt_Attachments = (TextView) findViewById(R.id.txtAttachments);
        img_MessageImage = (ImageView) findViewById(R.id.imgMessageImage);
        mandatoryMsgButtons = (LinearLayout) findViewById(R.id.mandatoryMsgButtons);
        mandatoryMsgButtons.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {

    }

    private void setListeners() {
        btn_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityStringInfo.shouldRefreshed = true;
                finish();
                MandatoryMessageDetailActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });
        btn_Status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectDataTaskForGetMandatoryMsgDet().execute();
            }
        });
    }

    private void setMessageData() {
        if (ActivityStringInfo.strMandatoryMessageId.size() > 0) {
            Iterator<String> it = ActivityStringInfo.strMandatoryMessageId.iterator();
            while (it.hasNext()) {
                stringInfo.strMessageId = "" + it.next();
                ActivityStringInfo.strMandatoryMessageId.remove(stringInfo.strMessageId);
                break;
            }
        }

        /** Get the Read mail**/
        Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageToUserList(stringInfo.strMessageId);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                if (c.getString(2).equals("")) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateMessageToUserRecords(stringInfo.strMessageId);
                }
            }
        }
        c.close();
        getMessageInfo();
        if (fillMessageDetailAttachmentList == null) {
            fillMessageDetailAttachmentList = new ArrayList<HashMap<String, String>>();
            bindAdapter();
        }
    }

    private void showAlertBox() {
        if (ActivityStringInfo.alertopen) {
            ringtone.play();
            getAlertDialogManager().showAlertDialog(MessageInfo.company_name, MessageInfo.strMMsgAlertText, MessageInfo.ok, null, false, new AlertDialogManager.OnCustomDialogClicklistenr() {
                @Override
                public void onPositiveClick() {
                    ringtone.stop();
                    ActivityStringInfo.alertopen = false;
                }

                @Override
                public void onNegativeClick() {

                }
            });
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
                Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
                e.printStackTrace();

            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.toLowerCase().equals("true")) {
                    Intent i = new Intent(MandatoryMessageDetailActivity.this, MandatoryMsgReadStatusActivity.class);
                    i.putExtra(INFO, stringInfo);
                    startActivity(i);
                    MandatoryMessageDetailActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
                } else {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
                e.printStackTrace();
            }
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
                    txt_MsgDateTime.setText(map.get(DatabaseConstant.key_MESSAGE_DATE));

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
            Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
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
            Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
            e.printStackTrace();
        }
    }

    public void fillAttachmentList() {
        try {
            lst_Attachment.setAdapter(new MyArrayAdapter(this, R.layout.list_message_attechments, fillMessageDetailAttachmentList));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_Attachment);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
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
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
                e.printStackTrace();
            }
            return v;
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
            Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
            writer.write(e.toString());
            e.printStackTrace();
        }
    }

    private class SelectDataTask extends AsyncTask<String, Void, String> {
        boolean error = false;

        // can use UI thread here
        protected void onPreExecute() {

            try {
                showTransparentProgressDialog(MessageInfo.loadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
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

                Utility.notification(MandatoryMessageDetailActivity.this, fileName);

                Cursor cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetailFileAttachmentListByFileId(stringInfo.strMessageId, fileId);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        if (cursor.getString(4).equals("")) {
                            MyDatabaseInstanceHolder.getDatabaseHelper().updateMessageAttachmentReadDate(stringInfo.strMessageId, fileId);
                            ActivityStringInfo.strMsgId = stringInfo.strMessageId;
                            ActivityStringInfo.strFileId = fileId;
                            startService(new Intent(MandatoryMessageDetailActivity.this, ServiceForDocumentRead.class));
                        }
                    }
                }
                cursor.close();
            } catch (Exception e) {
                error = true;
                Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
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
                Utility.saveExceptionDetails(MandatoryMessageDetailActivity.this, e);
                e.printStackTrace();
            }
        }
    }


}
