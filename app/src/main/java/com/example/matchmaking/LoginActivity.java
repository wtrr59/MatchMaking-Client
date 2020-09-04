package com.example.matchmaking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText loginId;
    EditText loginPw;

    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /*Intent intent = new Intent(getApplicationContext(), MatchMainActivity.class);
        intent.putExtra("userId","root");
        startActivity(intent);*/

        //바탕 클릭시 키보드 내리기
        constraintLayout = (ConstraintLayout)findViewById(R.id.loginConst);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginId.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(loginPw.getWindowToken(),0);
            }
        });

        loginId = (EditText)findViewById(R.id.loginId);
        loginPw = (EditText)findViewById(R.id.loginPw);

        final Button loginButton = (Button)findViewById(R.id.loginButton);
        Button signButton = (Button)findViewById(R.id.signButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginId.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(),"아이디를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(loginPw.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                //server에 id/pw 보내고 확인받기


                RetrofitHelper.getApiService().sendLogin(loginId.getText().toString(), loginPw.getText().toString()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String response_ = response.body();
                        if(response_ != null && response_.equals("성공")){
                            Intent intent = new Intent(getApplicationContext(), MatchMainActivity.class);
                            intent.putExtra("userId",loginId.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("LoginActivity", t.toString());
                    }
                });

            }
        });

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                startActivity(intent);
            }
        });


    }
}
