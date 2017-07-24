
package ism.android.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ism.android.R;


public class AlertDialogManager {

    private Context context;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private String msg;

    public AlertDialogManager(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            this.alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
//        }else {
        this.alertDialogBuilder = new AlertDialog.Builder(context);
//        }
        this.alertDialog = this.alertDialogBuilder.create();
        this.context = context;
    }

    public void showAlertDialog(int titleResourceId, int messageResourceId, Boolean showIcon) {
        this.showAlertDialog(this.context.getResources().getString(titleResourceId), this.context.getResources().getString(messageResourceId), showIcon);
    }

    public void showAlertDialog(int titleResourceId, String message, Boolean showIcon) {
        this.showAlertDialog(this.context.getResources().getString(titleResourceId), message, showIcon);
    }

    public void showAlertDialog(String title, int messageResourceId, Boolean showIcon) {
        this.showAlertDialog(title, this.context.getResources().getString(messageResourceId), showIcon);
    }

    public void showAlertDialog(String message, String posButton, String negButton, final OnCustomDialogClicklistenr listner) {
        showAlertDialog(this.context, this.context.getResources().getString(R.string.app_name), message, posButton, negButton, true, listner);
    }

    public void showAlertDialog(String title, String message, String posButton, String negButton, final OnCustomDialogClicklistenr listner) {
        showAlertDialog(this.context, title, message, posButton, negButton, true, listner);
    }

    public void showAlertDialog(String title, String message, String posButton, String negButton, boolean cancelable, final OnCustomDialogClicklistenr listner) {
        showAlertDialog(this.context, title, message, posButton, negButton, cancelable, listner);
    }


    public void showAlertDialog(String title, String message, Boolean showIcon) {
        msg = message;
        if (title != null) {
            this.alertDialog.setTitle(title);
        }

        if (message != null) {
            this.alertDialog.setMessage(message);
        }

        if (showIcon) {
            this.alertDialog.setIcon(R.mipmap.main_icon);
        }

        this.alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        this.alertDialog.show();
    }

    public void showAlertDialog(Context mContext, String title, String message, String posButton, String negButton, boolean cancelable, final OnCustomDialogClicklistenr listner) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.mipmap.main_icon);
        builder.setCancelable(cancelable);

        builder.setPositiveButton(posButton, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listner != null) {
                    listner.onPositiveClick();
                }
            }
        });
        builder.setNegativeButton(negButton, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listner != null) {
                    listner.onNegativeClick();
                }
            }
        });

        builder.create().show();
    }

    public void showAlertDialogForSingleChoiceItems(Context mContext, String title, String[] choiceList, int checkedItem, String posButton, String negButton, final OnSingleChoiceItemCustomDialogClicklistenr listner) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setIcon(R.mipmap.main_icon);
        builder.setSingleChoiceItems(choiceList, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listner != null) {
                    listner.onSingleChoiceItemClick(which);
                }
            }
        });
        builder.setPositiveButton(posButton, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listner != null) {
                    listner.onPositiveClick();
                }
            }
        });
        builder.setNegativeButton(negButton, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listner != null) {
                    listner.onNegativeClick();
                }
            }
        });

        builder.create().show();
    }

    public interface OnCustomDialogClicklistenr {
        public abstract void onPositiveClick();

        public abstract void onNegativeClick();
    }

    public interface OnSingleChoiceItemCustomDialogClicklistenr {
        public abstract void onPositiveClick();

        public abstract void onNegativeClick();

        public abstract void onSingleChoiceItemClick(int which);

    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

}
