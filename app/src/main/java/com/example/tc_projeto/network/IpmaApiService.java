package com.example.tc_projeto.network;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
import com.example.tc_projeto.model.WeatherWarning;

public interface IpmaApiService {
    @GET("forecast/warnings/warnings_www.json")
    Call<List<WeatherWarning>> getWeatherWarnings();
}
