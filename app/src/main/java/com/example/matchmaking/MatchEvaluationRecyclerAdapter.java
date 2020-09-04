package com.example.matchmaking;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchEvaluationRecyclerAdapter extends RecyclerView.Adapter<MatchEvaluationRecyclerAdapter.ViewHolder> {

    private ArrayList<User> recyclerItems = new ArrayList<>();
    private Context mContext;

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public MatchEvaluationRecyclerAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.match_evaluation_item,parent,false);
        MatchEvaluationRecyclerAdapter.ViewHolder tvh = new MatchEvaluationRecyclerAdapter.ViewHolder(view);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchEvaluationRecyclerAdapter.ViewHolder holder, int position) {
        holder.nickname.setText(recyclerItems.get(position).getNickname());
        holder.amusedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItems.get(position).getUserEval().setAmused(recyclerItems.get(position).getUserEval().getAmused()+1);
                RetrofitHelper.getApiService().updateUser(recyclerItems.get(position).getId(),recyclerItems.get(position)).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e("eval error: ",t.getMessage());
                    }
                });
                recyclerItems.remove(position);
                notifyDataSetChanged();
                if(recyclerItems.size() == 0)
                    ((Activity)mContext).finish();
            }
        });

        holder.mentalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItems.get(position).getUserEval().setMental(recyclerItems.get(position).getUserEval().getMental()+1);
                RetrofitHelper.getApiService().updateUser(recyclerItems.get(position).getId(),recyclerItems.get(position)).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e("eval error: ",t.getMessage());
                    }
                });
                recyclerItems.remove(position);
                notifyDataSetChanged();
                if(recyclerItems.size() == 0)
                    ((Activity)mContext).finish();
            }
        });

        holder.leadershipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItems.get(position).getUserEval().setLeadership(recyclerItems.get(position).getUserEval().getLeadership()+1);
                RetrofitHelper.getApiService().updateUser(recyclerItems.get(position).getId(),recyclerItems.get(position)).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e("eval error: ",t.getMessage());
                    }
                });
                recyclerItems.remove(position);
                notifyDataSetChanged();
                if(recyclerItems.size() == 0)
                    ((Activity)mContext).finish();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        Button amusedbtn;
        Button mentalbtn;
        Button leadershipbtn;
        TextView nickname;

        ViewHolder(View itemView){
            super(itemView);
            nickname = itemView.findViewById(R.id.match_eval_nickname);
            amusedbtn = itemView.findViewById(R.id.match_eval_amused);
            mentalbtn = itemView.findViewById(R.id.match_eval_mental);
            leadershipbtn = itemView.findViewById(R.id.match_eval_leadership);
        }
    }

    public void additem(User user){
        recyclerItems.add(user);
    }

    public Drawable gettierimg(String tier){
        switch (tier){
            case "Challenger":
                return mContext.getResources().getDrawable(R.drawable.emblem_challenger_36);
            case "GrandMaster":
                return mContext.getResources().getDrawable(R.drawable.emblem_grandmaster_36);
            case "Master":
                return mContext.getResources().getDrawable(R.drawable.emblem_master_36);
            case "Diamond":
                return mContext.getResources().getDrawable(R.drawable.emblem_diamond_36);
            case "Platinum":
                return mContext.getResources().getDrawable(R.drawable.emblem_platinum_36);
            case "Gold":
                return mContext.getResources().getDrawable(R.drawable.emblem_gold_36);
            case "Silver":
                return mContext.getResources().getDrawable(R.drawable.emblem_silver_36);
            case "Bronze":
                return mContext.getResources().getDrawable(R.drawable.emblem_bronze_36);
            case "Iron":
                return mContext.getResources().getDrawable(R.drawable.emblem_iron_36);
            default:
                return mContext.getResources().getDrawable(R.drawable.emblem_iron_36);
        }
    }
}
