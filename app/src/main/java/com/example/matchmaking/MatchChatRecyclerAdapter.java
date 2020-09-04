package com.example.matchmaking;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MatchChatRecyclerAdapter extends RecyclerView.Adapter<MatchChatRecyclerAdapter.ViewHolder>{

    private ArrayList<MatchChatRecyclerItem> chat_list = new ArrayList<>();
    private String userid;
    private String roomid;

    public MatchChatRecyclerAdapter(String userid,String roomid){
        this.userid = userid;
        this.roomid = roomid;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchChatRecyclerAdapter.ViewHolder holder, int position) {
        MatchChatRecyclerItem item = chat_list.get(position);

        if(item.getUserid() != null && userid != null && item.getUserid().equals(userid)){
            holder.opponent_name.setVisibility(View.GONE);
            holder.opponent_text.setVisibility(View.GONE);
            holder.opponent_time.setVisibility(View.GONE);

            holder.my_name.setText(item.getNickname());
            holder.my_text.setText(item.getText());
            holder.my_time.setText(item.getTexttime());

            holder.my_name.setVisibility(View.VISIBLE);
            holder.my_text.setVisibility(View.VISIBLE);
            holder.my_time.setVisibility(View.VISIBLE);
            if(position - 1 >= 0 && chat_list.get(position-1).getUserid().equals(chat_list.get(position).getUserid()))
                holder.my_name.setVisibility(View.GONE);
        }else if(item.getUserid() != null && userid != null && !item.getUserid().equals(userid)){
            holder.my_name.setVisibility(View.GONE);
            holder.my_text.setVisibility(View.GONE);
            holder.my_time.setVisibility(View.GONE);

            holder.opponent_name.setText(item.getNickname());
            holder.opponent_text.setText(item.getText());
            holder.opponent_time.setText(item.getTexttime());

            holder.opponent_name.setVisibility(View.VISIBLE);
            holder.opponent_text.setVisibility(View.VISIBLE);
            holder.opponent_time.setVisibility(View.VISIBLE);
            if(position - 1 >= 0 && chat_list.get(position-1).getUserid().equals(chat_list.get(position).getUserid()))
                holder.opponent_name.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.match_chat_item,parent,false);
        MatchChatRecyclerAdapter.ViewHolder tvh = new MatchChatRecyclerAdapter.ViewHolder(view);
        return tvh;
    }

    @Override
    public int getItemCount() {
        return chat_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView opponent_name;
        private TextView opponent_text;
        private TextView opponent_time;
        private TextView my_name;
        private TextView my_text;
        private TextView my_time;

        ViewHolder(View itemView){
            super(itemView);
            opponent_name = itemView.findViewById(R.id.match_chat_opponent_name);
            opponent_text = itemView.findViewById(R.id.match_chat_opponent_text);
            opponent_time = itemView.findViewById(R.id.match_chat_opponent_time);

            my_name = itemView.findViewById(R.id.match_chat_my_name);
            my_text = itemView.findViewById(R.id.match_chat_my_text);
            my_time = itemView.findViewById(R.id.match_chat_my_time);
        }
    }

    public void additem(String userid, String text, String time,String nickname, String roomid){
        chat_list.add(new MatchChatRecyclerItem(userid,text,time,nickname,roomid));
    }

    public ArrayList<MatchChatRecyclerItem> getChat_list() {
        return chat_list;
    }
}
