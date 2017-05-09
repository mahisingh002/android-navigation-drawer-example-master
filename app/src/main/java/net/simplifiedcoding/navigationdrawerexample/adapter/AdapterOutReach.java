package net.simplifiedcoding.navigationdrawerexample.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.simplifiedcoding.navigationdrawerexample.Constant.Constant;
import net.simplifiedcoding.navigationdrawerexample.Model.Datum;
import net.simplifiedcoding.navigationdrawerexample.Model.OutReachData;
import net.simplifiedcoding.navigationdrawerexample.Model.SuccessStoriesData;
import net.simplifiedcoding.navigationdrawerexample.R;
import net.simplifiedcoding.navigationdrawerexample.activities.MainActivity;
import net.simplifiedcoding.navigationdrawerexample.fragments.FragmentAddOutReach;
import net.simplifiedcoding.navigationdrawerexample.fragments.FragmentSuccessStories;
import net.simplifiedcoding.navigationdrawerexample.util.AndroidUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vibes on 6/3/17.
 */

public class AdapterOutReach extends RecyclerView.Adapter<AdapterOutReach.MyViewHolder> {

    private Context context;
    List<OutReachData.Datum> data;
    String status;
    private static ClickListener clickListener;
    SharedPreferences sharedpreferences;
    ProgressDialog pDialog;
    String user_id;


    public AdapterOutReach(Context context, List<OutReachData.Datum> data) {
        this.context = context;
        this.data = data;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title_name_tv, created_date_tv, title_tv, author_tv, task_comp_tv;
        RelativeLayout relative_deleteSS;

        public MyViewHolder(View v) {
            super(v);
            title_name_tv = (TextView) v.findViewById(R.id.title_name_tv);
            //author_name_tv = (TextView) v.findViewById(R.id.author_name_tv);
            created_date_tv = (TextView) v.findViewById(R.id.created_date_tv);
            //  read_more_tv = (TextView) v.findViewById(R.id.read_more_tv);
            title_tv = (TextView) v.findViewById(R.id.title_tv);
            task_comp_tv = (TextView) v.findViewById(R.id.task_comp_tv);
            author_tv = (TextView) v.findViewById(R.id.author_tv);
            relative_deleteSS = (RelativeLayout) v.findViewById(R.id.relative_deleteSS);
//            v.setOnClickListener(this);
//            read_more_tv.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.read_more_tv:
//
//                    break;
//            }
////            clickListener.onItemClick(getAdapterPosition(), view);
//        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_outreach, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (data != null && data.size() > 0) {

            if (sharedpreferences.getString("User_id", "") != null) {
                user_id = sharedpreferences.getString("User_id", "");
            }

//            holder.read_more_tv.setTag(position);
            holder.title_tv.setText("Officer Name:");
            holder.task_comp_tv.setText("Date of meeting");
            if (data.get(position).getOfficerName() != null && !data.get(position).getOfficerName().isEmpty()) {
                holder.title_name_tv.setText(" " + data.get(position).getOfficerName());
            }
           /* if (data.get(position).getDesignation() != null && !data.get(position).getDesignation().isEmpty()) {
                holder.author_name_tv.setText(" " + data.get(position).getDesignation());
            }*/
            String date = data.get(position).getDateOfMeeting();
            if (date != null && !date.isEmpty())
                holder.created_date_tv.setText(" " + AndroidUtil.formatDate(date, "yyyy-mm-dd", "dd-mm-yyyy"));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String officer_name = data.get(position).getOfficerName();
                    //String designation = data.get(position).getDesignation();
                    String date_of_meeting = data.get(position).getDateOfMeeting();
                    String venue = data.get(position).getVenu();
                    String topic = data.get(position).getTopic();
                    String outreach_id = data.get(position).getOutreachId();
                    String participants = data.get(position).getParticipants();
                    String pincode = data.get(position).getPincode();
                    String state = data.get(position).getState();
                    String activities = data.get(position).getActivities();
                    String audiance = data.get(position).getAudiance();
                    int state_id = data.get(position).getState_id();
                    int audiance_id = data.get(position).getAudience_id();

                    List<OutReachData.Img> image = data.get(position).getImg();

                    Fragment fragment = new FragmentAddOutReach();
                    Bundle bundle = new Bundle();
                    bundle.putString("frag", "frag_edit_outreach");
                    bundle.putString("topic", topic);
                    bundle.putInt("state_id", state_id);
                    bundle.putInt("audiance_id", audiance_id);
                    bundle.putString("officer_name", officer_name);
                    bundle.putString("participants", participants);
                    bundle.putString("pincode", pincode);
                    bundle.putString("state", state);
                    bundle.putString("activities", activities);
                    bundle.putString("audiance", audiance);
                    bundle.putString("date_of_meeting", date_of_meeting);
                    bundle.putString("venue", venue);
                    bundle.putString("outreach_id", outreach_id);
                    bundle.putParcelableArrayList("image", (ArrayList<? extends Parcelable>) image);
                    fragment.setArguments(bundle);
                    ((MainActivity) context).replacefragment(fragment);
                }
            });

            holder.relative_deleteSS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String OutReach_id = data.get(position).getOutreachId();
                    AlertShowDeleteOutReach(OutReach_id, user_id,position);
                    //data.remove(position);


                    notifyDataSetChanged();
                }
            });
        }

    }

    private void AlertShowDeleteOutReach(final String OutReach, final String user_id, final int position1) {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
        myAlertDialog.setTitle("");
        if (OutReach != null)
            myAlertDialog.setMessage("Are you sure you want to Delete ");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                // do something when the OK button is clicked
                outReachDelete(OutReach, user_id);
                data.remove(position1);



            }
        });
        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        myAlertDialog.show();
    }

    private void outReachDelete(String outreach_id, String id) {

        pDialog = new ProgressDialog(context, R.style.DialogTheme);
        pDialog.setCancelable(false);
        if (pDialog != null) {
            pDialog.setMessage("Loading...");
            if (!pDialog.isShowing())
                pDialog.show();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.WebUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        IOUTReachDELETE ioutRachDelete = retrofit.create(IOUTReachDELETE.class);

        Call<ResponseBody> submitDeleteOutReachTask = ioutRachDelete.getOutReachDelete(outreach_id, id);
        submitDeleteOutReachTask.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }

                        String data = null;
                        try {
                            data = response.body().string();
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.optString("status").contains("1")) {
                                String strTag = jsonObject.optString("tag");
                                Toast.makeText(context, strTag, Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.optString("status").contains("0")) {
                                String strTag = jsonObject.optString("tag");
                                Toast.makeText(context, strTag, Toast.LENGTH_SHORT).show();
                            }
                            notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                }
        );
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        AdapterOutReach.clickListener = clickListener;
    }


    public interface ClickListener {
        void onItemClick(int position, View v);
    }


    public interface IOUTReachDELETE {
        @GET(Constant.WebUrl.OUT_REACH_DELETE)
        Call<ResponseBody> getOutReachDelete(@Query("outreach_id") String outreach_id, @Query("id") String id);
    }


}

