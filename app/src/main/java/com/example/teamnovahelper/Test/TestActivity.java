package com.example.teamnovahelper.Test;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.teamnovahelper.R;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class TestActivity extends AppCompatActivity {

    private View loginButton, logoutButton;
    private TextView nickName;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        loginButton = findViewById(R.id.login);
        logoutButton = findViewById(R.id.logout);
        nickName = findViewById(R.id.nickname);
        profileImage = findViewById(R.id.profile);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginClient.getInstance().isKakaoTalkLoginAvailable(TestActivity.this)){
                    LoginClient.getInstance().loginWithKakaoTalk(TestActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
                        @Override
                        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                            if(oAuthToken != null){
                            }
                            if (throwable != null){
                            }
                            updateKakaoLoginUi();
                            return null;
                        }
                    });
                }else{
                    LoginClient.getInstance().loginWithKakaoAccount(TestActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
                        @Override
                        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                            if(oAuthToken != null){
                            }
                            if (throwable != null){
                            }
                            updateKakaoLoginUi();
                            return null;
                        }
                    });

                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });
            }
        });

        updateKakaoLoginUi();
    }

    private void updateKakaoLoginUi(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if(user != null){

                    nickName.setText(user.getKakaoAccount().getProfile().getNickname());
                    Glide.with(profileImage).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(profileImage);

                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);
                }else{
                    nickName.setText(null);
                    profileImage.setImageBitmap(null);

                    loginButton.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.GONE);
                }
                return null;
            }
        });
    }
}

