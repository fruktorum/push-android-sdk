package com.devinotele.devinosdk.sdk;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static volatile String BASE_URL = "https://integrationapi.net/push/sdk/";
    private static final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static volatile String CURRENT_REQUEST_URL = "";

    public void setApiBaseUrl(String newApiBaseUrl) {
        BASE_URL = newApiBaseUrl;
    }

    public String getCurrentRequestUrl() {
        return CURRENT_REQUEST_URL;
    }

    static Retrofit getRetrofitInstance(final String apiKey) {
            String url = DevinoSdk.getInstance().getSavedBaseUrl();
            if (url != null && !url.isEmpty()) {
                BASE_URL = url;
            }

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                CURRENT_REQUEST_URL = chain.request().url().toString();
                Request request = original.newBuilder()
                        .header("x-api-key", apiKey)
                        .header("Content-Type", "application/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            });

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return retrofit;
    }
}