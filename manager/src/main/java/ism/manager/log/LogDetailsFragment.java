package ism.manager.log;


import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ism.manager.ActivityStringInfo;
import ism.manager.MyDatabaseInstanceHolder;
import ism.manager.R;
import ism.manager.SplashActivity;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.utils.ActionItem;
import ism.manager.utils.DatabaseConstant;
import ism.manager.utils.DatePickerFragment;
import ism.manager.utils.MessageInfo;
import ism.manager.utils.QuickAction;
import ism.manager.utils.StaticVariables;
import ism.manager.webservices.ServicesHelper;
import ism.manager.webservices.Synchronization;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogDetailsFragment extends AppBaseFragment {


    private LinearLayout headerLayout;
    private LinearLayout mangerRoleLayout;
    private LinearLayout flagsLayout;
    private LinearLayout safetyIssuesLayout;
    private LinearLayout productivityLayout;
    private LinearLayout employeeRelationsLayout;
    private LinearLayout communicationLayout;
    private LinearLayout qualityLayout;
    private LinearLayout urgentIssueLayout;
    private LinearLayout msgApprovalLayout;

    private LinearLayout safetyListHeader;
    private LinearLayout qualityListHeader;
    private LinearLayout empRelationListHeader;
    private LinearLayout repairsListHeader;
    private LinearLayout notesListHeader;


    private LinearLayout lyl_Bank;
    private TextView flagCountTxt, employeeRelationsTxt, txtUrgentIssues, txtUrgentIssuesRemains, txtMsgAprovals, txt_FollowUp, txt_AllNewRepair;
    private ImageView addFlagBtn, addSafetyIssueBtn, addEmpRelationsBtn;

    private TextView txt_Date, safetyIssueCountTxt;
    private TextView txt_BankDeposit, txt_SelectOne;
    private EditText ed_BankDepositComment;
    private RadioGroup rbnGrp_BankDeposit, rbnGrpUrgent, rbnGrpMsgApproval, rbnGrpRepair, rbnGrpFollowUp;
    private RadioButton rbn_YesBankDeposit, rbn_NoBankDeposit, rbn_YesUrgent, rbn_NoneToReportUrgent, rbn_YesMessage, rbn_NoneToReportMessage, rbnYesRepair, rbnNoneToLogRepair, rbnYesFollowUp, rbnNoneToLogFollowUp;

    private TextView txtLocation;
    private TextView txtEnteredDate;
    private TextView txtEnteredBy;
    private EditText edtLogDate;
    private EditText edCommentQuality, edItemRepaired, edAddNote;
    private Button btnAddQuality, btnAddNote, btnAddRepaired;
    private ListView lst_SafetyIssues, lst_CommentQuality, lst_Productivity, lst_EmpRelations, lst_RepairedItems, lst_AdditionalNotes;

    private ListView lst_Manager, lst_Flags;

    private ScrollView scrollViewLogDetails;

    private MenuItem saveMenuItem;

    //Other Class Declaration
    ActivityStringInfo stringInfo;

    //Variable Declaration
    public static final String INFO = "INFO";
    private String isUpdateOrInsertRecord = "";
    int mMonth, mYear, mDay;
    Context mContext;
    ShowLogFlagAdapter logsAddFlagAdapter;
    ArrayList<AddFlagItem> listToBindFlag = new ArrayList<AddFlagItem>();
    ArrayList<HashMap<String, String>> safetyIssuesList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> employeeRelationsList = new ArrayList<HashMap<String, String>>();

    boolean isFieldValueEnable = true;

    boolean isDisableAll = false;

    String isUpdateOrInsertRecordForMain = "";
    String isUpdateOrInsertRecordForFoll = "";
    String isUpdateOrInsertRecordForFlags = "";

    public boolean saveClick = false;
    public boolean completeClick = false;

    public boolean isSaveCompleteEnable = true;

    public LogDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = LogDetailsFragment.this.getActivity();
        if (getArguments().containsKey(INFO)) {
            stringInfo = (ActivityStringInfo) getArguments().getSerializable(INFO);
        }
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }

        isDisableAll = getArguments().getBoolean("isDisable", false);
        Log.v("isUpdateOrInsert0:- ", "onCreate " + isUpdateOrInsertRecord);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_details, container, false);
        try {
            this.inItFlagIconList();
            this.initViews(view);
            this.setListeners();
            this.setValues();
            this.scrollViewLogDetails.post(new Runnable() {
                @Override
                public void run() {
                    scrollViewLogDetails.fullScroll(View.FOCUS_UP);
                }
            });
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_log_details, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_cancel) {
//            LogDetailsFragment.this.getActivity().finish();
//            return true;
//        } else if (id == R.id.action_save) {
//            if (isSaveCompleteEnable) {
//                saveClick = true;
//                completeClick = false;
//                logDetailEntry();
//            }
//            return true;
//        } else if (id == R.id.action_complete) {
//            if (isSaveCompleteEnable && validate()) {
//                completeClick = true;
//                saveClick = false;
//                logDetailEntry();
//            }
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void initViews(View v) {
        this.scrollViewLogDetails = (ScrollView) v.findViewById(R.id.scrollViewLogDetails);
        //Header Layout
        this.headerLayout = (LinearLayout) v.findViewById(R.id.headerLayout);
        this.txtLocation = (TextView) v.findViewById(R.id.txtLocation);
        this.txtEnteredDate = (TextView) v.findViewById(R.id.txtEnteredDate);
        this.txtEnteredBy = (TextView) v.findViewById(R.id.txtEnteredBy);
        this.edtLogDate = (EditText) v.findViewById(R.id.edtLogDate);

        //Manager List Layout
        this.mangerRoleLayout = (LinearLayout) v.findViewById(R.id.mangerRoleLayout);
        this.lst_Manager = (ListView) v.findViewById(R.id.lstManager);
        this.lst_Manager.setDivider(null);
        this.lst_Manager.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        this.txt_SelectOne = (TextView) v.findViewById(R.id.txtSelectOne);

        //Flag List Layout
        this.flagsLayout = (LinearLayout) v.findViewById(R.id.flagsLayout);
        this.flagCountTxt = (TextView) v.findViewById(R.id.flagCountTxt);
        this.addFlagBtn = (ImageView) v.findViewById(R.id.addFlagBtn);
        this.lst_Flags = (ListView) v.findViewById(R.id.lstFlags);

        //Safety Layout
        this.safetyIssuesLayout = (LinearLayout) v.findViewById(R.id.safetyIssuesLayout);
        this.lst_SafetyIssues = (ListView) v.findViewById(R.id.lstSafetyIssues);
        this.addSafetyIssueBtn = (ImageView) v.findViewById(R.id.addSafetyIssueBtn);
        this.safetyIssueCountTxt = (TextView) v.findViewById(R.id.safetyIssueCountTxt);

        //Quality comment layout
        this.qualityLayout = (LinearLayout) v.findViewById(R.id.qualityLayout);
        this.lst_CommentQuality = (ListView) v.findViewById(R.id.lstQualityComment);
        this.edCommentQuality = (EditText) v.findViewById(R.id.edCommentQuality);
        this.btnAddQuality = (Button) v.findViewById(R.id.btnAddQuality);

        //Product Layout
        this.productivityLayout = (LinearLayout) v.findViewById(R.id.productivityLayout);
        this.lyl_Bank = (LinearLayout) v.findViewById(R.id.lylBank);
        this.ed_BankDepositComment = (EditText) v.findViewById(R.id.edBankDepositComment);
        this.rbn_YesMessage = (RadioButton) v.findViewById(R.id.rbnYesMsgApproval);
        this.rbn_YesBankDeposit = (RadioButton) v.findViewById(R.id.rbnYesBankDeposit);
        this.rbn_NoBankDeposit = (RadioButton) v.findViewById(R.id.rbnNoBankDeposit);
        this.lst_Productivity = (ListView) v.findViewById(R.id.lstProductivity);
        this.txt_BankDeposit = (TextView) v.findViewById(R.id.txtBankDeposit);
        //Employee Relations Layout
        this.employeeRelationsLayout = (LinearLayout) v.findViewById(R.id.employeeRelationsLayout);

        this.lst_EmpRelations = (ListView) v.findViewById(R.id.lstEmpRelations);
        this.employeeRelationsTxt = (TextView) v.findViewById(R.id.employeeRelationsTxt);
        this.addEmpRelationsBtn = (ImageView) v.findViewById(R.id.addEmpRelationsBtn);

        //Communication Layout
        this.communicationLayout = (LinearLayout) v.findViewById(R.id.communicationLayout);
        this.urgentIssueLayout = (LinearLayout) v.findViewById(R.id.urgentIssueLayout);
        this.msgApprovalLayout = (LinearLayout) v.findViewById(R.id.msgApprovalLayout);
        this.txtUrgentIssues = (TextView) v.findViewById(R.id.txtUrgentIssues);
        this.txtUrgentIssuesRemains = (TextView) v.findViewById(R.id.txtUrgentIssuesRemains);
        this.txtMsgAprovals = (TextView) v.findViewById(R.id.txtMsgAproval);


        this.rbn_NoneToReportMessage = (RadioButton) v.findViewById(R.id.rbnNoneToResolveMsgApproval);
        this.rbn_NoneToReportUrgent = (RadioButton) v.findViewById(R.id.rbnNoneToReportUrgent);
        this.rbn_YesMessage = (RadioButton) v.findViewById(R.id.rbnYesMsgApproval);
        this.rbn_YesUrgent = (RadioButton) v.findViewById(R.id.rbnYesUrgent);


        //Maintenance Layout
        this.lst_RepairedItems = (ListView) v.findViewById(R.id.lstRepairedItems);
        this.lst_AdditionalNotes = (ListView) v.findViewById(R.id.lstAdditionalNotes);

        this.txt_FollowUp = (TextView) v.findViewById(R.id.txtFollowsup);
        this.txt_AllNewRepair = (TextView) v.findViewById(R.id.txtAllNewRepair);

        this.edItemRepaired = (EditText) v.findViewById(R.id.edItemRepaired);
        this.edAddNote = (EditText) v.findViewById(R.id.edAddNote);
        this.btnAddRepaired = (Button) v.findViewById(R.id.btnAddRepaired);
        this.btnAddNote = (Button) v.findViewById(R.id.btnAddNote);

        this.rbnYesRepair = (RadioButton) v.findViewById(R.id.rbnYesRepair);
        this.rbnNoneToLogRepair = (RadioButton) v.findViewById(R.id.rbnNoneToLogRepair);
        this.rbnYesFollowUp = (RadioButton) v.findViewById(R.id.rbnYesFollowUp);
        this.rbnNoneToLogFollowUp = (RadioButton) v.findViewById(R.id.rbnNoneToLogFollowUp);

        this.safetyListHeader = (LinearLayout) v.findViewById(R.id.safetyListHeader);
        this.qualityListHeader = (LinearLayout) v.findViewById(R.id.qualityListHeader);
        this.empRelationListHeader = (LinearLayout) v.findViewById(R.id.empRelationListHeader);
        this.repairsListHeader = (LinearLayout) v.findViewById(R.id.repairsListHeader);
        this.notesListHeader = (LinearLayout) v.findViewById(R.id.notesListHeader);

    }

    private void setListeners() {
        this.edtLogDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    LogDetailsFragment.this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    showDatePicker();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.addFlagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFlagDialog addFlagDialog = new AddFlagDialog(LogDetailsFragment.this.getActivity(), listToBindFlag);
                addFlagDialog.setCallBack(new AddFlagDialog.AddFlagDialogListener() {
                    @Override
                    public void onAddFlagButtonListener(ArrayList<AddFlagItem> selectedFlagList) {
                        ArrayList<AddFlagItem> flList = new ArrayList<AddFlagItem>();
                        flList.addAll(selectedFlagList);
                        listToBindFlag.clear();
                        listToBindFlag.addAll(flList);
                        setFlagListAdapter(listToBindFlag);
                        showToast("Flag Added", Toast.LENGTH_SHORT);
                    }
                });
                addFlagDialog.show();
            }
        });

        this.addSafetyIssueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafetyIssueEmpRelationDialog safetyIssueEmpRelationDialog = new SafetyIssueEmpRelationDialog(LogDetailsFragment.this.getActivity(), getResources().getString(R.string.safety_issues));
                safetyIssueEmpRelationDialog.setCallBack(new SafetyIssueEmpRelationDialog.SafetyIssueEmpRelationDialogListener() {
                    @Override
                    public void onAddIssuesButtonListener(ArrayList<HashMap<String, String>> selectedIssueList) {
                        try {
                            safetyIssuesList.addAll(selectedIssueList);
                            ActivityStringInfo.safetyIssueList.addAll(selectedIssueList);

                            if (safetyIssuesList != null && safetyIssuesList.size() > 0) {
                                safetyListHeader.setVisibility(View.VISIBLE);
                                lst_SafetyIssues.setVisibility(View.VISIBLE);
                            } else {
                                safetyListHeader.setVisibility(View.GONE);
                                lst_SafetyIssues.setVisibility(View.GONE);
                            }
                            lst_SafetyIssues.setAdapter(new MyArrayAdapterSafetyList(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_emp_and_safety, safetyIssuesList));
                            Utility.setListViewHeightBasedOnChildren(mContext, lst_SafetyIssues);

                        } catch (Exception e) {
                            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                            e.printStackTrace();
                        }
                    }
                });
                safetyIssueEmpRelationDialog.show();
                edCommentQuality.clearFocus();
                edItemRepaired.clearFocus();
                edAddNote.clearFocus();

            }
        });

        this.addEmpRelationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafetyIssueEmpRelationDialog safetyIssueEmpRelationDialog = new SafetyIssueEmpRelationDialog(LogDetailsFragment.this.getActivity(), getResources().getString(R.string.employee_relations));
                safetyIssueEmpRelationDialog.setCallBack(new SafetyIssueEmpRelationDialog.SafetyIssueEmpRelationDialogListener() {
                    @Override
                    public void onAddIssuesButtonListener(ArrayList<HashMap<String, String>> selectedEmpRelationList) {
                        try {
                            employeeRelationsList.addAll(selectedEmpRelationList);
                            ActivityStringInfo.employeeRelations.addAll(selectedEmpRelationList);
                            if (employeeRelationsList != null && employeeRelationsList.size() > 0) {
                                empRelationListHeader.setVisibility(View.VISIBLE);
                                lst_EmpRelations.setVisibility(View.VISIBLE);
                            } else {
                                empRelationListHeader.setVisibility(View.GONE);
                                lst_EmpRelations.setVisibility(View.GONE);
                            }
                            lst_EmpRelations.setAdapter(new MyArrayAdapterEmpRelationsList(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_emp_and_safety, employeeRelationsList));
                            Utility.setListViewHeightBasedOnChildren(mContext, lst_EmpRelations);

                        } catch (Exception e) {
                            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                            e.printStackTrace();
                        }
                    }
                });
                safetyIssueEmpRelationDialog.show();
                edCommentQuality.clearFocus();
                edItemRepaired.clearFocus();
                edAddNote.clearFocus();
            }
        });


        btnAddQuality.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edCommentQuality.getWindowToken(), 0);
                    if (edCommentQuality.getText().toString().equals("")) {
                        showToast(MessageInfo.strSafetyQuality, Toast.LENGTH_SHORT);
                    } else {
                        HashMap<String, String> hashPersonQuality = new HashMap<String, String>();

                        hashPersonQuality.put(DatabaseConstant.key_QUALITYID, "");
                        hashPersonQuality.put(DatabaseConstant.key_LOGID, ActivityStringInfo.selectedLogId);
                        hashPersonQuality.put(DatabaseConstant.key_USERID, ActivityStringInfo.strUser_id);
                        hashPersonQuality.put(DatabaseConstant.key_COMPLAINT, edCommentQuality.getText().toString());
                        hashPersonQuality.put(DatabaseConstant.key_TYPE, "Quality");
                        hashPersonQuality.put(DatabaseConstant.key_ADDEDBY_NAME, ActivityStringInfo.strFirstName + "  " + ActivityStringInfo.strLastName);
                        hashPersonQuality.put(DatabaseConstant.key_STATUS, "N");

                        ActivityStringInfo.personQuality.add(hashPersonQuality);
                        fillPersonQualityList();
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });

        btnAddNote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edAddNote.getWindowToken(), 0);
                    if (edAddNote.getText().toString().equals("")) {
                        showToast(MessageInfo.logMainNotes, Toast.LENGTH_SHORT);
                    } else {
                        HashMap<String, String> hashMaintenanceNotes = new HashMap<String, String>();
                        hashMaintenanceNotes.put(DatabaseConstant.key_ID, "");
                        hashMaintenanceNotes.put(DatabaseConstant.key_LOGID, ActivityStringInfo.selectedLogId);
                        hashMaintenanceNotes.put(DatabaseConstant.key_EMPLOYEE, ActivityStringInfo.strUser_id);
                        hashMaintenanceNotes.put(DatabaseConstant.key_DESCRIPTION, edAddNote.getText().toString());
                        hashMaintenanceNotes.put(DatabaseConstant.key_EMPLOYEE_NAME, ActivityStringInfo.strFirstName + "  " + ActivityStringInfo.strLastName);
                        hashMaintenanceNotes.put(DatabaseConstant.key_STATUS, "N");
                        ActivityStringInfo.maintenanceNotes.add(hashMaintenanceNotes);
                        fillMaintenanceNotes();
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });

        btnAddRepaired.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edItemRepaired.getWindowToken(), 0);
                    if (edItemRepaired.getText().toString().equals("")) {
                        showToast(MessageInfo.logMainRepairs, Toast.LENGTH_SHORT);
                    } else {
                        HashMap<String, String> hashMaintenanceRepaired = new HashMap<String, String>();
                        hashMaintenanceRepaired.put(DatabaseConstant.key_MRRDETAILSID, "");
                        hashMaintenanceRepaired.put(DatabaseConstant.key_LOGID, ActivityStringInfo.selectedLogId);
                        hashMaintenanceRepaired.put(DatabaseConstant.key_MRRID, "2");
                        hashMaintenanceRepaired.put(DatabaseConstant.key_EMPLOYEE, ActivityStringInfo.strUser_id);
                        hashMaintenanceRepaired.put(DatabaseConstant.key_DESCRIPTION, edItemRepaired.getText().toString());
                        hashMaintenanceRepaired.put(DatabaseConstant.key_TASKID, "");
                        hashMaintenanceRepaired.put(DatabaseConstant.key_EMPLOYEE_NAME, ActivityStringInfo.strFirstName + "  " + ActivityStringInfo.strLastName);
                        hashMaintenanceRepaired.put(DatabaseConstant.key_STATUS, "N");
                        ActivityStringInfo.maintenanceRepaired.add(hashMaintenanceRepaired);
                        fillMaintenanceRepaired();
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            }
        });
    }

    private QuickAction showAction(String textShow) {
        ActionItem addAction = new ActionItem();
        addAction.setTitle(textShow);
        addAction.setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_ism));

        final QuickAction mQuickAction = new QuickAction(mContext);
        mQuickAction.addActionItem(addAction);
        return mQuickAction;
    }

    private void setValues() {
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strFirstName.equals("") || ActivityStringInfo.strCompanyName.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
        //HedarLayout
        this.fillHeaderDetails();
        //ManagerListLayout
        this.fillManagerMap();
        this.fillManagerList();
        //FlagLayout
        this.fillLogFlagMap();
        this.fillFlagsList();
        //SafetyLayout
        ActivityStringInfo.safetyIssueList.clear();
        this.fillAccidentMap();
        this.fillInjuryMap();
        this.fillDamageMap();
        this.fillSafetyList();

        //Quality Comment
        this.fillQualityMap();
        this.fillPersonQualityList();

        //Productivity Layout
        if (ActivityStringInfo.selectedUserBankFlag.toLowerCase().equals("true")) {
            setBankDepositFlags();
        } else {
            lyl_Bank.setVisibility(View.GONE);

        }
        bindAdapterProductivity();

        //Employee Relations Layout
        ActivityStringInfo.employeeRelations.clear();
        fillNoShowMap();
        fillVerbalWarningMap();
        fillWrittenWarningMap();
        setCommunicationDetail();

//        bindAdapterForSpinnerInEmpRelations();
        fillEmployeeRelationList();
//        setSpinnerValueForEmpRelations();

        // for urgent option
        if (ActivityStringInfo.chkDetail3_urgent.equals("0"))
            rbn_YesUrgent.setChecked(true);
        else if (ActivityStringInfo.chkDetail3_urgent.equals("1"))
            rbn_NoneToReportUrgent.setChecked(true);

        // for message option
        if (ActivityStringInfo.chkDetail3_message.equals("0"))
            rbn_YesMessage.setChecked(true);
        else if (ActivityStringInfo.chkDetail3_message.equals("1"))
            rbn_NoneToReportMessage.setChecked(true);

        if (!ActivityStringInfo.isCreateNewLog) {
            if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                setDisableAll();
            } else {
                if (Utility.compareLastSyncForDisable(mContext)) {
                    setDisableAll();
                }
            }
            if (isDisableAll) {
                setDisableAll();
            }
        }

        ActionItem addAction = new ActionItem();
        addAction.setTitle("Urgent Issues: All Safety, Misconduct, Inappropriate Behavior, or Special Circumstances.");
        addAction.setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_ism));

        final QuickAction mQuickAction = new QuickAction(mContext);
        mQuickAction.addActionItem(addAction);

        txtUrgentIssues.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mQuickAction.show(v);
            }
        });

        //Maintenance Layout
        fillMaintenanceRepairedMap();
        fillMaintenanceNotesMap();

        setMaintenanceRepairedDetail();
        setFollowUpDetail();

