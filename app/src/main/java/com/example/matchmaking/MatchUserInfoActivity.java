package com.example.matchmaking;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchUserInfoActivity extends AppCompatActivity {

    private String userid;
    private User user;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;

    private TextView nicknametxt;
    private ImageView tierimg;

    private TextView tiertxt;
    private TextView positiontxt;
    private TextView voicetxt;
    private TextView aboutMetxt;

    private TextView amusednum;
    private TextView mentalnum;
    private TextView leadershipnum;

    private final static int EVALUATION_MAX_NUM = 200;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_user_info);

        userid = getIntent().getStringExtra("someoneid");

        nicknametxt = findViewById(R.id.match_userinfo_id);
        tierimg = findViewById(R.id.match_userinfo_tier_img);

        tiertxt = findViewById(R.id.match_userinfo_tier_write);
        positiontxt = findViewById(R.id.match_userinfo_position_write);
        voicetxt = findViewById(R.id.match_userinfo_voice_write);
        aboutMetxt = findViewById(R.id.match_userinfo_introduce_write);

        amusednum = findViewById(R.id.match_userinfo_status_num1);
        mentalnum = findViewById(R.id.match_userinfo_status_num2);
        leadershipnum = findViewById(R.id.match_userinfo_status_num3);

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

                tierimg.setImageDrawable(gettierimg(user.getTier()));
                nicknametxt.setText(user.getId());
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
                Log.e("UserInfo: ",t.getMessage());
            }
        });
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

}
