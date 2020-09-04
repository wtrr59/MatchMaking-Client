package com.example.matchmaking;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchEvaluationActivity extends AppCompatActivity {

    private MatchEvaluationRecyclerAdapter matchEvaluationRecyclerAdapter;
    private ArrayList<String> userlist;
    private String myid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_evaluation);

        userlist = getIntent().getStringArrayListExtra("userlist");
        myid = getIntent().getStringExtra("userid");

        for(int i = 0; i<userlist.size(); i++)
            if(userlist.get(i).equals(myid)) {
                userlist.remove(i);
                break;
            }


        matchEvaluationRecyclerAdapter = new MatchEvaluationRecyclerAdapter(this);

        RecyclerView evaluation_recyclerview = findViewById(R.id.match_eval_recycler);
        evaluation_recyclerview.setAdapter(matchEvaluationRecyclerAdapter);
        evaluation_recyclerview.setLayoutManager(new LinearLayoutManager(this));


        for(int i = 0; i < userlist.size(); i++){
            String user_id = userlist.get(i);
            RetrofitHelper.getApiService().receiveUser(user_id).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    matchEvaluationRecyclerAdapter.additem(user);
                    matchEvaluationRecyclerAdapter.notifyDataSetChanged();
                    evaluation_recyclerview.invalidateItemDecorations();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("MatchRoomActivity",t.getMessage());
                }
            });
        }

        findViewById(R.id.match_eval_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