//        txt_Date.setText(ActivityStringInfo.selectedLogForDate);

        fillMaintenanceRepaired();
        fillMaintenanceNotes();

        // for new repair option
        if (ActivityStringInfo.chkDetail4_newRepair.equals("0"))
            rbnYesRepair.setChecked(true);
        else if (ActivityStringInfo.chkDetail4_newRepair.equals("1"))
            rbnNoneToLogRepair.setChecked(true);

        //for follow up option
        if (ActivityStringInfo.chkDetail4_followUP.equals("0"))
            rbnYesFollowUp.setChecked(true);
        else if (ActivityStringInfo.chkDetail4_followUP.equals("1"))
            rbnNoneToLogFollowUp.setChecked(true);


    }

    /**
     * create safety log id
     *
     * @return maxSafetyLogId
     */
    private String createLogsSafetyLog() {
        String maxSafetyLogId = "";
        Cursor c = null;
        try {
            c = MyDatabaseInstanceHolder.getDatabaseHelper().getMaxSafetyLogId();
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getString(0).equals(null))
                        maxSafetyLogId = "0";
                    else
                        maxSafetyLogId = c.getString(0);
                }
            } else {
                maxSafetyLogId = "0";
            }

            if (c != null)
                c.close();


        } catch (Exception e) {
            maxSafetyLogId = "0";
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        System.out.println("maxSafetyLogId===" + maxSafetyLogId);
        return maxSafetyLogId;
    }

    /**
     * Fill Header Details of Log i.e. Log for the day of, Location ..etc.
     */
    private void fillHeaderDetails() {
        if (ActivityStringInfo.isCreateNewLog) {
            try {
                this.isUpdateOrInsertRecord = "insert";
                Log.v("isUpdateOrInsert1:- ", isUpdateOrInsertRecord);
                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getSelectedLogDetail(ActivityStringInfo.selectedLogId);
                if (c.getCount() > 0) {
                    this.isUpdateOrInsertRecord = "update";
                    Log.v("isUpdateOrInsert2:- ", isUpdateOrInsertRecord);
                    if (c.moveToFirst()) {
                        do {
                            this.edtLogDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            this.edtLogDate.setText(StaticVariables.dayWithDateFormat.format((StaticVariables.generalDateFormat.parse(c.getString(1)))));
                            this.edtLogDate.setEnabled(false);
                            this.edtLogDate.setClickable(false);
                            this.txtEnteredDate.setText(c.getString(2));
                            ActivityStringInfo.selectedCreatedDate = c.getString(2);
                            this.txtEnteredBy.setText(c.getString(4));
                            ActivityStringInfo.selectedCreatedUserId = c.getString(3);
                            if (ActivityStringInfo.selectedUserTypeId.equals(""))
                                ActivityStringInfo.selectedUserTypeId = (c.getString(5));

                            this.txtLocation.setText(ActivityStringInfo.strCompanyName);

                        } while (c.moveToNext());
                    }
                    c.close();
                } else {
                    c.close();
                    setLocalSystemDate();
                    this.txtEnteredBy.setText(ActivityStringInfo.strFirstName + "  " + ActivityStringInfo.strLastName);
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
                    this.txtEnteredDate.setText(sdfDate.format(cal.getTime()));
                    this.txtLocation.setText(ActivityStringInfo.strCompanyName);

                    ActivityStringInfo.strSafetyLogIdForAccidents = "" + (Integer.parseInt(createLogsSafetyLog()) + 1);
                    ActivityStringInfo.strSafetyLogIdForInjury = "" + (Integer.parseInt(ActivityStringInfo.strSafetyLogIdForAccidents) + 1);
                    ActivityStringInfo.strSafetyLogIdForDamage = "" + (Integer.parseInt(ActivityStringInfo.strSafetyLogIdForInjury) + 1);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        } else {
            Cursor c = null;
            try {
                c = MyDatabaseInstanceHolder.getDatabaseHelper().getSelectedLogDetail(ActivityStringInfo.selectedLogId);
                if (c.getCount() > 0) {
                    this.isUpdateOrInsertRecord = "update";
                    Log.v("isUpdateOrInsert3:- ", isUpdateOrInsertRecord);
                    if (c.moveToFirst()) {
                        do {
                            this.edtLogDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            this.edtLogDate.setText(StaticVariables.dayWithDateFormat.format((StaticVariables.generalDateFormat.parse(c.getString(1)))));
                            this.edtLogDate.setEnabled(false);
                            this.edtLogDate.setClickable(false);
                            this.txtEnteredDate.setText(c.getString(2));
                            ActivityStringInfo.selectedCreatedDate = c.getString(2);
                            this.txtEnteredBy.setText(c.getString(4));
                            ActivityStringInfo.selectedCreatedUserId = c.getString(3);
                            if (ActivityStringInfo.selectedUserTypeId.equals(""))
                                ActivityStringInfo.selectedUserTypeId = (c.getString(5));

                            this.txtLocation.setText(ActivityStringInfo.strCompanyName);

                        } while (c.moveToNext());
                    }
                } else {
                    this.isUpdateOrInsertRecord = "insert";
                    Log.v("isUpdateOrInsert4:- ", isUpdateOrInsertRecord);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                c.close();
            }

        }
    }

    /**
     * update log date
     *
     * @param Date
     */
    private void updateDate(String Date) {
        try {
            this.edtLogDate.setText(Date);
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    /**
     * set Local System Date
     */
    public void setLocalSystemDate() {
        final Calendar c = Calendar.getInstance();
        if (!ActivityStringInfo.selectedLogForDate.equals("")) {
            updateDate(ActivityStringInfo.selectedLogForDate);

            String[] strDate = ActivityStringInfo.selectedLogForDate.split("/");

            mYear = Integer.parseInt(strDate[2]);
            mMonth = Integer.parseInt(strDate[0]) - 1;
            mDay = Integer.parseInt(strDate[1]);

        } else {
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            updateDate(StaticVariables.dayWithDateFormat.format(c.getTime()));//changed date format
        }
        try {
            Date dtpre = getUptoUpdateTime(StaticVariables.generalDateFormat.format(c.getTime()));

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
            if (sdf.parse(ActivityStringInfo.strLastSyncDate).after(dtpre)) {
//                btn_Complete.setEnabled(false);
//                btn_Complete.setClickable(false);
//                btn_Complete.setTextColor(Color.parseColor("#A7A7A7"));
//
//                btn_Complete_Bottom.setEnabled(false);
//                btn_Complete_Bottom.setClickable(false);
//                btn_Complete_Bottom.setTextColor(Color.parseColor("#A7A7A7"));
//
//                btn_Save.setEnabled(false);
//                btn_Save.setClickable(false);
//                btn_Save.setTextColor(Color.parseColor("#A7A7A7"));
//
//                btn_Save_Bottom.setEnabled(false);
//                btn_Save_Bottom.setClickable(false);
//                btn_Save_Bottom.setTextColor(Color.parseColor("#A7A7A7"));
                //TODO disable save button also
            } else {
//                btn_Complete.setEnabled(true);
//                btn_Complete.setClickable(true);
//                btn_Complete.setTextColor(Color.parseColor("#ffffff"));
//
//                btn_Complete_Bottom.setEnabled(true);
//                btn_Complete_Bottom.setClickable(true);
//                btn_Complete_Bottom.setTextColor(Color.parseColor("#ffffff"));
//
//                btn_Save.setEnabled(true);
//                btn_Save.setClickable(true);
//                btn_Save.setTextColor(Color.parseColor("#ffffff"));
//
//                btn_Save_Bottom.setEnabled(true);
//                btn_Save_Bottom.setClickable(true);
//                btn_Save_Bottom.setTextColor(Color.parseColor("#ffffff"));
                //TODO disable save button also
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    /***
     * convert string date to Date object
     *
     * @param date
     * @return Date
     */
    public Date getUptoUpdateTime(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            String[] strDate = date.split("/");

            cal.set(Calendar.MONTH, Integer.parseInt(strDate[0]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strDate[1]));
            cal.set(Calendar.YEAR, Integer.parseInt(strDate[2]));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            System.out.println("ActivityStringInfo.strDailyLogHour)== sssss " + Math.round(Double.parseDouble(ActivityStringInfo.strDailyLogHour)));
            cal.add(Calendar.HOUR, (int) Math.round(Double.parseDouble(ActivityStringInfo.strDailyLogHour)));

            System.out.println("strUpdateTime== sssss " + cal.getTime());

        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return cal.getTime();
    }

    /***
     * used to display Date Picker
     */
    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(LogDetailsFragment.this.getFragmentManager(), "Date Picker");
    }

    /***
     * Listener used on set of date from date picker
     */
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try {
//                btn_Complete.setEnabled(true);
//                btn_Complete.setClickable(true);
//                btn_Complete.setTextColor(Color.parseColor("#ffffff"));
//
//                btn_Complete_Bottom.setEnabled(true);
//                btn_Complete_Bottom.setClickable(true);
//                btn_Complete_Bottom.setTextColor(Color.parseColor("#ffffff"));
                //TODO disable save button

                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, mYear);
                cal.set(Calendar.MONTH, mMonth);
                cal.set(Calendar.DAY_OF_MONTH, mDay);
                System.out.println("cal=====" + StaticVariables.generalDateFormat.format(cal.getTime()));
                Date dtpre = getUptoUpdateTime(StaticVariables.generalDateFormat.format(cal.getTime()));

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);

                if (sdf.parse(ActivityStringInfo.strLastSyncDate).before(cal.getTime())) {
                    showToast(MessageInfo.strNextDate, Toast.LENGTH_SHORT);
//                    setLocalSystemDate();
//                    btn_Complete.setEnabled(false);
//                    btn_Complete.setClickable(false);
//                    btn_Complete.setTextColor(Color.parseColor("#A7A7A7"));
//
//                    btn_Complete_Bottom.setEnabled(false);
//                    btn_Complete_Bottom.setClickable(false);
//                    btn_Complete_Bottom.setTextColor(Color.parseColor("#A7A7A7"));
                    //TODO disable save button

                } else if (sdf.parse(ActivityStringInfo.strLastSyncDate).after(dtpre)) {
                    showToast(MessageInfo.strLogsNotExistOnDate, Toast.LENGTH_SHORT);
                    setLocalSystemDate();
//                    btn_Complete.setEnabled(false);
//                    btn_Complete.setClickable(false);
//                    btn_Complete.setTextColor(Color.parseColor("#A7A7A7"));
//
//                    btn_Complete_Bottom.setEnabled(false);
//                    btn_Complete_Bottom.setClickable(false);
//                    btn_Complete_Bottom.setTextColor(Color.parseColor("#A7A7A7"));
                    //TODO disable save button

                } else {
                    String selectedCalDate = StaticVariables.dayWithDateFormat.format(cal.getTime());
                    updateDate(selectedCalDate);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    };

    public void fillManagerMap() {
        ActivityStringInfo.managerMap.clear();
        Cursor cursor = null;
        try {
            cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getAllUserTypeRecords();
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(DatabaseConstant.key_USERTYPEID, cursor.getString(0));
                        map.put(DatabaseConstant.key_USERTYPE, cursor.getString(1));
                        map.put(DatabaseConstant.key_TYPEDESCRIPTION, cursor.getString(2));
                        map.put(DatabaseConstant.key_FLAGBANK, cursor.getString(3));
                        map.put(DatabaseConstant.key_ISACTIVE, cursor.getString(4));
                        ActivityStringInfo.managerMap.add(map);

                    } while (cursor.moveToNext());
                }
            }
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    /**
     * Fill List of manager designations
     */
    public void fillManagerList() {
        try {
            this.mangerRoleLayout.setVisibility(View.VISIBLE);
            this.lst_Manager.setAdapter(new MyArrayAdapterManager(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_managers, ActivityStringInfo.managerMap));
            Utility.setListViewHeightBasedOnChildren(mContext, this.lst_Manager);
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    /**
     * Display the Manager List
     **/
    private class MyArrayAdapterManager extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();
        private RadioButton mSelectedRB;
        private int mSelectedPosition = -1;
        private Context aContext;


        public MyArrayAdapterManager(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            aContext = context;
            myData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.listitem_logs_managers, null);
                }
                final HashMap<String, String> hasValues = myData.get(position);
                final RadioButton chkTxt_Manager = (RadioButton) v.findViewById(R.id.chkTxtManager);
                chkTxt_Manager.setText(hasValues.get(DatabaseConstant.key_USERTYPE));

                String id = hasValues.get(DatabaseConstant.key_USERTYPEID).toString();
                if (!ActivityStringInfo.isCreateNewLog) {
                    if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                        chkTxt_Manager.setClickable(false);
                        chkTxt_Manager.setEnabled(false);
                        chkTxt_Manager.setFocusableInTouchMode(false);
                        edtLogDate.setFocusable(false);
                    } else {
                        if (Utility.compareLastSyncForDisable(mContext)) {
                            chkTxt_Manager.setClickable(false);
                            chkTxt_Manager.setEnabled(false);
                            chkTxt_Manager.setFocusableInTouchMode(false);
                            edtLogDate.setFocusable(false);
                        }
                    }
                } else {
                    if (position == 0) {
                        edtLogDate.setFocusable(false);

                    }
                }

                if (ActivityStringInfo.selectedUserTypeId.equals(id)) {
                    chkTxt_Manager.setChecked(true);
                    chkTxt_Manager.setEnabled(true);
                    mSelectedPosition = position;
                    mSelectedRB = chkTxt_Manager;
                    ActivityStringInfo.selectedUserBankFlag = hasValues.get(DatabaseConstant.key_FLAGBANK).toString();
                }


                chkTxt_Manager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO need to set request focus on spinner
//                        spin_PersonAccident.requestFocus();
//                        chkTxt_Manager.setNextFocusDownId(R.id.spinPersonAccident);
                        chkTxt_Manager.setFocusable(false);
                        if ((position != mSelectedPosition && mSelectedRB != null)) {
                            mSelectedRB.setChecked(false);
                        }
                        mSelectedPosition = position;
                        mSelectedRB = (RadioButton) v;
                        ActivityStringInfo.selectedUserTypeId = hasValues.get(DatabaseConstant.key_USERTYPEID).toString();
                        ActivityStringInfo.selectedUserBankFlag = hasValues.get(DatabaseConstant.key_FLAGBANK).toString();
                        ActivityStringInfo.chkDetail2_bankDeposit = "";
                    }
                });

            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

            return v;
        }
    }

    public void inItFlagIconList() {
        ActivityStringInfo.flagTextwithIcons.put("SecurityIssue", R.drawable.flag_security);
        ActivityStringInfo.flagTextwithIcons.put("EmployeeIncidentInjury", R.drawable.flag_emp_injry);
        ActivityStringInfo.flagTextwithIcons.put("GuestIncidentInjury", R.drawable.flag_guest_injury);
        ActivityStringInfo.flagTextwithIcons.put("MajorPropertyDamage", R.drawable.flag_major_prop_dmg);
        ActivityStringInfo.flagTextwithIcons.put("MajorEquipmentIssueBreakdown", R.drawable.flag_major_equip_brkdwn);
        ActivityStringInfo.flagTextwithIcons.put("Other", R.drawable.flag_others);
    }

    public void fillLogFlagMap() {
        ActivityStringInfo.logFlagArrayList.clear();
        Cursor cursorMain = null;
        try {
            Cursor c1 = null;

            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogFlag(ActivityStringInfo.selectedLogId);
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            HashMap<String, String> logFlagMap = new HashMap<String, String>();
                            logFlagMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(0));
                            logFlagMap.put(DatabaseConstant.key_FLAG, c1.getString(1));
                            logFlagMap.put(DatabaseConstant.key_FLAGGEDDATE, c1.getString(2));
                            logFlagMap.put(DatabaseConstant.key_ID, c1.getString(3));
                            logFlagMap.put(DatabaseConstant.key_LOG_ID, c1.getString(4));
                            logFlagMap.put(DatabaseConstant.key_USER_ID, c1.getString(5));
                            logFlagMap.put(DatabaseConstant.key_USER_NAME, c1.getString(6));
                            ActivityStringInfo.logFlagArrayList.add(logFlagMap);
                        } while (c1.moveToNext());
                    }
                }
                if (c1 != null) c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null) {
                    c1.close();
                }
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }

    }


    public void fillFlagsList() {
        if (ActivityStringInfo.isCreateNewLog) {
            isUpdateOrInsertRecordForFlags = "insert";
            Log.v("isUpdateOrInsert5:- ", isUpdateOrInsertRecord);
        } else {
            try {
                Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getLogFlag(ActivityStringInfo.selectedLogId);


                if (c.getCount() > 0) {
                    isUpdateOrInsertRecordForFlags = "update";
                    Log.v("isUpdateOrInsert6:- ", isUpdateOrInsertRecord);
                    if (c.moveToFirst()) {
                        do {
                            String flag = c.getString(1);
                            String desc = c.getString(0);
                            AddFlagItem flagItem = new AddFlagItem(ActivityStringInfo.flagTextwithIcons.get(c.getString(1)), c.getString(0), true);
                            flagItem.setDescription(c.getString(0));
                            flagItem.setFlag(c.getString(1));

                            if (c.getString(1).equals("Other")) {
                                flagItem.setOtherFlagText(c.getString(0));
                            }
                            flagItem.setFlaggedDate(c.getString(2));
                            flagItem.setFlagId(c.getString(3));
                            flagItem.setFlagLogId(c.getString(4));
                            flagItem.setFlagUserId(c.getString(5));
                            flagItem.setFlagUserName(c.getString(6));

                            listToBindFlag.add(flagItem);

                        } while (c.moveToNext());
                    }
                    c.close();
                    setFlagListAdapter(listToBindFlag);
                    if (listToBindFlag.size() > 0) {
//                        btn_clearFlags.setVisibility(View.VISIBLE);
                    }

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /*Display flags added */
    public void setFlagListAdapter(List<AddFlagItem> flags) {
        try {
            flagsLayout.setVisibility(View.VISIBLE);
            logsAddFlagAdapter = new ShowLogFlagAdapter(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_flag, flags);
            //Log.v("logs_details_all", " " + lst_Flags);
            lst_Flags.setAdapter(logsAddFlagAdapter);
            lst_Flags.setDivider(null);
            lst_Flags.setDividerHeight(0);

            Utility.setListViewHeightBasedOnChildren(mContext, lst_Flags);
        } catch (Exception e) {
            Utility.saveExceptionDetails(mContext, e);
            e.printStackTrace();
        }
    }

    public void fillAccidentMap() {
//        ActivityStringInfo.personAccident.clear();
        Cursor cursorMain = null;

        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyDescRecords(ActivityStringInfo.selectedLogId, "1");
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            ActivityStringInfo.strSafetyLogIdForAccidents = c1.getString(1);
                            HashMap<String, String> descMap = new HashMap<String, String>();
                            descMap.put(DatabaseConstant.key_ID, c1.getString(0));
                            descMap.put(DatabaseConstant.key_SAFETYLOGID, c1.getString(1));
                            descMap.put(DatabaseConstant.key_EMPLOYEEID, c1.getString(2));
                            descMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(3));
                            descMap.put(DatabaseConstant.key_ADDEDBYID, c1.getString(4));
                            descMap.put(DatabaseConstant.key_TYPE, "Accident");
                            descMap.put(DatabaseConstant.key_EMPLOYEE_NAME, c1.getString(5));
                            descMap.put(DatabaseConstant.key_ADDEDBY_NAME, c1.getString(6));
                            descMap.put(DatabaseConstant.key_STATUS, "E");
//                            ActivityStringInfo.personAccident.add(descMap);
                            ActivityStringInfo.safetyIssueList.add(descMap);

                        } while (c1.moveToNext());
                    }
                    if (c1 != null)
                        c1.close();
                } else {
                    getSafetyLogId("accident");
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();

            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }

    public void getSafetyLogId(String type) {
        try {
            if (type.equals("accident")) {
                Cursor c1;
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyLogRecords(ActivityStringInfo.selectedLogId, "1");
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            ActivityStringInfo.strSafetyLogIdForAccidents = c1.getString(0);
                        } while (c1.moveToNext());
                    }
                }
                if (c1 != null)
                    c1.close();
            } else if (type.equals("injury")) {
                Cursor c2;
                c2 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyLogRecords(ActivityStringInfo.selectedLogId, "2");
                if (c2.getCount() > 0) {
                    if (c2.moveToFirst()) {
                        do {
                            ActivityStringInfo.strSafetyLogIdForInjury = c2.getString(0);
                        } while (c2.moveToNext());
                    }
                }
                if (c2 != null)
                    c2.close();
            } else if (type.equals("damage")) {
                Cursor c3;
                c3 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyLogRecords(ActivityStringInfo.selectedLogId, "3");
                if (c3.getCount() > 0) {
                    if (c3.moveToFirst()) {
                        do {
                            ActivityStringInfo.strSafetyLogIdForDamage = c3.getString(0);
                        } while (c3.moveToNext());
                    }
                }
                if (c3 != null)
                    c3.close();

            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillInjuryMap() {
//        ActivityStringInfo.personInjury.clear();
        Cursor cursorMain = null;
        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyDescRecords(ActivityStringInfo.selectedLogId, "2");
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            ActivityStringInfo.strSafetyLogIdForInjury = c1.getString(1);
                            HashMap<String, String> descMap = new HashMap<String, String>();
                            descMap.put(DatabaseConstant.key_ID, c1.getString(0));
                            descMap.put(DatabaseConstant.key_SAFETYLOGID, c1.getString(1));
                            descMap.put(DatabaseConstant.key_EMPLOYEEID, c1.getString(2));
                            descMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(3));
                            descMap.put(DatabaseConstant.key_ADDEDBYID, c1.getString(4));
                            descMap.put(DatabaseConstant.key_TYPE, "Injury");
                            descMap.put(DatabaseConstant.key_EMPLOYEE_NAME, c1.getString(5));
                            descMap.put(DatabaseConstant.key_ADDEDBY_NAME, c1.getString(6));
                            descMap.put(DatabaseConstant.key_STATUS, "E");
//                            ActivityStringInfo.personInjury.add(descMap);
                            ActivityStringInfo.safetyIssueList.add(descMap);

                        } while (c1.moveToNext());
                    }
                    if (c1 != null)
                        c1.close();
                } else {
                    getSafetyLogId("injury");
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }


    public void fillDamageMap() {
//        ActivityStringInfo.personDamage.clear();
        Cursor cursorMain = null;

        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyDescRecords(ActivityStringInfo.selectedLogId, "3");
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            ActivityStringInfo.strSafetyLogIdForDamage = c1.getString(1);
                            HashMap<String, String> descMap = new HashMap<String, String>();
                            descMap.put(DatabaseConstant.key_ID, c1.getString(0));
                            descMap.put(DatabaseConstant.key_SAFETYLOGID, c1.getString(1));
                            descMap.put(DatabaseConstant.key_EMPLOYEEID, c1.getString(2));
                            descMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(3));
                            descMap.put(DatabaseConstant.key_ADDEDBYID, c1.getString(4));
                            descMap.put(DatabaseConstant.key_TYPE, "Damage");
                            descMap.put(DatabaseConstant.key_EMPLOYEE_NAME, c1.getString(5));
                            descMap.put(DatabaseConstant.key_ADDEDBY_NAME, c1.getString(6));
                            descMap.put(DatabaseConstant.key_STATUS, "E");
