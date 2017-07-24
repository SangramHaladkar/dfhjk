package ism.manager.log;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.StaticVariables;

/**
 * Created by Raj on 04/04/16.
 */
public class SafetyIssueEmpRelationDialog extends Dialog {

    private Context sContext;
    private String dType;
    private String issueType;
    private String empFieldId;

    private TextView issue_title, txtQuestionIssue, txtError, dialogBtnClose;
    private RadioGroup issueRadioGroup;
    private RadioButton issueOneRadioBtn, issueTwoRadioBtn, issueThreeRadioBtn;
    private Spinner spinnerPersonList;
    private EditText edDescription;
    private Button btnAddIssue;
    private ScrollView dialogScrollView;

    private SafetyIssueEmpRelationDialogListener dialogListener;

    private ArrayAdapter<String> spinnerUserListAdapter;

    private HashMap<String, String> empIdMap = new HashMap<String, String>();

    public interface SafetyIssueEmpRelationDialogListener {
        public abstract void onAddIssuesButtonListener(ArrayList<HashMap<String, String>> selectedFlagList);
    }

    public void setCallBack(SafetyIssueEmpRelationDialogListener listener) {
        this.dialogListener = listener;
    }

    public SafetyIssueEmpRelationDialog(Context context, String dialogType) {
        super(context);
        this.sContext = context;
        this.dType = dialogType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.safetyissue_emp_relation_dialog);
        edDescription = (EditText) findViewById(R.id.edDescription);
        btnAddIssue = (Button) findViewById(R.id.btnAddIssue);
        issue_title = (TextView) findViewById(R.id.issue_title);
        txtQuestionIssue = (TextView) findViewById(R.id.txtQuestionIssue);
        txtError = (TextView) findViewById(R.id.txtError);
        dialogBtnClose = (TextView) findViewById(R.id.dialogBtnClose);
        spinnerPersonList = (Spinner) findViewById(R.id.spinnerPersonList);
        issueRadioGroup = (RadioGroup) findViewById(R.id.issueRadioGroup);
        issueOneRadioBtn = (RadioButton) findViewById(R.id.issueOneRadioBtn);
        issueTwoRadioBtn = (RadioButton) findViewById(R.id.issueTwoRadioBtn);
        issueThreeRadioBtn = (RadioButton) findViewById(R.id.issueThreeRadioBtn);
        dialogScrollView =(ScrollView)findViewById(R.id.scrollViewDialog);

        setCancelable(false);
        setListener();

