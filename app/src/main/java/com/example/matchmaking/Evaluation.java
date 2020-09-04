package com.example.matchmaking;

public class Evaluation {
    private int amused;
    private int mental;
    private int leadership;

    public Evaluation(){
        this.amused = 0;
        this.mental = 0;
        this.leadership = 0;
    }

    public int getAmused() { return amused; }
    public int getMental() { return mental; }
    public int getLeadership() { return leadership; }

    public void setAmused(int amused) { this.amused = amused; }
    public void setMental(int mental) { this.mental = mental; }
    public void setLeadership(int leadership) { this.leadership = leadership; }
}
