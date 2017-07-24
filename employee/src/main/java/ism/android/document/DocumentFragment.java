package ism.android.document;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ism.android.ActivityStringInfo;
import ism.android.MyDatabaseInstanceHolder;
import ism.android.R;
import ism.android.SplashActivity;
import ism.android.Utility;
import ism.android.baseclasses.AppBaseFragment;
import ism.android.utils.DatabaseConstant;
import ism.android.utils.MessageInfo;
import ism.android.webservices.Synchronization;


/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentFragment extends AppBaseFragment {

    private FloatingActionButton upDirectoryBtn;

    private RecyclerView docListRecyclerView;

    private RecyclerView docListItemRecyclerView;

    public boolean shouldDocListRefreshed;

    private Spinner spinParentDirectory;

    private TextView txtLocation, txtDocs;

    private LinearLayout locationLayout, dynamicLayout;

    private LinearLayout.LayoutParams dlprams;

    // Variable Declaration

    private HashMap<String, String> dirWithID, idWithDir;

    private List<HashMap<String, String>> fillDocMap = new ArrayList<HashMap<String, String>>();

    private List<HashMap<String, String>> fillDocItemMap = new ArrayList<HashMap<String, String>>();

    private List<CharSequence> directories;

    private String strFileName;

    private String strFileLink;

    private String currentId = "";

    private ActivityStringInfo stringInfo;

    public static final String INFO = "INFO";

    private Context dContext;

    private Cursor cursorParents;

    private boolean isFirstSelected = true;

    private HorizontalScrollView pathScrollView;


    private HashMap<String, String> mapAll = new HashMap<String, String>();

    public DocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dContext = DocumentFragment.this.getActivity();
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
        View view = inflater.inflate(R.layout.fragment_document, container, false);
        try {
            this.initView(view);
            this.setListeners();
            if (view.findViewById(R.id.doc_item_detail_container) != null) {
                ActivityStringInfo.mTwoPane = true;
            } else {
                ActivityStringInfo.mTwoPane = false;
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return view;
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
            getDocListRefreshed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (ActivityStringInfo.shouldRefreshed) {
            ActivityStringInfo.shouldRefreshed = false;
            shouldDocListRefreshed = true;
            getDocListRefreshed();
        } else {
            setDocumentsAll();
        }
    }

    private void initView(View v) {
        if (v.findViewById(R.id.spinParentDirectory) != null) {
            this.spinParentDirectory = (Spinner) v.findViewById(R.id.spinParentDirectory);
        }
        if (v.findViewById(R.id.txtLocation) != null) {
            this.txtLocation = (TextView) v.findViewById(R.id.txtLocation);
        }
        if (v.findViewById(R.id.txtDocs) != null) {
            this.txtDocs = (TextView) v.findViewById(R.id.txtDocs);
            this.txtDocs.setTextColor(Color.parseColor("#005595"));
            SpannableString content = new SpannableString("Docs");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            this.txtDocs.setText(content);
        }
        if (v.findViewById(R.id.locationLayout) != null) {
            this.locationLayout = (LinearLayout) v.findViewById(R.id.locationLayout);
        }

        if (v.findViewById(R.id.dynamicLayout) != null) {
            this.dynamicLayout = (LinearLayout) v.findViewById(R.id.dynamicLayout);
            this.dlprams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        this.upDirectoryBtn = (FloatingActionButton) v.findViewById(R.id.upBtn);
        this.docListRecyclerView = (RecyclerView) v.findViewById(R.id.documentItem_list);
        this.pathScrollView =(HorizontalScrollView)v.findViewById(R.id.pathScrollView);

        if (v.findViewById(R.id.documentItemDetails_list) != null) {
            this.docListItemRecyclerView = (RecyclerView) v.findViewById(R.id.documentItemDetails_list);
        }

        assert docListRecyclerView != null;

    }

    private void setListeners() {

        if (this.txtDocs != null) {
            this.txtDocs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (spinParentDirectory != null)
                        spinParentDirectory.setVisibility(View.GONE);

                    if (upDirectoryBtn != null)
                        upDirectoryBtn.setVisibility(View.GONE);

                    locationLayout.setVisibility(View.GONE);
                    ActivityStringInfo.locationId.clear();
                    isFirstSelected = true;
                    bindAdapter("0");
                }
            });
        }

        if (this.upDirectoryBtn != null) {
            this.upDirectoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = null;
                    try {
                        if (ActivityStringInfo.locationId.size() > 0) {
                            if ((ActivityStringInfo.locationId.size() - 1) != 0) {
                                ActivityStringInfo.locationId.remove(ActivityStringInfo.locationId.size() - 1);
                                id = ActivityStringInfo.locationId.get(ActivityStringInfo.locationId.size() - 1);
                            } else {
                                id = "0";
                                ActivityStringInfo.locationId.clear();
                                ActivityStringInfo.lastSelectedParentId = "0";
                                fillDocSpinner();
                            }
                            bindAdapter("" + id);
                        }
                    } catch (Exception e) {
                        Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setDocumentsAll() {
        if (ActivityStringInfo.strDocRights.equals("") || ActivityStringInfo.strLogRights.equals("") || ActivityStringInfo.strUser_id.equals("")) {
            new SplashActivity().setValue();
        }
        if (Utility.getMandatoryMessage(dContext) != null) {
            Intent intent2 = Utility.getMandatoryMessage(dContext);
            intent2.putExtra(INFO, stringInfo);
            startActivity(intent2);
            DocumentFragment.this.getActivity().overridePendingTransition(R.anim.pump_top, R.anim.disappear);
            ActivityStringInfo.alertopen = true;
        } else {
            fillDocSpinner();
        }
    }

    /**
     * METHOD TO FILL THE PARENT DIRECTORIES INTO SPINNER
     **/
    public void fillDocSpinner() {
        try {
            Cursor cursorAll = null;
            cursorAll = MyDatabaseInstanceHolder.getDatabaseHelper().getAllDocument();

            if (cursorAll.getCount() > 0) {
                if (cursorAll.moveToFirst()) {
                    do {
                        mapAll.put(cursorAll.getString(0), cursorAll.getString(1) + "~" + cursorAll.getString(2));
                    } while (cursorAll.moveToNext());
                }
            }
            cursorAll.close();

            cursorParents = MyDatabaseInstanceHolder.getDatabaseHelper().getDocumentParentDirectories(DatabaseConstant.key_DOCUMENT_NAME, DatabaseConstant.key_ASC);
            if (ActivityStringInfo.lastSelectedParentId.equals("0")) {
                if (upDirectoryBtn != null)
                    upDirectoryBtn.setVisibility(View.GONE);

                if (locationLayout != null)
                    locationLayout.setVisibility(View.GONE);
//
//                if (spinParentDirectory != null)
//                    spinParentDirectory.setVisibility(View.GONE);

            } else {
                if (upDirectoryBtn != null)
                    upDirectoryBtn.setVisibility(View.VISIBLE);

                if (locationLayout != null)
                    locationLayout.setVisibility(View.VISIBLE);

//                if (spinParentDirectory != null)
//                    spinParentDirectory.setVisibility(View.VISIBLE);
            }

            if (cursorParents != null) {
                int totalparents = cursorParents.getCount();
                directories = new ArrayList<CharSequence>();
                dirWithID = new HashMap<String, String>();
                idWithDir = new HashMap<String, String>();
                if (totalparents != 0) {
                    directories.add(0, "Select Directory");

                    if (cursorParents.moveToFirst()) {
                        do {
                            directories.add(cursorParents.getString(2));
                            dirWithID.put(cursorParents.getString(2), cursorParents.getString(0));
                            idWithDir.put(cursorParents.getString(0), cursorParents.getString(2));
                        } while (cursorParents.moveToNext());
                    }

                    cursorParents.close();
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(DocumentFragment.this.getActivity(), R.layout.toolbar_regular_spinner_item_actionbar, directories);
                    adapter.setDropDownViewResource(R.layout.toolbar_regular_spinner_item_dropdown);
                    if (spinParentDirectory != null) {
                        spinParentDirectory.setVisibility(View.VISIBLE);
                        spinParentDirectory.setAdapter(adapter);
                        spinParentDirectory.setPrompt("Directory list");

                        int indexOfLastSelectedItem = directories.indexOf(idWithDir.get(ActivityStringInfo.lastSelectedParentId));
                        if (indexOfLastSelectedItem == -1) {
                            spinParentDirectory.setSelection(0);
                        } else {
                            spinParentDirectory.setSelection(directories.indexOf(idWithDir.get(ActivityStringInfo.lastSelectedParentId)));
                        }

                        spinParentDirectory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long posId) {
                                String selectedItem = spinParentDirectory.getSelectedItem().toString();
                                String id;

                                if (posId == 0) {
                                    id = "0";
//                                lstDirectory.clearChoices();
                                    ActivityStringInfo.locationId.clear();
                                    ActivityStringInfo.lastSelectedParentId = id;
                                    ActivityStringInfo.lastLocationId = "";

                                    if (upDirectoryBtn != null)
                                        upDirectoryBtn.setVisibility(View.GONE);

                                    if (locationLayout != null)
                                        locationLayout.setVisibility(View.GONE);

                                    if (spinParentDirectory != null)
                                        spinParentDirectory.setVisibility(View.GONE);

                                    bindAdapter(id);
                                } else {
                                    if (upDirectoryBtn != null)
                                        upDirectoryBtn.setVisibility(View.VISIBLE);

                                    if (locationLayout != null)
                                        locationLayout.setVisibility(View.VISIBLE);

                                    if (spinParentDirectory != null)
                                        spinParentDirectory.setVisibility(View.GONE);

                                    id = currentId;
                                    ActivityStringInfo.lastSelectedParentId = dirWithID.get(selectedItem);

                                    if (ActivityStringInfo.lastLocationId.equals(""))
                                        id = dirWithID.get(selectedItem);
                                    else {
                                        id = ActivityStringInfo.lastLocationId;
                                        ActivityStringInfo.lastLocationId = "";
                                    }

                                    String loc = FillLocation(id);
                                    if (!ActivityStringInfo.lastLocationString.equals("")) {
                                        if (ActivityStringInfo.lastLocationString.equals(loc)) {
                                            bindAdapter(id);
                                            ActivityStringInfo.lastLocationString = "";
                                        } else {
                                            spinParentDirectory.setSelection(0);
                                            ActivityStringInfo.lastLocationString = "";
                                        }
                                    } else {
                                        bindAdapter(id);
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });
                    } else {
                        bindAdapter("0");
                    }
                } else
                    showToast(MessageInfo.recordNotFound, Toast.LENGTH_SHORT);
            } else {
                cursorParents.close();
            }
        } catch (Exception e) {
            Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    /**
     * METHOD TO BIND THE ADAPTER WITH LIST VIEW
     **/
    public void bindAdapter(String parentId) {
        try {
            int totalSubDirectory = 0;
            fillDocMap.clear();

            Cursor cursorSubdirectory = null;
            currentId = parentId;
            if (parentId.equals("0")) {
                cursorSubdirectory = MyDatabaseInstanceHolder.getDatabaseHelper().getDocumentParentDirectories(DatabaseConstant.key_DOCUMENT_NAME, DatabaseConstant.key_ASC);
            } else {
                cursorSubdirectory = MyDatabaseInstanceHolder.getDatabaseHelper().getSubDirectory(parentId, DatabaseConstant.key_DOCUMENT_TYPE, DatabaseConstant.key_DESC);
            }
            totalSubDirectory = cursorSubdirectory.getCount();

            if (totalSubDirectory != 0) {
                if (cursorSubdirectory.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(DatabaseConstant.key_DOCUMENT_ID, cursorSubdirectory.getString(0));
                        map.put(DatabaseConstant.key_DOCUMENT_PARENT_ID, cursorSubdirectory.getString(1));
                        map.put(DatabaseConstant.key_DOCUMENT_NAME, cursorSubdirectory.getString(2));
                        map.put(DatabaseConstant.key_DOCUMENT_TYPE, cursorSubdirectory.getString(3));
                        map.put(DatabaseConstant.key_DOCUMENT_FILE_LINK, cursorSubdirectory.getString(4));

                        fillDocMap.add(map);

                    } while (cursorSubdirectory.moveToNext());
                }
                //txt_NoRecordMsg.setVisibility(View.GONE);
            } else {
                fillDocMap.clear();
            }
            cursorSubdirectory.close();
//            FillLocation(parentId);
            addButtons(parentId);
            setUpDocListRecyclerView(docListRecyclerView, fillDocMap);
        } catch (Exception e) {
            Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }


    private void setUpDocListRecyclerView(@NonNull RecyclerView recyclerView, List<HashMap<String, String>> docArrayList) {
        DocListRecyclerViewAdapter docListAdapter = new DocListRecyclerViewAdapter(docArrayList);
        recyclerView.setAdapter(docListAdapter);
        if (ActivityStringInfo.mTwoPane) {
            if (docArrayList != null && docArrayList.size() > 0)
                if (locationLayout != null)
                    locationLayout.setVisibility(View.VISIBLE);
            bindAdapterForMultipane(docArrayList.get(0).get(DatabaseConstant.key_DOCUMENT_ID));
        }

    }

    private void setUpDocListItemRecyclerView(@NonNull RecyclerView recyclerItemView, List<HashMap<String, String>> docArrayListItem) {
        DocListItemRecyclerViewAdapter docListItemAdapter = new DocListItemRecyclerViewAdapter(docArrayListItem);
        recyclerItemView.setAdapter(docListItemAdapter);
    }

    public void bindAdapterForMultipane(String parentId) {
        try {
            int totalSubDirectory = 0;
            fillDocItemMap.clear();

            Cursor cursorSubdirectory = null;
            currentId = parentId;
            if (parentId.equals("0")) {
                cursorSubdirectory = MyDatabaseInstanceHolder.getDatabaseHelper().getDocumentParentDirectories(DatabaseConstant.key_DOCUMENT_NAME, DatabaseConstant.key_ASC);
            } else {
                cursorSubdirectory = MyDatabaseInstanceHolder.getDatabaseHelper().getSubDirectory(parentId, DatabaseConstant.key_DOCUMENT_TYPE, DatabaseConstant.key_DESC);
            }
            totalSubDirectory = cursorSubdirectory.getCount();

            if (totalSubDirectory != 0) {
                if (cursorSubdirectory.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(DatabaseConstant.key_DOCUMENT_ID, cursorSubdirectory.getString(0));
                        map.put(DatabaseConstant.key_DOCUMENT_PARENT_ID, cursorSubdirectory.getString(1));
                        map.put(DatabaseConstant.key_DOCUMENT_NAME, cursorSubdirectory.getString(2));
                        map.put(DatabaseConstant.key_DOCUMENT_TYPE, cursorSubdirectory.getString(3));
                        map.put(DatabaseConstant.key_DOCUMENT_FILE_LINK, cursorSubdirectory.getString(4));

                        fillDocItemMap.add(map);

                    } while (cursorSubdirectory.moveToNext());
                }
                //txt_NoRecordMsg.setVisibility(View.GONE);
            } else {
                fillDocItemMap.clear();
            }
            cursorSubdirectory.close();
//            FillLocation(parentId);
            addButtons(parentId);
            setUpDocListItemRecyclerView(docListItemRecyclerView, fillDocItemMap);
        } catch (Exception e) {
            Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
            e.printStackTrace();
        }
    }

    private void addButtons(final String keyId) {
        if (!keyId.equals("0")) {
            String data = mapAll.get(keyId);
            String[] arr = data.split("~");
            String parentId = arr[0].toString();
            String btnTxt = arr[1].toString();
            TextView slash = new TextView(dContext);
            slash.setText(" / ");
            slash.setTextColor(Color.parseColor("#005595"));
            TextView btn = new TextView(dContext);
            btn.setId(Integer.parseInt(keyId));
            btn.setAllCaps(false);
            btn.setTextSize(16);
            btn.setPadding(0, 5, 0, 5);
            btn.setTextColor(Color.parseColor("#005595"));
            SpannableString content = new SpannableString(btnTxt);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            btn.setText(content);
            btn.setBackgroundResource(0);
            btn.setLayoutParams(dlprams);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityStringInfo.mTwoPane) {
                        bindAdapterForMultipane(keyId);
                    } else {
                        bindAdapter(keyId);
                    }
                }
            });
            btn.setFocusable(true);

            if (parentId.equals("0")) {
                dynamicLayout.removeAllViews();
            }

            if (dynamicLayout.getChildCount() > 0) {
                boolean isNotContain = true;
                for (int i = 0; i < dynamicLayout.getChildCount(); i++) {
                    int cId = dynamicLayout.getChildAt(i).getId();
                    if (Integer.parseInt(keyId) == cId) {
                        isNotContain = false;
                    }
                }
                if (isNotContain) {
                    dynamicLayout.addView(slash);
                    dynamicLayout.addView(btn);
                }
            } else {
                dynamicLayout.addView(slash);
                dynamicLayout.addView(btn);
            }
        }

        this.pathScrollView.post(new Runnable() {
            @Override
            public void run() {
                pathScrollView.fullScroll(View.FOCUS_RIGHT);
            }
        });
    }

    public class DocListItemRecyclerViewAdapter extends RecyclerView.Adapter<DocListItemRecyclerViewAdapter.ViewHolder> {

        private final List<HashMap<String, String>> documentItems;
        private SparseBooleanArray selectedItems;

        public DocListItemRecyclerViewAdapter(List<HashMap<String, String>> items) {
            this.documentItems = items;
            this.selectedItems = new SparseBooleanArray();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_documents_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final HashMap<String, String> hasValues = this.documentItems.get(position);
            holder.mView.setSelected(this.selectedItems.get(position));
            try {
                if (hasValues != null) {
                    String fileName = hasValues.get(DatabaseConstant.key_DOCUMENT_NAME);
                    String fileType = hasValues.get(DatabaseConstant.key_DOCUMENT_TYPE);

                    if (fileType.toUpperCase().trim().equals("FOLDER")) {
                        holder.imgFolder.setImageResource(R.drawable.ic_action_open_folder);
                        holder.txtFolderName.setText(hasValues.get(DatabaseConstant.key_DOCUMENT_NAME));
                    } else if (fileType.toUpperCase().trim().equals("FILE")) {

                        if (fileName.toUpperCase().contains(".DOC") || fileName.toUpperCase().contains(".DOCX")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_doc);
                        } else if (fileName.toUpperCase().contains(".JPG") || fileName.toUpperCase().contains(".JPEG")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_jpg);
                        } else if (fileName.toUpperCase().contains(".PNG")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_png);
                        } else if (fileName.toUpperCase().contains(".PDF")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_pdf);
                        } else if (fileName.toUpperCase().contains(".TXT")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_txt);
                        } else if (fileName.toUpperCase().contains(".XLS")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_xls);
                        } else if (fileName.toUpperCase().contains(".GIF")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_gif);
                        } else if (fileName.toUpperCase().contains(".BMP")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_bmp);
                        } else if (fileName.toUpperCase().contains(".FLV")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_flv);
                        } else if (fileName.toUpperCase().contains(".HTML")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_html);
                        } else if (fileName.toUpperCase().contains(".RTF")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_rtf);
                        } else if (fileName.toUpperCase().contains(".RAR")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_rar);
                        } else if (fileName.toUpperCase().contains(".ZIP")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_zip);
                        } else {
                            holder.imgFolder.setImageResource(R.drawable.icon_file);
                        }
                        holder.txtFolderName.setTextColor(Color.parseColor("#005595"));
                        SpannableString content = new SpannableString(hasValues.get(DatabaseConstant.key_DOCUMENT_NAME));
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        holder.txtFolderName.setText(content);
                    }

                    final String selectedItemId = hasValues.get(DatabaseConstant.key_DOCUMENT_ID);
                    final String selectedFileName = hasValues.get(DatabaseConstant.key_DOCUMENT_NAME);
                    final String selectedFileType = hasValues.get(DatabaseConstant.key_DOCUMENT_TYPE);
                    final String selectedFileLink = hasValues.get(DatabaseConstant.key_DOCUMENT_FILE_LINK);

                    holder.imgFolder.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            holder.txtFolderName.performClick();
                        }
                    });

                    //Button btnOpen = (Button)v.findViewById(R.id.btnOpen);
                    holder.txtFolderName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (upDirectoryBtn != null)
                                upDirectoryBtn.setVisibility(View.VISIBLE);

                            if (locationLayout != null)
                                locationLayout.setVisibility(View.VISIBLE);

                            if (spinParentDirectory != null)
                                spinParentDirectory.setVisibility(View.GONE);


                            try {
                                if (!selectedFileType.equals("FILE")) {
                                    ActivityStringInfo.locationId.add(selectedItemId);
                                    if (ActivityStringInfo.mTwoPane) {
                                        bindAdapterForMultipane(selectedItemId);
                                    } else {
                                        int fileFromSpinner = directories.indexOf(selectedFileName);
                                        if (fileFromSpinner >= 0 && fileFromSpinner <= (directories.size() - 1) && spinParentDirectory != null) {
                                            spinParentDirectory.setSelection(fileFromSpinner);
                                        }
                                        bindAdapter(selectedItemId);
                                    }
                                } else {
                                    try {
                                        if (!(selectedFileName == null) && !(selectedFileLink == null)) {
                                            strFileName = selectedFileName;
                                            strFileLink = selectedFileLink;
                                            downloadFile();
                                        } else {
                                            showToast(MessageInfo.fileDownloadingFail, Toast.LENGTH_SHORT);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        showToast(MessageInfo.fileDownloadingFail, Toast.LENGTH_SHORT);
                                    }
                                }
                            } catch (Exception e) {
                                Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(dContext, e);
                e.printStackTrace();
            }
        }

        public void toggleSelection(int pos, View view) {
//            if (selectedItems.get(pos, false)) {
//                selectedItems.delete(pos);
//                view.setSelected(false);
//            }
//            else {
//                selectedItems.put(pos, true);
//                view.setSelected(true);
//
//            }
//            notifyItemChanged(pos);
            this.selectedItems.clear();
            this.selectedItems.put(pos, true);
            view.setSelected(true);
            notifyDataSetChanged();
        }

        public void clearSelections() {
            this.selectedItems.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {
            return this.selectedItems.size();
        }

        public List<Integer> getSelectedItems() {
            List<Integer> items =
                    new ArrayList<Integer>(this.selectedItems.size());
            for (int i = 0; i < this.selectedItems.size(); i++) {
                items.add(this.selectedItems.keyAt(i));
            }
            return items;
        }

        @Override
        public int getItemCount() {
            return this.documentItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView txtFolderName;
            public final ImageView imgFolder;
            public HashMap<String, String> hasValues;

            public ViewHolder(View view) {
                super(view);
                view.setClickable(true);
                mView = view;
                txtFolderName = (TextView) view.findViewById(R.id.txtFolderName);
                imgFolder = (ImageView) view.findViewById(R.id.imgFolder);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + hasValues.toString() + "'";
            }
        }
    }


    public class DocListRecyclerViewAdapter extends RecyclerView.Adapter<DocListRecyclerViewAdapter.ViewHolder> {

        private final List<HashMap<String, String>> documentList;
        private SparseBooleanArray selectedListItems;

        public DocListRecyclerViewAdapter(List<HashMap<String, String>> itemsList) {
            this.documentList = itemsList;
            this.selectedListItems = new SparseBooleanArray();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_documents_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final HashMap<String, String> hasValues = this.documentList.get(position);
            if (ActivityStringInfo.mTwoPane) {
                if (position == 0 && isFirstSelected) {
                    this.selectedListItems.put(position, true);
                }

                if (this.selectedListItems.get(position)) {
                    holder.mView.setSelected(true);
                } else {
                    holder.mView.setSelected(false);
                }
            }

            try {
                if (hasValues != null) {
                    String fileName = hasValues.get(DatabaseConstant.key_DOCUMENT_NAME);
                    String fileType = hasValues.get(DatabaseConstant.key_DOCUMENT_TYPE);

                    if (fileType.toUpperCase().trim().equals("FOLDER")) {
                        holder.imgFolder.setImageResource(R.drawable.ic_action_open_folder);
                        holder.txtFolderName.setText(hasValues.get(DatabaseConstant.key_DOCUMENT_NAME));
                    } else if (fileType.toUpperCase().trim().equals("FILE")) {

                        if (fileName.toUpperCase().contains(".DOC") || fileName.toUpperCase().contains(".DOCX")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_doc);
                        } else if (fileName.toUpperCase().contains(".JPG") || fileName.toUpperCase().contains(".JPEG")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_jpg);
                        } else if (fileName.toUpperCase().contains(".PNG")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_png);
                        } else if (fileName.toUpperCase().contains(".PDF")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_pdf);
                        } else if (fileName.toUpperCase().contains(".TXT")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_txt);
                        } else if (fileName.toUpperCase().contains(".XLS")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_xls);
                        } else if (fileName.toUpperCase().contains(".GIF")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_gif);
                        } else if (fileName.toUpperCase().contains(".BMP")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_bmp);
                        } else if (fileName.toUpperCase().contains(".FLV")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_flv);
                        } else if (fileName.toUpperCase().contains(".HTML")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_html);
                        } else if (fileName.toUpperCase().contains(".RTF")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_rtf);
                        } else if (fileName.toUpperCase().contains(".RAR")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_rar);
                        } else if (fileName.toUpperCase().contains(".ZIP")) {
                            holder.imgFolder.setImageResource(R.drawable.file_extension_zip);
                        } else {
                            holder.imgFolder.setImageResource(R.drawable.icon_file);
                        }

                        holder.txtFolderName.setTextColor(Color.parseColor("#005595"));
                        SpannableString content = new SpannableString(hasValues.get(DatabaseConstant.key_DOCUMENT_NAME));
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        holder.txtFolderName.setText(content);
                    }

                    final String selectedItemId = hasValues.get(DatabaseConstant.key_DOCUMENT_ID);
                    final String selectedFileName = hasValues.get(DatabaseConstant.key_DOCUMENT_NAME);
                    final String selectedFileType = hasValues.get(DatabaseConstant.key_DOCUMENT_TYPE);
                    final String selectedFileLink = hasValues.get(DatabaseConstant.key_DOCUMENT_FILE_LINK);

                    holder.imgFolder.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            holder.txtFolderName.performClick();
                        }
                    });

                    //Button btnOpen = (Button)v.findViewById(R.id.btnOpen);
                    holder.txtFolderName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleSelection(position, v);

                            if (upDirectoryBtn != null)
                                upDirectoryBtn.setVisibility(View.VISIBLE);

                            if (locationLayout != null)
                                locationLayout.setVisibility(View.VISIBLE);

                            if (spinParentDirectory != null)
                                spinParentDirectory.setVisibility(View.GONE);


                            try {
                                if (!selectedFileType.equals("FILE")) {
                                    ActivityStringInfo.locationId.add(selectedItemId);
                                    if (ActivityStringInfo.mTwoPane) {
                                        bindAdapterForMultipane(selectedItemId);
                                    } else {
                                        int fileFromSpinner = directories.indexOf(selectedFileName);
                                        if (fileFromSpinner >= 0 && fileFromSpinner <= (directories.size() - 1) && spinParentDirectory != null) {
                                            spinParentDirectory.setSelection(fileFromSpinner);
                                        }
                                        bindAdapter(selectedItemId);
                                    }
                                } else {
                                    try {
                                        if (!(selectedFileName == null) && !(selectedFileLink == null)) {
                                            strFileName = selectedFileName;
                                            strFileLink = selectedFileLink;
                                            downloadFile();
                                        } else {
                                            showToast(MessageInfo.fileDownloadingFail, Toast.LENGTH_SHORT);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        showToast(MessageInfo.fileDownloadingFail, Toast.LENGTH_SHORT);
                                    }
                                }
                            } catch (Exception e) {
                                Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Utility.saveExceptionDetails(dContext, e);
                e.printStackTrace();
            }
        }

        public void toggleSelection(int pos, View view) {
            isFirstSelected = false;
            this.selectedListItems.clear();
            this.selectedListItems.put(pos, true);
//            view.setSelected(true);
            notifyDataSetChanged();
        }

        public void clearSelections() {
            this.selectedListItems.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {
            return this.selectedListItems.size();
        }

        public List<Integer> getSelectedItems() {
            List<Integer> items =
                    new ArrayList<Integer>(this.selectedListItems.size());
            for (int i = 0; i < this.selectedListItems.size(); i++) {
                items.add(this.selectedListItems.keyAt(i));
            }
            return items;
        }

        @Override
        public int getItemCount() {
            return this.documentList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView txtFolderName;
            public final ImageView imgFolder;
            public HashMap<String, String> hasValues;

            public ViewHolder(View view) {
                super(view);
                view.setClickable(true);
                mView = view;
                txtFolderName = (TextView) view.findViewById(R.id.txtFolderName);
                imgFolder = (ImageView) view.findViewById(R.id.imgFolder);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + hasValues.toString() + "'";
            }
        }
    }

    //method to download selected file.
    public void downloadFile() {
        if (Utility.Connectivity_Internet(dContext))
            new SelectDocDownloadTask().execute();
        else
            showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
    }

    private class SelectDocDownloadTask extends AsyncTask<String, Void, String> {
        boolean error = false;

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog("Downloading. Please wait...");
            } catch (Exception e) {
                Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
                e.printStackTrace();
            }
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                //System.out.println("strFileLink==="+strFileLink);
                URL u = new URL(strFileLink);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                //c.setDoOutput(true);
                c.connect();


                String PATH = Environment.getExternalStorageDirectory() + "/download/StaffTAP/";

                Cursor c1 = MyDatabaseInstanceHolder.getDatabaseHelper().getGlobalsRecords("download_path");
                if (c1.getCount() == 0) {
                    MyDatabaseInstanceHolder.getDatabaseHelper().insertDownloadFilePath("download_path", PATH);
                }
                c1.close();


                File file = new File(PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File outputFile = new File(file, strFileName);
                FileOutputStream fos = new FileOutputStream(outputFile);
                //FileOutputStream fos = new FileOutputStream(new File(file,strFileName));

                //InputStream instream = new java.io.BufferedInputStream(c.getInputStream());
                InputStream instream = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = instream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                instream.close();

                Utility.notification(DocumentFragment.this.getActivity(), strFileName);
                //notification();
            } catch (Exception e) {
                error = true;
                Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
                e.printStackTrace();

            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (error) {
                if (Utility.Connectivity_Internet(dContext))
                    showToast(MessageInfo.fileDownloadingFail, Toast.LENGTH_SHORT);
                else
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
                error = false;
            } else
                showToast(MessageInfo.fileDownloadingSuccess, Toast.LENGTH_SHORT);
        }
    }

    private void getDocListRefreshed() {
        new SelectDataTaskForDocRefresh().execute();
    }

    private class SelectDataTaskForDocRefresh extends AsyncTask<String, Void, String> {
        String strMsg = "";

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            try {
                showTransparentProgressDialog(MessageInfo.strRefresh);
            } catch (Exception e) {
                Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected String doInBackground(String... args) {
            try {
                Synchronization syc = new Synchronization(dContext);
                strMsg = syc.getInformation(DocumentFragment.this.getActivity());
            } catch (Exception e) {
                Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
                e.printStackTrace();
            }
            return "";
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final String result) {
            hideTransparentProgressDialog();
            if (strMsg.equals("true")) {
                stringInfo.clear();
                shouldDocListRefreshed = true;
                isFirstSelected = true;
                setDocumentsAll();
            } else if (strMsg != "true") {
                if (Utility.Connectivity_Internet(dContext)) {
                    if (!strMsg.equals(""))
                        showToast(strMsg, Toast.LENGTH_SHORT);
                } else
                    showToast(MessageInfo.strError, Toast.LENGTH_SHORT);
            }
        }
    }

    public String FillLocation(String parentId) {
        String strLocation = "";
        String tempparentid = parentId;
        try {
            if (!tempparentid.equals("0")) {
                List<String> tempLocationIdlst = new ArrayList<String>();
                List<String> templocationlst = new ArrayList<String>();
                int i = 0;
                while (true) {
                    tempLocationIdlst.add(tempparentid);

                    String data = mapAll.get(tempparentid);
                    String[] arr = data.split("~");
                    tempparentid = arr[0].toString();
                    if (i == 0) {
                        strLocation = arr[1].toString() + strLocation;
                        i = 1;
                    } else
                        strLocation = arr[1].toString() + "/" + strLocation;

                    templocationlst.add(arr[1].toString());

                    if (tempparentid.equals("0")) {
                        strLocation = "/" + strLocation;
                        break;
                    }
                }
                if (tempLocationIdlst.size() > 0) {
                    Collections.reverse(tempLocationIdlst);
                    Iterator<String> itr = tempLocationIdlst.iterator();
                    ActivityStringInfo.locationId.clear();
                    while (itr.hasNext()) {
                        ActivityStringInfo.locationId.add(itr.next());
                    }
                }
            }

            txtLocation.setText(strLocation);
        } catch (Exception e) {
            Utility.saveExceptionDetails(DocumentFragment.this.getActivity(), e);
            e.printStackTrace();
        }
        return strLocation;
    }

}
