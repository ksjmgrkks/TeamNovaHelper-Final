package com.example.teamnovahelper.Test;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "8e6222d2f281b266894124816dc30495");
    }
}

