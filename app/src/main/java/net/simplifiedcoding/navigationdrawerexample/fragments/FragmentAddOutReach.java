package net.simplifiedcoding.navigationdrawerexample.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import net.simplifiedcoding.navigationdrawerexample.Constant.Constant;
import net.simplifiedcoding.navigationdrawerexample.Model.NewTaskData;
import net.simplifiedcoding.navigationdrawerexample.Model.OutReach;
import net.simplifiedcoding.navigationdrawerexample.Model.OutReachData;
import net.simplifiedcoding.navigationdrawerexample.Model.SearchDesignation;
import net.simplifiedcoding.navigationdrawerexample.R;
import net.simplifiedcoding.navigationdrawerexample.activities.MainActivity;
import net.simplifiedcoding.navigationdrawerexample.util.AndroidUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class FragmentAddOutReach extends Fragment implements View.OnClickListener, Callback<OutReach> {
    View view;
    LinearLayout image_layout;
    AppCompatEditText officer_name_ed, designation_ed, venue_ed, topic, et_pincode, activities_ed, et_participants;
    TextView task_name_tv;
    private Spinner spn_audience_ed, spn_state;
    static TextView date_meeting_ed;
    AppCompatImageView date_meeting_iv;
    AppCompatButton btn_choose_iv, btn_submit;
    ProgressDialog pDialog;
    ImageView cross_image1, cross_image2, cross_image3, cross_image4, cross_image5, product_image5, product_image1, product_image2, product_image3, product_image4;
    RelativeLayout image1_layout, image2_layout, image3_layout, image4_layout, image5_layout;
    File image_file, document_file;
    ArrayList<File> document_list = new ArrayList<File>();
    HashMap<Integer, File> image_file_hashmap = new HashMap<Integer, File>();
    private int lastmapkey, mapkey, first_image = 0, second_image = 0, third_image = 0, fourth_image = 0, fifth_image = 0;
    private static final int PICK_DOCUMENT_FROM_CAMERA = 1;
    private static final int PICK_DOCUMENT_FROM_GALLERY = 2;
    private static final int CROP_FROM_CAMERA = 3;
    private Uri imageUri;
    String path, realPath, user_id;
    DialogFragment newFragment;
    HashMap<Integer, Uri> uri_hashmap = new HashMap<Integer, Uri>();
    SharedPreferences sharedpreferences;
    private int year;
    private int month;
    private int day;
    private String frag_edit_outreach, officer_name, date_of_meeting, venue, outreach_id, bundel_state,
            pincode, participants, activities, audiance, bundel_topic;
    private int audiance_id, state_id;
    private List<OutReachData.Img> image;
    private ArrayList<String> deleteimagelist = new ArrayList<String>();
    private List<OutReach.targetaudience> audList = new ArrayList<OutReach.targetaudience>();
    private List<OutReach.state> stateList = new ArrayList<OutReach.state>();
    private OutReach outReach;
    private View mySpinner;
    private AudiansAdapter audiansAdapter;
    private StateAdapter stateAdapter;
    private String audians, state;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_outreachactivity, container, false);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static FragmentAddOutReach newInstance() {
        FragmentAddOutReach fragment = new FragmentAddOutReach();


        return fragment;
    }

    void initView(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        activities_ed.clearFocus();
                        date_meeting_ed.clearFocus();
//                        designation_ed.clearFocus();
                        officer_name_ed.clearFocus();
                        spn_audience_ed.clearFocus();
                        venue_ed.clearFocus();
                        getFragmentManager().popBackStackImmediate();
                        return true;
                    }
                }
                return false;
            }
        });


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
        pDialog.setCancelable(true);
        officer_name_ed = (AppCompatEditText) view.findViewById(R.id.officer_name_ed);
        // designation_ed = (AppCompatEditText) view.findViewById(R.id.designation_ed);
        date_meeting_ed = (TextView) view.findViewById(R.id.date_meeting_ed);
        spn_audience_ed = (Spinner) view.findViewById(R.id.spn_audience_ed);
        spn_state = (Spinner) view.findViewById(R.id.spn_state);
        venue_ed = (AppCompatEditText) view.findViewById(R.id.venue_ed);
        ///////////////////////////////////////////////////////////////////////////
        topic = (AppCompatEditText) view.findViewById(R.id.topic);
        et_pincode = (AppCompatEditText) view.findViewById(R.id.et_pincode);
        et_participants = (AppCompatEditText) view.findViewById(R.id.et_participants);
        activities_ed = (AppCompatEditText) view.findViewById(R.id.activities_ed);


        btn_choose_iv = (AppCompatButton) view.findViewById(R.id.btn_choose_iv);
        btn_submit = (AppCompatButton) view.findViewById(R.id.btn_submit);
        date_meeting_iv = (AppCompatImageView) view.findViewById(R.id.date_meeting_iv);
        task_name_tv = (TextView) view.findViewById(R.id.task_name_tv);

        cross_image1 = (ImageView) view.findViewById(R.id.cross_image1);
        cross_image2 = (ImageView) view.findViewById(R.id.cross_image2);
        cross_image3 = (ImageView) view.findViewById(R.id.cross_image3);
        cross_image4 = (ImageView) view.findViewById(R.id.cross_image4);
        cross_image5 = (ImageView) view.findViewById(R.id.cross_image5);

        product_image1 = (ImageView) view.findViewById(R.id.product_image1);
        product_image2 = (ImageView) view.findViewById(R.id.product_image2);
        product_image3 = (ImageView) view.findViewById(R.id.product_image3);
        product_image4 = (ImageView) view.findViewById(R.id.product_image4);
        product_image5 = (ImageView) view.findViewById(R.id.product_image5);

        image1_layout = (RelativeLayout) view.findViewById(R.id.image1_layout);
        image2_layout = (RelativeLayout) view.findViewById(R.id.image2_layout);
        image3_layout = (RelativeLayout) view.findViewById(R.id.image3_layout);
        image4_layout = (RelativeLayout) view.findViewById(R.id.image4_layout);
        image5_layout = (RelativeLayout) view.findViewById(R.id.image5_layout);


        task_name_tv.setText(getString(R.string.out_reach_activity));
        Bundle bundle = getArguments();
        if (bundle != null) {
            frag_edit_outreach = bundle.getString("frag");
            officer_name = bundle.getString("officer_name");
            //designation = bundle.getString("designation");
            date_of_meeting = bundle.getString("date_of_meeting");
            venue = bundle.getString("venue");
            bundel_topic = bundle.getString("topic");
            outreach_id = bundle.getString("outreach_id");
            image = bundle.getParcelableArrayList("image");
            participants = bundle.getString("participants");
            pincode = bundle.getString("pincode");
            bundel_state = bundle.getString("state");
            activities = bundle.getString("activities");
            audiance = bundle.getString("audiance");
            state_id = bundle.getInt("state_id");
            audiance_id = bundle.getInt("audiance_id");
        }

        btn_choose_iv.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        date_meeting_iv.setOnClickListener(this);


        if (frag_edit_outreach != null && frag_edit_outreach.equalsIgnoreCase("frag_edit_outreach")) {
            if (officer_name != null && !officer_name.isEmpty()) {
                officer_name_ed.setText(officer_name);
            }
           /* if (designation != null && !designation.isEmpty()) {
                designation_ed.setText(designation);
            }*/
            if (date_of_meeting != null && !date_of_meeting.isEmpty()) {
                date_meeting_ed.setText(AndroidUtil.formatDate(date_of_meeting, "yyyy-mm-dd", "dd-mm-yyyy"));
            }
            if (venue != null && !venue.isEmpty()) {
                venue_ed.setText(venue);
            }
            if (participants != null && !participants.isEmpty()) {
                et_participants.setText(participants);
            }
            if (pincode != null && !pincode.isEmpty()) {
                et_pincode.setText(pincode);
            }
            if (activities != null && !activities.isEmpty()) {
                activities_ed.setText(activities);
            }
            if (bundel_topic != null && !bundel_topic.isEmpty()) {
                topic.setText(bundel_topic);
            }

            /*if (target_audience != null && !target_audience.isEmpty()) {
                //  audience_ed.setText(target_audience);
            }*/
            if (image != null && image.size() > 0) {
//                Picasso.with(this)
//                        .load("")
//                        .into(imageView);
                setImage();
            }


        } else {
            if (sharedpreferences.getString("name", "") != null) {
                officer_name_ed.setText(sharedpreferences.getString("name", ""));
            }
//            if (sharedpreferences.getString("designation", "") != null) {
//                designation_ed.setText(sharedpreferences.getString("designation", ""));
//            }
        }


        final Calendar cal = Calendar.getInstance();

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        setcrossClickListener();
        getDataStateAudiance();
    }

    private void setImage() {

        if (image.size() > 0 && image.get(0).getFile() != null && !image.get(0).getFile().equalsIgnoreCase("")) {
            if (image1_layout.getVisibility() == View.GONE) {
                image1_layout.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(image.get(0).getFile())
                        .into(product_image1);
//
            }
        } else {
            image1_layout.setVisibility(View.GONE);
        }

        if (image.size() > 1 && image.get(1).getFile() != null && !image.get(1).getFile().equalsIgnoreCase("")) {
            if (image2_layout.getVisibility() == View.GONE) {
                image2_layout.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(image.get(1).getFile())
                        .into(product_image2);
            }
        } else {
            image2_layout.setVisibility(View.GONE);
        }
        if (image.size() > 2 && image.get(2).getFile() != null && !image.get(2).getFile().equalsIgnoreCase("")) {
            if (image3_layout.getVisibility() == View.GONE) {
                image3_layout.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(image.get(2).getFile())
                        .into(product_image3);
            }
        } else {
            image3_layout.setVisibility(View.GONE);
        }
        if (image.size() > 3 && image.get(3).getFile() != null && !image.get(3).getFile().equalsIgnoreCase("")) {
            if (image4_layout.getVisibility() == View.GONE) {
                image4_layout.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(image.get(3).getFile())
                        .into(product_image4);
            }
        } else {
            image4_layout.setVisibility(View.GONE);
        }

        if (image.size() > 4 && image.get(4).getFile() != null && !image.get(4).getFile().equalsIgnoreCase("")) {
            if (image5_layout.getVisibility() == View.GONE) {
                image5_layout.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(image.get(4).getFile())
                        .into(product_image5);
            }
        } else {
            image5_layout.setVisibility(View.GONE);
        }


    }

    private void setcrossClickListener() {
        cross_image1.setOnClickListener(this);
        cross_image2.setOnClickListener(this);
        cross_image3.setOnClickListener(this);
        cross_image4.setOnClickListener(this);
        cross_image5.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_iv:
                if (image_file_hashmap != null && image_file_hashmap.size() < 5) {
                    if (!(view.findViewById(R.id.footer_dialog).getVisibility() == View.VISIBLE)) {
                        initFooter(view.findViewById(R.id.footer_dialog), "document");
                    }
                } else {
                    Toast.makeText(getActivity(), "You can upload maximum 5 images", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_submit:
                if (officer_name_ed.getText().length() == 0) {
                    officer_name_ed.setError("Please enter name");
                    officer_name_ed.requestFocus();
                    break;
                }
//                if (designation_ed.getText().length() == 0) {
//                    designation_ed.setError("Please enter designation");
//                    designation_ed.requestFocus();
//                    break;
//                }
                if (date_meeting_ed.getText().length() == 0) {
                    date_meeting_ed.setError("Please select date");
                    date_meeting_ed.requestFocus();
                    break;
                }
                if (topic.getText().length() == 0) {
                    topic.setError("Please enter topic");
                    topic.requestFocus();
                    break;
                }
                if (et_pincode.getText().length() == 0) {
                    et_pincode.setError("Please enter pincode");
                    et_pincode.requestFocus();
                    break;
                }
                if (venue_ed.getText().length() == 0) {
                    venue_ed.setError("Please enter venue details");
                    venue_ed.requestFocus();
                    break;
                }
                if (state == null || state.equalsIgnoreCase("Select State*") || state.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (activities_ed.getText().length() == 0) {
                    activities_ed.setError("Please enter activity details");
                    activities_ed.requestFocus();
                    break;
                }
                if (audians.equalsIgnoreCase("Select Audians") || audians.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please Select Audians", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (frag_edit_outreach != null && frag_edit_outreach.equalsIgnoreCase("frag_edit_outreach")) {
                    if (AndroidUtil.isConnectingToInternet(getActivity())) {
                        editOutReachServerData();
                    } else {
                        Toast.makeText(getActivity(), Constant.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (AndroidUtil.isConnectingToInternet(getActivity())) {
                        addOutReachServerData();
                    } else {
                        Toast.makeText(getActivity(), Constant.check_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.date_meeting_iv:

                try {
                    newFragment = new SelectDateFragment();
                    //                newFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
                    newFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case R.id.cross_image1:

                if (image_file_hashmap != null && image_file_hashmap.size() > 0 && image1_layout.getVisibility() == View.VISIBLE) {
                    image_file_hashmap.remove(0);
                    uri_hashmap.remove(0);
                    first_image = 0;
                }

                image1_layout.setVisibility(View.GONE);
                if (image_file_hashmap.size() == 0 || image_file_hashmap.size() == 1) {
                } else {
                }
                if (image != null && image.size() > 0 && image.get(0).getId() != null) {
                    deleteimagelist.add(image.get(0).getId());
                }

                break;
            case R.id.cross_image2:
                if (image_file_hashmap != null && image_file_hashmap.size() > 0 && image2_layout.getVisibility() == View.VISIBLE) {
                    image_file_hashmap.remove(1);
                    uri_hashmap.remove(1);
                    second_image = 0;
                }
                image2_layout.setVisibility(View.GONE);
                if (image_file_hashmap.size() == 0 || image_file_hashmap.size() == 1) {
                } else {
                }

                if (image != null && image.size() > 1 && image.get(1).getId() != null) {
                    deleteimagelist.add(image.get(1).getId());
                }

                break;
            case R.id.cross_image3:

                if (image_file_hashmap != null && image_file_hashmap.size() > 0 && image3_layout.getVisibility() == View.VISIBLE) {
                    image_file_hashmap.remove(2);
                    uri_hashmap.remove(2);
                    third_image = 0;
                }
                image3_layout.setVisibility(View.GONE);
                if (image_file_hashmap.size() == 0 || image_file_hashmap.size() == 1) {

                } else {
                }


                if (image != null && image.size() > 2 && image.get(2).getId() != null) {
                    deleteimagelist.add(image.get(2).getId());
                }
                break;
            case R.id.cross_image4:

                if (image_file_hashmap != null && image_file_hashmap.size() > 0 && image4_layout.getVisibility() == View.VISIBLE) {
                    image_file_hashmap.remove(3);
                    uri_hashmap.remove(3);
                    fourth_image = 0;
                }
                image4_layout.setVisibility(View.GONE);
                if (image_file_hashmap.size() == 0 || image_file_hashmap.size() == 1) {

                } else {
                }

                if (image != null && image.size() > 3 && image.get(3).getId() != null) {
                    deleteimagelist.add(image.get(3).getId());
                }
                break;
            case R.id.cross_image5:

                if (image_file_hashmap != null && image_file_hashmap.size() > 0 && image5_layout.getVisibility() == View.VISIBLE) {
                    image_file_hashmap.remove(4);
                    uri_hashmap.remove(4);
                    fifth_image = 0;
                }
                image5_layout.setVisibility(View.GONE);
                if (image_file_hashmap.size() == 0 || image_file_hashmap.size() == 1) {

                } else {
                }
                if (image != null && image.size() > 4 && image.get(4).getId() != null) {
                    deleteimagelist.add(image.get(4).getId());
                }
                break;
        }
    }

    public void initFooter(final View view, final String type) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            // slide up animation on the dialog
            Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_bounce);
            view.startAnimation(slideUp);
            ImageView downimage = (ImageView) view.findViewById(R.id.down_imageview);
            ImageView cameraimage = (ImageView) view.findViewById(R.id.camera_imageview);
            ImageView galleryimage = (ImageView) view.findViewById(R.id.gallery_imageview);
            cameraimage.setOnClickListener(new View.OnClickListener() {
                //                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {

                    // slide down animation for the footer after  clicking
                    Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
                    view.startAnimation(slideDown);
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getActivity().checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            ///method to get Images
                            takeimage(type);
                        } else {
                            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                                Toast.makeText(getActivity(), "Your Permission is needed to get " + "access the camera", Toast.LENGTH_LONG).show();
                            }
                            FragmentAddOutReach.this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA,}, PICK_DOCUMENT_FROM_CAMERA);
                        }
                    } else {
                        takeimage(type);
                    }
                }
            });
            galleryimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // slide down animation for footer dialog
                    Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
                    view.startAnimation(slideDown);
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        FragmentAddOutReach.this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_DOCUMENT_FROM_GALLERY);
                    } else {
                        takeimagefromCamera(type);
                    }
                }
            });

            downimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
                    view.startAnimation(slideDown);
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    /* //Added by Mahendra 13-4-2017
     private void galleryIntent() {
         Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                 MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         // Start the Intent
         startActivityForResult(galleryIntent, RESULT_LOAD);
     }
     */
    private void takeimagefromCamera(String type) {

        Intent intent = new Intent();
        try {
           /* intent.setType("image*//*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);*/
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (type.equalsIgnoreCase(type)) {
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_DOCUMENT_FROM_GALLERY);
            }
        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (permsRequestCode) {

            case PICK_DOCUMENT_FROM_CAMERA:

                boolean cameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    takeimage("document");
                }
                break;
            case CROP_FROM_CAMERA:
                boolean cropAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                break;
            case PICK_DOCUMENT_FROM_GALLERY:
                boolean galleryAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (galleryAccepted) {
                    takeimagefromCamera("document");
                }
                break;
        }
    }

    // getting the result of camera and gallery on base of request code
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = null;

        if (requestCode == PICK_DOCUMENT_FROM_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data != null) {
                        extras = data.getExtras();
                    }
                    if (imageUri != null) {
                        performCrop(imageUri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == PICK_DOCUMENT_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data != null) {
                        imageUri = null;

                        imageUri = data.getData();

                        if (imageUri != null && image_file_hashmap.size() < 5) {
                            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

                            //
                            if (imageUri.toString().startsWith("file://")) {
                                realPath = imageUri.toString().replace("file://", "");
                            } else {
                                realPath = getImagePath(imageUri);
                            }


                            File f = new File(realPath);
//                            Uri contentUri = Uri.fromFile(f);
                            Uri contentUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", f);
                            performCrop(contentUri);

                            document_file = new File(realPath);

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CROP_FROM_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    imageUri = data.getData();
                    //

                    if (imageUri != null && image_file_hashmap.size() < 5) {
                        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                        if (imageUri.toString().startsWith("file:")) {
                            realPath = imageUri.toString().replace("file://", "");
                        } else {
                            realPath = getImagePath(imageUri);
                        }
                        document_file = new File(realPath);
                        document_file = Compressor.getDefault(getActivity()).compressToFile(document_file);
                        Log.v("File size in kb after ", String.valueOf(document_file.length() / 1024));


                        if (first_image == 0) {
                            lastmapkey = 0;
                            image_file_hashmap.put(lastmapkey, document_file);
                            uri_hashmap.put(lastmapkey, imageUri);
                            first_image = 1;
                        } else if (second_image == 0) {
                            lastmapkey = 1;
                            image_file_hashmap.put(lastmapkey, document_file);
                            uri_hashmap.put(lastmapkey, imageUri);
                            second_image = 1;
                        } else if (third_image == 0) {
                            lastmapkey = 2;
                            image_file_hashmap.put(lastmapkey, document_file);
                            uri_hashmap.put(lastmapkey, imageUri);
                            third_image = 1;
                            //                            }
                        } else if (fourth_image == 0) {
                            lastmapkey = 3;
                            image_file_hashmap.put(lastmapkey, document_file);
                            uri_hashmap.put(lastmapkey, imageUri);
                            fourth_image = 1;
                            //                            }
                        } else if (fifth_image == 0) {
                            lastmapkey = 4;
                            image_file_hashmap.put(lastmapkey, document_file);
                            uri_hashmap.put(lastmapkey, imageUri);
                            fifth_image = 1;
                            //                            }
                        }


                    }

                    if (imageUri != null) {
                        settingCropedImage(imageUri);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();

        if (cursor != null && cursor.getCount() > 0) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            cursor.close();
        }
        return path;
    }

    private void settingCropedImage(Uri imageUri) {
        try {
            if (image1_layout.getVisibility() == View.GONE) {
                image1_layout.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                product_image1.setImageBitmap(bitmap);

            } else if (image2_layout.getVisibility() == View.GONE) {
                image2_layout.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                product_image2.setImageBitmap(bitmap);

            } else if (image3_layout.getVisibility() == View.GONE) {
                image3_layout.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                product_image3.setImageBitmap(bitmap);

            } else if (image4_layout.getVisibility() == View.GONE) {
                image4_layout.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                product_image4.setImageBitmap(bitmap);

            } else if (image5_layout.getVisibility() == View.GONE) {
                image5_layout.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                product_image5.setImageBitmap(bitmap);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takeimage(String type) {

        // calling the camera activity
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // get current timestamp
        String milliStr = Calendar.getInstance().getTimeInMillis() + "";

        File f = new File(android.os.Environment.getExternalStorageDirectory(), "SWACCH_" + milliStr + ".jpg");
//        imageUri = Uri.fromFile(f);
        imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        try {

            if (type.equalsIgnoreCase("document")) {
                startActivityForResult(intent, PICK_DOCUMENT_FROM_CAMERA);
            }

        } catch (ActivityNotFoundException e) {
        }
    }

    private void performCrop(Uri picUri) {
        try {
            //
            Intent cropIntent;
            //Start Crop Activity
            cropIntent = new Intent("com.android.camera.action.CROP");
            //            cropIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(cropIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getActivity().grantUriPermission(packageName, picUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            startActivityForResult(cropIntent, CROP_FROM_CAMERA);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
        }
    }

    private void checkEditText() {


    }

    private void addOutReachServerData() {
        Call<ResponseBody> call;
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

        MultipartBody.Part body1 = null, body2 = null, body3 = null, body4 = null, body5 = null;
        StringBuilder stringBuilder = new StringBuilder();
        if (image_file_hashmap.size() > 0) {
            File[] filesArray = new File[image_file_hashmap.size()];
            document_list.clear();
            ArrayList<Uri> uriList = new ArrayList<>();

            //                int i = 0;
            for (Map.Entry<Integer, File> map : image_file_hashmap.entrySet()) {
                document_list.add(map.getValue());
            }

            filesArray = document_list.toArray(filesArray);

            try {
                if (filesArray.length > 0) {
                    body1 = prepareFilePart("pic[]", filesArray[0]);
                }
                if (filesArray.length > 1) {
                    body2 = prepareFilePart("pic[]", filesArray[1]);
                }
                if (filesArray.length > 2) {
                    body3 = prepareFilePart("pic[]", filesArray[2]);
                }
                if (filesArray.length > 3) {
                    body4 = prepareFilePart("pic[]", filesArray[3]);
                }
                if (filesArray.length > 4) {
                    body5 = prepareFilePart("pic[]", filesArray[4]);
                }
                //                nameValuePair.put("image[]", filesArray);
//                final JSONArray jsonArray1 = new JSONArray();
                stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (body1 != null || body2 != null || body3 != null || body4 != null || body5 != null) {
            call = ifaqdata.getData(user_id, officer_name_ed.getText().toString(), date_meeting_ed.getText().toString(),
                    topic.getText().toString(), et_pincode.getText().toString(), et_participants.getText().toString(),
                    state, activities_ed.getText().toString(), audians, venue_ed.getText().toString(),
                    body1, body2, body3, body4, body5);

        } else {
            call = ifaqdata.getData(user_id, officer_name_ed.getText().toString(), date_meeting_ed.getText().toString(),
                    topic.getText().toString(), et_pincode.getText().toString(), et_participants.getText().toString(),
                    state, activities_ed.getText().toString(), audians, venue_ed.getText().toString());
        }
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
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.getInt("status") == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
                            Toast.makeText(getActivity(), jsonObject.getString("tag"), Toast.LENGTH_LONG).show();
                            venue_ed.setText("");
                            //audience_ed.setText("");
                            date_meeting_ed.setHint("Date of meeting*");
                            getFragmentManager().popBackStackImmediate();
//                            if (sharedpreferences.getString("name", "") != null&&sharedpreferences.getString("name", "").equalsIgnoreCase(officer_name_ed.getText().toString()) ) {
//                                officer_name_ed.setText(sharedpreferences.getString("name", ""));
//                            }else{
//                                officer_name_ed.setText("");
//                            }
//                            if (sharedpreferences.getString("designation", "") != null) {
//                                designation_ed.setText(sharedpreferences.getString("designation", ""));
//                            }
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("tag"), Toast.LENGTH_LONG).show();
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

    void getDataStateAudiance() {
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

        Call<OutReach> call = ifaqdata.getStateAud(user_id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<OutReach> call, Response<OutReach> response) {
        if (pDialog != null) {
            pDialog.dismiss();
        }
        outReach = response.body();
        if (outReach != null) {
            try {
                if (Integer.parseInt(outReach.getStatus()) == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
                    if (outReach.getTargetaudience() != null && outReach.getTargetaudience().size() > 0) {
                        audiSpinner();
                    }
                    if (outReach.getStatedata() != null && outReach.getStatedata().size() > 0) {
                        stateSpinner();
                    }
                } else {
                    Toast.makeText(getActivity(), outReach.getTag().toString(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(Call<OutReach> call, Throwable t) {
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }

    private void audiSpinner() {
        audList.clear();
        audList = new ArrayList<OutReach.targetaudience>();
        audList = outReach.getTargetaudience();
        OutReach.targetaudience dataaud = new OutReach().new targetaudience();
        dataaud.setName("Select Audiance");
        audList.add(0, dataaud);
        audiansAdapter = new AudiansAdapter(getActivity(), R.layout.cus_spinner, audList);
        spn_audience_ed.setAdapter(audiansAdapter);
        if (audiance_id > 1) {
            spn_audience_ed.setSelection(audiance_id);
        }
        spn_audience_ed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    audians = "";
                    return;
                }
                audians = outReach.getTargetaudience().get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void stateSpinner() {
        stateList.clear();
        stateList = new ArrayList<OutReach.state>();
        stateList = outReach.getStatedata();
        OutReach.state dataaud = new OutReach().new state();
        dataaud.setName("Select State");
        stateList.add(0, dataaud);
        stateAdapter = new StateAdapter(getActivity(), R.layout.cus_spinner, stateList);
        spn_state.setAdapter(stateAdapter);

        if (state_id > 0) {
            spn_state.setSelection(state_id);
        }

        spn_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    state = "";
//                    return;
//                }
//                try {
                int state_id = spn_state.getSelectedItemPosition();
                if (state_id > 0)
//                    state = stateList.get(state_id).getId();
                    state = String.valueOf(state_id);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public interface IFAQDATA {
        @Multipart
        @POST(Constant.WebUrl.CREATE_OUTREACH)
        Call<ResponseBody> getData(@Query("id") String user_id, @Query("officer_name") String officer_name, @Query("date") String date, @Query("topic") String topic, @Query("pincode") String pincode, @Query("participants") String participants, @Query("state") String state, @Query("activities") String activities, @Query("audiance") String audiance, @Query("venue") String venue, @Part MultipartBody.Part file, @Part MultipartBody.Part file1, @Part MultipartBody.Part file2, @Part MultipartBody.Part file3, @Part MultipartBody.Part file4);

        @GET(Constant.WebUrl.CREATE_OUTREACH)
        Call<ResponseBody> getData(@Query("id") String user_id, @Query("officer_name") String officer_name, @Query("date") String date, @Query("topic") String topic, @Query("pincode") String pincode, @Query("participants") String participants, @Query("state") String state, @Query("activities") String activities, @Query("audiance") String audiance, @Query("venue") String venue);

        @Multipart
        @POST(Constant.WebUrl.EDIT_OUTREACH)
        Call<ResponseBody> sendEditData(@Query("delete") String delete_img, @Query("outreach_id") String outreach_id, @Query("id") String user_id, @Query("officer_name") String officer_name, @Query("date") String date, @Query("topic") String topic, @Query("pincode") String pincode, @Query("participants") String participants, @Query("state") String state, @Query("activities") String activities, @Query("audiance") String audiance, @Query("venue") String venue, @Part MultipartBody.Part file, @Part MultipartBody.Part file1, @Part MultipartBody.Part file2, @Part MultipartBody.Part file3, @Part MultipartBody.Part file4);

        @GET(Constant.WebUrl.EDIT_OUTREACH)
        Call<ResponseBody> sendEditData(@Query("delete") String delete_img, @Query("outreach_id") String outreach_id, @Query("id") String user_id, @Query("officer_name") String officer_name, @Query("date") String date, @Query("topic") String topic, @Query("pincode") String pincode, @Query("participants") String participants, @Query("state") String state, @Query("activities") String activities, @Query("audiance") String audiance, @Query("venue") String venue);

        @GET(Constant.WebUrl.OUTREACHSTATE)
        Call<OutReach> getStateAud(@Query("id") String id);
    }

    private void editOutReachServerData() {
        Call<ResponseBody> call;
        pDialog = new ProgressDialog(getContext(), R.style.DialogTheme);
        pDialog.setCancelable(false);
        if (pDialog != null) {
            pDialog.setMessage(getString(R.string.loading));
            if (!pDialog.isShowing())
                pDialog.show();
        }


//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Constant.WebUrl.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(new OkHttpClient().setReadTimeout(30, TimeUnit.SECONDS)
//                .setConnectTimeout(30, TimeUnit.SECONDS))
//                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(10000, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.WebUrl.BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        IFAQDATA ifaqdata = retrofit.create(IFAQDATA.class);
        if (sharedpreferences.getString("User_id", "") != null) {
            user_id = sharedpreferences.getString("User_id", "");
        }

        MultipartBody.Part body1 = null, body2 = null, body3 = null, body4 = null, body5 = null;
        StringBuilder stringBuilder = new StringBuilder();
        if (image_file_hashmap.size() > 0) {
            File[] filesArray = new File[image_file_hashmap.size()];
            document_list.clear();
            ArrayList<Uri> uriList = new ArrayList<>();

            //                int i = 0;
            for (Map.Entry<Integer, File> map : image_file_hashmap.entrySet()) {
                document_list.add(map.getValue());
            }

            filesArray = document_list.toArray(filesArray);

            try {
                if (filesArray.length > 0) {
                    body1 = prepareFilePart("pic[]", filesArray[0]);
                }
                if (filesArray.length > 1) {
                    body2 = prepareFilePart("pic[]", filesArray[1]);
                }
                if (filesArray.length > 2) {
                    body3 = prepareFilePart("pic[]", filesArray[2]);
                }
                if (filesArray.length > 3) {
                    body4 = prepareFilePart("pic[]", filesArray[3]);
                }
                if (filesArray.length > 4) {
                    body5 = prepareFilePart("pic[]", filesArray[4]);
                }
                //                nameValuePair.put("image[]", filesArray);
                //
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final JSONArray jsonArray1 = new JSONArray();
        stringBuilder = new StringBuilder();
        if (deleteimagelist.size() > 0) {
            for (int i = 0; i < deleteimagelist.size(); i++) {
                try {

                    stringBuilder.append(deleteimagelist.get(i) + ",");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

//        spn_state.getSelectedItem().

        if (body1 != null || body2 != null || body3 != null || body4 != null || body5 != null) {
            call = ifaqdata.sendEditData(stringBuilder.toString(), outreach_id, user_id, officer_name_ed.getText().toString(),
                    date_meeting_ed.getText().toString(), topic.getText().toString(), et_pincode.getText().toString(),
                    et_participants.getText().toString(), state, activities_ed.getText().toString(), audians,
                    venue_ed.getText().toString(), body1, body2, body3, body4, body5);
        } else {
            call = ifaqdata.sendEditData(stringBuilder.toString(), outreach_id, user_id, officer_name_ed.getText().toString(),
                    date_meeting_ed.getText().toString(), topic.getText().toString(), et_pincode.getText().toString(),
                    et_participants.getText().toString(), state, activities_ed.getText().toString(),
                    audians, venue_ed.getText().toString());


        }

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
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.getInt("status") == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
                            Toast.makeText(getActivity(), jsonObject.getString("tag"), Toast.LENGTH_LONG).show();
                            venue_ed.setText("");
                            //audience_ed.setText("");
                            date_meeting_ed.setHint("Date of meeting*");
                            getFragmentManager().popBackStackImmediate();
//
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("tag"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        });
    }

    @NonNull
    public static MultipartBody.Part prepareFilePart(String partName, File file) {


        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(Constant.MULTIPART_FILE_FORM_DATA), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @NonNull
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(Constant.MULTIPART_FILE_FORM_DATA), descriptionString);
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), this, yy, mm, dd);

            //mDatePicker.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationEditProfile;
            //mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            //            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            return mDatePicker;
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }

        public void populateSetDate(int year, int month, int day) {
            date_meeting_ed.setText(day + "-" + month + "-" + year);

        }
    }

    // adapter for the audians spinner
    public class AudiansAdapter extends ArrayAdapter<OutReach.targetaudience> {
        List<OutReach.targetaudience> objects;

        public AudiansAdapter(Context ctx, int txtViewResourceId, List<OutReach.targetaudience> objects) {

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

    // adapter for the state spinner
    public class StateAdapter extends ArrayAdapter<OutReach.state> {
        List<OutReach.state> objects;

        public StateAdapter(Context ctx, int txtViewResourceId, List<OutReach.state> objects) {

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
}