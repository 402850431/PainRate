package com.example.innooz.seekbar2;

/**
 * Created by Innooz on 2017/10/5.
 */

public class MyData {

    public float nowProgress;
    public String nowTime;

    public MyData(float nowProgress, String nowTime) {
        this.nowProgress = nowProgress;
        this.nowTime = nowTime;
    }

    public float getNowProgress() {
        return nowProgress;
    }

    public void setNowProgress(float nowProgress) {
        this.nowProgress = nowProgress;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }
}
