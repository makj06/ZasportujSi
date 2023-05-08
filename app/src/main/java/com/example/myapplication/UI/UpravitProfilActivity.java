package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UpravitProfilActivity extends AppCompatActivity {

    //Inicializace proměnných
    private EditText jmenoInput, prijmeniInput, telefonInput, narozeninyInput;
    private ImageView profilObrazekView, zpetIV;
    private String jmeno, prijmeni, telefoniCislo, datumNarozeni, fUserID ;
    private Uri profilObrazekURI;
    private Button upravitBtn;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private StorageReference storageReference;
    private Calendar kalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upravit_profil);

        //Přiřazení jednotlivých proměnných k částem obrazovky
        jmenoInput = findViewById(R.id.profJmenoInput);
        prijmeniInput = findViewById(R.id.profPrijmeniInput);
        telefonInput = findViewById(R.id.profTelefonInput);
        narozeninyInput = findViewById(R.id.profRokNarozeniInput);
        upravitBtn = findViewById(R.id.profUprButton);
        profilObrazekView = findViewById(R.id.uprImageView);
        zpetIV = findViewById(R.id.uprZpetIV);

        //kalendář
        kalendar = Calendar.getInstance();

        //Firebase
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fStore =FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReference();
        fUserID = fUser.getUid();

        profilObrazekView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otevriGalerii();
            }
        });

        //Vytvoření datepickeru
        pridejDatepicker();

        //Přidání on click listeneru na imageView zpět
        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prechodDoProfilu();
            }
        });

        //Přidání on click listeneru na tlačítko upravit
        upravitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Získání textu z jednotlivých proměnných
                jmeno = jmenoInput.getText().toString();
                prijmeni = prijmeniInput.getText().toString();
                telefoniCislo = telefonInput.getText().toString();
                datumNarozeni = narozeninyInput.getText().toString();

                //Regex pro telefoní číslo
                String telCisloReg = "[+][4][2][0][0-9]{9}";
                Pattern telefonPattern = Pattern.compile(telCisloReg);
                Matcher telefonMatch = telefonPattern.matcher(telefoniCislo);


                //Kontrola, zda teleconi cislo je validní, pokud ano, tak se provede update dat v db.
                if(!TextUtils.isEmpty(telefoniCislo) && !telefonMatch.find()){
                    telefonInput.setError("Telefoní číslo je špatně zadáno. Zadej telefonní číslo s předvolnou +420 a bez mezer");
                    telefonInput.requestFocus();
                } else{
                    if(profilObrazekURI==null){
                        upravProfil(jmeno,prijmeni, datumNarozeni, telefoniCislo);
                    }else{
                        nahrajFotku();
                    }
                }
            }
        });
    }


    /**
     * Metoda, která je odpovědná za otevření galerie
     */
    private void otevriGalerii() {
        Intent galerie = new Intent();
        galerie.setType("image/*");
        galerie.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galerie,1);
    }

    /**
     * Metoda, která je odpovědná za přidělení adresy obrázku na zařízení do proměnné
     * a nastavení obrázku do ImageView.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            profilObrazekURI = data.getData();
            profilObrazekView.setImageURI(profilObrazekURI);
        }
    }

    /**
     * Metoda, která vytvoří datepicker
     */
    private void pridejDatepicker() {
        final Calendar kalendar = Calendar.getInstance();
        kalendar.add(Calendar.YEAR,-3);
        final int rok = kalendar.get(Calendar.YEAR);
        final int mesic = kalendar.get(Calendar.MONTH);
        final int den = kalendar.get(Calendar.DAY_OF_MONTH);

        narozeninyInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(UpravitProfilActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        month=month+1;
                        String datum = dayOfMonth + "." + month + "." + year;
                        narozeninyInput.setText(datum);

                    }
                },rok,mesic,den);
                dialog.getDatePicker().setMaxDate(kalendar.getTimeInMillis());
                dialog.show();
            }
        });
    }

    /**
     * Metoda, která se vyvolá při kliknutí tlačítka upravit. Metoda zajištuje vytvoření mapy a případný update v DB.
     * @param jmeno textová hodnota v poli EditText s názvem jmenoInput
     * @param prijmeni textová hodnota v poli EditText s názvem prijmeniInput
     * @param datumNarozeni textová hodnota v poli EditText s názvem narozeninyInput
     * @param telefoniCislo textová hodnota v poli EditText s názvem telefonInput
     */
    private void upravProfil(String jmeno, String prijmeni, String datumNarozeni, String telefoniCislo) {
        //Firebase
        DocumentReference dbRef = fStore.collection("Uživatelé").document(fUserID);

        //Vytvoření mapy pro poslání záznamu do databáze
        Map<String, Object> uzivatel = new HashMap<>();
        uzivatel.put("jmeno", jmeno);
        uzivatel.put("prijmeni",prijmeni);
        uzivatel.put("narozen", datumNarozeni);
        uzivatel.put("telefon",telefoniCislo);

        //Odebrání záznamů z mapy, které nechceme měnit.(jsou prázdné)
        if(TextUtils.isEmpty(jmeno)){
            uzivatel.remove("jmeno");
        }
        if(TextUtils.isEmpty(prijmeni)){
            uzivatel.remove("prijmeni");
        }
        if(TextUtils.isEmpty(datumNarozeni)){
            uzivatel.remove("narozen");
        }
        if(TextUtils.isEmpty(telefoniCislo)){
            uzivatel.remove("telefon");
        }

        //Update mapy v databázi
        if (uzivatel.isEmpty() && profilObrazekURI==null){
            Toast.makeText(UpravitProfilActivity.this, "Uživatelské data zůstala stejná", Toast.LENGTH_SHORT).show();
            prechodDoProfilu();
        }else {
            dbRef.update(uzivatel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(UpravitProfilActivity.this, "Uživatelské data byli změněny", Toast.LENGTH_SHORT).show();
                    prechodDoProfilu();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpravitProfilActivity.this, "Uživatelské data nebyli změněny, zkuste znovu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Metoda, která zabezpečuje nahrání fotky v případě, že se nahraje fotka na firebase, tak se pošlou data do db.
     */
    private void nahrajFotku() {
        StorageReference profilObrRef = storageReference.child("profiloveObrazky/"+ fUserID+ ".jpg");
        profilObrRef.putFile(profilObrazekURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                upravProfil(jmeno,prijmeni, datumNarozeni, telefoniCislo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpravitProfilActivity.this, "Uživatelské data nebylo možné odeslat, zkuste prosím později.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Metoda zabezpečující zpátky do profilu.
     */
    private void prechodDoProfilu() {
        Intent prechodProfil = new Intent(UpravitProfilActivity.this,ProfilActivity.class);
        prechodProfil.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(prechodProfil);
    }
}