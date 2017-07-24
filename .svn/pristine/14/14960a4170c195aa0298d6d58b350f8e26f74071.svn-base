package ism.manager.customview;

import java.util.ArrayList;
import java.util.List;

import ism.manager.ActivityStringInfo;
import ism.manager.log.AddFlagItem;
import ism.manager.log.LogsAddFlagAdapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CustomLogDialog {

//	Context ctx;
//
//	Dialog dialog = null;
//	List<AddFlagItem> rowItems;
//	List<AddFlagItem> flags;
//	List<AddFlagItem> flagFromDbList;
//	LogsAddFlagAdapter adapter;
//	String TAG = "CustomDialog";
//
//	//UI content
//	ListView dialogListView;
//	EditText otherFlagText;
//	Button addFlag;
//
//	// Other list variable
//	ArrayList<AddFlagItem> defaultFlagList;
//	ArrayList<AddFlagItem> selectedFlagList;
//	CustomLogDialogListener customLogDialogListener;
//
//
//	public static final String [] flagTexts = new String[] {"Security Issue", "Employee Incident Injury", "Guest Incident Injury" , "Major Property Damage",
//		"Major Equipment Issue Breakdown", "Other"};
//	public static final Integer [] iconsImage =  {R.drawable.flag_security, R.drawable.flag_emp_injry, R.drawable.flag_guest_injury, R.drawable.flag_major_prop_dmg,
//		R.drawable.flag_major_equip_brkdwn, R.drawable.flag_others};
//	public static final Integer [] icons =  {R.drawable.flag_grey_new, R.drawable.flag_grey_new, R.drawable.flag_grey_new, R.drawable.flag_grey_new,
//		R.drawable.flag_grey_new, R.drawable.flag_grey_new};
//
//
//	boolean isFirstClick = false;
////	MyDialogListener mListener;
//
//	public interface CustomLogDialogListener{
//		public abstract void onAddFlagButtonListener(ArrayList<AddFlagItem> selectedFlagList);
//	}
//
//	public void setCallBack(CustomLogDialogListener listener){
//		this.customLogDialogListener = listener;
//	}
//	public CustomDialog(Context context, ArrayList<AddFlagItem> flagItemList) {
//		super(context);
//		this.ctx = context;
//		this.flagFromDbList = flagItemList;
//		this.selectedFlagList = flagItemList;
//	}
//
//
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.custom_log_dialog);
//		dialogListView = (ListView) this.findViewById(R.id.dialogFlag_list);
//		otherFlagText = (EditText) findViewById(R.id.otherFlagEdText);
//		addFlag= (Button) findViewById(R.id.dilogBtnAddFlag);
//
//		rowItems = new ArrayList<AddFlagItem>();
//		flags = new ArrayList<AddFlagItem>();
//
//
//
//		for (int i = 0; i < flagTexts.length; i++) {
//
//			rowItems.add(new AddFlagItem(iconsImage[i], flagTexts[i],false));
//		}
//
//
//		if(!ActivityStringInfo.isCreateNewLog) {
//
//			if(flagFromDbList != null) {
//				for(int i = 0; i < rowItems.size(); i++ ) {
//					//compare two values from list and set flags
//					//loop for flagItem list
//					//String rowString=rowItems.get(i).getFlagText().trim();
//					String rowFlag     = rowItems.get(i).getFlagText().replaceAll("\\s", "");
//
//					for(int j = 0; j < flagFromDbList.size(); j++) {
//						String returnFlag =flagFromDbList.get(j).getFlag().trim();
//
//						if(rowFlag.equalsIgnoreCase(returnFlag)) {
//							rowItems.get(i).setChecked(true);
//							rowItems.get(i).setDescription(flagFromDbList.get(j).getDescription());
//							rowItems.get(i).setFlag(flagFromDbList.get(j).getFlag());
//							if(flagFromDbList.get(j).getFlag().trim().equals("Other")) {
//								otherFlagText.setVisibility(View.VISIBLE);
//								String otherText = flagFromDbList.get(j).getOtherFlagText();
//								otherFlagText.setText(otherText);
//							}
//							rowItems.get(i).setFlaggedDate(flagFromDbList.get(j).getFlaggedDate());
//							rowItems.get(i).setFlagId(flagFromDbList.get(j).getFlagId());
//							rowItems.get(i).setFlagLogId(flagFromDbList.get(j).getFlagLogId());
//							rowItems.get(i).setFlagUserId(flagFromDbList.get(j).getFlagUserId());
//							rowItems.get(i).setFlagUserName(flagFromDbList.get(j).getFlagUserName());
//							break;
//						}
//					}
//
//				}
//			}
//		}
//
//
//		this.setCancelable(true);
//
//		//this.setCanceledOnTouchOutside(true);
//
//		btn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				String otherReason = otherFlagText.getText().toString();
//				if(otherReason.trim() != "") {
//					rowItems.get(5).setOtherFlagText(otherReason);
//				} else {
//					rowItems.get(5).setChecked(false);
//					rowItems.get(5).setImageId(R.drawable.flag_grey);
//				}
//				Log.v(TAG, "" + rowItems.size());
//				mListener.OnCloseDialog();
//				dismiss();
//			}
//		});
//
//
//		adapter = new LogsAddFlagAdapter(ctx, R.layout.logs_details_addflag, rowItems);
//		dialogListView.setAdapter(adapter);
//
//		dialogListView.setOnItemClickListener(this);
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		ImageView image = (ImageView) view.findViewById(R.id.flag_image);
//		TextView txtView = (TextView) view.findViewById(R.id.flagname);
//		AddFlagItem flagItem = (AddFlagItem) parent.getAdapter().getItem(position);
//
//		boolean isCheck = flagItem.isChecked();
//		String text = flagItem.getFlagText();
//
//		isCheck = !isCheck;
//		//for loop
//		// get Flag which is checked alreay add it into the flags array
//
//
//		if(isCheck) {
//			image.setImageResource(iconsImage[position]);
//
//			txtView.setTextColor(Color.BLACK);
//
//			flags.add(flagItem);
//			//flagItemList.add(flagItem);
//			rowItems.get(position).setChecked(isCheck);
//			rowItems.get(position).setImageId(iconsImage[position]);
//			if(text.equals("Other")) {
//				otherFlagText.setVisibility(View.VISIBLE);
//				String otherReason = otherFlagText.getText().toString().trim();
//			}
//
//		} else {
//			image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.flag_grey));
//
//			txtView.setTextColor(Color.parseColor("#565555"));
//
//			flags.remove(flagItem);
//			//flagItemList.remove(flagItem);
//			rowItems.get(position).setChecked(isCheck);
//			rowItems.get(position).setImageId(R.drawable.flag_grey);
//			if(text.equals("Other")) {
//				otherFlagText.setVisibility(View.GONE);
//			}
//		}
//		flagItem.setChecked(isCheck);
//	}

}
