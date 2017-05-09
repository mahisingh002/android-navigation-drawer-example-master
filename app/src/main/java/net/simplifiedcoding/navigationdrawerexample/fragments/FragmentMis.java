package net.simplifiedcoding.navigationdrawerexample.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.simplifiedcoding.navigationdrawerexample.Constant.Constant;
import net.simplifiedcoding.navigationdrawerexample.Model.DataMis;
import net.simplifiedcoding.navigationdrawerexample.Model.Datum;
import net.simplifiedcoding.navigationdrawerexample.Model.FaqCommon;
import net.simplifiedcoding.navigationdrawerexample.Model.MisDateData;
import net.simplifiedcoding.navigationdrawerexample.Model.MisReportData;
import net.simplifiedcoding.navigationdrawerexample.Model.ShowMemberData;
import net.simplifiedcoding.navigationdrawerexample.Model.ShowPccitData;
import net.simplifiedcoding.navigationdrawerexample.Model.ShowdialogMis;
import net.simplifiedcoding.navigationdrawerexample.R;
import net.simplifiedcoding.navigationdrawerexample.activities.MainActivity;
//import net.simplifiedcoding.navigationdrawerexample.adapter.AdapterMisDetail;
import net.simplifiedcoding.navigationdrawerexample.adapter.AdapterDialogMis;
import net.simplifiedcoding.navigationdrawerexample.util.AndroidUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FragmentMis extends Fragment implements Callback<ShowMemberData>, View.OnClickListener {

    private static final String TAG = FragmentMis.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ShowMemberData showMemberData;
    private ProgressDialog pDialog;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String user_id, date;
    TextView no_task_tv;
    TextView select_memtv;
    View mySpinner;
    Spinner member_spinner, pr_ccit_spinner, ccit_spinner, cit_spinner, misdate_spinner, spinner_mistype, sp_verify;
    MemberAdapter memberAdapter;
    CitAdapter citAdapter;
    MemberCCcitAdapter memberccitAdapter;
    MemberPccitAdapter memberPccitAdapter;
    MisDateAdapter misDateAdapter;
    ShowPccitData showPccitData;
    private Call<ShowPccitData> submitTask = null;
    String member, pccit_member, ccit_member, cit_member, misdate;

    TextView go_btn, members_tv, date_tv, pr_ccit_tv, ccit_tv, cit_tv;
    RelativeLayout relative_mis;
    LinearLayout linear_adds_layout;
    List<ShowMemberData.Datum> datumemberList = new ArrayList<ShowMemberData.Datum>();
    List<ShowMemberData.Datum> citList = new ArrayList<ShowMemberData.Datum>();
    List<MisDateData.Datum> misdateDataList = new ArrayList<MisDateData.Datum>();
    AdapterMisDetail adapterMisDetail;
    List<String> prCitlistdata = new ArrayList<String>();
    List<String> CCitlistdata = new ArrayList<String>();
    LinearLayout header_layout;
    private int year;
    private int month;
    private int day;
    boolean clickfalg = true;
    String textPrint;
    String str_member, Str_date, str_pccit_member, str_cit_member, str_ccit_member, str_id;

    public FragmentMis() {
        // Required empty public constructor
    }


    public static FragmentMis newInstance() {
        FragmentMis fragment = new FragmentMis();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mis_filter, container, false);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        initView(rootView);

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
        pr_ccit_spinner = (Spinner) view.findViewById(R.id.pr_ccit_spinner);
        ccit_spinner = (Spinner) view.findViewById(R.id.ccit_spinner);
        cit_spinner = (Spinner) view.findViewById(R.id.cit_spinner);
        misdate_spinner = (Spinner) view.findViewById(R.id.misdate_spinner);

        sp_verify = (Spinner) view.findViewById(R.id.verify_spinner);

        go_btn = (TextView) view.findViewById(R.id.go_btn);
        linear_adds_layout = (LinearLayout) view.findViewById(R.id.linear_adds_layout);
        relative_mis = (RelativeLayout) view.findViewById(R.id.relative_mis);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_mis_detail);
        header_layout = (LinearLayout) view.findViewById(R.id.header_layout);
        select_memtv = (TextView) view.findViewById(R.id.select_memtv);
        members_tv = (TextView) view.findViewById(R.id.members_tv);
        pr_ccit_tv = (TextView) view.findViewById(R.id.pr_ccit_tv);
        ccit_tv = (TextView) view.findViewById(R.id.ccit_tv);
        cit_tv = (TextView) view.findViewById(R.id.cit_tv);
        date_tv = (TextView) view.findViewById(R.id.date_tv);
