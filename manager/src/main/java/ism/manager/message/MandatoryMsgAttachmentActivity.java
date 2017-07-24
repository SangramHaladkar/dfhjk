package ism.manager.message;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;

public class MandatoryMsgAttachmentActivity extends AppBaseActivity {

    //for UI component
    Button btn_Up, btn_Cancel, btn_Attach;
    TextView txt_Location, txt_LastLine, txt_NoRecordMsg;
    ListView lstDirectory;
    Spinner spinParentDirectory;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration

    boolean oriChanged = false;

    HashMap<String, String> dirWithID;
    List<HashMap<String, String>> fillmap = new ArrayList<HashMap<String, String>>();
    static List<String> location, locationId;
    List<CharSequence> directories;
    HashSet<String> storeCheckId;

    HashMap<String, String> hashDocumentList;

    public static final String INFO = "INFO";
    static String locationString = "";
    static String lastSelectedParentId = "0", lastLocationId = "0";
    String strFileName;
    String strFileLink;

    int total_width;
    int total_heigth;
    static int pwHeight = 0, pwWidth = 0;
    Context mContext;
    Cursor cursorParents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandatory_msg_attachment);
        try {
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            this.initViews();
            this.fillSpinner();
            this.setListeners();
        } catch (Exception e) {

        }
    }

    private void initViews() {
        hashDocumentList = new HashMap<String, String>(ActivityStringInfo.hashDocumentList);
        mContext = this;
        storeCheckId = new HashSet<String>();
        btn_Up = (Button) findViewById(R.id.btnUp);
        btn_Cancel = (Button) findViewById(R.id.btnCancel);
        btn_Attach = (Button) findViewById(R.id.btnAttach);
        lstDirectory = (ListView) findViewById(R.id.lstDirectory);
        spinParentDirectory = (Spinner) findViewById(R.id.spinParentDirectory);
        txt_Location = (TextView) findViewById(R.id.txtLocation);
        txt_LastLine = (TextView) findViewById(R.id.txtLastLine);
        txt_NoRecordMsg = (TextView) findViewById(R.id.txtNoRecordMsg);
        if(ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals(""))
        {
            new SplashActivity().setValue();
            System.out.println("ActivityStringInfo.strDocRights===="+ActivityStringInfo.strDocRights);
        }
        location = new ArrayList<String>();
        locationId = new ArrayList<String>();
        location.clear();
        locationId.clear();
        lastSelectedParentId="0";
        lastLocationId="0";
    }

    private void setListeners(){

        btn_Cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ActivityStringInfo.hashDocumentList = new HashMap<String, String>(hashDocumentList);
                Intent i = new Intent(getApplicationContext(),ComposeActivity.class);
                i.putExtra(INFO, stringInfo);
                startActivity(i);
                finish();
                MandatoryMsgAttachmentActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });

        btn_Attach.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),ComposeActivity.class);
                i.putExtra(INFO, stringInfo);
                startActivity(i);
                finish();
                MandatoryMsgAttachmentActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });

        btn_Up.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String id = null;
                try
                {
                    if(location.size()>0)
                    {
                        String loc="";
                        if((location.size()-1) !=0)
                        {
                            location.remove(location.size()-1);
                            locationId.remove(locationId.size()-1);

                            Iterator<String> itr = location.iterator();
                            while(itr.hasNext())
                            {
                                loc = loc + "/"+itr.next();
                            }
                            id = locationId.get(locationId.size()-1);
                        }
                        else
                        {
                            loc = "";
                            id = "0";
                            location.clear();
                            locationId.clear();
                            lastSelectedParentId = "0";
                            fillSpinner();
                        }
                        txt_Location.setText(loc);
                        locationString = loc;
                        bindAdapter(""+ id);
                    }
                }
                catch (Exception e)
                {
                    Utility.saveExceptionDetails(MandatoryMsgAttachmentActivity.this, e);
                    e.printStackTrace();
                }
            }
        });
    }

    // method to fill the parent directories into spinner
    private void fillSpinner()
    {
        try
        {
            cursorParents = MyDatabaseInstanceHolder.getDatabaseHelper().getDocumentParentDirectories(DatabaseConstant.key_DOCUMENT_NAME, DatabaseConstant.key_ASC);
            System.out.println("cursorParents=="+cursorParents.getCount());
            if(lastSelectedParentId.equals("0"))
            {
                btn_Up.setVisibility(View.GONE);
                btn_Cancel.setVisibility(View.VISIBLE);
                btn_Attach.setVisibility(View.GONE);
            }
            else
            {
                btn_Up.setVisibility(View.VISIBLE);
                btn_Cancel.setVisibility(View.VISIBLE);
                btn_Attach.setVisibility(View.VISIBLE);
            }

            if(cursorParents != null)
            {
                int totalparents = cursorParents.getCount();
                directories = new ArrayList<CharSequence>();
                dirWithID =  new HashMap<String, String>();

                if(totalparents != 0)
                {
                    directories.add("");
                    if(cursorParents.moveToFirst())
                    {
                        do{
                            directories.add(cursorParents.getString(2));
                            dirWithID.put(cursorParents.getString(2), cursorParents.getString(0));
                        }while(cursorParents.moveToNext());
                    }

                    cursorParents.close();
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, directories);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    spinParentDirectory.setAdapter(adapter);
                    spinParentDirectory.setPrompt("Directory list");

                    spinParentDirectory.setSelection(Integer.valueOf(lastSelectedParentId));

                    spinParentDirectory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3)
                        {
                            String selectedItem = spinParentDirectory.getSelectedItem().toString();
                            System.out.println("Selected Items : "+selectedItem);
                            String id;

                            if(selectedItem.equals(""))
                            {
                                id="0";
                                lstDirectory.clearChoices();
                                location.clear();
                                locationId.clear();
                                txt_Location.setText("");
                                lastSelectedParentId = id;
                                btn_Up.setVisibility(View.GONE);
                                btn_Cancel.setVisibility(View.VISIBLE);
                                btn_Attach.setVisibility(View.GONE);
                                bindAdapter(id);
                            }
                            else
                            {
                                btn_Up.setVisibility(View.VISIBLE);
                                btn_Cancel.setVisibility(View.VISIBLE);
                                btn_Attach.setVisibility(View.VISIBLE);

                                id = lastLocationId;
                                if(!lastSelectedParentId.equals(dirWithID.get(selectedItem)))
                                {
                                    oriChanged = false;
                                }

                                lastSelectedParentId = dirWithID.get(selectedItem);
                                if(oriChanged == false)
                                {
                                    location.clear();
                                    locationId.clear();
                                    location.add(selectedItem);
                                    locationId.add(dirWithID.get(selectedItem));
                                    id = dirWithID.get(selectedItem);
                                }
                                Iterator<String> itr = location.iterator();
                                String loc="";
                                while(itr.hasNext())
                                {
                                    loc = loc+ "/"+itr.next();
                                }

                                txt_Location.setText(loc);
                                bindAdapter(id);
                            }
                        }
                        public void onNothingSelected(AdapterView<?> arg0)
                        {
                            //nothing to do
                        }
                    });
                }
                else
                   showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            }
            else
            {
                cursorParents.close();
            }
        }
        catch (Exception e)
        {
            Utility.saveExceptionDetails(MandatoryMsgAttachmentActivity.this, e);
            e.printStackTrace();
        }
    }
    // method to bind the adapter with list view
    public void bindAdapter(String parentId)
    {
        try
        {
            int totalSubDirectory = 0;
            fillmap.clear();

            Cursor cursorSubdirectory = null;
            if(parentId.equals("0"))
            {
                lastLocationId = parentId;
                cursorSubdirectory = MyDatabaseInstanceHolder.getDatabaseHelper().getDocumentParentDirectories(DatabaseConstant.key_DOCUMENT_NAME, DatabaseConstant.key_ASC);
            }
            else
            {
                lastLocationId = parentId;
                cursorSubdirectory = MyDatabaseInstanceHolder.getDatabaseHelper().getSubDirectory(parentId, DatabaseConstant.key_DOCUMENT_TYPE, DatabaseConstant.key_DESC);
            }
            totalSubDirectory= cursorSubdirectory.getCount();
            if(totalSubDirectory != 0)
            {
                if(cursorSubdirectory.moveToFirst())
                {
                    do{
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(DatabaseConstant.key_DOCUMENT_ID, cursorSubdirectory.getString(0));
                        map.put(DatabaseConstant.key_DOCUMENT_PARENT_ID, cursorSubdirectory.getString(1));
                        map.put(DatabaseConstant.key_DOCUMENT_NAME, cursorSubdirectory.getString(2));
                        map.put(DatabaseConstant.key_DOCUMENT_TYPE, cursorSubdirectory.getString(3));
                        map.put(DatabaseConstant.key_DOCUMENT_FILE_LINK, cursorSubdirectory.getString(4));

                        fillmap.add(map);

                    }while(cursorSubdirectory.moveToNext());
                }
                txt_NoRecordMsg.setVisibility(View.GONE);
            }
            else
            {
                fillmap.clear();
                //txt_NoRecordMsg.setVisibility(View.VISIBLE);
                //txt_NoRecordMsg.setText("File/Folder not found.");
            }
            cursorSubdirectory.close();
            lstDirectory.setAdapter(new MyArrayAdapter(this,R.layout.list_message_mandatory_attachments , fillmap));
            Utility.setListViewHeightBasedOnChildren(mContext,lstDirectory);
        }
        catch (Exception e)
        {
            Utility.saveExceptionDetails(MandatoryMsgAttachmentActivity.this, e);
            e.printStackTrace();
        }
    }

    // class to bind list view and to get control of list view's element.
    @SuppressWarnings("unchecked")
    private class MyArrayAdapter extends ArrayAdapter
    {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        public MyArrayAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects)
        {
            super(context, textViewResourceId, objects);
            context = getContext();
            myData = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            try
            {
                if(v == null)
                {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.list_message_mandatory_attachments, null);
                }

                HashMap<String, String> hasValues = myData.get(position);

                if(hasValues != null)
                {
                    String fileName = hasValues.get(DatabaseConstant.key_DOCUMENT_NAME);
                    String fileType = hasValues.get(DatabaseConstant.key_DOCUMENT_TYPE);

                    final TextView txt_ItemName = (TextView)v.findViewById(R.id.txtFolderName);
                    ImageView img_ItemIcon = (ImageView)v.findViewById(R.id.imgFolder);
                    final CheckBox chkFileName = (CheckBox) v.findViewById(R.id.chkFile);
                    if(fileType.toUpperCase().trim().equals("FOLDER"))
                    {
                        txt_ItemName.setTextColor(Color.parseColor("#005595"));
                        SpannableString content = new SpannableString(hasValues.get(DatabaseConstant.key_DOCUMENT_NAME));
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        txt_ItemName.setText(content);

                        img_ItemIcon.setImageResource(R.drawable.ic_action_open_folder);
                        chkFileName.setVisibility(View.INVISIBLE);
                    }
                    else if(fileType.toUpperCase().trim().equals("FILE"))
                    {

                        txt_ItemName.setTextColor(Color.parseColor("#000000"));
                        txt_ItemName.setText(hasValues.get(DatabaseConstant.key_DOCUMENT_NAME));

                        if(fileName.toUpperCase().contains(".DOC") || fileName.toUpperCase().contains(".DOCX"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_doc);
                        }
                        else if(fileName.toUpperCase().contains(".JPG") || fileName.toUpperCase().contains(".JPEG"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_jpg);
                        }
                        else if(fileName.toUpperCase().contains(".PNG"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_png);
                        }
                        else if(fileName.toUpperCase().contains(".PDF"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_pdf);
                        }
                        else if(fileName.toUpperCase().contains(".TXT"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_txt);
                        }
                        else if(fileName.toUpperCase().contains(".XLS"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_xls);
                        }
                        else if(fileName.toUpperCase().contains(".GIF"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_gif);
                        }
                        else if(fileName.toUpperCase().contains(".BMP"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_bmp);
                        }
                        else if(fileName.toUpperCase().contains(".FLV"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_flv);
                        }
                        else if(fileName.toUpperCase().contains(".HTML"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_html);
                        }
                        else if(fileName.toUpperCase().contains(".RTF"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_rtf);
                        }
                        else if(fileName.toUpperCase().contains(".RAR"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_rar);
                        }
                        else if(fileName.toUpperCase().contains(".ZIP"))
                        {
                            img_ItemIcon.setImageResource(R.drawable.file_extension_zip);
                        }
                        else
                        {
                            img_ItemIcon.setImageResource(R.drawable.icon_file);
                        }
                    }

                    final String selectedItemId = hasValues.get(DatabaseConstant.key_DOCUMENT_ID);
                    final String selectedFileName = hasValues.get(DatabaseConstant.key_DOCUMENT_NAME);
                    final String selectedFileType = hasValues.get(DatabaseConstant.key_DOCUMENT_TYPE);
                    final String selectedFileLink = hasValues.get(DatabaseConstant.key_DOCUMENT_FILE_LINK);


                    img_ItemIcon.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            txt_ItemName.performClick();
                        }
                    });

                    if(storeCheckId.contains(selectedFileName))
                    {
                        chkFileName.setChecked(true);
                    }

                    chkFileName.setOnClickListener(new View.OnClickListener()
                    {

                        @Override
                        public void onClick(View v)
                        {

                            if (chkFileName.isChecked())
                            {
                                storeCheckId.add(selectedFileName);
                                ActivityStringInfo.hashDocumentList.put(selectedFileName,selectedFileLink);
                            }
                            else
                            {
                                storeCheckId.remove(selectedFileName);
                                ActivityStringInfo.hashDocumentList.remove(selectedFileName);
                            }
                            System.out.println("ActivityStringInfo.hashDocumentList=="+ActivityStringInfo.hashDocumentList.toString());
                        }
                    });

                    //Button btnOpen = (Button)v.findViewById(R.id.btnOpen);
                    txt_ItemName.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            btn_Up.setVisibility(View.VISIBLE);
                            btn_Cancel.setVisibility(View.VISIBLE);
                            btn_Attach.setVisibility(View.VISIBLE);

                            try
                            {
                                if(!selectedFileType.equals("FILE"))
                                {
                                    location.add(selectedFileName);
                                    locationId.add(selectedItemId);


                                    Iterator itr = location.iterator();
                                    String loc="";
                                    while(itr.hasNext())
                                    {
                                        loc = loc+ "/"+itr.next();
                                    }
                                    txt_Location.setText(loc);

                                    int fileFromSpinner = directories.indexOf(selectedFileName);
                                    if(fileFromSpinner >=0 && fileFromSpinner<= (directories.size()-1))
                                        spinParentDirectory.setSelection(fileFromSpinner);

                                    locationString = loc;
                                    bindAdapter(selectedItemId);
                                }
                            }
                            catch (Exception e)
                            {
                                //Utility.saveExceptionDetails(Document_Home.this, e);
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else{
                    // do nothing.
                }
            }
            catch (Exception e)
            {
                Utility.saveExceptionDetails(MandatoryMsgAttachmentActivity.this, e);
                e.printStackTrace();
            }
            return v;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        btn_Cancel.performClick();
    }
}
