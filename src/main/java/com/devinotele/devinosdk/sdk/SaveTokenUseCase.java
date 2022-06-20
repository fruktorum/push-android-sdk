package com.devinotele.devinosdk.sdk;


import com.google.firebase.messaging.FirebaseMessaging;


class SaveTokenUseCase extends BaseUC {

    private DevinoLogsCallback logsCallback;

    SaveTokenUseCase(HelpersPackage hp, DevinoLogsCallback callback) {
        super(hp);
        logsCallback = callback;
    }

    void run(FirebaseMessaging firebaseMessaging) {
        firebaseMessaging.getToken().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                return;
            }

            String token = task.getResult();
            String persistedToken = sharedPrefsHelper.getString(SharedPrefsHelper.KEY_PUSH_TOKEN);

            if (!token.equals(persistedToken)) {
                sharedPrefsHelper.saveData(SharedPrefsHelper.KEY_PUSH_TOKEN, token);
                networkRepository.updateToken(token);
                logsCallback.onMessageLogged("Push token persisted\n" + token);
                DevinoSdk.getInstance().appStarted();
            }
        });
    }
}
