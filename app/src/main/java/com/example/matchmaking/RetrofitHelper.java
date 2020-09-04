package com.example.matchmaking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.matchmaking.RetrofitInterface.API_URL;

public class RetrofitHelper {
    private static Retrofit retrofit = null;
    private static RetrofitInterface apiService = null;
    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static RetrofitInterface getApiService() {
        if(apiService == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL) //api의 baseURL
                    .addConverterFactory(GsonConverterFactory.create(gson)) // 나는 데이터를 자동으로 컨버팅할 수 있게 GsonFactory를 씀
                    .build();
            apiService = retrofit.create(RetrofitInterface.class); //실제 api Method들이선언된 Interface객체 선언
        }
        return apiService;
    }
}