//                            ActivityStringInfo.personDamage.add(descMap);
                            ActivityStringInfo.safetyIssueList.add(descMap);

                        } while (c1.moveToNext());
                    }
                    if (c1 != null)
                        c1.close();
                } else {
                    getSafetyLogId("damage");
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }

        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }


    public void fillSafetyList() {
        try {
//            safetyIssuesList.addAll(ActivityStringInfo.personAccident);
//            safetyIssuesList.addAll(ActivityStringInfo.personInjury);
//            safetyIssuesList.addAll(ActivityStringInfo.personDamage);
            safetyIssuesList.addAll(ActivityStringInfo.safetyIssueList);
            if (safetyIssuesList != null && safetyIssuesList.size() > 0) {
                safetyListHeader.setVisibility(View.VISIBLE);
                lst_SafetyIssues.setVisibility(View.VISIBLE);
            } else {
                safetyListHeader.setVisibility(View.GONE);
                lst_SafetyIssues.setVisibility(View.GONE);
            }
            lst_SafetyIssues.setAdapter(new MyArrayAdapterSafetyList(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_emp_and_safety, safetyIssuesList));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_SafetyIssues);

        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private class MyArrayAdapterSafetyList extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context sContext;

        public MyArrayAdapterSafetyList(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            sContext = context;
            myData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) sContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.listitem_logs_emp_and_safety, null);
                }
                final HashMap<String, String> hasValues = myData.get(position);
                final TextView txt_EnteredByUser = (TextView) v.findViewById(R.id.txtEnteredByUser);
                final TextView txt_PersonName = (TextView) v.findViewById(R.id.txtPersonName);
                final TextView txt_Description = (TextView) v.findViewById(R.id.txtDescription);
                TextView btn_Delete = (TextView) v.findViewById(R.id.btnDeleteItem);
                btn_Delete.setVisibility(View.VISIBLE);

                if (hasValues != null) {
                    txt_EnteredByUser.setText(hasValues.get(DatabaseConstant.key_TYPE));
                    txt_PersonName.setText(hasValues.get(DatabaseConstant.key_EMPLOYEE_NAME));
                    txt_Description.setText(hasValues.get(DatabaseConstant.key_DESCRIPTION));

                    if (hasValues.get(DatabaseConstant.key_TYPE) != null && (hasValues.get(DatabaseConstant.key_TYPE).equals(getResources().getString(R.string.propertydamage)) || hasValues.get(DatabaseConstant.key_TYPE).equals(getResources().getString(R.string.damage)))) {
                        txt_EnteredByUser.setText(getResources().getString(R.string.property_damage));
                    }

                    if (!ActivityStringInfo.isCreateNewLog) {
                        if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                            btn_Delete.setVisibility(View.INVISIBLE);
//
//                            btn_Delete.setClickable(false);
//                            btn_Delete.setEnabled(false);
//                            btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                        } else {
                            if (Utility.compareLastSyncForDisable(mContext)) {
                                btn_Delete.setVisibility(View.INVISIBLE);
//
//                                btn_Delete.setClickable(false);
//                                btn_Delete.setEnabled(false);
//                                btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                            }
                        }
                    }

//                    v.setBackgroundColor(Color.parseColor("#cccccc"));
//                    txt_EnteredByUser.setTextColor(Color.parseColor("#005595"));
//                    txt_PersonName.setTextColor(Color.parseColor("#005595"));
//                    txt_Description.setTextColor(Color.parseColor("#005595"));

                    txt_EnteredByUser.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_EnteredByUser.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.type), txt_EnteredByUser.getText().toString(), true);

//                            showAction((txt_Description.getText().toString())).show(v);

//                            showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });

                    txt_PersonName.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_PersonName.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.person), txt_PersonName.getText().toString(), true);

//                            showAction((txt_Description.getText().toString())).show(v);

//                            showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });

                    txt_Description.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_Description.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.description), txt_Description.getText().toString(), true);

//                            showAction((txt_Description.getText().toString())).show(v);

//                            showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });

                    btn_Delete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

