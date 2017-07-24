package ism.android.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseActivity;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.webservices.SyncServiceHelper;
import ism.android.webservices.Synchronization;
import okhttp3.MediaType;

public class ComposeActivity extends AppBaseActivity {

    private LinearLayout attachmentLayout;
    private LinearLayout toTextLayout;

    private EditText edSubject;
    private TextView txtFrom;
    private TextView txtTo;
    private TextView txtMsgDateTime;
    private TextView txtAttachments;
    private TextView sndTxtAlertLabel;
    private EditText edMessage;
    private ListView lstAttachments;
    private ImageView imgMessageImage;

    private RadioButton radioTextAlertButton;
    //alert Radio Group
    private RadioGroup alertTextRadioGroup;
    private RadioButton textYesRadioButton;
    private RadioButton textNoRadioButton;
    public static boolean flagTextAlert;
    public boolean isAddressBookSelected = false;
    public static String flagTextAlertString = "false";

    //Other Class Declaration
    ActivityStringInfo stringInfo;
    SyncServiceHelper syncServiceHelper;

    //Variable Declaration
    public static final String INFO = "INFO";
    private Context mContext;

    public static int IMAGE_REQUEST_CODE = 0;

    String strImagePath = "";
    String filename = "";

    SimpleAdapter adapter_AddPicture;

    public static boolean isAttachment = false;

    MenuItem pictureMenu;

    String strReqURL = ActivityStringInfo.wsLocation + "/ImageUpload.aspx";

    final MediaType XML = MediaType.parse("application/xml; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_compose);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            this.initViews();
            this.setValues();
            this.getAddressFromBook();
            this.attachImage();
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_composemail, menu);
        pictureMenu = menu.findItem(R.id.action_picture);
        if (pictureMenu != null) {
            if (stringInfo.strCommentForWhich.equals("Meeting")) {
                pictureMenu.setVisible(false);
            } else if (stringInfo.strCommentForWhich.equals("Mandatory")) {
                pictureMenu.setVisible(true);
                pictureMenu.setTitle(R.string.attach);
                pictureMenu.setIcon(R.drawable.ic_action_attachment_white);
            } else {
                pictureMenu.setVisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_picture:
                this.getPicture();
                break;
            case R.id.action_send:
                this.sendMessage();
                break;
            case R.id.action_inbox:
                onBackPressed();
                break;
            case R.id.action_clear:
                this.clearAll();
                break;
            case R.id.action_cancel:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ComposeActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
//        ActivityStringInfo.strCompositeUserId.clear();
//        stringInfo.clear();
//        deleteImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isAddressBookSelected) {
            ActivityStringInfo.strBody = "";
            ActivityStringInfo.strSubject = "";
            ActivityStringInfo.strCompositeUserId.clear();
            stringInfo.clear();
            deleteImage();
        }
    }

    /**
     * clear all fields
     */
    private void clearAll() {
        this.edMessage.setText("");
        this.edSubject.setText("");
        this.txtTo.setText("");
    }

    /**
     * clear all attachments
     */
    private void clearAttachments() {
        int count = lstAttachments.getCount();
        for (int i = 0; i < count; i++) {
            Utility.deleteRow_Picture(0, mContext, adapter_AddPicture, lstAttachments);
        }

    }

