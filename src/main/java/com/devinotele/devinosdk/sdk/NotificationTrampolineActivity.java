package com.devinotele.devinosdk.sdk;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.devinotele.devinosdk.R;

import java.util.Arrays;

public class NotificationTrampolineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
        try {
            DevinoSdk.getInstance().hideNotification(this);

            Bundle arguments = getIntent().getExtras();

            if (arguments != null) {

                String action = getIntent().getStringExtra(DevinoPushReceiver.KEY_DEEPLINK);
                String picture = getIntent().getStringExtra(DevinoPushReceiver.KEY_PICTURE);
                String messageId = getIntent().getStringExtra(DevinoPushReceiver.KEY_PUSH_ID);

                Intent startMain = new Intent(Intent.ACTION_VIEW);

                try {
                    startMain.setData(Uri.parse(action));
                    startMain.putExtra(DevinoPushReceiver.KEY_PICTURE, picture);
                    startMain.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );
                    this.startActivity(startMain);
                } catch (Throwable e) {
                    e.printStackTrace();
                    Log.d(
                            "DevinoPush",
                            "e.localizedMessage =  " + e.getLocalizedMessage()
                    );
                }
                finish();
            }

        } catch (Exception e) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("NotificationTrampolineActivity: " + e.getLocalizedMessage())
                    .setMessage(Arrays.toString(e.getStackTrace()))
                    .setPositiveButton(android.R.string.yes, null)
                    .show();
        }
    }
}