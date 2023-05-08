package com.example.myapplication.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.DatoveTypy.Udalost;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PridatUdalostActivity extends AppCompatActivity {
    
    //Inicializace proměnných
    private EditText nazevInput, proKohoInput, druhSportuInput, datumInput, zacatekInput, konecInput, kapacitaInput, adresaInput;
    private ImageView zpetIV;
    private String nazev, proKoho, druhSportu, datum, zacatek, konec, kapacita, adresa, vytvorilID, datumParse, zacatekParse;
    private double zemDelka, zemSirka;
    private Timestamp zacatekUdalosti;
    private Integer dKapacitaHodnota;
    private Button vytvoritUdalostBTN;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pridat_udalost);

        //Přidání jednotlivých proměnných k prvůk obrazovky
        nazevInput = findViewById(R.id.praNazevUdalostiInput);
        proKohoInput = findViewById(R.id.praKdoSePrihlasiInput);
        druhSportuInput = findViewById(R.id.praTypSportuInput);
        datumInput = findViewById(R.id.praDatumInput);
        zacatekInput = findViewById(R.id.praOdKdyInput);
        konecInput = findViewById(R.id.praDoKdyInput);
        kapacitaInput = findViewById(R.id.praKapacitaInput);
        adresaInput = findViewById(R.id.praMistoInput);
        vytvoritUdalostBTN = findViewById(R.id.praVytvoritUdalostBTN);
        zpetIV = findViewById(R.id.praZpetIV);

        //Firebase
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        zkontrolujPreposlani();

        dKapacitaHodnota = 1;

        //VytvoreniDialogu pro výběr kdo se na událost smí přihlásit
        vytvorProKohoPicker();

        //VytvoreniDialogu pro výběr kdo se na událost smí přihlásit
        vytvorSportPicker();

        //VytvoreniDialogu pro výběr kapacity
        vytvorKapacitaPicker();

        //Vytvoreni DatePickeru
        pridejDatepicker();

        //Vytvoření TimePickeru
        pridejTimePicker();

        //Přechod do aktivity pro výběr místa
        vyberMisto();

        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prechodUdalost();
            }
        });

        //Přidání onClick listeneru na odesílací tlačítko.
        zprovozniOdesilani();
    }

    /**
     * Metoda, která získává přeposlané data z oblibeneMistaActivity a následně je dává do příšlušných inputů
     */
    private void zkontrolujPreposlani() {
        nazev= getIntent().getStringExtra("nazev");
        proKoho = getIntent().getStringExtra("proKoho");
        druhSportu = getIntent().getStringExtra("druhSportu");
        datum = getIntent().getStringExtra("datum");
        zacatek = getIntent().getStringExtra("zacatek");
        konec = getIntent().getStringExtra("konec");
        kapacita = getIntent().getStringExtra("kapacita");
        adresa = getIntent().getStringExtra("adresa");
        zacatekParse = getIntent().getStringExtra("zacatekParse");
        datumParse = getIntent().getStringExtra("datumParse");
        zemSirka = getIntent().getDoubleExtra("zemSirka",0);
        zemDelka = getIntent().getDoubleExtra("zemDelka",0);

        if(nazev != null){
            nazevInput.setText(nazev);
        }

        if(proKoho != null){
            proKohoInput.setText(proKoho);
        }

        if(druhSportu != null){
            druhSportuInput.setText(druhSportu);
        }

        if(datum != null){
            datumInput.setText(datum);
        }

        if(zacatek != null){
            zacatekInput.setText(zacatek);
        }

        if(konec != null){
            konecInput.setText(konec);
        }

        if(kapacita != null){
            kapacitaInput.setText(kapacita + " hráčů");
        }

        if(adresa != null){
            adresaInput.setText(adresa);
        }

    }

    /**
     * Metoda odpovědná za přechod do obrazovky oblíbené místa
     */
    private void vyberMisto() {
        adresaInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oblibeneMisto = new Intent(PridatUdalostActivity.this, OblibeneMistaActivity.class);

                oblibeneMisto.putExtra("nazev",nazevInput.getText().toString());
                oblibeneMisto.putExtra("proKoho",proKohoInput.getText().toString());
                oblibeneMisto.putExtra("druhSportu",druhSportuInput.getText().toString());
                oblibeneMisto.putExtra("datum",datumInput.getText().toString());
                oblibeneMisto.putExtra("zacatek",zacatekInput.getText().toString());
                oblibeneMisto.putExtra("konec",konecInput.getText().toString());
                oblibeneMisto.putExtra("kapacita",kapacita);
                oblibeneMisto.putExtra("adresa",adresaInput.getText().toString());
                oblibeneMisto.putExtra("zacatekParse", zacatekParse);
                oblibeneMisto.putExtra("datumParse", datumParse);

                startActivity(oblibeneMisto);
            }
        });
    }


    /**
     * Metoda, která je odpovědná za zobrazení pickeru pro výběr sportu
     */
    private void vytvorSportPicker() {
        druhSportuInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(PridatUdalostActivity.this);
                d.setContentView(R.layout.number_picker_dialog);
                Button pridat = d.findViewById(R.id.numberDialogNastavitBtn);
                String[] sport = getResources().getStringArray(R.array.sporty);
                TextView nazev = d.findViewById(R.id.numberDialogNazev);
                nazev.setText("Kdo se smí přihlásit");
                NumberPicker numberPicker = d.findViewById(R.id.dialogNumberPicker);
                numberPicker.setMaxValue(4);
                numberPicker.setMinValue(0);
                numberPicker.setDisplayedValues(sport);

                pridat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        druhSportuInput.setText(sport[numberPicker.getValue()]);
                        d.dismiss();
                    }
                });
                d.show();
            }
        });
    }

    /**
     * Metoda, která je odpovědná za zobrazení pickeru pro koho je daná událost
     */
    private void vytvorProKohoPicker() {
        proKohoInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(PridatUdalostActivity.this);
                d.setContentView(R.layout.number_picker_dialog);
                Button pridat = d.findViewById(R.id.numberDialogNastavitBtn);
                String[] proKoho = getResources().getStringArray(R.array.proKoho);
                TextView nazev = d.findViewById(R.id.numberDialogNazev);
                nazev.setText("Kdo se smí přihlásit");
                NumberPicker numberPicker = d.findViewById(R.id.dialogNumberPicker);
                numberPicker.setMaxValue(1);
                numberPicker.setMinValue(0);
                numberPicker.setDisplayedValues(proKoho);

                pridat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        proKohoInput.setText(proKoho[numberPicker.getValue()]);
                        d.dismiss();
                    }
                });
                d.show();
            }
        });
    }

    /**
     * Metoda, která je odpovědná za zobrazení pickeru kapacity
     */
    private void vytvorKapacitaPicker() {
        kapacitaInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(PridatUdalostActivity.this);
                d.setContentView(R.layout.number_picker_dialog);
                Button pridat = d.findViewById(R.id.numberDialogNastavitBtn);
                TextView nazev = d.findViewById(R.id.numberDialogNazev);
                nazev.setText("Vyberte kapacitu");
                NumberPicker numberPicker = d.findViewById(R.id.dialogNumberPicker);
                numberPicker.setMaxValue(50);
                numberPicker.setMinValue(1);
                numberPicker.setValue(dKapacitaHodnota);

                pridat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dKapacitaHodnota = numberPicker.getValue();
                        kapacita = dKapacitaHodnota.toString();
                        kapacitaInput.setText(kapacita + " hráčů");
                        d.dismiss();
                    }
                });
                d.show();
            }
        });
    }

    /**
     * Metoda, která vytvoří timePicker a přiřadí ho jednotlivým inputům, který ho využívají
     * Inspirace zde: https://www.youtube.com/watch?v=mpd0Al01jjY
     */
    private void pridejTimePicker() {
        final int hodina = 0;
        final int minuta = 0;
        final Boolean typ24hPickeru = true;

        zacatekInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker = new TimePickerDialog(PridatUdalostActivity.this, android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        zacatekInput.setText(String.format(Locale.getDefault(),"%2d:%02d",hourOfDay,minute));
                        zacatekParse = String.format(Locale.getDefault(),"%02d:%02d",hourOfDay,minute);
                    }
                },hodina,minuta,typ24hPickeru);
                timePicker.setTitle("Vyberte začátek");
                timePicker.show();
            }
        });

        konecInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker = new TimePickerDialog(PridatUdalostActivity.this, android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        konecInput.setText(String.format(Locale.getDefault(),"%2d:%02d",hourOfDay,minute));
                    }
                },hodina,minuta,typ24hPickeru);

                timePicker.setTitle("Vyberte konec");
                timePicker.show();
            }
        });
    }

    /**
     * Metoda, která vytvoří datepicker
     */
    private void pridejDatepicker() {
        final Calendar kalendar = Calendar.getInstance();
        final int rok = kalendar.get(Calendar.YEAR);
        final int mesic = kalendar.get(Calendar.MONTH);
        final int den = kalendar.get(Calendar.DAY_OF_MONTH);

        datumInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(PridatUdalostActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        month=month+1;
                        String datum = dayOfMonth + "." + month + "." + year;
                        datumInput.setText(datum);

                        String den = "";
                        String mesic = "";
                        if(dayOfMonth<10){
                            den = "0"+dayOfMonth;
                        }else {
                            den = String.valueOf(dayOfMonth);
                        }

                        if (month <10){
                            mesic = "0"+month;
                        } else {
                            mesic = String.valueOf(month);
                        }

                        datumParse = den + "." + mesic + "." + year;
                    }
                },rok,mesic,den);
                dialog.getDatePicker().setMinDate(kalendar.getTimeInMillis());
                dialog.show();
            }
        });
    }

    /**
     * Metoda, kteráje zodpovědná za získání dat z Inputů, následnou kontrolu dat a odeslání do db
     */
    private void zprovozniOdesilani() {
        vytvoritUdalostBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Získání textu z inputů
                nazev = nazevInput.getText().toString();
                proKoho = proKohoInput.getText().toString();
                druhSportu = druhSportuInput.getText().toString();
                datum = datumInput.getText().toString();
                zacatek = zacatekInput.getText().toString();
                konec = konecInput.getText().toString();
                adresa = adresaInput.getText().toString();

                //Podmínky pro jednotlivé inputy. Pokud jsou splněny, tak se odešlou data
                if(TextUtils.isEmpty(nazev)){
                    nazevInput.setError("Je potřeba zadat název události");
                    nazevInput.requestFocus();
                } else if(TextUtils.isEmpty(proKoho)){
                    proKohoInput.setError("Je potřeba zadat zda se na událost mohou přihlásit všichni a nebo jen přátelé");
                    proKohoInput.requestFocus();
                }else if(TextUtils.isEmpty(druhSportu)){
                    druhSportuInput.setError("Je potřeba zadat druh sportu");
                    druhSportuInput.requestFocus();
                }else if(TextUtils.isEmpty(datum)){
                    datumInput.setError("Je potřeba zadat datum");
                    datumInput.requestFocus();
                }else if(TextUtils.isEmpty(zacatek)){
                    zacatekInput.setError("Je potřeba zadat čas začátku");
                    zacatekInput.requestFocus();
                }else if(TextUtils.isEmpty(konec)){
                    konecInput.setError("Je potřeba zadat čas konce");
                    konecInput.requestFocus();
                } else if(TextUtils.isEmpty(kapacita)){
                    kapacitaInput.setError("Je potřeba zadat kapacitu");
                    kapacitaInput.requestFocus();
                } else if(TextUtils.isEmpty(adresa)){
                    adresaInput.setError("Je potřeba zadat místo události");
                    adresaInput.requestFocus();
                }else {
                    vytvorUdalost();
                }
            }
        });
    }

    /**
     * Metoda, která je zodpovědná za poslání dat z inputů do databáze a tím vytvořit záznam o události.
     */
    private void vytvorUdalost() {
        //Kdo ID cloveka, kdo vytvoril událost.
        vytvorilID = fUser.getUid();
        DocumentReference dbRef = fStore.collection("Události").document();

        vytvorTimestamp();

        Udalost udalost = new Udalost(nazev,zacatek,konec, adresa,proKoho,datum,druhSportu,kapacita,vytvorilID,zemDelka,zemSirka,zacatekUdalosti);

        //Poslání do databáze
        dbRef.set(udalost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Map<String, Object> uzivatel= new HashMap<>();

                uzivatel.put("uživatel",vytvorilID);


                dbRef.collection("Prihlaseni").document(vytvorilID).set(uzivatel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                       Toast.makeText(PridatUdalostActivity.this, "Událost byla vytvořena", Toast.LENGTH_SHORT).show();
                        prechodUdalost();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PridatUdalostActivity.this, "Vytvoření události se nepovedno, zkuste znovu později", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PridatUdalostActivity.this, "Vytvoření události se nepovedno, zkuste znovu později", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Source: https://stackoverflow.com/questions/18915075/java-convert-string-to-timestamp
    private void vytvorTimestamp() {
        String timeStamp = datumParse + " " + zacatekParse+":00.000";
        try
        {
            //creating date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
            //parsing string to date using parse() method
            Date parsedDate = dateFormat.parse(timeStamp);
            //finally creating a timestamp
            zacatekUdalosti = new Timestamp(parsedDate);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Metoda, která je zodpovědná za přechod z obrazovky přidat událost do obrazovky událostí.
     */
    private void prechodUdalost() {
        Intent prechodUdalost = new Intent(PridatUdalostActivity.this, UdalostiActivity.class);
        prechodUdalost.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(prechodUdalost);
    }
}