package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Adaptery.ChatAdapter;
import com.example.myapplication.DatoveTypy.Zprava;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {


    //Inicializace proměnných
    private TextView nazevTV;
    private String nazev, udalostUid, userID;
    private EditText inputZprav;
    private FrameLayout odeslat;
    private List<Zprava> zpravaList;
    private ImageView zpetIV;
    private RecyclerView zpravyRV;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //Získání dat z předchozí aktivity
        nazev= getIntent().getStringExtra("nazev");
        udalostUid = getIntent().getStringExtra("udalostUid");

        //Firebase
        fStore= FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID= fAuth.getCurrentUser().getUid();

        //Přiřazení jednotlivých prvků obrazovky k proměnný
        nazevTV = findViewById(R.id.chatNazevTV);
        zpetIV = findViewById(R.id.chatZpetIV);
        inputZprav = findViewById(R.id.chatZpravaInput);
        odeslat = findViewById(R.id.chatPoslatFL);
        zpravyRV = findViewById(R.id.chatZpravyRV);

        //Nastavení
        nazevTV.setText(nazev);

        zprovozniChat();


        //On Click listener pro tlačítko odeslat
        odeslat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                odesliZpravu();
            }
        });

        //On Click listener pro tlačítko zpět
        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    /**
     * Metoda, která je odpovědná za poslání zprávy do DB.
     */
    private void odesliZpravu() {
        String zpravaText = inputZprav.getText().toString();
        Date datum = new Date();

        if(TextUtils.isEmpty(zpravaText)){
            Toast.makeText(this, "Nelze odeslat práznou zprávu.", Toast.LENGTH_SHORT).show();
        }else {
            Zprava zprava = new Zprava(zpravaText,userID,datum);

            DocumentReference zpravaRef = fStore.collection("Události") .document(udalostUid).collection("Chat").document();

            zpravaRef.set(zprava);
            zprovozniChat();

            inputZprav.setText(null);
        }


    }

    /**
     * Metoda, která je odpovědná za nahrání dat z databáze do jednotlivých listů a následné zobrazení.
     */
    private void zprovozniChat() {
        zpravyRV.setHasFixedSize(true);
        zpravyRV.setLayoutManager(new LinearLayoutManager(this));
        zpravaList = new ArrayList<>();
        chatAdapter = new ChatAdapter(GroupChatActivity.this,zpravaList,userID);

        fStore.collection("Události") .document(udalostUid).collection("Chat").orderBy("odeslano").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){

                        Zprava zprava = documentSnapshot.toObject(Zprava.class);
                        zpravaList.add(zprava);
                        zpravyRV.setAdapter(chatAdapter);
                    }
                    zpravyRV.smoothScrollToPosition(zpravaList.size());
                }
            }
        });
    }

}