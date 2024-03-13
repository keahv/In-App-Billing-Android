package com.example.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

     Button consume, sub;
    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    consume = findViewById(R.id.consume_button);
    consume.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, com.example.firstapp.consume.class));
        }
    });

        final List<String> skuList = new ArrayList<>();
        skuList.add("test_prod_1");

        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
            // Handle purchases update
        };

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

//        button.setOnClickListener(view -> {
//            billingClient.startConnection(new BillingClientStateListener() {
//                @Override
//                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
//                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                        SkuDetailsParams params = SkuDetailsParams.newBuilder()
//                                .setSkusList(skuList)
//                                .setType(BillingClient.SkuType.INAPP)
//                                .build();
//
//                        billingClient.querySkuDetailsAsync(params, (billingResult1, skuDetailsList) -> {
//                            for (com.android.billingclient.api.SkuDetails skuDetails : skuDetailsList) {
//                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
//                                        .setSkuDetails(skuDetails)
//                                        .build();
//
//                                int responseCode = billingClient.launchBillingFlow(MainActivity.this, flowParams).getResponseCode();
//                                // Handle response code
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onBillingServiceDisconnected() {
//                    // Handle billing service disconnection
//                }
//            });
//        });
    }
}