//                            if (hasValues.get(DatabaseConstant.key_TYPE).equals("Accident")) {
//
//                                ActivityStringInfo.personAccident.remove(position);
//                                if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
//                                    ActivityStringInfo.personAccidentDelete.add(hasValues.get(DatabaseConstant.key_ID));
//
//                                safetyIssuesList.remove(position);
//                            } else if (hasValues.get(DatabaseConstant.key_TYPE).equals("Damage")) {
//                                ActivityStringInfo.personDamage.remove(position);
//                                if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
//                                    ActivityStringInfo.personInjuryDelete.add(hasValues.get(DatabaseConstant.key_ID));
//
//                                safetyIssuesList.remove(position);
//                            } else if (hasValues.get(DatabaseConstant.key_TYPE).equals("Injury")) {
//                                ActivityStringInfo.personInjury.remove(position);
//                                if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
//                                    ActivityStringInfo.personDamageDelete.add(hasValues.get(DatabaseConstant.key_ID));
//
//                                safetyIssuesList.remove(position);
//                            }


                            ActivityStringInfo.safetyIssueList.remove(position);
                            if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
                                ActivityStringInfo.safetyIssuesListDelete.add(hasValues.get(DatabaseConstant.key_ID));

                            safetyIssuesList.clear();
                            fillSafetyList();
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

            return v;
        }
    }

    public void fillQualityMap() {
        ActivityStringInfo.personQuality.clear();

        Cursor c = null;

        try {
            c = MyDatabaseInstanceHolder.getDatabaseHelper().getLogQualityRecords(ActivityStringInfo.selectedLogId);
            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        HashMap<String, String> qualityMap = new HashMap<String, String>();
                        qualityMap.put(DatabaseConstant.key_QUALITYID, c.getString(0));
                        qualityMap.put(DatabaseConstant.key_LOGID, c.getString(1));
                        qualityMap.put(DatabaseConstant.key_USERID, c.getString(2));
                        qualityMap.put(DatabaseConstant.key_COMPLAINT, c.getString(3));
                        qualityMap.put(DatabaseConstant.key_TYPE, "Quality");
                        qualityMap.put(DatabaseConstant.key_ADDEDBY_NAME, c.getString(4));
                        qualityMap.put(DatabaseConstant.key_STATUS, "E");
                        ActivityStringInfo.personQuality.add(qualityMap);

                    } while (c.moveToNext());
                }
            }
            if (c != null)
                c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
    }

    public void fillPersonQualityList() {
        try {
            qualityLayout.setVisibility(View.VISIBLE);
            if (ActivityStringInfo.personQuality != null && ActivityStringInfo.personQuality.size() > 0) {
                qualityListHeader.setVisibility(View.VISIBLE);
            } else {
                qualityListHeader.setVisibility(View.GONE);
            }
            lst_CommentQuality.setAdapter(new MyArrayAdapterComments(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_quality_comment, ActivityStringInfo.personQuality));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_CommentQuality);
            edCommentQuality.setText("");
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    /**
     * Display the Person Comments
     **/
    @SuppressWarnings("unchecked")
    private class MyArrayAdapterComments extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context qContext;

        public MyArrayAdapterComments(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            qContext = context;
            myData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) qContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.listitem_logs_quality_comment, null);

                    final HashMap<String, String> hasValues = myData.get(position);
                    final TextView txt_CustomerName = (TextView) v.findViewById(R.id.txtEmployeeName);
                    final TextView txt_CustomerComment = (TextView) v.findViewById(R.id.txtEmployeeDescription);
                    TextView btn_Delete = (TextView) v.findViewById(R.id.btnDeleteComment);
                    btn_Delete.setVisibility(View.VISIBLE);

                    if (hasValues != null) {
                        txt_CustomerName.setText(hasValues.get(DatabaseConstant.key_ADDEDBY_NAME));
                        txt_CustomerComment.setText(hasValues.get(DatabaseConstant.key_COMPLAINT));

                        if (!ActivityStringInfo.isCreateNewLog) {
                            if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                                btn_Delete.setVisibility(View.INVISIBLE);
                                btn_Delete.setClickable(false);
                                btn_Delete.setEnabled(false);
                                btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                            } else {
                                if (Utility.compareLastSyncForDisable(mContext)) {
                                    btn_Delete.setVisibility(View.INVISIBLE);
                                    btn_Delete.setClickable(false);
                                    btn_Delete.setEnabled(false);
                                    btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                                }
                            }
                        }

//                        v.setBackgroundColor(Color.parseColor("#cccccc"));
//                        txt_CustomerName.setTextColor(Color.parseColor("#005595"));
//                        txt_CustomerComment.setTextColor(Color.parseColor("#005595"));

                        txt_CustomerName.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (!txt_CustomerName.getText().toString().equals(""))
                                    getAlertDialogManager().showAlertDialog(getResources().getString(R.string.entry_by), txt_CustomerName.getText().toString(), true);
//                                showAction(txt_CustomerComment.getText().toString()).show(v);

//                                    showToast(txt_CustomerComment.getText().toString(), Toast.LENGTH_SHORT);
                            }
                        });

                        txt_CustomerComment.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (!txt_CustomerComment.getText().toString().equals(""))
                                    getAlertDialogManager().showAlertDialog(getResources().getString(R.string.comment), txt_CustomerComment.getText().toString(), true);
//                                showAction(txt_CustomerComment.getText().toString()).show(v);

//                                    showToast(txt_CustomerComment.getText().toString(), Toast.LENGTH_SHORT);
                            }
                        });

                        btn_Delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityStringInfo.personQuality.remove(position);
                                if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
                                    ActivityStringInfo.personQualityDelete.add(hasValues.get(DatabaseConstant.key_QUALITYID));

                                fillPersonQualityList();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

            return v;
        }
    }

    public void setBankDepositFlags() {
        Cursor cursorMain = null;
        try {
            if (!ActivityStringInfo.isCreateNewLog) {
                if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                    rbn_YesBankDeposit.setEnabled(false);
                    rbn_YesBankDeposit.setClickable(false);
                    rbn_NoBankDeposit.setEnabled(false);
                    rbn_NoBankDeposit.setClickable(false);
                    ed_BankDepositComment.setEnabled(false);
                    ed_BankDepositComment.setClickable(false);
                } else {
                    if (Utility.compareLastSyncForDisable(mContext)) {
                        rbn_YesBankDeposit.setEnabled(false);
                        rbn_YesBankDeposit.setClickable(false);
                        rbn_NoBankDeposit.setEnabled(false);
                        rbn_NoBankDeposit.setClickable(false);
                        ed_BankDepositComment.setEnabled(false);
                        ed_BankDepositComment.setClickable(false);
                    }
                }
            }

            Cursor c1 = null;
            try {
                if (ActivityStringInfo.bankDepositComment.equals("")) {
                    //rbn_YesBankDeposit.setChecked(true);
                    rbn_NoBankDeposit.setChecked(false);
                    ed_BankDepositComment.setVisibility(View.GONE);
                } else {
                    rbn_YesBankDeposit.setChecked(false);
                    rbn_NoBankDeposit.setChecked(true);
                    ed_BankDepositComment.setVisibility(View.VISIBLE);
                    ed_BankDepositComment.setText(ActivityStringInfo.bankDepositComment);
                }

                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getSelectedProductivityDetailRecord("0", ActivityStringInfo.selectedLogId);
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            String fieldValue = c1.getString(3);

                            ActivityStringInfo.bankDepositComment = fieldValue;
                            if (fieldValue.equals("")) {
                                rbn_YesBankDeposit.setChecked(true);
                                rbn_NoBankDeposit.setChecked(false);
                                ed_BankDepositComment.setVisibility(View.GONE);
                            } else {
                                rbn_YesBankDeposit.setChecked(false);
                                rbn_NoBankDeposit.setChecked(true);
                                ed_BankDepositComment.setVisibility(View.VISIBLE);
                                ed_BankDepositComment.setText(fieldValue);
                            }

                        } while (c1.moveToNext());
                    }
                } else {
                    rbn_YesBankDeposit.setChecked(false);
                    rbn_NoBankDeposit.setChecked(false);
                    ed_BankDepositComment.setVisibility(View.GONE);
                }

                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }

    public void bindAdapterProductivity() {
        ActivityStringInfo.productivityFieldsMap1.clear();
        ActivityStringInfo.ed_ProductivityFieldsList.clear();
        ActivityStringInfo.dailyLogProdDetAnnualMap.clear();
        Cursor c = null;
        Cursor curDetail = null;
        try {
            /** Get the logs list**/
            ActivityStringInfo.productivityFieldsMap1.clear();

            c = MyDatabaseInstanceHolder.getDatabaseHelper().getLogProductivityFieldRecords();

            if (c.getCount() > 0) {
                isUpdateOrInsertRecord = "insert";
                Log.v("isUpdateOrInsert7:- ", isUpdateOrInsertRecord);
                if (c.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        String fieldId = c.getString(0);
                        map.put(DatabaseConstant.key_LOGID, ActivityStringInfo.selectedLogId);
                        map.put(DatabaseConstant.key_FIELDID, fieldId);

                        map.put(DatabaseConstant.key_FIELDTITLE, c.getString(1));
                        map.put(DatabaseConstant.key_FIELDTYPE, c.getString(2));
                        map.put(DatabaseConstant.key_TRACKANNUALLY, c.getString(3));
                        map.put(DatabaseConstant.key_ISACTIVE, c.getString(4));

                        curDetail = MyDatabaseInstanceHolder.getDatabaseHelper().getSelectedProductivityDetailRecord(fieldId, ActivityStringInfo.selectedLogId);

                        if (curDetail.getCount() > 0) {
                            isUpdateOrInsertRecord = "update";
                            Log.v("isUpdateOrInsert8:- ", isUpdateOrInsertRecord);
                            curDetail.moveToFirst();
                            map.put(DatabaseConstant.key_FIELDVALUE, curDetail.getString(3));
                        } else {
                            isUpdateOrInsertRecord = "insert";
                            Log.v("isUpdateOrInsert9:- ", isUpdateOrInsertRecord);
                            map.put(DatabaseConstant.key_FIELDVALUE, "");
                        }
                        curDetail.close();

                        if (ActivityStringInfo.dailyLogProdDetAnnualMap.containsKey(fieldId)) {
                            String temp = ActivityStringInfo.dailyLogProdDetAnnualMap.get(fieldId).toString();
                            String[] pastAnnualValues = temp.split("~");

                            map.put(DatabaseConstant.key_USERNAME1, pastAnnualValues[0]);
                            map.put(DatabaseConstant.key_RESPONSE1, pastAnnualValues[1]);
                            map.put(DatabaseConstant.key_USERNAME2, pastAnnualValues[2]);
                            map.put(DatabaseConstant.key_RESPONSE2, pastAnnualValues[3]);

                        } else {
                            map.put(DatabaseConstant.key_USERNAME1, "");
                            map.put(DatabaseConstant.key_RESPONSE1, "");
                            map.put(DatabaseConstant.key_USERNAME2, "");
                            map.put(DatabaseConstant.key_RESPONSE2, "");
                        }

                        ActivityStringInfo.productivityFieldsMap1.add(map);


                    } while (c.moveToNext());
                } else {
                    isUpdateOrInsertRecord = "update";
                    Log.v("isUpdateOrInsert10:- ", isUpdateOrInsertRecord);
                }
                c.close();

                fillProductivityList();
            }
            c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            c.close();
        }
    }

    public void fillProductivityList() {
        try {
            lst_Productivity.setAdapter(new MyArrayAdapterProductivity(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_productivity, ActivityStringInfo.productivityFieldsMap1));
            setListViewHeightBasedOnChildren(mContext, lst_Productivity);
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    /***
     * SET FOR THE LIST VIEW HEIGHT
     **/
    public static void setListViewHeightBasedOnChildren(Context context, ListView listView) {
        try {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                int lstItmHeight = listItem.getMeasuredHeight();

                totalHeight += 250;
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + 2 + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        } catch (Exception e) {
            Utility.saveExceptionDetails(context, e);
            e.printStackTrace();
        }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }

    private class MyArrayAdapterProductivity extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context pContext;

        public MyArrayAdapterProductivity(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            pContext = context;
            myData = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.listitem_logs_productivity, null);
                }

                final HashMap<String, String> hasValues = myData.get(position);
                if (hasValues != null) {
                    TableLayout tblay_AnnualTracking = (TableLayout) v.findViewById(R.id.tblayAnnualTracking);
                    LinearLayout lylProductivityList = (LinearLayout) v.findViewById(R.id.lylProductivityList);

                    TextView txt_FieldTitle = (TextView) v.findViewById(R.id.txtFieldTitle);
                    TextView txt_OneYearAgo = (TextView) v.findViewById(R.id.txtOneYearAgo);
                    TextView txt_TwoYearAgo = (TextView) v.findViewById(R.id.txtTwoYearAgo);

                    TextView txt_AddedBy = (TextView) v.findViewById(R.id.txtAddedBy);
                    TextView txt_Response = (TextView) v.findViewById(R.id.txtResponse);
                    TextView txt_OneYearAgoAddedBy = (TextView) v.findViewById(R.id.txtOneYearAgoAddedBy);
                    TextView txt_OneYearAgoResponse = (TextView) v.findViewById(R.id.txtOneYearAgoResponse);
                    TextView txt_TwoYearAgoAddedBy = (TextView) v.findViewById(R.id.txtTwoYearAgoAddedBy);
                    TextView txt_TwoYearAgoResponse = (TextView) v.findViewById(R.id.txtTwoYearAgoResponse);

                    TextView txt_Dollar = (TextView) v.findViewById(R.id.txtDollar);

                    final AutoCompleteTextView ed_FieldValue = (AutoCompleteTextView) v.findViewById(R.id.edFieldValue);

                    if (!ActivityStringInfo.isCreateNewLog) {
                        if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                            ed_FieldValue.setEnabled(false);
                            ed_FieldValue.setClickable(false);
                            isFieldValueEnable = false;
                        } else {
                            if (Utility.compareLastSyncForDisable(mContext)) {
                                ed_FieldValue.setEnabled(false);
                                ed_FieldValue.setClickable(false);
                                isFieldValueEnable = false;
                            }
                        }
                    }
                    if (ed_FieldValue.isEnabled()) {
                        ed_FieldValue.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
                    } else {
                        ed_FieldValue.setTextColor(ContextCompat.getColor(getContext(), R.color.textcolor));
                    }

//                    if (position % 2 == 0) {
//                        lylProductivityList.setBackgroundColor(Color.parseColor("#cccccc"));
//                    } else {
//                        txt_FieldTitle.setTextColor(Color.parseColor("#005595"));
//                        txt_OneYearAgo.setTextColor(Color.parseColor("#005595"));
//                        txt_TwoYearAgo.setTextColor(Color.parseColor("#005595"));
//
//                        txt_AddedBy.setTextColor(Color.parseColor("#005595"));
//                        txt_Response.setTextColor(Color.parseColor("#005595"));
//
//                        txt_OneYearAgoAddedBy.setTextColor(Color.parseColor("#005595"));
//                        txt_TwoYearAgoAddedBy.setTextColor(Color.parseColor("#005595"));
//                        txt_OneYearAgoResponse.setTextColor(Color.parseColor("#005595"));
//                        txt_TwoYearAgoResponse.setTextColor(Color.parseColor("#005595"));
//                    }

                    txt_FieldTitle.setText(hasValues.get(DatabaseConstant.key_FIELDTITLE));
                    ed_FieldValue.setText(hasValues.get(DatabaseConstant.key_FIELDVALUE));

                    final String fieldType = hasValues.get(DatabaseConstant.key_FIELDTYPE).toString();

                    if (fieldType.toLowerCase().equals("currency")) {

                        ed_FieldValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        ed_FieldValue.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});

                        txt_Dollar.setText("$");
                    } else if (fieldType.toLowerCase().equals("count")) {
                        ed_FieldValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                        InputFilter maxLengthFilter = new InputFilter.LengthFilter(10);
                        ed_FieldValue.setFilters(new InputFilter[]{maxLengthFilter});
                    } else if (fieldType.toLowerCase().equals("text")) {
                        ed_FieldValue.setInputType(InputType.TYPE_CLASS_TEXT);
                    }

                    String annualTracking = hasValues.get(DatabaseConstant.key_TRACKANNUALLY).toString();
                    if (annualTracking.toLowerCase().equals("true")) {
                        if (fieldType.toLowerCase().equals("text")) {
                            String userName1 = hasValues.get(DatabaseConstant.key_USERNAME1).toString();
                            String response1 = hasValues.get(DatabaseConstant.key_RESPONSE1).toString();
                            String userName2 = hasValues.get(DatabaseConstant.key_USERNAME2).toString();
                            String response2 = hasValues.get(DatabaseConstant.key_RESPONSE2).toString();

                            if (userName1.equals("") && response1.equals("") && userName2.equals("") && response2.equals("")) {
                                txt_AddedBy.setVisibility(View.GONE);
                                txt_Response.setVisibility(View.GONE);
                            } else {
                                txt_OneYearAgoAddedBy.setText(hasValues.get(DatabaseConstant.key_USERNAME1).toString());
                                txt_OneYearAgoResponse.setText(hasValues.get(DatabaseConstant.key_RESPONSE1).toString());

                                txt_TwoYearAgoAddedBy.setText(hasValues.get(DatabaseConstant.key_USERNAME2).toString());
                                txt_TwoYearAgoResponse.setText(hasValues.get(DatabaseConstant.key_RESPONSE2).toString());
                            }
                        } else {
                            txt_AddedBy.setVisibility(View.GONE);
                            txt_Response.setVisibility(View.GONE);

                            txt_OneYearAgoAddedBy.setVisibility(View.GONE);
                            txt_TwoYearAgoAddedBy.setVisibility(View.GONE);
                            txt_OneYearAgoResponse.setVisibility(View.GONE);
                            txt_TwoYearAgoResponse.setVisibility(View.GONE);
                        }
                    } else {
                        tblay_AnnualTracking.setVisibility(View.GONE);
                        ed_FieldValue.setWidth(250);
                    }
                    System.out.println("List size----->" + myData.size());
                    if (position == (myData.size() - 1)) {
                        System.out.println("List pos----->");
                        ed_FieldValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    }
                    ed_FieldValue.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            if (!ed_FieldValue.getText().equals("")) {
                                try {
                                    //ed_FieldValue.requestFocusFromTouch();
                                    LogDetailsFragment.this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                    System.out.print("afterTextChanged---->");
                                    if (fieldType.toLowerCase().equals("currency")) {
                                        int length = ed_FieldValue.getText().toString().indexOf(".");
                                        if (length == 7)
                                            ed_FieldValue.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
                                        else if (length == 8)
                                            ed_FieldValue.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 1)});
                                        else if (length == 9) {
                                            InputFilter maxLengthFilter = new InputFilter.LengthFilter(10);
                                            ed_FieldValue.setFilters(new InputFilter[]{maxLengthFilter});
                                        } else
                                            ed_FieldValue.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});

                                    }
                                    hasValues.put(DatabaseConstant.key_FIELDVALUE, ed_FieldValue.getText().toString());
                                } catch (Exception e) {
                                    Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                                    e.printStackTrace();
                                }
                            } else {
                                hasValues.put(DatabaseConstant.key_FIELDVALUE, "");
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return v;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isUpdateOrInsertRecord = "";
        Log.v("isUpdateOrInsertRecord", "onDestroy " + isUpdateOrInsertRecord);

    }

    //Employee Relations

    public void fillNoShowMap() {
//        ActivityStringInfo.personNoShow.clear();
        Cursor cursorMain = null;

        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogEmpRelationDescRecords(ActivityStringInfo.selectedLogId, "1");
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            HashMap<String, String> descMap = new HashMap<String, String>();

                            descMap.put(DatabaseConstant.key_EMPLOGID, c1.getString(0));
                            descMap.put(DatabaseConstant.key_LOGID, c1.getString(1));
                            descMap.put(DatabaseConstant.key_EMPFIELDID, c1.getString(2));
                            descMap.put(DatabaseConstant.key_EMPLOYEE, c1.getString(3));
                            descMap.put(DatabaseConstant.key_ADDEDBYID, c1.getString(4));
                            descMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(5));
                            descMap.put(DatabaseConstant.key_TYPE, "NoShow");
                            descMap.put(DatabaseConstant.key_EMPLOYEE_NAME, c1.getString(6));
                            descMap.put(DatabaseConstant.key_ADDEDBY_NAME, c1.getString(7));
                            descMap.put(DatabaseConstant.key_STATUS, "E");
