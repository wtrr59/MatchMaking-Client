package com.example.matchmaking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchChatActivity extends AppCompatActivity {

    private String userid;
    private String roomid;
    private MatchChatRecyclerAdapter matchChatRecyclerAdapter;
    private EditText editText;
    private Button acceptbtn;
    private RecyclerView recyclerView;
    private Socket mSocket;
    private User user;
    private boolean response_catch = false;
    private Gson gson = new Gson();
    private boolean isgetted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_chat);

        userid = getIntent().getStringExtra("userid");
        roomid = getIntent().getStringExtra("roomid");

        acceptbtn = findViewById(R.id.match_chat_acceptbtn);
        editText = findViewById(R.id.match_chat_edittxt);

        matchChatRecyclerAdapter = new MatchChatRecyclerAdapter(userid,roomid);

        recyclerView = findViewById(R.id.match_chat_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(matchChatRecyclerAdapter);

        RetrofitHelper.getApiService().getChats(roomid).enqueue(new Callback<List<MatchChatRecyclerItem>>() {
            @Override
            public void onResponse(Call<List<MatchChatRecyclerItem>> call, Response<List<MatchChatRecyclerItem>> response) {
                List<MatchChatRecyclerItem> arrayList = response.body();
                if(arrayList != null) {
                    matchChatRecyclerAdapter.getChat_list().clear();
                    for (int i = 0; i < arrayList.size(); i++) {
                        matchChatRecyclerAdapter.additem(arrayList.get(i).getUserid(), arrayList.get(i).getText(), arrayList.get(i).getTexttime(), arrayList.get(i).getNickname(), arrayList.get(i).getRoomid());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                matchChatRecyclerAdapter.notifyDataSetChanged();
                                recyclerView.invalidateItemDecorations();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MatchChatRecyclerItem>> call, Throwable t) {
                Log.e("chatgeterror",t.getMessage());
            }
        });

        RetrofitHelper.getApiService().receiveUser(userid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                response_catch = true;
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ChatError",t.getMessage());
            }
        });

        RetrofitHelper.getApiService().receiveUser(userid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        try {
            mSocket = IO.socket("http://192.249.19.251:8780");
            mSocket.connect();
            mSocket.on("arrivednewmsg", onChatArrived);
        } catch (Exception e) {
            e.printStackTrace();
        }



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edit_check();
            }
        });

        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (!response_catch);
                // sendnewmsg

                JsonObject readyInfo = new JsonObject();
                readyInfo.addProperty("chatuserid", user.getId());
                readyInfo.addProperty("text", editText.getText().toString());
                readyInfo.addProperty("texttime", new SimpleDateFormat("HH:mm").format(new Date()));
                readyInfo.addProperty("nickname", user.getNickname());
                readyInfo.addProperty("roomid", roomid);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(readyInfo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("sendnewmsg", jsonObject);
                editText.setText("");
            }
        });
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
                            recyclerView.invalidateItemDecorations();
                            recyclerView.scrollToPosition(matchChatRecyclerAdapter.getItemCount() - 1);
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    private void edit_check() {
        if(editText.getText().toString().trim().length() == 0){
            acceptbtn.setVisibility(View.INVISIBLE);
            acceptbtn.setEnabled(false);
        }else{
            acceptbtn.setVisibility(View.VISIBLE);
            acceptbtn.setEnabled(true);
        }
    }
}
