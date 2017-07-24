package ism.manager.log;

import ism.manager.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowLogFlagAdapter extends ArrayAdapter<AddFlagItem>{

	Context context;
	String TAG = "LogsAddFlagAdapter";

	public ShowLogFlagAdapter(Context context, int resourceId, List<AddFlagItem> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	private class ViewHolder {
		ImageView imageView;
		TextView textView;
		boolean flag;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		AddFlagItem rowItem = getItem(position);
		Log.v("Show Flag Adapter ", " " + rowItem);

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if(convertView == null) {

			convertView = mInflater.inflate(R.layout.listitem_logs_flag, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.flag_image);
			holder.textView = (TextView) convertView.findViewById(R.id.flagname);
			if(rowItem.isChecked())
				holder.textView.setTextColor(Color.BLACK);   // The selected flags description should show black color
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(rowItem.getFlagText().trim().equals("Other")) {
			holder.textView.setText(rowItem.getOtherFlagText());
		} else {
			holder.textView.setText(rowItem.getFlagText());
		}


		if(!rowItem.isChecked())
		{
			holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.flag_grey_new));
		}
		else
		{
			holder.imageView.setImageResource(rowItem.getImageId());

		}


		return convertView;
	}
}
