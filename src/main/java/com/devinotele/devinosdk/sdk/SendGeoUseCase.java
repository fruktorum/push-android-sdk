package com.devinotele.devinosdk.sdk;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

class SendGeoUseCase extends BaseUC {

    private final DevinoLogsCallback logsCallback;
    private final String eventTemplate = "Geo(%s, %s)";

    SendGeoUseCase(HelpersPackage hp, DevinoLogsCallback callback) {
        super(hp);
        logsCallback = callback;
    }

    void run(Double latitude, Double longitude) {
        String token = sharedPrefsHelper.getString(SharedPrefsHelper.KEY_PUSH_TOKEN);
        if (token.length() > 0) {
            trackSubscription(networkRepository.geo(latitude, longitude)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            json -> logsCallback.onMessageLogged(
                                    String.format(
                                            eventTemplate,
                                            latitude,
                                            longitude)
                                            + " -> "
                                            + json.toString()),
                            throwable -> {
                                if (throwable instanceof HttpException)
                                    logsCallback.onMessageLogged(
                                            getErrorMessage(
                                                    String.format(
                                                            eventTemplate,
                                                            latitude,
                                                            longitude
                                                    ) + " -> ",
                                                    ((HttpException) throwable))
                                    );
                                else
                                    logsCallback.onMessageLogged(
                                            String.format(
                                                    eventTemplate,
                                                    latitude,
                                                    longitude
                                            ) + " -> " + throwable.getMessage());
                            }
                    )
            );
        } else {
            logsCallback.onMessageLogged("Can't send geo -> token not registered");
        }
    }
}