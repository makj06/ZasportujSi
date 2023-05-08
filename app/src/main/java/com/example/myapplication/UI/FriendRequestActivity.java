package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.Adaptery.FriendRequestAdapter;
import com.example.myapplication.DatoveTypy.Uzivatel;
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

public class FriendRequestActivity extends AppCompatActivity implements KlikatelnyRVInterface {

    private RecyclerView RV;
    private ImageView zpetIV;
    private String userID;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private List<Uzivatel> uzivatelList;
    private List<String> kamaradList;
    private List<Uzivatel> requestList;
    private FriendRequestAdapter friendRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        //Přiřazení jednotlivých proměnných k prvkům obrazovky
        RV = findViewById(R.id.FriendRV);
        zpetIV = findViewById(R.id.friendRZpetIV);

        //Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //Inicializace listů
        uzivatelList = new ArrayList<>();
        kamaradList = new ArrayList<>();
        requestList = new ArrayList<>();

        //Inicializace adaptéru
        friendRequestAdapter = new FriendRequestAdapter(this, this,requestList);

        získejData();

        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zpetFriendlist = new Intent(FriendRequestActivity.this, FriendlistActivity.class);
                startActivity(zpetFriendlist);
                finish();
            }
        });
    }

    /**
     * Metoda, která je odpovědná za získání dat o Friendrequestech
     */
    private void získejData() {
        //Získání údajů o uživatelích
        fStore.collection("Uživatelé").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Uzivatel uzivatel = documentSnapshot.toObject(Uzivatel.class);
                        uzivatelList.add(uzivatel);
                    }
                    //V případě, že se data dostanou, tak dojde k získání dat o friendRequestech
                    fStore.collection("Uživatelé").document(userID).collection("FriendRequest").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {

                                //Získání ID uživatelů z requestů
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String kamaradID = documentSnapshot.getId();
                                    kamaradList.add(kamaradID);
                                }

                                //Získání pouze uživatelů, který jsou na friendRequestListu
                                for(int i = 0; i<uzivatelList.size(); i++){
                                    if(kamaradList.contains(uzivatelList.get(i).getUserID())){
                                        requestList.add(uzivatelList.get(i));
                                    }
                                }

                                //Nastavení recycler viewu
                                RV.setHasFixedSize(true);
                                RV.setLayoutManager(new LinearLayoutManager(FriendRequestActivity.this));
                                RV.setAdapter(friendRequestAdapter);
                            }
                        }
                    });


                }
            }
        });
    }


    /**
     * Aktivita, která se spustí po kliknutí na list
     * @param pozice pozice listu v poli
     */
    @Override
    public void onItemClick(int pozice) {
        Intent kamaradDetaily = new Intent(FriendRequestActivity.this,KamaradDetailyActivity.class);

        kamaradDetaily.putExtra("jmeno",requestList.get(pozice).getJmeno());
        kamaradDetaily.putExtra("prijmeni",requestList.get(pozice).getPrijmeni());
        kamaradDetaily.putExtra("email",requestList.get(pozice).getEmail());
        kamaradDetaily.putExtra("telefon",requestList.get(pozice).getTelefon());
        kamaradDetaily.putExtra("narozen",requestList.get(pozice).getNarozen());
        kamaradDetaily.putExtra("ID",requestList.get(pozice).getUserID());

        startActivity(kamaradDetaily);
    }
}