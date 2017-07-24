package ism.android.message;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.AlertDialogManager;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.utils.StaticVariables;
import ism.android.webservices.SyncServiceHelper;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends AppBaseFragment implements RecyclerView.OnItemTouchListener {

    private RecyclerView recyclerView;

    private ArrayList<HashMap<String, String>> fillMessageList;

    private ActivityStringInfo stringInfo;

    // Variable Declaration
    public static final String INFO = "INFO";

    private FragmentManager fragmentManager;

    private FloatingActionButton newMsg;

    public boolean shouldMsgListRefreshed;

    public boolean listItemSelected;

    TextView newMsgBtn;

    TextView newMMsgBtn;

    private Context mContext;

    private boolean isFirstSelected = true;

    private CoordinatorLayout coordinatorLayout;

    private MessageListItemRecyclerViewAdapter msgListItemRecyclerViewAdapter;

    private ActionMode actionMode;

    private GestureDetectorCompat gestureDetector;

    private ItemTouchHelper.Callback callback;
    private ItemTouchHelper helper;

    private HashSet<String> msgIdHashSet;

    private Activity mActivity;

    public static MessagesFragment getInstance() {
        return new MessagesFragment();
    }

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        msgIdHashSet = new HashSet<String>();
        mContext = MessagesFragment.this.getActivity();
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.messageItem_list);
        newMsg = (FloatingActionButton) view.findViewById(R.id.newMsg);
        assert recyclerView != null;

        if (view.findViewById(R.id.msg_item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            ActivityStringInfo.mTwoPane = true;
        } else {
            ActivityStringInfo.mTwoPane = false;
        }

//        if (!ActivityStringInfo.mTwoPane && ActivityStringInfo.itemSelected && ActivityStringInfo.itemId != null) {
//            String iId = ActivityStringInfo.itemId;
//            ActivityStringInfo.itemId = null;
//            ActivityStringInfo.itemSelected = false;
//            Intent intent = new Intent(MessagesFragment.this.getActivity(), ItemDetailActivity.class);
//            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, iId);
//            startActivity(intent);
//        } else if (ActivityStringInfo.mTwoPane && ActivityStringInfo.itemSelected && ActivityStringInfo.itemId != null) {
//            String iId = ActivityStringInfo.itemId;
//            ActivityStringInfo.itemId = null;
//            ActivityStringInfo.itemSelected = false;
//            Bundle arguments = new Bundle();
//            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, iId);
//            ItemDetailFragment fragment = new ItemDetailFragment();
//            fragment.setArguments(arguments);
//            fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.item_detail_container, fragment).commit();
//        }


        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityStringInfo.shouldRefreshed = false;
            shouldMsgListRefreshed = true;
            getRefresh();
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        try {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new IntentFilter("shouldrefresh"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ActivityStringInfo.shouldRefreshed) {
            ActivityStringInfo.shouldRefreshed = false;
            shouldMsgListRefreshed = true;
            getRefresh();
        } else {
            setAll();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideTransparentProgressDialog();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_messages_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            if (Utility.Connectivity_Internet(MessagesFragment.this.getActivity())) {
                getRefresh();
            }else {
                showToast("Either server is down or internet connection not found.", Toast.LENGTH_SHORT);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListeners() {
        this.newMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCompose();
            }
        });
    }


    public void setAll() {
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
        if (Utility.getMandatoryMessage(mContext) != null) {
            Intent intent2 = Utility.getMandatoryMessage(mContext);
            intent2.putExtra(INFO, stringInfo);
            startActivity(intent2);
            MessagesFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            ActivityStringInfo.alertopen = true;
        } else {
            loadMessageListData();
        }
//        loadMessageListData();
        setListeners();
    }

    private void loadMessageListData() {
        if (shouldMsgListRefreshed) {
            shouldMsgListRefreshed = false;
            if (fillMessageList == null) {
                ActivityStringInfo.hasMessageList = new HashMap<String, String>();
                fillMessageList = new ArrayList<HashMap<String, String>>();
            } else {
                fillMessageList.clear();
            }
            new SelectDataTaskForFillMessageList().execute();
        } else {
            if (fillMessageList == null) {
                ActivityStringInfo.hasMessageList = new HashMap<String, String>();
                fillMessageList = new ArrayList<HashMap<String, String>>();
                new SelectDataTaskForFillMessageList().execute();
            }
        }
    }

    public void getCompose() {
        try {
            ActivityStringInfo.strBody = "";
            ActivityStringInfo.strSubject = "";
            ActivityStringInfo.strCompositeUserId.clear();
            Intent intent1 = new Intent(MessagesFragment.this.getActivity(), ComposeActivity.class);
            intent1.putExtra(INFO, stringInfo);
            startActivity(intent1);
            MessagesFragment.this.getActivity().overridePendingTransition(R.anim.pump_top,
                    R.anim.disappear);
        } catch (Exception e) {
            Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private void showComposeMsgPopUp() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessagesFragment.this.getActivity(), R.style.TransparentProgressDialog);
            LayoutInflater inflater = MessagesFragment.this.getActivity().getLayoutInflater();
            final View myDialog = inflater.inflate(R.layout.compose_msg_option, null);
            newMsgBtn = (TextView) myDialog.findViewById(R.id.newGM);
            newMMsgBtn = (TextView) myDialog.findViewById(R.id.newMM);
            alertDialogBuilder.setView(myDialog);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(true);
            alertDialog.show();

            newMsgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    stringInfo.clear();
                    getCompose();
                }
            });

            // change to mandatory message
            newMMsgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    stringInfo.clear();
                    stringInfo.strCommentForWhich = "Mandatory";
                    getCompose();
                }
            });


        } catch (Exception e) {
            Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }


    private class SelectDataTaskForFillMessageList extends AsyncTask<String, Void, String> {

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            showTransparentProgressDialog(MessageInfo.loading);
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                try {
                    bindAdapter();
//                    parse();
                } catch (Exception e) {
                    Utility.saveExceptionDetails(getActivity(), e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            /** FILL MESSAGELIST **/
            if (fillMessageList.size() > 0) {
                setupRecyclerView(recyclerView, fillMessageList);
            } else {
                showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);

            }
        }
    }

    private void parse() {
        try {
            Date d = ActivityStringInfo.sdfDate.parse("6/21/2016 12:31:25 AM");
            Log.v("parse:", d.toString());


        } catch (Exception e) {
            Log.v("parse:", e.getMessage().toString());
        }

    }

    public void bindAdapter() {
        try {
            /** Get the message list **/
            int count = 0;
            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageList();
            SimpleDateFormat curDateformat = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat newDateformat = new SimpleDateFormat("MMM dd");

            System.out.println("c count: " + c.getCount());
            System.out.println("c String: " + c.toString());

            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getString(6).equals(StaticVariables.GIVE_TO_MESSAGE) || c.getString(6).equals(StaticVariables.GIVE_AWAY_MESSAGE) || c.getString(6).equals(StaticVariables.TRADE_REQUEST_MESSAGE) || c.getString(6).equals(StaticVariables.REQUEST_OFF_MESSAGE)) {
                        if (count == 0)
                            ActivityStringInfo.getMaxMsgId = c.getString(0) + "-" + c.getString(6);
                        ActivityStringInfo.getMinMsgId = c.getString(0) + "-" + c.getString(6);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(DatabaseConstant.key_MESSAGE_ID, c.getString(0));
                        map.put(DatabaseConstant.key_SUBJECT, c.getString(1));
                        map.put(DatabaseConstant.key_BODY, c.getString(2));
                        map.put(DatabaseConstant.key_FROM_USER_ID, c.getString(3));
                        map.put(DatabaseConstant.key_FROM_USER_NAME, c.getString(4));

                        // Removed due to date parse exception
//                        String msgDateTime = c.getString(5);
//                        String msgTime = msgDateTime.substring(msgDateTime.indexOf(" ") + 1);
//                        String msgDate = msgDateTime.substring(0, msgDateTime.indexOf(" "));
//
//                        Date objDate = curDateformat.parse(msgDate);
//                        String formatedDateString = newDateformat.format(objDate) + ", " + getFormattedTime(msgTime);

                        map.put(DatabaseConstant.key_MESSAGE_DATE, c.getString(5));
                        map.put(DatabaseConstant.key_TYPE, c.getString(6));
                        map.put(DatabaseConstant.key_SUB_TYPE, c.getString(7));
                        map.put(DatabaseConstant.key_PROCESS_ID, c.getString(8));
//                        map.put(DatabaseConstant.key_REPLY_USER_ID, c.getString(9));
//                        map.put(DatabaseConstant.key_REPLY_USER_NAME, c.getString(10));
                        ActivityStringInfo.hasMessageList.put("" + count, c.getString(0) + "-" + c.getString(6));
                        fillMessageList.add(map);
                        count++;

                        // Not in employee app code.
                    } else {
                        /** Start : Check for delete date **/
                        Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageToUserList(c.getString(0));
                        System.out.println("c1 count: " + c1.getCount());
                        System.out.println("c1 String: " + c1.toString());
                        int c1Count = c1.getCount();
                        if (c1Count > 0) {
                            while (c1.moveToNext()) {
                                if (c1.getString(3).equals("")) {
                                    if (count == 0)
                                        ActivityStringInfo.getMaxMsgId = c.getString(0) + "-" + c.getString(6);
                                    ActivityStringInfo.getMinMsgId = c.getString(0) + "-" + c.getString(6);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put(DatabaseConstant.key_MESSAGE_ID, c.getString(0));
                                    map.put(DatabaseConstant.key_SUBJECT, c.getString(1));
                                    map.put(DatabaseConstant.key_BODY, c.getString(2));
                                    map.put(DatabaseConstant.key_FROM_USER_ID, c.getString(3));
                                    map.put(DatabaseConstant.key_FROM_USER_NAME, c.getString(4));
                                    map.put(DatabaseConstant.key_MESSAGE_DATE, c.getString(5));
                                    map.put(DatabaseConstant.key_TYPE, c.getString(6));
                                    map.put(DatabaseConstant.key_SUB_TYPE, c.getString(7));
                                    map.put(DatabaseConstant.key_PROCESS_ID, c.getString(8));
                                    map.put(DatabaseConstant.key_REPLY_USER_ID, c.getString(9));
                                    map.put(DatabaseConstant.key_REPLY_USER_NAME, c.getString(10));
                                    ActivityStringInfo.hasMessageList.put("" + count, c.getString(0) + "-" + c.getString(6));
                                    fillMessageList.add(map);
                                    count++;
                                }
                            }
                        }
                        c1.close();
                    }
                    /** End **/
                }
            }
            c.close();
        } catch (Exception e) {
            Utility.saveExceptionDetails(getActivity(), e);
            e.printStackTrace();
        }
    }

    public String getFormattedTime(String shiftTime) {
        String time = "";
        try {
            Pattern p = Pattern.compile(":");
            String[] item = p.split(shiftTime);
            for (int i = 0; i < 2; i++) {
                time += item[i];
                if (i == 0)
                    time += ":";
            }
            time += shiftTime.substring(shiftTime.indexOf(" "));
        } catch (Exception e) {
            Utility.saveExceptionDetails(getActivity(), e);
            e.printStackTrace();
        }

        return time;
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<HashMap<String, String>> messageArrayList) {
        msgListItemRecyclerViewAdapter = new MessageListItemRecyclerViewAdapter(messageArrayList);
        recyclerView.setAdapter(msgListItemRecyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(this);
        gestureDetector = new GestureDetectorCompat(MessagesFragment.this.getActivity(), new RecyclerViewDemoOnGestureListener());
        if (ActivityStringInfo.selectedListItemIndex > 3)
            recyclerView.scrollToPosition(ActivityStringInfo.selectedListItemIndex);

        if (ActivityStringInfo.mTwoPane) {
            if (messageArrayList != null && messageArrayList.size() > 0)
                setDefaultListItemView(messageArrayList.get(ActivityStringInfo.selectedListItemIndex));
        }
    }

    public void setItemTouchHelper(@NonNull RecyclerView recyclerView, MessageListItemRecyclerViewAdapter adapter) {
        this.callback = new MessageListItemTouchHelper(adapter);
        this.helper = new ItemTouchHelper(callback);
        this.helper.attachToRecyclerView(recyclerView);
    }

    private void setDefaultListItemView(HashMap<String, String> messageArrListItem) {

        stringInfo.strMessageId = messageArrListItem.get(DatabaseConstant.key_MESSAGE_ID);
        stringInfo.strMeetingId = messageArrListItem.get(DatabaseConstant.key_PROCESS_ID);
        stringInfo.strMessageType = messageArrListItem.get(DatabaseConstant.key_TYPE);

        Fragment detailViewFragment = null;

        // available types in employee. S, GA, GT, T, MM else details
        // available types in manager B, M, I, GA, GT TR MM
        if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals(StaticVariables.BREAK_NOTIFICATION_MESSAGE)) {
            detailViewFragment = new MessageDetailsFragment();
        } else if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals(StaticVariables.MEETING_MESSAGE)) {
            detailViewFragment = new MeetingDetailsFragment();
        } else if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals(StaticVariables.IDEA_MESSAGE)) {
            detailViewFragment = new MessageDetailsFragment();
        } else if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals(StaticVariables.GIVE_AWAY_MESSAGE)) {
            detailViewFragment = new GiveAwayRequestFragment();
        } else if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals(StaticVariables.GIVE_TO_MESSAGE)) {
            detailViewFragment = new GiveToRequestFragment();
        } else if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals(StaticVariables.TRADE_REQUEST_MESSAGE)) {
            detailViewFragment = new TradeWithRequestFragment();
        }
