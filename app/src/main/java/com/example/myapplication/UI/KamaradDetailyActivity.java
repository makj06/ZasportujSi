package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

public class KamaradDetailyActivity extends AppCompatActivity {
    
    String jmeno, prijmeni, telefon, narozen, email, celeJmeno, UID;
    Integer pocetLet;
    TextView jmenoTV, telefonTV, narozenTV, emailTV;
    ImageView profilIV, zpetIV;
    FirebaseStorage fStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kamarad_detaily);

        //Získání dat z předchozí aktivity a uložení jich do proměnných
        jmeno = getIntent().getStringExtra("jmeno");
        prijmeni = getIntent().getStringExtra("prijmeni");
        telefon = getIntent().getStringExtra("telefon");
        narozen = getIntent().getStringExtra("narozen");
        email = getIntent().getStringExtra("email");
        UID = getIntent().getStringExtra("ID");

        celeJmeno = jmeno + " " + prijmeni;

        try {
            zjistiVek();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Přiřazení proměných k jednotlivým částem obrazovky
        jmenoTV = findViewById(R.id.detKJmenoTV);
        emailTV = findViewById(R.id.detKEmailTV);
        telefonTV = findViewById(R.id.detKTelefonTV);
        profilIV = findViewById(R.id.detKImageView);
        zpetIV = findViewById(R.id.detKzpetIV);
        narozenTV = findViewById(R.id.detKNarozenTV);

        ////Nastavení textů do jednotlivých textView
        jmenoTV.setText(celeJmeno);
        emailTV.setText(email);
        telefonTV.setText(telefon);

        if(pocetLet==null){
            narozenTV.setText("");
        }else{
            narozenTV.setText(pocetLet.toString() +" let");
        }



        nastavProfilovyObr();

        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KamaradDetailyActivity.this.finish();
            }
        });
    }

    /**
     * Metoda, která je odpovědná za zjistění věku uživatele
     * @throws ParseException
     */
    private void zjistiVek() throws ParseException {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd.MM.yyyy");

        Date date1 = simpleDateFormat.parse(currentDate);
        Date date2 = simpleDateFormat.parse(narozen);

        Instant instant = date1.toInstant();
        ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
        LocalDate aktualniDatum = zone.toLocalDate();

        Instant instant2 = date2.toInstant();
        ZonedDateTime zone2 = instant2.atZone(ZoneId.systemDefault());
        LocalDate datumNarozen = zone2.toLocalDate();

        pocetLet = Period.between(datumNarozen,aktualniDatum).getYears();
    }

    /**
     * Metoda, která je odpovědná za načtení obrázků do IV v profilu
     */
    private void nastavProfilovyObr() {

        fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReference();

        StorageReference obrazekReference = storageReference.child("profiloveObrazky/"+ UID + ".jpg");

        try {
            File obrazek = File.createTempFile("profiloveObrazky","jpg");
            obrazekReference.getFile(obrazek).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitMap = BitmapFactory.decodeFile(obrazek.getAbsolutePath());
                    profilIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    profilIV.setImageBitmap(bitMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(KamaradDetailyActivity.this, "Obrázek nebylo možné načíst", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}