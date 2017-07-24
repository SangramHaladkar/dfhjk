package ism.android.baseclasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ism.android.R;
import ism.android.Utility;
import ism.android.customview.ToastMessage;
import ism.android.customview.TransparentProgressDialog;
import ism.android.utils.AlertDialogManager;


public class AppBaseActivity extends AppCompatActivity {

    private ToastMessage toastMessage;
    private TransparentProgressDialog transparentProgressDialog;
    private int activeBusyTasks = 0;
    private AlertDialogManager alertDialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastMessage = new ToastMessage(this);
        alertDialogManager = new AlertDialogManager(this);
    }

    protected void showToast(String msg, int timeDuration) {
        toastMessage.showToastMsg(msg, timeDuration);
    }

    protected void showTransparentProgressDialog(String msg) {
        this.activeBusyTasks++;
        try {
            if (this.transparentProgressDialog == null) {
                transparentProgressDialog = new TransparentProgressDialog(AppBaseActivity.this);
                transparentProgressDialog.setMessage(msg);
                transparentProgressDialog.show();
            } else {
                if (transparentProgressDialog.isShowing()) {
                    transparentProgressDialog.dismiss();
                }
                transparentProgressDialog.setMessage(msg);
                transparentProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void hideTransparentProgressDialog() {
        try {
            this.activeBusyTasks--;
            if (this.activeBusyTasks < 0) {
                this.activeBusyTasks = 0;
            }

            if (this.activeBusyTasks == 0) {
                if (this.transparentProgressDialog != null) {
                    this.transparentProgressDialog.dismiss();
                    this.transparentProgressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected AlertDialogManager getAlertDialogManager() {
        return this.alertDialogManager;
    }

    protected BroadcastReceiver pushNotificationMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            try {
                ringtone.play();//play tone
            } catch (Exception e) {
                Utility.saveExceptionDetails(context, e);
                e.printStackTrace();
            }
            if (getAlertDialogManager().getAlertDialog().isShowing()) {
                getAlertDialogManager().getAlertDialog().dismiss();
            }
            String intentMsg = intent.getStringExtra("msg");
            getAlertDialogManager().showAlertDialog(getResources().getString(R.string.app_name), intentMsg, "OK", null, false, new AlertDialogManager.OnCustomDialogClicklistenr() {
                @Override
                public void onPositiveClick() {
                    ringtone.stop();
                }

                @Override
                public void onNegativeClick() {

                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.transparentProgressDialog != null && this.transparentProgressDialog.isShowing()) {
            this.transparentProgressDialog.dismiss();
            this.transparentProgressDialog = null;
        }
    }
}