//        else if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals(StaticVariables.REQUEST_OFF_MESSAGE)) {
//            detailViewFragment = new RequestOffFragment();
//        }
        else if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals(StaticVariables.MANDATORY_MESSAGE)) {
            detailViewFragment = new MandatoryMsgDetailFragment();
        } else if (messageArrListItem.get(DatabaseConstant.key_TYPE).equals("S") && (messageArrListItem.get(DatabaseConstant.key_SUB_TYPE) != null && messageArrListItem.get(DatabaseConstant.key_SUB_TYPE).toString().length() > 0)) {
            if (messageArrListItem.get(DatabaseConstant.key_SUB_TYPE).equals("GA")) {
                detailViewFragment = new GiveAwayResponseFragment();
            } else if (messageArrListItem.get(DatabaseConstant.key_SUB_TYPE).equals("GT") && !messageArrListItem.get(DatabaseConstant.key_PROCESS_ID).equals("")) {
                detailViewFragment = new GiveToResponseFragment();
            } else if (messageArrListItem.get(DatabaseConstant.key_SUB_TYPE).equals("T") && !messageArrListItem.get(DatabaseConstant.key_PROCESS_ID).equals("")) {
                detailViewFragment = new TradeShiftResponseFragment();
            }
        } else {
            detailViewFragment = new MessageDetailsFragment();
        }
        if (this.getActivity() != null && !getActivity().isFinishing()) {
            if (detailViewFragment != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(INFO, stringInfo);
                detailViewFragment.setArguments(bundle);
                fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager != null)
                    fragmentManager.beginTransaction().replace(R.id.msg_item_detail_container, detailViewFragment).commitAllowingStateLoss();
            } else {
                showToast("Not Available..", Toast.LENGTH_SHORT);
            }
        }
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

    public class MessageListItemRecyclerViewAdapter extends RecyclerView.Adapter<MessageListItemRecyclerViewAdapter.ViewHolder> {

        private final List<HashMap<String, String>> messageItems;
        private SparseBooleanArray selectedItems;
        private SparseBooleanArray selectedItemsforDelete;


        public MessageListItemRecyclerViewAdapter(List<HashMap<String, String>> items) {
            messageItems = items;
            selectedItems = new SparseBooleanArray();
            selectedItemsforDelete = new SparseBooleanArray();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_message_inbox, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final HashMap<String, String> hasValues = messageItems.get(position);

            if (actionMode != null) {
                holder.mView.setBackgroundResource(R.drawable.list_item_selector2);
                holder.mView.setSelected(selectedItemsforDelete.get(position));

                if (!selectedItemsforDelete.get(position) && selectedItems.get(position)) {
                    holder.mView.setBackgroundResource(R.drawable.list_item_selector);
                    holder.mView.setSelected(selectedItems.get(position));
                }

            } else {
                holder.mView.setBackgroundResource(R.drawable.list_item_selector);
                holder.mView.setSelected(selectedItems.get(position));
            }
            if (ActivityStringInfo.mTwoPane) {
                if (ActivityStringInfo.selectedListItemIndex == position) {
                    holder.mView.setSelected(true);
                    selectedItems.put(position, true);
                } else {
                    if (actionMode == null) {
                        holder.mView.setSelected(false);
                    }
                }
            } else {
                if (ActivityStringInfo.selectedListItemIndex == position && ActivityStringInfo.isListItemSelected) {
                    holder.mView.setSelected(true);
                    selectedItems.put(position, true);
                } else {
                    if (actionMode == null) {
                        holder.mView.setSelected(false);
                    }
                }
            }

            holder.txtMessageOwnerName.setTextColor(Color.parseColor("#000000"));
            holder.txtMessageOwnerName.setTypeface(null, Typeface.BOLD);
            holder.txtMessageDate.setTypeface(null, Typeface.BOLD);
            holder.txtSubjectName.setTextColor(Color.parseColor("#000000"));
            holder.txtSubjectName.setTypeface(null, Typeface.BOLD);

            if (hasValues != null) {
                if (hasValues.get(DatabaseConstant.key_TYPE).equals(
                        StaticVariables.IDEA_MESSAGE))
                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_bulb);
                else if (hasValues.get(DatabaseConstant.key_TYPE).equals(
                        StaticVariables.MANDATORY_MESSAGE))
                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_pin);
                else if (hasValues.get(DatabaseConstant.key_TYPE).equals(
                        StaticVariables.MEETING_MESSAGE))
                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_meeting);
                else if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.GIVE_AWAY_MESSAGE) || hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.GIVE_TO_MESSAGE) || hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.TRADE_REQUEST_MESSAGE))
                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_schedules);
                else if (hasValues.get(DatabaseConstant.key_TYPE).equals(
                        StaticVariables.REQUEST_OFF_MESSAGE))
                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_schedules);
                else if (hasValues.get(DatabaseConstant.key_TYPE).equals(
                        StaticVariables.BREAK_NOTIFICATION_MESSAGE))
                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_tea_break);
                else if (hasValues.get(DatabaseConstant.key_TYPE).equals(
                        StaticVariables.SHIFT_MESSAGE))
                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_exchange);
                else
                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_email);


                Cursor c = null;
                try {
                    /** Get the Read mail **/
                    c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageToUserList(hasValues.get(DatabaseConstant.key_MESSAGE_ID));
                    if (c.getCount() > 0) {
                        while (c.moveToNext()) {
                            if (!c.getString(2).equals("")) {
//                                holder.mView.setBackgroundColor(Color
//                                        .parseColor("#cccccc"));
                                holder.txtMessageOwnerName.setTextColor(Color.parseColor("#545A5D"));
                                holder.txtMessageOwnerName.setTypeface(null, Typeface.NORMAL);
                                holder.txtMessageDate.setTypeface(null, Typeface.NORMAL);
                                holder.txtSubjectName.setTextColor(Color.parseColor("#545A5D"));
                                holder.txtSubjectName.setTypeface(null, Typeface.NORMAL);
                                if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.GENERAL_MESSAGE))
                                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_drafts);
                            }
                        }
                    }
                    c.close();
                } catch (Exception e) {
                    Utility.saveExceptionDetails(getActivity(), e);
                    e.printStackTrace();
                } finally {
                    c.close();
                }

                Cursor c1 = null;
                try {
                    /** Get Attachment Link **/
                    c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageDetailFileAttachmentList(hasValues.get(DatabaseConstant.key_MESSAGE_ID));
                    if (c1.getCount() > 0) {
                        while (c1.moveToNext()) {
                            if (!c1.getString(3).equals("")) {
                                holder.imgPaperClip.setVisibility(View.VISIBLE);
                            } else {
                                holder.imgPaperClip.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        holder.imgPaperClip.setVisibility(View.GONE);
                    }
                    c1.close();
                } catch (Exception e) {
                    Utility.saveExceptionDetails(getActivity(), e);
                    e.printStackTrace();
                } finally {
                    c1.close();
                }

                // Log.d("========MESSAGE DETAILS========",hasValues.get(DatabaseConstant.key_SUBJECT));

                holder.txtMessageOwnerName.setText(hasValues.get(DatabaseConstant.key_FROM_USER_NAME));
                holder.txtSubjectName.setText(hasValues.get(DatabaseConstant.key_SUBJECT));
                holder.txtMessageDate.setText(hasValues.get(DatabaseConstant.key_MESSAGE_DATE));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (actionMode != null) {
                            myToggleSelection(position, v);
                        } else {
                            ActivityStringInfo.isListItemSelected = true;
                            ActivityStringInfo.selectedListItemIndex = position;
                            toggleSelection(position);

                            stringInfo.strMessageId = hasValues.get(DatabaseConstant.key_MESSAGE_ID);
                            stringInfo.strMeetingId = hasValues.get(DatabaseConstant.key_PROCESS_ID);
                            stringInfo.strMessageType = hasValues.get(DatabaseConstant.key_TYPE);
                            stringInfo.strMessageSubType = hasValues.get(DatabaseConstant.key_SUB_TYPE);
                            // Get a set of the entries
                            Set set = ActivityStringInfo.hasMessageList.entrySet();
                            // Get an iterator
                            Iterator i = set.iterator();
                            // Display elements
                            while (i.hasNext()) {
                                Map.Entry me = (Map.Entry) i.next();
                                if (me.getValue().equals(hasValues.get(DatabaseConstant.key_MESSAGE_ID) + "-" + hasValues.get(DatabaseConstant.key_TYPE))) {
                                    ActivityStringInfo.strCount = Integer.valueOf("" + me.getKey());
                                }
                            }

                            Cursor c = MyDatabaseInstanceHolder.getDatabaseHelper().getMessageToUserList(stringInfo.strMessageId);
                            long sucss = 0;
                            if (c.getCount() > 0) {
                                while (c.moveToNext()) {
                                    if (c.getString(2).equals("")) {
                                        sucss = MyDatabaseInstanceHolder.getDatabaseHelper().updateMessageToUserRecords(stringInfo.strMessageId);
                                    }
                                }
                            }
                            c.close();

                            if (ActivityStringInfo.mTwoPane) {
                                //set text color as read msg
                                holder.txtMessageOwnerName.setTextColor(Color.parseColor("#545A5D"));
                                holder.txtMessageOwnerName.setTypeface(null, Typeface.NORMAL);
                                holder.txtMessageDate.setTypeface(null, Typeface.NORMAL);
                                holder.txtSubjectName.setTextColor(Color.parseColor("#545A5D"));
                                holder.txtSubjectName.setTypeface(null, Typeface.NORMAL);
                                if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.GENERAL_MESSAGE))
                                    holder.imgMessageImage.setBackgroundResource(R.drawable.ic_action_drafts);

                                Fragment detailViewFragment = null;
                                if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.BREAK_NOTIFICATION_MESSAGE)) {
                                    detailViewFragment = new MessageDetailsFragment();
                                } else if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.MEETING_MESSAGE)) {
                                    detailViewFragment = new MeetingDetailsFragment();
                                } else if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.IDEA_MESSAGE)) {
                                    detailViewFragment = new MessageDetailsFragment();
                                } else if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.GIVE_AWAY_MESSAGE)) {
                                    detailViewFragment = new GiveAwayRequestFragment();
                                } else if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.GIVE_TO_MESSAGE)) {
                                    detailViewFragment = new GiveToRequestFragment();
                                } else if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.TRADE_REQUEST_MESSAGE)) {
                                    detailViewFragment = new TradeWithRequestFragment();
                                }
