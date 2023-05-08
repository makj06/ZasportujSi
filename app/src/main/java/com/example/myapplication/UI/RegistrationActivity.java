package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.DatoveTypy.Uzivatel;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;




public class RegistrationActivity extends AppCompatActivity {

    //Inicializace proměnných.
    private EditText jmenoInput, prijmeniInput, emailInput, hesloInput, hesloZnovuInput;
    private Button registerButton;
    private static final String TAG ="Aktivita";
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Přiřazení proměných k jednotlivým částem obrazovky
        jmenoInput = findViewById(R.id.regJmenoInput);
        prijmeniInput =findViewById(R.id.regPrijmeniInput);
        emailInput = findViewById(R.id.regEmailInput);
        hesloInput = findViewById(R.id.regHeslo);
        hesloZnovuInput = findViewById(R.id.regHesloZnovu);
        registerButton = findViewById(R.id.regButton);
        fStore = FirebaseFirestore.getInstance();

        //Přidání onClick listeneru k tlačítku.
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Získání dat z inputů
                String jmeno = jmenoInput.getText().toString();
                String prijmeni = prijmeniInput.getText().toString();
                String email = emailInput.getText().toString();
                String heslo = hesloInput.getText().toString();
                String hesloZnovu = hesloZnovuInput.getText().toString();

                //Jednotlivé podmínky, které musí být u inputů splněny.
                if (TextUtils.isEmpty(jmeno)){
                    jmenoInput.setError("Zadej jméno");
                    jmenoInput.requestFocus();
                }
                else if(TextUtils.isEmpty(prijmeni)){
                    emailInput.setError("Zadej příjmení");
                    emailInput.requestFocus();
                }
                else if(TextUtils.isEmpty(email)){
                    emailInput.setError("Zadej email");
                    emailInput.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailInput.setError("Formát emailu není správný");
                    emailInput.requestFocus();
                }
                else if(TextUtils.isEmpty(heslo)){
                    hesloInput.setError("Zadej heslo");
                    hesloInput.requestFocus();
                }
                else if(heslo.length()<10){
                    hesloInput.setError("Heslo musí mít alespoň 10 znaků");
                    hesloInput.requestFocus();
                }
                else if(TextUtils.isEmpty(hesloZnovu)){
                    hesloZnovuInput.setError("Zadej znovu heslo");
                    hesloZnovuInput.requestFocus();
                }
                else if(!heslo.equals(hesloZnovu)){
                    hesloZnovuInput.setError("Hesla se musí shodovat");
                    hesloZnovuInput.requestFocus();
                    hesloInput.clearComposingText();
                    hesloZnovuInput.clearComposingText();
                } else {
                    zaregistrujUzivatele(jmeno,prijmeni,email,heslo);
                }
            }
        });
    }

    /**
     * Metoda, která je zodpovědná přidání uživatele do Firebase Autentizace.
     * @param jmeno text, který uživatel zadal do inputu jmeno
     * @param prijmeni text, který uživatel zadal do inputu prijmeni
     * @param email text, který uživatel zadal do inputu email
     * @param heslo text, který uživatel zadal do inputu heslo
     */

    private void zaregistrujUzivatele(String jmeno,String prijmeni, String email, String heslo) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.createUserWithEmailAndPassword(email,heslo).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                /*
                V případě, že auentizace je úspěšná, tak se vytvojí Toast message. Následně se získá ID uživatele a pošle se mu Email,
                pomocí kterého ověří správnost jeho emailové adresy. Následně se uživatel přesune do aplikace. Flagy zajištují to, aby došlo k zavření otevřených aktivit.
                 */
                if(task.isSuccessful()){
                    //Inicializace zbytku proměnných, který se posílají do db.
                    String datumNarozeni = "";
                    String telefoniCislo = "";

                    //Firebase
                    FirebaseUser fUser = fAuth.getCurrentUser();
                    String fUserID = fUser.getUid();
                    DocumentReference dbRef = fStore.collection("Uživatelé").document(fUserID);

                    Uzivatel uzivatel =new Uzivatel(jmeno,prijmeni,email,telefoniCislo,datumNarozeni,fUserID);

                    //Poslání dat do databáze
                    dbRef.set(uzivatel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            fUser.sendEmailVerification();

                            Toast.makeText(RegistrationActivity.this, "Uživatel vytvořen. Ověřte účet pomocí emailu.", Toast.LENGTH_SHORT).show();
                            registracePrechodDoAplikace();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistrationActivity.this, "Registrace se nezdařila. Zkuste prosím znovu.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * Metoda, která zajištuje přechod k obrazovky registrace do obrazovky přehled.
     */

    private void registracePrechodDoAplikace() {
        Intent registracePrechod = new Intent(RegistrationActivity.this, ProfilActivity.class);
        registracePrechod.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(registracePrechod);
        finish();
    }
}