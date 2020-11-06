package com.akggame.android;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.akggame.akg_sdk.AKG_SDK;
import com.akggame.akg_sdk.LoginSDKCallback;
import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if ( AKG_SDK.INSTANCE.checkIsLogin(this)){
            startActivity(new Intent(LoginActivity.this,Main2Activity.class));
            finish();
        }
        
        Log.d("Density Check",String.valueOf(getResources().getDisplayMetrics().heightPixels));

        AKG_SDK.INSTANCE.onLogin(this,"Your Game", new LoginSDKCallback() {
            @Override
            public void onResponseSuccess(@NotNull String token, @NotNull String username, @NotNull String loginType) {

            }

            @Override
            public void onResponseFailed(@NotNull String message) {

            }
        });

    }
}
