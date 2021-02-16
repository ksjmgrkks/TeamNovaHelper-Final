package com.example.teamnovahelper.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.teamnovahelper.R;

public class LoadingActivity extends AppCompatActivity {
    //LoadingActivity 는 어플을 시작한다는 것을 알리는 로띠 라이브러리의 애니메이션을 표현한 액티비티입니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setTitle("로딩중");
        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("pencil.json");
        animationView.playAnimation();
        startLoading();
        ImageView image = (ImageView)findViewById(R.id.imageView8);

        Animation anima = AnimationUtils.loadAnimation(this, R.anim.alpha);

        image.startAnimation(anima);
    }
    private void startLoading() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}