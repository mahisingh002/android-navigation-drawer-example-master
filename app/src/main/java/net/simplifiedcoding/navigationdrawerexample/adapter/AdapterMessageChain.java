package net.simplifiedcoding.navigationdrawerexample.adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.simplifiedcoding.navigationdrawerexample.Model.FaqCommon;
import net.simplifiedcoding.navigationdrawerexample.Model.NewTaskData;
import net.simplifiedcoding.navigationdrawerexample.Model.PendingTaskDetail;
import net.simplifiedcoding.navigationdrawerexample.R;
import net.simplifiedcoding.navigationdrawerexample.activities.WebView_Activity;
import net.simplifiedcoding.navigationdrawerexample.service.DownloadTaskCleanMoney;
import net.simplifiedcoding.navigationdrawerexample.util.AndroidUtil;
import net.simplifiedcoding.navigationdrawerexample.util.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vibes on 6/3/17.
 */

public class AdapterMessageChain extends RecyclerView.Adapter<AdapterMessageChain.MyViewHolder> {

    private Context context;
    private ArrayList<FaqCommon> faqModelsList;
    List<PendingTaskDetail.Response> data;
    String status;
    private static final String TAG = AdapterMessageChain.class.getSimpleName();


    //    public AdapterNewTask(Context context, FaqModel data) {
//        this.context = context;
//        if (data != null) {
//            faqModelsList = data.getFaq();
//           // Log.e("check data on adapter",faqModelsList.toString());
//        }
//    }
    public AdapterMessageChain(Context context, List<PendingTaskDetail.Response> data) {
        this.context = context;
        this.data = data;

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView created_on, created_by, task_message,
                responded_on, task_compon_date_tv, attachment_prev_tv, attachment_current_tv, tv_viewattachment;
        LinearLayout ll_prev_attach, ll_curr_attach;

        public MyViewHolder(View v) {
            super(v);

            created_by = (TextView) v.findViewById(R.id.created_by);
            responded_on = (TextView) v.findViewById(R.id.responded_on);
            task_message = (TextView) v.findViewById(R.id.task_description_tv);
            ll_prev_attach = (LinearLayout) v.findViewById(R.id.ll_prev_attach);
            ll_curr_attach = (LinearLayout) v.findViewById(R.id.ll_curr_attach);
            attachment_prev_tv = (TextView) v.findViewById(R.id.attachment_prev_tv);
            attachment_current_tv = (TextView) v.findViewById(R.id.attachment_current_tv);
            tv_viewattachment = (TextView) v.findViewById(R.id.tv_viewattachment);

            ll_prev_attach.setVisibility(View.GONE);
            ll_curr_attach.setVisibility(View.GONE);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newmessage_chain, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final PendingTaskDetail.Response response = data.get(position);

        if (response.getUpdatedOn() != null && !response.getUpdatedOn().isEmpty()) {
            holder.responded_on.setText(" " + response.getUpdatedOn());
        }

        if (response.getCreatedBy() != null && !response.getCreatedBy().isEmpty()) {
            holder.created_by.setText(" " + response.getCreatedBy());
        }
        if (response.getRemark() != null && !response.getRemark().isEmpty()) {
            holder.task_message.setText(" " + response.getRemark());
        }
        if (response.getPreviousAttach() != null && !response.getPreviousAttach().isEmpty()) {
            holder.ll_prev_attach.setVisibility(View.VISIBLE);
            holder.attachment_prev_tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_attachment, 0, 0, 0);
            // tvAttachmentBottom.setText(" " + compTaskdataResponse.get(0).getDoc());
            holder.attachment_prev_tv.setText(" Click here to download");
        }

        if (response.getDoc() != null && !response.getDoc().isEmpty()) {
            holder.tv_viewattachment.setVisibility(View.VISIBLE);
            holder.tv_viewattachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(context, WebView_Activity.class);
                    in.putExtra(Config.Webview_url, response.getDoc().toString());
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(in);
                }
            });
        }

        if (response.getDoc() != null && !response.getDoc().isEmpty()) {
            holder.ll_curr_attach.setVisibility(View.VISIBLE);
            holder.attachment_current_tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_attachment, 0, 0, 0);
            // tvAttachmentBottom.setText(" " + compTaskdataResponse.get(0).getDoc());
            holder.attachment_current_tv.setText(" Click here to download");
            holder.attachment_current_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!data.get(position).getDoc().isEmpty()) {
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(position).getDoc()));
//                        context.startActivity(browserIntent);
//                    }
                    if (!response.getDoc().isEmpty()) {
                        if (isConnectingToInternet()) {
                            final int id = 1;
                            final NotificationManager mNotifyManager =
                                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            final android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                            mBuilder.setContentTitle("File Download")
                                    .setContentText("Download in progress")
                                    .setSmallIcon(R.drawable.pdf_icon);
                            new Thread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            int incr;
                                            for (incr = 0; incr <= 100; incr += 25) {
                                                mBuilder.setProgress(100, incr, false);
                                                mNotifyManager.notify(id, mBuilder.build());
                                                try {
                                                    // Sleep for 5 seconds
                                                    Thread.sleep(5 * 100);
                                                } catch (InterruptedException e) {
                                                    Log.d(TAG, "sleep failure");
                                                }
                                            }
                                            mBuilder.setContentText("Download complete")
                                                    .setProgress(0, 0, false);
                                            mNotifyManager.notify(id, mBuilder.build());
                                        }
                                    }
                            ).start();
                            new DownloadTaskCleanMoney(context, response.getDoc());
                            Toast.makeText(context, "Downloading file", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, Config.Interet_Error, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }


        holder.attachment_prev_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!data.get(position).getPreviousAttach().isEmpty()) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(position).getPreviousAttach()));
//                    context.startActivity(browserIntent);
//                }

                if (!response.getPreviousAttach().isEmpty()) {
                    if (isConnectingToInternet()) {
                        final int id = 1;
                        final NotificationManager mNotifyManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        final android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                        mBuilder.setContentTitle("File Download")
                                .setContentText("Download in progress")
                                .setSmallIcon(R.drawable.pdf_icon);
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        int incr;
                                        for (incr = 0; incr <= 100; incr += 25) {
                                            mBuilder.setProgress(100, incr, false);
                                            mNotifyManager.notify(id, mBuilder.build());
                                            try {
                                                Thread.sleep(5 * 1000);
                                            } catch (InterruptedException e) {
                                                Log.d(TAG, "sleep failure");
                                            }
                                        }
                                        mBuilder.setContentText("Download complete")
                                                .setProgress(0, 0, false);
                                        mNotifyManager.notify(id, mBuilder.build());
                                    }
                                }
                        ).start();

                        new DownloadTaskCleanMoney(context, response.getPreviousAttach());

                        Toast.makeText(context, "File Download..", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, Config.Interet_Error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}

