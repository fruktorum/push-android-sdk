package com.devinotele.devinosdk.sdk;

import android.content.Context;
import android.widget.Toast;

public class UpdateApiBaseUrlUseCase extends BaseUC {

    private DevinoLogsCallback logsCallback;
    private RetrofitClientInstance retrofitClientInstance;

    UpdateApiBaseUrlUseCase(HelpersPackage hp, DevinoLogsCallback callback) {
        super(hp);
        logsCallback = callback;
        retrofitClientInstance = new RetrofitClientInstance();
    }

    void run(String newApiBaseUrl, Context ctx) {
        sharedPrefsHelper.saveData(SharedPrefsHelper.KEY_API_BASE_URL, newApiBaseUrl);
        logsCallback.onMessageLogged("Api Root Url = " + newApiBaseUrl);
        retrofitClientInstance.setApiBaseUrl(newApiBaseUrl);
        Toast.makeText(ctx, "Api Root Url was updated", Toast.LENGTH_SHORT).show();
    }
}
