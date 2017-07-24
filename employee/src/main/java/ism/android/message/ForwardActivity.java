package ism.android.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseActivity;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.utils.StaticVariables;
import ism.android.webservices.SyncServiceHelper;
import ism.android.webservices.Synchronization;


public class ForwardActivity extends AppBaseActivity {


    private LinearLayout attachmentLayout;
    private LinearLayout toTextLayout;

    private EditText edSubject;
    private TextView txtFrom;
    private TextView txtTo;
    private TextView txtMsgDateTime;
    private TextView txtAttachments;
    private EditText edMessage;
    private ListView lstAttachments;
    private ListView lstAttachmentsNew;
    private ImageView imgMessageImage;


    private RadioButton radioTextAlertButton;
    //alert Radio Group
    private RadioGroup alertTextRadioGroup;
    private RadioButton textYesRadioButton;
    private RadioButton textNoRadioButton;

    //Other Class Declaration
    ActivityStringInfo stringInfo;
    SyncServiceHelper syncServiceHelper;

    //Variable Declaration
    public static final String INFO = "INFO";
    public static int IMAGE_REQUEST_CODE = 0;
    ArrayList<HashMap<String, String>> fillMessageDetailAttachmentList;
    String strImagePath = "";
    String fileName = "";
    SimpleAdapter adapter_AddPicture;
    Context mContext;
    String strReqURL = ActivityStringInfo.wsLocation + "/ImageUpload.aspx";
    public static boolean flagTextAlert;
    public static String flagTextAlertString = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forward);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            this.initViews();
            this.getAddressFromBook();
            this.attachImage();
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_composemail, menu);
        MenuItem pictureMenu = menu.findItem(R.id.action_picture);
        if (pictureMenu != null) {
            if (stringInfo.strCommentForWhich.equals("Meeting")) {
                pictureMenu.setVisible(false);
            } else if (stringInfo.strCommentForWhich.equals("Mandatory")) {
                pictureMenu.setVisible(true);
                pictureMenu.setTitle(R.string.attach);
                pictureMenu.setIcon(R.drawable.attach_white);
            } else {
                pictureMenu.setVisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_cancel) {
//            onBackPressed();
//            return true;
//        }

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
        stringInfo.clear();
        deleteImage();
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
     * send messages to receiver
     */
    private void sendMessage() {
        try {
            if (validation()) {
                int selectedId = alertTextRadioGroup.getCheckedRadioButtonId();
                radioTextAlertButton = (RadioButton) findViewById(selectedId);
                new SelectDataTaskForComposeMail().execute(edMessage.getText().toString().trim(), edSubject.getText().toString().trim(), (radioTextAlertButton.getText().toString().equals("Yes")) ? "true" : "false");
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }
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
                bln = false;
            } else if (edMessage.getText().length() <= 0) {
                showToast(MessageInfo.strEnterMessage, Toast.LENGTH_SHORT);
                bln = false;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }
        return bln;
    }

    /**
     * take picture for attachements
     */
    private void getPicture() {
        try {
            if (Utility.checkReadExternalStorage(mContext) || Utility.checkWriteExternalStorage(mContext)) {
                Utility.OpenCreateFile(mContext);
                startCameraActivity();
            } else {
                String msg = Utility.requestReadWriteExternalStoragePermission(mContext);
                showToast(msg, Toast.LENGTH_SHORT);
            }

        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
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
        this.mContext = ForwardActivity.this;
        this.syncServiceHelper = new SyncServiceHelper();
        this.attachmentLayout = (LinearLayout) findViewById(R.id.attachmentLayout);
        this.toTextLayout = (LinearLayout) findViewById(R.id.toTxt_layout);

        this.lstAttachments = (ListView) findViewById(R.id.lstAttachments);
        this.lstAttachmentsNew = (ListView) findViewById(R.id.lstAttachmentsNew);

        this.txtTo = (TextView) findViewById(R.id.txtTo);
        this.edSubject = (EditText) findViewById(R.id.edSubject);
        this.edMessage = (EditText) findViewById(R.id.edMessage);
        this.imgMessageImage = (ImageView) findViewById(R.id.imgMessageImage);
//        this.edSubject.requestFocus();
        this.toTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringInfo.strAddressBookName = "";
                ActivityStringInfo.previousActivityNew = ForwardActivity.class;
                ActivityStringInfo.strBody = edMessage.getText().toString();
                ActivityStringInfo.strSubject = edSubject.getText().toString();
                Intent intent1 = new Intent(getApplicationContext(), AddressBookActivity.class);
                intent1.putExtra(INFO, stringInfo);
                startActivity(intent1);
                finish();
                ForwardActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });
        this.txtTo.setNextFocusDownId(R.id.edSubject);
        this.edSubject.setNextFocusDownId(R.id.edMessage);
//        this.edMessage.requestFocus();
        this.txtTo.requestFocus();
        this.alertTextRadioGroup = (RadioGroup) findViewById(R.id.alertRadioGroup);
        this.textYesRadioButton = (RadioButton) findViewById(R.id.textYesRadio);
        this.textNoRadioButton = (RadioButton) findViewById(R.id.textNoRadio);
        this.textNoRadioButton.setChecked(true);
        this.setTitle(ActivityStringInfo.strSendFor);
        this.txtAttachments = (TextView) findViewById(R.id.txtAttachments);
        this.txtAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });
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
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }

        if (!stringInfo.strAddressBookName.equals(""))
            txtTo.setText(stringInfo.strAddressBookName);

        if (!ActivityStringInfo.strBody.equals(""))
            edMessage.setText(ActivityStringInfo.strBody);

        if (!ActivityStringInfo.strSubject.equals(""))
            edSubject.setText(ActivityStringInfo.strSubject);

        // get Attachment List of previous msg
        if (fillMessageDetailAttachmentList == null) {
            fillMessageDetailAttachmentList = new ArrayList<HashMap<String, String>>();

            if (ActivityStringInfo.strSendFor.equals("Forward"))
                bindAdapter();
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
                }
            }
            c.close();
            fillAttachmentList();
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }
    }

    public void fillAttachmentList() {
        try {
            if (fillMessageDetailAttachmentList.size() > 0) {
                lstAttachments.setAdapter(new MyArrayAdapter(this, R.layout.list_message_attechments, fillMessageDetailAttachmentList));
                Utility.setListViewHeightBasedOnChildren(mContext, lstAttachments);
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
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
                    Button btnDelete = (Button) v.findViewById(R.id.btnDelete);
                    String strFileName = hasValues.get(DatabaseConstant.key_FILE_NAME);
                    txt_MessageAttachmentFileName.setText(strFileName);
//                    txt_MessageAttachmentFileName.setTextColor(Color.parseColor("#000000"));
                    //Button btn_Open = (Button)v.findViewById(R.id.btnOpen);
                    ImageView img_ItemIcon = (ImageView) v.findViewById(R.id.imgDocIcon);
                    img_ItemIcon.setImageResource(R.drawable.paperclip);

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

                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myData.remove(position);
                            notifyDataSetChanged();
                            Utility.deleteRow_Picture(position, mContext, adapter_AddPicture, lstAttachments);
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ForwardActivity.this, e);
                e.printStackTrace();
            }

            return v;
        }
    }

    public void deleteImage() {
        try {
            File f2 = new File(Environment.getExternalStorageDirectory() + "/ISM_Image");
            final File f1[] = f2.listFiles();
            if (f1 != null) {
                System.out.println("Files Length :" + f1.length);
                for (int i = 0; i < f1.length; i++) {
                    f1[i].delete();
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }
    }

    protected void startCameraActivity() {
        try {
            fileName = Utility.getDate() + ".jpg";
            strImagePath = Environment.getExternalStorageDirectory() + "/ISM_Image/" + fileName;
            File file = new File(strImagePath);
            Uri fileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Log.i("ISM", "resultCode: " + resultCode);
            Log.i("ISM", "requestCode: " + requestCode);

            if (requestCode == IMAGE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    attachImage();
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void attachImage() {
        try {
            Utility.arrList_Picture = new ArrayList<HashMap<String, String>>();
            takePictures();
            Utility.refreshList_Picture(mContext, adapter_AddPicture, lstAttachmentsNew);
        } catch (Exception e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }
    }

    public void takePictures() {
        if (Utility.browsImages(mContext)) {
            if (Utility.strArr_SurveyPictures != null && Utility.strArr_SurveyPictures.length > 0) {
                try {
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
                    Utility.refreshList_Picture(mContext, adapter_AddPicture, lstAttachmentsNew);
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ForwardActivity.this, e);
                    e.printStackTrace();
                }
            }
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
            Utility.deleteRow_Picture(element, mContext, adapter_AddPicture, lstAttachmentsNew);
            fillAttachmentList();
        } catch (NumberFormatException e) {
            Utility.saveExceptionDetails(ForwardActivity.this, e);
            e.printStackTrace();
        }
    }

    private class SelectDataTaskForComposeMail extends AsyncTask<String, Void, String> {
        boolean btlResponse = false;
        String strMsg = "";
        String strBody = "";
        String strSubject = "";
        String flagAlert = "false";

        // can use UI thread here
        protected void onPreExecute() {
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            try {
                showTransparentProgressDialog(MessageInfo.loadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ForwardActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            String strFileName = "";
            strBody = args[0];
            strSubject = args[1];
            flagAlert = args[2];
            try {
                if (Utility.browsImages(mContext)) {
                    if (Utility.strArr_SurveyPictures != null && Utility.strArr_SurveyPictures.length > 0) {
                        try {
                            for (int k = 0; k < Utility.strArr_SurveyPictures.length; k++) {
                                Utility.scaleImage(mContext, Utility.strArr_SurveyPictures[k]);
                                String xmlstring = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><img>";
                                Bitmap bitmap = BitmapFactory.decodeFile(Utility.strArr_SurveyPictures[k]);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); // compress
                                // want.
                                byte[] byte_arr = stream.toByteArray();
                                String image_str = Base64.encodeBytes(byte_arr);
                                bitmap.recycle();
                                strFileName += Utility.file_Name[k] + ",";
                                xmlstring += "<images> <name>";
                                xmlstring += Utility.file_Name[k];
                                xmlstring += "</name> <imgbyte>";
                                xmlstring += image_str;
                                xmlstring += "</imgbyte> </images>";
                                xmlstring += "</img>";
//                                HttpPost httppost = new HttpPost(strReqURL);
//                                StringEntity se = new StringEntity(xmlstring.toString(), HTTP.UTF_8);
//
//                                se.setContentType("text/xml");
//                                httppost.setHeader("Content-Type","application/soap+xml;charset=UTF-8");
//                                httppost.setEntity(se);
//                                HttpClient httpclient = new DefaultHttpClient();
//                                BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
//                                System.out.println("=========== " + httpResponse.getAllHeaders()[4].getValue().toLowerCase());
//
//                                if(httpResponse.getAllHeaders()[4].getValue().toLowerCase().equals("success"))
//                                {
//                                    //pd.setMessage(ult.file_Name[k]+ "send successfully.");
//                                    System.out.println("true====");
//                                    xmlstring = "";
//                                }
                                ContentBody contentPart = new ByteArrayBody(byte_arr, Utility.file_Name[k]);
                                StringEntity se = new StringEntity(xmlstring.toString(), "UTF-8");
                                se.setContentType("text/xml");
                                String response = multiPost(strReqURL, se);
                                Log.v("Compose.java", "response: " + response);
                            }
                        } catch (Exception e) {
                            Utility.saveExceptionDetails(ForwardActivity.this, e);
                            e.printStackTrace();
                        }
                    }
                }
                String strUserId = "";
                String strDistributionId = "";
                String strGetId = "";
                System.out.println("ActivityStringInfo.strAddressUserId" + ActivityStringInfo.strAddressUserId);
                //for(int i  =0 ;i<ActivityStringInfo.strAddressUserId.size();i++)
                //{
                Iterator<String> it = ActivityStringInfo.strAddressUserId.iterator();
                while (it.hasNext()) {
                    strGetId = it.next();
                    System.out.println("strGetId==" + strGetId);
                    if (strGetId.contains("-0"))
                        strUserId += strGetId.substring(0, strGetId.indexOf("-")) + ",";
                    else if (!strGetId.contains("-0"))
                        strDistributionId += strGetId.substring(strGetId.indexOf("-") + 1) + ":" + StaticVariables.getOrgID(mContext) + ",";
                    else
                        strUserId += strGetId + ",";
                }
                //}
                System.out.println("strUserId==" + strUserId);
                System.out.println("strDistributionId==" + strDistributionId);
                ActivityStringInfo.strBody = this.strBody;
                ActivityStringInfo.strSubject = this.strSubject;
                String userId = "";

                if (ActivityStringInfo.strSendFor.equals("Forward"))
                    userId = stringInfo.strMessageId;
                else
                    userId = "0";


                flagTextAlertString = flagAlert;
                strMsg = syncServiceHelper.sendComposeMail(ForwardActivity.this, userId, strUserId, strDistributionId, strFileName);  // , "", flagTextAlertString
                if (strMsg.equals("true")) {
                    deleteImage();
                    btlResponse = true;
                    Synchronization syc = new Synchronization(mContext);
                    try {
                        strMsg = syc.getInformation(ForwardActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    btlResponse = false;

            } catch (Exception e) {
                Utility.saveExceptionDetails(ForwardActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
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
                Utility.saveExceptionDetails(ForwardActivity.this, e);
                e.printStackTrace();
            }
        }
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
            //conn.setRequestProperty("Host", "staging.stafftap.biz");
            conn.setRequestProperty("Host", ActivityStringInfo.wsHost);
            conn.addRequestProperty("Content-Length", reqEntity.getContentLength() + "");
            //conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());
            conn.addRequestProperty("Content-Type", "Text/xml");

            //OutputStream os = conn.getOutputStream();
            reqEntity.writeTo(conn.getOutputStream());
            //os.close();
            conn.connect();
            Log.v("compose.java", " hello " + conn.getResponseCode());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
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