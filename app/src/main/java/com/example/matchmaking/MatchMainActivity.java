package com.example.matchmaking;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.HEAD;


public class MatchMainActivity extends AppCompatActivity {

    private io.socket.client.Socket mSocket;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;

    TextView nicknametxt;
    ImageView tierimg;

    TextView tiertxt;
    TextView positiontxt;
    TextView voicetxt;
    private TextView aboutMetxt;

    TextView amusednum;
    TextView mentalnum;
    TextView leadershipnum;

    private String userid = "";

    User user;

    private final static int EVALUATION_MAX_NUM = 200;

    Retrofit retrofit;
    RetrofitInterface retrofitInterface;

    private boolean issetted = false;
    private Button match_start_btn;
    private ImageButton settingButton;
    private boolean ismatching = false;
    private boolean domatched = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_main);

        userid = getIntent().getStringExtra("userId");

        nicknametxt = findViewById(R.id.match_main_id);
        tierimg = findViewById(R.id.match_main_tier_img);

        tiertxt = findViewById(R.id.match_main_tier_write);
        positiontxt = findViewById(R.id.match_main_position_write);
        voicetxt = findViewById(R.id.match_main_voice_write);
        aboutMetxt = findViewById(R.id.match_main_introduce_write);

        amusednum = findViewById(R.id.match_status_num1);
        mentalnum = findViewById(R.id.match_status_num2);
        leadershipnum = findViewById(R.id.match_status_num3);

        progressBar1 = findViewById(R.id.match_status_progress1);
        progressBar2 = findViewById(R.id.match_status_progress2);
        progressBar3 = findViewById(R.id.match_status_progress3);

        progressBar1.setMax(EVALUATION_MAX_NUM);
        progressBar2.setMax(EVALUATION_MAX_NUM);
        progressBar3.setMax(EVALUATION_MAX_NUM);


        RetrofitHelper.getApiService().receiveUser(userid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                Log.e("Success", user.getId());
                tierimg.setImageDrawable(gettierimg(user.getTier()));
                nicknametxt.setText(user.getNickname());
                tiertxt.setText(user.getTier());
                positiontxt.setText(user.getPosition());
                voicetxt.setText(user.getVoice());
                aboutMetxt.setText(user.getAboutMe());
                amusednum.setText(Integer.toString(user.getUserEval().getAmused()));
                mentalnum.setText(Integer.toString(user.getUserEval().getMental()));
                leadershipnum.setText(Integer.toString(user.getUserEval().getLeadership()));

                runOnUiThread(new ProgressBarRunnable(progressBar1, 0, user.getUserEval().getAmused()));
                runOnUiThread(new ProgressBarRunnable(progressBar2, 0, user.getUserEval().getMental()));
                runOnUiThread(new ProgressBarRunnable(progressBar3, 0, user.getUserEval().getLeadership()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("error",t.getMessage());
            }
        });

        RetrofitHelper.getApiService().receiveUser(getIntent().getExtras().getString("userId")).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                Log.e("Success", user.getId());
                tierimg.setImageDrawable(gettierimg(user.getTier()));
                nicknametxt.setText(user.getNickname());
                tiertxt.setText(user.getTier());
                positiontxt.setText(user.getPosition());
                voicetxt.setText(user.getVoice());
                amusednum.setText(Integer.toString(user.getUserEval().getAmused()));
                mentalnum.setText(Integer.toString(user.getUserEval().getMental()));
                leadershipnum.setText(Integer.toString(user.getUserEval().getLeadership()));

                runOnUiThread(new ProgressBarRunnable(progressBar1, 0, user.getUserEval().getAmused()));
                runOnUiThread(new ProgressBarRunnable(progressBar2, 0, user.getUserEval().getMental()));
                runOnUiThread(new ProgressBarRunnable(progressBar3, 0, user.getUserEval().getLeadership()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Get Failed",t.getMessage());
            }
        });


        settingButton = (ImageButton) findViewById(R.id.match_main_setting);
        match_start_btn = findViewById(R.id.match_main_start);

        match_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(issetted == true) {
                    if(ismatching == false) {
                        try {
                            domatched = true;

                            mSocket = IO.socket("http://192.249.19.251:8780");
                            mSocket.connect();
                            mSocket.on(Socket.EVENT_CONNECT, onMatchStart); //Socket.EVENT_CONNECT : 연결이 성공하면 발생하는 이벤트, onConnect : callback 객체
                            mSocket.on("matchComplete", onMatchComplete);

                            match_start_btn.setText("MATCHING...");
                            match_start_btn.setBackgroundColor(getResources().getColor(R.color.canclecolor));
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                            ismatching = true;
                            settingButton.setClickable(false);
                            settingButton.startAnimation(animation);

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }else{
                        settingButton.setClickable(true);
                        ismatching = false;
                        settingButton.clearAnimation();
                        match_start_btn.setText("MATCHING START");
                        match_start_btn.setBackgroundColor(getResources().getColor(R.color.MatchButtonColor));
                        sendUnRooms();
                    }
                }else
                    Toast.makeText(getApplicationContext(),"설정을 완료해주세요.",Toast.LENGTH_SHORT).show();
            }
        });


        ImageButton settingButton = (ImageButton) findViewById(R.id.match_main_setting);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), SettingActivity.class);
                intent2.putExtra("userId", user.getId());
                startActivityForResult(intent2, 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            RetrofitHelper.getApiService().receiveUser(user.getId()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    user = response.body();
                    //view update
                    tierimg.setImageDrawable(gettierimg(user.getTier()));
                    nicknametxt.setText(user.getNickname());
                    tiertxt.setText(user.getTier());
                    positiontxt.setText(user.getPosition());
                    voicetxt.setText(user.getVoice());
                    aboutMetxt.setText(user.getAboutMe());
                    amusednum.setText(Integer.toString(user.getUserEval().getAmused()));
                    mentalnum.setText(Integer.toString(user.getUserEval().getMental()));
                    leadershipnum.setText(Integer.toString(user.getUserEval().getLeadership()));

                    runOnUiThread(new ProgressBarRunnable(progressBar1, 0, user.getUserEval().getAmused()));
                    runOnUiThread(new ProgressBarRunnable(progressBar2, 0, user.getUserEval().getMental()));
                    runOnUiThread(new ProgressBarRunnable(progressBar3, 0, user.getUserEval().getLeadership()));

                    issetted = true;
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("MatchMainActivity", t.toString());
                }
            });
        }
    }


    //matching start 버튼 누르면 소켓에 연결하고 User 정보를 보냄
    private Emitter.Listener onMatchStart = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            sendRooms();
        }
    };

    public void sendRooms(){
        int roomNumber;
        List<Integer> roomNumbers = new ArrayList<>();
        if(Numbering.tendency(user.getHope_tendency()) == 2 && Numbering.voice(user.getHope_voice()) == 2){
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), 0, 0, user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), 0, 1, user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), 1, 0, user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), 1, 1, user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
        }
        else if(Numbering.tendency(user.getHope_tendency()) == 2){
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), 0, Numbering.voice(user.getHope_voice()), user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), 1, Numbering.voice(user.getHope_voice()), user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
        }
        else if(Numbering.voice(user.getHope_voice()) == 2){
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), Numbering.tendency(user.getHope_tendency()), 0, user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), Numbering.tendency(user.getHope_tendency()), 1, user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
        }
        else {
            roomNumber = Numbering.room(Numbering.tier(user.getTier()), Numbering.tendency(user.getHope_tendency()), Numbering.voice(user.getHope_voice()), user.getHope_num() - 2);
            roomNumbers.add(roomNumber);
        }

        JsonObject userInfo = new JsonObject();
        userInfo.addProperty("userId", user.getId());
        userInfo.addProperty("userPosi", user.getPosition());
        userInfo.addProperty("roomNumbers", roomNumbers.toString());

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(userInfo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("enterRoom", jsonObject);
    }

    public void sendUnRoom(int num){
        JsonObject userInfo = new JsonObject();
        userInfo.addProperty("userId", user.getId());
        userInfo.addProperty("userPosi", user.getPosition());
        userInfo.addProperty("roomNumber", num);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(userInfo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("exitRoom", jsonObject);
    }

    public void sendUnRooms(){
        int roomNumber;

        roomNumber = Numbering.room(Numbering.tier(user.getTier()), 0, 0, user.getHope_num() - 2);
        sendUnRoom(roomNumber);
        roomNumber = Numbering.room(Numbering.tier(user.getTier()), 0, 1, user.getHope_num() - 2);
        sendUnRoom(roomNumber);
        roomNumber = Numbering.room(Numbering.tier(user.getTier()), 1, 0, user.getHope_num() - 2);
        sendUnRoom(roomNumber);
        roomNumber = Numbering.room(Numbering.tier(user.getTier()), 1, 1, user.getHope_num() - 2);
        sendUnRoom(roomNumber);

        mSocket.disconnect();
    }

    //
    private Emitter.Listener onMatchComplete = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(ismatching == true) {
                int check = 0;
                final String receiveData = args[0].toString();
                Log.d("matched", receiveData);
                StringTokenizer st = new StringTokenizer(receiveData, "\"");
                final ArrayList<String> userList = new ArrayList<String>();
                while (st.hasMoreElements()) {
                    String userId_ = st.nextToken();
                    if (userId_.equals("[") || userId_.equals("]") || userId_.equals(",")) continue;
                    if (user.getId().equals(userId_)) check++;
                    userList.add(userId_);
                }
                Log.d("check", check + "");
                Log.d("user", userList.get(0));
                if (check == 0) return;
                Intent intent = new Intent(getApplicationContext(), MatchRoomActivity.class);
                intent.putExtra("userid", user.getId());
                intent.putExtra("roomName", receiveData);
                intent.putStringArrayListExtra("userList", userList);

                sendUnRooms();
                mSocket.disconnect();
                ismatching = false;
                settingButton.clearAnimation();
                settingButton.setClickable(true);
                match_start_btn.setText("MATCHING START");
                match_start_btn.setBackgroundColor(getResources().getColor(R.color.MatchButtonColor));

                ismatching = false;
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(domatched) {
            sendUnRooms();
            mSocket.disconnect();
        }
    }


    @Override
    public void onBackPressed() {
        if(ismatching == false)
            super.onBackPressed();
        else {
            Toast.makeText(getApplicationContext(), "매칭을 취소합니다.", Toast.LENGTH_SHORT).show();
            settingButton.setClickable(true);
            ismatching = false;
            settingButton.clearAnimation();
            match_start_btn.setText("MATCHING START");
            match_start_btn.setBackgroundColor(getResources().getColor(R.color.MatchButtonColor));
            sendUnRooms();
            mSocket.disconnect();
        }
    }

    public Drawable gettierimg(String tier){
        switch (tier){
            case "Challenger":
                return getResources().getDrawable(R.drawable.emblem_challenger_128);
            case "GrandMaster":
                return getResources().getDrawable(R.drawable.emblem_grandmaster_128);
            case "Master":
                return getResources().getDrawable(R.drawable.emblem_master_128);
            case "Diamond":
                return getResources().getDrawable(R.drawable.emblem_diamond_128);
            case "Platinum":
                return getResources().getDrawable(R.drawable.emblem_platinum_128);
            case "Gold":
                return getResources().getDrawable(R.drawable.emblem_gold_128);
            case "Silver":
                return getResources().getDrawable(R.drawable.emblem_silver_128);
            case "Bronze":
                return getResources().getDrawable(R.drawable.emblem_bronze_128);
            case "Iron":
                return getResources().getDrawable(R.drawable.emblem_iron_128);
            default:
                return getResources().getDrawable(R.drawable.emblem_iron_128);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RetrofitHelper.getApiService().receiveUser(userid).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();

                amusednum.setText(Integer.toString(user.getUserEval().getAmused()));
                mentalnum.setText(Integer.toString(user.getUserEval().getMental()));
                leadershipnum.setText(Integer.toString(user.getUserEval().getLeadership()));

                runOnUiThread(new ProgressBarRunnable(progressBar1, 0, user.getUserEval().getAmused()));
                runOnUiThread(new ProgressBarRunnable(progressBar2, 0, user.getUserEval().getMental()));
                runOnUiThread(new ProgressBarRunnable(progressBar3, 0, user.getUserEval().getLeadership()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("error resume",t.getMessage());
            }
        });
    }
}
