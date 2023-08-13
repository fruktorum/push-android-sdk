package com.devinotele.devinosdk.sdk;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

class SaveTokenUseCase extends BaseUC {

    private final DevinoLogsCallback logsCallback;

    SaveTokenUseCase(HelpersPackage hp, DevinoLogsCallback callback) {
        super(hp);
        logsCallback = callback;
    }

    void run(FirebaseMessaging firebaseMessaging) {
        firebaseMessaging.getToken().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                logsCallback.onMessageLogged("Firebase Error:" + Objects.requireNonNull(task.getException()).getLocalizedMessage());
            } else {
                String token = task.getResult();
                if (token != null) {
                    run(token);
                }
            }
        });
    }

    void run (String token) {
        String persistedToken = sharedPrefsHelper.getString(SharedPrefsHelper.KEY_PUSH_TOKEN);

        if (!token.equals(persistedToken)) {
            sharedPrefsHelper.saveData(SharedPrefsHelper.KEY_PUSH_TOKEN, token);
            networkRepository.updateToken(token);
            logsCallback.onMessageLogged("Push token persisted\n" + token);
            DevinoSdk.getInstance().appStarted();
        }
    }

}