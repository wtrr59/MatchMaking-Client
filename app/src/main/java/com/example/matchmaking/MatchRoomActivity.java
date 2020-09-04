package com.example.matchmaking;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchRoomActivity extends AppCompatActivity {

    private String userid;
    private String roomid;
    private MatchRoomRecyclerAdapter matchRoomRecyclerAdapter;
    private RecyclerView recyclerView;
    private MatchChatRecyclerAdapter matchChatRecyclerAdapter;
    private RecyclerView chatrecyclerView;
    private Button chatbtn;
    private Activity activity;
    private Boolean isready = false;
    private Button readybtn;
    private ArrayList<String> userlist;
    private Socket mSocket;
    private long backKeyPressedTime = 0;
    private Boolean isEvaluate = false;
    boolean isout = false;
    private int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_room);

        activity = this;

        userlist = getIntent().getStringArrayListExtra("userList");
        Log.e("user: ",userlist.get(0));
        userid = getIntent().getStringExtra("userid");
        roomid = getIntent().getStringExtra("roomName");

        //room
        matchRoomRecyclerAdapter = new MatchRoomRecyclerAdapter(getApplicationContext());
        matchRoomRecyclerAdapter.setOnUserClickListener(new MatchRoomRecyclerAdapter.OnUserClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(),MatchUserInfoActivity.class);
                intent.putExtra("someoneid",matchRoomRecyclerAdapter.getUserlist().get(position).getId());
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.match_room_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(matchRoomRecyclerAdapter);

        //chat
        matchChatRecyclerAdapter = new MatchChatRecyclerAdapter(userid,roomid);

        chatrecyclerView = findViewById(R.id.match_room_chat_recycler);
        chatrecyclerView.setClickable(true);
        chatrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatrecyclerView.setAdapter(matchChatRecyclerAdapter);

        try {
            mSocket = IO.socket("http://192.249.19.251:8780");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, connected); //Socket.EVENT_CONNECT : 연결이 성공하면 발생하는 이벤트, onConnect : callback 객체
            mSocket.on("receiveReady", onReceiveReady);
            mSocket.on("receiveUnReady", onReceiveUnReady);
            mSocket.on("arrivednewmsg", onChatArrived);
            mSocket.on("leaveRoom", onLeaveRoom);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        for(int i = 0; i < userlist.size(); i++){
            String user_id = userlist.get(i);
            RetrofitHelper.getApiService().receiveUser(user_id).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    matchRoomRecyclerAdapter.additem(user);
                    matchRoomRecyclerAdapter.notifyDataSetChanged();
                    recyclerView.invalidateItemDecorations();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("MatchRoomActivity",t.getMessage());
                }
            });
        }

        chatbtn = findViewById(R.id.match_room_chat_btn);
        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MatchChatActivity.class);
                intent.putExtra("userid",userid);
                intent.putExtra("roomid",roomid);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,chatbtn,"custom_transition1");
                startActivity(intent,options.toBundle());
            }
        });

        readybtn = findViewById(R.id.match_room_ready);
        readybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEvaluate){
                    Intent intent = new Intent(getApplicationContext(), MatchEvaluationActivity.class);
                    intent.putStringArrayListExtra("userlist", userlist);
                    intent.putExtra("userid", userid);
                    RetrofitHelper.getApiService().deleteChats(roomid).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.e("deleteOK",response.body());
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("deleteFailed",t.getMessage());
                        }
                    });
                }
                else {
                    //소켓에 신호
                    if (isready == false) {
                        JsonObject readyInfo = new JsonObject();
                        readyInfo.addProperty("userId", userid);
                        readyInfo.addProperty("roomId", roomid);

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(readyInfo.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mSocket.emit("ready", jsonObject);

                        v.animate().translationY(-500);
                        isready = true;
                    }
                }
            }
        });

        findViewById(R.id.match_room_ready_cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isready == true) {
                    JsonObject readyInfo = new JsonObject();
                    readyInfo.addProperty("userId", userid);
                    readyInfo.addProperty("roomId", roomid);

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(readyInfo.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mSocket.emit("unready", jsonObject);

                    readybtn.animate().translationY(0);
                    isready = false;
                }
            }
        });
    }


    private Emitter.Listener connected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("MatchRoomActivity", "소켓 연결");
        }
    };

    private Emitter.Listener onReceiveReady = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JsonParser jsonParser = new JsonParser();
            JsonObject readyInfo = (JsonObject) jsonParser.parse(args[0] + "");
            try {
                String readyUser = readyInfo.get("userId").getAsString();
                Log.d("readyUser", readyUser + "      " + matchRoomRecyclerAdapter.getUserlist().get(0).getId());
                String readyRoom = readyInfo.get("roomId").getAsString();
                Log.d("readyRoom", readyRoom + "    " + roomid);
                if (!readyRoom.equals(roomid)) return;
                for (int i = 0; i < matchRoomRecyclerAdapter.getUserlist().size(); i++)
                    if (matchRoomRecyclerAdapter.getUserlist().get(i).getId().equals(readyUser))
                        matchRoomRecyclerAdapter.getUserlist().get(i).setTierimg(false);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        matchRoomRecyclerAdapter.notifyDataSetChanged();
                        recyclerView.invalidateItemDecorations();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < matchRoomRecyclerAdapter.getUserlist().size(); i++)
                if(matchRoomRecyclerAdapter.getUserlist().get(i).getTierimg()) return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    count = 5;
                    final TextView tv = findViewById(R.id.match_room_phrase1);
                    findViewById(R.id.match_room_ready_cardview).setEnabled(false);
                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {
                            tv.setText(count + "");
                            count--;
                            if(count < 0){
                                readybtn.animate().translationY(0);
                                readybtn.setText("평가하기");
                                isEvaluate = true;
                                cancel();
                            }
                        }

                    };
                    Timer timer = new Timer();
                    timer.schedule(tt, 0, 1000);


                }
            });
        }
    };

    private Emitter.Listener onReceiveUnReady = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JsonParser jsonParser = new JsonParser();
            JsonObject unreadyInfo = (JsonObject) jsonParser.parse(args[0] + "");
            try {
                String unreadyUser = unreadyInfo.get("userId").getAsString();
                Log.d("unreadyUser", unreadyUser);
                String unreadyRoom = unreadyInfo.get("roomId").getAsString();
                Log.d("unreadyRoom", unreadyRoom);
                if (!unreadyRoom.equals(roomid)) return;
                for (int j = 0; j < matchRoomRecyclerAdapter.getUserlist().size(); j++)
                    if (matchRoomRecyclerAdapter.getUserlist().get(j).getId().equals(unreadyUser))
                        matchRoomRecyclerAdapter.getUserlist().get(j).setTierimg(true);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        matchRoomRecyclerAdapter.notifyDataSetChanged();
                        recyclerView.invalidateItemDecorations();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onLeaveRoom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JsonParser jsonParsers = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParsers.parse(args[0] + "");
            if(roomid.equals(jsonObject.get("roomid").getAsString())) {
                Log.d("wer", "여기");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isEvaluate) return;
                        Toast.makeText(getApplicationContext(), "닷지 발생", Toast.LENGTH_SHORT).show();
                        mSocket.disconnect();
                        finish();
                    }
                });
            }
        }
    };

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "\'뒤로\'버튼을 한번 더 누르시면 매칭이 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            JsonObject userInfo = new JsonObject();
            userInfo.addProperty("roomid", roomid);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(userInfo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("leaveRoom", jsonObject);
            RetrofitHelper.getApiService().deleteChats(roomid).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("deleteOK",response.body());
                    isout = true;
                    finish();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("deleteFailed",t.getMessage());
                }
            });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        JsonObject userInfo = new JsonObject();
        userInfo.addProperty("roomid", roomid);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(userInfo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("leaveRoom", jsonObject);
        mSocket.disconnect();
    }


    private Emitter.Listener onChatArrived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // arrivednewmsg
            JsonParser jsonParser = new JsonParser();
            JsonObject chatinfo = (JsonObject) jsonParser.parse(args[0] + "");
            try {
                String arrived_roomid = chatinfo.get("roomid").getAsString();
                if(arrived_roomid.equals(roomid)) {
                    matchChatRecyclerAdapter.additem(chatinfo.get("chatuserid").getAsString(), chatinfo.get("text").getAsString(), chatinfo.get("texttime").getAsString(), chatinfo.get("nickname").getAsString(), arrived_roomid);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            matchChatRecyclerAdapter.notifyDataSetChanged();
                            chatrecyclerView.invalidateItemDecorations();
                            chatrecyclerView.scrollToPosition(matchChatRecyclerAdapter.getItemCount() - 1);
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
