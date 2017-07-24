package ism.manager.dashboard;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ism.manager.ActivityStringInfo;
import ism.manager.R;
import ism.manager.Utility;
import ism.manager.baseclasses.AppBaseFragment;
import ism.manager.customview.ToastMessage;
import ism.manager.utils.MessageInfo;
import ism.manager.webservices.Synchronization;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends AppBaseFragment {


    WebView dashboardWebview;
    ProgressBar dashboardProgressBar;
    TextView dashboardProgressText;
    LinearLayout loadingLayout;
    // Variable Declaration
    public static final String INFO = "INFO";
    Context mContext;
    ActivityStringInfo stringInfo;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = DashboardFragment.this.getActivity();
        if (getArguments().containsKey(INFO)) {
            stringInfo = (ActivityStringInfo) getArguments().getSerializable(INFO);
        }
        if (stringInfo == null) {
            stringInfo = new ActivityStringInfo();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        try {
            loadingLayout = (LinearLayout) view.findViewById(R.id.loadingLayout);
            dashboardProgressText = (TextView) view.findViewById(R.id.dashboardProgressText);
            dashboardProgressBar = (ProgressBar) view.findViewById(R.id.progressBar_dashboard);
            dashboardWebview = (WebView) view.findViewById(R.id.webView_dashboard);
            dashboardWebview.getSettings().setLoadsImagesAutomatically(true);
            dashboardWebview.getSettings().setBuiltInZoomControls(true);
            dashboardWebview.getSettings().setUseWideViewPort(true);
            dashboardWebview.getSettings().setJavaScriptEnabled(true);
            dashboardWebview.getSettings().setLoadWithOverviewMode(true);
            dashboardWebview.setScrollbarFadingEnabled(false);
            dashboardWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            dashboardWebview.clearCache(true);
            dashboardWebview.setWebChromeClient(new MyBrowser());
            dashboardWebview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    loadingLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    loadingLayout.setVisibility(View.GONE);
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                }
            });
        } catch (Exception e) {
            Utility.saveExceptionDetails(DashboardFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return view;
    }

    private void loadDashBoard() {
        if (Utility.getMandatoryMessage(mContext) != null && ActivityStringInfo.strMandatoryMsgRight.toLowerCase().equals("y")) {
            Intent intent2 = Utility.getMandatoryMessage(mContext);
            intent2.putExtra(INFO, stringInfo);
            startActivity(intent2);
            DashboardFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            ActivityStringInfo.alertopen = true;
        } else {
            if (dashboardWebview != null) {
                if (ActivityStringInfo.bundleSI != null) {
                    dashboardWebview.restoreState(ActivityStringInfo.bundleSI);
                } else {
                    dashboardWebview.clearHistory();
                    dashboardWebview.loadUrl(ActivityStringInfo.dashboardUrl + ActivityStringInfo.strLogin);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityStringInfo.shouldRefreshed) {
            ActivityStringInfo.shouldRefreshed = false;
            refreshDashboard();
        } else {
            loadDashBoard();
        }
        if(ActivityStringInfo.isLoggingOut){
            showTransparentProgressDialog(MessageInfo.registrationProgress_txt);
        }
    }

    private class MyBrowser extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            // Activities and WebViews measure progress with different scales.
            // The progress meter will automatically disappear when we reach 100%
            System.out.println("Progress--->" + progress);
            dashboardProgressBar.setProgress(progress);
            dashboardProgressText.setText(progress + "/" + dashboardProgressBar.getMax());
//            DashboardFragment.this.getActivity().setProgress(progress * 100);
            if (progress == 100) {
                System.out.println("Loading completed...");
                loadingLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshDashboard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshDashboard() {
        new SelectDataTaskForRefresh().execute();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (dashboardWebview != null)
            dashboardWebview.stopLoading();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dashboardWebview != null)
            dashboardWebview.stopLoading();

        hideTransparentProgressDialog();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            // Restore the state of the WebView
            ActivityStringInfo.bundleSI = savedInstanceState;
//            dashboardWebview.restoreState(savedInstanceState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of the WebView
        dashboardWebview.saveState(outState);
    }

    private class SelectDataTaskForRefresh extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strRefresh);
            } catch (Exception e) {
                Utility.saveExceptionDetails(DashboardFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                Synchronization syc = new Synchronization(mContext);
                strMsg = syc.getInformation(DashboardFragment.this.getActivity());
            } catch (Exception e) {
                Utility.saveExceptionDetails(DashboardFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true")) {
                loadDashBoard();
            } else if (strMsg != "true") {
                if (Utility.Connectivity_Internet(mContext)) {
                    if (!strMsg.equals(""))
                        showToast(strMsg, Toast.LENGTH_SHORT);
                } else {
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            }
        }
    }


}