//                            ActivityStringInfo.personNoShow.add(descMap);
                            ActivityStringInfo.employeeRelations.add(descMap);
                            Log.v("LogsDetailsAll.java", "fillNoShowMap()====>" + c1.getString(0));

                        } while (c1.moveToNext());
                    }
                }
                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }


    public void fillVerbalWarningMap() {
//        ActivityStringInfo.personVerbalWarning.clear();
        Cursor cursorMain = null;

        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogEmpRelationDescRecords(ActivityStringInfo.selectedLogId, "2");
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            HashMap<String, String> descMap = new HashMap<String, String>();

                            descMap.put(DatabaseConstant.key_EMPLOGID, c1.getString(0));
                            descMap.put(DatabaseConstant.key_LOGID, c1.getString(1));
                            descMap.put(DatabaseConstant.key_EMPFIELDID, c1.getString(2));
                            descMap.put(DatabaseConstant.key_EMPLOYEE, c1.getString(3));
                            descMap.put(DatabaseConstant.key_ADDEDBYID, c1.getString(4));
                            descMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(5));
                            descMap.put(DatabaseConstant.key_TYPE, "VerbalWarning");
                            descMap.put(DatabaseConstant.key_EMPLOYEE_NAME, c1.getString(6));
                            descMap.put(DatabaseConstant.key_ADDEDBY_NAME, c1.getString(7));
                            descMap.put(DatabaseConstant.key_STATUS, "E");
//                            ActivityStringInfo.personVerbalWarning.add(descMap);
                            ActivityStringInfo.employeeRelations.add(descMap);

                        } while (c1.moveToNext());
                    }
                }
                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }

    public void fillWrittenWarningMap() {
//        ActivityStringInfo.personWrittenWarning.clear();
        Cursor cursorMain = null;

        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogEmpRelationDescRecords(ActivityStringInfo.selectedLogId, "3");
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            HashMap<String, String> descMap = new HashMap<String, String>();

                            descMap.put(DatabaseConstant.key_EMPLOGID, c1.getString(0));
                            descMap.put(DatabaseConstant.key_LOGID, c1.getString(1));
                            descMap.put(DatabaseConstant.key_EMPFIELDID, c1.getString(2));
                            descMap.put(DatabaseConstant.key_EMPLOYEE, c1.getString(3));
                            descMap.put(DatabaseConstant.key_ADDEDBYID, c1.getString(4));
                            descMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(5));
                            descMap.put(DatabaseConstant.key_TYPE, "WrittenWarning");
                            descMap.put(DatabaseConstant.key_EMPLOYEE_NAME, c1.getString(6));
                            descMap.put(DatabaseConstant.key_ADDEDBY_NAME, c1.getString(7));
                            descMap.put(DatabaseConstant.key_STATUS, "E");
//                            ActivityStringInfo.personWrittenWarning.add(descMap);
                            ActivityStringInfo.employeeRelations.add(descMap);

                        } while (c1.moveToNext());
                    }
                }
                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }

    public void fillEmployeeRelationList() {
        try {
//            this.employeeRelationsList.addAll(ActivityStringInfo.personNoShow);
//            this.employeeRelationsList.addAll(ActivityStringInfo.personVerbalWarning);
//            this.employeeRelationsList.addAll(ActivityStringInfo.personWrittenWarning);
            this.employeeRelationsList.addAll(ActivityStringInfo.employeeRelations);
            if (employeeRelationsList != null && employeeRelationsList.size() > 0) {
                empRelationListHeader.setVisibility(View.VISIBLE);
                lst_EmpRelations.setVisibility(View.VISIBLE);
            } else {
                empRelationListHeader.setVisibility(View.GONE);
                lst_EmpRelations.setVisibility(View.GONE);
            }
            lst_EmpRelations.setAdapter(new MyArrayAdapterEmpRelationsList(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_emp_and_safety, employeeRelationsList));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_EmpRelations);
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void setCommunicationDetail() {
        Cursor cursorMain = null;

        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogCommunicationDetRecords(ActivityStringInfo.selectedLogId);
                if (c1.getCount() > 0) {
                    // TODO: 24/02/17 Change Flag 
//                    isUpdateOrInsertRecord = "update";
                    Log.v("isUpdateOrInsert11:- ", isUpdateOrInsertRecord);
                    if (c1.moveToFirst()) {
                        do {
                            String commId = c1.getString(2);
                            if (commId.equals("1")) {
                                String flagStatus = c1.getString(3);
                                if (flagStatus.toLowerCase().equals("true")) {
                                    //ActivityStringInfo.chkDetail3_urgent = "0";
                                    rbn_YesUrgent.setChecked(true);
                                    rbn_NoneToReportUrgent.setChecked(false);
                                } else {
                                    //ActivityStringInfo.chkDetail3_urgent = "1";
                                    rbn_YesUrgent.setChecked(false);
                                    rbn_NoneToReportUrgent.setChecked(true);
                                }
                            } else {
                                String flagStatus = c1.getString(3);
                                if (flagStatus.toLowerCase().equals("true")) {
                                    //ActivityStringInfo.chkDetail3_message = "0";
                                    rbn_YesMessage.setChecked(true);
                                    rbn_NoneToReportMessage.setChecked(false);
                                } else {
                                    //ActivityStringInfo.chkDetail3_message = "1";
                                    rbn_YesMessage.setChecked(false);
                                    rbn_NoneToReportMessage.setChecked(true);
                                }
                            }

                        } while (c1.moveToNext());
                    }
                } else {
                    isUpdateOrInsertRecord = "insert";
                    Log.v("isUpdateOrInsert12:- ", isUpdateOrInsertRecord);
                }
                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }

    private class MyArrayAdapterEmpRelationsList extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context eContext;

        public MyArrayAdapterEmpRelationsList(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            eContext = context;
            myData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) eContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.listitem_logs_emp_and_safety, null);
                }
                final HashMap<String, String> hasValues = myData.get(position);
                final TextView txt_EnteredByUser = (TextView) v.findViewById(R.id.txtEnteredByUser);
                final TextView txt_PersonName = (TextView) v.findViewById(R.id.txtPersonName);
                final TextView txt_Description = (TextView) v.findViewById(R.id.txtDescription);
                TextView btn_Delete = (TextView) v.findViewById(R.id.btnDeleteItem);
                btn_Delete.setVisibility(View.VISIBLE);
                if (hasValues != null) {
                    if (hasValues.get(DatabaseConstant.key_TYPE) != null && hasValues.get(DatabaseConstant.key_TYPE).equals("NoShow")) {
                        txt_EnteredByUser.setText(getResources().getString(R.string.no_show));
                    } else if (hasValues.get(DatabaseConstant.key_TYPE) != null && hasValues.get(DatabaseConstant.key_TYPE).equals("VerbalWarning")) {
                        txt_EnteredByUser.setText(getResources().getString(R.string.verbal_warning));
                    } else if (hasValues.get(DatabaseConstant.key_TYPE) != null && hasValues.get(DatabaseConstant.key_TYPE).equals("WrittenWarning")) {
                        txt_EnteredByUser.setText(getResources().getString(R.string.written_warning));
                    }
                    txt_PersonName.setText(hasValues.get(DatabaseConstant.key_EMPLOYEE_NAME));
                    txt_Description.setText(hasValues.get(DatabaseConstant.key_DESCRIPTION));

                    if (!ActivityStringInfo.isCreateNewLog) {
                        if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                            btn_Delete.setVisibility(View.INVISIBLE);
//                            btn_Delete.setClickable(false);
//                            btn_Delete.setEnabled(false);
//                            btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                        } else {
                            if (Utility.compareLastSyncForDisable(mContext)) {
                                btn_Delete.setVisibility(View.INVISIBLE);
//                                btn_Delete.setClickable(false);
//                                btn_Delete.setEnabled(false);
//                                btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                            }
                        }
                    }

//                    v.setBackgroundColor(Color.parseColor("#cccccc"));
//                    txt_EnteredByUser.setTextColor(Color.parseColor("#005595"));
//                    txt_PersonName.setTextColor(Color.parseColor("#005595"));
//                    txt_Description.setTextColor(Color.parseColor("#005595"));

                    txt_EnteredByUser.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_EnteredByUser.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.type), txt_EnteredByUser.getText().toString(), true);

//                            showAction(txt_Description.getText().toString()).show(v);

//                                showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });
                    txt_PersonName.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_PersonName.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.person), txt_PersonName.getText().toString(), true);

//                            showAction(txt_Description.getText().toString()).show(v);

//                                showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });
                    txt_Description.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_Description.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.description), txt_Description.getText().toString(), true);

//                            showAction(txt_Description.getText().toString()).show(v);

//                                showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });

                    btn_Delete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
