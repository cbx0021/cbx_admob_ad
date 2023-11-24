package com.addemo.cbx_admob_ad.ad_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ADCBRetrofitClient {

    private static native String getMain();
    private ADCBRetrofitClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getMain())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        myApi = retrofit.create(ADCBRetrofitService.class);
    }
    private static ADCBRetrofitClient instance = null;
    private final ADCBRetrofitService myApi;

    public static synchronized ADCBRetrofitClient getInstance() {
        if (instance == null) {
            instance = new ADCBRetrofitClient();
        }
        return instance;
    }

    public ADCBRetrofitService getMyApi() {
        return myApi;
    }
}