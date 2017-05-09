package net.simplifiedcoding.navigationdrawerexample.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.simplifiedcoding.navigationdrawerexample.Model.Search;
import net.simplifiedcoding.navigationdrawerexample.Model.UserSearch;
import net.simplifiedcoding.navigationdrawerexample.R;
import net.simplifiedcoding.navigationdrawerexample.activities.MainActivity;
import net.simplifiedcoding.navigationdrawerexample.fragments.FragmentCreateTask;

import java.util.ArrayList;

/**
 * Created by vibes on 28/3/17.
 */

public class AdapterDirectory extends RecyclerView.Adapter<AdapterDirectory.MyViewHolder> {

    private Context context;
    public ProgressDialog pDialog;
    private ArrayList<Search> userSearches = new ArrayList<>();
    SharedPreferences sharedpreferences;
    private static AdapterDirectory.ClickListener clickListener;

    public AdapterDirectory(Context context, UserSearch data) {
        this.context = context;
        if (data != null) {
            userSearches = data.getData();
            // Log.e("check data on adapter",faqModelsList.toString());
        }
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public AdapterDirectory.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_directory_list, parent, false);
        AdapterDirectory.MyViewHolder holder = new AdapterDirectory.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterDirectory.MyViewHolder holder, final int position) {
        if (userSearches != null && userSearches.size() > 0) {
//            holder.addtofav_tv.setTag(position);

           /* holder.tvCall.setText(userSearches.get(position).getUser_phone());
            holder.tvMail.setText(userSearches.get(position).getUser_email());
            holder.tvDescription.setText(userSearches.get(position).getUser_designation());*/
            holder.tvName.setText(userSearches.get(position).getUser_name());
            holder.tv_designation.setText(userSearches.get(position).getUser_designation());

           /* holder.tvAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("email", userSearches.get(position).getUser_email());
                    bundle.putString("id", userSearches.get(position).getUser_id());
                    Fragment fragment = new FragmentCreateTask();
                    fragment.setArguments(bundle);
                    ((MainActivity) context).replacefragment(fragment);
                }
            });*/
        }
    }

    @Override
    public int getItemCount() {
        return userSearches.size();
    }


    public void setOnItemClickListener(AdapterDirectory.ClickListener clickListener) {
        AdapterDirectory.clickListener = clickListener;
    }
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName,tv_designation;
        AppCompatButton tvAction;
        String user_id;


        public MyViewHolder(View v) {
            super(v);

            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvAction = (AppCompatButton) v.findViewById(R.id.btn_action);
            tv_designation = (TextView) v.findViewById(R.id.tv_designation);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

       /* @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addtofav_tv:
                    int pos = (int) view.getTag();
                    String taskId = userSearches.get(pos).getUser_id();
                    if (userSearches.get(pos).getFav().equalsIgnoreCase("1")) {
                        Toast.makeText(context, "Already added as favorite", Toast.LENGTH_SHORT).show();
                    } else {
                        if (AndroidUtil.isConnectingToInternet(context)) {
                            addToFav(taskId, pos);
                        } else {
                            Toast.makeText(context, Constant.check_internet_connection, Toast.LENGTH_SHORT).show();
                        }
                    }

                    return;
            }
        }*/


      /*  private void addToFav(String taskid, final int pos) {

            pDialog = new ProgressDialog(context, R.style.DialogTheme);
            pDialog.setCancelable(true);
            if (pDialog != null) {
                pDialog.setMessage(context.getString(R.string.loading));
                if (!pDialog.isShowing())
                    pDialog.show();
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.WebUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient())
                    .build();
            AdapterSearchList.IUSERLIST iuserlist = retrofit.create(AdapterSearchList.IUSERLIST.class);
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
                            JSONObject jsonObject = new JSONObject(data);
                            if (jsonObject.getInt("status") == Constant.StatusCode.CLEANMONEY_CODE_SUCCESS) {
                                changeDrawable();
                                userSearches.get(pos).setFav("1");

                            } else {
                                Toast.makeText(context, jsonObject.getString("tag") + "", Toast.LENGTH_LONG).show();
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
        }*/

       /* private void changeDrawable() {
            Drawable img = context.getResources().getDrawable(R.mipmap.icon_colorstar);
            addtofav_tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }

        private void retainDrawable() {
            Drawable img = context.getResources().getDrawable(R.mipmap.icon_blnkstar);
            addtofav_tv.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
        }*/
    }


    /* public interface IUSERLIST {
         @GET(Constant.WebUrl.ADD_TO_FAV_URL)
         Call<ResponseBody> getData(@Query("for") String type, @Query("id") String id);


     }*/
    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
