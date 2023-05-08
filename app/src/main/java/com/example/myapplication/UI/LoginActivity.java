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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, hesloInput;
    private TextView registraceOdkaz;
    private Button loginBTN, loginGoogleBTN, loginFacebookBTN;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Přiřazení proměných k jednotlivým částem obrazovky
        emailInput = findViewById(R.id.loginEmailInput);
        hesloInput = findViewById(R.id.loginHesloInput);
        registraceOdkaz = findViewById(R.id.loginZalozitUcetOdkaz);
        loginBTN = findViewById(R.id.loginTlacitko);
        loginGoogleBTN = findViewById(R.id.loginGoogleButton);
        loginFacebookBTN = findViewById(R.id.loginFacebookButton);
        fAuth = FirebaseAuth.getInstance();

        //Přidání on click listeneru na login tlačítko
        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Získání dat z inputů
                String email = emailInput.getText().toString();
                String heslo = hesloInput.getText().toString();

                //Jednotlivé podmínky, které musí být u inputů splněny.
                if (TextUtils.isEmpty(email)){
                    emailInput.setError("Zadejte email");
                    emailInput.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailInput.setError("Email není ve správné formě");
                    emailInput.requestFocus();
                } else if(TextUtils.isEmpty(email)){
                    emailInput.setError("Zadejte heslo");
                    emailInput.requestFocus();
                } else {
                    prihlasUzivatele(email,heslo);
                }
            }
        });

        //Přidání on click listeneru na google login tlačítko
        loginGoogleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googleLogin = new Intent(LoginActivity.this, GoogleLoginActivity.class);
                startActivity(googleLogin);
            }
        });

        //Přidání on click listeneru na facebook login tlačítko
        loginFacebookBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facebookLogin = new Intent(LoginActivity.this, FacebookLoginActivity.class);
                startActivity(facebookLogin);
            }
        });

        //Přechod do registrace
        registraceOdkaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prechodRegistrace();
            }
        });
    }

    /**
     * OnStart metoda, která kontroluje jestli je uživatel přihlášený
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(fAuth.getCurrentUser() != null){
            prechodLogin();
        }
    }

    /**
     * Metoda, která je zodpově za přihlášení uživatele.
     * @param eml email uživatele
     * @param psw heslo uživatele
     */

    private void prihlasUzivatele(String eml, String psw) {
        fAuth.signInWithEmailAndPassword(eml,psw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Přihlášení proběhlo v pořádku", Toast.LENGTH_SHORT).show();
                    prechodLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "Přihlášení se nepodařilo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metoda, která je zodpovědná za přechod z obrazovky login do obrazovky registrace
     */

    private void prechodRegistrace(){
        Intent prechodZLogin = new Intent(this, RegistrationActivity.class);
        startActivity(prechodZLogin);
    }

    /**
     * Metoda, která je zodpovědná za přechod z obrazovky login do obrazovky registrace
     */

    private void prechodLogin() {
        Intent prechodZLogin = new Intent(this, UdalostiActivity.class);
        prechodZLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(prechodZLogin);
    }
}