package net.simplifiedcoding.navigationdrawerexample.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.simplifiedcoding.navigationdrawerexample.Model.NewTaskData;
import net.simplifiedcoding.navigationdrawerexample.R;
import net.simplifiedcoding.navigationdrawerexample.util.AndroidUtil;

import java.util.List;

public class AdapterNewTask extends RecyclerView.Adapter<AdapterNewTask.MyViewHolder> {

    private Context context;

    List<NewTaskData.Datum> data;
    String status;
    private static ClickListener clickListener;
    boolean color_value;
    boolean Todo_monitering;

    public AdapterNewTask(Context context, List<NewTaskData.Datum> data, String status, boolean color_value, boolean Todo_monitering) {
        this.context = context;
        this.data = data;
        this.status = status;
        this.color_value = color_value;
        this.Todo_monitering = Todo_monitering;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView count_tv, name_tv,
                assigned_date_tv, assigned_name_tv, subject_tv, status_tv,
                task_comp_date_tv, date_tv, task_comp_tv, assigned_by_name_tv, assigned_by_tv, task_compon_date_tv;
        LinearLayout status_ll, ll_assignedto, ll_taskcomdate, ll_taskcomon, ll_subject, ll_clickable;
        RelativeLayout rl_name;
        CardView card_view;

        public MyViewHolder(View v) {
            super(v);
            name_tv = (TextView) v.findViewById(R.id.name_tv);
            date_tv = (TextView) v.findViewById(R.id.date_tv);
            subject_tv = (TextView) v.findViewById(R.id.subject_tv);
            card_view = (CardView) v.findViewById(R.id.card_view);
            ll_subject = (LinearLayout) v.findViewById(R.id.ll_subject);
            ll_clickable = (LinearLayout) v.findViewById(R.id.ll_clickable);
            rl_name = (RelativeLayout) v.findViewById(R.id.rl_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newtask_fragment, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (Todo_monitering == true) {
            if (data.get(position).getAssign_to() != null && !data.get(position).getAssign_to().isEmpty() && data.get(position).getAssign_to_designation() != null && !data.get(position).getAssign_to_designation().isEmpty()) {
                holder.name_tv.setText(data.get(position).getCreatedBy() + "-" + data.get(position).getAssign_by_desination());
            }
        } else {
            if (data.get(position).getAssign_to() != null && !data.get(position).getAssign_to().isEmpty() && data.get(position).getAssign_to_designation() != null && !data.get(position).getAssign_to_designation().isEmpty()) {
                holder.name_tv.setText(data.get(position).getAssign_to() + "-" + data.get(position).getAssign_to_designation());
            }
        }

        /*else {
            holder.ll_subject.setVisibility(View.GONE);
        }*/
        if (data.get(position).getTask_completion_date() != null && !data.get(position).getTask_completion_date().isEmpty()) {
            holder.date_tv.setText(AndroidUtil.formatMMDate(data.get(position).getTask_completion_date(), "dd-MM-yyyy", "MMM dd"));
        }
        if (data.get(position).getTaskSubject() != null && !data.get(position).getTaskSubject().isEmpty()) {
            holder.subject_tv.setText(data.get(position).getTaskSubject());
        }/*else {
            holder.rl_name.setVisibility(View.GONE);
        }*/

        if (color_value == true) {
            if (data.get(position).getColor() != null && !data.get(position).getColor().isEmpty()) {
                if (data.get(position).getColor().contains("1")) {
                    holder.card_view.setBackgroundColor(Color.parseColor("#ff3232"));
                } else if (data.get(position).getColor().contains("2")) {
                    holder.card_view.setBackgroundColor(Color.parseColor("#ffff00"));
                } else {
                    holder.card_view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

            } else {
                holder.card_view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        AdapterNewTask.clickListener = clickListener;
    }


    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}

