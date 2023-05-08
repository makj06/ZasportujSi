package com.example.myapplication.UI;

/**
 * Source: https://github.com/firebase/snippets-android/blob/3c06ff4c9e2c257c4ecc206c4cc3139fde7458a3/auth/app/src/main/java/com/google/firebase/quickstart/auth/FacebookLoginActivity.java#L98-L104
 * + moje úpravy
 */




import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.DatoveTypy.Uzivatel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class FacebookLoginActivity extends LoginActivity {

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private String fUserID;
    private DocumentReference dbRef;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        token = AccessToken.getCurrentAccessToken();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            zkontrolujLogin();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(FacebookLoginActivity.this, "Nebylo možné se přihlásit. Zkuste prosím později.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }


    /**
     * Metoda, která je zodpovědná za určení, zda byli uživatelovi data už poslány do DB. Pokud ne, tak se pošlou.
     * inspirace zde: https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection
     */
    private void zkontrolujLogin() {
        FirebaseUser user = mAuth.getCurrentUser();
        fUserID = user.getUid();
        dbRef =db.collection("Uživatelé").document(fUserID);
        dbRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        updateUI(user);
                    } else {
                        //Dokument neexistuje
                        posliDataDB();
                        updateUI(user);
                    }
                }
            }
        });
    }

    /**
     * Metoda, která je zodpovědá za poslání dat do databáze.
     */
    private void posliDataDB() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null){
            fUserID = user.getUid();
            //Získání jména a příjmení
            String celeJmeno = user.getDisplayName();
            String jmenoPrijmeni[] = celeJmeno.split(" ");

            //Vytvoreni promenných pro posilani do DB
            String jmeno = jmenoPrijmeni[0];
            String prijmeni = jmenoPrijmeni[1];
            String email = user.getEmail();
            String telefoniCislo = "";
            String datumNarozeni = "";

            updateUI(user);

            Uzivatel uzivatel = new Uzivatel(jmeno,prijmeni,email,telefoniCislo,datumNarozeni, fUserID);

            dbRef.set(uzivatel);
        } else{
            Toast.makeText(FacebookLoginActivity.this, "Nebylo možné se přihlásit. Zkuste prosím později.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metoda, která zabezpečuje přechod z facebook okna dovniřku aplikace.
     */
    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(FacebookLoginActivity.this, UdalostiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}