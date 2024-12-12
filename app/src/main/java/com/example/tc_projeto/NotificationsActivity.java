package com.example.tc_projeto;

import android.Manifest;
import android.content.Intent;
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
    private String selectedItem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        TextView reg= findViewById(R.id.not_reg);

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

//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            getLastKnownLocation();
//        }

        Spinner spinner = findViewById(R.id.spinner);

        // Cria um ArrayAdapter usando o array de strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);

        // Define o layout para o item do Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica o adaptador ao Spinner
        spinner.setAdapter(adapter);

        selectedItem = spinner.getSelectedItem().toString();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                Toast.makeText(parent.getContext(), "Item selecionado: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

    public String setDistrict(String reg) {
        if (reg.equals("BGC")) {
            return "Bragança";
        }
        if (reg.equals("AVR")) {
            return "Aveiro";
        }
        if (reg.equals("BEJ")) {
            return "Beja";
        }
        if (reg.equals("BRA")) {
            return "Braga";
        }
        if (reg.equals("CBR")) {
            return "Castelo Branco";
        }
        if (reg.equals("COI")) {
            return "Coimbra";
        }
        if (reg.equals("EVR")) {
            return "Évora";
        }
        if (reg.equals("FAR")) {
            return "Faro";
        }
        if (reg.equals("GUA")) {
            return "Guarda";
        }
        if (reg.equals("LEI")) {
            return "Leiria";
        }
        if (reg.equals("LIS")) {
            return "Lisboa";
        }
        if (reg.equals("PTG")) {
            return "Portalegre";
        }
        if (reg.equals("PRT")) {
            return "Porto";
        }
        if (reg.equals("STM")) {
            return "Santarém";
        }
        if (reg.equals("STB")) {
            return "Setúbal";
        }
        if (reg.equals("VCT")) {
            return "Viana do Castelo";
        }
        if (reg.equals("VRL")) {
            return "Vila Real";
        }
        if (reg.equals("VSE")) {
            return "Viseu";
        }
        // Caso o valor de 'reg' não seja encontrado, retorna "Desconhecido"
        return "Desconhecido";
    }

}
