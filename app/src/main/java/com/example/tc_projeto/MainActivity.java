package com.example.tc_projeto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tc_projeto.Adapter.WarningAdapter;
import com.example.tc_projeto.ViewModel.WeatherWarningViewModel;
import com.example.tc_projeto.model.WeatherWarning;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private WeatherWarningViewModel viewModel;
    private WarningAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.warningsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WarningAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WeatherWarningViewModel.class);
        viewModel.getWarnings().observe(this, warnings -> {
            if (warnings != null) {
                for (WeatherWarning warning : warnings) {
                    Log.d("Warning", "Tipo: " + warning.getAwarenessTypeName());
                }
                adapter.updateWarnings(warnings);
            }else {
                // Caso não haja dados, mostre uma mensagem de erro ou deixe a lista vazia
                Log.e("MainActivity", "Nenhum aviso disponível");
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Initialize the MapView
        mapView.getMapAsync(this);
        MapsInitializer.initialize(this);


        // Botões
        ImageButton homeButton = findViewById(R.id.button_home);
        ImageButton notificationsButton = findViewById(R.id.button_notifications);
        ImageButton settingsButton = findViewById(R.id.button_settings);

        // Set listeners para cada botão
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para a Activity Home
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para a Activity Notifications
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para a Activity Settings
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
