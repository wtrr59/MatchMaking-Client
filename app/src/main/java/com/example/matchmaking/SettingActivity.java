package com.example.matchmaking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends Activity {

    User user;

    EditText userNick_e;
    Spinner userTier_s;
    Spinner userPosi_s;
    Spinner userVoic_s;
    EditText userAboutMe_e;

    Spinner hope_tendency_s;
    Spinner hope_voice_s;
    Spinner hope_num_s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        userNick_e = (EditText)findViewById(R.id.setNick);
        userTier_s = (Spinner)findViewById(R.id.setTier);
        userPosi_s = (Spinner)findViewById(R.id.setPosi);
        userVoic_s = (Spinner)findViewById(R.id.setVoic);
        userAboutMe_e = (EditText)findViewById(R.id.setAboutMe);

        hope_tendency_s = (Spinner)findViewById(R.id.setHope_tendency);
        hope_voice_s = (Spinner)findViewById(R.id.setHope_voice);
        hope_num_s = (Spinner)findViewById(R.id.setHope_Num);

        Intent intent1 = getIntent();
        String userId = intent1.getExtras().getString("userId");
        RetrofitHelper.getApiService().receiveUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                userNick_e.setText(user.getNickname());
                userTier_s.setSelection(Numbering.tier(user.getTier()));
                userPosi_s.setSelection(Numbering.position(user.getPosition()));
                userVoic_s.setSelection(Numbering.voice(user.getVoice()));
                userAboutMe_e.setText(user.getAboutMe());

                if(user.getHope_tendency() != null)
                    hope_tendency_s.setSelection(Numbering.tendency(user.getHope_tendency()));
                if(user.getHope_voice() != null)
                    hope_voice_s.setSelection(Numbering.voice(user.getHope_voice()));
                if(user.getHope_num() != 0)
                    hope_num_s.setSelection(Numbering.num(user.getHope_num()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("SettingActivity", t.toString());
            }
        });

        Button settingComplete = (Button)findViewById(R.id.settingComplete);
        settingComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userNick_e.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),"닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                user.setNickname(userNick_e.getText().toString());
                user.setTier(userTier_s.getSelectedItem().toString());
                user.setPosition(userPosi_s.getSelectedItem().toString());
                user.setVoice(userVoic_s.getSelectedItem().toString());
                user.setAboutMe(userAboutMe_e.getText().toString());
                user.setHope_tendency(hope_tendency_s.getSelectedItem().toString());
                user.setHope_voice(hope_voice_s.getSelectedItem().toString());
                user.setHope_num(Integer.parseInt(hope_num_s.getSelectedItem().toString()));
                RetrofitHelper.getApiService().updateUser(user.getId(), user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("SettingActivity", t.toString());
                    }
                });
            }
        });

        //바탕 클릭시 키보드 내리기
        ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.settingConst);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userNick_e.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(userAboutMe_e.getWindowToken(), 0);
            }
        });
    }
}