        if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.safety_issues))) {
            issue_title.setText(sContext.getResources().getString(R.string.safety_issues));
            issueOneRadioBtn.setText(sContext.getResources().getString(R.string.accident));
            issueTwoRadioBtn.setText(sContext.getResources().getString(R.string.injury));
            issueThreeRadioBtn.setText(sContext.getResources().getString(R.string.property_damage));
            txtQuestionIssue.setVisibility(View.VISIBLE);

        } else if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.employee_relations))) {
            issue_title.setText(sContext.getResources().getString(R.string.employee_relations));
            issueOneRadioBtn.setText(sContext.getResources().getString(R.string.no_show));
            issueTwoRadioBtn.setText(sContext.getResources().getString(R.string.verbal_warning));
            issueThreeRadioBtn.setText(sContext.getResources().getString(R.string.written_warning));
            txtQuestionIssue.setVisibility(View.GONE);
        }
        issueOneRadioBtn.setChecked(true);
        issueType = issueOneRadioBtn.getText().toString().replaceAll("\\s", "");
        bindSpinnerAdapter();

        this.dialogScrollView.post(new Runnable() {
            @Override
            public void run() {
                dialogScrollView.fullScroll(View.FOCUS_UP);
            }
        });

    }

    private void setListener() {
        spinnerPersonList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!issueType.equals("NoShow")) {
                    if (position == 0) {
                        edDescription.setEnabled(false);
                        btnAddIssue.setEnabled(false);
                        txtError.setVisibility(View.GONE);
                    } else {
                        edDescription.setEnabled(true);
                        btnAddIssue.setEnabled(true);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialogBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        issueRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.issueOneRadioBtn) {
                    issueType = issueOneRadioBtn.getText().toString().replaceAll("\\s", "");
                    if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.safety_issues))) {
                        txtQuestionIssue.setText("Did any accidents take place?");
                        edDescription.setVisibility(View.VISIBLE);
                    } else if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.employee_relations))) {
                        edDescription.setVisibility(View.INVISIBLE);
                        empFieldId = "1";
                    }
                } else if (checkedId == R.id.issueTwoRadioBtn) {
                    issueType = issueTwoRadioBtn.getText().toString().replaceAll("\\s", "");

                    if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.safety_issues))) {
                        txtQuestionIssue.setText("Did any employee injuries occur?");
                    } else if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.employee_relations))) {
                        empFieldId = "2";

                    }
                    edDescription.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.issueThreeRadioBtn) {
                    issueType = issueThreeRadioBtn.getText().toString().replaceAll("\\s", "");
                    if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.safety_issues))) {
                        txtQuestionIssue.setText("Did any property damage occur?");
                    } else if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.employee_relations))) {
                        empFieldId = "3";
                    }
                    edDescription.setVisibility(View.VISIBLE);
                }

            }
        });
        btnAddIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edDescription.getWindowToken(), 0);
                if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.safety_issues))) {
                    addIssues();
                } else if (dType.equalsIgnoreCase(sContext.getResources().getString(R.string.employee_relations))) {
                    addEmpRelations();
                }
            }
        });

        edDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0)
                    txtError.setVisibility(View.GONE);
            }
        });

    }

    private void bindSpinnerAdapter() {
        try {

            spinnerUserListAdapter = new ArrayAdapter<String>(sContext, android.R.layout.simple_spinner_item);
            spinnerUserListAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spinnerUserListAdapter.add("Select Name of Person");

            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getAddressBookRecord();

            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getString(0).contains("-0") && c.getString(4).equals("" + StaticVariables.getOrgID(sContext))) {
                        empIdMap.put(c.getString(1) + "  " + c.getString(2), c.getString(0));
                        spinnerUserListAdapter.add(c.getString(1) + "  " + c.getString(2));
                    }
                }
            }

            c.close();
            spinnerPersonList.setAdapter(spinnerUserListAdapter);
            spinnerPersonList.setPrompt("User List");

        } catch (Exception e) {
            Utility.saveExceptionDetails(sContext, e);
            e.printStackTrace();
        }
    }

    private void addIssues() {
        ArrayList<HashMap<String, String>> issueList = new ArrayList<HashMap<String, String>>();
        try {
            if (edDescription.getText().toString().equals("")) {
                txtError.setText(MessageInfo.strSafetyDescription);
                txtError.setVisibility(View.VISIBLE);
            } else {
                txtError.setVisibility(View.GONE);

                HashMap<String, String> hashSafetyIssue = new HashMap<String, String>();
                String safetyLogId = "";
                if (issueType.equals(sContext.getResources().getString(R.string.accident))) {
                    safetyLogId = ActivityStringInfo.strSafetyLogIdForAccidents;
                } else if (issueType.equals(sContext.getResources().getString(R.string.injury))) {
                    safetyLogId = ActivityStringInfo.strSafetyLogIdForInjury;
                } else if (issueType.equals(sContext.getResources().getString(R.string.propertydamage))) {
                    safetyLogId = ActivityStringInfo.strSafetyLogIdForDamage;
                }
                hashSafetyIssue.put(DatabaseConstant.key_SAFETYLOGID, safetyLogId);
                hashSafetyIssue.put(DatabaseConstant.key_EMPLOYEEID, empIdMap.get(spinnerPersonList.getSelectedItem().toString()).replace("-0", ""));
                hashSafetyIssue.put(DatabaseConstant.key_ADDEDBYID, ActivityStringInfo.strUser_id);
                hashSafetyIssue.put(DatabaseConstant.key_ADDEDBY_NAME, ActivityStringInfo.strFirstName + "  " + ActivityStringInfo.strLastName);
                hashSafetyIssue.put(DatabaseConstant.key_EMPLOYEE_NAME, spinnerPersonList.getSelectedItem().toString());
                hashSafetyIssue.put(DatabaseConstant.key_DESCRIPTION, edDescription.getText().toString());
                hashSafetyIssue.put(DatabaseConstant.key_TYPE, issueType);
                hashSafetyIssue.put(DatabaseConstant.key_STATUS, "N");
                issueList.add(hashSafetyIssue);
                dialogListener.onAddIssuesButtonListener(issueList);
                dismiss();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(sContext, e);
            e.printStackTrace();
        }
    }


    private void addEmpRelations() {
        ArrayList<HashMap<String, String>> empRelationsList = new ArrayList<HashMap<String, String>>();
        try {
            if (!issueType.equals("NoShow") && edDescription.getText().toString().equals("")) {
                txtError.setText(MessageInfo.strMaintainSubject);
            } else {
                HashMap<String, String> hashEmpRelations = new HashMap<String, String>();
                hashEmpRelations.put(DatabaseConstant.key_EMPLOGID, "");
                hashEmpRelations.put(DatabaseConstant.key_LOGID, ActivityStringInfo.selectedLogId);
                hashEmpRelations.put(DatabaseConstant.key_EMPFIELDID, empFieldId);
                hashEmpRelations.put(DatabaseConstant.key_ADDEDBYID, ActivityStringInfo.strUser_id);
                hashEmpRelations.put(DatabaseConstant.key_EMPLOYEE, empIdMap.get(spinnerPersonList.getSelectedItem().toString()).replace("-0", ""));
                hashEmpRelations.put(DatabaseConstant.key_DESCRIPTION, edDescription.getText().toString());
                hashEmpRelations.put(DatabaseConstant.key_TYPE, issueType);
                hashEmpRelations.put(DatabaseConstant.key_EMPLOYEE_NAME, spinnerPersonList.getSelectedItem().toString());
                hashEmpRelations.put(DatabaseConstant.key_ADDEDBY_NAME, ActivityStringInfo.strFirstName + "  " + ActivityStringInfo.strLastName);
                hashEmpRelations.put(DatabaseConstant.key_STATUS, "N");
                empRelationsList.add(hashEmpRelations);
                dialogListener.onAddIssuesButtonListener(empRelationsList);
                dismiss();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(sContext, e);
            e.printStackTrace();
        }
    }
}
