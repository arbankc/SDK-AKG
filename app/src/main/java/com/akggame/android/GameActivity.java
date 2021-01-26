package com.akggame.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.akggame.akg_sdk.AKG_SDK;
import com.akggame.akg_sdk.MenuSDKCallback;
import com.akggame.akg_sdk.OttoPaySDKCallback;
import com.akggame.akg_sdk.PAYMENT_TYPE;
import com.akggame.akg_sdk.ProductSDKCallback;
import com.akggame.akg_sdk.PurchaseSDKCallback;
import com.akggame.akg_sdk.StartGameSDKCallback;
import com.akggame.akg_sdk.baseextend.BaseActivity;
import com.akggame.akg_sdk.dao.api.model.response.DataItemGameList;
import com.akggame.akg_sdk.dao.api.model.response.DepositResponse;
import com.akggame.akg_sdk.dao.api.model.response.GameListResponse;
import com.akggame.akg_sdk.dao.pojo.PurchaseItem;
import com.akggame.akg_sdk.rx.IView;
import com.akggame.akg_sdk.ui.component.FloatingButton;
import com.akggame.akg_sdk.ui.dialog.menu.GameListIView;
import com.akggame.akg_sdk.util.CacheUtil;
import com.akggame.akg_sdk.util.DeviceUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GameActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        FloatingButton floatingButton = findViewById(R.id.floatingButton);
        AKG_SDK.setFloatingButton(this, floatingButton,
                this, new MenuSDKCallback() {
                    @Override
                    public void onBindAccount(@NotNull Context context) {

                    }

                    @Override
                    public void onClickEula(@NotNull Context context, @NotNull String idGame) {

                    }

                    @Override
                    public void onContactUs(@NotNull Context context) {

                    }

                    @Override
                    public void onClickFbPage(@NotNull Context context) {

                    }


                    @Override
                    public void onCheckSDK(boolean isUpdated) {


                    }


                    @Override
                    public void onLogout() {

                    }
                });

        floatingButton.setFloat();

        Button btnPayment = findViewById(R.id.btnPayment);
        final Button btnOttoPay = findViewById(R.id.btnOttoPay);

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AKG_SDK.onSDKPayment(PAYMENT_TYPE.GOOGLE, GameActivity.this);
            }
        });


        btnOttoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                gotoPaymentOttoPay();
                AKG_SDK.onSDKPayment(PAYMENT_TYPE.OTTOPAY, GameActivity.this);

            }
        });


//        AKG_SDK.callStartGame(getSupportFragmentManager(), new StartGameSDKCallback() {
//            @Override
//            public void onStartGame(@NotNull DataItemGameList dataItemGameList) {
//                //get data response for game list
//            }
//        });

    }

    void gotoPaymentOttoPay() {
        AKG_SDK.onPaymentOttoPay(GameActivity.this,
                "User Id",
                "Game Product Id",
                new OttoPaySDKCallback() {
                    @Override
                    public void onFailedPayment(@NotNull String message) {

                    }

                    @Override
                    public void onSuccessPayment(@org.jetbrains.annotations.Nullable DepositResponse depositResponse) {

                    }
                });
    }

    void gotoPaymentGoogle() {
        AKG_SDK.getProductsGoogle(getApplication(), GameActivity.this, new ProductSDKCallback() {
            @Override
            public void ProductResult(@NotNull List<com.android.billingclient.api.SkuDetails> skuDetails) {
                AKG_SDK.launchBilling(GameActivity.this, skuDetails.get(0),
                        new PurchaseSDKCallback() {
                            @Override
                            public void onPurchasedItem(@NotNull PurchaseItem purchaseItem) {
                            }
                        });

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


}
