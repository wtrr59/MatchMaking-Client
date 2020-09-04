package com.example.matchmaking;


import android.graphics.Bitmap;

public class MatchRoomRecyclerItem {

    private String tiertxt;
    private String positiontxt;
    private String voice;
    private String id;
    private String nickname;
    private boolean tierimg = true;

    public MatchRoomRecyclerItem(String id, String tiertxt, String positiontxt, String voice, String nickname) {
        this.id = id;
        this.tiertxt = tiertxt;
        this.positiontxt = positiontxt;
        this.voice = voice;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public String getId() {
        return id;
    }

    public String getTiertxt() {
        return tiertxt;
    }

    public void setTiertxt(String tiertxt) {
        this.tiertxt = tiertxt;
    }

    public String getPositiontxt() {
        return positiontxt;
    }

    public void setPositiontxt(String positiontxt) {
        this.positiontxt = positiontxt;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public boolean getTierimg() {
        return tierimg;
    }

    public void setTierimg(boolean tierimg) {
        this.tierimg = tierimg;
    }
}
