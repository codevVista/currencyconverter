package com.dexwin.currencyconventer;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExchangeRateApi {
    @GET("convert")
    Call<ExchangeRateResponse> getExchangeRate(
            @Query("from") String from,
            @Query("to") String to,
            @Query("amount") double amount,
            @Query("access_key") String accessKey
    );
}