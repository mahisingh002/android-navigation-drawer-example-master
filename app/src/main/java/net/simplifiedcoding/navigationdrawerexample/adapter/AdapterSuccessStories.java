package net.simplifiedcoding.navigationdrawerexample.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import net.simplifiedcoding.navigationdrawerexample.Constant.Constant;
import net.simplifiedcoding.navigationdrawerexample.Model.Contribution;
import net.simplifiedcoding.navigationdrawerexample.Model.SuccessStoriesData;
import net.simplifiedcoding.navigationdrawerexample.R;
import net.simplifiedcoding.navigationdrawerexample.activities.MainActivity;
import net.simplifiedcoding.navigationdrawerexample.fragments.FragmentContribute;
import net.simplifiedcoding.navigationdrawerexample.fragments.FragmentSuccessStories;
import net.simplifiedcoding.navigationdrawerexample.util.AndroidUtil;
import net.simplifiedcoding.navigationdrawerexample.util.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class AdapterSuccessStories extends RecyclerView.Adapter<AdapterSuccessStories.MyViewHolder> {

    private Context context;
    List<SuccessStoriesData.Datum> data;
    String status;
    private static ClickListener clickListener;
    SharedPreferences sharedpreferences;
    ProgressDialog pDialog;
    String user_id;

    public AdapterSuccessStories(Context context, List<SuccessStoriesData.Datum> data) {
        this.context = context;
        this.data = data;
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title_name_tv, author_tv, created_date_tv;
        RelativeLayout relative_deleteSS;

        public MyViewHolder(View v) {
            super(v);
            title_name_tv = (TextView) v.findViewById(R.id.title_name_tv);
            author_tv = (TextView) v.findViewById(R.id.author_name_tv);
            created_date_tv = (TextView) v.findViewById(R.id.created_date_tv);
            relative_deleteSS = (RelativeLayout) v.findViewById(R.id.relative_deleteSS);
//            read_more_tv = (TextView) v.findViewById(R.id.read_more_tv);
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sucessstories, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (data != null && data.size() > 0) {

            // holder.read_more_tv.setTag(position);

            if (sharedpreferences.getString("User_id", "") != null) {
                user_id = sharedpreferences.getString("User_id", "");
            }

            if (data.get(position).getTitle() != null && !data.get(position).getTitle().isEmpty()) {
                holder.title_name_tv.setText(" " + data.get(position).getTitle());
            }
            if (data.get(position).getAuthor() != null && !data.get(position).getAuthor().isEmpty()) {
                holder.author_tv.setText(" " + data.get(position).getAuthor());
            }
            String[] date = data.get(position).getCreatedOn().split(" ");
            if (date.length > 0)
                holder.created_date_tv.setText(" " + AndroidUtil.formatDate(date[0], "yyyy-mm-dd", "dd-mm-yyyy"));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String title = data.get(position).getTitle();
                    String heading = data.get(position).getHeading();
                    String desc = data.get(position).getDescription();
                    String author = data.get(position).getAuthor();
                    String url = data.get(position).getUrl();
                    String success_id = data.get(position).getSuccessId();
                    String image = data.get(position).getImage();

                    Fragment fragment = new FragmentSuccessStories();
                    Bundle bundle = new Bundle();
                    bundle.putString("frag", "frag_edit");
                    bundle.putString("title", title);
                    bundle.putString("heading", heading);
                    bundle.putString("desc", desc);
                    bundle.putString("author", author);
                    bundle.putString("url", url);
                    bundle.putString("success_id", success_id);
                    bundle.putString("image", image);
                    fragment.setArguments(bundle);
                    ((MainActivity) context).replacefragment(fragment);
                }
            });

            holder.relative_deleteSS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String success_id = data.get(position).getSuccessId();
                    AlertShowDeleteSuccessStories(success_id, user_id,position);
                   // data.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void AlertShowDeleteSuccessStories(final String success_id, final String user_id, final int position1) {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
        myAlertDialog.setTitle("");
        if (success_id != null)
            myAlertDialog.setMessage("Are you sure you want to Delete ");
        myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                // do something when the OK button is clicked
                successStoryDelete(success_id, user_id);
                data.remove(position1);

            }
        });
        myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        myAlertDialog.show();
    }


    private void successStoryDelete(String success_id, String id) {

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

        ISSDELETE isuccessStoryDelete = retrofit.create(ISSDELETE.class);

        Call<ResponseBody> submitDeleteSucessStoryTask = isuccessStoryDelete.getDelete(success_id, id);
        submitDeleteSucessStoryTask.enqueue(
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
        AdapterSuccessStories.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public interface ISSDELETE {
        @GET(Constant.WebUrl.SUCCESS_STORY_DELETE)
        Call<ResponseBody> getDelete(@Query("success_id") String success_id, @Query("id") String id);
    }
}

