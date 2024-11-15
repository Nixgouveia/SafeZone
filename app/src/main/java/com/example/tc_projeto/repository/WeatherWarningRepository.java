package com.example.tc_projeto.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import com.example.tc_projeto.model.WeatherWarning;
import com.example.tc_projeto.network.IpmaApiService;
import com.example.tc_projeto.network.RetrofitClient;

public class WeatherWarningRepository {
    private final IpmaApiService apiService;

    public WeatherWarningRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<WeatherWarning>> getWeatherWarnings() {
        MutableLiveData<List<WeatherWarning>> data = new MutableLiveData<>();

        apiService.getWeatherWarnings().enqueue(new Callback<List<WeatherWarning>>() {
            @Override
            public void onResponse(Call<List<WeatherWarning>> call, Response<List<WeatherWarning>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<WeatherWarning>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }
}

