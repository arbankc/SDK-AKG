package com.akggame.android;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.akggame.akg_sdk.AKG_SDK;
import com.akggame.akg_sdk.MenuSDKCallback;
import com.akggame.akg_sdk.PAYMENT_TYPE;
import com.akggame.akg_sdk.ProductSDKCallback;
import com.akggame.akg_sdk.PurchaseSDKCallback;
import com.akggame.akg_sdk.dao.pojo.PurchaseItem;
import com.akggame.akg_sdk.ui.component.FloatingButton;
import com.akggame.akg_sdk.util.CacheUtil;
import com.akggame.akg_sdk.util.DeviceUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameActivity extends AppCompatActivity implements MenuSDKCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        FloatingButton floatingButton = findViewById(R.id.floatingButton);
        AKG_SDK.INSTANCE.setFloatingButton(this, floatingButton,
                this, new MenuSDKCallback() {
                    @Override
                    public void onCheckSDK(boolean isUpdated) {

                    }

                    @Override
                    public void onSuccessBind(@NotNull String token, @NotNull String loginType) {

                    }

                    @Override
                    public void onLogout() {

                    }


                });

        AKG_SDK.INSTANCE.getProducts(getApplication(), this, new ProductSDKCallback() {
            @Override
            public void ProductResult(@NotNull List<com.android.billingclient.api.SkuDetails> skuDetails) {


                AKG_SDK.INSTANCE.launchBilling(GameActivity.this, skuDetails.get(0),
                        new PurchaseSDKCallback() {
                            @Override
                            public void onPurchasedItem(@NotNull PurchaseItem purchaseItem) {

                            }
                        });

            }
        });


        Button btnPayment = findViewById(R.id.btnPayment);
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AKG_SDK.INSTANCE.onSDKPayment(PAYMENT_TYPE.GOOGLE,GameActivity.this);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AKG_SDK.SDK_PAYMENT_CODE) {
                PurchaseItem payment = data.getParcelableExtra(AKG_SDK.SDK_PAYMENT_DATA);
                //handle your puchase here
            }
        }
    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onSuccessBind(@NotNull String token, @NotNull String loginType) {

    }

    @Override
    public void onCheckSDK(boolean isUpdated) {

    }
}