//                            if (hasValues.get(DatabaseConstant.key_TYPE).equals("NoShow")) {
//                                ActivityStringInfo.personNoShow.remove(position);
//                                if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
//                                    ActivityStringInfo.personNoShowDelete.add(hasValues.get(DatabaseConstant.key_EMPLOGID));
//
//                            } else if (hasValues.get(DatabaseConstant.key_TYPE).equals("VerbalWarning")) {
//                                ActivityStringInfo.personVerbalWarning.remove(position);
//                                if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
//                                    ActivityStringInfo.personVerbalWarningDelete.add(hasValues.get(DatabaseConstant.key_EMPLOGID));
//
//                            } else if (hasValues.get(DatabaseConstant.key_TYPE).equals("WrittenWarning")) {
//                                ActivityStringInfo.personWrittenWarning.remove(position);
//                                if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
//                                    ActivityStringInfo.personWrittenWarningDelete.add(hasValues.get(DatabaseConstant.key_EMPLOGID));
//
//                            }
                            ActivityStringInfo.employeeRelations.remove(position);
                            if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
                                ActivityStringInfo.employeeRelationsDelete.add(hasValues.get(DatabaseConstant.key_EMPLOGID));

                            employeeRelationsList.clear();
                            fillEmployeeRelationList();
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

            return v;
        }
    }

    public void fillMaintenanceRepaired() {
        try {
            if (ActivityStringInfo.maintenanceRepaired != null && ActivityStringInfo.maintenanceRepaired.size() > 0) {
                repairsListHeader.setVisibility(View.VISIBLE);
            } else {
                repairsListHeader.setVisibility(View.GONE);
            }
            lst_RepairedItems.setAdapter(new MyArrayAdapterRepaired(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_quality_comment, ActivityStringInfo.maintenanceRepaired));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_RepairedItems);
            edItemRepaired.setText("");
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillMaintenanceNotes() {
        try {
            if (ActivityStringInfo.maintenanceNotes != null && ActivityStringInfo.maintenanceNotes.size() > 0) {
                notesListHeader.setVisibility(View.VISIBLE);
            } else {
                notesListHeader.setVisibility(View.GONE);
            }
            lst_AdditionalNotes.setAdapter(new MyArrayAdapterMaintenanceNotes(LogDetailsFragment.this.getActivity(), R.layout.listitem_logs_maintenance_notes, ActivityStringInfo.maintenanceNotes));
            Utility.setListViewHeightBasedOnChildren(mContext, lst_AdditionalNotes);
            edAddNote.setText("");
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public void fillMaintenanceRepairedMap() {
        ActivityStringInfo.maintenanceRepaired.clear();
        Cursor cursorMain = null;
        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogMaintenanceDetRecords(ActivityStringInfo.selectedLogId, "2");
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            HashMap<String, String> descMap = new HashMap<String, String>();

                            descMap.put(DatabaseConstant.key_MRRDETAILSID, c1.getString(0));
                            descMap.put(DatabaseConstant.key_LOGID, c1.getString(1));
                            descMap.put(DatabaseConstant.key_MRRID, c1.getString(2));
                            descMap.put(DatabaseConstant.key_EMPLOYEE, c1.getString(3));
                            descMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(4));
                            descMap.put(DatabaseConstant.key_TASKID, c1.getString(5));
                            descMap.put(DatabaseConstant.key_EMPLOYEE_NAME, c1.getString(6));
                            descMap.put(DatabaseConstant.key_STATUS, "E");
                            ActivityStringInfo.maintenanceRepaired.add(descMap);

                        } while (c1.moveToNext());
                    }
                }
                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();

        }
    }

    public void fillMaintenanceNotesMap() {
        ActivityStringInfo.maintenanceNotes.clear();
        Cursor cursorMain = null;
        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogNoteRecords(ActivityStringInfo.selectedLogId);
                if (c1.getCount() > 0) {
                    if (c1.moveToFirst()) {
                        do {
                            HashMap<String, String> descMap = new HashMap<String, String>();

                            descMap.put(DatabaseConstant.key_ID, c1.getString(0));
                            descMap.put(DatabaseConstant.key_LOGID, c1.getString(1));
                            descMap.put(DatabaseConstant.key_EMPLOYEE, c1.getString(2));
                            descMap.put(DatabaseConstant.key_DESCRIPTION, c1.getString(3));
                            descMap.put(DatabaseConstant.key_EMPLOYEE_NAME, c1.getString(4));
                            descMap.put(DatabaseConstant.key_STATUS, "E");
                            ActivityStringInfo.maintenanceNotes.add(descMap);

                        } while (c1.moveToNext());
                    }
                }
                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }

    public void setMaintenanceRepairedDetail() {
        Cursor cursorMain = null;

        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogMaintenanceLogRecords(ActivityStringInfo.selectedLogId, "1");
                if (c1.getCount() > 0) {
                    isUpdateOrInsertRecordForMain = "update";
                    if (c1.moveToFirst()) {
                        do {
                            String flagStatus = c1.getString(3);
                            if (flagStatus.toLowerCase().equals("true")) {
                                rbnYesRepair.setChecked(true);
                                rbnNoneToLogRepair.setChecked(false);
                            } else {
                                rbnYesRepair.setChecked(false);
                                rbnNoneToLogRepair.setChecked(true);
                            }
                        } while (c1.moveToNext());
                    }
                } else {
                    isUpdateOrInsertRecordForMain = "insert";
                }
                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();
        }
    }

    public void setFollowUpDetail() {
        Cursor cursorMain = null;
        try {
            Cursor c1 = null;
            try {
                c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogFollowUpLogRecords(ActivityStringInfo.selectedLogId, "1");
                if (c1.getCount() > 0) {
                    isUpdateOrInsertRecordForFoll = "update";
                    if (c1.moveToFirst()) {
                        do {
                            String flagStatus = c1.getString(3);
                            if (flagStatus.toLowerCase().equals("true")) {
                                rbnYesFollowUp.setChecked(true);
                                rbnNoneToLogFollowUp.setChecked(false);
                            } else {
                                rbnYesFollowUp.setChecked(false);
                                rbnNoneToLogFollowUp.setChecked(true);
                            }

                        } while (c1.moveToNext());
                    }
                } else {
                    isUpdateOrInsertRecordForFoll = "insert";
                    //TODO flag update
                }
                if (c1 != null)
                    c1.close();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            } finally {
                if (c1 != null)
                    c1.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        } finally {
            if (cursorMain != null)
                cursorMain.close();

        }
    }


    /**
     * Display the Maintenance Notes
     **/
    @SuppressWarnings("unchecked")
    private class MyArrayAdapterMaintenanceNotes extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context nContext;

        public MyArrayAdapterMaintenanceNotes(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            nContext = context;
            myData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) nContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.listitem_logs_maintenance_notes, null);
                }
                final HashMap<String, String> hasValues = myData.get(position);
                final TextView txt_EmployeeName = (TextView) v.findViewById(R.id.txtEmployeeName);
                final TextView txt_Description = (TextView) v.findViewById(R.id.txtNotes);
                TextView btn_Delete = (TextView) v.findViewById(R.id.btnDeleteNotes);
                btn_Delete.setVisibility(View.VISIBLE);

                if (hasValues != null) {
                    txt_EmployeeName.setText(hasValues.get(DatabaseConstant.key_EMPLOYEE_NAME));
                    txt_Description.setText(hasValues.get(DatabaseConstant.key_DESCRIPTION));

                    if (!ActivityStringInfo.isCreateNewLog) {
                        if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                            btn_Delete.setVisibility(View.INVISIBLE);
//                            btn_Delete.setEnabled(false);
//                            btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                        } else {
                            if (Utility.compareLastSyncForDisable(mContext)) {
                                btn_Delete.setVisibility(View.INVISIBLE);
//                                btn_Delete.setClickable(false);
//                                btn_Delete.setEnabled(false);
//                                btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                            }
                        }
                    }

//                    v.setBackgroundColor(Color.parseColor("#cccccc"));
//                    txt_Description.setTextColor(Color.parseColor("#005595"));
//
                    txt_EmployeeName.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_EmployeeName.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.entry_by), txt_EmployeeName.getText().toString(), true);

//                            showAction(txt_Description.getText().toString()).show(v);

//                                showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });

                    txt_Description.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_Description.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.note), txt_Description.getText().toString(), true);

//                            showAction(txt_Description.getText().toString()).show(v);

//                                showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });

                    btn_Delete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ActivityStringInfo.maintenanceNotes.remove(position);
                            if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
                                ActivityStringInfo.maintenanceNotesDelete.add(hasValues.get(DatabaseConstant.key_ID));

                            fillMaintenanceNotes();
                        }
                    });

                } else {
                    v.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

            return v;
        }
    }

    /**
     * Display the Maintenance Repaired
     **/
    @SuppressWarnings("unchecked")
    private class MyArrayAdapterRepaired extends ArrayAdapter {
        private List<HashMap<String, String>> myData = new ArrayList<HashMap<String, String>>();

        private Context rContext;

        public MyArrayAdapterRepaired(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
            super(context, textViewResourceId, objects);
            rContext = context;
            myData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) rContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.listitem_logs_quality_comment, null);
                }
                final HashMap<String, String> hasValues = myData.get(position);
                final TextView txt_EmployeeName = (TextView) v.findViewById(R.id.txtEmployeeName);
                final TextView txt_Description = (TextView) v.findViewById(R.id.txtEmployeeDescription);
                TextView btn_Delete = (TextView) v.findViewById(R.id.btnDeleteComment);
                btn_Delete.setVisibility(View.VISIBLE);


                if (hasValues != null) {
                    txt_EmployeeName.setText(hasValues.get(DatabaseConstant.key_EMPLOYEE_NAME));
                    txt_Description.setText(hasValues.get(DatabaseConstant.key_DESCRIPTION));


                    if (!ActivityStringInfo.isCreateNewLog) {
                        if (!ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                            btn_Delete.setVisibility(View.INVISIBLE);
//                            btn_Delete.setClickable(false);
//                            btn_Delete.setEnabled(false);
//                            btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                        } else {
                            if (Utility.compareLastSyncForDisable(mContext)) {
                                btn_Delete.setVisibility(View.INVISIBLE);
//                                btn_Delete.setClickable(false);
//                                btn_Delete.setEnabled(false);
//                                btn_Delete.setTextColor(Color.parseColor("#A7A7A7"));
                            }
                        }
                    }

//                    v.setBackgroundColor(Color.parseColor("#cccccc"));
//                    txt_EmployeeName.setTextColor(Color.parseColor("#005595"));
//                    txt_Description.setTextColor(Color.parseColor("#005595"));

                    txt_EmployeeName.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_EmployeeName.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.entry_by), txt_EmployeeName.getText().toString(), true);

//                            showAction(txt_Description.getText().toString()).show(v);

//                            showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });

                    txt_Description.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!txt_Description.getText().toString().equals(""))
                                getAlertDialogManager().showAlertDialog(getResources().getString(R.string.repairs), txt_Description.getText().toString(), true);

//                            showAction(txt_Description.getText().toString()).show(v);

