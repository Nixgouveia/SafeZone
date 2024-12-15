package com.example.tc_projeto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.tc_projeto.Adapter.WarningAdapter;
import com.example.tc_projeto.ViewModel.WeatherWarningViewModel;
import com.example.tc_projeto.model.WeatherWarning;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static  double CURRENT_LATITUDE = 40.2860;
    private static  double CURRENT_LONGITUDE = -7.5033;
    private static final int zoom = 11;
    private MapView mapView;
    private GoogleMap googleMap;
    private WeatherWarningViewModel viewModel;
    private WarningAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private String currentRegion;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView reg= findViewById(R.id.textinfo);

        scheduleWeatherWarningWorker();

        RecyclerView recyclerView = findViewById(R.id.warningsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WarningAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Recuperar o valor da SharedPreferences
        SharedPreferences sharedPreferences2 = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String selectedItem = sharedPreferences2.getString("selectedItem", "Nenhum item selecionado");

        getCurrentLoc(selectedItem);


        String itemSelecionado = getIntent().getStringExtra("itemSelecionado");


        viewModel = new ViewModelProvider(this).get(WeatherWarningViewModel.class);
        viewModel.getWarnings().observe(this, warnings -> {
            if (warnings != null && currentRegion != null) {
                List<WeatherWarning> filteredWarnings = filterWarningsByRegion(warnings, currentRegion);
                if(filteredWarnings.size()==0){reg.setText("Não exitem riscos para todo País");
                }else{}
                adapter.updateWarnings(filteredWarnings);
                updateMapWithWarnings(filteredWarnings);
            } else {
                Log.e("MainActivity", "Nenhum aviso disponível ou região não definida");
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        MapsInitializer.initialize(this);

        // Desativar rolagem do ScrollView ao interagir com o MapView
        ScrollView scrollView = findViewById(R.id.scrollView);
        mapView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return v.onTouchEvent(event);
        });

        ImageButton homeButton = findViewById(R.id.button_home);
        ImageButton notificationsButton = findViewById(R.id.button_notifications);
        ImageButton settingsButton = findViewById(R.id.button_settings);

        // Barra de navegação
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });

        notificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastKnownLocation();
        }

        // Configuração dos acordeões
        setupAccordion(R.id.header_fire, R.id.content_fire, R.id.indicator_fire);
        setupAccordion(R.id.header_evacuation, R.id.content_evacuation, R.id.indicator_evacuation);
        setupAccordion(R.id.header_flood, R.id.content_flood, R.id.indicator_flood);
    }

    private void setupAccordion(int headerId, int contentId, int indicatorId) {
        LinearLayout header = findViewById(headerId);
        LinearLayout content = findViewById(contentId);
        ImageView indicator = findViewById(indicatorId);

        header.setOnClickListener(v -> {
            if (content.getVisibility() == View.GONE) {
                content.setVisibility(View.VISIBLE);
                indicator.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                content.setVisibility(View.GONE);
                indicator.setImageResource(android.R.drawable.arrow_down_float);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            } else {
                // Permission denied
            }
        }
    }

    private void getLastKnownLocation() { /*MUDAR AQUI DEPOIS*/
        Location location = new Location("");
        location.setLatitude(CURRENT_LATITUDE);
        location.setLongitude(CURRENT_LONGITUDE);
        determineRegionFromLocation(location);
    }

    private void determineRegionFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                currentRegion = address.getAdminArea();
                currentRegion = getDistrictAbbreviation(currentRegion);
                Log.d("MainActivity", "Região atual: " + currentRegion);
                /*debugText.setText(currentRegion);*/

                // Trigger the observer to update the RecyclerView with the new region
                viewModel.getWarnings().observe(this, warnings -> {
                    if (warnings != null && currentRegion != null) {
                        List<WeatherWarning> filteredWarnings = filterWarningsByRegion(warnings, currentRegion);
                        adapter.updateWarnings(filteredWarnings);
                        updateMapWithWarnings(filteredWarnings);
                    } else {
                        Log.e("MainActivity", "Nenhum aviso disponível ou região não definida");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // função para obter a localização atual
    public void getCurrentLoc(String distrito) {
        switch (distrito) {
            case "Aveiro":
                CURRENT_LATITUDE = 40.6405;
                CURRENT_LONGITUDE = -8.6538;
                break;
            case "Beja":
                CURRENT_LATITUDE = 38.0151;
                CURRENT_LONGITUDE = -7.8637;
                break;
            case "Braga":
                CURRENT_LATITUDE = 41.5454;
                CURRENT_LONGITUDE = -8.4265;
                break;
            case "Bragança":
                CURRENT_LATITUDE = 41.8058;
                CURRENT_LONGITUDE = -6.7572;
                break;
            case "Castelo Branco":
                CURRENT_LATITUDE = 39.8222;
                CURRENT_LONGITUDE = -7.4909;
                break;
            case "Coimbra":
                CURRENT_LATITUDE = 40.211;
                CURRENT_LONGITUDE = -8.4294;
                break;
            case "Évora":
                CURRENT_LATITUDE = 38.5711;
                CURRENT_LONGITUDE = -7.9097;
                break;
            case "Faro":
                CURRENT_LATITUDE = 37.0179;
                CURRENT_LONGITUDE = -7.9333;
                break;
            case "Guarda":
                CURRENT_LATITUDE = 40.5373;
                CURRENT_LONGITUDE = -7.2658;
                break;
            case "Leiria":
                CURRENT_LATITUDE = 39.7476;
                CURRENT_LONGITUDE = -8.807;
                break;
            case "Lisboa":
                CURRENT_LATITUDE = 38.7167;
                CURRENT_LONGITUDE = -9.1399;
                break;
            case "Portalegre":
                CURRENT_LATITUDE = 39.2974;
                CURRENT_LONGITUDE = -7.4282;
                break;
            case "Porto":
                CURRENT_LATITUDE = 41.1496;
                CURRENT_LONGITUDE = -8.611;
                break;
            case "Santarém":
                CURRENT_LATITUDE = 39.2362;
                CURRENT_LONGITUDE = -8.6859;
                break;
            case "Setúbal":
                CURRENT_LATITUDE = 38.5244;
                CURRENT_LONGITUDE = -8.894;
                break;
            case "Viana do Castelo":
                CURRENT_LATITUDE = 41.6941;
                CURRENT_LONGITUDE = -8.8302;
                break;
            case "Vila Real":
                CURRENT_LATITUDE = 41.3006;
                CURRENT_LONGITUDE = -7.7441;
                break;
            case "Viseu":
                CURRENT_LATITUDE = 40.661;
                CURRENT_LONGITUDE = -7.9097;
                break;
        }
    }


    public String getDistrictAbbreviation(String dist) {
        switch (dist) {
            case "Aveiro":
                return "AVR";
            case "Beja":
                return "BEJ";
            case "Braga":
                return "BRA";
            case "Bragança":
                return "BGC";
            case "Castelo Branco":
                return "CBR";
            case "Coimbra":
                return "CBR";
            case "Évora":
                return "EVR";
            case "Faro":
                return "FAR";
            case "Guarda":
                return "GUA";
            case "Leiria":
                return "LEI";
            case "Lisboa":
                return "LIS";
            case "Portalegre":
                return "PTG";
            case "Porto":
                return "PRT";
            case "Santarém":
                return "STM";
            case "Setúbal":
                return "STB";
            case "Viana do Castelo":
                return "VCT";
            case "Vila Real":
                return "VRL";
            case "Viseu":
                return "VSE";
            default:
                return "Invalid District";
        }
    }

    private void updateMapLocation() {
        LatLng currentLocation = new LatLng(CURRENT_LATITUDE, CURRENT_LONGITUDE);
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Sua Região"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom));
    }

    private void updateMapWithWarnings(List<WeatherWarning> warnings) {
        if (googleMap != null) {
            googleMap.clear();
            updateMapLocation();
            for (WeatherWarning warning : warnings) {
                double warningLatitude = Double.parseDouble(warning.getLatitude());
                double warningLongitude = Double.parseDouble(warning.getLongitude());
                LatLng warningLocation = new LatLng(warningLatitude, warningLongitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(warningLocation)
                        .title(warning.getAwarenessTypeName())
                        .snippet("Level: " + warning.getAwarenessLevelID() + "\nRegion: " + warning.getIdAreaAviso()));
            }
        }
    }


    private List<WeatherWarning> filterWarningsByRegion(List<WeatherWarning> warnings, String region) {
        List<WeatherWarning> filteredWarnings = new ArrayList<>();
        String tipo="green";
        for (WeatherWarning warning : warnings) {
            if (!tipo.equals(warning.getAwarenessLevelID())) {
                filteredWarnings.add(warning);
            }
        }
        return filteredWarnings;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        updateMapLocation();
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

    private void scheduleWeatherWarningWorker() {
        // Configura o PeriodicWorkRequest para o WeatherWarningWorker
        WorkManager workManager = WorkManager.getInstance(this);
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                WeatherWarningWorker.class, 15, TimeUnit.MINUTES
        ).build();

        // Enqueue o Worker com uma política para substituir qualquer trabalho anterior
        workManager.enqueueUniquePeriodicWork(
                "WeatherWarningWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
        );
    }



}