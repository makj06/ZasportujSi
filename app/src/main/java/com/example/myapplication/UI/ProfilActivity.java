package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ProfilActivity extends AppCompatActivity {

    //Inicializace proměnných.
    private TextView jmenoView, emailView, telefonView, narozeninyView, uvitaniView;
    private String jmeno, prijmeni, email, telefon, narozeniny, FUserID;
    private Button odhlasitSeBtn, friendlistBtn, upravitProfilBtn;
    private ImageView profilImageView;
    private BottomNavigationView bView;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        //Přiřazení k jednotlivým prvkům obrazovky

        jmenoView = findViewById(R.id.profJmenoTxt);
        emailView = findViewById(R.id.profEmailTxt);
        telefonView = findViewById(R.id.profTelefonTxt);
        narozeninyView = findViewById(R.id.profRokNarozeniTxt);
        uvitaniView = findViewById(R.id.profUvitaniTxt);
        upravitProfilBtn = findViewById(R.id.profUpravitProfilBtn);
        friendlistBtn = findViewById(R.id.profFriendListBtn);
        odhlasitSeBtn = findViewById(R.id.profLogoutBtn);
        profilImageView = findViewById(R.id.profImageView);

        fAuth = FirebaseAuth.getInstance();
        fUser= fAuth.getCurrentUser();
        FUserID = fUser.getUid();
        fStore=FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReference();
        bView = findViewById(R.id.spodniNavigace);

        if (fUser == null) {
            Toast.makeText(ProfilActivity.this, "Data nejsou přístupná, zkuste později.", Toast.LENGTH_SHORT).show();
        } else{
            nactiData(fUser);
            nactiObrazek();
        }

        //Vytvoří se spodní menu
        vytvorSpodniMenu();

        //Přidání onclick listeneru na tlačítko upravit profil
        upravitProfilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upravitProfil();
            }
        });

        //Pridání onclick listeneru na tlačítko zobrazit přátele
        friendlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zobrazPratele();
            }
        });

        //Přidání onclick listeneru na tlačítko odlásit se
        odhlasitSeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                odhlasitSe();
            }
        });
    }


    /**
     * Metoda, která je zodpovědná za načetení dat z databáze.
     * @param fUser je konkrétní uživatel aplikace
     */
    private void nactiData(FirebaseUser fUser) {

        final DocumentReference dbRef = fStore.collection("Uživatelé").document(FUserID);

        dbRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dbSnapshot = task.getResult();
                    jmeno = dbSnapshot.getString("jmeno");
                    prijmeni = dbSnapshot.getString("prijmeni");
                    email = dbSnapshot.getString("email");
                    telefon = dbSnapshot.getString("telefon");
                    narozeniny = dbSnapshot.getString("narozen");


                    uvitaniView.setText("Vítejte" + " " + jmeno);
                    jmenoView.setText(jmeno + " " + prijmeni);
                    emailView.setText(email);
                    telefonView.setText(telefon);
                    narozeninyView.setText(narozeniny);
                } else {
                    Toast.makeText(ProfilActivity.this, "Data z profilu nebylo možné načíst", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metoda odpovědná za záskání obrázku u Firebase Storage
     */
    private void nactiObrazek() {
        StorageReference obrazekReference = storageReference.child("profiloveObrazky/"+ FUserID + ".jpg");

        try {
            File obrazek = File.createTempFile("profiloveObrazky","jpg");
            obrazekReference.getFile(obrazek).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitMap = BitmapFactory.decodeFile(obrazek.getAbsolutePath());
                    profilImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    profilImageView.setImageBitmap(bitMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(ProfilActivity.this, "Obrázek nebylo možné načíst nebo není nahraný", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda, který je zodpovědná za vytvoření spodního menu.
     */
    private void vytvorSpodniMenu() {
        bView.setSelectedItemId(R.id.home);

        bView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mojeUdalosti:
                        startActivity(new Intent(getApplicationContext(), MojeUdalostiActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
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
     * Metoda, která zabezpečuje odhlášení se a následný přechod do login aktivity.
     */
    private void odhlasitSe() {
        fAuth.signOut();
        Intent logOut = new Intent(ProfilActivity.this, LoginActivity.class);
        logOut.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logOut);
    }

    /**
     * Metoda, která zabezpečuje přechod do aktivity friendlist.
     */
    private void zobrazPratele() {
        Intent friendlist = new Intent(ProfilActivity.this, FriendlistActivity.class);
        startActivity(friendlist);
    }

    /**
     * Metoda, která zabezpečuje přechod do aktivity upravit profil.
     */
    private void upravitProfil() {
        Intent upravProfil = new Intent(ProfilActivity.this, UpravitProfilActivity.class);
        startActivity(upravProfil);
    }
}