//                            showToast(txt_Description.getText().toString(), Toast.LENGTH_SHORT);
                        }
                    });

                    btn_Delete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ActivityStringInfo.maintenanceRepaired.remove(position);
                            if (!hasValues.get(DatabaseConstant.key_STATUS).equals("N"))
                                ActivityStringInfo.maintenanceRepairedDelete.add(hasValues.get(DatabaseConstant.key_MRRDETAILSID));
                            fillMaintenanceRepaired();
                        }
                    });
                } else {
                    v.invalidate();
                    v.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

            return v;
        }
    }


    private void setDisableAll() {
        try {
            addFlagBtn.setClickable(false);
            addFlagBtn.setEnabled(false);

            addSafetyIssueBtn.setClickable(false);
            addSafetyIssueBtn.setEnabled(false);

            edCommentQuality.setEnabled(false);
            btnAddQuality.setClickable(false);
            btnAddQuality.setEnabled(false);

            addEmpRelationsBtn.setClickable(false);
            addEmpRelationsBtn.setEnabled(false);

            rbn_NoneToReportMessage.setClickable(false);
//            rbn_NoneToReportMessage.setEnabled(false);

            rbn_NoneToReportUrgent.setClickable(false);
//            rbn_NoneToReportUrgent.setEnabled(false);

            rbn_YesMessage.setClickable(false);
//            rbn_YesMessage.setEnabled(false);

            rbn_YesUrgent.setClickable(false);
//            rbn_YesUrgent.setEnabled(false);

            rbnNoneToLogFollowUp.setClickable(false);
//            rbnNoneToLogFollowUp.setEnabled(false);

            rbnNoneToLogRepair.setClickable(false);
//            rbnNoneToLogRepair.setEnabled(false);

            rbnYesFollowUp.setClickable(false);
//            rbnYesFollowUp.setEnabled(false);

            rbnYesRepair.setClickable(false);
//            rbnYesRepair.setEnabled(false);

            edItemRepaired.setEnabled(false);
            btnAddRepaired.setClickable(false);
            btnAddRepaired.setEnabled(false);

            edAddNote.setEnabled(false);
            btnAddNote.setClickable(false);
            btnAddNote.setEnabled(false);

            isSaveCompleteEnable = false;


        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    public boolean validate() {
        boolean flag = true;
        Log.v("LogDetails.java", "isFieldValueEnable=====>" + isFieldValueEnable);

        if (ActivityStringInfo.selectedUserTypeId.equals("")) {
            flag = false;
            showToast(MessageInfo.fillManagerTitle, Toast.LENGTH_SHORT);
            txt_SelectOne.setTextColor(Color.RED);
        } else if (lyl_Bank.getVisibility() == View.VISIBLE && !rbn_NoBankDeposit.isChecked() && !rbn_YesBankDeposit.isChecked()) {
            Log.v("LogDetails.java", "Checked:- 2");
            txt_BankDeposit.setTextColor(Color.RED);
            rbn_YesBankDeposit.setFocusable(true);
            showToast(MessageInfo.selectBankDeposit, Toast.LENGTH_SHORT);
            flag = false;
        } else if (lyl_Bank.getVisibility() == View.VISIBLE && ed_BankDepositComment.getVisibility() == View.VISIBLE && ed_BankDepositComment.getText().toString().equals("")) {
            Log.v("LogDetails.java", "Checked:- 1");
            ed_BankDepositComment.setFocusable(true);
            showToast(MessageInfo.selectBankDepositDesc, Toast.LENGTH_SHORT);
            flag = false;
        } else if (!rbn_NoneToReportUrgent.isChecked() && !rbn_YesUrgent.isChecked()) {
            Log.v("LogDetails.java", "Checked:- 3");
            txtUrgentIssuesRemains.setTextColor(Color.RED);
            rbn_YesUrgent.setFocusable(true);
            showToast(MessageInfo.logUrgentIssues, Toast.LENGTH_SHORT);
            flag = false;
        } else if (!rbn_NoneToReportMessage.isChecked() && !rbn_YesMessage.isChecked()) {
            Log.v("LogDetails.java", "Checked:- 4");
            txtMsgAprovals.setTextColor(Color.RED);
            rbn_YesMessage.setFocusable(true);
            showToast(MessageInfo.logMessges, Toast.LENGTH_SHORT);
            flag = false;
        } else if (!rbnNoneToLogRepair.isChecked() && !rbnYesRepair.isChecked()) {
            Log.v("LogDetails.java", "Checked:- 5");
            txt_AllNewRepair.setTextColor(Color.RED);
            rbnYesRepair.setFocusable(true);
            showToast(MessageInfo.logNewRepairItems, Toast.LENGTH_SHORT);
            flag = false;
        } else if (!rbnNoneToLogFollowUp.isChecked() && !rbnYesFollowUp.isChecked()) {
            Log.v("LogDetails.java", "Checked:- 6");
            txt_FollowUp.setTextColor(Color.RED);
            rbnYesFollowUp.setFocusable(true);
            showToast(MessageInfo.logFollowUpItems, Toast.LENGTH_SHORT);
            flag = false;
        } else if (isFieldValueEnable) {
            int totalRecords = ActivityStringInfo.productivityFieldsMap1.size();
            int i = 0;
            if (totalRecords > 0) {
                for (i = 0; i < totalRecords; i++) {
                    HashMap<String, String> map = ActivityStringInfo.productivityFieldsMap1.get(i);
                    if (map.get(DatabaseConstant.key_FIELDVALUE) == null || map.get(DatabaseConstant.key_FIELDVALUE).toString().equals("")) {
                        showToast(MessageInfo.requiredFieldGenericMsgforProductivityLog, Toast.LENGTH_SHORT);
                        flag = false;
                        /*if(i==0){
                            toastMsg.showToastMsg(MessageInfo.fillBreakfast_Lunch_Guest_Count, Toast.LENGTH_SHORT);
							flag = false;

						}
						if(i==1){
							toastMsg.showToastMsg(MessageInfo.fillBreakfast_Lunch_Sales, Toast.LENGTH_SHORT);
							flag = false;

						}
						if(i==2){
							toastMsg.showToastMsg(MessageInfo.fillDinner_Guest_Count, Toast.LENGTH_SHORT);
							flag = false;

						}
						if(i==3){
							toastMsg.showToastMsg(MessageInfo.fillDinner_Sales, Toast.LENGTH_SHORT);
							flag = false;
						}*/

                        break;
                    } else {
                        flag = true;
                    }

                }
            }

        } else {
        }

        return flag;
    }

    public void saveLog() {
        if (isSaveCompleteEnable) {
            saveClick = true;
            completeClick = false;
            logDetailEntry();
        } else {
            showToast("Log is not active. Can not perform this operation?", Toast.LENGTH_SHORT);
        }
    }

    public void completeLog() {
        if (isSaveCompleteEnable) {
            if (validate()) {
                completeClick = true;
                saveClick = false;
                logDetailEntry();
            }
        } else {
            showToast("Log is not active. Can not perform this operation?", Toast.LENGTH_SHORT);
        }
    }

    public void logDetailEntry() {
        if (ActivityStringInfo.isCreateNewLog) {

            Cursor cursor = MyDatabaseInstanceHolder.getDatabaseHelper().getAllLogDetailsRecords();
            Log.v("TAG - cursor count:- ", " " + cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Log.v("TAG Create date:- ", cursor.getString(1));
                    Log.v("TAG Update date:- ", cursor.getString(6));
                    Log.v("TAG User name:- ", cursor.getString(4));
                }
            }


            // TODO: 22/02/17  Newly added- Sangram H. -- Log not save issue.
            String string = edtLogDate.getText().toString();
            String[] parts = string.split(" ");
            String part1 = parts[0];
            String part2 = parts[1];
            Log.v("TAG part1:- ", part1);
            Log.v("TAG part2:- ", part2);
            Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogDetailsRecords(ActivityStringInfo.strUser_id, part2);
            Log.v("TAG - cursor count:- ", "" + c1.getCount());
            if (c1.getCount() > 0 && (isUpdateOrInsertRecord.equals("insert") || isUpdateOrInsertRecord.equals("update"))) {
                showToast(MessageInfo.strLogsExists, Toast.LENGTH_SHORT);
                setLocalSystemDate();
                c1.close();
            } else {
                c1.close();

                if (!ActivityStringInfo.selectedUserTypeId.equals("")) {
                    ActivityStringInfo.selectedLogForDate = edtLogDate.getText().toString();
                    new SelectDataTaskForAnnualLogs().execute();
                    ActivityStringInfo.strLogEnteredDate = txtEnteredDate.getText().toString();
                } else {
                    showToast(MessageInfo.fillManagerTitle, Toast.LENGTH_SHORT);
                    txt_SelectOne.setTextColor(Color.RED);
                }
            }
        } else {
            if (!ActivityStringInfo.selectedUserTypeId.equals("")) {
                new SelectDataTaskForAnnualLogs().execute();
                ActivityStringInfo.strLogEnteredDate = txtEnteredDate.getText().toString();
            } else {
                showToast(MessageInfo.fillManagerTitle, Toast.LENGTH_SHORT);
                txt_SelectOne.setTextColor(Color.RED);
            }
        }
    }

    private class SelectDataTaskForAnnualLogs extends AsyncTask<String, Void, String> {
        String strMsg = "";
        ServicesHelper servHelper = new ServicesHelper();

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.loadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {

                strMsg = servHelper.setPastAnnualFieldDetails(mContext);
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                Log.v("Webservice strMSG", "" + strMsg);
                if (strMsg.equals("true")) {
                    new SelectDataTaskForInsertAllRecordInDatabase().execute();


                } else {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else {
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                    }
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    private class SelectDataTaskForInsertAllRecordInDatabase extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                try {
                    boolean b = InsertRecordInDatabase();
                    Log.v("Logs insertRecDatabase", " " + b);
                    if (b) {
                        Synchronization syc = new Synchronization(mContext);
                        strMsg = syc.getInformation(LogDetailsFragment.this.getActivity());
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
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
                    Log.v("LogDetailsAll", "selectedLogStatus " + ActivityStringInfo.selectedLogStatus);
                    if (ActivityStringInfo.selectedLogStatus.equals("P") || ActivityStringInfo.selectedLogStatus.equals("I")) {
                        showToast(MessageInfo.logSavedSuccessfully, Toast.LENGTH_SHORT);
                    } else {
                        showToast(MessageInfo.logUpdatedSuccessfully, Toast.LENGTH_SHORT);
                    }
                } else {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
                LogDetailsFragment.this.getActivity().finish();
            } catch (Exception e) {
                Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    public boolean InsertRecordInDatabase() {
        boolean flag = false;
        try {

            boolean isBlankFieldValue = false;
            boolean isSelectedDeposit = false;

            if (isFieldValueEnable) {
                int totalRecords = ActivityStringInfo.productivityFieldsMap1.size();
                int i = 0;
                if (totalRecords > 0) {
                    for (i = 0; i < totalRecords; i++) {
                        HashMap<String, String> map = ActivityStringInfo.productivityFieldsMap1.get(i);
                        if (map.get(DatabaseConstant.key_FIELDVALUE) == null || map.get(DatabaseConstant.key_FIELDVALUE).toString().equals("")) {
                            isBlankFieldValue = true;
                            break;
                        }
                    }
                }
            }

            if (ActivityStringInfo.selectedUserBankFlag.toLowerCase().equals("true")) {
                if (rbn_NoBankDeposit.isChecked() || rbn_YesBankDeposit.isChecked()) {
                    if (rbn_YesBankDeposit.isChecked()) {
                        ActivityStringInfo.bankDepositComment = "";
                        ActivityStringInfo.chkDetail2_bankDeposit = "0";
                        isSelectedDeposit = true;
                    } else {
                        ActivityStringInfo.chkDetail2_bankDeposit = "1";
                        ActivityStringInfo.bankDepositComment = ed_BankDepositComment.getText().toString();
                        if (!ActivityStringInfo.bankDepositComment.equals("")) {
                            isSelectedDeposit = true;
                        } else {
                            isSelectedDeposit = false;
                            if (isBlankFieldValue) {
                                //											toastMsg.showToastMsg(MessageInfo.fillRequiredFields, Toast.LENGTH_SHORT);
                            } else {
                                //										toastMsg.showToastMsg(MessageInfo.selectBankDepositDesc, Toast.LENGTH_SHORT);
                            }
                        }
                    }
                } else {
                    isSelectedDeposit = false;
                    if (isBlankFieldValue) {
                        //									toastMsg.showToastMsg(MessageInfo.fillRequiredFields, Toast.LENGTH_SHORT);
                    } else {
                        //									toastMsg.showToastMsg(MessageInfo.selectBankDeposit, Toast.LENGTH_SHORT);
                    }
                }
            } else {
                isSelectedDeposit = true;
                if (isBlankFieldValue) {
                    //								toastMsg.showToastMsg(MessageInfo.fillRequiredFields, Toast.LENGTH_SHORT);
                }
            }


            if (rbn_NoneToReportUrgent.isChecked() || rbn_YesUrgent.isChecked()) {
                if (rbn_YesUrgent.isChecked())
                    ActivityStringInfo.chkDetail3_urgent = "0";
                else
                    ActivityStringInfo.chkDetail3_urgent = "1";
                if (rbn_NoneToReportMessage.isChecked() || rbn_YesMessage.isChecked()) {
                    if (rbn_YesMessage.isChecked())
                        ActivityStringInfo.chkDetail3_message = "0";
                    else
                        ActivityStringInfo.chkDetail3_message = "1";

                } else {
                    //								toastMsg.showToastMsg(MessageInfo.logMessges, Toast.LENGTH_SHORT);
                }
            } else {
                //							toastMsg.showToastMsg(MessageInfo.logUrgentIssues, Toast.LENGTH_SHORT);
            }


            if (rbnNoneToLogRepair.isChecked() || rbnYesRepair.isChecked()) {
                if (rbnYesRepair.isChecked())
                    ActivityStringInfo.chkDetail4_newRepair = "0";
                else
                    ActivityStringInfo.chkDetail4_newRepair = "1";
                if (rbnNoneToLogFollowUp.isChecked() || rbnYesFollowUp.isChecked()) {
                    if (rbnYesFollowUp.isChecked())
                        ActivityStringInfo.chkDetail4_followUP = "0";
                    else
                        ActivityStringInfo.chkDetail4_followUP = "1";

                    //								insertLogDataForMaintenanceIntoDatabase();
                } else {
                    //								toastMsg.showToastMsg(MessageInfo.logFollowUpItems, Toast.LENGTH_SHORT);

                }
            } else {
                //							toastMsg.showToastMsg(MessageInfo.logNewRepairItems, Toast.LENGTH_SHORT);
            }


            if (!ActivityStringInfo.isCreateNewLog) {
                Log.v("TAG", "create new log flag false enter");
                if (ActivityStringInfo.selectedCreatedUserId.equals(ActivityStringInfo.strUser_id)) {
                    if (!Utility.compareLastSyncForDisable(mContext)) {
                        insertLogDataForSafetyIntoDatabase();
                        insertLogDataOfProductivityIntoDatabase();
                        insertLogDataForEmpRelationIntoDatabase();
                        insertLogDataForMaintenanceIntoDatabase();
                        insertLogDataForLogFlagsIntoDatabase("0", ActivityStringInfo.selectedLogId, ActivityStringInfo.selectedCreatedUserId);
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
                Log.v("TAG", "create new log  flag false exit");
            } else if (ActivityStringInfo.isCreateNewLog) {
                Log.v("TAG", "create new log flag true enter");
                insertLogDataForSafetyIntoDatabase();
                //TODO Newly added
                insertLogDataOfProductivityIntoDatabase();
                insertLogDataForEmpRelationIntoDatabase();
                insertLogDataForMaintenanceIntoDatabase();
                insertLogDataForLogFlagsIntoDatabase("0", ActivityStringInfo.selectedLogId, ActivityStringInfo.selectedCreatedUserId);
                flag = true;
                Log.v("TAG", "create new log flag true exit");
            }

            return flag;
        } catch (Exception e) {
            return false;
        }
    }

    public void insertLogDataForSafetyIntoDatabase() {
        try {
            Log.v("TAG", "insertLogDataForSafetyIntoDatabase enter");

            Cursor c1 = null;

            if (isUpdateOrInsertRecord.equals("insert")) {
                System.out.println("LOG DETAIL INSERT");
                Log.v("TAG", "LOG DETAIL INSERT enter");
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);

                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogDetails(ActivityStringInfo.selectedLogId, ActivityStringInfo.selectedLogForDate,
                        ActivityStringInfo.strLogEnteredDate, ActivityStringInfo.strUser_id,
                        ActivityStringInfo.strFirstName + "  " + ActivityStringInfo.strLastName,
                        ActivityStringInfo.selectedUserTypeId, sdfDate.format(cal.getTime()), ActivityStringInfo.selectedLogStatus);

                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogSafetyLog(ActivityStringInfo.strSafetyLogIdForAccidents, ActivityStringInfo.selectedLogId, "1", "False");
                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogSafetyLog(ActivityStringInfo.strSafetyLogIdForInjury, ActivityStringInfo.selectedLogId, "2", "False");
                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogSafetyLog(ActivityStringInfo.strSafetyLogIdForDamage, ActivityStringInfo.selectedLogId, "3", "False");
                Log.v("TAG", "LOG DETAIL INSERT exit");
            }

            // TODO: 23/02/17 Commented for Logs issue.  -- Sangram H.
            else if (isUpdateOrInsertRecord.equals("update")) {
                System.out.println("LOG DETAIL UPDATE");
                String strStatus = "";
                if (ActivityStringInfo.selectedLogStatus.equals("P"))
                    strStatus = "P";
                else
                    strStatus = "PU";
                MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetails(ActivityStringInfo.selectedLogId, ActivityStringInfo.selectedUserTypeId, strStatus);
            }
            /*
             if(completeClick)
				{
					if(ActivityStringInfo.selectedLogStatus.equals("P"))
						strStatus = "I";
					else
						strStatus = "U";

					MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetails(ActivityStringInfo.selectedLogId, ActivityStringInfo.selectedUserTypeId, strStatus);
					}
				if(saveClick)
				{

					if(ActivityStringInfo.selectedLogStatus.equals("P"))
					{
						if(isUpdateOrInsertRecord.equals("insert"))
						{
							strStatus = "PI";
							}
						 if(isUpdateOrInsertRecord.equals("update")){
							strStatus = "PU";
							}
					}
					else{
						strStatus = "U";
					}

					MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetails(ActivityStringInfo.selectedLogId, ActivityStringInfo.selectedUserTypeId, strStatus);
					}
			 */

            c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getLogSafetyLogRecordsByLogId(ActivityStringInfo.selectedLogId);
            if (c1.getCount() > 0) {
                Log.v("TAG", "update log safety log enter");
                if (c1.moveToFirst()) {
                    do {
                        if (ActivityStringInfo.safetyIssueList.size() != 0) {
                            MyDatabaseInstanceHolder.getDatabaseHelper().updateLogSafetyLog(c1.getString(0), ActivityStringInfo.selectedLogId, c1.getString(2), "True");
                        } else {
                            MyDatabaseInstanceHolder.getDatabaseHelper().updateLogSafetyLog(c1.getString(0), ActivityStringInfo.selectedLogId, c1.getString(2), "False");
                        }
                    } while (c1.moveToNext());
                }
                Log.v("TAG", "update log safety log exit");
            }
            c1.close();

            for (int i = 0; i < ActivityStringInfo.safetyIssueList.size(); i++) {
                Log.v("TAG", "insert log safety desc enter");
                HashMap<String, String> getSafetyIssueValue = ActivityStringInfo.safetyIssueList.get(i);

                if (getSafetyIssueValue.get(DatabaseConstant.key_STATUS).equals("N")) {
                    String safetyLogId = getSafetyIssueValue.get(DatabaseConstant.key_SAFETYLOGID);
                    String employeeId = getSafetyIssueValue.get(DatabaseConstant.key_EMPLOYEEID);
                    String description = getSafetyIssueValue.get(DatabaseConstant.key_DESCRIPTION);
                    String addedById = getSafetyIssueValue.get(DatabaseConstant.key_ADDEDBYID);

                    long rs = MyDatabaseInstanceHolder.getDatabaseHelper().insertLogSafetyDesc("", safetyLogId, employeeId, description, addedById);
                    System.out.print("Insert" + rs);
                }
                Log.v("TAG", "insert log safety desc exit");
            }

            for (int j = 0; j < ActivityStringInfo.safetyIssuesListDelete.size(); j++) {
                MyDatabaseInstanceHolder.getDatabaseHelper().deleteLogSafetyDesc(ActivityStringInfo.safetyIssuesListDelete.get(j));
            }
            ActivityStringInfo.safetyIssuesListDelete.clear();

            ///insert record for Quality

            for (int i = 0; i < ActivityStringInfo.personQuality.size(); i++) {
                Log.v("TAG", "insert log quality enter");
                HashMap<String, String> getPersonQualityValue = ActivityStringInfo.personQuality.get(i);
                if (getPersonQualityValue.get(DatabaseConstant.key_STATUS).equals("N")) {
                    String qualityId = getPersonQualityValue.get(DatabaseConstant.key_QUALITYID);
                    String logId = getPersonQualityValue.get(DatabaseConstant.key_LOGID);
                    String userId = getPersonQualityValue.get(DatabaseConstant.key_USERID);
                    String complaint = getPersonQualityValue.get(DatabaseConstant.key_COMPLAINT);

                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLogQuality(qualityId, logId, userId, complaint);
                }
                Log.v("TAG", "insert log quality exit");
                /*					else if(getPersonAccidentValue.get(DatabaseConstant.key_STATUS).equals("D"))
                    {
						MyDatabaseInstanceHolder.getDatabaseHelper().deleteLogQuality(getPersonAccidentValue.get(DatabaseConstant.key_QUALITYID));
					}*/
            }
            for (int j = 0; j < ActivityStringInfo.personQualityDelete.size(); j++) {
                MyDatabaseInstanceHolder.getDatabaseHelper().deleteLogQuality(ActivityStringInfo.personQualityDelete.get(j));
            }
            ActivityStringInfo.personQualityDelete.clear();

        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        Log.v("TAG", "insertLogDataForSafetyIntoDatabase exit");
    }


    /**
     * Insert Log Data of Productivity Screen no2
     */
    public void insertLogDataOfProductivityIntoDatabase() {
        try {
            Log.v("TAG", "insert Log productivity data enter");
            System.out.println("isUpdateOrInsertRecord===2" + isUpdateOrInsertRecord);
            if (isUpdateOrInsertRecord.equals("update")) {
                Log.v("TAG", "update log prod enter");
                String logId = "";
                if (ActivityStringInfo.productivityFieldsMap1.size() != 0) {
                    for (int i = 0; i < ActivityStringInfo.productivityFieldsMap1.size(); i++) {
                        HashMap<String, String> getProductivityFieldValue = ActivityStringInfo.productivityFieldsMap1.get(i);
                        logId = getProductivityFieldValue.get(DatabaseConstant.key_LOGID);
                        String fieldValue = getProductivityFieldValue.get(DatabaseConstant.key_FIELDVALUE);
                        String fieldId = getProductivityFieldValue.get(DatabaseConstant.key_FIELDID);
                        String fieldType = getProductivityFieldValue.get(DatabaseConstant.key_FIELDTYPE);

                        Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getlogProductivityDetRecordByFieldIdLogId(fieldId, logId);
//                        JSONArray jArrayLogProductDet = new JSONArray();
//
//                        if (c1.getCount() > 0) {
//                            while (c1.moveToNext()) {
//                                JSONObject jobject_LogProductDet = new JSONObject();
//                                jobject_LogProductDet.put("FIELDID", c1.getString(2));
//                                jobject_LogProductDet.put("FIELDTYPE", c1.getString(4));
//                                jobject_LogProductDet.put("FIELDVALUE", c1.getString(3));
//                                jobject_LogProductDet.put("ID", c1.getString(0));
//                                jobject_LogProductDet.put("LOGID", c1.getString(1));
//                                jArrayLogProductDet.put(jobject_LogProductDet);
//                                //mHandler.sendEmptyMessage(EX);
//                            }
//
//                            System.out.println("LOG_PRODUCTIVITY_DET " + jArrayLogProductDet.toString());
//                        }
                        long reslt = 0;
                        if (c1.getCount() == 0) {
                            reslt = MyDatabaseInstanceHolder.getDatabaseHelper().insertLogProductivityDet("", logId, fieldId, fieldValue, fieldType);

                        } else {
                            reslt = MyDatabaseInstanceHolder.getDatabaseHelper().updateLogProductivityDet(logId, fieldId, fieldValue);
                        }
                        c1.close();

                    }
                } else {
                    logId = ActivityStringInfo.selectedLogId;
                }
                if (ActivityStringInfo.selectedUserBankFlag.toLowerCase().equals("true") && !(logId.equals(""))) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogProductivityDet(logId, "0", ActivityStringInfo.bankDepositComment);
                } else {
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogProductivityDet(logId, "0", "");
                }
                Log.v("TAG", "update log prod exit");
            } else if (isUpdateOrInsertRecord.equals("insert")) {
                Log.v("TAG", "insert log prod enter");
                String logId = "";
                if (ActivityStringInfo.productivityFieldsMap1.size() != 0) {
                    for (int i = 0; i < ActivityStringInfo.productivityFieldsMap1.size(); i++) {
                        HashMap<String, String> getProductivityFieldValue = ActivityStringInfo.productivityFieldsMap1.get(i);
                        logId = getProductivityFieldValue.get(DatabaseConstant.key_LOGID);
                        String fieldValue = getProductivityFieldValue.get(DatabaseConstant.key_FIELDVALUE);
                        String fieldId = getProductivityFieldValue.get(DatabaseConstant.key_FIELDID);
                        String fieldType = getProductivityFieldValue.get(DatabaseConstant.key_FIELDTYPE);

                        MyDatabaseInstanceHolder.getDatabaseHelper().insertLogProductivityDet("", logId, fieldId, fieldValue, fieldType);
                    }
                } else {
                    logId = ActivityStringInfo.selectedLogId;
                }
                if (ActivityStringInfo.selectedUserBankFlag.toLowerCase().equals("true") && !(logId.equals(""))) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLogProductivityDet("", logId, "0", ActivityStringInfo.bankDepositComment, "");
                } else {
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogProductivityDet(logId, "0", "");
                }
                Log.v("TAG", "insert log prod exit");
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        Log.v("TAG", "insert Log productivity data exit");
    }

    /**
     * Insert Log Data of EmpRElations Screen no3
     */

    public void insertLogDataForEmpRelationIntoDatabase() {
        try {
            System.out.println("isUpdateOrInsertRecord===3" + isUpdateOrInsertRecord);
            Log.v("TAG", "insert log emp relations enter");

            for (int i = 0; i < ActivityStringInfo.employeeRelations.size(); i++) {
                Log.v("TAG", "insert log emp relations  desc enter");
                HashMap<String, String> getPersonNoShowValue = ActivityStringInfo.employeeRelations.get(i);
                if (getPersonNoShowValue.get(DatabaseConstant.key_STATUS).equals("N")) {
                    String logId = getPersonNoShowValue.get(DatabaseConstant.key_LOGID);
                    String empFieldId = getPersonNoShowValue.get(DatabaseConstant.key_EMPFIELDID);
                    String employee = getPersonNoShowValue.get(DatabaseConstant.key_EMPLOYEE);
                    String addedById = getPersonNoShowValue.get(DatabaseConstant.key_ADDEDBYID);
                    String description = getPersonNoShowValue.get(DatabaseConstant.key_DESCRIPTION);

                    long rs = MyDatabaseInstanceHolder.getDatabaseHelper().insertLogEmpRelationDesc("", logId, empFieldId, employee, addedById, description);
                    System.out.print("Insert" + rs);
                }
                Log.v("TAG", "insert log emp relations  desc exit");
            }
            for (int j = 0; j < ActivityStringInfo.employeeRelationsDelete.size(); j++) {
                MyDatabaseInstanceHolder.getDatabaseHelper().deleteLogEmpRelationDesc(ActivityStringInfo.employeeRelationsDelete.get(j));
            }
            ActivityStringInfo.employeeRelationsDelete.clear();

            // update record for communication detail
            if (isUpdateOrInsertRecord.equals("insert")) {
                Log.v("TAG", "insert log comm details enter");
                String strFlagStatus = "";
                String strcommId = "";
                if (ActivityStringInfo.chkDetail3_urgent.equals("0")) {
                    strFlagStatus = "True";
                    strcommId = "1";
                } else {
                    strFlagStatus = "False";
                    strcommId = "1";
                }
                String maxId1 = "" + (Integer.parseInt(createCommDetLog("LOG_COMMUNICATION_DET")) + 1);
                /*System.out.println("maxId1 "+maxId1);
                System.out.println("ActivityStringInfo.selectedLogId "+ActivityStringInfo.selectedLogId);
				System.out.println("strcommId "+strcommId);
				System.out.println(" strFlagStatus "+strFlagStatus);*/
                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogCommunicationDet(maxId1, ActivityStringInfo.selectedLogId, strcommId, strFlagStatus);

                if (ActivityStringInfo.chkDetail3_message.equals("0")) {
                    strFlagStatus = "True";
                    strcommId = "2";
                } else {
                    strFlagStatus = "False";
                    strcommId = "2";
                }
                String maxId2 = "" + (Integer.parseInt(createCommDetLog("LOG_COMMUNICATION_DET")) + 1);
                MyDatabaseInstanceHolder.getDatabaseHelper().insertLogCommunicationDet(maxId2, ActivityStringInfo.selectedLogId, strcommId, strFlagStatus);
                Log.v("TAG", "insert log comm details exit");
            } else if (isUpdateOrInsertRecord.equals("update")) {
                Log.v("TAG", "update log comm details enter");
                if (ActivityStringInfo.chkDetail3_urgent.equals("0"))
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogCommunicationDet(ActivityStringInfo.selectedLogId, "1", "True");
                else
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogCommunicationDet(ActivityStringInfo.selectedLogId, "1", "False");


                if (ActivityStringInfo.chkDetail3_message.equals("0"))
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogCommunicationDet(ActivityStringInfo.selectedLogId, "2", "True");
                else
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogCommunicationDet(ActivityStringInfo.selectedLogId, "2", "False");
                Log.v("TAG", "update log comm details exit");
            }


        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        Log.v("TAG", "insert log emp relations exit");
    }

    /***
     * Insert Log Data for Maintenance Screen 4
     */

    public void insertLogDataForMaintenanceIntoDatabase() {
        try {
            Log.v("TAG", "insert log data maintaince enter");
            System.out.println("isUpdateOrInsertRecordForMain===" + isUpdateOrInsertRecordForMain);
            System.out.println("isUpdateOrInsertRecordForFoll===" + isUpdateOrInsertRecordForFoll);
            Log.v("LogDetailsAll.java", "ActivityStringInfo.selectedLogStatus======>>>>>>>>>>>>>>>>>>" + ActivityStringInfo.selectedLogStatus);
            Log.v("LogDetailsAll.java", "Log saved :" + this.saveClick + " Log completed : " + this.completeClick);

            String strStatus = "";
            if (completeClick) {
                Log.v("TAG", "status complete click enter");
                if (ActivityStringInfo.selectedLogStatus.equals("I")) //P
                    strStatus = "I";
                else
                    strStatus = "U";

                MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetails(ActivityStringInfo.selectedLogId, ActivityStringInfo.selectedUserTypeId, strStatus);
                Log.v("TAG", "status complete click exit");
            }
            if (saveClick) {
                Log.v("TAG", "status save click enter");
                if (ActivityStringInfo.selectedLogStatus.equals("I")) //P
                {
                    strStatus = "PI";

                } else if (ActivityStringInfo.selectedLogStatus.equals("P"))  // if(ActivityStringInfo.selectedLogStatus.equals("PI") || ActivityStringInfo.selectedLogStatus.equals("PU"))
                {
                    strStatus = "PU";
                } else {
                    strStatus = "U";
                }

                MyDatabaseInstanceHolder.getDatabaseHelper().updateLogDetails(ActivityStringInfo.selectedLogId, ActivityStringInfo.selectedUserTypeId, strStatus);
                Log.v("TAG", "status save click exit");
            }

            // update maintenanceRepaired record
            if (isUpdateOrInsertRecordForMain.equals("insert")) {
                Log.v("TAG", "insert maintenanceRepaired record enter");
                String maxId2 = "" + (Integer.parseInt(createCommDetLog("LOG_MAINTENANCE_LOG")) + 1);
                if (ActivityStringInfo.chkDetail4_newRepair.equals("0"))
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLogMaintenanceLog(maxId2, ActivityStringInfo.selectedLogId, "1", "True");
                else
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLogMaintenanceLog(maxId2, ActivityStringInfo.selectedLogId, "1", "False");
                Log.v("TAG", "insert maintenanceRepaired record exit");
            } else if (isUpdateOrInsertRecordForMain.equals("update")) {
                Log.v("TAG", "update maintenanceRepaired record enter");
                if (ActivityStringInfo.chkDetail4_newRepair.equals("0"))
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogMaintenanceLog(ActivityStringInfo.selectedLogId, "1", "True");
                else
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogMaintenanceLog(ActivityStringInfo.selectedLogId, "1", "False");
                Log.v("TAG", "update maintenanceRepaired record exit");
            }


            // insert maintenanceRepaired record Written Warning

            for (int i = 0; i < ActivityStringInfo.maintenanceRepaired.size(); i++) {
                Log.v("TAG", "insert maintenanceRepaired record Written Warning enter");
                HashMap<String, String> getMaintenanceRepairedValue = ActivityStringInfo.maintenanceRepaired.get(i);
                if (getMaintenanceRepairedValue.get(DatabaseConstant.key_STATUS).equals("N")) {
                    String logId = getMaintenanceRepairedValue.get(DatabaseConstant.key_LOGID);
                    String mrrId = getMaintenanceRepairedValue.get(DatabaseConstant.key_MRRID);
                    String employee = getMaintenanceRepairedValue.get(DatabaseConstant.key_EMPLOYEE);
                    String description = getMaintenanceRepairedValue.get(DatabaseConstant.key_DESCRIPTION);
                    String taskId = getMaintenanceRepairedValue.get(DatabaseConstant.key_TASKID);

                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLogMaintenanceDet("", logId, mrrId, employee, description, taskId);
                }
                /*else if(getPersonAccidentValue.get(DatabaseConstant.key_STATUS).equals("D"))
                        {
							MyDatabaseInstanceHolder.getDatabaseHelper().deleteLogMaintenanceDet(getPersonAccidentValue.get(DatabaseConstant.key_MRRDETAILSID));
						}*/

                Log.v("TAG", "insert maintenanceRepaired record Written Warning exit");
            }
            for (int j = 0; j < ActivityStringInfo.maintenanceRepairedDelete.size(); j++) {
                MyDatabaseInstanceHolder.getDatabaseHelper().deleteLogMaintenanceDet(ActivityStringInfo.maintenanceRepairedDelete.get(j));
            }
            ActivityStringInfo.maintenanceRepairedDelete.clear();


            // update follow up record
            if (isUpdateOrInsertRecordForFoll.equals("insert")) {
                Log.v("TAG", "insert follow up record enter");
                String maxId2 = "" + (Integer.parseInt(createCommDetLog("LOG_FOLLOW_UP_LOG")) + 1);
                if (ActivityStringInfo.chkDetail4_followUP.equals("0")) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLogFollowUpLog(maxId2, ActivityStringInfo.selectedLogId, "1", "True", "");
                } else {
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLogFollowUpLog(maxId2, ActivityStringInfo.selectedLogId, "1", "False", "");
                }
                Log.v("TAG", "insert follow up record exit");
            } else if (isUpdateOrInsertRecordForFoll.equals("update")) {
                Log.v("TAG", "update follow up record enter");
                if (ActivityStringInfo.chkDetail4_followUP.equals("0"))
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogFollowUpLog(ActivityStringInfo.selectedLogId, "1", "True");
                else
                    MyDatabaseInstanceHolder.getDatabaseHelper().updateLogFollowUpLog(ActivityStringInfo.selectedLogId, "1", "False");
                Log.v("TAG", "update follow up record exit");
            }

            // insert additional notes

            for (int i = 0; i < ActivityStringInfo.maintenanceNotes.size(); i++) {
                Log.v("TAG", "insert additional notes enter");
                HashMap<String, String> getMaintenanceNotesValue = ActivityStringInfo.maintenanceNotes.get(i);
                if (getMaintenanceNotesValue.get(DatabaseConstant.key_STATUS).equals("N")) {
                    String logId = getMaintenanceNotesValue.get(DatabaseConstant.key_LOGID);
                    String employee = getMaintenanceNotesValue.get(DatabaseConstant.key_EMPLOYEE);
                    String description = getMaintenanceNotesValue.get(DatabaseConstant.key_DESCRIPTION);

                    MyDatabaseInstanceHolder.getDatabaseHelper().insertLogNotes("", logId, employee, description);
                }
                /*else if(getPersonAccidentValue.get(DatabaseConstant.key_STATUS).equals("D"))
                        {
							MyDatabaseInstanceHolder.getDatabaseHelper().deleteLogNotes(getPersonAccidentValue.get(DatabaseConstant.key_ID));
						}*/
                Log.v("TAG", "insert additional notes exit");
            }
            for (int j = 0; j < ActivityStringInfo.maintenanceNotesDelete.size(); j++) {
                MyDatabaseInstanceHolder.getDatabaseHelper().deleteLogNotes(ActivityStringInfo.maintenanceNotesDelete.get(j));
            }
            ActivityStringInfo.maintenanceNotesDelete.clear();

        } catch (Exception e) {
            Utility.saveExceptionDetails(LogDetailsFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        Log.v("TAG", "insert log data maintaince exit");
    }

    /*insert data for flags*/
    public void insertLogDataForLogFlagsIntoDatabase(String flagId, String flagLogId, String flagUserId) {
        if (isUpdateOrInsertRecordForFlags.equals("insert")) {
            Log.v("in insertlog data ", "if case " + listToBindFlag.size());
            //for(int i = 0; i < dialogCustom.flags.size(); i++) {
            for (int i = 0; i < listToBindFlag.size(); i++) {
                //String flagDesc = dialogCustom.flags.get(i).getFlagText();
                String flagDesc = listToBindFlag.get(i).getFlagText();
                if (flagDesc.trim().equals("Other")) {
                    flagDesc = listToBindFlag.get(i).getOtherFlagText();
                }


                String flag = listToBindFlag.get(i).getFlagText().replaceAll("\\s", "");
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.US);
                String flaggedDate = sdfDate.format(cal.getTime());
                long id = MyDatabaseInstanceHolder.getDatabaseHelper().insertLogFlags(flagDesc, flag, flaggedDate, flagId, flagLogId, flagUserId, ActivityStringInfo.strFirstName + " " + ActivityStringInfo.strLastName);
                Log.v("LogDetails All", "id: " + id);
            }
        } else {
            //Log.v("Log Details All insert log", " hi");

            //first delete all flags
            MyDatabaseInstanceHolder.getDatabaseHelper().deleteAllLogFlagsList();
            String flagId2 = null;
            //for(int i = 0; i < dialogCustom.flags.size(); i++) {
            for (int i = 0; i < listToBindFlag.size(); i++) {
                if (listToBindFlag.get(i).getFlagId() != null) {
                    flagId2 = listToBindFlag.get(i).getFlagId();
                } else {
                    flagId2 = "0";
                }

                if (listToBindFlag.get(i).getFlagLogId() != null) {
                    flagLogId = listToBindFlag.get(i).getFlagLogId();
                }
                String flagDesc = listToBindFlag.get(i).getFlagText();
                if (flagDesc.trim().equals("Other")) {
                    flagDesc = listToBindFlag.get(i).getOtherFlagText();
                }
                //String flag     = listToBindFlag.get(i).getFlagText().replaceAll("\\s", "");
                String flag = listToBindFlag.get(i).getFlag();
                if (flag == null) {
                    flag = listToBindFlag.get(i).getFlagText().replaceAll("\\s", "");
                }
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.US);
                String flaggedDate = sdf.format(cal.getTime());
                long id = MyDatabaseInstanceHolder.getDatabaseHelper().insertLogFlags(flagDesc, flag, flaggedDate, flagId2, flagLogId, flagUserId, ActivityStringInfo.strFirstName + " " + ActivityStringInfo.strLastName);
                //Log.v("Log Details All ", "id: " + id);
            }
        }

    }

    public String createCommDetLog(String tableName) {
        Cursor c = null;
        String maxId = "";

        try {

            if (tableName.equals("LOG_MAINTENANCE_LOG"))
                c = MyDatabaseInstanceHolder.getDatabaseHelper().getMaxMainLogId();
            else if (tableName.equals("LOG_FOLLOW_UP_LOG"))
                c = MyDatabaseInstanceHolder.getDatabaseHelper().getMaxFollowUpLogId();
            else if (tableName.equals("LOG_COMMUNICATION_DET"))
                c = MyDatabaseInstanceHolder.getDatabaseHelper().getMaxCommDetId();

            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getString(0).equals(null))
                        maxId = "0";
                    else
                        maxId = c.getString(0);
                }
            } else {
                maxId = "0";
            }
            System.out.println("maxSafetyLogId===" + maxId);
            if (c != null)
                c.close();

        } catch (Exception e) {
            maxId = "0";
            if (c != null)
                c.close();

            e.printStackTrace();
        }
        return maxId;
    }
}