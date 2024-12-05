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
import android.widget.ImageButton;
import android.widget.TextView;

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

        RecyclerView recyclerView = findViewById(R.id.warningsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WarningAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WeatherWarningViewModel.class);
        viewModel.getWarnings().observe(this, warnings -> {
            if (warnings != null && currentRegion != null) {
                List<WeatherWarning> filteredWarnings = filterWarningsByRegion(warnings, currentRegion);
                if(filteredWarnings.size()==0){reg.setText("Não exitem riscos para " + setDistrict(currentRegion));
                }else{reg.setText("Região atual: " + setDistrict(currentRegion));}
                adapter.updateWarnings(filteredWarnings);
                updateMapWithWarnings(filteredWarnings);
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastKnownLocation();
        }

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
                currentRegion = address.getAdminArea(); // Use the appropriate method to get the region
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
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom));
    }

    private void updateMapWithWarnings(List<WeatherWarning> warnings) {
        if (googleMap != null) {
            googleMap.clear();
            updateMapLocation();
            for (WeatherWarning warning : warnings) {
                double warningLatitude = Double.parseDouble(warning.getLatitude());
                double warningLongitude = Double.parseDouble(warning.getLongitude());
                if (isWithinRadius(CURRENT_LATITUDE, CURRENT_LONGITUDE, warningLatitude, warningLongitude, RADIUS_KM)) {
                    LatLng warningLocation = new LatLng(warningLatitude, warningLongitude);
                    googleMap.addMarker(new MarkerOptions()
                            .position(warningLocation)
                            .title(warning.getAwarenessTypeName())
                            .snippet("Level: " + warning.getAwarenessLevelID() + "\nRegion: " + warning.getIdAreaAviso()));
                }
            }
        }
    }

    private boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusKm) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000 <= radiusKm;
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
