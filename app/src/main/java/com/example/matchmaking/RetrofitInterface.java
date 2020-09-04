package com.example.matchmaking;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitInterface {
    String API_URL = "http://192.249.19.251:8780/";

    @FormUrlEncoded
    @POST("users/login")
    Call<String> sendLogin(@Field("id")String id, @Field("password")String pw);

    @POST("users/sign")
    Call<String> sendSign(@Body User user);

    @GET("users/user/{userId}")
    Call<User> receiveUser(@Path("userId")String userId);

    @PUT("users/user/{userId}")
    Call<User> updateUser(@Path("userId")String userId, @Body User user);

    @GET("chats/chat/{roomid}")
    Call<List<MatchChatRecyclerItem>> getChats(@Path("roomid")String roomid);

    @DELETE("chats/chat/{roomid}")
    Call<String> deleteChats(@Path("roomid")String roomid);
}
