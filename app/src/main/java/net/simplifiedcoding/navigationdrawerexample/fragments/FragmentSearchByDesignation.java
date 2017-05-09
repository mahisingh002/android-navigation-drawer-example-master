package net.simplifiedcoding.navigationdrawerexample.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.simplifiedcoding.navigationdrawerexample.Constant.Constant;
import net.simplifiedcoding.navigationdrawerexample.Model.MisDateData;
import net.simplifiedcoding.navigationdrawerexample.Model.Search;
import net.simplifiedcoding.navigationdrawerexample.Model.SearchDesignation;
import net.simplifiedcoding.navigationdrawerexample.Model.ShowMemberData;
import net.simplifiedcoding.navigationdrawerexample.Model.UserSearch;
import net.simplifiedcoding.navigationdrawerexample.R;
import net.simplifiedcoding.navigationdrawerexample.activities.MainActivity;
//import net.simplifiedcoding.navigationdrawerexample.adapter.AdapterMisDetail;
import net.simplifiedcoding.navigationdrawerexample.adapter.AdapterSearchList;
import net.simplifiedcoding.navigationdrawerexample.util.AndroidUtil;
import net.simplifiedcoding.navigationdrawerexample.util.EndlessRecyclerViewScrollListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FragmentSearchByDesignation extends Fragment implements Callback<SearchDesignation>, View.OnClickListener, AdapterSearchList.ClickListener {

    private static final String TAG = FragmentSearchByDesignation.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private SearchDesignation searchDesignation;
    private ProgressDialog pDialog;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String user_id;
    TextView no_task_tv;
    TextView tv_NORecord;
    TextView select_memtv, addtofav_tv;
    UserSearch userSearch = null;
    View mySpinner;
    Spinner member_spinner, level_spinner, region_spinner;
    FuntionAdapter funtionAdapter;
    LevelAdapter levelAdapter;
    RegionAdapter regionAdapter;
    String funtion, region, level_id;
    TextView go_btn, members_tv, date_tv, pr_ccit_tv, ccit_tv, cit_tv, tvTitleName;
    RelativeLayout relative_mis;
    LinearLayout linear_adds_layout;
    List<SearchDesignation.Function> functionsList = new ArrayList<SearchDesignation.Function>();
    List<SearchDesignation.Level> levelList = new ArrayList<SearchDesignation.Level>();
    List<SearchDesignation.Region> regionList = new ArrayList<SearchDesignation.Region>();
    List<MisDateData.Datum> misdateDataList = new ArrayList<MisDateData.Datum>();
    FragmentMis.AdapterMisDetail adapterMisDetail;
    List<String> prCitlistdata = new ArrayList<String>();
    List<String> CCitlistdata = new ArrayList<String>();
    private int year;
    private int month;
    boolean flagclick = false;
    private int day;
    private AdapterSearchList adapterSearchList;
    private List<Search> searchResultData;
    boolean flagsearch = false;
    LinearLayout linear_spinner;
    boolean flagstop = true;


    int scroll_page = 0;

    public FragmentSearchByDesignation() {
        // Required empty public constructor
    }


    public static FragmentSearchByDesignation newInstance() {
        FragmentSearchByDesignation fragment = new FragmentSearchByDesignation();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_searchby_designation, container, false);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        initView(rootView);
        linear_spinner.setVisibility(View.VISIBLE);
        return rootView;
    }


    void initView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setPadding(0, 5, 10, 5);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(null);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ImageView imageView = (ImageView) view.findViewById(R.id.btn_drawer);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).clickEventSlide();
            }
        });

        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(false);
        member_spinner = (Spinner) view.findViewById(R.id.member_spinner);

        level_spinner = (Spinner) view.findViewById(R.id.level_spinner);
        region_spinner = (Spinner) view.findViewById(R.id.region_spinner);


        go_btn = (TextView) view.findViewById(R.id.go_btn);
        tv_NORecord = (TextView) view.findViewById(R.id.tv_NORecord);
        linear_adds_layout = (LinearLayout) view.findViewById(R.id.linear_adds_layout);
        tvTitleName = (TextView) view.findViewById(R.id.task_name_tv);
        relative_mis = (RelativeLayout) view.findViewById(R.id.relative_mis);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_mis_detail);
        linear_spinner = (LinearLayout) view.findViewById(R.id.linear_spinner);
        select_memtv = (TextView) view.findViewById(R.id.select_memtv);

        tvTitleName.setText(getString(R.string.searchby_deasignation));
        select_memtv.setOnClickListener(this);

        // flagsearch=false;
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_userlist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.e(TAG, "Page: " + page + " Count: " + totalItemsCount);

                if (flagsearch) {

                    SubmitDataApiCall(page + 1);

                }

            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);
                flagsearch = true;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                flagsearch = true;
            }
        });

        adapterSearchList = new AdapterSearchList(getContext(), null);
        recyclerView.setAdapter(adapterSearchList);
        adapterSearchList.setOnItemClickListener(FragmentSearchByDesignation.this);
        if (AndroidUtil.isConnectingToInternet(getActivity())) {
            getNewTaskServerData();
        } else {
            Toast.makeText(getActivity(), Constant.check_internet_connection, Toast.LENGTH_LONG).show();
        }
        go_btn.setOnClickListener(this);