//                                else if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.REQUEST_OFF_MESSAGE)) {
//                                    detailViewFragment = new RequestOffFragment();
//                                }
                                else if (hasValues.get(DatabaseConstant.key_TYPE).equals(StaticVariables.MANDATORY_MESSAGE)) {
                                    detailViewFragment = new MandatoryMsgDetailFragment();
                                } else if (hasValues.get(DatabaseConstant.key_TYPE).equals("S") && (hasValues.get(DatabaseConstant.key_SUB_TYPE) != null && hasValues.get(DatabaseConstant.key_SUB_TYPE).toString().length() > 0)) {
                                    if (hasValues.get(DatabaseConstant.key_SUB_TYPE).equals("GA")) {
                                        detailViewFragment = new GiveAwayResponseFragment();
                                    } else if (hasValues.get(DatabaseConstant.key_SUB_TYPE).equals("GT") && !hasValues.get(DatabaseConstant.key_PROCESS_ID).equals("")) {
                                        detailViewFragment = new GiveToResponseFragment();
                                    } else if (hasValues.get(DatabaseConstant.key_SUB_TYPE).equals("T") && !hasValues.get(DatabaseConstant.key_PROCESS_ID).equals("")) {
                                        detailViewFragment = new TradeShiftResponseFragment();
                                    }
                                } else {
                                    detailViewFragment = new MessageDetailsFragment();
                                }
                                if (detailViewFragment != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(INFO, stringInfo);
                                    detailViewFragment.setArguments(bundle);
                                    fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.msg_item_detail_container, detailViewFragment).commit();
                                } else {
                                    showToast("Not Available..", Toast.LENGTH_SHORT);
                                }
                            } else {
                                shouldMsgListRefreshed = true;
                                Context context = v.getContext();
                                Intent messageDetails = new Intent(context, MessageDetailsActivity.class);
                                messageDetails.putExtra(INFO, stringInfo);
                                context.startActivity(messageDetails);
                            }
                        }
                    }
                });

            }
        }

        public void toggleSelection(int pos) {
            isFirstSelected = false;
            selectedItems.clear();
            selectedItems.put(pos, true);
            notifyDataSetChanged();
        }

        public void toggleSelection(int pos, View view) {
//            view.setBackgroundResource(R.drawable.list_item_selector2);
            String processId = "anyType{}";
            if (!messageItems.get(pos).get(DatabaseConstant.key_PROCESS_ID).equals(null) ||
                    !messageItems.get(pos).get(DatabaseConstant.key_PROCESS_ID).equals("")) {
                processId = messageItems.get(pos).get(DatabaseConstant.key_PROCESS_ID);
            }

            String msgId = messageItems.get(pos).get(DatabaseConstant.key_MESSAGE_ID) + "-"
                    + messageItems.get(pos).get(DatabaseConstant.key_TYPE)
                    + "$"
                    + processId;

            if (selectedItemsforDelete.get(pos, false)) {
                msgIdHashSet.remove(msgId);
                selectedItemsforDelete.delete(pos);
                view.setSelected(false);
            } else {
                msgIdHashSet.add(msgId);
                selectedItemsforDelete.put(pos, true);
//                view.setSelected(true);
            }
            notifyItemChanged(pos);
        }

        public void clearSelections() {
            selectedItemsforDelete.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {
            return selectedItemsforDelete.size();
        }

        public List<Integer> getSelectedItems() {
            List<Integer> items = new ArrayList<Integer>(selectedItemsforDelete.size());
            for (int i = 0; i < selectedItemsforDelete.size(); i++) {
                items.add(selectedItemsforDelete.keyAt(i));
            }
            return items;
        }

        public void remove(int position) {
            messageItems.remove(position);
            notifyItemRemoved(position);
        }

        public void swap(int firstPosition, int secondPosition) {
            Collections.swap(messageItems, firstPosition, secondPosition);
            notifyItemMoved(firstPosition, secondPosition);
        }

        public List<HashMap<String, String>> getMessageItems() {
            return messageItems;
        }

        @Override
        public int getItemCount() {
            return messageItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView txtMessageOwnerName;
            public final TextView txtMessageDate;
            public final TextView txtSubjectName;
            public final ImageView imgMessageImage;
            public final ImageView imgPaperClip;
            public HashMap<String, String> hasValues;

            public ViewHolder(View view) {
                super(view);
                view.setClickable(true);
                mView = view;
                txtMessageOwnerName = (TextView) view.findViewById(R.id.txtMessageOwnerName);
                txtMessageDate = (TextView) view.findViewById(R.id.txtMessageDate);
                txtSubjectName = (TextView) view.findViewById(R.id.txtSubjectName);
                imgMessageImage = (ImageView) view.findViewById(R.id.imgMessageImage);
                imgPaperClip = (ImageView) view.findViewById(R.id.imgPaperClip);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + hasValues.toString() + "'";
            }
        }
    }

    public class MessageListItemTouchHelper extends ItemTouchHelper.SimpleCallback {

        private MessageListItemRecyclerViewAdapter messageListItemRecyclerViewAdapter;

        public MessageListItemTouchHelper(MessageListItemRecyclerViewAdapter mListItemRecyclerViewAdapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.messageListItemRecyclerViewAdapter = mListItemRecyclerViewAdapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            messageListItemRecyclerViewAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return super.isItemViewSwipeEnabled();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            if (actionMode != null) {
                actionMode.finish();
            }
            final HashSet<String> msgIdHashSet = new HashSet<String>();
            String processId = "anyType{}";
            if (!messageListItemRecyclerViewAdapter.getMessageItems().get(viewHolder.getAdapterPosition()).get(DatabaseConstant.key_PROCESS_ID).equals(null) ||
                    !messageListItemRecyclerViewAdapter.getMessageItems().get(viewHolder.getAdapterPosition()).get(DatabaseConstant.key_PROCESS_ID).equals("")) {
                processId = messageListItemRecyclerViewAdapter.getMessageItems().get(viewHolder.getAdapterPosition()).get(DatabaseConstant.key_PROCESS_ID);
            }

            final String msgId = messageListItemRecyclerViewAdapter.getMessageItems().get(viewHolder.getAdapterPosition()).get(DatabaseConstant.key_MESSAGE_ID) + "-"
                    + messageListItemRecyclerViewAdapter.getMessageItems().get(viewHolder.getAdapterPosition()).get(DatabaseConstant.key_TYPE)
                    + "$"
                    + processId;
            msgIdHashSet.add(msgId);

            getAlertDialogManager().showAlertDialog(getResources().getString(R.string.deletion_alert), getResources().getString(R.string.delete_message), getResources().getString(R.string.yes), getResources().getString(R.string.no), new AlertDialogManager.OnCustomDialogClicklistenr() {
                @Override
                public void onPositiveClick() {
                    deleteMessages(msgIdHashSet);
                    messageListItemRecyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                }

                @Override
                public void onNegativeClick() {
                    msgIdHashSet.clear();
                    messageListItemRecyclerViewAdapter.notifyItemChanged(viewHolder.getAdapterPosition());

                }
            });

        }
    }

    public void getRefresh() {
        new SelectDataTaskForRefresh().execute();
    }

    private class SelectDataTaskForRefresh extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
