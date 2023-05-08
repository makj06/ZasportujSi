package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SportovisteActivity extends AppCompatActivity {

    private Button tenisVyhledat, fotbalVyhledat, florbalVyhledat, basketVyhledat;
    private Integer dMezihodnota, dPocatecniHodnota;
    private String vzdalenost;
    private double zemSirka, zemDelka;
    private EditText vzdalenostET;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportoviste);


        BottomNavigationView view = findViewById(R.id.spodniNavigace);

        tenisVyhledat = findViewById(R.id.sportTenisBtn);
        fotbalVyhledat = findViewById(R.id.sportFotbalBtn);
        florbalVyhledat = findViewById(R.id.sportFlorbalBtn);
        basketVyhledat = findViewById(R.id.sportBasketbalBtn);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        vzdalenostET = findViewById(R.id.sportVzdalenostET);
        dPocatecniHodnota=10;

        vzdalenostET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pridejDialog();
            }
        });

        tenisVyhledat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vzdalenostET.getText().toString().isEmpty()){
                    Toast.makeText(SportovisteActivity.this, "Zadejte vzdálenost ve které chcete vyhledávat", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(SportovisteActivity.this,ZobrazMistaActivity.class);

                    intent.putExtra("sport","tenisové hřiště");
                    intent.putExtra("vzdalenost",vzdalenost);
                    intent.putExtra("zemDelka",zemDelka);
                    intent.putExtra("zemSirka",zemSirka);

                    startActivity(intent);
                }
            }
        });

        fotbalVyhledat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vzdalenostET.getText().toString().isEmpty()){
                    Toast.makeText(SportovisteActivity.this, "Zadejte vzdálenost ve které chcete vyhledávat", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(SportovisteActivity.this,ZobrazMistaActivity.class);

                    intent.putExtra("sport","fotbalové hřiště");
                    intent.putExtra("vzdalenost",vzdalenost);
                    intent.putExtra("zemDelka",zemDelka);
                    intent.putExtra("zemSirka",zemSirka);

                    startActivity(intent);
                }
            }
        });

        florbalVyhledat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vzdalenostET.getText().toString().isEmpty()){
                    Toast.makeText(SportovisteActivity.this, "Zadejte vzdálenost ve které chcete vyhledávat", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(SportovisteActivity.this,ZobrazMistaActivity.class);

                    intent.putExtra("sport","multifunkční hala");
                    intent.putExtra("vzdalenost",vzdalenost);
                    intent.putExtra("zemDelka",zemDelka);
                    intent.putExtra("zemSirka",zemSirka);

                    startActivity(intent);
                }
            }
        });

        basketVyhledat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vzdalenostET.getText().toString().isEmpty()){
                    Toast.makeText(SportovisteActivity.this, "Zadejte vzdálenost ve které chcete vyhledávat", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(SportovisteActivity.this,ZobrazMistaActivity.class);

                    intent.putExtra("sport","basketbalové hřiště");
                    intent.putExtra("vzdalenost",vzdalenost);
                    intent.putExtra("zemDelka",zemDelka);
                    intent.putExtra("zemSirka",zemSirka);

                    startActivity(intent);
                }
            }
        });

        vytvorSpodniMenu(view);



        ziskejPolohu();

    }

    /**
     * Metoda, která je odpovědná za zísnání aktuální polohy zařízení
     * source: https://www.youtube.com/watch?v=XfCbE4CyGVQ
     */

    private void ziskejPolohu() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(SportovisteActivity.this, Locale.getDefault());
                        List<Address> adresaList = null;
                        try {
                            adresaList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            zemSirka = adresaList.get(0).getLatitude();
                            zemDelka = adresaList.get(0).getLongitude();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(SportovisteActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ziskejPolohu();
            } else {
                Toast.makeText(this, "Pro vyhledání je nutné mít povolený přístup k poloze", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Metoda, která je odpovědná za vytvoření dialogu pro výběr
     */
    private void pridejDialog() {
        final Dialog d = new Dialog(SportovisteActivity.this);
        d.setContentView(R.layout.number_picker_dialog);
        Button pridat = d.findViewById(R.id.numberDialogNastavitBtn);
        TextView nazev = d.findViewById(R.id.numberDialogNazev);
        nazev.setText("Vyberte vzdálenost v km");
        NumberPicker numberPicker = d.findViewById(R.id.dialogNumberPicker);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);
        numberPicker.setValue(dPocatecniHodnota);

        pridat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dMezihodnota = numberPicker.getValue();
                vzdalenostET.setText(dMezihodnota + " Km");
                vzdalenost = String.valueOf(dMezihodnota *1000);
                dPocatecniHodnota = dMezihodnota;
                d.dismiss();
            }
        });
        d.show();
    }

    /**
     * Metoda, který je zodpovědná za vytvoření spodního menu.
     */
    private void vytvorSpodniMenu(BottomNavigationView view) {
        view.setSelectedItemId(R.id.sportoviste);

        view.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mojeUdalosti:
                        startActivity(new Intent(getApplicationContext(), MojeUdalostiActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), ProfilActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.udalost:
                        startActivity(new Intent(getApplicationContext(),UdalostiActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.sportoviste:
                        return true;
                }
                return false;
            }
        });
    }
}