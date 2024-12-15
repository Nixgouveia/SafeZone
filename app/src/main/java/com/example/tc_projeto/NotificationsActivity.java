package com.example.tc_projeto;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tc_projeto.Adapter.WarningAdapter;
import com.example.tc_projeto.ViewModel.WeatherWarningViewModel;
import com.example.tc_projeto.model.WeatherWarning;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NotificationsActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final double CURRENT_LATITUDE = 40.2860;
    private static final double CURRENT_LONGITUDE = -7.5033;
    private static final double RADIUS_KM = 1000000.0;
    private static final int zoom = 12;
    private MapView mapView;
    private GoogleMap googleMap;
    private WeatherWarningViewModel viewModel;
    private WarningAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private String currentRegion;
    private TextView debugText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);


        TextView reg= findViewById(R.id.not_reg);

        // Recuperar o valor da SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String selectedItem = sharedPreferences.getString("selectedItem", "Nenhum item selecionado");

        RecyclerView recyclerView = findViewById(R.id.warningsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WarningAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WeatherWarningViewModel.class);
        viewModel.getWarnings().observe(this, warnings -> {
            if (warnings != null && getDistrictAbbreviation(selectedItem) != null) {
                List<WeatherWarning> filteredWarnings = filterWarningsByRegion(warnings, getDistrictAbbreviation(selectedItem));
                if(filteredWarnings.size()==0){reg.setText("Não exitem riscos para " + selectedItem );
                }else{reg.setText("Região atual: " + selectedItem);}
                adapter.updateWarnings(filteredWarnings);
//                updateMapWithWarnings(filteredWarnings);
            } else {
                Log.e("MainActivity", "Nenhum aviso disponível ou região não definida");
            }
        });



        // Botões
        ImageButton homeButton = findViewById(R.id.button_home);
        ImageButton notificationsButton = findViewById(R.id.button_notifications);
        ImageButton settingsButton = findViewById(R.id.button_settings);

        // Barra de navegação
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }




    public String getDistrictAbbreviation(String dist) {
        if (dist.equals("Aveiro")) {
            return "AVR";
        } else if (dist.equals("Beja")) {
            return "BEJ";
        } else if (dist.equals("Braga")) {
            return "BRA";
        } else if (dist.equals("Bragança")) {
            return "BGC";
        } else if (dist.equals("Castelo Branco")) {
            return "CBO";
        } else if (dist.equals("Coimbra")) {
            return "CBR";
        } else if (dist.equals("Évora")) {
            return "EVR";
        } else if (dist.equals("Faro")) {
            return "FAR";
        } else if (dist.equals("Guarda")) {
            return "GUA";
        } else if (dist.equals("Leiria")) {
            return "LEI";
        } else if (dist.equals("Lisboa")) {
            return "LIS";
        } else if (dist.equals("Portalegre")) {
            return "PTG";
        } else if (dist.equals("Porto")) {
            return "PRT";
        } else if (dist.equals("Santarém")) {
            return "STM";
        } else if (dist.equals("Setúbal")) {
            return "STB";
        } else if (dist.equals("Viana do Castelo")) {
            return "VCT";
        } else if (dist.equals("Vila Real")) {
            return "VRL";
        } else if (dist.equals("Viseu")) {
            return "VSE";
        } else {
            return "Invalid District";
        }
    }



    private List<WeatherWarning> filterWarningsByRegion(List<WeatherWarning> warnings, String region) {
        List<WeatherWarning> filteredWarnings = new ArrayList<>();
        String tipo="green";
        for (WeatherWarning warning : warnings) {
            if (region.equals(warning.getIdAreaAviso()) && !tipo.equals(warning.getAwarenessLevelID())) {
                filteredWarnings.add(warning);
            }
        }
        return filteredWarnings;
    }

}
