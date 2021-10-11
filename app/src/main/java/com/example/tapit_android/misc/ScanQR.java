package com.example.tapit_android.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public final class ScanQR extends ActivityResultContract<String, String> {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String input) {
        Intent intent;
        try {

            intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", input); // "PRODUCT_MODE for bar codes


        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            intent = new Intent(Intent.ACTION_VIEW,marketUri);

        }
        return intent;
    }

    @Override
    public String parseResult(int resultCode, @Nullable Intent intent) {

            if (resultCode != Activity.RESULT_OK || intent == null) {
                return null;
            }

        return intent.getStringExtra("SCAN_RESULT");
    }
}
