package com.example.matchmaking;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MatchRoomRecyclerAdapter extends RecyclerView.Adapter<MatchRoomRecyclerAdapter.ViewHolder> {

    private ArrayList<MatchRoomRecyclerItem> userlist = new ArrayList<>();
    private Context mContext;
    private OnUserClickListener mListener = null;

    public interface OnUserClickListener{
        void OnItemClick(View v, int position);
    }


    public MatchRoomRecyclerAdapter(Context context){
        this.mContext = context;
    }
    public void setOnUserClickListener(OnUserClickListener listener) { this.mListener = listener; }


    @NonNull
    @Override
    public MatchRoomRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.match_room_item,parent,false);
        MatchRoomRecyclerAdapter.ViewHolder tvh = new MatchRoomRecyclerAdapter.ViewHolder(view);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchRoomRecyclerAdapter.ViewHolder holder, int position) {
        MatchRoomRecyclerItem item = userlist.get(position);
        if(item.getTierimg() == true)
            holder.tierimg.setImageDrawable(gettierimg(item.getTiertxt()));
        else
            holder.tierimg.setImageDrawable(mContext.getDrawable(R.drawable.outline_check_green_36));
        holder.nickname.setText(item.getNickname());
        holder.tiertxt.setText(item.getTiertxt());
        holder.positiontxt.setText(item.getPositiontxt());
        holder.voice.setText(item.getVoice());
    }


    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView tierimg;
        private TextView nickname;
        private TextView tiertxt;
        private TextView positiontxt;
        private TextView voice;

        ViewHolder(View itemView){
            super(itemView);
            tierimg = itemView.findViewById(R.id.match_room_item_tier_img);
            nickname = itemView.findViewById(R.id.match_room_item_id);
            tiertxt = itemView.findViewById(R.id.match_room_item_tier_write);
            positiontxt = itemView.findViewById(R.id.match_room_item_position_write);
            voice = itemView.findViewById(R.id.match_room_item_voice_write);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(RecyclerView.NO_POSITION != getAdapterPosition() && mListener != null)
                        mListener.OnItemClick(v,getAdapterPosition());
                }
            });
        }
    }

    public void additem(User user){
        userlist.add(new MatchRoomRecyclerItem(user.getId(),user.getTier(),user.getPosition(),user.getVoice(),user.getNickname()));
    }

    public ArrayList<MatchRoomRecyclerItem> getUserlist() {
        return userlist;
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
