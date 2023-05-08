package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UdalostDetailyActivity extends AppCompatActivity {

    //Inicializace proměnných
    private TextView nazevView, datumView, druhSportuView, kapacitaView, casView, mistoView, zobrazitView;
    private ImageView zpetView;
    private String nazev, datum, druhSportu, kapacita, cas, misto, zacatek, konec, uidUdalost, zakladatelID, userID;
    private Double zemSirka, zemDelka;
    private Integer pocetPrihlasenych;
    private Button chatBTN, prihlasitSeBtn, odhlasitSeBtn, dochazkaBtn;
    private FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udalost_detaily);

        //Získání dat z předchozí aktivity a uložení jich do proměnných
        nazev = getIntent().getStringExtra("nazev");
        datum = getIntent().getStringExtra("datum");
        druhSportu = getIntent().getStringExtra("sport");
        kapacita = getIntent().getStringExtra("kapacita");
        misto = getIntent().getStringExtra("misto");
        zacatek = getIntent().getStringExtra("zacatek");
        konec = getIntent().getStringExtra("konec");
        uidUdalost = getIntent().getStringExtra("UidUdalost");
        zakladatelID = getIntent().getStringExtra("zakladatelID");
        zemSirka = getIntent().getDoubleExtra("zemSirka",0);
        zemDelka = getIntent().getDoubleExtra("zemDelka",0);

        cas = zacatek + " - " + konec;

        //Firestore
        fStore = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Přiřazení proměných k jednotlivým částem obrazovky
        nazevView = findViewById(R.id.detNazevTV);
        datumView = findViewById(R.id.detDatumTV);
        druhSportuView = findViewById(R.id.detDruhSportuTV);
        kapacitaView = findViewById(R.id.detKapacitaTV);
        casView = findViewById(R.id.detCasTV);
        mistoView = findViewById(R.id.detMistoTV);
        zpetView = findViewById(R.id.detZpatkyIV);
        zobrazitView = findViewById(R.id.detZobrazitNaMape);
        chatBTN = findViewById(R.id.detChatBtn);
        prihlasitSeBtn = findViewById(R.id.detPrihlasitSeBtn);
        odhlasitSeBtn = findViewById(R.id.detOdhlasitBtn);
        dochazkaBtn = findViewById(R.id.detPrihlaseniBTN);

        //Nastavení textů do jednotlivých textView
        nazevView.setText(nazev);
        datumView.setText(datum);
        casView.setText(cas);
        druhSportuView.setText(druhSportu);
        mistoView.setText(misto);

        nastavKapacitu();

        zjistiDruhObrazovky();

        //OnClick listener pro zprovozdnění tlačítka zpět.
        zpetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prechodZpet();
            }
        });

        //OnClick listener pro zprovozdnění vyhledání místa na mapě.
        zobrazitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(zemSirka == 0 || zemDelka == 0){
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    if(!misto.isEmpty()){
                        intent.setData(Uri.parse("geo:0,0?q=" + misto));
                        Intent chooser = Intent.createChooser(intent,"Launch Maps");
                        startActivity(chooser);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    intent.setData(Uri.parse("geo:"+ zemSirka+","+ zemDelka));
                    Intent chooser = Intent.createChooser(intent,"Launch Maps");
                    startActivity(chooser);

                }

            }
        });

        //OnClick listener pro zprovozdnění tlačítka zpět.
        chatBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prechodChat();
            }
        });

        //OnClick listener pro zprovozdnění tlačítka prihlásit se.
        prihlasitSeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prihlasSeNaUdalost();
            }
        });

        //OnClick listener pro zprovozdnění tlačítka odhlásit se.
        odhlasitSeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odhlasitSeZUdalosti();
            }
        });

        //OnClick listener pro zprovozdnění tlačítka zobrazit docházku.
        dochazkaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UdalostDetailyActivity.this,UdalostPrihlaseniActivity.class);

                intent.putExtra("UidUdalost",uidUdalost);
                startActivity(intent);
            }
        });


    }

    /**
     * Metoda, která je odpovědná za přechod zpět do aktivity Událost.
     */
    private void prechodZpet() {
        UdalostDetailyActivity.this.finish();
    }

    /**
     * Metoda, která je odpovědná za přechod do aktivity Chat.
     */
    private void prechodChat() {
        Intent prechodChat = new Intent(UdalostDetailyActivity.this, GroupChatActivity.class);

        prechodChat.putExtra("nazev", nazev);
        prechodChat.putExtra("udalostUid", uidUdalost);

        startActivity(prechodChat);
    }

    /**
     * Metoda, která je odpovědná za získání počtu aktuálně přihlášených lidí na událost a následně nastaví kapacitu do textView.
     */
    private void nastavKapacitu() {
        //Získávání počtu přihlášených lidí
        Query query = fStore.collection("Události").document(uidUdalost).collection("Prihlaseni");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Výpočet proběhl správně. Dochází k nastavení textView
                    AggregateQuerySnapshot snapshot = task.getResult();
                    pocetPrihlasenych = Math.toIntExact(snapshot.getCount());
                    kapacitaView.setText(pocetPrihlasenych+ "/" + kapacita);
                } else {
                    // Výpočet proběhl správně. Dochází k nastavení textView a vypsání chybové zprávy
                    kapacitaView.setText( "?/" + kapacita);
                    Toast.makeText(UdalostDetailyActivity.this, "Aktuální kapacitu nebylo možné nahrát.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metoda, která je odpovědná za správné zobrazení tlačítek na obrazovce.
     */
    private void zjistiDruhObrazovky() {
        //Zjistění, zda uživatel založil událost.
        if(zakladatelID.equals(userID)){
            prihlasitSeBtn.setVisibility(View.INVISIBLE);
            odhlasitSeBtn.setVisibility(View.INVISIBLE);
        }else {
            //Zjištění pokud je uživatel již přihlášen k databázi
            fStore.collection("Události").document(uidUdalost).collection("Prihlaseni").whereEqualTo("uživatel",userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Map<String,Object> uzivatelZDb = new HashMap<>();

                            uzivatelZDb = document.getData();


                            if (uzivatelZDb.isEmpty()){
                                //Pokud není
                                prihlasitSeBtn.setVisibility(View.VISIBLE);
                                odhlasitSeBtn.setVisibility(View.INVISIBLE);

                            } else {
                                //Pokud je
                                prihlasitSeBtn.setVisibility(View.INVISIBLE);
                                odhlasitSeBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Metoda, která je odpovědná za přihlášení se uživatele k události.
     */
    private void prihlasSeNaUdalost() {
        //Nejprve se získá počet dokumentů v kolekci přihlášeni.
        long[] pocetZaznamuPole = {0};
        Query query =  fStore.collection("Události").document(uidUdalost).collection("Prihlaseni");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();

                    pocetZaznamuPole[0] = snapshot.getCount();
                    int kapacitaInt = Integer.valueOf(kapacita);

                    //Zjistění zda není plná kapacita
                    if( pocetZaznamuPole[0] < kapacitaInt){
                        //Pokud není
                        Map<String,Object> uzivatel = new HashMap<>();

                        uzivatel.put("uživatel",userID);

                        Map<String,Object> udalost = new HashMap<>();

                        udalost.put("událost",uidUdalost);

                        fStore.collection("Události").document(uidUdalost).collection("Prihlaseni").document(userID).set(uzivatel);
                        fStore.collection("Uživatelé").document(userID).collection("PrihlaseneUdalosti").document(uidUdalost).set(udalost);

                        UdalostDetailyActivity.this.recreate();
                        nastavKapacitu();

                        Toast.makeText(UdalostDetailyActivity.this, "Přihlásili jste se na událost", Toast.LENGTH_SHORT).show();
                    } else {
                        //Pokud je
                        Toast.makeText(UdalostDetailyActivity.this, "Kapacita události je plná, nelze se přihlásit", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(UdalostDetailyActivity.this, " Na událost se nebylo možné přihlásit, zkuste později.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metoda, která je odpovědná za odhlášení se z události.
     */
    private void odhlasitSeZUdalosti() {
        fStore.collection("Události").document(uidUdalost).collection("Prihlaseni").document(userID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fStore.collection("Uživatelé").document(userID).collection("PrihlaseneUdalosti").document(uidUdalost).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UdalostDetailyActivity.this, "Odhlásili jste se z události", Toast.LENGTH_SHORT).show();
                            UdalostDetailyActivity.this.recreate();

                            nastavKapacitu();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UdalostDetailyActivity.this, "Nebylo možné vás odhlásit z události, zkuste později", Toast.LENGTH_SHORT).show();
            }
        });
    }




}