package ism.manager.message;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ism.manager.ActivityStringInfo;
import ism.manager.MainActivity;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseActivity;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;
import ism.manager.webservices.Synchronization;

public class AddressBookActivity extends AppBaseActivity {

    //for UI component
    TextView btnAdd;
    TextView btnSelectAll;
    TextView btnCancel;
    TextView btnClearAll;
    ListView lstAddressBook;
    EditText ed_FindAddress;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";

    Context mContext;

    ArrayList<HashMap<String, String>> fillAddressBook;
    ArrayList<String> arraySpList = new ArrayList<String>();

    boolean mCheckALL = false;

    List<CharSequence> userAddress;
    public HashSet<String> strUserId = new HashSet<String>();
    Object storePreviousAddressId;

    public String TAG = "AddressBook";
    String orgName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_address_book);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            mContext = this;
            this.initViews();
            this.getAddressBookRecord();
            this.setListeners();
        } catch (Exception e) {
            Utility.saveExceptionDetails(AddressBookActivity.this, e);
            e.printStackTrace();
        }
    }

    private void initViews() {
        btnAdd = (TextView) findViewById(R.id.btnAdd);
        btnSelectAll = (TextView) findViewById(R.id.btnSelectAll);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        btnClearAll = (TextView) findViewById(R.id.btnClearAll);

        lstAddressBook = (ListView) findViewById(R.id.lstAddressBook);
        ed_FindAddress = (EditText) findViewById(R.id.ed_FindAddress);


        if (fillAddressBook == null) {
            fillAddressBook = new ArrayList<HashMap<String, String>>();
        }

        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
    }


    private void setListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arraySpList.size() > 0) {
                    for (int i = 0; i < arraySpList.size(); i++) {
                        ActivityStringInfo.strAddressUserId.add(arraySpList.get(i));
                        ActivityStringInfo.strCompositeUserId.add(arraySpList.get(i));
                    }
                }
                if (ActivityStringInfo.previousActivityNew == null) {
                    stringInfo.clear();
                    ActivityStringInfo.previousActivityNew = MainActivity.class;
                }
                Intent intent2 = new Intent(getApplicationContext(), ActivityStringInfo.previousActivityNew);
                intent2.putExtra(INFO, stringInfo);
                startActivity(intent2);
                finish();
                AddressBookActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            }
        });

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckALL = true;
                fillAddressBookRecord();
            }
        });

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityStringInfo.strAddressUserId.clear();
                ActivityStringInfo.strCompositeUserId.clear();
                mCheckALL = false;
                fillAddressBookRecord();

            }
        });

        ed_FindAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fillAddressBook.clear();
                searchAddressBookList();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_FindAddress.setText("");
                searchAddressBookList();
            }
        });
    }

    private void getAddressBookRecord() {
        bindAdapter();
        fillAddressBookRecord();
        getClear();
    }

    public void searchAddressBookList() {
        try {
            mCheckALL = false;
            if (!ed_FindAddress.getText().toString().trim().equals("")) {
                if (ActivityStringInfo.AddressBookList.size() > 0) {
                    for (int i = 0; i < ActivityStringInfo.AddressBookList.size(); i++) {
                        HashMap<String, String> hasValues = new HashMap<String, String>();
                        hasValues = ActivityStringInfo.AddressBookList.get(i);
                        String txtval = ed_FindAddress.getText().toString().trim().toLowerCase();
                        //TODO Need to add 2 more field in search filter i.e. Location name and Position Title when new web services will be available
                        if (hasValues.get(DatabaseConstant.key_FIRST_NAME).toString().trim().toLowerCase().contains(txtval)
                                || hasValues.get(DatabaseConstant.key_LAST_NAME).toString().trim().toLowerCase().contains(txtval)
                                || hasValues.get(DatabaseConstant.key_ORG_NAME).toString().trim().toLowerCase().contains(txtval)
                                || hasValues.get(DatabaseConstant.key_POSITION_TITLE).toString().trim().toLowerCase().contains(txtval)) {
                            HashMap<String, String> values = new HashMap<String, String>();
                            {
                                values.put(DatabaseConstant.key_USER_ID, hasValues.get(DatabaseConstant.key_USER_ID));
                                values.put(DatabaseConstant.key_FIRST_NAME, hasValues.get(DatabaseConstant.key_FIRST_NAME));
                                values.put(DatabaseConstant.key_LAST_NAME, hasValues.get(DatabaseConstant.key_LAST_NAME));
                                values.put(DatabaseConstant.key_POSITION_TITLE, hasValues.get(DatabaseConstant.key_POSITION_TITLE));
                                values.put(DatabaseConstant.key_ORG_NAME, hasValues.get(DatabaseConstant.key_ORG_NAME));
                                values.put(DatabaseConstant.key_ORG_ID, hasValues.get(DatabaseConstant.key_ORG_ID));
                                fillAddressBook.add(values);
                            }
                        }
                    }
                    strUserId.clear();
                    fillAddressBookRecord();
                    getClear();
                }
            } else {
                strUserId.clear();
                fillAddressBookRecord();
                getClear();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(AddressBookActivity.this, e);
            e.printStackTrace();
        }
    }

    public void bindAdapter() {

        try {
            /** Get the message Detail File Attachment List**/
            ActivityStringInfo.AddressBookList.clear();
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getAddressBookRecord();
            if (c.getCount() > 0) {
                while (c.moveToNext()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(DatabaseConstant.key_USER_ID, c.getString(0));
                    map.put(DatabaseConstant.key_FIRST_NAME, c.getString(1));
                    map.put(DatabaseConstant.key_LAST_NAME, c.getString(2));
                    map.put(DatabaseConstant.key_POSITION_TITLE, c.getString(5));
                    map.put(DatabaseConstant.key_ORG_ID, c.getString(4));
                    map.put(DatabaseConstant.key_ORG_NAME, c.getString(6));

                    ActivityStringInfo.AddressBookList.add(map);
                }
            }

            c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(AddressBookActivity.this, e);
            e.printStackTrace();
        }
    }

    public void fillAddressBookRecord() {
        try {
            if (!ed_FindAddress.getText().toString().trim().equals("")) {
                lstAddressBook.setAdapter(new MyArrayAdapter(this, R.layout.list_message_address_book, fillAddressBook));
                Utility.setListViewHeightBasedOnChildren(mContext, lstAddressBook);
            } else {
                lstAddressBook.setAdapter(new MyArrayAdapter(this, R.layout.list_message_address_book, ActivityStringInfo.AddressBookList));
                Utility.setListViewHeightBasedOnChildren(mContext, lstAddressBook);
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(AddressBookActivity.this, e);
            e.printStackTrace();
        }
    }

    /**
     * Display the Address Book
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
                    v = inflater.inflate(R.layout.list_message_address_book, null);
                }
                final HashMap<String, String> hasValues = myData.get(position);
                final CheckBox checkData = (CheckBox) v.findViewById(R.id.chkAddress);
                TextView txt_FirstName = (TextView) v.findViewById(R.id.txtFirstName);
                TextView txt_LastName = (TextView) v.findViewById(R.id.txtLastName);
                TextView txt_LocationName = (TextView) v.findViewById(R.id.txtLocationName);
                TextView txt_PositionTitle = (TextView) v.findViewById(R.id.txtPositionTitle);
                LinearLayout lyl_AddressColor = (LinearLayout) v.findViewById(R.id.lylAddressColor);

                if (hasValues != null) {
                    if (!hasValues.get(DatabaseConstant.key_POSITION_TITLE).equals("")) {
                        //it is user record
                        txt_FirstName.setText(hasValues.get(DatabaseConstant.key_FIRST_NAME));
                        txt_LastName.setText(hasValues.get(DatabaseConstant.key_LAST_NAME));
                        txt_LocationName.setText(hasValues.get(DatabaseConstant.key_ORG_NAME));
                        txt_PositionTitle.setText(hasValues.get(DatabaseConstant.key_POSITION_TITLE));
                    } else {
                        //It is distribution list
                        txt_FirstName.setText(hasValues.get(DatabaseConstant.key_ORG_NAME));
                        txt_LastName.setText(hasValues.get(DatabaseConstant.key_FIRST_NAME));
                        txt_LocationName.setText("");
                        txt_PositionTitle.setText("");
                    }
                }

//                if(position % 2 == 0)
//                {
//                    lyl_AddressColor.setBackgroundColor(Color.parseColor("#cccccc"));
//                    txt_FirstName.setTextColor(Color.parseColor("#000000"));
//                    txt_LastName.setTextColor(Color.parseColor("#000000"));
//
//                    txt_LocationName.setTextColor(Color.parseColor("#000000"));
//                    txt_PositionTitle.setTextColor(Color.parseColor("#000000"));
//                }
//                else
//                {
//                    lyl_AddressColor.setBackgroundColor(Color.parseColor("#00000000"));
//                    txt_FirstName.setTextColor(Color.parseColor("#005595"));
//                    txt_LastName.setTextColor(Color.parseColor("#005595"));
//
//                    txt_LocationName.setTextColor(Color.parseColor("#005595"));
//                    txt_PositionTitle.setTextColor(Color.parseColor("#005595"));
//                }

                final String chkId = "" + hasValues.get(DatabaseConstant.key_USER_ID);
                //Log.v("Address book", "chk id " + chkId);
                final String compositCheckId = chkId + ":" + hasValues.get(DatabaseConstant.key_ORG_ID);
                //Log.v("Address book", "composite id " + compositCheckId);
                if (mCheckALL) {
                    ActivityStringInfo.strAddressUserId.add(chkId);
                    ActivityStringInfo.strCompositeUserId.add(compositCheckId);
                    strUserId.add(chkId);
                    checkData.setChecked(true);
                } else {
                    checkData.setChecked(false);
                }

                if (ActivityStringInfo.strCompositeUserId.contains(compositCheckId)) {
                    strUserId.add(chkId);
                    checkData.setChecked(true);
                }
                checkData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.v("in adapter", "on click");
                        if (checkData.isChecked()) {
                            strUserId.add(chkId);
                            ActivityStringInfo.strAddressUserId.add(chkId);
                            ActivityStringInfo.strCompositeUserId.add(compositCheckId);
                            //Log.v("Address book", "on click composite id " + compositCheckId);
                        } else {
                            strUserId.remove(chkId);
                            ActivityStringInfo.strAddressUserId.remove(chkId);
                            ActivityStringInfo.strCompositeUserId.remove(compositCheckId);
                            mCheckALL = false;
                        }
                        getClear();
                    }
                });

                v.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        checkData.performClick();
                    }
                });
            } catch (Exception e) {
                Utility.saveExceptionDetails(AddressBookActivity.this, e);
                e.printStackTrace();
            }

            return v;
        }
    }

    public void getRefresh() {
        try {
            new SelectDataTaskForAddressBook().execute();
        } catch (Exception e) {
            Utility.saveExceptionDetails(AddressBookActivity.this, e);
            e.printStackTrace();
        }
    }

    public void getClear() {
        try {

            if (ed_FindAddress.getText().toString().trim().equals("")) {
                if (ActivityStringInfo.strAddressUserId.size() == ActivityStringInfo.AddressBookList.size()) {
                    mCheckALL = true;
//                    chk_CheckALLAddress.setChecked(true);
                } else {
                    mCheckALL = false;
//                    chk_CheckALLAddress.setChecked(false);
                }
            } else if (!ed_FindAddress.getText().toString().trim().equals("")) {
                if (strUserId.size() == fillAddressBook.size()) {
                    if (fillAddressBook.size() != 0) {
                        mCheckALL = false;
//                        chk_CheckALLAddress.setChecked(true);
                    }
                } else {
                    mCheckALL = false;
//                    chk_CheckALLAddress.setChecked(false);
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(AddressBookActivity.this, e);
            e.printStackTrace();
        }
    }

    private class SelectDataTaskForAddressBook extends AsyncTask<String, Void, String> {
        public String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog((MessageInfo.loadingProgressAddress_txt));
            } catch (Exception e) {
                Utility.saveExceptionDetails(AddressBookActivity.this, e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            Synchronization synchronization = new Synchronization(mContext);
            try {
                strMsg = synchronization.getInformation(mContext);
            } catch (Exception e) {
                Utility.saveExceptionDetails(AddressBookActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (!strMsg.equals("true")) {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
                Intent i = new Intent(getApplicationContext(), AddressBookActivity.class);
                i.putExtra(INFO, stringInfo);
                startActivity(i);
                finish();
                AddressBookActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            } catch (Exception e) {
                Utility.saveExceptionDetails(AddressBookActivity.this, e);
                e.printStackTrace();
            }
        }
    }
//    public void fillSpinner()
//    {
//        try
//        {
//            //Log.v("in adress book", "in fill spinner");
//            userAddress = new ArrayList<CharSequence>();
//            userAddress.add("Contacts");
//            for(int userCount = 0;userCount < ActivityStringInfo.AddressBookList.size();userCount++)
//            {
//                HashMap<String, String> getAddress = ActivityStringInfo.AddressBookList.get(userCount);
//                userAddress.add(getAddress.get(DatabaseConstant.key_FIRST_NAME)+"  "+getAddress.get(DatabaseConstant.key_LAST_NAME));
//            }
//
//            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, userAddress);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            sp_AddressBook.setAdapter(adapter);
//            sp_AddressBook.setPrompt("List of user");
//
//            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lst_AddressBook.getLayoutParams();
//            mlp.setMargins(0, 0, 0, lyl_AddressFooter.getHeight());
//            Utility.setListViewHeightBasedOnChildren(mContext,lst_AddressBook);
//
//            sp_AddressBook.setOnItemSelectedListener(new OnItemSelectedListener()
//            {
//
//                public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3)
//                {
//                    //	Log.v("Address book", "in  item selected");
//                    try
//                    {
//                        if(ActivityStringInfo.strAddressUserId != null)
//                        {
//                            arraySpList.clear();
//                            if(!sp_AddressBook.getSelectedItem().toString().equals("Contacts"))
//                            {
//                                //Log.v("in Address book", "in if case");
//                                for(int userCount = 0;userCount < ActivityStringInfo.AddressBookList.size();userCount++)
//                                {
//                                    HashMap<String, String> getAddress = ActivityStringInfo.AddressBookList.get(userCount);
//                                    String strName = getAddress.get(DatabaseConstant.key_FIRST_NAME)+"  "+getAddress.get(DatabaseConstant.key_LAST_NAME);
//
//                                    if(strName.equalsIgnoreCase(sp_AddressBook.getSelectedItem().toString()))
//                                    {
//                                        //	Log.v("Address Book", "sp address book " + getAddress.get(DatabaseConstant.key_USER_ID));
//                                        arraySpList.add(getAddress.get(DatabaseConstant.key_USER_ID));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    catch (Exception e)
//                    {
//                        Utility.saveExceptionDetails(AddressBook.this, e);
//                        e.printStackTrace();
//                    }
//                }
//                public void onNothingSelected(AdapterView<?> arg0)
//                {
//                    //nothing to do
//                }
//            });
//        }
//        catch (Exception e)
//        {
//            Utility.saveExceptionDetails(AddressBook.this, e);
//            e.printStackTrace();
//        }
//    }


    @Override
    public void onBackPressed() {
        if (ActivityStringInfo.previousActivityNew == null) {
            stringInfo.clear();
            ActivityStringInfo.previousActivityNew = MainActivity.class;
        }
        Intent intent2 = new Intent(getApplicationContext(), ActivityStringInfo.previousActivityNew);
        intent2.putExtra(INFO, stringInfo);
        startActivity(intent2);
        finish();
        AddressBookActivity.this.overridePendingTransition(R.anim.pump_top, R.anim.disappear);
    }
}
