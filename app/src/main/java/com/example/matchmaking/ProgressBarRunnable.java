package com.example.matchmaking;

import android.widget.ProgressBar;

public class ProgressBarRunnable implements Runnable {

    private ProgressBar progressBar;
    private int from;
    private int to;

    public ProgressBarRunnable(ProgressBar progressBar,int from, int to) {
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, from, to);
        anim.setDuration(1000);
        progressBar.startAnimation(anim);
    }
}
