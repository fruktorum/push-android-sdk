package com.devinotele.devinosdk.sdk;

import android.annotation.SuppressLint;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

class CustomEventUseCase extends BaseUC {

    private final DevinoLogsCallback logsCallback;
    private final String eventTemplate = "Custom event (%s) ";

    CustomEventUseCase(HelpersPackage hp, DevinoLogsCallback callback) {
        super(hp);
        logsCallback = callback;
    }

    @SuppressLint("CheckResult")
    void run(String eventName, HashMap<String, Object> eventData) {
        String token = sharedPrefsHelper.getString(SharedPrefsHelper.KEY_PUSH_TOKEN);
        if (token.length() > 0) {
            trackSubscription(networkRepository.customEvent(eventName, eventData)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            json -> logsCallback.onMessageLogged(
                                    String.format(
                                            eventTemplate,
                                            eventName
                                    ) + " -> " + json.toString()
                            ),
                            throwable -> {
                                if (throwable instanceof HttpException)
                                    logsCallback.onMessageLogged(
                                            getErrorMessage(
                                                    String.format(
                                                            eventTemplate,
                                                            eventName
                                                    ),
                                                    ((HttpException) throwable)
                                            )
                                    );
                                else {
                                    logsCallback.onMessageLogged(
                                            String.format(
                                                    eventTemplate,
                                                    eventName
                                            ) + " -> " + throwable.getMessage());
                                }
                            }
                    )
            );
        } else {
            logsCallback.onMessageLogged("Can't send custom event -> token not registered");
        }
    }
}