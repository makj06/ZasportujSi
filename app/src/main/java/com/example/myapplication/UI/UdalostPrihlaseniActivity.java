package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.Adaptery.UzivatelAdapter;
import com.example.myapplication.DatoveTypy.Uzivatel;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UdalostPrihlaseniActivity extends AppCompatActivity implements KlikatelnyRVInterface {

    private ImageView zpetIV;
    private RecyclerView ucastniciRV;
    private List<String> IDList;
    private List<Uzivatel> uzivatelList;
    private FirebaseFirestore fStore;
    private UzivatelAdapter uzivatelAdapter;
    private String IDDokumentu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udalost_prihlaseni);

        //Přiřažení proměnných k jednotlivým částem obrazovky
        zpetIV = findViewById(R.id.prihlZpatkyIV);
        ucastniciRV = findViewById(R.id.prihlRecyclerView);

        IDDokumentu = getIntent().getStringExtra("UidUdalost");

        //Firebase
        fStore = FirebaseFirestore.getInstance();

        získejData();

        //Zprovoznění tlačítka pro přechod zpět.
        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UdalostPrihlaseniActivity.this.finish();
            }
        });

    }

    /**
     * Metoda, která je odpovědná za získání dat o jednotlivých uživatelých z DB a následné nahrání jich do jednotlivých listů v RV.
     */
    private void získejData() {
        ucastniciRV.setHasFixedSize(true);
        ucastniciRV.setLayoutManager(new LinearLayoutManager(this));
        IDList = new ArrayList<>();
        uzivatelList = new ArrayList<>();
        uzivatelAdapter = new UzivatelAdapter(this, UdalostPrihlaseniActivity.this, uzivatelList);

        //Získání jednotlivých ID uživatelů, který jsou přihlášení na událost.
        fStore.collection("Události").document(IDDokumentu).collection("Prihlaseni").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                //Pokud se podaří data získat, tak se vezmou ID uživatelů a nahrajou se do array listu
                if(task.isSuccessful()){

                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){

                        String documentID = documentSnapshot.getId();
                        IDList.add(documentID);
                    }

                    /*
                    Po nahrání dat do arraylistu se projde celý arraylist a uloží se data o uživatelých do druhého atrraylistu.
                    Ze kterého se následně vytvoří jednotlivé listy v recycler view.
                    */
                    for(int i = 0; i < IDList.size(); i++){
                        String IDDokumentu = IDList.get(i);

                        fStore.collection("Uživatelé").document(IDDokumentu).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                Uzivatel uzivatel = documentSnapshot.toObject(Uzivatel.class);
                                uzivatelList.add(uzivatel);
                                ucastniciRV.setAdapter(uzivatelAdapter);

                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Metoda, která zajištuje otevření detailů konkrétního profilu
     * @param pozice je index ve kterým se nachází proměná, která byla zrovna stisknuta.
     */
    @Override
    public void onItemClick(int pozice) {
        Intent kamaradDetaily = new Intent(UdalostPrihlaseniActivity.this,KamaradDetailyActivity.class);

        kamaradDetaily.putExtra("jmeno",uzivatelList.get(pozice).getJmeno());
        kamaradDetaily.putExtra("prijmeni",uzivatelList.get(pozice).getPrijmeni());
        kamaradDetaily.putExtra("email",uzivatelList.get(pozice).getEmail());
        kamaradDetaily.putExtra("telefon",uzivatelList.get(pozice).getTelefon());
        kamaradDetaily.putExtra("narozen",uzivatelList.get(pozice).getNarozen());
        kamaradDetaily.putExtra("ID",uzivatelList.get(pozice).getUserID());

        startActivity(kamaradDetaily);
    }
}