//                MessagesFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
//                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    MessagesFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                } else {
//                    MessagesFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                }
                showTransparentProgressDialog(MessageInfo.strRefresh);
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
                e.printStackTrace();
            }

        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
//                MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRelatedRecords();  not in employee app code.
                Synchronization syc = new Synchronization(MessagesFragment.this.getActivity());
                return syc.getInformation(MessagesFragment.this.getActivity());
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            try {
                hideTransparentProgressDialog();

//                if (MessagesFragment.this.getActivity() != null) {
//                    MessagesFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//                }

                if (result.equals("true")) {
                    //&& ActivityStringInfo.strMandatoryMessageId.size() == 0) {
                    stringInfo.clear();
                    shouldMsgListRefreshed = true;
                    isFirstSelected = true;
                    ActivityStringInfo.selectedListItemIndex = 0;
                    ActivityStringInfo.isListItemSelected = false;
                    setAll();
                } else if (result != "true") {
                    Log.v("TAG",result);
//                    if (Utility.Connectivity_Internet(MessagesFragment.this.getActivity())) {
//                    if (!result.equals(""))
//                        showToast(strMsg, Toast.LENGTH_SHORT);
//                    }
//                    else
//                        showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private class RecyclerViewDemoOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
//            onClick(view);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (actionMode != null) {
                return;
            }
            // Start the CAB using the ActionMode.Callback defined above
            actionMode = getActivity().startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    MenuInflater inflater = actionMode.getMenuInflater();
                    inflater.inflate(R.menu.menu_cab_recyclerview, menu);
                    newMsg.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_delete:
                            getAlertDialogManager().showAlertDialog(getResources().getString(R.string.deletion_alert), getResources().getString(R.string.delete_messages), getResources().getString(R.string.yes), getResources().getString(R.string.no), new AlertDialogManager.OnCustomDialogClicklistenr() {
                                @Override
                                public void onPositiveClick() {
                                    deleteMessages(msgIdHashSet);
                                    if (msgListItemRecyclerViewAdapter != null && msgListItemRecyclerViewAdapter.getItemCount() > 0) {
                                        List<Integer> selectedItemPositions = msgListItemRecyclerViewAdapter.getSelectedItems();
                                        int currPos;
                                        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                                            currPos = selectedItemPositions.get(i);
                                            msgListItemRecyclerViewAdapter.remove(currPos);
                                        }
                                        if (ActivityStringInfo.mTwoPane) {
                                            if (msgListItemRecyclerViewAdapter.getMessageItems() != null && msgListItemRecyclerViewAdapter.getMessageItems().size() > 0) {
                                                if (ActivityStringInfo.selectedListItemIndex >= msgListItemRecyclerViewAdapter.getMessageItems().size()) {
                                                    ActivityStringInfo.selectedListItemIndex = msgListItemRecyclerViewAdapter.getMessageItems().size() - 1;
                                                }
                                                recyclerView.scrollToPosition(ActivityStringInfo.selectedListItemIndex);
                                                setDefaultListItemView(msgListItemRecyclerViewAdapter.getMessageItems().get(ActivityStringInfo.selectedListItemIndex));
                                            }
                                        }
                                        actionMode.finish();
                                        getRefresh();
                                    }
                                }

                                @Override
                                public void onNegativeClick() {
                                    actionMode.finish();
                                }
                            });
                            return true;
                        case R.id.action_cancel:
                            actionMode.finish();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    actionMode = null;
                    msgIdHashSet.clear();
                    if (msgListItemRecyclerViewAdapter != null && msgListItemRecyclerViewAdapter.getItemCount() > 0) {
                        msgListItemRecyclerViewAdapter.clearSelections();
                    }
                    newMsg.setVisibility(View.VISIBLE);
                }
            });