    /**
     * send messages to receiver
     */
    private void sendMessage() {
        try {
            if (validation()) {
                //removed text alert
                if (stringInfo.strCommentForWhich.equals("Meeting")) {
                    new SelectDataTaskForSendMeetingReply().execute(edMessage.getText().toString().trim());
                } else {
                    new SelectDataTaskForComposeMail().execute(edMessage.getText().toString().trim(), edSubject.getText().toString().trim());
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    /**
     * take picture for attachements
     */
    private void getPicture() {
        try {
            if (stringInfo.strCommentForWhich.equals("Mandatory")) {
                stringInfo.strAddressBookName = "";
                ActivityStringInfo.previousActivityNew = ComposeActivity.class;
                ActivityStringInfo.strBody = edMessage.getText().toString();
                ActivityStringInfo.strSubject = edSubject.getText().toString();
                isAddressBookSelected = true;
                Intent intent2 = new Intent(getApplicationContext(), MandatoryMsgAttachmentActivity.class);
                intent2.putExtra(INFO, stringInfo);
                startActivity(intent2);
                finish();
                ComposeActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            } else {
                if (Utility.checkReadExternalStorage(mContext) || Utility.checkWriteExternalStorage(mContext)) {
                    Utility.OpenCreateFile(mContext);
                    startCameraActivity();
                } else {
                    String msg = Utility.requestReadWriteExternalStoragePermission(mContext);
                    showToast(msg, Toast.LENGTH_SHORT);
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MessageInfo.external_storage_permission_request_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utility.OpenCreateFile(mContext);
                    startCameraActivity();
                } else {
                    String msg = Utility.requestReadWriteExternalStoragePermission(mContext);
                    showToast(msg, Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    private void initViews() {
        this.mContext = ComposeActivity.this;
        this.syncServiceHelper = new SyncServiceHelper();
        this.attachmentLayout = (LinearLayout) findViewById(R.id.attachmentLayout);
        this.toTextLayout = (LinearLayout) findViewById(R.id.toTxt_layout);
//        this.switchMM = (SwitchCompat) findViewById(R.id.switchMM);
        this.lstAttachments = (ListView) findViewById(R.id.lstAttachments);
        this.txtTo = (TextView) findViewById(R.id.txtTo);
        this.sndTxtAlertLabel = (TextView) findViewById(R.id.sndTxtAlert);
        this.txtAttachments = (TextView) findViewById(R.id.txtAttachments);
        this.txtAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

        this.edSubject = (EditText) findViewById(R.id.edSubject);
        this.edMessage = (EditText) findViewById(R.id.edMessage);
        this.imgMessageImage = (ImageView) findViewById(R.id.imgMessageImage);
        this.toTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringInfo.strAddressBookName = "";
                ActivityStringInfo.previousActivityNew = ComposeActivity.class;
                ActivityStringInfo.strBody = edMessage.getText().toString();
                ActivityStringInfo.strSubject = edSubject.getText().toString();
                isAddressBookSelected = true;
                Intent intent1 = new Intent(getApplicationContext(), AddressBookActivity.class);
                intent1.putExtra(INFO, stringInfo);
                startActivity(intent1);
                finish();
                ComposeActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });
        this.txtTo.setNextFocusDownId(R.id.edSubject);
        this.edSubject.setNextFocusDownId(R.id.edMessage);
        this.edMessage.requestFocus();
        this.alertTextRadioGroup = (RadioGroup) findViewById(R.id.alertRadioGroup);
        this.textYesRadioButton = (RadioButton) findViewById(R.id.textYesRadio);
        this.textNoRadioButton = (RadioButton) findViewById(R.id.textNoRadio);
        this.textNoRadioButton.setChecked(true);
//        this.switchMM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    switchMM.setTextColor(ContextCompat.getColor(ComposeActivity.this, R.color.appOrange));
//                    imgMessageImage.setVisibility(View.VISIBLE);
//                    sndTxtAlertLabel.setVisibility(View.GONE);
//                    alertTextRadioGroup.setVisibility(View.GONE);
//                    textNoRadioButton.setChecked(true);
//                    stringInfo.strCommentForWhich = "Mandatory";
//                    if (pictureMenu != null) {
//                        pictureMenu.setTitle(R.string.attach);
//                        pictureMenu.setIcon(R.drawable.ic_action_attachment_white);
//                    }
//                    edMessage.setHint("Type Mandatory Message Here...");
//                    edMessage.setHintTextColor(ContextCompat.getColor(ComposeActivity.this, R.color.appOrange));
//                } else {
//                    switchMM.setTextColor(ContextCompat.getColor(ComposeActivity.this, R.color.unselected));
//                    imgMessageImage.setVisibility(View.GONE);
//                    sndTxtAlertLabel.setVisibility(View.VISIBLE);
//                    alertTextRadioGroup.setVisibility(View.VISIBLE);
//                    textNoRadioButton.setChecked(true);
//                    stringInfo.strCommentForWhich = "";
//                    if (pictureMenu != null) {
//                        pictureMenu.setTitle(R.string.action_picture);
//                        pictureMenu.setIcon(R.drawable.ic_action_camera);
//                    }
//                    edMessage.setHint("Type Message Here...");
//                    edMessage.setHintTextColor(ContextCompat.getColor(ComposeActivity.this, R.color.textcolor));
//                }
//                setActionBarForMM(isChecked);
//                clearAttachments();
//            }
//        });
    }

    private void setActionBarForMM(boolean isMM) {
        ActionBar actionBar = getSupportActionBar();
        if (isMM) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(ComposeActivity.this, R.color.appOrange)));
        } else {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(ComposeActivity.this, R.color.colorPrimary)));
        }
    }

    private void setValues() {
        if (stringInfo.strCommentForWhich.equals("Meeting")) {
            txtTo.setText("Managers");
            toTextLayout.setClickable(false);
            toTextLayout.setEnabled(false);
            edSubject.setText(ActivityStringInfo.strSubject);
            imgMessageImage.setVisibility(View.GONE);
            attachmentLayout.setVisibility(View.GONE);
            sndTxtAlertLabel.setVisibility(View.VISIBLE);
            alertTextRadioGroup.setVisibility(View.VISIBLE);
//            switchMM.setVisibility(View.GONE);
        } else if (stringInfo.strCommentForWhich.equals("Mandatory")) {
            imgMessageImage.setVisibility(View.VISIBLE);
            sndTxtAlertLabel.setVisibility(View.GONE);
            alertTextRadioGroup.setVisibility(View.GONE);
            edMessage.setHint("Type Mandatory Message Here...");
            edMessage.setHintTextColor(ContextCompat.getColor(ComposeActivity.this, R.color.appOrange));
//            switchMM.setVisibility(View.VISIBLE);
//            switchMM.setChecked(true);
        } else {
            txtTo.setText("");
            toTextLayout.setClickable(true);
            toTextLayout.setEnabled(true);
            edSubject.setText(ActivityStringInfo.strSubject);
            imgMessageImage.setVisibility(View.GONE);
//            switchMM.setVisibility(View.VISIBLE);
            attachmentLayout.setVisibility(View.VISIBLE);
            sndTxtAlertLabel.setVisibility(View.VISIBLE);
            alertTextRadioGroup.setVisibility(View.VISIBLE);
        }

        edSubject.requestFocus();
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }

        if (!ActivityStringInfo.strMandatoryMsgRight.toLowerCase().equals("y")) {
//            switchMM.setVisibility(View.GONE);
            imgMessageImage.setVisibility(View.GONE);
        }
    }

    /***
     * Get the list of address from the address boo
     */
    public void getAddressFromBook() {
        try {
            if (ActivityStringInfo.strCompositeUserId.size() > 0 && ActivityStringInfo.strCompositeUserId != null) {

                Iterator<String> itComposite = ActivityStringInfo.strCompositeUserId.iterator();
                int count = 1;
                while (itComposite.hasNext()) {

                    String compositeId = itComposite.next();
                    //Log.v("Compose.java", userId + " " + compositeId);
                    for (int userCount = 0; userCount < ActivityStringInfo.AddressBookList.size(); userCount++) {
                        HashMap<String, String> getAddress = ActivityStringInfo.AddressBookList.get(userCount);
                        //Log.v("Compose.java", getAddress.get(DatabaseConstant.key_ORG_ID));
                        String tempId = getAddress.get(DatabaseConstant.key_USER_ID) + ":" + getAddress.get(DatabaseConstant.key_ORG_ID);

                        if (tempId.equals(compositeId)) {
                            Log.v("Compose.java", compositeId);
                            if (ActivityStringInfo.strCompositeUserId.size() == 1 || count == ActivityStringInfo.strCompositeUserId.size())
                                stringInfo.strAddressBookName += getAddress.get(DatabaseConstant.key_FIRST_NAME) + " " + getAddress.get(DatabaseConstant.key_LAST_NAME);
                            else
                                stringInfo.strAddressBookName += getAddress.get(DatabaseConstant.key_FIRST_NAME) + " " + getAddress.get(DatabaseConstant.key_LAST_NAME) + ", ";
                        }
                    }
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }

        if (!stringInfo.strAddressBookName.equals(""))
            txtTo.setText(stringInfo.strAddressBookName);

        if (!ActivityStringInfo.strBody.equals(""))
            edMessage.setText(ActivityStringInfo.strBody);

        if (!ActivityStringInfo.strSubject.equals(""))
            edSubject.setText(ActivityStringInfo.strSubject);
    }

    /**
     * validation while sending message
     *
     * @return
     */
    public boolean validation() {
        boolean bln = true;
        try {
            if (txtTo.getText().toString().length() <= 0) {
                showToast(MessageInfo.strSelectUserId, Toast.LENGTH_SHORT);
                bln = false;
            } else if (edSubject.getText().length() <= 0) {
                showToast(MessageInfo.strEnterSubject, Toast.LENGTH_SHORT);
                edSubject.requestFocus();
                bln = false;
            } else if (edMessage.getText().length() <= 0) {
                showToast(MessageInfo.strEnterMessage, Toast.LENGTH_SHORT);
                edMessage.requestFocus();
                bln = false;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
        return bln;
    }

    /**
     * AsyncTask for Compose Mail send
     */
    private class SelectDataTaskForComposeMail extends AsyncTask<String, Void, String> {
        boolean btlResponse = false;
        String strMsg = "";
        String strBody = "";
        String strSubject = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);

            } catch (Exception e) {
                Utility.saveExceptionDetails(ComposeActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            String strFileName = "";
            strBody = args[0];
            strSubject = args[1];

            try {
                if (Utility.browsImages(mContext)) {
                    if (Utility.strArr_SurveyPictures != null && Utility.strArr_SurveyPictures.length > 0) {
                        try {
                            for (int k = 0; k < Utility.strArr_SurveyPictures.length; k++) {
                                Utility.scaleImage(mContext, Utility.strArr_SurveyPictures[k]);
                                String xmlstring = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><img>";
                                Bitmap bitmap = BitmapFactory.decodeFile(Utility.strArr_SurveyPictures[k]);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); // compress
                                // want.
                                byte[] byte_arr = stream.toByteArray();
                                String image_str = Base64.encodeBytes(byte_arr);
                                bitmap.recycle();
                                strFileName += Utility.file_Name[k] + ",";
                                xmlstring += "<images><name>";
                                xmlstring += Utility.file_Name[k];
                                xmlstring += "</name><imgbyte>";
                                xmlstring += image_str;
                                xmlstring += "</imgbyte></images>";
                                xmlstring += "</img>";

                                ContentBody contentPart = new ByteArrayBody(byte_arr, Utility.file_Name[k]);
                                StringEntity se = new StringEntity(xmlstring.toString(), "UTF-8");
                                se.setContentType("text/xml");
                                String response = multiPost(strReqURL, se);
                                Log.v("Compose.java", "response: " + response);

                            }

                        } catch (Exception e) {
                            Utility.saveExceptionDetails(ComposeActivity.this, e);
                            e.printStackTrace();
                        }
                    }
                }
                String strUserId = "";
                String strDistributionId = "";
                String strGetId = "";

                Iterator<String> itComposite = ActivityStringInfo.strCompositeUserId.iterator();
                while (itComposite.hasNext()) {
                    strGetId = itComposite.next();
                    String[] arr = strGetId.split(":");
                    if (arr[0].contains("-0"))
                        strUserId += arr[0].substring(0, strGetId.indexOf("-")) + ",";
                    else if (!arr[0].contains("-0")) {
                        Log.v("Compose.java", "todist " + strGetId);
                        // need to discuss with Raj. in exception
                        strDistributionId += arr[0].substring(strGetId.indexOf("-") + 1) + ":" + arr[1] + ",";

                    } else
                        strUserId += arr[0] + ",";
                }

                ActivityStringInfo.strBody = this.strBody;
                ActivityStringInfo.strSubject = this.strSubject;
                strMsg = syncServiceHelper.sendComposeMail(ComposeActivity.this, "0", strUserId, strDistributionId, strFileName);
                if (strMsg.equals("true")) {
                    deleteImage();
                    btlResponse = true;
                    Synchronization syc = new Synchronization(mContext);
                    try {
                        strMsg = syc.getInformation(ComposeActivity.this);
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(ComposeActivity.this, e);
                        e.printStackTrace();
                    }
                } else {
                    btlResponse = false;
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ComposeActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        private String multiPost(String urlString, StringEntity reqEntity) {
            try {
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                HttpsURLConnection conn = (HttpsURLConnection) urlConnection;
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("User-Agent", "Fiddler");
//                conn.setRequestProperty("Host", "www.stafftap.biz");
                conn.setRequestProperty("Host", ActivityStringInfo.wsLocationImageUpload);
                conn.addRequestProperty("Content-Length", reqEntity.getContentLength() + "");
                //conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());
                conn.addRequestProperty("Content-Type", "Text/xml");

                //OutputStream os = conn.getOutputStream();
                reqEntity.writeTo(conn.getOutputStream());
                //os.close();
                conn.connect();
                Log.v("compose.java", " hello " + conn.getResponseCode());
                if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    return readStream(conn.getInputStream());
                }

            } catch (Exception e) {
                Log.e("compose.java", "multipart post error " + e + "(" + urlString + ")");
            }
            return null;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }


        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (btlResponse) {
                    showToast(MessageInfo.strMailSendSuccessfully, Toast.LENGTH_SHORT);
                    stringInfo.clear();
                    deleteImage();
                    ActivityStringInfo.shouldRefreshed = true;
                    onBackPressed();
                } else {
                    if (Utility.Connectivity_Internet(mContext))
                        showToast(MessageInfo.strSendErrorMsg, Toast.LENGTH_SHORT);
                    else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ComposeActivity.this, e);
                e.printStackTrace();
            }
        }
    }

    public int getExtensionImage(String strFileName) {
        if (strFileName.toUpperCase().contains(".DOC") || strFileName.toUpperCase().contains(".DOCX")) {
            return R.drawable.file_extension_doc;
        } else if (strFileName.toUpperCase().contains(".JPG") || strFileName.toUpperCase().contains(".JPEG")) {
            return R.drawable.file_extension_jpg;
        } else if (strFileName.toUpperCase().contains(".PNG")) {
            return R.drawable.file_extension_png;
        } else if (strFileName.toUpperCase().contains(".PDF")) {
            return R.drawable.file_extension_pdf;
        } else if (strFileName.toUpperCase().contains(".TXT")) {
            return R.drawable.file_extension_txt;
        } else if (strFileName.toUpperCase().contains(".XLS")) {
            return R.drawable.file_extension_xls;
        } else if (strFileName.toUpperCase().contains(".GIF")) {
            return R.drawable.file_extension_gif;
        } else if (strFileName.toUpperCase().contains(".BMP")) {
            return R.drawable.file_extension_bmp;
        } else if (strFileName.toUpperCase().contains(".FLV")) {
            return R.drawable.file_extension_flv;
        } else if (strFileName.toUpperCase().contains(".HTML")) {
            return R.drawable.file_extension_html;
        } else if (strFileName.toUpperCase().contains(".RTF")) {
            return R.drawable.file_extension_rtf;
        } else if (strFileName.toUpperCase().contains(".RAR")) {
            return R.drawable.file_extension_rar;
        } else if (strFileName.toUpperCase().contains(".ZIP")) {
            return R.drawable.file_extension_zip;
        } else {
            return R.drawable.icon_file;
        }
    }

    public void attachImage() {
        try {
            if (stringInfo.strCommentForWhich.equals("Mandatory")) {
                try {
                    if (Utility.arrList_Picture != null)
                        Utility.arrList_Picture.clear();

                    if (ActivityStringInfo.hashDocumentList.size() > 0) {
                        Set<Map.Entry<String, String>> set = ActivityStringInfo.hashDocumentList.entrySet();
                        // Get an iterator
                        Iterator<Map.Entry<String, String>> i = set.iterator();
                        // Display elements
                        int counter = 1;
                        while (i.hasNext()) {
                            Map.Entry me = (Map.Entry) i.next();
                            System.out.print(me.getKey() + ": ");
                            System.out.println(me.getValue());
                            HashMap<String, String> hasValues = new HashMap<String, String>();
                            hasValues.put("no", "" + counter);
                            hasValues.put("image", "" + getExtensionImage("" + me.getKey()));
                            hasValues.put("delete_image", "X");
                            hasValues.put("image_name", "" + me.getKey());
                            hasValues.put("file_path", "" + me.getValue());
                            Utility.arrList_Picture.add(hasValues);
                            counter++;
                        }
                    }
                    Utility.refreshList_Picture(mContext, adapter_AddPicture, lstAttachments);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ComposeActivity.this, e);
                    e.printStackTrace();
                }
            } else {
                Utility.arrList_Picture = new ArrayList<HashMap<String, String>>();
                takePictures();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    public void takePictures() {
        try {
            if (Utility.browsImages(mContext)) {
                if (Utility.strArr_SurveyPictures != null && Utility.strArr_SurveyPictures.length > 0) {
                    for (int k = 0; k < Utility.strArr_SurveyPictures.length; k++) {
                        strImagePath = Utility.strArr_SurveyPictures[k];
                        HashMap<String, String> hasValues = new HashMap<String, String>();
                        hasValues.put("no", "" + (Utility.arrList_Picture.size() + 1));
                        hasValues.put("image", "" + R.drawable.file_extension_jpg);
                        hasValues.put("delete_image", "X");
                        hasValues.put("image_name", Utility.file_Name[k]);
                        hasValues.put("file_path", strImagePath);
                        Utility.arrList_Picture.add(hasValues);
                    }
                    Utility.refreshList_Picture(mContext, adapter_AddPicture, lstAttachments);
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    public void myHandler_AddPicture(View v) {
        try {
            LinearLayout vwParentRow = (LinearLayout) v.getParent();
            vwParentRow.setClickable(true);
            TextView child = (TextView) vwParentRow.getChildAt(0);

            Bitmap mBitmap = BitmapFactory.decodeFile(strImagePath);

            ImageView imgView = (ImageView) findViewById(R.id.imgDocIcon);
            imgView.setImageBitmap(mBitmap);

            int element = Integer.valueOf(child.getText().toString());
            element -= 1;
            Utility.deleteRow_Picture(element, mContext, adapter_AddPicture, lstAttachments);
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    protected void startCameraActivity() {
        try {
            filename = Utility.getDate() + ".jpg";
            strImagePath = Environment.getExternalStorageDirectory() + "/ISM_Image/" + filename;
            File file = new File(strImagePath);
            Uri fileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == IMAGE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    attachImage();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    /**
     * delete attached image from external storage
     */
    public void deleteImage() {
        try {
            File f2 = new File(Environment.getExternalStorageDirectory() + "/ISM_Image");
            final File f1[] = f2.listFiles();
            if (f1 != null) {
                for (int i = 0; i < f1.length; i++) {
                    f1[i].delete();
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ComposeActivity.this, e);
            e.printStackTrace();
        }
    }

    /**
     * AsyncTask for Send Meeting Reply
     */
    private class SelectDataTaskForSendMeetingReply extends AsyncTask<String, Void, String> {
        String strMsg = "";
        String msgText = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);

            } catch (Exception e) {
                Utility.saveExceptionDetails(ComposeActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                msgText = args[0];
                strMsg = syncServiceHelper.sendReplyMeetingDetail(ComposeActivity.this, stringInfo.strMeetingId, msgText);
                if (strMsg.equals("true")) {

                    // Not in employee code.
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
//                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();

                    Synchronization syc = new Synchronization(mContext);
                    syc.getInformation(ComposeActivity.this);
                }

            } catch (Exception e) {
                Utility.saveExceptionDetails(ComposeActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.equals("true")) {
                    showToast(MessageInfo.strMailSendSuccessfully, Toast.LENGTH_SHORT);
                    if (ActivityStringInfo.strMsgArrListSize == 1) {
                        if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                            ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                            ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                        }
                    }
                    stringInfo.clear();
                    deleteImage();
                    ActivityStringInfo.shouldRefreshed = true;
                    onBackPressed();
//                    btn_Messages.performClick();
                } else {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals("") && !strMsg.equals(MessageInfo.strObjectNull)) {
//                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();      // Not in employy app code.
                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                            showToast(strMsg, Toast.LENGTH_SHORT);
                            stringInfo.clear();
                            deleteImage();
                            onBackPressed();
//                            btn_Messages.performClick();
                        } else {
                            showToast(MessageInfo.strObjectNull, Toast.LENGTH_SHORT);
                        }
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);

                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ComposeActivity.this, e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Text alert radio button onclick()
     *
     * @param view
     */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.textYesRadio:
                if (checked)
                    flagTextAlert = true;
                flagTextAlertString = "true";
                break;
            case R.id.textNoRadio:
                if (checked)
                    flagTextAlert = false;
                flagTextAlertString = "false";
                break;
        }
    }


}
