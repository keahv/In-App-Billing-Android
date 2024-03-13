package com.example.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

public class consume extends AppCompatActivity {
    BillingClient billingClient;
    Button button_consum_buy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consume);

        button_consum_buy =findViewById(R.id.button_consume_buy);
        button_consum_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consume_buy_Function();
            }
        });



         billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
    }



    PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases!=null){

                for(Purchase purchase: purchases){
                    handelPurchase(purchase);
                }



            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                Toast.makeText(consume.this, "BILLING_UNAVILABLE", Toast.LENGTH_SHORT).show();
            }
            else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Toast.makeText(consume.this, "ITEM_ALREADY_OWNED", Toast.LENGTH_SHORT).show();
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
                Toast.makeText(consume.this, "DEVELOPER_ERROR", Toast.LENGTH_SHORT).show();
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.NETWORK_ERROR) {
                Toast.makeText(consume.this, "NETWORK_ERROR", Toast.LENGTH_SHORT).show();
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
                Toast.makeText(consume.this, "SERVICE_DISCONNECTED", Toast.LENGTH_SHORT).show();
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
                Toast.makeText(consume.this, "FEATURE_NOT_SUPPORTED", Toast.LENGTH_SHORT).show();
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Toast.makeText(consume.this, "USER_CANCELED", Toast.LENGTH_SHORT).show();
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_NOT_OWNED) {
                Toast.makeText(consume.this, "ITEM_NOT_OWNED", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(consume.this, ""+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void handelPurchase(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);

    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
        //verify
        if (!verifyvalideSignature(purchase.getOriginalJson(), purchase.getSignature())){
            Toast.makeText(getApplicationContext(), "Error : invalid Purchase", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!purchase.isAcknowledged()){
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            Toast.makeText(this, "PURCHASED", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Already Purchased", Toast.LENGTH_SHORT).show();

        }
    } else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
        Toast.makeText(this, "UNSPECIFIED_STATE", Toast.LENGTH_SHORT).show();
    } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
        Toast.makeText(this, "PENDING", Toast.LENGTH_SHORT).show();
    }
    }

    private boolean verifyvalideSignature(String originalJson, String signature) {
        try {
            String base64Key = "";
            return verify.verifyPurchase(base64Key,originalJson,signature);
        }catch (IOException exception){
            return false;
        }
    }
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
            Toast.makeText(consume.this, "Acknowledged", Toast.LENGTH_SHORT).show();
        }
    };

    private void consume_buy_Function() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                    QueryProductDetailsParams queryProductDetailsParams =
                            QueryProductDetailsParams.newBuilder()
                                    .setProductList(
                                            ImmutableList.of(
                                                    QueryProductDetailsParams.Product.newBuilder()
                                                            .setProductId("product_id_example")
                                                            .setProductType(BillingClient.ProductType.SUBS)
                                                            .build()))
                                    .build();

                    billingClient.queryProductDetailsAsync(
                            queryProductDetailsParams,
                            new ProductDetailsResponseListener() {
                                public void onProductDetailsResponse(BillingResult billingResult,
                                                                     List<ProductDetails> productDetailsList) {
                                    // check billingResult
                                    // process returned productDetailsList
                                    for (ProductDetails productDetails: productDetailsList){
                                        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                                                ImmutableList.of(
                                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                                                .setProductDetails(productDetails)
                                                                .build()
                                                );

                                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                .setProductDetailsParamsList(productDetailsParamsList)
                                                .build();

                                        billingClient.launchBillingFlow(consume.this, billingFlowParams);
                                    }
                                }
                            }
                    );
                }
            }
        });
    }
}