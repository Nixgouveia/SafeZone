package com.example.tc_projeto.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import com.example.tc_projeto.model.WeatherWarning;
import com.example.tc_projeto.repository.WeatherWarningRepository;

public class WeatherWarningViewModel extends ViewModel {
    private final WeatherWarningRepository repository;
    private LiveData<List<WeatherWarning>> warnings;

    public WeatherWarningViewModel() {
        repository = new WeatherWarningRepository();
        warnings = repository.getWeatherWarnings();
    }

    public LiveData<List<WeatherWarning>> getWarnings() {
        return warnings;
    }
}
