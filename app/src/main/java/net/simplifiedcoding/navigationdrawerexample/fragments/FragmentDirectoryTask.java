///*
//package net.simplifiedcoding.navigationdrawerexample.fragments;
//
//import android.Manifest;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.AppCompatButton;
//import android.support.v7.widget.AppCompatEditText;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import net.simplifiedcoding.navigationdrawerexample.Constant.Constant;
//import net.simplifiedcoding.navigationdrawerexample.Model.MisLevel;
//import net.simplifiedcoding.navigationdrawerexample.Model.UserSearch;
//import net.simplifiedcoding.navigationdrawerexample.R;
//import net.simplifiedcoding.navigationdrawerexample.activities.MainActivity;
//import net.simplifiedcoding.navigationdrawerexample.adapter.AdapterDirectory;
//import net.simplifiedcoding.navigationdrawerexample.adapter.AdapterSearchList;
//import net.simplifiedcoding.navigationdrawerexample.util.AndroidUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.OkHttpClient;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.http.GET;
//import retrofit2.http.Query;
//
//
//public class FragmentDirectoryTask extends Fragment implements Callback<UserSearch>, View.OnClickListener,AdapterDirectory.ClickListener  {
//
//    private static final String TAG = FragmentDirectoryTask.class.getSimpleName();
//    private OnFragmentInteractionListener mListener;
//    private TextView tvTitleName;
//    private RecyclerView recyclerView;
//    private AdapterDirectory adapterDirectory;
//    private ProgressDialog pDialog;
//    private UserSearch userSearch;
//    private RadioGroup radioGroup;
//    private RadioButton radioN, radioUI, radioD, radioL;
//    private AppCompatButton btnSearch;
//    private String type = "", value = "";
//    private AppCompatEditText etSearch;
//    private LinearLayout llListHeader, llHeader;
//    Spinner level_spinner;
//    SharedPreferences sharedpreferences;
//    String user_id;
//    View mySpinner;
//    MISLevelAdapter MISLevelAdapter;
//    List<MisLevel.Datum> leveldataList = new ArrayList<>();
//    String level = "";
//    int checkedId;
//    int checkedstate;
//    private String level_type,level_value;
//
//    public FragmentDirectoryTask() {
//        // Required empty public constructor
//    }
//
//
//    public static FragmentDirectoryTask newInstance() {
//        FragmentDirectoryTask fragment = new FragmentDirectoryTask();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_searchtask, container, false);
//        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        initView(rootView);
//        return rootView;
//    }
//
//
//    void initView(View view) {
//
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        getFragmentManager().popBackStackImmediate();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//
//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        toolbar.setPadding(0, 5, 10, 5);
//        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
//        ((MainActivity) getActivity()).getSupportActionBar().setTitle(null);
//        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
//        ImageView imageView = (ImageView) view.findViewById(R.id.btn_drawer);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity) getActivity()).clickEventSlide();
//            }
//        });
//
//        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
//        pDialog.setCancelable(true);
//        tvTitleName = (TextView) view.findViewById(R.id.task_name_tv);
//        radioN = (RadioButton) view.findViewById(R.id.name);
//        radioUI = (RadioButton) view.findViewById(R.id.email);
//        radioD = (RadioButton) view.findViewById(R.id.designation);
//        radioL = (RadioButton) view.findViewById(R.id.level);
//        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
//        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_userlist);
//        btnSearch = (AppCompatButton) view.findViewById(R.id.btn_search);
//        etSearch = (AppCompatEditText) view.findViewById(R.id.et_search);
//        llHeader = (LinearLayout) view.findViewById(R.id.ll_header);
//        llListHeader = (LinearLayout) view.findViewById(R.id.ll_list_header);
//        level_spinner = (Spinner) view.findViewById(R.id.level_spinner);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
//        layoutManager.setSmoothScrollbarEnabled(true);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setNestedScrollingEnabled(false);
//
//        tvTitleName.setText("DIRECTORY");
//
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkkedId) {
//                checkedId = checkkedId;
//
//                RadioButton rb = (RadioButton) group.findViewById(checkkedId);
//                if (null != radioN && checkkedId > -1) {
////                    Toast.makeText(getContext(), rb.getText().toString(), Toast.LENGTH_SHORT).show();
//                    type = rb.getText().toString();
//                    value = etSearch.getText().toString();
//                    MISLevelAdapter = new MISLevelAdapter(getActivity(), R.layout.cus_spinner, leveldataList);
////                    level_spinner.setAdapter(MISLevelAdapter);
////                    MISLevelAdapter.notifyDataSetChanged();
//
//                }
//            }
//        });
//        llListHeader.setVisibility(View.GONE);
//        llHeader.setVisibility(View.VISIBLE);
//        btnSearch.setOnClickListener(this);
//        if (sharedpreferences.getString("User_id", "") != null) {
//            user_id = sharedpreferences.getString("User_id", "");
//        }
//
//        if (AndroidUtil.isConnectingToInternet(getActivity())) {
//            MislevelApiCall();
//        } else {
//            Toast.makeText(getActivity(), Constant.check_internet_connection, Toast.LENGTH_SHORT).show();
//        }
////        if (AndroidUtil.isConnectingToInternet(getActivity())) {
////            getUserlistData("all", "all");
////        } else {
////            Toast.makeText(getActivity(), Constant.check_internet_connection, Toast.LENGTH_SHORT).show();
////        }
//
//
//    }
//
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.activity_main_drawer, menu); // removed to not double the menu items
//    }
//
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_search:
//                if (etSearch.getText().length() != 0) {
//                    value = etSearch.getText().toString();
//                }
//                if (type.equalsIgnoreCase("Name") && etSearch.getText().length() == 0) {
//                    Toast.makeText(getActivity(), Constant.check_name, Toast.LENGTH_SHORT).show();
//                } else if (type.equalsIgnoreCase("Email") && etSearch.getText().length() == 0) {
//                    Toast.makeText(getActivity(), Constant.check_email, Toast.LENGTH_SHORT).show();
//                } else if (type.equalsIgnoreCase("Designation") && etSearch.getText().length() == 0) {
//                    Toast.makeText(getActivity(), Constant.check_designation, Toast.LENGTH_SHORT).show();
//                } else {
//                    if (AndroidUtil.isConnectingToInternet(getActivity())) {
//                        getUserlistData(type, value);
//                    } else {
//                        Toast.makeText(getActivity(), Constant.check_internet_connection, Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            case R.id.tv_call:
//
//
//                break;
//        }
//    }
//   */
///* void actionCall(View view, String number) {
//        int checkPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
//        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    getActivity(),
//                    new String[]{Manifest.permission.CALL_PHONE},
//                    Constant.Fields.ACTION_CALL_REQUEST_CODE);
//        } else {
//            callNumber(view, number);
//        }
//    }
//
//    private void callNumber(View view, String number) {
//        Intent intent = new Intent(Intent.ACTION_CALL);
//        intent.setData(Uri.parse("tel:" + number));
//        getContext().startActivity(intent);
//    }*//*
//
//
//    @Override
//    public void onItemClick(int position, View v) {
//
//        showDialouge(position);
//
//    }
//
//
//        protected void showDialouge(final int position) {
//            // TODO Auto-generated method stub
//            final Dialog dialog = new Dialog(getContext()) {
//                @Override
//                public void onStart() {
//                    super.onStart();
//                    Dialog dialog = this;
//                    if (dialog != null) {
//                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
//                        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                        dialog.getWindow().setLayout(width, height);
//                    }
//                }
//            };
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.layout_user_information);
//            TextView tv_name,tv_email,tv_call,tv_designation,tv_level;
//            tv_name = (TextView)dialog.findViewById(R.id.tv_name);
//            tv_email = (TextView)dialog.findViewById(R.id.tv_email);
//            tv_call = (TextView)dialog.findViewById(R.id.tv_call);
//            tv_designation = (TextView)dialog.findViewById(R.id.tv_designation);
//           // tv_level = (TextView)dialog.findViewById(R.id.tv_level);
//
//            */
///*tv_call.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    new AlertDialog.Builder(getContext())
//                            .setMessage(getContext().getString(R.string.confirmation_dialog))
//                            .setIcon(AndroidUtil.setTint(getContext().getResources().getDrawable(R.mipmap.ic_launcher_call), R.color.green))
//                            .setMessage(getContext().getString(R.string.do_you_want_to_make_call))
//                            .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if (userSearch.getData().get(position).getUser_phone() != null&&!userSearch.getData().get(position).getUser_phone().isEmpty())
//                                        actionCall(v, userSearch.getData().get(position).getUser_phone());
//                                    //make call from phone
//                                }
//                            })
//                            .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            }).show();
//
//                }
//            });*//*
//
//
//            if (userSearch.getData().get(position).getUser_name()!=null && !userSearch.getData().get(position).getUser_name().isEmpty()) {
//                tv_name.setText(" "+userSearch.getData().get(position).getUser_name());
//            }
//            if (userSearch.getData().get(position).getUser_email()!=null && !userSearch.getData().get(position).getUser_email().isEmpty()) {
//                tv_email.setText(" "+userSearch.getData().get(position).getUser_email());
//            }
//            if (userSearch.getData().get(position).getUser_phone()!=null && !userSearch.getData().get(position).getUser_phone().isEmpty()) {
//                tv_call.setText(" "+userSearch.getData().get(position).getUser_phone());
//            }
//            if (userSearch.getData().get(position).getUser_designation()!=null && !userSearch.getData().get(position).getUser_designation().isEmpty()) {
//                tv_designation.setText(" "+userSearch.getData().get(position).getUser_designation());
//            }
//            if (userSearch.getData().get(position).getLevel()!=null && !userSearch.getData().get(position).getLevel().isEmpty()) {
//                tv_level.setText(" "+userSearch.getData().get(position).getLevel());
//            }
//            Button btncancel = (Button) dialog.findViewById(R.id.btn_cancel);
//            btncancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//
//            dialog.show();
//    }
//
//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }
//
//    private void getUserlistData(String type, String value) {
//        if (pDialog != null) {
//            pDialog.setMessage(getString(R.string.loading));
//            if (!pDialog.isShowing())
//                pDialog.show();
//        }
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constant.WebUrl.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(new OkHttpClient())
//                .build();
//        IUSERLIST iuserlist = retrofit.create(IUSERLIST.class);
//        Call<UserSearch> call = iuserlist.getData(level_type,level_value,type, value, user_id);
//        call.enqueue(this);
//    }
//
//    @Override
//    public void onResponse(Call<UserSearch> call, Response<UserSearch> response) {
//        userSearch = response.body();
//        //Log.e(TAG,faqModelData.toString());
//        if (userSearch != null) {
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//            if (Integer.parseInt(userSearch.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
//                //  if(userSearch.getFaq()!=null||userSearch.getFaq().size()>0)
//                adapterDirectory = new AdapterDirectory(getContext(), userSearch);
//                recyclerView.setAdapter(adapterDirectory);
//                adapterDirectory.setOnItemClickListener(FragmentDirectoryTask.this);
//                llHeader.setVisibility(View.VISIBLE);
//                llListHeader.setVisibility(View.VISIBLE);
//
//            }
//        }
//    }
//
//    @Override
//    public void onFailure(Call<UserSearch> call, Throwable t) {
//
//    }
//
//
//    private void MislevelApiCall() {
//
//        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
//        pDialog.setCancelable(false);
//        if (pDialog != null) {
//            pDialog.setMessage(getString(R.string.loading));
//            if (!pDialog.isShowing())
//                pDialog.show();
//        }
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constant.WebUrl.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(new OkHttpClient().newBuilder().writeTimeout(1, TimeUnit.MINUTES).connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).build())
//                .build();
//        IUSERLIST icreatetask = retrofit.create(IUSERLIST.class);
//
//        Call<MisLevel> submitTask = icreatetask.getData(user_id);
//
//        submitTask.enqueue(new Callback<MisLevel>() {
//            @Override
//            public void onResponse(Call<MisLevel> call, Response<MisLevel> response) {
//                if (pDialog != null) {
//                    pDialog.dismiss();
//                }
//                try {
//                    MisLevel misLevel = response.body();
//                    if (misLevel != null) {
//
//                        if (Integer.parseInt(misLevel.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
//
//
//                            leveldataList = misLevel.getData();
//                            MisLevel.Datum datum = new MisLevel().new Datum();
//                            datum.setName("Select Level");
//                            leveldataList.add(0, datum);
//                            MISLevelAdapter = new MISLevelAdapter(getActivity(), R.layout.cus_spinner, leveldataList);
//                            level_spinner.setAdapter(MISLevelAdapter);
//
//
//                            level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//                                @Override
//                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                                    if (position == 0) {
//                                       level_value="";
//                                        level_type="";
//                                        return;
//                                    }
//                                   */
///* etSearch.setText("");
//                                    RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
//                                    if (rb != null && checkedId > -1) {
//                                        if (rb.isChecked()) {
//                                            rb.setChecked(false);
//                                        }
//                                    }*//*
//
//
//
//                                    level_type = "level";
//
//                                    level = leveldataList.get(position).getId();
//                                    level_value = level;
//
////                                    radioGroup.clearCheck();
//                                }
//
//                                @Override
//                                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                                }
//                            });
//
//                        } else {
//
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MisLevel> call, Throwable t) {
//                if (pDialog != null) {
//                    pDialog.dismiss();
//                }
//            }
//        });
//    }
//
//
//    public interface IUSERLIST {
//        @GET(Constant.WebUrl.ALLUSERLIST)
//        Call<UserSearch> getData(@Query("level_type") String level_type, @Query("level_value") String level_value,@Query("type") String type, @Query("value") String value, @Query("id") String id);
//
//        @GET(Constant.WebUrl.MIS_LEVEL)
//        Call<MisLevel> getData(@Query("id") String id);
//    }
//
//
//    // adapter for the member mislevel spinner
//    public class MISLevelAdapter extends ArrayAdapter<MisLevel.Datum> {
//        List<MisLevel.Datum> data;
//
//        public MISLevelAdapter(Context ctx, int txtViewResourceId, List<MisLevel.Datum> data) {
//
//            super(ctx, txtViewResourceId, data);
//            this.data = data;
//        }
//
//        //getting the dropdown of spinner
//        @Override
//        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
//            return getChildView(position, cnvtView, prnt);
//        }
//
//        @Override
//        public View getView(int pos, View cnvtView, ViewGroup prnt) {
//            return getCustomView(pos, cnvtView, prnt);
//        }
//
//        public View getCustomView(int position, View convertView, ViewGroup parent) {
//
//            checkedstate = position;
//            if (getActivity() != null) {
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                mySpinner = inflater.inflate(R.layout.spinner_item, parent, false);
//                TextView city_parent_text = (TextView) mySpinner.findViewById(R.id.tv);
//                try {
////                    if (checkedstate!=position) {
//                    city_parent_text.setText(data.get(position).getName());
////                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
////                if (position == 0) {
////                    city_parent_text.setTextColor(getResources().getColor(R.color.edit_text_color));
////                } else {
//                city_parent_text.setTextSize(13);
//                city_parent_text.setTextColor(getResources().getColor(R.color.textviewcolor));
////                }
//            }
//            return mySpinner;
//        }
//
//        public View getChildView(int position, View convertView, ViewGroup parent) {
//
//
//            if (getActivity() != null) {
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                mySpinner = inflater.inflate(R.layout.spinner_item_edit, parent, false);
//                TextView city_child_text = (TextView) mySpinner.findViewById(R.id.tv);
//                city_child_text.setTextSize(13);
//
//                city_child_text.setText(data.get(position).getName());
//                city_child_text.setTextColor(getResources().getColor(R.color.textviewcolor));
//            }
//            return mySpinner;
//        }
//
//    }
//
//}
//*/
