package com.example.redact_book;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Marker singleMarker; // Referencia al único marcador permitido

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        fusedLocationClient = new FusedLocationProviderClient(this);

        // Callback para recibir actualizaciones de ubicación
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Actualiza la posición en el mapa
                    updateLocationOnMap(location);
                }
            }
        };

        // Verificar permisos de ubicación y solicitarlos si es necesario
        if (checkLocationPermission()) {
            initMap();
        } else {
            requestLocationPermission();
        }
    }

    // Verificar si se tienen permisos de ubicación
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    // Solicitar permisos de ubicación al usuario
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    // Manejar la respuesta del usuario a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap(); // Inicializar el mapa si se conceden los permisos
            } else {
                // Mostrar mensaje de permiso denegado
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Inicializar el mapa
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Callback cuando el mapa está listo para ser usado
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Verificar si se tienen los permisos necesarios para la ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // Habilitar marcador de ubicación del usuario
            // Agregar listener de clic en el mapa para colocar un solo marcador
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // Si ya existe un marcador, quitarlo antes de colocar el nuevo
                    if (singleMarker != null) {
                        singleMarker.remove();
                    }
                    // Colocar el nuevo marcador con el título de la ubicación y coordenadas
                    singleMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.ubicacion))
                            .snippet("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude));
                }
            });
        }
    }

    // Método para actualizar la ubicación en el mapa (no se utiliza actualmente)
    private void updateLocationOnMap(Location location) {
        // No es necesario actualizar la ubicación actual en este caso
    }

    // Detener actualizaciones de ubicación al pausar la actividad
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // Detener las actualizaciones de ubicación
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Método para ir a la actividad Configuracion
    public void onAñadirConfig(View v) {
        Intent i = new Intent(v.getContext(), Configuracion.class);
        startActivity(i);
    }
}
