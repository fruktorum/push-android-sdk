package com.devinotele.devinosdk.sdk;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

class HandleTokenUseCase extends BaseUC {
    private final DevinoLogsCallback logsCallback;
    private final String phone;
    private final String email;
    private final String event = "Register token (put)";

    HandleTokenUseCase(HelpersPackage hp, DevinoLogsCallback callback, String phone, String email) {
        super(hp);
        logsCallback = callback;
        this.phone = phone;
        this.email = email;
    }

    void run(FirebaseMessaging firebaseMessaging) {
        boolean tokenRegistered =
                sharedPrefsHelper.getBoolean(SharedPrefsHelper.KEY_TOKEN_REGISTERED);
        HashMap<String, Object> customData = sharedPrefsHelper.getHashMap(SharedPrefsHelper.KEY_CUSTOM_DATA);
        if (tokenRegistered) {
            registerUser(email, phone, customData);
        } else {
            firebaseMessaging.getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            logsCallback.onMessageLogged(
                                    "Firebase Error: "
                                            + Objects.requireNonNull(task.getException())
                                            .getMessage()
                            );
                            return;
                        }
                        String token = task.getResult();
                        sharedPrefsHelper.saveData(SharedPrefsHelper.KEY_PUSH_TOKEN, token);
                        DevinoSdk.getInstance().appStarted();
                        registerUser(email, phone, customData);
                    });
        }
    }

    private void registerUser(String email, String phone, HashMap<String, Object> customData) {
        trackSubscription(networkRepository.registerUser(email, phone, customData)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        json -> {
                            sharedPrefsHelper.saveData(
                                    SharedPrefsHelper.KEY_TOKEN_REGISTERED,
                                    true
                            );
                            logsCallback.onMessageLogged(event + " -> " + json.toString());
                        },
                        throwable -> {
                            if (throwable instanceof HttpException)
                                logsCallback.onMessageLogged(
                                        getErrorMessage(
                                                event + " -> ",
                                                ((HttpException) throwable)
                                        )
                                );
                            else {
                                logsCallback.onMessageLogged(
                                        event + " -> " + throwable.getMessage()
                                );
                            }
                        }
                )
        );
    }
}