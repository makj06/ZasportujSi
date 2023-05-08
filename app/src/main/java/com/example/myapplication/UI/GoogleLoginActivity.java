package com.example.myapplication.UI;

/**
 * Source: https://github.com/firebase/snippets-android/blob/a0de4f583939c956ea0d1d35380c23276a312002/auth/app/src/main/java/com/google/firebase/quickstart/auth/GoogleSignInActivity.java#L67-L68
 * V kódu došlo k částečným úpravám, tak aby vyhovoval mému užití. (došlo k odstranění logů a přidání toast message)
 */



import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.DatoveTypy.Uzivatel;
import com.example.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class GoogleLoginActivity extends LoginActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference dbRef;
    private String fUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Nebylo možné se přihlásit. Zkuste prosím později.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            zkontrolujLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(GoogleLoginActivity.this, "Nebylo možné se přihlásit. Zkuste prosím později.", Toast.LENGTH_SHORT).show();
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
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!= null){
            FirebaseUser user = mAuth.getCurrentUser();
            fUserID = user.getUid();
            String jmeno =account.getGivenName();
            String prijmeni = account.getFamilyName();
            String email= account.getEmail();
            String datumNarozeni = "";
            String telefoniCislo = "";

            Uzivatel uzivatel = new Uzivatel(jmeno,prijmeni,email,telefoniCislo,datumNarozeni, fUserID);

            dbRef.set(uzivatel);
        } else{
            Toast.makeText(GoogleLoginActivity.this, "Nebylo možné se přihlásit. Zkuste prosím později.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metoda, která zabezpečuje přechod z google okna dovniřku aplikace.
     */
    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(GoogleLoginActivity.this, UdalostiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}