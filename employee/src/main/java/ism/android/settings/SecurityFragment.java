package ism.android.settings;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.MessageInfo;
import ism.android.webservices.ServicesHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecurityFragment extends AppBaseFragment {

    Button btn_Cancel, btn_Save;
    EditText ed_SecurityAnswer1, ed_SecurityAnswer2, ed_SecurityAnswer3;
    Spinner sp_SecurityQue1, sp_SecurityQue2, sp_SecurityQue3;

    String preSelectedQuestionOne = "", preSelectedQuestionTwo = "", preSelectedQuestionThree = "";
    String lastSelectedItem = "";
    String strQuestionId1 = "0", strQuestionId2 = "0", strQuestionId3 = "0";
    String strAnswer1 = "", strAnswer2 = "", strAnswer3 = "";

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;

    List<CharSequence> mainQuestionList;
    HashMap<String, String> questionIDMap;
    ArrayAdapter<CharSequence> adapter1, adapter2, adapter3;
    HashSet<String> hashSetQuestion;

    public SecurityFragment() {
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
        mContext = SecurityFragment.this.getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_security, container, false);
        try {
            this.initViews(view);
            this.setListeners();
            this.setValues();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return view;
    }

    private void initViews(View v) {
        btn_Save = (Button) v.findViewById(R.id.btnSave);
        btn_Cancel = (Button) v.findViewById(R.id.btnCancel);

        ed_SecurityAnswer1 = (EditText) v.findViewById(R.id.edSecurityAnswer1);
        ed_SecurityAnswer2 = (EditText) v.findViewById(R.id.edSecurityAnswer2);
        ed_SecurityAnswer3 = (EditText) v.findViewById(R.id.edSecurityAnswer3);

        sp_SecurityQue1 = (Spinner) v.findViewById(R.id.spSecurityQue1);
        sp_SecurityQue2 = (Spinner) v.findViewById(R.id.spSecurityQue2);
        sp_SecurityQue3 = (Spinner) v.findViewById(R.id.spSecurityQue3);

        mainQuestionList = new ArrayList<CharSequence>();
        hashSetQuestion  = new HashSet<String>();
        questionIDMap = new HashMap<String, String>();
    }

    private void setListeners() {
        sp_SecurityQue1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastSelectedItem = sp_SecurityQue1.getSelectedItem().toString();
                hashSetQuestion.clear();
                hashSetQuestion.add(sp_SecurityQue2.getSelectedItem().toString());
                hashSetQuestion.add(sp_SecurityQue3.getSelectedItem().toString());
                fillSpinnerSecurityQueOne();
                return false;
            }
        });
        sp_SecurityQue2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastSelectedItem = sp_SecurityQue2.getSelectedItem().toString();
                hashSetQuestion.clear();
                hashSetQuestion.add(sp_SecurityQue1.getSelectedItem().toString());
                hashSetQuestion.add(sp_SecurityQue3.getSelectedItem().toString());
                fillSpinnerSecurityQueTwo();
                return false;
            }
        });
        sp_SecurityQue3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastSelectedItem = sp_SecurityQue3.getSelectedItem().toString();
                hashSetQuestion.clear();
                hashSetQuestion.add(sp_SecurityQue2.getSelectedItem().toString());
                hashSetQuestion.add(sp_SecurityQue1.getSelectedItem().toString());
                fillSpinnerSecurityQueThree();
                return false;
            }
        });
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getValidateQuestions()) {
                        MyDatabaseInstanceHolder.getDatabaseHelper().updateQuestionAnswer("1", strQuestionId1, strAnswer1);
                        MyDatabaseInstanceHolder.getDatabaseHelper().updateQuestionAnswer("2", strQuestionId2, strAnswer2);
                        MyDatabaseInstanceHolder.getDatabaseHelper().updateQuestionAnswer("3", strQuestionId3, strAnswer3);
                        new SelectDataTaskForSetUpdateLoginChallenge().execute();
                    } else {
                        showToast(MessageInfo.strAllQuestionRequired, Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValues() {
        setQuestionsList();
        fillSpinnerSecurityQueOne();
        fillSpinnerSecurityQueTwo();
        fillSpinnerSecurityQueThree();
        setPreviousQuestionAnswer();
    }

    public boolean getValidateQuestions() {
        boolean validation = true;
        try {
            strQuestionId1 = questionIDMap.get(sp_SecurityQue1.getSelectedItem().toString());
            strQuestionId2 = questionIDMap.get(sp_SecurityQue2.getSelectedItem().toString());
            strQuestionId3 = questionIDMap.get(sp_SecurityQue3.getSelectedItem().toString());

            if (!strQuestionId1.equals("0") && !strQuestionId2.equals("0") && !strQuestionId3.equals("0")) {
                strAnswer1 = ed_SecurityAnswer1.getText().toString();
                if (strAnswer1.equals(""))
                    validation = false;

                strAnswer2 = ed_SecurityAnswer2.getText().toString();
                if (strAnswer2.equals(""))
                    validation = false;

                strAnswer3 = ed_SecurityAnswer3.getText().toString();
                if (strAnswer3.equals(""))
                    validation = false;
            } else {
                validation = false;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return validation;
    }

    @SuppressWarnings("unchecked")
    public List<CharSequence> getRemainQuestionList() {
        List<CharSequence> remainQuestion = new ArrayList(mainQuestionList);
        try {
            Iterator<String> it = hashSetQuestion.iterator();
            while (it.hasNext()) {
                String que = it.next();
                if (!que.equals("")) {
                    if (mainQuestionList.contains(que)) {
                        remainQuestion.remove(que);
                    }
                }
            }
            System.out.println("remainQuestion===" + remainQuestion.toString());
        } catch (Exception e) {
            Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return remainQuestion;
    }

    public void setQuestionsList() {
        Cursor c = null;
        try {
            c = MyDatabaseInstanceHolder.getDatabaseHelper().getLoginQuestionsRecords();
            System.out.println("No of Questions : " + c.getCount());

            if (c.getCount() > 0) {
                mainQuestionList.add("");
                questionIDMap.put("", "0");
                if (c.moveToFirst()) {
                    do {
                        mainQuestionList.add(c.getString(1));
                        questionIDMap.put(c.getString(1), "" + c.getInt(0));
                    }
                    while (c.moveToNext());
                }
                c.close();
            } else {
                if (c != null)
                    c.close();
            }
        } catch (Exception e) {
            if (c != null)
                c.close();

            Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void setPreviousQuestionAnswer() {
        Cursor c = null;
        try {
            c = MyDatabaseInstanceHolder.getDatabaseHelper().getUserRecord();
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        preSelectedQuestionOne = c.getString(20);
                        if (!preSelectedQuestionOne.equals(""))
                            sp_SecurityQue1.setSelection((Integer.parseInt(preSelectedQuestionOne)));
                        else
                            sp_SecurityQue1.setSelection(0);

                        preSelectedQuestionTwo = c.getString(22);
                        if (!preSelectedQuestionTwo.equals(""))
                            sp_SecurityQue2.setSelection((Integer.parseInt(preSelectedQuestionTwo)));
                        else
                            sp_SecurityQue2.setSelection(0);

                        preSelectedQuestionThree = c.getString(24);
                        if (!preSelectedQuestionThree.equals(""))
                            sp_SecurityQue3.setSelection((Integer.parseInt(preSelectedQuestionThree)));
                        else
                            sp_SecurityQue3.setSelection(0);

                        ed_SecurityAnswer1.setText(c.getString(21));
                        ed_SecurityAnswer2.setText(c.getString(23));
                        ed_SecurityAnswer3.setText(c.getString(25));
                    }
                    while (c.moveToNext());
                }
                c.close();
            } else {
                if (c != null)
                    c.close();
            }
        } catch (Exception e) {
            if (c != null)
                c.close();

            Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillSpinnerSecurityQueOne() {
        try {
            adapter1 = new ArrayAdapter<CharSequence>(SecurityFragment.this.getActivity(), R.layout.spinner_selected_listitem, getRemainQuestionList());
            adapter1.setDropDownViewResource(R.layout.multiline_spinner_dropdown_listitem);
            sp_SecurityQue1.setAdapter(adapter1);
            sp_SecurityQue1.setPrompt("Question list");
            sp_SecurityQue1.setSelection(adapter1.getPosition(lastSelectedItem));
        } catch (Exception e) {
            Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillSpinnerSecurityQueTwo() {
        try {
            adapter2 = new ArrayAdapter<CharSequence>(SecurityFragment.this.getActivity(), R.layout.spinner_selected_listitem, getRemainQuestionList());
            adapter2.setDropDownViewResource(R.layout.multiline_spinner_dropdown_listitem);
            sp_SecurityQue2.setAdapter(adapter2);
            sp_SecurityQue2.setPrompt("Question list");
            sp_SecurityQue2.setSelection(adapter2.getPosition(lastSelectedItem));

        } catch (Exception e) {
            Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillSpinnerSecurityQueThree() {
        try {
            adapter3 = new ArrayAdapter<CharSequence>(SecurityFragment.this.getActivity(), R.layout.spinner_selected_listitem, getRemainQuestionList());
            adapter3.setDropDownViewResource(R.layout.multiline_spinner_dropdown_listitem);
            sp_SecurityQue3.setAdapter(adapter3);
            sp_SecurityQue3.setPrompt("Question list");
            sp_SecurityQue3.setSelection(adapter3.getPosition(lastSelectedItem));
        } catch (Exception e) {
            Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private class SelectDataTaskForSetUpdateLoginChallenge extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                ServicesHelper servicesHelper = new ServicesHelper();
                strMsg = servicesHelper.setUpdateLoginChallenge(mContext, strQuestionId1, strAnswer1, strQuestionId2, strAnswer2, strQuestionId3, strAnswer3);
            } catch (Exception e) {
                Utility.saveExceptionDetails(SecurityFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true")) {
                showToast(MessageInfo.strSaveSuccess, Toast.LENGTH_SHORT);
            } else {
                if (Utility.Connectivity_Internet(mContext)) {
                    if (!strMsg.equals(""))
                        showToast(strMsg, Toast.LENGTH_SHORT);
                } else {
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            }
        }
    }
}
