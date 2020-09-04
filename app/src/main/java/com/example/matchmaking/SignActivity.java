package com.example.matchmaking;

import android.app.Activity;
import android.content.Context;
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

public class SignActivity extends Activity {

    ConstraintLayout constraintLayout;

    private User newUser = null;

    EditText signId;
    EditText signPw;
    EditText signNic;
    Spinner signTier;
    Spinner signPosi;
    Spinner signVoic;
    EditText signAboutMe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign);

        //바탕 클릭시 키보드 내리기
        constraintLayout = (ConstraintLayout)findViewById(R.id.signConst);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(signId.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(signPw.getWindowToken(),0);
                imm.hideSoftInputFromWindow(signNic.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(signAboutMe.getWindowToken(),0);
            }
        });

        signId = (EditText)findViewById(R.id.signId);
        signPw = (EditText)findViewById(R.id.signPw);
        signNic = (EditText)findViewById(R.id.signNick);
        signTier = (Spinner)findViewById(R.id.signTier);
        signPosi = (Spinner) findViewById(R.id.signPosi);
        signVoic = (Spinner) findViewById(R.id.signVoic);
        signAboutMe = (EditText) findViewById(R.id.signAboutMe);

        Button signCompButton = (Button)findViewById(R.id.signComplButton);

        signCompButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signId.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(),"아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(signPw.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(),"패스워드를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(signNic.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(),"닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                newUser = new User(signId.getText().toString(), signPw.getText().toString(), signNic.getText().toString(), signTier.getSelectedItem().toString(), signPosi.getSelectedItem().toString(), signVoic.getSelectedItem().toString(), signAboutMe.getText().toString());



                RetrofitHelper.getApiService().sendSign(newUser).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String response_ = response.body();
                        if(response_.equals("성공")){
                            Toast.makeText(getApplicationContext(),"회원가입 성공",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("SignActivity",t.getMessage());
                    }
                });
            }
        });
    }
}
