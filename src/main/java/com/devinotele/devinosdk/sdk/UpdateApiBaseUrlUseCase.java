package com.devinotele.devinosdk.sdk;

import android.content.Context;
import android.widget.Toast;

public class UpdateApiBaseUrlUseCase extends BaseUC {

    private final DevinoLogsCallback logsCallback;
    private final String event = "Api Root Url was changed";
    private final RetrofitClientInstance retrofitClientInstance;

    UpdateApiBaseUrlUseCase(HelpersPackage hp, DevinoLogsCallback callback) {
        super(hp);
        logsCallback = callback;
        retrofitClientInstance = new RetrofitClientInstance();
    }

    void run(String newApiBaseUrl, Context ctx) {
        sharedPrefsHelper.saveData(SharedPrefsHelper.KEY_API_BASE_URL, newApiBaseUrl);
        logsCallback.onMessageLogged(event + " -> " + newApiBaseUrl);
        retrofitClientInstance.setApiBaseUrl(newApiBaseUrl);
        Toast.makeText(ctx, event, Toast.LENGTH_SHORT).show();
    }
}