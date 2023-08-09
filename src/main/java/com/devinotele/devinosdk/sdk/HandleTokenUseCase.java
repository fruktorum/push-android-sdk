package com.devinotele.devinosdk.sdk;


import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


class HandleTokenUseCase extends BaseUC {

    private DevinoLogsCallback logsCallback;
    private String phone, email;
    private String event = "register token (put) ";

    HandleTokenUseCase(HelpersPackage hp, DevinoLogsCallback callback, String phone, String email) {
        super(hp);
        logsCallback = callback;
        this.phone = phone;
        this.email = email;
    }

    void run(FirebaseMessaging firebaseMessaging) {
        boolean tokenRegistered = sharedPrefsHelper.getBoolean(SharedPrefsHelper.KEY_TOKEN_REGISTERED);
        Log.d("111111", "tokenRegistered = " + tokenRegistered);
        if (tokenRegistered) {
            registerUser(email, phone);
            Log.d("111111", "registerUser");
        }
        else {
            firebaseMessaging.getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            logsCallback.onMessageLogged("Firebase Error: " + Objects.requireNonNull(task.getException()).getMessage());
                            return;
                        }
                        String token = task.getResult();
                        sharedPrefsHelper.saveData(SharedPrefsHelper.KEY_PUSH_TOKEN, token);
                        DevinoSdk.getInstance().appStarted();
                        registerUser(email, phone);
                    });
        }
    }

    private void registerUser(String email, String phone) {

        trackSubscription(networkRepository.registerUser(email, phone)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        json -> {
                            sharedPrefsHelper.saveData(SharedPrefsHelper.KEY_TOKEN_REGISTERED, true);
                            Log.d("111111", "json = " + json.toString());
                            logsCallback.onMessageLogged(event + json.toString());
                        },
                        throwable -> {
                            if (throwable instanceof HttpException)
                                logsCallback.onMessageLogged(getErrorMessage(event, ((HttpException) throwable)));
                            else
                                logsCallback.onMessageLogged(event + throwable.getMessage());
                        }
                )
        );
    }
}
