package com.devinotele.devinosdk.sdk;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

class SendGeoUseCase extends BaseUC {

    private final DevinoLogsCallback logsCallback;
    private final String eventTemplate = "Geo (%s, %s): ";
    private final RetrofitClientInstance retrofitClientInstance;

    SendGeoUseCase(HelpersPackage hp, DevinoLogsCallback callback) {
        super(hp);
        logsCallback = callback;
        retrofitClientInstance = new RetrofitClientInstance();
    }

    void run(Double latitude, Double longitude) {
        String token = sharedPrefsHelper.getString(SharedPrefsHelper.KEY_PUSH_TOKEN);
        HashMap<String, Object> customData =
                sharedPrefsHelper.getHashMap(SharedPrefsHelper.KEY_CUSTOM_DATA);
        if (token.length() > 0) {
            trackSubscription(networkRepository.geo(latitude, longitude, customData)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            json -> logsCallback.onMessageLogged(
                                    String.format(
                                            eventTemplate,
                                            latitude,
                                            longitude
                                    )
                                            + retrofitClientInstance.getCurrentRequestUrl()
                                            + " -> " + json.toString()
                            ),
                            throwable -> {
                                if (throwable instanceof HttpException)
                                    logsCallback.onMessageLogged(
                                            getErrorMessage(
                                                    String.format(
                                                            eventTemplate,
                                                            latitude,
                                                            longitude
                                                    )
                                                            + retrofitClientInstance.getCurrentRequestUrl()
                                                            + " -> ",
                                                    ((HttpException) throwable))
                                    );
                                else
                                    logsCallback.onMessageLogged(
                                            String.format(
                                                    eventTemplate,
                                                    latitude,
                                                    longitude
                                            )
                                                    + retrofitClientInstance.getCurrentRequestUrl()
                                                    + " -> " + throwable.getMessage());
                            }
                    )
            );
        } else {
            logsCallback.onMessageLogged("Can't send geo -> token not registered");
        }
    }
}