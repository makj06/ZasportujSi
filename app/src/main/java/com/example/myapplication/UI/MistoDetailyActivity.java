package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.DatoveTypy.OblibeneMisto;
import com.example.myapplication.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MistoDetailyActivity extends AppCompatActivity {

    private TextView nazevTV, oteviraciDobaTV, telefonTV, webTV, polohaTv, hodnoceniTV, pocetRecenziTV;
    private ImageView zpatkyIV, pridatIV, odebratIV, obrazekIV;
    private String IDMista, nazev, telefon, web, poloha, hodnoceni, pocetRecenzi, userID, ulice, cisloPopisne, mesto;
    private double zemSirka, zemDelka;
    private List<String> oteviraciDoba;
    private List<PhotoMetadata> metadata;
    private FetchPlaceRequest request;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misto_detaily);

        //Přiřažení proměnných k jednotlivým částem obrazovky
        nazevTV = findViewById(R.id.detMNazevTV);
        oteviraciDobaTV = findViewById(R.id.detMOtviraciDobaTV);
        telefonTV = findViewById(R.id.detMTelefonTV);
        webTV = findViewById(R.id.detMWebTV);
        polohaTv = findViewById(R.id.detMPolohaTV);
        hodnoceniTV = findViewById(R.id.detMHodniceniTV);
        pocetRecenziTV = findViewById(R.id.detMPocetHodnoceniTV);
        zpatkyIV = findViewById(R.id.detMZpatkyIV);
        pridatIV = findViewById(R.id.detMPridatIV);
        odebratIV = findViewById(R.id.detMOdebratIV);
        obrazekIV = findViewById(R.id.detMObrazekIV);

        //Firestore
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //Získání dat z Places API
        ziskejDataZGoogle();

        //Vybráni barvy srdce
        vyberIvSrdce();

        //OnClick listener pro image view přidat
        pridatIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pridejMisto();
            }
        });

        //OnClick listener pro image view odebrat
        odebratIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odeberMisto();
            }
        });

        //OnClick listener pro image view zpět
        zpatkyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MistoDetailyActivity.this.finish();
            }
        });

    }

    /**
     * Metoda, která je odpovědná za výběr správného srdce při tvorbě obrazovky
     */
    private void vyberIvSrdce() {
        fStore.collection("Uživatelé").document(userID).collection("Oblíbené Místa").whereEqualTo("idMista",IDMista).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Map<String,Object> uzivatelZDb = new HashMap<>();

                        uzivatelZDb = document.getData();


                        if (uzivatelZDb.isEmpty()){
                            //Pokud není
                            pridatIV.setVisibility(View.VISIBLE);
                            odebratIV.setVisibility(View.GONE);

                        } else {
                            //Pokud je
                            pridatIV.setVisibility(View.GONE);
                            odebratIV.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    /**
     * Metoda, která je odpovědná za odebrání místa z Db.
     */
    private void odeberMisto() {
        fStore.collection("Uživatelé").document(userID).collection("Oblíbené Místa").document(IDMista).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pridatIV.setVisibility(View.VISIBLE);
                odebratIV.setVisibility(View.GONE);
                Toast.makeText(MistoDetailyActivity.this, "Místo přidáno do oblíbených", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MistoDetailyActivity.this, "Místo nebylo možné odebrat z oblíbených. Zkuste prosím později", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Metoda, která je odpovědná za přidání místa do Db.
     */
    private void pridejMisto() {

        OblibeneMisto oblibeneMisto = new OblibeneMisto(ulice,cisloPopisne,mesto,nazev,IDMista,zemSirka,zemDelka);

        fStore.collection("Uživatelé").document(userID).collection("Oblíbené Místa").document(IDMista).set(oblibeneMisto).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pridatIV.setVisibility(View.GONE);
                odebratIV.setVisibility(View.VISIBLE);
                Toast.makeText(MistoDetailyActivity.this, "Místo přidáno do oblíbených", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MistoDetailyActivity.this, "Místo nebylo možné přidat do oblíbených", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * Metoda, které je odpovědná za získávání dat z google place API
     */
    private void ziskejDataZGoogle() {
        //Inicializace google places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.placesKey, Locale.ENGLISH);
        }
        //Vytvoření klienta
        placesClient = Places.createClient(this);
        //Získání ID místa, pro které chceme získat detaily
        IDMista = getIntent().getStringExtra("id");

        //Vytvoření pole, které bude obsahovat jednotlivé informace, které budou získány
        placeFields = Arrays.asList(Place.Field.LAT_LNG,Place.Field.NAME,Place.Field.ADDRESS_COMPONENTS,Place.Field.ID,Place.Field.RATING,Place.Field.USER_RATINGS_TOTAL, Place.Field.OPENING_HOURS,Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.PHOTO_METADATAS);

        //Vytvoření requestu na data
        request = FetchPlaceRequest.newInstance(IDMista, placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            //Získání dat
            Place place = response.getPlace();

            ziskejAdresu(place);


            //Přiřažení dat k jednotlivým proměným
            LatLng lat_long = place.getLatLng();
            zemDelka =  lat_long.longitude;
            zemSirka =  lat_long.latitude;
            nazev = place.getName();
            hodnoceni = String.valueOf(place.getRating());
            pocetRecenzi = String.valueOf(place.getUserRatingsTotal());
            telefon = place.getPhoneNumber();
            web = String.valueOf(place.getWebsiteUri());
            metadata = place.getPhotoMetadatas();

            if(ulice == null || cisloPopisne == null || mesto == null){
                poloha = "Přesná adresa není k dispozici";
            }else {
                poloha = ulice + " "+ cisloPopisne + " "+ mesto;
            }


            if(place.getOpeningHours() != null){
                oteviraciDoba = place.getOpeningHours().getWeekdayText();
            }

            nastavText();

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Toast.makeText(this, "Místo nebylo možné najít", Toast.LENGTH_SHORT).show();
                final int statusCode = apiException.getStatusCode();
            }
        });
    }

    /**
     * Metoda, která je odpovědná za vytvoření komponenty adresy
     * @param place konkrétní místo
     */
    private void ziskejAdresu(Place place) {
        AddressComponent[] addressComponent = place.getAddressComponents().asList().toArray(new AddressComponent[0]);

        for (int i = 0; i< addressComponent.length;i++){

            List<String> typ = new ArrayList<>();
            typ = addressComponent[i].getTypes();
            if (typ.contains("route")){
                ulice = addressComponent[i].getName();
            }

            if (typ.contains("street_number")){
                cisloPopisne = addressComponent[i].getName();
            }

            if (typ.contains("sublocality_level_1")){
                mesto = addressComponent[i].getName();
            }
        }
    }

    /**
     * Metoda, která je odpovědná za nastavení dat do jednotlivých View obrazovky.
     */
    private void nastavText() {
        nazevTV.setText(nazev);

        if(telefon == null){
            telefonTV.setText("Nejsou žádné informace o telefoním čísle");
        }else {
            telefonTV.setText(telefon);
        }

        if(web == "null"|| web == null ){
            webTV.setText("Nejsou žádné informace o webové stránce");
        }else {
            webTV.setText(web);
        }

        polohaTv.setText(poloha);

        hodnoceniTV.setText(hodnoceni);
        pocetRecenziTV.setText(pocetRecenzi + " hodnocení");

        if(oteviraciDoba == null){
            oteviraciDobaTV.setText("Nejsou žádné inforace o otevírací době");
        }else {
            oteviraciDobaTV.setText(oteviraciDoba.get(0) + "\n"
                    + oteviraciDoba.get(1) + "\n"
                    + oteviraciDoba.get(2) + "\n"
                    + oteviraciDoba.get(3) + "\n"
                    + oteviraciDoba.get(4) + "\n"
                    + oteviraciDoba.get(5)+ "\n"
                    + oteviraciDoba.get(6));
        }


        //Nahrání fotky s pomocé google api
        if(metadata == null || metadata.isEmpty()){
            obrazekIV.setImageResource(R.drawable.nophoto);
        } else {
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                obrazekIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
                obrazekIV.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        }
    }
}