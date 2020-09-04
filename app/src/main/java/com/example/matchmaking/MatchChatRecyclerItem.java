package com.example.matchmaking;

public class MatchChatRecyclerItem {

    private String chatuserid;
    private String text;
    private String texttime;
    private String nickname;
    private String roomid;

    public MatchChatRecyclerItem(String chatuserid, String text, String texttime,String nickname, String roomid) {
        this.chatuserid = chatuserid;
        this.text = text;
        this.texttime = texttime;
        this.nickname = nickname;
        this.roomid = roomid;
    }

    public String getUserid() {
        return chatuserid;
    }

    public void setUserid(String chatuserid) {
        this.chatuserid = chatuserid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTexttime() {
        return texttime;
    }

    public void setTexttime(String texttime) {
        this.texttime = texttime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }
}