//        relative_mis.setOnClickListener(this);
//        adapterNewTask = new AdapterNewTask(getContext());
//        recyclerView.setAdapter(adapterNewTask);

    }


    private void getNewTaskServerData() {
        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(false);
        if (pDialog != null) {
            pDialog.setMessage(getString(R.string.loading));
            if (!pDialog.isShowing())
                pDialog.show();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.WebUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        ICREATETASK icreatetask = retrofit.create(ICREATETASK.class);
        if (sharedpreferences.getString("User_id", "") != null) {
            user_id = sharedpreferences.getString("User_id", "");
        }
        Call<SearchDesignation> call = icreatetask.getDataDesignation(user_id);
//        Call<NewTaskData> call = ifaqdata.getData("Open", "2");
        call.enqueue(this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_main_drawer, menu); // removed to not double the menu items
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_btn:
                linear_adds_layout.setVisibility(View.GONE);
                if (AndroidUtil.isConnectingToInternet(getActivity())) {
                    flagstop = true;
                    adapterSearchList.clearAllData();
                    SubmitDataApiCall(1);
                } else {
                    Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.addtofav_tv:
                int pos = (int) view.getTag();
                String taskId = searchResultData.get(pos).getUser_id();
                addtofav_tv.setText("Added to Favourites");
                if (searchResultData.get(pos).getFav().equalsIgnoreCase("1")) {
                    Toast.makeText(getContext(), "Already added as favorite", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (AndroidUtil.isConnectingToInternet(getContext())) {
                        addToFav(taskId, pos);
                    } else {
                        Toast.makeText(getContext(), Constant.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.select_memtv:
                if (linear_adds_layout.getVisibility() == View.VISIBLE) {
                    linear_adds_layout.setVisibility(View.GONE);
                } else if (linear_adds_layout.getVisibility() == View.GONE) {
                    linear_adds_layout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    protected void showDialouge(final int position, final List<Search> userList) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(getContext()) {
            @Override
            public void onStart() {
                super.onStart();
                Dialog dialog = this;
                if (dialog != null) {
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setLayout(width, height);
                }
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_user_information);
        Button btn_action;
        ImageView btn_cancel;
        TextView tv_name, tv_email, tv_personal_email, tv_call, tv_designation, tv_level;
        btn_action = (Button) dialog.findViewById(R.id.btn_action);
        btn_cancel = (ImageView) dialog.findViewById(R.id.btn_cancel);
        tv_name = (TextView) dialog.findViewById(R.id.tv_name);
        tv_email = (TextView) dialog.findViewById(R.id.tv_email);
        tv_personal_email = (TextView) dialog.findViewById(R.id.tv_personal_email);
        tv_call = (TextView) dialog.findViewById(R.id.tv_call);
        tv_designation = (TextView) dialog.findViewById(R.id.tv_designation);
        tv_level = (TextView) dialog.findViewById(R.id.tv_level);
        addtofav_tv = (TextView) dialog.findViewById(R.id.addtofav_tv);
        tv_level.setVisibility(View.GONE);
        if (userList.get(position).getShowicon() == 0) {
            addtofav_tv.setVisibility(View.VISIBLE);
            btn_action.setVisibility(View.GONE);
        }

        addtofav_tv.setTag(position);
        btn_action.setTag(position);
        btn_action.setOnClickListener(this);
        addtofav_tv.setOnClickListener(this);
        /*btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("email", userSearch.getData().get(position).getUser_email());
                bundle.putString("id", userSearch.getData().get(position).getUser_id());
                Fragment fragment = new FragmentCreateTask();
                fragment.setArguments(bundle);
                ((MainActivity) getContext()).replacefragment(fragment);
            }
        });
*/


            /*tv_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new AlertDialog.Builder(getContext())
                            .setMessage(getContext().getString(R.string.confirmation_dialog))
                            .setIcon(AndroidUtil.setTint(getContext().getResources().getDrawable(R.mipmap.ic_launcher_call), R.color.green))
                            .setMessage(getContext().getString(R.string.do_you_want_to_make_call))
                            .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (userSearch.getData().get(position).getUser_phone() != null&&!userSearch.getData().get(position).getUser_phone().isEmpty())
                                        actionCall(v, userSearch.getData().get(position).getUser_phone());
                                    //make call from phone
                                }
                            })
                            .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            });*/

        if (userList.get(position).getUser_name() != null && !userList.get(position).getUser_name().isEmpty()) {
            tv_name.setText(" " + userList.get(position).getUser_name());
        }
        if (userList.get(position).getUser_email() != null && !userList.get(position).getUser_email().isEmpty()) {
            tv_email.setText(" " + userList.get(position).getUser_email());
        }
        if (userList.get(position).getPersonal_email() != null && !userList.get(position).getPersonal_email().isEmpty()) {
            tv_personal_email.setText(" " + userList.get(position).getPersonal_email());
        } else {
            tv_personal_email.setVisibility(View.GONE);
        }
        if (userList.get(position).getUser_phone() != null && !userList.get(position).getUser_phone().isEmpty()) {
            tv_call.setText(" " + userList.get(position).getUser_phone());
        } else {
            tv_call.setVisibility(View.GONE);
        }
        if (userList.get(position).getUser_designation() != null && !userList.get(position).getUser_designation().isEmpty()) {
            tv_designation.setText(" " + userList.get(position).getUser_designation());
        } else {
            tv_designation.setVisibility(View.GONE);
        }
      /*  if (userSearch.getData().get(position).getLevel()!=null && !userSearch.getData().get(position).getLevel().isEmpty()) {
            tv_level.setText(" "+userSearch.getData().get(position).getLevel());
        }*/
        if (userList.get(position).getFav().equalsIgnoreCase("1")) {
            Drawable img = getContext().getResources().getDrawable(R.mipmap.icon_colorstar);
            addtofav_tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
            addtofav_tv.setText("Added to Favourites");
        } else {
            Drawable img = getContext().getResources().getDrawable(R.mipmap.icon_blnkstar);
            addtofav_tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

            addtofav_tv.setText("Add to Favourite");
        }
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("email", userList.get(position).getUser_email());
                bundle.putString("id", userList.get(position).getUser_id());
                Fragment fragment = new FragmentCreateTask();
                fragment.setArguments(bundle);
                ((MainActivity) getContext()).replacefragment(fragment);
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addToFav(String taskid, final int pos) {

        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(true);
        if (pDialog != null) {
            pDialog.setMessage(getContext().getString(R.string.loading));
            if (!pDialog.isShowing())
                pDialog.show();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.WebUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        FragmentSearchTask.IUSERLIST1 iuserlist = retrofit.create(FragmentSearchTask.IUSERLIST1.class);
        if (sharedpreferences.getString("User_id", "") != null) {
            user_id = sharedpreferences.getString("User_id", "");
        }

        Call<ResponseBody> call = iuserlist.getData(taskid, user_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try {
                        String data = response.body().string();
                        linear_adds_layout.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.getInt("status") == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
                            tv_NORecord.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            changeDrawable();
                            userSearch.getData().get(pos).setFav("1");
                        } else {
                            Toast.makeText(getContext(), jsonObject.getString("tag") + "", Toast.LENGTH_LONG).show();
                            tv_NORecord.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            tv_NORecord.setText(jsonObject.getString("tag") + "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        });
    }

    private void changeDrawable() {
        Drawable img = getContext().getResources().getDrawable(R.mipmap.icon_colorstar);
        addtofav_tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

    }

    private void retainDrawable() {
        Drawable img = getContext().getResources().getDrawable(R.mipmap.icon_blnkstar);
        addtofav_tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
    }

    @Override
    public void onItemClick(int position, View v, List<Search> userList) {
        showDialouge(position, userList);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResponse(Call<SearchDesignation> call, Response<SearchDesignation> response) {
        searchDesignation = response.body();
        //Log.e(TAG,faqModelData.toString());
        if (pDialog.isShowing())
            pDialog.dismiss();

        if (searchDesignation != null) {
            if (Integer.parseInt(searchDesignation.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
//                linear_spinner.setVisibility(View.GONE);
                if (searchDesignation.getFunctions() != null && searchDesignation.getFunctions().size() > 0) {
                    funSpinner();
                }
                if (searchDesignation.getLevels() != null && searchDesignation.getLevels().size() > 0) {
                    levelSpinner();
                }
                if (searchDesignation.getRegions() != null && searchDesignation.getRegions().size() > 0) {
                    regionSpinner();
                }
            } else {
                linear_spinner.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFailure(Call<SearchDesignation> call, Throwable t) {
        if (pDialog.isShowing())
            pDialog.dismiss();
        Log.e("error", t + "");
    }

    public interface IFAQDATA {
        @GET(Constant.WebUrl.SHOW_MEMBER)
        Call<ShowMemberData> getData();
    }

    // adapter for the member spinner
    public class FuntionAdapter extends ArrayAdapter<SearchDesignation.Function> {
        List<SearchDesignation.Function> objects;

        public FuntionAdapter(Context ctx, int txtViewResourceId, List<SearchDesignation.Function> objects) {
            super(ctx, txtViewResourceId, objects);
            this.objects = objects;
        }

        //getting the dropdown of spinner
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getChildView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item, parent, false);
                TextView city_parent_text = (TextView) mySpinner.findViewById(R.id.tv);
                try {
                    city_parent_text.setText(objects.get(position).getUser_function());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return mySpinner;
        }

        public View getChildView(int position, View convertView, ViewGroup parent) {
            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item_edit, parent, false);
                TextView city_child_text = (TextView) mySpinner.findViewById(R.id.tv);
                city_child_text.setText(objects.get(position).getUser_function());
            }
            return mySpinner;
        }
    }

    // adapter for the member spinner
    public class LevelAdapter extends ArrayAdapter<SearchDesignation.Level> {
        List<SearchDesignation.Level> objects;

        public LevelAdapter(Context ctx, int txtViewResourceId, List<SearchDesignation.Level> objects) {

            super(ctx, txtViewResourceId, objects);
            this.objects = objects;
        }

        //getting the dropdown of spinner
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getChildView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item, parent, false);
                TextView city_parent_text = (TextView) mySpinner.findViewById(R.id.tv);
                try {
                    city_parent_text.setText(objects.get(position).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return mySpinner;
        }

        public View getChildView(int position, View convertView, ViewGroup parent) {
            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item_edit, parent, false);
                TextView city_child_text = (TextView) mySpinner.findViewById(R.id.tv);
                city_child_text.setText(objects.get(position).getName());
            }
            return mySpinner;
        }
    }

    private void funSpinner() {
        functionsList.clear();
        functionsList = new ArrayList<SearchDesignation.Function>();
        functionsList = searchDesignation.getFunctions();
        SearchDesignation.Function datafuntion = new SearchDesignation().new Function();
        datafuntion.setUser_function("Select Function");
        functionsList.add(0, datafuntion);
        funtionAdapter = new FuntionAdapter(getActivity(), R.layout.cus_spinner, functionsList);
        member_spinner.setAdapter(funtionAdapter);
        member_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    funtion = "";
                    return;
                }
                funtion = searchDesignation.getFunctions().get(position).getUser_function();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void levelSpinner() {
        levelList = new ArrayList<SearchDesignation.Level>();
        levelList = searchDesignation.getLevels();
        SearchDesignation.Level datafuntion = new SearchDesignation().new Level();
        datafuntion.setName("Select Level");
        levelList.add(0, datafuntion);
        levelAdapter = new LevelAdapter(getActivity(), R.layout.cus_spinner, levelList);
        level_spinner.setAdapter(levelAdapter);
        level_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    level_id = "";
                    return;
                }
                level_id = searchDesignation.getLevels().get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void regionSpinner() {
        regionList = new ArrayList<SearchDesignation.Region>();
        regionList = searchDesignation.getRegions();
        SearchDesignation.Region dataregion = new SearchDesignation().new Region();
        dataregion.setUser_region("Select Region");
        regionList.add(0, dataregion);
        regionAdapter = new RegionAdapter(getActivity(), R.layout.cus_spinner, regionList);
        region_spinner.setAdapter(regionAdapter);
        region_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    region = "";
                    return;
                }
                region = searchDesignation.getRegions().get(position).getUser_region();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void SubmitDataApiCall(int page) {

        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(true);
        if (pDialog != null) {
            pDialog.setMessage("Loading...");
            if (!pDialog.isShowing())
                pDialog.show();
        }

//        linear_spinner.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(180, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).build();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constant.WebUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(client).build();

        ICREATETASK icreatetask = retrofit.create(ICREATETASK.class);

        Call<UserSearch> submitTask = icreatetask.submitAllData(user_id, funtion, level_id, region, page + "");
        submitTask.enqueue(new Callback<UserSearch>() {
            @Override
            public void onResponse(Call<UserSearch> call, Response<UserSearch> response) {
                userSearch = response.body();
                //Log.e(TAG,faqModelData.toString());
                if (userSearch != null) {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    if (Integer.parseInt(userSearch.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {

                        searchResultData = userSearch.getData();
                        if (searchResultData != null && isVisible() && getContext() != null) {
                            tv_NORecord.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            Log.e(TAG + "check ", searchResultData.toString() + "");
                            for (Search coach : searchResultData)
                                adapterSearchList.addItem(coach);
                            flagsearch = false;
                            flagstop = false;
                        }
                    } else if (userSearch.getStatus().contains("0")) {

                        if (flagstop) {
                            //flagsearch=true;
                            tv_NORecord.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            tv_NORecord.setText("No Result Found");
//                        tv_NORecord.setText(userSearch.get"");
                        } else {

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<UserSearch> call, Throwable t) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        });
    }

    // adapter for the member region spinner
    public class RegionAdapter extends ArrayAdapter<SearchDesignation.Region> {
        List<SearchDesignation.Region> objects;

        public RegionAdapter(Context ctx, int txtViewResourceId, List<SearchDesignation.Region> objects) {

            super(ctx, txtViewResourceId, objects);
            this.objects = objects;
        }

        //getting the dropdown of spinner
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getChildView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item, parent, false);
                TextView city_parent_text = (TextView) mySpinner.findViewById(R.id.tv);
                try {
                    city_parent_text.setText(objects.get(position).getUser_region());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return mySpinner;
        }

        public View getChildView(int position, View convertView, ViewGroup parent) {


            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item_edit, parent, false);
                TextView city_child_text = (TextView) mySpinner.findViewById(R.id.tv);


                city_child_text.setText(objects.get(position).getUser_region());
            }
            return mySpinner;
        }

    }

    // adapter for the member pccit spinner
    public class MemberCCcitAdapter extends ArrayAdapter<String> {
        List<String> objects;

        public MemberCCcitAdapter(Context ctx, int txtViewResourceId, List<String> objects) {

            super(ctx, txtViewResourceId, objects);
            this.objects = objects;
        }

        //getting the dropdown of spinner
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getChildView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item, parent, false);
                TextView city_parent_text = (TextView) mySpinner.findViewById(R.id.tv);
                try {
                    city_parent_text.setText(objects.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (position == 0) {
//                    city_parent_text.setTextColor(getResources().getColor(R.color.edit_text_color));
//                } else {
//                    city_parent_text.setTextColor(getResources().getColor(R.color.textviewcolor));
//                }
            }
            return mySpinner;
        }

        public View getChildView(int position, View convertView, ViewGroup parent) {

            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item_edit, parent, false);
                TextView city_child_text = (TextView) mySpinner.findViewById(R.id.tv);


                city_child_text.setText(objects.get(position));
            }
            return mySpinner;
        }
    }

    // adapter for the member pccit spinner
    public class MisDateAdapter extends ArrayAdapter<MisDateData.Datum> {
        List<MisDateData.Datum> objects;

        public MisDateAdapter(Context ctx, int txtViewResourceId, List<MisDateData.Datum> objects) {

            super(ctx, txtViewResourceId, objects);
            this.objects = objects;
        }

        //getting the dropdown of spinner
        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getChildView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item, parent, false);
                TextView city_parent_text = (TextView) mySpinner.findViewById(R.id.tv);
                city_parent_text.setText(objects.get(position).getMisDate());
                /*try {
                    city_parent_text.setText(AndroidUtil.formatDate(objects.get(position).getMisDate(),"yyyy-mm-dd","dd-mm-yyyy"));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            return mySpinner;
        }

        public View getChildView(int position, View convertView, ViewGroup parent) {

            if (getActivity() != null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                mySpinner = inflater.inflate(R.layout.spinner_item_edit, parent, false);
                TextView city_child_text = (TextView) mySpinner.findViewById(R.id.tv);

                // city_child_text.setText(AndroidUtil.formatDate(objects.get(position).getMisDate(),"yyyy-mm-dd","dd-mm-yyyy"));
                city_child_text.setText(objects.get(position).getMisDate());
            }
            return mySpinner;
        }

    }


    public interface ICREATETASK {

        @GET(Constant.WebUrl.SUBMIT_DESIGNATION)
        Call<UserSearch> submitAllData(@Query("id") String id, @Query("function") String function, @Query("level") String level, @Query("region") String region, @Query("page") String page);

        @GET(Constant.WebUrl.SEARCH_DESIGNATION)
        Call<SearchDesignation> getDataDesignation(@Query("id") String id);
    }
}

