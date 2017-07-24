package ism.manager.baseclasses;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import ism.manager.Utility;
import ism.manager.customview.ToastMessage;
import ism.manager.customview.TransparentProgressDialog;
import ism.manager.utils.AlertDialogManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppBaseFragment extends Fragment {
    private ToastMessage toastMessage;
    private TransparentProgressDialog transparentProgressDialog;
    private int activeBusyTasks = 0;
    private AlertDialogManager alertDialogManager;
    private Activity mActivity;


    public AppBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastMessage = new ToastMessage(mActivity);
        alertDialogManager = new AlertDialogManager(mActivity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            mActivity = getActivity();
        }
    }


    protected void showToast(String msg, int timeDuration) {
        toastMessage.showToastMsg(msg, timeDuration);
    }

//    protected TransparentProgressDialog showGetTransparentProgressDialog(String msg) {
//        this.activeBusyTasks++;
//        if (this.transparentProgressDialog == null) {
//            return transparentProgressDialog = TransparentProgressDialog.show(getActivity(), msg);
//        } else {
//            return null;
//        }
//    }

    protected void showTransparentProgressDialog(String msg) {
        try {
            this.activeBusyTasks++;
            if (this.transparentProgressDialog == null) {
                if(mActivity!=null) {
                    transparentProgressDialog = new TransparentProgressDialog(mActivity);
                    transparentProgressDialog.setMessage(msg);
                    transparentProgressDialog.show();
                }
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
        }catch (Exception e){
            Utility.saveExceptionDetails(getContext(),e);
            e.printStackTrace();
        }
    }

    protected AlertDialogManager getAlertDialogManager() {
        return this.alertDialogManager;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
