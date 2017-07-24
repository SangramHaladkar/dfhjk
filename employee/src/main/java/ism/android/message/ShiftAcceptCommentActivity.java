package ism.android.message;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseActivity;
import ism.android.utils.MessageInfo;
import ism.android.webservices.SyncServiceHelper;
import ism.android.webservices.Synchronization;


public class ShiftAcceptCommentActivity extends AppBaseActivity {

    //for UI component
    ImageView img_MessageImage;
    Button btn_Submit;
    EditText ed_Comments;

    //Other Class Declaration
    ActivityStringInfo stringInfo;
    SyncServiceHelper serviceHelper;

    //Variable Declaration

    public static final String INFO = "INFO";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shift_comment);
            Intent i = getIntent();
            stringInfo = (ActivityStringInfo) i.getSerializableExtra(INFO);
            if (stringInfo == null) {
                stringInfo = new ActivityStringInfo();
            }
            mContext = this;

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            serviceHelper = new SyncServiceHelper();


            if (stringInfo.strCommentForWhich.equals("GA")) {
                getSupportActionBar().setTitle("Give Shift Away");
            } else if (stringInfo.strCommentForWhich.equals("GT")) {
                getSupportActionBar().setTitle("Give Shift To");
            } else if (stringInfo.strCommentForWhich.equals("T")) {
                getSupportActionBar().setTitle("Trade Shift");
            }

            btn_Submit = (Button) findViewById(R.id.btnSubmit);
            ed_Comments = (EditText) findViewById(R.id.edComments);

            if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
                new SplashActivity().setValue();
            }

            btn_Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stringInfo.strCommentForWhich.equals("GA"))
                        new SelectDataTaskForShiftGiveAwayComment().execute(ed_Comments.getText().toString());
                    else if (stringInfo.strCommentForWhich.equals("GT"))
                        new SelectDataTaskForShiftGiveToComment().execute(ed_Comments.getText().toString());
                    else if (stringInfo.strCommentForWhich.equals("T"))
                        new SelectDataTaskForTradeShiftComment().execute(ed_Comments.getText().toString());
                }
            });

        } catch (Exception e) {
            Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
            e.printStackTrace();
        }
    }


    private class SelectDataTaskForShiftGiveToComment extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                try {
                    strMsg = serviceHelper.sendShiftGiveToComment(ShiftAcceptCommentActivity.this, stringInfo.strShiftId, args[0]);
                    if (strMsg.equals("true")) {
                        Synchronization syc = new Synchronization(mContext);
                        try {
                            strMsg = syc.getInformation(ShiftAcceptCommentActivity.this);
                        } catch (Exception e) {
                            Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.equals("true")) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                    if (ActivityStringInfo.strMsgArrListSize == 1) {
                        if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                            ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                            ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                        }
                    }
                    ActivityStringInfo.shouldRefreshed = true;
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    onBackPressed();
                } else if (!strMsg.equals("false")) {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SelectDataTaskForShiftGiveAwayComment extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {

                ShiftAcceptCommentActivity.this.
                        showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                strMsg = serviceHelper.sendShiftGiveAwayComment(ShiftAcceptCommentActivity.this, stringInfo.strShiftId, args[0]);
                if (strMsg.equals("true")) {
                    Synchronization syc = new Synchronization(mContext);
                    strMsg = syc.getInformation(ShiftAcceptCommentActivity.this);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.equals("true")) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                    if (ActivityStringInfo.strMsgArrListSize == 1) {
                        if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                            ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                            ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                        }
                    }
                    ActivityStringInfo.shouldRefreshed = true;
                    onBackPressed();
                } else if (!strMsg.equals("false")) {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                e.printStackTrace();
            }
        }
    }

    private class SelectDataTaskForTradeShiftComment extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strloadingProgress_txt);
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {
            try {
                try {
                    strMsg = serviceHelper.sendTradeShiftAccept(ShiftAcceptCommentActivity.this, stringInfo.strShiftId, stringInfo.getSelectedId(), args[0]);
                    if (strMsg.equals("true")) {
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, stringInfo.strMessageId);
                        if (ActivityStringInfo.strMsgArrListSize == 1) {
                            if (ActivityStringInfo.msgCurrentPageStartIndex != 0 && ActivityStringInfo.msgCurrentPageNumber != 1) {
                                ActivityStringInfo.msgCurrentPageNumber = ActivityStringInfo.msgCurrentPageNumber - 1;
                                ActivityStringInfo.msgCurrentPageStartIndex = ActivityStringInfo.msgCurrentPageStartIndex - 8;
                            }
                        }
                        Synchronization syc = new Synchronization(mContext);
                        try {
                            strMsg = syc.getInformation(ShiftAcceptCommentActivity.this);
                        } catch (Exception e) {
                            Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();
                if (strMsg.equals("true")) {
                    stringInfo.clear();
                    ActivityStringInfo.shouldRefreshed = true;
                    onBackPressed();
                } else if (!strMsg.equals("false")) {
                    if (Utility.Connectivity_Internet(mContext)) {
                        if (!strMsg.equals(""))
                            showToast(strMsg, Toast.LENGTH_SHORT);
                    } else
                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(ShiftAcceptCommentActivity.this, e);
                e.printStackTrace();
            }
        }
    }
}
