package ism.manager.log;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ism.manager.R;
import ism.manager.Utility;

/**
 * Created by Raj on 25/03/16.
 */
public class AddFlagDialog extends Dialog {

    //UI content
    ListView dialogListView;
    EditText otherFlagEditText;
    Button addFlag;
    TextView dialogBtnClose;
    TextView errorText;

    LogsAddFlagAdapter logsAddFlagAdapter;

    Context dContext;

    // Other list variable
    ArrayList<AddFlagItem> flagItemsList;
    ArrayList<AddFlagItem> defaultflagItemsList;
    ArrayList<AddFlagItem> selectedFlagItemsList;


    AddFlagDialogListener addFlagDialogListener;

    public static final String[] flagTexts = new String[]{
            "Security Issue",
            "Employee Incident Injury",
            "Guest Incident Injury",
            "Major Property Damage",
            "Major Equipment Issue Breakdown",
            "Other"
    };
    public static final Integer[] iconsImage = {
            R.drawable.flag_security,
            R.drawable.flag_emp_injry,
            R.drawable.flag_guest_injury,
            R.drawable.flag_major_prop_dmg,
            R.drawable.flag_major_equip_brkdwn,
            R.drawable.flag_others
    };


    public interface AddFlagDialogListener {
        public abstract void onAddFlagButtonListener(ArrayList<AddFlagItem> selectedFlagList);
    }

    public void setCallBack(AddFlagDialogListener listener) {
        this.addFlagDialogListener = listener;
    }

    public AddFlagDialog(Context context, ArrayList<AddFlagItem> flagItemList) {
        super(context);
        this.dContext = context;
        this.defaultflagItemsList = new ArrayList<AddFlagItem>();
        this.flagItemsList = flagItemList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_log_dialog);
        dialogListView = (ListView) this.findViewById(R.id.dialogFlag_list);
        otherFlagEditText = (EditText) findViewById(R.id.otherFlagEdText);
        addFlag = (Button) findViewById(R.id.dilogBtnAddFlag);
        dialogBtnClose = (TextView) findViewById(R.id.dialogBtnClose);
        errorText = (TextView) findViewById(R.id.txtError);

        setCancelable(false);
        setListeners();
        loadFlagList();

    }

    private void loadFlagList() {
        for (int i = 0; i < flagTexts.length; i++) {
            defaultflagItemsList.add(new AddFlagItem(iconsImage[i], flagTexts[i], false));
        }

        if (flagItemsList != null) {
            for (int i = 0; i < defaultflagItemsList.size(); i++) {
                String rowFlag = defaultflagItemsList.get(i).getFlagText().replaceAll("\\s", "");
                for (int j = 0; j < flagItemsList.size(); j++) {
                    String returnFlag = flagItemsList.get(j).getFlag().trim();

                    if (rowFlag.equalsIgnoreCase(returnFlag)) {
                        defaultflagItemsList.get(i).setChecked(true);
                        defaultflagItemsList.get(i).setDescription(flagItemsList.get(j).getDescription());
                        defaultflagItemsList.get(i).setFlag(flagItemsList.get(j).getFlag());
                        if (flagItemsList.get(j).getFlag().trim().equals("Other")) {
                            otherFlagEditText.setVisibility(View.VISIBLE);
                            String otherText = flagItemsList.get(j).getOtherFlagText();
                            otherFlagEditText.setText(otherText);
                        } else {
                            otherFlagEditText.setVisibility(View.GONE);
                            otherFlagEditText.setText("");
                        }
                        defaultflagItemsList.get(i).setFlaggedDate(flagItemsList.get(j).getFlaggedDate());
                        defaultflagItemsList.get(i).setFlagId(flagItemsList.get(j).getFlagId());
                        defaultflagItemsList.get(i).setFlagLogId(flagItemsList.get(j).getFlagLogId());
                        defaultflagItemsList.get(i).setFlagUserId(flagItemsList.get(j).getFlagUserId());
                        defaultflagItemsList.get(i).setFlagUserName(flagItemsList.get(j).getFlagUserName());
                        break;
                    }
                }
            }
        }
        logsAddFlagAdapter = new LogsAddFlagAdapter(dContext, R.layout.listitem_logs_flag, this.defaultflagItemsList);
        dialogListView.setAdapter(logsAddFlagAdapter);
        Utility.setListViewHeightBasedOnChildren(dContext,dialogListView);
    }

    private void setListeners() {
        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView image = (ImageView) view.findViewById(R.id.flag_image);
                TextView txtView = (TextView) view.findViewById(R.id.flagname);
                AddFlagItem flagItem = (AddFlagItem) parent.getAdapter().getItem(position);
                boolean isCheck = flagItem.isChecked();
                if (isCheck) {
                    image.setImageDrawable(ContextCompat.getDrawable(dContext, R.drawable.flag_grey));
                    txtView.setTextColor(Color.parseColor("#565555"));
                    flagItem.setImageId(R.drawable.flag_grey);
                    if (flagItem.getFlagText().equals("Other")) {
                        otherFlagEditText.setText("");
                        otherFlagEditText.setVisibility(View.GONE);
                        errorText.setVisibility(View.GONE);
                    }
                    flagItem.setChecked(false);
                    if (flagItemsList != null && flagItemsList.size() > 0) {
                        for (AddFlagItem data : flagItemsList) {
                            if (data.getFlagText().equals(flagItem.getFlagText())) {
                                flagItemsList.remove(data);
                                break;
                            }
                        }
                    }
                } else {
                    image.setImageResource(iconsImage[position]);
                    txtView.setTextColor(Color.BLACK);
                    flagItem.setImageId(iconsImage[position]);
                    if (flagItem.getFlagText().equals("Other")) {
                        otherFlagEditText.setVisibility(View.VISIBLE);
                    }
                    flagItem.setChecked(true);
                    if (flagItemsList != null && flagItemsList.size() > 0) {
                        for (AddFlagItem data : flagItemsList) {
                            if (data.getFlagText().equals(flagItem.getFlagText())) {
                                flagItemsList.remove(data);
                                break;
                            }
                        }
                        flagItemsList.add(flagItem);
                    } else {
                        flagItemsList = new ArrayList<AddFlagItem>();
                        flagItemsList.add(flagItem);
                    }

                }
            }
        });

        dialogBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        addFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validate = true;
                for (AddFlagItem data : flagItemsList) {
                    if (data.getFlagText().equals(defaultflagItemsList.get(5).getFlagText())) {
                        if (otherFlagEditText.getText().toString().trim() != null && otherFlagEditText.getText().toString().trim().length() > 0) {
                            data.setOtherFlagText(otherFlagEditText.getText().toString());
                            errorText.setVisibility(View.GONE);
                            validate = true;
                        } else {
                            errorText.setVisibility(View.VISIBLE);
                            validate = false;
                        }
                        break;
                    }
                }
                if(validate) {
                    addFlagDialogListener.onAddFlagButtonListener(flagItemsList);
                    dismiss();
                }
            }
        });

        otherFlagEditText.addTextChangedListener(new TextWatcher() {

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
                    errorText.setVisibility(View.GONE);
            }
        });
    }
}
