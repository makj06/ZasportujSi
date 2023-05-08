package com.example.myapplication.DatoveTypy;


import java.util.Date;

public class Zprava {
    String text, IDOdesilatele;
    Date odeslano;

    //Prázdný konstruktor pro Firebase
    public  Zprava(){};

    //Konstruktor
    public Zprava(String text, String IDOdesilatele, Date odeslano) {
        this.text = text;
        this.IDOdesilatele = IDOdesilatele;
        this.odeslano = odeslano;
    }

    //Gettery a setry
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIDOdesilatele() {
        return IDOdesilatele;
    }

    public Date getOdeslano() {
        return odeslano;
    }
}