//            int idx = recyclerView.getChildAdapterPosition(view);
//            myToggleSelection(idx);
            super.onLongPress(e);
        }
    }

    private void myToggleSelection(int idx, View view) {
        msgListItemRecyclerViewAdapter.toggleSelection(idx, view);
        String title = getString(R.string.selected_count, msgListItemRecyclerViewAdapter.getSelectedItemCount());
        actionMode.setTitle(title);
    }

    public void deleteMessages(HashSet<String> storeCheckId) {
        if (storeCheckId != null && storeCheckId.size() > 0) {
            Iterator<String> iterator = storeCheckId.iterator();
            while (iterator.hasNext()) {

                String mixedMsgId = iterator.next();
                String actualMsgId = mixedMsgId.substring(0, mixedMsgId.indexOf("-"));
                String msgType = mixedMsgId.substring(mixedMsgId.indexOf("-") + 1, mixedMsgId.indexOf("$"));
                String processId = mixedMsgId.substring(mixedMsgId.indexOf("$") + 1);

                SyncServiceHelper syncHelper = new SyncServiceHelper();
                if (msgType.equals(StaticVariables.GIVE_AWAY_MESSAGE)) {
                    try {
                        String strMsg = syncHelper.denyGAApproval(MessagesFragment.this.getActivity(), actualMsgId, "false", MessageInfo.strDirectlyDeleted);
                        if (strMsg.toLowerCase().equals("true")) {
//                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftOfferingRecord(actualMsgId);
//                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRequestRecord(actualMsgId);
                        }
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                } else if (msgType.equals(StaticVariables.GIVE_TO_MESSAGE)) {
                    try {
                        String strMsg = syncHelper.denyGAApproval(MessagesFragment.this.getActivity(), actualMsgId, "true", MessageInfo.strDirectlyDeleted);
                        if (strMsg.toLowerCase().equals("true")) {
//                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftOfferingRecord(actualMsgId);
//                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteShiftRequestRecord(actualMsgId);
                        }
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                } else if (msgType.equals(StaticVariables.TRADE_REQUEST_MESSAGE)) {
                    try {
                        String strMsg = syncHelper.denyTradeShiftApproval(MessagesFragment.this.getActivity(), actualMsgId, MessageInfo.strDirectlyDeleted);
                        if (strMsg.toLowerCase().equals("true")) {
//                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteTradeShiftRecord(actualMsgId);
                        }
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                } else if (msgType
                        .equals(StaticVariables.REQUEST_OFF_MESSAGE)) {
                    try {
                        String strMsg = syncHelper.denyRequestOffApproval(MessagesFragment.this.getActivity(), actualMsgId, MessageInfo.strDirectlyDeleted);
                        if (strMsg.toLowerCase().equals("true")) {
//                            MyDatabaseInstanceHolder.getDatabaseHelper().deleteRequestOffRecord(actualMsgId);
                        }
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                } else if (msgType.equals(StaticVariables.MEETING_MESSAGE)) {
                    String response = "";
                    Cursor c1 = null;
                    try {
                        if (!processId.equals("")) {
                            c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getMeetingDetail(processId);
                            if (c1.getCount() > 0) {
                                response = syncHelper.sendReplyMeetingDetail(MessagesFragment.this.getActivity(), processId, MessageInfo.strDirectlyDeleted);
                                if (response.equals("true")) {
                                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteMeetingRecord(processId);
                                }
                            }
                            c1.close();
                        }
                        MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, actualMsgId);
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(MessagesFragment.this.getActivity(), e);
                        e.printStackTrace();
                    } finally {
                        c1.close();
                    }
                } else {
                    MyDatabaseInstanceHolder.getDatabaseHelper().deleteMessageRecords(null, actualMsgId);
                }

            }
        }

    }

}

