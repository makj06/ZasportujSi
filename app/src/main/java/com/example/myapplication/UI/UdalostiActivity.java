package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;
import com.example.myapplication.DatoveTypy.Udalost;
import com.example.myapplication.Adaptery.UdalostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UdalostiActivity extends AppCompatActivity implements KlikatelnyRVInterface {

    //Inicializace proměnných
    private RecyclerView udalostRV;
    private List<Udalost> udalostList, databazeList, meziList;
    private List<String> docUidList, friendIDList;
    private UdalostAdapter udalostAdapter;
    private ImageView pridatUdalost, filtr;
    private BottomNavigationView bView;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udalosti);

        //Přiřažení jednotlivých proměnných k prvkům obrazovky
        pridatUdalost = findViewById(R.id.udlPridatIV);
        bView = findViewById(R.id.spodniNavigace);
        udalostRV = findViewById(R.id.udlRecyclerView);
        filtr = findViewById(R.id.udlFiltrIV);

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        //Vytvoření listů
        databazeList = new ArrayList<>();
        udalostList = new ArrayList<>();
        docUidList = new ArrayList<>();
        friendIDList = new ArrayList<>();
        meziList = new ArrayList<>();

        //Vytvoreni adapteru
        udalostAdapter = new UdalostAdapter(this, UdalostiActivity.this, udalostList);

        zprovozniRV();

        //OnClickListener pro tlačítko přidat událost událost
        pridatUdalost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prechodVytvoritUdalost();
            }
        });

        //OnClickListener pro obrázek filtr
        filtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zprovozniFiltr();
            }
        });

        //Vytvoření spodní navigace
        vytvorSpodniMenu(bView);
    }


    /**
     * Metoda, která je odpovědná za vytáhnutí dat z databáze a provozdnění RV
     */
    private void zprovozniRV() {
        //SetUp Recycler Viewu
        udalostRV.setHasFixedSize(true);
        udalostRV.setLayoutManager(new LinearLayoutManager(this));

        //Získání aktuálního času
        Timestamp aktualniCas = new Timestamp(new Date());

        fStore.collection("Události").whereGreaterThan("zacatekUdalosti",aktualniCas).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){

                        Udalost udalost = documentSnapshot.toObject(Udalost.class);
                        databazeList.add(udalost);

                        String Uid = documentSnapshot.getId();
                        docUidList.add(Uid);
                    }
                    //UdalostList se naplní daty z snapShotu
                    udalostList.addAll(databazeList);

                    //Získání událostí, který jsou pro všechny a uložení jich do meziListu
                    for(int i = 0; i<udalostList.size(); i++){
                        if(udalostList.get(i).getCilovka().equals("Všichni")){
                            meziList.add(udalostList.get(i));
                        }
                    }

                    //Vyprázdnění listu události a následný naplnění ho daty z meziListu. V poslední řadě nastavení adapteru
                    udalostList.clear();
                    udalostList.addAll(meziList);
                    udalostRV.setAdapter(udalostAdapter);
                }
            }
        });
    }


    /**
     * Metoda, která je odpovědníá za přechod z obrazovky udásloti do obrazovky pridatUdalost
     */

    private void prechodVytvoritUdalost() {
        Intent vytvoritPrechod = new Intent(UdalostiActivity.this, PridatUdalostActivity.class);
        startActivity(vytvoritPrechod);
    }

    /**
     * Metoda, která je odpovědná za zprovoznění dialogu filtru a jeho logiku.
     */
    private void zprovozniFiltr() {
        final Dialog d = new Dialog(UdalostiActivity.this);
        d.setContentView(R.layout.filtr_dialog);

        //Vyprázdnění a znovunaplnění listu se záznamy z DB
        udalostList.clear();
        udalostList.addAll(databazeList);

        //Přiřazení inicializace proměnných
        ImageView zavriIV = d.findViewById(R.id.filtrZavritIV);
        Button odeslatBTN = d.findViewById(R.id.filtrBtn);
        EditText mestoInput = d.findViewById(R.id.filtrMestoET);
        RadioButton prateleRb = d.findViewById(R.id.filtrPrateleRB);
        CheckBox florbalChB = d.findViewById(R.id.filtrFlorbalChB);
        CheckBox fotbalChB = d.findViewById(R.id.filtrFotbalChB);
        CheckBox tenisChB = d.findViewById(R.id.filtrTenisChB);
        CheckBox basketbalChB = d.findViewById(R.id.filtrBasketballChB);
        CheckBox ostatniChB = d.findViewById(R.id.filtrOstatniChB);

        //Metoda, která naplní pole friendIDList
        ziskejFriendlist();

        zavriIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        //OnClickListerner pro dialogové tlačítko odeslat. Je odpovědný za správné fitrování dat.
        odeslatBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String kdoPrihlasit = "";
                meziList.clear();

                //Vyhodnocení z radiogroup kdo se smí přihlásit.
                if(prateleRb.isChecked()){
                    kdoPrihlasit = "Přátelé";
                }else {
                    kdoPrihlasit = "Všichni";
                }

                //Vyfiltrování příspěvků, který jsou pouze pro
                if(kdoPrihlasit.equals("Přátelé")){
                    vyfiltrujKamarady(meziList);

                }else{

                    //Získání událostí, který jsou založený kamarády
                    for(int i = 0; i<udalostList.size(); i++){
                        if(udalostList.get(i).getCilovka().equals("Všichni")){
                            meziList.add(udalostList.get(i));
                        }
                    }

                }

                //Vymazání listu událostí
                udalostList.clear();

                //Znovunaplnění listu událostí.
                if(fotbalChB.isChecked()){
                    for(int i =0; i<meziList.size();i++){

                        if(meziList.get(i).getSport().equals("Fotbal")){
                            udalostList.add(meziList.get(i));
                        }
                    }
                }

                if(florbalChB.isChecked()){
                    for(int i =0; i<meziList.size();i++){

                        if(meziList.get(i).getSport().equals("Florbal")){
                            udalostList.add(meziList.get(i));
                        }
                    }
                }

                if(tenisChB.isChecked()){
                    for(int i =0; i<meziList.size();i++){

                        if(meziList.get(i).getSport().equals("Tenis")){
                            udalostList.add(meziList.get(i));
                        }
                    }
                }

                if(basketbalChB.isChecked()){
                    for(int i =0; i<meziList.size();i++){

                        if(meziList.get(i).getSport().equals("Basketbal")){
                            udalostList.add(meziList.get(i));
                        }
                    }
                }

                if(ostatniChB.isChecked()){
                    for(int i =0; i<meziList.size();i++){

                        if(meziList.get(i).getSport().equals("Ostatní")){
                            udalostList.add(meziList.get(i));
                        }
                    }
                }

                if(!fotbalChB.isChecked() && !florbalChB.isChecked() && !tenisChB.isChecked() && !basketbalChB.isChecked() && !ostatniChB.isChecked()){
                    udalostList.addAll(meziList);
                }

                String mesto= mestoInput.getText().toString();

                if(!mesto.isEmpty()){
                    for(int i =udalostList.size()-1; i>=0 ;i--){

                        if(!udalostList.get(i).getMisto().toLowerCase(Locale.ROOT).contains(mesto.toLowerCase(Locale.ROOT))){
                            udalostList.remove(i);
                        }
                    }
                }

                udalostAdapter.notifyDataSetChanged();


                d.dismiss();
            }
        });

        d.show();

        /*Zvětšení okna
        Source:https://stackoverflow.com/questions/10242144/adjusting-size-of-custom-dialog-box-in-android
        */
        Window window = d.getWindow();
        window.setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Metoda odpovědná za získání z databáze list přátel
     */
    private void ziskejFriendlist() {
        String userID = fAuth.getCurrentUser().getUid();

        fStore.collection("Uživatelé").document(userID).collection("FriendList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    //Získání z DB ID všech kamarádů
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        String Uid = documentSnapshot.getId();
                        friendIDList.add(Uid);
                    }

                }
            }
        });
    }

    /**
     * Metoda, který je zodpovědná za vyfiltrování událostí, kterou založili kamarádi.
     * @param meziList arrayList, který skladuje vyfiltrované události.
     */
    private void vyfiltrujKamarady(List<Udalost> meziList) {
        //Získání událostí, který jsou založený kamarády
        for(int i = 0; i<udalostList.size(); i++){
            if(friendIDList.contains(udalostList.get(i).getUživatel())){
                meziList.add(udalostList.get(i));
            }
        }
    }


    /**
     * Metoda, který je zodpovědná za vytvoření spodního menu.
     */
    private void vytvorSpodniMenu(BottomNavigationView view) {
        view.setSelectedItemId(R.id.udalost);

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
     * Metoda odpovědná za přechod do událost detaily v případě kliknutí na položku v RV
     * @param pozice pozice události v poli
     */
    @Override
    public void onItemClick(int pozice) {
        Intent udalostDetaily = new Intent(UdalostiActivity.this,UdalostDetailyActivity.class);

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