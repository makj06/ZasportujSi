package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Adaptery.UdalostAdapter;
import com.example.myapplication.DatoveTypy.Udalost;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MojeUdalostiActivity extends AppCompatActivity implements KlikatelnyRVInterface {

    //Inicializace proměnných
    private Button prihlaseneBTN, vytvoreneBTN;
    private RecyclerView recyclerView;
    private BottomNavigationView bView;
    private List<Udalost> udalostList;
    private List<String> docUidList;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private UdalostAdapter udalostAdapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moje_udalosti);

        //Přiřazení jednotlivých proměnných k částem obrazovky
        prihlaseneBTN = findViewById(R.id.mojePrihlaseneBTN);
        vytvoreneBTN = findViewById(R.id.mojeVytvoreneBTN);
        recyclerView = findViewById(R.id.mojeRV);
        bView = findViewById(R.id.spodniNavigace);

        //Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //Vytvoření listů
        udalostList = new ArrayList<>();
        docUidList = new ArrayList<>();

        zprovozniRV();

        //OnClick listener pro zobrazení vytvořených události
        vytvoreneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ziskejVytvoreneUdalosti();
                vytvoreneBTN.setBackground(getResources().getDrawable(R.drawable.button));
                prihlaseneBTN.setBackgroundColor(getResources().getColor(R.color.primaryColor));
            }
        });

        //OnClick listener pro zobrazení událostí na které je uživatel přihlášen
        prihlaseneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ziskejPrihlaseneUdalosti();
                prihlaseneBTN.setBackground(getResources().getDrawable(R.drawable.button));
                vytvoreneBTN.setBackgroundColor(getResources().getColor(R.color.primaryColor));

            }
        });

        vytvorSpodniMenu();
    }

    /**
     * Metoda, která je odpovědná za prvnotní zprovoznění recycler viewu
     */
    private void zprovozniRV() {
        udalostAdapter = new UdalostAdapter(this,this,udalostList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(udalostAdapter);

        ziskejPrihlaseneUdalosti();
    }

    /**
     * Metoda, která proběhně při kliknutí na tlačítko prihlaseny a při startu activity. Nahrává jednotlivé data z databáze do arraylistu a následně je nahraje do Recycler vievu
     */
    private void ziskejPrihlaseneUdalosti() {
        fStore.collection("Uživatelé").document(userID).collection("PrihlaseneUdalosti").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> pridaneUdalostiIDList = new ArrayList<>();
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){

                        String documentID = documentSnapshot.getId();
                        pridaneUdalostiIDList.add(documentID);
                    }

                    fStore.collection("Události").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                udalostList.clear();
                                docUidList.clear();
                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()){

                                    if(pridaneUdalostiIDList.contains(documentSnapshot.getId())){
                                        Udalost udalost = documentSnapshot.toObject(Udalost.class);
                                        udalostList.add(udalost);

                                        String udalostID = documentSnapshot.getId();
                                        docUidList.add(udalostID);
                                    }
                                }
                                udalostAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }else {
                    Toast.makeText(MojeUdalostiActivity.this, "Data nebylo možné nahrát. Zkuste později.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metoda, která proběhně při kliknutí na tlačítko vytvorene. Nahrává jednotlivé data z databáze do arraylistu a následně je nahraje do Recycler vievu
     */
    private void ziskejVytvoreneUdalosti() {
        fStore.collection("Události").whereEqualTo("vytvoril",userID).orderBy("zacatekUdalosti").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    udalostList.clear();
                    docUidList.clear();

                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){

                        Udalost udalost = documentSnapshot.toObject(Udalost.class);
                        udalostList.add(udalost);

                        String udalostID = documentSnapshot.getId();
                        docUidList.add(udalostID);
                    }
                    udalostAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(MojeUdalostiActivity.this, "Data nebylo možné nahrát. Zkuste později.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Metoda, který je zodpovědná za vytvoření spodního menu.
     */
    private void vytvorSpodniMenu() {
        bView.setSelectedItemId(R.id.mojeUdalosti);

        bView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mojeUdalosti:
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
                        startActivity(new Intent(getApplicationContext(),SportovisteActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Metoda ondpovědná za otevírá detailů jednotlivých událostí
     * @param pozice v poli událostí
     */
    @Override
    public void onItemClick(int pozice) {
        Intent udalostDetaily = new Intent(MojeUdalostiActivity.this,UdalostDetailyActivity.class);

        udalostDetaily.putExtra("nazev",udalostList.get(pozice).getNazev());
        udalostDetaily.putExtra("datum",udalostList.get(pozice).getDatum());
        udalostDetaily.putExtra("zacatek",udalostList.get(pozice).getZacatek());
        udalostDetaily.putExtra("konec",udalostList.get(pozice).getKonec());
        udalostDetaily.putExtra("sport", udalostList.get(pozice).getSport());
        udalostDetaily.putExtra("kapacita", udalostList.get(pozice).getKapacita());
        udalostDetaily.putExtra("misto",udalostList.get(pozice).getMisto());
        udalostDetaily.putExtra("UidUdalost",docUidList.get(pozice));
        udalostDetaily.putExtra("zakladatelID",udalostList.get(pozice).getUživatel());
        udalostDetaily.putExtra("zemSirka", udalostList.get(pozice).getZemSirka());
        udalostDetaily.putExtra("zemDelka", udalostList.get(pozice).getZemDelka());

        startActivity(udalostDetaily);
    }
}