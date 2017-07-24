package ism.android.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ism.android.R;


/**
 * Created by Raj on 22/04/15.
 */
public class TransparentProgressDialog extends Dialog {

    TextView message;

    public TransparentProgressDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transperent_progressloader, null);
        message = (TextView) view.findViewById(R.id.progressDialogText);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        setContentView(view);
    }

    public void setMessage(CharSequence msg) {
        if (message != null)
            message.setText(msg);
    }
}
