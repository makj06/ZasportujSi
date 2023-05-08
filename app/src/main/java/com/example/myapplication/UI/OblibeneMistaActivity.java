package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Adaptery.OblibeneMistoAdapter;
import com.example.myapplication.DatoveTypy.OblibeneMisto;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OblibeneMistaActivity extends AppCompatActivity implements KlikatelnyRVInterface {

    private String nazev, proKoho, druhSportu, datum, zacatek, konec, kapacita, adresa, zacatekParse, datumParse;
    private double zemDelka;
    private double zemSirka;
    private ImageView zpetIV, pridatIV;
    private RecyclerView oblMistaRV;
    private List<OblibeneMisto> oblibeneMistoList;
    private OblibeneMistoAdapter oblibeneMistoAdapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oblibene_mista);

        //Přiřazení proměnných k jednotlivým částem obrazovky
        zpetIV = findViewById(R.id.oblZpetIV);
        pridatIV = findViewById(R.id.oblPridatIV);
        oblMistaRV= findViewById(R.id.oblRV);

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        //Získat data
        nazev = getIntent().getStringExtra("nazev");
        proKoho = getIntent().getStringExtra("proKoho");
        druhSportu = getIntent().getStringExtra("druhSportu");
        datum = getIntent().getStringExtra("datum");
        zacatek = getIntent().getStringExtra("zacatek");
        konec = getIntent().getStringExtra("konec");
        kapacita = getIntent().getStringExtra("kapacita");
        zacatekParse = getIntent().getStringExtra("zacatekParse");
        datumParse = getIntent().getStringExtra("datumParse");

        zprovozniRV();

        //Přidání onClick listeneru pro přechod do obrazovky přidat událost
        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pridatMisto = new Intent(OblibeneMistaActivity.this,PridatUdalostActivity.class);

                pridatMisto.putExtra("nazev",nazev);
                pridatMisto.putExtra("proKoho",proKoho);
                pridatMisto.putExtra("druhSportu",druhSportu);
                pridatMisto.putExtra("datum",datum);
                pridatMisto.putExtra("zacatek",zacatek);
                pridatMisto.putExtra("konec",konec);
                pridatMisto.putExtra("kapacita",kapacita);
                pridatMisto.putExtra("zacatekParse", zacatekParse);
                pridatMisto.putExtra("datumParse", datumParse);

                startActivity(pridatMisto);
            }
        });

        //Přidání onClick listeneru pro přechod do obrazovky přidat oblíbení místo
        pridatIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pridatMisto = new Intent(OblibeneMistaActivity.this,PridatMistoActivity.class);

                pridatMisto.putExtra("nazev",nazev);
                pridatMisto.putExtra("proKoho",proKoho);
                pridatMisto.putExtra("druhSportu",druhSportu);
                pridatMisto.putExtra("datum",datum);
                pridatMisto.putExtra("zacatek",zacatek);
                pridatMisto.putExtra("konec",konec);
                pridatMisto.putExtra("kapacita",kapacita);
                pridatMisto.putExtra("zacatekParse", zacatekParse);
                pridatMisto.putExtra("datumParse", datumParse);

                startActivity(pridatMisto);
            }
        });



    }

    private void zprovozniRV() {
        String userID = fAuth.getCurrentUser().getUid();
        oblMistaRV.setHasFixedSize(true);
        oblMistaRV.setLayoutManager(new LinearLayoutManager(this));
        oblibeneMistoList = new ArrayList<>();
        oblibeneMistoAdapter = new OblibeneMistoAdapter(OblibeneMistaActivity.this,oblibeneMistoList,this);

        fStore.collection("Uživatelé").document(userID).collection("Oblíbené Místa").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){

                        OblibeneMisto misto = documentSnapshot.toObject(OblibeneMisto.class);
                        oblibeneMistoList.add(misto);
                        oblMistaRV.setAdapter(oblibeneMistoAdapter);
                    }

                } else {
                    Toast.makeText(OblibeneMistaActivity.this, "Data o oblíbených místech nebylo možné nahrát", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onItemClick(int pozice) {
        Intent pridatUdalostActivity = new Intent(OblibeneMistaActivity.this,PridatUdalostActivity.class);

        zemDelka = oblibeneMistoList.get(pozice).getZemDelka();
        zemSirka = oblibeneMistoList.get(pozice).getZemSirka();

        String ulice = oblibeneMistoList.get(pozice).getUlice();
        String cisloPopisne = oblibeneMistoList.get(pozice).getCisloPopisne();
        String mesto = oblibeneMistoList.get(pozice).getMesto();

        if(ulice ==null){
            ulice = "";
        }

        if(cisloPopisne == null){
            cisloPopisne = "";
        }

        if(mesto ==null){
            mesto = "";
        }

        adresa = ulice + " " + cisloPopisne  + " " +mesto;

        pridatUdalostActivity.putExtra("nazev",nazev);
        pridatUdalostActivity.putExtra("proKoho",proKoho);
        pridatUdalostActivity.putExtra("druhSportu",druhSportu);
        pridatUdalostActivity.putExtra("datum",datum);
        pridatUdalostActivity.putExtra("zacatek",zacatek);
        pridatUdalostActivity.putExtra("konec",konec);
        pridatUdalostActivity.putExtra("kapacita",kapacita);
        pridatUdalostActivity.putExtra("adresa",adresa);
        pridatUdalostActivity.putExtra("zemDelka",zemDelka);
        pridatUdalostActivity.putExtra("zemSirka",zemSirka);
        pridatUdalostActivity.putExtra("zacatekParse", zacatekParse);
        pridatUdalostActivity.putExtra("datumParse", datumParse);

        startActivity(pridatUdalostActivity);
    }
}