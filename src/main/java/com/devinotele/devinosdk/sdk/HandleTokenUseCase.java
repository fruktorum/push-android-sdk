package com.devinotele.devinosdk.sdk;


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
        if (sharedPrefsHelper.getBoolean(SharedPrefsHelper.KEY_TOKEN_REGISTERED)) registerUser(email, phone);
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