//        tvTitleName.setText("FAQs");
        header_layout.setVisibility(View.GONE);
        if (AndroidUtil.isConnectingToInternet(getActivity())) {
            getNewTaskServerData();
            getmisDateData();
        } else {
            Toast.makeText(getActivity(), Constant.check_internet_connection, Toast.LENGTH_LONG).show();
        }

        go_btn.setOnClickListener(this);
        relative_mis.setOnClickListener(this);
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
        IFAQDATA ifaqdata = retrofit.create(IFAQDATA.class);
        if (sharedpreferences.getString("User_id", "") != null) {
            user_id = sharedpreferences.getString("User_id", "");
        }
        Call<ShowMemberData> call = ifaqdata.getData();
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
                if (linear_adds_layout.getVisibility() == View.VISIBLE) {
                    linear_adds_layout.setVisibility(View.GONE);
                    select_memtv.setVisibility(View.GONE);
                }
                if (AndroidUtil.isConnectingToInternet(getActivity())) {
                    SubmitDataApiCall();
                } else {
                    Toast.makeText(getActivity(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.relative_mis:
                if (linear_adds_layout.getVisibility() == View.GONE) {
                    linear_adds_layout.setVisibility(View.VISIBLE);
                    select_memtv.setVisibility(View.VISIBLE);
                } else if (linear_adds_layout.getVisibility() == View.VISIBLE) {
                    linear_adds_layout.setVisibility(View.GONE);
                    select_memtv.setVisibility(View.GONE);
                }
                break;
//            case R.id.date_filter_tv:
//                try {
//                    newFragment = new SelectDateFragment();
//                    //                newFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
//                    newFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.clear_date_tv:
//                date_filter_tv.setText("Date Filter");
//                break;
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onResponse(Call<ShowMemberData> call, Response<ShowMemberData> response) {
        showMemberData = response.body();
        //Log.e(TAG,faqModelData.toString());
        if (pDialog.isShowing())
            pDialog.dismiss();

        if (showMemberData != null) {

            if (Integer.parseInt(showMemberData.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
                if (showMemberData.getData() != null && showMemberData.getData().size() > 0) {
                    datumemberList = new ArrayList<ShowMemberData.Datum>();
                    datumemberList = showMemberData.getData();
                    ShowMemberData.Datum datumember = new ShowMemberData().new Datum();
                    datumember.setMember("All");
                    datumemberList.add(0, datumember);
                    memberAdapter = new MemberAdapter(getActivity(), R.layout.cus_spinner, datumemberList);
                    member_spinner.setAdapter(memberAdapter);
                }

                member_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            member = "";
                            resetPrccit();
                            resetccit();
                            resetcit();
                            return;
                        }
                        member = showMemberData.getData().get(position).getMember();
                        if (AndroidUtil.isConnectingToInternet(getActivity())) {
                            PccitTaskApiCall();
                        } else {
                            Toast.makeText(getActivity(), "Please check your Internet connection and try again", Toast.LENGTH_LONG).show();
                        }

//                        areaIdSelected = "" + areadata.get(position).getAreaId();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

        }
    }


    @Override
    public void onFailure(Call<ShowMemberData> call, Throwable t) {
        if (pDialog.isShowing())
            pDialog.dismiss();
        Log.e("error", t + "");
    }

    public interface IFAQDATA {
        @GET(Constant.WebUrl.SHOW_MEMBER)
        Call<ShowMemberData> getData();
    }


    private void getmisDateData() {
        final ProgressDialog pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
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
        ICREATETASK ifaqdata = retrofit.create(ICREATETASK.class);
        if (sharedpreferences.getString("User_id", "") != null) {
            user_id = sharedpreferences.getString("User_id", "");
        }
        Call<MisDateData> call = ifaqdata.submitMisDate();
        call.enqueue(new Callback<MisDateData>() {
            @Override
            public void onResponse(Call<MisDateData> call, Response<MisDateData> response) {
                if (pDialog.isShowing())
                    pDialog.dismiss();

                final MisDateData misDateData = response.body();
                if (misDateData != null) {

                    if (Integer.parseInt(misDateData.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
                        if (misDateData.getData() != null && misDateData.getData().size() > 0) {
                            misdateDataList = new ArrayList<MisDateData.Datum>();
                            misdateDataList = misDateData.getData();
                            MisDateData.Datum datumember = new MisDateData().new Datum();
                            datumember.setMisDate("Select Date");
                            misdateDataList.add(0, datumember);
                            misDateAdapter = new MisDateAdapter(getActivity(), R.layout.cus_spinner, misdateDataList);
                            misdate_spinner.setAdapter(misDateAdapter);
                        }

                        misdate_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    misdate = "";
                                    return;
                                }
                               /* misdate = misdate_spinner.getSelectedItem().toString();
                                Log.e("check spinner value",misdate);*/
                                misdate = misdateDataList.get(position).getMisDate();
                                date = misdate;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {


                            }
                        });

                    }
                }

            }

            @Override
            public void onFailure(Call<MisDateData> call, Throwable t) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        });

    }


    // adapter for the member spinner
    public class MemberAdapter extends ArrayAdapter<ShowMemberData.Datum> {
        List<ShowMemberData.Datum> objects;

        public MemberAdapter(Context ctx, int txtViewResourceId, List<ShowMemberData.Datum> objects) {

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
                    city_parent_text.setText(objects.get(position).getMember());
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


                city_child_text.setText(objects.get(position).getMember());
            }
            return mySpinner;
        }

    }

    // adapter for the member spinner
    public class CitAdapter extends ArrayAdapter<ShowMemberData.Datum> {
        List<ShowMemberData.Datum> objects;

        public CitAdapter(Context ctx, int txtViewResourceId, List<ShowMemberData.Datum> objects) {

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
                    city_parent_text.setText(objects.get(position).getMember());
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


                city_child_text.setText(objects.get(position).getMember());
            }
            return mySpinner;
        }

    }

    private void PccitTaskApiCall() {

        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(false);
        if (pDialog != null) {
            pDialog.setMessage("Updating...");
            if (!pDialog.isShowing())
                pDialog.show();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.WebUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient().newBuilder().writeTimeout(1, TimeUnit.MINUTES).connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).build())
                .build();
        ICREATETASK icreatetask = retrofit.create(ICREATETASK.class);


        Call<ShowPccitData> submitTask = icreatetask.submitTask(member, "");

        submitTask.enqueue(new Callback<ShowPccitData>() {
            @Override
            public void onResponse(Call<ShowPccitData> call, Response<ShowPccitData> response) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                try {
                    showPccitData = response.body();
                    if (showPccitData != null) {

                        if (Integer.parseInt(showPccitData.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
//                            resetPrccit();
                            prCitlistdata = showPccitData.getData();
//                            if (listdata.size() > 0) {
                            prCitlistdata.add(0, "Select Pr.CCIT");


                            memberPccitAdapter = new MemberPccitAdapter(getActivity(), R.layout.cus_spinner, prCitlistdata);
                            pr_ccit_spinner.setAdapter(memberPccitAdapter);
                            pr_ccit_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position == 0) {
                                        pccit_member = "";
                                        resetccit();
                                        resetcit();
                                        return;
                                    }
                                    pccit_member = showPccitData.getData().get(position);
                                    if (AndroidUtil.isConnectingToInternet(getActivity())) {
                                        CcitTaskApiCall();
                                    } else {
                                        Toast.makeText(getActivity(), "Please check your Internet connection and try again", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ShowPccitData> call, Throwable t) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        });
    }

    void resetMember() {
        datumemberList = new ArrayList<ShowMemberData.Datum>();
        ShowMemberData.Datum datumember = new ShowMemberData().new Datum();
        datumember.setMember("All");
        datumemberList.add(0, datumember);
        memberAdapter = new MemberAdapter(getActivity(), R.layout.cus_spinner, datumemberList);
        member_spinner.setAdapter(memberAdapter);

    }

    private void resetPrccit() {
        prCitlistdata.clear();
        prCitlistdata.add(0, "Select Pr.CCIT");
        memberPccitAdapter = new MemberPccitAdapter(getActivity(), R.layout.cus_spinner, prCitlistdata);
        pr_ccit_spinner.setAdapter(memberPccitAdapter);
    }

    private void resetccit() {
//
        CCitlistdata.clear();
        CCitlistdata.add(0, "Select CCIT");
        memberccitAdapter = new MemberCCcitAdapter(getActivity(), R.layout.cus_spinner, CCitlistdata);
        ccit_spinner.setAdapter(memberccitAdapter);
    }

    void resetcit() {
        citList = new ArrayList<ShowMemberData.Datum>();
        ShowMemberData.Datum datumember = new ShowMemberData().new Datum();
        datumember.setMember("Select CIT");
        citList.add(0, datumember);
        citAdapter = new CitAdapter(getActivity(), R.layout.cus_spinner, citList);
        cit_spinner.setAdapter(citAdapter);

    }

    private void CcitTaskApiCall() {

        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(false);
        if (pDialog != null) {
            pDialog.setMessage("Updating...");
            if (!pDialog.isShowing())
                pDialog.show();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.WebUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient().newBuilder().writeTimeout(2, TimeUnit.MINUTES).connectTimeout(2, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).build())
                .build();
        ICREATETASK icreatetask = retrofit.create(ICREATETASK.class);


        Call<ShowPccitData> submitTask = icreatetask.submitCcitTask(member, pccit_member);

        submitTask.enqueue(new Callback<ShowPccitData>() {
            @Override
            public void onResponse(Call<ShowPccitData> call, Response<ShowPccitData> response) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                try {
                    final ShowPccitData ccitData = response.body();
                    if (showPccitData != null) {

                        if (Integer.parseInt(ccitData.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {

                            List<ShowPccitData> datumemberList = new ArrayList<ShowPccitData>();
                            List<String> listdata = new ArrayList<String>();
                            listdata = ccitData.getData();
                            listdata.add(0, "Select CCIT");


                            MemberCCcitAdapter memberPccitAdapter = new MemberCCcitAdapter(getActivity(), R.layout.cus_spinner, listdata);
                            ccit_spinner.setAdapter(memberPccitAdapter);
                            ccit_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position == 0) {
                                        ccit_member = "";
                                        resetcit();
                                        return;
                                    }
                                    ccit_member = ccitData.getData().get(position);
                                    if (AndroidUtil.isConnectingToInternet(getActivity())) {
                                        CitTaskApiCall();
                                    } else {
                                        Toast.makeText(getActivity(), "Please check your Internet connection and try again", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });

                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ShowPccitData> call, Throwable t) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }
        });
    }

    private void CitTaskApiCall() {

        final ProgressDialog pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(false);
        if (pDialog != null) {
            pDialog.setMessage("Updating...");
            if (!pDialog.isShowing())
                pDialog.show();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.WebUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient().newBuilder().writeTimeout(2, TimeUnit.MINUTES).connectTimeout(2, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES).build())
                .build();
        ICREATETASK icreatetask = retrofit.create(ICREATETASK.class);


        Call<ShowMemberData> submitTask = icreatetask.submitCitTask(member, pccit_member, ccit_member);

        submitTask.enqueue(new Callback<ShowMemberData>() {
            @Override
            public void onResponse(Call<ShowMemberData> call, Response<ShowMemberData> response) {


                if (pDialog != null) {
                    pDialog.dismiss();
                }
                try {
                    final ShowMemberData showMemberData = response.body();

                    if (showMemberData != null) {

                        if (Integer.parseInt(showMemberData.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
                            citList = showMemberData.getData();
                            ShowMemberData.Datum datumember = new ShowMemberData().new Datum();
                            datumember.setMember("Select CIT");
                            citList.add(0, datumember);
//


                            CitAdapter memberPccitAdapter = new CitAdapter(getActivity(), R.layout.cus_spinner, citList);
                            cit_spinner.setAdapter(memberPccitAdapter);
                            cit_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (position == 0) {
                                        cit_member = "";
                                        return;
                                    }
                                    cit_member = citList.get(position).getMember();


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {


                                }
                            });

                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(Call<ShowMemberData> call, Throwable t) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }

            }
        });
    }


    private void SubmitDataApiCall() {

        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(false);
        if (pDialog != null) {
            pDialog.setMessage("Loading...");
            if (!pDialog.isShowing())
                pDialog.show();
        }
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(180, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).build();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constant.WebUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(client).build();


        ICREATETASK icreatetask = retrofit.create(ICREATETASK.class);


        Call<MisReportData> submitTask = icreatetask.submitAllData(date, member, cit_member, pccit_member, ccit_member);

        submitTask.enqueue(new Callback<MisReportData>() {
            @Override
            public void onResponse(Call<MisReportData> call, Response<MisReportData> response) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                try {
                    final MisReportData misReportData = response.body();
                    if (misReportData != null) {

                        if (Integer.parseInt(misReportData.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {

                            List<Datum> list = misReportData.getData();

                            Str_date = null;
                            str_member = null;
                            str_pccit_member = null;
                            str_ccit_member = null;
                            str_cit_member = null;

                            if (list != null && list.size() > 0) {
                                header_layout.setVisibility(View.VISIBLE);
                                if (misReportData.getDate() != null && !misReportData.getDate().isEmpty()) {
                                    date_tv.setVisibility(View.VISIBLE);
                                    date_tv.setText("Date: " + misReportData.getDate());
                                    Str_date = misReportData.getDate();


                                } else {
                                    date_tv.setVisibility(View.GONE);
                                }
                                if (misReportData.getClickable().equalsIgnoreCase("1") && !misReportData.getClickable().equalsIgnoreCase("0")) {
                                    clickfalg = true;
                                } else {
                                    clickfalg = false;
                                }
                                if (member != null && !member.isEmpty()) {
                                    members_tv.setVisibility(View.VISIBLE);
                                    // members_tv.setText("Members: " + member);
                                    str_member = member;
                                } else {
                                    members_tv.setVisibility(View.GONE);
                                }
                                if (pccit_member != null && !pccit_member.isEmpty()) {
                                    pr_ccit_tv.setVisibility(View.GONE);
                                    // pr_ccit_tv.setText("Pr.CCIT: " + pccit_member);

                                    str_pccit_member = pccit_member;

                                } else {
                                    pr_ccit_tv.setVisibility(View.GONE);
                                }
                                if (ccit_member != null && !ccit_member.isEmpty()) {
                                    ccit_tv.setVisibility(View.GONE);
                                    // ccit_tv.setText("CCIT: " + ccit_member);
                                    str_ccit_member = ccit_member;
                                } else {
                                    ccit_tv.setVisibility(View.GONE);
                                }
                                if (cit_member != null && !cit_member.isEmpty()) {
                                    cit_tv.setVisibility(View.GONE);
                                    // cit_tv.setText("CIT: " + cit_member);
                                    str_cit_member = cit_member;
                                } else {
                                    cit_tv.setVisibility(View.GONE);
                                }
                                if (Str_date != null) {
                                    if (str_member != null) {
                                        if (str_pccit_member != null) {
                                            if (str_ccit_member != null) {
                                                if (str_cit_member != null) {
                                                    members_tv.setText(str_member + " >> " + str_pccit_member + " >> " + str_ccit_member + ">>" + str_cit_member);
                                                } else {
                                                    members_tv.setText(str_member + " >> " + str_pccit_member + " >> " + str_ccit_member);
                                                }
                                            } else {
                                                members_tv.setText(str_member + " >> " + str_pccit_member);
                                            }
                                        } else {
                                            members_tv.setText(str_member);
                                        }

                                    } else {
                                        // members_tv.setText(Str_date);
                                    }
                                }

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                layoutManager.setSmoothScrollbarEnabled(true);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());

                                adapterMisDetail = new AdapterMisDetail(getContext(), list);
                                recyclerView.setAdapter(adapterMisDetail);
                            } else {
                            }
                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(Call<MisReportData> call, Throwable t) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }

            }
        });
    }

    // adapter for the member pccit spinner
    public class MemberPccitAdapter extends ArrayAdapter<String> {
        List<String> objects;

        public MemberPccitAdapter(Context ctx, int txtViewResourceId, List<String> objects) {

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

                city_child_text.setText(objects.get(position).getMisDate());
            }
            return mySpinner;
        }
    }


    public interface ICREATETASK {
        @GET(Constant.WebUrl.SHOW_PRCCIT)
        Call<ShowPccitData> submitTask(@Query("member") String member, @Query("PrCCITOffice") String PrCCITOffice);

        @GET(Constant.WebUrl.SHOW_CCIT)
        Call<ShowPccitData> submitCcitTask(@Query("member") String member, @Query("prccit") String prccit);

        @GET(Constant.WebUrl.SHOW_CIT)
        Call<ShowMemberData> submitCitTask(@Query("member") String member, @Query("prccit") String prccit, @Query("ccit") String ccit);

        @GET(Constant.WebUrl.SHOW_MISREPORT)
        Call<MisReportData> submitAllData(@Query("date") String date, @Query("member") String member, @Query("cit") String cit, @Query("prccit") String prccit, @Query("ccit") String ccit);

        @GET(Constant.WebUrl.SHOW_MISDATE)
        Call<MisDateData> submitMisDate();

        @GET(Constant.WebUrl.DIALOG_TASK)
        Call<ShowdialogMis> dialogTask(@Query("date") String date, @Query("id") String id, @Query("member") String member, @Query("prccit") String prccit, @Query("ccit") String ccit);

    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), this, yy, mm, dd);

            mDatePicker.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationEditProfile;

            return mDatePicker;
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }

        public void populateSetDate(int year, int month, int day) {
        }
    }


    public class AdapterMisDetail extends RecyclerView.Adapter<AdapterMisDetail.MyViewHolder> {

        private Context context;
        private ArrayList<FaqCommon> faqModelsList;
        List<Datum> data;
        String status;

        public AdapterMisDetail(Context context, List<Datum> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mis_detail_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            if (data.get(position).getName() != null && !data.get(position).getName().isEmpty()) {
                holder.mis_name_tv.setText(data.get(position).getName());
            }
            if (data.get(position).getVal() != null && !data.get(position).getVal().isEmpty()) {
                String value = AndroidUtil.formatDoubleData(Double.valueOf(data.get(position).getVal()));
                holder.mis_value_tv.setText(value);
            }

            if (clickfalg == true) {
                holder.mis_name_tv.setPaintFlags(holder.mis_name_tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.mis_name_tv.setTextColor(getResources().getColor(R.color.blue));

            }

            holder.ChildViewclick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickfalg) {
                        str_id = data.get(position).getId();
                        pDialog = new ProgressDialog(context, R.style.DialogTheme);
                        pDialog.setCancelable(false);
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
                        dialog.setContentView(R.layout.customdialog);
//                        dialog.setTitle("Title...");
                        // set the custom dialog components - text, image and button
                        final RecyclerView recyclerview_DialogMIs = (RecyclerView) dialog.findViewById(R.id.recyclerview_DialogMIs);
                        final ProgressBar misDialogProgress = (ProgressBar) dialog.findViewById(R.id.misDialogProgress);
                        final TextView tv_misDialong = (TextView) dialog.findViewById(R.id.tv_misDialong);
                        final ArrayList<DataMis> arrayListMIs = new ArrayList<>();

                        final ImageView dialogButton = (ImageView) dialog.findViewById(R.id.btn_click);
                        final TextView tv_NORecord = (TextView) dialog.findViewById(R.id.tv_NORecord);

                        final OkHttpClient[] client = {new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().
                                setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(180, TimeUnit.SECONDS).
                                connectTimeout(120, TimeUnit.SECONDS).build()};
                        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constant.WebUrl.BASE_URL).
                                addConverterFactory(GsonConverterFactory.create());
                        Retrofit retrofitDialog = builder.client(client[0]).build();

                        ICREATETASK dialogValue = retrofitDialog.create(ICREATETASK.class);

                        pDialog = new ProgressDialog(context, R.style.DialogTheme);
                        pDialog.setCancelable(true);
                        misDialogProgress.setVisibility(View.VISIBLE);
                        recyclerview_DialogMIs.setVisibility(View.GONE);
                        tv_NORecord.setVisibility(View.GONE);

                        if (pDialog != null) {
                            Call<ShowdialogMis> submitTask = dialogValue.dialogTask(Str_date, str_id, str_member,
                                    str_pccit_member, str_ccit_member);
                            submitTask.enqueue(
                                    new Callback<ShowdialogMis>() {
                                        @Override
                                        public void onResponse(Call<ShowdialogMis> call, Response<ShowdialogMis> response) {

                                            if (pDialog != null) {
                                                pDialog.dismiss();
                                            }

                                            misDialogProgress.setVisibility(View.GONE);
                                            recyclerview_DialogMIs.setVisibility(View.VISIBLE);
                                            tv_NORecord.setVisibility(View.GONE);

                                            ShowdialogMis showdialogMis = response.body();
                                            for (DataMis dataMis : showdialogMis.getData())
                                                arrayListMIs.add(dataMis);

                                            String strTitle = showdialogMis.getName();
                                            tv_misDialong.setText(strTitle);
                                            AdapterDialogMis adapterDialogMis = new AdapterDialogMis(context, arrayListMIs);
                                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                            recyclerview_DialogMIs.setLayoutManager(mLayoutManager);
                                            recyclerview_DialogMIs.setItemAnimator(new DefaultItemAnimator());
                                            recyclerview_DialogMIs.setAdapter(adapterDialogMis);
                                            adapterDialogMis.notifyDataSetChanged();

                                            if (pDialog != null) {
                                                pDialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onFailure
                                                (Call<ShowdialogMis> call, Throwable t) {
                                            if (pDialog != null) {
                                                pDialog.dismiss();
                                            }

                                            misDialogProgress.setVisibility(View.GONE);
                                            recyclerview_DialogMIs.setVisibility(View.GONE);
                                            tv_NORecord.setVisibility(View.VISIBLE);
                                        }
                                    });
                        }
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView mis_name_tv, mis_value_tv;
            LinearLayout ChildViewclick;

            public MyViewHolder(View v) {
                super(v);
                mis_name_tv = (TextView) v.findViewById(R.id.mis_name_tv);
                mis_value_tv = (TextView) v.findViewById(R.id.mis_value_tv);
                ChildViewclick = (LinearLayout) v.findViewById(R.id.id_mis_child);
            }
        }
    }

   /* private ArrayList<ShowdialogMis> openDataDialog_MisValue() {
        ArrayList<ShowdialogMis> dataMisArrayList = new ArrayList<>();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).readTimeout(180, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).build();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constant.WebUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofitDialog = builder.client(client).build();

        ICREATETASK dialogValue = retrofitDialog.create(ICREATETASK.class);

        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(true);
        if (pDialog != null) {
            pDialog.setMessage("Please Wait...");
            if (!pDialog.isShowing())
                pDialog.show();

            Call<ShowdialogMis> submitTask = dialogValue.dialogTask(Str_date, str_id, str_member, str_pccit_member);
            final ArrayList<ShowdialogMis> finalDataMisArrayList = dataMisArrayList;
            submitTask.enqueue(
                    new Callback<ShowdialogMis>() {
                        @Override
                        public void onResponse(Call<ShowdialogMis> call, Response<ShowdialogMis> response) {
                            if (pDialog != null) {
                                pDialog.dismiss();
                            }
                            ShowdialogMis showdialogMis = response.body();
                            finalDataMisArrayList.add(showdialogMis);
                            showdialogMis.getData();
                        }

                        @Override
                        public void onFailure(Call<ShowdialogMis> call, Throwable t) {
                            if (pDialog != null) {
                                pDialog.dismiss();
                            }
                        }
                    });
        }
        return dataMisArrayList;
    }
*/

}