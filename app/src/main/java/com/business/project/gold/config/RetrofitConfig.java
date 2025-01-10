package com.business.project.gold.config;

import com.business.project.gold.service.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private static Retrofit retrofit;
    private static ApiService apiService;

    public static Retrofit getRetrofitClient() {
        if (null == retrofit) {
            retrofit = new Retrofit.Builder()
                    //.baseUrl("http://10.0.2.2:8080/") // Replace with your base URL
                    .baseUrl("http://15.207.14.218:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        if (null == apiService) {
            apiService = RetrofitConfig.getRetrofitClient().create(ApiService.class);
        }
        return apiService;
    }
}
