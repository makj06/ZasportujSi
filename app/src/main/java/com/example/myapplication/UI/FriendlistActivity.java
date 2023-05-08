package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.Adaptery.UzivatelAdapter;
import com.example.myapplication.DatoveTypy.Kamarad;
import com.example.myapplication.DatoveTypy.Uzivatel;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendlistActivity extends AppCompatActivity implements KlikatelnyRVInterface {

    private RecyclerView friendlistRV;
    private ImageView zpatkyIV, pridatIV, zpatkyDialogIV, requestIV;
    private Button pridatDialogBtn;
    private EditText emailET;
    private FirebaseFirestore fStore;
    private UzivatelAdapter uzivatelAdapter;
    private List<Uzivatel> uzivatelList;
    private List<String> IDList;
    private FirebaseAuth fAuth;
    private String userID, emailZInputu;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        //Přiřazení jednolivých proměnných k prvkům obrazovky
        friendlistRV = findViewById(R.id.FriendRecyclerView);
        zpatkyIV = findViewById(R.id.friendZpatkyIV);
        pridatIV = findViewById(R.id.friendPridatIV);
        requestIV = findViewById(R.id.friendRequestIV);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        zprovozdniFriendlist();

        //Zprovoznění tlačítka pro přechod zpět
        zpatkyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prechodZpet();
            }
        });

        //Zprovoznění tlačítka pro puštění popUp okna
        pridatIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pridatKamaradaDialog();

            }
        });

        //Zprovoznění tlačítka pro spustění aktivity friendRequest
        requestIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendlistActivity.this, FriendRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Metoda, která je odpovědná za vytáhnutí dat z databáze a provozdnění RV
     */
    private void zprovozdniFriendlist() {
        friendlistRV.setHasFixedSize(true);
        friendlistRV.setLayoutManager(new LinearLayoutManager(this));
        IDList = new ArrayList<>();
        uzivatelList = new ArrayList<>();
        uzivatelAdapter = new UzivatelAdapter(this, FriendlistActivity.this, uzivatelList);

        //Získání jednotlivých ID uživatelů, který jsou přihlášení na událost.
        fStore.collection("Uživatelé").document(userID).collection("FriendList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    //Pokud se podaří data získat, tak se vezmou ID uživatelů a nahrajou se do array listu
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){

                        String documentID = documentSnapshot.getId();
                        IDList.add(documentID);
                    }
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
                            friendlistRV.setAdapter(uzivatelAdapter);
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
        Intent kamaradDetaily = new Intent(FriendlistActivity.this,KamaradDetailyActivity.class);

        kamaradDetaily.putExtra("jmeno",uzivatelList.get(pozice).getJmeno());
        kamaradDetaily.putExtra("prijmeni",uzivatelList.get(pozice).getPrijmeni());
        kamaradDetaily.putExtra("email",uzivatelList.get(pozice).getEmail());
        kamaradDetaily.putExtra("telefon",uzivatelList.get(pozice).getTelefon());
        kamaradDetaily.putExtra("narozen",uzivatelList.get(pozice).getNarozen());
        kamaradDetaily.putExtra("ID",uzivatelList.get(pozice).getUserID());

        startActivity(kamaradDetaily);
    }

    /**
     * Aktivita, která je zodpovědná za přechod z FriendlistActivity do ProfilActivity
     */
    private void prechodZpet() {
        Intent zpet = new Intent(FriendlistActivity.this, ProfilActivity.class);
        startActivity(zpet);
    }

    /**
     * Aktivita, která je zodpovědná za zapnutí dialogu pro přidání kamaráda.
     */
    private void pridatKamaradaDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View pridatKamaradaPopUp =getLayoutInflater().inflate(R.layout.pridat_kamarada_popup,null);

        //Přidání proměnných k jednotlivým částem dialogu.
        zpatkyDialogIV = pridatKamaradaPopUp.findViewById(R.id.dialogZpatkyIV);
        emailET = pridatKamaradaPopUp.findViewById(R.id.dialogEmailET);
        pridatDialogBtn = pridatKamaradaPopUp.findViewById(R.id.dialogPridatBTN);

        dialogBuilder.setView(pridatKamaradaPopUp);
        dialog = dialogBuilder.create();
        dialog.show();

        //Zprovoznění tlacitka pro zpatek
        zpatkyDialogIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Zprovoznění tlačítka pro přidání kamaráda do seznamu
        pridatDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailZInputu = emailET.getText().toString();

                if(TextUtils.isEmpty(emailZInputu)){
                    emailET.setError("Zadej email");
                    emailET.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailZInputu).matches()){
                    emailET.setError("Formát emailu není správný");
                    emailET.requestFocus();
                }
                else{
                    posliRequest();
                }
            }
        });
    }

    /**
     * Metoda, která je odpovědná za přidání kamaráda na friendlist
     */
    private void posliRequest() {
        fStore.collection("Uživatelé").whereEqualTo("email", emailZInputu).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot documentSnapshot :task.getResult()){

                            //Získání ID kamaráda
                            String kamaradovoID = documentSnapshot.getId();
                            //Získání třídy kamaráda:
                            Uzivatel uzivatel = documentSnapshot.toObject(Uzivatel.class);

                            //Získání emailu z proměnné uživatel a následné vytvoření instance davotého typu kamarád.
                            String email = uzivatel.getEmail();
                            Kamarad kamarad = new Kamarad(email,kamaradovoID);

                            //Kontrola, zda uživatel nezadal svůj email. (porovnávají se ID osob)
                            if(kamaradovoID.equals(userID)){
                                Toast.makeText(FriendlistActivity.this, "Není možné přidat sama sebe", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                //Pokud uživatel nezadal svůj email, tak se odešlou informace do db.
                                posliKamaradoviData(kamaradovoID,kamarad);
                            }
                        }
                    } else{
                        Toast.makeText(FriendlistActivity.this, "Kamaráda nebylo možné přidat", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
    }


    /**
     * Metoda, která je odpovědná za poslání dat do Databáze
     */
    private void posliKamaradoviData(String kamaradovoID, Kamarad kamarad) {

        DocumentReference uzivatelref = fStore.collection("Uživatelé") .document(kamaradovoID).collection("FriendRequest").document(userID);

        uzivatelref.set(kamarad).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                FriendlistActivity.this.recreate();
                Toast.makeText(FriendlistActivity.this, "Pozvánka byla odeslána", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FriendlistActivity.this, "Uživatele nebylo možné přidat. Zkuste prosím později.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

}