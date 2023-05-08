package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.DatoveTypy.OblibeneMisto;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PridatMistoActivity extends AppCompatActivity {
    private ImageView zpetIV;
    private Button  odeslatBtn;
    private EditText nazevInput, uliceInput, cisloInput, mestoInput;
    private String nazevMista, ulice, cisloPopisne, mesto, idMista, proKoho, druhSportu, nazev, datum, zacatek, konec, kapacita, zacatekParse, datumParse;
    private long zemDelka, zemSirka;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pridej_misto);

        zpetIV = findViewById(R.id.pridMZpetIV);
        nazevInput = findViewById(R.id.pridMNazevInput);
        uliceInput = findViewById(R.id.pridMUliceInput);
        cisloInput = findViewById(R.id.pridMCisloInput);
        mestoInput = findViewById(R.id.pridMMestoInput);
        odeslatBtn = findViewById(R.id.pridMBtn);

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        //Získání dat
        nazev = getIntent().getStringExtra("nazev");
        proKoho = getIntent().getStringExtra("proKoho");
        druhSportu = getIntent().getStringExtra("druhSportu");
        datum = getIntent().getStringExtra("datum");
        zacatek = getIntent().getStringExtra("zacatek");
        konec = getIntent().getStringExtra("konec");
        kapacita = getIntent().getStringExtra("kapacita");
        zacatekParse = getIntent().getStringExtra("zacatekParse");
        datumParse = getIntent().getStringExtra("datumParse");

        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prejdiZpet();
            }
        });

        odeslatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Získání dat z inputů
                nazevMista = nazevInput.getText().toString();
                ulice = uliceInput.getText().toString();
                cisloPopisne = cisloInput.getText().toString();
                mesto = mestoInput.getText().toString();
                zemDelka = 0;
                zemSirka = 0;
                idMista = "";

                //Kontrola, zda jsou všechny inputy vyplněné
                if (TextUtils.isEmpty(nazevMista)){
                    nazevInput.setError("Zadejte název");
                    nazevInput.requestFocus();
                } else if (TextUtils.isEmpty(ulice)){
                    uliceInput.setError("Zadejte ulici");
                    uliceInput.requestFocus();
                } else if(TextUtils.isEmpty(cisloPopisne)){
                    cisloInput.setError("Zadejte číslo popisné");
                    cisloInput.requestFocus();
                } else if(TextUtils.isEmpty(mesto)){
                    mestoInput.setError("Zadejte město");
                    mestoInput.requestFocus();
                } else {
                    pridejMisto(nazevMista, ulice,cisloPopisne,mesto,zemDelka,zemSirka,idMista);
                }
            }
        });
    }

    /**
     * Metoda odpovědná za přechod do obrazovky oblíbené místa
     */
    private void prejdiZpet() {
        Intent oblibeneMisto = new Intent(PridatMistoActivity.this,OblibeneMistaActivity.class);

        oblibeneMisto.putExtra("nazev",nazev);
        oblibeneMisto.putExtra("proKoho",proKoho);
        oblibeneMisto.putExtra("druhSportu",druhSportu);
        oblibeneMisto.putExtra("datum",datumParse);
        oblibeneMisto.putExtra("zacatek",zacatek);
        oblibeneMisto.putExtra("konec",konec);
        oblibeneMisto.putExtra("kapacita",kapacita);
        oblibeneMisto.putExtra("zacatekParse", zacatekParse);
        oblibeneMisto.putExtra("datumParse", datumParse);

        startActivity(oblibeneMisto);
    }

    /**
     * Metoda opdpovědná za přidání místa.
     * @param nazev Název místa
     * @param ulice Název ulice
     * @param cisloPopisne číslo popisné
     * @param mesto Název města
     * @param zemDelka Zem Délka místa
     * @param zemSirka Zeměpisná šířka místa
     * @param idMista ID místa
     */
    private void pridejMisto(String nazev, String ulice, String cisloPopisne, String mesto, long zemDelka, long zemSirka, String idMista) {
        String userID = fAuth.getCurrentUser().getUid();
        OblibeneMisto oblibeneMisto = new OblibeneMisto(ulice,cisloPopisne,mesto,nazev,idMista,zemSirka,zemDelka);

        fStore.collection("Uživatelé").document(userID).collection("Oblíbené Místa").document().set(oblibeneMisto).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                prejdiZpet();
                Toast.makeText(PridatMistoActivity.this, "Oblíbené místo bylo přidáno", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PridatMistoActivity.this, "Oblíbené misto nebylo možné přidat. Zkuste později znovu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}